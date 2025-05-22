package com.example.parentwithsubscription.features.socialmedia.model;

public class SocialMediaData {

    private String appName;
    private String packageName;
    private String senderName;
    private String message;

    public SocialMediaData(String appName, String packageName, String senderName, String message) {
        this.appName = appName;
        this.packageName = packageName;
        this.senderName = senderName;
        this.message = message;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }
}
