package com.example.byteplus_effects_plugin.effect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;

import com.example.byteplus_effects_plugin.common.utils.DensityUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;

public class RemoteResourceUndownloadView extends View {

    private Context mContext;

    // 进度的取值范围：0～1
    public float progress=0;
    private Paint mPaint = new Paint();

    //设置View默认的大小
    private float mDefaultWidth = DensityUtils.dp2px(mContext,10);
    private float mDefaultPadding = DensityUtils.dp2px(mContext,0);
    // 定义设置进度圆的默认半径
    private float mRadius = mDefaultWidth/2;
    //测量后的实际view的大小
    private float mMeasureWidth;
    private float mMeasureHeight;
    private RectF mRectF;

    //圆环的默认宽度
    private float mProgressBarHeight = DensityUtils.dp2px(mContext,5f);
    private float mProgressBarContentHeight = DensityUtils.dp2px(mContext,3.2f);

    //设置未加载进度的默认颜色
    private int mUnReachedBarColor = 0xffc4c4c4;
    //设置已加载进度的默认颜色
    private int mReachedBarColor = 0xffffffff;

    public RemoteResourceUndownloadView(Context context) {
        super(context);
        mContext = context;
        //初始化画笔风格，图形参数，如圆圈的颜色，绘制的文字等
        initView();
    }

    private void initView() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtils.d("progress: "+w + ", h: " +h+", oldw: "+oldw+", oldh: "+ oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量计算
        mMeasureWidth = measureSize(widthMeasureSpec);
        mMeasureHeight = measureSize(heightMeasureSpec);
        //重新测量
        setMeasuredDimension((int)mMeasureWidth, (int)mMeasureHeight);
        //绘制圆环的半径
        mRadius=(mMeasureWidth -mDefaultPadding*2-mProgressBarHeight-getPaddingLeft()-getPaddingRight())/2;
        //绘制进度圆弧的外切矩形定义
        mRectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);

    }
    private float measureSize(int measureSpec) {
        float result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            //当specMode = EXACTLY时，精确值模式，即当我们在布局文件中为View指定了具体的大小
            result = specSize;
        }else {
            result = mDefaultWidth;   //指定默认大小
            if (specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();

        //将画布移动到中心 view中心为原点(0,0)
        canvas.translate(mMeasureWidth/2,mMeasureHeight/2);
        mPaint.setStyle(Paint.Style.STROKE);
        //绘制未加载的进度，也就是绘制环背景
        mPaint.setColor(mUnReachedBarColor);
        mPaint.setStrokeWidth(mProgressBarHeight);
        //点(0,0)为原心
        canvas.drawCircle(0, 0, mRadius, mPaint);

        //绘制已加载的圆环进度
        mPaint.setColor(mReachedBarColor);
        mPaint.setStrokeWidth(mProgressBarContentHeight);
        //计算进度圆弧角度
        float sweepAngle = progress * 360;
        //绘制圆弧进度
        canvas.drawArc(mRectF, 0,
                sweepAngle, false, mPaint);
        //绘制显示进行的颜色
        mPaint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }


    //将设置的db转为屏幕像素
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    //更新进度
    public void setProgress(int number) {
        if (number>100){
            number=100;
        }
        if (number<0){
            number=0;
        }
        progress=number;
        invalidate();
    }

}
