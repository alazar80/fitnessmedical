package com.example.sql;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Showfeedbacks extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private List<Feedback> feedbackList;

int adminId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showfeedbacks);
        adminId = getIntent().getIntExtra("admin_id", -1);
        recyclerView = findViewById(R.id.recyclerView);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ApiConfig.ADMIN_GET_FEEDBACK + "?admin_id=" + adminId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    feedbackList = new ArrayList<>(); // Use the existing class variable

                    try {
                        if (response.getBoolean("success")) {
                            JSONArray feedbacks = response.getJSONArray("feedbacks");
                            for (int i = 0; i < feedbacks.length(); i++) {
                                JSONObject f = feedbacks.getJSONObject(i);
                                Feedback feedback = new Feedback(
                                        f.getInt("id"),
                                        f.isNull("user_id") ? -1 : f.getInt("user_id"),
                                        f.isNull("doctor_id") ? -1 : f.getInt("doctor_id"),
                                        f.getString("subject"),
                                        f.getString("message"),
                                        f.isNull("rating") ? 0 : f.getInt("rating"),
                                        f.getString("created_at"),
                                        f.isNull("status") ? "" : f.getString("status"),
                                        f.isNull("response") ? "" : f.getString("response")
                                );
                                feedbackList.add(feedback);
                            }

                            FeedbackAdapter adapter = new FeedbackAdapter(this, feedbackList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    ToastUtil.show(this, "Error loading feedback", 1/3);
                }
        );

        queue.add(request);

    }
}
