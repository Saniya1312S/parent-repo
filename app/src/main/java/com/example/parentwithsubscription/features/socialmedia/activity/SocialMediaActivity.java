package com.example.parentwithsubscription.features.socialmedia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parentwithsubscription.R;

public class SocialMediaActivity extends AppCompatActivity{
    LinearLayout whatsAppCard, instagramCard, facebookCard, snapChatCard, twitterCard, telegramCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        whatsAppCard = findViewById(R.id.whatsapp_card);
        instagramCard = findViewById(R.id.instagram_card);
        facebookCard = findViewById(R.id.facebook_card);
        snapChatCard = findViewById(R.id.snapchat_card);
        twitterCard = findViewById(R.id.twitter_card);
        telegramCard = findViewById(R.id.telegram_card);

        // Set OnClickListener for the WhatsApp card
        whatsAppCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SocialMediaActivity with WhatsApp platform
                Intent intent = new Intent(SocialMediaActivity.this, SocialMediaCommonActivity.class);
                intent.putExtra("PLATFORM", "WhatsApp"); // Send the platform info
                startActivity(intent);
            }
        });

        // Set OnClickListener for the Instagram card
        instagramCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SocialMediaActivity with Instagram platform
                Intent intent = new Intent(SocialMediaActivity.this, SocialMediaCommonActivity.class);
                intent.putExtra("PLATFORM", "Instagram"); // Send the platform info
                startActivity(intent);
            }
        });

        // Set OnClickListener for the Facebook card
        facebookCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SocialMediaActivity with Instagram platform
                Intent intent = new Intent(SocialMediaActivity.this, SocialMediaCommonActivity.class);
                intent.putExtra("PLATFORM", "Facebook"); // Send the platform info
                startActivity(intent);
            }
        });

        // Set OnClickListener for the SnapChat card
        snapChatCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SocialMediaActivity with Instagram platform
                Intent intent = new Intent(SocialMediaActivity.this, SocialMediaCommonActivity.class);
                intent.putExtra("PLATFORM", "Snapchat"); // Send the platform info
                startActivity(intent);
            }
        });

        // Set OnClickListener for the Twitter card
        twitterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SocialMediaActivity with Instagram platform
                Intent intent = new Intent(SocialMediaActivity.this, SocialMediaCommonActivity.class);
                intent.putExtra("PLATFORM", "Twitter"); // Send the platform info
                startActivity(intent);
            }
        });

        // Set OnClickListener for the Telegram card
        telegramCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SocialMediaActivity with Instagram platform
                Intent intent = new Intent(SocialMediaActivity.this, SocialMediaCommonActivity.class);
                intent.putExtra("PLATFORM", "Telegram"); // Send the platform info
                startActivity(intent);
            }
        });

    }
}








/*
package com.example.parent.features.socialmedia.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.socialmedia.adapter.SocialMediaAdapter;
import com.example.parent.features.socialmedia.model.SocialMediaData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SocialMediaActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private SocialMediaAdapter notificationAdapter;
    private List<SocialMediaData> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media.xml);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.socialMediaRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample JSON data
        String jsonData = "["
                + "{\"appName\":\"WhatsApp\", \"packageName\":\"com.whatsapp\", \"senderName\":\"John Doe\", \"message\":\"Hey! Are you free for a call?\"},"
                + "{\"appName\":\"Snapchat\", \"packageName\":\"com.snapchat.android\", \"senderName\":\"Jane Smith\", \"message\":\"Check out my new story!\"},"
                + "{\"appName\":\"Instagram\", \"packageName\":\"com.instagram.android\", \"senderName\":\"Alice Johnson\", \"message\":\"Your recent post got 100 likes!\"},"
                + "{\"appName\":\"YouTube\", \"packageName\":\"com.google.android.youtube\", \"senderName\":\"YouTube\", \"message\":\"New video suggestion: 'Learn Java in 30 minutes'\"},"
                + "{\"appName\":\"WhatsApp\", \"packageName\":\"com.whatsapp\", \"senderName\":\"Michael Brown\", \"message\":\"Can you send me the report by tomorrow?\"}"
                + "]";

        // Parse JSON to List<NotificationData>
        Gson gson = new Gson();
        Type listType = new TypeToken<List<SocialMediaData>>(){}.getType();
        notificationList = gson.fromJson(jsonData, listType);

        // Initialize the adapter and set it to RecyclerView
        notificationAdapter = new SocialMediaAdapter(notificationList, this);
        recyclerView.setAdapter(notificationAdapter);
    }
}
*/
