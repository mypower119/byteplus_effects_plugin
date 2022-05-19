package com.example.byteplus_effects_plugin.algorithm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.byteplus_effects_plugin.algorithm.config.AlgorithmConfig;
import com.example.byteplus_effects_plugin.algorithm.fragment.AlgorithmBoardFragment;
import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.algorithm.render.AlgorithmRender;
import com.example.byteplus_effects_plugin.algorithm.ui.AlgorithmUI;
import com.example.byteplus_effects_plugin.algorithm.ui.AlgorithmUIFactory;
import com.example.byteplus_effects_plugin.algorithm.view.TipManager;
import com.example.byteplus_effects_plugin.common.base.BaseBarGLActivity;
import com.example.byteplus_effects_plugin.common.imgsrc.video.VideoSourceImpl;
import com.example.byteplus_effects_plugin.common.model.ProcessInput;
import com.example.byteplus_effects_plugin.common.model.ProcessOutput;
import com.example.byteplus_effects_plugin.common.view.bubble.BubbleWindowManager;
import com.example.byteplus_effects_plugin.core.algorithm.AlgorithmResourceHelper;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseHelper;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created on 5/11/21 5:18 PM
 */
public class AlgorithmActivity extends BaseBarGLActivity
        implements AlgorithmUI.AlgorithmInfoProvider,
        AlgorithmBoardFragment.IAlgorithmCallback {
    public static final String BOARD_FRAGMENT_TAB = "algorithm_board_tag";

    private AlgorithmTask<?, ?> mAlgorithmTask;
    private AlgorithmUI<Object> mAlgorithmUI;
    private Map<AlgorithmTaskKey, Object> mAlgorithmParams;
    private AlgorithmRender mAlgorithmRender;

    @IdRes
    private int mBoardFragmentTargetId = R.id.fl_algorithm_board;
    private Fragment mBoardFragment;
    private TipManager mTipManager;

    private AlgorithmConfig mConfig;
    private boolean mAlgorithmOn = false;
    private Object mResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater.from(getContext())
                .inflate(R.layout.activity_algorithm,
                        findViewById(R.id.fl_base_gl), true);

        removeButtonImgDefault();

        initAlgorithm();

        initView();
    }


    private void initAlgorithm() {
        mConfig = parseAlgorithmConfig(getIntent());
        mAlgorithmTask = createAlgorithmTask(mConfig, new AlgorithmResourceHelper(getContext()), EffectLicenseHelper.getInstance(getContext()));
        mAlgorithmUI = createAlgorithmUI(mConfig);

        mAlgorithmParams = createAlgorithmParams(mConfig);
        mAlgorithmRender = new AlgorithmRender(getContext());

    }

    private void initView() {
        mTipManager = new TipManager();
        mAlgorithmUI.init(this);
        mTipManager.init(getContext(), findViewById(R.id.fl_algorithm_info));
        mBoardFragment = getBoardFragment(new HashSet<>(mAlgorithmParams.keySet()));

        if (mConfig.isShowBoard()) {
            showBoardFragment(mBoardFragment, mBoardFragmentTargetId, BOARD_FRAGMENT_TAB, false);
        }
    }


    @Override
    public boolean showBoardFragment() {
        if (null == mBoardFragment){
            mBoardFragment = getBoardFragment(new HashSet<>(mAlgorithmParams.keySet()));
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mAlgorithmTask.destroyTask();
                mAlgorithmRender.destroy();
                mImageUtil.release();
            }
        });
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);

        mAlgorithmTask.initTask();

        for (Map.Entry<AlgorithmTaskKey, Object> entry : mAlgorithmParams.entrySet()) {
            if (entry.getValue() instanceof Boolean) {
                boolean flag = (boolean) entry.getValue();
                onItem(entry.getKey(), flag);

            }
        }

        // if use vehook, then shell will delete this
        if (mConfig != null && mConfig.isAutoTest()){
               LogUtils.d("hit AutoTest");
              if (mImageSourceProvider instanceof VideoSourceImpl){
                  runOnUiThread(()->{
                      startPlay();
                  });
              }
       }
        // delete above don't delete this line

        // vehook_add don't delete this line
    }

    @Override
    public ProcessOutput processImpl(ProcessInput input) {

        //   {zh} 将转正后的纹理转成 ByteBuffer       {en} Convert the regular texture to ByteBuffer  
        ByteBuffer bb2 = mImageUtil.transferTextureToBuffer(
                input.getTexture(), BytedEffectConstants.TextureFormat.Texure2D, BytedEffectConstants.PixlFormat.RGBA8888,
                input.getWidth(), input.getHeight(), 1
        );
        LogUtils.d("mAlgorithmOn ="+mAlgorithmOn);
        if (mAlgorithmOn) {
            //   {zh} 执行算法检测，并更新 UI       {en} Perform algorithm detection and update the UI  
            mResult = mAlgorithmTask.process(bb2, input.getWidth(), input.getHeight(),
                    input.getWidth() * 4, BytedEffectConstants.PixlFormat.RGBA8888,
                    input.getSensorRotation());
            mAlgorithmUI.onReceiveResult(mResult);

            //   {zh} 将算法结果绘制到目标纹理上       {en} Draw algorithm results onto target texture  
            mAlgorithmRender.init(input.getWidth(), input.getHeight());
            mAlgorithmRender.setResizeRatio(1);

            mAlgorithmRender.drawAlgorithmResult(mResult, input.getTexture());

        }

        output.setTexture(input.getTexture());
        output.setWidth(input.getWidth());
        output.setHeight(input.getHeight());
        return output;
    }

    @Override
    public ProcessOutput processImageImpl(ProcessInput input) {
        return null;
    }

    @Override
    public void processImageYUV(byte[] data, int format, BytedEffectConstants.Rotation rotation, int width, int height, int runtimes) {
    }

    @Override
    public TipManager getTipManager() {
        return mTipManager;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public int getPreviewWidth() {
        return mImageSourceProvider.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return mImageSourceProvider.getHeight();
    }

    @Override
    public boolean fitCenter() {
        return false;
    }

    @Override
    public FragmentManager getFMManager() {
        return getSupportFragmentManager();
    }

    @Override
    public void setBoardTarget(int targetId) {
        mBoardFragmentTargetId = targetId;
    }

    @Override
    public void onItem(AlgorithmItem item, boolean flag) {
        if (null != mSurfaceView){
            mSurfaceView.queueEvent(()->{
                onItem(item.getKey(), flag);

            });
        }

        if (flag) {
            mBubbleTipManager.show(item.getTitle(), item.getDesc());
        }
        //  {zh} 数据UI同步，避免在关闭算法后 进入视频模式再回退，出现默认开启的问题  {en} Data UI synchronization, avoid the problem of default opening after closing the algorithm, entering video mode and then backing off
        mAlgorithmParams.put(item.getKey(), flag);
    }

    @Override
    public void onClickEvent(View view) {
        if (view.getId() == R.id.iv_close_board) {
            hideBoardFragment(mBoardFragment);
        } else if (view.getId() == R.id.iv_record_board) {
            takePic();
        }
    }

    @Override
    public boolean closeBoardFragment() {
        if (mBoardFragment != null && mBoardFragment.isVisible()) {
            hideBoardFragment(mBoardFragment);
            return true;
        }
        return false;

    }


    private void onItem(AlgorithmTaskKey key, boolean flag) {
        //   {zh} 算法 key 控制算法是否开启       {en} Algorithm key controls whether the algorithm is turned on
        mSurfaceView.queueEvent(()->{
            if (key.getKey().equals(mConfig.getType())) {
                mAlgorithmOn = flag;
            }
            mAlgorithmTask.setConfig(key, flag);
        });

        runOnUiThread(()->{
            mAlgorithmUI.onEvent(key, flag);

        });
    }

    @Override
    protected void takePic() {
        takePic(output.getTexture());
    }

    @Override
    public void onClick(View view) {
        LogUtils.d("onClick"+view);
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.img_open) {
            showBoardFragment(mBoardFragment, mBoardFragmentTargetId, BOARD_FRAGMENT_TAB, true);
        } else if(id == R.id.img_setting){
            mBubbleWindowManager.show(view, new BubbleWindowManager.BubbleCallback() {


                @Override
                public void onBeautyDefaultChanged(boolean on) {

                }

                @Override
                public void onResolutionChanged(int width, int height) {

                }

                @Override
                public void onPerformanceChanged(boolean on) {
                    mRlPerformance.setVisibility(on?View.VISIBLE:View.INVISIBLE);


                }
            }, BubbleWindowManager.ITEM_TYPE.PERFORMANCE);
        }
    }

    private Fragment getBoardFragment(HashSet<AlgorithmTaskKey> selectItems) {
        if (mAlgorithmUI.getFragmentGenerator() != null) {
            return new AlgorithmBoardFragment(this)
                    .setInnerFragment(mAlgorithmUI.getFragmentGenerator().create())
                    .setTitleId(mAlgorithmUI.getFragmentGenerator().title());
        } else {
            return new AlgorithmBoardFragment(this)
                    .setSelectSet(selectItems)
                    .setItem(mAlgorithmUI.getAlgorithmItem());
        }
    }


    private AlgorithmConfig parseAlgorithmConfig(Intent intent) {
        String sAlgorithmConfig = intent.getStringExtra(AlgorithmConfig.ALGORITHM_CONFIG_KEY);
        if (sAlgorithmConfig == null) {
            return null;
        }
        LogUtils.d("sAlgorithmConfig ="+sAlgorithmConfig);

        return new Gson().fromJson(sAlgorithmConfig, AlgorithmConfig.class);
    }

    private AlgorithmTask<?, ?> createAlgorithmTask(AlgorithmConfig config, AlgorithmResourceProvider provider, EffectLicenseProvider licenseProvider) {
        return AlgorithmTaskFactory.create(
                getKeyFromType(config.getType()),
                getApplicationContext(),
                provider,
                licenseProvider);
    }

    private AlgorithmUI<Object> createAlgorithmUI(AlgorithmConfig config) {
        return AlgorithmUIFactory.create(getKeyFromType(config.getType()));
    }

    private Map<AlgorithmTaskKey, Object> createAlgorithmParams(AlgorithmConfig config) {
        HashMap<AlgorithmTaskKey, Object> params = new HashMap<>();
        for (Map.Entry<String, Object> entry : config.getParams().entrySet()) {
            params.put(getKeyFromType(entry.getKey()), entry.getValue());
        }
        return params;
    }

    private AlgorithmTaskKey getKeyFromType(String stickerType) {
        return new AlgorithmTaskKey(stickerType);
    }


}
