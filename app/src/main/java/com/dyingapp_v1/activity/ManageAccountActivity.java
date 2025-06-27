package com.dyingapp_v1.activity;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ManageAccountActivity extends AppCompatActivity {

    private ImageView user_ava, ivToggle;
    private EditText edtFullname, edtPassword, edtMail, edtAddress, edtDOB;
    private AutoCompleteTextView autoGender;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_account);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String userId = UserSession.getUserId(this);
        Toast.makeText(this, "UserId = " + userId, Toast.LENGTH_SHORT).show();

        user_ava = findViewById(R.id.user_ava);
        edtFullname = findViewById(R.id.edtFullname);
        edtPassword = findViewById(R.id.edtPassword);
        edtMail = findViewById(R.id.edtMail);
        edtAddress = findViewById(R.id.edtAddress);
        autoGender = findViewById(R.id.autoGender);
        edtDOB = findViewById(R.id.edtDOB);
        ivToggle = findViewById(R.id.ivToggle);

        // Dropdown giới tính
        String[] genderOptions = {"Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        autoGender.setAdapter(adapter);

        TextInputLayout genderLayout = findViewById(R.id.genderLayout);
        autoGender.setOnClickListener(v -> {
            autoGender.showDropDown();
            genderLayout.setHint("");
        });

        fetchAndDisplayUserInfo(userId);
        addEvents();
    }

    private void addEvents() {
        ivToggle.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivToggle.setImageResource(R.drawable.ic_eye_open);
            } else {
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivToggle.setImageResource(R.drawable.ic_eye_open);
            }
            edtPassword.setSelection(edtPassword.length());
        });

        setEditConfirm(edtFullname, "Full Name");
        setEditConfirm(edtPassword, "Password");
        setEditConfirm(edtMail, "Email");
        setEditConfirm(edtAddress, "Address");
        setEditConfirm(edtDOB, "Date of Birth");
        setEditConfirm(autoGender, "Gender");
    }


    private void fetchAndDisplayUserInfo(String userId) {
        String url = ApiConnector.getUserInfoUrl(userId); // eg: https://yourserver.com/api/users/{id}

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");

                        String name = data.optString("UserName");
                        String password = data.optString("UserPassword");
                        String email = data.optString("UserEmail");
                        String address = data.optString("UserAddressDefault");
                        String dob = data.optString("UserDOB");
                        String gender = data.optString("UserGender");
                        String avatar = data.optString("UserAva");

                        edtFullname.setText(name);
                        edtPassword.setText(password);
                        edtMail.setText(email);
                        edtAddress.setText(address);
                        edtDOB.setText(dob);
                        autoGender.setText(gender, false); // set không trigger dropdown

                        if (avatar != null && avatar.startsWith("http")) {
                            Picasso.get().load(avatar).into(user_ava);
                        } else {
                            user_ava.setImageResource(R.drawable.ava_placeholder);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "❌ Lỗi đọc dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "❌ Lỗi khi tải thông tin người dùng", Toast.LENGTH_LONG).show();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void setEditConfirm(EditText editText, String fieldName) {
        editText.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("📝 Edit Confirmation")
                    .setMessage("You want to edit this " + fieldName + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        editText.setEnabled(true);
                        editText.requestFocus();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && editText.isEnabled()) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Confirm Update")
                        .setMessage("Update " + fieldName + " from old to new value?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            updateUserField(fieldName, editText.getText().toString());
                            editText.setEnabled(false);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            editText.setEnabled(false);
                        })
                        .show();
            }
        });
    }

    private void updateUserField(String fieldName, String newValue) {
        String userId = UserSession.getUserId(this);
        String url = ApiConnector.getUserUpdateUrl(userId); // ví dụ: https://yourapi.com/user/update/:id

        JSONObject updateBody = new JSONObject();
        try {
            switch (fieldName) {
                case "Full Name": updateBody.put("UserName", newValue); break;
                case "Password": updateBody.put("UserPassword", newValue); break;
                case "Email": updateBody.put("UserEmail", newValue); break;
                case "Address": updateBody.put("UserAddressDefault", newValue); break;
                case "Date of Birth": updateBody.put("UserDOB", newValue); break;
                case "Gender": updateBody.put("UserGender", newValue); break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "❌ JSON error", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                updateBody,
                response -> Toast.makeText(this, "✅ Updated successfully", Toast.LENGTH_SHORT).show(),
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "❌ Update failed", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }


}
