package com.example.byteplus_effects_plugin.common.view.bubble;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.common.model.BubbleConfig;
import com.example.byteplus_effects_plugin.common.model.EffectType;
import com.example.byteplus_effects_plugin.common.utils.DensityUtils;
import com.example.byteplus_effects_plugin.common.view.SwitchView;
import com.example.byteplus_effects_plugin.core.util.LogUtils;

/** {zh} 
 * 顶部弹窗管理器
 * Created  on 2021/5/19 2:08 下午
 */

/** {en}
 * Top pop-up manager
 * Created on 2021/5/19 2:08 pm
 */

public class BubbleWindowManager {
    private Context mContext;
    private BubblePopupWindow mPopupWindow = null;
    private View mBubbleView;
    private BubbleConfig mConfig;
    private String mKey="";

    public Context getContext() {
        return mContext;
    }

    public BubblePopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public View getBubbleView() {
        return mBubbleView;
    }

    public BubbleConfig getConfig() {
        return mConfig;
    }

    public String getKey() {
        return mKey;
    }

    public BubbleCallback getBubbleCallback() {
        return mBubbleCallback;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public enum ITEM_TYPE{
        EFFECT_TYPE,PERFORMANCE,BEAUTY,RESOLUTION;
    }


    public  interface BubbleCallback{
        void onBeautyDefaultChanged(boolean on);
        void onResolutionChanged(int width, int height);
        void onPerformanceChanged(boolean on);

    }

    private BubbleCallback mBubbleCallback;

    public BubbleWindowManager(Context context) {
        this.mContext = context;
        mPopupWindow = new BubblePopupWindow(mContext);
        mPopupWindow.setParam(DensityUtils.getScreenWidth((Activity) mContext) - mContext.getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.popwindow_margin) * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBubbleView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_view, null);
        mPopupWindow.setBubbleView(mBubbleView); //   {zh} 设置气泡内容       {en} Set bubble content  
        mConfig = new BubbleConfig(EffectType.LITE_ASIA,false, new Point(1280,720), true );
    }








    public void show( View anchor, BubbleCallback callback, ITEM_TYPE...item_types){
        if (null == anchor){
            LogUtils.e("null == anchor");
            return;
        }
        mBubbleCallback = callback;
        for (ITEM_TYPE stickerType: item_types){
            switch (stickerType){
                case BEAUTY:
                    mBubbleView.findViewById(R.id.rl_beauty).setVisibility(View.VISIBLE);
                    SwitchView  sw1 = mBubbleView.findViewById(R.id.sw_beauty);
                    sw1.setOpened(mConfig.isEnableBeauty());
                    sw1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sw1.setOpened(!sw1.isOpened());

                        }
                    });
                    sw1.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                        @Override
                        public void toggleToOn(SwitchView view) {
                            if (mBubbleCallback != null){
                                mBubbleCallback.onBeautyDefaultChanged(true);
                            }
                            mConfig.setEnableBeauty(true);
                        }

                        @Override
                        public void toggleToOff(SwitchView view) {
                            if (mBubbleCallback != null){
                                mBubbleCallback.onBeautyDefaultChanged(false);
                            }
                            mConfig.setEnableBeauty(false);

                        }
                    });

                    break;
//                case EFFECT_TYPE:
//                    mBubbleView.findViewById(R.id.rl_effect_type).setVisibility(View.VISIBLE);
//                    RadioGroup rg1 =  mBubbleView.findViewById(R.id.rg_effect);
//                    rg1.check(mConfig.getEffectType() == EffectType.LITE_ASIA ?R.id.rb_live:R.id.rb_camera);
//                    rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
//
//                            if (mBubbleCallback != null){
//                                EffectType stickerType = EffectType.LITE_ASIA;
//                                if (i == R.id.rb_live){
//                                    stickerType = LocaleUtils.isAsia(mContext)?EffectType.LITE_ASIA :EffectType.LITE_NOT_ASIA;
//                                }else if (i == R.id.rb_camera){
//                                    stickerType = LocaleUtils.isAsia(mContext)?EffectType.STANDARD_ASIA :EffectType.STANDARD_NOT_ASIA;
//                                }
//                                mBubbleCallback.onEffectTypeChanged(stickerType);
//                                mConfig.setEffectType(stickerType);
//
//                            }
//
//                        }
//                    });

//                    break;
                case PERFORMANCE:
                    mBubbleView.findViewById(R.id.rl_performance).setVisibility(View.VISIBLE);
                    SwitchView sw2 = mBubbleView.findViewById(R.id.sw_performance);
                    LogUtils.e("mConfig.isPerformance() ="+mConfig.isPerformance());
                    sw2.setOpened(mConfig.isPerformance());
                    sw2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sw2.setOpened(!sw2.isOpened());

                        }
                    });
                    sw2.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
                        @Override
                        public void toggleToOn(SwitchView view) {
                            if (mBubbleCallback != null){
                                mBubbleCallback.onPerformanceChanged(true);

                            }
                            mConfig.setPerformance(true);

                        }

                        @Override
                        public void toggleToOff(SwitchView view) {
                            if (mBubbleCallback != null){
                                mBubbleCallback.onPerformanceChanged(false);

                            }
                            mConfig.setPerformance(false);

                        }
                    });

                    break;
                case RESOLUTION:
                    mBubbleView.findViewById(R.id.rl_resolution).setVisibility(View.VISIBLE);
                    RadioGroup rg2 =  mBubbleView.findViewById(R.id.rg_resolution);
                    rg2.check(mConfig.getResolution().x == 1280?R.id.rb_720:R.id.rb_480);
                    rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            int resolution = 720;
                            if (i == R.id.rb_720){
                                resolution = 720;
                                if (mBubbleCallback != null){
                                    mBubbleCallback.onResolutionChanged(1280,720 );

                                }
                            }else if (i == R.id.rb_480){
                                resolution = 480;
                                if (mBubbleCallback != null){
                                    mBubbleCallback.onResolutionChanged(640,480 );

                                }
                            }
                            mConfig.setResolution(resolution == 720?new Point(1280,720):new Point(640,480));


                        }
                    });
                    break;

            }

        }

        int offset = anchor.getLeft()+anchor.getWidth()/2 - mContext.getResources().getDimensionPixelSize(com.example.byteplus_effects_plugin.R.dimen.popwindow_margin);
        mPopupWindow.show(anchor, Gravity.BOTTOM, offset); //   {zh} 显示弹窗       {en} Display popup  





    }
}
