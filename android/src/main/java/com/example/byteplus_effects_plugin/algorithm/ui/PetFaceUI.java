package com.example.byteplus_effects_plugin.algorithm.ui;

import android.content.Context;
import android.content.res.Configuration;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.algorithm.view.PetFaceInfoTip;
import com.example.byteplus_effects_plugin.algorithm.view.ResultTip;
import com.example.byteplus_effects_plugin.algorithm.view.TipManager;
import com.example.byteplus_effects_plugin.core.algorithm.PetFaceAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefPetFaceInfo;

/**
 * Created on 2020/8/18 20:57
 */
public class PetFaceUI extends BaseAlgorithmUI<BefPetFaceInfo> {

    @Override
    void initView() {
        if (!checkAvailable(tipManager())) return;
        tipManager().registerGenerator(PetFaceAlgorithmTask.PET_FACE,
                new TipManager.ResultTipGenerator<BefPetFaceInfo.PetFace>() {
                    @Override
                    public ResultTip<BefPetFaceInfo.PetFace> create(Context context) {
                        return new PetFaceInfoTip(context);
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
    public void onReceiveResult(BefPetFaceInfo befPetFaceInfo) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!checkAvailable(provider(), provider().getContext(), tipManager())) return;
                if (befPetFaceInfo == null)return;
                if (provider().getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    tipManager().updateInfo(PetFaceAlgorithmTask.PET_FACE, befPetFaceInfo.getFace90());
                } else {
                    tipManager().updateInfo(PetFaceAlgorithmTask.PET_FACE, befPetFaceInfo.getFace90());
                }
            }
        });
    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem petFace = new AlgorithmItem(PetFaceAlgorithmTask.PET_FACE);
        petFace.setIcon(R.drawable.ic_pet);
        petFace.setTitle(R.string.pet_face_title);
        petFace.setDesc(R.string.pet_face_desc);
        return petFace;
    }
}
