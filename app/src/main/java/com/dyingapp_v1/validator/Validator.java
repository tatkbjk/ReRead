package com.dyingapp_v1.validator;

import android.util.Patterns;

public class Validator {

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^(\\+84|0)\\d{9}$");
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) return false;
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).+$");
    }
}
