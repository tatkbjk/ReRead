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

public class CheckOutProductAdapter extends RecyclerView.Adapter<CheckOutProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;

    public CheckOutProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, price, originalPrice, quantity, discountTag;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgProduct);
            title = itemView.findViewById(R.id.txtProductTitle);
            price = itemView.findViewById(R.id.txtDiscountedPrice);
            originalPrice = itemView.findViewById(R.id.txtOrgPrice);
            quantity = itemView.findViewById(R.id.txtBookQuantity);
            discountTag = itemView.findViewById(R.id.txtSale);
        }
    }

    @NonNull
    @Override
    public CheckOutProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_checkout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutProductAdapter.ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.title.setText(product.getBookTitle());
        holder.quantity.setText("x" + product.getBookQuantity());

        int discountedPrice = product.getDiscountedPrice();
        holder.price.setText(String.format("%,d₫", discountedPrice).replace(",", "."));

        if (product.hasDiscount()) {
            holder.originalPrice.setVisibility(View.VISIBLE);
            holder.originalPrice.setText(String.format("%,d₫", product.getBookPrice()).replace(",", "."));
            holder.discountTag.setVisibility(View.VISIBLE);
            holder.discountTag.setText("-" + product.getBookSales() + "%");
        } else {
            holder.originalPrice.setVisibility(View.GONE);
            holder.discountTag.setVisibility(View.GONE);
        }

        String imageUrl = "https://ik.imagekit.io/reRead2025/" + product.getBookImg1();
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.ic_logo_white)
                .error(R.drawable.ic_logo_white)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
