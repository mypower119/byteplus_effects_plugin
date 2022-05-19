package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefVideoClsInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.VideoClsDetect;

import java.nio.ByteBuffer;

/**
 * Created on 5/14/21 10:50 AM
 */
public class VideoClsAlgorithmTask extends AlgorithmTask<VideoClsAlgorithmTask.VideoClsResourceProvider, BefVideoClsInfo> {

    public static final AlgorithmTaskKey VIDEO_CLS = AlgorithmTaskKeyFactory.create("videoCls", true);
    public static final int FRAME_INTERVAL = 5;

    private VideoClsDetect mDetector;
    private long mFrameCount = 0;

    public VideoClsAlgorithmTask(Context context, VideoClsResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new VideoClsDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(BytedEffectConstants.VideoClsModelType.BEF_AI_kVideoClsModel1,
                mResourceProvider.videoClsModel(),
                licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initVideoCls", ret)) return ret;
        return ret;
    }

    @Override
    public BefVideoClsInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("videoCls");
        boolean isLast = (++mFrameCount % FRAME_INTERVAL) == 0;
        BefVideoClsInfo info = mDetector.detect(buffer, pixlFormat, width, height, stride, isLast, rotation);
        LogTimerRecord.STOP("videoCls");
        if (isLast) {
            return info;
        }
        return null;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return VIDEO_CLS;
    }

    public interface VideoClsResourceProvider extends AlgorithmResourceProvider {
        String videoClsModel();
    }
}
