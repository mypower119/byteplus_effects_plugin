package com.example.byteplus_effects_plugin.effect.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.example.byteplus_effects_plugin.common.adapter.ItemViewRVAdapter;
import com.example.byteplus_effects_plugin.common.fragment.ItemViewPageFragment;
import com.example.byteplus_effects_plugin.common.model.CaptureResult;
import com.example.byteplus_effects_plugin.common.utils.BitmapUtils;
import com.example.byteplus_effects_plugin.common.view.bubble.BubbleWindowManager;
import com.example.byteplus_effects_plugin.core.effect.EffectResourceHelper;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.adapter.EffectRVAdapter;
import com.example.byteplus_effects_plugin.effect.fragment.MattingStickerFragment;
import com.example.byteplus_effects_plugin.effect.model.EffectButtonItem;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;

import java.util.ArrayList;

/** {zh} 
 * Created  on 2021/5/24 4:01 下午
 */

/** {en}
 * Created on 2021/5/24 4:01 pm
 */

public class MattingStickerActivity extends BaseEffectActivity implements MattingStickerFragment.MattingStickerCallback, ItemViewRVAdapter.OnItemClickListener<EffectButtonItem> {
    private MattingStickerFragment mFragment = null;
    public static final String EFFECT_TAG = "effect_board_tag";
    protected static final int REQUEST_RENDER_CACHE_PICKER = 12;
    public static final int TYPE_NO_MATTING = 0;
    public static final int TYPE_UPLOAD_MATTING = 1;
    protected String mBgPath;
    private final String BgKey = "BCCustomBackground";
    private final String BgValueDefault = "stickers/matting_bg/GE/generalEffect/resource1/background.png";


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
                        EffectButtonItem mItem = new EffectButtonItem(TYPE_UPLOAD_MATTING,R.drawable.ic_upload,R.string.upload_title);
                        mItem.setParent(mItem);
                        mItem.setSelectChild(mItem);
                        add(mItem);
                    }
                };
                EffectRVAdapter adapter = new EffectRVAdapter(items,MattingStickerActivity.this);
                ItemViewPageFragment<EffectRVAdapter> fragment = new ItemViewPageFragment<>();
                fragment.setAdapter(adapter);
                add(fragment);
            }
        };
        ArrayList<String> titles = new ArrayList<String>(){
            {
                add(getString(R.string.tab_matting));
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
        } else if (view.getId() == R.id.iv_record_board) {
            takePic();
        } else if (view.getId() == R.id.img_default) {
            mBgPath = null;
            checkAndSetRenderCache();
        }
    }

    @Override
    public void onItemClick(EffectButtonItem item, int position) {
        if (item.getId() == TYPE_NO_MATTING){
            if (null == mSurfaceView){
                return;
            }
            mSurfaceView.queueEvent(()->{
                mEffectManager.setSticker("");
            });


        }else if (item.getId() == TYPE_UPLOAD_MATTING){
            Intent intent =new Intent(this, PickerActivity.class);
            intent.putExtra(PickerConfig.SELECT_MODE,PickerConfig.PICKER_IMAGE);// {zh} 设置选择类型，默认是图片和视频可一起选择(非必填参数) {en} Set the selection type, the default is that pictures and videos can be selected together (non-required parameters)
            long maxSize=188743680L;// {zh} long long long long类型 {en} Long long long long type
            intent.putExtra(PickerConfig.MAX_SELECT_SIZE,maxSize); // {zh} 最大选择大小，默认180M（非必填参数） {en} Maximum selection size, default 180M (not required)
            intent.putExtra(PickerConfig.MAX_SELECT_COUNT,1);  // {zh} 最大选择数量，默认40（非必填参数） {en} Maximum number of selections, default 40 (not required)

            startActivityForResult(intent,REQUEST_RENDER_CACHE_PICKER);
        }
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.img_open) {
            showBoardFragment();
        } else if (id == R.id.img_default_activity) {
            mBgPath = null;
            checkAndSetRenderCache();

        } else if (view.getId() == R.id.img_setting) {
            mBubbleWindowManager.show(view, mBubbleCallback, BubbleWindowManager.ITEM_TYPE.BEAUTY, BubbleWindowManager.ITEM_TYPE.PERFORMANCE);
        }
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RENDER_CACHE_PICKER && resultCode == PickerConfig.RESULT_CODE) {
            if (data != null) {
                ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);// {zh} 选择完后返回的list {en} Returning list after selection
                if (null == select || select.size() == 0)return;
                mBgPath = select.get(0).path;
            } else {
                mBgPath = null;
            }
        }
    }

    protected void checkAndSetRenderCache() {
        //   {zh} Android 中选择图片会导致 SDK 销毁 & 重建       {en} Selecting a picture in Android causes the SDK to be destroyed & rebuilt  
        //   {zh} 直接在 onActivityResult 中设置图片不可用，       {en} Set the picture unavailable directly in onActivityResult,  
        //   {zh} 需要等待 SDK 重新初始化完成后再设置       {en} You need to wait for the SDK to reinitialize before setting  
        if (!TextUtils.isEmpty(mBgPath)) {
            CaptureResult captureResult = decodeByteBuffer(mBgPath);
            if (captureResult == null){
                LogUtils.e("decodeByteBuffer return null!!");

                return;
            }
            if (!mEffectManager.setRenderCacheTexture(
                    BgKey,
                    captureResult.getByteBuffer(),
                    captureResult.getWidth(),
                    captureResult.getHeight(),
                    4*captureResult.getWidth(),
                    BytedEffectConstants.PixlFormat.RGBA8888,
                    BytedEffectConstants.Rotation.CLOCKWISE_ROTATE_0)){
                LogUtils.e("setRenderCacheTexture fail!!");
            }
        }else{
            mEffectManager.setSticker("stickers/matting_bg");
            mEffectManager.setRenderCacheTexture(BgKey, new EffectResourceHelper(this).getStickerPath(BgValueDefault));
        }
    }

    private CaptureResult decodeByteBuffer(String path){
        Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(path,mMaxTextureSize,mMaxTextureSize);
        if (bitmap == null )return null;
        return new CaptureResult(BitmapUtils.bitmap2ByteBuffer(bitmap), bitmap.getWidth(),bitmap.getHeight());

    }

    @Override
    public void onEffectInitialized() {
        super.onEffectInitialized();
        checkAndSetRenderCache();


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


}
