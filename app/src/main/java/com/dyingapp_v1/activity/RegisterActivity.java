package com.dyingapp_v1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.validator.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import com.dyingapp_v1.model.User;


public class RegisterActivity extends AppCompatActivity {

    private EditText edt_full_name, edt_email, edt_phone, edt_password, edt_confirm_password;
    private ImageView iv_toggle_password, iv_toggle_confirm_password, btn_back;
    private Button btn_create_account;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();
    }

    private void addViews() {
        edt_full_name = findViewById(R.id.edt_full_name);
        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_phone);
        edt_password = findViewById(R.id.edt_password);
        edt_confirm_password = findViewById(R.id.edt_confirm_password);
        iv_toggle_password = findViewById(R.id.iv_toggle_password);
        iv_toggle_confirm_password = findViewById(R.id.iv_toggle_confirm_password);
        btn_create_account = findViewById(R.id.btn_create_account);
        btn_back = findViewById(R.id.btn_back);
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> finish());

        // 👁 Toggle password visibility
        iv_toggle_password.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                edt_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                iv_toggle_password.setImageResource(R.drawable.ic_eye_open);
            } else {
                edt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                iv_toggle_password.setImageResource(R.drawable.ic_eye_open);
            }
            edt_password.setSelection(edt_password.length());
        });

        iv_toggle_confirm_password.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                edt_confirm_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                iv_toggle_confirm_password.setImageResource(R.drawable.ic_eye_open);
            } else {
                edt_confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                iv_toggle_confirm_password.setImageResource(R.drawable.ic_eye_open);
            }
            edt_confirm_password.setSelection(edt_confirm_password.length());
        });

        btn_create_account.setOnClickListener(this::do_register);
    }

    // 🔐 Register logic
// 🔐 Register logic
    public void do_register(View v) {
        String fullName = edt_full_name.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String phone = edt_phone.getText().toString().trim();
        String password = edt_password.getText().toString();
        String confirmPassword = edt_confirm_password.getText().toString();

        // ✅ Frontend validations
        if (fullName.isEmpty()) {
            edt_full_name.setError("Full name is required");
            return;
        }
        if (!Validator.isValidEmail(email)) {
            edt_email.setError("Invalid email format");
            return;
        }
        if (!Validator.isValidPhone(phone)) {
            edt_phone.setError("Invalid phone number");
            return;
        }
        if (!Validator.isValidPassword(password)) {
            edt_password.setError("Password must have 6+ chars, upper, lower, special");
            return;
        }
        if (!password.equals(confirmPassword)) {
            edt_confirm_password.setError("Passwords do not match");
            return;
        }

        // ✅ Build user object
        User newUser = new User(
                null,
                "CUSTOMER",
                fullName,
                email,
                phone,
                "",            // UserDOB
                password,
                "",            // UserAva
                "",            // UserAddressDefault
                ""             // UserAddressOther
        );

        // ✅ Convert user to JSON
        JSONObject body;
        try {
            body = newUser.toJson();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create JSON body", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Send request to backend
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConnector.REGISTER_URL,
                body,
                response -> {
                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    openLoginActivity(null);
                    finish();
                },
                error -> {
                    String errorMessage = "Registration failed";

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject jsonError = new JSONObject(responseBody);
                            errorMessage = jsonError.optString("message", errorMessage);
                            // Nếu server báo tài khoản đã tồn tại
                            if (errorMessage.toLowerCase().contains("already exists") || errorMessage.toLowerCase().contains("exists")) {
                                showAccountExistsDialog();
                            } else {
                                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            }                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected error format from server", Toast.LENGTH_LONG).show();
                        }
                    } else if (error.getMessage() != null) {
                        Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void showAccountExistsDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Account exists")
                .setMessage("Email already registered. Login Now?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Go to Login", (dialog, which) -> openLoginActivity(null))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void openLoginActivity(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);

    }
}