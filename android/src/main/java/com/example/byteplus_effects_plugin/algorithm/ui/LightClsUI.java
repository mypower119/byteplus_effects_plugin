package com.example.byteplus_effects_plugin.algorithm.ui;

import android.view.View;
import android.widget.LinearLayout;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.common.view.PropertyTextView;
import com.example.byteplus_effects_plugin.core.algorithm.LightClsAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefLightclsInfo;

/**
 * Created on 2020/8/18 21:12
 */
public class LightClsUI extends BaseAlgorithmUI<BefLightclsInfo> {
    public static final int[] LIGHT_TYPE = {
            R.string.indoor_yellow, R.string.indoor_white, R.string.indoor_weak,
            R.string.sunny, R.string.cloudy, R.string.night, R.string.back_light
    };

    private LinearLayout llLightCls;
    private PropertyTextView tvLightClsType;
    private PropertyTextView tvLightClsCredibility;

    @Override
    void initView() {
        super.initView();

        addLayout(R.layout.layout_car_info, R.id.fl_algorithm_info);

        if (!checkAvailable(provider())) return;
        llLightCls = provider().findViewById(R.id.ll_carlight_cls);
        tvLightClsType = provider().findViewById(R.id.ptv_carlight_blur);
        tvLightClsType.setTitle(tvLightClsType.getContext().getString(R.string.light_cls_type));
        tvLightClsCredibility = provider().findViewById(R.id.ptv_carlight_grey);
        tvLightClsCredibility.setTitle(tvLightClsCredibility.getContext().getString(R.string.light_cls_credibility));

    }

    @Override
    public void onEvent(AlgorithmTaskKey key, boolean flag) {
        super.onEvent(key, flag);

        llLightCls.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onReceiveResult(BefLightclsInfo lightclsInfo) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!checkAvailable(provider(),
                        provider().getContext(), tipManager())) return;
                if (lightclsInfo == null)return;
                String type;

                if (lightclsInfo.getSelectedIndex() >= 0 && lightclsInfo.getSelectedIndex() < LIGHT_TYPE.length) {
                    type = provider().getContext().getString(LIGHT_TYPE[lightclsInfo.getSelectedIndex()]);
                } else {
                    type = "";
                }
                tvLightClsType.setValue(type);
                String credibility = String.valueOf(lightclsInfo.getProb());
                if (credibility.length() > 4) {
                    credibility = credibility.substring(0, 5);
                }
                tvLightClsCredibility.setValue(credibility);
            }
        });
    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem light = new AlgorithmItem(LightClsAlgorithmTask.LIGHT_CLS);
        light.setIcon(R.drawable.ic_light);
        light.setTitle(R.string.tab_light_cls);
        light.setDesc(R.string.light_cls_desc);
        return light;
    }
}
