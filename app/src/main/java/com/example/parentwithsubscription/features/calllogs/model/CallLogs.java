package com.example.parentwithsubscription.features.calllogs.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CallLogs {
    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("name")
    private String name;

    @SerializedName("count")
    private String count;

    @SerializedName("call_details")
    private List<CallLogsDetail> callLogsDetails;


    public CallLogs(String phoneNumber, String name, List<CallLogsDetail> filteredCalls, String totalDuration) {
    }

    public CallLogs() {

    }

    // Getters and setters
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

    public List<CallLogsDetail> getCallLogsDetails() {
        return callLogsDetails;
    }

    public void setCallLogsDetails(List<CallLogsDetail> callLogsDetails) {
        this.callLogsDetails = callLogsDetails;
    }

    private boolean blocked;  // New field to track blocked status

    // Other fields and methods...

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
