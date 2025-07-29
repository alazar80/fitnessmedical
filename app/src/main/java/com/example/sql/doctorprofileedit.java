package com.example.sql;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class doctorprofileedit extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private static final String PROFILE_URL = ApiConfig.DOCTOR_PROFILE_URL;
    private static final String UPDATE_URL  = ApiConfig.UPDATE_DOCTOR_PROFILE_URL;

    private ImageView ivProfile;
    private EditText etFirst, etLast, etEmail, etPhone,
            etGender, etDob, etSpec, etYears,
            etExp, etLicense, etAddress;
    private Button btnChangeImage, btnSave;
    private String imageBase64 = "";
    private int doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor_profile);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        doctorId = getIntent().getIntExtra("doctor_id", -1);
        ToastUtil.show(this, String.valueOf(doctorId), 1/3);

        if (doctorId < 0) { finish(); return; }

        ivProfile       = findViewById(R.id.profileImageView);
        btnChangeImage  = findViewById(R.id.btnChangeImage);
        btnSave         = findViewById(R.id.btnSave);

        etFirst   = findViewById(R.id.etFirstName);
        etLast    = findViewById(R.id.etLastName);
        etEmail   = findViewById(R.id.etEmail);
        etPhone   = findViewById(R.id.etPhone);
        etGender  = findViewById(R.id.etGender);
        etDob     = findViewById(R.id.etDob);
        etSpec    = findViewById(R.id.etSpecialty);
        etYears   = findViewById(R.id.etYears);
        etExp     = findViewById(R.id.etExperience);
        etLicense = findViewById(R.id.etLicense);
        etAddress = findViewById(R.id.etAddress);

        btnChangeImage.setOnClickListener(v ->
                startActivityForResult(
                        new Intent(Intent.ACTION_PICK)
                                .setType("image/*"),
                        PICK_IMAGE
                )
        );

        btnSave.setOnClickListener(v -> updateProfile());

        fetchProfile();
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
    }

    private void fetchProfile() {
        new Thread(() -> {
            try {
                JSONObject req = new JSONObject();
                req.put("doctor_id", doctorId);
                URL url = new URL(PROFILE_URL);
                HttpURLConnection c = (HttpURLConnection)url.openConnection();
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.setRequestProperty("Content-Type","application/json");
                try(OutputStreamWriter w=new OutputStreamWriter(c.getOutputStream())) {
                    w.write(req.toString());
                }

                InputStream in = c.getInputStream();
                StringBuilder sb = new StringBuilder();
                int ch;
                while ((ch = in.read()) != -1) sb.append((char)ch);
                JSONObject res = new JSONObject(sb.toString());
                if (res.optBoolean("success")) {
                    JSONObject d = res.getJSONObject("data");
                    runOnUiThread(() -> {
                        etFirst.setText(d.optString("first_name"));
                        etLast .setText(d.optString("last_name"));
                        etEmail.setText(d.optString("email"));
                        etPhone.setText(d.optString("phone"));
                        etGender.setText(d.optString("gender"));
                        etDob.setText(d.optString("dateofbirth"));
                        etSpec.setText(d.optString("specialty"));
                        etYears.setText(d.optString("years_of_experience"));
                        etExp.setText(d.optString("experience"));
                        etLicense.setText(d.optString("license_number"));
                        etAddress.setText(d.optString("address"));
                        String img = d.optString("profile_image","");
                        if (!img.isEmpty()) {
                            byte[] bytes = Base64.decode(img, Base64.DEFAULT);
                            Bitmap b = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            ivProfile.setImageBitmap(b);
                            imageBase64 = img;
                        }
                    });
                } else {
                    runOnUiThread(() ->
                            ToastUtil.show(this,res.optString("error"),1/3)
                    );
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        ToastUtil.show(this,"Fetch failed: "+e.getMessage(),1/3)
                );
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req,res,data);
        if (req==PICK_IMAGE && res==RESULT_OK && data!=null) {
            try {
                InputStream is = getContentResolver().openInputStream(data.getData());
                Bitmap bmp = BitmapFactory.decodeStream(is);
                ivProfile.setImageBitmap(bmp);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
            } catch (Exception e) { /* ignore */ }
        }
    }

    private void updateProfile() {
        new Thread(() -> {
            try {
                JSONObject req = new JSONObject();
                req.put("doctor_id", doctorId);
                req.put("first_name", etFirst.getText().toString());
                req.put("last_name",  etLast .getText().toString());
                req.put("email",      etEmail.getText().toString());
                req.put("phone",      etPhone.getText().toString());
                req.put("gender",     etGender.getText().toString());
                req.put("dateofbirth",etDob   .getText().toString());
                req.put("specialty",  etSpec .getText().toString());
                req.put("years_of_experience", etYears.getText().toString());
                req.put("experience", etExp   .getText().toString());
                req.put("license_number", etLicense.getText().toString());
                req.put("address",    etAddress.getText().toString());
                if (!imageBase64.isEmpty())
                    req.put("profile_image", imageBase64);

                URL url = new URL(UPDATE_URL);
                HttpURLConnection c = (HttpURLConnection)url.openConnection();
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.setRequestProperty("Content-Type","application/json");
                try(OutputStreamWriter w=new OutputStreamWriter(c.getOutputStream())) {
                    w.write(req.toString());
                }

                InputStream in = c.getInputStream();
                StringBuilder sb = new StringBuilder();
                int ch;
                while ((ch = in.read()) != -1) sb.append((char)ch);
                JSONObject res = new JSONObject(sb.toString());
                runOnUiThread(() -> {
                    if (res.optBoolean("success")) {
                        ToastUtil.show(this,"Profile updated",1/3);
                        finish();
                    } else {
                        ToastUtil.show(this,res.optString("error"),1/3);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        ToastUtil.show(this,"Update failed: "+e.getMessage(),1/3)
                );
            }
        }).start();
    }
}
