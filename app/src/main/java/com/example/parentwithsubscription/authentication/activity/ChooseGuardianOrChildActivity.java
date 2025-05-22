package com.example.parentwithsubscription.authentication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.locationtracking.activity.LocationTrackingActivity;
import com.google.android.material.imageview.ShapeableImageView;

public class ChooseGuardianOrChildActivity extends AppCompatActivity {

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_child_or_guardian);

        // Initialize ShapeableImageViews (replacing buttons)
        ShapeableImageView imgGuardian = findViewById(R.id.imgGuardian);
        ShapeableImageView imgChild = findViewById(R.id.imgChild);
        backButton = findViewById(R.id.backButton);

        // Initialize Skip Button
        Button btnLogin = findViewById(R.id.btnLogin);

        // Set OnClickListener for Guardian image
        imgGuardian.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseGuardianOrChildActivity.this, AddGuardianDetailsActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener for Child image
        imgChild.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseGuardianOrChildActivity.this, AddChildActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener for Skip button
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseGuardianOrChildActivity.this, LocationTrackingActivity.class);
            startActivity(intent);
        });

        // Set a click listener to handle the back action
        backButton.setOnClickListener(v -> {
        finish();
        });
    }
}
