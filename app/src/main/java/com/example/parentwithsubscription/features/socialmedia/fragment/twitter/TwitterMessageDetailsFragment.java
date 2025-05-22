package com.example.parentwithsubscription.features.socialmedia.fragment.twitter;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramMessageDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TwitterMessageDetailsFragment extends Fragment {

    private TextView userIdTextView;
    private RecyclerView twitterMessagesRecyclerView;
    private TwitterMessageDetailsAdapter twitterMessagesAdapter;
    private List<InstagramMessageDetails> twitterMessageDetailsList;

    private String userId;
    private String twitterMessagesJson;

    // Constructor to create a new instance of the fragment
    public static TwitterMessageDetailsFragment newInstance(String userId, String twitterMessagesJson) {
        TwitterMessageDetailsFragment fragment = new TwitterMessageDetailsFragment();
        Bundle args = new Bundle();
        args.putString("user_id", userId);
        args.putString("twitter_messages_details", twitterMessagesJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.twitter_message_detail, container, false); // Inflate the correct layout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userIdTextView = view.findViewById(R.id.user_id);
        twitterMessagesRecyclerView = view.findViewById(R.id.twitter_messages_recyclerview);

        twitterMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the arguments passed from the parent activity or fragment
        if (getArguments() != null) {
            userId = getArguments().getString("user_id");
            twitterMessagesJson = getArguments().getString("twitter_messages_details");
        }

        // Set user ID or "Unknown"
        userIdTextView.setText(userId == null || userId.isEmpty() ? "Unknown" : userId);

        // Parse the Twitter messages into a list
        Gson gson = new Gson();
        Type type = new TypeToken<List<InstagramMessageDetails>>() {}.getType();
        twitterMessageDetailsList = gson.fromJson(twitterMessagesJson, type);

        // Set the adapter for the RecyclerView
        twitterMessagesAdapter = new TwitterMessageDetailsAdapter(twitterMessageDetailsList);
        twitterMessagesRecyclerView.setAdapter(twitterMessagesAdapter);
    }

    public static class TwitterMessageDetailsAdapter extends RecyclerView.Adapter<TwitterMessageDetailsAdapter.TwitterMessageViewHolder> {

        private final List<InstagramMessageDetails> twitterMessageDetailsList;

        public TwitterMessageDetailsAdapter(List<InstagramMessageDetails> twitterMessageDetailsList) {
            this.twitterMessageDetailsList = twitterMessageDetailsList;
        }

        @NonNull
        @Override
        public TwitterMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the message item layout for Twitter
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
                long timestamp = Long.parseLong(message.getTime()); // Assuming message.getTime() is a string representing milliseconds
                // Format the timestamp using the formatTimestamp method
                String formattedTime = URIConstants.formatTimestamp(timestamp);
                holder.timestamp.setText(formattedTime); // Set the formatted time
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
