package com.example.byteplus_effects_plugin.algorithm.ui;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.byteplus_effects_plugin.algorithm.fragment.FaceInfoFragment;
import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItemGroup;
import com.example.byteplus_effects_plugin.core.algorithm.FaceAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefFaceInfo;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created on 2020/8/18 17:41
 */
public class FaceUI extends BaseAlgorithmUI<BefFaceInfo> {
    private FaceInfoFragment faceInfoFragment;

    private boolean mFaceAttrOn = false;

    @Override
    void initView() {
        super.initView();
        addLayout(R.layout.layout_face_info, R.id.fl_algorithm_info);
    }

    @Override
    public void onEvent(AlgorithmTaskKey key, boolean flag) {
        super.onEvent(key, flag);

        if (FaceAlgorithmTask.FACE.equals(key)) {
            enableFace106(flag);
        }

        if (FaceAlgorithmTask.FACE_ATTR.equals(key)) {
            mFaceAttrOn = flag;
        }
    }

    @Override
    public void onReceiveResult(BefFaceInfo befFaceInfo) {
        if (faceInfoFragment != null) {
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    faceInfoFragment.updateProperty(befFaceInfo, mFaceAttrOn);
                }
            });
        }
    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem face106 = (AlgorithmItem) new AlgorithmItem(FaceAlgorithmTask.FACE)
                .setIcon(R.drawable.ic_face106)
                .setTitle(R.string.face_106_title)
                .setDesc(R.string.face_106_desc);

        AlgorithmItem face280 = (AlgorithmItem) new AlgorithmItem(FaceAlgorithmTask.FACE_280,
                Collections.singletonList(FaceAlgorithmTask.FACE))
                .setDependencyToastId(R.string.open_face106_fist)
                .setIcon(R.drawable.ic_face280)
                .setTitle(R.string.face_280_title)
                .setDesc(R.string.face_280_desc);

        AlgorithmItem faceAttr = new AlgorithmItem(FaceAlgorithmTask.FACE_ATTR,
                Collections.singletonList(FaceAlgorithmTask.FACE));
        faceAttr.setIcon(R.drawable.ic_face_attr);
        faceAttr.setTitle(R.string.face_attr_title);
        faceAttr.setDesc(R.string.face_attr_desc);
        faceAttr.setDependencyToastId(R.string.open_face106_fist);

        AlgorithmItem faceMask = new AlgorithmItem(FaceAlgorithmTask.FACE_MASK,
                Collections.singletonList(FaceAlgorithmTask.FACE_280));
        faceMask.setIcon(R.drawable.ic_face_mask);
        faceMask.setTitle(R.string.face_mask_title);
        faceMask.setDesc(R.string.face_mask_desc);
        faceMask.setDependencyToastId(R.string.open_face280_fist);

        AlgorithmItem mouthMask = new AlgorithmItem(FaceAlgorithmTask.MOUTH_MASK,
                Collections.singletonList(FaceAlgorithmTask.FACE_280));
        mouthMask.setIcon(R.drawable.ic_face_lip_seg);
        mouthMask.setTitle(R.string.mouth_mask_title);
        mouthMask.setDesc(R.string.mouth_mask_desc);
        mouthMask.setDependencyToastId(R.string.open_face280_fist);

        AlgorithmItem teethMask = new AlgorithmItem(FaceAlgorithmTask.TEETH_MASK,
                Collections.singletonList(FaceAlgorithmTask.FACE_280));
        teethMask.setIcon(R.drawable.ic_face_teeth_seg);
        teethMask.setTitle(R.string.teeth_mask_title);
        teethMask.setDesc(R.string.teeth_mask_desc);
        teethMask.setDependencyToastId(R.string.open_face280_fist);

        AlgorithmItemGroup faceGroup = new AlgorithmItemGroup(
                Arrays.asList(face106, face280, faceAttr, mouthMask, teethMask, faceMask), true
        );

        faceGroup.setKey(FaceAlgorithmTask.FACE);
        faceGroup.setTitle(R.string.tab_face);
        return faceGroup;
    }


    private void enableFace106(final boolean flag) {
        hideAndShowFaceAction(!flag);
        if (!flag) {
            if (faceInfoFragment != null) {
                faceInfoFragment.onClose();
            }
        }
    }

    private void hideAndShowFaceAction(boolean hide) {
        FragmentManager fm = provider().getFMManager();
        FragmentTransaction ft = fm.beginTransaction();
        faceInfoFragment = (FaceInfoFragment) fm.findFragmentByTag("action");

        if (hide) {
            if (null != faceInfoFragment) {
                ft.hide(faceInfoFragment).commit();
            }
        } else {
            if (null == faceInfoFragment) {
                faceInfoFragment = new FaceInfoFragment();
                ft.replace(R.id.fl_face_info, faceInfoFragment, "action").commit();

            } else {
                ft.show(faceInfoFragment).commit();
            }
        }
    }
}
