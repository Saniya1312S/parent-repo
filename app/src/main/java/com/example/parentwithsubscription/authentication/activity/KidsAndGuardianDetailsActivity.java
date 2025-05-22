package com.example.parentwithsubscription.authentication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.parentwithsubscription.MainActivity;
import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.features.HomeActivity;
import com.example.parentwithsubscription.utils.SubscriptionUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class KidsAndGuardianDetailsActivity extends AppCompatActivity {

    private LinearLayout kidsContainer, guardianContainer;
    private Button btnComplete;

    private int maxKids = 0;
    private int maxParents = 0;

    private final List<String> kidNames = new ArrayList<>();
    private final List<Guardian> guardians = new ArrayList<>();

    private static final int REQUEST_ADD_CHILD = 1001;
    private static final int REQUEST_ADD_GUARDIAN = 1002;

    private static class Guardian {
        String name;
        String role;

        Guardian(String name, String role) {
            this.name = name;
            this.role = role;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_kids_and_guardian_details);

        kidsContainer = findViewById(R.id.kidsContainer);
        guardianContainer = findViewById(R.id.guardianContainer);
        btnComplete = findViewById(R.id.btnComplete);

        maxKids = SubscriptionUtils.getMaxChildren(this);
        maxParents = SubscriptionUtils.getMaxParents(this);

        // ✅ Add master guardian if passed
        String name = getIntent().getStringExtra("name");
        String role = getIntent().getStringExtra("role");
        if (name != null && role != null) {
            guardians.add(new Guardian(name, role));
        }

        renderKidProfiles();
        renderGuardianProfiles();

/*        btnComplete.setOnClickListener(v -> {
            Intent intent = new Intent(KidsAndGuardianDetailsActivity.this, MainActivity.class);
            startActivity(intent);
        });*/

        btnComplete.setOnClickListener(v -> {
            try {
                SharedPreferences prefs = getEncryptedPrefs();
                String userRole = prefs.getString("user_role", null);

                if ("GUEST".equalsIgnoreCase(userRole)) {
                    // Update role to MASTER
                    prefs.edit().putString("user_role", "MASTER").apply();
                    userRole = "MASTER";
                }

                if ("MASTER".equalsIgnoreCase(userRole)) {
                    startActivity(new Intent(KidsAndGuardianDetailsActivity.this, HomeActivity.class));
                } else {
                    startActivity(new Intent(KidsAndGuardianDetailsActivity.this, MainActivity.class));
                }

                finish(); // Optional: close this activity
            } catch (GeneralSecurityException | java.io.IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to read or update user role", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private SharedPreferences getEncryptedPrefs() throws GeneralSecurityException, IOException {
        String userPrefsFile = "guest_user_data";
        MasterKey masterKey = new MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                this,
                userPrefsFile,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    private void renderKidProfiles() {
        kidsContainer.removeAllViews();
        for (String name : kidNames) {
            View profileView = getLayoutInflater().inflate(R.layout.item_profile, kidsContainer, false);
            TextView tvName = profileView.findViewById(R.id.tvName);
            tvName.setText(name);
            kidsContainer.addView(profileView);
        }

        if (kidNames.size() < maxKids) {
            addAddButton(kidsContainer, true);
        }
    }

    private void renderGuardianProfiles() {
        guardianContainer.removeAllViews();
        for (Guardian guardian : guardians) {
            View profileView = getLayoutInflater().inflate(R.layout.item_profile, guardianContainer, false);
            TextView tvName = profileView.findViewById(R.id.tvName);
            tvName.setText(guardian.name + "\n(" + guardian.role + ")");
            guardianContainer.addView(profileView);
        }

        if (guardians.size() < maxParents) {
            addAddButton(guardianContainer, false);
        }
    }

    private void addAddButton(LinearLayout container, boolean isKid) {
        View addButton = getLayoutInflater().inflate(R.layout.item_add_button, container, false);
        ImageView imgAdd = addButton.findViewById(R.id.imgAdd);
        imgAdd.setOnClickListener(v -> {
            if (isKid) {
                startActivityForResult(new Intent(this, AddChildDetailsActivity.class), REQUEST_ADD_CHILD);
            } else {
                Class<?> targetClass = guardians.isEmpty()
                        ? AddMasterDetailsActivity.class
                        : AddGuardianDetailsActivity.class;
                startActivityForResult(new Intent(this, targetClass), REQUEST_ADD_GUARDIAN);
            }
        });
        container.addView(addButton);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == REQUEST_ADD_CHILD) {
            String name = data.getStringExtra("name");
            if (name != null) {
                kidNames.add(name);
                renderKidProfiles();
            }
        } else if (requestCode == REQUEST_ADD_GUARDIAN) {
            String name = data.getStringExtra("name");
            String role = data.getStringExtra("role");
            if (name != null && role != null) {
                guardians.add(new Guardian(name, role));
                renderGuardianProfiles();
            }
        }
    }
}










/*
package com.example.parentwithsubscription.authentication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parentwithsubscription.MainActivity;
import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.fragments.AccountFragment;
import com.example.parentwithsubscription.utils.SubscriptionUtils;

import java.util.ArrayList;
import java.util.List;

public class KidsAndGuardianDetailsActivity extends AppCompatActivity {

    private LinearLayout kidsContainer, guardianContainer;
    private Button btnComplete;

    private int maxKids = 0;
    private int maxParents = 0;

    private final List<String> kidNames = new ArrayList<>();
    private final List<Guardian> guardians = new ArrayList<>();

    private static final int REQUEST_ADD_CHILD = 1001;
    private static final int REQUEST_ADD_GUARDIAN = 1002;

    private static class Guardian {
        String name;
        String role;

        Guardian(String name, String role) {
            this.name = name;
            this.role = role;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_kids_and_guardian_details);

        kidsContainer = findViewById(R.id.kidsContainer);
        guardianContainer = findViewById(R.id.guardianContainer);
        btnComplete = findViewById(R.id.btnComplete);

        maxKids = SubscriptionUtils.getMaxChildren(this);
        maxParents = SubscriptionUtils.getMaxParents(this);

        renderKidProfiles();
        renderGuardianProfiles();

        // Set an onClickListener to handle button click
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open LoginActivity
                Intent intent = new Intent(KidsAndGuardianDetailsActivity.this, MainActivity.class);
                startActivity(intent); // Start the login activity
            }
        });
    }

    private void renderKidProfiles() {
        kidsContainer.removeAllViews();
        for (String name : kidNames) {
            View profileView = getLayoutInflater().inflate(R.layout.item_profile, kidsContainer, false);
            TextView tvName = profileView.findViewById(R.id.tvName);
            tvName.setText(name);
            kidsContainer.addView(profileView);
        }

        if (kidNames.size() < maxKids) {
            addAddButton(kidsContainer, true);
        }
    }

    private void renderGuardianProfiles() {
        guardianContainer.removeAllViews();
        for (Guardian guardian : guardians) {
            View profileView = getLayoutInflater().inflate(R.layout.item_profile, guardianContainer, false);
            TextView tvName = profileView.findViewById(R.id.tvName);
            tvName.setText(guardian.name + "\n(" + guardian.role + ")");
            guardianContainer.addView(profileView);
        }

        if (guardians.size() < maxParents) {
            addAddButton(guardianContainer, false);
        }
    }

    private void addAddButton(LinearLayout container, boolean isKid) {
        View addButton = getLayoutInflater().inflate(R.layout.item_add_button, container, false);
        ImageView imgAdd = addButton.findViewById(R.id.imgAdd);
        imgAdd.setOnClickListener(v -> {
            if (isKid) {
                startActivityForResult(new Intent(this, AddChildDetailsActivity.class), REQUEST_ADD_CHILD);
            } else {
                Class<?> targetClass = guardians.isEmpty()
                        ? AddMasterDetailsActivity.class
                        : AddGuardianDetailsActivity.class;
                startActivityForResult(new Intent(this, targetClass), REQUEST_ADD_GUARDIAN);
            }
        });
        container.addView(addButton);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == REQUEST_ADD_CHILD) {
            String name = data.getStringExtra("name");
            if (name != null) {
                kidNames.add(name);
                renderKidProfiles();
            }
        } else if (requestCode == REQUEST_ADD_GUARDIAN) {
            String name = data.getStringExtra("name");
            String role = data.getStringExtra("role");
            if (name != null && role != null) {
                guardians.add(new Guardian(name, role));
                renderGuardianProfiles();
            }
        }
    }

    private void openAccountSummary() {
        Intent intent = new Intent(this, AccountFragment.class);
        intent.putStringArrayListExtra("kidNames", new ArrayList<>(kidNames));

        ArrayList<String> guardianNames = new ArrayList<>();
        ArrayList<String> guardianRoles = new ArrayList<>();
        for (Guardian g : guardians) {
            guardianNames.add(g.name);
            guardianRoles.add(g.role);
        }

        intent.putStringArrayListExtra("guardianNames", guardianNames);
        intent.putStringArrayListExtra("guardianRoles", guardianRoles);
        startActivity(intent);
    }
}
*/
