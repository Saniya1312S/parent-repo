package com.example.parentwithsubscription.features.socialmedia.model.whatsapp;

import com.google.gson.annotations.SerializedName;

public class WhatsAppContacts {

    @SerializedName("contact_name")
    private String contactName;

    @SerializedName("phone_number")
    private String phoneNumber;

    // Constructor
    public WhatsAppContacts(String contactName, String phoneNumber) {
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "WhatsAppContacts{" +
                "contactName='" + contactName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
