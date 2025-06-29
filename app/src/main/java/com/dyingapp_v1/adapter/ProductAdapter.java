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
import com.dyingapp_v1.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;
    private static final String BASE_IMAGE_URL = "https://ik.imagekit.io/reRead2025/";

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Sử dụng ProductViewBinder để bind dữ liệu
        com.dyingapp_v1.util.ProductViewBinder.bindProduct(
                product,
                holder.txtProductName,
                holder.txtOriginalPrice,
                holder.txtDiscountedPrice,
                holder.txtDiscountTag,
                new TextView(holder.itemView.getContext()),  // nếu không dùng stock
                holder.imgProduct
        );

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(productList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtDiscountedPrice, txtDiscountTag, txtOriginalPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtDiscountedPrice = itemView.findViewById(R.id.txtDiscountedPrice);
            txtDiscountTag = itemView.findViewById(R.id.txtDiscountTag);
            txtOriginalPrice = itemView.findViewById(R.id.txtOriginalPrice);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
