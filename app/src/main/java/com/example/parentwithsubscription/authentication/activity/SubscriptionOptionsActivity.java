package com.example.parentwithsubscription.authentication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.model.Plan;
import com.example.parentwithsubscription.utils.SubscriptionUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class SubscriptionOptionsActivity extends AppCompatActivity {

    private Button tabBasic, tabStandard, tabPremium, btnSubscribe;
    private LinearLayout featuresContainer;
    private RadioGroup planRadioGroup;
    private TextView planTitle, planDescription;

    private String flowType = "subscribe";
    private Map<String, Plan> plansMap = new HashMap<>();
    private Plan currentPlan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_subscription_options);

        tabBasic = findViewById(R.id.tab_basic);
        tabStandard = findViewById(R.id.tab_standard);
        tabPremium = findViewById(R.id.tab_premium);
        btnSubscribe = findViewById(R.id.btn_subscribe);

        planTitle = findViewById(R.id.plan_title);
        planDescription = findViewById(R.id.plan_description);
        featuresContainer = findViewById(R.id.features_container);
        planRadioGroup = findViewById(R.id.plan_radio_group);

        flowType = getIntent().getStringExtra("flow_type");
        if (flowType == null) flowType = "subscribe";

        tabBasic.setOnClickListener(v -> selectTab("Basic"));
        tabStandard.setOnClickListener(v -> selectTab("Standard"));
        tabPremium.setOnClickListener(v -> selectTab("Premium"));

        btnSubscribe.setOnClickListener(v -> handleSubscription());

        fetchPlansFromServer();
    }

    private void selectTab(String planType) {
        resetTabStyles();
        if (planType.equals("Basic")) styleSelectedTab(tabBasic);
        else if (planType.equals("Standard")) styleSelectedTab(tabStandard);
        else styleSelectedTab(tabPremium);

        currentPlan = plansMap.get(planType);
        if (currentPlan != null) displayPlan(currentPlan);
    }

    private void resetTabStyles() {
        int primaryColor = ContextCompat.getColor(this, R.color.colorPrimary);
        tabBasic.setBackgroundColor(Color.WHITE);
        tabStandard.setBackgroundColor(Color.WHITE);
        tabPremium.setBackgroundColor(Color.WHITE);
        tabBasic.setTextColor(primaryColor);
        tabStandard.setTextColor(primaryColor);
        tabPremium.setTextColor(primaryColor);
    }

    private void styleSelectedTab(Button tab) {
        int highlightColor = ContextCompat.getColor(this, R.color.colorSecondaryLight);
        tab.setBackgroundColor(highlightColor);
        tab.setTextColor(Color.BLACK);
    }

    private void displayPlan(Plan plan) {
        planTitle.setText(plan.getTitle());
        planDescription.setText(plan.getDescription());
        featuresContainer.removeAllViews();
        planRadioGroup.removeAllViews();

        for (Map.Entry<String, Boolean> feature : plan.getFeatures().entrySet()) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);

            TextView featureText = new TextView(this);
            featureText.setText(feature.getKey());
            featureText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            ImageView icon = new ImageView(this);
            icon.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
            icon.setImageResource(feature.getValue() ? R.drawable.ic_check : R.drawable.ic_cross);

            row.addView(featureText);
            row.addView(icon);
            featuresContainer.addView(row);
        }

        for (Plan.PlanDetail detail : plan.getPlans()) {
            RadioButton rb = new RadioButton(this);
            rb.setText("₹" + detail.getCharges() + " / " + detail.getDuration() + " days");
            rb.setTag(detail);
            planRadioGroup.addView(rb);
        }
    }

    private void fetchPlansFromServer() {
        new Thread(() -> {
            Map<String, Plan> resultMap = new HashMap<>();
            try {
                URL url = new URL(URIConstants.PLAN_DETAILS);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject root = new JSONObject(result.toString());

                Iterator<String> keys = root.keys();
                while (keys.hasNext()) {
                    String planName = keys.next();
                    JSONObject planJson = root.getJSONArray(planName).getJSONObject(0);

                    String description = planJson.getString("description");

                    JSONObject featuresJson = planJson.getJSONObject("features");
                    Map<String, Boolean> features = new HashMap<>();
                    Iterator<String> featureKeys = featuresJson.keys();
                    while (featureKeys.hasNext()) {
                        String key = featureKeys.next();
                        features.put(key, featuresJson.getBoolean(key));
                    }

                    JSONArray plansArray = planJson.getJSONArray("plans");
                    List<Plan.PlanDetail> planDetails = new ArrayList<>();
                    for (int i = 0; i < plansArray.length(); i++) {
                        JSONObject p = plansArray.getJSONObject(i);
                        planDetails.add(new Plan.PlanDetail(p.getInt("duration"), p.getDouble("charges")));
                    }

                    resultMap.put(planName, new Plan(planName, description, features, planDetails));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (!resultMap.isEmpty()) {
                    plansMap = resultMap;
                    selectTab("Standard");
                } else {
                    Toast.makeText(this, "Failed to load plans", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void handleSubscription() {
        int selectedId = planRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a duration", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadio = findViewById(selectedId);
        Plan.PlanDetail selectedDetail = (Plan.PlanDetail) selectedRadio.getTag();

        if (selectedDetail == null || currentPlan == null) {
            Toast.makeText(this, "Invalid plan selected", Toast.LENGTH_SHORT).show();
            return;
        }

        int days = selectedDetail.getDuration();
        double charges = selectedDetail.getCharges();

        // Log duration
        Log.d("DURATION", "Duration of the plan: " + days);

        String endDate = calculateEndDate(selectedDetail.getDuration());

        switch (currentPlan.getTitle()) {
            case "Basic":
                SubscriptionUtils.saveLimits(this, 2, 1);
                SubscriptionUtils.saveCurrentPlan(this, "Basic", endDate);
                break;
            case "Standard":
                SubscriptionUtils.saveLimits(this, 4, 2);
                SubscriptionUtils.saveCurrentPlan(this, "Standard", endDate);
                break;
            case "Premium":
                SubscriptionUtils.saveLimits(this, 4, 2);
                SubscriptionUtils.saveCurrentPlan(this, "Premium", endDate);
                break;
            default:
                Toast.makeText(this, "Unknown plan type", Toast.LENGTH_SHORT).show();
                return;
        }

        // Continue to payment
        Bundle bundle = new Bundle();
        bundle.putString("subscription_type", currentPlan.getTitle());
        bundle.putString("subscription_duration", selectedRadio.getText().toString());
        bundle.putInt("subscription_days", days);
        bundle.putDouble("subscription_charges", charges);
        bundle.putString("flow_type", flowType);

        Intent intent = new Intent(this, PaymentOptionsActivity.class);
        intent.putExtras(bundle);

        if ("change_plan".equals(flowType)) {
            startActivityForResult(intent, 1234);
        } else {
            startActivity(intent);
        }
    }

    private String calculateEndDate(int durationDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, durationDays);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            boolean paymentSuccess = data != null && data.getBooleanExtra("payment_success", false);
            if (paymentSuccess) {
                Toast.makeText(this, "Plan changed successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}








/*
package com.example.parentwithsubscription.authentication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.utils.SubscriptionUtils;

import java.util.*;

public class SubscriptionOptionsActivity extends AppCompatActivity {

    private Button tabBasic, tabStandard, tabPremium;
    private LinearLayout planContainer;
    private TextView planTitle, planDescription;
    private LinearLayout featuresContainer;
    private RadioGroup planRadioGroup;
    private Button btnSubscribe;
    private String flowType = "subscribe";

    private String PLAN_DETAILS = URIConstants.PLAN_DETAILS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_subscription_options); // Your XML layout

        tabBasic = findViewById(R.id.tab_basic);
        tabStandard = findViewById(R.id.tab_standard);
        tabPremium = findViewById(R.id.tab_premium);
        btnSubscribe = findViewById(R.id.btn_subscribe);

        planContainer = findViewById(R.id.plan_container);
        planTitle = findViewById(R.id.plan_title);
        planDescription = findViewById(R.id.plan_description);
        featuresContainer = findViewById(R.id.features_container);
        planRadioGroup = findViewById(R.id.plan_radio_group);

        flowType = getIntent().getStringExtra("flow_type");
        if (flowType == null) flowType = "subscribe";

        View.OnClickListener tabClickListener = v -> {
            resetTabStyles();
            int highlightColor = ContextCompat.getColor(this, R.color.colorSecondaryLight);
            ((Button) v).setBackgroundColor(highlightColor);
            ((Button) v).setTextColor(Color.BLACK);

            if (v == tabBasic) displayPlan("Basic");
            else if (v == tabStandard) displayPlan("Standard");
            else displayPlan("Premium");
        };

        tabBasic.setOnClickListener(tabClickListener);
        tabStandard.setOnClickListener(tabClickListener);
        tabPremium.setOnClickListener(tabClickListener);

        tabStandard.performClick(); // Default tab

        btnSubscribe.setOnClickListener(v -> {
            int selectedId = planRadioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a duration", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadio = findViewById(selectedId);
            String duration = selectedRadio.getText().toString();
            String selectedPlan = planTitle.getText().toString();
            int days = getDaysForDuration(duration);

            Bundle bundle = new Bundle();
            bundle.putString("subscription_type", selectedPlan);
            bundle.putString("subscription_duration", duration);
            bundle.putInt("subscription_days", days);
            bundle.putString("flow_type", flowType);

            Intent intent = new Intent(this, PaymentOptionsActivity.class);
            intent.putExtras(bundle);

            if ("change_plan".equals(flowType)) {
                startActivityForResult(intent, 1234);
            } else {
                startActivity(intent);
            }
        });
    }

    private void resetTabStyles() {
        tabBasic.setBackgroundColor(Color.WHITE);
        tabStandard.setBackgroundColor(Color.WHITE);
        tabPremium.setBackgroundColor(Color.WHITE);

        int primaryColor = ContextCompat.getColor(this, R.color.colorPrimary);
        tabBasic.setTextColor(primaryColor);
        tabStandard.setTextColor(primaryColor);
        tabPremium.setTextColor(primaryColor);
    }

    private void displayPlan(String planType) {
        planTitle.setText(planType);
        featuresContainer.removeAllViews();
        planRadioGroup.removeAllViews();

        List<String> features = Arrays.asList(
                "Location Tracking",
                "Call Details",
                "SMS Details",
                "App Usage",
                "Contact Details"
        );

        Map<String, List<Boolean>> featureMap = new HashMap<>();
        featureMap.put("Basic", Arrays.asList(false, true, true, false, false));
        featureMap.put("Standard", Arrays.asList(false, true, true, false, true));
        featureMap.put("Premium", Arrays.asList(true, true, true, true, true));

        String description = "";
        String[] pricing = new String[4];
        switch (planType) {
            case "Basic":
                description = "2 parents for 1 child";
                pricing = new String[]{"₹299 / month", "₹799 / quarter", "₹1549 / half year", "₹2999 / year"};
                SubscriptionUtils.saveLimits(this, 2, 1);
                SubscriptionUtils.saveCurrentPlan(this, "Basic", "30 Aug 2025");
                break;
            case "Standard":
                description = "4 parents with 2 children";
                pricing = new String[]{"₹399 / month", "₹999 / quarter", "₹1899 / half year", "₹3599 / year"};
                SubscriptionUtils.saveLimits(this, 4, 2);
                SubscriptionUtils.saveCurrentPlan(this, "Standard", "21 Nov 2025");
                break;
            case "Premium":
                description = "Family pack (4 guardians, 2 children and grandparent)";
                pricing = new String[]{"₹999 / month", "₹2699 / quarter", "₹4999 / half year", "₹9999 / year"};
                SubscriptionUtils.saveLimits(this, 4, 2); // Assuming Premium has same child limit
                SubscriptionUtils.saveCurrentPlan(this, "Premium", "09 Feb 2026");
                break;
        }

        planDescription.setText(description);

        List<Boolean> availableFeatures = featureMap.get(planType);
        for (int i = 0; i < features.size(); i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            row.setPadding(0, 8, 0, 8);

            TextView featureName = new TextView(this);
            featureName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            featureName.setText(features.get(i));
            featureName.setTextSize(16);

            ImageView icon = new ImageView(this);
            icon.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
            icon.setImageResource(availableFeatures.get(i) ? R.drawable.ic_check : R.drawable.ic_cross);

            row.addView(featureName);
            row.addView(icon);
            featuresContainer.addView(row);
        }

        for (String price : pricing) {
            RadioButton rb = new RadioButton(this);
            rb.setText(price);
            rb.setTextSize(16);
            planRadioGroup.addView(rb);
        }
    }

    private int getDaysForDuration(String durationText) {
        durationText = durationText.toLowerCase();

        if (durationText.contains("month")) return 30;
        else if (durationText.contains("quarter")) return 90;
        else if (durationText.contains("half year")) return 180;
        else if (durationText.contains("year")) return 365;

        return 0; // fallback
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            boolean paymentSuccess = data != null && data.getBooleanExtra("payment_success", false);
            if (paymentSuccess) {
                Toast.makeText(this, "Plan changed successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
*/









/*
package com.example.parentwithsubscription.authentication.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.utils.SubscriptionUtils;

import java.util.*;

public class SubscriptionOptionsActivity extends AppCompatActivity {

    private Button tabBasic, tabStandard, tabPremium;
    private LinearLayout planContainer;
    private TextView planTitle, planDescription;
    private LinearLayout featuresContainer;
    private RadioGroup planRadioGroup;
    private Button btnSubscribe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_subscription_options); // Reuse the fragment's XML

        tabBasic = findViewById(R.id.tab_basic);
        tabStandard = findViewById(R.id.tab_standard);
        tabPremium = findViewById(R.id.tab_premium);
        btnSubscribe = findViewById(R.id.btn_subscribe);

        planContainer = findViewById(R.id.plan_container);
        planTitle = findViewById(R.id.plan_title);
        planDescription = findViewById(R.id.plan_description);
        featuresContainer = findViewById(R.id.features_container);
        planRadioGroup = findViewById(R.id.plan_radio_group);

        View.OnClickListener tabClickListener = v -> {
            resetTabStyles();
            int primaryColor = ContextCompat.getColor(this, R.color.colorSecondaryLight);
            ((Button) v).setBackgroundColor(primaryColor);
            ((Button) v).setTextColor(Color.BLACK);

            if (v == tabBasic) displayPlan("Basic");
            else if (v == tabStandard) displayPlan("Standard");
            else displayPlan("Premium");
        };

        tabBasic.setOnClickListener(tabClickListener);
        tabStandard.setOnClickListener(tabClickListener);
        tabPremium.setOnClickListener(tabClickListener);

        tabStandard.performClick(); // Default selected

        btnSubscribe.setOnClickListener(v -> {
            int selectedId = planRadioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a duration", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadio = findViewById(selectedId);
            String duration = selectedRadio.getText().toString();
            String selectedPlan = planTitle.getText().toString();

            Bundle bundle = new Bundle();
            bundle.putString("subscription_type", selectedPlan);
            bundle.putString("subscription_duration", duration);

            // Now launch PaymentOptionsActivity instead of Fragment
            Intent intent = new Intent(this, PaymentOptionsActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void resetTabStyles() {
        tabBasic.setBackgroundColor(Color.WHITE);
        tabStandard.setBackgroundColor(Color.WHITE);
        tabPremium.setBackgroundColor(Color.WHITE);

        int primaryColor = ContextCompat.getColor(this, R.color.colorPrimary);
        tabBasic.setTextColor(primaryColor);
        tabStandard.setTextColor(primaryColor);
        tabPremium.setTextColor(primaryColor);
    }

    private void displayPlan(String planType) {
        planTitle.setText(planType);
        featuresContainer.removeAllViews();
        planRadioGroup.removeAllViews();

        List<String> features = Arrays.asList(
                "Location Tracking",
                "Call Details",
                "SMS Details",
                "App Usage",
                "Contact Details"
        );

        Map<String, List<Boolean>> featureMap = new HashMap<>();
        featureMap.put("Basic", Arrays.asList(false, true, true, false, false));
        featureMap.put("Standard", Arrays.asList(false, true, true, false, true));
        featureMap.put("Premium", Arrays.asList(true, true, true, true, true));

        String description = "";
        String[] pricing = new String[4];
        switch (planType) {
            case "Basic":
                description = "2 parents for 1 child";
                pricing = new String[]{"₹299 / month", "₹799 / quarter", "₹1549 / half year", "₹2999 / year"};
                SubscriptionUtils.saveLimits(this, 2, 1);
                SubscriptionUtils.saveCurrentPlan(this, "Basic", "30 Aug 2025");
                break;
            case "Standard":
                description = "4 parents with 2 children";
                pricing = new String[]{"₹399 / month", "₹999 / quarter", "₹1899 / half year", "₹3599 / year"};
                SubscriptionUtils.saveLimits(this, 4, 2);
                SubscriptionUtils.saveCurrentPlan(this, "Standard", "21 Nov 2025");
                break;
            case "Premium":
                description = "Family pack (includes guardians, grandparent and 2 children)";
                pricing = new String[]{"₹999 / month", "₹2699 / quarter", "₹4999 / half year", "₹9999 / year"};
                SubscriptionUtils.saveLimits(this, 4, 2);
                SubscriptionUtils.saveCurrentPlan(this, "Premium", "09 Feb 2026");
                break;
        }

        planDescription.setText(description);

        List<Boolean> availableFeatures = featureMap.get(planType);
        for (int i = 0; i < features.size(); i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            row.setPadding(0, 8, 0, 8);

            TextView featureName = new TextView(this);
            featureName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            featureName.setText(features.get(i));
            featureName.setTextSize(16);

            ImageView icon = new ImageView(this);
            icon.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
            icon.setImageResource(availableFeatures.get(i) ? R.drawable.ic_check : R.drawable.ic_cross);

            row.addView(featureName);
            row.addView(icon);
            featuresContainer.addView(row);
        }

        for (String price : pricing) {
            RadioButton rb = new RadioButton(this);
            rb.setText(price);
            rb.setTextSize(16);
            planRadioGroup.addView(rb);
        }
    }
}
*/
