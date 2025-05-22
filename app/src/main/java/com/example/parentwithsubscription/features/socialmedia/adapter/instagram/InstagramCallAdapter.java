package com.example.parentwithsubscription.features.socialmedia.adapter.instagram;

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
import com.example.parentwithsubscription.features.socialmedia.model.instagram.InstagramCalls;

import java.util.List;

public class InstagramCallAdapter extends RecyclerView.Adapter<InstagramCallAdapter.ViewHolder>  {

    private List<InstagramCalls> instagramCallsList;

    public InstagramCallAdapter(List<InstagramCalls> instagramCallsList) {
        Log.d("Adagter for Insta", instagramCallsList.toString());
        this.instagramCallsList = instagramCallsList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.instagram_calls_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InstagramCalls call = instagramCallsList.get(position);
// After parsing the JSON
        Log.d("CallsFragment", "Instagram Calls List: " + instagramCallsList.get(position));
        Log.d("Call Type", "Call Type: " + call.getCallType());
        Log.d("Call Mode", "Call Mode: " +  call.getCallMode());
        // Display Name and Phone Number in the format "Name (Phone Number)"
        holder.userIdTextView.setText(call.getUserId());
        // Convert the time string to a long value (milliseconds)
        try {
            long timestamp = Long.parseLong(call.getTime()); // Assuming call.getTime() is a string representing milliseconds
            // Format the timestamp using the formatTimestamp method
            String formattedTime = URIConstants.formatTimestamp(timestamp);
            holder.callTimeTextView.setText(formattedTime); // Set the formatted time
            // Log the formatted timestamp for debugging
            Log.d("Call Time formatted", formattedTime);
        } catch (NumberFormatException e) {
            Log.e("WhatsAppCallAdapter", "Invalid time format: " + call.getTime(), e);
            holder.callTimeTextView.setText(call.getTime()); // Fallback to the original time if parsing fails
        }

        // Set call type icon (incoming or outgoing)
        if (call.getCallType() != null && call.getCallType().equals("incoming")) {
            if (call.getDuration().equals("0") || call.getDuration() == null) {
                holder.callIcon.setImageResource(R.drawable.ic_whatsapp_incoming_missedcall_icon); // Missed call icon
            } else {
                holder.callIcon.setImageResource(R.drawable.ic_whatsapp_incomingcall_icon); // Incoming call icon
            }
        } else if (call.getCallType() != null && call.getCallType().equals("outgoing")) {
            if (call.getDuration().equals("0") || call.getDuration() == null) {
                holder.callIcon.setImageResource(R.drawable.ic_whatsapp_outgoing_missedcall_icon); // Missed call icon
            } else {
                holder.callIcon.setImageResource(R.drawable.ic_whatsapp_outgoingcall_icon); // Outgoing call icon
            }
        }

        // Set the appropriate call mode icon (video or voice)
        if (call.getCallMode() != null && call.getCallMode().equals("video")) {
            holder.modeIcon.setImageResource(R.drawable.ic_whatsapp_video_icon); // Video call icon
        } else {
            holder.modeIcon.setImageResource(R.drawable.ic_whatsapp_call_icon); // Voice call icon
        }
    }



    @Override
    public int getItemCount() {
        return instagramCallsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userIdTextView, callTimeTextView, callDurationTextView;
        public ImageView callIcon, modeIcon;


        public ViewHolder(View itemView) {
            super(itemView);
            userIdTextView = itemView.findViewById(R.id.user_id); // Updated ID
            callTimeTextView = itemView.findViewById(R.id.call_time);
            callIcon = itemView.findViewById(R.id.whatsapp_call_icon);
            modeIcon = itemView.findViewById(R.id.whatsapp_call_mode_icon);
        }
    }
}
