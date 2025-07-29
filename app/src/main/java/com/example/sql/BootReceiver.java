package com.example.sql;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        SharedPreferences prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("alarms", "[]");
        Type type = new TypeToken<ArrayList<AlarmData>>() {}.getType();
        ArrayList<AlarmData> alarmList = gson.fromJson(json, type);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (AlarmData alarm : alarmList) {
            if (alarm.timeInMillis < System.currentTimeMillis()) {
                // Skip or reschedule to next day
                alarm.timeInMillis += 24 * 60 * 60 * 1000;
            }

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("soundSelect", alarm.soundSelect);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, alarm.id, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.timeInMillis, pendingIntent);
        }

        ToastUtil.show(context, "All alarms re-scheduled", 1/3);
    }
}
