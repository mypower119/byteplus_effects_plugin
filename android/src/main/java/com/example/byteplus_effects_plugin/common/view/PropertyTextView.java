package com.example.byteplus_effects_plugin.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.byteplus_effects_plugin.R;


/**
 * Created on 2020/9/2 10:33
 */
public class PropertyTextView extends FrameLayout {
    private TextView tvTitle;
    private TextView tvValue;
    private String mTitle;
    private String mValue;

    private TextPaint mTextPainter;
    private int mPresetMinWidth;

    public PropertyTextView(@NonNull Context context) {
        this(context, null);
    }

    public PropertyTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PropertyTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context)
                .inflate(R.layout.view_property_text, this, true);
        tvTitle = findViewById(R.id.tv_property_text);
        tvValue = findViewById(R.id.tv_property_value);

        mTextPainter = new TextPaint();
        mTextPainter.setTextSize(tvTitle.getTextSize());
        mPresetMinWidth = getMinimumWidth();

        if (attrs == null) return;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PropertyTextView);
        setTitle(ta.getString(R.styleable.PropertyTextView_propertyTitle));
        setValue(ta.getString(R.styleable.PropertyTextView_propertyValue));
        ta.recycle();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
        tvTitle.setText(title);
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        this.mValue = value;
        tvValue.setText(value);
    }

    public void setTitleAndSize(String title, float size) {
        this.mTitle = title;
        tvTitle.setText(title);
        tvTitle.setTextSize(size);
    }

    public void setValueAndSize(String value, float size) {
        this.mValue = value;
        tvValue.setText(value);
        tvValue.setTextSize(size);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float titleWidth = mTitle == null ? 0 : Layout.getDesiredWidth(mTitle, mTextPainter);
        float valueWidth = mValue == null ? 0 : Layout.getDesiredWidth(mValue, mTextPainter);
        float intervalWidth = getResources().getDimensionPixelSize(R.dimen.min_property_interval);
        setMeasuredDimension(Math.max((int)(titleWidth + valueWidth + intervalWidth), mPresetMinWidth), getMeasuredHeight());
    }
}
