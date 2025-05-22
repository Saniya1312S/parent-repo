package com.example.parentwithsubscription.authentication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddGuardianActivity extends AppCompatActivity {


    private TextInputLayout emailLayout, passwordLayout, reconfirmPasswordLayout, countryLayout;
    private TextInputEditText emailField, passwordField, reconfirmPasswordField;
    private Spinner countrySpinner;
    private Button guardianRegisterButton;
    private TextView errorText;
    private ImageButton backButton;

    private String GUARDIAN_REGISTER_URL = URIConstants.GUARDIAN_REGISTER_URL;
    private String GUARDIAN_MONGODB_URL = URIConstants.GUARDIAN_MONGODB_URL;

    private static final String TAG = "RegisterActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guardian);

        // Initialize views
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        reconfirmPasswordLayout = findViewById(R.id.reconfirmPasswordLayout);
        countryLayout = findViewById(R.id.countryLayout);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        reconfirmPasswordField = findViewById(R.id.reconfirmPassword);
        countrySpinner = findViewById(R.id.countrySpinner);
        guardianRegisterButton = findViewById(R.id.guardianRegisterButton);
        backButton = findViewById(R.id.backButton);
        // Populate country spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        // Set a touch listener to detect drawable click for password visibility
        passwordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableWidth = passwordField.getCompoundDrawables()[2].getBounds().width();
                int drawableLeft = passwordField.getWidth() - drawableWidth;
                if (event.getRawX() >= drawableLeft) {
                    togglePasswordVisibility(passwordField);
                    return true;
                }
            }
            return false;
        });

        reconfirmPasswordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableWidth = reconfirmPasswordField.getCompoundDrawables()[2].getBounds().width();
                int drawableLeft = reconfirmPasswordField.getWidth() - drawableWidth;
                if (event.getRawX() >= drawableLeft) {
                    togglePasswordVisibility(reconfirmPasswordField);
                    return true;
                }
            }
            return false;
        });

        // Add OnFocusChangeListener to confirm password field to validate passwords match
        reconfirmPasswordField.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePasswordsMatch();
            }
        });

        // Add TextWatcher to clear the error once the user starts typing again
        reconfirmPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                validatePasswordsMatch();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Email field validation in real-time (while typing)
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String email = emailField.getText().toString().trim();
                if (!isValidEmail(email)) {
                    emailLayout.setError("Invalid email format");
                } else {
                    emailLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        guardianRegisterButton.setOnClickListener(view -> {
            if (validateFields()) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String selectedCountry = countrySpinner.getSelectedItem().toString();
                String countryCode = getCountryCode(selectedCountry);
                sendGuardianRegistrationRequest(email, password, countryCode, selectedCountry);

                // Log the registration details
                Log.d("Registration Details", "Email: " + emailField.getText().toString());
                Log.d("Registration Details", "Password: " + passwordField.getText().toString());
                Log.d("Registration Details", "Country: " + selectedCountry);
                Log.d("Registration Details", "Country Code: " + countryCode);

                // Proceed to FreeOrSubscribeActivity with the country code and other details
                Intent intent = new Intent(AddGuardianActivity.this, ChooseGuardianOrChild.class);
                intent.putExtra("EMAIL", emailField.getText().toString());
                intent.putExtra("PASSWORD", passwordField.getText().toString());
                intent.putExtra("COUNTRY", selectedCountry);
                intent.putExtra("COUNTRY_CODE", countryCode);
                startActivity(intent);
                finish();
            }
        });

        // Set a click listener to handle the back action
        backButton.setOnClickListener(v -> {
            finish(); // Closes this activity and returns to the previous one
        });

    }

    // Method to validate password match
    private void validatePasswordsMatch() {
        String password = passwordField.getText().toString().trim();
        String confirmPassword = reconfirmPasswordField.getText().toString().trim();
        if (!password.equals(confirmPassword)) {
            reconfirmPasswordLayout.setError("Passwords do not match");
        } else {
            reconfirmPasswordLayout.setErrorEnabled(false);
        }
    }
    // Email validation function using regex
    private boolean isValidEmail(String email) {
        // Regex explanation:
        // ^: Anchors the start of the string.
        // [A-Za-z0-9+_.-]+: Matches one or more alphanumeric characters or allowed symbols in the username.
        // @: The '@' symbol.
        // [A-Za-z0-9.-]+: Matches one or more alphanumeric characters, periods or hyphens in the domain name.
        // \.: Matches the literal period symbol before the domain suffix.
        // [A-Za-z]{2,}: Ensures the domain suffix (like .com, .org) is at least two characters long.
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    // Method to get the country code based on country name
    private String getCountryCode(String country) {
        // Check if the selected country is not the default "Select Country" item
        if (country.equals("Select Country")) {
            return null; // Invalid selection
        }

        // Extract country code from the string (e.g., "India (+91)" -> "+91")
        String countryCode = null;
        try {
            // Split the string based on the format "CountryName (+CountryCode)"
            String[] parts = country.split("\\("); // Split on "(" to separate the country code
            if (parts.length == 2) {
                countryCode = parts[1].replace(")", ""); // Remove the closing ")" and get the code
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return countryCode; // Return the extracted country code (e.g., "+91"
    }

    // Method to validate fields
    private boolean validateFields() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String reconfirmPassword = reconfirmPasswordField.getText().toString().trim();
        String country = countrySpinner.getSelectedItem().toString();

        // Validate Email Format
        if (!isValidEmail(email)) {
            emailLayout.setError("Invalid email format");
            return false; // Stop execution if email is invalid
        } else {
            emailLayout.setErrorEnabled(false); // Disable error if email is valid
        }

        // Validate Password
        boolean valid = true;
        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            valid = false;
        } else {
            passwordLayout.setErrorEnabled(false);
        }

        // Validate Reconfirm Password
        if (reconfirmPassword.isEmpty()) {
            reconfirmPasswordLayout.setError("Please confirm your password");
            valid = false;
        } else {
            reconfirmPasswordLayout.setErrorEnabled(false);
        }

        // Validate Password Match
        if (!password.equals(reconfirmPassword)) {
            reconfirmPasswordLayout.setError("Passwords do not match");
            valid = false;
        }

        // Validate Country Selection and Get Country Code
        String countryCode = getCountryCode(country);
        if (countryCode == null) {
            countryLayout.setError("Invalid country selected");
            valid = false;
        } else {
            countryLayout.setErrorEnabled(false);
        }

        return true;
    }
    private void sendGuardianRegistrationRequest(String email, String password, String countryCode, String countryName) {
        OkHttpClient client = new OkHttpClient();

        String paidToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("PAID_USER_TOKEN", null);
        // Prepare JSON body
        String json = "{"
                + "\"USER_NAME\":\"" + email + "\","
                + "\"USER_PASSWORD\":\"" + password + "\","
                + "\"COUNTRY_CODE\":\"" + countryCode + "\""
                + "}";

        RequestBody body = RequestBody.create(json, okhttp3.MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(GUARDIAN_REGISTER_URL)
                .addHeader("Authorization", "Bearer " + paidToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Log.e("API_ERROR", "Registration failed: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String token = jsonResponse.getString("USER_TOKEN");
                        String userId = jsonResponse.getString("USER_ID");

                        Log.d("Primary Register Token ","Primary Register Token: " + token);
                        // Store token in SharedPreferences
                        getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                .edit()
                                .putString(email + "_guardian_token", token)
                                .putString("PAID_USER_ID", userId)
                                .apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() ->
                            Log.e("API_ERROR", "Server returned error: " + response.code())
                    );
                }
            }
        });
    }

    // Generic method to toggle visibility of password fields
    private void togglePasswordVisibility(TextInputEditText passwordField) {
        // Check if the password is currently visible or hidden
        if (passwordField.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            // If hidden, show the password
            passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, getResources().getDrawable(R.drawable.hide_password), null); // Set the "Hide" icon
        } else {
            // If visible, hide the password
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, getResources().getDrawable(R.drawable.show_password), null); // Set the "Show" icon
        }

        // Move the cursor to the end of the text
        passwordField.setSelection(passwordField.getText().length());
    }
    private void sendGuardianDetailsToApi(String email, String password, String countryCode, String fullName,
                                          String role, String aadhar, String dob, String phone) {
        String paidToken = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("PAID_USER_TOKEN", null);
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("USER_NAME", email);
            jsonBody.put("USER_PASSWORD", password);
            jsonBody.put("COUNTRY_CODE", countryCode);
            jsonBody.put("USER_FULL_NAME", fullName);
            jsonBody.put("USER_ROLES", "PARENT");
            jsonBody.put("AADHAR_DETAILS", aadhar);
            jsonBody.put("DATE_OF_BIRTH", dob); // Ensure dob is in YYYY-MM-DD
            jsonBody.put("PHONE_NUMBER", phone);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://192.168.0.103:5000/user/add-guardian")
                .addHeader("Authorization", "Bearer " + paidToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AddGuardianActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("GuardianAPI", "Response: " + responseData);

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String token = jsonResponse.getString("USER_TOKEN");

                        // Save guardian token as username_guardian_token
                        getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                .edit()
                                .putString(email + "_guardian_token", token)
                                .apply();

                        runOnUiThread(() -> {
                            Toast.makeText(AddGuardianActivity.this, "Guardian added successfully!", Toast.LENGTH_SHORT).show();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(AddGuardianActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
