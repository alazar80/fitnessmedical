package com.example.sql;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UserProfileEditActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final String FETCH_URL  = ApiConfig.FETCH_PROFILE_URL;
    private static final String UPDATE_URL = ApiConfig.UPDATE_USER_PROFILE_URL;

    private ImageView ivProfile;
    private EditText etName, etEmail, etPhone, etGender, etDob,
            etHeight, etWeight, etGoal, etLevel, etDays;
    private String imageBase64 = "";
    private int userId;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_edit_user_profile);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        ivProfile = findViewById(R.id.ivProfile);
        etName    = findViewById(R.id.etName);
        etEmail   = findViewById(R.id.etEmail);
        etPhone   = findViewById(R.id.etPhone);
        etGender  = findViewById(R.id.etGender);
        etDob     = findViewById(R.id.etDob);
        etHeight  = findViewById(R.id.etHeight);
        etWeight  = findViewById(R.id.etWeight);
        etGoal    = findViewById(R.id.etGoal);
        etLevel   = findViewById(R.id.etLevel);
        etDays    = findViewById(R.id.etDays);
        Button btnImg = findViewById(R.id.btnChangeImage);
        Button btnSave= findViewById(R.id.btnSave);

        queue = Volley.newRequestQueue(this);
        userId = getIntent().getIntExtra("user_id", -1);
        //ToastUtil.show(this, String.valueOf(userId), 1/3);
        if (userId<0) { finish(); return; }

        btnImg.setOnClickListener(v -> {
            startActivityForResult(
                    new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                    PICK_IMAGE
            );
        });
        btnSave.setOnClickListener(v -> saveProfile());
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
        fetchProfile();
    }

    private void fetchProfile() {
        JSONObject params = new JSONObject(Map.of("user_id", userId));
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST, FETCH_URL, params,
                resp -> {
                    etName .setText(resp.optString("name",""));
                    etEmail.setText(resp.optString("email",""));
                    etPhone.setText(resp.optString("phone",""));
                    etGender.setText(resp.optString("gender",""));
                    etDob  .setText(resp.optString("dateofbirth",""));
                    etHeight.setText(resp.optString("height",""));
                    etWeight.setText(resp.optString("weight",""));
                    etGoal .setText(resp.optString("fitness_goal",""));
                    etLevel.setText(resp.optString("experience_level",""));
                    etDays .setText(resp.optString("workout_days",""));
                    String img = resp.optString("profile_image","");
                    if (!img.isEmpty()) {
                        byte[] b = Base64.decode(img, Base64.DEFAULT);
                        ivProfile.setImageBitmap(BitmapFactory.decodeByteArray(b,0,b.length));
                        imageBase64 = img;
                    }
                },
                err -> ToastUtil.show(this,"Load failed",1/3)
        );
        queue.add(req);
    }

    @Override
    protected void onActivityResult(int rc, int rr, @Nullable Intent d) {
        super.onActivityResult(rc, rr, d);
        if (rc==PICK_IMAGE && rr==RESULT_OK && d!=null) {
            try {
                Uri uri = d.getData();
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ivProfile.setImageBitmap(bmp);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
            } catch (Exception e) { /* ignore */ }
        }
    }

    private void saveProfile() {
        StringRequest req = new StringRequest(
                Request.Method.POST, UPDATE_URL,
                resp -> {
                    ToastUtil.show(this,"Profile updated",1/3);
                    finish();
                },
                err -> ToastUtil.show(this,"Update failed",1/3)
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("user_id", String.valueOf(userId));
                p.put("name", etName.getText().toString());
                p.put("email", etEmail.getText().toString());
                p.put("phone", etPhone.getText().toString());
                p.put("gender",etGender.getText().toString());
                p.put("dateofbirth",etDob.getText().toString());
                p.put("height",etHeight.getText().toString());
                p.put("weight",etWeight.getText().toString());
                p.put("fitness_goal",etGoal.getText().toString());
                p.put("experience_level",etLevel.getText().toString());
                p.put("workout_days",etDays.getText().toString());
                if (!imageBase64.isEmpty()) p.put("profile_image", imageBase64);
                return p;
            }
        };
        queue.add(req);
    }
}
