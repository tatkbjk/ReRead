package com.dyingapp_v1.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyingapp_v1.R;
import com.dyingapp_v1.adapter.QuotationAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.Quotation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuotationActivity extends AppCompatActivity {

    RecyclerView recyclerQuotation;
    QuotationAdapter adapter;
    List<Quotation> quotationList = new ArrayList<>();
    Button btnCreateQuotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        recyclerQuotation = findViewById(R.id.recyclerQuotation);
        btnCreateQuotation = findViewById(R.id.btnCreateQuotation);

        recyclerQuotation.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuotationAdapter(quotationList);
        recyclerQuotation.setAdapter(adapter);

        btnCreateQuotation.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuotationRequestActivity.class);
            startActivityForResult(intent, 1001);
        });


        loadUserQuotations();
    }

    private void loadUserQuotations() {
        SharedPreferences prefs = getSharedPreferences("userSession", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }


        String url = ApiConnector.GET_QUOTATIONS_URL + "/user/" + userId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(QuotationActivity.this, "Network error", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(QuotationActivity.this, "Failed to load quotations", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray data = json.getJSONArray("data");

                    quotationList.clear();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject q = data.getJSONObject(i);
                        quotationList.add(new Quotation(
                                q.getString("_id"),
                                q.getString("bookname"),
                                q.optString("author", ""),
                                q.optString("publisher", ""),
                                q.optString("condition", ""),
                                q.optString("note", ""),
                                q.optString("status", ""),
                                q.optString("price", ""),
                                q.optString("imgBook", "")
                        ));
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());

                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(QuotationActivity.this, "Parse error", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Khi quay về từ QuotationRequestActivity sau khi submit thành công
            loadUserQuotations(); // Tải lại dữ liệu
        }
    }

}