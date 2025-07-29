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

public class AdminAssignMealFragment extends Fragment {

    private Spinner userSpinner;
    private ListView mealListView;
    private Button assignButton;

    private List<User> userList = new ArrayList<>();
    private List<Meal> mealList = new ArrayList<>();

    private ArrayAdapter<String> userAdapter;
    private ArrayAdapter<String> mealAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_assign_meal, container, false);

        userSpinner = new Spinner(requireContext());
        LinearLayout parentLayout = (LinearLayout) ((ScrollView) view.findViewById(R.id.scrollView)).getChildAt(0);
        // Now guaranteed to exist
        parentLayout.addView(userSpinner, 0); // Add at the top



        mealListView = view.findViewById(R.id.mealListView);
        assignButton = view.findViewById(R.id.assignButton);
        mealListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        fetchUsers();
        fetchMeals();

        assignButton.setOnClickListener(v -> assignMeals());
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
                        Log.e("FetchUsers", "Error parsing users", e);
                    }
                },
                error -> Log.e("FetchUsers", "Error fetching users", error)
        );
        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void fetchMeals() {
        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.GET_MEALS_URL,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        List<String> mealTitles = new ArrayList<>();
                        mealList.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            int id = obj.getInt("id");
                            String title = obj.getString("title");
                            mealList.add(new Meal(id, title));
                            mealTitles.add(title);
                        }
                        mealAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, mealTitles);
                        mealListView.setAdapter(mealAdapter);
                    } catch (Exception e) {
                        Log.e("FetchMeals", "Error parsing meals", e);
                    }
                },
                error -> Log.e("FetchMeals", "Error fetching meals", error)
        );
        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void assignMeals() {
        int selectedUserIndex = userSpinner.getSelectedItemPosition();
        if (selectedUserIndex < 0 || userList.isEmpty()) {
            ToastUtil.show(getContext(), "Select a user", 1/3);
            return;
        }

        int userId = userList.get(selectedUserIndex).id;
        SparseBooleanArray checked = mealListView.getCheckedItemPositions();
        List<String> selectedMealIds = new ArrayList<>();

        for (int i = 0; i < checked.size(); i++) {
            int pos = checked.keyAt(i);
            if (checked.valueAt(i)) {
                selectedMealIds.add(String.valueOf(mealList.get(pos).id));
            }
        }

        if (selectedMealIds.isEmpty()) {
            ToastUtil.show(getContext(), "Select meals", 1/3);
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.ASSIGN_MEALS_URL,
                response -> ToastUtil.show(getContext(), "Meals assigned!", 1/3),
                error -> ToastUtil.show(getContext(), "Failed to assign", 1/3)) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", String.valueOf(userId));
                map.put("meal_ids", new JSONArray(selectedMealIds).toString());
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

    static class Meal {
        int id;
        String title;
        Meal(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }
}
