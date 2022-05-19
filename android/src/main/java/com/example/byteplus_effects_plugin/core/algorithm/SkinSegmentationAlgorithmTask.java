package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefSkinSegInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.SkinSegmentation;

import java.nio.ByteBuffer;

public class SkinSegmentationAlgorithmTask extends AlgorithmTask<SkinSegmentationAlgorithmTask.SkinSegmentationResourceProvider, BefSkinSegInfo> {

    public static final AlgorithmTaskKey SKIN_SEGMENTATION = AlgorithmTaskKeyFactory.create("skinSegmentation", true);

    private SkinSegmentation mDetector;

    public SkinSegmentationAlgorithmTask(Context context, SkinSegmentationResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new SkinSegmentation();
    }

    @Override
    public int initTask() {
        boolean onlineLicenseFlag = mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE;
        int ret = mDetector.init(mContext, mResourceProvider.skinSegmentationModel(), mLicenseProvider.getLicensePath(), onlineLicenseFlag);
        if (!checkResult("initSkinSegmentation", ret)) return ret;
        return ret;
    }

    @Override
    public BefSkinSegInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("skinSegmentation");
        BefSkinSegInfo info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("skinSegmentation");
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
        return SKIN_SEGMENTATION;
    }


    public interface SkinSegmentationResourceProvider extends AlgorithmResourceProvider {
        String skinSegmentationModel();
    }
}
