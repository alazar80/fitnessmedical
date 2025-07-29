package com.example.sql;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DoctorAssignMealActivity extends AppCompatActivity {

    private Spinner userSpinner;
    private ListView mealListView;
    private Button assignButton;

    private List<User> userList = new ArrayList<>();
    private List<Meal> mealList = new ArrayList<>();

   int docId;
    String doctorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_assign_meal);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        docId = getIntent().getIntExtra("doctor_id", -1);
        doctorId= String.valueOf(docId);
        userSpinner = findViewById(R.id.userSpinner);
        mealListView = findViewById(R.id.mealListView);
        assignButton = findViewById(R.id.assignButton);

        fetchUsers();
        fetchMeals(doctorId);
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
        assignButton.setOnClickListener(v -> assignSelectedMeals());
    }

    private void fetchUsers() {
        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.USERS_API,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        List<String> usernames = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            User user = new User();
                            user.setId(obj.getInt("id"));
                            user.setUsername(obj.getString("username"));
                            userList.add(user);
                            usernames.add(user.getUsername());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, usernames);
                        userSpinner.setAdapter(adapter);
                    } catch (Exception e) {
                        ToastUtil.show(this, "Failed to parse users", 1/3);
                    }
                },
                error -> ToastUtil.show(this, "Failed to load users", 1/3)
        );
        Volley.newRequestQueue(this).add(request);
    }

//    private void fetchMeals() {
//        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.GET_MEALS_WITH_DOCTOR_ID,
//                response -> {
//                    try {
//                        JSONArray array = new JSONArray(response);
//                        List<String> mealTitles = new ArrayList<>();
//                        for (int i = 0; i < array.length(); i++) {
//                            JSONObject obj = array.getJSONObject(i);
//                            Meal meal = new Meal(
//                                    obj.getString("title"),
//                                    obj.getInt("calories"),
//                                    obj.getString("description"),
//                                    obj.getString("category"),
//                                    obj.getString("fitnessGoal"),
//                                    obj.getString("mealtype"),
//                                    obj.getString("image_id")
//                            );
//                            meal.setId(obj.getInt("id"));
//                            mealList.add(meal);
//                            mealTitles.add(meal.getTitle());
//                        }
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, mealTitles);
//                        mealListView.setAdapter(adapter);
//                        mealListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//                    } catch (Exception e) {
//                        ToastUtil.show(this, "Failed to parse meals", 1/3);
//                    }
//                },
//                error -> ToastUtil.show(this, "Failed to load meals", 1/3)
//        );
//        Volley.newRequestQueue(this).add(request);
//    }

//    private void assignSelectedMeals() {
//        int selectedUserIndex = userSpinner.getSelectedItemPosition();
//        if (selectedUserIndex == -1) {
//            ToastUtil.show(this, "Select a user", 1/3);
//            return;
//        }
//
//        int userId = userList.get(selectedUserIndex).getId();
//        JSONArray selectedMealIds = new JSONArray();
//
//        for (int i = 0; i < mealListView.getCount(); i++) {
//            if (mealListView.isItemChecked(i)) {
//                selectedMealIds.put(mealList.get(i).getId());
//            }
//        }
//
//        if (selectedMealIds.length() == 0) {
//            ToastUtil.show(this, "Select at least one meal", 1/3);
//            return;
//        }
//
//        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.ASSIGN_MEALS_URL,
//                response -> ToastUtil.show(this, "Meals assigned", 1/3),
//                error -> ToastUtil.show(this, "Assign failed: " + error.getMessage(), 1/3)
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", String.valueOf(userId));
//                params.put("doctor_id", String.valueOf(doctorId));
//                params.put("meal_ids", selectedMealIds.toString());
//                return params;
//            }
//        };
//
//        Volley.newRequestQueue(this).add(request);
//    }

//    private void fetchMeals(int doctorId) {
//        // 1) clear old data
//        mealList.clear();
//
//        // 2) build URL with doctor_id (our PHP will only return rows where user_id IS NULL)
//        String url = ApiConfig.GET_UNASSIGNED_MEALS + "?doctor_id=" + doctorId;
//
//        StringRequest request = new StringRequest(
//                Request.Method.GET,
//                url,
//                response -> {
//                    try {
//                        JSONArray array = new JSONArray(response);
//                        List<String> mealTitles = new ArrayList<>();
//
//                        for (int i = 0; i < array.length(); i++) {
//                            JSONObject obj = array.getJSONObject(i);
//                            Meal meal = new Meal(
//                                    obj.getString("title"),
//                                    obj.getInt("calories"),
//                                    obj.getString("description"),
//                                    obj.getString("category"),
//                                    obj.getString("fitnessGoal"),
//                                    obj.getString("mealtype"),
//                                    obj.getString("image_id")
//                            );
//                            meal.setId(obj.getInt("id"));
//                            mealList.add(meal);
//                            mealTitles.add(meal.getTitle());
//                        }
//
//                        // 3) repopulate the ListView
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                                this,
//                                android.R.layout.simple_list_item_multiple_choice,
//                                mealTitles
//                        );
//                        mealListView.setAdapter(adapter);
//                        mealListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//
//                    } catch (JSONException e) {
//                        ToastUtil.show(this, "Failed to parse meals", 1/3);
//                    }
//                },
//                error -> ToastUtil.show(this, "Failed to load meals", 1/3)
//        );
//
//        Volley.newRequestQueue(this).add(request);
//    }

    private void fetchMeals(String doctorId) {
        mealList.clear();
        String url = ApiConfig.GET_UNASSIGNED_MEALS + "?doctor_id=" + doctorId;

        Log.d("FETCH_MEALS", "Request URL: " + url);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.d("FETCH_MEALS", "Raw response: " + response);
                    try {
                        JSONArray array = new JSONArray(response);
                        List<String> mealTitles = new ArrayList<>();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Meal meal = new Meal(
                                    obj.getString("title"),
                                    obj.getInt("calories"),
                                    obj.getString("description"),
                                    obj.getString("category"),
                                    obj.getString("fitnessGoal"),
                                    obj.getString("mealtype"),
                                    obj.getString("image_id")
                            );
                            meal.setId(obj.getInt("id"));
                            mealList.add(meal);
                            mealTitles.add(meal.getTitle());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_list_item_multiple_choice,
                                mealTitles
                        );
                        mealListView.setAdapter(adapter);
                        mealListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                    } catch (JSONException e) {
                        Log.e("FETCH_MEALS", "Parse error", e);
                        ToastUtil.show(this, "Failed to parse meals", 1/3);
                    }
                },
                error -> {
                    Log.e("FETCH_MEALS", "Volley error", error);
                    ToastUtil.show(this, "Failed to load meals: " + error.toString(), 1/3);
                }
        );

        Volley.newRequestQueue(this).add(request);
    }


    private void assignSelectedMeals() {
        int userPos = userSpinner.getSelectedItemPosition();
        if (userPos < 0) {
            ToastUtil.show(this, "Select a user", 1/3);
            return;
        }
        int userId = userList.get(userPos).getId();

        List<Integer> sel = new ArrayList<>();
        for (int i = 0; i < mealListView.getCount(); i++) {
            if (mealListView.isItemChecked(i)) {
                sel.add(mealList.get(i).getId());
            }
        }

        JSONObject payload = new JSONObject();
        try {
            payload.put("doctor_id", doctorId);
            payload.put("user_id",   userId);
            payload.put("meal_ids",  new JSONArray(sel));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ApiConfig.ASSIGN_MEALS_URL;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST, url, payload,
                resp -> ToastUtil.show(this, "Meals assigned", 1/3),
                err  -> ToastUtil.show(this, err.getMessage(), 1/3)
        );
        Volley.newRequestQueue(this).add(req);
    }

}
