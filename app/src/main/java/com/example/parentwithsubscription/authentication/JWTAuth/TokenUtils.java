package com.example.parentwithsubscription.authentication.JWTAuth;

import android.util.Log;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class TokenUtils {

    // Method to decode and extract expiry date from JWT token
    public static Date getTokenExpiryDate(String jwtToken) {
        try {
            // Decode the token
            DecodedJWT decodedJWT = JWT.decode(jwtToken);
            // Extract the expiry date (exp claim)
            long expiryTimestamp = decodedJWT.getExpiresAt().getTime();
            Log.d("Expiry time", String.valueOf(expiryTimestamp));
            return new Date(expiryTimestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // In case of error, return null
        }
    }

    // Method to check if the token is expired
    public static boolean isTokenExpired(String jwtToken) {
        Date expiryDate = getTokenExpiryDate(jwtToken);
        Log.d("Expiry time", String.valueOf(expiryDate));
        if (expiryDate != null) {
            // Compare expiry date with the current date
            return expiryDate.before(new Date());
        }
        return true;  // If unable to decode token or get expiry, consider it expired
    }
}
