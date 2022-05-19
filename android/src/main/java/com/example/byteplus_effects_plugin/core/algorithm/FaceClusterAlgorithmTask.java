package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.FaceCluster;

import java.nio.ByteBuffer;

/**
 * Created on 5/13/21 6:37 PM
 */
public class FaceClusterAlgorithmTask extends AlgorithmTask<AlgorithmResourceProvider, Object> {

    public static final AlgorithmTaskKey FACE_CLUSTER = AlgorithmTaskKeyFactory.create("faceCluster", true);

    private FaceCluster mDetector;

    public FaceClusterAlgorithmTask(Context context, AlgorithmResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new FaceCluster();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(mContext, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initCluster", ret)) return ret;
        return ret;
    }

    @Override
    public Object process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        return null;
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
        return FACE_CLUSTER;
    }

    public int[] cluster(float[][] features, int size) {
        return mDetector.cluster(features, size);
    }
}
