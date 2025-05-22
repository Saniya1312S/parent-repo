package com.example.parentwithsubscription.features.appusage.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppUsageFragment extends Fragment {

    private RecyclerView recyclerView, installedAppsRecycler, uninstalledAppsRecycler;
    private TextView noInstalledMessage, noUninstalledMessage;
    private AppUsageAdapter adapter;
    private OkHttpClient client;
    private final String APP_USAGE_URL = URIConstants.APP_USAGE_URL;
    private List<AppUsage> appUsageList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_app_usage, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        installedAppsRecycler = rootView.findViewById(R.id.installed_apps_recycler);
        uninstalledAppsRecycler = rootView.findViewById(R.id.uninstalled_apps_recycler);

        noInstalledMessage = rootView.findViewById(R.id.no_installed_apps_message);
        noUninstalledMessage = rootView.findViewById(R.id.no_uninstalled_apps_message);

        client = new OkHttpClient();

        fetchDataFromApi();

        return rootView;
    }

    private void fetchDataFromApi() {
        Request request = new Request.Builder()
                .url(APP_USAGE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("AppUsageFragment", "Error fetching data", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && getActivity() != null) {
                    String jsonData = response.body().string();
                    getActivity().runOnUiThread(() -> parseAndDisplayData(jsonData));
                } else {
                    Log.e("AppUsageFragment", "Failed to get response");
                }
            }
        });
    }

    private void parseAndDisplayData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Parse app usage
            JSONArray appUsageListJson = jsonObject.getJSONArray("app_usage");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<AppUsage>>() {}.getType();
            appUsageList = gson.fromJson(appUsageListJson.toString(), listType);

            // Sort descending by usage time
            Collections.sort(appUsageList, (app1, app2) ->
                    Integer.compare(Integer.parseInt(app2.getUsageTime()), Integer.parseInt(app1.getUsageTime()))
            );

            // Set adapter
            adapter = new AppUsageAdapter(getContext(), appUsageList);
            recyclerView.setAdapter(adapter);

            // Parse installed apps
            List<InstallUninstalledApp> installedApps = new ArrayList<>();
            JSONArray installedArray = jsonObject.getJSONArray("installed_apps");
            for (int i = 0; i < installedArray.length(); i++) {
                JSONObject app = installedArray.getJSONObject(i);
                String appName = app.getString("app_name");
                String packageName = app.getString("package_name");
                long time = app.getLong("time");
                installedApps.add(new InstallUninstalledApp(appName, packageName, time));
            }

            if (installedApps.isEmpty()) {
                noInstalledMessage.setVisibility(View.VISIBLE);
            } else {
                noInstalledMessage.setVisibility(View.GONE);
                installedAppsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                InstallUninstalledAppAdapter installedAppAdapter = new InstallUninstalledAppAdapter(getContext(), installedApps);
                installedAppsRecycler.setAdapter(installedAppAdapter);
            }

            // Parse uninstalled apps
            List<InstallUninstalledApp> uninstalledApps = new ArrayList<>();
            JSONArray uninstalledArray = jsonObject.getJSONArray("uninstalled_apps");
            for (int i = 0; i < uninstalledArray.length(); i++) {
                JSONObject app = uninstalledArray.getJSONObject(i);
                String appName = app.getString("app_name");
                String packageName = app.getString("package_name");
                long time = app.getLong("time");
                uninstalledApps.add(new InstallUninstalledApp(appName, packageName, time));
            }

            if (uninstalledApps.isEmpty()) {
                noUninstalledMessage.setVisibility(View.VISIBLE);
            } else {
                noUninstalledMessage.setVisibility(View.GONE);
                uninstalledAppsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                InstallUninstalledAppAdapter uninstalledAppAdapter = new InstallUninstalledAppAdapter(getContext(), uninstalledApps);
                uninstalledAppsRecycler.setAdapter(uninstalledAppAdapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AppUsageFragment", "Error parsing JSON data", e);
        }
    }
}
