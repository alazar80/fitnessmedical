package com.example.sql;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StopAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (AlarmReceiver.ringtone != null && AlarmReceiver.ringtone.isPlaying()) {
                AlarmReceiver.ringtone.stop();
                AlarmReceiver.ringtone = null; // release reference
            }
        } catch (Exception e) {
            Log.e("StopAlarmReceiver", "Failed to stop ringtone", e);
        }
    }
}
