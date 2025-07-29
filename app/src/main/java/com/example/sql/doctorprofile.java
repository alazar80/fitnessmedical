package com.example.sql;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class doctorprofile extends AppCompatActivity {

    private static final String TAG = "DoctorProfileActivity";
    private static final String PROFILE_URL = ApiConfig.DOCTOR_PROFILE_URL;

    private ImageView profileImageView;
    private TextView fullnameTextView, emailTextView, phoneTextView, genderTextView, dateofbirthTextView;
    private TextView specialityTextView, yearsofexperienceTextView, experiencedetailTextView;
    private TextView licenseTextView, addressTextView;
    private Button editProfileImageButton, editProfileButton;

    private int doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);

        String lang = getSharedPreferences("prefs", MODE_PRIVATE).getString("lang", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        doctorId = getIntent().getIntExtra("doctor_id", -1);
        if (doctorId == -1) {
            ToastUtil.show(this, "Invalid doctor ID", 1/3);
            finish();
            return;
        }
        initViews();
        fetchDoctorProfile();
//        logoutButton.setOnClickListener(v -> confirmLogout());
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, doctorprofileedit.class);
            intent.putExtra("doctor_id", doctorId);
            startActivity(intent);
        });
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
    }

    private void initViews() {
        profileImageView = findViewById(R.id.ExerciseImageView);
        fullnameTextView = findViewById(R.id.fullnameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        genderTextView = findViewById(R.id.genderTextView);
        dateofbirthTextView = findViewById(R.id.dateofbirthTextView);
        specialityTextView = findViewById(R.id.specialityTextView);
        yearsofexperienceTextView = findViewById(R.id.yearsofexperienceTextView);
        experiencedetailTextView = findViewById(R.id.experiencedetailTextView);
        licenseTextView = findViewById(R.id.licenseTextView);
        addressTextView = findViewById(R.id.addressTextView);
        editProfileImageButton = findViewById(R.id.editProfileImageButton);
        editProfileButton = findViewById(R.id.editProfileButton);
//        logoutButton = findViewById(R.id.logoutButton);
    }

    private void fetchDoctorProfile() {
        new Thread(() -> {
            try {
                URL url = new URL(PROFILE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("doctor_id", doctorId);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonRequest.toString());
                writer.flush();
                writer.close();

                InputStream is = conn.getInputStream();
                StringBuilder response = new StringBuilder();
                int ch;
                while ((ch = is.read()) != -1) {
                    response.append((char) ch);
                }
                is.close();

                JSONObject obj = new JSONObject(response.toString());
                if (obj.optBoolean("success")) {
                    JSONObject data = obj.getJSONObject("data");
                    runOnUiThread(() -> populateFields(data));
                } else {
                    runOnUiThread(() -> ToastUtil.show(this, obj.optString("error"), 1/3));
                }

            } catch (Exception e) {
                Log.e(TAG, "Fetch profile failed", e);
                runOnUiThread(() -> ToastUtil.show(this, "Error: " + e.getMessage(), 1/3));
            }
        }).start();
    }

    private void populateFields(JSONObject data) {
        try {
            String fullName = data.getString("first_name") + " " + data.getString("last_name");
            fullnameTextView.setText(fullName);
            emailTextView.setText(data.getString("email"));
            phoneTextView.setText(data.getString("phone"));
            genderTextView.setText(data.getString("gender"));
            dateofbirthTextView.setText(data.getString("dateofbirth"));
            specialityTextView.setText(data.getString("specialty"));
            yearsofexperienceTextView.setText(data.getString("years_of_experience"));
            experiencedetailTextView.setText(data.getString("experience"));
            addressTextView.setText(data.getString("address"));
            licenseTextView.setText(data.getString("license_number"));

            String imageBase64 = data.optString("profile_image", "");
            if (!imageBase64.isEmpty()) {
                byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                profileImageView.setImageBitmap(bitmap);
            }

        } catch (Exception e) {
            ToastUtil.show(this, "Error displaying profile", 1/3);
            Log.e(TAG, "Display error", e);
        }
    }


//    private void confirmLogout() {
//        new AlertDialog.Builder(this)
//                .setTitle("Log Out")
//                .setMessage("Do you really want to log out?")
//                .setPositiveButton("Yes", (dialog, which) -> {
//                    startActivity(new Intent(this, LoginActivity.class));
//                    finish();
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }
}
