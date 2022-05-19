package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BachSkeletonDetect;
import com.bytedance.labcv.effectsdk.BefBachSkeletonInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;

import java.nio.ByteBuffer;

public class BachSkeletonAlgorithmTask extends AlgorithmTask<BachSkeletonAlgorithmTask.BachSkeletonResourceProvider, BefBachSkeletonInfo> {

    public static final AlgorithmTaskKey BACH_SKELETON = AlgorithmTaskKeyFactory.create("bachSkeleton", true);

    private BachSkeletonDetect mDetector;

    public BachSkeletonAlgorithmTask(Context context, BachSkeletonResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new BachSkeletonDetect();
    }

    @Override
    public int initTask() {
        boolean onlineLicenseFlag = mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE;
        int ret = mDetector.init(mContext, mResourceProvider.bachSkeletonModel(), mLicenseProvider.getLicensePath(), onlineLicenseFlag);
        if (!checkResult("initBachSkeleton", ret)) return ret;
        return ret;
    }

    @Override
    public BefBachSkeletonInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("bachSkeleton");
        BefBachSkeletonInfo info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("bachSkeleton");
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
        return BACH_SKELETON;
    }


    public interface BachSkeletonResourceProvider extends AlgorithmResourceProvider {
        String bachSkeletonModel();
    }
}
