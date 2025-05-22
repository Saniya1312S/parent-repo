package com.example.parentwithsubscription.features.socialmedia.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.socialmedia.model.SocialMediaData;

import java.util.List;

public class SocialMediaAdapter extends RecyclerView.Adapter<SocialMediaAdapter.NotificationViewHolder> {

    private List<SocialMediaData> notificationList;
    private Context context; // Store the context to access resources

    // Constructor now accepts a Context as well
    public SocialMediaAdapter(List<SocialMediaData> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context; // Initialize the context
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_media_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        SocialMediaData notification = notificationList.get(position);

        // Set the sender name and message
        holder.senderNameTextView.setText(notification.getSenderName() + ": ");
        holder.messageTextView.setText(notification.getMessage());

        // Get the app icon using package name and set it
        String packageName = notification.getPackageName(); // Correctly use the package name
        Drawable appIcon = getAppIcon(packageName);
        holder.appIconImageView.setImageDrawable(appIcon);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    // ViewHolder to hold the views for each item
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        public TextView senderNameTextView;
        public TextView messageTextView;
        public ImageView appIconImageView;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            appIconImageView = itemView.findViewById(R.id.appIconImageView);
        }
    }

    // Function to retrieve the app icon based on the package name
    private Drawable getAppIcon(String packageName) {
        try {
            // Check for specific package names and return custom icons
            switch (packageName) {
                case "com.whatsapp":
                    return ContextCompat.getDrawable(context, R.drawable.whatsapp_icon); // WhatsApp
                case "com.snapchat.android":
                    return ContextCompat.getDrawable(context, R.drawable.snapchat_icon); // Snapchat
                case "com.instagram.android":
                    return ContextCompat.getDrawable(context, R.drawable.instagram_icon); // Instagram
                case "com.google.android.youtube":
                    return ContextCompat.getDrawable(context, R.drawable.youtube_icon); // YouTube
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









/*
package com.example.parentapplocation;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SocialMediaAdapter extends RecyclerView.Adapter<SocialMediaAdapter.NotificationViewHolder> {

    private List<SocialMediaData> notificationList;

    public SocialMediaAdapter(List<SocialMediaData> notificationList) {
        this.notificationList = notificationList;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_media_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        SocialMediaData notification = notificationList.get(position);

        // Set the sender name and message
        holder.senderNameTextView.setText(notification.getSenderName() + ": ");
        holder.messageTextView.setText(notification.getMessage());

        // Set the app icon based on the package name (app name in this case)
        String packageName = notification.getAppName(); // package name is the app name here
        setAppIcon(holder.appIconImageView, packageName);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    // ViewHolder to hold the views for each item
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        public TextView senderNameTextView;
        public TextView messageTextView;
        public ImageView appIconImageView;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            appIconImageView = itemView.findViewById(R.id.appIconImageView);
        }
    }

    // Function to set the app icon based on the package name
    private void setAppIcon(ImageView imageView, String packageName) {
        try {
            // Use PackageManager to get app icon based on package name
            PackageManager packageManager = imageView.getContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            Drawable appIcon = packageManager.getApplicationIcon(applicationInfo);

            // Set the app icon to ImageView
            imageView.setImageDrawable(appIcon);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // Handle error if app is not found (you can set a default icon here)
        }
    }
}*/
