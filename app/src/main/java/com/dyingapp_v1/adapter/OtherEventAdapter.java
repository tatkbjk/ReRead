package com.dyingapp_v1.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyingapp_v1.R;
import com.dyingapp_v1.activity.EventDetailActivity;
import com.dyingapp_v1.model.Event;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OtherEventAdapter extends RecyclerView.Adapter<OtherEventAdapter.EventViewHolder> {

    private final List<Event> eventList;
    private final Context context;

    public OtherEventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEvent;
        TextView txtEventName;

        public EventViewHolder(View itemView) {
            super(itemView);
            imgEvent = itemView.findViewById(R.id.imgEvent);
            txtEventName = itemView.findViewById(R.id.txtEventName);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.other_event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.txtEventName.setText(event.getEventName());

        // Load ảnh nếu có
        if (event.getImg() != null && !event.getImg().trim().isEmpty()) {
            Picasso.get()
                    .load(event.getImg().trim())
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
                    .into(holder.imgEvent);
        } else {
            holder.imgEvent.setImageResource(R.drawable.ic_error);
        }

        // 🟡 Bổ sung sự kiện click (chuyển sang EventDetailActivity)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("event", event);  // Event cần Serializable
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
