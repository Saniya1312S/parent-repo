package com.example.parentwithsubscription.features.socialmedia.model.instagram;

import com.google.gson.annotations.SerializedName;

public class InstagramMessageDetails {

    @SerializedName("message_type")
    private String messageType;

    @SerializedName("message_time")
    private String time;

    @SerializedName("classification")
    private String classification;

    @SerializedName("message")
    private String content;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "InstagramMessageDetails{" +
                "message_type='" + messageType + '\'' +
                ", time='" + time + '\'' +
                ", classification='" + classification + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
