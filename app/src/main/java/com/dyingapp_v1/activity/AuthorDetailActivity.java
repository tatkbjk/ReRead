package com.dyingapp_v1.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.dyingapp_v1.adapter.OtherAuthorsAdapter;
import com.dyingapp_v1.adapter.ProductAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.Author;
import com.dyingapp_v1.model.Product;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AuthorDetailActivity extends AppCompatActivity {

    public static final String EXTRA_AUTHOR_ID = "AUTHOR_ID";
    private RequestQueue queue;
    private String currentAuthorId;

    private ShapeableImageView imgAuthorAvatarDetail;
    private TextView txtAuthorNameDetail, txtAuthorBorn, txtAuthorGenre, txtAuthorDesc, txtShowMoreDesc;
    private boolean isDescExpanded = false;

    private RecyclerView rvAuthorWorks, rvOtherAuthors;
    private ProductAdapter authorWorksAdapter;
    private OtherAuthorsAdapter otherAuthorsAdapter;
    private List<Product> authorWorksList;
    private List<Author> otherAuthorsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);

        queue = Volley.newRequestQueue(this);
        currentAuthorId = getIntent().getStringExtra(EXTRA_AUTHOR_ID);
        if (currentAuthorId == null) {
            Toast.makeText(this, "Author ID is missing!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerViews();
        setupShowMoreToggle();

        fetchAuthorDetails();
        fetchOtherAuthors();
    }

    private void initViews() {
        imgAuthorAvatarDetail = findViewById(R.id.imgAuthorAvatarDetail);
        txtAuthorNameDetail = findViewById(R.id.txtAuthorNameDetail);
        txtAuthorBorn = findViewById(R.id.txtAuthorBorn);
        txtAuthorGenre = findViewById(R.id.txtAuthorGenre);
        txtAuthorDesc = findViewById(R.id.txtAuthorDesc);

        // ================== THÊM LẠI DÒNG NÀY ĐỂ ÁNH XẠ VIEW ==================
        txtShowMoreDesc = findViewById(R.id.txtShowMoreDesc);
        // ====================================================================
    }

    private void setupRecyclerViews() {
        rvAuthorWorks = findViewById(R.id.rvAuthorWorks);
        rvAuthorWorks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        authorWorksList = new ArrayList<>();
        authorWorksAdapter = new ProductAdapter(this, authorWorksList);
        rvAuthorWorks.setAdapter(authorWorksAdapter);

        rvOtherAuthors = findViewById(R.id.rvOtherAuthors);
        rvOtherAuthors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        otherAuthorsList = new ArrayList<>();
        otherAuthorsAdapter = new OtherAuthorsAdapter(this, otherAuthorsList);
        rvOtherAuthors.setAdapter(otherAuthorsAdapter);
    }

    private void setupShowMoreToggle() {
        // Giờ thì txtShowMoreDesc sẽ không còn là null nữa
        txtShowMoreDesc.setOnClickListener(v -> {
            isDescExpanded = !isDescExpanded;
            if (isDescExpanded) {
                txtAuthorDesc.setMaxLines(Integer.MAX_VALUE);
                txtShowMoreDesc.setText("Show Less");
            } else {
                txtAuthorDesc.setMaxLines(4);
                txtShowMoreDesc.setText("Show More");
            }
        });
    }

    private void fetchAuthorDetails() {
        String url = ApiConnector.getAuthorDetailsUrl(currentAuthorId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");
                            String name = data.optString("AuthorName");
                            String desc = data.optString("AuthorDesc");
                            String imageUrl = data.optString("AuthorImage", "").trim();
                            String born = data.optString("AuthorBorn", "N/A");
                            String genre = data.optString("AuthorGenre", "N/A");

                            txtAuthorNameDetail.setText(name);
                            txtAuthorDesc.setText(desc);
                            txtAuthorBorn.setText(born);
                            txtAuthorGenre.setText(genre);

                            if (!imageUrl.isEmpty()) {
                                Picasso.get().load(imageUrl).into(imgAuthorAvatarDetail);
                            }

                            if (!name.isEmpty()) {
                                fetchAuthorWorks(name);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("AuthorDetail", "JSON parsing error", e);
                    }
                },
                error -> Log.e("AuthorDetail", "Volley error: " + error.toString())
        );
        queue.add(request);
    }

    private void fetchAuthorWorks(String authorName) {
        String url = ApiConnector.getBooksByAuthorUrl(authorName);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            authorWorksList.clear();
                            // Bạn cần thêm logic parsing cho Product ở đây
                            // Ví dụ:
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                Product p = new Product(
                                        obj.optInt("BookInfoID"),
                                        obj.optString("BookISBN_n"),
                                        obj.optString("_id"),
                                        obj.optString("BookTitle"),
                                        obj.optInt("BookPrice"),
                                        obj.optInt("BookSales"),
                                        obj.optString("BookImg1"),
                                        obj.optString("BookCond")
                                );
                                authorWorksList.add(p);
                            }
                            authorWorksAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        Log.e("AuthorDetail", "JSON works parsing error", e);
                    }
                }, error -> {
            Log.e("AuthorDetail", "Volley works error: " + error.toString());
        });
        queue.add(request);
    }

    private void fetchOtherAuthors() {
        String url = ApiConnector.AUTHORS_URL;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            otherAuthorsList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                String id = obj.optString("_id");
                                if (!id.equals(currentAuthorId)) {
                                    Author author = new Author();
                                    author.setId(id);
                                    author.setAuthorName(obj.optString("AuthorName"));
                                    author.setAuthorImage(obj.optString("AuthorImage"));
                                    otherAuthorsList.add(author);
                                }
                            }
                            otherAuthorsAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        Log.e("AuthorDetail", "JSON other authors parsing error", e);
                    }
                }, error -> {
            Log.e("AuthorDetail", "Volley other authors error: " + error.toString());
        });
        queue.add(request);
    }
}
