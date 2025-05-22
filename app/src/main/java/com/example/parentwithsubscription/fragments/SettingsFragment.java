package com.example.parentwithsubscription.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.parentwithsubscription.MainActivity;
import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.GlobalData;
import com.google.android.material.card.MaterialCardView;

public class SettingsFragment extends Fragment {

    private MaterialCardView changePasswordCard, logoutCard;
    private ImageView icPassword, icLogout;
    private TextView changePasswordText, logoutText;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize views
        initViews(view);

        // Set onClick listeners for each action
        setUpListeners();

        return view;
    }

    private void initViews(View view) {
        // Initialize MaterialCardViews
        changePasswordCard = view.findViewById(R.id.cardChangePassword);
        logoutCard = view.findViewById(R.id.cardLogout);

        // Initialize ImageViews
        icPassword = view.findViewById(R.id.icPassword);
        icLogout = view.findViewById(R.id.icLogout);

        // Initialize TextViews (for labels)
        changePasswordText = view.findViewById(R.id.changePasswordText);
        logoutText = view.findViewById(R.id.logoutText);
    }

    private void setUpListeners() {
        // Click listener for "Change Password" card
        changePasswordCard.setOnClickListener(v -> {
            // Navigate to Change Password screen
            Toast.makeText(getContext(), "Navigate to Change Password Screen", Toast.LENGTH_SHORT).show();
        });

        // Click listener for "Logout" card
        logoutCard.setOnClickListener(v -> {
            // Clear the shared preferences (remove guest data)
            clearUserData();

            // Redirect to the main activity (Login screen)
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();  // Close the current activity
        });
    }

    private void clearUserData() {
        // Clear SharedPreferences data
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("guest_user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Clear the shared preferences to log out the user
        editor.clear();
        editor.apply();

        // Optionally clear other global data (if you're using GlobalData)
        GlobalData.setDeviceId("null");

        Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
    }
}

