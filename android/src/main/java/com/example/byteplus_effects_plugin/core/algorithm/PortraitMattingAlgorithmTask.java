package com.example.byteplus_effects_plugin.core.algorithm;

import static com.bytedance.labcv.effectsdk.BytedEffectConstants.PortraitMatting.BEF_PORTAITMATTING_SMALL_MODEL;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.PortraitMatting;

import java.nio.ByteBuffer;

/**
 * Created on 5/13/21 5:41 PM
 */
public class PortraitMattingAlgorithmTask extends AlgorithmTask<PortraitMattingAlgorithmTask.PortraitMattingResourceProvider, PortraitMatting.MattingMask> {
    public static final AlgorithmTaskKey PORTRAIT_MATTING = AlgorithmTaskKeyFactory.create("portraitMatting", true);

    public static final boolean FLIP_ALPHA = false;

    private PortraitMatting mDetector;

    public PortraitMattingAlgorithmTask(Context context, PortraitMattingResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new PortraitMatting();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(mContext, mResourceProvider.portraitMattingModel(), BEF_PORTAITMATTING_SMALL_MODEL, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initPortraitMatting", ret)) return ret;

        return ret;
    }

    @Override
    public PortraitMatting.MattingMask process(ByteBuffer buffer, int width, int height,
                                                  int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("detectMatting");
        PortraitMatting.MattingMask mattingMask = mDetector.detectMatting(buffer, pixlFormat, width, height, stride, rotation, FLIP_ALPHA);
        LogTimerRecord.STOP("detectMatting");
        return mattingMask;
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
        return PORTRAIT_MATTING;
    }

    public interface PortraitMattingResourceProvider extends AlgorithmResourceProvider {
        String portraitMattingModel();
    }
}
