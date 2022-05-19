package com.example.byteplus_effects_plugin.algorithm.ui;

import android.content.Context;
import android.content.res.Configuration;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.algorithm.view.HandInfoTip;
import com.example.byteplus_effects_plugin.algorithm.view.ResultTip;
import com.example.byteplus_effects_plugin.algorithm.view.TipManager;
import com.example.byteplus_effects_plugin.core.algorithm.HandAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefHandInfo;

/**
 * Created on 2020/8/18 20:15
 */
public class HandUI extends BaseAlgorithmUI<BefHandInfo> {

    @Override
    void initView() {
        if (!checkAvailable(tipManager())) return;
        tipManager().registerGenerator(HandAlgorithmTask.HAND, new TipManager.ResultTipGenerator<BefHandInfo.BefHand>() {
            @Override
            public ResultTip<BefHandInfo.BefHand> create(Context context) {
                return new HandInfoTip(context);
            }
        });
    }

    @Override
    public void onEvent(AlgorithmTaskKey key, boolean flag) {
        super.onEvent(key, flag);

        if (checkAvailable(tipManager())) {
            tipManager().enableOrRemove(key, flag);
        }
    }

    @Override
    public void onReceiveResult(BefHandInfo handInfo) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!checkAvailable(provider(), provider().getContext(), tipManager())) return;
                if (handInfo == null)return;
                if (provider().getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    tipManager().updateInfo(HandAlgorithmTask.HAND, handInfo.getHands());
                } else {
                    tipManager().updateInfo(HandAlgorithmTask.HAND, handInfo.getHands());
                }
            }
        });
    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem hand = new AlgorithmItem(HandAlgorithmTask.HAND);
        hand.setIcon(R.drawable.ic_hand);
        hand.setTitle(R.string.hand_detect_title);
        hand.setDesc(R.string.hand_detect_desc);
        return hand;
    }
}
