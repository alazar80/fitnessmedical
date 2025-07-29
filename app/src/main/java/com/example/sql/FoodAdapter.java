package com.example.sql;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FoodAdapter extends BaseAdapter {
    private Context ctx;
    private List<Food> list;
    public FoodAdapter(Context c, List<Food> l) {
        ctx = c; list = l;
    }
    @Override public int getCount() { return list.size(); }
    @Override public Food getItem(int i) { return list.get(i); }
    @Override public long getItemId(int i) { return i; }
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(ctx)
                .inflate(R.layout.item_food, parent, false);
        ImageView img = convertView.findViewById(R.id.imgFood);
        TextView name = convertView.findViewById(R.id.txtName);
        Food f = getItem(pos);
        name.setText(f.name);
        Glide.with(ctx)
             .load(f.imageUrl)
             .placeholder(R.drawable.ic_default_profile)
             .into(img);
        return convertView;
    }
}
