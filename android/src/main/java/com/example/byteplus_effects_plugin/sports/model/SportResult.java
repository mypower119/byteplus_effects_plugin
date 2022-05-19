package com.example.byteplus_effects_plugin.sports.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 2021/7/18 17:22
 */
public class SportResult implements Parcelable {
    public static final String SPORT_RESULT_KEY = "sport_result_key";

    private SportItem sportItem;
    private int count;
    private int time;

    public SportResult(SportItem sportItem, int count, int time) {
        this.sportItem = sportItem;
        this.count = count;
        this.time = time;
    }

    public SportItem getSportItem() {
        return sportItem;
    }

    public void setSportItem(SportItem sportItem) {
        this.sportItem = sportItem;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.sportItem, flags);
        dest.writeInt(this.count);
        dest.writeInt(this.time);
    }

    public void readFromParcel(Parcel source) {
        this.sportItem = source.readParcelable(SportItem.class.getClassLoader());
        this.count = source.readInt();
        this.time = source.readInt();
    }

    protected SportResult(Parcel in) {
        this.sportItem = in.readParcelable(SportItem.class.getClassLoader());
        this.count = in.readInt();
        this.time = in.readInt();
    }

    public static final Creator<SportResult> CREATOR = new Creator<SportResult>() {
        @Override
        public SportResult createFromParcel(Parcel source) {
            return new SportResult(source);
        }

        @Override
        public SportResult[] newArray(int size) {
            return new SportResult[size];
        }
    };
}
