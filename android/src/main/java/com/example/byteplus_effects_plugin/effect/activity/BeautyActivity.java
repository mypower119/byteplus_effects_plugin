package com.example.byteplus_effects_plugin.effect.activity;

import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.DESC_HAIR_DYE_HIGHLIGHT_PART_A;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.DESC_HAIR_DYE_HIGHLIGHT_PART_B;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.DESC_HAIR_DYE_HIGHLIGHT_PART_C;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.DESC_HAIR_DYE_HIGHLIGHT_PART_D;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.MASK;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_BEAUTY_BODY;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_BEAUTY_FACE;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_BEAUTY_RESHAPE;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_FILTER;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_HAIR_DYE_FULL;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_HAIR_DYE_HIGHLIGHT;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_LIPSTICK_GLOSSY;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_LIPSTICK_MATTE;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_MAKEUP;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_PALETTE;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.example.byteplus_effects_plugin.common.model.EffectType;
import com.example.byteplus_effects_plugin.common.view.bubble.BubbleWindowManager;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.config.EffectConfig;
import com.example.byteplus_effects_plugin.effect.fragment.EffectFragment;
import com.example.byteplus_effects_plugin.effect.model.ColorItem;
import com.example.byteplus_effects_plugin.effect.model.EffectButtonItem;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


/** {zh} 
 * 美颜美型
 */

/** {en}
 * Beauty beauty
 */

public class BeautyActivity extends BaseEffectActivity implements EffectFragment.IEffectCallback{
    private EffectFragment mEffectFragment = null;
    private String mFeature = "";
    public static final String EFFECT_TAG = "effect_board_tag";
    public static final String FEATURE_AR_LIPSTICK = "feature_ar_lipstick";
    public static final String FEATURE_AR_HAIR_DYE = "feature_ar_hair_dye";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showBoardFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private EffectFragment generateEffectFragment() {
        if (mEffectFragment != null) return mEffectFragment;

        String feature = "";
        String sEffectConfig = getIntent().getStringExtra(EffectConfig.EffectConfigKey);
        if (sEffectConfig != null) {
            EffectConfig effectConfig = new Gson().fromJson(sEffectConfig, EffectConfig.class);
            if (effectConfig != null) {
                feature = effectConfig.getFeature();
            }
        }
        mFeature = feature;
        EffectFragment effectFragment = new EffectFragment();
        if (feature != null && feature.equals(FEATURE_AR_LIPSTICK)) {
//            effectFragment.setColorListPosition(EffectFragment.BOARD_FRAGMENT_HEAD_ABOVE).useProgressBar(true);
            effectFragment.useProgressBar(false);
            effectFragment.setData(mContext,mEffectDataManager, getLipstickTabItems(),mEffectConfig.getEffectType());
        } else if (feature != null && feature.equals(FEATURE_AR_HAIR_DYE)) {
            mEffectManager.setSyncLoadResource(true);
            effectFragment.setColorListPosition(EffectFragment.BOARD_FRAGMENT_HEAD_ABOVE).useProgressBar(false);
            effectFragment.setData(mContext,mEffectDataManager, getHairDyeTabItems(),mEffectConfig.getEffectType());
        } else {
            effectFragment.setData(mContext,mEffectDataManager, getTabItems(),mEffectConfig.getEffectType());
        }
        effectFragment.setCallback(this);
        return effectFragment;
    }

    public List<EffectFragment.TabItem> getTabItems() {
        return Arrays.asList(
                new EffectFragment.TabItem(TYPE_BEAUTY_FACE, R.string.tab_face_beautification),
                new EffectFragment.TabItem(TYPE_BEAUTY_RESHAPE, R.string.tab_face_beauty_reshape),
                new EffectFragment.TabItem(TYPE_BEAUTY_BODY, R.string.tab_face_beauty_body),
                new EffectFragment.TabItem(TYPE_MAKEUP, R.string.tab_face_makeup),
                new EffectFragment.TabItem(TYPE_FILTER, R.string.tab_filter)
//                new EffectFragment.TabItem(TYPE_PALETTE, R.string.tab_palette)
        );
    }

    public List<EffectFragment.TabItem> getLipstickTabItems() {
        return Arrays.asList(
                new EffectFragment.TabItem(TYPE_LIPSTICK_GLOSSY, R.string.tab_lipstick_glossy),
                new EffectFragment.TabItem(TYPE_LIPSTICK_MATTE, R.string.tab_lipstick_matte)
        );
    }

    public List<EffectFragment.TabItem> getHairDyeTabItems() {
        return Arrays.asList(
                new EffectFragment.TabItem(TYPE_HAIR_DYE_FULL, R.string.tab_hair_dye_full),
                new EffectFragment.TabItem(TYPE_HAIR_DYE_HIGHLIGHT, R.string.tab_hair_dye_highlight)
        );
    }


    @Override
    public void updateComposeNodes(Set<EffectButtonItem> effectButtonItems) {
        if (mSurfaceView != null) {
            mSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    String[][] nodesAndTags = mEffectDataManager.generateComposerNodesAndTags(effectButtonItems);
                    mEffectManager.setComposeNodes(nodesAndTags[0]);
                    StringBuilder sb = new StringBuilder();
                    for (String item : nodesAndTags[0]) {
                        sb.append(item);
                        sb.append(" ");

                    }
                    LogUtils.d("nodes =" + sb.toString());


                }
            });
        }

    }



    @Override
    public void updateComposerNodeIntensity(EffectButtonItem item) {

        if (mSurfaceView != null) {
            mSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (item.getNode() == null)return;
                    for (int i = 0; i < item.getNode().getKeyArray().length; i++) {
                        if ( ((item.getId() & MASK) == TYPE_PALETTE) && item.isEnableNegative() ) {
                            mEffectManager.updateComposerNodeIntensity(item.getNode().getPath(),
                                    item.getNode().getKeyArray()[i], item.getIntensityArray()[i]-0.5f);
                        } else {
                            mEffectManager.updateComposerNodeIntensity(item.getNode().getPath(),
                                    item.getNode().getKeyArray()[i], item.getIntensityArray()[i]);
                        }

                        LogUtils.d("updateComposerNodeIntensity +"+item.getNode().getPath() + "  "+item.getNode().getKeyArray()[i]+" "+item.getIntensityArray()[i]);

                    }
                }
            });
        }

    }

    @Override
    public void onFilterSelected(String filter) {
        if (null != mSurfaceView) {
            mSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mEffectManager.setFilter(filter != null ? filter : "");

                }
            });
        }

    }

    @Override
    public void onFilterValueChanged(float cur) {
        if (null != mSurfaceView) {
            mSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mEffectManager.updateFilterIntensity(cur);
                }
            });
        }

    }

    @Override
    public void showTip(String title, String desc) {
        if (mBubbleTipManager != null){
            mBubbleTipManager.show(title, desc);
        }

    }

    @Override
    public void setImgCompareHeightBy(float y, int duration) {
        setImgCompareViewHeightBy(y, duration);
    }

    @Override
    public void onHairDyeSelected(int part, ColorItem colorItem) {

        // TODO:
        String str_part = "";
        switch (part) {
            case DESC_HAIR_DYE_HIGHLIGHT_PART_A:
                str_part = "msg: part A, (";
                break;
            case DESC_HAIR_DYE_HIGHLIGHT_PART_B:
                str_part = "msg: part B, (";
                break;
            case DESC_HAIR_DYE_HIGHLIGHT_PART_C:
                str_part = "msg: part C, (";
                break;
            case DESC_HAIR_DYE_HIGHLIGHT_PART_D:
                str_part = "msg: part D, (";
                break;
            default:
                str_part = "msg: part full, (";
        }

        String str_color = "";
        str_color =  colorItem.getR() +", "+ colorItem.getG() +", "+ colorItem.getB() + ")";
        LogUtils.e(str_part + str_color);

        // color set message should be sent in GL thread, so that color-setting message can be sent after the resources are loaded
        mSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mEffectManager.setHairColorByPart(part,colorItem.getR(),colorItem.getG(),colorItem.getB(),colorItem.getA());
            }
        });
    }

    @Override
    public void onClickEvent(View view) {
        if (null == view) return;
        if (view.getId() == R.id.iv_close_board) {
            hideBoardFragment(mEffectFragment);

        } else if (view.getId() == R.id.iv_record_board) {
            takePic();
        } else if (view.getId() == R.id.img_default) {
            //  set to default
            resetDefault();
            if (mEffectFragment != null) {
                mEffectFragment.resetToDefault();
            }

        }
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.img_open) {
            showBoardFragment();
        }else if (id == R.id.img_default_activity) {
            resetDefault();
            if (mEffectFragment != null) {
                mEffectFragment.resetToDefault();
            }

        }  else if (view.getId() == R.id.img_setting) {
            if ( (mFeature != null) && (mFeature.equals(FEATURE_AR_LIPSTICK) || mFeature.equals(FEATURE_AR_HAIR_DYE)) ) {
                mBubbleWindowManager.show(view, mBubbleCallback, BubbleWindowManager.ITEM_TYPE.BEAUTY, BubbleWindowManager.ITEM_TYPE.PERFORMANCE);
            } else {
                mBubbleWindowManager.show(view, mBubbleCallback, BubbleWindowManager.ITEM_TYPE.PERFORMANCE);
            }
        }

    }



    @Override
    public void onEffectInitialized() {
        super.onEffectInitialized();

    }

    @Override
    public boolean closeBoardFragment() {
        if (mEffectFragment != null && mEffectFragment.isVisible()) {
            hideBoardFragment(mEffectFragment);
            return true;
        }
        return false;

    }

    @Override
    public boolean showBoardFragment() {
        if(null == mEffectFragment){
            mEffectFragment = generateEffectFragment();
        }
        showBoardFragment(mEffectFragment, mBoardFragmentTargetId, EFFECT_TAG, true);
        return true;
    }

    @Override
    public EffectType getEffectType() {
        return mEffectConfig.getEffectType();
    }


    @Override
    protected void setBeautyDefault() {
        Set<EffectButtonItem> mSelected = mEffectFragment.getSelectNodes();
        Set<EffectButtonItem> defaults = mEffectDataManager.getDefaultItems();
        for (EffectButtonItem it : mSelected) {
            if (defaults.contains(it) ) {
                continue;
            }
            for (int i = 0; i < it.getNode().getKeyArray().length; i++) {
                mEffectManager.updateComposerNodeIntensity(it.getNode().getPath(),
                        it.getNode().getKeyArray()[i], it.isEnableNegative()?0.5f:0f);
            }
        }
        super.setBeautyDefault();

    }

}
