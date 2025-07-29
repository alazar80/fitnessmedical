package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manageuser extends AppCompatActivity {

    private static final String TAG = "ManageUsersActivity";
    private static final String GET_USERS_URL    = ApiConfig.GET_USERS_URL;
    private static final String DELETE_USER_URL  = ApiConfig.URL_DELETE_USER;
    // e.g. https://your.server.com/delete_user.php

    private ListView userListView;
    private ArrayList<User> userList = new ArrayList<>();
    private UserAdapter adapter;
int adminId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manageuser);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
         adminId = getIntent().getIntExtra("admin_id", -1);

        userListView = findViewById(R.id.userListView);
        adapter      = new UserAdapter(this, userList);
        userListView.setAdapter(adapter);

        EditText searchInput = findViewById(R.id.searchUser);
        Button   btnSearch   = findViewById(R.id.btnSearchUsers);
        btnSearch.setOnClickListener(v -> {
            String q = searchInput.getText().toString().trim().toLowerCase();
            if (!q.isEmpty()) adapter.updateList(filter(q));
            else              adapter.updateList(userList);
        });

        fetchUsers();
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());

    }

    private void fetchUsers() {
        StringRequest req = new StringRequest(Request.Method.GET, GET_USERS_URL,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        userList.clear();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            int id    = o.optInt("id");
                            String username = o.optString("username");
                            String email    = o.optString("email");
                            String phone    = o.optString("phone");
                            userList.add(new User(id, username, email, phone));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e(TAG, "Parse error", e);
                        ToastUtil.show(this, "Error parsing data", 1/3);
                    }
                },
                err -> {
                    Log.e(TAG, "Fetch error", err);
                    ToastUtil.show(this, "Failed to load users", 1/3);
                }
        );
        Volley.newRequestQueue(this).add(req);
    }

    private ArrayList<User> filter(String q) {
        ArrayList<User> out = new ArrayList<>();
        for (User u : userList) {
            if (u.getUsername().toLowerCase().contains(q)
                    || u.getEmail().toLowerCase().contains(q)
                    || u.getPhone().toLowerCase().contains(q)) {
                out.add(u);
            }
        }
        return out;
    }

    /** Called by UserAdapter when the user confirms “Delete” */
    public void deleteUser(final String id) {
        StringRequest req = new StringRequest(Request.Method.POST, DELETE_USER_URL,
                response -> {
                    // parse JSON: { success: true } or { success: false, error: "..." }
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optBoolean("success")) {
                            // remove locally
                            for (int i = 0; i < userList.size(); i++) {
                                if (String.valueOf(userList.get(i).getId()).equals(id)) {
                                    userList.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                            ToastUtil.show(this, "User deleted", 1/3);
                        } else {
                            ToastUtil.show(this,
                                    "Delete failed: " + json.optString("error"),
                                    1/3);
                        }
                    } catch (Exception e) {
                        ToastUtil.show(this, "Invalid response", 1/3);
                    }
                },
                error -> ToastUtil.show(this, "Network error", 1/3)
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("id", id);
                return p;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(this, DoctorsActivity.class);
//        intent.putExtra("user_id", getIntent().getIntExtra("user_id", -1)); // pass back user ID
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
//
//    }
}
