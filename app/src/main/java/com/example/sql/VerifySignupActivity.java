package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class VerifySignupActivity extends AppCompatActivity {
    private EditText    etToken;
    private Button      btnVerify;
    private ProgressBar progressBar;
    private String      email;
    public boolean     isDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_signup);

        email    = getIntent().getStringExtra("email");
        isDoctor = getIntent().getBooleanExtra("is_doctor", false);

        etToken     = findViewById(R.id.etToken);
        btnVerify   = findViewById(R.id.btnVerify);
        progressBar = findViewById(R.id.pbVerify);
        progressBar.setVisibility(ProgressBar.GONE);

        btnVerify.setOnClickListener(v -> attemptVerify());
    }

    private void attemptVerify() {
        String token = etToken.getText().toString().trim();
        if (!token.matches("\\d{6}")) {
            etToken.setError("Enter a 6-digit code");
            return;
        }

        btnVerify.setEnabled(false);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("token", token);
        } catch (JSONException e) {
            showError("JSON error");
            return;
        }

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                ApiConfig.VERIFY_SIGNUP_URL,
                body,
                response -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    btnVerify.setEnabled(true);

                    if (response.optBoolean("success", false)) {
                        int newId = isDoctor
                                ? response.optInt("doctor_id", -1)
                                : response.optInt("user_id",   -1);
                        if (newId > 0) {
                            assignDefaultsThenNavigate(newId);
                        } else {
                            showError("No ID returned");
                        }
                    } else {
                        showError(response.optString("error", "Verification failed"));
                    }
                },
                error -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    btnVerify.setEnabled(true);
                    showError("Network error: " + error.getMessage());
                }
        ) {
            @Override public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Content-Type", "application/json");
                return h;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        Volley.newRequestQueue(this).add(req);
    }

    private void assignDefaultsThenNavigate(int newId) {
        String url = isDoctor
                ? ApiConfig.doctorassignUrl
                : ApiConfig.userassignUrl;

        StringRequest req = new StringRequest(
                Request.Method.POST,
                url,
                resp -> {
                    // Success
                    Toast.makeText(this, "Request Successful", Toast.LENGTH_SHORT).show();
                    navigateToWelcome(newId);
                },
                err -> {
                    // Error
                    Toast.makeText(this, "Request Failed: " + err.getMessage(), Toast.LENGTH_SHORT).show();
                    navigateToWelcome(newId); // still navigate on error
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put(isDoctor ? "doctor_id" : "user_id", String.valueOf(newId));
                return p;
            }
        };
        Volley.newRequestQueue(this).add(req);
    }


    private void navigateToWelcome(int newId) {
        Intent i = new Intent(
                this,
                isDoctor ? doctorwelcome.class
                        : Welcome.class
        );
        i.putExtra(isDoctor ? "doctor_id" : "user_id", newId);
        i.putExtra("email", email);
        startActivity(i);
        finish();
    }

    private void showError(String msg) {
        ToastUtil.show(this, msg, 1/3);
        btnVerify.setEnabled(true);
        progressBar.setVisibility(ProgressBar.GONE);
    }
}
