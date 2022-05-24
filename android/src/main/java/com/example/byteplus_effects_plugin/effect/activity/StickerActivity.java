package com.example.byteplus_effects_plugin.effect.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.bef.effectsdk.message.MessageCenter;
import com.example.byteplus_effects_plugin.common.model.CaptureResult;
import com.example.byteplus_effects_plugin.common.task.SavePicTask;
import com.example.byteplus_effects_plugin.common.utils.BitmapUtils;
import com.example.byteplus_effects_plugin.common.utils.DensityUtils;
import com.example.byteplus_effects_plugin.common.utils.LocaleUtils;
import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.common.view.bubble.BubbleWindowManager;
import com.example.byteplus_effects_plugin.core.effect.EffectManager;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.config.StickerConfig;
import com.example.byteplus_effects_plugin.effect.fragment.SelectUploadFragment;
import com.example.byteplus_effects_plugin.effect.fragment.TabStickerFragment;
import com.example.byteplus_effects_plugin.effect.model.SelectUploadItem;
import com.example.byteplus_effects_plugin.effect.resource.StickerFetch;
import com.example.byteplus_effects_plugin.effect.resource.StickerGroup;
import com.example.byteplus_effects_plugin.effect.resource.StickerItem;
import com.example.byteplus_effects_plugin.effect.resource.StickerPage;
import com.example.byteplus_effects_plugin.effect.task.FaceVerifyThreadHandler;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.example.byteplus_effects_plugin.resource.BaseResource;
import com.example.byteplus_effects_plugin.resource.ErrorCode;
import com.example.byteplus_effects_plugin.resource.LocalResource;
import com.example.byteplus_effects_plugin.resource.ResourceManager;
import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileFilter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * {zh}
 * 贴纸活动页
 */

/**
 * {en}
 * Sticker activity page
 */


public class StickerActivity extends BaseEffectActivity implements TabStickerFragment.OnTabStickerFramentCallback, StickerFetch.FetchListener, BaseResource.ResourceListener, SelectUploadFragment.ISelectUploadCallback, MessageCenter.Listener {
    private TabStickerFragment mFragment = null;
    public static final String EFFECT_TAG = "effect_board_tag";
    public static final String FRAGMENT_SELECT_UPLOAD = "fragment_select_upload";
    public static final int ANIMATION_DURATION = 400;

    private StickerConfig mStickerConfig;
    private StickerItem mSelectedItem;
    private HashMap<BaseResource, ResourceIndex> mResourceIndexMap = new HashMap<>();
    private StickerFetch mStickerFetch;
    private ResourceManager mResourceManager;

    // 上传贴纸所用成员
    private SelectUploadFragment mSelectUploadFragment;
    public static final int SELECT_UPLOAD = R.drawable.ic_select_upload;
    public static final int DEFAULT_UPLOAD_1 = R.drawable.ruining;
    public static final int DEFAULT_UPLOAD_2 = R.drawable.shiwen;
    public static final int DEFAULT_UPLOAD_3 = R.drawable.yangfan;
    protected static final int REQUEST_SELECT_UPLOAD_PICKER = 13;
    private FaceVerifyThreadHandler mThreadHandler;
    private Handler mHandler;
    private Messenger mMessenger;
    private String mSelectedItemKey = null;
    private String mSelectedItemType = null;
    private String mImgUploadPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEffectManager.addMessageListener(this);

        LayoutInflater.from(this)
                .inflate(R.layout.layout_select_upload,findViewById(R.id.fl_effect_fragment), true);

        mStickerConfig = parseStickerConfig(getIntent());

        //   {zh} 默认弹出底部面板       {en} Bottom panel pops up by default
        showBoardFragment();
        if (null == mStickerConfig) return;

        mResourceManager = ResourceManager.getInstance(this);

        // 加载本地贴纸，应用安装目录下stickerRootPath目录内有文件夹时做本地素材测试，否则进入在线下发流程
        List<String> info = new ArrayList<String>(){
            {
                // 加载本地测试贴纸的info设置示例
                String stickerRootPath = "/storage/emulated/0/Android/data/" + mContext.getPackageName() + "/files/assets/resource/StickerResource.bundle/stickers/local/";
                File[] files = new File(stickerRootPath).listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        if (pathname.isDirectory() && !pathname.isHidden()) {
                            return true;
                        }
                        return false;
                    }
                });

                if (files != null) {
                    for (File file : files) {
                        add(file.getName());
                        add(stickerRootPath+file.getName());
                    }
                }
                // 在此设置本地测试贴纸。只需要为每个贴纸依次添加"标题"、"路径"两个字符串即可，程序会自动切换为本地测试的功能
                // 需要切换回素材下发功能时，注释掉所有设置使用的add()方法即可。

            }
        };
        StickerPage page = stickerPageWithLocalResource(info);
        if (page != null) {
            // 若能从info中解析出"标题-路径对"，则进行本地测试
            mFragment.setData(Arrays.asList(page.getTabs()));
        } else {
            // 若info未设置或不能从info中不能解析出"标题-路径对"，则进入正常的素材下发流程
            // 远程素材下发
            mStickerFetch = new StickerFetch();
            mStickerFetch.setListener(this);
            try {
                mStickerFetch.fetchPageWithType(this, mStickerConfig.getType().getName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                switch (what) {
                    case FaceVerifyThreadHandler.FACE_DETECT:
                        //   {zh} 上传的图片中有且仅有一张才开始设置渲染缓存       {en} Only one of the uploaded images was detected
                        // start detect when only one of the uploaded images is detected
                        if (msg.arg1 == 1){
//                            ToastUtils.show("人脸检测完成！开始setRenderCache");
                            if (setRenderCacheTexture((CaptureResult) msg.obj)) {
                                // RenderCacheTexture 设置成功
                                hideBoardFragment(mFragment);
                            }

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.show(getResources().getString(R.string.no_face_detected));

                                }
                            });
                            LogUtils.e("the  bitmap uploaded contains no face or more than one face!!");
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        mEffectManager.removeMessageListener(this);
        mSelectedItem = null;
        mResourceManager.clearLoadingResource();

        super.onDestroy();
    }

    private StickerConfig parseStickerConfig(Intent intent) {
        String sAlgorithmConfig = intent.getStringExtra(StickerConfig.StickerConfigKey);
        if (sAlgorithmConfig == null) {
            return null;
        }

        return new Gson().fromJson(sAlgorithmConfig, StickerConfig.class);
    }


    private TabStickerFragment generateStickerFragment() {
        if (mFragment != null) return mFragment;
        mFragment = new TabStickerFragment();
        mFragment.setCallback(this);
        return mFragment;
    }

    @Override
    public void onStickerSelected(StickerItem item, int tabIndex, int contentIndex) {
        if (item.getResource() == null || item.getResource() instanceof LocalResource) {
            mSelectedItem = null;

            String path = item.getResource() == null ? "" : mResourceManager.syncGetResource(item.getResource()).path;
            // 读取本地素材时传递的是绝对路径，需要传递相对 StickerResource.bundle 路径可使用mEffectManager.setSticker(path);
            mEffectManager.setStickerAbs(path);
            didSelectItem(item, tabIndex, contentIndex);
        } else {
            willSelectItem(item, tabIndex, contentIndex);
            mResourceManager.asyncGetResource(item.getResource(), this);
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_record) {
            if (mSelectedItemType != null) {
                if (mSelectedItemType.contains("msgcap")) {
                    mEffectManager.sendCaptureMessage();
                    return;
                }
            }
        }
        super.onClick(view);
        if (id == R.id.img_open) {
            showBoardFragment();
        } else if (id == R.id.img_default_activity) {
            resetDefault();
            mSelectedItem = null;
            didSelectItem(null, 0, 0);
        } else if (view.getId() == R.id.img_setting) {
            mBubbleWindowManager.show(view, mBubbleCallback, BubbleWindowManager.ITEM_TYPE.BEAUTY, BubbleWindowManager.ITEM_TYPE.PERFORMANCE);
        }

    }

    @Override
    public void onClickEvent(View view) {
        if (view.getId() == R.id.iv_close_board) {
            hideBoardFragment(mFragment);

        } else if (view.getId() == R.id.iv_record_board) {
            if (mSelectedItemType != null) {
                if (mSelectedItemType.contains("msgcap")) {
                    mEffectManager.sendCaptureMessage();
                    return;
                }
            }
            takePic();
        } else if (view.getId() == R.id.img_default) {
            resetDefault();
            mSelectedItem = null;
            didSelectItem(null, 0, 0);
        }
    }

    public void setSelectUploadFragmentHeight(float y, int duration) {
        int boardLayoutID = getResources().getIdentifier("fl_select_upload", "id", getPackageName());
        View selectUploadView = getWindow().findViewById(boardLayoutID);
        selectUploadView.animate().y(y).setDuration(duration).start();
    }

    @Override
    public void onEffectInitialized() {
        super.onEffectInitialized();
        if (mStickerConfig != null && !TextUtils.isEmpty(mStickerConfig.getStickerPath())) {
            mEffectManager.setStickerAbs(mStickerConfig.getStickerPath());
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
    protected void hideBoardFragment(Fragment fragment) {
        // change SelectUpdateFragment position height
        float height = getResources().getDisplayMetrics().heightPixels
                 - getResources().getDimensionPixelSize(R.dimen.height_board_bottom)
                 - DensityUtils.dp2px(mContext,64+6);
        setSelectUploadFragmentHeight(height ,ANIMATION_DURATION);
        super.hideBoardFragment(fragment);
    }

    @Override
    public boolean showBoardFragment() {
        if (null == mFragment) {
            mFragment = generateStickerFragment();
        }
        showBoardFragment(mFragment, mBoardFragmentTargetId, EFFECT_TAG, true);

        // change SelectUpdateFragment position height
        float height = getResources().getDisplayMetrics().heightPixels
                - getResources().getDimensionPixelSize(R.dimen.height_board_total)
                - DensityUtils.dp2px(mContext,64+6);
        setSelectUploadFragmentHeight(height ,ANIMATION_DURATION);

        return true;
    }

    @Override
    protected boolean isAutoTest() {
        return mStickerConfig != null && !TextUtils.isEmpty(mStickerConfig.getStickerPath());
    }

    @Override
    protected void setEffectByConfig() {
        if (mEffectManager == null || mStickerConfig == null) return;
        mEffectManager.setSticker(mStickerConfig.getStickerPath());
    }


    @Override
    public void onStickerFetchSuccess(StickerPage page, boolean fromCache) {
        runOnUiThread(() -> {
            mFragment.setData(Arrays.asList(page.getTabs()));
        });
    }

    @Override
    public void onStickerFetchError(int errorCode, String msg) {
        runOnUiThread(() -> {
//            ToastUtils.show(msg);
            switch (errorCode){
                case ErrorCode.DEFAULT:
                    ToastUtils.show(mContext.getString(R.string.resource_download_fail));
                    break;
                case ErrorCode.NETWORK_NOT_AVAILABLE:
                    ToastUtils.show(mContext.getString(R.string.network_needed));
                    break;
                case ErrorCode.FILE_NOT_AVAILABLE:
                    ToastUtils.show(mContext.getString(R.string.file_not_prepared));
                    break;
                case ErrorCode.MD5_CHECK_ERROR:
                    ToastUtils.show(mContext.getString(R.string.file_check_fail));
                    break;
                case ErrorCode.UNZIP_ERROR:
                    ToastUtils.show(mContext.getString(R.string.unzip_fail));
                    break;
            }
        });
    }

    @Override
    public void onResourceSuccess(BaseResource resource, BaseResource.ResourceResult result) {
        runOnUiThread(() -> {
            if (mSelectedItem != null && resource == mSelectedItem.getResource()) {
                didSelectItem(mSelectedItem);
                mSelectedItem = null;

                if (null != mSurfaceView) {
                    mSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            mEffectManager.setStickerAbs(result.path);
                        }
                    });
                }

                if (mSelectedItemType != null && mSelectedItemType.contains("hideboard")) {
                    hideBoardFragment(mFragment);
                }
                if (mSelectedItemType != null && mSelectedItemType.contains("msgcap")) {
                    setImgCompareViewVisibility(View.GONE);
                } else {
                    setImgCompareViewVisibility(View.VISIBLE);
                }

            }
            refreshResourceUI(resource);
            mResourceIndexMap.remove(resource);
        });

    }

    @Override
    public void onResourceFail(BaseResource resource, int errorCode, String msg) {
        runOnUiThread(() -> {
            switch (errorCode){
                case ErrorCode.DEFAULT:
                    ToastUtils.show(mContext.getString(R.string.resource_download_fail));
                    break;
                case ErrorCode.NETWORK_NOT_AVAILABLE:
                    ToastUtils.show(mContext.getString(R.string.network_needed));
                    break;
                case ErrorCode.FILE_NOT_AVAILABLE:
                    ToastUtils.show(mContext.getString(R.string.file_not_prepared));
                    break;
                case ErrorCode.MD5_CHECK_ERROR:
                    ToastUtils.show(mContext.getString(R.string.file_check_fail));
                    break;
                case ErrorCode.UNZIP_ERROR:
                    ToastUtils.show(mContext.getString(R.string.unzip_fail));
                    break;
            }

            refreshResourceUI(resource);
            mResourceIndexMap.remove(resource);
        });
    }

    @Override
    public void onResourceStart(BaseResource resource) {
        runOnUiThread(() -> {
            refreshResourceUI(resource);
        });
    }

    @Override
    public void onResourceProgressChanged(BaseResource resource, float progress) {
        runOnUiThread(() -> {
            refreshResourceUI(resource);
        });
    }

    private void willSelectItem(StickerItem stickerItem, int tabIndex, int contentIndex) {
        mSelectedItem = stickerItem;
        mResourceIndexMap.put(stickerItem.getResource(), new ResourceIndex(tabIndex, contentIndex));
    }

    private void didSelectItem(StickerItem stickerItem) {
        ResourceIndex index = mResourceIndexMap.get(stickerItem.getResource());
        if (index == null) {
            didSelectItem(stickerItem, 0, 0);
        } else {
            didSelectItem(stickerItem, index.tabIndex, index.contentIndex);
        }
    }

    private void didSelectItem(StickerItem stickerItem, int tabIndex, int contentIndex) {
        mFragment.selectItem(tabIndex, contentIndex);

        if (stickerItem != null && mBubbleTipManager != null) {
            mBubbleTipManager.show(stickerItem.getTitle(this), null);
            if (!TextUtils.isEmpty(stickerItem.getTip(this))) {
                ToastUtils.show(stickerItem.getTip(this));
            }
        }

        if (mSelectedItem != null) {
            if (mSelectedItem.getKey() != null) {
                // 1. display SelectUploadFragment 2. start face verify thread
                showSelectUploadFragment();
            } else {
                // 1. clear render cache 2. close face verify thread 3. hide SelectUploadFragment
                closeSelectUploadFragment();
            }
            mSelectedItemKey = mSelectedItem.getKey();
            mSelectedItemType = mSelectedItem.getType();
        } else {
            closeSelectUploadFragment();
            mSelectedItemKey = null;
            mSelectedItemType = null;
        }

    }

    private void refreshResourceUI(BaseResource resource) {
        ResourceIndex index = mResourceIndexMap.get(resource);
        assert index != null;
        mFragment.refreshItem(index.tabIndex, index.contentIndex);
    }

    @Override
    public void onUploadSelected(SelectUploadItem buttonItem, int position) {
        if (buttonItem.getIcon() == SELECT_UPLOAD) {
            // jump to image select activity
            Intent intent =new Intent(this, PickerActivity.class);
            intent.putExtra(PickerConfig.SELECT_MODE,PickerConfig.PICKER_IMAGE);// {zh} 设置选择类型，默认是图片和视频可一起选择(非必填参数) {en} Set the selection type, the default is that pictures and videos can be selected together (non-required parameters)
            long maxSize=188743680L;// {zh} long long long long类型 {en} Long long long long type
            intent.putExtra(PickerConfig.MAX_SELECT_SIZE,maxSize); // {zh} 最大选择大小，默认180M（非必填参数） {en} Maximum selection size, default 180M (not required)
            intent.putExtra(PickerConfig.MAX_SELECT_COUNT,1);  // {zh} 最大选择数量，默认40（非必填参数） {en} Maximum number of selections, default 40 (not required)
            startActivityForResult(intent,REQUEST_SELECT_UPLOAD_PICKER);
        } else {
            if(setRenderCacheTexture(buttonItem.getIcon())){
                hideBoardFragment(mFragment);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_SELECT_UPLOAD_PICKER && resultCode == PickerConfig.RESULT_CODE) {
            if (data != null) {
                ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);// {zh} 选择完后返回的list {en} Returning list after selection
                if (null == select || select.size() == 0)return;
                String imagePath = select.get(0).path;
                checkFaceContained(imagePath);
            } else {
                mImgUploadPath = null;
            }
        }
    }

    /** {zh}
     * 设置选择的底图
     * Set the selected image to compare
     *
     * @param imagePath
     */
    /** {en}
     * Set the selected base map
     * Set the selected image to compare
     *
     * @param imagePath
     */

    public void checkFaceContained(String imagePath) {
        Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(imagePath, 800, 800);
        if (bitmap != null && !bitmap.isRecycled()) {
            Message msg = mThreadHandler.obtainMessage(FaceVerifyThreadHandler.SET_ORIGINAL, bitmap);
            msg.replyTo = mMessenger;
            msg.sendToTarget();
        } else {
            ToastUtils.show("failed to get image");
        }
    }

    @Override
    public void onMessageReceived(int i, int i1, int i2, String s) {
        ByteBuffer buf = null;
        if(i == EffectManager.MSG_ID_CAPTURE_IMAGE_RESULT && s != null && !s.isEmpty())
        {
            buf = mEffectManager.getCapturedImageByteBufferWithKey(s,mTextureWidth,mTextureHeight);
            final CaptureResult captureResult = new CaptureResult(buf, mTextureWidth, mTextureHeight);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null == captureResult || captureResult.getWidth() == 0 || captureResult.getHeight() == 0 || null == captureResult.getByteBuffer()) {
                        ToastUtils.show(getString(com.example.byteplus_effects_plugin.R.string.capture_fail));
                    } else {
                        LogUtils.d("takePic return success");
                        SavePicTask task = new SavePicTask(new SavePicTask.SavePicDelegate() {
                            @Override
                            public ContentResolver getContentResolver() {
                                return mContext.getContentResolver();
                            }

                            @Override
                            public void onSavePicFinished(boolean success, String path) {
                                if (success) {
                                    setResult(Activity.RESULT_OK, getIntent().putExtra("image_path", path));

                                    ToastUtils.show(getString(R.string.capture_ok));
                                } else {

                                    ToastUtils.show(getString(R.string.capture_fail));
                                }
                                finish();


                            }
                        });
                        task.execute(captureResult);
                    }
                }
            });
        }
    }

    private static class ResourceIndex {
        int tabIndex;
        int contentIndex;

        public ResourceIndex(int tabIndex, int contentIndex) {
            this.tabIndex = tabIndex;
            this.contentIndex = contentIndex;
        }
    }

    private void showSelectUploadFragment(){
        // create SelectUploadFragment, ThreadHandler, Messenger if empty

        if (mSelectUploadFragment == null) {
            List<SelectUploadItem> list = new ArrayList<SelectUploadItem>(){
                {
                    add(new SelectUploadItem(SELECT_UPLOAD));
                    add(new SelectUploadItem(DEFAULT_UPLOAD_1));
                    add(new SelectUploadItem(DEFAULT_UPLOAD_2));
                    add(new SelectUploadItem(DEFAULT_UPLOAD_3));
                }
            };
            mSelectUploadFragment = SelectUploadFragment.newInstance(list).setUploadSelectedCallback(this);
        }

        // set SelectUpload position height
        float height = getResources().getDisplayMetrics().heightPixels
                - getResources().getDimensionPixelSize(R.dimen.height_board_total)
                - DensityUtils.dp2px(mContext,64+6);
        setSelectUploadFragmentHeight(height ,ANIMATION_DURATION);

        showFragment(mSelectUploadFragment,R.id.fl_select_upload,FRAGMENT_SELECT_UPLOAD);

        if (mThreadHandler == null){
            mThreadHandler = FaceVerifyThreadHandler.createFaceVerifyHandlerThread(mContext);
        }
        if (mMessenger == null) {
            mMessenger = new Messenger(mHandler);
        }
        mThreadHandler.resume();
    }

    private void hideSelectUploadFragment(){
        hideFragment(mSelectUploadFragment);
    }

    private void closeSelectUploadFragment(){
        if (mSelectedItemKey != null) {
            clearRenderCacheTexture(mSelectedItemKey);
        }
        if (mThreadHandler != null) {
            mThreadHandler.quit();
            mThreadHandler = null;
        }

        if (mMessenger != null) {
            mMessenger = null;
        }
        hideFragment(mSelectUploadFragment);
    }

//    public String getDrawableUri(@DrawableRes int id){
//        String uri = mContext.getPackageResourcePath() +
//                getResources().getResourcePackageName(id) + "/" +
//                getResources().getResourceTypeName(id) + "/" +
//                getResources().getResourceEntryName(id);
//        return uri;
//    }

    // 本地资源调试
    private StickerPage stickerPageWithLocalResource(List<String> info){
        if (info == null) {
            return null;
        }
        if (info.size()%2 != 0 || info.size() == 0) {
            return null;
        }
        StickerPage stickerPage = new StickerPage();
        String lang = LocaleUtils.getLanguage(mContext);
        stickerPage.setTitles(new HashMap(){
            {
                put(lang,mStickerConfig.getType().getName());
            }
        });

        StickerGroup stickerGroup = new StickerGroup();
        stickerGroup.setTitles(new HashMap(){
            {
                put(lang,mStickerConfig.getType().getName());
            }
        });

        StickerItem[] stickerItems = new StickerItem[info.size()/2];
        for(int i = 0; i< info.size()/2; i++){
            StickerItem stickerItem = new StickerItem();
            int finalI = i;
            stickerItem.setTitles(new HashMap(){
                {
                    put(lang,info.get(finalI *2));
                }
            });
            stickerItem.setResource(new LocalResource(null,info.get(finalI *2 + 1)));
            stickerItems[i] = stickerItem;
        }
        stickerGroup.setItems(stickerItems);

        stickerPage.setTabs(new StickerGroup[]{stickerGroup});

        return stickerPage;
    }

    public boolean setRenderCacheTexture(@DrawableRes int id){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
        if (bitmap != null) {
            ByteBuffer buffer = BitmapUtils.bitmap2ByteBuffer(bitmap);
            if (!mEffectManager.setRenderCacheTexture(
                    mSelectedItemKey,
                    buffer,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    4*bitmap.getWidth(),
                    BytedEffectConstants.PixlFormat.RGBA8888,
                    BytedEffectConstants.Rotation.CLOCKWISE_ROTATE_0)){
                LogUtils.e("setRenderCacheTexture fail!!");
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean setRenderCacheTexture(CaptureResult captureResult){
        if (captureResult == null){
            LogUtils.e("decodeByteBuffer return null!!");
            return false;
        }
        if (!mEffectManager.setRenderCacheTexture(
                mSelectedItemKey,
                captureResult.getByteBuffer(),
                captureResult.getWidth(),
                captureResult.getHeight(),
                4*captureResult.getWidth(),
                BytedEffectConstants.PixlFormat.RGBA8888,
                BytedEffectConstants.Rotation.CLOCKWISE_ROTATE_0)){
            LogUtils.e("setRenderCacheTexture fail!!");
            return false;
        }
        return true;
    }

    public void clearRenderCacheTexture(String key){
        if (key != null) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(0);
            if (!mEffectManager.setRenderCacheTexture(
                    mSelectedItemKey,
                    buffer,
                    0,
                    0,
                    0,
                    BytedEffectConstants.PixlFormat.RGBA8888,
                    BytedEffectConstants.Rotation.CLOCKWISE_ROTATE_0)){
                LogUtils.e("setRenderCacheTexture fail!!");
            }
        }
    }

}
