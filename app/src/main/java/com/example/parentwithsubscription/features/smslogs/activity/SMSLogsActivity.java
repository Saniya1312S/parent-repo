package com.example.parentwithsubscription.features.smslogs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SMSLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SMSLogsAdapter smsLogsAdapter;
    private String SMS_LOGS_URL = URIConstants.SMS_LOGS_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_logs);

        recyclerView = findViewById(R.id.sms_logs_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchSMSLogs();
    }

    private void fetchSMSLogs() {

        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Build the request
        Request request = new Request.Builder()
                .url(SMS_LOGS_URL)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Handle failure
                runOnUiThread(() -> {
                    // Show error to the user (optional)
                    Toast.makeText(SMSLogsActivity.this, "Error fetching SMS logs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    // Parse the JSON response using Gson
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    Type smsListType = new TypeToken<List<SMSLogs>>(){}.getType();
                    List<SMSLogs> smsLogsList = gson.fromJson(jsonResponse, smsListType);

                    // Update the UI with the fetched data
                    runOnUiThread(() -> {
                        smsLogsAdapter = new SMSLogsAdapter(smsLogsList, new OnSMSLogClickListener() {
                            @Override
                            public void onSMSClick(SMSLogs smsLogs) {
                                Intent intent = new Intent(SMSLogsActivity.this, SMSLogsDetailActivity.class);
                                intent.putExtra("contact_name", smsLogs.getName());
                                intent.putExtra("contact_phone_number", smsLogs.getPhoneNumber());
                                intent.putExtra("sms_details", new Gson().toJson(smsLogs.getSmsLogsDetails()));
                                startActivity(intent);
                            }
                        });

                        // Set the adapter to the RecyclerView
                        recyclerView.setAdapter(smsLogsAdapter);
                    });
                } else {
                    // Handle failure case when response is not successful
                    runOnUiThread(() -> {
                        Toast.makeText(SMSLogsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }











    /*private void fetchSMSLogs() {
        // Simulating the JSON response for example purposes.
        String jsonResponse = "["
                + "{\"phone_number\": \"+917981214937\", \"name\": \"John Doe\", \"count\": \"10\", \"messages\": ["
                + "{\"sms_type\": \"received\", \"content\": \"Your bank account has been blocked. Please contact support.\", \"classification\": \"spam\", \"time\": \"2024-12-10 11:07:28\"}, "
                + "{\"sms_type\": \"sent\", \"content\": \"Meeting at 5pm. Don't be late!\", \"classification\": \"ham\", \"time\": \"2024-12-09 22:07:49\"}, "
                + "{\"sms_type\": \"received\", \"content\": \"Free coupon for 20% off! Redeem now.\", \"classification\": \"spam\", \"time\": \"2024-12-09 18:40:26\"}, "
                + "{\"sms_type\": \"sent\", \"content\": \"Your appointment is confirmed for tomorrow at 3pm.\", \"classification\": \"ham\", \"time\": \"2024-12-09 18:37:44\"}, "
                + "{\"sms_type\": \"sent\", \"content\": \"Reminder: Your car insurance is about to expire!\", \"classification\": \"warning\", \"time\": \"2024-12-08 15:32:10\"}, "
                + "{\"sms_type\": \"received\", \"content\": \"Important update about your online order. Check your email.\", \"classification\": \"ham\", \"time\": \"2024-12-07 14:01:30\"}, "
                + "{\"sms_type\": \"received\", \"content\": \"We have detected suspicious activity on your account. Please secure it immediately.\", \"classification\": \"spam\", \"time\": \"2024-12-06 16:05:40\"}, "
                + "{\"sms_type\": \"sent\", \"content\": \"You have a scheduled interview tomorrow at 10am. Best of luck!\", \"classification\": \"ham\", \"time\": \"2024-12-06 14:44:59\"}, "
                + "{\"sms_type\": \"sent\", \"content\": \"Warning! You have received an unsolicited message from a suspicious source.\", \"classification\": \"warning\", \"time\": \"2024-12-05 17:23:15\"}, "
                + "{\"sms_type\": \"received\", \"content\": \"Flash Sale! Get up to 50% off on electronics today only.\", \"classification\": \"spam\", \"time\": \"2024-12-04 09:45:22\"}"
                + "]}, "
                + "{\"phone_number\": \"9398221797\", \"name\": \"Jane Smith\", \"count\": \"3\", \"messages\": ["
                + "{\"sms_type\": \"sent\", \"content\": \"Urgent! Your subscription will expire soon. Please renew.\", \"classification\": \"warning\", \"time\": \"2024-12-10 12:14:05\"}, "
                + "{\"sms_type\": \"received\", \"content\": \"Congratulations! You have won a $1000 gift card. Claim it now.\", \"classification\": \"spam\", \"time\": \"2024-12-10 12:16:05\"}, "
                + "{\"sms_type\": \"sent\", \"content\": \"Thank you for your payment. Your bill is now settled.\", \"classification\": \"ham\", \"time\": \"2024-12-10 12:17:05\"}"
                + "]}, "
                + "{\"phone_number\": \"9876543210\", \"name\": \"Mike Johnson\", \"count\": \"2\", \"messages\": ["
                + "{\"sms_type\": \"received\", \"content\": \"New job offer! Apply now!\", \"classification\": \"spam\", \"time\": \"2024-12-11 09:30:00\"}, "
                + "{\"sms_type\": \"sent\", \"content\": \"Your package has been shipped and will arrive tomorrow.\", \"classification\": \"ham\", \"time\": \"2024-12-11 10:00:00\"}"
                + "]}"
                + "]";


        // Parse the JSON response using Gson
        Gson gson = new Gson();
        Type smsListType = new TypeToken<List<SMSLogs>>() {
        }.getType();
        smsLogsList = gson.fromJson(jsonResponse, smsListType);
        smsLogsAdapter = new SMSLogsAdapter(smsLogsList, new OnSMSLogClickListener() {
            @Override
            public void onSMSClick(SMSLogs smsLogs) {
                Intent intent = new Intent(SMSLogsActivity.this, SMSLogsDetailActivity.class);
                intent.putExtra("contact_name", smsLogs.getName());
                intent.putExtra("contact_phone_number", smsLogs.getPhoneNumber());
                intent.putExtra("sms_details", new Gson().toJson(smsLogs.getSmsLogsDetails()));
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(smsLogsAdapter);
    }*/
}










/*
package com.example.parent.features.smslogs.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.smslogs.adapter.SMSAdapter;
import com.example.parent.features.smslogs.model.SMSLog;

import java.util.ArrayList;
import java.util.List;

public class SMSLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SMSAdapter adapter;
    private List<SMSLog> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_logs);

        recyclerView = findViewById(R.id.sms_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Static message data
        messageList = new ArrayList<>();
        messageList.add(new SMSLog("John Doe", "Hi, How are you?", "12:34 PM", R.drawable.ic_person));
        messageList.add(new SMSLog("Jane Smith", "I'm doing great, thanks for asking!", "08:15 AM", R.drawable.ic_person));
        messageList.add(new SMSLog("Michael Brown", "I’m working on the project right now, how about you?", "05:12 PM", R.drawable.ic_person));

        adapter = new SMSAdapter(messageList);
        recyclerView.setAdapter(adapter);
    }
}

*/
