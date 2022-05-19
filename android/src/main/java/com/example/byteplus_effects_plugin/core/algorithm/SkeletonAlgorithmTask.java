package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefSkeletonInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.SkeletonDetect;

import java.nio.ByteBuffer;

/**
 * Created on 5/13/21 5:39 PM
 */
public class SkeletonAlgorithmTask extends AlgorithmTask<SkeletonAlgorithmTask.SkeletonResourceProvider, BefSkeletonInfo> {
    public static final AlgorithmTaskKey SKELETON = AlgorithmTaskKeyFactory.create("skeleton", true);

    private SkeletonDetect mDetector;

    public SkeletonAlgorithmTask(Context context, SkeletonResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new SkeletonDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(mContext, mResourceProvider.skeletonModel(), licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initSkeleton", ret)) return ret;

        return ret;
    }

    @Override
    public BefSkeletonInfo process(ByteBuffer buffer, int width, int height, int stride,
                                      BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("detectSkeleton");
        BefSkeletonInfo skeletonInfo = mDetector.detectSkeleton(buffer, pixlFormat, width, height,
                stride, rotation);
        LogTimerRecord.STOP("detectSkeleton");
        return skeletonInfo;
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
        return SKELETON;
    }

    public interface SkeletonResourceProvider extends AlgorithmResourceProvider {
        String skeletonModel();
    }
}
