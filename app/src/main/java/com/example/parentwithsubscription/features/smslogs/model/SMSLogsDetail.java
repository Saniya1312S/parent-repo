package com.example.parentwithsubscription.features.smslogs.model;

import com.google.gson.annotations.SerializedName;

public class SMSLogsDetail {

    @SerializedName("message_type")
    private String smsType;

    @SerializedName("message")
    private String content;

    @SerializedName("classification")
    private String smsClassificationType;

    @Override
    public String toString() {
        return "SMSLogsDetail{" +
                "smsType='" + smsType + '\'' +
                ", content='" + content + '\'' +
                ", smsClassificationType='" + smsClassificationType + '\'' +
                ", time='" + time + '\'' +
                '}';
    }


    public SMSLogsDetail() {
    }

    public SMSLogsDetail(String smsType, String time, String smsClassificationType, String content) {
        this.smsType = smsType;
        this.time = time;
        this.smsClassificationType = smsClassificationType;
        this.content = content;
    }

    @SerializedName("message_time")
    private String time;

    public String getSmsType() {
        return smsType;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSmsClassificationType() {
        return smsClassificationType;
    }

    public void setSmsClassificationType(String smsClassificationType) {
        this.smsClassificationType = smsClassificationType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
