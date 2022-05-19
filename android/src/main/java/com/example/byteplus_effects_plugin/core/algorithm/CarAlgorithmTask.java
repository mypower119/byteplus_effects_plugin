package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.algorithm.factory.AlgorithmTaskKeyFactory;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.BefCarDetectInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.CarDetect;

import java.nio.ByteBuffer;

/**
 * Created on 5/14/21 11:24 AM
 */
public class CarAlgorithmTask extends AlgorithmTask<CarAlgorithmTask.CarResourceProvider, BefCarDetectInfo> {
    //  {zh} 总开关  {en} Master switch
    public static final AlgorithmTaskKey CAR_ALGO = AlgorithmTaskKeyFactory.create("carAlgo");
    public static final AlgorithmTaskKey CAR_RECOG = AlgorithmTaskKeyFactory.create("carRecog");
    public static final AlgorithmTaskKey BRAND_RECOG = AlgorithmTaskKeyFactory.create("carBrand");

    public static final double GREY_THREHOLD = 40.0;
    public static final double BLUR_THREHOLD = 5.0;

    private CarDetect mDetector;

    public CarAlgorithmTask(Context context, CarResourceProvider resourceProvider, EffectLicenseProvider licenseProvider) {
        super(context, resourceProvider, licenseProvider);

        mDetector = new CarDetect();
    }

    @Override
    public int initTask() {
        if (!mLicenseProvider.checkLicenseResult("getLicensePath"))
            return mLicenseProvider.getLastErrorCode();

        String licensePath = mLicenseProvider.getLicensePath();
        int ret = mDetector.createHandle(licensePath, mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
        if (!checkResult("initCarDetect", ret)) return ret;
        ret = mDetector.setModel(BytedEffectConstants.CarModelType.DetectModel, mResourceProvider.carModel());
        if (!checkResult("set DetectModel ", ret)) return ret;
        ret = mDetector.setModel(BytedEffectConstants.CarModelType.BrandNodel, mResourceProvider.carBrandModel());
        if (!checkResult("set BrandNodel ", ret)) return ret;
        ret = mDetector.setModel(BytedEffectConstants.CarModelType.OCRModel, mResourceProvider.brandOcrModel());
        if (!checkResult("set OCRModel ", ret)) return ret;
        ret = mDetector.setModel(BytedEffectConstants.CarModelType.TrackModel, mResourceProvider.carTrackModel());
        if (!checkResult("set TrackModel ", ret)) return ret;
        return ret;
    }

    @Override
    public BefCarDetectInfo process(ByteBuffer buffer, int width, int height, int stride, BytedEffectConstants.PixlFormat pixlFormat, BytedEffectConstants.Rotation rotation) {
        LogTimerRecord.RECORD("detectCar");
        LogUtils.d("process car");
        BefCarDetectInfo carDetectInfo = mDetector.detect(buffer, pixlFormat, width, height, stride, rotation);
        LogTimerRecord.STOP("detectCar");
        return carDetectInfo;
    }

    @Override
    public void setConfig(AlgorithmTaskKey key, Object p) {
        super.setConfig(key, p);
        LogUtils.d("setConfig car");

        mDetector.setParam(BytedEffectConstants.CarParamType.BEF_Car_Detect, getBoolConfig(CAR_RECOG) ? 1f : -1f);
        mDetector.setParam(BytedEffectConstants.CarParamType.BEF_Brand_Rec, getBoolConfig(BRAND_RECOG) ? 1f : -1f);
    }

    @Override
    public int destroyTask() {
        mDetector.release();
        return 0;
    }

    @Override
    public int[] preferBufferSize() {
        return new int[0];
    }

    @Override
    public AlgorithmTaskKey key() {
        return CAR_RECOG;
    }

    public interface CarResourceProvider extends AlgorithmResourceProvider {
        String carModel();

        String carBrandModel();

        String brandOcrModel();

        String carTrackModel();
    }
}
