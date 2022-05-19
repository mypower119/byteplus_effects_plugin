package com.example.byteplus_effects_plugin.common.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.common.utils.DensityUtils;

public class SelectViewHolder extends RecyclerView.ViewHolder {

    private int drawableSelected = R.drawable.bg_item_focused;
    private int drawableUnselected = R.drawable.bg_item_unselect_selector;


    private int colorOn;
    private int colorOff;

    private LinearLayout llContent;
    private LinearLayout llBackground;
    private ImageView iv;
    private TextView tvTitle;
    private View vPoint;

    private boolean isOn = false;
    private boolean isPointOn = false;

    public SelectViewHolder(View itemView) {
        super(itemView);

        llContent = itemView.findViewById(R.id.ll_content);
        llBackground = itemView.findViewById(R.id.ll_select_background);
        iv = itemView.findViewById(R.id.iv_select_options);
        tvTitle = itemView.findViewById(R.id.tv_title_face_options);
        vPoint = itemView.findViewById(R.id.v_face_options);

        colorOn = ActivityCompat.getColor(itemView.getContext(), R.color.colorWhite);
        colorOff = ActivityCompat.getColor(itemView.getContext(), R.color.colorGrey);
    }

    public void setIcon(int iconResource) {
//        iv.setImageResource(iconResource);
        Glide.with(iv).load(iconResource).apply(RequestOptions.bitmapTransform(new RoundedCorners((int) DensityUtils.dp2px(itemView.getContext(), 2)))).into(iv);
    }

    public void setIcon(String iconUrl) {
        Glide.with(iv).load(iconUrl).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                iv.setBackgroundResource(0);
                iv.setImageDrawable(resource);
            }
        });
    }

    public void setTitle(String title) {
        if (title.isEmpty()) {
            tvTitle.setVisibility(GONE);
        } else {
            tvTitle.setVisibility(VISIBLE);
            tvTitle.setText(title);
        }
    }

    public void change(boolean on) {
        if (on) {
            on();
        } else {
            off();
        }
    }

    public void on() {
        isOn = true;
        tvTitle.setTextColor(colorOn);
        llBackground.setBackgroundResource(drawableSelected);
    }

    public void off() {
        isOn = false;
        tvTitle.setTextColor(colorOff);
        llBackground.setBackgroundResource(drawableUnselected);
    }

    public boolean isOn() {
        return isOn;
    }

    public void setMarqueue(boolean flag){
        if (tvTitle instanceof MarqueeTextView){
            ((MarqueeTextView) tvTitle).setMarqueue(flag);
        }
    }

    public void pointChange(boolean on) {
        isPointOn = on;
        if (on) {
            //   {zh} 风格妆中会修改drawable缓存的填充色，此处使用单独的固定颜色的drawable       {en} Style makeup will modify the fill color of the drawable cache, here use a separate fixed color drawable
            vPoint.setBackgroundResource(R.drawable.dot_point_blue);
        } else {
            vPoint.setBackgroundResource(0);
        }
    }

    public boolean isPointOn() {
        return isPointOn;
    }

}
