package com.example.parentwithsubscription.common;

public class GlobalData {
    private static String deviceId = "3103456789";  // default fallback

    public static String getDeviceId() {
        return deviceId;
    }

    public static void setDeviceId(String newDeviceId) {
        deviceId = newDeviceId;
    }
}
