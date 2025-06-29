package com.dyingapp_v1.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterEventActivity extends AppCompatActivity {
    Button btnRegister;
    EditText edtNote;
    String userID;
    String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();

        userID = UserSession.getUserId(this); // 🔐 Lấy từ session
        eventID = getIntent().getStringExtra("eventId"); // 📦 Lấy từ intent
    }

    private void addViews() {
        btnRegister = findViewById(R.id.btnRegister);
        edtNote = findViewById(R.id.edtNote);
        btnRegister.setOnClickListener(v -> registerForEvent());
    }

    private void registerForEvent() {
        String note = edtNote.getText().toString().trim();

        if (userID == null || eventID == null) {
            Toast.makeText(this, "Missing userID or eventID.", Toast.LENGTH_SHORT).show();
            return;
        }

        String registrationID = generateRegistrationID(); // 🆕 Tạo ID

        JSONObject body = new JSONObject();
        try {
            body.put("RegistrationID", registrationID);
            body.put("EventID", eventID);
            body.put("UserID", userID);
            body.put("Note", note);
            body.put("Joined", 0);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ApiConnector.REGISTER_EVENT_URL,
                body,
                response -> {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Registration unsuccessful!", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );


        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // 🆕 Hàm tạo RegistrationID dạng R1742112672895
    private String generateRegistrationID() {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 90 + 10); // từ 10 đến 99
        return "R" + random + timestamp;
    }
}
