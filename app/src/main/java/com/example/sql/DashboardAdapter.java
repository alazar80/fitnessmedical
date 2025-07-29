package com.example.sql;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DashboardAdapter
        extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(DashboardItem item);
    }

    private final List<DashboardItem> items;
    private final OnItemClickListener listener;

    public DashboardAdapter(List<DashboardItem> items,
                            OnItemClickListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        DashboardItem item = items.get(pos);
        h.title.setText(item.getTitle());
        h.value.setText(String.valueOf(item.getValue()));
        h.icon.setImageResource(item.getIconRes());
        h.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView title;
        final TextView  value;

        ViewHolder(View itemView) {
            super(itemView);
            icon  = itemView.findViewById(R.id.ivIcon);
            title = itemView.findViewById(R.id.tvTitle);
            value = itemView.findViewById(R.id.tvValue);
        }
    }
}

