package com.dyingapp_v1.activity;
import com.dyingapp_v1.validator.Validator;

import static com.dyingapp_v1.connector.ApiConnector.FIND_EMAIL_URL;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView btn_back;
    private TextView edt_email;
    private Button btn_send_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

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
        edt_email = findViewById(R.id.edt_email);
        btn_send_link = findViewById(R.id.btn_send_link);
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> finish());

        btn_send_link.setOnClickListener(view -> {
            String email = edt_email.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Validator.isValidEmail(email)) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }


            // POST request to FIND_EMAIL_URL
            StringRequest request = new StringRequest(Request.Method.POST, FIND_EMAIL_URL,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            boolean success = json.getBoolean("success");
                            boolean exists = json.getBoolean("exists");

                            if (success && exists) {
                                Intent intent = new Intent(this, ResetPassword2Activity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(this, "Response parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        });
    }
}
