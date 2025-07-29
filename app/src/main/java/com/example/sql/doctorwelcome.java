package com.example.sql;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public class doctorwelcome  extends BaseActivity {

    private static final String TAG = "WelcomeDoctorActivity";
    private static final String API_URL = ApiConfig.WELCOME_DOCTOR_INSERT;

    private EditText etSpecialty, etYearsExperience, etExperience, etAddress, etLicenseNumber;
    private Button btnSubmit;

    private int doctorId;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctorwelcome); // Ensure your XML layout matches
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        etSpecialty = findViewById(R.id.et_specialty);
        etYearsExperience = findViewById(R.id.et_years_experience);
        etExperience = findViewById(R.id.et_experience_description);
        etAddress = findViewById(R.id.et_address);
        etLicenseNumber = findViewById(R.id.et_license_number);
        btnSubmit = findViewById(R.id.btn_submit_doctor);

        doctorId = getIntent().getIntExtra("doctor_id", -1);
        email = getIntent().getStringExtra("email");

        if (doctorId == -1 || email == null) {
            ToastUtil.show(this, "Missing doctor data. Please login again.", 1/3);
            finish();
            return;
        }

        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        String specialty = etSpecialty.getText().toString().trim();
        String years = etYearsExperience.getText().toString().trim();
        String experience = etExperience.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String license = etLicenseNumber.getText().toString().trim();

        if (specialty.isEmpty() || years.isEmpty() || experience.isEmpty() || address.isEmpty() || license.isEmpty()) {
            ToastUtil.show(this, "All fields are required.", 1/3);
            return;
        }

        new Thread(() -> sendDoctorData(specialty, years, experience, address, license)).start();
    }

    private void sendDoctorData(String specialty, String years, String experience, String address, String license) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String data = "doctor_id=" + URLEncoder.encode(String.valueOf(doctorId), "UTF-8") +
                    "&specialty=" + URLEncoder.encode(specialty, "UTF-8") +
                    "&years_experience=" + URLEncoder.encode(years, "UTF-8") +
                    "&experience=" + URLEncoder.encode(experience, "UTF-8") +
                    "&address=" + URLEncoder.encode(address, "UTF-8") +
                    "&license_number=" + URLEncoder.encode(license, "UTF-8");

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            String response = responseBuilder.toString();
            Log.d(TAG, "Server Response: " + response);

            JSONObject obj = new JSONObject(response);

            runOnUiThread(() -> {
                if (obj.optBoolean("success")) {
                    ToastUtil.show(this, "Doctor profile saved successfully!", 1/3);
                    navigateToDashboard();
                } else {
                    ToastUtil.show(this, "Error: " + obj.optString("error"), 1/3);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error submitting doctor info: " + e.getMessage());
            runOnUiThread(() -> ToastUtil.show(this, "Error: " + e.getMessage(), 1/3));
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DoctorsActivity.class);
        intent.putExtra("doctor_id", doctorId);
        startActivity(intent);
        finish();
    }
}
