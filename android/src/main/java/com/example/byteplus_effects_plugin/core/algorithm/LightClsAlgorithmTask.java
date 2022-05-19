package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefLightclsInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.LightClsDetect;

import java.nio.ByteBuffer;

/**
 * Created on 5/13/21 5:48 PM
 */
public class LightClsAlgorithmTask extends AlgorithmTask<LightClsAlgorithmTask.LightClsResourceProvider, BefLightclsInfo> {
    public static final AlgorithmTaskKey LIGHT_CLS = AlgorithmTaskKeyFactory.create("lightCls", true);

    private static final int FPS = 5;

    private LightClsDetect mDetector;

    public LightClsAlgorithmTask(Context context, LightClsResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new LightClsDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(mContext, mResourceProvider.lightClsModel(), licensePath, FPS,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initLightCls", ret)) return ret;

        return ret;
    }

    @Override
    public BefLightclsInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("detectLight");
        BefLightclsInfo lightclsInfo = mDetector.detectLightCls(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("detectLight");
        return lightclsInfo;
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
        return LIGHT_CLS;
    }

    public interface LightClsResourceProvider extends AlgorithmResourceProvider {
        String lightClsModel();
    }
}
