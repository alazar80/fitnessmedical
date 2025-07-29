package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
    private Spinner roleSpinner;
    private EditText emailInput;
    private Button sendCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        roleSpinner     = findViewById(R.id.forgotRoleSpinner);
        emailInput      = findViewById(R.id.forgotEmailInput);
        sendCodeButton  = findViewById(R.id.sendCodeButton);

        // Populate spinner with “User” / “Doctor”
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.forgot_roles_array,            // define “user” and “doctor” in strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
        sendCodeButton.setOnClickListener(v -> attemptSendCode());
    }

  private void attemptSendCode() {
    String email = emailInput.getText().toString().trim().toLowerCase();
    if (TextUtils.isEmpty(email)) {
        ToastUtil.show(this, "Enter your email", 1/3);
        return;
    }

    String role = roleSpinner.getSelectedItem().toString().toLowerCase();
    if (!role.equals("user") && !role.equals("doctor")) {
        ToastUtil.show(this, "Select role", 1/3);
        return;
    }

    sendCodeButton.setEnabled(false); // 🔒 Disable to prevent rapid multiple clicks
    sendVerificationRequest(role, email);
}


    private void sendVerificationRequest(String role, String email) {
        String url = ApiConfig.SEND_VERIFICATION_EMAIL_URL;

        JSONObject body;
        try {
            body = new JSONObject();
            body.put("role", role);
            body.put("email", email);
            Log.d("ForgotPwd", "Sending payload: " + body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.show(this, "Failed to build request", 1/3);
            sendCodeButton.setEnabled(true);
            sendCodeButton.setAlpha(1f);
            return;
        }

        sendCodeButton.setEnabled(false);
        sendCodeButton.setAlpha(0.5f);

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    boolean success = response.optBoolean("success", false);
                    if (success) {
                        ToastUtil.show(this, "Verification code sent. Check your email.", 1/3);
                        Intent i = new Intent(this, ResetPasswordActivity.class);
                        i.putExtra("role", role);
                        i.putExtra("email", email);
                        startActivity(i);
                        finish();
                    } else {
                        String err = response.optString("error", "Unknown error");
                        ToastUtil.show(this, "Error: " + err, 1/3);
                        // Re-enable
                        sendCodeButton.setEnabled(true);
                        sendCodeButton.setAlpha(1f);
                    }
                },
                error -> {
                    // Re-enable
                    sendCodeButton.setEnabled(true);
                    sendCodeButton.setAlpha(1f);

                    if (error.networkResponse != null) {
                        int code = error.networkResponse.statusCode;
                        String resp = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Log.e("ForgotPwd", "Raw server response: " + resp);
                        ToastUtil.show(this, "Server error: " + code, 1/3);
                    } else {
                        Log.e("ForgotPwd", "Volley error", error);
                        ToastUtil.show(this, "Network error: " + error.getMessage(), 1/3);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Content-Type", "application/json");
                return h;
            }
        };
        // Optional: disable caching so each press really hits the server
        req.setShouldCache(false);

        Volley.newRequestQueue(this).add(req);
    }

}
