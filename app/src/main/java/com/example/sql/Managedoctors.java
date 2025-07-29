package com.example.sql;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import androidx.appcompat.widget.SearchView; // ✅ CORRECT


public class Managedoctors extends AppCompatActivity {

    private static final String TAG = "Manage Doctors Activity";
    private static final String GET_DOCTORS_URL = ApiConfig.GET_DOCTORS_URL;
    private static final String DELETE_DOCTOR_URL =ApiConfig.DELETE_DOCTOR_URL;

    private EditText idInput;
    private Button  btnDeleteDoctor;
    private RecyclerView doctorRecyclerView;
    private List<Doctor> doctorList;
    private DoctorAdapter doctorAdapter;
    private int adminId ; // replace with actual logic if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managedoctors);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        adminId = getIntent().getIntExtra("admin_id", -1);
        doctorRecyclerView = findViewById(R.id.doctorRecyclerView);
        doctorList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(doctorList, R.layout.item_doctor_edit_delete, new DoctorAdapter.OnDoctorClickListener() {
        @Override
            public void onDoctorClick(Doctor doctor) {
                showDoctorDetailsDialog(Managedoctors.this, doctor);
            }

            @Override
            public void onEditClick(Doctor doctor) {
                showEditDoctorDialog(Managedoctors.this, doctor, () -> doctorAdapter.notifyDataSetChanged());
            }

            @Override
            public void onDeleteClick(Doctor doctor) {
                showDeleteDoctorDialog(Managedoctors.this, doctor, () -> {
                    deleteDoctor(String.valueOf(doctor.getId()));
                    doctorList.remove(doctor);
                    doctorAdapter.notifyDataSetChanged();
                });
            }
        });

        doctorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        doctorRecyclerView.setAdapter(doctorAdapter);

        // Automatically load doctors here
        fetchDoctors();
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
//        btnDeleteDoctor = findViewById(R.id.btnDeleteDoctor);
        // Make sure this exists if you use delete
//        btnDeleteDoctor.setOnClickListener(v -> {
//            String id = idInput.getText().toString().trim();
//            if (TextUtils.isEmpty(id)) {
//                ToastUtil.show(this, "Enter a Doctor ID to delete", 1/3);
//            } else {
//                deleteDoctor(id);
//            }
//        });
        SearchView searchView = findViewById(R.id.searchViewDoctors);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doctorAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doctorAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }
    private void fetchDoctors() {
        StringRequest request = new StringRequest(Request.Method.GET, GET_DOCTORS_URL,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        List<Doctor> parsedList = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            Doctor doctor = new Doctor(
                                    obj.optString("email", "Unknown"),
                                    obj.optString("phone", "N/A"),
                                    obj.optInt("user_id")
                            );
                            doctor.setId(obj.optInt("id"));
                            parsedList.add(doctor);
                        }

// ✅ Update adapter via method
                        doctorAdapter.updateData(parsedList);

                    } catch (Exception e) {
                        Log.e(TAG, "Parse error", e);
                        ToastUtil.show(this, "Error parsing data", 1/3);
                    }
                },
                error -> {
                    Log.e(TAG, "Network error", error);
                    ToastUtil.show(this, "Failed to load data", 1/3);
                });

        Volley.newRequestQueue(this).add(request);
    }
    private void showDoctorDetailsDialog(Context context, Doctor doctor) {
        new AlertDialog.Builder(context)
                .setTitle("Doctor Details")
                .setMessage("Name: " + doctor.getName() + "\nPhone: " + doctor.getPhoneNumber())
                .setPositiveButton("OK", null)
                .show();
    }
    private void showEditDoctorDialog(Context context, Doctor doctor, Runnable onEdited) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_doctor, null);
        EditText nameInput = view.findViewById(R.id.editDoctorName);
        EditText phoneInput = view.findViewById(R.id.editDoctorPhone);

        nameInput.setText(doctor.getName());
        phoneInput.setText(doctor.getPhoneNumber());

        new AlertDialog.Builder(context)
                .setTitle("Edit Doctor")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName  = nameInput.getText().toString().trim();
                    String newPhone = phoneInput.getText().toString().trim();
                    // 1) push to server
                    updateDoctor(
                            String.valueOf(doctor.getId()),
                            newName,
                            newPhone,
                            () -> {
                                // 2) on success, update local model & UI
                                doctor.setName(newName);
                                doctor.setPhoneNumber(newPhone);
                                onEdited.run();
                            }
                    );
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void updateDoctor(String id, String name, String phone, Runnable onSuccess) {
        StringRequest req = new StringRequest(Request.Method.POST,
                ApiConfig.UPDATE_DOCTOR_URL,
                response -> {
                    // you might want to parse the JSON and check "success":true
                    onSuccess.run();
                    ToastUtil.show(this, "Doctor updated", 1/3);
                },
                error -> ToastUtil.show(this, "Failed to update doctor", 1/3)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("id",    id);
                p.put("name",  name);
                p.put("phone", phone);
                return p;
            }
        };
        Volley.newRequestQueue(this).add(req);
    }


    private void deleteDoctor(String id) {
        StringRequest request = new StringRequest(Request.Method.POST, DELETE_DOCTOR_URL,
                response -> ToastUtil.show(this, "Doctor deleted if exists.", 1/3),
                error -> ToastUtil.show(this, "Failed to delete doctor.", 1/3)) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
    private void showDeleteDoctorDialog(Context context, Doctor doctor, Runnable onDeleted) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Doctor")
                .setMessage("Are you sure you want to delete " + doctor.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> onDeleted.run())
                .setNegativeButton("Cancel", null)
                .show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, DoctorsActivity.class);
        intent.putExtra("user_id", getIntent().getIntExtra("user_id", -1)); // pass back user ID
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

}
