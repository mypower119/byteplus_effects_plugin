package com.example.byteplus_effects_plugin.algorithm.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefDistanceInfo;

import java.text.DecimalFormat;

public class HumanDistanceTip extends ResultTip<BefDistanceInfo.BefDistance> {
    private static final String TAG = "HandInfoTip";
    private TextView tvDist;

    private int height = getResources().getDimensionPixelSize(R.dimen.distance_info_height);
    private DecimalFormat df = new DecimalFormat("0.00");

    public HumanDistanceTip(@NonNull Context context) {
        super(context);
        addLayout(context, R.layout.view_distance_info);
        tvDist = findViewById(R.id.tv_dist);
    }

    public HumanDistanceTip(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HumanDistanceTip(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void updateInfo(BefDistanceInfo.BefDistance item) {
        if (null == item) return;
        if (null == tvDist) return;

        tvDist.setText(df.format(item.getDis()));

        Rect rect = item.getFaceRect().toRect();
        getRectInScreenCord(rect);
        int top = rect.top;
        int left = rect.left;
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.getLayoutParams();
        marginLayoutParams.leftMargin = left;
        marginLayoutParams.topMargin = top > height ? (top - height) : 0;
        setLayoutParams(marginLayoutParams);

    }


}
