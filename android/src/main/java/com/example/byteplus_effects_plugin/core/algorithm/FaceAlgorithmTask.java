package com.example.byteplus_effects_plugin.core.algorithm;

import static com.bytedance.labcv.effectsdk.BytedEffectConstants.BEF_DETECT_SMALL_MODEL;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceAction.BEF_DETECT_FULL;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_AGE;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_ATTRACTIVE;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_CONFUSE;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_EXPRESSION;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_GENDER;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceAttribute.BEF_FACE_ATTRIBUTE_HAPPINESS;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceExtraModel.BEF_MOBILE_FACE_240_DETECT;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceExtraModel.BEF_MOBILE_FACE_280_DETECT;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceSegmentConfig.BEFF_MOBILE_FACE_REST_MASK;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceSegmentConfig.BEF_MOBILE_FACE_MOUTH_MASK;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceSegmentConfig.BEF_MOBILE_FACE_TEETH_MASK;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefFaceInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.FaceDetect;

import java.nio.ByteBuffer;

/**
 * Created on 5/7/21 3:22 PM
 */
public class FaceAlgorithmTask extends AlgorithmTask<FaceAlgorithmTask.FaceResourceProvider, BefFaceInfo> {
    public static final AlgorithmTaskKey FACE = AlgorithmTaskKey.createKey("face", true);
    public static final AlgorithmTaskKey FACE_280 = AlgorithmTaskKey.createKey("face280");
    public static final AlgorithmTaskKey FACE_ATTR = AlgorithmTaskKey.createKey("faceAttr");
    public static final AlgorithmTaskKey FACE_MASK = AlgorithmTaskKey.createKey("faceMask");
    public static final AlgorithmTaskKey MOUTH_MASK = AlgorithmTaskKey.createKey("mouthMask");
    public static final AlgorithmTaskKey TEETH_MASK = AlgorithmTaskKey.createKey("teethMask");

    private final FaceDetect mDetector;

    public FaceAlgorithmTask(Context context, FaceResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new FaceDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(mContext, mResourceProvider.faceModel(),
                BEF_DETECT_SMALL_MODEL | BEF_DETECT_FULL, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFace", ret)) return ret;
        ret = mDetector.initExtra(mContext, mResourceProvider.faceExtraModel(), BEF_MOBILE_FACE_280_DETECT);
        if (!checkResult("initFaceExtra", ret)) return ret;

        ret = mDetector.initAttri(mContext, mResourceProvider.faceAttrModel(), licensePath,
                                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFaceAttr", ret)) {return ret;}


        return ret;
    }

    @Override
    public BefFaceInfo process(ByteBuffer buffer, int width, int height, int stride,
                               BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("detectFace");
        BefFaceInfo faceInfo = mDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("detectFace");
        if (getBoolConfig(FACE_MASK)) {
            LogTimerRecord.RECORD("detectFaceMask");
            mDetector.getFaceMask(faceInfo, BytedEffectConstants.FaceSegmentType.BEF_FACE_FACE_MASK);
            LogTimerRecord.STOP("detectFaceMask");
        }
        if (getBoolConfig(MOUTH_MASK)) {
            LogTimerRecord.RECORD("detectMouthMask");
            mDetector.getFaceMask(faceInfo, BytedEffectConstants.FaceSegmentType.BEF_FACE_MOUTH_MASK);
            LogTimerRecord.STOP("detectMouthMask");
        }
        if (getBoolConfig(TEETH_MASK)) {
            LogTimerRecord.RECORD("detectTeethMask");
            mDetector.getFaceMask(faceInfo, BytedEffectConstants.FaceSegmentType.BEF_FACE_TEETH_MASK);
            LogTimerRecord.STOP("detectTeethMask");
        }
        return faceInfo;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        return 0;
    }

    @Override
    public void setConfig(AlgorithmTaskKey key, Object p) {
        super.setConfig(key, p);

        int detectConfig = (BytedEffectConstants.FaceAction.BEF_FACE_DETECT |
                BytedEffectConstants.DetectMode.BEF_DETECT_MODE_VIDEO |
                BytedEffectConstants.FaceAction.BEF_DETECT_FULL);
        if (getBoolConfig(FACE_280)) {
            detectConfig |= BEF_MOBILE_FACE_280_DETECT;
        }

        if (getBoolConfig(FACE_MASK)) {
            detectConfig |= BEF_MOBILE_FACE_240_DETECT | BEFF_MOBILE_FACE_REST_MASK;
        }
        if (getBoolConfig(MOUTH_MASK)) {
            detectConfig |= BEF_MOBILE_FACE_240_DETECT | BEF_MOBILE_FACE_MOUTH_MASK;
        }
        if (getBoolConfig(TEETH_MASK)) {
            detectConfig |= BEF_MOBILE_FACE_240_DETECT | BEF_MOBILE_FACE_TEETH_MASK;
        }

        mDetector.setFaceDetectConfig(detectConfig);

        int attrConfig = 0;
        if (getBoolConfig(FACE_ATTR)) {
            attrConfig |= (BEF_FACE_ATTRIBUTE_EXPRESSION | BEF_FACE_ATTRIBUTE_HAPPINESS |
                    BEF_FACE_ATTRIBUTE_AGE | BEF_FACE_ATTRIBUTE_GENDER | BEF_FACE_ATTRIBUTE_ATTRACTIVE |
                    BEF_FACE_ATTRIBUTE_CONFUSE);
        }
        mDetector.setAttriDetectConfig(attrConfig);
    }

    @Override
    public int[] preferBufferSize() {
        if (getBoolConfig(FACE_ATTR) || getBoolConfig(FACE_280) || getBoolConfig(FACE_MASK)) {
            return new int[]{360, 640};
        } else {
            return new int[]{128, 224};
        }
    }

    @Override
    public AlgorithmTaskKey key() {
        return FACE;
    }

    public interface FaceResourceProvider extends AlgorithmResourceProvider {
        String faceModel();

        String faceExtraModel();

        String faceAttrModel();
    }
}
