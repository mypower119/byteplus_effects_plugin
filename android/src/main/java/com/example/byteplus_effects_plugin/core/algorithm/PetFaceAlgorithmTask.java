package com.example.byteplus_effects_plugin.core.algorithm;

import static com.bytedance.labcv.effectsdk.BytedEffectConstants.PetFaceDetectConfig.BEF_PET_FACE_DETECT_CAT;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.PetFaceDetectConfig.BEF_PET_FACE_DETECT_DOG;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefPetFaceInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.PetFaceDetect;

import java.nio.ByteBuffer;

/**
 * Created on 5/13/21 6:06 PM
 */
public class PetFaceAlgorithmTask extends AlgorithmTask<PetFaceAlgorithmTask.PetFaceResourceProvider, BefPetFaceInfo> {

    public static final AlgorithmTaskKey PET_FACE = AlgorithmTaskKeyFactory.create("petFace", true);

    public static final int DETECT_CONFIG = BEF_PET_FACE_DETECT_CAT | BEF_PET_FACE_DETECT_DOG;

    private PetFaceDetect mDetector;

    public PetFaceAlgorithmTask(Context context, PetFaceResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new PetFaceDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(mContext, mResourceProvider.petFaceModel(), DETECT_CONFIG,
                licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initPetFace", ret)) return ret;

        return ret;
    }

    @Override
    public BefPetFaceInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("petFace");
        BefPetFaceInfo petFaceInfo = mDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("petFace");
        return petFaceInfo;
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
        return PET_FACE;
    }

    public interface PetFaceResourceProvider extends AlgorithmResourceProvider {
        String petFaceModel();
    }
}
