package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.ActionRecognition;
import com.bytedance.labcv.effectsdk.BefActionRecognitionInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;

import java.nio.ByteBuffer;

/**
 * Created on 2021/7/21 11:48
 */
public class ActionRecognitionAlgorithmTask extends AlgorithmTask<ActionRecognitionAlgorithmTask.ActionRecognitionResourceProvider, BefActionRecognitionInfo> {
    public static final AlgorithmTaskKey ACTION_RECOGNITION = AlgorithmTaskKeyFactory.create("actionRecognition", true);
    public static final AlgorithmTaskKey ACTION_RECOGNITION_TYPE = AlgorithmTaskKeyFactory.create("actionRecognitionType");

    private final ActionRecognition mDetector;
    private int mConfirmTime = 3000;

    public ActionRecognitionAlgorithmTask(Context context, ActionRecognitionResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new ActionRecognition();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.init(mContext, mResourceProvider.actionRecognitionModelPath(), licensePath,
                                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("init action recognition", ret)) return ret;
        return ret;
    }

    public int initTask(ActionType type) {
        int ret = initTask();
        if (!checkResult("init action recognition", ret)) return ret;
        ret = setActionType(type);
        if (!checkResult("set action recognition template", ret)) return ret;
        return ret;
    }

    public BefActionRecognitionInfo.PoseDetectResult startPose(
            ByteBuffer buffer, int width, int height, int stride,
            BytedEffectConstants.PixlFormat pixlFormat,
            BefActionRecognitionInfo.ActionRecognitionPoseType poseType,
            BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("actionRecognitionPose");
        BefActionRecognitionInfo.PoseDetectResult ret = mDetector.detectPose(buffer,
                pixlFormat, width, height, stride, poseType, rotation);
        LogTimerRecord.STOP("actionRecognitionPose");
        return ret;
    }

    @Override
    public BefActionRecognitionInfo process(ByteBuffer buffer, int width, int height, int stride,
                                            BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("actionRecognition");
        BefActionRecognitionInfo ret = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation, mConfirmTime);
        LogTimerRecord.STOP("actionRecognition");
        return ret;
    }

    public int setActionType(ActionType actionType) {
        String templatePath = mResourceProvider.templateForActionType(actionType);
        if (templatePath == null) {
            return -1;
        }
        updateConfirmTime(actionType);
        return mDetector.setTemplate(templatePath);
    }

    @Override
    public int destroyTask() {
        mDetector.destroy();
        return 0;
    }

    @Override
    public void setConfig(AlgorithmTaskKey key, Object p) {
        super.setConfig(key, p);
        if (key == ACTION_RECOGNITION_TYPE) {
            if (p instanceof ActionType) {
                setActionType((ActionType) p);
            }
        }
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return ACTION_RECOGNITION;
    }

    private void updateConfirmTime(ActionType type) {
        if (type == ActionType.OPEN_CLOSE_JUMP) {
            mConfirmTime = 2000;
        }
        mConfirmTime = 3000;
    }

    public interface ActionRecognitionResourceProvider extends AlgorithmResourceProvider {
        String actionRecognitionModelPath();
        String templateForActionType(ActionType actionType);
    }

    public enum ActionType {
        OPEN_CLOSE_JUMP,
        SIT_UP,
        DEEP_SQUAT,
        PUSH_UP,
        PLANK,
        LUNGE,
        LUNGE_SQUAT,
        HIGH_RUN,
        KNEELING_PUSH_UP,
        HIP_BRIDGE,
    }
}
