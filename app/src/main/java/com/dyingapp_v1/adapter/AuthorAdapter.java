package com.dyingapp_v1.adapter;

import android.content.Context;
import android.content.Intent; // <-- Thêm import
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyingapp_v1.R;
import com.dyingapp_v1.activity.AuthorDetailActivity; // <-- Thêm import
import com.dyingapp_v1.model.Author;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder> {

    private Context context;
    private List<Author> authorList;

    public AuthorAdapter(Context context, List<Author> authorList) {
        this.context = context;
        this.authorList = authorList;
    }

    @NonNull
    @Override
    public AuthorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_author_grid, parent, false);
        return new AuthorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorViewHolder holder, int position) {
        Author author = authorList.get(position);
        holder.txtAuthorName.setText(author.getAuthorName());
        Log.d("AuthorImageURL", "URL: '" + author.getAuthorImage() + "'");

        if (author.getAuthorImage() != null && !author.getAuthorImage().isEmpty()) {
            String imageUrl = author.getAuthorImage().trim();
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ava_placeholder)
                    .error(R.drawable.ava_placeholder)
                    .into(holder.imgAuthorAvatar);
        } else {
            holder.imgAuthorAvatar.setImageResource(R.drawable.ava_placeholder);
        }

        // --- SỬA LẠI ONCLICK LISTENER ---
        holder.itemView.setOnClickListener(v -> {
            // Tạo Intent để mở AuthorDetailActivity
            Intent intent = new Intent(context, AuthorDetailActivity.class);

            // Đặt ID của tác giả vào intent để màn hình sau có thể nhận
            intent.putExtra(AuthorDetailActivity.EXTRA_AUTHOR_ID, author.getId());

            // Khởi chạy Activity mới
            context.startActivity(intent);
        });
        // -------------------------------
    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    public static class AuthorViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgAuthorAvatar;
        TextView txtAuthorName;

        public AuthorViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAuthorAvatar = itemView.findViewById(R.id.imgAuthorAvatar);
            txtAuthorName = itemView.findViewById(R.id.txtAuthorName);
        }
    }
}
