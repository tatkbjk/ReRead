package com.dyingapp_v1.activity;

import static com.dyingapp_v1.connector.ApiConnector.RESET_PASSWORD_URL;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.dyingapp_v1.validator.Validator;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPassword2Activity extends AppCompatActivity {

    private ImageView btn_toggle_new_password, btn_toggle_repeat_password, btn_back;
    private EditText edt_new_password, edt_repeat_password;
    private Button btn_submit;

    private String email; 

    private boolean isPasswordVisible = false;
    private boolean isrepeatPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();
    }

    private void addViews() {
        btn_back = findViewById(R.id.btn_back);
        btn_submit = findViewById(R.id.btn_submit);
        edt_new_password = findViewById(R.id.edt_new_password);
        edt_repeat_password = findViewById(R.id.edt_repeat_password);
        btn_toggle_new_password = findViewById(R.id.btn_toggle_new_password);
        btn_toggle_repeat_password = findViewById(R.id.btn_toggle_repeat_password);

        // nhận email từ Intent
        email = getIntent().getStringExtra("email");
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> finish());


        // 👁 Toggle password visibility
        btn_toggle_new_password.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                edt_new_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btn_toggle_new_password.setImageResource(R.drawable.ic_eye_open);
            } else {
                edt_new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btn_toggle_new_password.setImageResource(R.drawable.ic_eye_open);
            }
            edt_new_password.setSelection(edt_new_password.length());
        });

        btn_toggle_repeat_password.setOnClickListener(v -> {
            isrepeatPasswordVisible = !isrepeatPasswordVisible;
            if (isrepeatPasswordVisible) {
                edt_repeat_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btn_toggle_repeat_password.setImageResource(R.drawable.ic_eye_open);
            } else {
                edt_repeat_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btn_toggle_repeat_password.setImageResource(R.drawable.ic_eye_open);
            }
            edt_repeat_password.setSelection(edt_repeat_password.length());
        });

        btn_submit.setOnClickListener(v -> {
            String password = edt_new_password.getText().toString().trim();
            String repeatPassword = edt_repeat_password.getText().toString().trim();

            // Kiểm tra định dạng password
            if (!Validator.isValidPassword(password)) {
                edt_new_password.setError("Password must have 6+ chars, upper, lower, special");
                return;
            }

            // Kiểm tra repeat password
            if (!password.equals(repeatPassword)) {
                edt_repeat_password.setError("Passwords do not match");
                return;
            }

            // Gửi request cập nhật password
            StringRequest request = new StringRequest(Request.Method.POST, RESET_PASSWORD_URL,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            boolean success = json.getBoolean("success");
                            String message = json.getString("message");

                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                            if (success) {
                                Intent intent = new Intent(ResetPassword2Activity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } catch (Exception e) {
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        String errorMsg = "Unknown error";

                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                JSONObject data = new JSONObject(responseBody);
                                if (data.has("message")) {
                                    errorMsg = data.getString("message");
                                } else {
                                    errorMsg = responseBody;
                                }
                            } catch (Exception e) {
                                errorMsg = "Error parsing error response";
                            }
                        } else if (error.getMessage() != null) {
                            errorMsg = error.getMessage();
                        }

                        Toast.makeText(this, "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("newPassword", password);
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }
            };

            Volley.newRequestQueue(this).add(request);
        });
    }
}
