package com.example.parentwithsubscription.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.locationtracking.fragment.LocationTrackingFragment;

public class HomeFragment extends Fragment {

    private Fragment locationTrackingFragment;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 🔧 Ensure the container is visible
        View containerView = view.findViewById(R.id.location_tracking_fragment_container);
        if (containerView != null) {
            containerView.setVisibility(View.VISIBLE);
        }

        // Initialize and show the LocationTrackingFragment
        locationTrackingFragment = new LocationTrackingFragment();
        showFragment(locationTrackingFragment);

        return view;
    }

    private void showFragment(Fragment fragmentToShow) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        // Check if the fragment is already added
        if (!fragmentToShow.isAdded()) {
            // Replace any existing fragment with the new one
            transaction.replace(R.id.location_tracking_fragment_container, fragmentToShow);
        }

        // Commit the transaction to show the fragment
        transaction.commit();
    }
}











/*
package com.example.parentwithsubscription.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.parentwithsubscription.R;
import com.google.android.gms.maps.*;

public class HomeFragment extends Fragment {


    private SupportMapFragment mapFragment;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Reference all layouts
        View homeLayout = view.findViewById(R.id.homeLayout);
        View locationHistoryLayout = view.findViewById(R.id.locationHistoryLayout);
        View callDetailsLayout = view.findViewById(R.id.callDetailsLayout);
        View smsDetailsLayout = view.findViewById(R.id.smsDetailsLayout);
        View appUsageLayout = view.findViewById(R.id.appUsageLayout);
        View socialMediaLayout = view.findViewById(R.id.socialMediaLayout);
        View contactsLayout = view.findViewById(R.id.contactsLayout);

        // Show only homeLayout at start
        hideAllIncludedLayouts(locationHistoryLayout, callDetailsLayout, smsDetailsLayout, appUsageLayout, socialMediaLayout, contactsLayout);
        homeLayout.setVisibility(View.VISIBLE); // ✅ Default layout

        setupCards(view);

        return view;
    }

    private void setupCards(View view) {
        // Get references to included layouts
        View homeLayout = view.findViewById(R.id.homeLayout);
        View locationHistoryLayout = view.findViewById(R.id.locationHistoryLayout);
        View callDetailsLayout = view.findViewById(R.id.callDetailsLayout);
        View smsDetailsLayout = view.findViewById(R.id.smsDetailsLayout);
        View appUsageLayout = view.findViewById(R.id.appUsageLayout);
        View socialMediaLayout = view.findViewById(R.id.socialMediaLayout);
        View contactsLayout = view.findViewById(R.id.contactsLayout);

        // Optionally: Hide all first
        hideAllIncludedLayouts(locationHistoryLayout, callDetailsLayout, smsDetailsLayout, appUsageLayout, socialMediaLayout, contactsLayout);

        view.findViewById(R.id.gps_card).setOnClickListener(v -> {
            hideAllIncludedLayouts(homeLayout, callDetailsLayout, smsDetailsLayout, appUsageLayout, socialMediaLayout, contactsLayout);
            locationHistoryLayout.setVisibility(View.VISIBLE);
        });

        view.findViewById(R.id.call_logs_card).setOnClickListener(v -> {
            hideAllIncludedLayouts(homeLayout, callDetailsLayout, smsDetailsLayout, appUsageLayout, socialMediaLayout, contactsLayout);
            callDetailsLayout.setVisibility(View.VISIBLE);
        });

        view.findViewById(R.id.message_logs_card).setOnClickListener(v -> {
            hideAllIncludedLayouts(homeLayout, callDetailsLayout, smsDetailsLayout, appUsageLayout, socialMediaLayout, contactsLayout);
            smsDetailsLayout.setVisibility(View.VISIBLE);
        });

        view.findViewById(R.id.app_usage_card).setOnClickListener(v -> {
            hideAllIncludedLayouts(homeLayout, callDetailsLayout, smsDetailsLayout, appUsageLayout, socialMediaLayout, contactsLayout);
            appUsageLayout.setVisibility(View.VISIBLE);
        });

        view.findViewById(R.id.social_media_card).setOnClickListener(v -> {
            hideAllIncludedLayouts(homeLayout, callDetailsLayout, smsDetailsLayout, appUsageLayout, socialMediaLayout, contactsLayout);
            socialMediaLayout.setVisibility(View.VISIBLE);
        });

        view.findViewById(R.id.contacts_card).setOnClickListener(v -> {
            hideAllIncludedLayouts(homeLayout, callDetailsLayout, smsDetailsLayout, appUsageLayout, socialMediaLayout, contactsLayout);
            contactsLayout.setVisibility(View.VISIBLE);
        });
    }

    private void hideAllIncludedLayouts(View... layouts) {
        for (View layout : layouts) {
            layout.setVisibility(View.GONE);
        }
    }
}
*/
