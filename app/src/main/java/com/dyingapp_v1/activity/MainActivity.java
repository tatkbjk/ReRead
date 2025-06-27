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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        String userId = UserSession.getUserId(this);
        System.out.println("✅ userId từ session: " + userId);
        Toast.makeText(this, "userId = " + userId, Toast.LENGTH_LONG).show();

        txtUserName = findViewById(R.id.txtUserName);
        imgAvatar = findViewById(R.id.imgAvatar);

        fetchAndDisplayUserInfo(userId, txtUserName, imgAvatar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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


}