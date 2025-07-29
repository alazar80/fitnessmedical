package com.example.sql;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Enterfeedback extends AppCompatActivity {
        EditText editSubject, editMessage;
        Button btnSubmit;
        ImageView backButton;
        int userId,doctorId;
        @SuppressLint("WrongViewCast")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_feedback);
            ThemeUtil.applyBackground(this, R.id.mainLayout);
            ThemeUtil.applyThemeFromPrefs(this);

            userId = getIntent().getIntExtra("user_id", -1);
            doctorId = getIntent().getIntExtra("doctor_id", -1);

            editSubject = findViewById(R.id.editSubject);
            editMessage = findViewById(R.id.editMessage);
            btnSubmit = findViewById(R.id.btnSubmit);
            backButton=findViewById(R.id.backButton);
            backButton.setOnClickListener(v ->onBackPressed());

            btnSubmit.setOnClickListener(v -> {
                String subject = editSubject.getText().toString().trim();
                String message = editMessage.getText().toString().trim();

                if (subject.isEmpty() || message.isEmpty()) {
                    ToastUtil.show(this, "All fields required", 1/3);
                    return;
                }

                sendReport(subject, message);
                onBackPressed();
            });


        }

        private void sendReport(String subject, String message) {
            StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.GET_USER_REPORT,
                    response -> ToastUtil.show(this, "Submitted", 1/3),
                    error -> ToastUtil.show(this, "Error: " + error.toString(), 1/3)
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put("subject", subject);
                    map.put("message", message);
                    if (userId != -1) {
                        map.put("userId", String.valueOf(userId));
                    }
                    if (doctorId != -1) {
                        map.put("doctorId", String.valueOf(doctorId));
                    }
                    return map;
                }

            };

            Volley.newRequestQueue(this).add(request);
        }
    }
