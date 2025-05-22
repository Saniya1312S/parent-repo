package com.example.parentwithsubscription.features.socialmedia.activity.snapchat;

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

public class SnapChatMessageDetailsActivity extends AppCompatActivity {

    private TextView userIdTextView;
    private RecyclerView snapchatMessagesRecyclerView;
    private SnapChatMessageDetailsAdapter snapchatMessagesAdapter;
    private List<InstagramMessageDetails> snapchatMessageDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snapchat_message_details);

        // Initialize the views
        userIdTextView = findViewById(R.id.user_id);
        snapchatMessagesRecyclerView = findViewById(R.id.snapchat_messages_recyclerview);

        // Set up RecyclerView with LinearLayoutManager
        snapchatMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the contact details passed from the main activity
        String userId = getIntent().getStringExtra("user_id");
        String snapchatMessagesJson = getIntent().getStringExtra("snapchat_messages_details");

        // Set contact name or "Unknown"
        userIdTextView.setText(userId == null || userId.isEmpty() ? "Unknown" : userId);

        // Parse the Snapchat messages into a list
        Gson gson = new Gson();
        Type type = new TypeToken<List<InstagramMessageDetails>>() {}.getType();
        snapchatMessageDetailsList = gson.fromJson(snapchatMessagesJson, type);

        // Set the adapter for the RecyclerView, passing the userId as a parameter
        snapchatMessagesAdapter = new SnapChatMessageDetailsAdapter(snapchatMessageDetailsList, userId);
        snapchatMessagesRecyclerView.setAdapter(snapchatMessagesAdapter);
    }

    public static class SnapChatMessageDetailsAdapter extends RecyclerView.Adapter<SnapChatMessageDetailsAdapter.SnapChatMessageViewHolder> {

        private List<InstagramMessageDetails> snapchatMessageDetailsList;
        private String userId;

        public SnapChatMessageDetailsAdapter(List<InstagramMessageDetails> snapchatMessageDetailsList, String userId) {
            this.snapchatMessageDetailsList = snapchatMessageDetailsList;
            this.userId = userId;  // Store user_id
        }

        @NonNull
        @Override
        public SnapChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snapchat_message_detail_item, parent, false);
            return new SnapChatMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SnapChatMessageViewHolder holder, int position) {
            InstagramMessageDetails message = snapchatMessageDetailsList.get(position);

            // Set the message content
            String messageContent = message.getContent();
            String messageTime = message.getTime();
            String messageStatus = message.getClassification();

            // Set the timestamp
            try {
                long timestamp = Long.parseLong(message.getTime()); // Assuming call.getTime() is a string representing milliseconds
                // Format the timestamp using the formatTimestamp method
                String formattedTime = URIConstants.formatTimestamp(timestamp);
                holder.timeText.setText(formattedTime); // Set the formatted time
                // Log the formatted timestamp for debugging
                Log.d("Time formatted", formattedTime);
            } catch (NumberFormatException e) {
                holder.timeText.setText(message.getTime()); // Fallback to the original time if parsing fails
            }

            // Initialize the SpannableString to apply different styles for classifications
            SpannableString spannableMessage = new SpannableString(messageContent);

            // Append the message status to the content (e.g., (Spam), (Warning))
            if ("spam".equalsIgnoreCase(messageStatus)) {
                spannableMessage = new SpannableString(messageContent + " (Spam)");
                spannableMessage.setSpan(new ForegroundColorSpan(Color.RED), messageContent.length(), spannableMessage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ("warning".equalsIgnoreCase(messageStatus)) {
                spannableMessage = new SpannableString(messageContent + " (Warning)");
                spannableMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#FFA500")), messageContent.length(), spannableMessage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // Set the message content with the updated spannable text (status colored)
            holder.messageText.setText(spannableMessage);

            // Set gravity and background depending on the message type (sent or received)
            if ("sent".equalsIgnoreCase(message.getMessageType())) {
                holder.userName.setText("ME");
                holder.userName.setTextColor(Color.RED);
                holder.timeText.setGravity(Gravity.END);  // Timestamp for sent message
                holder.messageLine.setBackgroundColor(Color.RED); // Red for sent messages
            } else {
                holder.userName.setText(userId.toUpperCase());
                holder.userName.setTextColor(Color.BLUE);
                holder.timeText.setGravity(Gravity.START);  // Timestamp for received message
                holder.messageLine.setBackgroundColor(Color.BLUE);  // Blue for received messages
            }
        }

        @Override
        public int getItemCount() {
            return snapchatMessageDetailsList.size();
        }

        public class SnapChatMessageViewHolder extends RecyclerView.ViewHolder {

            TextView userName, messageText, timeText;
            View messageLine;

            public SnapChatMessageViewHolder(@NonNull View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.user_label);
                messageText = itemView.findViewById(R.id.messageText);
                timeText = itemView.findViewById(R.id.timeText);
                messageLine = itemView.findViewById(R.id.messageLine);
            }
        }
    }
}

