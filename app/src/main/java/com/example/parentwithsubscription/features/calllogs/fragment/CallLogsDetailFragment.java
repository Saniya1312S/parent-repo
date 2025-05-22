package com.example.parentwithsubscription.features.calllogs.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.calllogs.model.CallLogsDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CallLogsDetailFragment extends Fragment {

    private TextView contactNameTextView, phoneNumberTextView;
    private LinearLayout callLogsLayout;
    private List<CallLogsDetail> callList;

    public CallLogsDetailFragment() {
        // Required empty public constructor
    }

    public static CallLogsDetailFragment newInstance(String contactName, String phoneNumber, String callsJson) {
        CallLogsDetailFragment fragment = new CallLogsDetailFragment();
        Bundle args = new Bundle();
        args.putString("contact_name", contactName);
        args.putString("contact_phone_number", phoneNumber);
        args.putString("contact_calls", callsJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.call_logs_detail, container, false);

        contactNameTextView = view.findViewById(R.id.contact_name);
        phoneNumberTextView = view.findViewById(R.id.contact_phone_number);
        callLogsLayout = view.findViewById(R.id.call_logs_layout);

        // Get passed arguments
        Bundle args = getArguments();
        if (args == null) {
            Toast.makeText(getContext(), "No call details found", Toast.LENGTH_SHORT).show();
            return view;
        }

        String contactName = args.getString("contact_name", "Unknown");
        String phoneNumber = args.getString("contact_phone_number", "");
        String callsJson = args.getString("contact_calls", "[]");

        contactNameTextView.setText(contactName.isEmpty() ? "Unknown" : contactName);
        phoneNumberTextView.setText(phoneNumber);

        // Parse the JSON call logs
        Gson gson = new Gson();
        Type type = new TypeToken<List<CallLogsDetail>>() {}.getType();
        callList = gson.fromJson(callsJson, type);

        // Populate layout dynamically
        for (CallLogsDetail call : callList) {
            LinearLayout callLogItem = (LinearLayout) inflater.inflate(R.layout.call_logs_detail_item, null);

            ImageView callTypeIcon = callLogItem.findViewById(R.id.call_type_icon);
            TextView callDuration = callLogItem.findViewById(R.id.call_duration);
            TextView callDateTime = callLogItem.findViewById(R.id.call_date_time);
            TextView callTypeText = callLogItem.findViewById(R.id.call_type);

            // Normalize and set icon
            String callType = call.getCallType().trim().toLowerCase();
            switch (callType) {
                case "incoming":
                    callTypeIcon.setImageResource(R.drawable.ic_incoming_call);
                    break;
                case "outgoing":
                    callTypeIcon.setImageResource(R.drawable.ic_outgoing_call);
                    break;
                case "missed":
                default:
                    callTypeIcon.setImageResource(R.drawable.ic_missed_call);
                    break;
            }

            // Set call duration
            try {
                callDuration.setText(URIConstants.formatDuration(call.getDuration()));
            } catch (Exception e) {
                callDuration.setText(call.getDuration());
            }

            // Set call timestamp
            try {
                long timestamp = call.getTime();
                callDateTime.setText(URIConstants.formatTimestamp(timestamp));
            } catch (NumberFormatException e) {
                callDateTime.setText(String.valueOf(call.getTime()));
            }

            // Set call type label
            callTypeText.setText(call.getCallType() + " Call");

            // Add to parent layout
            callLogsLayout.addView(callLogItem);
        }

        return view;
    }
}
