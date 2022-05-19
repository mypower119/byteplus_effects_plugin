package com.example.byteplus_effects_plugin.algorithm.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefCarDetectInfo;

public class CarRectInfoTip extends ResultTip<BefCarDetectInfo.BefCarRect> {
    private TextView tvOrientation;
    private int height = getResources().getDimensionPixelSize(R.dimen.car_orientation_info_height);
    private Context mContext;

    static int[] OrientationMap= {
            R.string.car_direction_zhengqian,
            R.string.car_direction_zhengzhou,
            R.string.car_direction_zuoce,
            R.string.car_direction_youce,
            R.string.car_direction_zuoceqian,
            R.string.car_direction_youceqian,
            R.string.car_direction_zuocehou,
            R.string.car_direction_youcehou
    };

    public CarRectInfoTip(@NonNull Context context) {
        super(context);
        init(context);

    }

    public CarRectInfoTip(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public CarRectInfoTip(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    private void init(Context context){
        addLayout(context, R.layout.view_car_rect);
        tvOrientation = findViewById(R.id.tv_orientation);
        mContext = context;
    }

    @Override
    void updateInfo(BefCarDetectInfo.BefCarRect info) {
        if (info == null)return;
        if (info.getOrientation() < 0 || info.getOrientation() > 7) return;
        tvOrientation.setText(mContext.getText(OrientationMap[info.getOrientation()]));

        Rect rect = new Rect(info.getLeft(), info.getTop(),info.getRight(),info.getBottom());
        getRectInScreenCord(rect);
        int top = rect.top - height;
        int left = rect.left;
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.getLayoutParams();
        marginLayoutParams.leftMargin = Math.max(left, 0);
        marginLayoutParams.topMargin = Math.max(top, 0);
        setLayoutParams(marginLayoutParams);

    }
}
