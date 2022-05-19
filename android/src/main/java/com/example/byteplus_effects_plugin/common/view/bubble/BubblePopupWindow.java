package com.example.byteplus_effects_plugin.common.view.bubble;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.common.utils.DensityUtils;


/**
 * @author yuyh.
 * @date 2016/8/25.
 */
public class BubblePopupWindow extends PopupWindow {

    private BubbleRelativeLayout bubbleView;
    private Context context;

    public BubblePopupWindow(Context context) {
        this.context = context;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        setOutsideTouchable(false);
        setClippingEnabled(false);

        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.transparent));
        setBackgroundDrawable(dw);
    }

    public void setBubbleView(View view) {
        bubbleView = new BubbleRelativeLayout(context);
        bubbleView.setBackgroundColor(Color.TRANSPARENT);
        bubbleView.addView(view);
        setContentView(bubbleView);
    }

    public void setParam(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void show(View parent) {
        show(parent, Gravity.TOP, getMeasuredWidth() / 2);
    }

    public void show(View parent, int gravity) {
        show(parent, gravity, getMeasuredWidth() / 2);
    }




    /** {zh} 
     * 显示弹窗
     *
     * @param parent
     * @param gravity
     * @param bubbleOffset 气泡尖角位置偏移量。默认位于中间
     */
    /** {en} 
     * Display popup
     *
     * @param parent
     * @param gravity
     * @param bubbleOffset Bubble Position Offset. Default is in the middle
     */

    public void show(View parent, int gravity, float bubbleOffset) {

        BubbleRelativeLayout.BubbleLegOrientation orientation = BubbleRelativeLayout.BubbleLegOrientation.LEFT;
        if (!this.isShowing()) {
            switch (gravity) {
                case Gravity.BOTTOM:
                    orientation = BubbleRelativeLayout.BubbleLegOrientation.TOP;
                    break;
                case Gravity.TOP:
                    orientation = BubbleRelativeLayout.BubbleLegOrientation.BOTTOM;
                    break;
                case Gravity.RIGHT:
                    orientation = BubbleRelativeLayout.BubbleLegOrientation.LEFT;
                    break;
                case Gravity.LEFT:
                    orientation = BubbleRelativeLayout.BubbleLegOrientation.RIGHT;
                    break;
                default:
                    break;
            }
            bubbleView.setBubbleParams(orientation, bubbleOffset); //    {zh} 设置气泡布局方向及尖角偏移        {en} Set the bubble layout direction and sharp angle offset  

            int[] location = new int[2];
            parent.getLocationOnScreen(location);

            switch (gravity) {
                case Gravity.BOTTOM:
                    showAtLocation(parent, Gravity.NO_GRAVITY,context.getResources().getDimensionPixelSize(R.dimen.popwindow_margin), location[1]+parent.getHeight()-DensityUtils.getStatusBarHeight((Activity)context));
                    break;
                case Gravity.TOP:
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] - getMeasureHeight());
                    break;
                case Gravity.RIGHT:
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + parent.getWidth(), location[1] - (parent.getHeight() / 2));
                    break;
                case Gravity.LEFT:
                    showAtLocation(parent, Gravity.NO_GRAVITY, location[0] - getMeasuredWidth(), location[1] - (parent.getHeight() / 2));
                    break;
                default:
                    break;
            }

            /** {zh} 
             * 点击popupWindow让背景变暗
             */
            /** {en} 
             * Click on popupWindow to darken the background
             */

//            final WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
//               {zh} lp.alpha = 0.4f;//代表透明程度，范围为0 - 1.0f               {en} Lp.alpha = 0.4f;//represents the degree of transparency, ranging from 0 to 1.0f
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//            getActivity().getWindow().setAttributes(lp);
//
//            this.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//                    lp.alpha = 1.0f;
//                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                    getActivity().getWindow().setAttributes(lp);
//                }
//            });
        } else {
            this.dismiss();
        }
    }

    private Activity getActivity(){
        return (Activity)context;
    }

    /** {zh} 
     * 测量高度
     * @return
     */
    /** {en} 
     * Measuring height
     * @return
     */

    public int getMeasureHeight() {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popHeight = getContentView().getMeasuredHeight();
        return popHeight;
    }

    /** {zh} 
     * 测量宽度
     * @return
     */
    /** {en} 
     * Measure width
     * @return
     */

    public int getMeasuredWidth() {
        getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popWidth = getContentView().getMeasuredWidth();
        return popWidth;
    }
}
