package com.dyingapp_v1.activity;

import static com.dyingapp_v1.connector.ApiConnector.getBookInfoUrl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.dyingapp_v1.R;
import com.dyingapp_v1.adapter.ProductAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.util.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dyingapp_v1.util.ProductViewBinder;
import com.dyingapp_v1.model.Product;

import java.util.ArrayList;
import java.util.List;


public class ProductDetailActivity extends AppCompatActivity {

    private int bookInfoID;

    private ImageView imgProduct, imgWishList, imgCart, imgProfile;
    private ShapeableImageView btn_back;
    private TextView txtDiscountedPrice, txtDiscountTag, txtOriginalPrice, txtProductName, txtProductDiscription;
    private TextView valueAuthor, valuePublisher, valueLanguage, valueGenre;

    private TextView txtShowMore;
    private ImageView imgShowMore;
    private boolean isExpanded = false;
    private MaterialButton btnAddToCart;
    private MaterialButton btnBuyNow;

    private RecyclerView rvPickedList;
    private ProductAdapter recommenderAdapter;

    SearchView searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        EdgeToEdge.enable(this);

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
        String bookISBN_n = getIntent().getStringExtra("BookISBN_n");

        String title = getIntent().getStringExtra("Title");
        int price = getIntent().getIntExtra("Price", 0);
        int sales = getIntent().getIntExtra("Sales", 0);
        String img = getIntent().getStringExtra("Img");
        String desc = getIntent().getStringExtra("Desc");

        // Tính giá sau
        // Tạo product tạm từ intent
        Product product = new Product(
                bookInfoID,
                bookISBN_n,         // ISBN không có trong intent
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
                            String authorFromServer = data.getString("BookAut");
                            String publisherFromServer = data.getString("BookPub");
                            String languageFromServer = data.getString("BookLang");
                            String genreFromServer = data.getString("BookGenre");

                            // Gán lên UI
                            txtProductDiscription.setText(descFromServer);
                            valueAuthor.setText(authorFromServer);
                            valuePublisher.setText(publisherFromServer);
                            valueLanguage.setText(languageFromServer);
                            valueGenre.setText(genreFromServer);

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

        // GỌI RECOMMENDER API
        String userId = UserSession.getUserId(this); // có thể null nếu chưa login
        String recommenderUrl = ApiConnector.getRecommendations(userId, bookISBN_n);

        JsonObjectRequest recommenderRequest = new JsonObjectRequest(Request.Method.GET, recommenderUrl, null,
                response -> {
                    try {
                        JSONArray recommendedBooks = response.getJSONArray("recommendations");
                        List<Product> recommendedList = new ArrayList<>();

                        for (int i = 0; i < recommendedBooks.length(); i++) {
                            String isbn = recommendedBooks.getJSONObject(i).getString("BookISBN_n");
                            String detailUrl = ApiConnector.getBookISBNUrl(isbn);

                            JsonObjectRequest detailRequest = new JsonObjectRequest(Request.Method.GET, detailUrl, null,
                                    detailResp -> {
                                        try {
                                            JSONArray dataArray = detailResp.optJSONArray("data");
                                            if (dataArray != null && dataArray.length() > 0) {
                                                JSONObject book = dataArray.getJSONObject(0);
                                                Product p = new Product(
                                                        book.getInt("BookInfoID"),
                                                        book.getString("BookISBN_n"),
                                                        book.optString("_id", ""),
                                                        book.getString("BookTitle"),
                                                        book.getInt("BookPrice"),
                                                        book.getInt("BookSales"),
                                                        book.getString("BookImg1"),
                                                        book.getString("BookCond")
                                                );
                                                recommendedList.add(p);
                                                recommenderAdapter.notifyItemInserted(recommendedList.size() - 1);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    error -> Log.e("RecommenderDetail", "Detail error: " + error.getMessage())
                            );

                            Volley.newRequestQueue(this).add(detailRequest);
                        }

                        recommenderAdapter = new ProductAdapter(ProductDetailActivity.this, recommendedList);
                        rvPickedList.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this, RecyclerView.HORIZONTAL, false));
                        rvPickedList.setAdapter(recommenderAdapter);

                        recommenderAdapter.setOnItemClickListener(clickedProduct -> {
                            Intent intent = new Intent(ProductDetailActivity.this, ProductDetailActivity.class);
                            intent.putExtra("BookInfoID", clickedProduct.getBookInfoId());
                            intent.putExtra("Title", clickedProduct.getBookTitle());
                            intent.putExtra("Price", clickedProduct.getBookPrice());
                            intent.putExtra("Sales", clickedProduct.getBookSales());
                            intent.putExtra("Img", clickedProduct.getBookImg1());
                            intent.putExtra("ISBN", clickedProduct.getBookISBN_n());
                            intent.putExtra("Desc", clickedProduct.getBookCond());
                            startActivity(intent);
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("Recommender", "Error: " + error.getMessage())
        );


        Volley.newRequestQueue(this).add(recommenderRequest);


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
        btnBuyNow = findViewById(R.id.btnBuyNow);

        valueAuthor = findViewById(R.id.valueAuthor);
        valuePublisher = findViewById(R.id.valuePublisher);
        valueLanguage = findViewById(R.id.valueLanguage);
        valueGenre = findViewById(R.id.valueGenre);


        imgWishList = findViewById(R.id.imgWishList);

        imgCart = findViewById(R.id.imgCart);
        imgProfile = findViewById(R.id.imgProfile);


        rvPickedList = findViewById(R.id.rvPickedList);

        searchBar = findViewById(R.id.searchBar);  // đảm bảo bạn đã ánh xạ searchBar nếu có

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


        imgWishList.setOnClickListener(v -> addToWishlist());

        imgCart.setOnClickListener(v -> openMyCartActivity());
        imgProfile.setOnClickListener(v -> openManageAccountActivity());


        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    // Tạo intent để chuyển sang ProductSearchActivity
                    Intent intent = new Intent(ProductDetailActivity.this, ProductSearchActivity.class);
                    intent.putExtra("keyword", query.trim());  // gửi từ khóa tìm kiếm
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void openManageAccountActivity() {
        Intent intent = new Intent(ProductDetailActivity.this, ManageAccountActivity.class);
        startActivity(intent);
    }

    private void openMyCartActivity() {
        Intent intent = new Intent(ProductDetailActivity.this, MyCartActivity.class);
        startActivity(intent);

    }



    private void addToWishlist() {
        String userId = UserSession.getUserId(this);  // Lấy UserID từ session
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Login to proceed.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiConnector.WISHLIST_ADD_URL;

        JSONObject body = new JSONObject();
        try {
            body.put("UserID", userId);
            body.put("BookInfoID", bookInfoID);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(this, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error." + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Wishlist", "Add error: " + error.getMessage());
                    Toast.makeText(this, "Adding error.", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
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