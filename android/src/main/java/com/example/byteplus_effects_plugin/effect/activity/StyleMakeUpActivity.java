package com.example.byteplus_effects_plugin.effect.activity;

import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_CLOSE;
import static com.example.byteplus_effects_plugin.effect.manager.EffectDataManager.TYPE_STYLE_MAKEUP;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.example.byteplus_effects_plugin.common.view.bubble.BubbleWindowManager;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.config.EffectConfig;
import com.example.byteplus_effects_plugin.effect.fragment.StyleMakeUpFragment;
import com.example.byteplus_effects_plugin.effect.model.EffectButtonItem;
import com.google.gson.Gson;


/** {zh} 
 * Created  on 2021/5/24 7:58 下午
 */

/** {en}
 * Created on 2021/5/24 7:58 pm
 */

public class StyleMakeUpActivity extends BaseEffectActivity implements StyleMakeUpFragment.IMakeUpCallback {
    private StyleMakeUpFragment mFragment;
    public static final String EFFECT_TAG = "style_board_tag";


    private EffectConfig mEffectConfig;

    private EffectButtonItem mSelected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEffectConfig = parseEffectConfig(getIntent());
        showBoardFragment();
    }

    private EffectConfig parseEffectConfig(Intent intent) {
        String sAlgorithmConfig = intent.getStringExtra(EffectConfig.EffectConfigKey);
        if (sAlgorithmConfig == null) {
            return null;
        }

        return new Gson().fromJson(sAlgorithmConfig, EffectConfig.class);
    }

    private StyleMakeUpFragment generateStickerFragment() {
        if (mFragment != null) return mFragment;
        mFragment = new StyleMakeUpFragment(mContext);
        mFragment.setData(mEffectDataManager.getItem(TYPE_STYLE_MAKEUP), this);

        return mFragment;

    }


    @Override
    public void updateComposerNodeIntensity(String node, String key, float value) {
        if (mSurfaceView != null) {
            mSurfaceView.queueEvent(() -> {
                mEffectManager.updateComposerNodeIntensity(node, key, value);
            });

        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.img_open) {
            showBoardFragment();
        } else if (id == R.id.img_default_activity) {
            resetDefault();
        } else if (view.getId() == R.id.img_setting) {
            mBubbleWindowManager.show(view, mBubbleCallback, BubbleWindowManager.ITEM_TYPE.BEAUTY, BubbleWindowManager.ITEM_TYPE.PERFORMANCE);
        }

    }

    @Override
    protected void resetDefault() {
        //   {zh} 移除风格妆节点       {en} Remove style makeup node  
        if (mSurfaceView != null) {
            mSurfaceView.queueEvent(() -> {
                //   {zh} 恢复默认美颜，如果有       {en} Restore default beauty, if any  
                super.resetDefault();
                if (mSelected != null && mSelected.getId() != TYPE_CLOSE) {
                    if (mSelected.getNode() != null) {
                        mEffectManager.removeComposeNodes(new String[]{mSelected.getNode().getPath()});
                    }
                    //  {zh} 重置强度值  {en} Reset strength value
                    mEffectDataManager.resetItem(mSelected);


                }
            });
        }
        runOnUiThread(()->{
            //   {zh} 重置选择框及进度条       {en} Reset the selection box and progress bar  
            if (null != mFragment) {
                mFragment.resetDefault();

            }
        });


    }

    @Override
    public void onClickEvent(View view) {
        if (view.getId() == R.id.iv_close_board) {
            hideBoardFragment(mFragment);

        } else if (view.getId() == R.id.iv_record_board) {
            takePic();
        } else if (view.getId() == R.id.img_default) {
            resetDefault();
        }

    }

    @Override
    public boolean closeBoardFragment() {
        if (mFragment != null && mFragment.isVisible()) {
            hideBoardFragment(mFragment);
            return true;
        }
        return false;
    }

    @Override
    public boolean showBoardFragment() {
        if (null == mFragment) {
            mFragment = generateStickerFragment();
        }
        showBoardFragment(mFragment, mBoardFragmentTargetId, EFFECT_TAG, true);
        return true;
    }


    @Override
    public void onMakeUpItemSelect(EffectButtonItem item) {
        if (item != null && mBubbleTipManager != null) {
            mBubbleTipManager.show(item.getTitle(),0);
        }

        if (mSurfaceView != null) {
            mSurfaceView.queueEvent(() -> {

                if (mSelected!= null && mSelected.getId() != TYPE_CLOSE) {
                    if (mSelected.getNode() != null) {
                        mEffectManager.removeComposeNodes(new String[]{mSelected.getNode().getPath()});
                        if (item.getId() == TYPE_CLOSE){
                            //  {zh} 重置强度值  {en} Reset strength value
                            mEffectDataManager.resetItem(mSelected);
                        }

                    }


                }
                mSelected = item;

                if (mSelected.getId() == TYPE_CLOSE) {
                    return;
                }
                if (mSelected.getNode() == null) {
                    return;
                }
                mEffectManager.appendComposeNodes(new String[]{mSelected.getNode().getPath()});
                if (item.getNode() != null) {
                    for (int i = 0; i < mSelected.getNode().getKeyArray().length; i++) {
                        mEffectManager.updateComposerNodeIntensity(mSelected.getNode().getPath(),
                                item.getNode().getKeyArray()[i], mSelected.getIntensityArray()[i]);
                    }
                }
            });
        }

    }



}
