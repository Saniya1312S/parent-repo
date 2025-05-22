package com.example.parentwithsubscription.features.appusage.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.appusage.model.AppUsage;

import java.util.List;
import java.util.Random;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.ViewHolder> {
    private Context context;
    private List<AppUsage> appUsageList;

    // Max usage time (24 hours in seconds)
    private static final int MAX_USAGE_TIME = 86400;

    public AppUsageAdapter(Context context, List<AppUsage> appUsageList) {
        this.context = context;
        this.appUsageList = appUsageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.app_usage_item, parent, false);
        return new ViewHolder(view);
    }
/*

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppUsage appUsage = appUsageList.get(position);

        // Set the app name and usage time
        holder.appName.setText(appUsage.getAppName());
        holder.usageTime.setText(formatTime(Integer.parseInt(appUsage.getUsageTime())));

        // Get the app icon using the package name and set it to the ImageView
        Drawable appIcon = getAppIcon(appUsage.getPackageName());
        holder.appIcon.setImageDrawable(appIcon);

        // Set the max value for progress bar as MAX_USAGE_TIME (86,400 seconds)
        holder.usageProgress.setMax(MAX_USAGE_TIME);
        holder.usageProgress.setProgress(Integer.parseInt(appUsage.getUsageTime()));

        // Set a random progress tint color
        int randomColor = generateRandomColor();
        holder.usageProgress.setProgressTintList(ColorStateList.valueOf(randomColor));
    }
*/
/*
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppUsage appUsage = appUsageList.get(position);


        Log.d("app usage data", appUsage.getUsage().toString());
        // Set the app name and usage time
        holder.appName.setText(appUsage.getAppName());
        holder.usageTime.setText(formatTime(Integer.parseInt(appUsage.getUsageTime())));

        // Get the app icon
        Drawable appIcon = getAppIcon(appUsage.getPackageName());
        holder.appIcon.setImageDrawable(appIcon);

        // Set progress bar values
        holder.usageProgress.setMax(MAX_USAGE_TIME);
        int usageTime = Integer.parseInt(appUsage.getUsageTime());
        holder.usageProgress.setProgress(usageTime);

        // Set a random progress color
        int randomColor = generateRandomColor();
        holder.usageProgress.setProgressTintList(ColorStateList.valueOf(randomColor));

        // Handle progress bar click to show the graph
        holder.usageProgress.setOnClickListener(v -> {
            Intent intent = new Intent(context, LineGraphActivity.class);
            intent.putExtra("appName", appUsage.getAppName());
            intent.putExtra("usageTime", usageTime);
            intent.putExtra("progressColor", randomColor);
            context.startActivity(intent);
        });
    }*/

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppUsage appUsage = appUsageList.get(position);

        Log.d("app usage data", appUsage.getAppName());
        // Set the app name and usage time
        holder.appName.setText(appUsage.getAppName());
//        holder.usageTime.setText(formatTime(Integer.parseInt(appUsage.getUsageTime())));

        try {
            Long timestamp = Long.parseLong(appUsage.getUsageTime());
            String formattedDuration = URIConstants.formatDuration(timestamp*1000);
            holder.usageTime.setText(formattedDuration);
        } catch (NumberFormatException e) {
            holder.usageTime.setText(appUsage.getUsageTime());
        }
        // Get the app icon
        Drawable appIcon = getAppIcon(appUsage.getPackageName());
        holder.appIcon.setImageDrawable(appIcon);

        // Set progress bar values
        holder.usageProgress.setMax(MAX_USAGE_TIME);
        int usageTime = Integer.parseInt(appUsage.getUsageTime());
        holder.usageProgress.setProgress(usageTime);

        // Set a random progress color
        int randomColor = generateRandomColor();
        holder.usageProgress.setProgressTintList(ColorStateList.valueOf(randomColor));

        // Handle progress bar click to show the graph
        /*holder.usageProgress.setOnClickListener(v -> {
            // Convert your sessions into JSON
            Gson gson = new Gson();
            String jsonData = gson.toJson(appUsage.getUsage());  // Send the actual usage data here
            Log.d("Usage JSON", jsonData);  // Ensure the format is correct

            Intent intent = new Intent(context, LineGraphActivity.class);
            intent.putExtra("appName", appUsage.getAppName());
            intent.putExtra("usageTime", usageTime);
            intent.putExtra("progressColor", randomColor);
            intent.putExtra("usageJson", jsonData);  // Send the JSON data to LineGraphActivity
            context.startActivity(intent);
        });*/
    }

    @Override
    public int getItemCount() {
        return appUsageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName, usageTime;
        ProgressBar usageProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the views in each item layout
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            usageTime = itemView.findViewById(R.id.usageTime);
            usageProgress = itemView.findViewById(R.id.usageProgress);
        }
    }

    // Helper method to format time in seconds to minutes and seconds
/*    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (seconds > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else {
            return String.format("%dh %dm", hours, minutes);
        }
    }*/

    // Helper method to generate a random color
    private int generateRandomColor() {
        Random random = new Random();
        // Random RGB values
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        // Return the color in ARGB format
        return Color.rgb(red, green, blue);
    }
    // Helper method to retrieve app icons by package name
    private Drawable getAppIcon(String packageName) {
        try {
            // Check for specific package names and return custom icons
            switch (packageName) {
                case "com.whatsapp":
                    return ContextCompat.getDrawable(context, R.drawable.whatsapp_icon); // WhatsApp
                case "com.samsung.android.incallui":
                    return ContextCompat.getDrawable(context, R.drawable.phone_icon); // Call (using a default icon)
                case "com.snapchat.android":
                    return ContextCompat.getDrawable(context, R.drawable.snapchat_icon); // Snapchat
                case "com.google.android.youtube":
                    return ContextCompat.getDrawable(context, R.drawable.youtube_icon); // YouTube
                case "com.rapido.passenger":
                    return ContextCompat.getDrawable(context, R.drawable.rapido_icon); // Rapido (using a default icon)
                case "com.instagram.android":
                    return ContextCompat.getDrawable(context, R.drawable.instagram_icon); // Instagram
                case "com.facebook.katana":
                    return ContextCompat.getDrawable(context, R.drawable.facebook_icon); // Facebook
                case "com.twitter.android":
                    return ContextCompat.getDrawable(context, R.drawable.twitter_icon); // Twitter
                case "com.spotify.music":
                    return ContextCompat.getDrawable(context, R.drawable.spotify_icon); // Spotify
/*                case "com.zhiliaoapp.musically":
                    return ContextCompat.getDrawable(context, R.drawable.tiktok_icon); // TikTok
                case "com.google.android.apps.maps":
                    return ContextCompat.getDrawable(context, R.drawable.google_maps_icon); // Google Maps
                // Add more custom app icons here as needed*/
                default:
                    // If not a special case, return the app's actual icon
                    return context.getPackageManager().getApplicationIcon(packageName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // In case of an error (e.g., app not found), return a default icon
            return ContextCompat.getDrawable(context, android.R.drawable.sym_def_app_icon); // Default icon if the app is not found
        }
    }


}










/*
package com.example.parent.features.appusage.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.parent.R;
import com.example.parent.features.appusage.model.AppUsageData;

import java.util.List;

public class AppUsageAdapter extends BaseAdapter {

    private Context context;
    private List<AppUsageData> appDataList;
    private PackageManager packageManager;
    private LayoutInflater inflater;

    public AppUsageAdapter(Context context, List<AppUsageData> appDataList, PackageManager packageManager) {
        this.context = context;
        this.appDataList = appDataList;
        this.packageManager = packageManager;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return appDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return appDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.app_usage_item, parent, false);
        }

        ImageView appIcon = convertView.findViewById(R.id.appIcon);
        TextView appName = convertView.findViewById(R.id.appName);
        TextView usageTime = convertView.findViewById(R.id.usageTime);

        AppUsageData appData = appDataList.get(position);

        // Get the app name and package name from the app data object
        String appNameStr = appData.getApp_name();
        String packageName = appData.getPackage_name();
        String usageTimeStr = appData.getUsage_time();

        // Set the app name and usage time to the TextViews
        appName.setText(appNameStr);
        usageTime.setText(usageTimeStr);

        // Set the app icon by calling the getAppIcon method
        setAppIcon(appIcon, packageName);

        return convertView;
    }

    // Function to retrieve the app icon based on the package name
    private void setAppIcon(ImageView imageView, String packageName) {
        try {
            // Use the custom method to get the app icon
            Drawable appIcon = getAppIcon(packageName);

            // Set the app icon to the ImageView
            imageView.setImageDrawable(appIcon);

        } catch (Exception e) {
            // Log and handle error if app icon retrieval fails
            Log.e("setAppIcon", "Error retrieving app icon: " + packageName);
            imageView.setImageResource(R.drawable.ic_default_app);  // Fallback to default icon
        }
    }

    // Function to retrieve the app icon based on the package name
    private Drawable getAppIcon(String packageName) {
        try {
            // Check for specific package names and return custom icons
            switch (packageName) {
                case "com.whatsapp":
                    return ContextCompat.getDrawable(context, R.drawable.whatsapp_icon); // WhatsApp
                case "com.samsung.android.incallui":
                    return ContextCompat.getDrawable(context, R.drawable.phone_icon); // Call (using a default icon)
                case "com.snapchat.android":
                    return ContextCompat.getDrawable(context, R.drawable.snapchat_icon); // Snapchat
                case "com.google.android.youtube":
                    return ContextCompat.getDrawable(context, R.drawable.youtube_icon); // YouTube
                case "com.rapido.passenger":
                    return ContextCompat.getDrawable(context, R.drawable.rapido_icon); // Rapido (using a default icon)
                case "com.instagram.android":
                    return ContextCompat.getDrawable(context, R.drawable.instagram_icon); // Instagram
                // Add more custom app icons here as needed
                default:
                    // If not a special case, return the app's actual icon
                    return context.getPackageManager().getApplicationIcon(packageName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // In case of an error (e.g., app not found), return a default icon
            return ContextCompat.getDrawable(context, android.R.drawable.sym_def_app_icon); // Default icon if the app is not found
        }
    }
}
*/
