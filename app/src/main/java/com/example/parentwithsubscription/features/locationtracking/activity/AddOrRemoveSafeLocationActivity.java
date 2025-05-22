package com.example.parentwithsubscription.features.locationtracking.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.locationtracking.adapter.LocationAdapter;
import com.example.parentwithsubscription.features.locationtracking.model.LocationData;
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

public class AddOrRemoveSafeLocationActivity extends AppCompatActivity {

    private RecyclerView locationHistoryRecyclerView;
    private static final String LOCATION_HISTORY_URL = URIConstants.LOCATION_HISTORY_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_unsafe_location);

        locationHistoryRecyclerView = findViewById(R.id.locationHistoryRecyclerView);
        locationHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch data from the API
        fetchLocationData();
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
                runOnUiThread(() -> {
                    Toast.makeText(AddOrRemoveSafeLocationActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    Log.e("LocationTracking", "API request failed", e);
                });
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
                        runOnUiThread(() -> {
                            // Set up the adapter with the location data
                            LocationAdapter adapter = new LocationAdapter(locationHistory);
                            locationHistoryRecyclerView.setAdapter(adapter);
                        });

                    } catch (Exception e) {
                        Log.e("LocationTracking", "Error parsing the response", e);
                        runOnUiThread(() -> {
                            Toast.makeText(AddOrRemoveSafeLocationActivity.this, "Failed to parse data", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    // Handle the error if response is not successful
                    runOnUiThread(() -> {
                        Toast.makeText(AddOrRemoveSafeLocationActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}











/*package com.example.parent.features.locationtracking.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.locationtracking.adapter.LocationAdapter;
import com.example.parent.features.locationtracking.model.GeofenceData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class AddOrRemoveSafeLocationActivity extends AppCompatActivity {
    private RecyclerView locationHistoryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_unsafe_location);

        locationHistoryRecyclerView = findViewById(R.id.locationHistoryRecyclerView);
        locationHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // JSON String without "locationHistory" wrapper
        String jsonString = "[\n" +
                "  {\n" +
                "    \"latitude\": 40.748817,\n" +
                "    \"longitude\": -73.985428,\n" +
                "    \"address\": \"Empire State Building, New York, NY\",\n" +
                "    \"timestamp\": \"2025-03-07T12:34:56Z\",\n" +
                "    \"geofence\": \"inside\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"latitude\": 34.052235,\n" +
                "    \"longitude\": -118.243683,\n" +
                "    \"address\": \"Los Angeles, CA\",\n" +
                "    \"timestamp\": \"2025-03-06T08:22:43Z\",\n" +
                "    \"geofence\": \"outside\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"latitude\": 37.774929,\n" +
                "    \"longitude\": -122.419418,\n" +
                "    \"address\": \"San Francisco, CA\",\n" +
                "    \"timestamp\": \"2025-03-05T15:45:00Z\",\n" +
                "    \"geofence\": \"inside\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"latitude\": 51.507351,\n" +
                "    \"longitude\": -0.127758,\n" +
                "    \"address\": \"London, UK\",\n" +
                "    \"timestamp\": \"2025-03-04T10:00:00Z\",\n" +
                "    \"geofence\": \"inside\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"latitude\": 48.856613,\n" +
                "    \"longitude\": 2.352222,\n" +
                "    \"address\": \"Paris, France\",\n" +
                "    \"timestamp\": \"2025-03-03T14:20:00Z\",\n" +
                "    \"geofence\": \"outside\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"latitude\": -33.868820,\n" +
                "    \"longitude\": 151.209290,\n" +
                "    \"address\": \"Sydney, Australia\",\n" +
                "    \"timestamp\": \"2025-03-02T11:15:30Z\",\n" +
                "    \"geofence\": \"inside\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"latitude\": 40.730610,\n" +
                "    \"longitude\": -73.935242,\n" +
                "    \"address\": \"Brooklyn, New York, NY\",\n" +
                "    \"timestamp\": \"2025-03-01T17:55:00Z\",\n" +
                "    \"geofence\": \"outside\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"latitude\": 35.689487,\n" +
                "    \"longitude\": 139.691711,\n" +
                "    \"address\": \"Tokyo, Japan\",\n" +
                "    \"timestamp\": \"2025-02-28T09:10:45Z\",\n" +
                "    \"geofence\": \"inside\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"latitude\": -34.603684,\n" +
                "    \"longitude\": -58.381559,\n" +
                "    \"address\": \"Buenos Aires, Argentina\",\n" +
                "    \"timestamp\": \"2025-02-27T13:50:30Z\",\n" +
                "    \"geofence\": \"outside\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"latitude\": 39.904202,\n" +
                "    \"longitude\": 116.407394,\n" +
                "    \"address\": \"Beijing, China\",\n" +
                "    \"timestamp\": \"2025-02-26T12:30:00Z\",\n" +
                "    \"geofence\": \"inside\"\n" +
                "  }\n" +
                "]";


        // Parse the JSON string into a list of Location objects
        Gson gson = new Gson();
        Type locationListType = new TypeToken<List<GeofenceData>>() {
        }.getType();
        List<GeofenceData> locationHistory = gson.fromJson(jsonString, locationListType);

        // Setting up the adapter
        LocationAdapter adapter = new LocationAdapter(locationHistory);
        locationHistoryRecyclerView.setAdapter(adapter);
    }
}*/









/*
package com.example.parent.features.locationtracking.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.locationtracking.adapter.LocationAdapter;
import com.example.parent.features.locationtracking.adapter.RemoveLocationAdapter;
import com.example.parent.features.locationtracking.model.GeofenceData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddOrRemoveSafeLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_unsafe_location);

        loadLocationsOutsideGeofence();  // Load the locations outside geofence
        loadLocationsInsideGeofence();
    }

    private void loadLocationsInsideGeofence() {
        try {
            // Simulate a JSON response
            String jsonResponse = "[\n" +
                    "  {\"address\": \"789 Outside Lane, City A\", \"latitude\": 41.8781, \"longitude\": -87.6298, \"timestamp\": \"2025-02-04T08:30:00Z\"},\n" +
                    "  {\"address\": \"234 Remote Road, City B\", \"latitude\": 36.1699, \"longitude\": -115.1398, \"timestamp\": \"2025-02-04T09:15:00Z\"},\n" +
                    "  {\"address\": \"567 Lost Street, City C\", \"latitude\": 37.7749, \"longitude\": -122.4194, \"timestamp\": \"2025-02-04T10:00:00Z\"},\n" +
                    "  {\"address\": \"345 Outlying Blvd, City D\", \"latitude\": 40.7128, \"longitude\": -74.0060, \"timestamp\": \"2025-02-04T11:00:00Z\"},\n" +
                    "  {\"address\": \"456 Forgotten Ave, City E\", \"latitude\": 34.0522, \"longitude\": -118.2437, \"timestamp\": \"2025-02-04T12:00:00Z\"},\n" +
                    "  {\"address\": \"678 Distant St, City F\", \"latitude\": 51.5074, \"longitude\": -0.1278, \"timestamp\": \"2025-02-04T13:00:00Z\"},\n" +
                    "  {\"address\": \"890 Faraway Rd, City G\", \"latitude\": 48.8566, \"longitude\": 2.3522, \"timestamp\": \"2025-02-04T14:00:00Z\"},\n" +
                    "  {\"address\": \"123 Remote Point, City H\", \"latitude\": 55.7558, \"longitude\": 37.6173, \"timestamp\": \"2025-02-04T15:30:00Z\"},\n" +
                    "  {\"address\": \"987 Far Road, City I\", \"latitude\": 39.9042, \"longitude\": 116.4074, \"timestamp\": \"2025-02-04T16:45:00Z\"},\n" +
                    "  {\"address\": \"321 Distant Way, City J\", \"latitude\": 35.6895, \"longitude\": 139.6917, \"timestamp\": \"2025-02-04T17:00:00Z\"}\n" +
                    "]";

            // Parse the JSON response
            JSONArray jsonArray = new JSONArray(jsonResponse);
            List<GeofenceData> locationList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject locationJson = jsonArray.getJSONObject(i);
                String address = locationJson.getString("address");
                double latitude = locationJson.getDouble("latitude");
                double longitude = locationJson.getDouble("longitude");
                String timestamp = locationJson.getString("timestamp");

                GeofenceData location = new GeofenceData(address, latitude, longitude, timestamp);
                locationList.add(location);
            }

            // Set up RecyclerView
            RecyclerView recyclerView = findViewById(R.id.safe_locations_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            RemoveLocationAdapter locationAdapter = new RemoveLocationAdapter(locationList, new RemoveLocationAdapter.OnLocationRemoveClickListener() {
                @Override
                public void onRemoveClick(GeofenceData location) {
                    // Handle the "+" button click here
                    Toast.makeText(AddOrRemoveSafeLocationActivity.this, "Removed: " + location.getAddress(), Toast.LENGTH_SHORT).show();
                }
            });

            recyclerView.setAdapter(locationAdapter);

        } catch (Exception e) {
            Log.e("JSON Parsing Error", "Error parsing locations outside geofence", e);
            Toast.makeText(this, "Failed to load locations", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLocationsOutsideGeofence() {
        try {
            // Simulate a JSON response
*/
/*            String jsonResponse = "[\n" +
                    "  {\"address\": \"789 Outside Lane, City A\", \"latitude\": 41.8781, \"longitude\": -87.6298, \"timestamp\": \"2025-02-04T08:30:00Z\"},\n" +
                    "  {\"address\": \"234 Remote Road, City B\", \"latitude\": 36.1699, \"longitude\": -115.1398, \"timestamp\": \"2025-02-04T09:15:00Z\"},\n" +
                    "  {\"address\": \"567 Lost Street, City C\", \"latitude\": 37.7749, \"longitude\": -122.4194, \"timestamp\": \"2025-02-04T10:00:00Z\"}\n" +
                    "]";*//*

            // Simulate a JSON response
            String jsonResponse = "[\n" +
                    "  {\"address\": \"789 Outside Lane, City A\", \"latitude\": 41.8781, \"longitude\": -87.6298, \"timestamp\": \"2025-02-04T08:30:00Z\"},\n" +
                    "  {\"address\": \"234 Remote Road, City B\", \"latitude\": 36.1699, \"longitude\": -115.1398, \"timestamp\": \"2025-02-04T09:15:00Z\"},\n" +
                    "  {\"address\": \"567 Lost Street, City C\", \"latitude\": 37.7749, \"longitude\": -122.4194, \"timestamp\": \"2025-02-04T10:00:00Z\"},\n" +
                    "  {\"address\": \"345 Outlying Blvd, City D\", \"latitude\": 40.7128, \"longitude\": -74.0060, \"timestamp\": \"2025-02-04T11:00:00Z\"},\n" +
                    "  {\"address\": \"456 Forgotten Ave, City E\", \"latitude\": 34.0522, \"longitude\": -118.2437, \"timestamp\": \"2025-02-04T12:00:00Z\"},\n" +
                    "  {\"address\": \"678 Distant St, City F\", \"latitude\": 51.5074, \"longitude\": -0.1278, \"timestamp\": \"2025-02-04T13:00:00Z\"},\n" +
                    "  {\"address\": \"890 Faraway Rd, City G\", \"latitude\": 48.8566, \"longitude\": 2.3522, \"timestamp\": \"2025-02-04T14:00:00Z\"},\n" +
                    "  {\"address\": \"123 Remote Point, City H\", \"latitude\": 55.7558, \"longitude\": 37.6173, \"timestamp\": \"2025-02-04T15:30:00Z\"},\n" +
                    "  {\"address\": \"987 Far Road, City I\", \"latitude\": 39.9042, \"longitude\": 116.4074, \"timestamp\": \"2025-02-04T16:45:00Z\"},\n" +
                    "  {\"address\": \"321 Distant Way, City J\", \"latitude\": 35.6895, \"longitude\": 139.6917, \"timestamp\": \"2025-02-04T17:00:00Z\"}\n" +
                    "]";

            */
/*
            JSONArray jsonArray = new JSONArray(jsonResponse);
            List<GeofenceData> locationList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject locationJson = jsonArray.getJSONObject(i);
                String address = locationJson.getString("address");
                double latitude = locationJson.getDouble("latitude");
                double longitude = locationJson.getDouble("longitude");
                String timestamp = locationJson.getString("timestamp");

                GeofenceData location = new GeofenceData(address, latitude, longitude, timestamp);
                locationList.add(location);
            }

            // Set the adapter for RecyclerView
            RecyclerView recyclerView = findViewById(R.id.locations_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            LocationAdapter locationAdapter = new LocationAdapter(locationList);
            recyclerView.setAdapter(locationAdapter);
        } catch (Exception e) {
            Log.e("JSON Parsing Error", "Error parsing locations outside geofence", e);
            Toast.makeText(this, "Failed to load locations", Toast.LENGTH_SHORT).show();
        }*//*

            // Parse the JSON response
            JSONArray jsonArray = new JSONArray(jsonResponse);
            List<GeofenceData> locationList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject locationJson = jsonArray.getJSONObject(i);
                String address = locationJson.getString("address");
                double latitude = locationJson.getDouble("latitude");
                double longitude = locationJson.getDouble("longitude");
                String timestamp = locationJson.getString("timestamp");

                GeofenceData location = new GeofenceData(address, latitude, longitude, timestamp);
                locationList.add(location);
            }

            // Set up RecyclerView
            RecyclerView recyclerView = findViewById(R.id.unsafe_locations_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            LocationAdapter locationAdapter = new LocationAdapter(locationList, new LocationAdapter.OnLocationAddClickListener() {
                @Override
                public void onAddClick(GeofenceData location) {
                    // Handle the "+" button click here
                    Toast.makeText(AddOrRemoveSafeLocationActivity.this, "Added: " + location.getAddress(), Toast.LENGTH_SHORT).show();
                }
            });

            recyclerView.setAdapter(locationAdapter);

        } catch (Exception e) {
            Log.e("JSON Parsing Error", "Error parsing locations outside geofence", e);
            Toast.makeText(this, "Failed to load locations", Toast.LENGTH_SHORT).show();
        }
    }
}
*/
