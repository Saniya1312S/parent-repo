package com.example.parentwithsubscription.features.smslogs.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.smslogs.model.SMSLogsDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SMSLogsDetailActivity extends AppCompatActivity {

    private TextView contactNameTextView, phoneNumberTextView;  // Added for contact name and phone number
    private RecyclerView smsLogsRecyclerView;
    private SMSLogsAdapter smsLogsAdapter;
    private List<SMSLogsDetail> smsLogsDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_logs_detail);

        // Initialize the RecyclerView and TextViews for contact name and phone number
        smsLogsRecyclerView = findViewById(R.id.sms_logs_recyclerview);
        contactNameTextView = findViewById(R.id.contact_name);  // Ensure this ID is correct in your layout XML
        phoneNumberTextView = findViewById(R.id.contact_phone_number);  // Ensure this ID is correct in your layout XML
        smsLogsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the contact details passed from the main activity (ensure these extras are passed correctly)
        String contactName = getIntent().getStringExtra("contact_name");
        String phoneNumber = getIntent().getStringExtra("contact_phone_number");
        String smsJson = getIntent().getStringExtra("sms_details");

        // Set the contact name or "Unknown" if the name is empty
        if (contactName == null || contactName.isEmpty()) {
            contactNameTextView.setText("Unknown");
        } else {
            contactNameTextView.setText(contactName);
        }

        // Set the phone number
        phoneNumberTextView.setText(phoneNumber);

        // Parse the SMS details into a list of SMSLogsDetail
        Gson gson = new Gson();
        Type type = new TypeToken<List<SMSLogsDetail>>() {}.getType();
        smsLogsDetailList = gson.fromJson(smsJson, type);

        // Set the adapter for RecyclerView
        smsLogsAdapter = new SMSLogsAdapter(smsLogsDetailList);
        smsLogsRecyclerView.setAdapter(smsLogsAdapter);
    }

    private static class SMSLogsAdapter extends RecyclerView.Adapter<SMSLogsAdapter.SMSLogsViewHolder> {

        private List<SMSLogsDetail> smsLogsDetailList;

        public SMSLogsAdapter(List<SMSLogsDetail> smsLogsDetailList) {
            this.smsLogsDetailList = smsLogsDetailList;
        }

        @NonNull
        @Override
        public SMSLogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_logs_detail_item, parent, false);
            return new SMSLogsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SMSLogsViewHolder holder, int position) {
            SMSLogsDetail smsLogsDetail = smsLogsDetailList.get(position);

            // Set the content and timestamp
            String smsContentText = smsLogsDetail.getContent();
            holder.smsContent.setText(applyMessageStatus(smsContentText, smsLogsDetail.getSmsClassificationType()));

            // Format and set the timestamp
            try {
                long timestamp = Long.parseLong(smsLogsDetail.getTime()); // Assuming call.getTime() is a string representing milliseconds
                // Format the timestamp using the formatTimestamp method
                String formattedTime = URIConstants.formatTimestamp(timestamp);
                holder.smsDateTime.setText(formattedTime);// Set the formatted time
                // Log the formatted timestamp for debugging
                Log.d("Time formatted", formattedTime);
            } catch (NumberFormatException e) {
                holder.smsDateTime.setText(smsLogsDetail.getTime()); // Fallback to the original time if parsing fails
            }

            // Set gravity and background color based on message type (sent/received)
            String smsType = smsLogsDetail.getSmsType().trim().toLowerCase();  // sent or received
            if ("sent".equalsIgnoreCase(smsType)) {
                holder.messageContainer.setGravity(Gravity.END);  // Sent messages align to the right
                holder.smsContent.setBackgroundResource(R.drawable.sms_message_bubble_sent);
                holder.smsContent.setTextColor(Color.WHITE);
                holder.smsDateTime.setGravity(Gravity.END);  // Timestamp for sent message (right)
            } else {
                holder.messageContainer.setGravity(Gravity.START);  // Received messages align to the left
                holder.smsContent.setBackgroundResource(R.drawable.sms_message_bubble_received);
                holder.smsDateTime.setGravity(Gravity.START);  // Timestamp for received message (left)
            }
        }

        @Override
        public int getItemCount() {
            return smsLogsDetailList.size();
        }

        private SpannableString applyMessageStatus(String content, String status) {
            SpannableString spannableMessage = new SpannableString(content);
            if ("spam".equalsIgnoreCase(status)) {
                content = content + " (Spam)";
                spannableMessage = new SpannableString(content);
                int spamStartIndex = content.length() - 6; // Start index for "(Spam)"
                int spamEndIndex = content.length();
                spannableMessage.setSpan(new ForegroundColorSpan(Color.RED), spamStartIndex, spamEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ("warning".equalsIgnoreCase(status)) {
                content = content + " (Warning)";
                spannableMessage = new SpannableString(content);
                int warningStartIndex = content.length() - 9; // Start index for "(Warning)"
                int warningEndIndex = content.length();
                spannableMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#FFA500")), warningStartIndex, warningEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return spannableMessage;
        }

        public static class SMSLogsViewHolder extends RecyclerView.ViewHolder {

            TextView smsContent, smsDateTime;
            LinearLayout messageContainer;

            public SMSLogsViewHolder(@NonNull View itemView) {
                super(itemView);
                smsContent = itemView.findViewById(R.id.sms_content);
                smsDateTime = itemView.findViewById(R.id.sms_date_time);
                messageContainer = itemView.findViewById(R.id.message_container);
            }
        }
    }
}







/*
package com.example.parent.features.smslogs.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parent.R;
import com.example.parent.features.smslogs.model.SMSLogsDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SMSLogsDetailActivity extends AppCompatActivity {
    private TextView contactNameTextView, phoneNumberTextView;
    private LinearLayout smsLogsLayout; // For dynamically adding SMS logs
    private List<SMSLogsDetail> smsLogsDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_logs_detail);

        // Initializing views
        contactNameTextView = findViewById(R.id.contact_name);
        phoneNumberTextView = findViewById(R.id.contact_phone_number);
        smsLogsLayout = findViewById(R.id.sms_logs_layout);

        // Get the contact details passed from the main activity
        String contactName = getIntent().getStringExtra("contact_name");
        String phoneNumber = getIntent().getStringExtra("contact_phone_number");
        String smsJson = getIntent().getStringExtra("sms_details");

        // Set contact name or "Unknown"
        if (contactName == null || contactName.isEmpty()) {
            contactNameTextView.setText("Unknown");
        } else {
            contactNameTextView.setText(contactName);
        }

        phoneNumberTextView.setText(phoneNumber);

        // Parse the SMS details into a list of SMSLogsDetail
        Gson gson = new Gson();
        Type type = new TypeToken<List<SMSLogsDetail>>() {}.getType();
        smsLogsDetailList = gson.fromJson(smsJson, type);

        // Dynamically add each SMS log item to the layout
        for (SMSLogsDetail smsLogsDetail : smsLogsDetailList) {
            // Inflate the generic message layout
            LinearLayout smsLogItem = (LinearLayout) getLayoutInflater().inflate(R.layout.sms_logs_detail_item, null);

            // Get the elements from the layout
            TextView smsContent = smsLogItem.findViewById(R.id.sms_content);
            TextView smsDateTime = smsLogItem.findViewById(R.id.sms_date_time);

            String smsContentText = smsLogsDetail.getContent();
            String messageStatus = smsLogsDetail.getSmsClassificationType();  // (spam, ham, warning)
            String smsType = smsLogsDetail.getSmsType().trim().toLowerCase();  // sent or received

            // Set the message content
            smsContent.setText(smsContentText);

*/
/*            // Change content color based on message status (spam, ham, warning)
            if ("spam".equalsIgnoreCase(messageStatus)) {
                smsContent.setTextColor(Color.RED);
                smsContent.setText(smsContentText + " (Spam)");
            } else if ("warning".equalsIgnoreCase(messageStatus)) {
                smsContent.setTextColor(Color.parseColor("#FFA500")); // Orange color
                smsContent.setText(smsContentText + " (Warning)");
            } else {
                smsContent.setTextColor(Color.BLACK);
            }*//*

            // Initialize the SpannableString to apply different styles
            SpannableString spannableMessage = new SpannableString(smsContentText);

            // Append the message status to the content (e.g., (Spam), (Warning))
            if ("spam".equalsIgnoreCase(messageStatus)) {
                smsContentText = smsContentText + " (Spam)";  // Append "(Spam)" to the message
                spannableMessage = new SpannableString(smsContentText);
                int spamStartIndex = smsContentText.length() - 6; // Start index for "(Spam)"
                int spamEndIndex = smsContentText.length();
                spannableMessage.setSpan(new ForegroundColorSpan(Color.RED), spamStartIndex, spamEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ("warning".equalsIgnoreCase(messageStatus)) {
                smsContentText = smsContentText + " (Warning)";  // Append "(Warning)" to the message
                spannableMessage = new SpannableString(smsContentText);
                int warningStartIndex = smsContentText.length() - 9; // Start index for "(Warning)"
                int warningEndIndex = smsContentText.length();
                spannableMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#FFA500")), warningStartIndex, warningEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // Set the message content with the updated spannable text (status colored)
            smsContent.setText(spannableMessage);


            // Set gravity and background color based on the message type (sent or received)
            if ("sent".equals(smsType)) {
                smsLogItem.setGravity(Gravity.END);  // Sent messages align to the right
//                smsLogItem.setBackgroundColor(Color.WHITE);      // White background for sent messages
                smsDateTime.setGravity(Gravity.END);  // Timestamp for sent message (right)
            } else if ("received".equals(smsType)) {
                smsLogItem.setGravity(Gravity.START);  // Received messages align to the left
                smsLogItem.setBackgroundColor(Color.parseColor("#D0E7FF")); // Blue background for received messages
                smsDateTime.setGravity(Gravity.START);  // Timestamp for received message (left)
            }

            // Set the timestamp below the message
//            smsDateTime.setText(smsLogsDetail.getTime());
            // Convert the Unix timestamp (assuming smsLogsDetail.getTime() returns Unix time in seconds)
            long unixTime = Long.parseLong(smsLogsDetail.getTime());  // Convert to milliseconds
            Date date = new Date(unixTime);

            // Format the date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
            String formattedDate = dateFormat.format(date);

            // Set the formatted date and time to the TextView
            smsDateTime.setText(formattedDate);
            smsDateTime.setBackgroundColor(Color.TRANSPARENT);  // Ensure no background color for timestamp

            // Add the SMS log item to the layout
            smsLogsLayout.addView(smsLogItem);
        }
    }
}

*/








/*
package com.example.parent.features.smslogs.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parent.R;
import com.example.parent.features.calllogs.model.CallLogsDetail;
import com.example.parent.features.smslogs.model.SMSLogsDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SMSLogsDetailActivity extends AppCompatActivity {
    private TextView contactNameTextView, phoneNumberTextView;
    private LinearLayout smsLogsLayout; // For dynamically adding call logs
    private List<SMSLogsDetail> smsLogsDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_logs_detail);

        // Initializing views
        contactNameTextView = findViewById(R.id.contact_name);
        phoneNumberTextView = findViewById(R.id.contact_phone_number);
        smsLogsLayout = findViewById(R.id.sms_logs_layout); // This is where call log items are dynamically added

        // Get the contact details passed from the main activity
        String contactName = getIntent().getStringExtra("contact_name");
        String phoneNumber = getIntent().getStringExtra("contact_phone_number");
        String smsJson = getIntent().getStringExtra("sms_details");

        // Set contact name, or show "Unknown" if it's null or empty
        if (contactName == null || contactName.isEmpty()) {
            contactNameTextView.setText("Unknown");
        } else {
            contactNameTextView.setText(contactName);
        }

        phoneNumberTextView.setText(phoneNumber);

        // Parse the JSON call logs into a list of Call objects
        Gson gson = new Gson();
        Type type = new TypeToken<List<SMSLogsDetail>>() {}.getType();
        smsLogsDetailList = gson.fromJson(smsJson, type);

        // Dynamically add each call log item to the layout
        for (SMSLogsDetail smsLogsDetail : smsLogsDetailList) {
            // Inflate the call log item layout
            LinearLayout smsLogItem = (LinearLayout) getLayoutInflater().inflate(R.layout.sms_logs_detail_item, null);

            // Set the call type icon
            ImageView smsTypeIcon = smsLogItem.findViewById(R.id.sms_type_icon);
            String smsType = smsLogsDetail.getSmsType().trim().toLowerCase(); // Normalize call type

            // Log the call type to check for correctness
            Log.d("SMSLogsDetail", "SMS Type: " + smsType);

            if ("received".equals(smsType)) {
                smsTypeIcon.setImageResource(R.drawable.ic_incoming_call);
            } else if ("sent".equals(smsType)) {
                smsTypeIcon.setImageResource(R.drawable.ic_outgoing_call);
            }else {
                // Default to missed call icon if callType is unknown or invalid
                smsTypeIcon.setImageResource(R.drawable.ic_missed_call);
            }

            // Set the call duration
            TextView smsContent = smsLogItem.findViewById(R.id.sms_content);
            smsContent.setText(smsLogsDetail.getContent());

            // Set the call type (incoming, outgoing, missed)
            TextView smsTypeText = smsLogItem.findViewById(R.id.sms_type);
            smsTypeText.setText(smsLogsDetail.getSmsType());

            // Set the call date/time
            TextView smsDateTime = smsLogItem.findViewById(R.id.sms_date_time);
            smsDateTime.setText(smsLogsDetail.getTime());

            // Add the call log item to the call logs layout
            smsLogsLayout.addView(smsLogItem);
        }
    }
}
*/
