package com.example.parentwithsubscription.features.smslogs.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.smslogs.adapter.SMSLogsAdapter;
import com.example.parentwithsubscription.features.smslogs.listener.OnSMSLogClickListener;
import com.example.parentwithsubscription.features.smslogs.model.SMSLogs;
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

public class SMSLogsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SMSLogsAdapter smsLogsAdapter;
    private final String SMS_LOGS_URL = URIConstants.SMS_LOGS_URL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_sms_logs, container, false);

        recyclerView = rootView.findViewById(R.id.sms_logs_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchSMSLogs();

        return rootView;
    }

    private void fetchSMSLogs() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(SMS_LOGS_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Error fetching SMS logs: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (getActivity() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    Type smsListType = new TypeToken<List<SMSLogs>>() {}.getType();
                    List<SMSLogs> smsLogsList = gson.fromJson(jsonResponse, smsListType);

                    getActivity().runOnUiThread(() -> {
                        smsLogsAdapter = new SMSLogsAdapter(smsLogsList, smsLogs -> {
                            SMSLogsDetailFragment detailFragment = new SMSLogsDetailFragment();

                            Bundle args = new Bundle();
                            args.putString("contact_name", smsLogs.getName());
                            args.putString("contact_phone_number", smsLogs.getPhoneNumber());
                            args.putString("sms_details", new Gson().toJson(smsLogs.getSmsLogsDetails()));
                            detailFragment.setArguments(args);

                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, detailFragment)
                                    .addToBackStack(null)
                                    .commit();
                        });

                        recyclerView.setAdapter(smsLogsAdapter);
                    });
                } else {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}
