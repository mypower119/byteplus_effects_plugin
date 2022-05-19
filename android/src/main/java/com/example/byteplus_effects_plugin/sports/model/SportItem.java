package com.example.byteplus_effects_plugin.sports.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.byteplus_effects_plugin.core.algorithm.ActionRecognitionAlgorithmTask;
import com.bytedance.labcv.effectsdk.BefActionRecognitionInfo;

/**
 * Created on 2021/7/15 11:45
 */
public class SportItem implements Parcelable {
    public static final String SPORT_ITEM_KEY = "sport_item_key";

    private ActionRecognitionAlgorithmTask.ActionType type;
    private int imgRes;
    private int retImgRes;
    private int textRes;
    private int previewVideoRes;
    private int maskRes;
    private boolean needLandscape;

    private int sportTime;

    public SportItem(ActionRecognitionAlgorithmTask.ActionType type, int imgRes, int retImgRes, int textRes, int previewVideoRes, int maskRes, boolean needLandscape) {
        this.type = type;
        this.imgRes = imgRes;
        this.retImgRes = retImgRes;
        this.textRes = textRes;
        this.previewVideoRes = previewVideoRes;
        this.maskRes = maskRes;
        this.needLandscape = needLandscape;
    }

    public SportItem(ActionRecognitionAlgorithmTask.ActionType type, int imgRes, int textRes, int previewVideoRes, int maskRes, boolean needLandscape) {
        this.type = type;
        this.imgRes = imgRes;
        this.textRes = textRes;
        this.previewVideoRes = previewVideoRes;
        this.maskRes = maskRes;
        this.needLandscape = needLandscape;
    }

    public BefActionRecognitionInfo.ActionRecognitionPoseType readyPoseType() {
        switch (type) {
            case OPEN_CLOSE_JUMP:
            case DEEP_SQUAT:
            case HIGH_RUN:
                return BefActionRecognitionInfo.ActionRecognitionPoseType.STAND;
            case SIT_UP:
            case HIP_BRIDGE:
                return BefActionRecognitionInfo.ActionRecognitionPoseType.SITTING;
            case PUSH_UP:
            case PLANK:
            case KNEELING_PUSH_UP:
                return BefActionRecognitionInfo.ActionRecognitionPoseType.LYING;
            case LUNGE:
            case LUNGE_SQUAT:
                return BefActionRecognitionInfo.ActionRecognitionPoseType.SIDERIGHT;

        }
        return BefActionRecognitionInfo.ActionRecognitionPoseType.STAND;
    }

    public ActionRecognitionAlgorithmTask.ActionType getType() {
        return type;
    }

    public void setType(ActionRecognitionAlgorithmTask.ActionType type) {
        this.type = type;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public int getTextRes() {
        return textRes;
    }

    public void setTextRes(int textRes) {
        this.textRes = textRes;
    }

    public int getPreviewVideoRes() {
        return previewVideoRes;
    }

    public void setPreviewVideoRes(int previewVideoRes) {
        this.previewVideoRes = previewVideoRes;
    }

    public int getSportTime() {
        return sportTime;
    }

    public void setSportTime(int sportTime) {
        this.sportTime = sportTime;
    }

    public boolean getNeedLandscape() {
        return needLandscape;
    }

    public void setNeedLandscape(boolean needLandscape) {
        this.needLandscape = needLandscape;
    }

    public int getMaskRes() {
        return maskRes;
    }

    public void setMaskRes(int maskRes) {
        this.maskRes = maskRes;
    }

    public int getRetImgRes() {
        return retImgRes;
    }

    public void setRetImgRes(int retImgRes) {
        this.retImgRes = retImgRes;
    }

    public boolean isNeedLandscape() {
        return needLandscape;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeInt(this.imgRes);
        dest.writeInt(this.retImgRes);
        dest.writeInt(this.textRes);
        dest.writeInt(this.previewVideoRes);
        dest.writeInt(this.maskRes);
        dest.writeByte(this.needLandscape ? (byte) 1 : (byte) 0);
        dest.writeInt(this.sportTime);
    }

    public void readFromParcel(Parcel source) {
        int tmpType = source.readInt();
        this.type = tmpType == -1 ? null : ActionRecognitionAlgorithmTask.ActionType.values()[tmpType];
        this.imgRes = source.readInt();
        this.retImgRes = source.readInt();
        this.textRes = source.readInt();
        this.previewVideoRes = source.readInt();
        this.maskRes = source.readInt();
        this.needLandscape = source.readByte() != 0;
        this.sportTime = source.readInt();
    }

    protected SportItem(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : ActionRecognitionAlgorithmTask.ActionType.values()[tmpType];
        this.imgRes = in.readInt();
        this.retImgRes = in.readInt();
        this.textRes = in.readInt();
        this.previewVideoRes = in.readInt();
        this.maskRes = in.readInt();
        this.needLandscape = in.readByte() != 0;
        this.sportTime = in.readInt();
    }

    public static final Creator<SportItem> CREATOR = new Creator<SportItem>() {
        @Override
        public SportItem createFromParcel(Parcel source) {
            return new SportItem(source);
        }

        @Override
        public SportItem[] newArray(int size) {
            return new SportItem[size];
        }
    };
}
