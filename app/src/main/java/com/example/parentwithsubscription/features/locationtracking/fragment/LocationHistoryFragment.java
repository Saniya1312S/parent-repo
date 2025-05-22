package com.example.parentwithsubscription.features.locationtracking.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.locationtracking.model.LocationData;
import com.example.parentwithsubscription.features.locationtracking.adapter.LocationAdapter;
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

public class LocationHistoryFragment extends Fragment {

    private RecyclerView locationHistoryRecyclerView;
    private static final String LOCATION_HISTORY_URL = URIConstants.LOCATION_HISTORY_URL; // Replace with the actual URL

    public LocationHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_safe_unsafe_location, container, false);

        // Initialize RecyclerView
        locationHistoryRecyclerView = view.findViewById(R.id.locationHistoryRecyclerView);
        locationHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch location data
        fetchLocationData();

        return view;
    }

    private void fetchLocationData() {
        OkHttpClient client = new OkHttpClient();

        // Prepare the request to fetch data from the API
        Request request = new Request.Builder()
                .url(LOCATION_HISTORY_URL) // Replace with your actual API URL
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the failure
                if (getContext() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                        Log.e("LocationTracking", "API request failed", e);
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // Get the JSON response string
                        String jsonResponse = response.body().string();

                        // Parse the JSON string into a list of LocationData objects
                        Gson gson = new Gson();
                        Type locationListType = new TypeToken<List<LocationData>>() {}.getType();
                        List<LocationData> locationHistory = gson.fromJson(jsonResponse, locationListType);

                        // Update the UI on the main thread
                        if (getContext() != null) {
                            getActivity().runOnUiThread(() -> {
                                // Set up the adapter with the location data
                                LocationAdapter adapter = new LocationAdapter(locationHistory);
                                locationHistoryRecyclerView.setAdapter(adapter);
                            });
                        }

                    } catch (Exception e) {
                        Log.e("LocationTracking", "Error parsing the response", e);
                        if (getContext() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Failed to parse data", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                } else {
                    // Handle the error if response is not successful
                    if (getContext() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }
}
