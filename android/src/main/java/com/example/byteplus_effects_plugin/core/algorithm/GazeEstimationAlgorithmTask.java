package com.example.byteplus_effects_plugin.core.algorithm;

import static com.bytedance.labcv.effectsdk.BytedEffectConstants.BEF_DETECT_SMALL_MODEL;
import static com.bytedance.labcv.effectsdk.BytedEffectConstants.FaceAction.BEF_DETECT_FULL;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefFaceInfo;
import com.bytedance.labcv.effectsdk.BefGazeEstimationInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.FaceDetect;
import com.bytedance.labcv.effectsdk.GazeEstimation;

import java.nio.ByteBuffer;

/**
 * Created on 5/14/21 10:38 AM
 */
public class GazeEstimationAlgorithmTask extends AlgorithmTask<GazeEstimationAlgorithmTask.GazeEstimationResourceProvider, BefGazeEstimationInfo> {
    public static final AlgorithmTaskKey GAZE_ESTIMATION = AlgorithmTaskKeyFactory.create("gazeEstimation", true);
    public static final AlgorithmTaskKey GAZE_ESTIMATION_FOV = AlgorithmTaskKeyFactory.create("algorithm_fov");

    public static final int LINE_LEN = 0;

    public static final int FACE_DETECT_CONFIG = (BytedEffectConstants.FaceAction.BEF_FACE_DETECT |
            BytedEffectConstants.DetectMode.BEF_DETECT_MODE_VIDEO |
            BytedEffectConstants.FaceAction.BEF_DETECT_FULL);

    private FaceDetect mFaceDetector;
    private GazeEstimation mDetector;

    public GazeEstimationAlgorithmTask(Context context, GazeEstimationResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new GazeEstimation();
        mFaceDetector = new FaceDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("init gaze", ret)) return ret;


        ret = mDetector.setModel(BytedEffectConstants.GazeEstimationModelType.BEF_GAZE_ESTIMATION_MODEL1, mResourceProvider.gazeEstimationModel());
        if (!checkResult("init gaze", ret)) return ret;

        ret = mFaceDetector.init(mContext, mResourceProvider.faceModel(),
                BEF_DETECT_SMALL_MODEL | BEF_DETECT_FULL, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFace", ret)) return ret;
        mFaceDetector.setFaceDetectConfig(FACE_DETECT_CONFIG);
        return ret;
    }

    @Override
    public BefGazeEstimationInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        BefFaceInfo faceInfo = mFaceDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        if (faceInfo == null) {
            return null;
        }
        BefGazeEstimationInfo info = new BefGazeEstimationInfo();
        if (faceInfo != null && faceInfo.getFace106s().length > 0) {
            mDetector.setParam(BytedEffectConstants.GazeEstimationParamType.BEF_GAZE_ESTIMATION_CAMERA_FOV, getFloatConfig(GAZE_ESTIMATION_FOV));
            LogTimerRecord.RECORD("gazeEstimation");
            info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation, faceInfo, LINE_LEN);
            LogTimerRecord.STOP("gazeEstimation");
        }
        return info;
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        mFaceDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return GAZE_ESTIMATION;
    }

    public interface GazeEstimationResourceProvider extends AlgorithmResourceProvider {
        String faceModel();
        String gazeEstimationModel();
    }
}
