package com.example.parentwithsubscription.features.calllogs.model;

import com.google.gson.annotations.SerializedName;

public class CallLogsDetail {
    @SerializedName("call_types")
    private String callType;

    @SerializedName("call_time")
    private long time;

    @SerializedName("duration")
    private int duration;


    // Getters and setters
    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
