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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout emailLayout, passwordLayout, reconfirmPasswordLayout, countryLayout;
    private TextInputEditText emailField, passwordField, reconfirmPasswordField;
    private Spinner countrySpinner;
    private Button freeRegisterButton, subRegisterButton;
    private TextView errorText;
    private ImageButton backButton;

    private String REGISTER_URL = URIConstants.REGISTER_URL;

    private static final String TAG = "RegisterActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        reconfirmPasswordLayout = findViewById(R.id.reconfirmPasswordLayout);
        countryLayout = findViewById(R.id.countryLayout);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        reconfirmPasswordField = findViewById(R.id.reconfirmPassword);
        countrySpinner = findViewById(R.id.countrySpinner);
        freeRegisterButton = findViewById(R.id.freeRegisterButton);
        backButton = findViewById(R.id.backButton);
        subRegisterButton = findViewById(R.id.subRegisterButton);

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

        freeRegisterButton.setOnClickListener(view -> {
            if (validateFields()) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String selectedCountry = countrySpinner.getSelectedItem().toString();
                String countryCode = getCountryCode(selectedCountry);

                try {
                    sendRegistrationRequest(email, password, countryCode, selectedCountry);
                } catch (Exception e) {
                    Log.e("Registration Error", "Failed to send registration request", e);
                    Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Log the registration details
                Log.d("Registration Details", "Email: " + email);
                Log.d("Registration Details", "Password: " + password);
                Log.d("Registration Details", "Country: " + selectedCountry);
                Log.d("Registration Details", "Country Code: " + countryCode);

                // Proceed to MainActivity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                intent.putExtra("COUNTRY", selectedCountry);
                intent.putExtra("COUNTRY_CODE", countryCode);
                startActivity(intent);
                finish();
            }
        });

        subRegisterButton.setOnClickListener(view -> {
            if (validateFields()) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String selectedCountry = countrySpinner.getSelectedItem().toString();
                String countryCode = getCountryCode(selectedCountry);

                try {
                    sendRegistrationRequest(email, password, countryCode, selectedCountry);
                } catch (Exception e) {
                    Log.e("Registration Error", "Failed to send registration request", e);
                    Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Log the registration details
                Log.d("Registration Details", "Email: " + email);
                Log.d("Registration Details", "Password: " + password);
                Log.d("Registration Details", "Country: " + selectedCountry);
                Log.d("Registration Details", "Country Code: " + countryCode);

                // Proceed to SubscriptionOptionsActivity
                Intent intent = new Intent(RegisterActivity.this, SubscriptionOptionsActivity.class);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                intent.putExtra("COUNTRY", selectedCountry);
                intent.putExtra("COUNTRY_CODE", countryCode);
                startActivity(intent);
            }
        });

        // Set a click listener to handle the back action
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
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
    private void sendRegistrationRequest(String email, String password, String countryCode, String countryName) {
        OkHttpClient client = new OkHttpClient();

        // Prepare JSON body
        String json = "{"
                + "\"USER_NAME\":\"" + email + "\","
                + "\"USER_PASSWORD\":\"" + password + "\","
                + "\"COUNTRY_CODE\":\"" + countryCode + "\""
                + "}";

        RequestBody body = RequestBody.create(json, okhttp3.MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(REGISTER_URL)
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

                        Log.d("Primary Register Token ","Primary Register Token: " + token);
                        // Store token in SharedPreferences
                        getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("USER_TOKEN", token)
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












/*
package com.example.parent.authentication.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parent.MainActivity;
import com.example.parent.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText parentNameField, parentPhoneField, parentAddressField, childNameField, childPhoneField, aadharNumberField, dobField;
    private Button nextButton, registerButton;
    private LinearLayout socialMediaOptionsLayout;
    private CheckBox socialMediaPermission, whatsappCheck, facebookCheck, instagramCheck, snapchatCheck, twitterCheck, telegramCheck;
    private TextView loginLink, loginLink1;
    private ImageButton backButton;

    // Input Layouts for error handling
    private TextInputLayout parentNameLayout, parentPhoneLayout, parentAddressLayout, childNameLayout, childPhoneLayout, aadharNumberLayout, dobLayout;

    private int selectedYear, selectedMonth, selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        parentNameField = findViewById(R.id.parentName);
        parentPhoneField = findViewById(R.id.parentPhone);
        parentAddressField = findViewById(R.id.parentAddress);
        childNameField = findViewById(R.id.childName);
        childPhoneField = findViewById(R.id.childPhone);
        aadharNumberField = findViewById(R.id.childAadhar);
        dobField = findViewById(R.id.childDob); // New DOB field
        nextButton = findViewById(R.id.nextButton);
        registerButton = findViewById(R.id.registerButton);
        socialMediaPermission = findViewById(R.id.socialMediaPermission);
        socialMediaOptionsLayout = findViewById(R.id.socialMediaOptions);
        whatsappCheck = findViewById(R.id.whatsapp);
        facebookCheck = findViewById(R.id.facebook);
        instagramCheck = findViewById(R.id.instagram);
        snapchatCheck = findViewById(R.id.snapchat);
        twitterCheck = findViewById(R.id.twitter);
        telegramCheck = findViewById(R.id.telegram);
        loginLink = findViewById(R.id.loginLink);
        loginLink1 = findViewById(R.id.loginLink1);
        backButton = findViewById(R.id.backButton);

        // Input Layouts for validation errors
        parentNameLayout = findViewById(R.id.parentNameLayout);
        parentPhoneLayout = findViewById(R.id.parentPhoneLayout);
        parentAddressLayout = findViewById(R.id.parentAddressLayout);
        childNameLayout = findViewById(R.id.childNameLayout);
        childPhoneLayout = findViewById(R.id.childPhoneLayout);
        aadharNumberLayout = findViewById(R.id.childAadharLayout);
        dobLayout = findViewById(R.id.childDobLayout);

        // Handle Next Button (Parent Details to Child Details)
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate parent details
                String parentName = parentNameField.getText().toString().trim();
                String parentPhone = parentPhoneField.getText().toString().trim();
                String parentAddress = parentAddressField.getText().toString().trim();

                if (parentName.isEmpty()) {
                    parentNameLayout.setError("Parent name is required");
                } else {
                    parentNameLayout.setErrorEnabled(false);
                }

                if (parentPhone.isEmpty()) {
                    parentPhoneLayout.setError("Parent phone number is required");
                } else if (parentPhone.length() != 10) {
                    parentPhoneLayout.setError("Phone number must be 10 digits");
                } else {
                    parentPhoneLayout.setErrorEnabled(false);
                }

                if (parentAddress.isEmpty()) {
                    parentAddressLayout.setError("Parent address is required");
                } else {
                    parentAddressLayout.setErrorEnabled(false);
                }

                if (!parentName.isEmpty() && !parentPhone.isEmpty() && parentPhone.length() == 10 && !parentAddress.isEmpty()) {
                    // Hide the parent section and show the child section
                    findViewById(R.id.parentLayout).setVisibility(View.GONE);
                    findViewById(R.id.childLayout).setVisibility(View.VISIBLE);
                }
            }
        });

        // Handle Social Media Permission Checkbox
        socialMediaPermission.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                socialMediaOptionsLayout.setVisibility(View.VISIBLE);
            } else {
                socialMediaOptionsLayout.setVisibility(View.GONE);
            }
        });

        // Handle Register Button (Final validation)
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate child details
                String childName = childNameField.getText().toString().trim();
                String childPhone = childPhoneField.getText().toString().trim();
                String aadharNumber = aadharNumberField.getText().toString();
                String dob = dobField.getText().toString().trim();

                if (childName.isEmpty()) {
                    childNameLayout.setError("Child name is required");
                } else {
                    childNameLayout.setErrorEnabled(false);
                }

                if (childPhone.isEmpty()) {
                    childPhoneLayout.setError("Child phone number is required");
                } else if (childPhone.length() != 10) {
                    childPhoneLayout.setError("Phone number must be 10 digits");
                } else {
                    childPhoneLayout.setErrorEnabled(false);
                }

                if (aadharNumber.isEmpty() || !aadharNumber.matches("\\d{16}")) {
                    aadharNumberLayout.setError("Please enter a valid 16-digit Aadhar number");
                } else {
                    aadharNumberLayout.setErrorEnabled(false);
                }

                if (dob.isEmpty()) {
                    dobLayout.setError("Please select a valid Date of Birth");
                } else {
                    dobLayout.setErrorEnabled(false);
                }

                // If social media is selected, check the selected apps
                if (socialMediaPermission.isChecked() && (!whatsappCheck.isChecked() && !facebookCheck.isChecked() && !instagramCheck.isChecked() && !snapchatCheck.isChecked() && !twitterCheck.isChecked() && !telegramCheck.isChecked())) {
                    Toast.makeText(RegisterActivity.this, "Please select at least one social media app", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If everything is valid, show success message and return to login
                if (!childName.isEmpty() && !childPhone.isEmpty() && childPhone.length() == 10 && !aadharNumber.isEmpty() && aadharNumber.matches("\\d{16}") && !dob.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close the register activity
                }
            }
        });

        // Handle the login link click
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login activity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        loginLink1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the login activity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set a click listener to handle the back action
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the child details section and show the parent details section
                findViewById(R.id.childLayout).setVisibility(View.GONE);
                findViewById(R.id.parentLayout).setVisibility(View.VISIBLE);
            }
        });

        // Set up the DatePicker dialog for the Date of Birth
        dobField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                Calendar calendar = Calendar.getInstance();
                selectedYear = calendar.get(Calendar.YEAR);
                selectedMonth = calendar.get(Calendar.MONTH);
                selectedDay = calendar.get(Calendar.DAY_OF_MONTH);

                // Show the DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            // Set the selected date in the EditText
                            dobField.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }, selectedYear, selectedMonth, selectedDay);
                datePickerDialog.show();
            }
        });

        // Add TextWatcher to restrict phone number to 10 digits
        parentPhoneField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 10) {
                    parentPhoneField.setText(charSequence.subSequence(0, 10));
                    parentPhoneField.setSelection(10);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        childPhoneField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 10) {
                    childPhoneField.setText(charSequence.subSequence(0, 10));
                    childPhoneField.setSelection(10);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Add TextWatcher to restrict Aadhar number to 16 digits
        aadharNumberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 16) {
                    aadharNumberField.setText(charSequence.subSequence(0, 16));
                    aadharNumberField.setSelection(16);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }
}











*/
/*
package com.example.loginandregisteruiforparent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText parentNameField, parentPhoneField, parentAddressField, childNameField, childPhoneField;
    private Button nextButton, registerButton;
    private LinearLayout socialMediaOptionsLayout;
    private CheckBox socialMediaPermission, whatsappCheck, facebookCheck, instagramCheck, snapchatCheck, twitterCheck, telegramCheck;
    private TextView loginLink, loginLink1;
    private ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        parentNameField = findViewById(R.id.parentName);
        parentPhoneField = findViewById(R.id.parentPhone);
        parentAddressField = findViewById(R.id.parentAddress);
        childNameField = findViewById(R.id.childName);
        childPhoneField = findViewById(R.id.childPhone);
        nextButton = findViewById(R.id.nextButton);
        registerButton = findViewById(R.id.registerButton);
        socialMediaPermission = findViewById(R.id.socialMediaPermission);
        socialMediaOptionsLayout = findViewById(R.id.socialMediaOptions);
        whatsappCheck = findViewById(R.id.whatsapp);
        facebookCheck = findViewById(R.id.facebook);
        instagramCheck = findViewById(R.id.instagram);
        snapchatCheck = findViewById(R.id.snapchat);
        twitterCheck = findViewById(R.id.twitter);
        telegramCheck = findViewById(R.id.telegram);
        loginLink = findViewById(R.id.loginLink);
        loginLink1 = findViewById(R.id.loginLink1);
        backButton = findViewById(R.id.backButton);
        // Handle Next Button (Parent Details to Child Details)
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate parent details
                if (parentNameField.getText().toString().isEmpty() || parentPhoneField.getText().toString().isEmpty() || parentAddressField.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all parent details", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Hide the parent section and show the child section
                findViewById(R.id.parentLayout).setVisibility(View.GONE);
                findViewById(R.id.childLayout).setVisibility(View.VISIBLE);
            }
        });

        // Handle Social Media Permission Checkbox
        socialMediaPermission.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                socialMediaOptionsLayout.setVisibility(View.VISIBLE);
            } else {
                socialMediaOptionsLayout.setVisibility(View.GONE);
            }
        });

        // Handle Register Button (Final validation)
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate child details
                if (childNameField.getText().toString().isEmpty() || childPhoneField.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all child details", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If social media is selected, check the selected apps
                if (socialMediaPermission.isChecked() && (!whatsappCheck.isChecked() && !facebookCheck.isChecked() && !instagramCheck.isChecked() && !snapchatCheck.isChecked() && !twitterCheck.isChecked() && !telegramCheck.isChecked())) {
                    Toast.makeText(RegisterActivity.this, "Please select at least one social media app", Toast.LENGTH_SHORT).show();
                    return;
                }

                // If everything is valid, show success message and return to login
                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the register activity
            }
        });

        // Handle the login link click
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the register activity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        loginLink1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the register activity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set a click listener to handle the back action
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();  // Go to the previous screen when back button is pressed
            }
        });
    }
}
*/

