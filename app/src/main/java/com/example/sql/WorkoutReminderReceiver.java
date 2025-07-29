package com.example.sql;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.Random;

public class   WorkoutReminderReceiver extends BroadcastReceiver {
    private static final int NOTIF_ID_BASE = 100;

    // Your four motivational quotes (you can edit or replace these)
    private static final String[] QUOTES = new String[]{
            "Psst! Ready to sweat it out? 👊\nLevel up your fitness with your next training adventure.",
            "⚔️ Let's go beyond our limits!\nWitnessing progress in your body will be a great motivator.",
            "Now is the time to act!\nDon't give up, stand up, start exercising!",
            "🚀 You can't stop now!\nOne more rep, one step closer to your goal! 🏅"
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        // 1) Extract the user_id that was baked into the PendingIntent
        //    (when we scheduled the alarm in Welcome.scheduleWeeklyNotifications(...))
        int alarmUserId = intent.getIntExtra("user_id", -1);
        if (alarmUserId < 0) {
            // No valid user_id → nothing to do
            return;
        }

        // 2) Load "current_user_id" and "last_user_id" from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int currentUserId = prefs.getInt("current_user_id", -1);
        int lastUserId    = prefs.getInt("last_user_id", -1);

        // 3) Decide which ID to consider "the owner of this alarm":
        //    If someone is still logged in (currentUserId != -1), use that.
        //    Otherwise, fall back to lastUserId.
        int effectiveUserId = (currentUserId != -1) ? currentUserId : lastUserId;

        // 4) If the alarmUserId doesn’t match whatever ID we consider active/last, bail out.
        if (alarmUserId != effectiveUserId) {
            // This alarm belongs to a user who is neither currently active nor last‐logged‐in.
            return;
        }

        // 5) Now we know this alarm is valid for "effectiveUserId". Show the notification.

        int dayOfWeek = intent.getIntExtra("day_of_week", -1);

        // Pick one random quote
        int idx = new Random().nextInt(QUOTES.length);
        String fullQuote = QUOTES[idx];

        // Optional: build a title like "Monday Workout!"
        String notifTitle = "Workout Reminder";
        if (dayOfWeek != -1) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            String dayName = cal.getDisplayName(
                    Calendar.DAY_OF_WEEK,
                    Calendar.LONG,
                    context.getResources().getConfiguration().getLocales().get(0)
            );
            notifTitle = dayName + " Workout!";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                "workout_reminder_channel" // ensure this matches the channel you created elsewhere
        )
                .setSmallIcon(R.drawable.ic_launcher_foreground) // replace with your own drawable
                .setContentTitle(notifTitle)
                .setContentText(fullQuote)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(fullQuote))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        // Unique notification ID so Monday’s alert doesn’t stomp Tuesday’s, for example:
        int notifId = NOTIF_ID_BASE + (dayOfWeek >= 0 ? dayOfWeek + alarmUserId * 10 : alarmUserId);
        manager.notify(notifId, builder.build());
    }
}
