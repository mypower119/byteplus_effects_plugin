package com.example.byteplus_effects_plugin.sports.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.example.byteplus_effects_plugin.R;

/**
 * Created on 2021/7/15 19:07
 */
public class AspectRatioLayout extends FrameLayout {
    public static final float DEFAULT_RATIO = 1.f;
    public static final int LENGTH_WIDTH = 1;
    public static final int LENGTH_HEIGHT = 2;

    private float mRatio = DEFAULT_RATIO;
    private int mAspectLength = LENGTH_WIDTH;
    private int mCornerRadius = 0;

    public AspectRatioLayout(@NonNull Context context) {
        super(context);
    }

    public AspectRatioLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
    }

    public AspectRatioLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.AspectRatioLayout);
        mRatio = ta.getFloat(ta.getIndex(R.styleable.AspectRatioLayout_aspect_ratio), 1.f);
        mAspectLength = ta.getInt(ta.getIndex(R.styleable.AspectRatioLayout_aspect_ratio_length), LENGTH_WIDTH);
        mCornerRadius = ta.getDimensionPixelSize(ta.getIndex(R.styleable.AspectRatioLayout_aspect_ratio_corner_radius), 0);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        canvas.save();
        Path path = new Path();
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        path.addRoundRect(rectF, mCornerRadius, mCornerRadius, Path.Direction.CCW);
        canvas.clipPath(path);
        boolean ret = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return ret;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (mAspectLength) {
            case LENGTH_WIDTH:
                if (widthMode == MeasureSpec.EXACTLY) {
                    heightMode = MeasureSpec.EXACTLY;
                    heightSize = (int) (widthSize *1.f / mRatio);
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
                }
                break;
            case LENGTH_HEIGHT:
                if (heightMode == MeasureSpec.EXACTLY) {
                    widthMode = MeasureSpec.EXACTLY;
                    widthSize = (int) (heightSize * mRatio);
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
                }
                break;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
