package com.example.parentwithsubscription.authentication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parentwithsubscription.R;

public class PaymentOptionsActivity extends AppCompatActivity {

    private String subscriptionType;
    private String flowType = "subscribe"; // default flow

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_payment_options); // Reusing fragment XML

        Bundle data = getIntent().getExtras();
        if (data != null) {
            flowType = data.getString("flow_type", "subscribe");
        }

        if (data != null) {
            String planType = data.getString("subscription_type", "Unknown");
            String durationText = data.getString("subscription_duration", "Unknown");
            int days = data.getInt("subscription_days", 0);
            // Log.d("PAYMENT", "Plan: " + planType);
            // Log.d("PAYMENT", "Duration: " + durationText);
            // Log.d("PAYMENT", "Days: " + days);
        }

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            subscriptionType = intent.getStringExtra("subscription_type");
        }

        TextView title = findViewById(R.id.childcare_payment_title);
        title.setText("Selected Plan: " + (subscriptionType != null ? subscriptionType : "Unknown"));

        // RadioButtons
        RadioButton radioCard = findViewById(R.id.radio_card);
        RadioButton radioUPI = findViewById(R.id.radio_upi);
        RadioButton radioPayPal = findViewById(R.id.radio_paypal);

        // Input sections
        LinearLayout layoutCard = findViewById(R.id.layout_card_details);
        RadioGroup upiGroup = findViewById(R.id.layout_upi_options);
        LinearLayout layoutPayPal = findViewById(R.id.layout_paypal_details);

        EditText couponEditText = findViewById(R.id.edit_coupon_code);
        Button applyCouponButton = findViewById(R.id.btn_apply_coupon);

        Button payButton = findViewById(R.id.btn_pay);
        CheckBox autoRenewalCheckBox = findViewById(R.id.checkbox_autorenewal);

        // Initially hide all input sections
        layoutCard.setVisibility(View.GONE);
        upiGroup.setVisibility(View.GONE);
        layoutPayPal.setVisibility(View.GONE);

        View.OnClickListener radioClickListener = v -> {
            layoutCard.setVisibility(View.GONE);
            upiGroup.setVisibility(View.GONE);
            layoutPayPal.setVisibility(View.GONE);

            if (radioCard.isChecked()) {
                layoutCard.setVisibility(View.VISIBLE);
            } else if (radioUPI.isChecked()) {
                upiGroup.setVisibility(View.VISIBLE);
            } else if (radioPayPal.isChecked()) {
                layoutPayPal.setVisibility(View.VISIBLE);
            }
        };

        radioCard.setOnClickListener(radioClickListener);
        radioUPI.setOnClickListener(radioClickListener);
        radioPayPal.setOnClickListener(radioClickListener);

        applyCouponButton.setOnClickListener(v -> {
            String enteredCode = couponEditText.getText().toString().trim();
            Toast.makeText(this, enteredCode + " Coupon Applied!", Toast.LENGTH_SHORT).show();
        });

        payButton.setOnClickListener(v -> {
            // Data to be passed to the next screen
            Intent next = new Intent(this, AddMasterDetailsActivity.class);
            Bundle dataBundle = getIntent().getExtras(); // existing data
            if (dataBundle == null) {
                dataBundle = new Bundle();
            }

            boolean isValid = true;
            String couponCode = couponEditText.getText().toString().trim();

            // If Card Payment is selected
            if (radioCard.isChecked()) {
                EditText number = findViewById(R.id.card_number);
                EditText expiry = findViewById(R.id.card_expiry);
                EditText cvv = findViewById(R.id.card_cvv);
                EditText name = findViewById(R.id.card_name);
                EditText address = findViewById(R.id.card_address);

                String cardNum = number.getText().toString().trim();
                String cardExp = expiry.getText().toString().trim();
                String cardCvv = cvv.getText().toString().trim();
                String cardName = name.getText().toString().trim();
                String cardAddress = address.getText().toString().trim();

                if (cardNum.isEmpty() || cardExp.isEmpty() || cardCvv.isEmpty() || cardName.isEmpty() || cardAddress.isEmpty()) {
                    Toast.makeText(this, "Please fill in all card details", Toast.LENGTH_SHORT).show();
                    isValid = false;
                } else {
                    dataBundle.putString("payment_method", "Card");
                    dataBundle.putString("card_number", cardNum);
                    dataBundle.putString("card_expiry", cardExp);
                    dataBundle.putString("card_cvv", cardCvv);
                    dataBundle.putString("card_name", cardName);
                    dataBundle.putString("card_address", cardAddress);
                }
            }
            // If UPI Payment is selected
            else if (radioUPI.isChecked()) {
                int selectedUPIId = upiGroup.getCheckedRadioButtonId();
                if (selectedUPIId == -1) {
                    Toast.makeText(this, "Please select a UPI method", Toast.LENGTH_SHORT).show();
                    isValid = false;
                } else {
                    RadioButton selectedUPI = findViewById(selectedUPIId);
                    String selectedUPIName = selectedUPI.getText().toString();
                    dataBundle.putString("payment_method", "UPI");
                    dataBundle.putString("selected_upi", selectedUPIName);
                }
            }
            // If PayPal Payment is selected
            else if (radioPayPal.isChecked()) {
                EditText paypalEmail = findViewById(R.id.paypal_email);
                String paypalEmailStr = paypalEmail.getText().toString().trim();

                if (paypalEmailStr.isEmpty()) {
                    Toast.makeText(this, "Please enter PayPal email", Toast.LENGTH_SHORT).show();
                    isValid = false;
                } else {
                    dataBundle.putString("payment_method", "PayPal");
                    dataBundle.putString("paypal_email", paypalEmailStr);
                }
            }
            // If no payment method selected
            else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Add coupon code if entered
            if (!couponCode.isEmpty()) {
                dataBundle.putString("coupon_code", couponCode);
            }

            // Add auto-renewal status
            dataBundle.putBoolean("auto_renewal", autoRenewalCheckBox.isChecked());

            // Final step: conditional navigation
            if (isValid) {
                if ("change_plan".equals(flowType)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtras(dataBundle);
                    resultIntent.putExtra("payment_success", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    next.putExtras(dataBundle);
                    startActivity(next);
                }
            }
        });
    }
}













/*
package com.example.parentwithsubscription.authentication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parentwithsubscription.R;

public class PaymentOptionsActivity extends AppCompatActivity {

    private String subscriptionType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_payment_options); // Reusing fragment XML

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            subscriptionType = intent.getStringExtra("subscription_type");
        }

        TextView title = findViewById(R.id.childcare_payment_title);
        title.setText("Selected Plan: " + (subscriptionType != null ? subscriptionType : "Unknown"));

        // RadioButtons
        RadioButton radioCard = findViewById(R.id.radio_card);
        RadioButton radioUPI = findViewById(R.id.radio_upi);
        RadioButton radioPayPal = findViewById(R.id.radio_paypal);

        // Input sections
        LinearLayout layoutCard = findViewById(R.id.layout_card_details);
        RadioGroup upiGroup = findViewById(R.id.layout_upi_options);
        LinearLayout layoutPayPal = findViewById(R.id.layout_paypal_details);

        EditText couponEditText = findViewById(R.id.edit_coupon_code);
        Button applyCouponButton = findViewById(R.id.btn_apply_coupon);

        Button payButton = findViewById(R.id.btn_pay);

        // Initially hide all input sections
        layoutCard.setVisibility(View.GONE);
        upiGroup.setVisibility(View.GONE);
        layoutPayPal.setVisibility(View.GONE);

        View.OnClickListener radioClickListener = v -> {
            layoutCard.setVisibility(View.GONE);
            upiGroup.setVisibility(View.GONE);
            layoutPayPal.setVisibility(View.GONE);

            if (radioCard.isChecked()) {
                layoutCard.setVisibility(View.VISIBLE);
            } else if (radioUPI.isChecked()) {
                upiGroup.setVisibility(View.VISIBLE);
            } else if (radioPayPal.isChecked()) {
                layoutPayPal.setVisibility(View.VISIBLE);
            }
        };

        radioCard.setOnClickListener(radioClickListener);
        radioUPI.setOnClickListener(radioClickListener);
        radioPayPal.setOnClickListener(radioClickListener);

        applyCouponButton.setOnClickListener(v -> {
            String enteredCode = couponEditText.getText().toString().trim();
            Toast.makeText(this, enteredCode + " CouponApplied!", Toast.LENGTH_SHORT).show();
        });

        payButton.setOnClickListener(v -> {
            if (radioCard.isChecked()) {
                EditText number = findViewById(R.id.card_number);
                EditText expiry = findViewById(R.id.card_expiry);
                EditText cvv = findViewById(R.id.card_cvv);
                EditText name = findViewById(R.id.card_name);
                EditText address = findViewById(R.id.card_address);

                String cardNum = number.getText().toString().trim();
                String cardExp = expiry.getText().toString().trim();
                String cardCvv = cvv.getText().toString().trim();
                String cardName = name.getText().toString().trim();
                String cardAddress = address.getText().toString().trim();

*/
/*                // Optionally validate
                if (cardName.isEmpty() || cardAddress.isEmpty()) {
                    Toast.makeText(this, "Please enter your full name and billing address", Toast.LENGTH_SHORT).show();
                    return;
                }*//*


                Log.d("Card Payment", "Card Number: " + cardNum);
                Log.d("Card Payment", "Name: " + cardName);
                Log.d("Card Payment", "Address: " + cardAddress);
            } else if (radioUPI.isChecked()) {
                int selectedUPIId = upiGroup.getCheckedRadioButtonId();
                if (selectedUPIId == -1) {
                    Toast.makeText(this, "Please select a UPI method", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton selectedUPI = findViewById(selectedUPIId);
                Log.d("UPI Payment", "Selected UPI: " + selectedUPI.getText());
            } else if (radioPayPal.isChecked()) {
                EditText paypalEmail = findViewById(R.id.paypal_email);
                Log.d("PayPal Payment", "Email: " + paypalEmail.getText());
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            // Proceed to next screen (e.g., AddMasterDetailsActivity)
            Intent next = new Intent(this, AddMasterDetailsActivity.class);
            startActivity(next);
        });
    }
}

*/
