package com.example.byteplus_effects_plugin.algorithm.ui;

import android.content.Context;
import android.content.res.Configuration;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.algorithm.view.HumanDistanceTip;
import com.example.byteplus_effects_plugin.algorithm.view.ResultTip;
import com.example.byteplus_effects_plugin.algorithm.view.TipManager;
import com.example.byteplus_effects_plugin.core.algorithm.HumanDistanceAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefDistanceInfo;

/**
 * Created on 2020/8/18 21:18
 */
public class HumanDistanceUI extends BaseAlgorithmUI<BefDistanceInfo> {

    @Override
    void initView() {
        super.initView();
        if (!checkAvailable(tipManager())) return;
        tipManager().registerGenerator(HumanDistanceAlgorithmTask.HUMAN_DISTANCE,
                new TipManager.ResultTipGenerator<BefDistanceInfo.BefDistance>() {
                    @Override
                    public ResultTip<BefDistanceInfo.BefDistance> create(Context context) {
                        return new HumanDistanceTip(context);
                    }
                });
    }

    @Override
    public void onEvent(AlgorithmTaskKey key, boolean flag) {
        super.onEvent(key, flag);

        if (!checkAvailable(tipManager())) return;
        tipManager().enableOrRemove(key, flag);
    }

    @Override
    public void onReceiveResult(BefDistanceInfo distanceInfo) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!checkAvailable(provider(),
                        provider().getTipManager(), provider().getContext())) return;
                if (distanceInfo == null)return;
                if (provider().getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    tipManager().updateInfo(HumanDistanceAlgorithmTask.HUMAN_DISTANCE, distanceInfo.getBefDistance());
                } else {
                    tipManager().updateInfo(HumanDistanceAlgorithmTask.HUMAN_DISTANCE, distanceInfo.getBefDistance());
                }
            }
        });
    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem humanDistance = new AlgorithmItem(HumanDistanceAlgorithmTask.HUMAN_DISTANCE);
        humanDistance.setIcon(R.drawable.ic_distance);
        humanDistance.setTitle(R.string.setting_human_dist);
        humanDistance.setDesc(R.string.setting_human_dist_desc);
        return humanDistance;
    }
}
