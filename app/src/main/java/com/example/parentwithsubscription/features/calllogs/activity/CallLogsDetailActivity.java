package com.example.parentwithsubscription.features.calllogs.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.calllogs.model.CallLogsDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class CallLogsDetailActivity extends AppCompatActivity {

    private TextView contactNameTextView, phoneNumberTextView;
    private LinearLayout callLogsLayout; // For dynamically adding call logs
    private List<CallLogsDetail> callList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_logs_detail);

        // Initializing views
        contactNameTextView = findViewById(R.id.contact_name);
        phoneNumberTextView = findViewById(R.id.contact_phone_number);
        callLogsLayout = findViewById(R.id.call_logs_layout); // This is where call log items are dynamically added

        // Get the contact details passed from the main activity
        String contactName = getIntent().getStringExtra("contact_name");
        String phoneNumber = getIntent().getStringExtra("contact_phone_number");
        String callsJson = getIntent().getStringExtra("contact_calls");

        // Set contact name, or show "Unknown" if it's null or empty
        if (contactName == null || contactName.isEmpty()) {
            contactNameTextView.setText("Unknown");
        } else {
            contactNameTextView.setText(contactName);
        }

        phoneNumberTextView.setText(phoneNumber);

        // Parse the JSON call logs into a list of Call objects
        Gson gson = new Gson();
        Type type = new TypeToken<List<CallLogsDetail>>() {}.getType();
        callList = gson.fromJson(callsJson, type);

        // Dynamically add each call log item to the layout
        for (CallLogsDetail call : callList) {
            // Inflate the call log item layout
            LinearLayout callLogItem = (LinearLayout) getLayoutInflater().inflate(R.layout.call_logs_detail_item, null);

            // Set the call type icon
            ImageView callTypeIcon = callLogItem.findViewById(R.id.call_type_icon);
            String callType = call.getCallType().trim().toLowerCase(); // Normalize call type

            // Log the call type to check for correctness
            Log.d("CallLogsDetail", "Call Type: " + callType);

            if ("incoming".equals(callType)) {
                callTypeIcon.setImageResource(R.drawable.ic_incoming_call);
            } else if ("outgoing".equals(callType)) {
                callTypeIcon.setImageResource(R.drawable.ic_outgoing_call);
            } else if ("missed".equals(callType)) {
                callTypeIcon.setImageResource(R.drawable.ic_missed_call);
            } else {
                // Default to missed call icon if callType is unknown or invalid
                callTypeIcon.setImageResource(R.drawable.ic_missed_call);
            }

            // Convert call duration (seconds) to HH:MM:SS
            TextView callDuration = callLogItem.findViewById(R.id.call_duration);
            try {
                String formattedDuration = URIConstants.formatDuration(call.getDuration());
                callDuration.setText(formattedDuration);
                // Log the formatted timestamp for debugging
                Log.d("Time formatted", formattedDuration);
            } catch (NumberFormatException e) {
                callDuration.setText(call.getDuration());
            }


            TextView callDateTime = callLogItem.findViewById(R.id.call_date_time);
            try {
                long timestamp = call.getTime(); // Assuming call.getTime() is a string representing milliseconds
                // Format the timestamp using the formatTimestamp method
                String formattedTime = URIConstants.formatTimestamp(timestamp);
                callDateTime.setText(formattedTime);// Set the formatted time
                // Log the formatted timestamp for debugging
                Log.d("Time formatted", formattedTime);
            } catch (NumberFormatException e) {
                callDateTime.setText(String.valueOf(call.getTime())); // Fallback to the original time if parsing fails
            }

            // Set the call type (incoming, outgoing, missed)
            TextView callTypeText = callLogItem.findViewById(R.id.call_type);
            callTypeText.setText(call.getCallType() + " Call");

            // Add the call log item to the call logs layout
            callLogsLayout.addView(callLogItem);
        }
    }
}









/*
package com.example.parent.features.calllogs.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parent.R;
import com.example.parent.features.calllogs.model.CallLogsDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CallLogsDetailActivity extends AppCompatActivity {

    private TextView contactNameTextView, phoneNumberTextView, callLogsTextView;
    private List<CallLogsDetail> callList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_logs_detail);

        contactNameTextView = findViewById(R.id.contact_name);
        phoneNumberTextView = findViewById(R.id.contact_phone_number);
        callLogsTextView = findViewById(R.id.call_logs);



        // Get the contact details passed from the main activity
        String contactName = getIntent().getStringExtra("contact_name");
        String phoneNumber = getIntent().getStringExtra("contact_phone_number");
        String callsJson = getIntent().getStringExtra("contact_calls");

        // Set contact name, or show "Unknown" if it's null or empty
        if (contactName == null || contactName.isEmpty()) {
            contactNameTextView.setText("Unknown");
        } else {
            contactNameTextView.setText(contactName);
        }

        phoneNumberTextView.setText(phoneNumber);

        // Parse the JSON call logs into a list of Call objects
        Gson gson = new Gson();
        Type type = new TypeToken<List<CallLogsDetail>>() {}.getType();
        callList = gson.fromJson(callsJson, type);

        // Display call logs
        StringBuilder callLogsBuilder = new StringBuilder();
        for (CallLogsDetail call : callList) {
            callLogsBuilder.append(call.getTime() + "\n")
                    .append(call.getCallType() + ", " + call.getDuration())
                    .append("\n\n");
        }
        callLogsTextView.setText(callLogsBuilder.toString());
    }
}
*/









/*
package com.example.parentapplocation;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CallLogsDetailActivity extends AppCompatActivity {

    private TextView contactNameTextView, phoneNumberTextView, callLogsTextView;
    private List<Calls> callList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs_detail);

        contactNameTextView = findViewById(R.id.contact_name);
        phoneNumberTextView = findViewById(R.id.contact_phone_number);
        callLogsTextView = findViewById(R.id.call_logs);


        // Get the contact details passed from the main activity
        String contactName = getIntent().getStringExtra("contact_name");
        String phoneNumber = getIntent().getStringExtra("contact_phone_number");
        String callsJson = getIntent().getStringExtra("contact_calls");

        contactNameTextView.setText(contactName);
        phoneNumberTextView.setText(phoneNumber);

        // Parse the JSON call logs into a list of Call objects
        Gson gson = new Gson();
        Type type = new TypeToken<List<Calls>>() {}.getType();
        callList = gson.fromJson(callsJson, type);

        // Display call logs
        StringBuilder callLogsBuilder = new StringBuilder();
        for (Calls call : callList) {
            callLogsBuilder.append(call.getTime() + "\n")
                    .append(call.getCallType() + ", " + call.getDuration())
                    .append("\n\n");
        }
        callLogsTextView.setText(callLogsBuilder.toString());
    }
}
*/
