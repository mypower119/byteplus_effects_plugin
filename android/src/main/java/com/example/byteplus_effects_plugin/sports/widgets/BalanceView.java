package com.example.byteplus_effects_plugin.sports.widgets;

import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.byteplus_effects_plugin.R;

/**
 * Created on 2021/12/6 14:35
 */
public class BalanceView extends View {
    public static final int DEFAULT_BAR_LENGTH = 600;
    public static final int DEFAULT_BAR_WIDTH = 100;
    public static final int DEFAULT_BAR_STROKE_WIDTH = 8;
    public static final int DEFAULT_CIRCLE_RADIUS = 105;
    public static final int DEFAULT_DASH_CIRCLE_WIDTH = 6;
    public static final int DEFAULT_STROKE_CIRCLE_WIDTH = 8;
    public static final int DEFAULT_BALANCED_COLOR = -15309569;
    public static final int DEFAULT_UNBALANCED_COLOR = -354815;
    public static final int DEFAULT_BALANCE_DRAWABLE_RADIUS = 83;
    public static final float DEFAULT_BALANCE_WAIT_TIME = 1000.f;
    public static final float DEFAULT_BALANCE_THRESHOLD = 75.f;
    public static final float DEFAULT_BALANCE_THRESHOLD_ERROR = 5.f;
    public static final float DEFAULT_BALANCE_THRESHOLD_FROM = 0.f;
    public static final float DEFAULT_BALANCE_THRESHOLD_TO = 150.f;
    public static final boolean DEFAULT_PLAY_FEEDBACK = true;


    public enum State {
        UNBALANCED,
        BALANCED,
        OK,
    }

    private int mBarLength;
    private int mBarWidth;
    private int mBarStrokeWidth;

    private int mCircleRadius;
    private int mDashCircleStrokeWidth;
    private int mStrokeCircleStrokeWidth;

    @ColorInt
    private int mBalancedColor;
    @ColorInt
    private int mUnbalancedColor;

    private int mBalanceDrawableRadius;
    private Drawable mBalancedDrawable;
    private Drawable mUnbalancedDrawable;
    private Drawable mOkDrawable;

    private float mBalanceWaitTime;
    private float mBalanceThreshold;
    private float mBalanceThresholdError;
    private float mBalanceThresholdFrom;
    private float mBalanceThresholdTo;

    private boolean mPlayFeedback;

    private Paint mBarPainter;
    private Paint mDashCirclePainter;
    private Paint mStrokeCirclePainter;
    private float mCirclePosition = 0.f;
    private long mStartCountDownTime;

    private OnBalanceListener mListener;

    private State mState = State.UNBALANCED;

    public BalanceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }

    public BalanceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        init();
    }

    public BalanceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(attrs);
        init();
    }

    private void initAttr(AttributeSet attr) {
        TypedArray ta = getContext().obtainStyledAttributes(attr, R.styleable.BalanceView);
        mBarLength = ta.getDimensionPixelSize(R.styleable.BalanceView_balance_barLength, DEFAULT_BAR_LENGTH);
        mBarWidth = ta.getDimensionPixelSize(R.styleable.BalanceView_balance_barWidth, DEFAULT_BAR_WIDTH);
        mBarStrokeWidth = ta.getDimensionPixelSize(R.styleable.BalanceView_balance_barStrokeWidth, DEFAULT_BAR_STROKE_WIDTH);
        mCircleRadius = ta.getDimensionPixelSize(R.styleable.BalanceView_balance_circleRadius, DEFAULT_CIRCLE_RADIUS);
        mDashCircleStrokeWidth = ta.getDimensionPixelSize(R.styleable.BalanceView_balance_dashCircleStrokeWidth, DEFAULT_DASH_CIRCLE_WIDTH);
        mStrokeCircleStrokeWidth = ta.getDimensionPixelSize(R.styleable.BalanceView_balance_strokeCircleStrokeWidth, DEFAULT_STROKE_CIRCLE_WIDTH);
        mBalancedColor = ta.getColor(R.styleable.BalanceView_balance_balancedColor, DEFAULT_BALANCED_COLOR);
        mUnbalancedColor = ta.getColor(R.styleable.BalanceView_balance_unbalancedColor, DEFAULT_UNBALANCED_COLOR);
        mBalanceDrawableRadius = ta.getDimensionPixelSize(R.styleable.BalanceView_balance_balanceDrawableRadius, DEFAULT_BALANCE_DRAWABLE_RADIUS);
        mBalancedDrawable = ta.getDrawable(R.styleable.BalanceView_balance_balancedDrawable);
        mUnbalancedDrawable = ta.getDrawable(R.styleable.BalanceView_balance_unbalancedDrawable);
        mOkDrawable = ta.getDrawable(R.styleable.BalanceView_balance_okDrawable);
        mBalanceWaitTime = ta.getFloat(R.styleable.BalanceView_balance_balanceWaitTime, DEFAULT_BALANCE_WAIT_TIME);
        mBalanceThreshold = ta.getFloat(R.styleable.BalanceView_balance_balanceThreshold, DEFAULT_BALANCE_THRESHOLD);
        mBalanceThresholdError = ta.getFloat(R.styleable.BalanceView_balance_balanceThresholdError, DEFAULT_BALANCE_THRESHOLD_ERROR);
        mBalanceThresholdFrom = ta.getFloat(R.styleable.BalanceView_balance_balanceThresholdFrom, DEFAULT_BALANCE_THRESHOLD_FROM);
        mBalanceThresholdTo = ta.getFloat(R.styleable.BalanceView_balance_balanceThresholdTo, DEFAULT_BALANCE_THRESHOLD_TO);
        mPlayFeedback = ta.getBoolean(R.styleable.BalanceView_balance_playFeedback, DEFAULT_PLAY_FEEDBACK);
        ta.recycle();
    }

    private void init() {
        mBarPainter = new Paint();
        mBarPainter.setAntiAlias(true);
        mBarPainter.setStyle(Paint.Style.STROKE);

        mDashCirclePainter = new Paint();
        mDashCirclePainter.setAntiAlias(true);
        mDashCirclePainter.setStyle(Paint.Style.STROKE);
        mDashCirclePainter.setPathEffect(new DashPathEffect(new float[]{10, 10}, 1));

        mStrokeCirclePainter = new Paint();
        mStrokeCirclePainter.setAntiAlias(true);
        mStrokeCirclePainter.setStyle(Paint.Style.STROKE);
        mStrokeCirclePainter.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setListener(OnBalanceListener listener) {
        mListener = listener;
    }

    public void setTheta(float theta) {
        if (mState == State.OK) {
            return;
        }

        mCirclePosition = getPositionFromTheta(theta);

        if (Math.abs(theta - mBalanceThreshold) <= mBalanceThresholdError) {
            changeToState(State.BALANCED);
        } else {
            changeToState(State.UNBALANCED);
        }

        invalidate();
    }

    private void changeToState(State state) {
        if (state == mState) {
            return;
        }

        if (mListener != null) {
            mListener.onBalanceStateChanged(state);
        }

        mState = state;

        if (state == State.BALANCED) {
            playFeedback();
            mStartCountDownTime = System.currentTimeMillis();
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBar(canvas);
        drawCircle(canvas);
        drawCircleImage(canvas);
    }

    private void drawBar(Canvas canvas) {
        int color = mState == State.UNBALANCED ? mUnbalancedColor : mBalancedColor;
        mBarPainter.setColor(color);
        mBarPainter.setStrokeWidth(mBarStrokeWidth);

        float left = (getWidth() - mBarWidth) / 2.f;
        float top = (getHeight() - mBarLength) / 2.f;
        canvas.drawRoundRect(left, top, left + mBarWidth, top + mBarLength, mBarWidth / 2.f, mBarWidth / 2.f, mBarPainter);
    }

    private void drawCircle(Canvas canvas) {
        int color = mState == State.UNBALANCED ? mUnbalancedColor : mBalancedColor;
        mDashCirclePainter.setColor(color);
        mDashCirclePainter.setStrokeWidth(mDashCircleStrokeWidth);

        canvas.drawCircle(getWidth() / 2.f, getHeight() / 2.f, mCircleRadius, mDashCirclePainter);

        if (mState == State.BALANCED) {
            mStrokeCirclePainter.setColor(color);
            mStrokeCirclePainter.setStrokeWidth(mStrokeCircleStrokeWidth);

            float left = (getWidth()) / 2.f - mCircleRadius;
            float top = (getHeight()) / 2.f - mCircleRadius;
            float progress = (System.currentTimeMillis() - mStartCountDownTime) / mBalanceWaitTime;
            canvas.drawArc(left, top, left + mCircleRadius * 2, top + mCircleRadius * 2, -90, progress * 360, false, mStrokeCirclePainter);

            if (progress >= 1.f) {
                changeToState(State.OK);
            } else {
                invalidate();
            }
        }
    }

    private void drawCircleImage(Canvas canvas) {
        Drawable circleDrawable = null;
        switch (mState) {
            case UNBALANCED:
                circleDrawable = mUnbalancedDrawable;
                break;
            case BALANCED:
                circleDrawable = mBalancedDrawable;
                break;
            case OK:
                circleDrawable = mOkDrawable;
                break;
        }
        assert circleDrawable != null;

        float circlePosition = mState == State.OK ? 0.5f : mCirclePosition;
        boolean reverse = circlePosition > 0.5f;

        int top = (int) ((getHeight() - mBarLength) / 2.f - mBalanceDrawableRadius + mBarLength * circlePosition);
        int left = (int) ((getWidth()) / 2.f - mBalanceDrawableRadius);
        canvas.save();
        canvas.translate(left + mBalanceDrawableRadius, top + mBalanceDrawableRadius);
        canvas.scale(1, reverse ? -1 : 1);
        circleDrawable.setBounds(-mBalanceDrawableRadius, -mBalanceDrawableRadius, mBalanceDrawableRadius, mBalanceDrawableRadius);
        circleDrawable.draw(canvas);
        canvas.restore();
    }

    private float getPositionFromTheta(float theta) {
        if (theta < mBalanceThresholdFrom) {
            theta = mBalanceThresholdFrom;
        } else if (theta > mBalanceThresholdTo) {
            theta = mBalanceThresholdTo;
        }

        return (theta - mBalanceThresholdFrom) / (mBalanceThresholdTo - mBalanceThresholdFrom);
    }

    private void playFeedback() {
        if (!mPlayFeedback) return;

        Vibrator vibrator = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    public interface OnBalanceListener {
        void onBalanceStateChanged(State state);
    }
}
