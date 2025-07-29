package com.example.sql;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExercisePartitionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ExerciseAdapter adapter;

    private final int userId = 2; // Change as needed
    private Spinner userSpinner;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_partition);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userSpinner = findViewById(R.id.userSpinner);
        fetchUsers();

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected user
                User selectedUser = userList.get(position);
                int userId = selectedUser.getId();
                // Fetch exercises for this user
                fetchExercisesFromApi(userId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        fetchExercisesFromApi(userId);
    }

    private void fetchExercisesFromApi(int userId) {
        progressBar.setVisibility(View.VISIBLE);

        String url = ApiConfig.BASE_URL+"view.php"; // CHANGE THIS

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    List<Exercise> allExercises = parseExerciseJson(response);
                    Map<Integer, List<Exercise>> partitioned = partitionByUserId(allExercises);
                    List<Exercise> userExercises = partitioned.get(userId);

                    if (userExercises == null || userExercises.isEmpty()) {
                        ToastUtil.show(this, "No exercises for user " + userId, 1/3);
                        recyclerView.setAdapter(null);
                    } else {
                        adapter = new ExerciseAdapter(userExercises, true, null, userId);
                        recyclerView.setAdapter(adapter);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    String errMsg = error.getMessage();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errMsg += "\n" + new String(error.networkResponse.data);
                    }
                    ToastUtil.show(this, "Volley Error: " + errMsg, 1/3);
                }

        );

        queue.add(jsonArrayRequest);
    }

    private List<Exercise> parseExerciseJson(JSONArray jsonArr) {
        List<Exercise> exercises = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);

                Exercise exercise = new Exercise();
                exercise.setId(obj.optInt("id", 0));
                exercise.setUserId(obj.optInt("userId", 0));
                exercise.setName(obj.optString("exerciseName", ""));
                exercise.setDuration(obj.optString("duration", ""));
                exercise.setType(obj.optString("type", ""));
                exercise.setDescription(obj.optString("description", ""));
                exercise.setFitnessGoal(obj.optString("fitnessGoal", ""));
                exercise.setExperienceLevel(obj.optString("experienceLevel", ""));
                exercise.setIconId(obj.optString("iconId", ""));

                exercises.add(exercise);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exercises;
    }


    private void fetchUsers() {
        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.USERS_API,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        userList.clear();
                        List<String> userNames = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            User user = new User();
                            user.setId(obj.getInt("id"));
                            user.setUsername(obj.getString("username"));
                            userList.add(user);
                            userNames.add(user.getUsername());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, userNames);
                        userSpinner.setAdapter(adapter);
                    } catch (Exception e) {
                        ToastUtil.show(this, "User parsing failed", 1/3);
                    }
                },
                error -> ToastUtil.show(this, "Failed to load users", 1/3)
        );

        Volley.newRequestQueue(this).add(request);
    }
    private Map<Integer, List<Exercise>> partitionByUserId(List<Exercise> allExercises) {
        Map<Integer, List<Exercise>> partitioned = new HashMap<>();
        for (Exercise ex : allExercises) {
            int userId = ex.getUserId();
            if (!partitioned.containsKey(userId)) {
                partitioned.put(userId, new ArrayList<>());
            }
            partitioned.get(userId).add(ex);
        }
        return partitioned;
    }
}
