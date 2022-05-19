package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.HairParser;

import java.nio.ByteBuffer;

/**
 * Created on 5/13/21 3:49 PM
 */
public class HairParserAlgorithmTask extends AlgorithmTask<HairParserAlgorithmTask.HairParserResourceProvider, HairParser.HairMask> {
    public static final AlgorithmTaskKey HAIR_PARSER = AlgorithmTaskKeyFactory.create("hairParser", true);

    public static final boolean FLIP_ALPHA = false;

    private HairParser mDetector;

    public HairParserAlgorithmTask(Context context, HairParserResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new HairParser();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(mContext, mResourceProvider.hairParserModel(), mLicenseProvider.getLicensePath(),
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initHairParser", ret)) return ret;

        ret = mDetector.setParam((int)preferBufferSize()[0], (int)preferBufferSize()[1], true, true);
        if (!checkResult("initHairParser", ret)) return ret;
        return ret;
    }

    @Override
    public HairParser.HairMask process(ByteBuffer buffer, int width, int height,
                                       int stride, BytedEffectConstants.PixlFormat pixlFormat,
                                       BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("parseHair");
        HairParser.HairMask hairMask = mDetector.parseHair(buffer, pixlFormat, width, height, stride,
                rotation, FLIP_ALPHA);
        LogTimerRecord.STOP("parseHair");
        return hairMask;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[] {128, 224};
    }

    @Override
    public AlgorithmTaskKey key() {
        return HAIR_PARSER;
    }

    public interface HairParserResourceProvider extends AlgorithmResourceProvider {
        String hairParserModel();
    }
}
