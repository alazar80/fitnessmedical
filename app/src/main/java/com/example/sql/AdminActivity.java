package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";

    private TextView txtUsers, txtSubscribed, txtDoctors, txtEligible;
    private TextView txtAssignedExercises, txtAssignedMeals;
    private ProgressBar loadingBar;

    private static final String URL_GET_USERS = ApiConfig.GET_USERS_URL;
    private static final String URL_GET_DOCTORS = ApiConfig.GET_DOCTORS_URL;
private int adminId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        adminId = getIntent().getIntExtra("admin_id", -1);
        if (adminId == -1) {
            ToastUtil.show(this, "Invalid Admin ID", 1/3);
            finish();
            return;
        }
        initViews();
        setupButtonClicks();
        loadDashboardStats();
    }

    private void initViews() {
        txtUsers = findViewById(R.id.txtTotalUsers);
        txtSubscribed = findViewById(R.id.txtSubscribed);
        txtDoctors = findViewById(R.id.txtDoctors);
        txtEligible = findViewById(R.id.txtEligible);
        txtAssignedExercises = findViewById(R.id.txtAssignedExercises);
        txtAssignedMeals = findViewById(R.id.txtAssignedMeals);
        loadingBar = findViewById(R.id.loadingBar);
    }

    private void setupButtonClicks() {
        setClickListener(R.id.btnManageUsers, Manageuser.class);
        setClickListener(R.id.btnManageDoctors, Managedoctors.class);
        setClickListener(R.id.btnManageExercises, AdminManageexercise.class);
        setClickListener(R.id.btnManageMeals, AdminManagemeal.class);
        setClickListener(R.id.btnManageFeedbacks, Showfeedbacks.class);
    }

    private void setClickListener(int buttonId, Class<?> targetActivity) {
        findViewById(buttonId).setOnClickListener(v -> openActivity(targetActivity));
    }

    private void openActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("admin_id", adminId);
        startActivity(intent);
    }


    private void loadDashboardStats() {
        loadingBar.setVisibility(View.VISIBLE);
        getUserStats();
        getDoctorStats();
        fetchExerciseStats();
        fetchMealStats();
        fetchTotalExerciseCount();
        fetchTotalMealCount();

    }

    private void getUserStats() {
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_USERS, response -> {
            try {
                JSONArray array = new JSONArray(response);
                int totalUsers = array.length();
                int subscribed = 0;

                for (int i = 0; i < array.length(); i++) {
                    JSONObject user = array.getJSONObject(i);
                    if (!user.isNull("doctor_id")) {
                        subscribed++;
                    }
                }

                txtUsers.setText(String.valueOf(totalUsers));
                txtSubscribed.setText(String.valueOf(subscribed));
            } catch (Exception e) {
                Log.e(TAG, "User JSON parsing error: ", e);
            }
        }, error -> Log.e(TAG, "User request error: ", error));

        Volley.newRequestQueue(this).add(request);
    }

    private void getDoctorStats() {
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_DOCTORS, response -> {
            try {
                JSONArray array = new JSONArray(response);
                int totalDoctors = array.length();
                int eligible = 0;

                for (int i = 0; i < array.length(); i++) {
                    JSONObject doc = array.getJSONObject(i);
                    if (doc.has("is_eligible") && "yes".equalsIgnoreCase(doc.getString("is_eligible"))) {
                        eligible++;
                    }
                }

                txtDoctors.setText(String.valueOf(totalDoctors));
                txtEligible.setText(String.valueOf(eligible));
            } catch (Exception e) {
                Log.e(TAG, "Doctor JSON parsing error: ", e);
            } finally {
                loadingBar.setVisibility(View.GONE);
            }
        }, error -> {
            Log.e(TAG, "Doctor request error: ", error);
            loadingBar.setVisibility(View.GONE);
        });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchExerciseStats() {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.GET_EXERCISE_COUNT_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            txtAssignedExercises.setText(String.valueOf(obj.getInt("assigned_exercises")));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exercise count parsing error: ", e);
                    }
                }, error -> Log.e(TAG, "Exercise count error: ", error)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", "1");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchMealStats() {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.GET_MEAL_COUNT_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            txtAssignedMeals.setText(String.valueOf(obj.getInt("assigned_meals")));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Meal count parsing error: ", e);
                    }
                }, error -> Log.e(TAG, "Meal count error: ", error)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", "1");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchTotalExerciseCount() {
        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.GET_EXERCISES_URL + "?user_id=1",
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONArray assigned = obj.getJSONArray("assigned");
                            JSONArray notAssigned = obj.getJSONArray("not_assigned");
                            int total = assigned.length() + notAssigned.length();

                            ((TextView) findViewById(R.id.txtAppointments)).setText(String.valueOf(total));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Total exercise count error: ", e);
                    }
                }, error -> Log.e(TAG, "Total exercise fetch error: ", error));

        Volley.newRequestQueue(this).add(request);
    }


    private void fetchTotalMealCount() {
        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.GET_MEALS_URL + "?user_id=1",
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONArray assigned = obj.getJSONArray("assigned");
                            JSONArray notAssigned = obj.getJSONArray("not_assigned");
                            int total = assigned.length() + notAssigned.length();

                            ((TextView) findViewById(R.id.txtFeedback)).setText(String.valueOf(total));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Total meal count error: ", e);
                    }
                }, error -> Log.e(TAG, "Total meal fetch error: ", error));

        Volley.newRequestQueue(this).add(request);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}
