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
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.FaceDetect;

import java.nio.ByteBuffer;

/**
 * Created on 5/13/21 6:08 PM
 */
public class ConcentrateAlgorithmTask extends AlgorithmTask<ConcentrateAlgorithmTask.ConcentrateResourceProvider, ConcentrateAlgorithmTask.BefConcentrationInfo> {

    public static final AlgorithmTaskKey CONCENTRATION = AlgorithmTaskKeyFactory.create("concentration", true);
    public static final int INTERVAL = 1000;
    public static final float MIN_YAW = -14;
    public static final float MAX_YAW = 7;
    public static final float MIN_PITCH = -12;
    public static final float MAX_PITCH = 12;

    public static final int FACE_DETECT_CONFIG = (BytedEffectConstants.FaceAction.BEF_FACE_DETECT |
            BytedEffectConstants.DetectMode.BEF_DETECT_MODE_VIDEO |
            BytedEffectConstants.FaceAction.BEF_DETECT_FULL);
    private FaceDetect mFaceDetector;
    private int mTotalCount;
    private int mConcentrationCount;
    private long mLastProcess;

    public ConcentrateAlgorithmTask(Context context, ConcentrateResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mFaceDetector = new FaceDetect();
    }

    @Override
    public int initTask() {
        mTotalCount = 0;
        mConcentrationCount = 0;
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mFaceDetector.init(mContext, mResourceProvider.faceModel(),
                BEF_DETECT_SMALL_MODEL | BEF_DETECT_FULL, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFace", ret)) return ret;

        mFaceDetector.setFaceDetectConfig(FACE_DETECT_CONFIG);
        return ret;
    }

    @Override
    public BefConcentrationInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("concentration");
        BefFaceInfo faceInfo = mFaceDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("concentration");
        if (faceInfo == null) {
            return null;
        }
        if ((System.currentTimeMillis() - mLastProcess) < INTERVAL) {
            return null;
        }
        mLastProcess = System.currentTimeMillis();
        if (faceInfo != null && faceInfo.getFace106s().length > 0) {
            BefFaceInfo.Face106 face106 = faceInfo.getFace106s()[0];
            boolean available = face106.getYaw() <= MAX_YAW && face106.getYaw() >= MIN_YAW && face106.getPitch() <= MAX_PITCH && face106.getPitch() >= MIN_PITCH;
            mTotalCount += 1;
            if (available) {
                mConcentrationCount += 1;
            }
        } else {
            mConcentrationCount = 0;
            mTotalCount = 0;
        }
        return new BefConcentrationInfo(mTotalCount, mConcentrationCount);
    }

    @Override
    public int destroyTask() {
        mTotalCount = 0;
        mConcentrationCount = 0;
        mFaceDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return CONCENTRATION;
    }

    public interface ConcentrateResourceProvider extends AlgorithmResourceProvider {
        String faceModel();
    }

    public static class BefConcentrationInfo {
        public int total;
        public int concentration;

        public BefConcentrationInfo(int total, int concentration) {
            this.total = total;
            this.concentration = concentration;
        }
    }
}
