package com.example.byteplus_effects_plugin.sports;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.common.config.ImageSourceConfig;
import com.example.byteplus_effects_plugin.common.utils.ReflectUtils;
import com.example.byteplus_effects_plugin.sports.manager.SportsDataManager;
import com.example.byteplus_effects_plugin.sports.model.SportItem;

import java.util.Locale;

/**
 * Created on 2021/7/16 11:19
 */
public class SportsPresetActivity
        extends AppCompatActivity
        implements View.OnClickListener,
        NumberPicker.OnValueChangeListener,
        MediaPlayer.OnCompletionListener {
    private SportItem mSportItem;

    private TextView tvTitle;
    private ImageView ivBack;
    private TextView tvTime;
    private Button btnStart;
    private NumberPicker npMinuteTime;
    private NumberPicker npSecondTime;
    private VideoView vv;
    private ImageView ivPlay;

    public static void startActivity(Context context, SportItem item) {
        Intent intent = new Intent(context, SportsPresetActivity.class);
        intent.putExtra(SportItem.SPORT_ITEM_KEY, item);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_sports_preset);
        tvTitle = findViewById(R.id.tv_title_sports_preset);
        ivBack = findViewById(R.id.iv_back_sports_preset);
        tvTime = findViewById(R.id.tv_time_sports_preset);
        btnStart = findViewById(R.id.btn_start_sports_preset);
        npMinuteTime = findViewById(R.id.v_time_minute_picker_sports_preset);
        npSecondTime = findViewById(R.id.v_time_second_picker_sports_preset);
        vv = findViewById(R.id.v_player_sports_preset);
        ivPlay = findViewById(R.id.iv_player_sports_preset);

        initData();
        initView();
    }

    private void initData() {
        mSportItem = getIntent().getParcelableExtra(SportItem.SPORT_ITEM_KEY);
        if (mSportItem == null) {
            throw new IllegalStateException("sport item must not be null");
        }
    }

    private void initView() {
        tvTitle.setText(mSportItem.getTextRes());
        ivBack.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        vv.setOnClickListener(this);
        vv.setOnCompletionListener(this);

        npMinuteTime.setOnValueChangedListener(this);
        SportsDataManager.getMinutePickerItem(getString(R.string.time_min)).attachToNumberPicker(npMinuteTime);
        npMinuteTime.setWrapSelectorWheel(false);
        npSecondTime.setOnValueChangedListener(this);
        SportsDataManager.getSecondPickerItem(getString(R.string.time_sec)).attachToNumberPicker(npSecondTime);
        npSecondTime.setWrapSelectorWheel(false);

        vv.setVideoURI(new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(getPackageName())
                .appendPath(String.valueOf(mSportItem.getPreviewVideoRes()))
            .build());
        vv.start();
    }

    private int mVideoPosition;
    private boolean mVideoPlaying;
    @Override
    protected void onRestart() {
        super.onRestart();

        seekTo(mVideoPosition, !mVideoPlaying);
    }

    @Override
    protected void onPause() {
        mVideoPlaying = vv.isPlaying();
        mVideoPosition = vv.getCurrentPosition();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back_sports_preset) {
            finish();
        } else if (v.getId() == R.id.btn_start_sports_preset) {
            mSportItem.setSportTime(getSportTime());
            ImageSourceConfig sourceConfig = new ImageSourceConfig(
                    ImageSourceConfig.ImageSourceType.TYPE_CAMERA,
                    String.valueOf(Camera.CameraInfo.CAMERA_FACING_FRONT));
            sourceConfig.setRequestWidth(1280);
            sourceConfig.setRequestHeight(720);
            com.example.byteplus_effects_plugin.sports.SportsActivity.startActivity(this, sourceConfig, mSportItem);
            finishAfterTransition();
        } else if (v.getId() == R.id.v_player_sports_preset) {
            if (vv.isPlaying()) {
                ivPlay.setVisibility(View.VISIBLE);
                vv.pause();
            } else {
                ivPlay.setVisibility(View.GONE);
                vv.start();
            }
        } else if (v.getId() == R.id.iv_player_sports_preset) {
            vv.start();
            ivPlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        int minute = npMinuteTime.getValue();
        int second = npSecondTime.getValue();
        tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d", minute, second));
        btnStart.setEnabled(minute != 0 || second != 0);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.seekTo(0);
        mp.start();
    }

    private int getSportTime() {
        return npMinuteTime.getValue() * 60 + npSecondTime.getValue();
    }

    private void seekTo(int position, boolean shouldPause) {
        vv.start();
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        MediaPlayer mediaPlayer = (MediaPlayer) ReflectUtils.getObjectValue(VideoView.class, vv, "mMediaPlayer");
                        if (mediaPlayer != null) {
                            mediaPlayer.seekTo(position, MediaPlayer.SEEK_CLOSEST);

                            if (shouldPause) {
                                vv.pause();
                            }

                            return;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                vv.seekTo(position);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (shouldPause) {
                            vv.pause();
                        }
                    }
                }, 100);
            }
        });
    }
}
