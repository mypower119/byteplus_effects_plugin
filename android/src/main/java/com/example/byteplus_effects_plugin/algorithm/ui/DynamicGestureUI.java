package com.example.byteplus_effects_plugin.algorithm.ui;

import android.view.View;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.common.view.PropertyTextView;
import com.example.byteplus_effects_plugin.core.algorithm.DynamicGestureAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefDynamicGestureInfo;

/**
 * Created on 2020/8/19 17:51
 */
public class DynamicGestureUI extends BaseAlgorithmUI<BefDynamicGestureInfo> {

    private static int[] DYNAMIC_GESTURE_TYPES = {
            R.string.dyngest_result_swiping_left,
            R.string.dyngest_result_swiping_right,
            R.string.dyngest_result_swiping_down,
            R.string.dyngest_result_swiping_up,
            R.string.dyngest_result_sliding_two_fingers_left,
            R.string.dyngest_result_sliding_two_fingers_right,
            R.string.dyngest_result_sliding_two_fingers_down,
            R.string.dyngest_result_sliding_two_fingers_up,
            R.string.dyngest_result_zooming_in_with_full_hand,
            R.string.dyngest_result_zooming_out_with_full_hand,
            R.string.dyngest_result_zooming_in_with_two_fingers,
            R.string.dyngest_result_zooming_out_with_two_fingers,
            R.string.dyngest_result_thump_up,
            R.string.dyngest_result_thump_down,
            R.string.dyngest_result_shaking_hand,
            R.string.dyngest_result_stop_sign,
            R.string.dyngest_result_drumming_fingers,
            R.string.dyngest_result_no_gesture
    };

    private static final int DELAY_SHOW_FRAME_NUMS = 32;
    private int mFrameNum = 0;

    private BefDynamicGestureInfo.GestureInfo mBefDynamicGestureInfo;

    private PropertyTextView ptv;

    @Override
    void initView() {
        super.initView();

        addLayout(R.layout.layout_c1_info, R.id.fl_algorithm_info);

        if (!checkAvailable(provider())) return;
        ptv = provider().findViewById(R.id.ptv_c1);
    }

    @Override
    public void onEvent(AlgorithmTaskKey key, boolean flag) {
        super.onEvent(key, flag);

        ptv.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        if (!flag) {
            ptv.setTitle("");
            ptv.setValue("");
        }
    }

    @Override
    public void onReceiveResult(BefDynamicGestureInfo befDynamicGestureInfo) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!checkAvailable(provider(),
                        provider().getContext(), tipManager())) return;

                if (befDynamicGestureInfo == null || befDynamicGestureInfo.getGestureInfos() == null) return;

                boolean needShowResult = befDynamicGestureInfo.getGestureInfos()[0].getActionType() >= 0 &&
                                         befDynamicGestureInfo.getGestureInfos()[0].getActionType() <= 17;
                if(befDynamicGestureInfo != null && needShowResult) {
                    mFrameNum = DELAY_SHOW_FRAME_NUMS;
                    mBefDynamicGestureInfo = befDynamicGestureInfo.getGestureInfos()[0];
                } else {
                    mFrameNum--;
                }

                if (mFrameNum > 0 && needShowResult) {
                    ptv.setTitle(provider().getString(DYNAMIC_GESTURE_TYPES[mBefDynamicGestureInfo.getActionType()]));
                    ptv.setValue(String.format("%.1f", mBefDynamicGestureInfo.getActionScore() * 100.0f));
                } else {
                    ptv.setTitle(provider().getString(R.string.tab_dynamic_gesture));
                    ptv.setValue(provider().getString(R.string.dynamic_gesture_no_results));
                }
            }
        });
    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        return (AlgorithmItem) new AlgorithmItem(DynamicGestureAlgorithmTask.DYNAMIC_GESTURE)
                .setIcon(R.drawable.ic_dynamic_gesture)
                .setTitle(R.string.tab_dynamic_gesture);
    }
}
