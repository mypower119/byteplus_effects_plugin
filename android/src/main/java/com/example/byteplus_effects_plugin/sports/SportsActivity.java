package com.example.byteplus_effects_plugin.sports;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.algorithm.render.AlgorithmRender;
import com.example.byteplus_effects_plugin.common.base.BaseGLActivity;
import com.example.byteplus_effects_plugin.common.config.ImageSourceConfig;
import com.example.byteplus_effects_plugin.common.imgsrc.camera.CameraSourceImpl;
import com.example.byteplus_effects_plugin.common.model.ProcessInput;
import com.example.byteplus_effects_plugin.common.model.ProcessOutput;
import com.example.byteplus_effects_plugin.common.utils.DensityUtils;
import com.example.byteplus_effects_plugin.core.algorithm.ActionRecognitionAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.AlgorithmResourceHelper;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseHelper;
import com.example.byteplus_effects_plugin.core.util.ImageUtil;
import com.bytedance.labcv.effectsdk.BefActionRecognitionInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.example.byteplus_effects_plugin.sports.model.SportItem;
import com.example.byteplus_effects_plugin.sports.model.SportResult;
import com.example.byteplus_effects_plugin.sports.task.CountDownTask;
import com.example.byteplus_effects_plugin.sports.utils.SensorManager;
import com.example.byteplus_effects_plugin.sports.widgets.BalanceView;
import com.example.byteplus_effects_plugin.sports.widgets.SportMaskView;
import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created on 2021/7/18 10:43
 */
public class SportsActivity extends BaseGLActivity implements CountDownTask.CountDownCallback, SensorManager.OnSensorChangedListener, BalanceView.OnBalanceListener {
    public static final int STATE_CHECK_BALANCE = 0;
    //  {zh} 开始识别状态  {en} Start recognition status
    public static final int STATE_START_RECOGNIZE = 1;
    //  {zh} 识别成功状态  {en} Recognize success status
    public static final int STATE_RECOGNIZE_SUCCESS = 2;
    //  {zh} 识别成功，开始检测倒计时状态  {en} Recognition successful, start detecting countdown status
    public static final int STATE_COUNT_DOWN = 3;
    //  {zh} 检测状态  {en} Detection status
    public static final int STATE_COUNTING = 4;

    //  {zh} 识别成功倒计时  {en} Recognition success countdown
    public static final int COUNT_DOWN_NUMBER = 3;
    //  {zh} 识别成功等待时间  {en} Identify success latency
    public static final int SUC_WAIT_NUMBER = 1;

    //  {zh} 识别成功等待时间 CountDownTask  {en} Identify Success Wait Time CountDownTask
    public static final int COUNT_DOWN_RECOGNIZE_SUC_KEY = 1;
    //  {zh} 识别成功检测倒计时 CountDownTask  {en} Recognition Successful detection countdown CountDownTask
    public static final int COUNT_DOWN_WAIT_KEY = 2;
    //  {zh} 检测倒计时 CountDownTask  {en} CountDownTask
    public static final int COUNT_DOWN_TIME_KEY = 3;

    private SportItem mSportItem;
    private ActionRecognitionAlgorithmTask mAlgorithm;
    private AlgorithmRender mAlgorithmRender;
    private volatile int mCount = 0;

    private CountDownTask mRecognizeSucCountDown;
    private CountDownTask mWaitCountDown;
    private CountDownTask mTimeCountDown;

    private int mBeepId;
    private SoundPool mSoundPool = null;

    private TextView tvTitle;
    private TextView tvCount;
    private TextView tvTime;
    private SportMaskView vMask;
    private TextView tvCountDown;
    private ImageView ivExit;
    private ImageView ivSwitch;
    private ImageView ivDone;

    private SensorManager mSensorManager;
    private BalanceView bvBalance;

    private final AtomicInteger mState = new AtomicInteger(STATE_CHECK_BALANCE);

    public static void startActivity(Context context, ImageSourceConfig sourceConfig, SportItem sportItem) {
        Intent intent = new Intent(context, SportsActivity.class);
        intent.putExtra(ImageSourceConfig.IMAGE_SOURCE_CONFIG_KEY, new Gson().toJson(sourceConfig));
        intent.putExtra(SportItem.SPORT_ITEM_KEY, sportItem);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        setRequestedOrientation(mSportItem.getNeedLandscape() ?
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        LayoutInflater.from(this)
                .inflate(R.layout.activity_sports,
                        findViewById(R.id.fl_container));

        tvTitle = findViewById(R.id.tv_title_sports);
        ivExit = findViewById(R.id.iv_exit_sports);
        ivSwitch = findViewById(R.id.iv_switch_sports);
        ivDone = findViewById(R.id.iv_done_sports);
        tvCount = findViewById(R.id.tv_count_sports);
        tvTime = findViewById(R.id.tv_time_sports);
        tvCountDown = findViewById(R.id.tv_count_down_sports);
        vMask = findViewById(R.id.v_mask_sports);
        bvBalance = findViewById(R.id.bv_balance);

        initView();

        mSensorManager = new SensorManager(this);
        mSensorManager.start(this);

        bvBalance.setListener(this);

        changeToState(STATE_CHECK_BALANCE);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                forwardState();
//            }
//        }, 2000);
    }

    private void initData() {
        mSportItem = getIntent().getParcelableExtra(SportItem.SPORT_ITEM_KEY);
        if (mSportItem == null) {
            throw new IllegalStateException("sport item must not be null");
        }
    }

    private void initView() {
        ivExit.setOnClickListener(this);
        ivSwitch.setOnClickListener(this);
        ivDone.setOnClickListener(this);

        boolean isLandscape = mSportItem.getNeedLandscape();
        int[] dpPadding = new int[]{
                isLandscape ? 144 : 42,
                isLandscape ? 26 : 82,
                isLandscape ? 144 : 42,
                isLandscape ? 40 : 78,
        };
        vMask.setPadding(
                (int) (DensityUtils.dp2px(this, dpPadding[0])),
                (int) (DensityUtils.dp2px(this, dpPadding[1])),
                (int) (DensityUtils.dp2px(this, dpPadding[2])),
                (int) (DensityUtils.dp2px(this, dpPadding[3]))
        );
        vMask.setShowCorner(!isLandscape);
        vMask.setSvgMask(mSportItem.getMaskRes());
        vMask.setFlipHorizontal(isLandscape);

        int dpMarginStart = (int) DensityUtils.dp2px(this, isLandscape ? 80 : 20);
        ViewGroup.MarginLayoutParams exitParams = (ViewGroup.MarginLayoutParams) ivExit.getLayoutParams();
        exitParams.setMarginStart(dpMarginStart);
        ivExit.setLayoutParams(exitParams);
        ViewGroup.MarginLayoutParams switchParams = (ViewGroup.MarginLayoutParams) ivSwitch.getLayoutParams();
        switchParams.setMarginEnd(dpMarginStart);
        ivSwitch.setLayoutParams(switchParams);
        ViewGroup.MarginLayoutParams doneParams = (ViewGroup.MarginLayoutParams) ivDone.getLayoutParams();
        doneParams.setMarginEnd(dpMarginStart);
        ivDone.setLayoutParams(doneParams);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);

        mAlgorithm = new ActionRecognitionAlgorithmTask(getApplicationContext(),
                new AlgorithmResourceHelper(getApplicationContext()),
                EffectLicenseHelper.getInstance(getApplicationContext()));
        mAlgorithm.initTask(mSportItem.getType());
        mAlgorithmRender = new AlgorithmRender(getApplicationContext());
    }

    @Override
    public ProcessOutput processImpl(ProcessInput input) {
        ByteBuffer buffer = mImageUtil.transferTextureToBuffer(input.getTexture(),
                BytedEffectConstants.TextureFormat.Texure2D,
                BytedEffectConstants.PixlFormat.RGBA8888,
                input.getWidth(), input.getHeight(), 1.f);

        int state = mState.get();
        if (state == STATE_START_RECOGNIZE) {
            BefActionRecognitionInfo.PoseDetectResult result = mAlgorithm.startPose(buffer,
                    input.getWidth(), input.getHeight(), input.getWidth() * 4,
                    BytedEffectConstants.PixlFormat.RGBA8888, mSportItem.readyPoseType(),
                    BytedEffectConstants.Rotation.CLOCKWISE_ROTATE_0);
            if (result != null && result.isDetected) {
                forwardState();
            }
        } else if (state == STATE_COUNTING || state == STATE_RECOGNIZE_SUCCESS) {
            BefActionRecognitionInfo result = mAlgorithm.process(buffer, input.getWidth(),
                    input.getHeight(), input.getWidth() * 4,
                    BytedEffectConstants.PixlFormat.RGBA8888,
                    BytedEffectConstants.Rotation.CLOCKWISE_ROTATE_0);
            if (result != null) {
                if (state == STATE_COUNTING && result.recognizeSucceed) {
                    addCount();
                }

                mAlgorithmRender.init(input.getWidth(), input.getHeight());
                mAlgorithmRender.setResizeRatio(1);
                mAlgorithmRender.drawActionRecognition(result, input.getTexture(), state == STATE_COUNTING);
            }
        }


        boolean isLandscape = mSportItem.getNeedLandscape();
        if (isLandscape) {
            ImageUtil.Transition t = new ImageUtil.Transition()
                    .rotate(270);
            output.texture = mImageUtil.transferTextureToTexture(input.getTexture(),
                    BytedEffectConstants.TextureFormat.Texure2D,
                    BytedEffectConstants.TextureFormat.Texure2D,
                    input.getHeight(), input.getWidth(), t);
            output.width = input.getHeight();
            output.height = input.getWidth();
        } else {
            output.texture = input.getTexture();
            output.width = input.getWidth();
            output.height = input.getHeight();
        }

        return output;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (view.getId() == R.id.iv_exit_sports) {
            finish();
        } else if (view.getId() == R.id.iv_switch_sports) {
            if (mImageSourceProvider instanceof CameraSourceImpl) {
                int cameraId = 1 - ((CameraSourceImpl) mImageSourceProvider).getCameraID();
                mImageSourceConfig.setMedia(String.valueOf(cameraId));
                ((CameraSourceImpl) mImageSourceProvider).changeCamera(cameraId ,null);
            }
        } else if (view.getId() == R.id.iv_done_sports) {
            endSport();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRecognizeSucCountDown != null) {
            mRecognizeSucCountDown.resume();
        }
        if (mWaitCountDown != null) {
            mWaitCountDown.resume();
        }
        if (mTimeCountDown != null) {
            mTimeCountDown.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRecognizeSucCountDown != null) {
            mRecognizeSucCountDown.pause();
        }
        if (mWaitCountDown != null) {
            mWaitCountDown.pause();
        }
        if (mTimeCountDown != null) {
            mTimeCountDown.pause();
        }

        if (mAlgorithm != null) {
            mAlgorithm.destroyTask();
        }
    }

    @Override
    protected void onDestroy() {
        mSensorManager.stop();
        if (mRecognizeSucCountDown != null) {
            mRecognizeSucCountDown.cancel();
        }
        if (mWaitCountDown != null) {
            mWaitCountDown.cancel();
        }
        if (mTimeCountDown != null) {
            mTimeCountDown.cancel();
        }
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
        super.onDestroy();
    }

    @Override
    public void onCountDownDone(int key) {
        switch (key) {
            case COUNT_DOWN_RECOGNIZE_SUC_KEY:
                forwardState();
                mRecognizeSucCountDown = null;
                break;
            case COUNT_DOWN_WAIT_KEY:
                forwardState();
                mWaitCountDown = null;
                break;
            case COUNT_DOWN_TIME_KEY:
                endSport();
                mTimeCountDown = null;
                break;
        }
    }

    @Override
    public void onCountDownTo(int key, int count) {
        switch (key) {
            case COUNT_DOWN_WAIT_KEY:
                tvCountDown.setText(String.valueOf(count+1));
                break;
            case COUNT_DOWN_TIME_KEY:
                int minute = count / 60;
                int second = count - minute * 60;
                tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d", minute, second));
                tvTime.setTextColor(count > 5 ? getResources().getColor(R.color.text_3)
                        : getResources().getColor(R.color.error_5));
                break;
        }
    }

    private void addCount() {
        mCount += 1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvCount.setText(String.valueOf(mCount));
                playBeep();
            }
        });
    }

    private void forwardState() {
        int state = mState.get();
        if (state >= STATE_COUNTING) {
            return;
        }

        changeToState(mState.incrementAndGet());
    }

    private void changeToState(int state) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mState.compareAndSet(state, state)) {
                        changeToState(state);
                    }
                }
            });
            return;
        }
        switch (state) {
            case STATE_CHECK_BALANCE:
                bvBalance.setVisibility(View.VISIBLE);
                vMask.setVisibility(View.GONE);
                tvTitle.setText(R.string.sport_assistance_adjust_device);
                tvTitle.setTextSize(COMPLEX_UNIT_SP, mSportItem.isNeedLandscape() ? 32 : 20);
                break;
            case STATE_START_RECOGNIZE:
                bvBalance.setVisibility(View.GONE);
                vMask.setVisibility(View.VISIBLE);
                tvTitle.setText(R.string.fit_outline);
                tvTitle.setTextSize(COMPLEX_UNIT_SP, 32);
                break;
            case STATE_RECOGNIZE_SUCCESS:
                int blue = getResources().getColor(R.color.brand_color);
                tvTitle.setBackgroundColor(blue);
                tvTitle.setText(R.string.recognize_success);
                vMask.setStrokeColor(blue);

                mRecognizeSucCountDown = new CountDownTask(COUNT_DOWN_RECOGNIZE_SUC_KEY, SUC_WAIT_NUMBER, this);
                mRecognizeSucCountDown.start();
                break;
            case STATE_COUNT_DOWN:
                tvCountDown.setVisibility(View.VISIBLE);
                tvCountDown.setText(String.valueOf(COUNT_DOWN_NUMBER));
                vMask.setVisibility(View.GONE);

                mWaitCountDown = new CountDownTask(COUNT_DOWN_WAIT_KEY, COUNT_DOWN_NUMBER, this);
                mWaitCountDown.start();
                break;
            case STATE_COUNTING:
                tvCountDown.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
                vMask.setVisibility(View.GONE);
                ivSwitch.setVisibility(View.GONE);
                ivDone.setVisibility(View.VISIBLE);
                tvCount.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.VISIBLE);

                tvCount.setText(String.valueOf(0));
                mTimeCountDown = new CountDownTask(COUNT_DOWN_TIME_KEY, mSportItem.getSportTime(), this);
                mTimeCountDown.start();
                break;
        }
    }

    private void endSport() {
        int count = mCount;
        int time = mSportItem.getSportTime() - mTimeCountDown.currentCount();
        SportResult result = new SportResult(mSportItem, count, time);
        SportsResultActivity.startActivity(this, result);
        finishAfterTransition();
    }

    @Override
    public void onSensorChanged(float theta) {
        bvBalance.setTheta(theta);
    }

    @Override
    public void onBalanceStateChanged(BalanceView.State state) {
        if (state == BalanceView.State.OK) {
            if (mState.get() > STATE_CHECK_BALANCE) {
                return;
            }
            forwardState();
        }
    }

    private void playBeep() {
        if (mSoundPool == null) {
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .build();
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    soundPool.play(sampleId, 1, 1, 1, 0, 1);
                }
            });
            mBeepId = mSoundPool.load(getApplicationContext(), R.raw.beep, 1);
        }

        mSoundPool.play(mBeepId, 1, 1, 1, 0, 1);
    }
}
