package com.example.parentwithsubscription.features.socialmedia.activity.facebook;

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
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramMessageDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class FacebookMessageDetailsActivity extends AppCompatActivity {

    private TextView userIdTextView;
    private RecyclerView facebookMessagesRecyclerView;
    private FacebookMessageDetailsAdapter facebookMessagesAdapter;
    private List<InstagramMessageDetails> facebookMessageDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instagram_message_details);

        // Initialize the views
        userIdTextView = findViewById(R.id.user_id);
        facebookMessagesRecyclerView = findViewById(R.id.instagram_messages_recyclerview);

        // Set up RecyclerView with LinearLayoutManager
        facebookMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the contact details passed from the main activity
        String userId = getIntent().getStringExtra("user_id");
        String instagramMessagesJson = getIntent().getStringExtra("facebook_messages_details");

        // Set contact name or "Unknown"
        userIdTextView.setText(userId == null || userId.isEmpty() ? "Unknown" : userId);


        // Parse the WhatsApp messages into a list
        Gson gson = new Gson();
        Type type = new TypeToken<List<InstagramMessageDetails>>() {}.getType();
        facebookMessageDetailsList = gson.fromJson(instagramMessagesJson, type);

        // Set the adapter for the RecyclerView
        facebookMessagesAdapter = new FacebookMessageDetailsAdapter(facebookMessageDetailsList);
        facebookMessagesRecyclerView.setAdapter(facebookMessagesAdapter);
    }

    public static class FacebookMessageDetailsAdapter extends RecyclerView.Adapter<FacebookMessageDetailsAdapter.FacebookMessageViewHolder>{

        private List<InstagramMessageDetails> facebookMessageDetailsList;
        public FacebookMessageDetailsAdapter(List<InstagramMessageDetails> facebookMessageDetailsList) {
            this.facebookMessageDetailsList = facebookMessageDetailsList;
        }

        @NonNull
        @Override
        public FacebookMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_message_detail_item, parent, false);
            return new FacebookMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FacebookMessageViewHolder holder, int position) {
            InstagramMessageDetails message = facebookMessageDetailsList.get(position);

            // Set the message content
//            holder.messageText.setText(message.getContent());
            String smsContentText = message.getContent();
            // Set the timestamp
            try {
                long timestamp = Long.parseLong(message.getTime()); // Assuming call.getTime() is a string representing milliseconds
                // Format the timestamp using the formatTimestamp method
                String formattedTime = URIConstants.formatTimestamp(timestamp);
                holder.timestamp.setText(formattedTime); // Set the formatted time
                // Log the formatted timestamp for debugging
                Log.d("Time formatted", formattedTime);
            } catch (NumberFormatException e) {
                holder.timestamp.setText(message.getTime()); // Fallback to the original time if parsing fails
            }
            String messageStatus = message.getClassification();


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

            Log.d("Spam Ham", String.valueOf(spannableMessage));
            // Set the message content with the updated spannable text (status colored)
            holder.messageText.setText(spannableMessage);


            // Set gravity and background depending on the message type
            if ("sent".equalsIgnoreCase(message.getMessageType())) {
                holder.messageContainer.setGravity(Gravity.END);  // Sent messages align to the right
                holder.messageText.setBackgroundResource(R.drawable.facebook_message_bubble_sent);
                holder.messageText.setTextColor(Color.WHITE);
                holder.timestamp.setGravity(Gravity.END);  // Timestamp for sent message
            } else {
                holder.messageContainer.setGravity(Gravity.START);  // Received messages align to the left
                holder.messageText.setBackgroundResource(R.drawable.instagram_message_bubble_received);
                holder.timestamp.setGravity(Gravity.START);  // Timestamp for received message
            }
        }

        @Override
        public int getItemCount() {
            return facebookMessageDetailsList.size();
        }

        public class FacebookMessageViewHolder extends RecyclerView.ViewHolder {

            TextView messageText, timestamp;
            LinearLayout messageContainer;

            public FacebookMessageViewHolder(@NonNull View itemView) {
                super(itemView);
                messageText = itemView.findViewById(R.id.whatsapp_message_content);
                timestamp = itemView.findViewById(R.id.whatsapp_message_date_time);
                messageContainer = itemView.findViewById(R.id.whatsapp_message_container);
            }
        }
    }
}
