package com.example.sql;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Collections;
import java.util.Set;

public class NotificationUtil {
    private static final String PREFERENCES_NAME = "app_preferences";
    private static final String NOTIFICATIONS_ENABLED_KEY = "notifications_enabled";
    private static final String PREFS_NAME        = "notif_prefs";
    private static final String KEY_WATER_ENABLED = "water_enabled";
    private static final String KEY_WATER_TIMES   = "water_times";
    private static final String KEY_MOTIV_ENABLED = "motivation_enabled";
    private static final String KEY_MOTIV_TIMES   = "motivation_times";

    // 2) Base request codes so water & motiv don’t collide
    private static final int BASE_WATER = 1000;
    private static final int BASE_MOTIV = 2000;

    public static void setNotificationsEnabled(Context context, boolean isEnabled) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(NOTIFICATIONS_ENABLED_KEY, isEnabled);
        editor.apply();
    }

    public static boolean isNotificationsEnabled(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(NOTIFICATIONS_ENABLED_KEY, true); // Default is true
    }

    public static void rescheduleAll(Context ctx) {
        // ↓ remove any call to cancelAll(ctx) if you want to keep older alarms
        SharedPreferences prefs =
                ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (prefs.getBoolean(KEY_WATER_ENABLED, false)) {
            scheduleCategory(
                    ctx,
                    prefs.getStringSet(KEY_WATER_TIMES, Collections.emptySet()),
                    BASE_WATER,
                    "Water reminder",
                    "Drink water to be healthier!"
            );
        }
        if (prefs.getBoolean(KEY_MOTIV_ENABLED, false)) {
            scheduleCategory(
                    ctx,
                    prefs.getStringSet(KEY_MOTIV_TIMES, Collections.emptySet()),
                    BASE_MOTIV,
                    "Let's go beyond our limits!",
                    "Witnessing progress in your body will be a great motivator."
            );
        }
    }

    // ← ADD this method right here in NotificationUtil.java
    private static void scheduleCategory(
            Context ctx,
            Set<String> times,
            int baseRequestCode,
            String title,
            String message
    ) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        int idx = 0;
        for (String hhmm : times) {
            String[] p = hhmm.split(":");
            int hour = Integer.parseInt(p[0]), min = Integer.parseInt(p[1]);
            int reqId = baseRequestCode + idx++;

            Intent i = new Intent(ctx, AlarmReceiver.class)
                    .setAction("com.yourapp.NOTIF_" + reqId);
            i.putExtra("notif_id", reqId);
            i.putExtra("title", title);
            i.putExtra("message", message);

            PendingIntent pi = PendingIntent.getBroadcast(
                    ctx,
                    reqId,
                    i,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, min);
            c.set(Calendar.SECOND, 0);
            if (c.before(Calendar.getInstance())) c.add(Calendar.DATE, 1);

            am.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    c.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pi
            );
        }
    }

    // ← And add this too, for selective cancellation if you ever need it:
    public static void cancelCategory(Context ctx, int count, int baseRequestCode) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < count; i++) {
            Intent intent = new Intent(ctx, AlarmReceiver.class)
                    .setAction("com.yourapp.NOTIF_" + (baseRequestCode + i));
            PendingIntent pi = PendingIntent.getBroadcast(
                    ctx,
                    baseRequestCode + i,
                    intent,
                    PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
            );
            if (pi != null) am.cancel(pi);
        }
    }
}
