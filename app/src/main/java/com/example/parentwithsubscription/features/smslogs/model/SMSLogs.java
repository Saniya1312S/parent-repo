package com.example.parentwithsubscription.features.smslogs.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SMSLogs {
    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("name")
    private String name;

    @SerializedName("count")
    private String count;

    @SerializedName("messages")
    private List<SMSLogsDetail> smsLogsDetails;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<SMSLogsDetail> getSmsLogsDetails() {
        return smsLogsDetails;
    }

    public void setSmsLogsDetails(List<SMSLogsDetail> smsLogsDetails) {
        this.smsLogsDetails = smsLogsDetails;
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

    public SMSLogs(String phoneNumber, String name, String count, List<SMSLogsDetail> smsLogsDetails) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.count = count;
        this.smsLogsDetails = smsLogsDetails;
    }

    @Override
    public String toString() {
        return "SMSLogs{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", count='" + count + '\'' +
                ", smsLogsDetails=" + smsLogsDetails +
                '}';
    }

    public SMSLogs() {
    }
}







