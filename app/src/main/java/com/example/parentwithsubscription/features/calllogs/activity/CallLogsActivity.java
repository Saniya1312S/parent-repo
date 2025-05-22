package com.example.parentwithsubscription.features.calllogs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.calllogs.adapter.CallLogsAdapter;
import com.example.parentwithsubscription.features.calllogs.listener.OnCallLogClickListener;
import com.example.parentwithsubscription.features.calllogs.model.CallDataSummary;
import com.example.parentwithsubscription.features.calllogs.model.CallLogs;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class CallLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CallLogsAdapter contactAdapter;
    private List<CallLogs> contactList;
    private String CALL_SUMMARY_URL = URIConstants.CALL_SUMMARY_URL;
    private String CALL_LOGS_URL = URIConstants.CALL_LOGS_URL;

    // TextViews for displaying call statistics
    TextView totalIncomingCalls, totalOutgoingCalls;
    TextView totalMissedCalls;

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the OkHttpClient
        client = new OkHttpClient();

        // Initialize the TextViews
        totalIncomingCalls = findViewById(R.id.totalIncomingCalls);
        totalOutgoingCalls = findViewById(R.id.totalOutgoingCalls);
        totalMissedCalls = findViewById(R.id.totalMissedCalls);

        // Fetch call logs from the API
        fetchCallLogs();
        fetchCallsDetails();
    }

    private void fetchCallsDetails() {
        // URL for the API endpoint

        // Create a request
        Request request = new Request.Builder()
                .url(CALL_SUMMARY_URL)
                .build();

        // Asynchronous request using OkHttpClient
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., no internet)
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the JSON response as a string
                    String jsonResponse = response.body().string();

                    // Parse the JSON response using Gson
                    Gson gson = new Gson();

                    try {
                        // Parse the JSON response as a single CallDataSummary object
                        CallDataSummary callData = gson.fromJson(jsonResponse, CallDataSummary.class);

                        // Check if callData is not null
                        if (callData != null) {
                            // Update UI with the fetched data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    totalIncomingCalls.setText(String.valueOf(callData.getTotalIncomingCalls()));
                                    totalOutgoingCalls.setText(String.valueOf(callData.getTotalOutgoingCalls()));
                                    totalMissedCalls.setText(String.valueOf(callData.getTotalMissedCalls()));
                                }
                            });
                        }
                    } catch (JsonSyntaxException e) {
                        // Handle parsing failure
                        e.printStackTrace();
                        Log.e("JSON Parsing Error", jsonResponse);
                    }
                }
            }
        });
    }



    private void fetchCallLogs() {
        // The API URL to fetch the current day rolling call logs

        // Create a request object
        Request request = new Request.Builder()
                .url(CALL_LOGS_URL)
                .build();

        // Asynchronous HTTP request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., no internet connection)
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CallLogsActivity.this, "Failed to load call logs", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the JSON response as a string
                    String jsonResponse = response.body().string();

                    // Print the raw API response to log
                    Log.d("API Response", jsonResponse);

                    // Parse the JSON response using Gson
                    Gson gson = new Gson();

                    // Parse the response as an array (root array contains a single object)
                    Type responseType = new TypeToken<List<CallLogs>>(){}.getType();  // Updated to match the structure
                    List<CallLogs> responseList = gson.fromJson(jsonResponse, responseType);

                    // Extract the call logs list
                    if (responseList != null && !responseList.isEmpty()) {
                        List<CallLogs> contactList = responseList;

                        if (contactList != null && !contactList.isEmpty()) {
                            // Set up the adapter with the list of contacts (call logs)
                            contactAdapter = new CallLogsAdapter(contactList, new OnCallLogClickListener() {
                                @Override
                                public void onContactClick(CallLogs contact) {
                                    // Open the detailed call logs activity when a contact is clicked
                                    Intent intent = new Intent(CallLogsActivity.this, CallLogsDetailActivity.class);
                                    intent.putExtra("contact_name", contact.getName());
                                    intent.putExtra("contact_phone_number", contact.getPhoneNumber());
                                    intent.putExtra("contact_calls", new Gson().toJson(contact.getCallLogsDetails()));
                                    startActivity(intent);
                                }

                                @Override
                                public void onContactBlock(CallLogs contact) {
                                    // Block the contact
                                    contact.setBlocked(true);

                                    // Use RecyclerView.post() to notify adapter after layout pass
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Find position of the contact in the list
                                            int position = contactList.indexOf(contact);

                                            // Notify that the specific contact has changed (only update that contact)
                                            contactAdapter.notifyItemChanged(position);

                                            // Show the block message
                                            Toast.makeText(CallLogsActivity.this, "Contact " + contact.getName() + " has been blocked.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onContactUnblock(CallLogs contact) {
                                    // Unblock the contact
                                    contact.setBlocked(false);

                                    // Use RecyclerView.post() to notify adapter after layout pass
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Find position of the contact in the list
                                            int position = contactList.indexOf(contact);

                                            // Notify that the specific contact has changed (only update that contact)
                                            contactAdapter.notifyItemChanged(position);

                                            // Show the unblock message
                                            Toast.makeText(CallLogsActivity.this, "Contact " + contact.getName() + " has been unblocked.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }, CallLogsActivity.this, recyclerView); // Pass RecyclerView here

                            // Set the adapter to the RecyclerView
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(contactAdapter);
                                }
                            });
                        } else {
                            // Handle empty contact list
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CallLogsActivity.this, "No call logs available", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // Handle the case where the response list is empty or malformed
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CallLogsActivity.this, "Malformed response", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Handle error if response is not successful
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CallLogsActivity.this, "Error loading call logs", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        });
    }
}
















/*
package com.example.parentwithsubscription.features.calllogs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.calllogs.adapter.CallLogsAdapter;
import com.example.parentwithsubscription.features.calllogs.adapter.MissedCallsAdapter;
import com.example.parentwithsubscription.features.calllogs.listener.OnCallLogClickListener;
import com.example.parentwithsubscription.features.calllogs.model.CallDataSummary;
import com.example.parentwithsubscription.features.calllogs.model.CallLogs;
import com.example.parentwithsubscription.features.calllogs.model.MissedCall;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CallLogsAdapter contactAdapter;
    private List<CallLogs> contactList;
    private String CALL_SUMMARY_URL = URIConstants.CALL_SUMMARY_URL;
    private String CALL_LOGS_URL = URIConstants.CALL_LOGS_URL;

    // TextViews for displaying call statistics
    TextView totalIncomingCalls, totalOutgoingCalls, totalIncomingDuration, totalOutgoingDuration;
    TextView totalMissedCalls;

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the OkHttpClient
        client = new OkHttpClient();

        // Initialize the TextViews
        totalIncomingCalls = findViewById(R.id.totalIncomingCalls);
        totalOutgoingCalls = findViewById(R.id.totalOutgoingCalls);
        totalMissedCalls = findViewById(R.id.totalMissedCalls);

        // Fetch call logs from the API
        fetchCallLogs();
        fetchCallsDetails();
//        fetchMissedCallsDetails();
    }

*/
/*
    private void fetchMissedCallsDetails() {
        // Inside your Activity or Fragment

        RecyclerView recyclerViewIncoming = findViewById(R.id.recyclerViewIncoming);
        RecyclerView recyclerViewOutgoing = findViewById(R.id.recyclerViewOutgoing);

// Sample data (in reality, you would parse this from an API response)
        String jsonResponse = "{\n" +
                "    \"frequentMissedOutgoing\": [\n" +
                "        {\n" +
                "            \"missedOutgoingCalls\": 3,\n" +
                "            \"phone_number\": \"7523556029\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"frequentMissedIncoming\": [\n" +
                "        {\n" +
                "            \"missedIncomingCalls\": 3,\n" +
                "            \"phone_number\": \"7222905772\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"missedIncomingCalls\": 3,\n" +
                "            \"phone_number\": \"7719583509\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

// Use a JSON library like Gson to parse the JSON response
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> responseMap = gson.fromJson(jsonResponse, type);

// Parse incoming calls
        List<Map<String, Object>> incomingCallsJson = (List<Map<String, Object>>) responseMap.get("frequentMissedIncoming");
        List<MissedCall> incomingCalls = new ArrayList<>();
        for (Map<String, Object> callData : incomingCallsJson) {
            String phoneNumber = (String) callData.get("phone_number");
            int missedCalls = ((Double) callData.get("missedIncomingCalls")).intValue();
            incomingCalls.add(new MissedCall(phoneNumber, missedCalls));
        }

// Parse outgoing calls
        List<Map<String, Object>> outgoingCallsJson = (List<Map<String, Object>>) responseMap.get("frequentMissedOutgoing");
        List<MissedCall> outgoingCalls = new ArrayList<>();
        for (Map<String, Object> callData : outgoingCallsJson) {
            String phoneNumber = (String) callData.get("phone_number");
            int missedCalls = ((Double) callData.get("missedOutgoingCalls")).intValue();
            outgoingCalls.add(new MissedCall(phoneNumber, missedCalls));
        }

// Set up adapters for RecyclerViews
        MissedCallsAdapter incomingAdapter = new MissedCallsAdapter(incomingCalls);
        MissedCallsAdapter outgoingAdapter = new MissedCallsAdapter(outgoingCalls);

// Set the adapters to the RecyclerViews
        recyclerViewIncoming.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewIncoming.setAdapter(incomingAdapter);

        recyclerViewOutgoing.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOutgoing.setAdapter(outgoingAdapter);

    }
*//*


    */
/*private void fetchMissedCallsDetails() {

        RecyclerView recyclerViewIncoming = findViewById(R.id.recyclerViewIncoming);
        RecyclerView recyclerViewOutgoing = findViewById(R.id.recyclerViewOutgoing);
        // URL for the API endpoint
        String url = "http://192.168.0.105:5000/call/current_day_missed_calls?device_id=5551233030";

        // Create a request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Asynchronous request using OkHttpClient
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., no internet)
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the JSON response as a string
                    String jsonResponse = response.body().string();

                    // Log the response for debugging
                    Log.d("API Response", jsonResponse);

                    // Use Gson to parse the JSON response
                    Gson gson = new Gson();

                    // Parse the outer array (this is a List of Maps)
                    Type type = new TypeToken<List<Map<String, Object>>>(){}.getType();
                    List<Map<String, Object>> responseList = gson.fromJson(jsonResponse, type);

                    // Get the first item in the array
                    Map<String, Object> responseMap = responseList.get(0);

                    // Parse incoming calls
                    List<Map<String, Object>> incomingCallsJson = (List<Map<String, Object>>) responseMap.get("frequentMissedIncoming");
                    List<MissedCall> incomingCalls = new ArrayList<>();
                    for (Map<String, Object> callData : incomingCallsJson) {
                        String phoneNumber = (String) callData.get("phone_number");
                        int missedCalls = ((Double) callData.get("missedIncomingCalls")).intValue();
                        incomingCalls.add(new MissedCall(phoneNumber, missedCalls));
                    }

                    // Parse outgoing calls
                    List<Map<String, Object>> outgoingCallsJson = (List<Map<String, Object>>) responseMap.get("frequentMissedOutgoing");
                    List<MissedCall> outgoingCalls = new ArrayList<>();
                    for (Map<String, Object> callData : outgoingCallsJson) {
                        String phoneNumber = (String) callData.get("phone_number");
                        int missedCalls = ((Double) callData.get("missedOutgoingCalls")).intValue();
                        outgoingCalls.add(new MissedCall(phoneNumber, missedCalls));
                    }

                    // Run the UI update on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Set up adapters for RecyclerViews
                            MissedCallsAdapter incomingAdapter = new MissedCallsAdapter(incomingCalls);
                            MissedCallsAdapter outgoingAdapter = new MissedCallsAdapter(outgoingCalls);

                            // Set the adapters to the RecyclerViews
                            recyclerViewIncoming.setLayoutManager(new LinearLayoutManager(CallLogsActivity.this));
                            recyclerViewIncoming.setAdapter(incomingAdapter);

                            recyclerViewOutgoing.setLayoutManager(new LinearLayoutManager(CallLogsActivity.this));
                            recyclerViewOutgoing.setAdapter(outgoingAdapter);
                        }
                    });
                }
            }
        });
    }
*//*

    private void fetchMissedCallsDetails() {
//        RecyclerView recyclerViewIncoming = findViewById(R.id.recyclerViewIncoming);
//        RecyclerView recyclerViewOutgoing = findViewById(R.id.recyclerViewOutgoing);
        // URL for the API endpoint
        String url = "http://192.168.0.113:5000/call/current_day_missed_calls?device_id=5551233030";

        // Create a request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Asynchronous request using OkHttpClient
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., no internet)
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the JSON response as a string
                    String jsonResponse = response.body().string();

                    // Log the response for debugging
                    Log.d("API Response", jsonResponse);

                    // Use Gson to parse the JSON response
                    Gson gson = new Gson();

                    // Parse the outer array (this is a List of Maps)
                    Type type = new TypeToken<List<Map<String, Object>>>(){}.getType();
                    List<Map<String, Object>> responseList = gson.fromJson(jsonResponse, type);

                    // Get the first item in the array
                    Map<String, Object> responseMap = responseList.get(0);

                    // Parse incoming calls
                    List<Map<String, Object>> incomingCallsJson = (List<Map<String, Object>>) responseMap.get("frequentMissedIncoming");
                    List<MissedCall> incomingCalls = new ArrayList<>();
                    for (Map<String, Object> callData : incomingCallsJson) {
                        String phoneNumber = (String) callData.get("phone_number");
                        String name = (String) callData.get("name"); // Get the name
                        int missedCalls = ((Double) callData.get("missedIncomingCalls")).intValue();
                        incomingCalls.add(new MissedCall(phoneNumber, missedCalls, name));
                    }

                    // Parse outgoing calls
                    List<Map<String, Object>> outgoingCallsJson = (List<Map<String, Object>>) responseMap.get("frequentMissedOutgoing");
                    List<MissedCall> outgoingCalls = new ArrayList<>();
                    for (Map<String, Object> callData : outgoingCallsJson) {
                        String phoneNumber = (String) callData.get("phone_number");
                        String name = (String) callData.get("name"); // Get the name
                        int missedCalls = ((Double) callData.get("missedOutgoingCalls")).intValue();
                        outgoingCalls.add(new MissedCall(phoneNumber, missedCalls, name));
                    }

                    // Run the UI update on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Set up adapters for RecyclerViews
                            MissedCallsAdapter incomingAdapter = new MissedCallsAdapter(incomingCalls);
                            MissedCallsAdapter outgoingAdapter = new MissedCallsAdapter(outgoingCalls);

                            // Set the adapters to the RecyclerViews
//                            recyclerViewIncoming.setLayoutManager(new LinearLayoutManager(CallLogsActivity.this));
//                            recyclerViewIncoming.setAdapter(incomingAdapter);
//
//                            recyclerViewOutgoing.setLayoutManager(new LinearLayoutManager(CallLogsActivity.this));
//                            recyclerViewOutgoing.setAdapter(outgoingAdapter);
                        }
                    });
                }
            }
        });
    }


    */
/*private void fetchCallsDetails() {
        // URL for the API endpoint
        String url = "http://192.168.0.107:5000/call/get_call_summary?device_id=5551233030";

        // Create a request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Asynchronous request using OkHttpClient
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., no internet)
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the JSON response as a string
                    String jsonResponse = response.body().string();

                    // Parse the JSON response using Gson
                    Gson gson = new Gson();

                    try {
                        // Since the response is an array, we parse it as a List of CallDataSummary
                        Type listType = new TypeToken<List<CallDataSummary>>() {}.getType();
                        List<CallDataSummary> callDataList = gson.fromJson(jsonResponse, listType);

                        // Check if the list is not empty
                        if (callDataList != null && !callDataList.isEmpty()) {
                            CallDataSummary callData = callDataList.get(0); // Get the first item from the list

                            // Update UI with the fetched data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    totalIncomingCalls.setText("" + callData.getTotalIncomingCalls() + "");
                                    totalOutgoingCalls.setText("" + callData.getTotalOutgoingCalls() + "");
                                    totalMissedCalls.setText("" + callData.getTotalMissedCalls() + "");
                                }
                            });
                        }
                    } catch (JsonSyntaxException e) {
                        // Handle parsing failure
                        e.printStackTrace();
                        Log.e("JSON Parsing Error", jsonResponse);
                    }
                }
            }
        });
    }*//*


    private void fetchCallsDetails() {
        // URL for the API endpoint

        // Create a request
        Request request = new Request.Builder()
                .url(CALL_SUMMARY_URL)
                .build();

        // Asynchronous request using OkHttpClient
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., no internet)
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the JSON response as a string
                    String jsonResponse = response.body().string();

                    // Parse the JSON response using Gson
                    Gson gson = new Gson();

                    try {
                        // Parse the JSON response as a single CallDataSummary object
                        CallDataSummary callData = gson.fromJson(jsonResponse, CallDataSummary.class);

                        // Check if callData is not null
                        if (callData != null) {
                            // Update UI with the fetched data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    totalIncomingCalls.setText(String.valueOf(callData.getTotalIncomingCalls()));
                                    totalOutgoingCalls.setText(String.valueOf(callData.getTotalOutgoingCalls()));
                                    totalMissedCalls.setText(String.valueOf(callData.getTotalMissedCalls()));
                                }
                            });
                        }
                    } catch (JsonSyntaxException e) {
                        // Handle parsing failure
                        e.printStackTrace();
                        Log.e("JSON Parsing Error", jsonResponse);
                    }
                }
            }
        });
    }



    private void fetchCallLogs() {
        // The API URL to fetch the current day rolling call logs

        // Create a request object
        Request request = new Request.Builder()
                .url(CALL_LOGS_URL)
                .build();

        // Asynchronous HTTP request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., no internet connection)
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CallLogsActivity.this, "Failed to load call logs", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            */
/*@Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the JSON response as a string
                    String jsonResponse = response.body().string();

                    // Print the raw API response to log
                    Log.d("API Response", jsonResponse);

                    // Parse the JSON response using Gson
                    Gson gson = new Gson();

                    // Parse the response as an array (root array contains a single object)
                    Type responseType = new TypeToken<List<Map<String, List<CallLogs>>>>(){}.getType();
                    List<Map<String, List<CallLogs>>> responseList = gson.fromJson(jsonResponse, responseType);

                    // Extract the first object (should be the only object in the array)
                    if (responseList != null && !responseList.isEmpty()) {
                        Map<String, List<CallLogs>> responseMap = responseList.get(0);
                        List<CallLogs> contactList = responseMap.get("call_logs");

                        if (contactList != null && !contactList.isEmpty()) {
                            // Set up the adapter with the list of contacts (call logs)
                            contactAdapter = new CallLogsAdapter(contactList, new OnCallLogClickListener() {
                                @Override
                                public void onContactClick(CallLogs contact) {
                                    // Open the detailed call logs activity when a contact is clicked
                                    Intent intent = new Intent(CallLogsActivity.this, CallLogsDetailActivity.class);
                                    intent.putExtra("contact_name", contact.getName());
                                    intent.putExtra("contact_phone_number", contact.getPhoneNumber());
                                    intent.putExtra("contact_calls", new Gson().toJson(contact.getCallLogsDetails()));
                                    startActivity(intent);
                                }

                                @Override
                                public void onContactBlock(CallLogs contact) {
                                    // Set the blocked flag to true for the selected contact
                                    contact.setBlocked(true);

                                    // Notify the adapter that the data has changed
                                    contactAdapter.notifyDataSetChanged();

                                    // Correct the context issue: Use CallLogsActivity.this to refer to the context
                                    Toast.makeText(CallLogsActivity.this, "Contact " + contact.getName() + " has been blocked.", Toast.LENGTH_SHORT).show();
                                }
                            },CallLogsActivity.this);

                            // Set the adapter to the RecyclerView
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(contactAdapter);
                                }
                            });
                        } else {
                            // Handle empty contact list
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CallLogsActivity.this, "No call logs available", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // Handle the case where the response list is empty or malformed
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CallLogsActivity.this, "Malformed response", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Handle error if response is not successful
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CallLogsActivity.this, "Error loading call logs", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }*//*





            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Get the JSON response as a string
                    String jsonResponse = response.body().string();

                    // Print the raw API response to log
                    Log.d("API Response", jsonResponse);

                    // Parse the JSON response using Gson
                    Gson gson = new Gson();

                    // Parse the response as an array (root array contains a single object)
                    Type responseType = new TypeToken<List<CallLogs>>(){}.getType();  // Updated to match the structure
                    List<CallLogs> responseList = gson.fromJson(jsonResponse, responseType);

                    // Extract the call logs list
                    if (responseList != null && !responseList.isEmpty()) {
                        List<CallLogs> contactList = responseList;

                        if (contactList != null && !contactList.isEmpty()) {
                            // Set up the adapter with the list of contacts (call logs)
                            contactAdapter = new CallLogsAdapter(contactList, new OnCallLogClickListener() {
                                @Override
                                public void onContactClick(CallLogs contact) {
                                    // Open the detailed call logs activity when a contact is clicked
                                    Intent intent = new Intent(CallLogsActivity.this, CallLogsDetailActivity.class);
                                    intent.putExtra("contact_name", contact.getName());
                                    intent.putExtra("contact_phone_number", contact.getPhoneNumber());
                                    intent.putExtra("contact_calls", new Gson().toJson(contact.getCallLogsDetails()));
                                    startActivity(intent);
                                }

                                @Override
                                public void onContactBlock(CallLogs contact) {
                                    // Block the contact
                                    contact.setBlocked(true);

                                    // Use RecyclerView.post() to notify adapter after layout pass
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Find position of the contact in the list
                                            int position = contactList.indexOf(contact);

                                            // Notify that the specific contact has changed (only update that contact)
                                            contactAdapter.notifyItemChanged(position);

                                            // Show the block message
                                            Toast.makeText(CallLogsActivity.this, "Contact " + contact.getName() + " has been blocked.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onContactUnblock(CallLogs contact) {
                                    // Unblock the contact
                                    contact.setBlocked(false);

                                    // Use RecyclerView.post() to notify adapter after layout pass
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Find position of the contact in the list
                                            int position = contactList.indexOf(contact);

                                            // Notify that the specific contact has changed (only update that contact)
                                            contactAdapter.notifyItemChanged(position);

                                            // Show the unblock message
                                            Toast.makeText(CallLogsActivity.this, "Contact " + contact.getName() + " has been unblocked.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }, CallLogsActivity.this, recyclerView); // Pass RecyclerView here

                            // Set the adapter to the RecyclerView
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(contactAdapter);
                                }
                            });
                        } else {
                            // Handle empty contact list
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CallLogsActivity.this, "No call logs available", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // Handle the case where the response list is empty or malformed
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CallLogsActivity.this, "Malformed response", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Handle error if response is not successful
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CallLogsActivity.this, "Error loading call logs", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        });
    }
}

*/






/*
package com.example.parent.features.calllogs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.calllogs.adapter.CallLogsAdapter;
import com.example.parent.features.calllogs.listener.OnCallLogClickListener;
import com.example.parent.features.calllogs.model.CallDataSummary;
import com.example.parent.features.calllogs.model.CallLogs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CallLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CallLogsAdapter contactAdapter;
    private List<CallLogs> contactList;

    // TextViews for displaying call statistics
    TextView totalIncomingCalls, totalOutgoingCalls, totalIncomingDuration, totalOutgoingDuration;
    TextView mostOutgoingCalls, mostOutgoingCallsDuration, mostIncomingCalls, mostIncomingCallsDuration;
    TextView totalMissedCalls;
    LinearLayout incomingCallsTextView, outgoingCallsTextView, missedCallsTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize the TextViews
        totalIncomingCalls = findViewById(R.id.totalIncomingCalls);
        totalOutgoingCalls = findViewById(R.id.totalOutgoingCalls);
        totalMissedCalls = findViewById(R.id.totalMissedCalls);

        mostOutgoingCalls = findViewById(R.id.mostOutgoingCalls);
        mostOutgoingCallsDuration = findViewById(R.id.mostOutgoingCallsDuration);
        mostIncomingCalls = findViewById(R.id.mostIncomingCalls);
        mostIncomingCallsDuration = findViewById(R.id.mostIncomingCallsDuration);

        // Fetch call logs from the API
        fetchCallLogs();
        fetchCallsDetails();
    }

    private void fetchCallsDetails() {
        // Sample JSON response (this would come from your API in a real scenario)
        String jsonResponse = "{\n" +
                "  \"total_incoming_calls\": 6,\n" +
                "  \"total_outgoing_calls\": 9,\n" +
                "  \"total_incoming_duration\": 600,\n" +
                "  \"total_outgoing_duration\": 2400,\n" +
                "  \"most_outgoing_calls\": {\n" +
                "    \"number\": \"1234567890\",\n" +
                "    \"duration\": 300\n" +
                "  },\n" +
                "  \"most_incoming_calls\": {\n" +
                "    \"number\": \"9876543210\",\n" +
                "    \"duration\": 400\n" +
                "  },\n" +
                "  \"total_missed_calls\": 10\n" +
                "}";

        // Parse the JSON response using Gson
        Gson gson = new Gson();
        CallDataSummary callData = gson.fromJson(jsonResponse, CallDataSummary.class);

        // Populate the data into TextViews
        totalIncomingCalls.setText("Total Incoming Calls: " + callData.getTotalIncomingCalls());
        totalOutgoingCalls.setText("Total Outgoing Calls: " + callData.getTotalOutgoingCalls());
        totalMissedCalls.setText("Total Missed Calls: " + callData.getTotalMissedCalls());

*/
/*        // Check if MostOutgoingCalls is null before accessing it
        if (callData.getMostOutgoingCalls() != null) {
            mostOutgoingCalls.setText("Most Outgoing Calls: " + callData.getMostOutgoingCalls().getNumber());
            mostOutgoingCallsDuration.setText("Duration: " + callData.getMostOutgoingCalls().getDuration() + "s");
        } else {
            mostOutgoingCalls.setText("Most Outgoing Calls: N/A");
            mostOutgoingCallsDuration.setText("Duration: N/A");
        }

        // Check if MostIncomingCalls is null before accessing it
        if (callData.getMostIncomingCalls() != null) {
            mostIncomingCalls.setText("Most Incoming Calls: " + callData.getMostIncomingCalls().getNumber());
            mostIncomingCallsDuration.setText("Duration: " + callData.getMostIncomingCalls().getDuration() + "s");
        } else {
            mostIncomingCalls.setText("Most Incoming Calls: N/A");
            mostIncomingCallsDuration.setText("Duration: N/A");
        }*//*

    }


    private void fetchCallLogs() {

        // Simulating the JSON response for example purposes.
//        String jsonResponse = "[{\"phone_number\": \"+917981214937\", \"name\": \"Pinki\", \"count\": \"4\", \"calls\": [{\"call_type\": \"Incoming\", \"duration\": \"85\", \"time\": \"2024-12-10 11:07:28\"}, {\"call_type\": \"Outgoing\", \"duration\": \"81\", \"time\": \"2024-12-09 22:07:49\"}, {\"call_type\": \"Missed\", \"duration\": \"10\", \"time\": \"2024-12-09 18:40:26\"}, {\"call_type\": \"Missed\", \"duration\": \"13\", \"time\": \"2024-12-09 18:37:44\"}], \"total_duration\": \"189\"}, {\"phone_number\": \"9398221797\", \"name\": \"Minni\", \"count\": \"1\", \"calls\": [{\"call_type\": \"Outgoing\", \"duration\": \"0\", \"time\": \"2024-12-10 12:14:05\"}], \"total_duration\": \"0\"}]";
*/
/*
        String jsonResponse = "["
                + "{\"phone_number\": \"+917981214937\", \"name\": \"Pinki\", \"count\": \"24\", \"calls\": ["
                + "{\"call_type\": \"Incoming Call\", \"duration\": \"85\", \"time\": \"2024-12-10 11:07:28\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"81\", \"time\": \"2024-12-09 22:07:49\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0\", \"time\": \"2024-12-09 18:40:26\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0\", \"time\": \"2024-12-09 18:37:44\"}, "
                + "{\"call_type\": \"Incoming Call\", \"duration\": \"100\", \"time\": \"2024-12-08 15:32:10\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"55\", \"time\": \"2024-12-07 14:01:30\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0\", \"time\": \"2024-12-06 16:05:40\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"120\", \"time\": \"2024-12-06 14:44:59\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0\", \"time\": \"2024-12-05 17:23:15\"}, "
                + "{\"call_type\": \"Incoming Call\", \"duration\": \"90\", \"time\": \"2024-12-04 09:45:22\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"60\", \"time\": \"2024-12-04 08:50:01\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0\", \"time\": \"2024-12-03 19:30:11\"}, "
                + "{\"call_type\": \"Incoming Call\", \"duration\": \"150\", \"time\": \"2024-12-02 13:12:44\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"40\", \"time\": \"2024-12-01 11:25:30\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0\", \"time\": \"2024-12-01 09:38:52\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"30\", \"time\": \"2024-11-30 17:16:00\"}, "
                + "{\"call_type\": \"Incoming Call\", \"duration\": \"110\", \"time\": \"2024-11-29 16:50:44\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0\", \"time\": \"2024-11-28 18:24:16\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"25\", \"time\": \"2024-11-28 14:11:05\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0\", \"time\": \"2024-11-27 20:05:40\"}"
                + "], \"total_duration Call\": \"1763\"}, "
                + "{\"phone_number\": \"9398221797\", \"name\": \"Minni\", \"count\": \"3\", \"calls\": ["
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"0\", \"time\": \"2024-12-10 12:14:05\"},"
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0\", \"time\": \"2024-12-10 12:16:05\"},"
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0\", \"time\": \"2024-12-10 12:17:05\"}"
                + "], \"total_duration\": \"0\"},"
                +"{\"phone_number\": \"9876543210\", \"name\": \"\", \"count\": \"2\", \"calls\": [{\"call_type\": \"Incoming Call\", \"duration\": \"120\", \"time\": \"2024-12-11 09:30:00\"}, {\"call_type\": \"Outgoing Call\", \"duration\": \"150\", \"time\": \"2024-12-11 10:00:00\"}], \"total_duration\": \"270\"}"
                + "]";
*//*


        String jsonResponse = "["
                + "{\"phone_number\": \"+917981214937\", \"name\": \"Pinki\", \"count\": \"20\", \"calls\": ["
                + "{\"call_type\": \"Incoming Call\", \"duration\": \"1hr 21min 25sec\", \"time\": \"2024-12-10 11:07:28\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"1min 21sec\", \"time\": \"2024-12-09 22:07:49\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0sec\", \"time\": \"2024-12-09 18:40:26\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0sec\", \"time\": \"2024-12-09 18:37:44\"}, "
                + "{\"call_type\": \"Incoming Call\", \"duration\": \"1min 40sec\", \"time\": \"2024-12-08 15:32:10\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"55sec\", \"time\": \"2024-12-07 14:01:30\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0sec\", \"time\": \"2024-12-06 16:05:40\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"2min 0sec\", \"time\": \"2024-12-06 14:44:59\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0sec\", \"time\": \"2024-12-05 17:23:15\"}, "
                + "{\"call_type\": \"Incoming Call\", \"duration\": \"2hr 1min 30sec\", \"time\": \"2024-12-04 09:45:22\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"1min 0sec\", \"time\": \"2024-12-04 08:50:01\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0sec\", \"time\": \"2024-12-03 19:30:11\"}, "
                + "{\"call_type\": \"Incoming Call\", \"duration\": \"2min 30sec\", \"time\": \"2024-12-02 13:12:44\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"40sec\", \"time\": \"2024-12-01 11:25:30\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0sec\", \"time\": \"2024-12-01 09:38:52\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"30sec\", \"time\": \"2024-11-30 17:16:00\"}, "
                + "{\"call_type\": \"Incoming Call\", \"duration\": \"1min 50sec\", \"time\": \"2024-11-29 16:50:44\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0sec\", \"time\": \"2024-11-28 18:24:16\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"25sec\", \"time\": \"2024-11-28 14:11:05\"}, "
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0sec\", \"time\": \"2024-11-27 20:05:40\"}"
                + "], \"total_duration Call\": \"29min 23sec\"}, "
                + "{\"phone_number\": \"9398221797\", \"name\": \"Minni\", \"count\": \"3\", \"calls\": ["
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"0sec\", \"time\": \"2024-12-10 12:14:05\"},"
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0sec\", \"time\": \"2024-12-10 12:16:05\"},"
                + "{\"call_type\": \"Missed Call\", \"duration\": \"0sec\", \"time\": \"2024-12-10 12:17:05\"}"
                + "], \"total_duration\": \"0sec\"},"
                + "{\"phone_number\": \"9876543210\", \"name\": \"\", \"count\": \"2\", \"calls\": ["
                + "{\"call_type\": \"Incoming Call\", \"duration\": \"2min 0sec\", \"time\": \"2024-12-11 09:30:00\"}, "
                + "{\"call_type\": \"Outgoing Call\", \"duration\": \"2min 30sec\", \"time\": \"2024-12-11 10:00:00\"}], "
                + "\"total_duration\": \"4min 30sec\"}"
                + "]";


        // Parse the JSON response using Gson
        Gson gson = new Gson();
        Type contactListType = new TypeToken<List<CallLogs>>() {
        }.getType();
        contactList = gson.fromJson(jsonResponse, contactListType);
        contactAdapter = new CallLogsAdapter(contactList, new OnCallLogClickListener() {
            @Override
            public void onContactClick(CallLogs contact) {
                // Open the detailed call logs activity when a contact is clicked
                Intent intent = new Intent(CallLogsActivity.this, CallLogsDetailActivity.class);
                intent.putExtra("contact_name", contact.getName());
                intent.putExtra("contact_phone_number", contact.getPhoneNumber());
                intent.putExtra("total_duration", contact.getTotalDuration());
                intent.putExtra("contact_calls", new Gson().toJson(contact.getCalls()));
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(contactAdapter);
    }

}
*/
