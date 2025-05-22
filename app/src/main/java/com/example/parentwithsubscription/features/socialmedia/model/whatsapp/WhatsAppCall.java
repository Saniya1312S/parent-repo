package com.example.parentwithsubscription.features.socialmedia.model.whatsapp;

import com.google.gson.annotations.SerializedName;

public class WhatsAppCall {
    private String name;
    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("call_type")
    private String callType;  // incoming or outgoing

    @SerializedName("call_mode")
    private String callMode;  // voice or video

    @SerializedName("call_time")
    private String time;
    private String duration;

    // Constructor
    public WhatsAppCall(String name, String phoneNumber, String callType, String callMode, String time, String duration) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.callType = callType;
        this.callMode = callMode;
        this.time = time;
        this.duration = duration;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCallType() {
        return callType;
    }

    public String getCallMode() {
        return callMode;
    }

    public String getTime() {
        return time;
    }

    public String getDuration() {
        return duration;
    }
}

