package com.example.sql;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
public class choosetrainer extends AppCompatActivity {
    private RecyclerView doctorRecyclerView;
    private List<Doctor> doctorList;
    private DoctorAdapter doctorAdapter;
    private int userId;
    private TextView alreadyHasTrainerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_trainer);

        // ① BIND the TextView here:
        alreadyHasTrainerText = findViewById(R.id.alreadyHasTrainerText);

        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);

        userId = getIntent().getIntExtra("user_id", -1);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        doctorRecyclerView = findViewById(R.id.doctorRecyclerView);
        doctorList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(doctorList, R.layout.item_doctor, new DoctorAdapter.OnDoctorClickListener() {
        @Override
                    public void onDoctorClick(Doctor doctor) {
                        Intent i = new Intent(choosetrainer.this, PaymentActivity.class);
                        i.putExtra("user_id", userId);
                        i.putExtra("doctor_id", doctor.getId() + "");
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                    }
                    @Override public void onEditClick(Doctor d)  { }
                    @Override public void onDeleteClick(Doctor d){ }
                }
        );
        doctorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        doctorRecyclerView.setAdapter(doctorAdapter);

        // ② Now call with the correct param name:
        checkUserHasDoctor();
    }

    private void fetchDoctors() {
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET,
                ApiConfig.GET_DOCTORS_URL,
                null,
                response -> {
                    doctorList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject o = null;
                        try {
                            o = response.getJSONObject(i);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Doctor d = new Doctor(
                                o.optString("email","Unknown"),
                                o.optString("phone","N/A"),
                                o.optInt("user_id")
                        );
                        d.setId(o.optInt("id"));
                        doctorList.add(d);
                    }
                    doctorAdapter.notifyDataSetChanged();
                },
                err -> ToastUtil.show(this, "Failed to load doctors", 1/3)
        );
        Volley.newRequestQueue(this).add(req);
    }

    private void checkUserHasDoctor() {
        // ③ Use “user_id” (not “id”)
        String url = ApiConfig.GET_USER_BY_ID_URL + "?user_id=" + userId;

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    int doctorIdFromServer = response.optInt("doctor_id", 0);
                    if (doctorIdFromServer == 0) {
                        // no doctor → show list
                        doctorRecyclerView.setVisibility(View.VISIBLE);
                        alreadyHasTrainerText.setVisibility(View.GONE);
                        fetchDoctors();
                    } else {
                        // already has trainer → hide list, show text
                        doctorRecyclerView.setVisibility(View.GONE);
                        alreadyHasTrainerText.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    // on network error, bind alreadyHasTrainerText was non‐null, so this won’t NPE anymore
                    doctorRecyclerView.setVisibility(View.VISIBLE);
                    alreadyHasTrainerText.setVisibility(View.GONE);
                    fetchDoctors();
                }
        );
        Volley.newRequestQueue(this).add(req);
    }
}
