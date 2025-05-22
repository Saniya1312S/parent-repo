package com.example.parentwithsubscription.features.appusage.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppUsage implements Parcelable {
    @SerializedName("app_name")
    String appName;
    @SerializedName("package_name")
    String packageName;
    @SerializedName("usage_time")
    String usageTime;
    @SerializedName("sessions")
    List<AppUsageSessions> usage;
    private Drawable appIcon;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(String usageTime) {
        this.usageTime = usageTime;
    }

    public List<AppUsageSessions> getUsage() {
        return usage;
    }

    public void setUsage(List<AppUsageSessions> usage) {
        this.usage = usage;
    }

    public AppUsage(String appName, Drawable appIcon, String usageTime) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.usageTime = usageTime;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    protected AppUsage(Parcel in) {
        appName = in.readString();
        usageTime = in.readString();
        packageName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeString(usageTime);
        dest.writeString(packageName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppUsage> CREATOR = new Creator<AppUsage>() {
        @Override
        public AppUsage createFromParcel(Parcel in) {
            return new AppUsage(in);
        }

        @Override
        public AppUsage[] newArray(int size) {
            return new AppUsage[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "AppUsage{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", usageTime='" + usageTime + '\'' +
                ", usage=" + usage +
                ", appIcon=" + appIcon +
                '}';
    }
}











/*package com.example.parent.features.appusage.model;

public class AppUsageData {
    private String app_name;
    private String package_name;
    private String usage_time;

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getUsage_time() {
        return usage_time;
    }

    public void setUsage_time(String usage_time) {
        this.usage_time = usage_time;
    }
}*/

