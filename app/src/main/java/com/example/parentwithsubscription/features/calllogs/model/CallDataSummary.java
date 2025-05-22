package com.example.parentwithsubscription.features.calllogs.model;

import com.google.gson.annotations.SerializedName;

public class CallDataSummary {
    @SerializedName("incoming_calls")
    private int totalIncomingCalls;

    @SerializedName("outgoing_calls")
    private int totalOutgoingCalls;

    @SerializedName("missed_calls")
    private int totalMissedCalls;


    // Getter methods
    public int getTotalIncomingCalls() {
        return totalIncomingCalls;
    }

    public int getTotalOutgoingCalls() {
        return totalOutgoingCalls;
    }

    public int getTotalMissedCalls() {
        return totalMissedCalls;
    }
}
