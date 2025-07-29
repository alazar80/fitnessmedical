package com.example.sql;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Adapter for both read-only (item_meal.xml) and doctor-edit (item_meals_edit_delete.xml) cards */
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder>
        implements Filterable {

    public interface OnMealClickListener {
        void onEditClick(Meal meal);
        void onDeleteClick(Meal meal);
        void onItemClick(Meal meal);
    }

    private final Context context;
    private final List<Meal> fullList;
    private final List<Meal> filteredList;
    private final boolean isEditable;
    private final OnMealClickListener listener;

    public MealAdapter(Context context, List<Meal> meals) {
        this(context, meals, false, null);
    }

    public MealAdapter(Context context,
                       List<Meal> meals,
                       boolean isEditable,
                       OnMealClickListener listener) {
        this.context      = context;
        this.fullList     = meals;
        this.filteredList = new ArrayList<>(meals);
        this.isEditable   = isEditable;
        this.listener     = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = isEditable
                ? R.layout.item_meals_edit_delete
                : R.layout.item_meal;
        View v = LayoutInflater.from(context)
                .inflate(layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Meal m = filteredList.get(pos);

        // Title & calories
        h.title.setText(m.getTitle());
        h.calories.setText(m.getCalories() + " kcal");

        // Image (handles both item_meal:id/mealImage and edit layout:id/mealIcon)
        Glide.with(context)
                .load(ApiConfig.GET_MEAL_ICON_ID + m.getImageId() + ".jpg")
                .placeholder(R.drawable.ic_plate_fork)
                .into(h.image);

        // Doctor-mode buttons
        if (isEditable && h.editButton != null && h.deleteButton != null && listener != null) {
            h.editButton.setOnClickListener(__ -> listener.onEditClick(m));
            h.deleteButton.setOnClickListener(__ -> listener.onDeleteClick(m));
        }
        if (!isEditable && listener != null) {
            h.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(m);
            });

        }

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence cs) {
                String q = cs == null ? "" : cs.toString().toLowerCase().trim();
                List<Meal> res = new ArrayList<>();
                if (q.isEmpty()) {
                    res.addAll(fullList);
                } else {
                    for (Meal m : fullList) {
                        if (m.getTitle().toLowerCase().contains(q) ||
                                m.getDescription().toLowerCase().contains(q) ||
                                m.getCategory().toLowerCase().contains(q)) {
                            res.add(m);
                        }

                    }
                }
                FilterResults fr = new FilterResults();
                fr.values = res;
                return fr;
            }
            @Override @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence cs, FilterResults fr) {
                filteredList.clear();
                filteredList.addAll((List<Meal>) fr.values);
                notifyDataSetChanged();
            }
        };
    }

    public void updateData(List<Meal> newMeals) {
        fullList.clear();
        fullList.addAll(newMeals);
        filteredList.clear();
        filteredList.addAll(newMeals);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  title, calories;
        ImageView image;
        Button    editButton, deleteButton;

        ViewHolder(@NonNull View v) {
            super(v);

            // Title: item_meal uses @+id/mealTitle, edit layout uses @+id/mealName
            title = v.findViewById(R.id.mealTitle);
            if (title == null) title = v.findViewById(R.id.mealName);

            // Calories: edit layout uses @+id/mealCalories, item_meal uses @+id/calories
            calories = v.findViewById(R.id.mealCalories);
            if (calories == null) calories = v.findViewById(R.id.calories);

            // Image: item_meal uses @+id/mealImage, edit layout uses @+id/mealIcon
            image = v.findViewById(R.id.mealImage);
            if (image == null) image = v.findViewById(R.id.mealIcon);

            // Doctor-mode buttons (only in editable layout)
            editButton   = v.findViewById(R.id.editButton);
            deleteButton = v.findViewById(R.id.deleteButton);
        }

    }

}
