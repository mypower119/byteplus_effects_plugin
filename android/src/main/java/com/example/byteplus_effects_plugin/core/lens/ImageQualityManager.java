package com.example.byteplus_effects_plugin.core.lens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.core.Config;
import com.example.byteplus_effects_plugin.core.lens.util.ImageQualityUtil;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseHelper;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.core.util.timer_record.LogTimerRecord;
import com.bytedance.labcv.effectsdk.AdaptiveSharpen;
import com.bytedance.labcv.effectsdk.BefVideoSRInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.bytedance.labcv.effectsdk.NightScene;
import com.bytedance.labcv.effectsdk.RenderManager;
import com.bytedance.labcv.effectsdk.VideoSR;

/**
 * Created on 5/8/21 2:40 PM
 */
public class ImageQualityManager implements ImageQualityInterface {
    protected VideoSR mVideoSRTask;
    protected NightScene mNightSceneTask;
    protected AdaptiveSharpen mAdaptiveSharpenTask;

    public boolean mEnableVideoSr = false;
    public boolean mEnableNightScene = false;
    public boolean mEnableAdaptiveSharpen = false;


    protected boolean mPause = false; // pause means do nothing
    protected Context mContext;
    protected ImageQualityResourceProvider mResourceProvider;
    protected EffectLicenseProvider mLicenseProvider;

    //power level
    private BytedEffectConstants.ImageQulityPowerLevel mPowerLevel = BytedEffectConstants.ImageQulityPowerLevel.POWER_LEVEL_AUTO;

    //   {zh} 这里只建议业务方调整两个参数：amount和over_ratio。       {en} Only two parameters are recommended for business parties to adjust: amount and over_ratio.  
    //asf config
    private float mAmount = -1; //   {zh} 锐化强度增益，默认-1（无效值），即不调整。有效值为>0：当设置>1时，会增大锐化强度，设置<1时，减弱锐化强度。       {en} Sharpening strength gain, default -1 (invalid value), that is, it is not adjusted. The effective value is > 0: when setting > 1, the sharpening strength will be increased, and when setting < 1, the sharpening strength will be reduced.  
    private float mOverRatio = -1; //   {zh} 黑白边的容忍度增益，默认-1（无效值），即不调整。       {en} The tolerance gain of black and white edges, default -1 (invalid value), that is, it is not adjusted.  
    private float mEdgeWeightGamma = -1; //   {zh} 对中低频边缘的锐化强度进行调整， 默认-1（无效值），即 不调整。有效值为>0       {en} Adjust the sharpening intensity of the middle and low frequency edges, default -1 (invalid value), that is, no adjustment. Valid value is > 0  
    private int mDiffImgSmoothEnable = -1; //   {zh} 开启后减少锐化带来的边缘artifacts，但锐化强度会比关闭时弱一些， 默认-1（无效值），即保持内部设置，目前设置为开启。 有效值为0或1，0--关闭，1--开启       {en} After opening, the edge artifacts brought by sharpening will be reduced, but the sharpening intensity will be weaker than when closing. The default is -1 (invalid value), that is, the internal settings are maintained, and the current setting is on. Valid values are 0 or 1, 0 -- off, 1 -- on  
    private BytedEffectConstants.ImageQualityAsfSceneMode mAsfMode = BytedEffectConstants.ImageQualityAsfSceneMode.ASF_SCENE_MODE_LIVE_RECORED_FRONT;

    //vide sr config
    private String mRWPermissionDir;

    // common config
    private int mMaxFrameWidth = 720;
    private int mMaxFrameHeight = 1280;

    private int mLastFrameWidth = 0;
    private int mLastFrameHeight = 0;

    public ImageQualityManager (Context context, ImageQualityResourceProvider provider){
        mContext = context;
        mResourceProvider = provider;
        mLicenseProvider = EffectLicenseHelper.getInstance(mContext);
    }
    public int init(String dir){
        mRWPermissionDir = dir;
        return 0;
    }

    public int destroy(){
        if(mNightSceneTask != null){
            mNightSceneTask.release();
            mNightSceneTask = null;
        }

        if(mVideoSRTask != null){
            mVideoSRTask.release();
            mVideoSRTask = null;
        }

        if (mAdaptiveSharpenTask != null){
            mAdaptiveSharpenTask.release();
            mAdaptiveSharpenTask = null;
        }

        return BytedEffectConstants.BytedResultCode.BEF_RESULT_SUC;
    }

    public void selectImageQuality(BytedEffectConstants.ImageQualityType imageQualityType, boolean on){

        //   {zh} 判断是否支持视频超分       {en} Determine whether video super score is supported  
        if (imageQualityType == BytedEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_VIDEO_SR || imageQualityType == BytedEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_ADAPTIVE_SHARPEN) {
            if (on & !ImageQualityUtil.isSupportVideoSR(mContext)) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, R.string.video_sr_not_support, Toast.LENGTH_SHORT).show();
                    }
                });
                return ;
            }

        }

        setImageQuality(imageQualityType, on);
    }

    private boolean setImageQuality(BytedEffectConstants.ImageQualityType imageQualityType, boolean open)
    {
        //   {zh} 打开或者关闭夜景增强       {en} Turn on or off Nightscape Enhancement  
        if (imageQualityType == BytedEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_NIGHT_SCENE){
            mEnableNightScene = open;

            if (open){
                if (mNightSceneTask == null){
                    mNightSceneTask = new NightScene();
                    if (mLicenseProvider.checkLicenseResult("getLicensePath")) {
                        int ret = mNightSceneTask.init(mLicenseProvider.getLicensePath(), mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
                        if (!checkResult("init night scene", ret)) {
                            mNightSceneTask.release();
                            mNightSceneTask = null;
                        }
                    }
                }
            }else {
               if(mNightSceneTask != null){
                    mNightSceneTask.release();
                    mNightSceneTask = null;
                }
            }
        }
        //   {zh} 打开或者关闭视频超分       {en} Turn on or off video super score  
        else if (imageQualityType == BytedEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_VIDEO_SR) {
            mEnableVideoSr = open;
            if (open) {
                if (mVideoSRTask == null) {
                    mVideoSRTask = new VideoSR();
                    if (mLicenseProvider.checkLicenseResult("getLicensePath")) {
                        int ret = mVideoSRTask.init(mLicenseProvider.getLicensePath(), mRWPermissionDir, mMaxFrameHeight, mMaxFrameWidth, mPowerLevel,
                                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);
                        if (!checkResult("init video sr", ret)) {
                            mVideoSRTask.release();
                            mVideoSRTask = null;
                        }
                    }
                }
            } else {
                if (mVideoSRTask != null) {
                    mVideoSRTask.release();
                    mVideoSRTask = null;
                }
            }
        }else if (imageQualityType == BytedEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_ADAPTIVE_SHARPEN){
            mEnableAdaptiveSharpen = open;
            if (open) {
                if (mAdaptiveSharpenTask == null){
                    mAdaptiveSharpenTask = new AdaptiveSharpen();
                    if (mLicenseProvider.checkLicenseResult("getLicensePath")) {
                        int ret = mAdaptiveSharpenTask.init(mLicenseProvider.getLicensePath(), mMaxFrameHeight, mMaxFrameWidth,
                                mAsfMode.getMode(), mPowerLevel.getLevel(), mAmount, mOverRatio, mEdgeWeightGamma, mDiffImgSmoothEnable,
                                mLicenseProvider.getLicenseMode() == EffectLicenseProvider.LICENSE_MODE_ENUM.ONLINE_LICENSE);

                        if (!checkResult("init adaptive sharpen", ret)) {
                            mAdaptiveSharpenTask.release();
                            mAdaptiveSharpenTask = null;
                        }
                    }
                }
            } else {
                if (mAdaptiveSharpenTask != null){
                    mAdaptiveSharpenTask.release();
                    mAdaptiveSharpenTask = null;
                    mLastFrameHeight = 0;
                    mLastFrameWidth = 0;
                }
            }
        }

        return true;
    }

    public int processTexture(int srcTextureId,
                              int srcTextureWidth, int srcTextureHeight, ImageQualityResult result)
    {
        // If pause, just return src result
        if (mPause){
            result.texture = srcTextureId;
            result.width = srcTextureWidth;
            result.height = srcTextureHeight;
            return 0;
        }

        if (mEnableVideoSr){
            if (mVideoSRTask != null){
                LogUtils.d("超分处理");

                // This step to judge if the resolution larger than 720p, if true, we release the task, and disable it
                {
                    if ((srcTextureWidth * srcTextureHeight) > 1280 * 720){
                        mEnableVideoSr = false;
                        mVideoSRTask.release();
                        mVideoSRTask = null;

                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, R.string.video_sr_resolution_not_support, Toast.LENGTH_SHORT).show();
                            }
                        });
                        //  {zh} 如果超过720p,返回输入纹理，保证视频编码器不会因为输入无效纹理导致的崩溃  {en} If it exceeds 720p, return the input texture to ensure that the video encoder will not crash due to invalid texture input
                        result.texture = srcTextureId;
                        result.width = srcTextureWidth;
                        result.height = srcTextureHeight;
                        return 0;                    }
                }

                LogTimerRecord.RECORD("video_sr");
                BefVideoSRInfo videoSrResult = mVideoSRTask.process(srcTextureId, srcTextureWidth, srcTextureHeight);
                if (videoSrResult == null){
                    return BytedEffectConstants.BytedResultCode.BEF_RESULT_FAIL;
                }
                LogTimerRecord.STOP("video_sr");

                result.height  = srcTextureHeight * 2;
                result.width   = srcTextureWidth * 2;
                result.texture = videoSrResult.getDestTextureId();

                return BytedEffectConstants.BytedResultCode.BEF_RESULT_SUC;
            }

        }else if (mEnableNightScene){
            if (mNightSceneTask != null){
                Integer destTextureId = new Integer(0);
                LogTimerRecord.RECORD("night_scene");
                int ret = mNightSceneTask.process(srcTextureId, destTextureId, srcTextureWidth, srcTextureHeight);
                if (ret != BytedEffectConstants.BytedResultCode.BEF_RESULT_SUC){
                    return ret;
                }
                LogTimerRecord.STOP("night_scene");
                result.height  = srcTextureHeight;
                result.width   = srcTextureWidth;
                result.texture = destTextureId.intValue();
                return ret;
            }
        } else if (mEnableAdaptiveSharpen && mAdaptiveSharpenTask != null){
            if (mLastFrameWidth != srcTextureWidth || mLastFrameHeight != srcTextureHeight){
                int ret = mAdaptiveSharpenTask.setProperty(mAdaptiveSharpenTask.getmSceneMode(), mAdaptiveSharpenTask.getmPowerLevel(),
                        srcTextureWidth, srcTextureHeight, mAdaptiveSharpenTask.getmAmount(), mAdaptiveSharpenTask.getmOverRatio(),
                        mAdaptiveSharpenTask.getmEdgeWeightGamma(), mAdaptiveSharpenTask.getmDiffImgSmoothEnable());

                if (ret != BytedEffectConstants.BytedResultCode.BEF_RESULT_SUC){
                    Log.e("mAdaptiveSharpenTask", "setProperty: " + ret);
                    return ret;
                }
                mLastFrameWidth = srcTextureWidth;
                mLastFrameHeight = srcTextureHeight;

            }

            Integer destTextureId = new Integer(0);

            LogTimerRecord.RECORD("adaptive_sharpen");
            int ret = mAdaptiveSharpenTask.process(srcTextureId, destTextureId);
            Log.e("mAdaptiveSharpenTask", "processTexture: " + ret);
            LogTimerRecord.STOP("adaptive_sharpen");

            result.height = srcTextureHeight;
            result.width  = srcTextureWidth;
            if (ret == BytedEffectConstants.BytedResultCode.BEF_RESULT_SUC) {
                result.texture = destTextureId.intValue();
            }else {
                result.texture = srcTextureId;
            }
            return ret;

        }
        return BytedEffectConstants.BytedResultCode.BEF_RESULT_FAIL;
    }

    public void recoverStatus(){
        if(mEnableNightScene){
            mNightSceneTask = new NightScene();
            mNightSceneTask.init(mResourceProvider.getLicensePath());
        }

        if (mEnableVideoSr){
            mVideoSRTask = new VideoSR();
            mVideoSRTask.init(mResourceProvider.getLicensePath(), mRWPermissionDir, mMaxFrameHeight, mMaxFrameWidth, mPowerLevel);
        }

        if (mEnableAdaptiveSharpen) {
            mAdaptiveSharpenTask = new AdaptiveSharpen();
            mLastFrameWidth = 0;
            mLastFrameHeight = 0;

            mAdaptiveSharpenTask.init(mResourceProvider.getLicensePath(), mMaxFrameHeight, mMaxFrameWidth,
                    mAsfMode.getMode(), mPowerLevel.getLevel(), mAmount, mOverRatio, mEdgeWeightGamma, mDiffImgSmoothEnable);
        }
    }

    public void setPause(boolean pause) {
        this.mPause = pause;
    }

    protected boolean checkResult(String msg, int ret) {
        if (ret != 0 && ret != -11 && ret != 1) {
            String log = msg + " error: " + ret;
            LogUtils.e(log);
            String toast = RenderManager.formatErrorCode(ret);
            if (toast == null) {
                toast = log;
            }
            Intent intent = new Intent(Config.CHECK_RESULT_BROADCAST_ACTION);
            intent.putExtra("msg", toast);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return false;
        }
        return true;
    }
}
