package com.example.parentwithsubscription.features.appusage.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class AppUsageSessions {

    @SerializedName("start_time")
    private String openTime;

    @SerializedName("end_time")
    private String closeTime;

    @SerializedName("duration")
    private int duration;

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @NonNull
    @Override
    public String toString() {
        return "AppUsageSessions{" +
                "openTime='" + openTime + '\'' +
                ", closeTime='" + closeTime + '\'' +
                ", duration=" + duration +
                '}';
    }
}

