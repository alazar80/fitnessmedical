package com.example.sql;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminAssignExerciseActivity extends AppCompatActivity {

    private Spinner userSpinner;
    private ListView exerciseListView;
    private Button assignButton;

    private List<User> userList = new ArrayList<>();
    private List<Exercise> exerciseList = new ArrayList<>();
    private ArrayAdapter<String> exerciseAdapter;
int adminId;
    TextView AdminManageExercises;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_assign_exercise);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        AdminManageExercises=findViewById(R.id.ManageAssignExercisesTitle);

        AdminManageExercises.setText("Admin Assign Exercises ");
        adminId = getIntent().getIntExtra("admin_id", -1);
        userSpinner = findViewById(R.id.userSpinner);
        exerciseListView = findViewById(R.id.exerciseListView);
        assignButton = findViewById(R.id.assignButton);

        fetchUsers();
        fetchExercises();
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
        assignButton.setOnClickListener(v -> assignSelectedExercises());
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

//    private void fetchExercises() {
//        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.GET_EXERCISES_WITH_DOCTOR_ID,
//                response -> {
//                    try {
//                        JSONArray array = new JSONArray(response);
//                        exerciseList.clear();
//                        List<String> exerciseNames = new ArrayList<>();
//                        for (int i = 0; i < array.length(); i++) {
//                            JSONObject obj = array.getJSONObject(i);
//                            Exercise exercise = new Exercise();
//                            exercise.setId(obj.getInt("id"));
//                            exercise.setName(obj.getString("name"));
//                            exerciseList.add(exercise);
//                            exerciseNames.add(exercise.getName());
//                        }
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, exerciseNames);
//                        exerciseListView.setAdapter(adapter);
//                        exerciseListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//                    } catch (Exception e) {
//                        ToastUtil.show(this, "Exercise parsing failed", 1/3);
//                    }
//                },
//                error -> ToastUtil.show(this, "Failed to load exercises", 1/3)
//        );
//
//        Volley.newRequestQueue(this).add(request);
//    }





//    private void assignSelectedExercises() {
//        int selectedUserIndex = userSpinner.getSelectedItemPosition();
//        if (selectedUserIndex == -1) {
//            ToastUtil.show(this, "Select a user", 1/3);
//            return;
//        }
//
//        int userId = userList.get(selectedUserIndex).getId();
//
//        JSONArray selectedExerciseIds = new JSONArray();
//        for (int i = 0; i < exerciseListView.getCount(); i++) {
//            if (exerciseListView.isItemChecked(i)) {
//                selectedExerciseIds.put(exerciseList.get(i).getId());
//            }
//        }
//
//        if (selectedExerciseIds.length() == 0) {
//            ToastUtil.show(this, "Select at least one exercise", 1/3);
//            return;
//        }
//
//        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.ASSIGN_EXERCISES_URL,
//                response -> ToastUtil.show(this, "Exercises assigned", 1/3),
//                error -> ToastUtil.show(this, "Assignment failed: " + error.getMessage(), 1/3)
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", String.valueOf(userId));
//                params.put("doctor_id", String.valueOf(doctorId));
//                params.put("exercise_ids", selectedExerciseIds.toString()); // Sends as JSON array
//                return params;
//            }
//        };
//
//        Volley.newRequestQueue(this).add(request);
//    }
private void fetchExercises() {
    String url = ApiConfig.GET_EXERCISES_ASSIGN_ADMIN + "?admin_id=" + adminId;
    StringRequest req = new StringRequest(Request.Method.GET, url,
            response -> {
                try {
                    JSONArray arr = new JSONArray(response);
                    exerciseList.clear();
                    List<String> names = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        Exercise ex = new Exercise();
                        ex.setId(o.getInt("id"));
                        ex.setName(o.getString("name"));
                        exerciseList.add(ex);
                        names.add(ex.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_list_item_multiple_choice,
                            names
                    );
                    exerciseListView.setAdapter(adapter);
                    exerciseListView.setChoiceMode(
                            ListView.CHOICE_MODE_MULTIPLE
                    );
                } catch (JSONException e) {
                    ToastUtil.show(this, "Exercise parse error", 1/3);
                }
            },
            err -> ToastUtil.show(this, "Load exercises failed", 1/3));
    Volley.newRequestQueue(this).add(req);
}





    private void assignSelectedExercises() {
        int userPos = userSpinner.getSelectedItemPosition();
        if (userPos < 0) {
            ToastUtil.show(this, "Select a user", 1/3);
            return;
        }
        int userId = userList.get(userPos).getId();

        // collect IDs
        List<Integer> sel = new ArrayList<>();
        for (int i = 0; i < exerciseListView.getCount(); i++) {
            if (exerciseListView.isItemChecked(i)) {
                sel.add(exerciseList.get(i).getId());
            }
        }

        // build JSON payload
        JSONObject payload = new JSONObject();
        try {
            payload.put("admin_id", adminId);
            payload.put("user_id", userId);
            payload.put("exercise_ids", new JSONArray(sel));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // note: on emulator use 10.0.2.2; on device use your PC’s LAN IP
        String url = ApiConfig.ADMIN_ASSIGN_EXERCISE;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST, url, payload,
                resp -> ToastUtil.show(this, "Exercises assigned", 1/3),
                err  -> ToastUtil.show(this, err.getMessage(), 1/3)
        );
        Volley.newRequestQueue(this).add(req);
    }

}
