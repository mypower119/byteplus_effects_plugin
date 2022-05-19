package com.example.byteplus_effects_plugin.effect.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.fragment.ItemViewPageFragment;
import com.example.byteplus_effects_plugin.common.view.bubble.BubbleWindowManager;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.adapter.MattingStickerRVAdapter;
import com.example.byteplus_effects_plugin.effect.fragment.MattingStickerFragment;
import com.example.byteplus_effects_plugin.effect.model.EffectButtonItem;

import java.util.ArrayList;

/** {zh} 
 * Created  on 2021/5/24 4:01 下午
 */

/** {en}
 * Created on 2021/5/24 4:01 pm
 */

public class BackgroundBlurActivity extends BaseEffectActivity implements MattingStickerFragment.MattingStickerCallback, ItemViewRVAdapter.OnItemClickListener<EffectButtonItem>  {
    private MattingStickerFragment mFragment = null;
    public static final String EFFECT_TAG = "effect_board_tag";
    protected static final String mBackgroundBlurPath = "stickers/background_blur";
    private boolean mSwitch = true;
    public static final int TYPE_NO_MATTING = 0;
    public static final int TYPE_UPLOAD_MATTING = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBoardFragment();
    }


    private MattingStickerFragment generateStickerFragment(){
        if (mFragment != null) return mFragment;
        ArrayList<Fragment> fragments = new ArrayList<Fragment>(){
            {
                ArrayList<EffectButtonItem> items = new ArrayList<EffectButtonItem>(){
                    {
//                        add(new EffectButtonItem(TYPE_NO_MATTING,R.drawable.clear,R.string.close));
                        EffectButtonItem mItem = new EffectButtonItem(TYPE_UPLOAD_MATTING,R.drawable.ic_background_blur_highlight,R.string.tab_background_blur);
                        mItem.setSelected(true);
                        add(mItem);
                    }
                };
                MattingStickerRVAdapter adapter = new MattingStickerRVAdapter(items,BackgroundBlurActivity.this);
                ItemViewPageFragment<MattingStickerRVAdapter> fragment = new ItemViewPageFragment<>();
                fragment.setAdapter(adapter);
                add(fragment);
            }
        };
        ArrayList<String> titles = new ArrayList<String>(){
            {
                add(getString(R.string.tab_background_blur));
            }
        };

        mFragment = new MattingStickerFragment(fragments,titles);

        mFragment.setMattingStickerCallback(this);

        return mFragment;

    }

    @Override
    public void onClickEvent(View view) {
        if (view.getId() == R.id.iv_close_board) {
            hideBoardFragment(mFragment);
        } else if (view.getId() == R.id.img_default) {
            if(mSwitch){
                mSwitch = false;
                ItemViewPageFragment fragment = (ItemViewPageFragment)mFragment.getCurrentFragment();
                MattingStickerRVAdapter adapter = (MattingStickerRVAdapter)fragment.getAdapter();
                EffectButtonItem item = adapter.getItemList().get(0);
                item.setSelected(false);
                fragment.refreshUI();
                if (null == mSurfaceView){
                    return;
                }
                mSurfaceView.queueEvent(()->{
                    mEffectManager.setSticker(mSwitch ? mBackgroundBlurPath : "");
                });
            }
        } else if (view.getId() == R.id.iv_record_board) {
            takePic();
        }
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.img_open) {
            showBoardFragment();
        } else if (id == R.id.img_default_activity) {
            if(mSwitch){
                mSwitch = false;
                ItemViewPageFragment fragment = (ItemViewPageFragment)mFragment.getCurrentFragment();
                MattingStickerRVAdapter adapter = (MattingStickerRVAdapter)fragment.getAdapter();
                EffectButtonItem item = adapter.getItemList().get(0);
                item.setSelected(false);
                fragment.refreshUI();
                if (null == mSurfaceView){
                    return;
                }
                mSurfaceView.queueEvent(()->{
                    mEffectManager.setSticker(mSwitch ? mBackgroundBlurPath : "");
                });
            }
        }  else if (view.getId() == R.id.img_setting) {
            mBubbleWindowManager.show(view, mBubbleCallback, BubbleWindowManager.ITEM_TYPE.BEAUTY, BubbleWindowManager.ITEM_TYPE.PERFORMANCE);
        }
    }


    @Override
    public void onEffectInitialized() {
        super.onEffectInitialized();
        mEffectManager.setSticker(mBackgroundBlurPath);
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
        if (null == mFragment){
            mFragment = generateStickerFragment();
        }
        showBoardFragment(mFragment, mBoardFragmentTargetId, EFFECT_TAG, true);
        return true;

    }

    @Override
    public void onItemClick(EffectButtonItem item, int position) {
        if (item.getId() == TYPE_UPLOAD_MATTING){
            mSwitch = !mSwitch;
            item.setSelected(mSwitch);
            ((ItemViewPageFragment)mFragment.getCurrentFragment()).refreshUI();
            if (null == mSurfaceView){
                return;
            }
            mSurfaceView.queueEvent(()->{
                mEffectManager.setSticker(mSwitch ? mBackgroundBlurPath : "");
            });
        }
    }
}
