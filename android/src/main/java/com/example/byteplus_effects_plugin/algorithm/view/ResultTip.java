package com.example.byteplus_effects_plugin.algorithm.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.byteplus_effects_plugin.common.utils.PreviewSizeManager;


/** {zh} 
 * 基类 飘在屏幕任意位置的预测结果 可跟随
 * A prediction floating anywhere on the screen
 *
 * @param <T>
 */

/** {en}
 * Base class, the prediction result floating anywhere on the screen, can follow
 * A prediction floating anywhere on the screen
 *
 * @param  < T >
 */

public abstract class ResultTip<T> extends FrameLayout {
    public ResultTip(@NonNull Context context) {
        super(context);
    }

    public ResultTip(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ResultTip(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addLayout(@NonNull Context context, int layoutId) {
        LayoutInflater.from(context).inflate(layoutId, this);
    }

    abstract void updateInfo(T info);

    /** {zh} 
     * 因相机输出比例和屏幕比例不一致会执行纹理裁剪，裁剪后框的坐标也要对应裁剪
     * Texture clipping is performed because the camera output ratio is not consistent with the screen ratio,
     * the coordinates of the cropped frame should also be cropped accordingly
     *
     * @param rect
     * @param preViewHeight
     * @param previewWidth
     * @param surfaceViewHeight
     * @param surfaceViewWidth
     * @return
     */
    /** {en} 
     * Texture cropping will be performed due to inconsistency between the camera output scale and the screen scale, and the coordinates of the cropped frame should also be cropped accordingly
     * Texture clipping is performed because the camera output ratio is not consistent with the screen ratio,
     * the coordinates of the cropped frame should also be cropped accordingly
     *
     * @param rect
     * @param preViewHeight
     * @param previewWidth
     * @param surfaceViewHeight
     * @param surfaceViewWidth
     * @return
     */

    @Deprecated
    Rect getRectInScreenCord(Rect rect, int preViewHeight, int previewWidth, int surfaceViewHeight, int surfaceViewWidth) {
        float ratio1 = previewWidth * 1.0f / surfaceViewWidth;
        float ratio2 = preViewHeight * 1.0f / surfaceViewHeight;
        if (ratio1 < ratio2) {
            int offset = (preViewHeight - previewWidth * surfaceViewHeight / surfaceViewWidth) / 2;
            return new Rect(rect.left, rect.top - offset, rect.right, rect.bottom - offset);
        } else {
            int offset = (previewWidth - preViewHeight * surfaceViewWidth / surfaceViewHeight) / 2;
            return new Rect(rect.left - offset, rect.top, rect.right - offset, rect.bottom);
        }
    }

    void getRectInScreenCord(Rect rect) {
        PreviewSizeManager.getInstance().previewToView(rect);
    }
}
