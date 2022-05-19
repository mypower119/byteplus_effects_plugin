package com.example.byteplus_effects_plugin.algorithm.ui;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.byteplus_effects_plugin.algorithm.fragment.SkyInfoFragment;
import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.core.algorithm.SkySegAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefSkyInfo;

/**
 * Created on 5/14/21 2:16 PM
 */
public class SkySegUI extends BaseAlgorithmUI<BefSkyInfo> {
    private SkyInfoFragment skyInfoFragment;
    private boolean mSkyAttrOn = true;

    @Override
    public void onEvent(AlgorithmTaskKey key, boolean flag) {
        super.onEvent(key, flag);
//        enableSkySegment(flag);
        mSkyAttrOn = flag;


    }

    private void enableSkySegment(final boolean flag) {
        hideAndShowSkyAction(!flag);
        if (!flag) {
            if (skyInfoFragment != null) {
                skyInfoFragment.onClose();
            }
        }
    }

    private void hideAndShowSkyAction(boolean hide) {
        FragmentManager fm = provider().getFMManager();
        FragmentTransaction ft = fm.beginTransaction();
        skyInfoFragment = (SkyInfoFragment) fm.findFragmentByTag("sky");

        if (hide) {
            if (null != skyInfoFragment) {
                ft.hide(skyInfoFragment).commit();
            }
        } else {
            if (null == skyInfoFragment) {
                skyInfoFragment = new SkyInfoFragment();
                ft.replace(R.id.fl_algorithm_info, skyInfoFragment, "sky").commit();

            } else {
                ft.show(skyInfoFragment).commit();
            }
        }
    }
    @Override
    public void onReceiveResult(BefSkyInfo befSkyInfo) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (skyInfoFragment != null) {
                    skyInfoFragment.updateProperty(befSkyInfo, mSkyAttrOn);
                }
            }
        });
    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
                AlgorithmItem skySegment = new AlgorithmItem(SkySegAlgorithmTask.SKY_SEGMENT);
        skySegment.setIcon(R.drawable.ic_sky_segment);
        skySegment.setTitle(R.string.segment_sky_title);
        skySegment.setDesc(R.string.segment_sky_desc);
        return skySegment;
    }
}
