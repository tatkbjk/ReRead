package com.dyingapp_v1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.squareup.picasso.Picasso;
import com.dyingapp_v1.model.Event;
import com.dyingapp_v1.adapter.EventAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.adapter.OtherEventAdapter;

import org.json.JSONObject;
import org.json.JSONArray;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class EventDetailActivity extends AppCompatActivity {
    ImageView imgEventDetail;
    TextView txtEventName, txtEventDate, txtEventTime, txtEventLoc, txtEventDesc;
    Button btnRegister;
    RecyclerView rvOtherEvent;
    OtherEventAdapter otherEventAdapter;
    List<Event> otherEventList = new ArrayList<>();
    Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvent();
        getEventDataFromIntent();
        showEventDetails();
        loadOtherEvents();
    }

    private void addEvent() {
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailActivity.this, RegisterEventActivity.class);
            // Gửi ID sự kiện sang trang đăng ký
            intent.putExtra("eventId", currentEvent.getEventID());
            startActivity(intent);
        });
    }


    private void addViews() {
        imgEventDetail = findViewById(R.id.imgEventDetail);
        txtEventName = findViewById(R.id.txtEventName);
        txtEventDate = findViewById(R.id.txtEventDate);
        txtEventTime = findViewById(R.id.txtEventTime);
        txtEventLoc = findViewById(R.id.txtEventLoc);
        txtEventDesc = findViewById(R.id.txtEventDesc);
        btnRegister = findViewById(R.id.btnRegister);
        rvOtherEvent = findViewById(R.id.rvOtherEvent);

        rvOtherEvent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        otherEventAdapter = new OtherEventAdapter(otherEventList, this);
        rvOtherEvent.setAdapter(otherEventAdapter);
    }

    private void getEventDataFromIntent() {
        currentEvent = (Event) getIntent().getSerializableExtra("event");
    }

    private void showEventDetails() {
        if (currentEvent == null) return;

        txtEventName.setText(currentEvent.getEventName());

        String formattedStart = currentEvent.getFormattedEventStart(); // dd/MM/yyyy HH:mm
        String[] parts = formattedStart.split(" ");
        txtEventDate.setText(parts.length > 0 ? "🗓 Date: " + parts[0] : "🗓 Date: N/A");
        txtEventTime.setText(parts.length > 1 ? "🕐 Time: " + parts[1] : "🕐 Time: N/A");
        txtEventLoc.setText("📍 Location: " + currentEvent.getEventLoc());

        if (currentEvent.getEventDesc() != null && !currentEvent.getEventDesc().isEmpty()) {
            txtEventDesc.setText(String.join("\n\n", currentEvent.getEventDesc()));
        } else {
            txtEventDesc.setText("No description available.");
        }

        if (currentEvent.getImg() != null && !currentEvent.getImg().trim().isEmpty()) {
            Picasso.get().load(currentEvent.getImg().trim())
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
                    .into(imgEventDetail);
        } else {
            imgEventDetail.setImageResource(R.drawable.ic_error);
        }

        // ✅ Ẩn nút nếu không mở đăng ký
        if (!currentEvent.getEventStatus().equalsIgnoreCase("Open For Registration")) {
            btnRegister.setVisibility(View.INVISIBLE);
        } else {
            btnRegister.setVisibility(View.VISIBLE);
        }
    }


    private void loadOtherEvents() {
        String url = ApiConnector.EVENT_LIST_URL;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> parseEventList(response),
                error -> Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void parseEventList(JSONArray response) {
        try {
            otherEventList.clear();
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                Event event = new Event();
                event.setEventID(obj.optString("EventID"));
                event.setEventName(obj.optString("EventName"));
                event.setEventType(obj.optString("EventType"));
                event.setEventStart(obj.optString("EventStart"));
                event.setEventEnd(obj.optString("EventEnd"));
                event.setEventLoc(obj.optString("EventLoc"));
                event.setEventCpct(obj.optInt("EventCpct"));
                event.setImg(obj.optString("Img"));
                String rawDesc = obj.optString("EventDesc");
                List<String> descList = Arrays.asList(rawDesc.split("\\\\n")); // nếu JSON escape bằng \\n
                event.setEventDesc(descList);


                // Bỏ qua sự kiện đang xem
                if (currentEvent != null && event.getEventID().equals(currentEvent.getEventID())) {
                    continue;
                }

                otherEventList.add(event);
            }

            otherEventAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Error parsing event data", Toast.LENGTH_SHORT).show();
        }
    }
}