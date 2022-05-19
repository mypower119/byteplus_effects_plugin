package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefDynamicGestureInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.DynamicGestureDetect;

import java.nio.ByteBuffer;

public class DynamicGestureAlgorithmTask extends AlgorithmTask<DynamicGestureAlgorithmTask.DynamicGestureResourceProvider, BefDynamicGestureInfo> {

    public static final AlgorithmTaskKey DYNAMIC_GESTURE = AlgorithmTaskKeyFactory.create("dynamicGesture", true);

    private DynamicGestureDetect mDetector;

    public DynamicGestureAlgorithmTask(Context context, DynamicGestureResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new DynamicGestureDetect();
    }

    @Override
    public int initTask() {
        boolean onlineLicenseFlag = mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE;
        int ret = mDetector.init(mContext, mResourceProvider.dynamicGestureModel(), mLicenseProvider.getLicensePath(), onlineLicenseFlag);
        if (!checkResult("initDynamicGesture", ret)) return ret;
        return ret;
    }

    @Override
    public BefDynamicGestureInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("dynamicGesture");
        BefDynamicGestureInfo info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("dynamicGesture");
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
        return DYNAMIC_GESTURE;
    }


    public interface DynamicGestureResourceProvider extends AlgorithmResourceProvider {
        String dynamicGestureModel();
    }
}
