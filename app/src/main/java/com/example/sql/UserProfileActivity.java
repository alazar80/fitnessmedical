package com.example.sql;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "User Profile View Activity";
    private static final String FETCH_PROFILE_URL = ApiConfig.FETCH_PROFILE_URL;
    private static final String UPDATE_PROFILE_IMAGE_URL = ApiConfig.UPDATE_PROFILE_IMAGE_URL;
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private TextView usernameTextView, useridTextView, emailTextView, phoneTextView,
            heightTextView, weightTextView, fitnessGoalTextView, experienceLevelTextView,
            workoutDaysTextView, genderTextView, dateofbirthTextView;

    private Button editProfileImageButton, editProfileButton;

    private int userId;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
        initializeViews();
        requestQueue = Volley.newRequestQueue(this);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            showToastSafe("Invalid User ID!");
            finish();
            return;
        }

        loadUserData();  // Cleaned: call this directly
        setButtonListeners();
    }

    private void initializeViews() {
        useridTextView = findViewById(R.id.useridTextView);
        profileImageView = findViewById(R.id.profileImageView);
        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        genderTextView = findViewById(R.id.genderTextView);
        dateofbirthTextView = findViewById(R.id.dateofbirthTextView);
        heightTextView = findViewById(R.id.heightTextView);
        weightTextView = findViewById(R.id.weightTextView);
        fitnessGoalTextView = findViewById(R.id.fitnessgoalTextView);
        experienceLevelTextView = findViewById(R.id.experiencelevelTextView);
        workoutDaysTextView = findViewById(R.id.workoutdaysTextView);
        editProfileImageButton = findViewById(R.id.editProfileImageButton);
        editProfileButton = findViewById(R.id.editProfileButton);
    }

    private void loadUserData() {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, FETCH_PROFILE_URL,
                new JSONObject(params),
                this::handleProfileResponse,
                this::handleVolleyError
        );

        requestQueue.add(request);
    }

    private void handleProfileResponse(JSONObject response) {
        Log.d(TAG, "Server Response: " + response);
        if (response.has("error")) {
            showToastSafe("Error: " + response.optString("error"));
            return;
        }

        try {
            populateProfile(response);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing profile data", e);
            showToastSafe("Failed to load profile data");
        }
    }

    private void populateProfile(JSONObject user) {
        usernameTextView.setText(user.optString("name", "N/A"));
        useridTextView.setText(user.optString("id", "N/A"));
        emailTextView.setText(user.optString("email", "N/A"));
        phoneTextView.setText(user.optString("phone", "N/A"));
        heightTextView.setText(user.optString("height", "N/A"));
        weightTextView.setText(user.optString("weight", "N/A"));
        genderTextView.setText(user.optString("gender", "N/A"));
        dateofbirthTextView.setText(user.optString("dateofbirth", "N/A"));
        fitnessGoalTextView.setText(user.optString("fitness_goal", "N/A"));
        experienceLevelTextView.setText(user.optString("experience_level", "N/A"));
        workoutDaysTextView.setText(user.optString("workout_days", "N/A"));

        // ✅ NEW: Load profile image from Base64
        String filename = user.optString("profile_image", "");
        if (!filename.isEmpty()) {
            String imageUrl = ApiConfig.BASE_URL + filename;
            Log.d(TAG, "Loading image URL: " + imageUrl);  // Helpful for debugging!

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_profile)
                    .error(R.drawable.ic_default_profile)
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.ic_default_profile);
        }


    }


    private void setButtonListeners() {
        editProfileImageButton.setOnClickListener(v -> openImagePicker());
        editProfileButton.setOnClickListener(v -> navigateToEditProfile());

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
                uploadProfileImage(bitmap);
            } catch (IOException e) {
                showToastSafe("Failed to load image");
                Log.e(TAG, "Image load error", e);
            }
        }
    }

    private void uploadProfileImage(Bitmap bitmap) {
        String encodedImage = encodeImageToBase64(bitmap);

        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_PROFILE_IMAGE_URL,
                response -> {
                    Log.d(TAG, "Image upload response: " + response);
                    showToastSafe("Profile image updated");
                },
                error -> {
                    Log.e(TAG, "Upload error", error);
                    showToastSafe("Image upload failed");
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("profile_image", encodedImage);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void navigateToEditProfile() {
        Intent intent = new Intent(this, UserProfileEditActivity.class); // or EditProfileActivity if available
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void handleVolleyError(VolleyError error) {
        String message = "Server unreachable";
        if (error.networkResponse != null && error.networkResponse.data != null) {
            message = new String(error.networkResponse.data);
        } else if (error.getMessage() != null) {
            message = error.getMessage();
        }
        Log.e(TAG, "Volley error: " + message, error);
        showToastSafe("Error: " + message);
    }

    private void showToastSafe(String message) {
        runOnUiThread(() -> ToastUtil.show(this, message, 1/3));
    }
}
