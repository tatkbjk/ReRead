package com.dyingapp_v1.activity;

import static com.dyingapp_v1.connector.ApiConnector.getBookInfoUrl;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dyingapp_v1.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import com.dyingapp_v1.util.ProductViewBinder;
import com.dyingapp_v1.model.Product;



public class ProductDetailActivity extends AppCompatActivity {

    private int bookInfoID;

    private ImageView imgProduct, imgWishList;
    private ShapeableImageView btn_back;
    private TextView txtDiscountedPrice, txtDiscountTag, txtOriginalPrice, txtProductName, txtProductDiscription;

    private TextView txtShowMore;
    private ImageView imgShowMore;
    private boolean isExpanded = false;
    private MaterialButton btnAddToCart;
    private MaterialButton btnBuyNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();

        // 👉 Xử lý intent truyền sang
//        int bookInfoID = getIntent().getIntExtra("BookInfoID", 0);
        bookInfoID = getIntent().getIntExtra("BookInfoID", 0);
        Log.d("AddToCartBottomSheet", "bookInfoId = " + bookInfoID);

        String title = getIntent().getStringExtra("Title");
        int price = getIntent().getIntExtra("Price", 0);
        int sales = getIntent().getIntExtra("Sales", 0);
        String img = getIntent().getStringExtra("Img");
        String desc = getIntent().getStringExtra("Desc");

        // 👉 Tính giá sau giảm
// Tạo product tạm từ intent
        Product product = new Product(
                bookInfoID,
                "",         // ISBN không có trong intent
                "",         // ID không cần trong detail
                title,
                price,
                sales,
                img,
                ""          // condition không quan trọng ở đây
        );
        product.setCurrentQty(0); // hoặc để mặc định

        // Gán dữ liệu lên giao diện bằng binder
        ProductViewBinder.bindProduct(
                product,
                txtProductName,
                txtOriginalPrice,
                txtDiscountedPrice,
                txtDiscountTag,
                new TextView(this),
                imgProduct
        );

        txtProductDiscription.setText(desc);

        Picasso.get()
                .load("https://ik.imagekit.io/reRead2025/" + img)
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(imgProduct);



        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getBookInfoUrl(bookInfoID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String descFromServer = data.getString("BookDesc");

                            txtProductDiscription.setText(descFromServer);
                        } else {
                            txtProductDiscription.setText("Không tìm thấy mô tả sách");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        txtProductDiscription.setText("Lỗi phân tích dữ liệu");
                    }
                },
                error -> {
                    error.printStackTrace();
                    txtProductDiscription.setText("Lỗi kết nối server");
                }
        );

        requestQueue.add(stringRequest);

    }




    private void addViews() {

        btn_back = findViewById(R.id.btn_back);
        imgProduct = findViewById(R.id.imgProduct);
        imgWishList = findViewById(R.id.imgWishList);
        txtDiscountedPrice = findViewById(R.id.txtDiscountedPrice);
        txtDiscountTag = findViewById(R.id.txtDiscountTag);
        txtOriginalPrice = findViewById(R.id.txtOriginalPrice);
        txtProductName = findViewById(R.id.txtProductName);
        txtProductDiscription = findViewById(R.id.txtProductDiscription);

        txtShowMore = findViewById(R.id.textView3);
        imgShowMore = findViewById(R.id.imgShowMoreDiscription);

        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnAddToCart2);


    }

    private void addEvents() {

        btn_back.setOnClickListener(v -> finish());
        txtShowMore.setOnClickListener(v -> toggleDescription());
        imgShowMore.setOnClickListener(v -> toggleDescription());

        btnAddToCart.setOnClickListener(v -> {
            AddToCartBottomSheet bottomSheet = new AddToCartBottomSheet();
            Bundle args = new Bundle();
            args.putInt("bookInfoId", bookInfoID); // hoặc ID thật
            bottomSheet.setArguments(args);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

        btnBuyNow.setOnClickListener(v -> {
            BuyNowBottomSheet bottomSheet = new BuyNowBottomSheet();
            Bundle args = new Bundle();

            args.putInt("bookInfoId", bookInfoID); // hoặc ID thật
            bottomSheet.setArguments(args);

            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

    }

    private void toggleDescription() {
        if (isExpanded) {
            txtProductDiscription.setMaxLines(3);
            txtShowMore.setText("Show More");
            imgShowMore.setImageResource(R.drawable.ic_showmore);
        } else {
            txtProductDiscription.setMaxLines(Integer.MAX_VALUE);
            txtShowMore.setText("Show Less");
            imgShowMore.setImageResource(R.drawable.ic_showless);
        }
        isExpanded = !isExpanded;
    }



}