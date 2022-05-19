package com.example.byteplus_effects_plugin.algorithm.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefCarDetectInfo;

public class BrandRecogTip extends ResultTip<BefCarDetectInfo.BefBrandInfo> {
    private TextView tvOrientation;
    private int height = getResources().getDimensionPixelSize(R.dimen.car_orientation_info_height);

    static String[] provinceMap= {
            String.valueOf(R.string.car_direction_zhengqian),
            String.valueOf(R.string.car_direction_zhengzhou),
            String.valueOf(R.string.car_direction_zuoce),
            String.valueOf(R.string.car_direction_youce),
            String.valueOf(R.string.car_direction_zuoceqian),
            String.valueOf(R.string.car_direction_youceqian),
            String.valueOf(R.string.car_direction_zuocehou),
            String.valueOf(R.string.car_direction_youcehou)
    };



    public BrandRecogTip(@NonNull Context context) {
        super(context);
        init(context);


    }

    public BrandRecogTip(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public BrandRecogTip(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    private void init(Context context){
        addLayout(context, R.layout.view_car_rect);
        tvOrientation = findViewById(R.id.tv_orientation);

    }

    @Override
    void updateInfo(BefCarDetectInfo.BefBrandInfo info) {
        if (info == null)return;
        tvOrientation.setText(info.getBrandOcrString());

        Rect rect = new Rect((int)(info.getPoints()[1].getX()), (int)(info.getPoints()[1].getY()),(int)(info.getPoints()[3].getX()),(int)(info.getPoints()[3].getY()));
        getRectInScreenCord(rect);

        int top = rect.top - height;
        int left = rect.left;
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.getLayoutParams();
        marginLayoutParams.leftMargin = Math.max(left, 0);
        marginLayoutParams.topMargin = Math.max(top, 0);
        setLayoutParams(marginLayoutParams);

    }
}
