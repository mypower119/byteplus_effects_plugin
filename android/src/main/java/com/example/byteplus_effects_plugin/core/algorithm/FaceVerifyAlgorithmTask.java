package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.bytedance.labcv.effectsdk.BefFaceFeature;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.FaceVerify;

import java.nio.ByteBuffer;

/**
 * Created on 5/13/21 6:13 PM
 */
public class FaceVerifyAlgorithmTask extends AlgorithmTask<FaceVerifyAlgorithmTask.FaceVerifyResourceProvider, Object> {

    public static final AlgorithmTaskKey FACE_VERIFY = AlgorithmTaskKeyFactory.create("faceVerify", true);

    private static final int MAX_FACE = 10;

    private FaceVerify mDetector;
    public FaceVerifyAlgorithmTask(Context context, FaceVerifyResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new FaceVerify();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(mContext, mResourceProvider.faceModel(),
                mResourceProvider.faceVerifyModel(), MAX_FACE, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFaceVerify", ret)) return ret;

        return ret;
    }

    @Override
    public Object process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        return new FaceVerifyCaptureResult(buffer, width, height);
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
        return FACE_VERIFY;
    }

    public BefFaceFeature extractFeatureSingle(ByteBuffer buffer, BytedEffectConstants.PixlFormat pixelFormat, int width, int height, int stride, BytedEffectConstants.Rotation rotation) {
        return mDetector.extractFeatureSingle(buffer, pixelFormat, width, height, stride, rotation);
    }

    public BefFaceFeature extractFeature(ByteBuffer buffer, BytedEffectConstants.PixlFormat pixelFormat, int width, int height, int stride, BytedEffectConstants.Rotation rotation) {
        return mDetector.extractFeature(buffer, pixelFormat, width, height, stride, rotation);
    }

    public double verify(float[] f1, float[] f2) {
        return mDetector.verify(f1, f2);
    }

    public double distToScore(double dist) {
        return mDetector.distToScore(dist);
    }

    public interface FaceVerifyResourceProvider extends AlgorithmResourceProvider {
        String faceModel();
        String faceVerifyModel();
    }


    public static class FaceVerifyCaptureResult {
        public ByteBuffer buffer;
        public int width;
        public int height;

        public FaceVerifyCaptureResult(ByteBuffer buffer, int width, int height) {
            this.buffer = buffer;
            this.width = width;
            this.height = height;
        }
    }
}
