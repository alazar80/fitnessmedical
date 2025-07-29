package com.example.sql;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class AlarmReceiver extends BroadcastReceiver {
    public static Ringtone ringtone; // static so we can stop it later

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get medication data from intent
        String medicationName = intent.getStringExtra("medicationName");
        String dosage = intent.getStringExtra("dosage");

        // Pass data to notification helper
        NotificationHelper.showAlarmNotification(context, medicationName, dosage);

        // Play alarm sound
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        if (ringtone != null) {
            try {
                ringtone.play();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
}
