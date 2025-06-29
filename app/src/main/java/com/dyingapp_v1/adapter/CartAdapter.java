package com.dyingapp_v1.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dyingapp_v1.R;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.Product;
import com.dyingapp_v1.util.UserSession;
import com.squareup.picasso.Picasso;

import java.util.List;
import android.widget.Toast;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<Product> cartItems;

    public CartAdapter(Context context, List<Product> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    private boolean isAllSelected = false;

//    public void setAllSelected(boolean selected) {
//        isAllSelected = selected;
//        notifyDataSetChanged(); // Refresh all view holders
//    }


    public void setAllSelected(boolean selected) {
        for (Product product : cartItems) {
            product.setSelected(selected);
        }
        notifyDataSetChanged();

        if (checkListener != null) {
            checkListener.onCartItemCheckedChanged(); // cập nhật subtotal/total
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartItems.get(position);

        holder.txtProductTitle.setText(product.getBookTitle());
        holder.txtDiscountedPrice.setText(String.format("%,d đ", product.getDiscountedPrice()).replace(",", "."));
        holder.txtOrgPrice.setText(String.format("%,d đ", product.getBookPrice()).replace(",", "."));
        holder.txtSale.setText("-" + product.getBookSales() + "%");
        holder.txtBookQuantity.setText(String.valueOf(product.getBookQuantity()));

//        holder.chkSelectItem.setChecked(isAllSelected);

        holder.chkSelectItem.setOnCheckedChangeListener(null); // prevent loop

        // Hide original price if no discount
        if (!product.hasDiscount()) {
            holder.txtOrgPrice.setVisibility(View.GONE);
            holder.txtSale.setVisibility(View.GONE);
        } else {
            holder.txtOrgPrice.setVisibility(View.VISIBLE);
            holder.txtSale.setVisibility(View.VISIBLE);
        }

        Picasso.get()
                .load("https://ik.imagekit.io/reRead2025/" + product.getBookImg1())
                .placeholder(R.drawable.ic_logo)
                .into(holder.imgProduct);

//        holder.btnPlus.setOnClickListener(v -> {
//            int current = product.getBookQuantity(); // số lượng hiện tại người dùng chọn
//            int maxQty = product.getCurrentQty();    // số lượng tồn kho
//
//            if (current < maxQty) {
//                product.setBookQuantity(current + 1);
//                holder.txtBookQuantity.setText(String.valueOf(product.getBookQuantity()));
//            } else {
//                Toast.makeText(context, "Quantity chosen exceeded current stock", Toast.LENGTH_SHORT).show();
//            }
//
//            Log.d("CartAdapter", "current = " + current + ", maxQty = " + maxQty);
//
//        });

        holder.btnPlus.setOnClickListener(v -> {
            int current = product.getBookQuantity();
            int maxQty = product.getCurrentQty();

            if (current < maxQty) {
                product.setBookQuantity(current + 1);
                holder.txtBookQuantity.setText(String.valueOf(product.getBookQuantity()));

                if (checkListener != null && product.isSelected()) {
                    checkListener.onCartItemCheckedChanged();
                }
            } else {
                Toast.makeText(context, "Quantity chosen exceeded current stock", Toast.LENGTH_SHORT).show();
            }
        });



        holder.btnMinus.setOnClickListener(v -> {
            int current = product.getBookQuantity();
            if (current > 1) {
                product.setBookQuantity(current - 1);
                holder.txtBookQuantity.setText(String.valueOf(product.getBookQuantity()));

                if (checkListener != null && product.isSelected()) {
                    checkListener.onCartItemCheckedChanged();
                }
            }
            else {
                Toast.makeText(context, "Minimum quantity is 0", Toast.LENGTH_SHORT).show();
            }

        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Delete")
                    .setMessage("Remove this item from cart?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String userId = UserSession.getUserId(context);  // bạn đã có class này
                        String isbn = product.getBookISBN_n();

                        String url = ApiConnector.getDeleteCartItemUrl(userId, isbn);

                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.DELETE,
                                url,
                                null,
                                response -> {
                                    cartItems.remove(position); // xoá trong list
                                    notifyItemRemoved(position); // cập nhật UI
                                    Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
                                },
                                error -> {
                                    Toast.makeText(context, "Error deleting item", Toast.LENGTH_SHORT).show();
                                }
                        );

                        Volley.newRequestQueue(context).add(request);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        holder.chkSelectItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setSelected(isChecked);  // Bạn cần thêm trường selected vào Product model
            if (checkListener != null) {
                checkListener.onCartItemCheckedChanged();
            }
        });
        holder.chkSelectItem.setChecked(product.isSelected());


        holder.imgAddToWishlist.setOnClickListener(v -> {
            if (wishlistClickListener != null) {
                wishlistClickListener.onWishlistClick(cartItems.get(position));
            }
        });


        holder.imgProduct.setOnClickListener(v -> {
            if (imageClickListener != null) {
                imageClickListener.onImageClick(product);
            }
        });


    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox chkSelectItem;
        ImageView imgProduct, btnPlus, btnMinus, btnDelete, imgAddToWishlist;
        TextView txtProductTitle, txtOrgPrice, txtDiscountedPrice, txtSale, txtBookQuantity;


        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            chkSelectItem = itemView.findViewById(R.id.chkSelectItem);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductTitle = itemView.findViewById(R.id.txtProductTitle);
            txtOrgPrice = itemView.findViewById(R.id.txtOrgPrice);
            txtDiscountedPrice = itemView.findViewById(R.id.txtDiscountedPrice);
            txtSale = itemView.findViewById(R.id.txtSale);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.imgDeleteItem);
            txtBookQuantity = itemView.findViewById(R.id.txtBookQuantity);

            imgAddToWishlist = itemView.findViewById(R.id.imgAddToWishlist);


        }
    }

    public interface OnCartItemCheckListener {
        void onCartItemCheckedChanged();
    }

    private OnCartItemCheckListener checkListener;

    public void setOnCartItemCheckListener(OnCartItemCheckListener listener) {
        this.checkListener = listener;
    }

    public interface OnWishlistClickListener {
        void onWishlistClick(Product product);
    }


    private OnWishlistClickListener wishlistClickListener;

    public void setOnWishlistClickListener(OnWishlistClickListener listener) {
        this.wishlistClickListener = listener;
    }

    public interface OnImageClickListener {
        void onImageClick(Product product);
    }

    private OnImageClickListener imageClickListener;

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.imageClickListener = listener;
    }


}
