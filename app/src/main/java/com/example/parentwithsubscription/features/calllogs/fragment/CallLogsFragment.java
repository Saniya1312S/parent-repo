package com.example.parentwithsubscription.features.calllogs.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.calllogs.adapter.CallLogsAdapter;
import com.example.parentwithsubscription.features.calllogs.listener.OnCallLogClickListener;
import com.example.parentwithsubscription.features.calllogs.model.CallLogs;
import com.example.parentwithsubscription.features.calllogs.model.CallDataSummary;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CallLogsFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_call_logs, container, false);

        // Initialize views
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the OkHttpClient
        client = new OkHttpClient();

        // Initialize the TextViews
        totalIncomingCalls = rootView.findViewById(R.id.totalIncomingCalls);
        totalOutgoingCalls = rootView.findViewById(R.id.totalOutgoingCalls);
        totalMissedCalls = rootView.findViewById(R.id.totalMissedCalls);

        // Fetch call logs from the API
        fetchCallLogs();
        fetchCallsDetails();

        return rootView;  // Return the root view after initialization
    }

    private void fetchCallsDetails() {
        // URL for the API endpoint
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
                            getActivity().runOnUiThread(new Runnable() {
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
        Request request = new Request.Builder()
                .url(CALL_LOGS_URL)
                .build();

        // Asynchronous HTTP request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (e.g., no internet connection)
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Failed to load call logs", Toast.LENGTH_SHORT).show();
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
                        contactList = responseList;

                        if (contactList != null && !contactList.isEmpty()) {
                            // Set up the adapter with the list of contacts (call logs)
                            contactAdapter = new CallLogsAdapter(contactList, new OnCallLogClickListener() {
                                @Override
                                public void onContactClick(CallLogs contact) {
                                    // Open the detailed call logs fragment when a contact is clicked
                                    CallLogsDetailFragment fragment = new CallLogsDetailFragment();

                                    // Pass the data to the fragment using a Bundle
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contact_name", contact.getName());
                                    bundle.putString("contact_phone_number", contact.getPhoneNumber());
                                    bundle.putString("contact_calls", new Gson().toJson(contact.getCallLogsDetails()));
                                    fragment.setArguments(bundle);

                                    // Replace the current fragment with the CallLogsDetailFragment
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                }

                                @Override
                                public void onContactBlock(CallLogs contact) {
                                    // Block the contact
                                    contact.setBlocked(true);

                                    // Use post() to notify adapter after layout pass
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Find position of the contact in the list
                                            int position = contactList.indexOf(contact);

                                            // Notify that the specific contact has changed (only update that contact)
                                            contactAdapter.notifyItemChanged(position);

                                            // Show the block message
                                            Toast.makeText(getActivity(), "Contact " + contact.getName() + " has been blocked.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onContactUnblock(CallLogs contact) {
                                    // Unblock the contact
                                    contact.setBlocked(false);

                                    // Use post() to notify adapter after layout pass
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Find position of the contact in the list
                                            int position = contactList.indexOf(contact);

                                            // Notify that the specific contact has changed (only update that contact)
                                            contactAdapter.notifyItemChanged(position);

                                            // Show the unblock message
                                            Toast.makeText(getActivity(), "Contact " + contact.getName() + " has been unblocked.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }, getContext(), recyclerView); // Pass RecyclerView here

                            // Set the adapter to the RecyclerView
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setAdapter(contactAdapter);
                                }
                            });
                        } else {
                            // Handle empty contact list
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "No call logs available", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // Handle the case where the response list is empty or malformed
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Malformed response", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Handle error if response is not successful
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Error loading call logs", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
