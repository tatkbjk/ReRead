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
import com.dyingapp_v1.adapter.ProductAdapter;
import com.dyingapp_v1.adapter.WishlistAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.Product;
import com.dyingapp_v1.util.NavigationUtil;
import com.dyingapp_v1.util.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListWishlistActivity extends AppCompatActivity {
        private List<Product> wishlistItems = new ArrayList<>();
        private WishlistAdapter wishlistAdapter;
        private RecyclerView recyclerView;
        private ImageView btn_back, imgProfile;

        private CheckBox chkSelectAll;
        private ImageView imgDeleteAll;
        private List<Product> pickedList = new ArrayList<>();
        private ProductAdapter pickedAdapter;

        private int expectedWishlistSize = 0;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_list_wishlist); // <-- layout riêng cho wishlist

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            addViews();
            addEvents();
        }


    private void addViews() {
            recyclerView = findViewById(R.id.recyclerViewCart); // dùng lại ID recyclerViewCart nếu cần
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            wishlistAdapter = new WishlistAdapter(this, wishlistItems);




            recyclerView.setAdapter(wishlistAdapter);

            wishlistAdapter.setOnImageClickListener(product -> {
                Intent intent = new Intent(ListWishlistActivity.this, ProductDetailActivity.class);
                intent.putExtra("BookInfoID", product.getBookInfoId());
                intent.putExtra("BookISBN_n", product.getBookISBN_n());
                intent.putExtra("Title", product.getBookTitle());
                intent.putExtra("Price", product.getBookPrice()); // nếu có
                intent.putExtra("Sales", product.getBookSales()); // nếu có
                intent.putExtra("Img", product.getBookImg1());
                intent.putExtra("Desc", product.getBookCond()); // nếu có
                startActivity(intent);
            });


            btn_back = findViewById(R.id.btn_back);
            btn_back.setOnClickListener(v -> finish());



            String userId = UserSession.getUserId(this);

            loadWishlistForUser(userId);

            imgProfile = findViewById(R.id.imgProfile);

            chkSelectAll = findViewById(R.id.chkSelectAll);
            imgDeleteAll = findViewById(R.id.imgDeleteAll);


            RecyclerView rvPicked = findViewById(R.id.rvRecommendations);
            rvPicked.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            pickedAdapter = new ProductAdapter(this, pickedList);
            rvPicked.setAdapter(pickedAdapter);

            pickedAdapter.setOnItemClickListener(product -> {
                Intent intent = new Intent(ListWishlistActivity.this, ProductDetailActivity.class);
                intent.putExtra("BookInfoID", product.getBookInfoId());
                intent.putExtra("BookISBN_n", product.getBookISBN_n());
                intent.putExtra("Title", product.getBookTitle());
                intent.putExtra("Price", product.getBookPrice());
                intent.putExtra("Sales", product.getBookSales());
                intent.putExtra("Img", product.getBookImg1());
                intent.putExtra("Desc", product.getBookCond());
                startActivity(intent);
            });


    }

    private void addEvents() {
        imgProfile.setOnClickListener(v -> {
            NavigationUtil.navigateToManageAccount(this);
        });

        chkSelectAll.setOnClickListener(v -> {
            boolean isChecked = chkSelectAll.isChecked();
            wishlistAdapter.setAllSelected(isChecked);
        });

        imgDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Delete selected items? This action is not reversible.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteSelectedItems();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


    }

    private void loadWishlistForUser(String userId) {
        String url = ApiConnector.getWishlistUrl(userId);



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("Wishlist", "📥 Raw JSON response: " + response.toString());

                        if (response.getBoolean("success")) {
                            JSONArray items = response.getJSONArray("data");
                            wishlistItems.clear();

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                int bookInfoId = Integer.parseInt(item.getString("BookInfoID"));

                                fetchBasicBookInfo(bookInfoId);  // 👈 Gọi API mới
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("Wishlist", "JSON error: " + e.getMessage());
                    }
                },
                error -> Log.e("Wishlist", "Volley error: " + error.getMessage())
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchBasicBookInfo(int bookInfoId) {
        String url = ApiConnector.getBasicBookInfoUrl(bookInfoId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject book = response.getJSONObject("data");

                            Product product = new Product();
                            product.setBookInfoId(book.getInt("BookInfoID"));
                            product.setBookTitle(book.getString("BookTitle"));
                            product.setBookImg1(book.getString("BookImg1"));
                            product.setBookISBN_n(book.getString("BookISBN_n"));

                            // ✅ Gọi tiếp API từ ISBN
                            fetchDetailFromISBN(product);  // 👈 new method
                        } else {
                            Log.w("Wishlist", "❌ Failed basic info response");
                        }
                    } catch (JSONException e) {
                        Log.e("Wishlist", "JSON parse error: " + e.getMessage());
                    }
                },
                error -> Log.e("Wishlist", "Volley error: " + error.getMessage())
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchDetailFromISBN(Product product) {
        String isbn = product.getBookISBN_n();
        String url = ApiConnector.getBookISBNUrl(isbn);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        if (dataArray.length() > 0) {
                            JSONObject data = dataArray.getJSONObject(0);

                            product.setBookPrice(data.optInt("BookPrice", 0));
                            product.setBookSales(data.optInt("BookSales", 0));
                            product.setBookCond(data.optString("BookCond", ""));

                            wishlistItems.add(product);
                            wishlistAdapter.notifyItemInserted(wishlistItems.size() - 1);

                            // Gọi fetchPickedForUser sau khi đã load xong tất cả sách trong wishlist
                            if (wishlistItems.size() == expectedWishlistSize) {
                                fetchPickedForUser(UserSession.getUserId(this));
                            }

                        }
                    } catch (JSONException e) {
                        Log.e("Wishlist", "fetchDetailFromISBN: JSON error: " + e.getMessage());
                    }
                },
                error -> Log.e("Wishlist", "fetchDetailFromISBN: Volley error: " + error.getMessage())
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void deleteSelectedItems() {
        List<Product> toRemove = new ArrayList<>();
        for (Product p : wishlistItems) {
            if (p.isSelected()) {
                toRemove.add(p);
            }
        }

        if (toRemove.isEmpty()) {
            Toast.makeText(this, "Select at least 1 item.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = UserSession.getUserId(this);
        RequestQueue queue = Volley.newRequestQueue(this);

        for (Product product : toRemove) {
            String bookInfoId = String.valueOf(product.getBookInfoId());
            String url = ApiConnector.getDeleteWishlistItemUrl(userId, bookInfoId);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                    response -> {
                        // Nếu xoá thành công từ backend → xoá khỏi danh sách hiển thị
                        wishlistItems.remove(product);
                        wishlistAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Deleted: " + product.getBookTitle(), Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Log.e("Wishlist", "❌ Delete error for BookInfoID " + bookInfoId + ": " + error.getMessage());
                        Toast.makeText(this, "❌ Failed to delete: " + product.getBookTitle(), Toast.LENGTH_SHORT).show();
                    });

            queue.add(request);
        }
    }

    private void fetchPickedForUser(String userId) {
        pickedList.clear();
        pickedAdapter.notifyDataSetChanged();

        for (Product p : wishlistItems) {
            String isbn = p.getBookISBN_n();
            if (isbn == null || isbn.isEmpty()) continue;

            String url = ApiConnector.getRecommendations(userId, isbn); // 👈 Gợi ý theo user + sách cụ thể

            JsonObjectRequest recRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        JSONArray items = response.optJSONArray("recommendations");
                        if (items == null || items.length() == 0) return;

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject obj = items.optJSONObject(i);
                            if (obj == null) continue;

                            String recIsbn = obj.optString("BookISBN_n");
                            if (recIsbn == null || recIsbn.isEmpty()) continue;

                            String detailUrl = ApiConnector.getBookISBNUrl(recIsbn);
                            JsonObjectRequest detailRequest = new JsonObjectRequest(
                                    Request.Method.GET,
                                    detailUrl,
                                    null,
                                    detailResp -> {
                                        try {
                                            JSONArray dataArray = detailResp.optJSONArray("data");

                                            expectedWishlistSize = items.length();

                                            if (dataArray != null && dataArray.length() > 0) {
                                                JSONObject book = dataArray.getJSONObject(0);
                                                int bookInfoId = book.optInt("BookInfoID");
                                                String title = book.optString("BookTitle");
                                                int price = book.optInt("BookPrice");
                                                int sales = book.optInt("BookSales");
                                                String img = book.optString("BookImg1");
                                                String cond = book.optString("BookCond");

                                                Product product = new Product(bookInfoId, recIsbn, "", title, price, sales, img, cond);
                                                // ⚠️ Bạn có thể cần kiểm tra trùng
                                                if (!isDuplicateISBN(recIsbn)) {
                                                    pickedList.add(product);
                                                    pickedAdapter.notifyItemInserted(pickedList.size() - 1);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            Log.e("Picked", "JSON parse error", e);
                                        }
                                    },
                                    error -> Log.e("Picked", "Detail fetch error", error)
                            );
                            Volley.newRequestQueue(this).add(detailRequest);
                        }

                    },
                    error -> Log.e("Picked", "Recommend fetch error", error)
            );

            Volley.newRequestQueue(this).add(recRequest);
        }
    }

    private boolean isDuplicateISBN(String isbn) {
        for (Product p : pickedList) {
            if (p.getBookISBN_n().equals(isbn)) return true;
        }
        return false;
    }

}

