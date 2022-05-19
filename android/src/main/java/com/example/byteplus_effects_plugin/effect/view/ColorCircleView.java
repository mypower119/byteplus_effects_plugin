package com.example.byteplus_effects_plugin.effect.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.model.ColorItem;

public class ColorCircleView extends View {

    private final String TAG = "ColorCircleView";

    private int mViewCenterX;   // {zh} view宽的中心点 {en} Wide center point of view
    private int mViewCenterY;   // {zh} view高的中心点 {en} View high center point

    private float mInnerRadius; // {zh} 最里面白色圆的半径 {en} The radius of the innermost white circle
    private float mInnerPadding; // {zh} 最里面白色圆的半径 {en} The radius of the innermost white circle
    private float mRingWidth; // {zh} 圆环的宽度 {en} Width of the ring
    private ColorItem mInnerColor;    // {zh} 内圆的颜色 {en} Color of inner circle
    private int mRingColor;    // {zh} 默认圆环的颜色 {en} Default ring color
    private Paint mPaint;

    private RectF mRectF; // {zh} 圆环的矩形区域 {en} Rectangular area of the ring
    private boolean mSelected = false;

    public ColorCircleView(Context context) {
        super(context);
    }

    public ColorCircleView(Context context, @Nullable  AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public ColorCircleView(Context context, @Nullable  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);

    }

    public ColorCircleView(Context context, @Nullable  AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);

    }


    public void setmInnerColor(ColorItem color){
        mInnerColor = color;
        invalidate();
    }

    public void setSelected(boolean flag){
        mSelected = flag;
        invalidate();


    }


    private void init(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorCircleView);
        mInnerRadius = a.getDimension(R.styleable.ColorCircleView_circle_radius, 18);
        // {zh} 圆环的间距 {en} Spacing of rings
        mInnerPadding = a.getDimension(R.styleable.ColorCircleView_ring_padding, 4);
        // {zh} 圆环宽度 {en} Ring width
        mRingWidth = a.getDimension(R.styleable.ColorCircleView_ring_width, 1.5f);

        // {zh} 圆环的默认颜色(圆环占据的是里面的圆的空间) {en} The default color of the ring (the ring occupies the space of the circle inside)
        mRingColor = a.getColor(R.styleable.ColorCircleView_ring_color, context.getResources().getColor(R.color.colorWhite));

        a.recycle();

        // {zh} 抗锯齿画笔 {en} Anti-aliasing brush
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // {zh} 防止边缘锯齿 {en} Prevent jagged edges
        mPaint.setAntiAlias(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=(int)(mInnerRadius+mInnerPadding+mRingWidth)*2;
        int height=width;
        // {zh} 设置测量值的尺寸,传宽和高 {en} Set the size, transmission width and height of the measured value
        setMeasuredDimension(width,height);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // {zh} view的宽和高,相对于父布局(用于确定圆心) {en} The width and height of the view, relative to the parent layout (used to determine the center of the circle)
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        mViewCenterX = viewWidth / 2;
        mViewCenterY = viewHeight / 2;
        // {zh} 画矩形 {en} Draw rectangle
        mRectF = new RectF((mViewCenterX - mInnerRadius - mRingWidth/2 - mInnerPadding), (mViewCenterY - mInnerRadius - mRingWidth/2- mInnerPadding) , (mViewCenterX + mInnerRadius + mRingWidth/2+ mInnerPadding), (mViewCenterY + mInnerRadius + mRingWidth/2+ mInnerPadding));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mInnerColor.toInt());
        if (mSelected){
            canvas.drawCircle(mViewCenterX, mViewCenterY, mInnerRadius, mPaint);
            // {zh} 画默认圆环 {en} Draw default ring
            drawNormalRing(canvas);
        }else {
            canvas.drawCircle(mViewCenterX, mViewCenterY, mInnerRadius+mInnerPadding+mRingWidth, mPaint);

        }

    }



    /** {zh} 
     * 画默认圆环
     *
     * @param canvas
     */
    /** {en} 
     * Draw default ring
     *
     * @param canvas
     */
    private void drawNormalRing(Canvas canvas) {
        Paint ringNormalPaint = new Paint(mPaint);
        ringNormalPaint.setStyle(Paint.Style.STROKE);
        ringNormalPaint.setStrokeWidth(mRingWidth);
        ringNormalPaint.setColor(mRingColor);// {zh} 圆环默认颜色为白色 {en} The default color of the ring is white
        canvas.drawArc(mRectF, 360, 360, false, ringNormalPaint);
    }


}
