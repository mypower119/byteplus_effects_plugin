package com.example.byteplus_effects_plugin.lens.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.byteplus_effects_plugin.common.base.BaseBarGLActivity;
import com.example.byteplus_effects_plugin.common.imgsrc.camera.CameraSourceImpl;
import com.example.byteplus_effects_plugin.common.model.ProcessInput;
import com.example.byteplus_effects_plugin.common.model.ProcessOutput;
import com.example.byteplus_effects_plugin.common.view.bubble.BubbleWindowManager;
import com.example.byteplus_effects_plugin.core.lens.ImageQualityInterface;
import com.example.byteplus_effects_plugin.core.lens.ImageQualityManager;
import com.example.byteplus_effects_plugin.core.lens.ImageQualityResourceHelper;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.lens.config.ImageQualityConfig;
import com.example.byteplus_effects_plugin.lens.fragment.BoardFragment;
import com.example.byteplus_effects_plugin.lens.manager.ImageQualityDataManager;
import com.google.gson.Gson;

import java.util.HashSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

;

/**
 * Created on 5/18/21 7:07 PM
 */
public class ImageQualityActivity extends BaseBarGLActivity
        implements View.OnClickListener, BoardFragment.IImageQualityCallback {
    private ImageQualityInterface mImageQuality;

    private ImageQualityConfig mConfig;
    private ImageQualityDataManager mDataManager;
    private Fragment mBoardFragment;
    private ImageView imgCompare;
    private volatile boolean isSelected = true;

    protected volatile boolean isOn = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater.from(this)
                .inflate(R.layout.activity_image_quality,
                        findViewById(R.id.fl_base_gl), true);

        removeButtonImgDefault();

        initView();

        //   {zh} 默认弹出底部面板       {en} Bottom panel pops up by default  
        showBoardFragment();
    }

    private void initView() {
        mConfig = parseConfig(getIntent());
        mDataManager = new ImageQualityDataManager();
        mBoardFragment = getBoardFragment(mConfig, mDataManager);
        if (mBoardFragment != null) {
            showBoardFragment(mBoardFragment, R.id.fl_image_quality, "", false);
        }

        imgCompare = findViewById(R.id.img_compare);
        imgCompare.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isOn = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        isOn = true;
                        break;
                }
                return true;
            }
        });
    }

    private void initAlgorithm() {
        mImageQuality = new ImageQualityManager(this, new ImageQualityResourceHelper(getApplicationContext()));
        mImageQuality.init(getExternalFilesDir("assets").getAbsolutePath());
        if (null == mDataManager || null == mConfig) return;
        //   {zh} 设置默认开启       {en} Set default on  
        ImageQualityDataManager.ImageQualityItem item = mDataManager.getItem(mConfig.getKey());
        if (item != null) {
            mImageQuality.selectImageQuality(item.getType(), isSelected);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);

        initAlgorithm();
    }


    @Override
    public ProcessOutput processImpl(ProcessInput input) {
        ImageQualityInterface.ImageQualityResult result = new ImageQualityInterface.ImageQualityResult();


        if (isOn){
            int ret = mImageQuality.processTexture(input.getTexture(), input.getWidth(), input.getHeight(), result);
            if (ret != 0) {
                output.width = input.getWidth();
                output.height = input.getHeight();
                output.texture = input.getTexture();
                return output;
            }

            output.setTexture(result.getTexture());
            output.setWidth(result.getWidth());
            output.setHeight(result.getHeight());
        }else {
            output.width = input.getWidth();
            output.height = input.getHeight();
            output.texture = input.getTexture();
        }

        return output;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == com.example.byteplus_effects_plugin.R.id.img_record){
            takePic(output.texture);
            return;
        }
        super.onClick(view);

        if (view.getId() == R.id.img_open) {
            showBoardFragment();
        } else if (view.getId() == R.id.img_setting) {
            BubbleWindowManager.ITEM_TYPE[] types;
            if (mImageSourceProvider instanceof CameraSourceImpl){
                types = new BubbleWindowManager.ITEM_TYPE[]{BubbleWindowManager.ITEM_TYPE.RESOLUTION, BubbleWindowManager.ITEM_TYPE.PERFORMANCE};

            }else {
                types = new BubbleWindowManager.ITEM_TYPE[]{BubbleWindowManager.ITEM_TYPE.PERFORMANCE};


            }
            mBubbleWindowManager.show(  view, new BubbleWindowManager.BubbleCallback() {


                @Override
                public void onBeautyDefaultChanged(boolean on) {

                }

                @Override
                public void onResolutionChanged(int width, int height) {
                    if (mImageSourceProvider instanceof CameraSourceImpl){
                        ((CameraSourceImpl)mImageSourceProvider).setPreferSize(width,height);
                        ((CameraSourceImpl)mImageSourceProvider).changeCamera(((CameraSourceImpl)mImageSourceProvider).getCameraID(),null);

                    }


                }

                @Override
                public void onPerformanceChanged(boolean on) {
                    mRlPerformance.setVisibility(on?View.VISIBLE:View.INVISIBLE);
                }
            }, types);
        }


    }

    @Override
    public void onItem(ImageQualityDataManager.ImageQualityItem item, boolean flag) {
        if (flag && mBubbleTipManager != null){
            mBubbleTipManager.show(item.getTitle(), item.getDesc());
        }
        mSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                isSelected = flag;
                mImageQuality.selectImageQuality(item.getType(), flag);
            }
        });
    }

    @Override
    public void onClickEvent(View view) {
        if (view.getId() == com.example.byteplus_effects_plugin.R.id.iv_record_board){
            takePic(output.texture);
        }else if (view.getId() == R.id.iv_close_board) {
            hideBoardFragment(mBoardFragment);
        }
    }

    private ImageQualityConfig parseConfig(Intent intent) {
        String sConfig = intent.getStringExtra(ImageQualityConfig.IMAGE_QUALITY_KEY);
        if (sConfig == null) {
            return null;
        }
        LogUtils.d("imagequlity config ="+sConfig);
        return new Gson().fromJson(sConfig, ImageQualityConfig.class);
    }

    private Fragment getBoardFragment(ImageQualityConfig config, ImageQualityDataManager dataManager) {
        ImageQualityDataManager.ImageQualityItem item = dataManager.getItem(config.getKey());
        if (item == null) {
            return null;
        }
        HashSet selected =  new HashSet<ImageQualityDataManager.ImageQualityItem>();
        selected.add(item);
        Fragment fragment = new BoardFragment()
                .setSelectSet(selected)
                .setCallback(this)
                .setItem(item);
        return fragment;
    }

    @Override
    public boolean closeBoardFragment() {
        hideBoardFragment(mBoardFragment);
        return true;
    }

    @Override
    public boolean showBoardFragment() {
        if (null == mBoardFragment) return false;
        showBoardFragment(mBoardFragment, R.id.fl_image_quality, "", true);

        return true;
    }

    @Override
    public void onPause() {
        mSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mImageQuality.destroy();
            }
        });
        //super.onPause 会触发glsurfaceview释放context，需要放在gl资源释放后调用
        super.onPause();
    }


}
