package com.dyingapp_v1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyingapp_v1.R;
import com.dyingapp_v1.model.EventForm;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventFormAdapter extends RecyclerView.Adapter<EventFormAdapter.ViewHolder> {

    private final Context context;
    private final List<EventForm> eventList;

    public EventFormAdapter(Context context, List<EventForm> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_registration, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventForm event = eventList.get(position);

        // Set event title
        holder.tvEventTitle.setText(event.getEventName());

        // Format and set event date
        String formattedDate = formatDate(event.getEventDate());
        holder.tvEventDate.setText(formattedDate.isEmpty() ? "Unknown date" : formattedDate);

        // Load image
        if (event.getEventImg() != null && !event.getEventImg().isEmpty()) {
            Picasso.get()
                    .load(event.getEventImg())
                    .placeholder(R.drawable.ic_launcher_background) // ảnh fallback
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imgEventThumb);
        } else {
            holder.imgEventThumb.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEventThumb;
        TextView tvEventTitle, tvEventDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEventThumb = itemView.findViewById(R.id.imgEventThumb);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
        }
    }

    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = iso.parse(isoDate);
            SimpleDateFormat display = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH);
            return display.format(date);
        } catch (Exception e) {
            return isoDate;
        }
    }
}