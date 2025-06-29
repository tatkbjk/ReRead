package com.dyingapp_v1.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyingapp_v1.R;
import com.dyingapp_v1.model.Event;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {
    Activity context;
    int resource;

    public EventAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;


        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);

            holder = new ViewHolder();
            holder.imgEvent = convertView.findViewById(R.id.imgEvent);
            holder.txtEventName = convertView.findViewById(R.id.txtEventName);
            holder.txtEventStatus = convertView.findViewById(R.id.txtEventStatus);
            holder.txtEventLoc = convertView.findViewById(R.id.txtEventLoc);
            holder.txtEventStart = convertView.findViewById(R.id.txtEventStart);
            holder.imgStatusDot = convertView.findViewById(R.id.imgStatusDot);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Event e = getItem(position);
        if (e != null) {
            holder.txtEventName.setText(e.getEventName());
            holder.txtEventStatus.setText(e.getEventStatus());
            holder.txtEventLoc.setText(e.getEventLoc());
            holder.txtEventStart.setText(e.getFormattedEventStart());
            String status = e.getEventStatus();
            holder.txtEventStatus.setText(status);

            // Gán icon màu tương ứng trạng thái
            if ("Registration Closed".equals(status)) {
                holder.imgStatusDot.setImageResource(R.drawable.ic_ellipse_red);
            } else if ("Open For Registration".equals(status)) {
                holder.imgStatusDot.setImageResource(R.drawable.ic_ellipse_green);
            } else {
                holder.imgStatusDot.setImageResource(R.drawable.ic_error); // fallback
            }


            String imageUrl = e.getImg();
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                Picasso.get()
                        .load(imageUrl.trim())
                        .placeholder(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                        .into(holder.imgEvent, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("Picasso", "Image loaded successfully");
                            }

                            @Override
                            public void onError(Exception ex) {
                                Log.e("Picasso", "Image load failed", ex);
                            }
                        });
            } else {
                Log.w("ImageLink", "Image URL is null or empty");
                holder.imgEvent.setImageResource(R.drawable.ic_error);
            }
        }

        return convertView;
    }

    public void addEvents(List<Event> events) {
        clear();                // Xoá dữ liệu cũ
        addAll(events);         // Thêm danh sách mới
        notifyDataSetChanged(); // Cập nhật lại giao diện
    }

    static class ViewHolder {
        ImageView imgEvent;
        ImageView imgStatusDot;
        TextView txtEventName, txtEventStatus, txtEventLoc, txtEventStart;
    }
}
