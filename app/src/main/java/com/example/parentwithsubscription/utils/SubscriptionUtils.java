package com.example.parentwithsubscription.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SubscriptionUtils {
    private static final String PREFS_NAME = "SubscriptionPrefs";

    public static void saveLimits(Context context, int maxParents, int maxChildren) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("max_parents", maxParents);
        editor.putInt("max_children", maxChildren);
        editor.apply();
    }

    public static int getMaxParents(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt("max_parents", 0);
    }

    public static int getMaxChildren(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt("max_children", 0);
    }

    public static String getCurrentPlanName(Context context) {
        return context.getSharedPreferences("SubscriptionPrefs", Context.MODE_PRIVATE)
                .getString("plan_name", "Basic");
    }

    public static String getCurrentPlanEndDate(Context context) {
        return context.getSharedPreferences("SubscriptionPrefs", Context.MODE_PRIVATE)
                .getString("plan_end_date", "June 1, 2025");
    }

    public static void saveCurrentPlan(Context context, String planName, String endDate) {
        context.getSharedPreferences("SubscriptionPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString("plan_name", planName)
                .putString("plan_end_date", endDate)
                .apply();
    }
}
