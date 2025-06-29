package com.dyingapp_v1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dyingapp_v1.R;
import com.dyingapp_v1.model.SaleOrder;

import java.text.SimpleDateFormat;
import java.util.*;

public class SaleOrderAdapter extends RecyclerView.Adapter<SaleOrderAdapter.ViewHolder> {
    private final List<SaleOrder> orderList;

    public SaleOrderAdapter(List<SaleOrder> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SaleOrder order = orderList.get(position);
        holder.tvOrderId.setText("#" + order.getId());
        holder.tvDeliveryStatus.setText(order.getOrderStatus());
        holder.tvExpectedDelivery.setText(formatDate(order.getOrderDate()));
        holder.tvTotalAmount.setText(order.getOrderTotal() + " ₫");
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvDeliveryStatus, tvExpectedDelivery, tvTotalAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvDeliveryStatus = itemView.findViewById(R.id.tvDeliveryStatus);
            tvExpectedDelivery = itemView.findViewById(R.id.tvExpectedDelivery);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
        }
    }

    private String formatDate(String rawDate) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = input.parse(rawDate);
            SimpleDateFormat output = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
            return output.format(date);
        } catch (Exception e) {
            return rawDate;
        }
    }
}