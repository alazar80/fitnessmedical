package com.example.sql;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class AdminAssignExerciseFragment extends Fragment {

    private Spinner userSpinner;
    private ListView exerciseListView;
    private Button assignButton;

    private List<User> userList = new ArrayList<>();
    private List<Exercise> exerciseList = new ArrayList<>();

    private ArrayAdapter<String> userAdapter;
    private ArrayAdapter<String> exerciseAdapter;

    private static final String TAG = "AssignExercise";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_assign_exercise, container, false);

        userSpinner = view.findViewById(R.id.userSpinner);
        exerciseListView = view.findViewById(R.id.exerciseListView);
        assignButton = view.findViewById(R.id.assignButton);
        exerciseListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        fetchUsers();
        fetchExercises();

        assignButton.setOnClickListener(v -> assignExercises());

        return view;
    }

    private void fetchUsers() {
        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.GET_USERS_URL,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        List<String> userNames = new ArrayList<>();
                        userList.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            int id = obj.getInt("id");
                            String email = obj.getString("email");
                            userList.add(new User(id, email));
                            userNames.add(email);
                        }
                        userAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, userNames);
                        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        userSpinner.setAdapter(userAdapter);
                    } catch (Exception e) {
                        Log.e(TAG, "User parse error", e);
                    }
                },
                error -> Log.e(TAG, "User fetch error", error)
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void fetchExercises() {
        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.GET_EXERCISES_URL,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        List<String> exerciseNames = new ArrayList<>();
                        exerciseList.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            exerciseList.add(new Exercise(id, name));
                            exerciseNames.add(name);
                        }
                        exerciseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, exerciseNames);
                        exerciseListView.setAdapter(exerciseAdapter);
                    } catch (Exception e) {
                        Log.e(TAG, "Exercise parse error", e);
                    }
                },
                error -> Log.e(TAG, "Exercise fetch error", error)
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void assignExercises() {
        int selectedUserIndex = userSpinner.getSelectedItemPosition();
        if (selectedUserIndex < 0 || userList.isEmpty()) {
            ToastUtil.show(getContext(), "Select a user", 1/3);
            return;
        }
        int userId = userList.get(selectedUserIndex).id;

        SparseBooleanArray checked = exerciseListView.getCheckedItemPositions();
        List<String> selectedIds = new ArrayList<>();

        for (int i = 0; i < checked.size(); i++) {
            int pos = checked.keyAt(i);
            if (checked.valueAt(i)) {
                selectedIds.add(String.valueOf(exerciseList.get(pos).id));
            }
        }

        if (selectedIds.isEmpty()) {
            ToastUtil.show(getContext(), "Select at least one exercise", 1/3);
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.ASSIGN_EXERCISES_URL,
                response -> ToastUtil.show(getContext(), "Exercises assigned!", 1/3),
                error -> ToastUtil.show(getContext(), "Assign failed", 1/3)) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", String.valueOf(userId));
                map.put("exercise_ids", new JSONArray(selectedIds).toString());
                return map;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    static class User {
        int id;
        String email;
        User(int id, String email) {
            this.id = id;
            this.email = email;
        }
    }

    static class Exercise {
        int id;
        String name;
        Exercise(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
