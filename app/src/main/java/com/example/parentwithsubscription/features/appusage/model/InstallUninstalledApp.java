package com.example.parentwithsubscription.features.appusage.model;

import com.google.gson.annotations.SerializedName;

public class InstallUninstalledApp {

    @SuppressWarnings("app_name")
    @SerializedName("app_name")
    private String appName;

    @SerializedName("package_name")
    private String packageName;

    @SerializedName("time")
    private long time;

    // Updated constructor to include packageName
    public InstallUninstalledApp(String appName, String packageName, long time) {
        this.appName = appName;
        this.packageName = packageName;
        this.time = time;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public long getTime() {
        return time;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
