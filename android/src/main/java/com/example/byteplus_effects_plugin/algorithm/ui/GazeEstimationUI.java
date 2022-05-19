package com.example.byteplus_effects_plugin.algorithm.ui;

import android.view.View;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.common.view.PropertyTextView;
import com.example.byteplus_effects_plugin.core.algorithm.GazeEstimationAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefGazeEstimationInfo;

/**
 * Created on 2020/8/31 18:31
 */
public class GazeEstimationUI extends BaseAlgorithmUI<BefGazeEstimationInfo> {
    //   {zh} 摄像头在设备横轴上的位置比例       {en} Proportion of camera position on the horizontal axis of the device  
    public static final float DEFAULT_POSITION = 0.5F;
    //   {zh} 设备的宽度，单位 mm       {en} Width of equipment in mm  
    public static final int DEFAULT_WIDTH = 120;
    //   {zh} 设备的高度，单位 mm       {en} Height of equipment in mm  
    public static final int DEFAULT_HEIGHT = 200;

    private PropertyTextView ptv;

    @Override
    void initView() {
        super.initView();

        addLayout(R.layout.layout_c1_info, R.id.fl_algorithm_info);

        if (!checkAvailable(provider())) return;
        ptv = provider().findViewById(R.id.ptv_c1);
        ptv.setTitle(ptv.getContext().getString(R.string.gaze_inside));
    }

    @Override
    public void onEvent(AlgorithmTaskKey key, boolean flag) {
        super.onEvent(key, flag);

        ptv.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onReceiveResult(BefGazeEstimationInfo gazeEstimationInfo) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!checkAvailable(provider())) return;
                if (gazeEstimationInfo == null || gazeEstimationInfo.getFaceCount() <= 0
                        || !gazeEstimationInfo.getInfos()[0].isValid()) {
                    ptv.setValue(provider().getContext().getString(R.string.no));
                    return;
                }
                float[] lEye = gazeEstimationInfo.getInfos()[0].getLeye_pos();
                float[] rEye = gazeEstimationInfo.getInfos()[0].getReye_pos();
                float[] gaze = gazeEstimationInfo.getInfos()[0].getMid_gaze();
                float[] eye = new float[lEye.length];
                for (int i = 0; i < lEye.length; i++) {
                    eye[i] = (lEye[i] + rEye[i]) / 2;
                }
                float x = eye[0] - gaze[0] / gaze[2] * eye[2];
                float y = eye[1] - gaze[1] / gaze[2] * eye[2];
                LogUtils.d("point: x " + x + ", y " + y);

                int width = DEFAULT_WIDTH;
                int height = DEFAULT_HEIGHT;
                float position = DEFAULT_POSITION;
                int leftWidth = (int) (width * position);
                int rightWidth = (int) (width * (1 - position));
                boolean inside = x >= -leftWidth && x <= rightWidth && y <= height && y >= 0;
                ptv.setValue(provider().getContext().getString(inside ? R.string.yes : R.string.no));
            }
        });
    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem item = new AlgorithmItem(GazeEstimationAlgorithmTask.GAZE_ESTIMATION);
        item.setIcon(R.drawable.ic_gaze);
        item.setTitle(R.string.tab_gaze);
        return item;
    }
}
