package com.example.sql;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FoodListFragment extends Fragment {
    private static final String ARG_MEALS = "meal_list";
    private ArrayList<Meal> mealList;
    private MealAdapter adapter;

    public static FoodListFragment newInstance(ArrayList<Meal> list) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_MEALS, list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mealList = getArguments().getParcelableArrayList(ARG_MEALS);
        } else {
            mealList = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_list, container, false);
        RecyclerView rv = view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MealAdapter(getContext(), mealList);
        rv.setAdapter(adapter);
        return view;
    }

    public void filter(String text) {
        adapter.getFilter().filter(text);
    }
}
