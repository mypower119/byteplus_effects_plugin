package com.example.byteplus_effects_plugin.effect.gesture;

import android.content.Context;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.example.byteplus_effects_plugin.common.utils.PreviewSizeManager;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;


/**
 * {zh}
 * Created on 2/7/21 3:13 PM
 *
 * @brief 手势检测类
 * @details 内部整合了多种手势检测（单击、长按、缩放、旋转、触摸等），提供统一输出口。
 */

/**
 * {en}
 * Created on 2/7/21 3:13 PM
 *
 * @brief The gesture detection class
 * @details integrates a variety of gesture detection (click, long press, zoom, rotation, touch, etc.) to provide a unified output port.
 */

public class GestureManager implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        ScaleGestureDetector.OnScaleGestureListener,
        RotateGestureDetector.OnRotateGestureListener {
    private final OnTouchListener mListener;

    private final GestureDetector mGesture;
    private final ScaleGestureDetector mScaleGesture;
    private final RotateGestureDetector mRotateGesture;
    private boolean mScrollFlag = false;
    private PointF mScrollLastPos = new PointF(-2, -2);
    private float mPreScaleFactor = 1f;
    private float mAccScaleFactor = 1f;

    private final PointerInfo mPointerInfo = new PointerInfo();

    public GestureManager(Context context, OnTouchListener listener) {
        mListener = listener;

        mGesture = new GestureDetector(context, this);
        mScaleGesture = new ScaleGestureDetector(context, this);
        mRotateGesture = new RotateGestureDetector(context, this);
    }


    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex;
        int pointCount = event.getPointerCount();
        PointerInfo info;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                info = getPointerInfo(event, 0);
                mListener.onTouchEvent(BytedEffectConstants.TouchEventCode.BEGAN, info.x, info.y, info.force, info.majorRadius, info.pointerId, pointCount);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
                info = getPointerInfo(event, pointerIndex);
                mListener.onTouchEvent(BytedEffectConstants.TouchEventCode.BEGAN, info.x, info.y, info.force, info.majorRadius, info.pointerId, pointCount);
                break;
            case MotionEvent.ACTION_UP:
                info = getPointerInfo(event, 0);
                mListener.onTouchEvent(BytedEffectConstants.TouchEventCode.ENDED, info.x, info.y, info.force, info.majorRadius, info.pointerId, pointCount);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
                info = getPointerInfo(event, pointerIndex);
                mListener.onTouchEvent(BytedEffectConstants.TouchEventCode.ENDED, info.x, info.y, info.force, info.majorRadius, info.pointerId, pointCount);
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < pointCount; i++) {
                    info = getPointerInfo(event, i);
                    mListener.onTouchEvent(BytedEffectConstants.TouchEventCode.MOVED, info.x, info.y, info.force, info.majorRadius, info.pointerId, pointCount);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                info = getPointerInfo(event, 0);
                mListener.onTouchEvent(BytedEffectConstants.TouchEventCode.CANCELLED, info.x, info.y, info.force, info.majorRadius, info.pointerId, pointCount);
                break;
            default:
                break;
        }

        boolean receive = mScaleGesture.onTouchEvent(event);
        receive |= mRotateGesture.onTouchEvent(event);
        receive |= mGesture.onTouchEvent(event);

        return receive;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        LogUtils.d("gesture down");
        mScrollFlag = true;

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        LogUtils.d("gesture click");
        mListener.onGestureEvent(BytedEffectConstants.GestureEventCode.TAP, transX(e.getX()), transY(e.getY()), 0, 0, 1.f);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        LogUtils.d("gesture scroll");
        if (mScrollFlag) {
            mScrollLastPos.x = e1.getX();
            mScrollLastPos.y = e1.getY();
            mScrollFlag = false;
        }
        float dx = e2.getX() - mScrollLastPos.x;
        float dy = e2.getY() - mScrollLastPos.y;
        mScrollLastPos.x = e2.getX();
        mScrollLastPos.y = e2.getY();
        mListener.onGestureEvent(BytedEffectConstants.GestureEventCode.PAN,
                transX(e2.getX()), transY(e2.getY()),
                PreviewSizeManager.getInstance().viewXFactor(dx),
                PreviewSizeManager.getInstance().viewYFactor(dy),
                1.f);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        LogUtils.d("gesture long press");
        mListener.onGestureEvent(BytedEffectConstants.GestureEventCode.LONG_PRESS, transX(e.getX()), transY(e.getY()), 0, 0, 1.f);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        LogUtils.d("gesture scale");

        mAccScaleFactor *= detector.getScaleFactor();
        float tmpScale = mAccScaleFactor / mPreScaleFactor;
            mListener.onGestureEvent(BytedEffectConstants.GestureEventCode.SCALE, tmpScale, 0, 0, 0, mScaleGesture.getScaleFactor());
        mPreScaleFactor = mAccScaleFactor;
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        LogUtils.e("onScaleBegin");

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        LogUtils.e("onScaleEnd");
        mPreScaleFactor = 1f;
        mAccScaleFactor = 1f;

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        LogUtils.d("gesture double click");
        mListener.onGestureEvent(BytedEffectConstants.GestureEventCode.DOUBLE_CLICK, transX(e.getX()), transY(e.getY()), 0, 0, 3.f);
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onRotate(RotateGestureDetector detector) {
        LogUtils.d("gesture rotate");
            mListener.onGestureEvent(BytedEffectConstants.GestureEventCode.ROTATE, -detector.getRotationDegreesDelta() / 360, 0, 0, 0, 1.f);
        return false;
    }

    @Override
    public boolean onRotateBegin(RotateGestureDetector detector) {
        LogUtils.d("onRotateBegin ");

        return true;
    }

    @Override
    public void onRotateEnd(RotateGestureDetector detector) {
        mListener.onGestureEvent(BytedEffectConstants.GestureEventCode.ROTATE, -detector.getRotationDegreesDelta() / 360, 0, 0, 0, 6.f);
        LogUtils.d("onRotateEnd ");

    }

    private float transX(float x) {
        return PreviewSizeManager.getInstance().viewToPreviewXFactor(x);
    }

    private float transY(float y) {
        return PreviewSizeManager.getInstance().viewToPreviewYFactor(y);
    }

    private PointerInfo getPointerInfo(MotionEvent event, int pointerIndex) {
        PointerInfo info = mPointerInfo;
        info.pointerId = event.getPointerId(pointerIndex);
        info.x = transX(event.getX(pointerIndex));
        info.y = transY(event.getY(pointerIndex));
        info.force = event.getPressure(pointerIndex);
        info.pointerId = event.getPointerId(pointerIndex);
        info.majorRadius = 30;
        return info;
    }

    private static class PointerInfo {
        float x;
        float y;
        float force;
        float majorRadius;
        int pointerId;
    }

    public interface OnTouchListener {
        void onTouchEvent(BytedEffectConstants.TouchEventCode eventCode, float x, float y, float force, float majorRadius, int pointerId, int pointerCount);

        void onGestureEvent(BytedEffectConstants.GestureEventCode eventCode, float x, float y, float dx, float dy, float factor);
    }
}
