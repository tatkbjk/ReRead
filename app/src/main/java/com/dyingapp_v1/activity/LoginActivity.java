package com.dyingapp_v1.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.dyingapp_v1.util.UserSession;
import com.dyingapp_v1.validator.Validator;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText edt_email, edt_password;
    private ImageView btn_toggle_password, btn_back;
    private Button btn_login;

    private CheckBox cb_save_login;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();
    }

    private void addViews() {
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        btn_toggle_password = findViewById(R.id.btn_toggle_password);
        btn_back = findViewById(R.id.btn_back);
        btn_login = findViewById(R.id.btn_login);

        cb_save_login = findViewById(R.id.cb_save_login);

        // simplify sharedpref
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String savedEmail = prefs.getString("email", "");
        String savedPassword = prefs.getString("password", "");

        edt_email.setText(savedEmail);
        edt_password.setText(savedPassword);
        cb_save_login.setChecked(!savedEmail.isEmpty());


    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> finish());

        btn_toggle_password.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                edt_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btn_toggle_password.setImageResource(R.drawable.ic_eye_open);
            } else {
                edt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btn_toggle_password.setImageResource(R.drawable.ic_eye_open);
            }
            edt_password.setSelection(edt_password.length());
        });

        btn_login.setOnClickListener(this::do_login);
    }

    private void do_login(View view) {
        String email = edt_email.getText().toString().trim();
        String password = edt_password.getText().toString().trim();

        if (!Validator.isValidEmail(email)) {
            edt_email.setError("Invalid email format");
            return;
        }

        if (password.isEmpty()) {
            edt_password.setError("Password is required");
            return;
        }

        // ✅ Tạo JSON request body
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create login data", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Gửi request đến backend
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConnector.LOGIN_URL,
                body,
                response -> {
                    try {
                        if (response.getBoolean("success")) {

                            JSONObject user = response.getJSONObject("user");

                            String userId = user.optString("id"); // 👈 đổi từ "_id" sang "id"
                            UserSession.saveUserId(this, userId);
                            Log.d("LoginActivity", "✅ Returned userId = " + userId);

                            String name = user.optString("name");
                            String emailReturned = user.optString("email");
                            String avatar = user.optString("avatar");

                            Toast.makeText(this, "Welcome " + name + "!", Toast.LENGTH_SHORT).show();
                            // TODO: Lưu session bằng SharedPreferences nếu cần

                            if (cb_save_login.isChecked()) {
                                getSharedPreferences("loginPrefs", MODE_PRIVATE)
                                        .edit()
                                        .putString("email", email)
                                        .putString("password", password)
                                        .apply();
                            }

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();


                        
        
                            } else {
                                showLoginFailedDialog();
                            }
                                
                        
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Invalid server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMsg = "Login failed";

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject jsonError = new JSONObject(responseBody);
                            errorMsg = jsonError.optString("message", errorMsg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (error.getMessage() != null) {
                        errorMsg = error.getMessage();
                    }

                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void showLoginFailedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage("Account not found. Register new?")
                .setPositiveButton("Register", (dialog, which) -> openRegisterActivity(null))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }


    public void openRegisterActivity(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openResetPasswordActivity(View view) {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }
}
