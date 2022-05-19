package com.example.byteplus_effects_plugin.sports;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.sports.adapter.SportsHomeRVAdapter;
import com.example.byteplus_effects_plugin.sports.manager.SportsDataManager;
import com.example.byteplus_effects_plugin.sports.model.SportItem;

/**
 * Created on 2021/7/13 19:45
 */
public class SportsHomeActivity extends AppCompatActivity implements View.OnClickListener, SportsHomeRVAdapter.OnItemClickListener {
    private RecyclerView rv;
    private ImageView ivBack;

    private SportsDataManager mDataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_sports_home);

        ivBack = findViewById(R.id.iv_back_sports_home);
        rv = findViewById(R.id.rv_sports_home);

        initData();

        initView();
    }

    private void initData() {
        mDataManager = new SportsDataManager();
    }

    private void initView() {
        ivBack.setOnClickListener(this);

        SportsHomeRVAdapter adapter = new SportsHomeRVAdapter(mDataManager.getHomeItems(), this);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
        }
    }

    @Override
    public void onItemClick(SportItem item) {
        SportsPresetActivity.startActivity(this, item);
    }
}
