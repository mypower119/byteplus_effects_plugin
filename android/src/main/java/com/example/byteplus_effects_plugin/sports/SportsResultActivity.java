package com.example.byteplus_effects_plugin.sports;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.sports.model.SportResult;
import com.example.byteplus_effects_plugin.sports.task.BitmapSaveTask;

/**
 * Created on 2021/7/18 17:19
 */
public class SportsResultActivity extends AppCompatActivity implements View.OnClickListener, BitmapSaveTask.BitmapSaveDelegate {

    private ImageView ivBack;
    private ImageView ivPlaceHolder;
    private TextView tvCount;
    private TextView tvTime;
    private ImageView ivExit;
    private ImageView ivSave;
    private View vResult;

    private SportResult mSportResult;

    public static void startActivity(Context context, SportResult result) {
        Intent intent = new Intent(context, SportsResultActivity.class);
        intent.putExtra(SportResult.SPORT_RESULT_KEY, result);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#F7F8FA"));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_sports_result);

        ivBack = findViewById(R.id.iv_back_sports_result);
        ivPlaceHolder = findViewById(R.id.iv_placeholder_sports_result);
        tvCount = findViewById(R.id.tv_count_sport_result_info);
        tvTime = findViewById(R.id.tv_time_sport_result_info);
        ivExit = findViewById(R.id.iv_exit_sports_result);
        ivSave = findViewById(R.id.iv_save_sports_result);
        vResult = findViewById(R.id.v_info_sports_result);

        initData();
        initView();
    }

    private void initData() {
        mSportResult = getIntent().getParcelableExtra(SportResult.SPORT_RESULT_KEY);
        if (mSportResult == null) {
            throw new IllegalStateException("sport result must not be null");
        }
    }

    private void initView() {
        ivBack.setOnClickListener(this);
        ivExit.setOnClickListener(this);
        ivSave.setOnClickListener(this);
        tvCount.setText(String.valueOf(mSportResult.getCount()));
        tvTime.setText(String.valueOf(mSportResult.getTime()));
        ivPlaceHolder.setImageResource(mSportResult.getSportItem().getRetImgRes());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back_sports_result) {
            finish();
        } else if (v.getId() == R.id.iv_exit_sports_result) {
            finish();
        } else if (v.getId() == R.id.iv_save_sports_result) {
            Bitmap bitmap = Bitmap.createBitmap(vResult.getWidth(), vResult.getHeight(), Bitmap.Config.ARGB_8888);
            vResult.draw(new Canvas(bitmap));
            new BitmapSaveTask(this).execute(bitmap);
        }
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
}
