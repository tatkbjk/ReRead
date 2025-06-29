package com.dyingapp_v1.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dyingapp_v1.R;
import com.dyingapp_v1.connector.ApiConnector;
import com.dyingapp_v1.model.Quotation;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuotationAdapter extends RecyclerView.Adapter<QuotationAdapter.ViewHolder> {

    private List<Quotation> quotationList;

    public QuotationAdapter(List<Quotation> quotationList) {
        this.quotationList = quotationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quotation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quotation q = quotationList.get(position);
        holder.txtTitle.setText(q.getTitle());
        holder.txtAuthor.setText("Author: " + q.getAuthor());
        holder.txtPublisher.setText("Publisher: " + q.getPublisher());
        holder.txtNote.setText("Note: " + q.getNote());
        holder.txtPrice.setText(q.getPrice()+"đ");
        holder.txtStatus.setText(q.getStatus());
        holder.txtCondition.setText("Condition: " + q.getCondition());

        // ✅ Change background color based on status
        String status = q.getStatus();
        int color;

        // Set default enabled = false
        holder.btnAccept.setEnabled(false);
        holder.btnRefuse.setEnabled(false);

        // Set default gray color
        holder.btnAccept.setBackgroundColor(android.graphics.Color.LTGRAY);
        holder.btnRefuse.setBackgroundColor(android.graphics.Color.LTGRAY);

        if (status.equalsIgnoreCase("This quotation item has been accepted, confirm shipping?")) {
            color = android.graphics.Color.parseColor("#F5F1E1");

            // ✅ Cả 2 nút được phép nhấn
            holder.btnAccept.setEnabled(true);
            holder.btnRefuse.setEnabled(true);

            // ✅ Đổi màu cho biết là khả dụng (nếu muốn)
            holder.btnAccept.setBackgroundColor(android.graphics.Color.parseColor("#0B7E43"));
            holder.btnRefuse.setBackgroundColor(android.graphics.Color.parseColor("#381319"));

            holder.btnAccept.setOnClickListener(v -> {
                showConfirmationDialog(holder.itemView.getContext(), q, position, "accept");
            });

            holder.btnRefuse.setOnClickListener(v -> {
                showConfirmationDialog(holder.itemView.getContext(), q, position, "refuse");
            });

        } else if (status.equalsIgnoreCase("Pending already requested!")) {
            color = android.graphics.Color.parseColor("#D9D9D9");

            // ✅ Chỉ nút Refuse được phép nhấn
            holder.btnRefuse.setEnabled(true);
            holder.btnRefuse.setBackgroundColor(android.graphics.Color.parseColor("#F44336")); // đỏ

            holder.btnRefuse.setOnClickListener(v -> {
                showConfirmationDialog(holder.itemView.getContext(), q, position, "refuse");
            });

        } else if (status.equalsIgnoreCase("Successfully sold item!")) {
            color = android.graphics.Color.parseColor("#D1E2D8");
        } else {
            color = android.graphics.Color.WHITE;
        }

        holder.itemView.setBackgroundColor(color);

        if (q.getImgBook() != null && !q.getImgBook().isEmpty()) {
            byte[] decodedString = Base64.decode(q.getImgBook(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imgBook.setImageBitmap(decodedByte);
        }

    }
    private void showConfirmationDialog(Context context, Quotation q, int position, String action) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to " + action + " this quotation?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (action.equals("accept")) {
                        updateQuotationStatus(context, q.getId(), "Successfully sold item!", position);
                    } else if (action.equals("refuse")) {
                        deleteQuotationFromServer(context, q.getId(), position);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void updateQuotationStatus(Context context, String quotationId, String newStatus, int position) {
        OkHttpClient client = new OkHttpClient();

        String json = "{\"status\":\"" + newStatus + "\"}";

        RequestBody body = RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                json
        );

        Request request = new Request.Builder()
                .url(ApiConnector.UPDATE_QUOTATION_STATUS_URL + "/" + quotationId + "/status")
                .patch(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        quotationList.get(position).setStatus(newStatus);
                        notifyItemChanged(position);
                    });
                }
            }
        });
    }


    private void deleteQuotationFromServer(Context context, String quotationId, int position) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(ApiConnector.DELETE_QUOTATION_URL + "/" + quotationId)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        quotationList.remove(position);
                        notifyItemRemoved(position);
                    });
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return quotationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStatus, txtTitle, txtNote, txtAuthor, txtCondition, txtPublisher, txtPrice;
        ImageView imgBook;
        Button btnAccept, btnRefuse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgLogo);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtNote = itemView.findViewById(R.id.txtNote);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtCondition = itemView.findViewById(R.id.txtCondition);
            txtPublisher = itemView.findViewById(R.id.txtPublisher);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnRefuse = itemView.findViewById(R.id.btnRefuse);
        }
    }
}