package com.example.sql;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText tokenInput, newPasswordInput;
    private Button resetButton;
    private String role, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        // Grab role + email from Intent extras
        role  = getIntent().getStringExtra("role");   // “user” or “doctor”
        email = getIntent().getStringExtra("email");

        tokenInput       = findViewById(R.id.resetTokenInput);
        newPasswordInput = findViewById(R.id.resetNewPasswordInput);
        resetButton      = findViewById(R.id.resetPasswordButton);
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
        resetButton.setOnClickListener(v -> attemptReset());
    }

    private void attemptReset() {
        String token   = tokenInput.getText().toString().trim();
        String newPass = newPasswordInput.getText().toString().trim();

        // 1) token: non‐empty, exactly 32 hex chars
        if (TextUtils.isEmpty(token) ||
                token.length() != 6 ||
                !token.matches("[0-9A-Fa-f]{6}")) {
            ToastUtil.show(
                    this,
                    "Enter a valid 6‐character token",
                    1/3
            );
            return;
        }

        // 2) new password: ≥ 6 chars
        if (TextUtils.isEmpty(newPass) || newPass.length() < 6) {
            ToastUtil.show(
                    this,
                    "Enter a new password (at least 6 characters)",
                   1/3
            );
            return;
        }

        // If you also want to enforce e.g. “contains digit” or “uppercase”, add further regex checks here.

        // 3) Everything valid → call server
        sendResetRequest(role, email, token, newPass);
    }



//    private void attemptReset() {
//        String token     = tokenInput.getText().toString().trim();
//        String newPass   = newPasswordInput.getText().toString().trim();
//
//        if (TextUtils.isEmpty(token)) {
//            ToastUtil.show(this, "Enter the verification token", 1/3);
//            return;
//        }
//        if (TextUtils.isEmpty(newPass) || newPass.length() < 6) {
//            ToastUtil.show(this, "Enter a new password (min 6 chars)", 1/3);
//            return;
//        }
//
//        sendResetRequest(role, email, token, newPass);
//    }

    private void sendResetRequest(
            String role,
            String email,
            String token,
            String newPass
    ) {
        String url = ApiConfig.RESET_PASSWORD_URL; // e.g. "https://yourdomain.com/reset_password.php"

        try {
            JSONObject body = new JSONObject();
            body.put("role",         role);
            body.put("email",        email);
            body.put("token",        token);
            body.put("new_password", newPass);

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    response -> {
                        boolean success = response.optBoolean("success", false);
                        if (success) {
                            ToastUtil.show(
                                    ResetPasswordActivity.this,
                                    "Password reset successful. You can now log in.",
                                    1/3
                            );
                            finish(); // go back to LoginActivity
                        } else {
                            String err = response.optString("error", "Unknown error");
                            ToastUtil.show(
                                    ResetPasswordActivity.this,
                                    "Error: " + err,
                                    1/3
                            );
                        }
                    },
                    error -> ToastUtil.show(
                            ResetPasswordActivity.this,
                            "Network error: " + error.getMessage(),
                           1/3
                    )
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> h = new HashMap<>();
                    h.put("Content-Type", "application/json");
                    return h;
                }
            };

            Volley.newRequestQueue(this).add(req);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "Request error", 1/3);
        }
    }
}
