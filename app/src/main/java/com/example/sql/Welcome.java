package com.example.sql;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Welcome extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private static final String API_URL = ApiConfig.USER_WELCOME_INSERT;

    // Existing fields
    private EditText etHeight, etWeight;
    private Spinner spinnerGoal, spinnerExperience, spinnerDays;
    private Button btnContinue;

    // Seven CheckBoxes for exact weekdays
    private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;

    private String userId = "";
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);

        String lang = getSharedPreferences("prefs", MODE_PRIVATE).getString("lang", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(
                config,
                getBaseContext().getResources().getDisplayMetrics()
        );

        initViews();
        setupSpinners();

        int passedUserId = getIntent().getIntExtra("user_id", -1);
        email = getIntent().getStringExtra("email");

        if (passedUserId == -1 || email == null || email.isEmpty()) {
            ToastUtil.show(this, "Missing user data. Please login again.", 1/3);
            finish();
            return;
        }
        userId = String.valueOf(passedUserId);

        btnContinue.setOnClickListener(v -> handleContinue());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "workout_reminder_channel",
                    "Workout Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for weekly workout notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

    }

    private void initViews() {
        etHeight       = findViewById(R.id.et_height);
        etWeight       = findViewById(R.id.et_weight);
        spinnerGoal    = findViewById(R.id.spinner_goal);
        spinnerExperience = findViewById(R.id.spinner_experience);
        spinnerDays    = findViewById(R.id.spinner_days);   // KEEPING the spinner
        btnContinue    = findViewById(R.id.btn_continue);

        cbMonday    = findViewById(R.id.cb_monday);
        cbTuesday   = findViewById(R.id.cb_tuesday);
        cbWednesday = findViewById(R.id.cb_wednesday);
        cbThursday  = findViewById(R.id.cb_thursday);
        cbFriday    = findViewById(R.id.cb_friday);
        cbSaturday  = findViewById(R.id.cb_saturday);
        cbSunday    = findViewById(R.id.cb_sunday);
    }

    private void setupSpinners() {
        String[] fitnessGoals     = {"Lose Weight", "Build Muscle", "Improve Stamina", "Maintain Fitness"};
        String[] experienceLevels = {"Beginner", "Intermediate", "Advanced"};
        String[] workoutDays      = {"1-2 Days", "3-4 Days", "5-6 Days", "7 Days"};

        spinnerGoal.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fitnessGoals)
        );
        spinnerExperience.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, experienceLevels)
        );
        spinnerDays.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, workoutDays)
        );
    }

    private void handleContinue() {
        String height         = etHeight.getText().toString().trim();
        String weight         = etWeight.getText().toString().trim();
        String fitnessGoal    = spinnerGoal.getSelectedItem().toString();
        String experienceLevel= spinnerExperience.getSelectedItem().toString();
        String workoutDays    = spinnerDays.getSelectedItem().toString(); // e.g. "3-4 Days"

        if (height.isEmpty() || weight.isEmpty()) {
            ToastUtil.show(this, "Please enter height and weight.", 1/3);
            return;
        }

        // 1) Collect checked weekdays into a List<Integer>
        ArrayList<Integer> selectedWeekdays = new ArrayList<>();
        if (cbMonday.isChecked())    selectedWeekdays.add(Calendar.MONDAY);
        if (cbTuesday.isChecked())   selectedWeekdays.add(Calendar.TUESDAY);
        if (cbWednesday.isChecked()) selectedWeekdays.add(Calendar.WEDNESDAY);
        if (cbThursday.isChecked())  selectedWeekdays.add(Calendar.THURSDAY);
        if (cbFriday.isChecked())    selectedWeekdays.add(Calendar.FRIDAY);
        if (cbSaturday.isChecked())  selectedWeekdays.add(Calendar.SATURDAY);
        if (cbSunday.isChecked())    selectedWeekdays.add(Calendar.SUNDAY);

        // 2) Validate that at least one checkbox is selected
        if (selectedWeekdays.isEmpty()) {
            ToastUtil.show(this, "Select at least one workout day from checkboxes.", 1/3);
            return;
        }

        // 3) Build comma-separated string of weekday numbers (for your DB or logging, if desired)
        StringBuilder daysBuilder = new StringBuilder();
        for (int day : selectedWeekdays) {
            daysBuilder.append(day).append(",");
        }
        String weekdayNumbersParam = daysBuilder.substring(0, daysBuilder.length() - 1);
        // e.g. "2,4,6" for Mon, Wed, Fri

        // 4) Send profile data INCLUDING spinnerDays string to your API, on background thread
        new Thread(() -> sendProfileData(
                height,
                weight,
                fitnessGoal,
                experienceLevel,
                workoutDays   // still sending "1-2 Days", "3-4 Days", etc.
        )).start();

        // 5) Immediately schedule weekly notifications at 8 AM for the CHECKED weekdays
        scheduleWeeklyNotifications(selectedWeekdays);

        // 6) Navigate to next screen
        navigateToDashboard();
    }

    private void sendProfileData(
            String height,
            String weight,
            String fitnessGoal,
            String experienceLevel,
            String workoutDays   // e.g. "3-4 Days"
    ) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String data = "user_id=" + URLEncoder.encode(userId, "UTF-8")
                    + "&height=" + URLEncoder.encode(height, "UTF-8")
                    + "&weight=" + URLEncoder.encode(weight, "UTF-8")
                    + "&fitnessGoal=" + URLEncoder.encode(fitnessGoal, "UTF-8")
                    + "&experienceLevel=" + URLEncoder.encode(experienceLevel, "UTF-8")
                    + "&workoutDays=" + URLEncoder.encode(workoutDays, "UTF-8");
            // Still passing spinnerDays value here

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            os.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            String response = responseBuilder.toString();
            Log.d(TAG, "Server Response: " + response);

            JSONObject obj = new JSONObject(response);
            runOnUiThread(() -> {
                if (obj.optBoolean("success")) {
                    ToastUtil.show(this, "Profile saved successfully!", 1/3);
                    // We already called navigateToDashboard() in handleContinue(),
                    // so no need to call it again here
                } else {
                    ToastUtil.show(this, "Error: " + obj.optString("error"), 1/3);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error sending profile: " + e.getMessage());
            runOnUiThread(() ->
                    ToastUtil.show(this, "Error: " + e.getMessage(), 1/3)
            );
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(Welcome.this, second.class);
        intent.putExtra("user_id", Integer.parseInt(userId));
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    // ─── Helper Methods for Notification Scheduling ──────────────────────────────────────

    /**
     * Creates or reuses a notification channel named "workout_reminder_channel".
     * Required on Android 8.0+.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId   = "workout_reminder_channel";
            CharSequence name  = "Workout Reminders";
            String description = "Channel for weekly workout notifications";
            int importance     = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Schedules a repeating alarm at 8:00 AM for each selected weekday.
     * Uses AlarmManager.setRepeating(...) with a 7-day interval.
     */
    private void scheduleWeeklyNotifications(ArrayList<Integer> weekdays) {
        // Get the AlarmManager from this Activity
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Ensure the notification channel exists (Android O+)
        createNotificationChannel();

        for (int dayOfWeek : weekdays) {
            // 1) Build a Calendar set to that weekday at 08:00:00
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // 2) If “now” is already after this weekday-8 AM, or this weekday is before today, roll forward a week
            Calendar now = Calendar.getInstance();
            if (now.after(calendar) || dayOfWeek < now.get(Calendar.DAY_OF_WEEK)) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            }

            // 3) Build an Intent to fire WorkoutReminderReceiver
            Intent intent = new Intent(Welcome.this, WorkoutReminderReceiver.class);
            intent.putExtra("day_of_week", dayOfWeek);
            intent.putExtra("user_id", Integer.parseInt(userId));  // <— make sure you put the numeric ID here

            int requestCode = dayOfWeek + Integer.parseInt(userId) * 10;
            PendingIntent pi = PendingIntent.getBroadcast(
                    Welcome.this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7,
                    pi
            );

        }
    }
    // ────────────────────────────────────────────────────────────────────────────────────
}
