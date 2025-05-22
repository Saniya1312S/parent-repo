package com.example.parentwithsubscription.features.socialmedia.activity.whatsapp;/*
package com.example.parent.features.socialmedia.activity.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.socialmedia.adapter.whatsapp.WhatsAppCallAdapter;
import com.example.parent.features.socialmedia.adapter.whatsapp.WhatsAppMessagesAdapter;
import com.example.parent.features.socialmedia.listener.whatsapp.OnWhatsAppMessagesClickListener;
import com.example.parent.features.socialmedia.model.whatsapp.WhatsAppCall;
import com.example.parent.features.socialmedia.model.whatsapp.WhatsAppMessages;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WhatsAppActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerView, callsRecyclerView;
    private WhatsAppMessagesAdapter whatsAppMessagesAdapter;
    private WhatsAppCallAdapter whatsAppCallAdapter;
    private List<WhatsAppMessages> whatsAppMessagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp);

        messagesRecyclerView = findViewById(R.id.whatsapp_messages_recycler_view);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        callsRecyclerView = findViewById(R.id.whatsapp_calls_recycler_view);
        callsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchWhatsAppMessages();
        fetchWhatsAppCalls();
    }

    private void fetchWhatsAppCalls() {
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
            callsRecyclerView.setAdapter(whatsAppCallAdapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchWhatsAppMessages() {
        // Simulating the JSON response for example purposes.
*/
/*        String jsonResponse = "["
                + "{\"phone_number\": \"+917981214937\", \"name\": \"John Doe\", \"count\": \"5\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey! What's up? Did you get my message?\", \"time\": \"2024-12-10 10:00:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yeah, I saw it! Just been busy with work, you know how it is.\", \"time\": \"2024-12-10 10:05:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Totally. I feel that! Also, I got a reminder about that meeting tomorrow. Are you still on for 5 PM?\", \"time\": \"2024-12-10 10:10:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yes! I’ll be there, just need to finish up a couple of things first.\", \"time\": \"2024-12-10 10:12:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sounds good! Oh, and don't forget about the new promo I sent you for that online course. It's a great deal!\", \"time\": \"2024-12-10 10:15:00\"}"
                + "]}, "
                + "{\"phone_number\": \"9398221797\", \"name\": \"Jane Smith\", \"count\": \"4\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Haha, I almost missed it. Thanks for the heads-up. I'll check it out later.\", \"time\": \"2024-12-10 10:20:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"No problem! It's a really good deal, you should definitely look at it. Let me know if you need the link again.\", \"time\": \"2024-12-10 10:25:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Will do! Anyway, are we still meeting for coffee later today?\", \"time\": \"2024-12-10 10:30:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Absolutely! I’ll meet you at 3 PM. See you then!\", \"time\": \"2024-12-10 10:35:00\"}"
                + "]}, "
                + "{\"phone_number\": \"9876543210\", \"name\": \"Mike Johnson\", \"count\": \"3\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Hey, do you have a moment to chat? I need some advice on a project.\", \"time\": \"2024-12-10 11:00:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sure! What's going on?\", \"time\": \"2024-12-10 11:05:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"I’m stuck on a presentation. Can you help me with the structure?\", \"time\": \"2024-12-10 11:10:00\"}"
                + "]}"
                + "]";*//*


        String jsonResponse = "["
                + "{\"phone_number\": \"+917981214937\", \"name\": \"John Doe\", \"count\": \"5\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey! What's up? Did you get my message?\", \"time\": \"2024-12-10 10:00:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yeah, I saw it! Just been busy with work, you know how it is.\", \"time\": \"2024-12-10 10:05:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Totally. I feel that! Also, I got a reminder about that meeting tomorrow. Are you still on for 5 PM?\", \"time\": \"2024-12-10 10:10:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Yes! I’ll be there, just need to finish up a couple of things first.\", \"time\": \"2024-12-10 10:12:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sounds good! Oh, and don't forget about the new promo I sent you for that online course. It's a great deal!\", \"time\": \"2024-12-10 10:15:00\"}"
                + "]}, "
                + "{\"phone_number\": \"9398221797\", \"name\": \"Jane Smith\", \"count\": \"4\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Haha, I almost missed it. Thanks for the heads-up. I'll check it out later.\", \"time\": \"2024-12-10 10:20:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"No problem! It's a really good deal, you should definitely look at it. Let me know if you need the link again.\", \"time\": \"2024-12-10 10:25:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Will do! Anyway, are we still meeting for coffee later today?\", \"time\": \"2024-12-10 10:30:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Absolutely! I’ll meet you at 3 PM. See you then!\", \"time\": \"2024-12-10 10:35:00\"}"
                + "]}, "
                + "{\"phone_number\": \"9876543210\", \"name\": \"Mike Johnson\", \"count\": \"3\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Hey, do you have a moment to chat? I need some advice on a project.\", \"time\": \"2024-12-10 11:00:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sure! What's going on?\", \"time\": \"2024-12-10 11:05:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"I’m stuck on a presentation. Can you help me with the structure?\", \"time\": \"2024-12-10 11:10:00\"}"
                + "]}, "
                + "{\"phone_number\": \"+917032109876\", \"name\": \"Emily Brown\", \"count\": \"6\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, I got the files you sent. Thanks for that!\", \"time\": \"2024-12-10 12:00:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"No problem, glad they were useful!\", \"time\": \"2024-12-10 12:05:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Also, did you manage to finish that task I mentioned?\", \"time\": \"2024-12-10 12:10:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Almost there, just need a few more details from you.\", \"time\": \"2024-12-10 12:12:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Got it! I'll send you the details in a bit.\", \"time\": \"2024-12-10 12:15:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Perfect, looking forward to it!\", \"time\": \"2024-12-10 12:18:00\"}"
                + "]}, "
                + "{\"phone_number\": \"+919067890123\", \"name\": \"Chris Williams\", \"count\": \"2\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, let's catch up sometime soon!\", \"time\": \"2024-12-10 13:00:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Sounds good! Let's plan for next week.\", \"time\": \"2024-12-10 13:05:00\"}"
                + "]}, "
                + "{\"phone_number\": \"+918977665544\", \"name\": \"Olivia Harris\", \"count\": \"3\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Hey! Can you send me that document again? I lost it in the email thread.\", \"time\": \"2024-12-10 14:00:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Sure, I’ll send it over now.\", \"time\": \"2024-12-10 14:05:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Thanks! You're a lifesaver.\", \"time\": \"2024-12-10 14:10:00\"}"
                + "]}, "
                + "{\"phone_number\": \"+919234567890\", \"name\": \"Daniel Martinez\", \"count\": \"4\", \"messages\": ["
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Hey, can you remind me when the meeting is tomorrow?\", \"time\": \"2024-12-10 15:00:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"The meeting's at 9 AM tomorrow, don’t forget!\", \"time\": \"2024-12-10 15:05:00\"}, "
                + "{\"whatsapp_message_type\": \"sent\", \"content\": \"Thanks! I'll be there on time.\", \"time\": \"2024-12-10 15:10:00\"}, "
                + "{\"whatsapp_message_type\": \"received\", \"content\": \"Great! See you then.\", \"time\": \"2024-12-10 15:15:00\"}"
                + "]}"
                + "]";

        // Parse the JSON response using Gson
        Gson gson = new Gson();
        Type whatsAppMessagesListType = new TypeToken<List<WhatsAppMessages>>() {
        }.getType();
        whatsAppMessagesList = gson.fromJson(jsonResponse, whatsAppMessagesListType);
        whatsAppMessagesAdapter = new WhatsAppMessagesAdapter(whatsAppMessagesList, new OnWhatsAppMessagesClickListener() {
            @Override
            public void onWhatsAppMessagesClick(WhatsAppMessages whatsAppMessages) {
                Intent intent = new Intent(WhatsAppActivity.this, WhatsAppMessageDetailsActivity.class);
                intent.putExtra("contact_name", whatsAppMessages.getName());
                intent.putExtra("contact_phone_number", whatsAppMessages.getPhoneNumber());
                intent.putExtra("whatsapp_messages_details", new Gson().toJson(whatsAppMessages.getWhatsAppMessagesDetails()));
                startActivity(intent);
            }
        });

        messagesRecyclerView.setAdapter(whatsAppMessagesAdapter);
    }
}*/
