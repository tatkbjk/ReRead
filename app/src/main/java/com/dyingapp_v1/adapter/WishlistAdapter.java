package com.dyingapp_v1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyingapp_v1.R;
import com.dyingapp_v1.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;



public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {
    private final Context context;
    private final List<Product> wishlistItems;

    public WishlistAdapter(Context context, List<Product> wishlistItems) {
        this.context = context;
        this.wishlistItems = wishlistItems;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wishlist_detail, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        Product p = wishlistItems.get(position);
        holder.txtTitle.setText(p.getBookTitle());
//        holder.txtDiscountedPrice.setText(String.format("%,d₫", p.getDiscountedPrice()));
//        holder.txtOrgPrice.setText(String.format("%,d₫", p.getBookPrice()));

        // Load ảnh
        Picasso.get()
                .load("https://ik.imagekit.io/reRead2025/" + p.getBookImg1())
                .placeholder(R.drawable.ic_logo)
                .into(holder.imgProduct);

        // Delete, Add to Cart v.v... thêm code ở đây

        holder.chkSelectItem.setOnCheckedChangeListener(null); // tránh vòng lặp khi recyclerview scroll
        holder.chkSelectItem.setChecked(p.isSelected());

        holder.chkSelectItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            p.setSelected(isChecked);
        });

        holder.imgProduct.setOnClickListener(v -> {
            if (imageClickListener != null) {
                imageClickListener.onImageClick(p);
            }
        });

    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    static class WishlistViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
//        TextView txtDiscountedPrice, txtOrgPrice;
        ImageView imgProduct;
        CheckBox chkSelectItem;
        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtWishlistProductTitle);
//            txtDiscountedPrice = itemView.findViewById(R.id.txtWishlistProductDiscountedPrice);
//            txtOrgPrice = itemView.findViewById(R.id.txtWishlistProductOrgPrice);
            imgProduct = itemView.findViewById(R.id.imgProductWishlist);
            chkSelectItem = itemView.findViewById(R.id.chkSelectItem);

        }
    }

    public void setAllSelected(boolean selected) {
        for (Product product : wishlistItems) {
            product.setSelected(selected);
        }
        notifyDataSetChanged();
    }

    public interface OnImageClickListener {
        void onImageClick(Product product);
    }

    private OnImageClickListener imageClickListener;

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.imageClickListener = listener;
    }


}
