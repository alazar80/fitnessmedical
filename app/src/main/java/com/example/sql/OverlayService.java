package com.example.sql;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class OverlayService extends Service {

    private WindowManager windowManager;
    private View overlayView;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Check overlay permission before proceeding
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            ToastUtil.show(this, "Overlay permission not granted!", 1/3);
            stopSelf();
            return START_NOT_STICKY;
        }
        showOverlay(intent);
        return START_STICKY;
    }
    //        playAlarmSound();

    private void showOverlay(Intent intent) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT
        );
        if (intent != null && intent.hasExtra("medicationNameInput")) {
            String medicationNameInput = intent.getStringExtra("medicationNameInput");
            String dosage = intent.getStringExtra("dosageInput");
            TextView motivationalText = overlayView.findViewById(R.id.motivationalText);
            motivationalText.setText(medicationNameInput);
            TextView dosageText = overlayView.findViewById(R.id.dosageText);
            dosageText.setText(dosage);
        }
        windowManager.addView(overlayView, params);
        overlayView.findViewById(R.id.dismissButton).setOnClickListener(v -> stopSelf());
    }

    private void playAlarmSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.kalimba);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null) {
            windowManager.removeView(overlayView);
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
