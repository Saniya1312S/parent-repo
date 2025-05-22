package com.example.parentwithsubscription;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.parentwithsubscription.authentication.JWTAuth.TokenUtils;
import com.example.parentwithsubscription.authentication.activity.RegisterActivity;
import com.example.parentwithsubscription.common.GlobalData;
import com.example.parentwithsubscription.common.URIConstants;
import com.example.parentwithsubscription.features.HomeActivity;
import com.example.parentwithsubscription.features.locationtracking.activity.LocationTrackingActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText emailField, passwordField;
    private TextInputLayout emailLayout, passwordLayout;
    private Button loginButton;
    private TextView registerLink;

    private static final String TAG = "MainActivity";
    private static final String LOGIN_URL = URIConstants.LOGIN_URL;
    private static final OkHttpClient client = new OkHttpClient();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        registerLink = findViewById(R.id.registerLink);

        emailField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = emailField.getText().toString().trim();
                if (!isValidEmail(email)) {
                    emailLayout.setError("Invalid email format");
                } else {
                    emailLayout.setErrorEnabled(false);
                }
            }
            public void afterTextChanged(Editable s) {}
        });

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

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (!isValidEmail(email)) {
                emailLayout.setError("Invalid email address");
                return;
            } else {
                emailLayout.setErrorEnabled(false);
            }

            if (password.isEmpty()) {
                passwordLayout.setError("Password is required");
                return;
            } else {
                passwordLayout.setErrorEnabled(false);
            }

            if (isUserSessionValid(email)) {
                Toast.makeText(this, "Login Successful (Local)", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeActivity.class));
            } else {
                reAuthenticate(email, password);
            }
        });

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private void togglePasswordVisibility(TextInputEditText passwordField) {
        if (passwordField.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.hide_password), null);
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.show_password), null);
        }
        passwordField.setSelection(passwordField.getText().length());
    }

    private void reAuthenticate(String email, String password) {
        String jsonBody = "{ \"USER_NAME\": \"" + email + "\", \"USER_PASSWORD\": \"" + password + "\" }";

        RequestBody body = RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonBody);

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d(TAG, "Response: " + responseData);

                    if (responseData.contains("tokens")) {
                        String accessToken = extractAccessToken(responseData);
                        String countryCode = extractCountryCode(responseData);
                        String userRole = extractUserRole(responseData);

                        Log.d(TAG, "Access Token: " + accessToken);
                        Log.d(TAG, "User Role: " + userRole);

                        saveUserSession(email, accessToken, countryCode, userRole);
                        GlobalData.setDeviceId(getDeviceIdForRole(userRole, countryCode));

                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Invalid credentials or user type", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    String errorMsg = parseErrorMessage(response.body().string());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private boolean isUserSessionValid(String email) {
        try {
            SharedPreferences prefs = getEncryptedPrefs();
            String storedUsername = prefs.getString("username", null);
            String token = prefs.getString("access_token", null);

            if (email.equals(storedUsername) && token != null && !TokenUtils.isTokenExpired(token)) {
                String countryCode = prefs.getString("country_code", null);
                String userRole = prefs.getString("user_role", null);
                GlobalData.setDeviceId(getDeviceIdForRole(userRole, countryCode));
                return true;
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveUserSession(String username, String accessToken, String countryCode, String userRole) {
        try {
            SharedPreferences sharedPreferences = getEncryptedPrefs();
            sharedPreferences.edit()
                    .putString("username", username)
                    .putString("access_token", accessToken)
                    .putString("country_code", countryCode)
                    .putString("user_role", userRole)
                    .apply();
            Log.d("Session", "Saving user role: " + userRole);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private SharedPreferences getEncryptedPrefs() throws GeneralSecurityException, IOException {
        String userPrefsFile = "guest_user_data";
        MasterKey masterKey = new MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                this,
                userPrefsFile,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    private String extractAccessToken(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            return jsonObject.getJSONObject("tokens").getString("access");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractCountryCode(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            return jsonObject.getString("COUNTRY_CODE");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractUserRole(String responseData) {
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            return jsonObject.getString("USER_ROLE");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String parseErrorMessage(String errorResponseData) {
        try {
            JSONObject jsonObject = new JSONObject(errorResponseData);
            return jsonObject.getString("Message");
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown error";
        }
    }

    private String getDeviceIdForRole(String role, String countryCode) {
        if (role == null) return "0000000000";
        if (role.equalsIgnoreCase("GUEST")) {
            return "+91".equals(countryCode) ? "9103456789" : "3103456789";
        } else if (role.equalsIgnoreCase("MASTER")) {
            return "3103456789";
        }
        return "0000000000";
    }
}
