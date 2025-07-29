package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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

public class EnduranceImprovementExercise extends AppCompatActivity {

    private RecyclerView recyclerViewExercises, recyclerViewWarmExercises;
    private ExerciseAdapter exerciseAdapter, warmUpAdapter;
    private List<Exercise> exerciseList, warmUpList;
    private SwitchCompat warmUpSwitch;
    private Button startButton;
    public int userId;
    private String fitnessGoal;
    private List<String> gifUrlList;  // For GIF links or local references

    private final String URL = ApiConfig.GET_EXERCISE_BY_USER_FOR_GYM; // Change this!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnew);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        gifUrlList = new ArrayList<>();



        userId = getIntent().getIntExtra("user_id", -1);

        if (userId == -1) {
            ToastUtil.show(this, "Missing user ID", 1/3);
            finish();
            return;
        }


        initViews();

        exerciseList = new ArrayList<>();
        warmUpList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(exerciseList, false,userId);
        warmUpAdapter = new ExerciseAdapter(warmUpList, false,userId);



        recyclerViewExercises.setAdapter(exerciseAdapter);
        recyclerViewWarmExercises.setAdapter(warmUpAdapter);

        fetchAllExercises();

        warmUpSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                recyclerViewWarmExercises.setVisibility(isChecked ? View.VISIBLE : View.GONE)
        );

        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity2.class); // ✅ Send to workout screen
            boolean isWarmUpEnabled = warmUpSwitch.isChecked();

            if (isWarmUpEnabled) {
                List<Exercise> combinedList = new ArrayList<>();
                combinedList.addAll(warmUpList);
                combinedList.addAll(exerciseList);
                intent.putParcelableArrayListExtra("exerciseList", new ArrayList<>(combinedList));
            } else {
                intent.putParcelableArrayListExtra("exerciseList", new ArrayList<>(exerciseList));
            }


            intent.putExtra("user_id", userId);
            intent.putExtra("isWarmUpEnabled", isWarmUpEnabled);
            startActivity(intent);
        });


    }

    private void initViews() {
        recyclerViewExercises = findViewById(R.id.recyclerViewExercises);
        recyclerViewWarmExercises = findViewById(R.id.recyclerViewWarmExercises);
        warmUpSwitch = findViewById(R.id.warmUpSwitch);
        startButton = findViewById(R.id.startButton);

        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWarmExercises.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWarmExercises.setVisibility(View.GONE);
    }
    private void fetchAllExercises() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    try {
                        Log.d("EXERCISE_RESPONSE", "Raw response: " + response);

                        JSONObject json = new JSONObject(response);
                        boolean success = json.optBoolean("success", false);
                        if (!success) {
                            ToastUtil.show(this, "Failed to fetch exercises", 1/3);
                            return;
                        }

                        fitnessGoal = json.optString("fitnessGoal", "Unknown");
                        JSONArray typesArray = json.optJSONArray("types");
                        if (typesArray != null) {
                            List<String> allTypes = new ArrayList<>();
                            for (int i = 0; i < typesArray.length(); i++) {
                                allTypes.add(typesArray.getString(i));
                            }
                            Log.d("ALL_TYPES", "Received types: " + allTypes);
                        }

                        JSONArray array = json.getJSONArray("exercises");
                        gifUrlList = new ArrayList<>(); // 🔥 Initialize gif list here

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            Exercise ex = new Exercise(
                                    obj.getString("name"),
                                    obj.getString("duration"),
                                    obj.getString("type"),
                                    obj.getString("description"),
                                    obj.getString("fitnessGoal"),
                                    obj.getString("experienceLevel"),
                                    obj.getString("icon_id")
                            );

                            // Collect GIF URL
                            String gifUrl = obj.optString("gif_url", "").trim();
                            if (!gifUrl.isEmpty()) {
                                gifUrlList.add(gifUrl);
                                Log.d("GIF_URL", "Added: " + gifUrl);
                            }

                            String type = ex.getType().toLowerCase().trim();
                            if (type.equals("warm-up") || type.contains("warm")) {
                                warmUpList.add(ex);
                                Log.d("SPLIT", "Added to warm-up: " + ex.getName());
                            } else if (type.equals("main") || type.contains("main")) {
                                exerciseList.add(ex);
                                Log.d("SPLIT", "Added to main: " + ex.getName());
                            }
                        }

                        warmUpAdapter.notifyDataSetChanged();
                        exerciseAdapter.notifyDataSetChanged();

//                        int total = exerciseList.size() + warmUpList.size();
//                        String message = "Goal: " + fitnessGoal + ", Total: " + total;
//                        ToastUtil.show(newnew.this, message, 1/3);

                    } catch (Exception e) {
                        Log.e("EXERCISE_PARSE", "JSON error", e);
                        ToastUtil.show(this, "Parse error", 1/3);
                    }
                },
                error -> {
                    Log.e("EXERCISE_REQUEST", "Volley error", error);
                    ToastUtil.show(this, "Network error", 1/3);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };

        queue.add(request);
    }

    private void showExerciseBottomSheet(Exercise exercise) {
        // Build full GIF URL from iconId
        String gifUrl = ApiConfig.GET_EXERCISE_ICON_ID + exercise.getIconId() + ".gif";

        // You need to modify ExerciseDetailActivity to accept URL instead of drawable
        ExerciseDetailActivity sheet = ExerciseDetailActivity.newInstance(
                exercise.getName(),
                exercise.getDescription(),
                gifUrl
        );

        sheet.show(getSupportFragmentManager(), "ExerciseDetail");
    }


}
