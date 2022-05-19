package com.example.byteplus_effects_plugin.algorithm.ui;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.core.algorithm.ChromaKeyingAlgorithmTask;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefChromaKeyingInfo;

/**
 * Created on 5/13/21 3:12 PM
 */
public class ChromaKeyingUI extends BaseAlgorithmUI<BefChromaKeyingInfo> {
    @Override
    public void onReceiveResult(BefChromaKeyingInfo algorithmResult) {

    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem item = new AlgorithmItem(ChromaKeyingAlgorithmTask.CHROMA_KEYING);
        item.setIcon(R.drawable.ic_chroma_keying);
        item.setTitle(R.string.chroma_keying_title);
        item.setDesc(R.string.chroma_keying_desc);
        return item;
    }
}
