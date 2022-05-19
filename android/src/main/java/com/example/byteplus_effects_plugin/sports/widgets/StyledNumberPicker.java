package com.example.byteplus_effects_plugin.sports.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.example.byteplus_effects_plugin.common.utils.ReflectUtils;
import com.example.byteplus_effects_plugin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2021/7/16 14:12
 */
public class StyledNumberPicker extends NumberPicker {
    public static final String SELECTION_DIVIDER = "mSelectionDivider";
    public static final String SELECTION_DIVIDER_HEIGHT = "mSelectionDividerHeight";
    public static final String SELECTION_INDICES = "mSelectorIndices";
    public static final String SELECTION_WHEEL_PAINT = "mSelectorWheelPaint";

    public static final int[] EMPTY_INDICES = new int[0];

    private volatile List<EditText> mStyledViews;
    private int mDividerHeight = 1;
    private int mTextColor = Color.BLACK;
    private int mTextSize = 16;
    private int mTextTranslateX = 0;

    public StyledNumberPicker(Context context) {
        super(context);
    }

    public StyledNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
    }

    public StyledNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }

    public StyledNumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.StyledNumberPicker);
        Drawable divider = ta.getDrawable(R.styleable.StyledNumberPicker_styled_number_picker_divider);
        mDividerHeight = ta.getDimensionPixelSize(R.styleable.StyledNumberPicker_styled_number_picker_divider_height, mDividerHeight);
        mTextColor = ta.getColor(R.styleable.StyledNumberPicker_styled_number_picker_text_color, mTextColor);
        mTextSize = ta.getDimensionPixelSize(R.styleable.StyledNumberPicker_styled_number_picker_text_size, mTextSize);
        mTextTranslateX = ta.getDimensionPixelSize(R.styleable.StyledNumberPicker_styled_number_picker_text_translateX, mTextTranslateX);
        ta.recycle();

        try {
            ReflectUtils.setObjectValue(NumberPicker.class, this, SELECTION_DIVIDER, divider);
            ReflectUtils.setIntValue(NumberPicker.class, this, SELECTION_DIVIDER_HEIGHT, mDividerHeight);
            Object paint = ReflectUtils.getObjectValue(NumberPicker.class, this, SELECTION_WHEEL_PAINT);
            if (paint instanceof Paint) {
                ((Paint) paint).setColor(mTextColor);
                ((Paint) paint).setTextSize(mTextSize);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (EditText text : mStyledViews) {
            text.setTextColor(mTextColor);
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            text.setEnabled(false);
            text.setTranslationX(mTextTranslateX);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof EditText) {
            if (mStyledViews == null) {
                mStyledViews = new ArrayList<>();
            }
            mStyledViews.add((EditText) child);
        }
        super.addView(child, index, params);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        if (child instanceof EditText) {
            if (mStyledViews == null) {
                mStyledViews = new ArrayList<>();
            }
            mStyledViews.add((EditText) child);
        }
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            // draw text
            canvas.save();
            canvas.translate(mTextTranslateX, 0);
            Object divider = ReflectUtils.getObjectValue(NumberPicker.class, this, SELECTION_DIVIDER);
            ReflectUtils.setObjectValue(NumberPicker.class, this, SELECTION_DIVIDER, null);
            super.onDraw(canvas);
            ReflectUtils.setObjectValue(NumberPicker.class, this, SELECTION_DIVIDER, divider);
            canvas.restore();

            // draw divider
            Object indices = ReflectUtils.getObjectValue(NumberPicker.class, this, SELECTION_INDICES);
            ReflectUtils.setObjectValue(NumberPicker.class, this, SELECTION_INDICES, EMPTY_INDICES);
            super.onDraw(canvas);
            ReflectUtils.setObjectValue(NumberPicker.class, this, SELECTION_INDICES, indices);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            super.onDraw(canvas);
        }
    }
}
