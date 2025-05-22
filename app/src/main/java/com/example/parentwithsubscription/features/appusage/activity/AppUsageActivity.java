package com.example.parentwithsubscription.features.appusage.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.appusage.adapter.AppUsageAdapter;
import com.example.parentwithsubscription.features.appusage.adapter.InstallUninstalledAppAdapter;
import com.example.parentwithsubscription.features.appusage.model.AppUsage;
import com.example.parentwithsubscription.features.appusage.model.InstallUninstalledApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppUsageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppUsageAdapter adapter;
    private List<AppUsage> appUsageList;
    private RecyclerView installedAppsRecycler, uninstalledAppsRecycler;
    private OkHttpClient client;
    private String APP_USAGE_URL = URIConstants.APP_USAGE_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        installedAppsRecycler = findViewById(R.id.installed_apps_recycler);
        uninstalledAppsRecycler = findViewById(R.id.uninstalled_apps_recycler);

        client = new OkHttpClient();

        // Fetch data from the API
        fetchDataFromApi();
    }

    private void fetchDataFromApi() {

        Request request = new Request.Builder()
                .url(APP_USAGE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AppUsageActivity", "Error fetching data", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    runOnUiThread(() -> parseAndDisplayData(jsonData));
                } else {
                    Log.e("AppUsageActivity", "Failed to get response");
                }
            }
        });
    }

    /*private void parseAndDisplayData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Parse app usage data
            JSONArray appUsageListJson = jsonObject.getJSONArray("app_usage");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<AppUsage>>() {}.getType();
            appUsageList = gson.fromJson(appUsageListJson.toString(), listType);

            // Sort the app usage data based on usage_time (descending order)
            Collections.sort(appUsageList, new Comparator<AppUsage>() {
                @Override
                public int compare(AppUsage app1, AppUsage app2) {
                    return Integer.compare(Integer.parseInt(app2.getUsageTime()), Integer.parseInt(app1.getUsageTime()));
                }
            });

            // Set up the app usage recycler view
            adapter = new AppUsageAdapter(this, appUsageList);
            recyclerView.setAdapter(adapter);

            // Parse installed apps data
            List<InstallUninstalledApp> installedApps = new ArrayList<>();
            JSONArray installedArray = jsonObject.getJSONArray("installed_apps");
            for (int i = 0; i < installedArray.length(); i++) {
                JSONObject app = installedArray.getJSONObject(i);
                String appName = app.getString("app_name");
                String packageName = app.getString("package_name");
                long time = app.getLong("time");
                installedApps.add(new InstallUninstalledApp(appName, packageName, time));
            }

            // Set up installed apps recycler view
            installedAppsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            InstallUninstalledAppAdapter installedAppAdapter = new InstallUninstalledAppAdapter(this, installedApps);
            installedAppsRecycler.setAdapter(installedAppAdapter);

            // Parse uninstalled apps data
            List<InstallUninstalledApp> uninstalledApps = new ArrayList<>();
            JSONArray uninstalledArray = jsonObject.getJSONArray("uninstalled_apps");
            for (int i = 0; i < uninstalledArray.length(); i++) {
                JSONObject app = uninstalledArray.getJSONObject(i);
                String appName = app.getString("app_name");
                String packageName = app.getString("package_name");
                long time = app.getLong("time");
                uninstalledApps.add(new InstallUninstalledApp(appName, packageName, time));
            }

            // Set up uninstalled apps recycler view
            uninstalledAppsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            InstallUninstalledAppAdapter uninstalledAppAdapter = new InstallUninstalledAppAdapter(this, uninstalledApps);
            uninstalledAppsRecycler.setAdapter(uninstalledAppAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AppUsageActivity", "Error parsing JSON data", e);
        }
    }*/
    private void parseAndDisplayData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Parse app usage data
            JSONArray appUsageListJson = jsonObject.getJSONArray("app_usage");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<AppUsage>>() {}.getType();
            appUsageList = gson.fromJson(appUsageListJson.toString(), listType);

            // Sort the app usage data based on usage_time (descending order)
            Collections.sort(appUsageList, new Comparator<AppUsage>() {
                @Override
                public int compare(AppUsage app1, AppUsage app2) {
                    return Integer.compare(Integer.parseInt(app2.getUsageTime()), Integer.parseInt(app1.getUsageTime()));
                }
            });

            // Set up the app usage recycler view
            adapter = new AppUsageAdapter(this, appUsageList);
            recyclerView.setAdapter(adapter);

            // Parse installed apps data
            List<InstallUninstalledApp> installedApps = new ArrayList<>();
            JSONArray installedArray = jsonObject.getJSONArray("installed_apps");
            for (int i = 0; i < installedArray.length(); i++) {
                JSONObject app = installedArray.getJSONObject(i);
                String appName = app.getString("app_name");
                String packageName = app.getString("package_name");
                long time = app.getLong("time");
                installedApps.add(new InstallUninstalledApp(appName, packageName, time));
            }

            // Check if there are installed apps
            if (installedApps.isEmpty()) {
                // Show message if no installed apps
                findViewById(R.id.no_installed_apps_message).setVisibility(View.VISIBLE);
            } else {
                // Hide message and display installed apps
                findViewById(R.id.no_installed_apps_message).setVisibility(View.GONE);
                installedAppsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                InstallUninstalledAppAdapter installedAppAdapter = new InstallUninstalledAppAdapter(this, installedApps);
                installedAppsRecycler.setAdapter(installedAppAdapter);
            }

            // Parse uninstalled apps data
            List<InstallUninstalledApp> uninstalledApps = new ArrayList<>();
            JSONArray uninstalledArray = jsonObject.getJSONArray("uninstalled_apps");
            for (int i = 0; i < uninstalledArray.length(); i++) {
                JSONObject app = uninstalledArray.getJSONObject(i);
                String appName = app.getString("app_name");
                String packageName = app.getString("package_name");
                long time = app.getLong("time");
                uninstalledApps.add(new InstallUninstalledApp(appName, packageName, time));
            }

            // Check if there are uninstalled apps
            if (uninstalledApps.isEmpty()) {
                // Show message if no uninstalled apps
                findViewById(R.id.no_uninstalled_apps_message).setVisibility(View.VISIBLE);
            } else {
                // Hide message and display uninstalled apps
                findViewById(R.id.no_uninstalled_apps_message).setVisibility(View.GONE);
                uninstalledAppsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                InstallUninstalledAppAdapter uninstalledAppAdapter = new InstallUninstalledAppAdapter(this, uninstalledApps);
                uninstalledAppsRecycler.setAdapter(uninstalledAppAdapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AppUsageActivity", "Error parsing JSON data", e);
        }
    }

}






/*
package com.example.parent.features.appusage.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.parent.R;
import com.example.parent.features.appusage.adapter.AppUsageAdapter;
import com.example.parent.features.appusage.adapter.InstallUninstalledAppAdapter;
import com.example.parent.features.appusage.model.AppUsage;


import com.example.parent.features.appusage.model.AppUsageData;
import com.example.parent.features.appusage.model.InstallUninstalledApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppUsageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AppUsageAdapter adapter;
    private List<AppUsage> appUsageList;
    private RecyclerView installedAppsRecycler, uninstalledAppsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

         try{
             // Sample JSON Data
             String jsonData = "{\n" +
                     "  \"app_usage\": [\n" +
                     "    {\"app_name\": \"WhatsApp\", \"package_name\": \"com.whatsapp\", \"usage_time\": \"4560\", \"sessions\": [\n" +
                     "      {\"openTime\": \"2025-02-07T08:15:00\", \"closeTime\": \"2025-02-07T08:45:00\", \"duration\": 30},\n" +
                     "      {\"openTime\": \"2025-02-07T14:00:00\", \"closeTime\": \"2025-02-07T14:30:00\", \"duration\": 30},\n" +
                     "      {\"openTime\": \"2025-02-07T21:15:00\", \"closeTime\": \"2025-02-07T21:45:00\", \"duration\": 30}\n" +
                     "    ]},\n" +
                     "    {\"app_name\": \"Call\", \"package_name\": \"com.samsung.android.incallui\", \"usage_time\": \"1560\", \"sessions\": [\n" +
                     "      {\"openTime\": \"2025-02-07T09:00:00\", \"closeTime\": \"2025-02-07T09:10:00\", \"duration\": 10},\n" +
                     "      {\"openTime\": \"2025-02-07T16:00:00\", \"closeTime\": \"2025-02-07T16:15:00\", \"duration\": 15},\n" +
                     "      {\"openTime\": \"2025-02-07T19:00:00\", \"closeTime\": \"2025-02-07T19:10:00\", \"duration\": 10}\n" +
                     "    ]},\n" +
                     "    {\"app_name\": \"Snapchat\", \"package_name\": \"com.snapchat.android\", \"usage_time\": \"2400\", \"sessions\": [\n" +
                     "      {\"openTime\": \"2025-02-07T10:00:00\", \"closeTime\": \"2025-02-07T10:20:00\", \"duration\": 20},\n" +
                     "      {\"openTime\": \"2025-02-07T12:00:00\", \"closeTime\": \"2025-02-07T12:20:00\", \"duration\": 20},\n" +
                     "      {\"openTime\": \"2025-02-07T18:00:00\", \"closeTime\": \"2025-02-07T18:20:00\", \"duration\": 20}\n" +
                     "    ]},\n" +
                     "    {\"app_name\": \"YouTube\", \"package_name\": \"com.google.android.youtube\", \"usage_time\": \"14400\", \"sessions\": [\n" +
                     "      {\"openTime\": \"2025-02-07T07:00:00\", \"closeTime\": \"2025-02-07T08:30:00\", \"duration\": 90},\n" +
                     "      {\"openTime\": \"2025-02-07T12:30:00\", \"closeTime\": \"2025-02-07T13:00:00\", \"duration\": 30},\n" +
                     "      {\"openTime\": \"2025-02-07T16:30:00\", \"closeTime\": \"2025-02-07T18:00:00\", \"duration\": 90},\n" +
                     "      {\"openTime\": \"2025-02-07T20:00:00\", \"closeTime\": \"2025-02-07T21:00:00\", \"duration\": 60}\n" +
                     "    ]},\n" +
                     "    {\"app_name\": \"Spotify\", \"package_name\": \"com.spotify.music\", \"usage_time\": \"9000\", \"sessions\": [\n" +
                     "      {\"openTime\": \"2025-02-07T08:00:00\", \"closeTime\": \"2025-02-07T09:00:00\", \"duration\": 60},\n" +
                     "      {\"openTime\": \"2025-02-07T13:00:00\", \"closeTime\": \"2025-02-07T14:00:00\", \"duration\": 60},\n" +
                     "      {\"openTime\": \"2025-02-07T17:00:00\", \"closeTime\": \"2025-02-07T18:00:00\", \"duration\": 60},\n" +
                     "      {\"openTime\": \"2025-02-07T22:00:00\", \"closeTime\": \"2025-02-07T22:30:00\", \"duration\": 30}\n" +
                     "    ]},\n" +
                     "    {\"app_name\": \"Facebook\", \"package_name\": \"com.facebook.katana\", \"usage_time\": \"4200\", \"sessions\": [\n" +
                     "      {\"openTime\": \"2025-02-07T08:45:00\", \"closeTime\": \"2025-02-07T09:05:00\", \"duration\": 20},\n" +
                     "      {\"openTime\": \"2025-02-07T11:00:00\", \"closeTime\": \"2025-02-07T11:25:00\", \"duration\": 25},\n" +
                     "      {\"openTime\": \"2025-02-07T16:30:00\", \"closeTime\": \"2025-02-07T17:10:00\", \"duration\": 40}\n" +
                     "    ]},\n" +
                     "    {\"app_name\": \"Google Maps\", \"package_name\": \"com.google.android.apps.maps\", \"usage_time\": \"3300\", \"sessions\": [\n" +
                     "      {\"openTime\": \"2025-02-07T08:30:00\", \"closeTime\": \"2025-02-07T08:55:00\", \"duration\": 25},\n" +
                     "      {\"openTime\": \"2025-02-07T14:30:00\", \"closeTime\": \"2025-02-07T14:55:00\", \"duration\": 25},\n" +
                     "      {\"openTime\": \"2025-02-07T19:30:00\", \"closeTime\": \"2025-02-07T19:55:00\", \"duration\": 25}\n" +
                     "    ]},\n" +
                     "    {\"app_name\": \"TikTok\", \"package_name\": \"com.zhiliaoapp.musically\", \"usage_time\": \"7500\", \"sessions\": [\n" +
                     "      {\"openTime\": \"2025-02-07T09:00:00\", \"closeTime\": \"2025-02-07T09:30:00\", \"duration\": 30},\n" +
                     "      {\"openTime\": \"2025-02-07T12:30:00\", \"closeTime\": \"2025-02-07T13:00:00\", \"duration\": 30},\n" +
                     "      {\"openTime\": \"2025-02-07T17:30:00\", \"closeTime\": \"2025-02-07T18:00:00\", \"duration\": 30},\n" +
                     "      {\"openTime\": \"2025-02-07T21:00:00\", \"closeTime\": \"2025-02-07T21:05:00\", \"duration\": 5}\n" +
                     "    ]},\n" +
                     "    {\"app_name\": \"Twitter\", \"package_name\": \"com.twitter.android\", \"usage_time\": \"4800\", \"sessions\": [\n" +
                     "      {\"openTime\": \"2025-02-07T07:30:00\", \"closeTime\": \"2025-02-07T08:00:00\", \"duration\": 30},\n" +
                     "      {\"openTime\": \"2025-02-07T11:30:00\", \"closeTime\": \"2025-02-07T12:00:00\", \"duration\": 30},\n" +
                     "      {\"openTime\": \"2025-02-07T17:30:00\", \"closeTime\": \"2025-02-07T18:00:00\", \"duration\": 30}\n" +
                     "    ]}\n" +
                     "  ],\n" +
                     "  \"installed_apps\": [\n" +
                     "    {\"app_name\": \"Facebook\", \"package_name\": \"com.facebook.katana\", \"time\": 1676796323764},\n" +
                     "    {\"app_name\": \"Snapchat\", \"package_name\": \"com.snapchat.android\", \"time\": 1676800323752},\n" +
                     "    {\"app_name\": \"Instagram\", \"package_name\": \"com.instagram.android\", \"time\": 1676814921000},\n" +
                     "    {\"app_name\": \"Twitter\", \"package_name\": \"com.twitter.android\", \"time\": 1676828525000}\n" +
                     "  ],\n" +
                     "  \"uninstalled_apps\": [\n" +
                     "    {\"app_name\": \"Instagram\", \"package_name\": \"com.instagram.android\", \"time\": 1676804921000},\n" +
                     "    {\"app_name\": \"Twitter\", \"package_name\": \"com.twitter.android\", \"time\": 1676818525000}\n" +
                     "  ]\n" +
                     "}";

             // Parse the JSON Data
             JSONObject jsonObject = new JSONObject(jsonData);


             JSONArray appUsageListJson = jsonObject.getJSONArray("app_usage");
             // Convert the JSONArray to List<AppUsage> using Gson
             Gson gson = new Gson();
             Type listType = new TypeToken<List<AppUsage>>() {}.getType();
             List<AppUsage> appUsageList = gson.fromJson(appUsageListJson.toString(), listType);

            // Sort the appUsageList based on usage_time (descending order)
             Collections.sort(appUsageList, new Comparator<AppUsage>() {
                 @Override
                 public int compare(AppUsage app1, AppUsage app2) {
                     return Integer.compare(Integer.parseInt(app2.getUsageTime()), Integer.parseInt(app1.getUsageTime()));
                 }
             });

             // Set up the adapter
             adapter = new AppUsageAdapter(this, appUsageList);
             recyclerView.setAdapter(adapter);


             // Initialize RecyclerViews
             installedAppsRecycler = findViewById(R.id.installed_apps_recycler);
             uninstalledAppsRecycler = findViewById(R.id.uninstalled_apps_recycler);

             // Create lists for installed and uninstalled apps
             List<InstallUninstalledApp> installedApps = new ArrayList<>();
             List<InstallUninstalledApp> uninstalledApps = new ArrayList<>();


             JSONArray installedArray = jsonObject.getJSONArray("installed_apps");
             JSONArray uninstalledArray = jsonObject.getJSONArray("uninstalled_apps");

             // Add data to the lists
             for (int i = 0; i < installedArray.length(); i++) {
                 JSONObject app = installedArray.getJSONObject(i);
                 String appName = app.getString("app_name");
                 String packageName = app.getString("package_name"); // Get the package name
                 long time = app.getLong("time");
                 installedApps.add(new InstallUninstalledApp(appName, packageName, time));
             }

             for (int i = 0; i < uninstalledArray.length(); i++) {
                 JSONObject app = uninstalledArray.getJSONObject(i);
                 String appName = app.getString("app_name");
                 String packageName = app.getString("package_name"); // Get the package name
                 long time = app.getLong("time");
                 uninstalledApps.add(new InstallUninstalledApp(appName, packageName, time));
             }

             // Set up RecyclerViews for installed apps
*/
/*             installedAppsRecycler.setLayoutManager(new LinearLayoutManager(this));
             installedAppsRecycler.setAdapter(new InstallUninstalledAppAdapter(this, installedApps));*//*


             installedAppsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
             InstallUninstalledAppAdapter appAdapter = new InstallUninstalledAppAdapter(this, installedApps);
             installedAppsRecycler.setAdapter(appAdapter);
             // Set up RecyclerViews for uninstalled apps
*/
/*             uninstalledAppsRecycler.setLayoutManager(new LinearLayoutManager(this));
             uninstalledAppsRecycler.setAdapter(new InstallUninstalledAppAdapter(this, uninstalledApps));
         *//*


             uninstalledAppsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
             InstallUninstalledAppAdapter uninstalledAppAdapter = new InstallUninstalledAppAdapter(this, uninstalledApps);
             uninstalledAppsRecycler.setAdapter(uninstalledAppAdapter);
         }
         catch (Exception e) {
             e.printStackTrace();
         }

    }


    private AppUsageData parseJsonData(String jsonData) {
        Gson gson = new Gson();
        Type listType = new TypeToken<AppUsageData>() {}.getType();
        return gson.fromJson(jsonData, listType);
    }
}
*/




/*
package com.example.parent.features.appusage.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.appusage.adapter.AppUsageAdapter;
import com.example.parent.features.appusage.adapter.InstallUninstalledAppAdapter;
import com.example.parent.features.appusage.model.AppUsage;
import com.example.parent.features.appusage.model.InstallUninstalledApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppUsageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AppUsageAdapter adapter;
    private List<AppUsage> appUsageList;
    private RecyclerView installedAppsRecycler, uninstalledAppsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);

        // Initialize RecyclerView and set LayoutManager
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample JSON Data
        String jsonData = "[\n" +
                "  {\"app_name\": \"WhatsApp\", \"package_name\": \"com.whatsapp\", \"usage_time\": \"4560\", \"sessions\": [\n" +
                "    {\"openTime\": \"2025-02-07T08:15:00\", \"closeTime\": \"2025-02-07T08:45:00\", \"duration\": 30},\n" +
                "    {\"openTime\": \"2025-02-07T14:00:00\", \"closeTime\": \"2025-02-07T14:30:00\", \"duration\": 30},\n" +
                "    {\"openTime\": \"2025-02-07T21:15:00\", \"closeTime\": \"2025-02-07T21:45:00\", \"duration\": 30}\n" +
                "  ]},\n" +
                "  {\"app_name\": \"Call\", \"package_name\": \"com.samsung.android.incallui\", \"usage_time\": \"1560\", \"sessions\": [\n" +
                "    {\"openTime\": \"2025-02-07T09:00:00\", \"closeTime\": \"2025-02-07T09:10:00\", \"duration\": 10},\n" +
                "    {\"openTime\": \"2025-02-07T16:00:00\", \"closeTime\": \"2025-02-07T16:15:00\", \"duration\": 15},\n" +
                "    {\"openTime\": \"2025-02-07T19:00:00\", \"closeTime\": \"2025-02-07T19:10:00\", \"duration\": 10}\n" +
                "  ]},\n" +
                "  {\"app_name\": \"Snapchat\", \"package_name\": \"com.snapchat.android\", \"usage_time\": \"2400\", \"sessions\": [\n" +
                "    {\"openTime\": \"2025-02-07T10:00:00\", \"closeTime\": \"2025-02-07T10:20:00\", \"duration\": 20},\n" +
                "    {\"openTime\": \"2025-02-07T12:00:00\", \"closeTime\": \"2025-02-07T12:20:00\", \"duration\": 20},\n" +
                "    {\"openTime\": \"2025-02-07T18:00:00\", \"closeTime\": \"2025-02-07T18:20:00\", \"duration\": 20}\n" +
                "  ]},\n" +
                "  {\"app_name\": \"YouTube\", \"package_name\": \"com.google.android.youtube\", \"usage_time\": \"14400\", \"sessions\": [\n" +
                "    {\"openTime\": \"2025-02-07T07:00:00\", \"closeTime\": \"2025-02-07T08:30:00\", \"duration\": 90},\n" +
                "    {\"openTime\": \"2025-02-07T12:30:00\", \"closeTime\": \"2025-02-07T13:00:00\", \"duration\": 30},\n" +
                "    {\"openTime\": \"2025-02-07T16:30:00\", \"closeTime\": \"2025-02-07T18:00:00\", \"duration\": 90},\n" +
                "    {\"openTime\": \"2025-02-07T20:00:00\", \"closeTime\": \"2025-02-07T21:00:00\", \"duration\": 60}\n" +
                "  ]},\n" +
                "  {\"app_name\": \"Spotify\", \"package_name\": \"com.spotify.music\", \"usage_time\": \"9000\", \"sessions\": [\n" +
                "    {\"openTime\": \"2025-02-07T08:00:00\", \"closeTime\": \"2025-02-07T09:00:00\", \"duration\": 60},\n" +
                "    {\"openTime\": \"2025-02-07T13:00:00\", \"closeTime\": \"2025-02-07T14:00:00\", \"duration\": 60},\n" +
                "    {\"openTime\": \"2025-02-07T17:00:00\", \"closeTime\": \"2025-02-07T18:00:00\", \"duration\": 60},\n" +
                "    {\"openTime\": \"2025-02-07T22:00:00\", \"closeTime\": \"2025-02-07T22:30:00\", \"duration\": 30}\n" +
                "  ]},\n" +
                "  {\"app_name\": \"Facebook\", \"package_name\": \"com.facebook.katana\", \"usage_time\": \"4200\", \"sessions\": [\n" +
                "    {\"openTime\": \"2025-02-07T08:45:00\", \"closeTime\": \"2025-02-07T09:05:00\", \"duration\": 20},\n" +
                "    {\"openTime\": \"2025-02-07T11:00:00\", \"closeTime\": \"2025-02-07T11:25:00\", \"duration\": 25},\n" +
                "    {\"openTime\": \"2025-02-07T16:30:00\", \"closeTime\": \"2025-02-07T17:10:00\", \"duration\": 40}\n" +
                "  ]},\n" +
                "  {\"app_name\": \"Google Maps\", \"package_name\": \"com.google.android.apps.maps\", \"usage_time\": \"3300\", \"sessions\": [\n" +
                "    {\"openTime\": \"2025-02-07T08:30:00\", \"closeTime\": \"2025-02-07T08:55:00\", \"duration\": 25},\n" +
                "    {\"openTime\": \"2025-02-07T14:30:00\", \"closeTime\": \"2025-02-07T14:55:00\", \"duration\": 25},\n" +
                "    {\"openTime\": \"2025-02-07T19:30:00\", \"closeTime\": \"2025-02-07T19:55:00\", \"duration\": 25}\n" +
                "  ]},\n" +
                "  {\"app_name\": \"TikTok\", \"package_name\": \"com.zhiliaoapp.musically\", \"usage_time\": \"7500\", \"sessions\": [\n" +
                "    {\"openTime\": \"2025-02-07T09:00:00\", \"closeTime\": \"2025-02-07T09:30:00\", \"duration\": 30},\n" +
                "    {\"openTime\": \"2025-02-07T12:30:00\", \"closeTime\": \"2025-02-07T13:00:00\", \"duration\": 30},\n" +
                "    {\"openTime\": \"2025-02-07T17:30:00\", \"closeTime\": \"2025-02-07T18:00:00\", \"duration\": 30},\n" +
                "    {\"openTime\": \"2025-02-07T21:00:00\", \"closeTime\": \"2025-02-07T21:05:00\", \"duration\": 5}\n" +
                "  ]},\n" +
                "  {\"app_name\": \"Twitter\", \"package_name\": \"com.twitter.android\", \"usage_time\": \"4800\", \"sessions\": [\n" +
                "    {\"openTime\": \"2025-02-07T07:30:00\", \"closeTime\": \"2025-02-07T08:00:00\", \"duration\": 30},\n" +
                "    {\"openTime\": \"2025-02-07T11:30:00\", \"closeTime\": \"2025-02-07T12:00:00\", \"duration\": 30},\n" +
                "    {\"openTime\": \"2025-02-07T17:30:00\", \"closeTime\": \"2025-02-07T18:00:00\", \"duration\": 30}\n" +
                "  ]}\n" +
                "]";

        // Parse JSON Data
        appUsageList = parseJsonData(jsonData);

        // Sort the appUsageList based on usage_time (descending order)
        Collections.sort(appUsageList, new Comparator<AppUsage>() {
            @Override
            public int compare(AppUsage app1, AppUsage app2) {
                return Integer.compare(Integer.parseInt(app2.getUsageTime()), Integer.parseInt(app1.getUsageTime()));
            }
        });

        // Set up the adapter
        adapter = new AppUsageAdapter(this, appUsageList);
        recyclerView.setAdapter(adapter);

// Initialize RecyclerViews
        installedAppsRecycler = findViewById(R.id.installed_apps_recycler);
        uninstalledAppsRecycler = findViewById(R.id.uninstalled_apps_recycler);

        // Create lists for installed and uninstalled apps
        List<InstallUninstalledApp> installedApps = new ArrayList<>();
        List<InstallUninstalledApp> uninstalledApps = new ArrayList<>();

        try {
            // Sample JSON for testing
            String jsonString = "{\n" +
                    "  \"installed_apps\": [\n" +
                    "    {\"app_name\": \"Facebook\", \"package_name\": \"com.facebook.katana\", \"time\": 1676796323764},\n" +
                    "    {\"app_name\": \"Snapchat\", \"package_name\": \"com.snapchat.android\", \"time\": 1676800323752},\n" +
                    "    {\"app_name\": \"Instagram\", \"package_name\": \"com.instagram.android\", \"time\": 1676814921000},\n" +
                    "    {\"app_name\": \"Twitter\", \"package_name\": \"com.twitter.android\", \"time\": 1676828525000}\n" +
                    "  ],\n" +
                    "  \"uninstalled_apps\": [\n" +
                    "    {\"app_name\": \"Instagram\", \"package_name\": \"com.instagram.android\", \"time\": 1676804921000},\n" +
                    "    {\"app_name\": \"Twitter\", \"package_name\": \"com.twitter.android\", \"time\": 1676818525000}\n" +
                    "  ]\n" +
                    "}";

            // Parse JSON
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray installedArray = jsonObject.getJSONArray("installed_apps");
            JSONArray uninstalledArray = jsonObject.getJSONArray("uninstalled_apps");

            // Add data to the lists
            for (int i = 0; i < installedArray.length(); i++) {
                JSONObject app = installedArray.getJSONObject(i);
                String appName = app.getString("app_name");
                String packageName = app.getString("package_name"); // Get the package name
                long time = app.getLong("time");
                installedApps.add(new InstallUninstalledApp(appName, packageName, time));
            }

            for (int i = 0; i < uninstalledArray.length(); i++) {
                JSONObject app = uninstalledArray.getJSONObject(i);
                String appName = app.getString("app_name");
                String packageName = app.getString("package_name"); // Get the package name
                long time = app.getLong("time");
                uninstalledApps.add(new InstallUninstalledApp(appName, packageName, time));
            }

            // Set up RecyclerViews for installed apps
            installedAppsRecycler.setLayoutManager(new LinearLayoutManager(this));
            installedAppsRecycler.setAdapter(new InstallUninstalledAppAdapter(this, installedApps));

            // Set up RecyclerViews for uninstalled apps
            uninstalledAppsRecycler.setLayoutManager(new LinearLayoutManager(this));
            uninstalledAppsRecycler.setAdapter(new InstallUninstalledAppAdapter(this, uninstalledApps));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<AppUsage> parseJsonData(String jsonData) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<AppUsage>>() {}.getType();
        return gson.fromJson(jsonData, listType);
    }
}
*/







/*
package com.example.parent.features.appusage.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parent.R;
import com.example.parent.features.appusage.adapter.AppUsageAdapter;
import com.example.parent.features.appusage.model.AppUsageData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class AppUsageActivity extends AppCompatActivity {

    private ListView listView;
    private PackageManager packageManager;
    private List<AppUsageData> appDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);

        listView = findViewById(R.id.appUsageListView);
        packageManager = getPackageManager();

        // Here you would fetch the JSON data (for now we use a mock example)
        String jsonResponse = getMockJsonResponse();

        // Parse the JSON data
        parseJsonResponse(jsonResponse);

        // Set the custom adapter to ListView
        AppUsageAdapter appAdapter = new AppUsageAdapter(this, appDataList, packageManager);
        listView.setAdapter(appAdapter);
    }

    // Mock JSON response (you would normally fetch this from an API)
    private String getMockJsonResponse() {
        return "[\n" +
                "  {\"app_name\": \"WhatsApp\", \"package_name\": \"com.whatsapp\", \"usage_time\": \"1 hours 16 minutes\"},\n" +
                "  {\"app_name\": \"Call\", \"package_name\": \"com.samsung.android.incallui\", \"usage_time\": \"0 hours 26 minutes\"},\n" +
                "  {\"app_name\": \"Snapchat\", \"package_name\": \"com.snapchat.android\", \"usage_time\": \"0 hours 40 minutes\"},\n" +
                "  {\"app_name\": \"YouTube\", \"package_name\": \"com.google.android.youtube\", \"usage_time\": \"4 hours 4 minutes\"},\n" +
                "  {\"app_name\": \"com.rapido.passenger\", \"package_name\": \"com.rapido.passenger\", \"usage_time\": \"0 hours 5 minutes\"},\n" +
                "  {\"app_name\": \"Instagram\", \"package_name\": \"com.instagram.android\", \"usage_time\": \"0 hours 48 minutes\"}" +
                "]";
    }

    // Parse JSON response
    private void parseJsonResponse(String jsonResponse) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<AppUsageData>>(){}.getType();
        appDataList = gson.fromJson(jsonResponse, listType);
    }
}
*/
