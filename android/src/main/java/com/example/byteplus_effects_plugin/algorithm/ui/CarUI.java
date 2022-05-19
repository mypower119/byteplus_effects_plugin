package com.example.byteplus_effects_plugin.algorithm.ui;

import static com.example.byteplus_effects_plugin.core.algorithm.CarAlgorithmTask.BLUR_THREHOLD;
import static com.example.byteplus_effects_plugin.core.algorithm.CarAlgorithmTask.BRAND_RECOG;
import static com.example.byteplus_effects_plugin.core.algorithm.CarAlgorithmTask.CAR_RECOG;
import static com.example.byteplus_effects_plugin.core.algorithm.CarAlgorithmTask.GREY_THREHOLD;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.widget.LinearLayout;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItemGroup;
import com.example.byteplus_effects_plugin.algorithm.view.BrandRecogTip;
import com.example.byteplus_effects_plugin.algorithm.view.CarRectInfoTip;
import com.example.byteplus_effects_plugin.algorithm.view.ResultTip;
import com.example.byteplus_effects_plugin.algorithm.view.TipManager;
import com.example.byteplus_effects_plugin.common.utils.LocaleUtils;
import com.example.byteplus_effects_plugin.common.view.PropertyTextView;
import com.example.byteplus_effects_plugin.core.algorithm.CarAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefCarDetectInfo;

import java.util.Arrays;

public class CarUI extends BaseAlgorithmUI<BefCarDetectInfo> {



    private LinearLayout llLightCls;
    private PropertyTextView tvCarlightGrey;
    private PropertyTextView tvCarLightBlur;


    @Override
    void initView() {
        super.initView();

        addLayout(R.layout.layout_car_info, R.id.fl_algorithm_info);

        if (!checkAvailable(tipManager())) return;
        llLightCls = provider().findViewById(R.id.ll_carlight_cls);
        tvCarlightGrey = provider().findViewById(R.id.ptv_carlight_grey);
        tvCarLightBlur = provider().findViewById(R.id.ptv_carlight_blur);
        tipManager().registerGenerator(CAR_RECOG, new TipManager.ResultTipGenerator<BefCarDetectInfo.BefCarRect>() {
            @Override
            public ResultTip<BefCarDetectInfo.BefCarRect> create(Context context) {
                return new CarRectInfoTip(context);
            }
        });

        tipManager().registerGenerator(BRAND_RECOG, new TipManager.ResultTipGenerator<BefCarDetectInfo.BefBrandInfo>() {
            @Override
            public ResultTip<BefCarDetectInfo.BefBrandInfo> create(Context context) {
                return new BrandRecogTip(context);
            }
        });
    }

    @Override
    public void onReceiveResult(BefCarDetectInfo carDetectInfo) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (null == carDetectInfo)return;
                if (provider().getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    tipManager().updateInfo(CAR_RECOG, carDetectInfo.getCarRects());
                    tipManager().updateInfo(BRAND_RECOG, carDetectInfo.getBrandInfos());
                } else {
                    tipManager().updateInfo(CAR_RECOG, carDetectInfo.getCarRects());
                    tipManager().updateInfo(BRAND_RECOG, carDetectInfo.getBrandInfos());
                }
                tvCarlightGrey.setValue(carDetectInfo.getGrayScore() > GREY_THREHOLD ? provider().getString(R.string.car_gray_ok) : provider().getString(R.string.car_gray_not));
                tvCarLightBlur.setValue(carDetectInfo.getBlurScore() > BLUR_THREHOLD ? provider().getString(R.string.car_blur_ok) : provider().getString(R.string.car_blur_not));
            }
        });
    }


    @Override
    public void onEvent(AlgorithmTaskKey key, boolean flag) {

        super.onEvent(key, flag);

        if (checkAvailable(tipManager())) {
            tipManager().enableOrRemove(key, flag);
        }
        if (key.equals(CAR_RECOG) ) {
            llLightCls.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        }

    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        AlgorithmItem carDetect = (AlgorithmItem) new AlgorithmItem(CAR_RECOG)
                .setIcon(R.drawable.ic_car)
                .setTitle(R.string.car_detect)
                .setDesc(R.string.car_detect);

        AlgorithmItem brandDetect = (AlgorithmItem) new AlgorithmItem(BRAND_RECOG)
                .setIcon(R.drawable.ic_car_brand)
                .setTitle(R.string.brand_rec)
                .setDesc(R.string.brand_rec);


        AlgorithmItemGroup faceGroup = new AlgorithmItemGroup(
                Arrays.asList(carDetect, brandDetect), false
        );
        //  {zh} 只中国大陆格式车牌  {en} Only Chinese mainland format license plate
        if (LocaleUtils.isCarBrandLimit(provider().getContext())){
            faceGroup.setItems(Arrays.asList(carDetect));
        }
        faceGroup.setKey(CarAlgorithmTask.CAR_ALGO);
        faceGroup.setTitle(R.string.tab_car);
        return faceGroup;
    }
}
