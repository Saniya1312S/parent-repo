package com.example.parentwithsubscription.features.calllogs.model;

public class MissedCall {
    private String phoneNumber;
    private int missedCalls;
    private String name;

    // Constructor
    public MissedCall(String phoneNumber, int missedCalls, String name) {
        this.phoneNumber = phoneNumber;
        this.missedCalls = missedCalls;
        this.name = name;
    }

    // Getters and setters (optional)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getMissedCalls() {
        return missedCalls;
    }

    public void setMissedCalls(int missedCalls) {
        this.missedCalls = missedCalls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

/*
package com.example.parent.features.calllogs.model;

public class MissedCall {
    private String phoneNumber;
    private int missedCalls;


    // Constructor
    public MissedCall(String phoneNumber, int missedCalls) {
        this.phoneNumber = phoneNumber;
        this.missedCalls = missedCalls;
    }

    // Getters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getMissedCalls() {
        return missedCalls;
    }
}
*/