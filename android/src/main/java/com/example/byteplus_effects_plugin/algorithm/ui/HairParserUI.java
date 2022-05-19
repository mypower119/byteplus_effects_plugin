package com.example.byteplus_effects_plugin.algorithm.ui;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.core.algorithm.HairParserAlgorithmTask;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.HairParser;

/**
 * Created on 5/13/21 4:02 PM
 */
public class HairParserUI extends BaseAlgorithmUI<HairParser.HairMask> {
    @Override
    public void onReceiveResult(HairParser.HairMask algorithmResult) {

    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem item = new AlgorithmItem(HairParserAlgorithmTask.HAIR_PARSER);
        item.setIcon(R.drawable.ic_hair_parser);
        item.setTitle(R.string.segment_hair_title);
        item.setDesc(R.string.segment_hair_desc);
        return item;
    }
}
