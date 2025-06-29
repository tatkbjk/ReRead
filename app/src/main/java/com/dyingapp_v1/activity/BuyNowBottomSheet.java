package com.dyingapp_v1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.Product;
import com.dyingapp_v1.util.UserSession;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dyingapp_v1.util.ProductViewBinder;


public class BuyNowBottomSheet extends BottomSheetDialogFragment {

    private ImageView imgBookImage, btnMinus, btnPlus;
    private TextView txtBookTitle, txtBookStock, quantityText, txtDiscountedPrice, txtDiscountTag, txtOriginalPrice;
    private Button btnGood, btnAcceptable, btnLikeNew, btnBuyNow2;
    private int bookInfoId;
    private int quantity = 1;
    private String selectedCondition = null;




    private Map<String, Product> productMap = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_buy_now, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ view
        imgBookImage = view.findViewById(R.id.imgBookImage);
        txtBookTitle = view.findViewById(R.id.txtBookTitle);
        txtBookStock = view.findViewById(R.id.txtBookStock);
        quantityText = view.findViewById(R.id.quantityText);

        btnLikeNew = view.findViewById(R.id.btnLikeNew);
        btnGood = view.findViewById(R.id.btnGood);
        btnAcceptable = view.findViewById(R.id.btnAcceptable);
        btnBuyNow2 = view.findViewById(R.id.btnBuyNow2);

        txtOriginalPrice = view.findViewById(R.id.txtOriginalPrice);
        txtDiscountedPrice = view.findViewById(R.id.txtDiscountedPrice);
        txtDiscountTag = view.findViewById(R.id.txtDiscountTag);

        btnMinus = view.findViewById(R.id.btnMinus);
        btnPlus = view.findViewById(R.id.btnPlus);

        // Nhận bookInfoId từ Bundle
        bookInfoId = getArguments().getInt("bookInfoId", -1);

        if (bookInfoId != -1) {
            loadBookStockByInfoId(bookInfoId);
        }

//        btnLikeNew.setOnClickListener(v -> updateUIByCondition("Like New"));
//        btnGood.setOnClickListener(v -> updateUIByCondition("Good"));
//        btnAcceptable.setOnClickListener(v -> updateUIByCondition("Acceptable"));

        btnLikeNew.setOnClickListener(v -> {
            Log.d("DEBUG", "Clicked Like New");
            updateUIByCondition("Like New");
        });
        btnGood.setOnClickListener(v -> {
            Log.d("DEBUG", "Clicked Good");
            updateUIByCondition("Good");
        });
        btnAcceptable.setOnClickListener(v -> {
            Log.d("DEBUG", "Clicked Acceptable");
            updateUIByCondition("Acceptable");
        });

        quantityText.setText(String.valueOf(quantity));

        btnPlus.setOnClickListener(v -> {
            Log.d("DEBUG_CLICK", "Plus clicked");

            Product selectedProduct = productMap.get(getSelectedCondition());
            if (selectedProduct != null && quantity < selectedProduct.getCurrentQty()) {
                quantity++;
                quantityText.setText(String.valueOf(quantity));
            }
        });


        btnMinus.setOnClickListener(v -> {
            Log.d("DEBUG_CLICK", "Plus clicked");

            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
            }
        });


        btnBuyNow2.setOnClickListener(v -> {
            Product selectedProduct = productMap.get(getSelectedCondition());
            String userId = UserSession.getUserId(getContext());

            if (userId == null) {
                Log.e("CART", "❌ User not logged in.");
                Toast.makeText(getContext(), "Please log in to proceed", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedProduct.setBookQuantity(quantity);  // 👈 Gán số lượng vào sản phẩm
            selectedProduct.setSelected(true);          // 👈 Đánh dấu là được chọn (nếu bạn sử dụng cờ này để tính toán)

            // Tạo intent và truyền sang CheckOutActivity
            Intent intent = new Intent(getActivity(), CheckOutActivity.class);
            ArrayList<Product> selectedProducts = new ArrayList<>();
            selectedProducts.add(selectedProduct);

            int subtotal = selectedProduct.getDiscountedPrice() * quantity;
            int vouchervalue = 0; // Không dùng voucher ở BuyNowBottomSheet
            int total = subtotal;

            intent.putParcelableArrayListExtra("selectedProducts", selectedProducts);
            intent.putExtra("subtotal", subtotal);
            intent.putExtra("vouchervalue", vouchervalue);
            intent.putExtra("total", total);
            intent.putExtra("discRate", 0);
            intent.putExtra("discReq", 0);
            intent.putExtra("voucherCode", "");

            startActivity(intent);
            dismiss();
        });

    }


    private String getSelectedCondition () {
        return selectedCondition;
    }



    private void loadBookStockByInfoId(int bookInfoId) {
        String url = ApiConnector.getAllBookstockUrl(bookInfoId);
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray dataArray = response.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);

                                String id = obj.getString("_id");
                                String isbn = obj.getString("BookISBN_n");
                                String title = obj.getString("BookTitle");
                                int price = obj.getInt("BookPrice");
                                int sales = obj.getInt("BookSales");
                                String img1 = obj.getString("BookImg1");
                                String cond = obj.getString("BookCond");
                                int qty = obj.getInt("CurrentQty");

                                // Tạo Product và lưu vào map
                                Product product = new Product(bookInfoId, isbn, id, title, price, sales, img1, cond);
                                product.setCurrentQty(qty); // 👈 phải có setter ở trên
                                productMap.put(cond, product);

                                // Set nút theo điều kiện + số lượng
                                switch (cond) {
                                    case "Like New":
                                        setButtonState(btnLikeNew, qty);
                                        break;
                                    case "Good":
                                        setButtonState(btnGood, qty);
                                        break;
                                    case "Acceptable":
                                        setButtonState(btnAcceptable, qty);
                                        break;
                                }


                                // Gán ảnh và tiêu đề cho lần đầu
                                if (i == 0) {
                                    txtBookTitle.setText(title);
                                    Picasso.get().load("https://ik.imagekit.io/reRead2025/" + img1).into(imgBookImage);
                                    updateUIByCondition(cond); // Gán mặc định theo điều kiện đầu tiên
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace());

        queue.add(request);
    }

    private void setButtonState(Button button, int qty) {
        button.setText(button.getText().toString());
        if (qty == 0) {
            button.setEnabled(false);
            button.setBackgroundColor(getResources().getColor(R.color.grayDisabled));
        } else {
            button.setEnabled(true);
            button.setBackgroundColor(getResources().getColor(R.color.white)); // reset lại màu
        }
    }


    private void updateUIByCondition(String condition) {
        selectedCondition = condition;

        Product product = productMap.get(condition);
        if (product == null) return;

        int price = product.getBookPrice();
        int sales = product.getBookSales();
        int qty = product.getCurrentQty();
        String img1 = product.getBookImg1();
        String title = product.getBookTitle(); // 👈 bổ sung dòng này

        int discounted = price * (100 - sales) / 100;

        // Cập nhật toàn bộ UI theo điều kiện mới
        ProductViewBinder.bindProduct(
                product,
                txtBookTitle,
                txtOriginalPrice,
                txtDiscountedPrice,
                txtDiscountTag,
                txtBookStock,
                imgBookImage
        );

        // Đặt tất cả về nền trắng
        btnLikeNew.setBackgroundColor(getResources().getColor(R.color.white));
        btnGood.setBackgroundColor(getResources().getColor(R.color.white));
        btnAcceptable.setBackgroundColor(getResources().getColor(R.color.white));

        // Tô nút đang chọn thành xanh
        switch (condition) {
            case "Like New":
                btnLikeNew.setBackgroundColor(getResources().getColor(R.color.activeGreen));
                break;
            case "Good":
                btnGood.setBackgroundColor(getResources().getColor(R.color.activeGreen));
                break;
            case "Acceptable":
                btnAcceptable.setBackgroundColor(getResources().getColor(R.color.activeGreen));
                break;
        }

        quantity = 1;
        quantityText.setText(String.valueOf(quantity));


    }

}
