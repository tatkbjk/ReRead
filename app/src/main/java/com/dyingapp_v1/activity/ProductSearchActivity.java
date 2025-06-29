package com.dyingapp_v1.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.dyingapp_v1.adapter.ProductAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductSearchActivity extends AppCompatActivity {
    private SearchView searchBar;
    private RecyclerView rvSearchResults;

    private List<Product> currentProducts = new ArrayList<>();
    private boolean sortDescending = true;
    ShapeableImageView btnFilter;
    private FrameLayout filterContainer;
    private View filterView;

    private final Map<Integer, String> genreMap = new HashMap<>();
    private final Map<Integer, String> langMap = new HashMap<>();

    Button btnApplyFilter;
    CheckBox chkLanguageVietnamese, chkLanguageEnglish, chkGenrePsychology, chkGenreScience, chkGenreEducation, chkGenreTextbook;
    CheckBox chkGenreLiterature, chkGenreDictionary, chkGenreLanguageLearning;

    private List<Product> originalProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        searchBar = findViewById(R.id.searchBar);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        filterContainer = findViewById(R.id.filterContainer);  // ánh xạ đúng filter container


        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null && !keyword.isEmpty()) {
            searchBar.setQuery(keyword, false); // set text lên thanh search
            searchBooks(keyword);
        }

        // 👉 Cho phép user nhập lại keyword nếu muốn
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    searchBooks(query.trim());
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageView imgSortPrice = findViewById(R.id.imgSortPrice);
        imgSortPrice.setOnClickListener(v -> {
            if (!currentProducts.isEmpty()) {
                sortDescending = !sortDescending;
                sortByDiscountedPrice(currentProducts, sortDescending);
                setupRecycler(currentProducts);

                imgSortPrice.setRotation(sortDescending ? 0f : 180f);  // lật icon
            }
        });

        btnFilter = findViewById(R.id.btnFilter);

        addEvents();
    }

    private void addEvents() {
        btnFilter.setOnClickListener(v -> showFilterOverlay());


    }

    private void showFilterOverlay() {
        if (filterView == null) {
            filterView = getLayoutInflater().inflate(R.layout.filter_item, filterContainer, false);
            filterContainer.addView(filterView);

            // Ánh xạ các checkbox toàn cục
            chkGenreEducation = filterView.findViewById(R.id.chkGenreEducation);
            chkGenreLiterature = filterView.findViewById(R.id.chkGenreLiterature);
            chkGenreTextbook = filterView.findViewById(R.id.chkGenreTextbook);
            chkGenreScience = filterView.findViewById(R.id.chkGenreScience);
            chkGenrePsychology = filterView.findViewById(R.id.chkGenrePsychology);
            chkGenreDictionary = filterView.findViewById(R.id.chkGenreDictionary);
            chkGenreLanguageLearning = filterView.findViewById(R.id.chkGenreLanguageLearning);

            chkLanguageVietnamese = filterView.findViewById(R.id.chkLanguageVietnamese);
            chkLanguageEnglish = filterView.findViewById(R.id.chkLanguageEnglish);

            btnApplyFilter = filterView.findViewById(R.id.btnApplyFilter);

            btnApplyFilter.setOnClickListener(v2 -> {
                List<Product> filtered = new ArrayList<>();

                for (Product p : originalProducts) {
                    boolean genreOk = true;
                    boolean langOk = true;

                    String genre = genreMap.getOrDefault(p.getBookInfoId(), "").toLowerCase();
                    String lang = langMap.getOrDefault(p.getBookInfoId(), "").toLowerCase();

                    if (chkGenreEducation.isChecked() && !genre.contains("education")) genreOk = false;
                    if (chkGenreLiterature.isChecked() && !genre.contains("literature")) genreOk = false;
                    if (chkGenreTextbook.isChecked() && !genre.contains("textbook")) genreOk = false;
                    if (chkGenreScience.isChecked() && !genre.contains("science")) genreOk = false;
                    if (chkGenrePsychology.isChecked() && !genre.contains("psychology")) genreOk = false;
                    if (chkGenreDictionary.isChecked() && !genre.contains("dictionary")) genreOk = false;
                    if (chkGenreLanguageLearning.isChecked() && !genre.contains("language")) genreOk = false;

                    if (chkLanguageVietnamese.isChecked() && !lang.contains("vietnamese")) langOk = false;
                    if (chkLanguageEnglish.isChecked() && !lang.contains("english")) langOk = false;

                    if ((chkGenreEducation.isChecked() || chkGenreLiterature.isChecked() || chkGenreTextbook.isChecked() || chkGenreScience.isChecked()
                            || chkGenrePsychology.isChecked() || chkGenreDictionary.isChecked() || chkGenreLanguageLearning.isChecked()) && !genreOk)
                        continue;

                    if ((chkLanguageVietnamese.isChecked() || chkLanguageEnglish.isChecked()) && !langOk)
                        continue;

                    filtered.add(p);
                }

                setupRecycler(filtered);
                filterContainer.setVisibility(View.GONE);
            });
        }

        filterContainer.setVisibility(View.VISIBLE);  // chỉ hiển thị filter
    }

    private void sortByDiscountedPrice(List<Product> products, boolean descending) {
        products.sort((a, b) -> {
            int priceA = a.getDiscountedPrice();  // Giả sử bạn có getDiscountedPrice()
            int priceB = b.getDiscountedPrice();
            return descending ? Integer.compare(priceB, priceA) : Integer.compare(priceA, priceB);
        });
    }


    private void searchBooks(String keyword) {
        String url = ApiConnector.getSearchBooksUrl(Uri.encode(keyword));
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray data = response.getJSONArray("data");
                            List<Product> results = new ArrayList<>();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                int bookInfoID = obj.getInt("BookInfoID");
                                String bookISBN = obj.getString("BookISBN_n");




                                // Gọi tiếp để lấy BookTitle và BookImg1
                                String detailUrl = ApiConnector.getBasicBookInfoUrl(bookInfoID);
                                JsonObjectRequest infoRequest = new JsonObjectRequest(Request.Method.GET, detailUrl, null,
                                        infoRes -> {
                                            try {
                                                if (infoRes.getBoolean("success")) {
                                                    JSONObject book = infoRes.getJSONObject("data");

                                                    String genre = book.optString("BookGenre", "");
                                                    String lang = book.optString("BookLang", "");
                                                    genreMap.put(bookInfoID, genre);
                                                    langMap.put(bookInfoID, lang);

                                                    Product product = new Product(
                                                            bookInfoID,
                                                            bookISBN,
                                                            "",
                                                            book.getString("BookTitle"),
                                                            obj.optInt("BookPrice", 0),
                                                            obj.optInt("BookSales", 0),
                                                            book.getString("BookImg1"),
                                                            ""
                                                    );
                                                    results.add(product);

                                                    if (results.size() == data.length()) {
                                                        setupRecycler(results);
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        },
                                        Throwable::printStackTrace
                                );
                                queue.add(infoRequest);
                            }

                        } else {
                            Toast.makeText(this, "Không tìm thấy sách", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi tìm kiếm", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }

//    private void setupRecycler(List<Product> products) {
//        currentProducts = new ArrayList<>(products);
//        sortByDiscountedPrice(currentProducts, sortDescending);
//        ProductAdapter adapter = new ProductAdapter(this, currentProducts);
//        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
//        rvSearchResults.setAdapter(adapter);
//    }

    private void setupRecycler(List<Product> products) {
        originalProducts = new ArrayList<>(products); // giữ kết quả gốc
        currentProducts = new ArrayList<>(products);  // dùng để hiển thị và filter
        sortByDiscountedPrice(currentProducts, sortDescending);

        ProductAdapter adapter = new ProductAdapter(this, currentProducts);
        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        rvSearchResults.setAdapter(adapter);

        adapter.setOnItemClickListener(product -> {
            openProductDetailActivity(product);
        });
    }


    public void openProductDetailActivity(Product product) {
        Intent intent = new Intent(ProductSearchActivity.this, ProductDetailActivity.class);
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
