package com.example.parentwithsubscription.features;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.locationtracking.fragment.LocationTrackingFragment;
import com.example.parentwithsubscription.fragments.AccountFragment;
import com.example.parentwithsubscription.fragments.HomeFragment;
import com.example.parentwithsubscription.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set listener for the Bottom Navigation view
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.settings) {
                selectedFragment = new SettingsFragment();
            } else if (id == R.id.account) {
                selectedFragment = new AccountFragment();
            }

            // Replace the fragment in the container if selected
            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Load LocationTrackingFragment by default when the app starts
        if (savedInstanceState == null) {
            // Load the LocationTrackingFragment first when the app launches
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LocationTrackingFragment())
                    .commit();

            // Optionally, set a default selected item for the bottom navigation
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }
}
