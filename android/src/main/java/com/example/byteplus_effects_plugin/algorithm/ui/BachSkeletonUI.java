package com.example.byteplus_effects_plugin.algorithm.ui;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.core.algorithm.BachSkeletonAlgorithmTask;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefBachSkeletonInfo;

/**
 * Created on 2020/8/18 21:10
 */
public class BachSkeletonUI extends BaseAlgorithmUI<BefBachSkeletonInfo> {

    @Override
    public void onReceiveResult(BefBachSkeletonInfo algorithmResult) {

    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem skeleton = new AlgorithmItem(BachSkeletonAlgorithmTask.BACH_SKELETON);
        skeleton.setIcon(R.drawable.ic_bach_skeleton);
        skeleton.setTitle(R.string.bach_skeleton_title);
        skeleton.setDesc(R.string.bach_skeleton_desc);
        return skeleton;
    }
}
