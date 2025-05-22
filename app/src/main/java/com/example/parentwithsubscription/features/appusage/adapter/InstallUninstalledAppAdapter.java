package com.example.parentwithsubscription.features.appusage.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.appusage.model.InstallUninstalledApp;

import java.util.List;

public class InstallUninstalledAppAdapter extends RecyclerView.Adapter<InstallUninstalledAppAdapter.ViewHolder> {

    private List<InstallUninstalledApp> appList;
    private Context context;

    public InstallUninstalledAppAdapter(Context context, List<InstallUninstalledApp> appList) {
        this.context = context;
        this.appList = appList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.install_uninstall_app_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InstallUninstalledApp app = appList.get(position);

        // Set app icon
        Drawable appIcon = getAppIcon(app.getPackageName());
        holder.appIcon.setImageDrawable(appIcon);

        // Set app name
        holder.appName.setText(app.getAppName());

        try {
            long timestamp = app.getTime(); // Assuming call.getTime() is a string representing milliseconds
            // Format the timestamp using the formatTimestamp method
            String formattedTime = URIConstants.formatTimestamp(timestamp);
            holder.appTime.setText(formattedTime); // Set the formatted time
            // Log the formatted timestamp for debugging
            Log.d("Time formatted", formattedTime);
        } catch (NumberFormatException e) {
            holder.appTime.setText(String.valueOf(app.getTime())); // Fallback to the original time if parsing fails
        }
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appTime;

        public ViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appTime = itemView.findViewById(R.id.app_time);
        }
    }

    // Helper method to retrieve app icons
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