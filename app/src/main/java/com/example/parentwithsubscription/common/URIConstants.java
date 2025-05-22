package com.example.parentwithsubscription.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public interface URIConstants {



    public final String deviceId = GlobalData.getDeviceId();  // Dynamically use the GlobalData deviceId

    public final String URL_DOMAIN = "http://192.168.0.107:5000/";

    public final String LOGIN_URL = URL_DOMAIN + "user/login";

    public final String REGISTER_URL = URL_DOMAIN + "user/register";
    public final String SUBSCRIBE_URL = URL_DOMAIN + "user/subscribe";
    public final String GUARDIAN_REGISTER_URL = URL_DOMAIN + "user/guardian-register";
    public final String GUARDIAN_DETAILS_REGISTER_URL = URL_DOMAIN + "user/add_guardian_details";
    public final String GUARDIAN_MONGODB_URL = URL_DOMAIN + "user/guardian-family-tree";
    public final String CHILD_MONGODB_URL = URL_DOMAIN + "user/child-family-tree";

    public final String LOCATION_HISTORY_URL = URL_DOMAIN + "location/get_location_filter_data?device_id=" + deviceId;

    public final String CALL_LOGS_URL = URL_DOMAIN + "call/get_call_filter_data?device_id=" + deviceId;
    public final String CALL_SUMMARY_URL = URL_DOMAIN + "call/get_call_summary?device_id=" + deviceId;

    public final String SMS_LOGS_URL = URL_DOMAIN + "message/get_messages_filter_data?device_id=" + deviceId;

    public final String APP_USAGE_URL = URL_DOMAIN + "app_usage/get_app_usage_filter_data?device_id=" + deviceId;

    public final String CONTACTS_URL = URL_DOMAIN + "contacts/get_contacts_data?device_id=" + deviceId;

    public final String SOCIAL_MEDIA_URL = URL_DOMAIN + "social_media/get_filtered_social_media_data";

    public final String FAMILY_DETAILS = URL_DOMAIN + "/user/family-details";

    public final String PLAN_DETAILS = URL_DOMAIN + "/user/plans";

    public final String CHILD_MOBILE_NUMBER = URL_DOMAIN +"user/child-mobile?member_name=";
    // Default method to format timestamp based on location/time zone
    static String formatTimestamp(long timestamp) {
        // Get the default locale and time zone
        Locale currentLocale = Locale.getDefault();
        TimeZone timeZone = TimeZone.getDefault();

        // Convert the timestamp from milliseconds to a human-readable date format
        Date date = new Date(timestamp);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a", currentLocale);

        // Set the time zone based on the system's default time zone
        format.setTimeZone(timeZone);

        return format.format(date);
    }

    static String formatDuration(long milliseconds) {
        int seconds = (int) (milliseconds / 1000);
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        if (hours > 0) {
            return remainingSeconds > 0
                    ? String.format("%dh %dm %ds", hours, minutes, remainingSeconds)
                    : String.format("%dh %dm", hours, minutes);
        } else {
            return remainingSeconds > 0
                    ? String.format("%dm %ds", minutes, remainingSeconds)
                    : String.format("%ds", minutes);
        }
    }
}
/*
public final String SOCIAL_MEDIA_WHATSAPP_CALLS_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_WHATSAPP_MESSAGES_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_WHATSAPP_CONTACTS_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_INSTAGRAM_CALLS_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_INSTAGRAM_MESSAGES_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_INSTAGRAM_CONTACTS_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_SNAPCHAT_CALLS_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_SNAPCHAT_MESSAGES_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_SNAPCHAT_CONTACTS_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_TWITTER_CALLS_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_TWITTER_MESSAGES_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_TWITTER_CONTACTS_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_TELEGRAM_CALLS_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_TELEGRAM_MESSAGES_URL = URL_DOMAIN + "";
public final String SOCIAL_MEDIA_TELEGRAM_CONTACTS_URL = URL_DOMAIN + "";*/
