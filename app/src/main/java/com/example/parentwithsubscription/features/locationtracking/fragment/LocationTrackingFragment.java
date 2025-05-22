package com.example.parentwithsubscription.features.locationtracking.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.authentication.activity.SubscriptionOptionsActivity;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.appusage.fragment.AppUsageFragment;
import com.example.parentwithsubscription.features.calllogs.fragment.CallLogsFragment;
import com.example.parentwithsubscription.features.contacts.fragment.PhoneContactsFragment;
import com.example.parentwithsubscription.features.locationtracking.model.LocationData;
import com.example.parentwithsubscription.features.smslogs.fragment.SMSLogsFragment;
import com.example.parentwithsubscription.features.socialmedia.fragment.SocialMediaFragment;
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
import java.security.GeneralSecurityException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationTrackingFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng locationToUpdate;
    private String locationAddress;
    private TextView locationTimeSpentTextView, recentLocation;
    private LinearLayout gpsCard;

    private final String LOCATION_HISTORY_URL = URIConstants.LOCATION_HISTORY_URL;  // Replace with actual URL

    public LocationTrackingFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_location_tracking, container, false);

        Button subscribeBtn = view.findViewById(R.id.subscribe_button);
        LinearLayout subscriptionInfoLayout = view.findViewById(R.id.subscription_info_layout);
        ConstraintLayout constraintLayout = view.findViewById(R.id.location_tracking_root); // Root ConstraintLayout

        try {
            SharedPreferences prefs = getEncryptedPrefs();
            String userRole = prefs.getString("user_role", null);

            Log.d("LocationTrackingFragment", "Read user_role: " + userRole);

            // Apply layout constraint changes based on role
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            if ("GUEST".equalsIgnoreCase(userRole)) {
                constraintSet.setDimensionRatio(R.id.map, "H,13:10");
                constraintSet.applyTo(constraintLayout);
                subscriptionInfoLayout.setVisibility(View.VISIBLE);

                subscribeBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(requireContext(), SubscriptionOptionsActivity.class);
                    startActivity(intent);
                });
            } else {
                constraintSet.setDimensionRatio(R.id.map, "H,11:10");
                constraintSet.applyTo(constraintLayout);
                subscriptionInfoLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrefsError", "Failed to load prefs", e);
            subscriptionInfoLayout.setVisibility(View.GONE); // fallback
        }

        // Initialize views
        locationTimeSpentTextView = view.findViewById(R.id.location_time_spent);
        recentLocation = view.findViewById(R.id.recent_location);
        gpsCard = view.findViewById(R.id.gps_card);

        gpsCard.setOnClickListener(v -> replaceFragment(new LocationHistoryFragment()));

        // Load the map
        if (savedInstanceState == null) {
            SupportMapFragment mapFragment = new SupportMapFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map, mapFragment)
                    .commit();
            mapFragment.getMapAsync(this);
        }

        setupNavigationCards(view);
        loadLocationDataFromAPI();

        return view;
    }

    private SharedPreferences getEncryptedPrefs() throws GeneralSecurityException, IOException {
        String userPrefsFile = "guest_user_data";
        MasterKey masterKey = new MasterKey.Builder(requireContext())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                requireContext(),
                userPrefsFile,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    private void setupNavigationCards(View view) {
        // Setting onClickListeners for the navigation cards
        view.findViewById(R.id.gps_card).setOnClickListener(v -> {
            replaceFragment(new LocationHistoryFragment());
        });

        view.findViewById(R.id.call_logs_card).setOnClickListener(v -> {
            replaceFragment(new CallLogsFragment());
        });

       view.findViewById(R.id.message_logs_card).setOnClickListener(v -> {
            replaceFragment(new SMSLogsFragment());
        });

       view.findViewById(R.id.app_usage_card).setOnClickListener(v -> {
            replaceFragment(new AppUsageFragment());
        });

      view.findViewById(R.id.social_media_card).setOnClickListener(v -> {
            replaceFragment(new SocialMediaFragment());
        });

        view.findViewById(R.id.contacts_card).setOnClickListener(v -> {
            replaceFragment(new PhoneContactsFragment());
        });
    }

    private void replaceFragment(Fragment fragment) {
        // Get the FragmentManager and start a transaction
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // Replace the current fragment with the new one
        transaction.replace(R.id.location_tracking_fragment_container, fragment);

        // Optionally add this transaction to the back stack so the user can go back to the previous fragment
        transaction.addToBackStack(null);  // null means no custom name for the back stack entry

        // Commit the transaction
        transaction.commit();
    }

    private void loadLocationDataFromAPI() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(LOCATION_HISTORY_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LocationTracking", "Error fetching location data", e);
                // Ensure fragment is attached before updating the UI
                if (isAdded() && getContext() != null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to fetch location data", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<LocationData>>() {}.getType();
                    List<LocationData> locationDataList = gson.fromJson(responseString, listType);

                    if (!locationDataList.isEmpty()) {
                        LocationData recentLocationData = locationDataList.get(0);
                        locationAddress = recentLocationData.getLocation().getAddress();
                        locationToUpdate = new LatLng(
                                recentLocationData.getLocation().getLatitude(),
                                recentLocationData.getLocation().getLongitude()
                        );

                        // Ensure fragment is attached before updating the UI
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                recentLocation.setText("Recent Location: " + locationAddress);
                                if (mMap != null) {
                                    updateMapWithLocation(locationAddress, locationToUpdate.latitude, locationToUpdate.longitude, "Timestamp here");
                                }
                            });

                            // Calculate most time spent
                            long maxDuration = 0;
                            String mostTimeSpentAddress = "";
                            for (LocationData data : locationDataList) {
                                if (data.getDuration() > maxDuration) {
                                    maxDuration = data.getDuration();
                                    mostTimeSpentAddress = data.getLocation().getAddress();
                                }
                            }

                            String finalMostTimeSpentAddress = mostTimeSpentAddress;
                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> locationTimeSpentTextView.setText("Most Time Spent: " + finalMostTimeSpentAddress));
                            }
                        }
                    }
                }
            }
        });
    }

    private void updateMapWithLocation(String address, double latitude, double longitude, String timestamp) {
        if (mMap == null) return;

        mMap.clear();
        LatLng location = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title("Recent Location")
                .snippet(address + "\nTime: " + timestamp);

        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_info_window, null);
                ((TextView) view.findViewById(R.id.info_window_title)).setText(marker.getTitle());
                ((TextView) view.findViewById(R.id.info_window_address)).setText(marker.getSnippet().split("\n")[0]);
                ((TextView) view.findViewById(R.id.info_window_time)).setText(marker.getSnippet().split("\n")[1]);
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if (locationToUpdate != null && locationAddress != null) {
            updateMapWithLocation(locationAddress, locationToUpdate.latitude, locationToUpdate.longitude, "Timestamp here");
        }
    }
}









/*
package com.example.parentwithsubscription.features.locationtracking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.appusage.activity.AppUsageActivity;
import com.example.parentwithsubscription.features.calllogs.activity.CallLogsActivity;
import com.example.parentwithsubscription.features.contacts.activity.PhoneContactsActivity;
import com.example.parentwithsubscription.features.locationtracking.activity.AddOrRemoveSafeLocationActivity;
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

public class LocationTrackingFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng locationToUpdate;
    private String locationAddress;
    private TextView locationTimeSpentTextView, recentLocation;
    private LinearLayout gpsCard;

    private final String LOCATION_HISTORY_URL = URIConstants.LOCATION_HISTORY_URL;

    public LocationTrackingFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_location_tracking, container, false);

        // Load the map dynamically using child fragment manager
        if (savedInstanceState == null) {
            SupportMapFragment mapFragment = new SupportMapFragment();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map, mapFragment)
                    .commit();
            mapFragment.getMapAsync(this); // Important
        }

        // Initialize views
        locationTimeSpentTextView = view.findViewById(R.id.location_time_spent);
        recentLocation = view.findViewById(R.id.recent_location);
        gpsCard = view.findViewById(R.id.gps_card); // Make sure gps_card exists in layout

        gpsCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Safe location clicked", Toast.LENGTH_SHORT).show();
        });

        setupNavigationCards(view);
        loadLocationDataFromAPI();

        return view;
    }

    private void setupNavigationCards(View view) {
        // Check if context is available before proceeding
        if (getContext() == null) return;

        // Setting onClickListeners for the navigation cards
        view.findViewById(R.id.gps_card).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddOrRemoveSafeLocationActivity.class));
        });

        // Setting onClickListeners for the navigation cards
        view.findViewById(R.id.call_logs_card).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CallLogsActivity.class));
        });

        view.findViewById(R.id.message_logs_card).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SMSLogsActivity.class));
        });

        view.findViewById(R.id.app_usage_card).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AppUsageActivity.class));
        });

        view.findViewById(R.id.social_media_card).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SocialMediaActivity.class));
        });

        view.findViewById(R.id.contacts_card).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), PhoneContactsActivity.class));
        });
    }
    private void loadLocationDataFromAPI() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(LOCATION_HISTORY_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LocationTracking", "Error fetching location data", e);
                // Ensure fragment is attached before updating the UI
                if (isAdded() && getContext() != null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to fetch location data", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<LocationData>>() {}.getType();
                    List<LocationData> locationDataList = gson.fromJson(responseString, listType);

                    if (!locationDataList.isEmpty()) {
                        LocationData recentLocationData = locationDataList.get(0);
                        locationAddress = recentLocationData.getLocation().getAddress();
                        locationToUpdate = new LatLng(
                                recentLocationData.getLocation().getLatitude(),
                                recentLocationData.getLocation().getLongitude()
                        );

                        // Ensure fragment is attached before updating the UI
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                recentLocation.setText("Recent Location: " + locationAddress);
                                if (mMap != null) {
                                    updateMapWithLocation(locationAddress, locationToUpdate.latitude, locationToUpdate.longitude, URIConstants.formatTimestamp(recentLocationData.getFromTime()));
                                }
                            });

                            // Calculate most time spent
                            long maxDuration = 0;
                            String mostTimeSpentAddress = "";
                            for (LocationData data : locationDataList) {
                                if (data.getDuration() > maxDuration) {
                                    maxDuration = data.getDuration();
                                    mostTimeSpentAddress = data.getLocation().getAddress();
                                }
                            }

                            String finalMostTimeSpentAddress = mostTimeSpentAddress;
                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> locationTimeSpentTextView.setText("Most Time Spent: " + finalMostTimeSpentAddress));
                            }
                        }
                    }
                }
            }
        });
    }

    private void updateMapWithLocation(String address, double latitude, double longitude, String timestamp) {
        if (mMap == null) return;

        mMap.clear();
        LatLng location = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title("Recent Location")
                .snippet(address + "\nTime: " + timestamp);

        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_info_window, null);
                ((TextView) view.findViewById(R.id.info_window_title)).setText(marker.getTitle());
                ((TextView) view.findViewById(R.id.info_window_address)).setText(marker.getSnippet().split("\n")[0]);
                ((TextView) view.findViewById(R.id.info_window_time)).setText(marker.getSnippet().split("\n")[1]);
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if (locationToUpdate != null && locationAddress != null) {
            updateMapWithLocation(locationAddress, locationToUpdate.latitude, locationToUpdate.longitude, "Timestamp here");
        }
    }
}
*/
