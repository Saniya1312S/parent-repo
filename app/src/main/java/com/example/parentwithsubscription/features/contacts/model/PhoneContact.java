package com.example.parentwithsubscription.features.contacts.model;

import com.google.gson.annotations.SerializedName;

public class PhoneContact {
    private String name;
    @SerializedName("phone_number")
    private String phoneNumber;
    private boolean isHeader;

    public PhoneContact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isHeader = false;
    }

    public PhoneContact(String name, boolean isHeader) {
        this.name = name;
        this.isHeader = isHeader;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isHeader() {
        return isHeader;
    }
}

