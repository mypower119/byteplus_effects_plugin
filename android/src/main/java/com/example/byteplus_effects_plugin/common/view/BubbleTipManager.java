package com.example.byteplus_effects_plugin.common.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.common.utils.DensityUtils;

/**
 * Created on 2021/5/19 17:19
 */
public class BubbleTipManager {
    public static final long ANIMATION_DURATION = 2000;

    private final Context mContext;
    private View mTipView;


    public BubbleTipManager(Context context, ViewGroup container) {
        this.mContext = context;
        mTipView = LayoutInflater.from(context).inflate(R.layout.layout_tip, null, false);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.TOP;
        lp.topMargin = (int) DensityUtils.dp2px(context, 100f);
        mTipView.setVisibility(View.INVISIBLE);
        container.addView(mTipView,lp);
    }

    /** {zh} 
     * @brief 短时间显示描述信息
     * @param title 简略描述
     * @param desc 详细描述
     */
    /** {en} 
     * @brief Short time display description information
     * @param title brief description
     * @param desc  detailed description
     */

    public void show(int title, int desc) {

        updateBubble(mTipView, title, desc);

        show(mTipView);
    }
    /** {zh} 
     * @brief 短时间显示描述信息
     * @param title 简略描述
     * @param desc 详细描述
     */
    /** {en} 
     * @brief Short time display description information
     * @param title brief description
     * @param desc  detailed description
     */

    public void show(String title, String desc) {

        updateBubble(mTipView, title, desc);

        show(mTipView);
    }

    private void show(View view) {
        view.setVisibility(View.VISIBLE);

        view.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
    }

    private void updateBubble(View view, int title, int desc) {
        updateBubble(view, title> 0?mContext.getString(title):"",desc > 0? mContext.getString(desc):"" );
    }

    private void updateBubble(View view, String title, String desc) {
        view.clearAnimation();
        view.setAlpha(1);
        TextView tvTitle = view.findViewById(R.id.tv_tip_title);
        TextView tvDesc = view.findViewById(R.id.tv_tip_desc);
        if (TextUtils.isEmpty(desc)){
            tvDesc.setVisibility(View.GONE);
            view.findViewById(R.id.tip_divider).setVisibility(View.GONE);
        }else {
            tvDesc.setVisibility(View.VISIBLE);
            view.findViewById(R.id.tip_divider).setVisibility(View.VISIBLE);
            tvDesc.setText(desc);

        }
        tvTitle.setText(title);
    }
}
