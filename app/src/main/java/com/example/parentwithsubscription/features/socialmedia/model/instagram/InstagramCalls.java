package com.example.parentwithsubscription.features.socialmedia.model.instagram;

import com.google.gson.annotations.SerializedName;

public class InstagramCalls {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("call_type")
    private String callType;  // incoming or outgoing

    @SerializedName("call_mode")
    private String callMode;  // voice or video

    @SerializedName("call_time")
    private String time;
    private String duration;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallMode() {
        return callMode;
    }

    public void setCallMode(String callMode) {
        this.callMode = callMode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "InstagramCalls{" +
                "userId='" + userId + '\'' +
                ", callType='" + callType + '\'' +
                ", callMode='" + callMode + '\'' +
                ", time='" + time + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
