package com.example.parentwithsubscription.features.socialmedia.adapter.whatsapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.socialmedia.model.whatsapp.WhatsAppCall;

import java.util.List;

public class WhatsAppCallAdapter extends RecyclerView.Adapter<WhatsAppCallAdapter.ViewHolder> {

    private List<WhatsAppCall> callList;

    public WhatsAppCallAdapter(List<WhatsAppCall> callList) {
        this.callList = callList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_calls_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WhatsAppCall call = callList.get(position);
// After parsing the JSON
        Log.d("CallsFragment", "WhatsApp Calls List: " + callList.get(position));
        Log.d("Phone number","Phone number: " + call.getPhoneNumber());
        Log.d("Call Type", "Call Type: " + call.getCallType());
        Log.d("Call Mode", "Call Mode: " +  call.getCallMode());
        Log.d("Call Time", "Call Time: " +  call.getTime());
        // Display Name and Phone Number in the format "Name (Phone Number)"
        holder.contactDetailsTextView.setText(call.getName() + " (" + call.getPhoneNumber() + ")");
//        holder.callTimeTextView.setText(call.getTime());
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
        return callList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactDetailsTextView; // ID from XML
        public TextView callTimeTextView;
        public TextView callDurationTextView;
        public ImageView callIcon;
        public ImageView modeIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            contactDetailsTextView = itemView.findViewById(R.id.contact_details); // Updated ID
            callTimeTextView = itemView.findViewById(R.id.whatsapp_call_time);
            callIcon = itemView.findViewById(R.id.whatsapp_call_icon);
            modeIcon = itemView.findViewById(R.id.whatsapp_call_mode_icon);
        }
    }
}
