package com.example.parentwithsubscription.features.socialmedia.model.whatsapp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WhatsAppMessages {

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("name")
    private String name;

    @SerializedName("count")
    private String count;


    @SerializedName("message_detail")
    private List<WhatsAppMessagesDetails> whatsAppMessagesDetails;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<WhatsAppMessagesDetails> getWhatsAppMessagesDetails() {
        return whatsAppMessagesDetails;
    }

    public void setWhatsAppMessagesDetails(List<WhatsAppMessagesDetails> whatsAppMessagesDetails) {
        this.whatsAppMessagesDetails = whatsAppMessagesDetails;
    }
}
