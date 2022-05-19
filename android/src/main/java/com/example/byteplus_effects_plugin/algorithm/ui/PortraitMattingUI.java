package com.example.byteplus_effects_plugin.algorithm.ui;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.core.algorithm.PortraitMattingAlgorithmTask;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.PortraitMatting;

/**
 * Created on 5/14/21 2:15 PM
 */
public class PortraitMattingUI extends BaseAlgorithmUI<PortraitMatting.MattingMask> {
    @Override
    public void onReceiveResult(PortraitMatting.MattingMask algorithmResult) {

    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem item = new AlgorithmItem(PortraitMattingAlgorithmTask.PORTRAIT_MATTING);
        item.setIcon(R.drawable.ic_prtrait_matting);
        item.setTitle(R.string.portait_matting_title);
        item.setDesc(R.string.portait_matting_desc);
        return item;
    }
}
