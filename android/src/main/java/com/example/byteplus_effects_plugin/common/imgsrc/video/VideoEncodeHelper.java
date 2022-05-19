package com.example.byteplus_effects_plugin.common.imgsrc.video;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.opengl.EGLContext;

import com.example.byteplus_effects_plugin.core.util.LogUtils;

import java.nio.ByteBuffer;

/**
 * Created  on 2021/5/12 6:34 PM
 */
public class VideoEncodeHelper implements SimplePlayer.IAudioDataListener{
    private static final int RECORDING_OFF = 0;
    private static final int RECORDING_ON = 1;
    private static final int RECORDING_RESUMED = 2;
    private boolean mRecordingEnabled;
    private TextureMovieEncoder mVideoEncoder = new TextureMovieEncoder();
    private int mRecordingStatus = RECORDING_OFF;
    private final int mBitRateFactor = 5;


    public VideoEncodeHelper() {

    }

    public String getVideoPath(){
        return mVideoEncoder.getOutputPath();
    }


    @Override
    public void onAudioData(ByteBuffer buffer, MediaCodec.BufferInfo bufferInfo) {
        if (null != mVideoEncoder) {
            LogUtils.e("addAudioData");
            mVideoEncoder.getMediaMuxerManager().addAudioData(buffer, bufferInfo);

        }
    }

    /** {zh} 
     * 录制纹理数据
     * @param context
     * @param dstTexture
     * @param mVideoWidth
     * @param mVideoHeight
     * @param timeStamp 时间戳
     */
    /** {en} 
     * Record texture data
     * @param context
     * @param dstTexture
     * @param mVideoWidth
     * @param mVideoHeight
     * @param timeStamp  timestamp
     */

    public void onVideoData( EGLContext context,int dstTexture, int mVideoWidth, int mVideoHeight, long timeStamp){
        switch (mRecordingStatus) {
            case RECORDING_OFF:
                LogUtils.d("START recording");
                // start recording
                mVideoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                        null, mVideoWidth, mVideoHeight, mBitRateFactor*mVideoWidth * mVideoHeight, context));
                mRecordingStatus = RECORDING_ON;
                break;
            case RECORDING_RESUMED:
                LogUtils.d("RESUME recording");
                mVideoEncoder.updateSharedContext(context);
                mRecordingStatus = RECORDING_ON;
                break;
            case RECORDING_ON:
                // yay
                break;
            default:
                throw new RuntimeException("unknown status " + mRecordingStatus);
        }


        // Set the video encoder's texture name.  We only need to do this once, but in the
        // current implementation it has to happen after the video encoder is started, so
        // we just do it here.
        //
        mVideoEncoder.setTextureId(dstTexture);

        // Tell the video encoder thread that a new frame is available.
        // This will be ignored if we're not actually recording.
        mVideoEncoder.frameAvailable(timeStamp);
    }

    @Override
    public void onAudioFormatExtracted(MediaFormat format) {
        if (null != mVideoEncoder) {
            LogUtils.e("addAudioTrack");

            mVideoEncoder.getMediaMuxerManager().addAudioTrack(format);

        }


    }


    public void destroy(){
        if (null != mVideoEncoder) {
            mVideoEncoder.stopRecording();

        }
    }
}
