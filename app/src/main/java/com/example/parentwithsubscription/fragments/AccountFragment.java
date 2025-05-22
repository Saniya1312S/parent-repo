package com.example.parentwithsubscription.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.authentication.activity.AddChildDetailsActivity;
import com.example.parentwithsubscription.authentication.activity.AddGuardianDetailsActivity;
import com.example.parentwithsubscription.authentication.activity.SubscriptionOptionsActivity;
import com.example.parentwithsubscription.common.GlobalData;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.locationtracking.fragment.LocationTrackingFragment;
import com.example.parentwithsubscription.utils.SubscriptionUtils;

import org.json.*;

import java.io.*;
import java.net.*;
import java.util.*;

import androidx.fragment.app.FragmentTransaction;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.security.GeneralSecurityException;

public class AccountFragment extends Fragment {

    private static final int REQUEST_ADD_CHILD = 1001;
    private static final int REQUEST_ADD_GUARDIAN = 1002;
    private static final int REQUEST_CHANGE_PLAN = 2001;

    private LinearLayout guardianList, kidList;
    private TextView guardianHeader, kidHeader;
    private LinearLayout subscriptionInfoLayout, accountLayout;
    private TextView planStatus;
    private Button subscribeBtn;

    private ArrayList<String> kidNames = new ArrayList<>();
    private ArrayList<String> guardianNames = new ArrayList<>();

    private int maxKids, maxParents;
    private String userRole;

    private String FAMILY_DETAILS = URIConstants.FAMILY_DETAILS;
    private String CHILD_MOBILE_NUMBER = URIConstants.CHILD_MOBILE_NUMBER;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        initViews(view);

        userRole = getUserRole();
        Log.d("AccountFragment", "User role: " + userRole);

        switch (userRole) {
            case "GUEST":
                showGuestView();
                break;
            case "MASTER":
                fetchFamilyDetailsFromApi();
                break;
            case "GUARDIAN":
                Log.d("AccountFragment", "Guardian view loaded");
                break;
        }

        return view;
    }

    private void initViews(View view) {
        guardianList = view.findViewById(R.id.guardian_list);
        kidList = view.findViewById(R.id.kid_list);
        guardianHeader = view.findViewById(R.id.guardians_header);
        kidHeader = view.findViewById(R.id.kids_header);
        subscriptionInfoLayout = view.findViewById(R.id.subscription_info_layout);
        accountLayout = view.findViewById(R.id.accountLayout);
        planStatus = view.findViewById(R.id.plan_status);
        subscribeBtn = view.findViewById(R.id.subscribe_button);

        maxKids = SubscriptionUtils.getMaxChildren(requireContext());
        maxParents = SubscriptionUtils.getMaxParents(requireContext());

        subscribeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SubscriptionOptionsActivity.class);

            String role = getUserRole();
            if ("MASTER".equals(role)) {
                intent.putExtra("flow_type", "change_plan");
                startActivityForResult(intent, 2001);
            } else {
                intent.putExtra("flow_type", "subscribe");
                startActivity(intent);
            }
        });
    }

    private void showGuestView() {
        accountLayout.setVisibility(View.VISIBLE);
        subscriptionInfoLayout.setVisibility(View.VISIBLE);

        planStatus.setText("You're currently using the app as a guest. Subscribe to create a family profile.");
        subscribeBtn.setText("Subscribe");

        guardianHeader.setVisibility(View.GONE);
        kidHeader.setVisibility(View.GONE);
        guardianList.setVisibility(View.GONE);
        kidList.setVisibility(View.GONE);
    }

    private void fetchFamilyDetailsFromApi() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String paidToken = prefs.getString("PAID_USER_TOKEN", null);

        if (paidToken == null) {
            Log.e("AccountFragment", "PAID_USER_TOKEN is null");
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(FAMILY_DETAILS);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + paidToken);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    JSONObject json = new JSONObject(result.toString());
                    JSONArray guardians = json.getJSONArray("guardians");
                    JSONArray children = json.getJSONArray("children");

                    guardianNames.clear();
                    kidNames.clear();

                    for (int i = 0; i < guardians.length(); i++) {
                        JSONObject g = guardians.getJSONObject(i);
                        guardianNames.add(g.getString("name"));
                    }

                    for (int i = 0; i < children.length(); i++) {
                        JSONObject c = children.getJSONObject(i);
                        kidNames.add(c.getString("name"));
                    }

                    requireActivity().runOnUiThread(this::renderFamilyProfiles);

                } else {
                    Log.e("AccountFragment", "API error: " + responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void renderFamilyProfiles() {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        guardianList.removeAllViews();
        kidList.removeAllViews();
        accountLayout.setVisibility(View.VISIBLE);

        if (!guardianNames.isEmpty()) {
            guardianHeader.setVisibility(View.VISIBLE);
            guardianList.setVisibility(View.VISIBLE);

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            guardianList.addView(layout);

            for (String name : guardianNames) {
                View profileView = inflater.inflate(R.layout.item_profile, guardianList, false);
                TextView tvName = profileView.findViewById(R.id.tvName);
                tvName.setText(name);
                layout.addView(profileView);
            }

            if (guardianNames.size() < maxParents) {
                layout.addView(createAddButton(false));
            }
        }

        kidHeader.setVisibility(View.VISIBLE);
        kidList.setVisibility(View.VISIBLE);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        kidList.addView(layout);

        for (String name : kidNames) {
            View profileView = inflater.inflate(R.layout.item_profile, kidList, false);
            TextView tvName = profileView.findViewById(R.id.tvName);
            tvName.setText(name);
            profileView.setOnClickListener(v -> fetchChildMobileNumber(name));
            layout.addView(profileView);
        }

        if (kidNames.size() < maxKids) {
            layout.addView(createAddButton(true));
        }

        subscriptionInfoLayout.setVisibility(View.VISIBLE);
        String planName = SubscriptionUtils.getCurrentPlanName(requireContext());
        String endDate = SubscriptionUtils.getCurrentPlanEndDate(requireContext());

        planStatus.setText("Current Plan: " + planName + "\nEnds on: " + endDate);
        subscribeBtn.setText("Change Plan");
    }

    private View createAddButton(boolean isKid) {
        View addButton = LayoutInflater.from(getContext()).inflate(R.layout.item_add_button, null, false);
        ImageView imgAdd = addButton.findViewById(R.id.imgAdd);

        imgAdd.setOnClickListener(v -> {
            Context context = requireContext();
            Intent intent = isKid ?
                    new Intent(context, AddChildDetailsActivity.class) :
                    new Intent(context, AddGuardianDetailsActivity.class);

            int requestCode = isKid ? REQUEST_ADD_CHILD : REQUEST_ADD_GUARDIAN;
            startActivityForResult(intent, requestCode);
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 0, 0, 0);
        addButton.setLayoutParams(params);

        return addButton;
    }

    private void fetchChildMobileNumber(String childName) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String paidToken = prefs.getString("PAID_USER_TOKEN", null);

        if (paidToken == null) {
            Log.e("AccountFragment", "PAID_USER_TOKEN is null");
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(CHILD_MOBILE_NUMBER + URLEncoder.encode(childName, "UTF-8"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + paidToken);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    String mobileNumber = result.toString().trim();
                    GlobalData.setDeviceId(mobileNumber);

                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Mobile Number: " + mobileNumber, Toast.LENGTH_SHORT).show();
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new LocationTrackingFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                    });
                } else {
                    Log.e("AccountFragment", "API error: " + responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String getUserRole() {
        try {
            MasterKey masterKey = new MasterKey.Builder(requireContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    requireContext(),
                    "guest_user_data",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            return sharedPreferences.getString("user_role", "GUEST");
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return "GUEST";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_ADD_CHILD || requestCode == REQUEST_ADD_GUARDIAN) {
                fetchFamilyDetailsFromApi();
            } else if (requestCode == REQUEST_CHANGE_PLAN && data != null && data.getBooleanExtra("payment_success", false)) {
                Toast.makeText(getContext(), "Plan updated successfully!", Toast.LENGTH_SHORT).show();
                fetchFamilyDetailsFromApi();
            }
        }
    }
}







/*
package com.example.parentwithsubscription.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.authentication.activity.AddChildDetailsActivity;
import com.example.parentwithsubscription.authentication.activity.AddGuardianDetailsActivity;
import com.example.parentwithsubscription.authentication.activity.SubscriptionOptionsActivity;
import com.example.parentwithsubscription.common.GlobalData;
import com.example.parentwithsubscription.features.locationtracking.fragment.LocationTrackingFragment;
import com.example.parentwithsubscription.utils.SubscriptionUtils;

import org.json.*;

import java.io.*;
import java.net.*;
import java.util.*;

import androidx.fragment.app.FragmentTransaction;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.security.GeneralSecurityException;

public class AccountFragment extends Fragment {

    private LinearLayout guardianList, kidList;
    private TextView guardianHeader, kidHeader;
    private LinearLayout subscriptionInfoLayout, accountLayout;
    private TextView planStatus;
    private Button subscribeBtn;

    private ArrayList<String> kidNames = new ArrayList<>();
    private ArrayList<String> guardianNames = new ArrayList<>();

    private int maxKids, maxParents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        initViews(view);

        String userRole = getUserRole();
        Log.d("AccountFragment", "User role: " + userRole);

        switch (userRole) {
            case "GUEST":
                showGuestView();
                break;

            case "MASTER":
                fetchFamilyDetailsFromApi();
                break;

            case "GUARDIAN":
                Log.d("AccountFragment", "Guardian view loaded");
                break;
        }

        return view;
    }

    private void initViews(View view) {
        guardianList = view.findViewById(R.id.guardian_list);
        kidList = view.findViewById(R.id.kid_list);
        guardianHeader = view.findViewById(R.id.guardians_header);
        kidHeader = view.findViewById(R.id.kids_header);
        subscriptionInfoLayout = view.findViewById(R.id.subscription_info_layout);
        accountLayout = view.findViewById(R.id.accountLayout);
        planStatus = view.findViewById(R.id.plan_status);
        subscribeBtn = view.findViewById(R.id.subscribe_button);

        maxKids = SubscriptionUtils.getMaxChildren(requireContext());
        maxParents = SubscriptionUtils.getMaxParents(requireContext());

*/
/*        subscribeBtn.setOnClickListener(v -> {
            SubscriptionOptionsFragment fragment = new SubscriptionOptionsFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });*//*

        subscribeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SubscriptionOptionsActivity.class);
            startActivity(intent);
        });

    }

    private void showGuestView() {
        accountLayout.setVisibility(View.VISIBLE);
        subscriptionInfoLayout.setVisibility(View.VISIBLE);

        planStatus.setText("You're currently using the app as a guest. Subscribe to create a family profile.");
        subscribeBtn.setText("Subscribe");

        guardianHeader.setVisibility(View.GONE);
        kidHeader.setVisibility(View.GONE);
        guardianList.setVisibility(View.GONE);
        kidList.setVisibility(View.GONE);
    }

    private void fetchFamilyDetailsFromApi() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String paidToken = prefs.getString("PAID_USER_TOKEN", null);

        if (paidToken == null) {
            Log.e("AccountFragment", "PAID_USER_TOKEN is null");
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.0.107:5000/user/family-details");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + paidToken);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    JSONObject json = new JSONObject(result.toString());
                    JSONArray guardians = json.getJSONArray("guardians");
                    JSONArray children = json.getJSONArray("children");

                    guardianNames.clear();
                    kidNames.clear();

                    for (int i = 0; i < guardians.length(); i++) {
                        JSONObject g = guardians.getJSONObject(i);
                        guardianNames.add(g.getString("name"));
                    }

                    for (int i = 0; i < children.length(); i++) {
                        JSONObject c = children.getJSONObject(i);
                        kidNames.add(c.getString("name"));
                    }

                    requireActivity().runOnUiThread(this::renderFamilyProfiles);

                } else {
                    Log.e("AccountFragment", "API error: " + responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void renderFamilyProfiles() {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        guardianList.removeAllViews();
        kidList.removeAllViews();
        accountLayout.setVisibility(View.VISIBLE);

        if (!guardianNames.isEmpty()) {
            guardianHeader.setVisibility(View.VISIBLE);
            guardianList.setVisibility(View.VISIBLE);

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            guardianList.addView(layout);

            for (String name : guardianNames) {
                View profileView = inflater.inflate(R.layout.item_profile, guardianList, false);
                TextView tvName = profileView.findViewById(R.id.tvName);
                tvName.setText(name);
                layout.addView(profileView);
            }

            if (guardianNames.size() < maxParents) {
                layout.addView(createAddButton(false));
            }
        }

        kidHeader.setVisibility(View.VISIBLE);
        kidList.setVisibility(View.VISIBLE);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        kidList.addView(layout);

        for (String name : kidNames) {
            View profileView = inflater.inflate(R.layout.item_profile, kidList, false);
            TextView tvName = profileView.findViewById(R.id.tvName);
            tvName.setText(name);
            profileView.setOnClickListener(v -> fetchChildMobileNumber(name));
            layout.addView(profileView);
        }

        // Always show add button if max not reached, even if no kids yet
        if (kidNames.size() < maxKids) {
            layout.addView(createAddButton(true));
        }

        subscriptionInfoLayout.setVisibility(View.VISIBLE);
        String planName = SubscriptionUtils.getCurrentPlanName(requireContext());
        String endDate = SubscriptionUtils.getCurrentPlanEndDate(requireContext());

        planStatus.setText("Current Plan: " + planName + "\nEnds on: " + endDate);
        subscribeBtn.setText("Change Plan");
    }

    private void fetchChildMobileNumber(String childName) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String paidToken = prefs.getString("PAID_USER_TOKEN", null);

        if (paidToken == null) {
            Log.e("AccountFragment", "PAID_USER_TOKEN is null");
            return;
        }

        new Thread(() -> {
            try {
                // Make the GET request to fetch child's mobile number
                URL url = new URL("http://192.168.0.107:5000/user/child-mobile?member_name=" + URLEncoder.encode(childName, "UTF-8"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + paidToken);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Assuming the response is just the mobile number as plain text
                    String mobileNumber = result.toString().trim();
                    GlobalData.setDeviceId(mobileNumber);  // Save to GlobalData

                    // Show confirmation (Toast or Dialog)
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Mobile Number: " + mobileNumber, Toast.LENGTH_SHORT).show();

                        // Navigate to LocationTrackingFragment
                        LocationTrackingFragment locationTrackingFragment = new LocationTrackingFragment();
                        // Replace fragment
                        FragmentTransaction transaction = requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction();
                        transaction.replace(R.id.fragment_container, locationTrackingFragment);  // Use your actual container ID
                        transaction.addToBackStack(null);
                        transaction.commit();
                    });

                } else {
                    Log.e("AccountFragment", "API error: " + responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    */
/*private View createAddButton(boolean isKid) {
        View addButton = LayoutInflater.from(getContext()).inflate(R.layout.item_add_button, null, false);
        ImageView imgAdd = addButton.findViewById(R.id.imgAdd);

        imgAdd.setOnClickListener(v -> {
            Fragment fragment = isKid ? new AddChildDetailsFragment() : new AddGuardianDetailsFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 0, 0, 0);
        addButton.setLayoutParams(params);

        return addButton;
    }*//*

    private View createAddButton(boolean isKid) {
        View addButton = LayoutInflater.from(getContext()).inflate(R.layout.item_add_button, null, false);
        ImageView imgAdd = addButton.findViewById(R.id.imgAdd);

        imgAdd.setOnClickListener(v -> {
            Context context = requireContext();
            Intent intent;

            if (isKid) {
                intent = new Intent(context, AddChildDetailsActivity.class);
            } else {
                intent = new Intent(context, AddGuardianDetailsActivity.class);
            }

            context.startActivity(intent);
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 0, 0, 0);
        addButton.setLayoutParams(params);

        return addButton;
    }


    private String getUserRole() {
        try {
            MasterKey masterKey = new MasterKey.Builder(requireContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    requireContext(),
                    "guest_user_data",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            return sharedPreferences.getString("user_role", "GUEST");

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return "GUEST";
        }
    }
}
*/











/*package com.example.parentwithsubscription.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.utils.SubscriptionUtils;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.security.GeneralSecurityException;


public class AccountFragment extends Fragment {

    private LinearLayout guardianList, kidList;
    private TextView guardianHeader, kidHeader;
    private LinearLayout subscriptionInfoLayout;
    private TextView planStatus;
    private Button subscribeBtn;

    private ArrayList<String> kidNames;
    private ArrayList<String> guardianNames;
    private ArrayList<String> guardianRoles;

    private boolean isMasterAdded = false;

    private int maxKids = 0;
    private int maxParents = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        guardianList = view.findViewById(R.id.guardian_list);
        kidList = view.findViewById(R.id.kid_list);
        guardianHeader = view.findViewById(R.id.guardians_header);
        kidHeader = view.findViewById(R.id.kids_header);
        subscriptionInfoLayout = view.findViewById(R.id.subscription_info_layout);
        planStatus = view.findViewById(R.id.plan_status);
        subscribeBtn = view.findViewById(R.id.subscribe_button);

        maxKids = SubscriptionUtils.getMaxChildren(requireContext());
        maxParents = SubscriptionUtils.getMaxParents(requireContext());

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("kidNames")) {
            kidNames = bundle.getStringArrayList("kidNames");
            guardianNames = bundle.getStringArrayList("guardianNames");
            guardianRoles = bundle.getStringArrayList("guardianRoles");
            saveListToPrefs("kidNames", kidNames);
            saveListToPrefs("guardianNames", guardianNames);
            saveListToPrefs("guardianRoles", guardianRoles);
        } else {
            kidNames = loadListFromPrefs("kidNames");
            guardianNames = loadListFromPrefs("guardianNames");
            guardianRoles = loadListFromPrefs("guardianRoles");
        }

        renderFamilyProfiles();

        getParentFragmentManager().setFragmentResultListener("childData", this, (requestKey, result) -> {
            String newKidName = result.getString("name");
            if (newKidName != null) {
                if (kidNames == null) {
                    kidNames = new ArrayList<>();
                }
                kidNames.add(newKidName);
                saveListToPrefs("kidNames", kidNames);
                renderFamilyProfiles();
            }
        });

        getParentFragmentManager().setFragmentResultListener("guardianData", this, (requestKey, result) -> {
            String newGuardianName = result.getString("name");
            String newGuardianRole = result.getString("role");
            if (newGuardianName != null && newGuardianRole != null) {
                if (guardianNames == null) guardianNames = new ArrayList<>();
                if (guardianRoles == null) guardianRoles = new ArrayList<>();
                guardianNames.add(newGuardianName);
                guardianRoles.add(newGuardianRole);
                saveListToPrefs("guardianNames", guardianNames);
                saveListToPrefs("guardianRoles", guardianRoles);
                renderFamilyProfiles();
            }
        });

        subscribeBtn.setOnClickListener(v -> {
            SubscriptionOptionsFragment fragment = new SubscriptionOptionsFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void renderFamilyProfiles() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        boolean hasProfiles = false;

        guardianList.removeAllViews();
        kidList.removeAllViews();

        isMasterAdded = false;

        // ==== Guardians ====
        if (guardianNames != null && guardianRoles != null && !guardianNames.isEmpty()) {
            guardianHeader.setVisibility(View.VISIBLE);
            guardianList.setVisibility(View.VISIBLE);

            LinearLayout guardianLayout = new LinearLayout(getContext());
            guardianLayout.setOrientation(LinearLayout.HORIZONTAL);
            guardianList.addView(guardianLayout);

            for (int i = 0; i < guardianNames.size(); i++) {
                View profileView = inflater.inflate(R.layout.item_profile, guardianList, false);
                TextView tvName = profileView.findViewById(R.id.tvName);
                tvName.setText(guardianNames.get(i) + "\n(" + guardianRoles.get(i) + ")");
                guardianLayout.addView(profileView);
            }

            if (guardianNames.size() < maxParents) {
                guardianLayout.addView(createAddButton(false));
            }

            isMasterAdded = true;
            updateUserRoleToMaster();
            hasProfiles = true;
        }

        // ==== Kids ====
        if (isMasterAdded) {
            kidHeader.setVisibility(View.VISIBLE);
            kidList.setVisibility(View.VISIBLE);

            LinearLayout kidLayout = new LinearLayout(getContext());
            kidLayout.setOrientation(LinearLayout.HORIZONTAL);
            kidList.addView(kidLayout);

            if (kidNames != null && !kidNames.isEmpty()) {
                for (String name : kidNames) {
                    View profileView = inflater.inflate(R.layout.item_profile, kidList, false);
                    TextView tvName = profileView.findViewById(R.id.tvName);
                    tvName.setText(name);
                    kidLayout.addView(profileView);
                }
            }

            if (kidNames.size() < maxKids) {
                kidLayout.addView(createAddButton(true));
            }

            hasProfiles = true;
        }

        // ==== Subscription Info ====
        subscriptionInfoLayout.setVisibility(View.VISIBLE);
        String planName = SubscriptionUtils.getCurrentPlanName(requireContext());
        String endDate = SubscriptionUtils.getCurrentPlanEndDate(requireContext());

        if (hasProfiles) {
            planStatus.setText("Current Plan: " + planName + "\nEnds on: " + endDate);
            subscribeBtn.setText("Change Plan");
        } else {
            planStatus.setText("You're currently using the free trial, it ends on " + endDate + ".");
            subscribeBtn.setText("Subscribe");
        }

        planStatus.setVisibility(View.VISIBLE);
        subscribeBtn.setVisibility(View.VISIBLE);
    }

    private View createAddButton(boolean isKid) {
        View addButton = LayoutInflater.from(getContext()).inflate(R.layout.item_add_button, null, false);
        ImageView imgAdd = addButton.findViewById(R.id.imgAdd);

        imgAdd.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            if (isKid) {
                transaction.replace(R.id.fragment_container, new AddChildDetailsFragment());
            } else {
                transaction.replace(R.id.fragment_container, new AddGuardianDetailsFragment());
            }
            transaction.addToBackStack(null);
            transaction.commit();
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 0, 0, 0);
        addButton.setLayoutParams(params);

        return addButton;
    }

    private void saveListToPrefs(String key, ArrayList<String> list) {
        if (getContext() == null) return;
        getContext().getSharedPreferences("FamilyPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString(key, TextUtils.join(",", list))
                .apply();
    }

    private ArrayList<String> loadListFromPrefs(String key) {
        if (getContext() == null) return new ArrayList<>();
        String saved = getContext().getSharedPreferences("FamilyPrefs", Context.MODE_PRIVATE)
                .getString(key, "");
        ArrayList<String> list = new ArrayList<>();
        if (!saved.isEmpty()) {
            list.addAll(Arrays.asList(saved.split(",")));
        }
        return list;
    }

    private void updateUserRoleToMaster() {
        try {
            MasterKey masterKey = new MasterKey.Builder(requireContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    requireContext(),
                    "guest_user_data",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            sharedPreferences.edit()
                    .putString("user_role", "MASTER")
                    .apply();

        } catch (GeneralSecurityException | java.io.IOException e) {
            e.printStackTrace();
        }
    }

}*/










/*
package com.example.parentwithsubscription.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.utils.SubscriptionUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class AccountFragment extends Fragment {

    private LinearLayout guardianList, kidList;
    private TextView guardianHeader, kidHeader;
    private LinearLayout subscriptionInfoLayout;

    private ArrayList<String> kidNames;
    private ArrayList<String> guardianNames;
    private ArrayList<String> guardianRoles;

    private boolean isMasterAdded = false;

    private int maxKids = 0;
    private int maxParents = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        guardianList = view.findViewById(R.id.guardian_list);
        kidList = view.findViewById(R.id.kid_list);
        guardianHeader = view.findViewById(R.id.guardians_header);
        kidHeader = view.findViewById(R.id.kids_header);
        subscriptionInfoLayout = view.findViewById(R.id.subscription_info_layout);

        maxKids = SubscriptionUtils.getMaxChildren(requireContext());
        maxParents = SubscriptionUtils.getMaxParents(requireContext());

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("kidNames")) {
            kidNames = bundle.getStringArrayList("kidNames");
            guardianNames = bundle.getStringArrayList("guardianNames");
            guardianRoles = bundle.getStringArrayList("guardianRoles");
            saveListToPrefs("kidNames", kidNames);
            saveListToPrefs("guardianNames", guardianNames);
            saveListToPrefs("guardianRoles", guardianRoles);
        } else {
            kidNames = loadListFromPrefs("kidNames");
            guardianNames = loadListFromPrefs("guardianNames");
            guardianRoles = loadListFromPrefs("guardianRoles");
        }

        renderFamilyProfiles();

        getParentFragmentManager().setFragmentResultListener("childData", this, (requestKey, result) -> {
            String newKidName = result.getString("name");
            if (newKidName != null) {
                if (kidNames == null) {
                    kidNames = new ArrayList<>();
                }
                kidNames.add(newKidName);
                saveListToPrefs("kidNames", kidNames);
                renderFamilyProfiles();
            }
        });

        getParentFragmentManager().setFragmentResultListener("guardianData", this, (requestKey, result) -> {
            String newGuardianName = result.getString("name");
            String newGuardianRole = result.getString("role");
            if (newGuardianName != null && newGuardianRole != null) {
                if (guardianNames == null) guardianNames = new ArrayList<>();
                if (guardianRoles == null) guardianRoles = new ArrayList<>();
                guardianNames.add(newGuardianName);
                guardianRoles.add(newGuardianRole);
                saveListToPrefs("guardianNames", guardianNames);
                saveListToPrefs("guardianRoles", guardianRoles);
                renderFamilyProfiles();
            }
        });

        Button subscribeButton = view.findViewById(R.id.subscribe_button);
        subscribeButton.setOnClickListener(v -> {
            SubscriptionOptionsFragment fragment = new SubscriptionOptionsFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void renderFamilyProfiles() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        boolean hasProfiles = false;

        guardianList.removeAllViews();
        kidList.removeAllViews();

        isMasterAdded = false;

        // ==== Guardians ====
        if (guardianNames != null && guardianRoles != null && !guardianNames.isEmpty()) {
            guardianHeader.setVisibility(View.VISIBLE);
            guardianList.setVisibility(View.VISIBLE);

            LinearLayout guardianLayout = new LinearLayout(getContext());
            guardianLayout.setOrientation(LinearLayout.HORIZONTAL);
            guardianList.addView(guardianLayout);

            for (int i = 0; i < guardianNames.size(); i++) {
                View profileView = inflater.inflate(R.layout.item_profile, guardianList, false);
                TextView tvName = profileView.findViewById(R.id.tvName);
                tvName.setText(guardianNames.get(i) + "\n(" + guardianRoles.get(i) + ")");
                guardianLayout.addView(profileView);
            }

            if (guardianNames.size() < maxParents) {
                guardianLayout.addView(createAddButton(false));
            }

            isMasterAdded = true;
            hasProfiles = true;
        }

        // ==== Kids ====
        if (isMasterAdded) {
            kidHeader.setVisibility(View.VISIBLE);
            kidList.setVisibility(View.VISIBLE);

            LinearLayout kidLayout = new LinearLayout(getContext());
            kidLayout.setOrientation(LinearLayout.HORIZONTAL);
            kidList.addView(kidLayout);

            if (kidNames != null && !kidNames.isEmpty()) {
                for (String name : kidNames) {
                    View profileView = inflater.inflate(R.layout.item_profile, kidList, false);
                    TextView tvName = profileView.findViewById(R.id.tvName);
                    tvName.setText(name);
                    kidLayout.addView(profileView);
                }
            }

            if (kidNames.size() < maxKids) {
                kidLayout.addView(createAddButton(true));
            }

            hasProfiles = true;
        }

        subscriptionInfoLayout.setVisibility(hasProfiles ? View.GONE : View.VISIBLE);
    }

    private View createAddButton(boolean isKid) {
        View addButton = LayoutInflater.from(getContext()).inflate(R.layout.item_add_button, null, false);
        ImageView imgAdd = addButton.findViewById(R.id.imgAdd);

        imgAdd.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            if (isKid) {
                transaction.replace(R.id.fragment_container, new AddChildDetailsFragment());
            } else {
                transaction.replace(R.id.fragment_container, new AddGuardianDetailsFragment());
            }
            transaction.addToBackStack(null);
            transaction.commit();
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 0, 0, 0);
        addButton.setLayoutParams(params);

        return addButton;
    }

    private void saveListToPrefs(String key, ArrayList<String> list) {
        if (getContext() == null) return;
        getContext().getSharedPreferences("FamilyPrefs", Context.MODE_PRIVATE)
                .edit()
                .putString(key, TextUtils.join(",", list))
                .apply();
    }

    private ArrayList<String> loadListFromPrefs(String key) {
        if (getContext() == null) return new ArrayList<>();
        String saved = getContext().getSharedPreferences("FamilyPrefs", Context.MODE_PRIVATE)
                .getString(key, "");
        ArrayList<String> list = new ArrayList<>();
        if (!saved.isEmpty()) {
            list.addAll(Arrays.asList(saved.split(",")));
        }
        return list;
    }
}
*/













/*
package com.example.parentwithsubscription.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.utils.SubscriptionUtils;

import java.util.ArrayList;

public class AccountFragment extends Fragment {

    private LinearLayout guardianList, kidList;
    private TextView guardianHeader, kidHeader;
    private LinearLayout subscriptionInfoLayout;

    private ArrayList<String> kidNames;
    private ArrayList<String> guardianNames;
    private ArrayList<String> guardianRoles;

    private boolean isMasterAdded = false;

    private int maxKids = 0;
    private int maxParents = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        guardianList = view.findViewById(R.id.guardian_list);
        kidList = view.findViewById(R.id.kid_list);
        guardianHeader = view.findViewById(R.id.guardians_header);
        kidHeader = view.findViewById(R.id.kids_header);
        subscriptionInfoLayout = view.findViewById(R.id.subscription_info_layout);

        // Load the subscription limits (max kids and max parents) from SharedPreferences
        maxKids = SubscriptionUtils.getMaxChildren(requireContext());
        maxParents = SubscriptionUtils.getMaxParents(requireContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            kidNames = bundle.getStringArrayList("kidNames");
            guardianNames = bundle.getStringArrayList("guardianNames");
            guardianRoles = bundle.getStringArrayList("guardianRoles");
        }

        renderFamilyProfiles();

        getParentFragmentManager().setFragmentResultListener("childData", this, (requestKey, result) -> {
            String newKidName = result.getString("name");
            if (newKidName != null) {
                if (kidNames == null) {
                    kidNames = new ArrayList<>();
                }
                kidNames.add(newKidName);
                renderFamilyProfiles();
            }
        });

        getParentFragmentManager().setFragmentResultListener("guardianData", this, (requestKey, result) -> {
            String newGuardianName = result.getString("name");
            String newGuardianRole = result.getString("role");
            if (newGuardianName != null && newGuardianRole != null) {
                if (guardianNames == null) {
                    guardianNames = new ArrayList<>();
                }
                if (guardianRoles == null) {
                    guardianRoles = new ArrayList<>();
                }
                guardianNames.add(newGuardianName);
                guardianRoles.add(newGuardianRole);
                renderFamilyProfiles();
            }
        });

        Button subscribeButton = view.findViewById(R.id.subscribe_button);
        subscribeButton.setOnClickListener(v -> {
            SubscriptionOptionsFragment fragment = new SubscriptionOptionsFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void renderFamilyProfiles() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        boolean hasProfiles = false;

        guardianList.removeAllViews();
        kidList.removeAllViews();

        isMasterAdded = false;

        // ==== Guardians ====
        if (guardianNames != null && guardianRoles != null && !guardianNames.isEmpty()) {
            guardianHeader.setVisibility(View.VISIBLE);
            guardianList.setVisibility(View.VISIBLE);

            LinearLayout guardianLayout = new LinearLayout(getContext());
            guardianLayout.setOrientation(LinearLayout.HORIZONTAL);
            guardianList.addView(guardianLayout);

            for (int i = 0; i < guardianNames.size(); i++) {
                View profileView = inflater.inflate(R.layout.item_profile, guardianList, false);
                TextView tvName = profileView.findViewById(R.id.tvName);
                tvName.setText(guardianNames.get(i) + "\n(" + guardianRoles.get(i) + ")");
                guardianLayout.addView(profileView);
            }

            // Show "+" button after all guardian profiles
            if (guardianNames.size() < maxParents) {
                guardianLayout.addView(createAddButton(false)); // Show add button if maxParents is not reached
            }

            isMasterAdded = true;
            hasProfiles = true;
        }

        // ==== Kids ====
        if (isMasterAdded) {
            kidHeader.setVisibility(View.VISIBLE);
            kidList.setVisibility(View.VISIBLE);

            LinearLayout kidLayout = new LinearLayout(getContext());
            kidLayout.setOrientation(LinearLayout.HORIZONTAL);
            kidList.addView(kidLayout);

            if (kidNames != null && !kidNames.isEmpty()) {
                for (String name : kidNames) {
                    View profileView = inflater.inflate(R.layout.item_profile, kidList, false);
                    TextView tvName = profileView.findViewById(R.id.tvName);
                    tvName.setText(name);
                    kidLayout.addView(profileView);
                }
            }

            // Show "+" button after kid profiles (if any or none)
            if (kidNames.size() < maxKids) {
                kidLayout.addView(createAddButton(true)); // Show add button if maxKids is not reached
            }

            hasProfiles = true;
        }

        subscriptionInfoLayout.setVisibility(hasProfiles ? View.GONE : View.VISIBLE);
    }

    private View createAddButton(boolean isKid) {
        View addButton = LayoutInflater.from(getContext()).inflate(R.layout.item_add_button, null, false);
        ImageView imgAdd = addButton.findViewById(R.id.imgAdd);

        imgAdd.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            if (isKid) {
                transaction.replace(R.id.fragment_container, new AddChildDetailsFragment());
            } else {
                transaction.replace(R.id.fragment_container, new AddGuardianDetailsFragment());
            }
            transaction.addToBackStack(null);
            transaction.commit();
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 0, 0, 0);
        addButton.setLayoutParams(params);

        return addButton;
    }
}
*/
















/*
package com.example.parentwithsubscription.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.parentwithsubscription.R;

public class AccountFragment extends Fragment {

    private LinearLayout accountLayout;
    private LinearLayout subscriptionInfoLayout;
    private Button subscribeButton;
    private TextView planStatusTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize Views
        accountLayout = view.findViewById(R.id.accountLayout);
        subscriptionInfoLayout = view.findViewById(R.id.subscription_info_layout);
        subscribeButton = view.findViewById(R.id.subscribe_button);
        planStatusTextView = view.findViewById(R.id.plan_status);

        // Set Free Trial Info (for now, this can be dynamic based on your data)
        planStatusTextView.setText("You're currently using the free trial, it ends on June 1, 2025.");

        // Handle Subscribe Button Click
        subscribeButton.setOnClickListener(v -> {
            // Navigate to SubscriptionOptionsFragment when clicked
            SubscriptionOptionsFragment fragment = new SubscriptionOptionsFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)  // Replace with your container ID
                    .addToBackStack(null)  // Optionally add to back stack
                    .commit();
        });

        return view;
    }
}
*/
