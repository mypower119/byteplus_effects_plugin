package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefStudentIdOcrInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.StudentIdOcr;

import java.nio.ByteBuffer;

/**
 * Created on 5/14/21 12:02 PM
 */
public class StudentIdOcrAlgorithmTask extends AlgorithmTask<StudentIdOcrAlgorithmTask.StudentIdOcrResourceProvider, BefStudentIdOcrInfo> {

    public static final AlgorithmTaskKey STUDENT_ID_OCR = AlgorithmTaskKeyFactory.create("student_id_ocr", true);

    private StudentIdOcr mDetector;
    public StudentIdOcrAlgorithmTask(Context context, StudentIdOcrResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);
        mDetector = new StudentIdOcr();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        int ret = mDetector.init(mLicenseProvider.getLicensePath());
        if (!checkResult("init student_id_ocr", ret)) return ret;
        ret = mDetector.setModel(BytedEffectConstants.StudentIdOcrModelType.BEF_STUDENT_ID_OCR_MODEL, mResourceProvider.studentIdOcrModel());
        if (!checkResult("init student_id_ocr", ret)) return ret;
        return ret;
    }

    @Override
    public BefStudentIdOcrInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("student_id_ocr");
        BefStudentIdOcrInfo info = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("student_id_ocr");
        return info;
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
        return null;
    }

    public interface StudentIdOcrResourceProvider extends AlgorithmResourceProvider {
        String studentIdOcrModel();
    }
}
