package com.example.byteplus_effects_plugin.effect.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.byteplus_effects_plugin.common.utils.DensityUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;

public class DownloadView extends View {

    private Context mContext;

    // 进度的取值范围：0～1
    public float mProgress =0;
    private Paint mPaint;
    private Bitmap mDownloadBitmap;

    //设置View默认的大小
    private float mProgressBarWidth;
    private float mProgressBarPadding;

    // 定义设置进度圆的默认半径
    private float mRadius;
    //测量后的实际view的大小
    private float mMeasureWidth;
    private float mMeasureHeight;
    private RectF mRectF = new RectF();

    //圆环的默认宽度
    private float mProgressBarHeight;
    private float mProgressBarContentWidth;

    //设置未加载进度的默认颜色
    private int mUnReachedBarColor;
    //设置已加载进度的默认颜色
    private int mReachedBarColor;

    private DownloadState mStatus = DownloadState.REMOTE;

    public enum DownloadState {
        REMOTE,
        DOWNLOADING,
        CACHED
    }

    public DownloadView(Context context) {
        super(context);
        mContext = context;
    }

    public DownloadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context, attrs);
    }

    public DownloadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public DownloadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs){

        mContext = context;
        setWillNotDraw(false);// 防止onDraw方法不执行

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DownloadView);
        mProgressBarWidth = a.getDimension(R.styleable.DownloadView_bar_width, DensityUtils.dp2px(mContext,12));
        mProgressBarPadding = a.getDimension(R.styleable.DownloadView_bar_width_padding, DensityUtils.dp2px(mContext,1));
        mProgressBarHeight = a.getDimension(R.styleable.DownloadView_bar_content_height,DensityUtils.dp2px(mContext,2));
        mProgressBarContentWidth = a.getDimension(R.styleable.DownloadView_bar_content_height,DensityUtils.dp2px(mContext,1.6f));
        mUnReachedBarColor = a.getColor(R.styleable.DownloadView_unreached_bar_color,context.getResources().getColor(R.color.unreached_bar_color));
        mReachedBarColor = a.getColor(R.styleable.DownloadView_reached_bar_color,context.getResources().getColor(R.color.colorWhite));
        a.recycle();

        // {zh} 抗锯齿画笔 {en} Anti-aliasing brush
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // {zh} 防止边缘锯齿 {en} Prevent jagged edges
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
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
        mRadius=(mMeasureWidth-2*mProgressBarPadding-mProgressBarHeight-getPaddingLeft()-getPaddingRight())/2;
        //绘制进度圆弧的外切矩形定义
        mRectF.set(-mRadius, -mRadius, mRadius, mRadius);

    }
    private float measureSize(int measureSpec) {
        float result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            //当specMode = EXACTLY时，精确值模式，即当我们在布局文件中为View指定了具体的大小
            result = specSize;
        }else {
            result =mProgressBarWidth;   //指定默认大小
            if (specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        switch (mStatus){
            case REMOTE:
                if (mDownloadBitmap == null) {
                    mDownloadBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_download);
                }
                canvas.drawBitmap(mDownloadBitmap, 0, 0, mPaint);

                break;
            case DOWNLOADING:
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
                mPaint.setStrokeWidth(mProgressBarContentWidth);
                //计算进度圆弧角度
                float sweepAngle = mProgress * 360;
                //绘制圆弧进度
                canvas.drawArc(mRectF, -90,
                        sweepAngle, false, mPaint);
                //绘制显示进行的颜色
                mPaint.setStyle(Paint.Style.FILL);
                canvas.restore();
                break;
            case CACHED:
                this.setVisibility(View.INVISIBLE);
                break;
            default:

        }


    }

    //更新进度
    public void setProgress(float progress) {
        if( progress <0 || progress > 1 ){
            return;
        }
        mProgress = progress;
        invalidate();
    }

    public void setState(DownloadState status) {
        mStatus = status;
        setVisibility(visibilityOfState(status));
        invalidate();
    }

    private void setProgressStatus (DownloadState status) {
        this.mStatus = status;
    }

    public float getProgressStatus() {
        return mProgress;
    }

    private int visibilityOfState(DownloadState state) {
        switch (state) {
            case REMOTE:
            case DOWNLOADING:
                return View.VISIBLE;
            case CACHED:
                return View.INVISIBLE;
        }
        throw new IllegalArgumentException();
    }
}
