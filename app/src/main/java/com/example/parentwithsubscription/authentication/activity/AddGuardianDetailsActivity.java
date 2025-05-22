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

import okhttp3.*;

public class AddGuardianDetailsActivity extends AppCompatActivity {

    EditText fullName, phone, address, aadhar, dob;
    Spinner roleSpinner;
    Button btnSave;
    ImageButton backButton;

    String selectedRole, selectedDOB;
    String GUARDIAN_MONGODB_URL = URIConstants.GUARDIAN_MONGODB_URL;
    String GUARDIAN_DETAILS_REGISTER_URL = URIConstants.GUARDIAN_DETAILS_REGISTER_URL;

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

        setupRoleSpinner();
        setupDOBPicker();

        btnSave.setOnClickListener(v -> {
            if (validateFields()) {
                logGuardianDetails();
                Toast.makeText(this, "Guardian details logged!", Toast.LENGTH_SHORT).show();
                submitGuardianDetails();
            }
        });

        backButton.setOnClickListener(v -> finish());
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
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
        Log.d("GuardianDetails", "Full Name: " + fullName.getText().toString());
        Log.d("GuardianDetails", "Phone: " + phone.getText().toString());
        Log.d("GuardianDetails", "Address: " + address.getText().toString());
        Log.d("GuardianDetails", "Aadhar: " + aadhar.getText().toString());
        Log.d("GuardianDetails", "DOB: " + selectedDOB);
        Log.d("GuardianDetails", "Role: " + selectedRole);
    }

    private void submitGuardianDetails() {
        OkHttpClient client = new OkHttpClient();

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String paidToken = prefs.getString("PAID_USER_TOKEN", null);
        String userId = prefs.getString("PAID_USER_ID", null);

        if (paidToken == null || userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] dobParts = selectedDOB.split("/");
        String formattedDOB = dobParts[2] + "-" + String.format("%02d", Integer.parseInt(dobParts[1])) + "-" + String.format("%02d", Integer.parseInt(dobParts[0]));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("USER_ID", userId);
            jsonObject.put("USER_FULL_NAME", fullName.getText().toString());
            jsonObject.put("USER_ROLES", "PARENT");
            jsonObject.put("AADHAR_DETAILS", aadhar.getText().toString());
            jsonObject.put("DATE_OF_BIRTH", formattedDOB);
            jsonObject.put("PHONE_NUMBER", phone.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "JSON creation error", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(GUARDIAN_DETAILS_REGISTER_URL)
                .addHeader("Authorization", "Bearer " + paidToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(AddGuardianDetailsActivity.this, "API call failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> submitToParentMongo(paidToken));
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(AddGuardianDetailsActivity.this, "Submission failed: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void submitToParentMongo(String paidToken) {
        OkHttpClient client = new OkHttpClient();

        String[] dobParts = selectedDOB.split("/");
        String formattedDOB = dobParts[2] + "-" + String.format("%02d", Integer.parseInt(dobParts[1])) + "-" + String.format("%02d", Integer.parseInt(dobParts[0]));

        String json = "{"
                + "\"name\":\"" + fullName.getText().toString() + "\","
                + "\"familyrole\":\"" + selectedRole + "\","
                + "\"dob\":\"" + formattedDOB + "\","
                + "\"address\":\"" + address.getText().toString() + "\","
                + "\"track\":\"false\","
                + "\"mobile\":\"" + phone.getText().toString() + "\""
                + "}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(GUARDIAN_MONGODB_URL)
                .addHeader("Authorization", "Bearer " + paidToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(AddGuardianDetailsActivity.this, "Mongo API error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 201) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddGuardianDetailsActivity.this, "Guardian added to family!", Toast.LENGTH_SHORT).show();

                        Intent result = new Intent();
                        result.putExtra("name", fullName.getText().toString());
                        result.putExtra("role", selectedRole);
                        setResult(RESULT_OK, result);
                        finish();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(AddGuardianDetailsActivity.this, "Mongo API failed: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}














/*
package com.example.parentwithsubscription.authentication.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddGuardianDetailsActivity extends AppCompatActivity {

    EditText fullName, phone, address, occupation, aadhar, dob;
    Spinner roleSpinner;
    Button btnNext;
    String selectedRole, selectedCountry, selectedDOB;
    private ImageButton backButton;
    private String GUARDIAN_MONGODB_URL = URIConstants.GUARDIAN_MONGODB_URL;
    private String GUARDIAN_DETAILS_REGISTER_URL = URIConstants.GUARDIAN_DETAILS_REGISTER_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian_details);

        fullName = findViewById(R.id.inputFullName);
        phone = findViewById(R.id.inputPhone);
        address = findViewById(R.id.inputAddress);
        occupation = findViewById(R.id.inputOccupation);
        aadhar = findViewById(R.id.inputAadhar);
        dob = findViewById(R.id.inputDOB);
        roleSpinner = findViewById(R.id.spinnerRole);
        btnNext = findViewById(R.id.btnNext);
        backButton = findViewById(R.id.backButton);


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

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddGuardianDetailsActivity.this,
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
            finish();
        });
    }

    private boolean validateFields() {
        boolean isValid = true;

        String fullNameStr = fullName.getText().toString().trim();
        String phoneStr = phone.getText().toString().trim();
        String addressStr = address.getText().toString().trim();
        String occupationStr = occupation.getText().toString().trim();
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

        // Occupation
        if (occupationStr.isEmpty()) {
            ((TextInputLayout) occupation.getParent().getParent()).setError("Occupation is required");
            isValid = false;
        } else {
            ((TextInputLayout) occupation.getParent().getParent()).setErrorEnabled(false);
        }

        // Aadhar
        if (aadharStr.isEmpty() || aadharStr.length() != 12) {
                ((TextInputLayout) aadhar.getParent().getParent()).setError("Aadhar must be 12 digits");
                isValid = false;
        } else {
                ((TextInputLayout) aadhar.getParent().getParent()).setErrorEnabled(false);
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

        // Get the token and user ID
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String paidToken = prefs.getString("PAID_USER_TOKEN", null);
        String userId = prefs.getString("PAID_USER_ID", null);  // Make sure you store this somewhere when logging in

        if (paidToken == null || userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format DOB to YYYY-MM-DD
        String[] dobParts = selectedDOB.split("/");
        String formattedDOB = dobParts[2] + "-" + String.format("%02d", Integer.parseInt(dobParts[1])) + "-" + String.format("%02d", Integer.parseInt(dobParts[0]));

        // Prepare JSON body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("USER_ID", userId);
            jsonObject.put("USER_FULL_NAME", fullName.getText().toString());
            jsonObject.put("USER_ROLES", "PARENT");
            jsonObject.put("AADHAR_DETAILS",aadhar.getText().toString());
            jsonObject.put("DATE_OF_BIRTH", formattedDOB);
            jsonObject.put("PHONE_NUMBER", phone.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Request setup
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(GUARDIAN_DETAILS_REGISTER_URL)
                .addHeader("Authorization", "Bearer " + paidToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AddGuardianDetailsActivity.this, "API call failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddGuardianDetailsActivity.this, "Guardian details submitted successfully!", Toast.LENGTH_LONG).show();
                        // Optional: navigate to another screen
                        submitToParentMongo(paidToken);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(AddGuardianDetailsActivity.this, "Server error: " + response.code() + "\n" + responseBody, Toast.LENGTH_LONG).show());
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
                + "\"occupation\":\"" + occupation.getText().toString() + "\","
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
                runOnUiThread(() -> Toast.makeText(AddGuardianDetailsActivity.this, "Parent Mongo API call failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() == 201) {
                    Log.d("ParentMongo", "Success: " + responseBody);
                    runOnUiThread(() -> {
                        Toast.makeText(AddGuardianDetailsActivity.this, "Family document created.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddGuardianDetailsActivity.this, ChooseGuardianOrChildActivity.class));
                    });
                } else {
                    Log.e("ParentMongo", "Failed: " + response.code() + ", " + responseBody);
                    runOnUiThread(() -> Toast.makeText(AddGuardianDetailsActivity.this, "Parent Mongo API error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
*/
