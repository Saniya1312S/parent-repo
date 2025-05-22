package com.example.parentwithsubscription.authentication.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import okhttp3.*;

public class AddMasterDetailsActivity extends AppCompatActivity {

    EditText fullName, phone, address, aadhar, dob;
    Spinner roleSpinner;
    Button btnSave;
    ImageButton backButton;

    final String TAG = "AddMasterDetailsActivity";

    String selectedRole, selectedDOB;

    private final String SUBSCRIBE_URL = URIConstants.SUBSCRIBE_URL;
    private final String GUARDIAN_MONGODB_URL = URIConstants.GUARDIAN_MONGODB_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian_details);

        fullName = findViewById(R.id.inputFullName);
        phone = findViewById(R.id.inputPhone);
        address = findViewById(R.id.inputAddress);
        aadhar = findViewById(R.id.inputAadhar);
        dob = findViewById(R.id.inputDOB);
        roleSpinner = findViewById(R.id.spinnerRole);
        btnSave = findViewById(R.id.btnSave);
        backButton = findViewById(R.id.backButton);
// Get the data passed from PaymentOptionsActivity
        Bundle data = getIntent().getExtras();
        if (data != null) {
            String paymentMethod = data.getString("payment_method", "Unknown");

            // Log the payment method
            Log.d(TAG, "Payment Method: " + paymentMethod);

            // Coupon Code (if any)
            String couponCode = data.getString("coupon_code");
            if (couponCode != null && !couponCode.isEmpty()) {
                Log.d(TAG, "Coupon Code: " + couponCode);
            }

            // Auto-renewal status
            boolean isAutoRenewalEnabled = data.getBoolean("auto_renewal", false);
            Log.d(TAG, "Auto-Renewal Enabled: " + isAutoRenewalEnabled);

            // ✅ Subscription Info
            String planType = data.getString("subscription_type", "Unknown");
            String durationText = data.getString("subscription_duration", "Unknown");
            int days = data.getInt("subscription_days", 0);

            Log.d(TAG, "Plan Type: " + planType);
            Log.d(TAG, "Duration: " + durationText);
            Log.d(TAG, "Days: " + days);

            // Payment method–specific details
            if (paymentMethod.equals("Card")) {
                String cardNumber = data.getString("card_number");
                String cardExpiry = data.getString("card_expiry");
                String cardCvv = data.getString("card_cvv");
                String cardName = data.getString("card_name");
                String cardAddress = data.getString("card_address");

                Log.d(TAG, "Card Number: " + cardNumber);
                Log.d(TAG, "Card Expiry: " + cardExpiry);
                Log.d(TAG, "Card CVV: " + cardCvv);
                Log.d(TAG, "Card Name: " + cardName);
                Log.d(TAG, "Card Address: " + cardAddress);
            } else if (paymentMethod.equals("UPI")) {
                String selectedUPI = data.getString("selected_upi");
                Log.d(TAG, "Selected UPI: " + selectedUPI);
            } else if (paymentMethod.equals("PayPal")) {
                String paypalEmail = data.getString("paypal_email");
                Log.d(TAG, "PayPal Email: " + paypalEmail);
            }
        }
        setupRoleSpinner();
        setupDOBPicker();

        btnSave.setOnClickListener(v -> {
            if (validateFields()) {
                logGuardianDetails();
                Toast.makeText(this, "Guardian details logged!", Toast.LENGTH_SHORT).show();
                submitGuardianDetails();
            }
        });

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupRoleSpinner() {
        ArrayList<String> roles = new ArrayList<>();
        roles.add("Father");
        roles.add("Mother");
        roles.add("Friends & Relative");
        roles.add("Others");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
        roleSpinner.setSelection(0);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = null;
            }
        });
    }

    private void setupDOBPicker() {
        dob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, month1, dayOfMonth) -> {
                        selectedDOB = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        dob.setText(selectedDOB);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (fullName.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) fullName.getParent().getParent()).setError("Full Name is required");
            isValid = false;
        } else {
            ((TextInputLayout) fullName.getParent().getParent()).setErrorEnabled(false);
        }

        if (phone.getText().toString().trim().length() != 10) {
            ((TextInputLayout) phone.getParent().getParent()).setError("Phone must be 10 digits");
            isValid = false;
        } else {
            ((TextInputLayout) phone.getParent().getParent()).setErrorEnabled(false);
        }

        if (address.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) address.getParent().getParent()).setError("Address is required");
            isValid = false;
        } else {
            ((TextInputLayout) address.getParent().getParent()).setErrorEnabled(false);
        }

        if (aadhar.getText().toString().trim().length() != 12) {
            ((TextInputLayout) aadhar.getParent().getParent()).setError("Aadhar must be 12 digits");
            isValid = false;
        } else {
            ((TextInputLayout) aadhar.getParent().getParent()).setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(selectedDOB)) {
            ((TextInputLayout) dob.getParent().getParent()).setError("Date of Birth is required");
            isValid = false;
        } else {
            ((TextInputLayout) dob.getParent().getParent()).setErrorEnabled(false);
        }

        return isValid;
    }

    private void logGuardianDetails() {
        Log.d("GuardianDetails", "Full Name: " + fullName.getText());
        Log.d("GuardianDetails", "Phone: " + phone.getText());
        Log.d("GuardianDetails", "Address: " + address.getText());
        Log.d("GuardianDetails", "Aadhar: " + aadhar.getText());
        Log.d("GuardianDetails", "DOB: " + selectedDOB);
        Log.d("GuardianDetails", "Role: " + selectedRole);
    }

    public static String generateTransactionId() {
        // Create a UUID and trim it to a more compact string
        return "TXN" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12).toUpperCase();
    }
    private void submitGuardianDetails() {
        OkHttpClient client = new OkHttpClient();

        String userToken = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .getString("USER_TOKEN", null);

        if (userToken == null) {
            Toast.makeText(this, "No user token found. Please register first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extract data passed from PaymentOptionsActivity
        Bundle data = getIntent().getExtras();
        if (data != null) {
            String planType = data.getString("subscription_type", "Unknown");
            String durationText = data.getString("subscription_duration", "Unknown");
            int days = data.getInt("subscription_days", 0);
            String paymentMethod = data.getString("payment_method", "Unknown");
            boolean isAutoRenewalEnabled = data.getBoolean("auto_renewal", false);
            String couponCode = data.getString("coupon_code", "");

            // Hardcoding the TRANSACTION_ID for now
            String transactionId = generateTransactionId();// Replace with your static transaction ID

            // Prepare the date of birth in correct format
            String[] dobParts = selectedDOB.split("/");
            String formattedDOB = dobParts[2] + "-" + String.format("%02d", Integer.parseInt(dobParts[1])) + "-" + String.format("%02d", Integer.parseInt(dobParts[0]));

            // Build the JSON payload
            JSONObject jsonPayload = new JSONObject();
            try {
                jsonPayload.put("USER_FULL_NAME", fullName.getText().toString());
                jsonPayload.put("AADHAR_DETAILS", aadhar.getText().toString());
                jsonPayload.put("PHONE_NUMBER", phone.getText().toString());
                jsonPayload.put("PLAN_TYPE", planType);
                jsonPayload.put("DURATION", days); // Duration in days
                jsonPayload.put("PAYMENT_TYPE", paymentMethod);
//                jsonPayload.put("TRANSACTION_ID", data.getString("transaction_id", "Unknown"));
                jsonPayload.put("TRANSACTION_ID", transactionId); // Using the static value
                jsonPayload.put("CURRENCY", "INR");
                jsonPayload.put("AUTO_RENEWAL_FLAG", isAutoRenewalEnabled);
                jsonPayload.put("DATE_OF_BIRTH", formattedDOB);

                // If a discount code is provided, add it to the payload
                if (!TextUtils.isEmpty(couponCode)) {
                    jsonPayload.put("DISCOUNT_CODE", couponCode);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Prepare the request body
            RequestBody body = RequestBody.create(jsonPayload.toString(), MediaType.parse("application/json; charset=utf-8"));

            // Make the API call
            Request request = new Request.Builder()
                    .url(SUBSCRIBE_URL)
                    .addHeader("Authorization", "Bearer " + userToken)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(AddMasterDetailsActivity.this, "API call failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            String paidToken = jsonResponse.getString("USER_TOKEN");

                            Log.d("Subscription", "Received token: " + paidToken);

                            getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                    .edit()
                                    .putString("PAID_USER_TOKEN", paidToken)
                                    .apply();

                            runOnUiThread(() -> {
                                Toast.makeText(AddMasterDetailsActivity.this, "Guardian details submitted!", Toast.LENGTH_SHORT).show();
                                submitToParentMongo(paidToken);
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(AddMasterDetailsActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }
    }

    private void submitToParentMongo(String paidToken) {
        OkHttpClient client = new OkHttpClient();

        String[] dobParts = selectedDOB.split("/");
        String formattedDOB = dobParts[2] + "-" + String.format("%02d", Integer.parseInt(dobParts[1])) + "-" + String.format("%02d", Integer.parseInt(dobParts[0]));

        String json = "{"
                + "\"name\":\"" + fullName.getText() + "\","
                + "\"familyrole\":\"" + selectedRole + "\","
                + "\"dob\":\"" + formattedDOB + "\","
                + "\"address\":\"" + address.getText() + "\","
                + "\"track\":false,"
                + "\"mobile\":\"" + phone.getText() + "\""
                + "}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(GUARDIAN_MONGODB_URL)
                .addHeader("Authorization", "Bearer " + paidToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(AddMasterDetailsActivity.this, "Mongo API failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() == 201) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddMasterDetailsActivity.this, "Family document created.", Toast.LENGTH_SHORT).show();

                        // ✅ Proceed directly to KidsAndGuardianDetailsActivity
                        Intent next = new Intent(AddMasterDetailsActivity.this, KidsAndGuardianDetailsActivity.class);
                        next.putExtra("name", fullName.getText().toString());
                        next.putExtra("role", selectedRole);
                        startActivity(next);
                        finish();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(AddMasterDetailsActivity.this, "Mongo API error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
