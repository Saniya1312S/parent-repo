package com.example.parentwithsubscription.features.locationtracking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.appusage.activity.AppUsageActivity;
import com.example.parentwithsubscription.features.calllogs.activity.CallLogsActivity;
import com.example.parentwithsubscription.features.contacts.activity.PhoneContactsActivity;
import com.example.parentwithsubscription.features.locationtracking.model.LocationData;
import com.example.parentwithsubscription.features.smslogs.activity.SMSLogsActivity;
import com.example.parentwithsubscription.features.socialmedia.activity.SocialMediaActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng locationToUpdate;
    private String locationAddress;
    private DrawerLayout drawerLayout;
    private ImageView profileIcon;
    // TextView to display the most time spent location
    private TextView locationTimeSpentTextView, recentLocation;

    private LinearLayout addRemoveSafeLocation;
    // Static location for the most-time-spent place
    private String mostTimeSpentLocationAddress;

    private String LOCATION_HISTORY_URL = URIConstants.LOCATION_HISTORY_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracking);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        // Initialize the TextView to show the most-time-spent location
        locationTimeSpentTextView = findViewById(R.id.location_time_spent);
        recentLocation = findViewById(R.id.recent_location);
        addRemoveSafeLocation = findViewById(R.id.gps_card);

        // Load location data from the API
        loadLocationDataFromAPI();

        // Find cards by ID and set click listeners (same as before)
        View callLogsCard = findViewById(R.id.call_logs_card);
        View smsLogsCard = findViewById(R.id.message_logs_card);
        View appUsageCard = findViewById(R.id.app_usage_card);
        View socialMediaCard = findViewById(R.id.social_media_card);
        View contactsCard = findViewById(R.id.contacts_card);

        callLogsCard.setOnClickListener(v -> {
            // Navigate to CallLogsActivity
            Intent intent = new Intent(LocationTrackingActivity.this, CallLogsActivity.class);
            startActivity(intent);
        });

        smsLogsCard.setOnClickListener(v -> {
            // Navigate to MessageLogsActivity
            Intent intent = new Intent(LocationTrackingActivity.this, SMSLogsActivity.class);
            startActivity(intent);
        });

        appUsageCard.setOnClickListener(v -> {
            // Navigate to AppUsageActivity
            Intent intent = new Intent(LocationTrackingActivity.this, AppUsageActivity.class);
            startActivity(intent);
        });

        socialMediaCard.setOnClickListener(v -> {
            // Navigate to SocialMediaActivity
            Intent intent = new Intent(LocationTrackingActivity.this, SocialMediaActivity.class);
            startActivity(intent);
        });

        contactsCard.setOnClickListener(v -> {
            // Navigate to PhoneContactsActivity
            Intent intent = new Intent(LocationTrackingActivity.this, PhoneContactsActivity.class);
            startActivity(intent);
        });

        addRemoveSafeLocation.setOnClickListener(v -> {
            // Navigate to AddOrRemoveSafeLocationActivity
            Intent intent = new Intent(LocationTrackingActivity.this, AddOrRemoveSafeLocationActivity.class);
            startActivity(intent);
        });
    }

    private void loadLocationDataFromAPI() {
        OkHttpClient client = new OkHttpClient();

        // Build the request to fetch data from LOCATION_HISTORY_URL
        Request request = new Request.Builder()
                .url(LOCATION_HISTORY_URL)
                .build();

        // Enqueue the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LocationTracking", "Error fetching location data", e);
                runOnUiThread(() -> Toast.makeText(LocationTrackingActivity.this, "Failed to fetch location data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // Get the JSON response as a string
                        assert response.body() != null;
                        String responseString = response.body().string();
                        Log.d("Raw JSON", responseString);


                        // Use Gson to convert the JSON string into a list of LocationData objects
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<LocationData>>(){}.getType();
                        List<LocationData> locationDataList = gson.fromJson(responseString, listType);

                        Log.d("Location data from api", locationDataList.toString());

                        // Extract the first location (Recent Location)
                        LocationData recentLocationData = locationDataList.get(0);
                        String recentAddress = recentLocationData.getLocation().getAddress();
                        double recentLatitude = recentLocationData.getLocation().getLatitude();
                        double recentLongitude = recentLocationData.getLocation().getLongitude();
                        long recentFromTime = recentLocationData.getFromTime();
                        long recentToTime = recentLocationData.getToTime();

                        // Set the recent location on the map (do this on the main thread)
                        locationAddress = recentAddress;
                        locationToUpdate = new LatLng(recentLatitude, recentLongitude);
                        runOnUiThread(() -> {
                            if (mMap != null) {
                                updateMapWithLocation(recentAddress, recentLatitude, recentLongitude, URIConstants.formatTimestamp(recentFromTime));
                            }
                        });

                        // Update the UI on the main thread
                        runOnUiThread(() -> {
                            if (recentLocationData != null) {
                                recentLocation.setText("Recent Location: " + recentAddress);
                            }
                        });

                        // Now find the location with the highest duration (Most Time Spent Location)
                        LocationData mostTimeSpentLocation = null;
                        long maxDuration = 0;

                        for (LocationData locationData : locationDataList) {
                            long duration = locationData.getDuration();

                            // Check if the current location has the highest duration
                            if (duration > maxDuration) {
                                maxDuration = duration;
                                mostTimeSpentLocation = locationData;
                            }
                        }

                        if (mostTimeSpentLocation != null) {
                            // Get the address for the most-time-spent location
                            String mostTimeSpentAddress = mostTimeSpentLocation.getLocation().getAddress();

                            // Update UI with the most-time-spent location address (on the main thread)
                            mostTimeSpentLocationAddress = mostTimeSpentAddress;
                            runOnUiThread(() -> {
                                if (locationTimeSpentTextView != null) {
                                    locationTimeSpentTextView.setText("Most Time Spent: " + mostTimeSpentLocationAddress);
                                }
                            });
                        }

                    } catch (Exception e) {
                        Log.e("LocationTracking", "Error parsing location history API response", e);
                        runOnUiThread(() -> Toast.makeText(LocationTrackingActivity.this, "Failed to load location data", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void updateMapWithLocation(String address, double latitude, double longitude, String timestamp) {
        if (mMap == null) {
            return;
        }

        // Clear any existing markers on the map
        mMap.clear();

        // Create a LatLng object for the static location
        LatLng location = new LatLng(latitude, longitude);

        Log.d("Latitude Longitude", "Latitude: " + String.valueOf(latitude) + ", Longitude: "+ String.valueOf(longitude));
        // Add a marker for the static location
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title("Recent Location")  // This will be displayed as the title above the address
                .snippet(address + "\nTime: " + timestamp);  // This will show the address and timestamp in the info window

        recentLocation.setText("Recent Location: " + address);
        Marker marker = mMap.addMarker(markerOptions);

        // Move the camera to the static location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

        // Set custom info window adapter
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                TextView titleTextView = view.findViewById(R.id.info_window_title);
                TextView addressTextView = view.findViewById(R.id.info_window_address);
                TextView timeTextView = view.findViewById(R.id.info_window_time);

                // Set title, address, and time on the custom info window
                titleTextView.setText("Recent Location");
                addressTextView.setText(marker.getSnippet().split("\n")[0]);  // First part: address
                timeTextView.setText(marker.getSnippet().split("\n")[1]);  // Second part: time

                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;  // Return null to use the default contents
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Log to confirm map is ready
        Log.d("LocationTracking", "Map is ready.");

        // If the location was already fetched, update the map now
        if (locationToUpdate != null && locationAddress != null) {
            String timestamp = "2025-02-03 12:34:56";  // You should ideally get this timestamp dynamically
            updateMapWithLocation(locationAddress, locationToUpdate.latitude, locationToUpdate.longitude, timestamp);  // Pass the timestamp
        }
    }
}