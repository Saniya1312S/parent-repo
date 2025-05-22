package com.example.parentwithsubscription.features.socialmedia.adapter.whatsapp;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.socialmedia.listener.whatsapp.OnWhatsAppMessagesClickListener;
import com.example.parentwithsubscription.features.socialmedia.model.whatsapp.WhatsAppMessages;
import com.example.parentwithsubscription.features.socialmedia.model.whatsapp.WhatsAppMessagesDetails;

import java.util.List;

public class WhatsAppMessagesAdapter extends RecyclerView.Adapter<WhatsAppMessagesAdapter.WhatsAppMessagesViewHolder> {

    private List<WhatsAppMessages> whatsAppMessagesList;
    private OnWhatsAppMessagesClickListener listener;

    public WhatsAppMessagesAdapter(List<WhatsAppMessages> whatsAppMessagesList, OnWhatsAppMessagesClickListener listener) {
        this.whatsAppMessagesList = whatsAppMessagesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WhatsAppMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_message_item, parent, false);
        return new WhatsAppMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WhatsAppMessagesViewHolder holder, int position) {
        WhatsAppMessages whatsAppMessages = whatsAppMessagesList.get(position);

        // Use a default placeholder image for now
        holder.contactImageView.setImageResource(R.drawable.ic_placeholder); // Use a default image

        // Set contact name
        String displayName = whatsAppMessages.getName();
        if (whatsAppMessages.getName() == null || whatsAppMessages.getName().trim().isEmpty() || whatsAppMessages.getName().equalsIgnoreCase("unknown")) {
            displayName = "Unknown Contact (" + whatsAppMessages.getCount() + ")";
            holder.nameTextView.setTextColor(Color.RED); // Change text color for unknown names
        } else {
            holder.nameTextView.setTextColor(Color.BLACK); // Default color for valid names
        }
        holder.nameTextView.setText(displayName);

        // Set the last message text
        String lastMessage = "";
        String sentPrefix = "";  // Variable to hold "sent:" prefix

        if (whatsAppMessages.getWhatsAppMessagesDetails() != null && !whatsAppMessages.getWhatsAppMessagesDetails().isEmpty()) {
            WhatsAppMessagesDetails lastMessageObj = whatsAppMessages.getWhatsAppMessagesDetails().get(whatsAppMessages.getWhatsAppMessagesDetails().size() - 1);
            lastMessage = lastMessageObj.getContent();

            // Check if the message is sent and prepend "sent:" if true
            if (lastMessageObj.getWhatsappMessageType().equalsIgnoreCase("sent")) { // Assuming there is an `isSent()` method or a boolean field indicating if it's sent
                String sentText = "sent: " + lastMessage;
                // Apply the color to "sent" only
                int start = sentText.toLowerCase().indexOf("sent");
                int end = start + "sent".length();

                // If "sent" is found in the message
                if (start != -1) {
                    SpannableString spannableMessage = new SpannableString(sentText);
                    spannableMessage.setSpan(new ForegroundColorSpan(Color.parseColor("#2196F3")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.lastMessageTextView.setText(spannableMessage);
                } else {
                    holder.lastMessageTextView.setText(sentText); // Just set it normally if no need to highlight
                }
            } else {
                holder.lastMessageTextView.setText(lastMessage); // Normal message without "sent:" prefix
            }
        }

        // Set the time
        String time = "";
        if (whatsAppMessages.getWhatsAppMessagesDetails() != null && !whatsAppMessages.getWhatsAppMessagesDetails().isEmpty()) {
            WhatsAppMessagesDetails lastMessageObj = whatsAppMessages.getWhatsAppMessagesDetails().get(whatsAppMessages.getWhatsAppMessagesDetails().size() - 1);
            time = lastMessageObj.getTime(); // Assuming the time is in a suitable format, e.g. "12:00 PM"
        }
//        holder.timeTextView.setText(time);
        // Convert the time string to a long value (milliseconds)
        try {
            long timestamp = Long.parseLong(time); // Assuming call.getTime() is a string representing milliseconds
            // Format the timestamp using the formatTimestamp method
            String formattedTime = URIConstants.formatTimestamp(timestamp);
            holder.timeTextView.setText(formattedTime); // Set the formatted time
            // Log the formatted timestamp for debugging
            Log.d("Time formatted", formattedTime);
        } catch (NumberFormatException e) {
            holder.timeTextView.setText(time); // Fallback to the original time if parsing fails
        }


        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onWhatsAppMessagesClick(whatsAppMessages));
    }


    @Override
    public int getItemCount() {
        return whatsAppMessagesList.size();
    }

    public static class WhatsAppMessagesViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImageView; // ID: profile_picture
        TextView nameTextView, timeTextView, lastMessageTextView;

        public WhatsAppMessagesViewHolder(View itemView) {
            super(itemView);
            contactImageView = itemView.findViewById(R.id.profile_picture); // Updated to use correct ID
            nameTextView = itemView.findViewById(R.id.contact_name);
            timeTextView = itemView.findViewById(R.id.message_time); // Updated to use correct ID
            lastMessageTextView = itemView.findViewById(R.id.last_message);
        }
    }
}












/*
package com.example.socialmediaapplication.whatsapp;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapplication.R;

import java.util.List;

public class WhatsAppMessagesAdapter extends RecyclerView.Adapter<WhatsAppMessagesAdapter.WhatsAppMessagesViewHolder>{

    private List<WhatsAppMessages> whatsAppMessagesList;
    private OnWhatsAppMessagesClickListener listener;

    public WhatsAppMessagesAdapter(List<WhatsAppMessages> whatsAppMessagesList, OnWhatsAppMessagesClickListener listener) {
        this.whatsAppMessagesList = whatsAppMessagesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WhatsAppMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_message_item, parent, false);
        return new WhatsAppMessagesAdapter.WhatsAppMessagesViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull WhatsAppMessagesViewHolder holder, int position) {
        WhatsAppMessages whatsAppMessages = whatsAppMessagesList.get(position);
        String displayName = whatsAppMessages.getName() + " (" + whatsAppMessages.getCount() + ")";
        // Log the message being bound
        Log.d("WhatsAppMessagesAdapter", "Binding message: " + displayName);
        if (whatsAppMessages.getName() == null || whatsAppMessages.getName().trim().isEmpty() || whatsAppMessages.getName().equalsIgnoreCase("unknown")) {

            displayName = "Unknown Contact (" + whatsAppMessages.getCount() + ")"; // Prepending "Unknown Contact"
            holder.nameTextView.setTextColor(Color.RED); // Set text color to red for unknown names
//            holder.phoneNumberTextView.setTextColor(Color.RED);
        } else {
            holder.nameTextView.setTextColor(Color.BLACK); // Set text color to black for valid names
        }

        // Set the final name with prepended text if needed
        holder.nameTextView.setText(displayName);

        // Set the phone number
        holder.phoneNumberTextView.setText(whatsAppMessages.getPhoneNumber());

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> listener.onWhatsAppMessagesClick(whatsAppMessages));
    }


    @Override
    public int getItemCount() {
        return whatsAppMessagesList.size();
    }



    public static class WhatsAppMessagesViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneNumberTextView;

        public WhatsAppMessagesViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneNumberTextView = itemView.findViewById(R.id.contact_phone_number);
        }
    }
}
*/
