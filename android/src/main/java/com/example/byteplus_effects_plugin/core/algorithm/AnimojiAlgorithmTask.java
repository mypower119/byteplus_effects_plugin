package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.AnimojiDetect;
import com.bytedance.labcv.effectsdk.BefAnimojiInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;

import java.nio.ByteBuffer;

/**
 * Created on 5/14/21 11:19 AM
 */
public class AnimojiAlgorithmTask extends AlgorithmTask<AnimojiAlgorithmTask.AnimojiResourceProvider, BefAnimojiInfo> {

    public static final AlgorithmTaskKey ANIMOJI = AlgorithmTaskKeyFactory.create("animoji", true);
    public static final int INPUT_WIDTH = 256;
    public static final int INPUT_HEIGHT = 256;

    private AnimojiDetect mDetector;

    public AnimojiAlgorithmTask(Context context, AnimojiResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new AnimojiDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(licensePath, mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("init animoji", ret)) return ret;
        ret = mDetector.setModel(mResourceProvider.animojiModel(), INPUT_WIDTH, INPUT_HEIGHT);
        if (!checkResult("init animoji set model", ret)) return ret;
        return ret;
    }

    @Override
    public BefAnimojiInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        BefAnimojiInfo info;

        LogTimerRecord.RECORD("animoji");
        info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("animoji");

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
        return ANIMOJI;
    }

    public interface AnimojiResourceProvider extends AlgorithmResourceProvider {
        String animojiModel();
    }
}
