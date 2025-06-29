package com.dyingapp_v1.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.dyingapp_v1.adapter.EventFormAdapter;
import com.dyingapp_v1.adapter.SaleOrderAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.EventForm;
import com.dyingapp_v1.model.SaleOrder;
import com.dyingapp_v1.util.UserSession;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManageAccountActivity extends AppCompatActivity {

    private ImageView user_ava, ivEditName, ivEditMail, ivEditAddress, ivEditDOB;
    private EditText edtFullname, edtMail, edtAddress, edtDOB;
    private AutoCompleteTextView autoGender;
    private Button btn_change_password, btn_profile_update;
    private RecyclerView rvEvents;
    private RecyclerView rvSaleOrder;

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

        initViews();
        setupGenderDropdown();
        fetchAndDisplayUserInfo(UserSession.getUserId(this));
        fetchUserEvents(UserSession.getUserId(this));
        fetchUserOrders(UserSession.getUserId(this));
        setupEditTriggers();
        setupUpdateButton();
    }

    private void initViews() {
        user_ava = findViewById(R.id.user_ava);
        edtFullname = findViewById(R.id.edtFullname);
        edtMail = findViewById(R.id.edtMail);
        edtAddress = findViewById(R.id.edtAddress);
        edtDOB = findViewById(R.id.edtDOB);
        autoGender = findViewById(R.id.autoGender);
        btn_change_password = findViewById(R.id.btn_change_password);
        btn_profile_update = findViewById(R.id.btn_profile_update);
        rvEvents = findViewById(R.id.rvEvents);

        ivEditName = findViewById(R.id.ivEditName);
        ivEditMail = findViewById(R.id.ivEditMail);
        ivEditAddress = findViewById(R.id.ivEditAddress);
        ivEditDOB = findViewById(R.id.ivEditDOB);
        rvSaleOrder = findViewById(R.id.rvSaleOrder);
    }

    private void setupGenderDropdown() {
        List<String> genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Other");

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_dropdown_gender,
                R.id.dropdown_text,
                genderList
        );

        autoGender.setAdapter(genderAdapter);
        autoGender.setText("Male", false);

        autoGender.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("📝 Edit Gender")
                    .setMessage("Do you want to edit Gender?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        autoGender.setEnabled(true);
                        autoGender.setFocusable(true);
                        autoGender.setFocusableInTouchMode(true);
                        autoGender.requestFocus();
                        autoGender.showDropDown();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void fetchAndDisplayUserInfo(String userId) {
        String url = ApiConnector.getUserInfoUrl(userId);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        edtFullname.setText(data.optString("UserName"));
                        edtMail.setText(data.optString("UserEmail"));
                        edtAddress.setText(data.optString("UserAddressDefault"));
                        edtDOB.setText(data.optString("UserDOB"));

                        String gender = data.optString("UserGender");
                        autoGender.setText(!gender.isEmpty() ? gender : "Male", false);

                        String avatar = data.optString("UserAva");
                        if (avatar != null && avatar.startsWith("http")) {
                            Picasso.get().load(avatar).into(user_ava);
                        } else {
                            user_ava.setImageResource(R.drawable.ava_placeholder);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, "❌ Error reading user info", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "❌ Failed to load user info", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void setupEditTriggers() {
        setEditListener(ivEditName, edtFullname, "Full Name");
        setEditListener(ivEditMail, edtMail, "Email");
        setEditListener(ivEditAddress, edtAddress, "Address");
        setEditListener(ivEditDOB, edtDOB, "Date of Birth");
    }

    private void setEditListener(ImageView editIcon, EditText editText, String fieldName) {
        editIcon.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("📝 Edit " + fieldName)
                    .setMessage("Do you want to edit this field?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        editText.setEnabled(true);
                        editText.setFocusable(true);
                        editText.setFocusableInTouchMode(true);
                        editText.setCursorVisible(true);
                        editText.requestFocus();
                        if (!editText.getText().toString().isEmpty()) {
                            editText.setSelection(editText.getText().length());
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void setupUpdateButton() {
        btn_profile_update.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Update")
                    .setMessage("Do you want to update your profile?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        JSONObject updateBody = new JSONObject();
                        try {
                            updateBody.put("UserName", edtFullname.getText().toString());
                            updateBody.put("UserEmail", edtMail.getText().toString());
                            updateBody.put("UserAddressDefault", edtAddress.getText().toString());
                            updateBody.put("UserDOB", edtDOB.getText().toString());
                            updateBody.put("UserGender", autoGender.getText().toString());
                        } catch (JSONException e) {
                            Toast.makeText(this, "❌ JSON error", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.POST,
                                ApiConnector.getUserUpdateUrl(UserSession.getUserId(this)),
                                updateBody,
                                response -> Toast.makeText(this, "✅ Updated successfully", Toast.LENGTH_SHORT).show(),
                                error -> Toast.makeText(this, "❌ Update failed", Toast.LENGTH_SHORT).show()
                        );
                        Volley.newRequestQueue(this).add(request);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void fetchUserEvents(String userId) {
        String url = ApiConnector.getEventsByUserId(userId);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        List<EventForm> eventList = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Log.d("EVENT_DATA", obj.toString());

                            // Đọc dữ liệu từ JSON
                            String registrationId = obj.optString("RegistrationID");
                            String eventId = obj.optString("EventID");
                            String regDate = obj.optString("RegDate");
                            String note = obj.optString("Note", "No note");
                            boolean joined = obj.optInt("Joined") == 1;

                            String eventName = obj.optString("EventName");
                            String eventDate = obj.optString("EventDate");
                            String eventImg = obj.optString("EventImg");

                            EventForm event = new EventForm(
                                    registrationId, eventId, regDate, note, joined
                            );

                            // Gán thêm các field hiển thị
                            event.setEventName(eventName);
                            event.setEventDate(eventDate);
                            event.setEventImg(eventImg);

                            eventList.add(event);
                        }

                        // Setup RecyclerView
                        rvEvents.setLayoutManager(new LinearLayoutManager(this));
                        rvEvents.setAdapter(new EventFormAdapter(this, eventList));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "❌ Error parsing events", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "❌ Failed to load events", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
    private void fetchUserOrders(String userId) {
        String url = ApiConnector.getOrdersByUserId(userId);
        Log.d("DEBUG_ORDERS", "userId = " + userId);
        Log.d("DEBUG_ORDERS", "URL = " + ApiConnector.getOrdersByUserId(userId));

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        List<SaleOrder> orders = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);

                            JSONObject oid = obj.getJSONObject("_id");
                            String orderId = oid.optString("$oid");
                            String orderStatus = obj.optString("OrderStatus");
                            String orderDate = obj.optString("OrderDate");
                            int orderTotal = Integer.parseInt(obj.optString("OrderTotal"));

                            orders.add(new SaleOrder(orderId, orderStatus, orderDate, orderTotal));
                        }

                        rvSaleOrder.setLayoutManager(new LinearLayoutManager(this));
                        rvSaleOrder.setAdapter(new SaleOrderAdapter(orders));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Không tải được đơn hàng", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(request);
    }



}