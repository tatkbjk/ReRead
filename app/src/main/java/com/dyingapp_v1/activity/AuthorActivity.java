package com.dyingapp_v1.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.dyingapp_v1.adapter.AuthorAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.Author;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Giả sử tên Activity của bạn là AuthorActivity và dùng layout activity_author_list
public class AuthorActivity extends AppCompatActivity {

    private RecyclerView rvAuthors;
    private AuthorAdapter authorAdapter;
    private List<Author> authorList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng layout dạng lưới bạn đã chọn
        setContentView(R.layout.activity_author);

        // Khởi tạo các view và danh sách
        rvAuthors = findViewById(R.id.rvAuthors);
        authorList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        // Khởi tạo và cài đặt adapter cho RecyclerView
        // GridLayoutManager đã được set trong XML, nên không cần set lại ở đây
        authorAdapter = new AuthorAdapter(this, authorList);
        rvAuthors.setAdapter(authorAdapter);

        // Gọi hàm để lấy dữ liệu tác giả từ server
        fetchAuthors();
    }

    private void fetchAuthors() {
        String url = ApiConnector.AUTHORS_URL;

        // Tạo yêu cầu GET để lấy danh sách tác giả
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // Kiểm tra xem yêu cầu có thành công không
                        if (response.getBoolean("success")) {
                            // Lấy mảng 'data' chứa các tác giả
                            JSONArray dataArray = response.getJSONArray("data");

                            // Duyệt qua từng đối tượng trong mảng
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject authorObject = dataArray.getJSONObject(i);

                                // Tạo đối tượng Author từ JSON
                                Author author = new Author();
                                author.setId(authorObject.getString("_id"));
                                author.setAuthorName(authorObject.getString("AuthorName"));
                                author.setAuthorImage(authorObject.getString("AuthorImage"));

                                // Thêm tác giả vào danh sách
                                authorList.add(author);
                            }

                            // Cập nhật lại RecyclerView để hiển thị dữ liệu mới
                            authorAdapter.notifyDataSetChanged();
                        } else {
                            // Hiển thị thông báo lỗi từ server
                            String message = response.getString("message");
                            Toast.makeText(AuthorActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AuthorActivity.this, "Lỗi phân tích dữ liệu JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Xử lý các lỗi mạng
                    error.printStackTrace();
                    Toast.makeText(AuthorActivity.this, "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                }
        );

        // Thêm yêu cầu vào hàng đợi để thực thi
        requestQueue.add(jsonObjectRequest);
    }
}
