package com.dyingapp_v1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.dyingapp_v1.adapter.EventAdapter;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.fragment.EventFilterFragment;
import com.dyingapp_v1.model.Event;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;


public class EventListActivity extends AppCompatActivity {

    ImageView imgFilter;
    ImageView imgSort;
    TextView txtSortDate;
    ListView lvEvent;

    EventAdapter adapter;
    List<Event> currentEvents = new ArrayList<>();
    boolean isSortedAscending = true;

    TextView txtChatBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();
        addEvents();
    }

    private void addEvents() {
        imgFilter.setOnClickListener(v -> {
            // Inflate layout cho popup filter
            View popupView = getLayoutInflater().inflate(R.layout.event_filter, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true); // focusable

            // Ánh xạ view trong popup
            CheckBox chkbWorkshop = popupView.findViewById(R.id.chkbWorkshop);
            CheckBox chkbArtShow = popupView.findViewById(R.id.chkbArtShow);
            CheckBox chkbCompetition = popupView.findViewById(R.id.chkbCompetition);
            CheckBox chkbOpen = popupView.findViewById(R.id.chkbOpenRegistration);
            CheckBox chkbClosed = popupView.findViewById(R.id.chkClosedRegistration);
            Button btnApply = popupView.findViewById(R.id.btnApply);

            // Xử lý khi nhấn Apply
            btnApply.setOnClickListener(btn -> {
                boolean isWorkshop = chkbWorkshop.isChecked();
                boolean isArtShow = chkbArtShow.isChecked();
                boolean isCompetition = chkbCompetition.isChecked();
                boolean isOpen = chkbOpen.isChecked();
                boolean isClosed = chkbClosed.isChecked();

                List<Event> filtered = new ArrayList<>();
                for (Event e : currentEvents) {
                    boolean matchType = (!isWorkshop && !isArtShow && !isCompetition)
                            || (isWorkshop && e.getEventType().equalsIgnoreCase("Workshop"))
                            || (isArtShow && e.getEventType().equalsIgnoreCase("Art Show"))
                            || (isCompetition && e.getEventType().equalsIgnoreCase("Competition"));

                    boolean matchStatus = (!isOpen && !isClosed)
                            || (isOpen && e.getEventStatus().equalsIgnoreCase("Open For Registration"))
                            || (isClosed && e.getEventStatus().equalsIgnoreCase("Registration Closed"));

                    if (matchType && matchStatus) {
                        filtered.add(e);
                    }
                }

                adapter.clear();
                adapter.addAll(filtered);
                popupWindow.dismiss(); // đóng popup
            });

            //popupWindow.showAsDropDown(imgFilter, -32, -50);
            int[] location = new int[2];
            imgFilter.getLocationOnScreen(location);

            int x = location[0] - 320; // Dịch trái
            int y = location[1] + imgFilter.getHeight() - 50; // Dịch lên

            popupWindow.showAtLocation(imgFilter, Gravity.NO_GRAVITY, x, y);

        });

    }



    private void addViews() {
        imgFilter = findViewById(R.id.imgFilter);
        imgSort = findViewById(R.id.imgSort);
        txtSortDate = findViewById(R.id.txtSortDate);
        lvEvent = findViewById(R.id.lvEvent);
        adapter = new EventAdapter(this, R.layout.event_item);
        lvEvent.setAdapter(adapter);

        // 👉 BẮT SỰ KIỆN CLICK VÀO MỘT EVENT
        lvEvent.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = adapter.getItem(position);

            if (selectedEvent != null) {
                Intent intent = new Intent(EventListActivity.this, EventDetailActivity.class);
                intent.putExtra("event", selectedEvent); // truyền đối tượng Event
                startActivity(intent); // chuyển sang màn hình chi tiết
            }
        });

        // 👉 BẮT SỰ KIỆN SORT
        imgSort.setOnClickListener(v -> sortEventsByDate());
        txtSortDate.setOnClickListener(v -> sortEventsByDate());

        fetchEvents(); // gọi API sau khi đã set adapter

//        txtChatBot = findViewById(R.id.txtChatBot);
//        txtChatBot.setOnClickListener(v -> {
//            Intent intent = new Intent(EventListActivity.this, ChatBotActivity.class);
//            startActivity(intent);
//        });
    }


    private void fetchEvents() {
        String url = ApiConnector.EVENT_LIST_URL;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<Event> events = parseEvents(response);

                    currentEvents = events; // 🔧 THÊM DÒNG NÀY
                    adapter.clear();        // Clear trước khi add
                    adapter.addAll(events); // hoặc adapter.addEvents(events); nếu bạn dùng custom method
                },
                error -> {
                    Toast.makeText(this, "Không thể tải danh sách sự kiện", Toast.LENGTH_SHORT).show();
                    Log.e("EventListActivity", "Lỗi Volley", error);
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    private List<Event> parseEvents(JSONArray response) {
        List<Event> events = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);

                Event event = new Event();
                event.setId(obj.optString("_id"));
                event.setEventID(obj.optString("EventID"));
                event.setEventName(obj.optString("EventName"));
                event.setEventType(obj.optString("EventType"));
                event.setEventStart(obj.optString("EventStart"));
                event.setEventEnd(obj.optString("EventEnd"));
                event.setEventLoc(obj.optString("EventLoc"));
                event.setEventCpct(obj.optInt("EventCpct"));
                event.setImg(obj.optString("Img"));
                // ✅ Parse EventDesc là List<String>
                JSONArray descArray = obj.optJSONArray("EventDesc");
                if (descArray != null) {
                    List<String> descriptions = new ArrayList<>();
                    for (int j = 0; j < descArray.length(); j++) {
                        descriptions.add(descArray.optString(j));
                    }
                    event.setEventDesc(descriptions);
                }

                events.add(event);
            }
        } catch (Exception e) {
            Log.e("EventListActivity", "Lỗi parse JSON", e);
        }
        return events;
    }
    private void sortEventsByDate() {
        if (currentEvents == null || currentEvents.isEmpty()) return;

        Collections.sort(currentEvents, new Comparator<Event>() {
            public int compare(Event e1, Event e2) {
                String s1 = e1.getEventStart();
                String s2 = e2.getEventStart();
                return isSortedAscending ? s1.compareTo(s2) : s2.compareTo(s1);
            }
        });

        isSortedAscending = !isSortedAscending;

        adapter.clear();
        adapter.addAll(currentEvents);
        txtSortDate.setText(isSortedAscending ? "Date ↑" : "Date ↓");
    }

}