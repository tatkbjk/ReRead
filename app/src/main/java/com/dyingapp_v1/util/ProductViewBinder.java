package com.dyingapp_v1.util;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyingapp_v1.R;
import com.dyingapp_v1.model.Product;
import com.squareup.picasso.Picasso;

public class ProductViewBinder {
    public static void bindProduct(Product product,
                                   TextView titleView,
                                   TextView originalPriceView,
                                   TextView discountedPriceView,
                                   TextView discountTagView,
                                   TextView stockView,
                                   ImageView imageView) {

        titleView.setText(product.getBookTitle());
        stockView.setText(String.valueOf(product.getCurrentQty()));

        if (product.hasDiscount()) {
            originalPriceView.setVisibility(View.VISIBLE);
            discountedPriceView.setVisibility(View.VISIBLE);
            discountTagView.setVisibility(View.VISIBLE);

            originalPriceView.setText(String.format("%,d đ", product.getBookPrice()).replace(",", "."));
            discountedPriceView.setText(String.format("%,d đ", product.getDiscountedPrice()).replace(",", "."));
            discountTagView.setText("-" + product.getBookSales() + "%");
        } else {
            originalPriceView.setVisibility(View.GONE);
            discountedPriceView.setVisibility(View.VISIBLE);
            discountTagView.setVisibility(View.GONE);

            // Gán giá gốc vào ô discounted
            discountedPriceView.setText(String.format("%,d đ", product.getBookPrice()).replace(",", "."));
        }

        Picasso.get().load("https://ik.imagekit.io/reRead2025/" + product.getBookImg1())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(imageView);
    }
}
