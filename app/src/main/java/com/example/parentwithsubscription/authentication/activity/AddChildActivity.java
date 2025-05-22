package com.example.parentwithsubscription.authentication.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.*;

public class AddChildActivity extends AppCompatActivity {

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
        setContentView(R.layout.fragment_add_child);

        // Initialize views
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
                Log.d("ChildDetails", "Name: " + name.getText().toString());
                submitChildDetailsToMongo();
            }
        });

        backButton.setOnClickListener(v -> onBackPressed());
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
        aadhar.setVisibility("India".equals(selectedCountry) ? View.VISIBLE : View.GONE);
    }

    private void setupDOBPicker() {
        dob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        selectedDOB = day + "/" + (month + 1) + "/" + year;
                        dob.setText(selectedDOB);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(name.getText())) {
            name.setError("Name is required");
            return false;
        }

        if (phone.getText().length() != 10) {
            phone.setError("Phone must be 10 digits");
            return false;
        }

        if (TextUtils.isEmpty(address.getText())) {
            address.setError("Address is required");
            return false;
        }

        if ("India".equals(selectedCountry) && (aadhar.getText().length() != 12)) {
            aadhar.setError("Aadhar must be 12 digits");
            return false;
        }

        if (TextUtils.isEmpty(selectedDOB)) {
            dob.setError("DOB is required");
            return false;
        }

        int trackId = radioGroupTrack.getCheckedRadioButtonId();
        if (trackId == -1) {
            Toast.makeText(this, "Select track option", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            RadioButton trackRadio = findViewById(trackId);
            isTrackEnabled = "Yes".equalsIgnoreCase(trackRadio.getText().toString());
        }

        return true;
    }

    private void submitChildDetailsToMongo() {
        String paidToken = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .getString("PAID_USER_TOKEN", null);

        if (paidToken == null) {
            Toast.makeText(this, "No paid token found", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] dobParts = selectedDOB.split("/");
        String formattedDOB = dobParts[2] + "-" + String.format("%02d", Integer.parseInt(dobParts[1])) + "-" + String.format("%02d", Integer.parseInt(dobParts[0]));

        String json = "{"
                + "\"name\":\"" + name.getText() + "\","
                + "\"familyrole\":\"" + selectedRole.toUpperCase() + "\","
                + "\"dob\":\"" + formattedDOB + "\","
                + "\"address\":\"" + address.getText() + "\","
                + "\"mobile\":\"" + phone.getText() + "\","
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
                runOnUiThread(() -> Toast.makeText(AddChildActivity.this, "API call failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                if (response.code() == 201 && res.contains("Child added successfully")) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddChildActivity.this, "Child added!", Toast.LENGTH_SHORT).show();
                        Intent result = new Intent();
                        result.putExtra("name", name.getText().toString());
                        setResult(RESULT_OK, result);

                        Intent intent = new Intent(AddChildActivity.this, KidsAndGuardianDetailsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(AddChildActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}









/*
package com.example.parentwithsubscription.authentication.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parentwithsubscription.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class AddChildActivity extends AppCompatActivity {

    EditText name, phone, address, occupation, grade, aadhar, dob;
    Spinner roleSpinner, countrySpinner;
    RadioGroup radioGroupTrack;
    Button btnSaveChild, btnSkip;
    private ImageButton backButton;

    String selectedRole = "", selectedCountry = "", selectedDOB = "";
    boolean isTrackEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);

        // Initialize the views
        name = findViewById(R.id.inputChildName);
        phone = findViewById(R.id.inputChildPhone);
        address = findViewById(R.id.inputChildAddress);
        occupation = findViewById(R.id.inputChildOccupation);
        grade = findViewById(R.id.inputGrade);
        aadhar = findViewById(R.id.inputChildAadhar);
        dob = findViewById(R.id.inputChildDOB);
        roleSpinner = findViewById(R.id.spinnerRole);
        countrySpinner = findViewById(R.id.spinnerCountry);
        radioGroupTrack = findViewById(R.id.radioGroupTrack);
        btnSaveChild = findViewById(R.id.btnSaveChild);
        backButton = findViewById(R.id.backButton);
//        btnSkip = findViewById(R.id.btnSkip);

        // Setup spinners and date picker
        setupSpinners();
        setupDOBPicker();

        // Toggle Aadhar field visibility based on the selected country
        toggleAadharFieldVisibility();

        btnSaveChild.setOnClickListener(v -> {
            if (validateFields()) {
                // Log the details of the child
                Log.d("Child Details", "Name: " + name.getText().toString());
                Log.d("Child Details", "Phone: " + phone.getText().toString());
                Log.d("Child Details", "Address: " + address.getText().toString());
                Log.d("Child Details", "Role: " + selectedRole);
                Log.d("Child Details", "Occupation: " + occupation.getText().toString());
                Log.d("Child Details", "Grade: " + grade.getText().toString());
                Log.d("Child Details", "Aadhar: " + aadhar.getText().toString());
                Log.d("Child Details", "Date of Birth: " + selectedDOB);
                Log.d("Child Details", "Track Enabled: " + isTrackEnabled);

                // Show a confirmation toast
                Toast.makeText(this, "Child details logged successfully!", Toast.LENGTH_SHORT).show();

                submitChildDetailsToMongo();
            }


*/
/*            Intent intent = new Intent(AddChildActivity.this, ChooseGuardianOrChild.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear activity stack to prevent returning to AddGuardianActivity
            startActivity(intent);*//*

        });
        // Set a click listener to handle the back action
        backButton.setOnClickListener(v -> {
            finish(); // Closes this activity and returns to the previous one
        });
*/
/*
        btnSkip.setOnClickListener(v -> {
            // Navigate to SubscribeActivity
            Intent intent = new Intent(AddChildActivity.this, MainActivity.class);
            startActivity(intent);
        });*//*

    }


    // Setup spinners for role and country
    private void setupSpinners() {
        ArrayList<String> roles = new ArrayList<>();
        roles.add("Son");
        roles.add("Daughter");

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayList<String> countries = new ArrayList<>();
        countries.add("India");
        countries.add("USA");
        countries.add("UK");
        countries.add("Canada");
        countries.add("Australia");

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = parent.getItemAtPosition(position).toString();
                toggleAadharFieldVisibility();  // Update Aadhar visibility based on selected country
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Toggle Aadhar field visibility based on country selection
    private void toggleAadharFieldVisibility() {
        if ("India".equals(selectedCountry)) {
            aadhar.setVisibility(View.VISIBLE);
        } else {
            aadhar.setVisibility(View.GONE);
        }
    }

    // Setup date picker for Date of Birth
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

    // Validate fields before saving the data
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

        if (TextUtils.isEmpty(occupation.getText().toString())) {
            occupation.setError("Occupation is required");
            occupation.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(grade.getText().toString())) {
            grade.setError("Grade is required");
            grade.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(aadhar.getText().toString()) || aadhar.getText().toString().length() != 12) {
            if ("India".equals(selectedCountry)) {
                aadhar.setError("Valid Aadhar number (12 digits) is required");
                aadhar.requestFocus();
                return false;
            }
        }

        if (TextUtils.isEmpty(selectedDOB)) {
            dob.setError("Date of Birth is required");
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
        String paidToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("PAID_USER_TOKEN", null);
        if (paidToken == null) {
            Toast.makeText(this, "Paid token not found. Please register guardian first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format date to YYYY-MM-DD
        String[] dobParts = selectedDOB.split("/");
        String formattedDOB = dobParts[2] + "-" + String.format("%02d", Integer.parseInt(dobParts[1])) + "-" + String.format("%02d", Integer.parseInt(dobParts[0]));

        String json = "{"
                + "\"name\":\"" + name.getText().toString() + "\","
                + "\"familyrole\":\"" + selectedRole.toUpperCase() + "\","
                + "\"dob\":\"" + formattedDOB + "\","
                + "\"occupation\":\"" + occupation.getText().toString() + "\","
                + "\"address\":\"" + address.getText().toString() + "\","
                + "\"mobile\":\"" + phone.getText().toString() + "\","
                + "\"grade\":\"" + grade.getText().toString() + "\","
                + "\"track\":" + isTrackEnabled
                + "}";

        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(json, okhttp3.MediaType.parse("application/json; charset=utf-8"));

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://192.168.0.101:5000/user/child-family-tree")
                .addHeader("Authorization", "Bearer " + paidToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AddChildActivity.this, "API call failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() == 201 && responseBody.contains("Child added successfully")) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddChildActivity.this, "Child added successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddChildActivity.this, ChooseGuardianOrChild.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(AddChildActivity.this, "Failed to add child: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
*/
