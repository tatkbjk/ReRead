package com.dyingapp_v1.activity;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.dyingapp_v1.adapter.ProductAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.Product;
import com.dyingapp_v1.util.UserSession;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView txtUserName;
    private ShapeableImageView imgAvatar;

    private ArrayList<Product> recommendationsList;
    private ProductAdapter recommendationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        String userId = UserSession.getUserId(this);
        System.out.println("✅ userId từ session: " + userId);
        Toast.makeText(this, "userId = " + userId, Toast.LENGTH_LONG).show();

        fetchRecommendations(userId);


        txtUserName = findViewById(R.id.txtUserName);
        imgAvatar = findViewById(R.id.imgAvatar);

        fetchAndDisplayUserInfo(userId, txtUserName, imgAvatar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recommendationsList = new ArrayList<>();
        recommendationAdapter = new ProductAdapter(this, recommendationsList);

        RecyclerView rvRecommendations = findViewById(R.id.rvRecommendations);
        rvRecommendations.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecommendations.setAdapter(recommendationAdapter);

        recommendationAdapter.setOnItemClickListener(product -> {
            openProductDetailActivity(product);
        });


        RecyclerView rvFlashSales = findViewById(R.id.rvFlashSales);
        rvFlashSales.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Product> flashSaleList = new ArrayList<>();
        ProductAdapter flashSaleAdapter = new ProductAdapter(this, flashSaleList);
        rvFlashSales.setAdapter(flashSaleAdapter);
        flashSaleAdapter.setOnItemClickListener(product -> {
            openProductDetailActivity(product);
        });


        //

        RecyclerView rvPBestSellers = findViewById(R.id.rvPBestSellers);
        rvPBestSellers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Product> bestSellersList = new ArrayList<>();
        ProductAdapter bestSellersAdapter = new ProductAdapter(this, bestSellersList);
        rvPBestSellers.setAdapter(bestSellersAdapter);
        bestSellersAdapter.setOnItemClickListener(product -> {
            openProductDetailActivity(product);
        });

        // Flash Sale
        RequestQueue flashQueue = Volley.newRequestQueue(this);
        JsonObjectRequest flashRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiConnector.FLASH_SALE_URL,
                null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            int bookInfoId = obj.optInt("BookInfoID");
                            String bookISBN_n = obj.optString("BookISBN_n");

                            String id = obj.optString("id");
                            String title = obj.optString("BookTitle");
                            int price = obj.optInt("BookPrice");
                            int sales = obj.optInt("BookSales");
                            String img1 = obj.optString("BookImg1");
                            String cond = obj.optString("BookCond");

                            Product p = new Product(bookInfoId, bookISBN_n, id, title, price, sales, img1, cond);


                            flashSaleList.add(p);
                        }

                        flashSaleAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý JSON flash sale", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Không tải được flash sale", Toast.LENGTH_SHORT).show();
                }
        );
        flashQueue.add(flashRequest);

        // Best Sellers
        RequestQueue bestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest bestRequest = new JsonObjectRequest(
                Request.Method.GET,
                ApiConnector.BEST_SELLERS_URL,
                null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            int bookInfoId = obj.optInt("BookInfoID");
                            String bookISBN_n = obj.optString("BookISBN_n");

                            String id = obj.optString("id");
                            String title = obj.optString("BookTitle");
                            int price = obj.optInt("BookPrice");
                            int sales = obj.optInt("BookSales");
                            String img1 = obj.optString("BookImg1");
                            String cond = obj.optString("BookCond");

                            Product p = new Product(bookInfoId, bookISBN_n, id, title, price, sales, img1, cond);
                            bestSellersList.add(p);
                        }

                        bestSellersAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý JSON best sellers", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Không tải được best sellers", Toast.LENGTH_SHORT).show();
                }
        );
        bestQueue.add(bestRequest);
    }

    private void fetchAndDisplayUserInfo(String userId, TextView txtUserName, ShapeableImageView imgAvatar) {
        String url = ApiConnector.getUserInfoUrl(userId);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");

                        String name = data.optString("UserName");
                        String avatar = data.optString("UserAva");

                        System.out.println("✅ name: " + name);
                        System.out.println("✅ avatar: " + avatar);

                        txtUserName.setText(name);

                        if (avatar != null && avatar.startsWith("http")) {
                            Picasso.get().load(avatar).into(imgAvatar);
                        } else {
                            // Ảnh mặc định nếu avatar null
                            imgAvatar.setImageResource(R.drawable.ava_placeholder);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "❌ Lỗi đọc JSON user info", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "❌ Lỗi lấy thông tin user", Toast.LENGTH_LONG).show();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    public void openProductDetailActivity(Product product) {
        Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
        intent.putExtra("BookInfoID", product.getBookInfoId());
        intent.putExtra("BookISBN_n", product.getBookISBN_n());
        intent.putExtra("Title", product.getBookTitle());
        intent.putExtra("Price", product.getBookPrice());
        intent.putExtra("Sales", product.getBookSales());
        intent.putExtra("Img", product.getBookImg1());
        intent.putExtra("Desc", product.getBookCond());
        startActivity(intent);
    }

    private void fetchRecommendations(String userId) {
        String url = ApiConnector.getRecommendations(userId, null);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray items = response.optJSONArray("recommendations");
                        if (items == null) {
                            Toast.makeText(this, "Không có gợi ý", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        recommendationsList.clear();
                        recommendationAdapter.notifyDataSetChanged();

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject obj = items.optJSONObject(i);
                            if (obj == null) continue;

                            String bookISBN = obj.optString("BookISBN_n");
                            if (bookISBN == null || bookISBN.isEmpty()) continue;

                            System.out.println("📚 ISBN được gợi ý: " + bookISBN);

                            String detailUrl = ApiConnector.getBookISBNUrl(bookISBN);
                            JsonObjectRequest detailRequest = new JsonObjectRequest(
                                    Request.Method.GET,
                                    detailUrl,
                                    null,
                                    detailResp -> {
                                        try {
                                            JSONArray dataArray = detailResp.optJSONArray("data");
                                            if (dataArray != null && dataArray.length() > 0) {
                                                JSONObject book = dataArray.getJSONObject(0);  // ← Lấy phần tử đầu tiên

                                                int bookInfoId = book.optInt("BookInfoID");
                                                String title = book.optString("BookTitle");
                                                int price = book.optInt("BookPrice");
                                                int sales = book.optInt("BookSales");
                                                String img = book.optString("BookImg1");
                                                String cond = book.optString("BookCond");

                                                Product p = new Product(bookInfoId, bookISBN, "", title, price, sales, img, cond);
                                                recommendationsList.add(p);
                                                recommendationAdapter.notifyItemInserted(recommendationsList.size() - 1);
                                            }

                                        
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(this, "❌ JSON lỗi khi lấy chi tiết sách", Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    error -> {
                                        error.printStackTrace();
                                        Toast.makeText(this, "❌ Lỗi khi load chi tiết sách", Toast.LENGTH_SHORT).show();
                                    }
                            );

                            Volley.newRequestQueue(this).add(detailRequest);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "❌ Lỗi xử lý JSON từ API recommendation", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "❌ Lỗi khi gọi Flask server", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}
