package com.example.parentwithsubscription.features.socialmedia.activity.twitter;

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

public class TwitterMessageDetailsActivity extends AppCompatActivity {

    private TextView userIdTextView;
    private RecyclerView twitterMessagesRecyclerView;
    private TwitterMessageDetailsAdapter twitterMessagesAdapter;
    private List<InstagramMessageDetails> twitterMessageDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_message_detail);  // Use correct layout XML for Twitter message details

        // Initialize the views
        userIdTextView = findViewById(R.id.user_id);
        twitterMessagesRecyclerView = findViewById(R.id.twitter_messages_recyclerview);

        // Set up RecyclerView with LinearLayoutManager
        twitterMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the contact details passed from the main activity
        String userId = getIntent().getStringExtra("user_id");
        String twitterMessagesJson = getIntent().getStringExtra("twitter_messages_details");

        // Set contact name or "Unknown"
        userIdTextView.setText(userId == null || userId.isEmpty() ? "Unknown" : userId);

        // Parse the Twitter messages into a list
        Gson gson = new Gson();
        Type type = new TypeToken<List<InstagramMessageDetails>>() {}.getType();
        twitterMessageDetailsList = gson.fromJson(twitterMessagesJson, type);

        // Set the adapter for the RecyclerView
        twitterMessagesAdapter = new TwitterMessageDetailsAdapter(twitterMessageDetailsList);
        twitterMessagesRecyclerView.setAdapter(twitterMessagesAdapter);
    }

    public static class TwitterMessageDetailsAdapter extends RecyclerView.Adapter<TwitterMessageDetailsAdapter.TwitterMessageViewHolder>{

        private List<InstagramMessageDetails> twitterMessageDetailsList;

        public TwitterMessageDetailsAdapter(List<InstagramMessageDetails> twitterMessageDetailsList) {
            this.twitterMessageDetailsList = twitterMessageDetailsList;
        }

        @NonNull
        @Override
        public TwitterMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_message_detail_item, parent, false);
            return new TwitterMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TwitterMessageViewHolder holder, int position) {
            InstagramMessageDetails message = twitterMessageDetailsList.get(position);

            // Set the message content
            String messageContent = message.getContent();
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
            SpannableString spannableMessage = new SpannableString(messageContent);

            // Append the message status to the content (e.g., (Spam), (Warning))
            if ("spam".equalsIgnoreCase(messageStatus)) {
                messageContent = messageContent + " (Spam)";  // Append "(Spam)" to the message
                spannableMessage = new SpannableString(messageContent);
                int spamStartIndex = messageContent.length() - 6; // Start index for "(Spam)"
                int spamEndIndex = messageContent.length();
                spannableMessage.setSpan(new ForegroundColorSpan(Color.RED), spamStartIndex, spamEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ("warning".equalsIgnoreCase(messageStatus)) {
                messageContent = messageContent + " (Warning)";  // Append "(Warning)" to the message
                spannableMessage = new SpannableString(messageContent);
                int warningStartIndex = messageContent.length() - 9; // Start index for "(Warning)"
                int warningEndIndex = messageContent.length();
                spannableMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#FFA500")), warningStartIndex, warningEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            Log.d("Message Status", String.valueOf(spannableMessage));
            // Set the message content with the updated spannable text (status colored)
            holder.messageText.setText(spannableMessage);

            // Set gravity and background depending on the message type
            if ("sent".equalsIgnoreCase(message.getMessageType())) {
                holder.messageContainer.setGravity(Gravity.END);  // Sent messages align to the right
                holder.messageText.setBackgroundResource(R.drawable.twitter_message_bubble_sent);  // Use Twitter-specific bubble
                holder.messageText.setTextColor(Color.WHITE);
                holder.timestamp.setGravity(Gravity.END);  // Timestamp for sent message
            } else {
                holder.messageContainer.setGravity(Gravity.START);  // Received messages align to the left
                holder.messageText.setBackgroundResource(R.drawable.twitter_message_bubble_received);  // Use Twitter-specific bubble
                holder.timestamp.setGravity(Gravity.START);  // Timestamp for received message
            }
        }

        @Override
        public int getItemCount() {
            return twitterMessageDetailsList.size();
        }

        public static class TwitterMessageViewHolder extends RecyclerView.ViewHolder {

            TextView messageText, timestamp;
            LinearLayout messageContainer;

            public TwitterMessageViewHolder(@NonNull View itemView) {
                super(itemView);
                messageText = itemView.findViewById(R.id.whatsapp_message_content);
                timestamp = itemView.findViewById(R.id.whatsapp_message_date_time);
                messageContainer = itemView.findViewById(R.id.whatsapp_message_container);
            }
        }
    }
}
