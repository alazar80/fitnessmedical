package com.example.sql;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DoctorSettingsActivity extends BaseActivity {
    private int userId, doctorId;
    private Spinner languageSpinner;
    private Switch themeSwitch, notificationSwitch;
    private Switch soundToggle, screenToggle, vibrationToggle;
    private Switch highContrastToggle, colorBlindToggle, voiceToggle;
    private View editProfileBtn, changePasswordBtn, screenReaderBtn;
    private Button logoutBtn;
    private Button btnSmall, btnMedium, btnLarge;
    private ImageView backButton;
    public static boolean LANGUAGE_CHANGED = false;
    Spinner goalSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isHighContrast = prefs.getBoolean("highContrast", false);
        boolean isColorBlind = prefs.getBoolean("colorBlind", false);

        if (isHighContrast) {
            setTheme(R.style.AppTheme_HighContrast);
        } else if (isColorBlind) {
            setTheme(R.style.AppTheme_ColorBlind);
        } else {
            setTheme(R.style.AppTheme); // default
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        if (prefs.getBoolean("keepScreenOn", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        // Grab IDs from Intent
        userId = getIntent().getIntExtra("user_id", -1);
        doctorId = getIntent().getIntExtra("doctor_id", -1);

        // Find views
        themeSwitch = findViewById(R.id.switchbutton);
        notificationSwitch = findViewById(R.id.notification_switch);
        languageSpinner = findViewById(R.id.language_spinner);
        soundToggle = findViewById(R.id.soundToggle);
        screenToggle = findViewById(R.id.screenToggle);
//        vibrationToggle = findViewById(R.id.vibrationToggle);
        highContrastToggle = findViewById(R.id.highContrastToggle);
        colorBlindToggle = findViewById(R.id.colorBlindToggle);
//        voiceToggle = findViewById(R.id.voiceToggle);
        editProfileBtn = findViewById(R.id.editProfileButton);
        changePasswordBtn = findViewById(R.id.changePasswordButton);

        screenReaderBtn = findViewById(R.id.screenReaderButton);
        logoutBtn = findViewById(R.id.logout);
        backButton = findViewById(R.id.backButton);

        // Text-size buttons
        btnSmall  = findViewById(R.id.textSizeSmall);
        btnMedium = findViewById(R.id.textSizeMedium);
        btnLarge  = findViewById(R.id.textSizeLarge);

        btnSmall.setOnClickListener(v -> setFontScale(0.85f));
        btnMedium.setOnClickListener(v -> setFontScale(1.00f));
        btnLarge.setOnClickListener(v -> setFontScale(1.15f));




        // ─── PLACE THE “fetch current goal” REQUEST HERE ───
        // (so that goalAdapter and goalSpinner already exist)
//        String urlGet = ApiConfig.GET_USER_GOAL
//                + "?user_id=" + userId;
//        RequestQueue queue = Volley.newRequestQueue(this);
//        StringRequest getReq = new StringRequest(
//                Request.Method.GET,
//                urlGet,
//                response -> {
//                    try {
//                        JSONObject obj = new JSONObject(response);
//                        if (obj.getBoolean("success")) {
//                            String serverGoal = obj.getString("goal");
//                            int idx = goalAdapter.getPosition(serverGoal);
//                            if (idx >= 0) {
//                                goalSpinner.setSelection(idx);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                },
//                error -> {
//                    // network or parse error (you can log or ignore)
//                }
//        );
//        queue.add(getReq);
        // ─────────────────────────────────────────────────────

        // 2) Now you can attach your OnItemSelectedListener



        SharedPreferences.Editor editor = prefs.edit();

        // Theme toggle
        themeSwitch.setChecked(ThemeUtil.isNightMode(this));
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeUtil.saveThemePreference(this, isChecked);
            recreate();
        });

        // Notifications toggle
        notificationSwitch.setChecked(NotificationUtil.isNotificationsEnabled(this));
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NotificationUtil.setNotificationsEnabled(this, isChecked);
            ToastUtil.show(this, "Notifications " + (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT);
        });

        // Sound toggle
        soundToggle.setChecked(prefs.getBoolean("soundEnabled", true));
        soundToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("soundEnabled", isChecked).apply();
            ToastUtil.show(this, "Sound " + (isChecked ? "on" : "off"), Toast.LENGTH_SHORT);
        });

        // Keep screen on toggle
        screenToggle.setChecked(prefs.getBoolean("keepScreenOn", false));
        screenToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("keepScreenOn", isChecked).apply();
            if (isChecked) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });

        // Vibration feedback toggle
//        vibrationToggle.setChecked(prefs.getBoolean("vibrationEnabled", true));
//        vibrationToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            editor.putBoolean("vibrationEnabled", isChecked).apply();
//            ToastUtil.show(this, "Vibration " + (isChecked ? "on" : "off"), 1/3);
//        });

        // High contrast mode toggle
        highContrastToggle.setChecked(prefs.getBoolean("highContrast", false));
        highContrastToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("highContrast", isChecked).apply();
            recreate();
        });

        // Color-blind mode toggle
        colorBlindToggle.setChecked(prefs.getBoolean("colorBlind", false));
        colorBlindToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("colorBlind", isChecked).apply();
            recreate();
        });

        // Voice guidance toggle
//        voiceToggle.setChecked(prefs.getBoolean("voiceGuidance", false));
//        voiceToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            editor.putBoolean("voiceGuidance", isChecked).apply();
//            ToastUtil.show(this, "Voice guidance " + (isChecked ? "on" : "off"), 1/3);
//        });

        // Language spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
        String currLang = prefs.getString("lang", "en");
        languageSpinner.setSelection(currLang.equals("am") ? 1 : 0);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onNothingSelected(AdapterView<?> parent) {}
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sel = (position == 0) ? "en" : "am";
                if (!sel.equals(currLang)) {
                    LocaleHelper.setLocale(DoctorSettingsActivity.this, sel);
                    editor.putString("lang", sel).apply();
                    LANGUAGE_CHANGED = true;
                    onBackPressed();
                }
            }
        });

        // Navigation buttons
        editProfileBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, UserProfileEditActivity.class);
            i.putExtra("user_id", userId);
            i.putExtra("doctor_id", doctorId);
            startActivity(i);
        });
        changePasswordBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, ChangePasswordActivity.class);
            i.putExtra("user_id", userId);
            i.putExtra("doctor_id", doctorId);
            startActivity(i);
        });
//        goal_spinner.setOnClickListener(v -> {
//            Intent i = new Intent(this, second.class);
//            i.putExtra("user_id", userId);
//            i.putExtra("doctor_id", doctorId);
//            startActivity(i);
//        });
        screenReaderBtn.setOnClickListener(v -> {
            Intent i = new Intent(this, second.class);
            i.putExtra("user_id", userId);
            i.putExtra("doctor_id", doctorId);
            startActivity(i);
        });
        logoutBtn.setOnClickListener(v -> {
            prefs.edit()
                    .putInt("last_user_id", userId)
                    .remove("current_user_id")
                    .putBoolean("keepLoggedIn", false)
                    .remove("current_user_role")
                    .apply();


            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("user_id", userId);
            i.putExtra("doctor_id", doctorId);
            startActivity(i);
        });
        backButton.setOnClickListener(v -> onBackPressed());
    }

    // Class-level helper for text size
    private void setFontScale(float scale) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        prefs.edit().putFloat("fontScale", scale).apply();
        recreate();
    }

    // Override getResources to apply font scale
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        float fontScale = prefs.getFloat("fontScale", 1.0f);
        Configuration config = new Configuration(res.getConfiguration());
        if (config.fontScale != fontScale) {
            config.fontScale = fontScale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return res;
    }


    @Override
    public void onBackPressed() {
        if (LANGUAGE_CHANGED) {
            Intent intent = new Intent(this, DoctorsActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("doctor_id", doctorId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

}
