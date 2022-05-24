package com.example.byteplus_effects_plugin.common.base;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.common.config.UIConfig;
import com.example.byteplus_effects_plugin.common.imgsrc.camera.CameraSourceImpl;
import com.example.byteplus_effects_plugin.common.model.BubbleConfig;
import com.example.byteplus_effects_plugin.common.model.CaptureResult;
import com.example.byteplus_effects_plugin.common.task.SavePicTask;
import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.common.view.BubbleTipManager;
import com.example.byteplus_effects_plugin.common.view.bubble.BubbleWindowManager;
import com.example.byteplus_effects_plugin.core.opengl.GlUtil;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.opengles.GL10;

/** {zh} 
 * 包含顶部bar和底部收起拍照按钮的GLActivity
 */

/** {en}
 * GLActivity with top bar and bottom retract photo button
 */

public abstract class BaseBarGLActivity extends BaseGLActivity {

    protected ImageView mImgBack;
    protected ImageView mImgSetting;
    protected ImageView mImgAlbum;
    protected ImageView mImgRotate;

    protected ImageView mImgOpen;
    protected ImageView mImgRecord;
    protected ImageView mImgDefault;
    protected boolean useImgDefault = true;

    //   {zh} 性能数据展示相关       {en} Performance data display related  
    private TextView mTvFps;
    private TextView mTimeCost;
    private TextView mInputSize;
    protected RelativeLayout mRlPerformance;

    //   {zh} 顶部弹出的设置气泡窗口管理类       {en} Top pop-up settings bubble window management class  
    protected BubbleWindowManager mBubbleWindowManager;
    //   {zh} 顶部弹出的气泡窗口设置值封装       {en} Top pop-up bubble window setting value encapsulation  
    protected BubbleConfig mBubbleConfig;

    //   {zh} 顶部的弹出提示管理器       {en} Top pop-up prompt manager  
    protected BubbleTipManager mBubbleTipManager;

    //   {zh} 自定义Handler       {en} Custom Handler  
    protected InnerHandler mHandler = null;

    protected UIConfig mConfig;

    private static final int UPDATE_INFO = 1;
    private static final int UPDATE_INFO_INTERVAL = 1000;

    /** {zh} 
     * 关闭底部功能面板
     * @return
     */
    /** {en} 
     * Close the bottom function panel
     * @return
     */

    public abstract boolean closeBoardFragment();
    /** {zh} 
     * 开启底部功能面板
     * @return
     */
    /** {en} 
     * Open the bottom function panel
     * @return
     */

    public abstract boolean showBoardFragment();




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("onCreate");
        LayoutInflater.from(this).inflate(R.layout.layout_base_bar, mContainer, true);
        mBubbleWindowManager = new BubbleWindowManager(this);
        mBubbleConfig = mBubbleWindowManager.getConfig();
        mHandler = new InnerHandler(this);
        initView();

        mConfig = parseUiConfig(getIntent());
        if (!mConfig.isEnableRotate()){
            mImgRotate.setVisibility(View.INVISIBLE);
        }
        if (!mConfig.isEnbaleAblum()){
            mImgAlbum.setVisibility(View.INVISIBLE);
        }




    }


    private UIConfig parseUiConfig(Intent intent){
        String sStr = intent.getStringExtra(UIConfig.KEY);
        LogUtils.d("effectConfig ="+sStr);
        if (sStr == null) {
            return new UIConfig();
        }

        return new Gson().fromJson(sStr, UIConfig.class);
    }







    private void initView(){
        mBubbleTipManager = new BubbleTipManager(mContext,
                findViewById(R.id.root_view));
        mImgBack = findViewById(R.id.img_back);
        mImgBack.setOnClickListener(this);
        mImgSetting = findViewById(R.id.img_setting);
        mImgSetting.setOnClickListener(this);
        mImgAlbum = findViewById(R.id.img_album);
        mImgAlbum.setOnClickListener(this);
        mImgRotate = findViewById(R.id.img_rotate);
        mImgRotate.setOnClickListener(this);
        mImgOpen = findViewById(R.id.img_open);
        mImgOpen.setOnClickListener(this);
        mImgRecord = findViewById(R.id.img_record);
        mImgRecord.setOnClickListener(this);
        mImgDefault = findViewById(R.id.img_default_activity);
        mImgDefault.setOnClickListener(this);
        if (!useImgDefault) {
            mImgDefault.setVisibility(View.GONE);
        }

        mInputSize = findViewById(R.id.tv_resolution);
        mTvFps = findViewById(R.id.tv_fps);
        mTimeCost = findViewById(R.id.tv_time);


        findViewById(R.id.root_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return closeBoardFragment();
            }
        });

        if (!(mImageSourceProvider instanceof CameraSourceImpl)){
            mImgAlbum.setVisibility(View.INVISIBLE);
            mImgRotate.setVisibility(View.INVISIBLE);

        }
        mRlPerformance = findViewById(R.id.rl_performance);
        //    {zh} 设置性能的展示区        {en} Set performance display area  
        if (mBubbleConfig != null){

            mRlPerformance.setVisibility(mBubbleConfig.isPerformance()?View.VISIBLE:View.INVISIBLE);
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessageDelayed(UPDATE_INFO, UPDATE_INFO_INTERVAL);


    }

    double frameCount = 0;
    @Override
    public void onDrawFrame(GL10 gl10) {
        long start = System.currentTimeMillis();
        super.onDrawFrame(gl10);
        long cost = System.currentTimeMillis() - start;
        if (frameCount++%10 == 0){
            runOnUiThread(()->{
                mTimeCost.setText(cost + "ms");
                mInputSize.setText(mTextureHeight + "*" + mTextureWidth);
            });
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.img_back){
            finish();
        }else if (view.getId() == R.id.img_album){
            startChoosePic();

        }else if (view.getId() == R.id.img_record || view.getId() == R.id.iv_record_board){
            takePic();

        }else if(view.getId() == R.id.img_rotate){
            if (mImageSourceProvider  instanceof  CameraSourceImpl){
                int cameraId = 1 - ((CameraSourceImpl) mImageSourceProvider).getCameraID();
                mImageSourceConfig.setMedia(String.valueOf(cameraId));
                ((CameraSourceImpl) mImageSourceProvider).changeCamera(cameraId ,null);
            }

        }

    }


    volatile boolean mIsCapturing = false;

    /** {zh}
     * 截图保存,试用于结果渲染到由ImageUtils准备的纹理中的功能
     */
    /** {en}
     * Save screenshots, try the function of rendering results into textures prepared by ImageUtils
     */

    protected void takePic(){

        if (mImageUtil == null || mImageUtil.getOutputTexture() == GlUtil.NO_TEXTURE){
            LogUtils.e("离屏渲染的目标纹理未初始化");
            return;
        }
        takePic(mImageUtil.getOutputTexture());
    }

    protected void takeHalPic() {
    }

    /** {zh} 
     * 截图保存
     * @param textureId 结果纹理
     */
    /** {en} 
     * Screenshot save
     * @param textureId  result texture
     */

    protected void takePic(int textureId){
        if (mIsCapturing) {
            ToastUtils.show(getString(R.string.capture_ing));
            return;
        }
        if (null != mSurfaceView) {
            mSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (null == mImageUtil) return;
                    if (mIsCapturing) {
                        ToastUtils.show(getString(R.string.capture_ing));
                        return;
                    }
                    mIsCapturing = true;
                    LogUtils.d("takePic invoked");
                    final CaptureResult captureResult = new CaptureResult(mImageUtil.captureRenderResult(textureId, mTextureWidth, mTextureHeight),
                            mTextureWidth,mTextureHeight );
                    mIsCapturing = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null == captureResult || captureResult.getWidth() == 0 || captureResult.getHeight() == 0 || null == captureResult.getByteBuffer()) {
                                ToastUtils.show(getString(R.string.capture_fail));
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
            });
        }

    }



    /** {zh} 
     * 显示升起面板
     * @param fragment
     * @param layoutId
     * @param tag
     * @param animate
     */
    /** {en} 
     * Show rising panel
     * @param fragment
     * @param layoutId
     * @param tag
     * @param animate
     */

    protected void showBoardFragment(Fragment fragment, int layoutId, String tag, boolean animate) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (animate) {
            ft.setCustomAnimations(R.anim.board_enter, R.anim.board_exit);
        }
        Fragment existFragment = fm.findFragmentByTag(tag);

        if (existFragment == null) {
            ft.add(layoutId, fragment, tag).show(fragment).commitNow();
        } else {
            ft.show(fragment).commitNow();
        }
        showOrHideBoard(true, animate);
    }

    /** {zh}
     * 显示升起面板
     * @param fragment
     * @param layoutId
     * @param tag
     * @param animate
     */
    /** {en}
     * Show rising panel
     * @param fragment
     * @param layoutId
     * @param tag
     */

    protected void showFragment(Fragment fragment, int layoutId, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment existFragment = fm.findFragmentByTag(tag);

        if (existFragment == null) {
            ft.add(layoutId, fragment, tag).show(fragment).commitNow();
        } else {
            ft.show(fragment).commitNow();
        }
    }

    /** {zh}
     * 收起功能面板
     * @param fragment
     */
    /** {en}
     * Collapse function panel
     * @param fragment
     */

    protected void hideFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(fragment).commitNow();
        }
    }

    /** {zh} 
     * 收起功能面板
     * @param fragment
     */
    /** {en} 
     * Collapse function panel
     * @param fragment
     */

    protected void hideBoardFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.board_enter, R.anim.board_exit);
            ft.hide(fragment).commitNow();
        }

        showOrHideBoard(false, true);
    }

    protected void showOrHideBoard(boolean show, boolean animate) {
        if (show) {
            mImgOpen.setVisibility(View.GONE);
            mImgRecord.setVisibility(View.GONE);
            if(useImgDefault){
                mImgDefault.setVisibility(View.GONE);
            }

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mImgOpen.setVisibility(View.VISIBLE);
                    mImgRecord.setVisibility(View.VISIBLE);
                    if (useImgDefault) {
                        mImgDefault.setVisibility(View.VISIBLE);
                    }
                }
            }, animate ? 400 : 0);

        }
    }


    protected static class InnerHandler extends Handler {
        private final WeakReference<BaseBarGLActivity> mActivity;

        public InnerHandler(BaseBarGLActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseBarGLActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case UPDATE_INFO:
                        activity.mTvFps.setText("" + activity.mFrameRator.getFrameRate());
                        sendEmptyMessageDelayed(UPDATE_INFO, UPDATE_INFO_INTERVAL);
                        break;


                }
            }
        }

    }

    public void removeButtonImgDefault(){
        useImgDefault = false;
        mImgDefault.setVisibility(View.GONE);
    }

}
