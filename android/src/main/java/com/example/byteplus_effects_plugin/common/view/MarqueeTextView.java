package com.example.byteplus_effects_plugin.common.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class MarqueeTextView extends AppCompatTextView {
    private boolean mEnableFocus = true;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // {zh} 设置单行 {en} Set single line
        setSingleLine();
        // {zh} 设置Ellipsize {en} Set Ellipsize
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        // {zh} 获取焦点 {en} Get focus
        setFocusable(true);
        // {zh} 走马灯的重复次数，-1代表无限重复 {en} The number of repetitions of the loop animation, -1 represents infinite repetitions
        setMarqueeRepeatLimit(-1);
        // {zh} 强制获得焦点 {en} Mandatory focus
        setFocusableInTouchMode(true);
    }

    public void setMarqueue(boolean flag){
        mEnableFocus = flag;


    }

    /*
    /* {zh}
     *这个属性这个View得到焦点,在这里我们设置为true,这个View就永远是有焦点的
     */
    /* {en}
     *This property, this View gets the focus, where we set it to true, this View will always have the focus
     */
    @Override
    public boolean isFocused() {
        return mEnableFocus;
    }
}