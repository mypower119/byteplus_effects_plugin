package com.example.byteplus_effects_plugin.sports.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.byteplus_effects_plugin.common.utils.DensityUtils;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.sports.utils.SVGPathParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 2021/7/18 14:06
 */
public class SportMaskView extends View {
    private final SVGPathParser mSvgParser = new SVGPathParser();

    private int mSVGRes;
    private int mStrokeColor;
    private int mStrokeWidth;
    private int mBackgroundColor;
    private boolean mFlipHorizontal = false;

    private SVGPathParser.SVGItem mSvgItem;
    private Paint mPaint;
    private Matrix mTransform;

    private boolean mShowCorner = true;
    private Bitmap mCornerLeftTop;
    private Bitmap mCornerLeftBottom;
    private Bitmap mCornerRightTop;
    private Bitmap mCornerRightBottom;

    public SportMaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public SportMaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    public SportMaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SportMaskView);
        mSVGRes = ta.getResourceId(R.styleable.SportMaskView_sport_mask_svg, 0);
        mStrokeColor = ta.getColor(R.styleable.SportMaskView_sport_mask_strokeColor, Color.BLACK);
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.SportMaskView_sport_mask_strokeWidth, 4);
        mBackgroundColor = ta.getColor(R.styleable.SportMaskView_sport_mask_backgroundColor, Color.TRANSPARENT);
        ta.recycle();

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mCornerLeftTop = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sport_mask_left_top);
        mCornerLeftBottom = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sport_mask_left_bottom);
        mCornerRightTop = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sport_mask_right_top);
        mCornerRightBottom = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sport_mask_right_bottom);
    }

    private void updatePath() {
        if (mSVGRes == 0) {
            return;
        }
        InputStream is = getResources().openRawResource(mSVGRes);
        try {
            mSvgItem = mSvgParser.parseAVG(is);

            calTransform();
            for (SVGPathParser.SVGItem.PathItem item : mSvgItem.paths) {
                item.path.transform(mTransform);
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            mSvgItem = null;
        }
    }

    public void setStrokeColor(int color) {
        mStrokeColor = color;
        postInvalidate();
    }

    public void setSvgMask(int svg) {
        mSVGRes = svg;
        updatePath();
    }

    public void setShowCorner(boolean showCorner) {
        mShowCorner = showCorner;
        postInvalidate();
    }

    public void setFlipHorizontal(boolean flipHorizontal) {
        mFlipHorizontal = flipHorizontal;
        updatePath();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (mSvgItem == null || mSvgItem.paths == null) {
            return;
        }

        canvas.save();
        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (SVGPathParser.SVGItem.PathItem pathItem : mSvgItem.paths) {
            canvas.clipPath(pathItem.path, Region.Op.DIFFERENCE);
        }
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        canvas.restore();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        for (SVGPathParser.SVGItem.PathItem pathItem : mSvgItem.paths) {
            if (mStrokeColor != 0) {
                mPaint.setColor(mStrokeColor);
            } else {
                mPaint.setColor(pathItem.strokeColor);
            }
            if (mStrokeWidth > 0) {
                mPaint.setStrokeWidth(mStrokeWidth);
            } else {
                mPaint.setStrokeWidth(pathItem.strokeWidth);
            }
            canvas.drawPath(pathItem.path, mPaint);
        }

        if (!mShowCorner) {
            return;
        }
        int cornerPaddingStart = (int) DensityUtils.dp2px(getContext(), 24.f);
        int cornerPaddingEnd = cornerPaddingStart;
        int cornerPaddingTop = (int) DensityUtils.dp2px(getContext(), 26.f);
        int cornerPaddingBottom = (int) DensityUtils.dp2px(getContext(), 72.f);
        int cornerLength = (int) DensityUtils.dp2px(getContext(), 18.f);
        canvas.drawBitmap(mCornerLeftTop,
                new Rect(0, 0, mCornerLeftTop.getWidth(), mCornerLeftTop.getHeight()),
                new Rect(cornerPaddingStart, cornerPaddingTop, cornerLength + cornerPaddingStart, cornerLength + cornerPaddingTop), mPaint);
        canvas.drawBitmap(mCornerLeftBottom,
                new Rect(0, 0, mCornerLeftBottom.getWidth(), mCornerLeftBottom.getHeight()),
                new Rect(cornerPaddingStart, getHeight() - cornerLength - cornerPaddingBottom, cornerLength + cornerPaddingStart, getHeight() - cornerPaddingBottom), mPaint);
        canvas.drawBitmap(mCornerRightTop,
                new Rect(0, 0, mCornerRightTop.getWidth(), mCornerRightTop.getHeight()),
                new Rect(getWidth() - cornerLength - cornerPaddingEnd, cornerPaddingTop, getWidth() - cornerPaddingEnd, cornerPaddingTop + cornerLength), mPaint);
        canvas.drawBitmap(mCornerRightBottom,
                new Rect(0, 0, mCornerRightBottom.getWidth(), mCornerRightBottom.getHeight()),
                new Rect(getWidth() - cornerLength - cornerPaddingEnd, getHeight() - cornerLength - cornerPaddingBottom, getWidth() - cornerPaddingEnd, getHeight() - cornerPaddingBottom), mPaint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePath();
    }

    private void calTransform() {
        if (mTransform == null) {
            mTransform = new Matrix();
        } else {
            mTransform.reset();
        }

        int ww = getWidth();
        int hh = getHeight();
        int w = mSvgItem.width;
        int h = mSvgItem.height;

        mTransform.setRectToRect(new RectF(0, 0, w, h), new RectF(
                getPaddingStart(),
                getPaddingTop(),
                ww - getPaddingEnd(),
                hh - getPaddingBottom()),
                Matrix.ScaleToFit.CENTER);
        if (mFlipHorizontal) {
            mTransform.postScale(-1, 1, (ww - getPaddingEnd() - getPaddingStart()) / 2 + getPaddingStart(),
                    (hh - getPaddingBottom() - getPaddingTop()) / 2 + getPaddingTop());
        }
    }
}
