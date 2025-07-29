package com.example.sql;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.lang.reflect.Type;


public class medicationalarmactivity extends AppCompatActivity {

    private EditText alarmDateTimeInput,medicationNameInput,dosageInput;
    private TextView currentDateTimeView;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isAlarmSet = false;
    private LocalDateTime alarmDateTime;
    public Button timer;
    private int userId;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ImageView backButton ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicationalarmactivity);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        String lang = getSharedPreferences("prefs", MODE_PRIVATE).getString("lang", "en");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
        dosageInput= findViewById(R.id.dosageInput);
        medicationNameInput= findViewById(R.id.medicationNameInput);
        alarmDateTimeInput = findViewById(R.id.alarmDateTimeInput);
        currentDateTimeView = findViewById(R.id.currentDateTimeView);
        Button setAlarmButton = findViewById(R.id.saveAlarmButton);
        timer=findViewById(R.id.timer);
        alarmDateTimeInput.setOnClickListener(v -> showDateTimePicker());
        userId = getIntent().getIntExtra("user_id", -1);
        setAlarmButton.setOnClickListener(v -> setAlarm());
        timer.setOnClickListener( v->{
            Intent ResttimerIntent = new Intent(this, TimerActivity.class);
            ResttimerIntent.putExtra("doctor_id", userId);
            startActivity(ResttimerIntent);});
        startClock();
        Button showListBtn = findViewById(R.id.btn_show_alarms);
        showListBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlarmListActivity.class);
            startActivity(intent);
        });
//        MobileAds.initialize(this, initializationStatus -> {});
//        AdView adView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);

    }

    private void setAlarm() {
        String inputDateTime = alarmDateTimeInput.getText().toString();
        String medName = medicationNameInput.getText().toString();
        String dosage = dosageInput.getText().toString();

        if (inputDateTime.isEmpty() || medName.isEmpty() || dosage.isEmpty()) {
            ToastUtil.show(this, "Please fill all fields", 1/3);
            return;
        }

        try {
            alarmDateTime = LocalDateTime.parse(inputDateTime, formatter);
            isAlarmSet = true;

            // Create alarm
            long timeMillis = alarmDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            int alarmId = (int) System.currentTimeMillis();

            AlarmData alarmData = new AlarmData();
            alarmData.id = alarmId;
            alarmData.hour = alarmDateTime.getHour();
            alarmData.minute = alarmDateTime.getMinute();
            alarmData.timeInMillis = timeMillis;
            alarmData.medicationName = medName;
            alarmData.dosage = dosage;
            alarmData.soundSelect = 0; // or use a spinner like in Alarm.java

            saveAlarmToPrefs(alarmData);
            scheduleAlarm(alarmData);

            ToastUtil.show(this, "Alarm set for: " + inputDateTime, 1/3);
        } catch (Exception e) {
            ToastUtil.show(this, "Invalid format. Use YYYY-MM-DD HH:MM:SS.", 1/3);
        }
    }


    private void startClock() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
                updateCurrentTimeView(currentDateTime);
                checkAlarm(currentDateTime);
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void updateCurrentTimeView(LocalDateTime currentDateTime) {
        currentDateTimeView.setText("Current Time: " + currentDateTime.format(formatter));
    }

    private void checkAlarm(LocalDateTime currentDateTime) {
        if (isAlarmSet && alarmDateTime != null && currentDateTime.equals(alarmDateTime.truncatedTo(ChronoUnit.SECONDS))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                requestOverlayPermission();
            } else {
                startOverlayService();
            }
            isAlarmSet = false;
        }
    }

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
            ToastUtil.show(this, "Please grant overlay permission", 1/3);
        }
    }

    private void startOverlayService() {
        try {
            Intent intent = new Intent(this, OverlayService.class);
            intent.putExtra("medicationNameInput","Medication Name:- "+medicationNameInput.getText().toString() );
            intent.putExtra("dosageInput","Dosage:- "+ dosageInput.getText().toString());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }ToastUtil.show(this, "Alarm Triggered!", 1/3);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "Error starting overlay service", 1/3);
        }
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                String formattedDateTime = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d:00",
                        year, month + 1, dayOfMonth, hourOfDay, minute);
                alarmDateTimeInput.setText(formattedDateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }





    private void saveAlarmToPrefs(AlarmData alarmData) {
        SharedPreferences prefs = getSharedPreferences("alarm_prefs", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = prefs.getString("alarms", "[]");
        Type type = new TypeToken<ArrayList<AlarmData>>() {}.getType();
        ArrayList<AlarmData> alarmList;
        alarmList = gson.fromJson(json, type);

        // Remove existing if same ID
        for (int i = 0; i < alarmList.size(); i++) {
            if (alarmList.get(i).id == alarmData.id) {
                alarmList.remove(i);
                break;
            }
        }

        alarmList.add(alarmData);
        prefs.edit().putString("alarms", gson.toJson(alarmList)).apply();
    }

    private void scheduleAlarm(AlarmData alarmData) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                ToastUtil.show(this, "Grant exact alarm permission and try again", 1/3);
                return;
            }
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("soundSelect", alarmData.soundSelect);
        intent.putExtra("medicationName", alarmData.medicationName); // ✅ Pass medication name
        intent.putExtra("dosage", alarmData.dosage);                 // ✅ Pass dosage

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, alarmData.id, intent, PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmData.timeInMillis, pendingIntent);
    }



}