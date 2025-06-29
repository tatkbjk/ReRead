package com.dyingapp_v1.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;

import com.dyingapp_v1.R;
import com.dyingapp_v1.connector.ApiConnector;

public class QuotationRequestActivity extends AppCompatActivity {

    Button btnYes, btnNo;
    EditText edtName, edtEmail, edtPhone, edtNote, edtBookname, edtAuthor, edtPublisher, edtPrice;
    RadioGroup radioCondition;
    private ImageView imgBook;
    private Button btnSelectImage;
    private String base64Image = "";
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation_request);

        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtBookname = findViewById(R.id.edtBookname);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtPublisher = findViewById(R.id.edtPublisher);
        edtNote = findViewById(R.id.edtNote);
        edtPrice = findViewById(R.id.edtPrice);
        radioCondition = findViewById(R.id.radioCondition);
        imgBook = findViewById(R.id.imgBook);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnNo.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        btnYes.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String bookname = edtBookname.getText().toString().trim();
            String author = edtAuthor.getText().toString().trim();
            String publisher = edtPublisher.getText().toString().trim();
            String price = edtPrice.getText().toString().trim();
            int selectedConditionId = radioCondition.getCheckedRadioButtonId();
            String note = edtNote.getText().toString().trim();

            if (name.isEmpty() || !email.contains("@") || phone.length() < 9 || selectedConditionId == -1) {
                Toast.makeText(this, "Please adjust input format as required.", Toast.LENGTH_SHORT).show();
                return;
            }

            String condition = ((RadioButton) findViewById(selectedConditionId)).getText().toString();

            SharedPreferences prefs = getSharedPreferences("userSession", MODE_PRIVATE);
            String userId = prefs.getString("userId", null);

            JSONObject json = new JSONObject();
            try {
                json.put("name", name);
                json.put("email", email);
                json.put("phone", phone);
                json.put("condition", condition);
                json.put("note", note);
                json.put("bookname", bookname);
                json.put("author", author);
                json.put("publisher", publisher);
                json.put("price", price);
                json.put("userId", userId != null ? userId : "guest");
                json.put("imgBook", base64Image);
            } catch (Exception e) {
                Toast.makeText(this, "Error preparing data.", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json.toString()
            );


            Request request = new Request.Builder()
                    .url(ApiConnector.CREATE_QUOTATION_URL)
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(QuotationRequestActivity.this, "Network error", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(QuotationRequestActivity.this, "Quotation submitted successfully!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK); // báo cho QuotationActivity biết là đã gửi thành công
                            finish();
                        });

                    } else {
                        String responseBody = response.body() != null ? response.body().string() : "No body";
                        Log.e("QuotationSubmit", "Error " + response.code() + ": " + responseBody);
                        runOnUiThread(() -> Toast.makeText(QuotationRequestActivity.this, "Submit failed: " + response.code(), Toast.LENGTH_LONG).show());
                    }
                }
            });
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Resize và nén ảnh
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Giảm chất lượng xuống 50
                byte[] imageBytes = baos.toByteArray();
                base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                // Hiển thị ảnh
                imgBook.setImageBitmap(scaledBitmap);

                // Encode Base64
                base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                Log.d("BASE64_LENGTH", "Length: " + base64Image.length());

            } catch (Exception e) {
                Toast.makeText(this, "Image error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }


}