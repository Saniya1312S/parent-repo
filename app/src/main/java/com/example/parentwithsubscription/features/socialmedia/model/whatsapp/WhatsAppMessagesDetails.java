package com.example.parentwithsubscription.features.socialmedia.model.whatsapp;

import com.google.gson.annotations.SerializedName;

public class WhatsAppMessagesDetails {
    @SerializedName("message_type")
    private String whatsappMessageType;

    @SerializedName("message")
    private String content;

    @SerializedName("message_time")
    private String time;

    @SerializedName("classification")
    private String classification;

    public String getWhatsappMessageType() {
        return whatsappMessageType;
    }

    public void setWhatsappMessageType(String whatsappMessageType) {
        this.whatsappMessageType = whatsappMessageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
