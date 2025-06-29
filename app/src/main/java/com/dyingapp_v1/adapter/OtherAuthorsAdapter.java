package com.dyingapp_v1.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dyingapp_v1.R;
import com.dyingapp_v1.activity.AuthorDetailActivity;
import com.dyingapp_v1.model.Author;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class OtherAuthorsAdapter extends RecyclerView.Adapter<OtherAuthorsAdapter.ViewHolder> {

    private Context context;
    private List<Author> authorList;

    public OtherAuthorsAdapter(Context context, List<Author> authorList) {
        this.context = context;
        this.authorList = authorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout item_author_circle.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_author_circle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Author author = authorList.get(position);
        holder.txtAuthorName.setText(author.getAuthorName());

        if (author.getAuthorImage() != null && !author.getAuthorImage().isEmpty()) {
            // Dùng trim() để xóa khoảng trắng hoặc ký tự lạ ở đầu/cuối URL
            String imageUrl = author.getAuthorImage().trim();
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ava_placeholder)
                    .error(R.drawable.ava_placeholder)
                    .into(holder.imgAuthorAvatar);
        } else {
            holder.imgAuthorAvatar.setImageResource(R.drawable.ava_placeholder);
        }

        // Bắt sự kiện click để mở trang chi tiết của tác giả khác
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AuthorDetailActivity.class);
            intent.putExtra(AuthorDetailActivity.EXTRA_AUTHOR_ID, author.getId());
            // Thêm flag để xóa activity cũ và tạo mới, tránh bị chồng chéo
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgAuthorAvatar;
        TextView txtAuthorName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAuthorAvatar = itemView.findViewById(R.id.imgAuthorAvatar);
            txtAuthorName = itemView.findViewById(R.id.txtAuthorName);
        }
    }
}
