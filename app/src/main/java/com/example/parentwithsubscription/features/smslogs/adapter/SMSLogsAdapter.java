package com.example.parentwithsubscription.features.smslogs.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.smslogs.listener.OnSMSLogClickListener;
import com.example.parentwithsubscription.features.smslogs.model.SMSLogs;

import java.util.List;

public class SMSLogsAdapter extends RecyclerView.Adapter<SMSLogsAdapter.SMSViewHolder> {

    private List<SMSLogs> smsLogsList;
    private OnSMSLogClickListener listener;

    public SMSLogsAdapter(List<SMSLogs> smsLogsList, OnSMSLogClickListener listener) {
        this.smsLogsList = smsLogsList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public SMSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_log_item, parent, false);
        return new SMSViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SMSViewHolder holder, int position) {
SMSLogs smsLogs = smsLogsList.get(position);
        String displayName = smsLogs.getName() + " (" + smsLogs.getCount() + ")";

        if (smsLogs.getName() == null || smsLogs.getName().trim().isEmpty() || smsLogs.getName().equalsIgnoreCase("unknown")){

            displayName = "Unknown Contact (" + smsLogs.getCount() + ")"; // Prepending "Unknown Contact"
            holder.nameTextView.setTextColor(Color.RED); // Set text color to red for unknown names
//            holder.phoneNumberTextView.setTextColor(Color.RED);
        } else {
            holder.nameTextView.setTextColor(Color.BLACK); // Set text color to black for valid names
        }

        // Set the final name with prepended text if needed
        holder.nameTextView.setText(displayName);

        // Set the phone number
        holder.phoneNumberTextView.setText(smsLogs.getPhoneNumber());

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> listener.onSMSClick(smsLogs));
    }


    @Override
    public int getItemCount() {
        return smsLogsList.size();
    }

    public static class SMSViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneNumberTextView;

        public SMSViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.contact_name);
            phoneNumberTextView = itemView.findViewById(R.id.contact_phone_number);
        }
    }
}





