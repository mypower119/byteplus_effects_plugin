package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefSkyInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.SkySegment;

import java.nio.ByteBuffer;

/**
 * Created on 5/13/21 5:45 PM
 */
public class SkySegAlgorithmTask extends AlgorithmTask<SkySegAlgorithmTask.SkeSegResourceProvider, BefSkyInfo> {

    public static final AlgorithmTaskKey SKY_SEGMENT = AlgorithmTaskKeyFactory.create("skySegment", true);

    public static final boolean FLIP_ALPHA = false;
    public static final boolean SYK_CHECK = true;
    private SkySegment mDetector;

    public SkySegAlgorithmTask(Context context, SkeSegResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new SkySegment();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(mContext, mResourceProvider.skySegModel(), licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initSkySegment", ret)) return ret;

        ret = mDetector.setParam(preferBufferSize()[0], preferBufferSize()[1]);
        if (!checkResult("SetSkySegmentParam", ret)){
            return ret;
        }
        return ret;
    }

    @Override
    public BefSkyInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("skySegment");
        BefSkyInfo skyInfo = mDetector.detectSky(buffer, pixlFormat, width, height, stride, rotation, FLIP_ALPHA, SYK_CHECK);
        LogTimerRecord.STOP("skySegment");
        return skyInfo;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[]{128, 224};
    }

    @Override
    public AlgorithmTaskKey key() {
        return SKY_SEGMENT;
    }

    public interface SkeSegResourceProvider extends AlgorithmResourceProvider {
        String skySegModel();
    }
}
