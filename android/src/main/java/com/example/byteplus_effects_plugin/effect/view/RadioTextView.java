package com.example.byteplus_effects_plugin.effect.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.byteplus_effects_plugin.R;


public class RadioTextView extends FrameLayout {
    private View mImgDot;
    private TextView mTvItem;
    private boolean selected = false;

    @Override
    public boolean isSelected() {
        return selected;
    }

    public RadioTextView(Context context) {
        super(context);
        init(context);
    }

    public RadioTextView(Context context, @Nullable  AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttr(context, attrs);
    }

    public RadioTextView(Context context, @Nullable  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);

    }

    public RadioTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);

    }

    private void init(Context context){
        View  view = LayoutInflater.from(context).inflate(R.layout.layout_radio_text, this, true);
        mImgDot = view.findViewById(R.id.img_dot);
        mTvItem = view.findViewById(R.id.tv_item);
    }

    private void initAttr(Context context, AttributeSet attr) {
        TypedArray arr = context.obtainStyledAttributes(attr, R.styleable.RadioTextView);


        selected = arr.getBoolean(R.styleable.RadioTextView_selected,false);
        String text = arr.getString(R.styleable.RadioTextView_text);
        int color = arr.getColor(R.styleable.RadioTextView_color,0xffffff);
        GradientDrawable gradientDrawable = (GradientDrawable)mImgDot.getBackground();
        gradientDrawable.setColor(color);
        mTvItem.setText(text);
        setState(selected);
        arr.recycle();
    }

    public void setState(boolean flag){
        selected = flag;
        if (selected){
            mImgDot.setVisibility(View.VISIBLE);
            mTvItem.setTextColor(getResources().getColor(R.color.colorWhite));
        }else {
            mImgDot.setVisibility(View.INVISIBLE);
            mTvItem.setTextColor(getResources().getColor(R.color.transparent_white));

        }

    }
}
