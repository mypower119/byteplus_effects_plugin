package com.example.byteplus_effects_plugin.core.algorithm;

import static com.bytedance.labcv.effectsdk.BytedEffectConstants.BEF_DETECT_SMALL_MODEL;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceAction.BEF_DETECT_FULL;

import android.content.Context;
import android.os.Build;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefDistanceInfo;
import com.bytedance.labcv.effectsdk.BefFaceInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.FaceDetect;
import com.bytedance.labcv.effectsdk.HumanDistance;

import java.nio.ByteBuffer;

/**
 * Created on 5/13/21 5:50 PM
 */
public class HumanDistanceAlgorithmTask extends AlgorithmTask<HumanDistanceAlgorithmTask.HumanDistanceResourceProvider, BefDistanceInfo> {

    public static final AlgorithmTaskKey HUMAN_DISTANCE = AlgorithmTaskKeyFactory.create("humanDistance", true);
    // TODO: move to algorithm task, for global config
    public static final AlgorithmTaskKey HUMAN_DISTANCE_FRONT = AlgorithmTaskKeyFactory.create("humanDistanceFront");
    public static final AlgorithmTaskKey HUMAN_DISTANCE_FOV = AlgorithmTaskKeyFactory.create("algorithm_fov");

    public static final int FACE_DETECT_CONFIG = (BytedEffectConstants.FaceAction.BEF_FACE_DETECT |
            BytedEffectConstants.DetectMode.BEF_DETECT_MODE_VIDEO |
            BytedEffectConstants.FaceAction.BEF_DETECT_FULL);
    private FaceDetect mFaceDetector;
    private HumanDistance mDetector;
    private float mFov;
    private boolean mIsFront;

    public HumanDistanceAlgorithmTask(Context context, HumanDistanceResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new HumanDistance();
        mFaceDetector = new FaceDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(mContext, mResourceProvider.faceModel(),
                mResourceProvider.faceAttrModel(),
                mResourceProvider.humanDistanceModel(),
                licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initHumanDistance", ret)) return ret;

        ret = mFaceDetector.init(mContext, mResourceProvider.faceModel(),
                BEF_DETECT_SMALL_MODEL | BEF_DETECT_FULL,
                licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFace", ret)) return ret;
        mFaceDetector.setFaceDetectConfig(FACE_DETECT_CONFIG);
        return ret;
    }

    @Override
    public BefDistanceInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        BefFaceInfo faceInfo = mFaceDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        if (faceInfo == null) {
            return null;
        }
        String deviceName = Build.MODEL;
        mDetector.setParam(BytedEffectConstants.HumanDistanceParamType.BEF_HumanDistanceCameraFov.getValue(), getFloatConfig(HUMAN_DISTANCE_FOV));
        LogTimerRecord.RECORD("humanDistance");
        BefDistanceInfo distanceInfo = mDetector.detectDistance(buffer, pixlFormat, width, height, stride, deviceName, getBoolConfig(HUMAN_DISTANCE_FRONT), rotation);
        LogTimerRecord.STOP("humanDistance");
        return distanceInfo;
    }

    @Override
    public int destroyTask() {
        mFaceDetector.release();
        mDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return HUMAN_DISTANCE;
    }

    public interface HumanDistanceResourceProvider extends AlgorithmResourceProvider {

        String faceModel();

        String faceAttrModel();

        String humanDistanceModel();
    }
}
