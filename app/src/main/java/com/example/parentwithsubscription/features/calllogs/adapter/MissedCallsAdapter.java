package com.example.parentwithsubscription.features.calllogs.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.calllogs.model.MissedCall;

import java.util.List;

public class MissedCallsAdapter extends RecyclerView.Adapter<MissedCallsAdapter.ViewHolder> {

    private List<MissedCall> missedCallsList;

    // Constructor
    public MissedCallsAdapter(List<MissedCall> missedCallsList) {
        this.missedCallsList = missedCallsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.missed_call_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MissedCall missedCall = missedCallsList.get(position);
        String phoneNumber = missedCall.getPhoneNumber();
        int missedCalls = missedCall.getMissedCalls();
        String name = missedCall.getName();  // Get the name

        // Set phone number and missed calls count
        holder.phoneNumberTextView.setText(phoneNumber);

        // Set the name
        holder.nameTextView.setText(name + " (" + missedCalls + ")");  // Set the name in the new TextView
    }

    @Override
    public int getItemCount() {
        return missedCallsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView phoneNumberTextView;
        TextView nameTextView;  // TextView for the name

        public ViewHolder(View itemView) {
            super(itemView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);  // Find the name TextView
        }
    }
}







/*
package com.example.parent.features.calllogs.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parent.R;
import com.example.parent.features.calllogs.model.MissedCall;

import java.util.List;

public class MissedCallsAdapter extends RecyclerView.Adapter<MissedCallsAdapter.ViewHolder> {

    private List<MissedCall> missedCallsList;

    // Constructor
    public MissedCallsAdapter(List<MissedCall> missedCallsList) {
        this.missedCallsList = missedCallsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.missed_call_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MissedCall missedCall = missedCallsList.get(position);
        String phoneNumber = missedCall.getPhoneNumber();
        int missedCalls = missedCall.getMissedCalls();

        // Format: "PhoneNumber (MissedCalls)"
        holder.phoneNumberTextView.setText(phoneNumber + " (" + missedCalls + ")");
    }

    @Override
    public int getItemCount() {
        return missedCallsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView phoneNumberTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
        }
    }
}
*/
