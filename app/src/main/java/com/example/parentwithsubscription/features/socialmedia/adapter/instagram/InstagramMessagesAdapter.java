package com.example.parentwithsubscription.features.socialmedia.adapter.instagram;

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
import com.example.parentwithsubscription.features.socialmedia.listener.instagram.OnInstagramMessagesClickListener;
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramMessageDetails;
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramMessages;

import java.util.List;

public class InstagramMessagesAdapter extends RecyclerView.Adapter<InstagramMessagesAdapter.InstagramMessagesViewHolder> {

    private List<InstagramMessages> instagramMessagesList;
    private OnInstagramMessagesClickListener onInstagramMessagesClickListener;
    public InstagramMessagesAdapter(List<InstagramMessages> instagramMessagesList, OnInstagramMessagesClickListener onInstagramMessagesClickListener) {
        this.instagramMessagesList = instagramMessagesList;
        this.onInstagramMessagesClickListener = onInstagramMessagesClickListener;
    }

    @NonNull
    @Override
    public InstagramMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_message_item, parent, false);
        return new InstagramMessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstagramMessagesViewHolder holder, int position) {

        InstagramMessages instagramMessages = instagramMessagesList.get(position);

        // Use a default placeholder image for now
        holder.contactImageView.setImageResource(R.drawable.ic_placeholder); // Use a default image

        // Set contact name
        String displayName = instagramMessages.getUserId();
        if (instagramMessages.getUserId() == null || instagramMessages.getUserId().trim().isEmpty() || instagramMessages.getUserId().equalsIgnoreCase("unknown")) {
            displayName = "Unknown Contact";
            holder.nameTextView.setTextColor(Color.RED); // Change text color for unknown names
        } else {
            holder.nameTextView.setTextColor(Color.BLACK); // Default color for valid names
        }
        holder.nameTextView.setText(displayName);

        // Set the last message text
        String lastMessage = "";
        String sentPrefix = "";  // Variable to hold "sent:" prefix

        if (instagramMessages.getInstagramMessageDetails() != null && !instagramMessages.getInstagramMessageDetails().isEmpty()) {
            InstagramMessageDetails lastMessageObj = instagramMessages.getInstagramMessageDetails().get(instagramMessages.getInstagramMessageDetails().size() - 1);
            lastMessage = lastMessageObj.getContent();

            // Check if the message is sent and prepend "sent:" if true
            if (lastMessageObj.getMessageType().equalsIgnoreCase("sent")) { // Assuming there is an `isSent()` method or a boolean field indicating if it's sent
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
        if (instagramMessages.getInstagramMessageDetails() != null && !instagramMessages.getInstagramMessageDetails().isEmpty()) {
            InstagramMessageDetails lastMessageObj = instagramMessages.getInstagramMessageDetails().get(instagramMessages.getInstagramMessageDetails().size() - 1);
            time = lastMessageObj.getTime(); // Assuming the time is in a suitable format, e.g. "12:00 PM"
        }
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
        holder.itemView.setOnClickListener(v -> onInstagramMessagesClickListener.onInstagramMessageClick(instagramMessages));
        }

    @Override
    public int getItemCount() {
        return instagramMessagesList.size();
    }

    public static class InstagramMessagesViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImageView; // ID: profile_picture
        TextView nameTextView, timeTextView, lastMessageTextView;

        public InstagramMessagesViewHolder(View itemView) {
            super(itemView);
            contactImageView = itemView.findViewById(R.id.profile_picture); // Updated to use correct ID
            nameTextView = itemView.findViewById(R.id.contact_name);
            timeTextView = itemView.findViewById(R.id.message_time); // Updated to use correct ID
            lastMessageTextView = itemView.findViewById(R.id.last_message);
        }
    }
}
