package com.example.sql;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {
    private final List<ImageItem> data;
    private final Context context;

    public HistoryAdapter(List<ImageItem> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_history, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ImageItem item = data.get(position);
        Glide.with(context)
                .load(item.url)
                .into(holder.historyImage);
        holder.historyDate.setText(item.date);
        // inside onBindViewHolder(...)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WallpaperActivity.class);
            intent.putExtra("image_url", data.get(position).url);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView historyImage;
        TextView historyDate;

        public VH(@NonNull View itemView) {
            super(itemView);
            historyImage = itemView.findViewById(R.id.historyImage);
            historyDate  = itemView.findViewById(R.id.historyDate);
        }
    }
}
