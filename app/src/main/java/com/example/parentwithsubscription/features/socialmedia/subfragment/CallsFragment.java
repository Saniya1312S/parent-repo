package com.example.parentwithsubscription.features.socialmedia.subfragment;

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
import com.example.parentwithsubscription.common.GlobalData;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.socialmedia.adapter.instagram.InstagramCallAdapter;
import com.example.parentwithsubscription.features.socialmedia.adapter.whatsapp.WhatsAppCallAdapter;
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramCalls;
import com.example.parentwithsubscription.features.socialmedia.model.whatsapp.WhatsAppCall;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CallsFragment extends Fragment {

    private RecyclerView callsRecyclerView;
    private WhatsAppCallAdapter whatsAppCallAdapter;
    private InstagramCallAdapter instagramCallAdapter;

    private static final String ARG_PLATFORM = "platform";
    private String platform;
    private String logType = "calls";

    private String SOCIAL_MEDIA_URL = URIConstants.SOCIAL_MEDIA_URL;

    public CallsFragment() {
        // Required empty public constructor
    }

    public static CallsFragment newInstance(String platform) {
        CallsFragment fragment = new CallsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLATFORM, platform);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            platform = getArguments().getString(ARG_PLATFORM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calls_fragment, container, false);

        callsRecyclerView = view.findViewById(R.id.whatsapp_calls_recycler_view);
        callsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        if (platform != null) {
            fetchDataFromApi(platform, logType);
            /*String jsonData;
            switch (platform) {
                case "WhatsApp":
                    jsonData = "["
                            + "{\"name\": \"John Doe\", \"phone_number\": \"+917981214937\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-10 14:30:00\", \"duration\": \"5m 34s\"},"
                            + "{\"name\": \"Jane Smith\", \"phone_number\": \"+919876543210\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-09 16:45:12\", \"duration\": \"12m 10s\"},"
                            + "{\"name\": \"Mike Johnson\", \"phone_number\": \"+919988776655\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-08 19:20:30\", \"duration\": \"0\"},"
                            + "{\"name\": \"Emily Brown\", \"phone_number\": \"+917003221101\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-07 10:15:50\", \"duration\": \"8m 30s\"},"
                            + "{\"name\": \"Chris Williams\", \"phone_number\": \"+919067890123\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-06 13:15:22\", \"duration\": \"3m 45s\"},"
                            + "{\"name\": \"Amanda Miller\", \"phone_number\": \"+918912345678\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-05 11:20:50\", \"duration\": \"15m 22s\"},"
                            + "{\"name\": \"David Lee\", \"phone_number\": \"+917032109876\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-04 09:45:00\", \"duration\": \"2m 0s\"},"
                            + "{\"name\": \"Sarah Taylor\", \"phone_number\": \"+917876543210\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-03 18:05:15\", \"duration\": \"10m 5s\"},"
                            + "{\"name\": \"Kevin White\", \"phone_number\": \"+919827364511\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-02 21:30:45\", \"duration\": \"4m 55s\"},"
                            + "{\"name\": \"Olivia Harris\", \"phone_number\": \"+919345678901\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-01 17:25:30\", \"duration\": \"0\"},"
                            + "{\"name\": \"Jason Scott\", \"phone_number\": \"+918188822244\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-30 14:55:12\", \"duration\": \"7m 30s\"},"
                            + "{\"name\": \"Sophia Adams\", \"phone_number\": \"+917654321098\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-29 20:10:25\", \"duration\": \"6m 45s\"},"
                            + "{\"name\": \"Ethan Walker\", \"phone_number\": \"+918977665544\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-28 15:40:05\", \"duration\": \"9m 10s\"},"
                            + "{\"name\": \"Lucas Moore\", \"phone_number\": \"+919876543567\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-27 10:30:00\", \"duration\": \"11m 30s\"},"
                            + "{\"name\": \"Megan King\", \"phone_number\": \"+917654321234\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-26 12:15:30\", \"duration\": \"0\"},"
                            + "{\"name\": \"Isabella Johnson\", \"phone_number\": \"+919688777555\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-25 19:45:12\", \"duration\": \"13m 0s\"},"
                            + "{\"name\": \"Daniel Martinez\", \"phone_number\": \"+918766554433\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-24 08:30:15\", \"duration\": \"1m 40s\"}"
                            + "]";
                    fetchCalls(jsonData, "WhatsApp");
                    fetchDataFromApi(platform, logType);
                    break;
                case "Instagram":
                    jsonData = "["
                            + "{\"user_id\": \"user_12345\", \"call_mode\": \"audio\", \"call_type\": \"incoming\", \"time\": \"2025-03-03T10:15:00Z\", \"duration\": 120},"
                            + "{\"user_id\": \"user_67890\", \"call_mode\": \"video\", \"call_type\": \"outgoing\", \"time\": \"2025-03-03T11:00:00Z\", \"duration\": 180},"
                            + "{\"user_id\": \"user_11111\", \"call_mode\": \"audio\", \"call_type\": \"incoming\", \"time\": \"2025-03-03T12:00:00Z\", \"duration\": 150},"
                            + "{\"user_id\": \"user_22222\", \"call_mode\": \"video\", \"call_type\": \"outgoing\", \"time\": \"2025-03-03T13:00:00Z\", \"duration\": 200}"
                            + "]";
                    fetchCalls(jsonData, "Instagram");
                    break;
                case "Facebook":
                    jsonData = "["
                            + "{\"user_id\": \"user_12345\", \"call_mode\": \"audio\", \"call_type\": \"incoming\", \"time\": \"2025-03-03T10:15:00Z\", \"duration\": 120},"
                            + "{\"user_id\": \"user_67890\", \"call_mode\": \"video\", \"call_type\": \"outgoing\", \"time\": \"2025-03-03T11:00:00Z\", \"duration\": 180},"
                            + "{\"user_id\": \"user_11111\", \"call_mode\": \"audio\", \"call_type\": \"incoming\", \"time\": \"2025-03-03T12:00:00Z\", \"duration\": 150},"
                            + "{\"user_id\": \"user_22222\", \"call_mode\": \"video\", \"call_type\": \"outgoing\", \"time\": \"2025-03-03T13:00:00Z\", \"duration\": 200}"
                            + "]";
                    fetchCalls(jsonData, "Facebook");
                    break;
                case "SnapChat":
                    jsonData = "["
                            + "{\"user_id\": \"user_12345\", \"call_mode\": \"audio\", \"call_type\": \"incoming\", \"time\": \"2025-03-03T10:15:00Z\", \"duration\": 120},"
                            + "{\"user_id\": \"user_67890\", \"call_mode\": \"video\", \"call_type\": \"outgoing\", \"time\": \"2025-03-03T11:00:00Z\", \"duration\": 180},"
                            + "{\"user_id\": \"user_11111\", \"call_mode\": \"audio\", \"call_type\": \"incoming\", \"time\": \"2025-03-03T12:00:00Z\", \"duration\": 150},"
                            + "{\"user_id\": \"user_22222\", \"call_mode\": \"video\", \"call_type\": \"outgoing\", \"time\": \"2025-03-03T13:00:00Z\", \"duration\": 200}"
                            + "]";
                    fetchCalls(jsonData, "SnapChat");
                    break;
                case "Twitter":
                    jsonData = "["
                            + "{\"user_id\": \"user_12345\", \"call_mode\": \"audio\", \"call_type\": \"incoming\", \"time\": \"2025-03-03T10:15:00Z\", \"duration\": 120},"
                            + "{\"user_id\": \"user_67890\", \"call_mode\": \"video\", \"call_type\": \"outgoing\", \"time\": \"2025-03-03T11:00:00Z\", \"duration\": 180},"
                            + "{\"user_id\": \"user_11111\", \"call_mode\": \"audio\", \"call_type\": \"incoming\", \"time\": \"2025-03-03T12:00:00Z\", \"duration\": 150},"
                            + "{\"user_id\": \"user_22222\", \"call_mode\": \"video\", \"call_type\": \"outgoing\", \"time\": \"2025-03-03T13:00:00Z\", \"duration\": 200}"
                            + "]";
                    fetchCalls(jsonData, "Twitter");
                    break;
                case "Telegram":
                    jsonData = "["
                            + "{\"name\": \"John Doe\", \"phone_number\": \"+917981214937\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-10 14:30:00\", \"duration\": \"5m 34s\"},"
                            + "{\"name\": \"Jane Smith\", \"phone_number\": \"+919876543210\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-09 16:45:12\", \"duration\": \"12m 10s\"},"
                            + "{\"name\": \"Mike Johnson\", \"phone_number\": \"+919988776655\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-08 19:20:30\", \"duration\": \"0\"},"
                            + "{\"name\": \"Emily Brown\", \"phone_number\": \"+917003221101\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-07 10:15:50\", \"duration\": \"8m 30s\"},"
                            + "{\"name\": \"Chris Williams\", \"phone_number\": \"+919067890123\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-06 13:15:22\", \"duration\": \"3m 45s\"},"
                            + "{\"name\": \"Amanda Miller\", \"phone_number\": \"+918912345678\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-05 11:20:50\", \"duration\": \"15m 22s\"},"
                            + "{\"name\": \"David Lee\", \"phone_number\": \"+917032109876\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-04 09:45:00\", \"duration\": \"2m 0s\"},"
                            + "{\"name\": \"Sarah Taylor\", \"phone_number\": \"+917876543210\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-03 18:05:15\", \"duration\": \"10m 5s\"},"
                            + "{\"name\": \"Kevin White\", \"phone_number\": \"+919827364511\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-02 21:30:45\", \"duration\": \"4m 55s\"},"
                            + "{\"name\": \"Olivia Harris\", \"phone_number\": \"+919345678901\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-01 17:25:30\", \"duration\": \"0\"},"
                            + "{\"name\": \"Jason Scott\", \"phone_number\": \"+918188822244\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-30 14:55:12\", \"duration\": \"7m 30s\"},"
                            + "{\"name\": \"Sophia Adams\", \"phone_number\": \"+917654321098\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-29 20:10:25\", \"duration\": \"6m 45s\"},"
                            + "{\"name\": \"Ethan Walker\", \"phone_number\": \"+918977665544\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-28 15:40:05\", \"duration\": \"9m 10s\"},"
                            + "{\"name\": \"Lucas Moore\", \"phone_number\": \"+919876543567\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-27 10:30:00\", \"duration\": \"11m 30s\"},"
                            + "{\"name\": \"Megan King\", \"phone_number\": \"+917654321234\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-26 12:15:30\", \"duration\": \"0\"},"
                            + "{\"name\": \"Isabella Johnson\", \"phone_number\": \"+919688777555\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-25 19:45:12\", \"duration\": \"13m 0s\"},"
                            + "{\"name\": \"Daniel Martinez\", \"phone_number\": \"+918766554433\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-24 08:30:15\", \"duration\": \"1m 40s\"}"
                            + "]";
                    fetchCalls(jsonData, "Telegram");
                    break;
                default:
                    Toast.makeText(getContext(), "Platform not recognized", Toast.LENGTH_SHORT).show();
            }*/
        }

        return view;
    }

    private void fetchDataFromApi(String appName, String logType) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        String url = SOCIAL_MEDIA_URL + "?log_type=" + logType + "&appname=" + appName + "&device_id=" + GlobalData.getDeviceId();
        Log.d("API_URL", "Request URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Log the error message for debugging purposes
                Log.e("API_ERROR", "API Request Failed: " + e.getMessage(), e);

                // Ensure UI updates happen on the main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Show a Toast message indicating the failure and error message
                        Toast.makeText(getContext(), "API Request Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
            @Override
            public void onResponse(Call call, final Response response) {
                if (response.isSuccessful()) {
                    try {
                        final String responseData = response.body().string();
                        getActivity().runOnUiThread(() -> {
                            fetchCalls(responseData, appName);
                        });
                    } catch (Exception e) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "API Request Failed", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void fetchCalls(String jsonData, String platform) {
        try {
            Gson gson = new Gson();
            Type listType;

            if (platform.equals("WhatsApp") || platform.equals("Telegram")) {
                listType = new TypeToken<List<WhatsAppCall>>() {}.getType();
                List<WhatsAppCall> callList = gson.fromJson(jsonData, listType);
                whatsAppCallAdapter = new WhatsAppCallAdapter(callList);
                callsRecyclerView.setAdapter(whatsAppCallAdapter);
            } else {
                listType = new TypeToken<List<InstagramCalls>>() {}.getType();
                List<InstagramCalls> callList = gson.fromJson(jsonData, listType);
                instagramCallAdapter = new InstagramCallAdapter(callList);
                callsRecyclerView.setAdapter(instagramCallAdapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error parsing " + platform + " JSON", Toast.LENGTH_SHORT).show();
        }
    }
}















/*
package com.example.parent.features.socialmedia.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.socialmedia.adapter.instagram.InstagramCallAdapter;
import com.example.parent.features.socialmedia.adapter.whatsapp.WhatsAppCallAdapter;
import com.example.parent.features.socialmedia.model.instagram.InstagramCalls;
import com.example.parent.features.socialmedia.model.instagram.InstagramData;
import com.example.parent.features.socialmedia.model.whatsapp.WhatsAppCall;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CallsFragment extends Fragment {

    private RecyclerView callsRecyclerView;
    private WhatsAppCallAdapter whatsAppCallAdapter;
    private InstagramCallAdapter instagramCallAdapter;

    private static final String ARG_PLATFORM = "platform";
    private String platform;

    public CallsFragment() {
        // Required empty public constructor
    }

    // Static method to create a new instance of CallsFragment with platform info
    public static CallsFragment newInstance(String platform) {
        CallsFragment fragment = new CallsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLATFORM, platform);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            platform = getArguments().getString(ARG_PLATFORM);  // Retrieve platform from arguments
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View view = inflater.inflate(R.layout.calls_fragment, container, false);

        // Initialize RecyclerView
        callsRecyclerView = view.findViewById(R.id.whatsapp_calls_recycler_view);
        callsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Check if platform argument is passed and fetch data accordingly
        String platform = getArguments().getString("platform");

        if (platform != null && platform.equals("WhatsApp")) {
            fetchWhatsAppCalls();
        } else if (platform != null && platform.equals("Instagram")) {
            fetchInstagramCalls();  // Implement this if needed for Instagram
        } else if (platform != null && platform.equals("Facebook")) {
            fetchFacebookCalls();  // Implement this if needed for Facebook
        } else if (platform != null && platform.equals("SnapChat")) {
            fetchSnapChatCalls();  // Implement this if needed for SnapChat
        } else if (platform != null && platform.equals("Twitter")) {
            fetchTwitterCalls();  // Implement this if needed for Twitter
        } else if (platform != null && platform.equals("Telegram")) {
            fetchTelegramCalls();  // Implement this if needed for Telegram
        }

        return view;
    }


    // Method to fetch WhatsApp calls and set adapter
    private void fetchWhatsAppCalls() {
        Log.d("CallsFragment", "WhatsApp Calls method");

*/
/*        String jsonString = "["
                + "{\"name\": \"John Doe\", \"phone_number\": \"+917981214937\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-10 14:30:00\", \"duration\": \"5m 34s\"},"
                + "{\"name\": \"Jane Smith\", \"phone_number\": \"+919876543210\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-09 16:45:12\", \"duration\": \"12m 10s\"},"
                + "{\"name\": \"Mike Johnson\", \"phone_number\": \"+919988776655\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-08 19:20:30\", \"duration\": \"0\"},"
                + "{\"name\": \"Emily Brown\", \"phone_number\": \"+917003221101\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-07 10:15:50\", \"duration\": \"8m 30s\"}"
                + "]";*//*




        String jsonString = "["
                + "{\"name\": \"John Doe\", \"phone_number\": \"+917981214937\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-10 14:30:00\", \"duration\": \"5m 34s\"},"
                + "{\"name\": \"Jane Smith\", \"phone_number\": \"+919876543210\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-09 16:45:12\", \"duration\": \"12m 10s\"},"
                + "{\"name\": \"Mike Johnson\", \"phone_number\": \"+919988776655\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-08 19:20:30\", \"duration\": \"0\"},"
                + "{\"name\": \"Emily Brown\", \"phone_number\": \"+917003221101\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-07 10:15:50\", \"duration\": \"8m 30s\"},"
                + "{\"name\": \"Chris Williams\", \"phone_number\": \"+919067890123\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-06 13:15:22\", \"duration\": \"3m 45s\"},"
                + "{\"name\": \"Amanda Miller\", \"phone_number\": \"+918912345678\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-05 11:20:50\", \"duration\": \"15m 22s\"},"
                + "{\"name\": \"David Lee\", \"phone_number\": \"+917032109876\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-04 09:45:00\", \"duration\": \"2m 0s\"},"
                + "{\"name\": \"Sarah Taylor\", \"phone_number\": \"+917876543210\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-03 18:05:15\", \"duration\": \"10m 5s\"},"
                + "{\"name\": \"Kevin White\", \"phone_number\": \"+919827364511\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-02 21:30:45\", \"duration\": \"4m 55s\"},"
                + "{\"name\": \"Olivia Harris\", \"phone_number\": \"+919345678901\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-01 17:25:30\", \"duration\": \"0\"},"
                + "{\"name\": \"Jason Scott\", \"phone_number\": \"+918188822244\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-30 14:55:12\", \"duration\": \"7m 30s\"},"
                + "{\"name\": \"Sophia Adams\", \"phone_number\": \"+917654321098\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-29 20:10:25\", \"duration\": \"6m 45s\"},"
                + "{\"name\": \"Ethan Walker\", \"phone_number\": \"+918977665544\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-28 15:40:05\", \"duration\": \"9m 10s\"},"
                + "{\"name\": \"Lucas Moore\", \"phone_number\": \"+919876543567\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-27 10:30:00\", \"duration\": \"11m 30s\"},"
                + "{\"name\": \"Megan King\", \"phone_number\": \"+917654321234\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-26 12:15:30\", \"duration\": \"0\"},"
                + "{\"name\": \"Isabella Johnson\", \"phone_number\": \"+919688777555\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-25 19:45:12\", \"duration\": \"13m 0s\"},"
                + "{\"name\": \"Daniel Martinez\", \"phone_number\": \"+918766554433\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-24 08:30:15\", \"duration\": \"1m 40s\"}"
                + "]";


        try {
            // Parse the JSON string using Gson
            Gson gson = new Gson();
            Type listType = new TypeToken<List<WhatsAppCall>>() {}.getType();
            // Get the call data array from the response
            List<WhatsAppCall> callList = gson.fromJson(jsonString, listType);

            // Set the adapter to RecyclerView
            whatsAppCallAdapter = new WhatsAppCallAdapter(callList);
            // After parsing the JSON
            Log.d("CallsFragment", "WhatsApp Calls List Size: " + callList.size());
            callsRecyclerView.setAdapter(whatsAppCallAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to fetch Instagram calls and set adapter (for future implementation)
    private void fetchInstagramCalls() {
        String jsonData = "{\n" +
                "\"app_name\": \"instagram\",\n" +
                "\"package_name\": \"com.instagram.android\",\n" +
                "\"call_log\": [\n" +
                "    {\n" +
                "        \"user_id\": \"user_12345\",\n" +
                "        \"call_mode\": \"audio\",\n" +
                "        \"call_type\": \"incoming\",\n" +
                "        \"time\": \"2025-03-03T10:15:00Z\",\n" +
                "        \"duration\": 120\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"call_mode\": \"video\",\n" +
                "        \"call_type\": \"outgoing\",\n" +
                "        \"time\": \"2025-03-03T11:00:00Z\",\n" +
                "        \"duration\": 180\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"call_mode\": \"audio\",\n" +
                "        \"call_type\": \"incoming\",\n" +
                "        \"time\": \"2025-03-03T12:00:00Z\",\n" +
                "        \"duration\": 150\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"call_mode\": \"video\",\n" +
                "        \"call_type\": \"outgoing\",\n" +
                "        \"time\": \"2025-03-03T13:00:00Z\",\n" +
                "        \"duration\": 200\n" +
                "    }\n" +
                "],\n" +
                "\"message_log\": [\n" +
                "    {\n" +
                "        \"user_id\": \"user_12345\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hey, how's it going?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:30:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Not bad, just chilling. How about you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Same here. Just catching up on some work. What's new?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:32:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"I'm doing great, thanks! What about you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been busy with some projects. We should catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:33:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Totally! I miss our hangouts. Let's plan something.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:34:20Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hello! Long time no see!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I know, right? It's been ages. How have you been?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been good! Been traveling a lot for work. How about you?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Same here! Busy with work. We should catch up soon!\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:18:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Let's catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Definitely! It's been so long. What have you been up to?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Work has been keeping me busy, but I have some free time next week.\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Great! Let's plan for next week. I'll check my schedule.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:19:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]\n" +
                "}";



        try {
            // Parse the JSON string using Gson for Instagram calls
            Gson gson = new Gson();
            // Parse the call_log from the JSON string
            InstagramData instagramData = gson.fromJson(jsonData, InstagramData.class);
            List<InstagramCalls> callList = instagramData.getInstagramCalls();

            // Set the adapter for Instagram calls
            instagramCallAdapter = new InstagramCallAdapter(callList);
            callsRecyclerView.setAdapter(instagramCallAdapter);
            Log.d("Instagram Calls Data ", "Instagram Calls: " + callList.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void fetchFacebookCalls() {
        String jsonData = "{\n" +
                "\"app_name\": \"facebook\",\n" +
                "\"package_name\": \"com.facebook.android\",\n" +
                "\"call_log\": [\n" +
                "    {\n" +
                "        \"user_id\": \"user_12345\",\n" +
                "        \"call_mode\": \"audio\",\n" +
                "        \"call_type\": \"incoming\",\n" +
                "        \"time\": \"2025-03-03T10:15:00Z\",\n" +
                "        \"duration\": 120\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"call_mode\": \"video\",\n" +
                "        \"call_type\": \"outgoing\",\n" +
                "        \"time\": \"2025-03-03T11:00:00Z\",\n" +
                "        \"duration\": 180\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"call_mode\": \"audio\",\n" +
                "        \"call_type\": \"incoming\",\n" +
                "        \"time\": \"2025-03-03T12:00:00Z\",\n" +
                "        \"duration\": 150\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"call_mode\": \"video\",\n" +
                "        \"call_type\": \"outgoing\",\n" +
                "        \"time\": \"2025-03-03T13:00:00Z\",\n" +
                "        \"duration\": 200\n" +
                "    }\n" +
                "],\n" +
                "\"message_log\": [\n" +
                "    {\n" +
                "        \"user_id\": \"user_12345\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hey, how's it going?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:30:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Not bad, just chilling. How about you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Same here. Just catching up on some work. What's new?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:32:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"I'm doing great, thanks! What about you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been busy with some projects. We should catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:33:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Totally! I miss our hangouts. Let's plan something.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:34:20Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hello! Long time no see!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I know, right? It's been ages. How have you been?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"I've been good! Been traveling a lot for work. How about you?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Same here! Busy with work. We should catch up soon!\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:18:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Let's catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Definitely! It's been so long. What have you been up to?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Work has been keeping me busy, but I have some free time next week.\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:17:45Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Great! Let's plan for next week. I'll check my schedule.\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:19:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]\n" +
                "}";



        try {
            // Parse the JSON string using Gson for Instagram calls
            Gson gson = new Gson();
            // Parse the call_log from the JSON string
            InstagramData instagramData = gson.fromJson(jsonData, InstagramData.class);
            List<InstagramCalls> callList = instagramData.getInstagramCalls();

            // Set the adapter for Instagram calls
            instagramCallAdapter = new InstagramCallAdapter(callList);
            callsRecyclerView.setAdapter(instagramCallAdapter);
            Log.d("Facebook Calls Data ", "Facebook Calls: " + callList.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fetchSnapChatCalls() {
        String jsonDataSnapchat = "{\n" +
                "\"app_name\": \"snapchat\",\n" +
                "\"package_name\": \"com.snapchat.android\",\n" +
                "\"contacts\": [\n" +
                "    {\n" +
                "        \"user_id\": \"user_12345\",\n" +
                "        \"user_name\": \"John Doe\",\n" +
                "        \"contact_snap\": \"@johndoe\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"user_name\": \"Jane Smith\",\n" +
                "        \"contact_snap\": \"@janesmith\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"user_name\": \"Tom Harris\",\n" +
                "        \"contact_snap\": \"@tomharris\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"user_name\": \"Emily Davis\",\n" +
                "        \"contact_snap\": \"@emilydavis\"\n" +
                "    }\n" +
                "],\n" +
                "\"call_log\": [\n" +
                "    {\n" +
                "        \"user_id\": \"user_12345\",\n" +
                "        \"call_mode\": \"audio\",\n" +
                "        \"call_type\": \"incoming\",\n" +
                "        \"time\": \"2025-03-03T10:15:00Z\",\n" +
                "        \"duration\": 120\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"call_mode\": \"video\",\n" +
                "        \"call_type\": \"outgoing\",\n" +
                "        \"time\": \"2025-03-03T11:00:00Z\",\n" +
                "        \"duration\": 180\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"call_mode\": \"audio\",\n" +
                "        \"call_type\": \"incoming\",\n" +
                "        \"time\": \"2025-03-03T12:00:00Z\",\n" +
                "        \"duration\": 150\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"call_mode\": \"video\",\n" +
                "        \"call_type\": \"outgoing\",\n" +
                "        \"time\": \"2025-03-03T13:00:00Z\",\n" +
                "        \"duration\": 200\n" +
                "    }\n" +
                "],\n" +
                "\"message_log\": [\n" +
                "    {\n" +
                "        \"user_id\": \"user_12345\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hey! What's up?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:30:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Not much, just chilling. How about you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"spam\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Same here, working on some things. Any plans?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:32:45Z\",\n" +
                "                \"classification\": \"warning\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hey! I'm doing well. How are you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Good to hear! Been busy lately. Let's catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:33:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hello! It's been a while!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Yeah, it's been too long! How have you been?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hey! Let's meet up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Definitely! How's next week for you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]\n" +
                "}";

        try {
            // Parse the JSON string using Gson for Instagram calls
            Gson gson = new Gson();
            // Parse the call_log from the JSON string
            InstagramData instagramData = gson.fromJson(jsonDataSnapchat, InstagramData.class);
            List<InstagramCalls> callList = instagramData.getInstagramCalls();

            // Set the adapter for Instagram calls
            instagramCallAdapter = new InstagramCallAdapter(callList);
            callsRecyclerView.setAdapter(instagramCallAdapter);
            Log.d("SnapChat Calls Data ", "SnapChat Calls: " + callList.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchTwitterCalls() {
        String jsonDataTwitter = "{\n" +
                "\"app_name\": \"snapchat\",\n" +
                "\"package_name\": \"com.snapchat.android\",\n" +
                "\"contacts\": [\n" +
                "    {\n" +
                "        \"user_id\": \"user_12345\",\n" +
                "        \"user_name\": \"John Doe\",\n" +
                "        \"contact_snap\": \"@johndoe\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"user_name\": \"Jane Smith\",\n" +
                "        \"contact_snap\": \"@janesmith\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"user_name\": \"Tom Harris\",\n" +
                "        \"contact_snap\": \"@tomharris\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"user_name\": \"Emily Davis\",\n" +
                "        \"contact_snap\": \"@emilydavis\"\n" +
                "    }\n" +
                "],\n" +
                "\"call_log\": [\n" +
                "    {\n" +
                "        \"user_id\": \"user_12345\",\n" +
                "        \"call_mode\": \"audio\",\n" +
                "        \"call_type\": \"incoming\",\n" +
                "        \"time\": \"2025-03-03T10:15:00Z\",\n" +
                "        \"duration\": 120\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"call_mode\": \"video\",\n" +
                "        \"call_type\": \"outgoing\",\n" +
                "        \"time\": \"2025-03-03T11:00:00Z\",\n" +
                "        \"duration\": 180\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"call_mode\": \"audio\",\n" +
                "        \"call_type\": \"incoming\",\n" +
                "        \"time\": \"2025-03-03T12:00:00Z\",\n" +
                "        \"duration\": 150\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"call_mode\": \"video\",\n" +
                "        \"call_type\": \"outgoing\",\n" +
                "        \"time\": \"2025-03-03T13:00:00Z\",\n" +
                "        \"duration\": 200\n" +
                "    }\n" +
                "],\n" +
                "\"message_log\": [\n" +
                "    {\n" +
                "        \"user_id\": \"user_12345\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hey! What's up?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:30:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Not much, just chilling. How about you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"spam\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Same here, working on some things. Any plans?\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:32:45Z\",\n" +
                "                \"classification\": \"warning\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_67890\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hey! I'm doing well. How are you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T10:31:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Good to hear! Been busy lately. Let's catch up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T10:33:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_11111\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hello! It's been a while!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T12:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Yeah, it's been too long! How have you been?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T12:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"user_id\": \"user_22222\",\n" +
                "        \"message_details\": [\n" +
                "            {\n" +
                "                \"message\": \"Hey! Let's meet up soon!\",\n" +
                "                \"message_type\": \"sent\",\n" +
                "                \"message_time\": \"2025-03-03T13:15:00Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"message\": \"Definitely! How's next week for you?\",\n" +
                "                \"message_type\": \"received\",\n" +
                "                \"message_time\": \"2025-03-03T13:16:30Z\",\n" +
                "                \"classification\": \"ham\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]\n" +
                "}";

        try {
            // Parse the JSON string using Gson for Instagram calls
            Gson gson = new Gson();
            // Parse the call_log from the JSON string
            InstagramData instagramData = gson.fromJson(jsonDataTwitter, InstagramData.class);
            List<InstagramCalls> callList = instagramData.getInstagramCalls();

            // Set the adapter for Instagram calls
            instagramCallAdapter = new InstagramCallAdapter(callList);
            callsRecyclerView.setAdapter(instagramCallAdapter);
            Log.d("SnapChat Calls Data ", "SnapChat Calls: " + callList.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchTelegramCalls() {

        String jsonString = "["
                + "{\"name\": \"John Doe\", \"phone_number\": \"+917981214937\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-10 14:30:00\", \"duration\": \"5m 34s\"},"
                + "{\"name\": \"Jane Smith\", \"phone_number\": \"+919876543210\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-09 16:45:12\", \"duration\": \"12m 10s\"},"
                + "{\"name\": \"Mike Johnson\", \"phone_number\": \"+919988776655\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-08 19:20:30\", \"duration\": \"0\"},"
                + "{\"name\": \"Emily Brown\", \"phone_number\": \"+917003221101\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-07 10:15:50\", \"duration\": \"8m 30s\"},"
                + "{\"name\": \"Chris Williams\", \"phone_number\": \"+919067890123\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-06 13:15:22\", \"duration\": \"3m 45s\"},"
                + "{\"name\": \"Amanda Miller\", \"phone_number\": \"+918912345678\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-05 11:20:50\", \"duration\": \"15m 22s\"},"
                + "{\"name\": \"David Lee\", \"phone_number\": \"+917032109876\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-04 09:45:00\", \"duration\": \"2m 0s\"},"
                + "{\"name\": \"Sarah Taylor\", \"phone_number\": \"+917876543210\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-03 18:05:15\", \"duration\": \"10m 5s\"},"
                + "{\"name\": \"Kevin White\", \"phone_number\": \"+919827364511\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-12-02 21:30:45\", \"duration\": \"4m 55s\"},"
                + "{\"name\": \"Olivia Harris\", \"phone_number\": \"+919345678901\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-12-01 17:25:30\", \"duration\": \"0\"},"
                + "{\"name\": \"Jason Scott\", \"phone_number\": \"+918188822244\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-30 14:55:12\", \"duration\": \"7m 30s\"},"
                + "{\"name\": \"Sophia Adams\", \"phone_number\": \"+917654321098\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-29 20:10:25\", \"duration\": \"6m 45s\"},"
                + "{\"name\": \"Ethan Walker\", \"phone_number\": \"+918977665544\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-28 15:40:05\", \"duration\": \"9m 10s\"},"
                + "{\"name\": \"Lucas Moore\", \"phone_number\": \"+919876543567\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-27 10:30:00\", \"duration\": \"11m 30s\"},"
                + "{\"name\": \"Megan King\", \"phone_number\": \"+917654321234\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-26 12:15:30\", \"duration\": \"0\"},"
                + "{\"name\": \"Isabella Johnson\", \"phone_number\": \"+919688777555\", \"call_type\": \"outgoing\", \"call_mode\": \"video\", \"time\": \"2024-11-25 19:45:12\", \"duration\": \"13m 0s\"},"
                + "{\"name\": \"Daniel Martinez\", \"phone_number\": \"+918766554433\", \"call_type\": \"incoming\", \"call_mode\": \"voice\", \"time\": \"2024-11-24 08:30:15\", \"duration\": \"1m 40s\"}"
                + "]";


        try {
            // Parse the JSON string using Gson
            Gson gson = new Gson();
            Type listType = new TypeToken<List<WhatsAppCall>>() {}.getType();
            // Get the call data array from the response
            List<WhatsAppCall> callList = gson.fromJson(jsonString, listType);

            // Set the adapter to RecyclerView
            whatsAppCallAdapter = new WhatsAppCallAdapter(callList);
            // After parsing the JSON
            Log.d("CallsFragment", "WhatsApp Calls List Size: " + callList.size());
            callsRecyclerView.setAdapter(whatsAppCallAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
        }
    }
}
*/
