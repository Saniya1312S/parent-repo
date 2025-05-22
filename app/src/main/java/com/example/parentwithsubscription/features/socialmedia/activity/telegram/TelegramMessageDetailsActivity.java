package com.example.parentwithsubscription.features.socialmedia.activity.telegram;

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
import com.example.parentwithsubscription.features.socialmedia.model.whatsapp.WhatsAppMessagesDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TelegramMessageDetailsActivity extends AppCompatActivity {

    private TextView contactNameTextView, phoneNumberTextView;
    private RecyclerView telegramMessagesRecyclerView;
    private TelegramMessagesAdapter telegramMessagesAdapter;
    private List<WhatsAppMessagesDetails> telegramMessagesDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telegram_message_detail);

        // Initialize the views
        contactNameTextView = findViewById(R.id.contact_name);
        phoneNumberTextView = findViewById(R.id.contact_phone_number);
        telegramMessagesRecyclerView = findViewById(R.id.telegram_messages_recyclerview);

        // Set up RecyclerView with LinearLayoutManager
        telegramMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the contact details passed from the main activity
        String contactName = getIntent().getStringExtra("contact_name");
        String phoneNumber = getIntent().getStringExtra("contact_phone_number");
        String whatsappMessagesJson = getIntent().getStringExtra("messages_details");

        // Set contact name or "Unknown"
        contactNameTextView.setText(contactName == null || contactName.isEmpty() ? "Unknown" : contactName);
        phoneNumberTextView.setText(phoneNumber);

        // Parse the WhatsApp messages into a list
        Gson gson = new Gson();
        Type type = new TypeToken<List<WhatsAppMessagesDetails>>() {}.getType();
        telegramMessagesDetailsList = gson.fromJson(whatsappMessagesJson, type);

        // Set the adapter for the RecyclerView
        telegramMessagesAdapter = new TelegramMessagesAdapter(telegramMessagesDetailsList);
        telegramMessagesRecyclerView.setAdapter(telegramMessagesAdapter);
    }

    public static class TelegramMessagesAdapter extends RecyclerView.Adapter<TelegramMessagesAdapter.TelegramMessageViewHolder> {

        private List<WhatsAppMessagesDetails> messages;

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

        public class TelegramMessageViewHolder extends RecyclerView.ViewHolder {

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