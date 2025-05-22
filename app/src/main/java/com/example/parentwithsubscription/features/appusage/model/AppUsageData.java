package com.example.parentwithsubscription.features.appusage.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppUsageData {
    @SerializedName("app_usage")
    private List<AppUsage> appUsage;  // List of app usage data

    @SerializedName("installed_apps")
    private List<InstallUninstalledApp> installedUninstalledApps;  // Combined list of installed and uninstalled apps

    @SerializedName("uninstalled_apps")
    private List<InstallUninstalledApp> uninstalledUninstalledApps;


    public List<AppUsage> getAppUsage() {
        return appUsage;
    }

    public void setAppUsage(List<AppUsage> appUsage) {
        this.appUsage = appUsage;
    }

    public List<InstallUninstalledApp> getInstalledUninstalledApps() {
        return installedUninstalledApps;
    }

    public void setInstalledUninstalledApps(List<InstallUninstalledApp> installedUninstalledApps) {
        this.installedUninstalledApps = installedUninstalledApps;
    }

    public List<InstallUninstalledApp> getUninstalledUninstalledApps() {
        return uninstalledUninstalledApps;
    }

    public void setUninstalledUninstalledApps(List<InstallUninstalledApp> uninstalledUninstalledApps) {
        this.uninstalledUninstalledApps = uninstalledUninstalledApps;
    }
}





/*
package com.example.parent.features.appusage.model;

import java.util.List;

public class AppUsageData {
    private List<AppUsage> appUsage;  // List of app usage data
    private List<InstallUninstalledApp> installedUninstalledApps;  // Combined list of installed and uninstalled apps

    public List<AppUsage> getAppUsage() {
        return appUsage;
    }

    public void setAppUsage(List<AppUsage> appUsage) {
        this.appUsage = appUsage;
    }

    public List<InstallUninstalledApp> getInstalledUninstalledApps() {
        return installedUninstalledApps;
    }

    public void setInstalledUninstalledApps(List<InstallUninstalledApp> installedUninstalledApps) {
        this.installedUninstalledApps = installedUninstalledApps;
    }
}
*/
