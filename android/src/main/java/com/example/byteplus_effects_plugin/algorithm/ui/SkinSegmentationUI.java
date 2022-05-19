package com.example.byteplus_effects_plugin.algorithm.ui;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.core.algorithm.SkinSegmentationAlgorithmTask;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefSkinSegInfo;

/**
 * Created on 5/13/21 3:12 PM
 */
public class SkinSegmentationUI extends BaseAlgorithmUI<BefSkinSegInfo> {
    @Override
    public void onReceiveResult(BefSkinSegInfo algorithmResult) {

    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem item = new AlgorithmItem(SkinSegmentationAlgorithmTask.SKIN_SEGMENTATION);
        item.setIcon(R.drawable.ic_skin_segmentation);
        item.setTitle(R.string.skin_segmentation_title);
        item.setDesc(R.string.skin_segmentation_desc);
        return item;
    }
}
