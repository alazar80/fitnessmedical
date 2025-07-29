package com.example.sql;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton, signupButton;
    private FrameLayout blurOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean already = prefs.getBoolean("keepLoggedIn", false);
        int savedUserId = prefs.getInt("current_user_id", -1);
        initViews();
        setupLanguageSpinner();

//        Button notepadButton  = findViewById(R.id.notepadButton);
        Button contactButton  = findViewById(R.id.contactButton);
//        Button wallpaperButton = findViewById(R.id.wallpaperButton);
        Button quoteButton = findViewById(R.id.quoteButton);
        Button sketchButton=findViewById(R.id.sketchButton);
//        Button guitarButton = findViewById(R.id.guitarButton);

//        guitarButton.setOnClickListener(v ->
//                startActivity(new Intent(this, GuitarTunerActivity.class))
//        );
        quoteButton.setOnClickListener(v ->
                startActivity(new Intent(this, ZenQuotesActivity.class))
        );
//        notepadButton.setOnClickListener(v ->
//                startActivity(new Intent(this, Notepad.class))
//        );
        contactButton.setOnClickListener(v ->
                startActivity(new Intent(this, SaveContactsActivity.class))
        );
//        wallpaperButton.setOnClickListener(v ->
//                startActivity(new Intent(this, WallpaperActivity.class))
//        );
        sketchButton.setOnClickListener(v ->
                startActivity(new Intent(this, Sketch.class))
        );

    }

    private void initViews() {
        emailInput    = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton   = findViewById(R.id.loginButton);
        signupButton  = findViewById(R.id.signUpButton);
        blurOverlay   = findViewById(R.id.blurOverlay);

        loginButton.setOnClickListener(v -> validateAndLogin());
        signupButton.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class))
        );

        passwordInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateAndLogin();
                return true;
            }
            return false;
        });

        Switch themeSwitch = findViewById(R.id.switchbutton);
        themeSwitch.setChecked(ThemeUtil.isNightMode(this));
        themeSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
            ThemeUtil.saveThemePreference(this, isChecked);
            recreate();
        });
        TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);
        forgotPasswordText.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(i);
        });
        TextView termsandconditions = findViewById(R.id.termsandconditions);
        termsandconditions.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, Termsandconditions.class);
            startActivity(i);
        });
    }

    private void setupLanguageSpinner() {
        Spinner spinner = findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.language_array, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String current = getSharedPreferences("prefs", MODE_PRIVATE)
                .getString("lang", "en");
        spinner.setSelection(current.equals("am") ? 1 : 0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                String sel = (pos == 0) ? "en" : "am";
                if (!sel.equals(current)) {
                    LocaleHelper.setLocale(LoginActivity.this, sel);
                    recreate();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void validateAndLogin() {
        String email = emailInput.getText().toString()
                .trim().toLowerCase();
        String pass  = passwordInput.getText().toString()
                .trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            ToastUtil.show(this, "Enter both email & password", Toast.LENGTH_SHORT);
            return;
        }
        callLoginApi(email, pass);
    }

    private void callLoginApi(String email, String password) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurOverlay.setVisibility(View.VISIBLE);
            blurOverlay.setRenderEffect(
                    RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            );
        }

        StringRequest req = new StringRequest(
                Request.Method.POST,
                ApiConfig.LOGIN_URL,
                resp -> {
                    hideBlur();
                    try {
                        JSONObject o = new JSONObject(resp);
                        if (o.getBoolean("success")) {
                            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                            String role = o.getString("role").toLowerCase();

                            prefs.edit()
                                    .putInt("current_user_id", o.getInt("user_id"))
                                    .putBoolean("keepLoggedIn", true)
                                    .putString("current_user_role", role)  // so your redirect knows where to send them
                                    .apply();

                            Intent i;
                            switch (role) {
                                case "doctor":
                                    i = new Intent(this, DoctorsActivity.class)
                                            .putExtra("doctor_id", o.getInt("user_id"));
                                    break;
                                case "admin":
                                    i = new Intent(this, AdminActivity.class)
                                            .putExtra("admin_id", o.getInt("user_id"));
                                    break;
                                default:
                                    i = new Intent(this,second.class)
                                            .putExtra("user_id", o.getInt("user_id"));
                            }
                            startActivity(i);
                            finish();
                        }
                        else {
                            ToastUtil.show(this,
                                    "Login failed: " + o.getString("error"),
                                    Toast.LENGTH_SHORT
                            );

                        }
                    } catch (Exception e) {
                        ToastUtil.show(this, "Parse error", Toast.LENGTH_SHORT);
                    }
                },
                err -> {
                    hideBlur();
                    ToastUtil.show(this,
                            "Network error: " + err.getMessage(),
                            Toast.LENGTH_SHORT
                    );
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("email", email);
                p.put("password", password);
                return p;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        Volley.newRequestQueue(this).add(req);
    }

    private void hideBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurOverlay.setRenderEffect(null);
            blurOverlay.setVisibility(View.GONE);
        }
    }
}
