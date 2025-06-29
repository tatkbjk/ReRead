package com.dyingapp_v1.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dyingapp_v1.R;

public class QRCodeActivity extends AppCompatActivity {

    private TextView txtTotalAmount;
    private ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qrcode);

        addViews();
        addEvents();
    }

    private void addViews() {
        txtTotalAmount = findViewById(R.id.txtTotalAmount);

        // Nhận và hiển thị tổng tiền
        int total = getIntent().getIntExtra("total", 0);
        txtTotalAmount.setText(formatCurrency(total));

        btn_back = findViewById(R.id.btn_back);
    }

    private void addEvents() {

        btn_back.setOnClickListener(v -> finish());
    }

    private String formatCurrency(int amount) {
        return String.format("%,d₫", amount).replace(',', '.');
    }
}
