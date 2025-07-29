package com.example.sql;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
public class ChangePasswordActivity extends AppCompatActivity {
    EditText oldPasswordInput, newPasswordInput;
    Button changePasswordBtn;
    int userId, doctorId;
    private static final String CHANGE_PASSWORD_URL = ApiConfig.CHANGE_PASSWORD_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);

        // grab IDs from the Intent
        userId   = getIntent().getIntExtra("user_id",   -1);
        doctorId = getIntent().getIntExtra("doctor_id", -1);
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {   onBackPressed();});
        oldPasswordInput   = findViewById(R.id.editTextOldPassword);
        newPasswordInput   = findViewById(R.id.editTextNewPassword);
        changePasswordBtn  = findViewById(R.id.buttonChangePassword);

        changePasswordBtn.setOnClickListener(v -> {
            String oldPass = oldPasswordInput.getText().toString().trim();
            String newPass = newPasswordInput.getText().toString().trim();
            if (oldPass.isEmpty() || newPass.isEmpty()) {
                ToastUtil.show(this, "All fields required", 1/3);
                return;
            }
            changePassword(oldPass, newPass);
        });
    }

    private void changePassword(String oldPassword, String newPassword) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest req = new StringRequest(Request.Method.POST, CHANGE_PASSWORD_URL,
                response -> {
                    if (response.contains("\"success\":true")) {
                        new AlertDialog.Builder(this)
                                .setTitle("Password Changed")
                                .setMessage("Log out or stay?")
                                .setPositiveButton("Log Out", (d,w) -> {
                                    Intent i = new Intent(this, LoginActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                })
                                .setNegativeButton("Stay",(d,w)->onBackPressed())
                                .setCancelable(false)
                                .show();
                    } else {
                        ToastUtil.show(this, "Error: "+response, 1/3);
                    }
                },
                error -> ToastUtil.show(this, "Server error", 1/3)
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                if (userId != -1) {
                    p.put("user_id",    String.valueOf(userId));
                } else if (doctorId != -1) {
                    p.put("doctor_id",  String.valueOf(doctorId));
                }
                p.put("old_password", oldPassword);
                p.put("new_password", newPassword);
                return p;
            }
        };
        queue.add(req);
    }
}
