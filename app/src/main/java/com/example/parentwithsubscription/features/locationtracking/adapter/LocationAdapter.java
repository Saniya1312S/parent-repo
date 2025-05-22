package com.example.parentwithsubscription.features.locationtracking.adapter;

import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.locationtracking.model.LocationData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<LocationData> locationList;

    private String formattedFromTime, formattedToTime;
    public LocationAdapter(List<LocationData> locationList) {
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_safe_location_item, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationData locationData = locationList.get(position);

        // Access location and other details from the LocationData object
        LocationData.Location location = locationData.getLocation();
        holder.locationAddress.setText(location.getAddress());

        try {
             formattedFromTime = URIConstants.formatTimestamp(locationData.getFromTime());
        } catch (NumberFormatException e) {
            formattedFromTime  = String.valueOf(locationData.getFromTime());
        }


        try {
            formattedToTime = URIConstants.formatTimestamp(locationData.getToTime());
        } catch (NumberFormatException e) {
            formattedToTime  = String.valueOf(locationData.getToTime());
        }
        holder.locationTimestamp.setText("From: " + formattedFromTime + " To: " + formattedToTime + "\n" + "Duration: " + URIConstants.formatDuration(locationData.getDuration()*1000));

//        holder.locationTimestamp.setText(formatTimestamp(locationData.getFromTime(), locationData.getToTime()) + "\n" + "Duration: " + formatDuration(locationData.getDuration()));

// Check if the location is inside or outside for the switch state
        boolean isInside = "inside".equals(locationData.getGeofence()); // Fixed the condition here
        holder.geofenceSwitch.setChecked(isInside); // Set the switch state based on the geofence status

        // Update switch color based on the initial state (inside or outside)
        updateSwitchColor(holder, isInside);

        // Add listener for switch toggle
        holder.geofenceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the geofence status based on the switch state
            if (isChecked) {
                locationData.setGeofence("inside");
                updateSwitchColor(holder, true); // Default color for "Inside"
            } else {
                locationData.setGeofence("outside");
                updateSwitchColor(holder, false); // Red color for "Outside"
            }
        });
    }

    private void updateSwitchColor(LocationViewHolder holder, boolean isInside) {
        if (!isInside) {
            // When OFF (outside), set the color to red.
            holder.geofenceSwitch.setThumbTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.red)));
            holder.geofenceSwitch.setTrackTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.red)));
        } else {
            // When ON (inside), set the color to the default system color (gray).
            holder.geofenceSwitch.setThumbTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray)));
            holder.geofenceSwitch.setTrackTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray)));
        }
    }

    /*private String formatTimestamp(long fromTime, long toTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss a");
        Date startDate = new Date(fromTime);
        Date endDate = new Date(toTime);
        return "From: " + sdf.format(startDate) + " To: " + sdf.format(endDate);
    }

    private String formatDuration(long durationInSeconds) {
        long hours = durationInSeconds / 3600;
        long minutes = (durationInSeconds % 3600) / 60;
        long seconds = durationInSeconds % 60;

        StringBuilder durationString = new StringBuilder();

        // Append hours if greater than 0
        if (hours > 0) {
            durationString.append(hours).append("h ");
        }

        // Append minutes if greater than 0
        if (minutes > 0) {
            durationString.append(minutes).append("m ");
        }

        // Append seconds if greater than 0
        if (seconds > 0) {
            durationString.append(seconds).append("s");
        }

        return durationString.toString().trim();
    }
*/
    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView locationAddress, locationTimestamp;
        Switch geofenceSwitch;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationAddress = itemView.findViewById(R.id.locationAddress);
            locationTimestamp = itemView.findViewById(R.id.locationTimestamp);
            geofenceSwitch = itemView.findViewById(R.id.geofenceSwitch);
        }
    }
}







/*
package com.example.parent.features.locationtracking.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.locationtracking.model.GeofenceData;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<GeofenceData> locationList;

    public LocationAdapter(List<GeofenceData> locationList) {
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_safe_location_item, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        GeofenceData location = locationList.get(position);

        holder.locationAddress.setText(location.getAddress());
        holder.locationTimestamp.setText(location.getTimestamp());

        boolean isInside = location.getGeofence().equals("inside");
        holder.geofenceSwitch.setChecked(isInside);

        // Update switch color
        updateSwitchColor(holder, isInside);

        // Add listener for switch toggle
        holder.geofenceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the geofence status based on the switch state
            if (isChecked) {
                location.setGeofence("inside");
                updateSwitchColor(holder, true); // Default color for "Inside"
            } else {
                location.setGeofence("outside");
                updateSwitchColor(holder, false); // Red color for "Outside"
            }
        });
    }

    private void updateSwitchColor(LocationViewHolder holder, boolean isInside) {
        if (!isInside) {
            // When OFF (outside), set the color to red.
            holder.geofenceSwitch.setThumbTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.red)));
            holder.geofenceSwitch.setTrackTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.red)));
        } else {
            // When ON (inside), set the color to the default system color (gray).
            holder.geofenceSwitch.setThumbTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray)));
            holder.geofenceSwitch.setTrackTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray)));
        }
    }


    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {

        TextView locationAddress, locationTimestamp;
        Switch geofenceSwitch;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationAddress = itemView.findViewById(R.id.locationAddress);
            locationTimestamp = itemView.findViewById(R.id.locationTimestamp);
            geofenceSwitch = itemView.findViewById(R.id.geofenceSwitch);
        }
    }
}
*/
