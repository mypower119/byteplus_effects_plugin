package com.example.byteplus_effects_plugin.common.imgsrc.video;

import static com.example.byteplus_effects_plugin.common.utils.FileUtils.generateVideoFile;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import androidx.annotation.NonNull;

import com.example.byteplus_effects_plugin.core.util.LogUtils;

import java.io.IOException;
import java.nio.ByteBuffer;


public class MediaMuxerManager {

    private MediaMuxer mMediaMuxer;
    private String mOutputPath;
    private int mAudioTrack;
    private int mVideoTrack;
    private boolean mVideoTrackReady = false;
    private boolean mAudioTrackReady = false;
    private boolean mStart = false;


    public MediaMuxerManager() {
        try {
            mOutputPath = generateVideoFile();
            mMediaMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getOutputPath() {
        return mOutputPath;
    }

    public void addAudioTrack(MediaFormat format) {
        try {
            mAudioTrack = mMediaMuxer.addTrack(format);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        mAudioTrackReady = true;
        ready();


    }


    public void addVideoTrack(MediaFormat format) {
        try {
            mVideoTrack = mMediaMuxer.addTrack(format);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        mVideoTrackReady = true;
        ready();


    }

    public void ready() {
        if (mAudioTrackReady && mVideoTrackReady) {
            mMediaMuxer.start();
            mStart = true;
        }

    }

    public void addVideoData(@NonNull ByteBuffer byteBuffer, @NonNull MediaCodec.BufferInfo bufferInfo) {
        if (mStart) {
            mMediaMuxer.writeSampleData(mVideoTrack, byteBuffer, bufferInfo);

        }
    }

    public void addAudioData(@NonNull ByteBuffer byteBuffer, @NonNull MediaCodec.BufferInfo bufferInfo) {
        if (mStart) {
            mMediaMuxer.writeSampleData(mAudioTrack, byteBuffer, bufferInfo);
        }
    }


    /**
     * release muxer, will auto stop first
     */
    public void release() {
        if (mMediaMuxer != null) {
            try {
                mMediaMuxer.release();
            } catch (IllegalStateException e) {
                LogUtils.e(e.getMessage());
            }
            mMediaMuxer = null;
            mStart = false;
            mVideoTrackReady = false;
            mAudioTrackReady = false;
        }
    }


}
