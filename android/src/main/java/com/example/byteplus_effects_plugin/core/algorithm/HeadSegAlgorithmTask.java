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
import com.bytedance.labcv.effectsdk.BefHeadSegInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.FaceDetect;
import com.bytedance.labcv.effectsdk.HeadSegment;

import java.nio.ByteBuffer;

/**
 * Created on 5/13/21 2:59 PM
 */
public class HeadSegAlgorithmTask extends AlgorithmTask<HeadSegAlgorithmTask.HeadSegResourceProvider, BefHeadSegInfo> {
    public static final AlgorithmTaskKey HEAD_SEGMENT = AlgorithmTaskKeyFactory.create("headSeg", true);

    public static final int FACE_DETECT_CONFIG = (BytedEffectConstants.FaceAction.BEF_FACE_DETECT |
            BytedEffectConstants.DetectMode.BEF_DETECT_MODE_VIDEO |
            BytedEffectConstants.FaceAction.BEF_DETECT_FULL);

    private HeadSegment mHeadSegDetector;
    private FaceDetect mFaceDetector;

    public HeadSegAlgorithmTask(Context context, HeadSegResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mFaceDetector = new FaceDetect();
        mHeadSegDetector = new HeadSegment();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mHeadSegDetector.init(mContext, mResourceProvider.headSegModel(), licensePath,
                            mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initHeadSegment", ret)) return ret;

        ret = mFaceDetector.init(mContext, mResourceProvider.faceModel(),
                BEF_DETECT_SMALL_MODEL | BEF_DETECT_FULL, licensePath,
                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initFace", ret)) return ret;
        mFaceDetector.setFaceDetectConfig(FACE_DETECT_CONFIG);
        return ret;
    }

    @Override
    public BefHeadSegInfo process(ByteBuffer buffer, int width, int height, int stride,
                                  BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        BefFaceInfo faceInfo = mFaceDetector.detectFace(buffer, pixlFormat, width, height, stride, rotation);
        if (faceInfo == null) {
            return null;
        }
        LogTimerRecord.RECORD("headSegment");
        BefHeadSegInfo segInfo = mHeadSegDetector.process(buffer, pixlFormat, width, height,
                stride, rotation, faceInfo.getFace106s());
        LogTimerRecord.STOP("headSegment");
        return segInfo;
    }

    @Override
    public int destroyTask() {
        mFaceDetector.release();
        mHeadSegDetector.release();
        return 0;
    }

    @Override
    public AlgorithmTaskKey key() {
        return HEAD_SEGMENT;
    }

    @Override
    public int[] preferBufferSize() {
        return null;
    }

    public interface HeadSegResourceProvider extends AlgorithmResourceProvider {
        String faceModel();
        String headSegModel();
    }
}
