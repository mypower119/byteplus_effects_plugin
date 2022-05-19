package com.example.byteplus_effects_plugin.algorithm.ui;//package com.example.byteplus_effects_plugin.algorithm.ui.ui;
//
//import com.example.byteplus_effects_plugin.R;
//import com.example.byteplus_effects_plugin.app.core.v4.algorithm.AlgorithmInterface;
//import com.example.byteplus_effects_plugin.app.core.v4.algorithm.task.StudentIdOcrTask;
//import com.example.byteplus_effects_plugin.app.model.AlgorithmItem;
//import com.bytedance.labcv.effectsdk.BefStudentIdOcrInfo;
//import com.bytedance.labcv.effectsdk.library.LogUtils;
//
///**
// * Created on 2020/9/18 16:58
// */
//public class StudentIdOcrTestUI extends BaseAlgorithmUI {
//
//    @Override
//    void initCallback() {
//        super.initCallback();
//
//        provider().getAlgorithm().addResultCallback(new AlgorithmInterface.ResultCallback<BefStudentIdOcrInfo>() {
//            @Override
//            protected void doResult(BefStudentIdOcrInfo befStudentIdOcrInfo, int framecount) {
//                LogUtils.e("student ocr info: " + befStudentIdOcrInfo.toString());
//            }
//        });
//    }
//
//    @Override
//    public AlgorithmItem getAlgorithmItem() {
//        AlgorithmItem item = new AlgorithmItem(StudentIdOcrTask.STUDENT_ID_OCR);
//        item.setIcon(R.drawable.ic_skeleton);
//        item.setTitle(R.string.tab_student_id_ocr_test);
//        item.setTabTitleId(R.string.tab_student_id_ocr_test);
//        return item;
//    }
//}
