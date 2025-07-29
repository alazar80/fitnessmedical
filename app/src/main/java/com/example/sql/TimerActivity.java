package com.example.sql;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Configuration;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

        private EditText timerInputEditText;
        private TextView timerTextView;
        private Button startButton, resetButton;
        private CountDownTimer countDownTimer;
        private boolean isRunning = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_timer);
            String lang = getSharedPreferences("prefs", MODE_PRIVATE).getString("lang", "en");
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            timerInputEditText = findViewById(R.id.timerInputEditText);
            timerTextView = findViewById(R.id.timerTextView);
            startButton = findViewById(R.id.startButton);
            resetButton = findViewById(R.id.resetButton);

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isRunning) {
                        startTimer();
                    } else {
                        ToastUtil.show(TimerActivity.this, "Timer is already running!", 1/3);
                    }
                }
            });

            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetTimer();
                }
            });
        }

        private void startTimer() {
            String input = timerInputEditText.getText().toString();
            if (input.isEmpty()) {
                ToastUtil.show(this, "Please enter rest time in seconds.", 1/3);
                return;
            }

            int timeInSeconds = Integer.parseInt(input);
            if (timeInSeconds <= 0) {
                ToastUtil.show(this, "Please enter a valid rest time.", 1/3);
                return;
            }

            isRunning = true;
            startButton.setEnabled(false);
            countDownTimer = new CountDownTimer(timeInSeconds * 1000L, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int secondsLeft = (int) (millisUntilFinished / 1000);
                    timerTextView.setText(formatTime(secondsLeft));
                }

                @Override
                public void onFinish() {
                    timerTextView.setText("00:00");
                    isRunning = false;
                    startButton.setEnabled(true);
                    ToastUtil.show(TimerActivity.this, "Rest time is over!", 1/3);
                }
            }.start();
        }

        private void resetTimer() {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            isRunning = false;
            timerTextView.setText("00:00");
            startButton.setEnabled(true);
        }

        private String formatTime(int totalSeconds) {
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
