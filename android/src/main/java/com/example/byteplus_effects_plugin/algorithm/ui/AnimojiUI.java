package com.example.byteplus_effects_plugin.algorithm.ui;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.core.algorithm.AnimojiAlgorithmTask;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefAnimojiInfo;

/**
 * Created on 2020/11/16 16:06
 */
public class AnimojiUI extends BaseAlgorithmUI<BefAnimojiInfo> {

    @Override
    public void onReceiveResult(BefAnimojiInfo algorithmResult) {
        LogUtils.e(algorithmResult.toString());
    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        return (AlgorithmItem)new AlgorithmItem(AnimojiAlgorithmTask.ANIMOJI)
                .setTitle(R.string.tab_animoji)
                .setIcon(R.drawable.ic_c2);
    }
}
