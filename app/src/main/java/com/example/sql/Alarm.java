package com.example.sql;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class Alarm extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int REQUEST_CODE = 100;

    private TimePicker alarmTimePicker;
    private TextView alarmState;
    private int soundSelect;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        alarmTimePicker = findViewById(R.id.timePicker);
        alarmState = findViewById(R.id.alarm_state);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Button showListBtn = findViewById(R.id.btn_show_alarms);
        showListBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlarmListActivity.class);
            startActivity(intent);
        });
        Intent editIntent = getIntent();
        if (editIntent.hasExtra("edit_alarm_id")) {
            int hour = editIntent.getIntExtra("edit_hour", 0);
            int minute = editIntent.getIntExtra("edit_minute", 0);
            soundSelect = editIntent.getIntExtra("edit_sound", 0);

            alarmTimePicker.setHour(hour);
            alarmTimePicker.setMinute(minute);

            alarmState.setText("Editing alarm: " + String.format("%02d:%02d", hour, minute));
        }


        setupSpinner();
        setupAlarmOnButton();
        setupAlarmOffButton();
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.stepbrothers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void setupAlarmOnButton() {
        Button alarmOn = findViewById(R.id.alarm_on);
        alarmOn.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Intent permissionIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(permissionIntent);
                    ToastUtil.show(this, "Please allow exact alarm permission and try again", 1/3);
                    return;
                }
            }

            int hour = alarmTimePicker.getHour();
            int minute = alarmTimePicker.getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // If selected time is before now, schedule for next day
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            // Create unique ID for each alarm
            int alarmId = getIntent().hasExtra("edit_alarm_id") ?
                    getIntent().getIntExtra("edit_alarm_id", -1) :
                    (int) System.currentTimeMillis();


            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("soundSelect", soundSelect);

            pendingIntent = PendingIntent.getBroadcast(
                    this, alarmId, intent, PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            // Save alarm data
            AlarmData alarmData = new AlarmData();
            alarmData.id = alarmId;
            alarmData.hour = hour;
            alarmData.minute = minute;
            alarmData.soundSelect = soundSelect;
            alarmData.timeInMillis = calendar.getTimeInMillis();

            saveAlarmToPrefs(alarmData); // New method for multiple alarm support

            String hourStr = String.format("%02d", hour);
            String minuteStr = String.format("%02d", minute);
            alarmState.setText("Alarm set for: " + hourStr + ":" + minuteStr);
            ToastUtil.show(this, "Alarm set successfully!", 1/3);
        });

    }

    private void setupAlarmOffButton() {
        Button alarmOff = findViewById(R.id.alarm_off);
        alarmOff.setOnClickListener(view -> {
            if (alarmManager != null && pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                alarmState.setText("Alarm canceled");
                ToastUtil.show(this, "Alarm canceled!", 1/3);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        soundSelect = position;
    }
    private void saveAlarmData(Calendar calendar, int hour, int minute, int soundSelect) {
        getSharedPreferences("alarm_prefs", MODE_PRIVATE)
                .edit()
                .putLong("alarm_time", calendar.getTimeInMillis())
                .putInt("alarm_hour", hour)
                .putInt("alarm_minute", minute)
                .putInt("alarm_sound", soundSelect)
                .apply();
    }
    private void saveAlarmToPrefs(AlarmData alarmData) {
        SharedPreferences prefs = getSharedPreferences("alarm_prefs", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = prefs.getString("alarms", "[]");
        Type type = new TypeToken<ArrayList<AlarmData>>() {}.getType();
        ArrayList<AlarmData> alarmList = gson.fromJson(json, type);

        // Remove old alarm with the same ID if it exists
        for (int i = 0; i < alarmList.size(); i++) {
            if (alarmList.get(i).id == alarmData.id) {
                alarmList.remove(i);
                break;
            }
        }

        alarmList.add(alarmData);

        String updatedJson = gson.toJson(alarmList);
        prefs.edit().putString("alarms", updatedJson).apply();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No action needed
    }
}
