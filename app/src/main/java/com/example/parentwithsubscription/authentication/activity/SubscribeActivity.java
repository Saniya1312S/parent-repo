package com.example.parentwithsubscription.authentication.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SubscribeActivity extends AppCompatActivity {

    EditText fullName, phone, address, occupation, aadhar, dob;
    Spinner roleSpinner;
    Button btnNext;
    private ImageButton backButton;
    String selectedRole, selectedCountry, selectedDOB;

    private String SUBSCRIBE_URL = URIConstants.SUBSCRIBE_URL;
    private String GUARDIAN_MONGODB_URL = URIConstants.GUARDIAN_MONGODB_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber_registration);

        fullName = findViewById(R.id.inputFullName);
        phone = findViewById(R.id.inputPhone);
        address = findViewById(R.id.inputAddress);
//        occupation = findViewById(R.id.inputOccupation);
        aadhar = findViewById(R.id.inputAadhar);
        dob = findViewById(R.id.inputDOB);
        roleSpinner = findViewById(R.id.spinnerRole);
        btnNext = findViewById(R.id.btnNext);
        backButton = findViewById(R.id.backButton);
        TextInputLayout aadharLayout = findViewById(R.id.aadharLayout);

        // Get selected country from Intent
        Intent intent = getIntent();
        selectedCountry = intent.getStringExtra("COUNTRY");

        Log.d("Country", selectedCountry);
        // Show or hide Aadhar field based on country
        if (selectedCountry != null && selectedCountry.equals("India (+91)")) {
            aadharLayout.setVisibility(View.VISIBLE);
        } else {
            aadharLayout.setVisibility(View.GONE);
        }

        // Set up the role spinner
        ArrayList<String> roles = new ArrayList<>();
        roles.add("Father");
        roles.add("Mother");
        roles.add("Grandfather");
        roles.add("Grandmother");
        roles.add("Uncle");
        roles.add("Aunt");

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        roleSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedRole = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parentView) {
                selectedRole = null;
            }
        });

        // Default selection
        roleSpinner.setSelection(0);

        // Date picker for DOB
        dob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SubscribeActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        selectedDOB = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        dob.setText(selectedDOB);
                    }, year, month, day);
            datePickerDialog.show();
        });

        btnNext.setOnClickListener(v -> {
            if (validateFields()) {
                // Log details
                Log.d("Registered Guardian Details", "Full Name: " + fullName.getText().toString());
                Log.d("Registered Guardian Details", "Country: " + selectedCountry);
                Log.d("Registered Guardian Details", "Phone: " + phone.getText().toString());
                Log.d("Registered Guardian Details", "Address: " + address.getText().toString());
                Log.d("Registered Guardian Details", "Occupation: " + occupation.getText().toString());
                Log.d("Registered Guardian Details", "Aadhar: " + aadhar.getText().toString());
                Log.d("Registered Guardian Details", "Date of Birth: " + selectedDOB);
                Log.d("Registered Guardian Details", "Role: " + selectedRole);

                Toast.makeText(this, "Registered Guardian details logged successfully!", Toast.LENGTH_SHORT).show();

                submitGuardianDetails();
            }
        });

        // Set a click listener to handle the back action
        backButton.setOnClickListener(v -> {
            finish(); // Closes this activity and returns to the previous one
        });

    }

    private boolean validateFields() {
        boolean isValid = true;

        String fullNameStr = fullName.getText().toString().trim();
        String phoneStr = phone.getText().toString().trim();
        String addressStr = address.getText().toString().trim();
//        String occupationStr = occupation.getText().toString().trim();
        String aadharStr = aadhar.getText().toString().trim();

        // Full Name
        if (fullNameStr.isEmpty()) {
            ((TextInputLayout) fullName.getParent().getParent()).setError("Full Name is required");
            isValid = false;
        } else {
            ((TextInputLayout) fullName.getParent().getParent()).setErrorEnabled(false);
        }

        // Phone
        if (phoneStr.isEmpty() || phoneStr.length() != 10) {
            ((TextInputLayout) phone.getParent().getParent()).setError("Phone number must be 10 digits");
            isValid = false;
        } else {
            ((TextInputLayout) phone.getParent().getParent()).setErrorEnabled(false);
        }

        // Address
        if (addressStr.isEmpty()) {
            ((TextInputLayout) address.getParent().getParent()).setError("Address is required");
            isValid = false;
        } else {
            ((TextInputLayout) address.getParent().getParent()).setErrorEnabled(false);
        }

/*
        // Occupation
        if (occupationStr.isEmpty()) {
            ((TextInputLayout) occupation.getParent().getParent()).setError("Occupation is required");
            isValid = false;
        } else {
            ((TextInputLayout) occupation.getParent().getParent()).setErrorEnabled(false);
        }
*/

        // Aadhar
        if ("India (+91)".equals(selectedCountry)) {
            if (aadharStr.isEmpty() || aadharStr.length() != 12) {
                ((TextInputLayout) aadhar.getParent().getParent()).setError("Aadhar must be 12 digits");
                isValid = false;
            } else {
                ((TextInputLayout) aadhar.getParent().getParent()).setErrorEnabled(false);
            }
        }

        // Date of Birth
        if (TextUtils.isEmpty(selectedDOB)) {
            ((TextInputLayout) dob.getParent().getParent()).setError("Date of Birth is required");
            isValid = false;
        } else {
            ((TextInputLayout) dob.getParent().getParent()).setErrorEnabled(false);
        }

        return isValid;
    }

    private void submitGuardianDetails() {
        OkHttpClient client = new OkHttpClient();

        // Retrieve the initial user token
        String userToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("USER_TOKEN", null);

        if (userToken == null) {
            Toast.makeText(this, "No user token found. Please register first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format date to YYYY-MM-DD
        String[] dobParts = selectedDOB.split("/");
        String formattedDOB = dobParts[2] + "-" + String.format("%02d", Integer.parseInt(dobParts[1])) + "-" + String.format("%02d", Integer.parseInt(dobParts[0]));

        // Prepare JSON
        String json = "{"
                + "\"USER_FULL_NAME\":\"" + fullName.getText().toString() + "\","
                + "\"USER_ROLES\":\"" + "PARENT" + "\","
                + "\"AADHAR_DETAILS\":\"" + (aadhar.getVisibility() == View.VISIBLE ? aadhar.getText().toString() : "") + "\","
                + "\"DATE_OF_BIRTH\":\"" + formattedDOB + "\","
                + "\"PHONE_NUMBER\":\"" + phone.getText().toString() + "\""
                + "}";

        RequestBody body = RequestBody.create(json, okhttp3.MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(SUBSCRIBE_URL)
                .addHeader("Authorization", "Bearer " + userToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SubscribeActivity.this, "API call failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String paidToken = jsonResponse.getString("USER_TOKEN");

                        Log.d("Primary Subscription Token ","Primary Subscription Token: " + paidToken);
                        // Save the new token as PAID_USER_TOKEN
                        getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("PAID_USER_TOKEN", paidToken)
                                .apply();

                        runOnUiThread(() -> {
                            Toast.makeText(SubscribeActivity.this, "Guardian details submitted successfully!", Toast.LENGTH_SHORT).show();
                            submitToParentMongo(paidToken);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(SubscribeActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void submitToParentMongo(String paidToken) {
        OkHttpClient client = new OkHttpClient();

        // Format date to YYYY-MM-DD
        String[] dobParts = selectedDOB.split("/");
        String formattedDOB = dobParts[2] + "-" + String.format("%02d", Integer.parseInt(dobParts[1])) + "-" + String.format("%02d", Integer.parseInt(dobParts[0]));

        // Prepare JSON
        String json = "{"
                + "\"name\":\"" + fullName.getText().toString() + "\","
                + "\"familyrole\":\"" + selectedRole + "\","
                + "\"dob\":\"" + formattedDOB + "\","
//                + "\"occupation\":\"" + occupation.getText().toString() + "\","
                + "\"address\":\"" + address.getText().toString() + "\","
                + "\"track\":\"" + "false" + "\","
                + "\"mobile\":\"" + phone.getText().toString() + "\""
                + "}";

        RequestBody body = RequestBody.create(json, okhttp3.MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(GUARDIAN_MONGODB_URL)
                .addHeader("Authorization", "Bearer " + paidToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SubscribeActivity.this, "Parent Mongo API call failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() == 201) {
                    Log.d("ParentMongo", "Success: " + responseBody);
                    runOnUiThread(() -> {
                        Toast.makeText(SubscribeActivity.this, "Family document created.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SubscribeActivity.this, SubscriptionOptionsActivity.class));
                    });
                } else {
                    Log.e("ParentMongo", "Failed: " + response.code() + ", " + responseBody);
                    runOnUiThread(() -> Toast.makeText(SubscribeActivity.this, "Parent Mongo API error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

}
