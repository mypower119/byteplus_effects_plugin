package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefC2Info;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.C2Detect;

import java.nio.ByteBuffer;

/**
 * Created on 5/14/21 10:36 AM
 */
public class C2AlgorithmTask extends AlgorithmTask<C2AlgorithmTask.C2ResourceProvider, BefC2Info> {

    public static final AlgorithmTaskKey C2 = AlgorithmTaskKeyFactory.create("c2", true);
    private C2Detect mDetector;

    public C2AlgorithmTask(Context context, C2ResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new C2Detect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(BytedEffectConstants.C2ModelType.BEF_AI_kC2Model1,
                mResourceProvider.c2Model(), licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initC2", ret)) return ret;

        ret = mDetector.setParam(BytedEffectConstants.C2ParamType.BEF_AI_C2_USE_VIDEO_MODE, 1);
        if (!checkResult("initC2", ret)) return ret;
        ret = mDetector.setParam(BytedEffectConstants.C2ParamType.BEF_AI_C2_USE_MultiLabels, 1);
        if (!checkResult("initC2", ret)) return ret;
        return ret;
    }

    @Override
    public BefC2Info process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {

        LogTimerRecord.RECORD("c2");
        BefC2Info info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("c2");
        return info;
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
        return C2;
    }

    public interface C2ResourceProvider extends AlgorithmResourceProvider {
        String c2Model();
    }
}
