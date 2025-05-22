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
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.parentwithsubscription.MainActivity;
import com.example.parentwithsubscription.R;
import com.example.parentwithsubscription.common.URIConstants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

public class GuestRegisterActivity extends AppCompatActivity {

    private TextInputLayout emailLayout, passwordLayout, reconfirmPasswordLayout, countryLayout;
    private TextInputEditText emailField, passwordField, reconfirmPasswordField;
    private Spinner countrySpinner;
    private Button registerButton;
    private TextView subscribeRedirect, errorText;
    private ImageButton backButton;

    private String GUEST_REGISTER_URL = URIConstants.REGISTER_URL;
    private static final String TAG = "GuestRegisterActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_register);

        // Initialize views
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        reconfirmPasswordLayout = findViewById(R.id.reconfirmPasswordLayout);
        countryLayout = findViewById(R.id.countryLayout);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        reconfirmPasswordField = findViewById(R.id.reconfirmPassword);
        countrySpinner = findViewById(R.id.countrySpinner);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);
        subscribeRedirect = findViewById(R.id.subscribeRedirect);

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

        // Register Button Click
        registerButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String reconfirmPassword = reconfirmPasswordField.getText().toString().trim();
            String country = countrySpinner.getSelectedItem().toString();

            // Validate Email Format
            if (!isValidEmail(email)) {
                emailLayout.setError("Invalid email format");
                return; // Stop execution if email is invalid
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

            // If all fields are valid, proceed to register
            if (valid) {
                try {
                    // Prepare the JSON payload
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("USER_NAME", email); // Send email as 'USER_NAME'
                    jsonBody.put("USER_PASSWORD", password); // Send password as 'USER_PASSWORD'
                    jsonBody.put("COUNTRY_CODE", countryCode); // Send the country code, not the name

                    // Send the request to the server
                    sendRegistrationRequest(jsonBody);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GuestRegisterActivity.this, "Error preparing registration data", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Login redirect
        subscribeRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(GuestRegisterActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Set a click listener to handle the back action
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(GuestRegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
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


    private void sendRegistrationRequest(JSONObject jsonBody) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

        Request request = new Request.Builder()
                .url(GUEST_REGISTER_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle network failure
                Log.e("On registration request Fail", e.toString());
                runOnUiThread(() -> Toast.makeText(GuestRegisterActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Log response code and body for debugging
                int responseCode = response.code();
                String responseData = response.body() != null ? response.body().string() : "";

                Log.d(TAG, "Response Code: " + responseCode);
                Log.d(TAG, "Response Body: " + responseData);

                // Handle success (200 OK)
                if (responseCode == 200) {
                    try {
                        // Log the start of success handling
                        Log.d(TAG, "Registration Success. Handling response...");

                        // Parse the response
                        JSONObject jsonResponse = new JSONObject(responseData);

                        // Check for "Message" indicating successful registration
                        if (jsonResponse.optString("Message").equalsIgnoreCase("User created successfully")) {
                            Log.d(TAG, "User created successfully - Navigating to MainActivity");

                            runOnUiThread(() -> {
                                Toast.makeText(GuestRegisterActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();

                                // Retrieve data to store in EncryptedSharedPreferences
                                String email = emailField.getText().toString().trim();
                                String password = passwordField.getText().toString().trim();
                                String country = countrySpinner.getSelectedItem().toString();

                                Log.d("Register Country", country);
                                // Call method to store data in EncryptedSharedPreferences
  /*                              try {
                                    storeInEncryptedSharedPreferences(email, password, country);
                                } catch (GeneralSecurityException | IOException e) {
                                    e.printStackTrace();
                                    runOnUiThread(() -> Toast.makeText(GuestRegisterActivity.this, "Error storing data locally", Toast.LENGTH_SHORT).show());
                                }*/

                                // Navigate to MainActivity
                                Intent intent = new Intent(GuestRegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the stack
                                startActivity(intent);
                                finish(); // Close the registration activity
                            });
                        } else {
                            Log.d(TAG, "Unexpected Message received from server: " + jsonResponse.optString("Message"));
                            runOnUiThread(() -> {
                                Toast.makeText(GuestRegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing server response: " + e.getMessage(), e);
                        runOnUiThread(() -> Toast.makeText(GuestRegisterActivity.this, "Error parsing server response", Toast.LENGTH_SHORT).show());
                    }
                }
                // Handle email already exists error (409 Conflict)
                else if (responseCode == 409) {
                    Log.d(TAG, "Email already exists. Responding with Toast.");
                    runOnUiThread(() -> {
                        Toast.makeText(GuestRegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                    });
                }
                // Handle other non-200 codes (like 400, 500)
                else {
                    Log.d(TAG, "Registration failed with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(GuestRegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    });
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

    private void storeInEncryptedSharedPreferences(String username, String password, String country) throws GeneralSecurityException, IOException {
        // Create a unique key for the user based on their username
        String userPrefsFile = "guest_user_data_" + username;

        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        // Create EncryptedSharedPreferences instance with unique key for each user
        EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                userPrefsFile,  // Use unique file name based on username
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        // Store the data in EncryptedSharedPreferences
        sharedPreferences.edit()
                .putString("username", username)
                .putString("password", password)
                .putString("country", country)
                .apply();

        Log.d(TAG, "Data successfully stored in EncryptedSharedPreferences.");
    }

    private void clearEncryptedSharedPreferences() throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                "user_data",
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
        sharedPreferences.edit().clear().apply();
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
}









/*package com.example.parent.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.text.Editable;
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
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.parent.MainActivity;
import com.example.parent.R;
import com.example.parent.common.URIConstants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.GeneralSecurityException;
public class GuestRegisterActivity extends AppCompatActivity {

    private TextInputLayout usernameLayout, passwordLayout, phoneLayout, reconfirmPasswordLayout, countryLayout;
    private TextInputEditText usernameField, passwordField, phoneField, reconfirmPasswordField;
    private Spinner countrySpinner;
    private Button registerButton;
    private TextView subscribeRedirect;
    private ImageButton backButton;

    private String GUEST_REGISTER_URL = URIConstants.GUEST_REGISTER_URL;

    private static final String TAG = "GuestRegisterActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_register);

        // Initialize views
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        reconfirmPasswordLayout = findViewById(R.id.reconfirmPasswordLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        countryLayout = findViewById(R.id.countryLayout);
        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        reconfirmPasswordField = findViewById(R.id.reconfirmPassword);
        phoneField = findViewById(R.id.phoneNumber);
        countrySpinner = findViewById(R.id.countrySpinner);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);
        subscribeRedirect = findViewById(R.id.subscribeRedirect);

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
            // Only validate passwords when the field loses focus (user stops typing)
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
                // Clear the error if the passwords match
                validatePasswordsMatch();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Register Button Click
        registerButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String reconfirmPassword = reconfirmPasswordField.getText().toString().trim();
            String phone = phoneField.getText().toString().trim();
            String country = countrySpinner.getSelectedItem().toString();

            // Validate Username
            boolean valid = true;
            if (username.isEmpty()) {
                usernameLayout.setError("Username is required");
                valid = false;
            } else {
                usernameLayout.setErrorEnabled(false);
            }

            // Validate Password
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

            // Validate Phone Number
            if (phone.isEmpty()) {
                phoneLayout.setError("Phone number is required");
                valid = false;
            } else if (phone.length() != 10) {
                phoneLayout.setError("Phone number must be 10 digits");
                valid = false;
            } else {
                phoneLayout.setErrorEnabled(false);
            }

            // Validate Country Selection
            if (country.equals("Select Country")) {  // Assuming "Select Country" is the default value
                countryLayout.setError("Please select a country");
                valid = false;
            } else {
                countryLayout.setErrorEnabled(false);
            }

            // If all fields are valid, proceed to register
            if (valid) {
                try {
                    // Store data in EncryptedSharedPreferences including country
                    storeInEncryptedSharedPreferences(username, password, phone, country);

                    // Simulate successful registration
                    Toast.makeText(GuestRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                    // Log the details (For debugging purposes)
                    Log.d(TAG, "Username: " + username);
                    Log.d(TAG, "Password: " + password);
                    Log.d(TAG, "Phone: " + phone);
                    Log.d(TAG, "Country: " + country);

                    // Redirect to login page after successful registration
                    Intent intent = new Intent(GuestRegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (GeneralSecurityException | IOException e) {
                    Log.e(TAG, "Error storing data securely: " + e.getMessage());
                    Toast.makeText(GuestRegisterActivity.this, "Registration failed! Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Login redirect
        subscribeRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(GuestRegisterActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Set a click listener to handle the back action
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(GuestRegisterActivity.this, MainActivity.class);
            startActivity(intent);
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

    private void storeInEncryptedSharedPreferences(String username, String password, String phone, String country) throws GeneralSecurityException, IOException {
        // Create a unique key for the user based on their username
        String userPrefsFile = "user_data_" + username;

        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        // Create EncryptedSharedPreferences instance with unique key for each user
        EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                userPrefsFile,  // Use unique file name based on username
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        // Store the data in EncryptedSharedPreferences
        sharedPreferences.edit()
                .putString("username", username)
                .putString("password", password)
                .putString("phone", phone)
                .putString("country", country)
                .apply();

        Log.d(TAG, "Data successfully stored in EncryptedSharedPreferences.");
    }

    private void clearEncryptedSharedPreferences() throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                "user_data",
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
        sharedPreferences.edit().clear().apply();
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
}*/












/*
package com.example.parent.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.text.Editable;
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
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.parent.MainActivity;
import com.example.parent.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GuestRegisterActivity extends AppCompatActivity {

    private TextInputLayout usernameLayout, passwordLayout, phoneLayout, reconfirmPasswordLayout, countryLayout;
    private TextInputEditText usernameField, passwordField, phoneField, reconfirmPasswordField;
    private Spinner countrySpinner;
    private Button registerButton;
    private TextView subscribeRedirect;
    private ImageButton backButton;

    private static final String TAG = "GuestRegisterActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_register);

        // Initialize views
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        reconfirmPasswordLayout = findViewById(R.id.reconfirmPasswordLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        countryLayout = findViewById(R.id.countryLayout);
        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        reconfirmPasswordField = findViewById(R.id.reconfirmPassword);
        phoneField = findViewById(R.id.phoneNumber);
        countrySpinner = findViewById(R.id.countrySpinner);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);
        subscribeRedirect = findViewById(R.id.subscribeRedirect);

        // Populate country spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        // Set a touch listener to detect drawable click for password visibility
        // Set OnTouchListener for passwordField to toggle visibility
        passwordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableWidth = passwordField.getCompoundDrawables()[2].getBounds().width();
                int drawableLeft = passwordField.getWidth() - drawableWidth;
                if (event.getRawX() >= drawableLeft) {
                    togglePasswordVisibility(passwordField);  // Use the generic method
                    return true;
                }
            }
            return false;
        });

// Set OnTouchListener for reconfirmPasswordField to toggle visibility
        reconfirmPasswordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableWidth = reconfirmPasswordField.getCompoundDrawables()[2].getBounds().width();
                int drawableLeft = reconfirmPasswordField.getWidth() - drawableWidth;
                if (event.getRawX() >= drawableLeft) {
                    togglePasswordVisibility(reconfirmPasswordField);  // Use the generic method
                    return true;
                }
            }
            return false;
        });

*/
/*        // Add TextWatcher to confirm password field to validate passwords match
        reconfirmPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                String password = passwordField.getText().toString().trim();
                String confirmPassword = charSequence.toString().trim();

                if (!confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
                    reconfirmPasswordLayout.setError("Passwords do not match");
                } else {
                    reconfirmPasswordLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });*//*

        // Add OnFocusChangeListener to confirm password field to validate passwords match
        reconfirmPasswordField.setOnFocusChangeListener((v, hasFocus) -> {
            // Only validate passwords when the field loses focus (user stops typing)
            if (!hasFocus) {
                String password = passwordField.getText().toString().trim();
                String confirmPassword = reconfirmPasswordField.getText().toString().trim();

                // Check if the passwords match
                if (!confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
                    reconfirmPasswordLayout.setError("Passwords do not match");
                } else {
                    reconfirmPasswordLayout.setErrorEnabled(false);
                }
            }
        });


        // Register Button Click
        registerButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String reconfirmPassword = reconfirmPasswordField.getText().toString().trim();
            String phone = phoneField.getText().toString().trim();
            String country = countrySpinner.getSelectedItem().toString();

            // Validate Username
            if (username.isEmpty()) {
                usernameLayout.setError("Username is required");
            } else {
                usernameLayout.setErrorEnabled(false);
            }

            // Validate Password
            if (password.isEmpty()) {
                passwordLayout.setError("Password is required");
            } else {
                passwordLayout.setErrorEnabled(false);
            }

            // Validate Reconfirm Password (handled by TextWatcher)
            if (reconfirmPassword.isEmpty()) {
                reconfirmPasswordLayout.setError("Please confirm your password");
            } else {
                reconfirmPasswordLayout.setErrorEnabled(false);
            }

            // Validate Phone Number
            if (phone.isEmpty()) {
                phoneLayout.setError("Phone number is required");
            } else if (phone.length() != 10) {
                phoneLayout.setError("Phone number must be 10 digits");
            } else {
                phoneLayout.setErrorEnabled(false);
            }

            // Validate Country Selection
            if (country.equals("Select Country")) {  // Assuming "Select Country" is the default value
                countryLayout.setError("Please select a country");
            } else {
                countryLayout.setErrorEnabled(false);
            }

            // If all fields are valid, proceed to register
            if (!username.isEmpty() && !password.isEmpty() && password.equals(reconfirmPassword) &&
                    phone.length() == 10 && !country.equals("Select Country")) {
                try {
                    // Store data in EncryptedSharedPreferences including country
                    storeInEncryptedSharedPreferences(username, password, phone, country);

                    // Simulate successful registration
                    Toast.makeText(GuestRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                    // Log the details (For debugging purposes)
                    Log.d(TAG, "Username: " + username);
                    Log.d(TAG, "Password: " + password);
                    Log.d(TAG, "Phone: " + phone);
                    Log.d(TAG, "Country: " + country);

                    // Redirect to login page after successful registration
                    Intent intent = new Intent(GuestRegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (GeneralSecurityException | IOException e) {
                    Log.e(TAG, "Error storing data securely: " + e.getMessage());
                    Toast.makeText(GuestRegisterActivity.this, "Registration failed! Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Login redirect
        subscribeRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(GuestRegisterActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Set a click listener to handle the back action
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(GuestRegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

*/
/*    private void togglePasswordVisibility() {
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
    }*//*


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



    private void storeInEncryptedSharedPreferences(String username, String password, String phone, String country) throws GeneralSecurityException, IOException {
        // Create a unique key for the user based on their username
        String userPrefsFile = "user_data_" + username;

        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        // Create EncryptedSharedPreferences instance with unique key for each user
        EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                userPrefsFile,  // Use unique file name based on username
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        // Store the data in EncryptedSharedPreferences
        sharedPreferences.edit()
                .putString("username", username)
                .putString("password", password)
                .putString("phone", phone)
                .putString("country", country)
                .apply();

        Log.d(TAG, "Data successfully stored in EncryptedSharedPreferences.");
    }

    private void clearEncryptedSharedPreferences() throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        EncryptedSharedPreferences sharedPreferences = (EncryptedSharedPreferences) EncryptedSharedPreferences.create(
                "user_data",
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
        sharedPreferences.edit().clear().apply();
    }
}
*/