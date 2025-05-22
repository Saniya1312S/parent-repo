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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.*;

public class AddChildDetailsActivity extends AppCompatActivity {

    EditText name, phone, address, aadhar, dob;
    Spinner countrySpinner;
    RadioGroup roleRadioGroup, radioGroupTrack;
    Button btnSaveChild;
    ImageButton backButton;
    String selectedRole = "", selectedCountry = "", selectedDOB = "";
    boolean isTrackEnabled = false;

    private final String CHILD_MONGODB_URL = URIConstants.CHILD_MONGODB_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        name = findViewById(R.id.inputChildName);
        phone = findViewById(R.id.inputChildPhone);
        address = findViewById(R.id.inputChildAddress);
        aadhar = findViewById(R.id.inputChildAadhar);
        dob = findViewById(R.id.inputChildDOB);
        roleRadioGroup = findViewById(R.id.radioGroupRole);
        countrySpinner = findViewById(R.id.spinnerCountry);
        radioGroupTrack = findViewById(R.id.radioGroupTrack);
        btnSaveChild = findViewById(R.id.btnSaveChild);
        backButton = findViewById(R.id.backButton);

        setupCountrySpinner();
        setupDOBPicker();

        btnSaveChild.setOnClickListener(v -> {
            int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
            if (selectedRoleId != -1) {
                RadioButton selectedRoleButton = findViewById(selectedRoleId);
                selectedRole = selectedRoleButton.getText().toString();
            } else {
                Toast.makeText(this, "Please select relationship (Son or Daughter)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (validateFields()) {
                Log.d("Child Details", "Name: " + name.getText().toString());
                Log.d("Child Details", "Track Enabled: " + isTrackEnabled);

                submitChildDetailsToMongo();
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void setupCountrySpinner() {
        ArrayList<String> countries = new ArrayList<>();
        countries.add("India");
        countries.add("USA");
        countries.add("UK");
        countries.add("Canada");
        countries.add("Australia");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = parent.getItemAtPosition(position).toString();
                toggleAadharVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void toggleAadharVisibility() {
        if ("India".equals(selectedCountry)) {
            aadhar.setVisibility(View.VISIBLE);
        } else {
            aadhar.setVisibility(View.GONE);
        }
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
        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError("Name is required");
            name.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone.getText().toString()) || phone.getText().toString().length() != 10) {
            phone.setError("Phone number must be exactly 10 digits");
            phone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(address.getText().toString())) {
            address.setError("Address is required");
            address.requestFocus();
            return false;
        }

        if ("India".equals(selectedCountry)) {
            if (TextUtils.isEmpty(aadhar.getText().toString()) || aadhar.getText().toString().length() != 12) {
                aadhar.setError("Aadhar must be 12 digits");
                aadhar.requestFocus();
                return false;
            }
        }

        if (TextUtils.isEmpty(selectedDOB)) {
            dob.setError("DOB is required");
            dob.requestFocus();
            return false;
        }

        int selectedTrackId = radioGroupTrack.getCheckedRadioButtonId();
        if (selectedTrackId == -1) {
            Toast.makeText(this, "Please select Track (Yes or No)", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            RadioButton selectedTrackButton = findViewById(selectedTrackId);
            isTrackEnabled = selectedTrackButton.getText().toString().equalsIgnoreCase("Yes");
        }

        return true;
    }

    private void submitChildDetailsToMongo() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String paidToken = prefs.getString("PAID_USER_TOKEN", null);

        if (paidToken == null) {
            Toast.makeText(this, "Paid token not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] dobParts = selectedDOB.split("/");
        String formattedDOB = dobParts[2] + "-" + String.format("%02d", Integer.parseInt(dobParts[1])) + "-" + String.format("%02d", Integer.parseInt(dobParts[0]));

        String json = "{"
                + "\"name\":\"" + name.getText().toString() + "\","
                + "\"familyrole\":\"" + selectedRole.toUpperCase() + "\","
                + "\"dob\":\"" + formattedDOB + "\","
                + "\"address\":\"" + address.getText().toString() + "\","
                + "\"mobile\":\"" + phone.getText().toString() + "\","
                + "\"track\":" + isTrackEnabled
                + "}";

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(CHILD_MONGODB_URL)
                .addHeader("Authorization", "Bearer " + paidToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(AddChildDetailsActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 201) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddChildDetailsActivity.this, "Child added successfully!", Toast.LENGTH_SHORT).show();
                        Intent result = new Intent();
                        result.putExtra("name", name.getText().toString());
                        result.putExtra("profileImage", ""); // Optional placeholder
                        setResult(RESULT_OK, result);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(AddChildDetailsActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
