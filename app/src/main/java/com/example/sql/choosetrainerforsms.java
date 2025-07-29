package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class choosetrainerforsms extends AppCompatActivity {
    private RecyclerView doctorRecyclerView;
    private List<Doctor> doctorList;
    private DoctorAdapter doctorAdapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_trainer_for_sms);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // init RecyclerView + adapter
        userId = getIntent().getIntExtra("user_id", -1);
        doctorRecyclerView = findViewById(R.id.doctorRecyclerView);
        doctorList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(doctorList, R.layout.item_doctor, new DoctorAdapter.OnDoctorClickListener() {

        @Override
            public void onDoctorClick(Doctor doctor) {
                // navigate to sms.class
                Intent i = new Intent(choosetrainerforsms.this, sms.class);
                i.putExtra("user_id", userId);
                i.putExtra("doctor_id", doctor.getId() + "");
                i.putExtra("phone", doctor.getPhoneNumber());
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
            @Override public void onEditClick(Doctor d) { /*no-op*/ }
            @Override public void onDeleteClick(Doctor d) { /*no-op*/ }
        });
        doctorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        doctorRecyclerView.setAdapter(doctorAdapter);

        fetchDoctors();
    }

    private void fetchDoctors() {
        String url = ApiConfig.GET_DOCTORS_URL;
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
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
                                o.optString("email", "Unknown"),
                                o.optString("phone", "N/A"),
                                o.optInt("user_id")
                        );
                        d.setId(o.optInt("id"));
                        doctorList.add(d);
                    }
                    doctorAdapter.notifyDataSetChanged();
                },
                error -> ToastUtil.show(this, "Failed to load doctors", 1/3)
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }
}
