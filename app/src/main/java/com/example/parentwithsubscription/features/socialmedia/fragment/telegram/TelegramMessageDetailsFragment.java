package com.example.parentwithsubscription.features.socialmedia.fragment.telegram;

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
import com.example.parentwithsubscription.features.socialmedia.model.whatsapp.WhatsAppMessagesDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TelegramMessageDetailsFragment extends Fragment {

    private TextView contactNameTextView, phoneNumberTextView;
    private RecyclerView telegramMessagesRecyclerView;
    private TelegramMessagesAdapter telegramMessagesAdapter;
    private List<WhatsAppMessagesDetails> telegramMessagesDetailsList;

    private String contactName;
    private String phoneNumber;
    private String telegramMessagesJson;

    // Constructor
    public static TelegramMessageDetailsFragment newInstance(String contactName, String phoneNumber, String telegramMessagesJson) {
        TelegramMessageDetailsFragment fragment = new TelegramMessageDetailsFragment();
        Bundle args = new Bundle();
        args.putString("contact_name", contactName);
        args.putString("contact_phone_number", phoneNumber);
        args.putString("messages_details", telegramMessagesJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.telegram_message_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactNameTextView = view.findViewById(R.id.contact_name);
        phoneNumberTextView = view.findViewById(R.id.contact_phone_number);
        telegramMessagesRecyclerView = view.findViewById(R.id.telegram_messages_recyclerview);

        telegramMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the arguments passed from the parent activity or fragment
        if (getArguments() != null) {
            contactName = getArguments().getString("contact_name");
            phoneNumber = getArguments().getString("contact_phone_number");
            telegramMessagesJson = getArguments().getString("messages_details");
        }

        // Set contact name and phone number
        contactNameTextView.setText(contactName == null || contactName.isEmpty() ? "Unknown" : contactName);
        phoneNumberTextView.setText(phoneNumber);

        // Parse the Telegram messages into a list
        Gson gson = new Gson();
        Type type = new TypeToken<List<WhatsAppMessagesDetails>>() {}.getType();
        telegramMessagesDetailsList = gson.fromJson(telegramMessagesJson, type);

        // Set the adapter for the RecyclerView
        telegramMessagesAdapter = new TelegramMessagesAdapter(telegramMessagesDetailsList);
        telegramMessagesRecyclerView.setAdapter(telegramMessagesAdapter);
    }

    public static class TelegramMessagesAdapter extends RecyclerView.Adapter<TelegramMessagesAdapter.TelegramMessageViewHolder> {

        private final List<WhatsAppMessagesDetails> messages;

        public TelegramMessagesAdapter(List<WhatsAppMessagesDetails> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public TelegramMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the message item layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_message_detail_item, parent, false);
            return new TelegramMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TelegramMessageViewHolder holder, int position) {
            WhatsAppMessagesDetails message = messages.get(position);

            // Set the message content
            String smsContentText = message.getContent();
            // Set the timestamp
            try {
                long timestamp = Long.parseLong(message.getTime()); // Assuming message.getTime() is a string representing milliseconds
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
            if ("sent".equalsIgnoreCase(message.getWhatsappMessageType())) {
                holder.messageContainer.setGravity(Gravity.END);  // Sent messages align to the right
                holder.messageText.setBackgroundResource(R.drawable.telegram_message_bubble_sent);
                holder.timestamp.setGravity(Gravity.END);  // Timestamp for sent message
            } else {
                holder.messageContainer.setGravity(Gravity.START);  // Received messages align to the left
                holder.messageText.setBackgroundResource(R.drawable.telegram_message_bubble_received);
                holder.timestamp.setGravity(Gravity.START);  // Timestamp for received message
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public static class TelegramMessageViewHolder extends RecyclerView.ViewHolder {

            TextView messageText, timestamp;
            LinearLayout messageContainer;

            public TelegramMessageViewHolder(@NonNull View itemView) {
                super(itemView);
                messageText = itemView.findViewById(R.id.whatsapp_message_content);
                timestamp = itemView.findViewById(R.id.whatsapp_message_date_time);
                messageContainer = itemView.findViewById(R.id.whatsapp_message_container);
            }
        }
    }
}
