package com.example.byteplus_effects_plugin.algorithm.ui;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.core.algorithm.SkeletonAlgorithmTask;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefSkeletonInfo;

/**
 * Created on 2020/8/18 21:10
 */
public class SkeletonUI extends BaseAlgorithmUI<BefSkeletonInfo> {

    @Override
    public void onReceiveResult(BefSkeletonInfo algorithmResult) {

    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem skeleton = new AlgorithmItem(SkeletonAlgorithmTask.SKELETON);
        skeleton.setIcon(R.drawable.ic_skeleton);
        skeleton.setTitle(R.string.skeleton_detect_title);
        skeleton.setDesc(R.string.skeleton_detect_desc);
        return skeleton;
    }
}
