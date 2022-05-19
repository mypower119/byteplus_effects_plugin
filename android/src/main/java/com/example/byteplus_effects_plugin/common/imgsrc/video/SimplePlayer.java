package com.example.byteplus_effects_plugin.common.imgsrc.video;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SimplePlayer {

    private static final String TAG = "Player";
    private static final boolean VERBOSE = true;
    private static final long TIMEOUT_US = 10000;

    private static final int TYPE_VIDEO = 0;
    private static final int TYPE_AUDIO = 1;

    private IPlayStateListener mListener;
    private static IAudioDataListener mAudioDataListener;
    private VideoDecodeThread mVideoDecodeThread;
    private AudioDecodeThread mAudioDecodeThread;
    private boolean isPlaying;
    private boolean isPause;
    private long mLastStartTime;
    private long mDurationTime;
    private String filePath;
    private Surface surface;

    //   {zh} 是否取消播放线程       {en} Whether to cancel the playback thread  
    private boolean cancel = false;

    public interface IPlayStateListener {
        void videoAspect(int width, int height, int videoRotation);

        void onVideoEnd();
    }

    public interface IAudioDataListener {
        void onAudioFormatExtracted(MediaFormat format);

        void onAudioData(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo);
    }

    public SimplePlayer(Surface surface, String filePath) {
        this.surface = surface;
        this.filePath = filePath;
        isPlaying = false;
        isPause = false;
    }


    /** {zh} 
     * 设置回调
     *
     * @param mListener
     */
    /** {en} 
     * Set callback
     *
     * @param mListener
     */

    public void setPlayStateListener(IPlayStateListener mListener) {
        this.mListener = mListener;
    }

    /** {zh} 
     * 设置编码好的音频数据回调 用于合成mp4
     *
     * @param audioDataListener
     */
    /** {en} 
     * Set encoded audio data callbacks for synthesizing mp4
     *
     * @param audioDataListener
     */

    public void setAudioDataListener(IAudioDataListener audioDataListener) {
        this.mAudioDataListener = audioDataListener;
    }

    /** {zh} 
     * 是否处于播放状态
     *
     * @return
     */
    /** {en} 
     * Is it playing
     *
     * @return
     */

    public boolean isPlaying() {
        return isPlaying && !isPause;
    }

    /** {zh} 
     * 开始播放
     */
    /** {en} 
     * Start playing
     */

    public void play() {
        isPlaying = true;
        mLastStartTime = System.currentTimeMillis();
        if (mVideoDecodeThread == null) {
            mVideoDecodeThread = new VideoDecodeThread();
            mVideoDecodeThread.start();
        }
        if (mAudioDecodeThread == null) {
            mAudioDecodeThread = new AudioDecodeThread();
            mAudioDecodeThread.start();
        }
    }

    /** {zh} 
     * 暂停
     */
    /** {en} 
     * Pause
     */

    public void pause() {
        isPause = true;
        mDurationTime = System.currentTimeMillis() - mLastStartTime;
    }

    /** {zh} 
     * 继续播放
     */
    /** {en} 
     * Keep playing
     */

    public void continuePlay() {
        isPause = false;
        mLastStartTime = System.currentTimeMillis();
    }

    /** {zh} 
     * 停止播放
     */
    /** {en} 
     * Stop playing
     */

    public void stop() {
        isPlaying = false;
    }

    /** {zh} 
     * 销毁
     */
    /** {en} 
     * Destroy
     */

    public void destroy() {
        stop();
        if (mAudioDecodeThread != null) {
            mAudioDecodeThread.interrupt();
        }
        if (mVideoDecodeThread != null) {
            mVideoDecodeThread.interrupt();
        }
        mVideoDecodeThread = null;
        mAudioDecodeThread = null;

    }

    /** {zh} 
     * 解复用，得到需要解码的数据
     *
     * @param extractor
     * @param decoder
     * @param inputBuffers
     * @return
     */
    /** {en} 
     * Demultiplexing to get the data to be decoded
     *
     * @param extractor
     * @param decoder
     * @param inputBuffers
     * @return
     */

    @SuppressLint("WrongConstant")
    private static boolean decodeMediaData(MediaExtractor extractor, MediaCodec decoder, ByteBuffer[] inputBuffers, int stickerType) {
        boolean isMediaEOS = false;
        int inputBufferIndex = decoder.dequeueInputBuffer(TIMEOUT_US);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            int sampleSize = extractor.readSampleData(inputBuffer, 0);
            if (stickerType == TYPE_AUDIO && null != mAudioDataListener) {
                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

                if (sampleSize < 0) {
                    info.size = 0;
                    info.flags = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                } else {
                    info.size = sampleSize;
                    info.flags = extractor.getSampleFlags();
                    info.presentationTimeUs = extractor.getSampleTime();
                    info.offset = 0;
                }
                mAudioDataListener.onAudioData(inputBuffer, info);

            }
            if (sampleSize < 0) {
                decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                isMediaEOS = true;
                if (VERBOSE) {
                    Log.d(TAG, "end of stream");
                }
            } else {
                decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                extractor.advance();
            }
        }
        return isMediaEOS;
    }

    /** {zh} 
     * 解码延时
     *
     * @param bufferInfo
     * @param startMillis
     */
    /** {en} 
     * Decoding delay
     *
     * @param bufferInfo
     * @param startMillis
     */

    private void decodeDelay(MediaCodec.BufferInfo bufferInfo, long startMillis) {
        while (bufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis() - startMillis) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /** {zh} 
     * 获取媒体类型的轨道
     *
     * @param extractor
     * @param mediaType
     * @return
     */
    /** {en} 
     * Get the track of the media type
     *
     * @param extractor
     * @param mediaType
     * @return
     */

    private static int getTrackIndex(MediaExtractor extractor, String mediaType) {
        int trackIndex = -1;
        try {
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat mediaFormat = extractor.getTrackFormat(i);
                String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith(mediaType)) {
                    trackIndex = i;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return trackIndex;
    }

    /** {zh} 
     * 视频解码线程
     */
    /** {en} 
     * Video decoding thread
     */

    private class VideoDecodeThread extends Thread {
        @Override
        public void run() {
            MediaExtractor videoExtractor = new MediaExtractor();
            MediaCodec videoCodec = null;
            MediaMetadataRetriever mediaRet = new MediaMetadataRetriever();
            if (filePath == null) return;
            try {
                mediaRet.setDataSource(filePath);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return;
            }
            try {
                videoExtractor.setDataSource(filePath);
                mediaRet.setDataSource(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int videoTrackIndex;
            //    {zh} 获取视频所在轨道        {en} Get the track where the video is  
            videoTrackIndex = getTrackIndex(videoExtractor, "video/");
            if (videoTrackIndex >= 0) {
                MediaFormat mediaFormat = videoExtractor.getTrackFormat(videoTrackIndex);

                int width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
                int height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
                float time = mediaFormat.getLong(MediaFormat.KEY_DURATION) / 1000000;
                String rotation = mediaRet.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                int videoRotation = 0;
                if (rotation != null) {
                    videoRotation = Integer.valueOf(rotation);
                }

                if (mListener != null) {
                    mListener.videoAspect(videoRotation % 180 == 0 ? width : height, videoRotation % 180 == 0 ? height : width, videoRotation);
                }
                videoExtractor.selectTrack(videoTrackIndex);
                try {
                    videoCodec = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME));
                    videoCodec.configure(mediaFormat, surface, null, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (videoCodec == null) {
                if (VERBOSE) {
                    Log.d(TAG, "video decoder is unexpectedly null");
                }
                return;
            }

            videoCodec.start();
            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            ByteBuffer[] inputBuffers = videoCodec.getInputBuffers();
            boolean isVideoEOS = false;

            try {
                while (!Thread.interrupted() && !cancel) {
                    if (isPlaying) {
                        //    {zh} 暂停        {en} Pause  
                        if (isPause) {
                            continue;
                        }
                        //    {zh} 将资源传递到解码器        {en} Passing resources to the decoder  
                        if (!isVideoEOS) {
                            isVideoEOS = decodeMediaData(videoExtractor, videoCodec, inputBuffers, TYPE_VIDEO);
                        }
                        //    {zh} 获取解码后的数据        {en} Get the decoded data  
                        int outputBufferIndex = videoCodec.dequeueOutputBuffer(videoBufferInfo, TIMEOUT_US);
                        switch (outputBufferIndex) {
                            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                                if (VERBOSE) {
                                    Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED");
                                }
                                break;
                            case MediaCodec.INFO_TRY_AGAIN_LATER:
                                if (VERBOSE) {
                                    Log.d(TAG, "INFO_TRY_AGAIN_LATER");
                                }
                                break;
                            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                                if (VERBOSE) {
                                    Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
                                }
                                break;
                            default:
                                //    {zh} 延迟解码        {en} Delayed decoding  
                                decodeDelay(videoBufferInfo, mLastStartTime - mDurationTime);
                                //    {zh} 释放资源        {en} Release resources  
                                videoCodec.releaseOutputBuffer(outputBufferIndex, true);
                                break;
                        }
                        //    {zh} 结尾        {en} End  
                        if ((videoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            Log.v(TAG, "buffer stream end");
                            if (null != mListener) {
                                mListener.onVideoEnd();
                            }
                            break;
                        }
                    }
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            //    {zh} 释放解码器        {en} Release the decoder
            videoCodec.stop();
            videoCodec.release();
            videoExtractor.release();
        }
    }

    /** {zh} 
     * 音频解码线程
     */
    /** {en} 
     * Audio decoding thread
     */

    private class AudioDecodeThread extends Thread {
        private int mInputBufferSize;
        private AudioTrack audioTrack;

        @Override
        public void run() {
            MediaExtractor audioExtractor = new MediaExtractor();
            MediaCodec audioCodec = null;
            try {
                audioExtractor.setDataSource(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < audioExtractor.getTrackCount(); i++) {
                try {
                    MediaFormat mediaFormat = audioExtractor.getTrackFormat(i);

                    String mime = mediaFormat.getString(MediaFormat.KEY_MIME);

                    if (mime.startsWith("audio/")) {
                        audioExtractor.selectTrack(i);

                        if (mAudioDataListener != null) {
                            mAudioDataListener.onAudioFormatExtracted(mediaFormat);

                        }
                        int audioChannels = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                        int audioSampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                        int minBufferSize = AudioTrack.getMinBufferSize(audioSampleRate,
                                (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO),
                                AudioFormat.ENCODING_PCM_16BIT);
                        int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                        mInputBufferSize = minBufferSize > 0 ? minBufferSize * 4 : maxInputSize;
                        int frameSizeInBytes = audioChannels * 2;
                        mInputBufferSize = (mInputBufferSize / frameSizeInBytes) * frameSizeInBytes;
                        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                audioSampleRate,
                                (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO),
                                AudioFormat.ENCODING_PCM_16BIT,
                                mInputBufferSize,
                                AudioTrack.MODE_STREAM);
                        audioTrack.play();
                        try {
                            audioCodec = MediaCodec.createDecoderByType(mime);
                            audioCodec.configure(mediaFormat, null, null, 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (audioCodec == null) {
                if (VERBOSE) {
                    Log.d(TAG, "audio decoder is unexpectedly null");
                }
                return;
            }
            audioCodec.start();
            final ByteBuffer[] buffers = audioCodec.getOutputBuffers();
            int sz = buffers[0].capacity();
            if (sz <= 0) {
                sz = mInputBufferSize;
            }
            byte[] mAudioOutTempBuf = new byte[sz];

            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();
            ByteBuffer[] inputBuffers = audioCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = audioCodec.getOutputBuffers();
            boolean isAudioEOS = false;
            while (!Thread.interrupted() && !cancel) {
                if (isPlaying) {
                    //    {zh} 暂停        {en} Pause  
                    if (isPause) {
                        continue;
                    }
                    //    {zh} 解码        {en} Decoding  
                    if (!isAudioEOS) {
                        isAudioEOS = decodeMediaData(audioExtractor, audioCodec, inputBuffers, TYPE_AUDIO);
                    }
                    //    {zh} 获取解码后的数据        {en} Get the decoded data  
                    int outputBufferIndex = audioCodec.dequeueOutputBuffer(audioBufferInfo, TIMEOUT_US);
                    switch (outputBufferIndex) {
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                            if (VERBOSE) {
                                Log.d(TAG, "INFO_OUTPUT_FORMAT_CHANGED");
                            }
                            break;
                        case MediaCodec.INFO_TRY_AGAIN_LATER:
                            if (VERBOSE) {
                                Log.d(TAG, "INFO_TRY_AGAIN_LATER");
                            }
                            break;
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                            outputBuffers = audioCodec.getOutputBuffers();
                            if (VERBOSE) {
                                Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
                            }
                            break;
                        default:
                            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                            //    {zh} 延时解码，跟视频时间同步        {en} Delayed decoding, synchronized with video time  
                            decodeDelay(audioBufferInfo, mLastStartTime - mDurationTime);
                            //    {zh} 如果解码成功，则将解码后的音频PCM数据用AudioTrack播放出来        {en} If the decoding is successful, the decoded audio PCM data is played out with AudioTrack  
                            if (audioBufferInfo.size > 0) {
                                if (mAudioOutTempBuf.length < audioBufferInfo.size) {
                                    mAudioOutTempBuf = new byte[audioBufferInfo.size];
                                }
                                outputBuffer.position(0);
                                outputBuffer.get(mAudioOutTempBuf, 0, audioBufferInfo.size);
                                outputBuffer.clear();

                                if (audioTrack != null)
                                    audioTrack.write(mAudioOutTempBuf, 0, audioBufferInfo.size);
                            }
                            //    {zh} 释放资源        {en} Release resources  
                            audioCodec.releaseOutputBuffer(outputBufferIndex, false);
                            break;
                    }

                    //    {zh} 结尾了        {en} End  
                    if ((audioBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        if (VERBOSE) {
                            Log.d(TAG, "BUFFER_FLAG_END_OF_STREAM");
                        }
                        break;
                    }
                }
            }

            //    {zh} 释放MediaCode 和AudioTrack        {en} Release MediaCode and AudioTrack  
            audioCodec.stop();
            audioCodec.release();
            audioExtractor.release();
            audioTrack.stop();
            audioTrack.release();
        }


    }
}
