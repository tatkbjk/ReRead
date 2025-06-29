package com.dyingapp_v1.activity;

import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.dyingapp_v1.adapter.CartAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.Product;
import com.dyingapp_v1.util.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyCartActivity extends AppCompatActivity {
    private List<Product> cartProductList = new ArrayList<>();
    private CartAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView btn_back, imgDeleteAll, imgTick;
    private CheckBox chkSelectAll;
    private TextView txtSubtotal, txtVoucherInUse, txtTotal;
    private EditText edtVoucher;
    private Handler voucherHandler = new Handler();
    private Runnable voucherRunnable;

    private double voucherDiscountRate = 0.0; // Eg: 0.1 nghĩa là 10%
    private Button btnCheckOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_cart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();

    }


    private void addViews() {



        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(this, cartProductList);
        adapter.setOnImageClickListener(product -> {
            Intent intent = new Intent(MyCartActivity.this, ProductDetailActivity.class);
            intent.putExtra("BookInfoID", product.getBookInfoId());
            intent.putExtra("BookISBN_n", product.getBookISBN_n());
            intent.putExtra("Title", product.getBookTitle());
            intent.putExtra("Price", product.getBookPrice());
            intent.putExtra("Sales", product.getBookSales());
            intent.putExtra("Img", product.getBookImg1());
            intent.putExtra("Desc", product.getBookCond());
            startActivity(intent);
        });



        recyclerView.setAdapter(adapter);



        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());

        String userId = UserSession.getUserId(this);
        Log.d("MyCartActivity", "📦 Logged in user ID: " + userId);
        loadCartForUser(userId);



        imgDeleteAll = findViewById(R.id.imgDeleteAll);
        chkSelectAll = findViewById(R.id.chkSelectAll);
        edtVoucher = findViewById(R.id.edtVoucher);
        txtVoucherInUse = findViewById(R.id.txtVoucherInUse);
        txtTotal = findViewById(R.id.txtTotal);
        txtSubtotal = findViewById(R.id.txtSubtotal);
        imgTick = findViewById(R.id.imgTick);

        imgTick.setVisibility(View.GONE); // Ẩn icon tic


        btnCheckOut = findViewById(R.id.btnCheckOut);





    }

    private void addEvents() {
//        imgDeleteAll.setOnClickListener(delete all from user's cart');
//        chkSelectall.setonclicklistener(all items are ticked (selected))


        chkSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adapter.setAllSelected(isChecked);
            calculateTotals(); // ✅ cập nhật lại tổng
        });


        imgDeleteAll.setOnClickListener(v -> {
            String userId = UserSession.getUserId(this);
            deleteSelectedItemsFromCart(userId);
        });

        edtVoucher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Huỷ gọi cũ nếu đang gõ
                voucherHandler.removeCallbacks(voucherRunnable);

                imgTick.setVisibility(View.GONE); // Ẩn icon tick khi đang nhập
            }

            @Override
            public void afterTextChanged(Editable s) {
                voucherRunnable = () -> {
                    String code = s.toString().trim();
                    if (!code.isEmpty()) {
                        checkVoucherValidity(code);
                    }
                };
                voucherHandler.postDelayed(voucherRunnable, 1000); // chờ 1s sau khi người dùng dừng gõ
            }
        });
        adapter.setOnCartItemCheckListener(() -> calculateTotals());


        adapter.setOnWishlistClickListener(product -> {
            // Gửi API thêm vào wishlist
            String userId = UserSession.getUserId(MyCartActivity.this);
            if (userId == null || userId.isEmpty()) {
                Toast.makeText(MyCartActivity.this, "Login to proceed.", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject body = new JSONObject();
            try {
                body.put("UserID", userId);
                body.put("BookInfoID", product.getBookInfoId());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ApiConnector.WISHLIST_ADD_URL, body,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(MyCartActivity.this, "✔ Added to Wishlist: " + product.getBookTitle(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyCartActivity.this, "⚠ Already in Wishlist: " + product.getBookTitle(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Log.e("Wishlist", "❌ Add error: " + error.getMessage());
                        Toast.makeText(MyCartActivity.this, "❌ Error adding: " + product.getBookTitle(), Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(MyCartActivity.this).add(request);
        });


    }

    private void checkVoucherValidity(String code) {
        String url = ApiConnector.getCheckVoucherUrl();
        JSONObject body = new JSONObject();
        try {
            body.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    boolean success = response.optBoolean("success", false);
                    if (success) {
                        imgTick.setVisibility(View.VISIBLE);

                        // Parse dữ liệu voucher từ JSON
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            int discRate = data.optInt("DiscRate", 0);
                            int discReq = data.optInt("DiscReq", 0);

                            voucherDiscountRate = discRate / 100.0;

                            // Gán discReq vào edtVoucher để dùng trong tính toán
                            edtVoucher.setTag(discReq);

                            // 👉 Hiển thị text đẹp hơn
                            txtVoucherInUse.setText(String.format("-%d%% (áp dụng từ %,dđ)", discRate, discReq));
                            txtVoucherInUse.setVisibility(View.VISIBLE);

                            Toast.makeText(this, "Valid Voucher", Toast.LENGTH_SHORT).show();
                        }

                        calculateTotals();
                    } else {
                        imgTick.setVisibility(View.GONE);
                        voucherDiscountRate = 0.0;
                        edtVoucher.setTag(0);
                        txtVoucherInUse.setText(""); // clear text
                        txtVoucherInUse.setVisibility(View.GONE);
                        Toast.makeText(this, "Invalid Voucher", Toast.LENGTH_SHORT).show();
                        calculateTotals();
                    }
                },
                error -> {
                    imgTick.setVisibility(View.GONE);
                    voucherDiscountRate = 0.0;
                    edtVoucher.setTag(0);
                    txtVoucherInUse.setText("");
                    txtVoucherInUse.setVisibility(View.GONE);
                    Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }


    private void loadCartForUser(String userId) {
        String url = ApiConnector.getCartUrl(userId);
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray items = data.getJSONArray("CartItems");

                            cartProductList.clear(); // clear cũ

                            txtSubtotal.setText(formatCurrency(0));
                            txtVoucherInUse.setText("-0%");
                            txtTotal.setText(formatCurrency(0));

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                String isbn = item.getString("BookISBN_n");
                                int qty = item.getInt("BookQuantity");

                                // Gọi lấy thông tin chi tiết
                                fetchBookStockByISBN(isbn, qty);

                            }
                        }
                    } catch (JSONException e) {
                        Log.e("MyCartActivity", "❌ JSON parsing error: " + e.getMessage());
                    }
                },
                error -> Log.e("MyCartActivity", "❌ Volley error: " + error.getMessage())
        );

        queue.add(request);


    }


    private void fetchBookStockByISBN(String isbn, int quantity) {
        String url = ApiConnector.getBookISBNUrl(isbn);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        if (dataArray.length() > 0) {
                            JSONObject book = dataArray.getJSONObject(0);

                            Product product = new Product();
                            product.setBookISBN_n(isbn);
                            product.setBookTitle(book.getString("BookTitle"));
                            product.setBookCond(book.getString("BookCond"));
                            product.setBookImg1(book.getString("BookImg1"));
                            product.setBookPrice(book.getInt("BookPrice"));
                            product.setBookSales(book.getInt("BookSales"));
                            product.setCurrentQty(book.getInt("CurrentQty"));
                            product.setBookInfoId(book.getInt("BookInfoID"));
                            product.setBookQuantity(quantity);
                            product.setSelected(false); // ✅ Quan trọng!
                            Log.d("fetchBookStock", "✅ book JSON: " + book.toString());

                            cartProductList.add(product);
                            adapter.notifyItemInserted(cartProductList.size() - 1);
                        }
                    } catch (JSONException e) {
                        Log.e("fetchBookStock", "JSON lỗi: " + e.getMessage());
                    }
                },
                error -> Log.e("fetchBookStock", "Lỗi gọi API ISBN " + isbn + ": " + error.getMessage())
        );

        Volley.newRequestQueue(this).add(request);

    }
    private void deleteSelectedItemsFromCart(String userId) {
        List<Product> selectedProducts = new ArrayList<>();

        for (Product product : cartProductList) {
            if (product.isSelected()) {
                selectedProducts.add(product);
            }
        }

        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "Please select at least one item to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Remove selected items from cart?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    RequestQueue queue = Volley.newRequestQueue(this);

                    // 👉 Nếu tất cả đều được chọn, dùng clear all
                    if (selectedProducts.size() == cartProductList.size()) {
                        String url = ApiConnector.getDeleteAllCartUrl(userId);
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                                response -> {
                                    cartProductList.clear();
                                    adapter.notifyDataSetChanged();
                                    calculateTotals();
                                    Toast.makeText(this, "🗑 All items removed.", Toast.LENGTH_SHORT).show();
                                },
                                error -> Log.e("Cart", "❌ Delete all error: " + error.getMessage())
                        );
                        queue.add(request);
                        return;
                    }

                    // 👉 Ngược lại, xoá từng item riêng lẻ
                    for (Product product : selectedProducts) {
                        String isbn = product.getBookISBN_n();
                        String url = ApiConnector.getDeleteCartItemUrl(userId, isbn);

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                                response -> {
                                    cartProductList.remove(product);
                                    adapter.notifyDataSetChanged();
                                    calculateTotals();
                                },
                                error -> Log.e("Cart", "❌ Delete error for ISBN " + isbn + ": " + error.getMessage())
                        );

                        queue.add(request);
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void calculateTotals() {
        int subtotal = 0;

        for (Product p : cartProductList) {
            if (p.isSelected()) {
                subtotal += p.getDiscountedPrice() * p.getBookQuantity();
            }
        }

        int discountAmount = (int) (subtotal * voucherDiscountRate);
        int total = subtotal - discountAmount;

        txtSubtotal.setText(formatCurrency(subtotal));
        txtVoucherInUse.setText(String.format("-%d%%", (int)(voucherDiscountRate * 100)));
        txtTotal.setText(formatCurrency(total));

    }


    private String formatCurrency(int amount) {
        return String.format("%,d₫", amount).replace(',', '.');
    }

    public void openCheckOutActivity(View view) {
        ArrayList<Product> selectedProducts = new ArrayList<>();
        int subtotal = 0;

        for (Product p : cartProductList) {
            if (p.isSelected()) {
                selectedProducts.add(p);
                subtotal += p.getBookPrice() * p.getBookQuantity();
            }
        }

        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "Must select at least one product to proceed CheckOut", Toast.LENGTH_SHORT).show();
            return;
        }

        int discReq = (edtVoucher.getTag() != null) ? (int) edtVoucher.getTag() : 0;
        int discountAmount = (int) (subtotal * voucherDiscountRate);
        int total = subtotal - discountAmount;

        Intent intent = new Intent(MyCartActivity.this, CheckOutActivity.class);

        intent.putParcelableArrayListExtra("selectedProducts", selectedProducts);
        for (Product p : selectedProducts) {
            Log.d("CheckOutActivityData", "📦 Selected: " +
                    p.getBookTitle() +
                    ", qty: " + p.getBookQuantity() +
                    ", selected: " + p.isSelected());
        }

        intent.putExtra("subtotal", subtotal);
        intent.putExtra("vouchervalue", discountAmount);
        intent.putExtra("total", total);
        intent.putExtra("discRate", (int)(voucherDiscountRate * 100));
        intent.putExtra("discReq", discReq);
        intent.putExtra("voucherCode", edtVoucher.getText().toString().trim());

        startActivity(intent);


    }

    // on click imgProduct,             openProductDetailActivity(product);


}