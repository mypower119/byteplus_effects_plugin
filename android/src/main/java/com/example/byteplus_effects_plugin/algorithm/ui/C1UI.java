package com.example.byteplus_effects_plugin.algorithm.ui;

import android.view.View;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.common.view.PropertyTextView;
import com.example.byteplus_effects_plugin.core.algorithm.C1AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefC1Info;

/**
 * Created on 2020/8/19 17:49
 */
public class C1UI extends BaseAlgorithmUI<BefC1Info> {
    public static final int SHOW_NUM = 1;

    public static final String[] C1_TYPES = {
            "Baby",   "Beach", "Building",   "Car",    "Cartoon", "Cat",    "Dog",    "Flower", "Food", "Group", "Hill",
            "Indoor", "Lake",  "Nightscape", "Selfie", "Sky",     "Statue", "Street", "Sunset", "Text", "Tree",  "Other"
    };

    private PropertyTextView ptv;

    @Override
    void initView() {
        super.initView();

        addLayout(R.layout.layout_c1_info, R.id.fl_algorithm_info);

        if (!checkAvailable(provider())) return;
        ptv = provider().findViewById(R.id.ptv_c1);
    }

    @Override
    public void onReceiveResult(BefC1Info algorithmResult) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!checkAvailable(provider())) return;
                if (algorithmResult == null) return;
                BefC1Info.BefC1CategoryItem[] items = algorithmResult.topN(SHOW_NUM);
                if (items.length > 0) {
                    BefC1Info.BefC1CategoryItem item = items[0];
                    ptv.setTitle(C1_TYPES[item.getId()]);
                    ptv.setValue(String.format("%.2f", item.getProb()));
                } else {
                    ptv.setTitle(provider().getContext().getString(R.string.tab_c1));
                    ptv.setValue(provider().getString(R.string.video_cls_no_results));
                }
            }
        });
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
    public AlgorithmItem getAlgorithmItem() {
        return (AlgorithmItem) new AlgorithmItem(C1AlgorithmTask.C1)
                .setIcon(R.drawable.ic_c1)
                .setTitle(R.string.tab_c1)
                .setDesc(R.string.c1_desc);
    }
}
