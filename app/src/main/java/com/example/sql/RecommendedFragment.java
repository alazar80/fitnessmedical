package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * If PHP returns {"success":false} → show “You have not subscribed…” text.
 * Otherwise fetch “recommended” meals, then split them into five categories
 * and populate the five RecyclerViews (rvBreakfast, rvLunch, etc.).
 */
public class RecommendedFragment extends Fragment {
    private static final String ARG_USER_ID = "user_id";

    private int userId;
    private TextView tvNoTrainer;
    private LinearLayout llMealsContainer;

    // Five separate RecyclerViews and Adapters:
    private RecyclerView rvBreakfast, rvLunch, rvDinner, rvPreWorkout, rvPostWorkout;
    private MealAdapter breakfastAdapter, lunchAdapter, dinnerAdapter,
            preWorkoutAdapter, postWorkoutAdapter;

    // Temporaries to hold parsed meals:
    private final List<Meal> breakfastList   = new ArrayList<>();
    private final List<Meal> lunchList       = new ArrayList<>();
    private final List<Meal> dinnerList      = new ArrayList<>();
    private final List<Meal> preWorkoutList  = new ArrayList<>();
    private final List<Meal> postWorkoutList = new ArrayList<>();

    public static RecommendedFragment newInstance(int userId) {
        RecommendedFragment f = new RecommendedFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_USER_ID, userId);
        f.setArguments(b);
        return f;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommended, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1) Bind the “no trainer” TextView
        tvNoTrainer = view.findViewById(R.id.tvNoTrainer);

        // 2) Bind the LinearLayout that wraps all five RecyclerViews
        llMealsContainer = view.findViewById(R.id.llMealsContainer);

        // 3) Bind each of the five RecyclerViews
        rvBreakfast   = view.findViewById(R.id.rvBreakfast);
        rvLunch       = view.findViewById(R.id.rvLunch);
        rvDinner      = view.findViewById(R.id.rvDinner);
        rvPreWorkout  = view.findViewById(R.id.rvPreWorkout);
        rvPostWorkout = view.findViewById(R.id.rvPostWorkout);

        // 4) Set horizontal LayoutManager for each RecyclerView
        rvBreakfast  .setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvLunch      .setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvDinner     .setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPreWorkout .setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPostWorkout.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // 5) Read userId argument
        if (getArguments() != null) {
            userId = getArguments().getInt(ARG_USER_ID);
        }

        // 6) Fetch from server; if no doctor_id, show only tvNoTrainer.
        fetchRecommendedMealsFromServer();
    }

    private void fetchRecommendedMealsFromServer() {
        String url = ApiConfig.GET_RECOMMENDED_MEALS_BY_USER;

        // 1) Hide both tvNoTrainer and the entire container at first
        tvNoTrainer.setVisibility(View.GONE);
        llMealsContainer.setVisibility(View.GONE);

        // 2) Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // 3) Create the StringRequest
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    try {
                        JSONObject root = new JSONObject(response);
                        boolean ok = root.optBoolean("success", false);

                        if (!ok) {
                            // Server says “no doctor_id” → show only the “no trainer” text
                            tvNoTrainer.setVisibility(View.VISIBLE);
                            llMealsContainer.setVisibility(View.GONE);
                            return;
                        }

                        // success==true → parse the meals into each List<Meal>…
                        JSONArray arr = root.optJSONArray("meals");

                        // Clear previous lists
                        breakfastList.clear();
                        lunchList.clear();
                        dinnerList.clear();
                        preWorkoutList.clear();
                        postWorkoutList.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            Meal m = new Meal(
                                    obj.getInt("id"),
                                    obj.getString("title"),
                                    obj.getInt("calories"),
                                    obj.getString("description"),
                                    obj.getString("category"),
                                    obj.getString("fitnessGoal"),
                                    obj.getString("mealtype"),
                                    obj.getString("image_id")
                            );

                            // Categorize by m.getCategory()
                            String cat = m.getCategory().trim().toLowerCase();
                            switch (cat) {
                                case "breakfast":
                                    breakfastList.add(m);
                                    break;
                                case "lunch":
                                    lunchList.add(m);
                                    break;
                                case "dinner":
                                    dinnerList.add(m);
                                    break;
                                case "pre-workout":
                                    preWorkoutList.add(m);
                                    break;
                                case "post-workout":
                                    postWorkoutList.add(m);
                                    break;
                                default:
                                    break;
                            }
                        }

                        // 4) Show the container now that we have data
                        tvNoTrainer.setVisibility(View.GONE);
                        llMealsContainer.setVisibility(View.VISIBLE);

                        // 5) For each non-empty category, set adapter & make that RecyclerView visible
                        if (!breakfastList.isEmpty()) {
                            breakfastAdapter = new MealAdapter(getContext(), breakfastList, false, new MealAdapter.OnMealClickListener() {
                                @Override public void onEditClick(Meal meal) {}
                                @Override public void onDeleteClick(Meal meal) {}
                                @Override public void onItemClick(Meal meal) {
                                    showMealBottomSheet(meal); // ✅ This works now
                                }
                            });
                            rvBreakfast.setAdapter(breakfastAdapter);
                            rvBreakfast.setVisibility(View.VISIBLE);

                        }
                        if (!lunchList.isEmpty()) {
                            lunchAdapter = new MealAdapter(getContext(), lunchList, false, new MealAdapter.OnMealClickListener() {
                                @Override public void onEditClick(Meal meal) {}
                                @Override public void onDeleteClick(Meal meal) {}
                                @Override public void onItemClick(Meal meal) {
                                    showMealBottomSheet(meal);
                                }
                            });
                            rvLunch.setAdapter(lunchAdapter);
                            rvLunch.setVisibility(View.VISIBLE);
                        }
                        if (!dinnerList.isEmpty()) {
                            dinnerAdapter = new MealAdapter(getContext(), dinnerList, false, new MealAdapter.OnMealClickListener() {
                                @Override public void onEditClick(Meal meal) {}
                                @Override public void onDeleteClick(Meal meal) {}
                                @Override public void onItemClick(Meal meal) {
                                    showMealBottomSheet(meal);
                                }
                            });
                            rvDinner.setAdapter(dinnerAdapter);
                            rvDinner.setVisibility(View.VISIBLE);
                        }
                        if (!preWorkoutList.isEmpty()) {
                            preWorkoutAdapter = new MealAdapter(getContext(), preWorkoutList, false, new MealAdapter.OnMealClickListener() {
                                @Override public void onEditClick(Meal meal) {}
                                @Override public void onDeleteClick(Meal meal) {}
                                @Override public void onItemClick(Meal meal) {
                                    showMealBottomSheet(meal);
                                }
                            });
                            rvPreWorkout.setAdapter(preWorkoutAdapter);
                            rvPreWorkout.setVisibility(View.VISIBLE);
                        }
                        if (!postWorkoutList.isEmpty()) {
                            postWorkoutAdapter = new MealAdapter(getContext(), postWorkoutList, false, new MealAdapter.OnMealClickListener() {
                                @Override public void onEditClick(Meal meal) {}
                                @Override public void onDeleteClick(Meal meal) {}
                                @Override public void onItemClick(Meal meal) {
                                    showMealBottomSheet(meal);
                                }
                            });
                            rvPostWorkout.setAdapter(postWorkoutAdapter);
                            rvPostWorkout.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.show(getContext(),
                                "Error parsing response", 1/3);
                        // On parse error, show only “no trainer”
                        tvNoTrainer.setVisibility(View.VISIBLE);
                        llMealsContainer.setVisibility(View.GONE);
                    }
                },
                error -> {
                    error.printStackTrace();
                    ToastUtil.show(getContext(),
                            "Network error loading recommended meals", 1/3);
                    // On network error, show only “no trainer”
                    tvNoTrainer.setVisibility(View.VISIBLE);
                    llMealsContainer.setVisibility(View.GONE);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };

        // 4) Add the request to the queue
        queue.add(request);
    }
    private void showMealBottomSheet(Meal meal) {
        String imageUrl = ApiConfig.GET_MEAL_ICON_ID + meal.getImageId() + ".jpg";

        MealDetailBottomSheet sheet = MealDetailBottomSheet.newInstance(
                meal.getTitle(),
                meal.getDescription(),
                imageUrl,
                meal.getCalories()
        );

        sheet.show(getParentFragmentManager(), "MealDetail");
    }


    // If you need SearchView filtering, call filter(...) on each non-null adapter similarly:
    public void filter(String query) {
        if (breakfastAdapter  != null) breakfastAdapter.getFilter().filter(query);
        if (lunchAdapter      != null) lunchAdapter.getFilter().filter(query);
        if (dinnerAdapter     != null) dinnerAdapter.getFilter().filter(query);
        if (preWorkoutAdapter != null) preWorkoutAdapter.getFilter().filter(query);
        if (postWorkoutAdapter!= null) postWorkoutAdapter.getFilter().filter(query);
    }
}
