package com.dyingapp_v1.activity;

import static com.dyingapp_v1.validator.Validator.isValidPhone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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
import com.dyingapp_v1.adapter.CheckOutProductAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.Product;
import com.dyingapp_v1.util.UserSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.widget.Toast;

public class CheckOutActivity extends AppCompatActivity {
    private ImageView btn_back;
    private EditText edtFullname, edtPhone, edtAddress;
    private TextView txtSubtotal, txtVoucher, txtTotal, txtVoucherApplied, txtShipFee;
    private RecyclerView recyclerView;
    private ArrayList<Product> products;
    private int subtotal, voucherValue, total;
    private String userId;
    private Button btnPlaceOrder;
    private RadioButton radSelfPick, radDoorDash;

    private int shippingFee = 0;

    private int calculateSubtotalFromDiscountedPrice() {
        int calculatedSubtotal = 0;
        for (Product p : products) {
            calculatedSubtotal += p.getDiscountedPrice() * p.getBookQuantity();
        }
        return calculatedSubtotal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = UserSession.getUserId(this);

        products = getIntent().getParcelableArrayListExtra("selectedProducts");
        for (Product p : products) {
            Log.d("CheckOutActivityData", "📦 Selected: " +
                    p.getBookTitle() +
                    ", qty: " + p.getBookQuantity() +
                    ", selected: " + p.isSelected());
        }

        subtotal = calculateSubtotalFromDiscountedPrice();
        voucherValue = getIntent().getIntExtra("vouchervalue", 0);
        total = subtotal - voucherValue + shippingFee;

        loadUserInfo();
        addViews();
        addEvents();
    }

    private void addViews() {
        edtFullname = findViewById(R.id.edtFullname);
        edtPhone = findViewById(R.id.edtPhoneNumber);
        edtAddress = findViewById(R.id.edtAddress);

        txtSubtotal = findViewById(R.id.txtSubtotal2);
        txtVoucher = findViewById(R.id.txtVoucher2);
        txtTotal = findViewById(R.id.txtTotal2);
        txtVoucherApplied = findViewById(R.id.txtVoucherApplied);
        txtShipFee = findViewById(R.id.txtShipFee);
        txtShipFee.setText(formatCurrency(shippingFee));

        btn_back = findViewById(R.id.btn_back);

        recyclerView = findViewById(R.id.recyclerViewCheckout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CheckOutProductAdapter adapter = new CheckOutProductAdapter(this, products);
        recyclerView.setAdapter(adapter);

        String voucherCode = getIntent().getStringExtra("voucherCode");
        int discRate = getIntent().getIntExtra("discRate", 0);

        if (voucherCode != null && !voucherCode.isEmpty() && discRate > 0) {
            txtVoucherApplied.setText(voucherCode);
        }

        txtSubtotal.setText(formatCurrency(subtotal));
        txtVoucher.setText("-" + formatCurrency(voucherValue));
        txtTotal.setText(formatCurrency(total));
    }

    private void addEvents() {
        btn_back.setOnClickListener(v -> finish());

        radSelfPick = findViewById(R.id.radSelfPick);
        radDoorDash = findViewById(R.id.radDoorDash);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        radSelfPick.setOnClickListener(v -> {
            shippingFee = 0;
            updateTotal();
        });

        radDoorDash.setOnClickListener(v -> {
            shippingFee = 30000;
            updateTotal();
        });

        btnPlaceOrder.setOnClickListener(v -> {
            String name = edtFullname.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPhone(phone)) {
                Toast.makeText(this, "Phone number invalid!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!radSelfPick.isChecked() && !radDoorDash.isChecked()) {
                Toast.makeText(this, "Choose a Delivery Mode", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ MỞ QRCodeActivity và truyền total
            Intent intent = new Intent(this, QRCodeActivity.class);
            intent.putExtra("total", total); // truyền tổng tiền
            startActivity(intent);
        });
    }

    private void loadUserInfo() {
        String url = ApiConnector.getUserInfoUrl(userId);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");
                            edtFullname.setText(data.optString("UserName", ""));
                            edtPhone.setText(data.optString("UserPhoneDefault", ""));
                            edtAddress.setText(data.optString("UserAddressDefault", ""));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private String formatCurrency(int amount) {
        return String.format("%,d₫", amount).replace(',', '.');
    }

    private void updateTotal() {
        total = subtotal - voucherValue + shippingFee;
        txtTotal.setText(formatCurrency(total));
        txtShipFee.setText(formatCurrency(shippingFee));
    }
}
