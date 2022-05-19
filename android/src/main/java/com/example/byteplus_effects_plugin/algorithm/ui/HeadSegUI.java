package com.example.byteplus_effects_plugin.algorithm.ui;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.core.algorithm.HeadSegAlgorithmTask;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefHeadSegInfo;

/**
 * Created on 5/13/21 3:12 PM
 */
public class HeadSegUI extends BaseAlgorithmUI<BefHeadSegInfo> {
    @Override
    public void onReceiveResult(BefHeadSegInfo algorithmResult) {

    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem item = new AlgorithmItem(HeadSegAlgorithmTask.HEAD_SEGMENT);
        item.setIcon(R.drawable.ic_head_seg);
        item.setTitle(R.string.head_segment_title);
        item.setDesc(R.string.head_segment_desc);
        return item;
    }
}
