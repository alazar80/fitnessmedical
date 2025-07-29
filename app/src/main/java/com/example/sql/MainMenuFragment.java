    // MainMenuFragment.java
    package com.example.sql;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;
    import com.android.volley.RequestQueue;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;

    import org.json.JSONArray;
    import org.json.JSONObject;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    // … your imports …

    public class MainMenuFragment extends Fragment {
        private static final String ARG_USER_ID = "user_id";
        private int userId;

        // Adapters
        private MealAdapter breakfastAdapter,
                lunchAdapter,
                dinnerAdapter,
                preWorkoutAdapter,
                postWorkoutAdapter;

        private RecyclerView rvBreakfast, rvLunch, rvDinner, rvPreWorkout, rvPostWorkout;

        public static MainMenuFragment newInstance(int userId) {
            MainMenuFragment f = new MainMenuFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_USER_ID, userId);
            f.setArguments(b);
            return f;
        }

        @Nullable @Override
        public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
            View v = inf.inflate(R.layout.fragment_main_menu, c, false);
            if (getArguments()!=null) userId = getArguments().getInt(ARG_USER_ID);

            rvBreakfast   = v.findViewById(R.id.rvBreakfast);
            rvLunch       = v.findViewById(R.id.rvLunch);
            rvDinner      = v.findViewById(R.id.rvDinner);
            rvPreWorkout  = v.findViewById(R.id.rvPreWorkout);
            rvPostWorkout = v.findViewById(R.id.rvPostWorkout);

            LinearLayoutManager horz = new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false);
            rvBreakfast.setLayoutManager(horz);
            rvLunch.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            rvDinner.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            rvPreWorkout.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            rvPostWorkout.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));

            fetchMealsFromServer();
            return v;
        }

        private void fetchMealsFromServer() {
            String url = ApiConfig.GET_MAIN_MEAL_BY_USER;
            RequestQueue q = Volley.newRequestQueue(requireContext());
            StringRequest r = new StringRequest(com.android.volley.Request.Method.POST, url,
                    resp -> {
                        try {
                            JSONObject j = new JSONObject(resp);
                            if (!j.optBoolean("success",false)) {
                                ToastUtil.show(getContext(),"Load failed",1/3);
                                return;
                            }
                            JSONArray a = j.optJSONArray("meals");
                            List<Meal> bfst=new ArrayList<>(), lch=new ArrayList<>(),
                                    dnr=new ArrayList<>(), pre=new ArrayList<>(), post=new ArrayList<>();
                            for(int i=0;i<a.length();i++){
                                JSONObject o=a.getJSONObject(i);
                                Meal m=new Meal(
                                        o.getInt("id"),
                                        o.getString("title"),
                                        o.getInt("calories"),
                                        o.getString("description"),
                                        o.getString("category"),
                                        o.getString("fitnessGoal"),
                                        o.getString("mealtype"),
                                        o.getString("image_id")
                                );
                                switch(m.getCategory().toLowerCase().trim()){
                                    case "breakfast":    bfst.add(m); break;
                                    case "lunch":        lch.add(m);  break;
                                    case "dinner":       dnr.add(m);  break;
                                    case "pre-workout":  pre.add(m);  break;
                                    case "post-workout": post.add(m); break;
                                }
                            }
                            breakfastAdapter = new MealAdapter(getContext(), bfst, false, new MealAdapter.OnMealClickListener() {
                                @Override public void onEditClick(Meal m) {}
                                @Override public void onDeleteClick(Meal m) {}
                                @Override public void onItemClick(Meal m) {
                                    showMealBottomSheet(m);
                                }
                            });

                            lunchAdapter = new MealAdapter(getContext(), lch, false, new MealAdapter.OnMealClickListener() {
                                @Override public void onEditClick(Meal m) {}
                                @Override public void onDeleteClick(Meal m) {}
                                @Override public void onItemClick(Meal m) {
                                    showMealBottomSheet(m);
                                }
                            });

                            dinnerAdapter = new MealAdapter(getContext(), dnr, false, new MealAdapter.OnMealClickListener() {
                                @Override public void onEditClick(Meal m) {}
                                @Override public void onDeleteClick(Meal m) {}
                                @Override public void onItemClick(Meal m) {
                                    showMealBottomSheet(m);
                                }
                            });

                            preWorkoutAdapter = new MealAdapter(getContext(), pre, false, new MealAdapter.OnMealClickListener() {
                                @Override public void onEditClick(Meal m) {}
                                @Override public void onDeleteClick(Meal m) {}
                                @Override public void onItemClick(Meal m) {
                                    showMealBottomSheet(m);
                                }
                            });

                            postWorkoutAdapter = new MealAdapter(getContext(), post, false, new MealAdapter.OnMealClickListener() {
                                @Override public void onEditClick(Meal m) {}
                                @Override public void onDeleteClick(Meal m) {}
                                @Override public void onItemClick(Meal m) {
                                    showMealBottomSheet(m);
                                }
                            });


                            rvBreakfast.setAdapter(breakfastAdapter);
                            rvLunch.setAdapter(lunchAdapter);
                            rvDinner.setAdapter(dinnerAdapter);
                            rvPreWorkout.setAdapter(preWorkoutAdapter);
                            rvPostWorkout.setAdapter(postWorkoutAdapter);
                        } catch(Exception e){ e.printStackTrace(); }
                    },
                    err -> err.printStackTrace()
            ){
                @Override protected Map<String,String> getParams(){
                    Map<String,String> p=new HashMap<>();
                    p.put("user_id", String.valueOf(userId));
                    return p;
                }
            };
            q.add(r);
        }

        // Called from DietDay’s SearchView
        public void filter(String query) {
            if (breakfastAdapter   != null) breakfastAdapter.getFilter().filter(query);
            if (lunchAdapter       != null) lunchAdapter.getFilter().filter(query);
            if (dinnerAdapter      != null) dinnerAdapter.getFilter().filter(query);
            if (preWorkoutAdapter  != null) preWorkoutAdapter.getFilter().filter(query);
            if (postWorkoutAdapter != null) postWorkoutAdapter.getFilter().filter(query);
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

    }
