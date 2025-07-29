package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class done extends AppCompatActivity {


        private ImageView backButton, videoButton, soundButton;
        private TextView exerciseNameText, repsCount, previousButton, skipButton;
        private Button doneButton;
private int userId;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_done);
            ThemeUtil.applyBackground(this, R.id.mainLayout);
            ThemeUtil.applyThemeFromPrefs(this);
            // Initialize views

            videoButton = findViewById(R.id.videoButton);
            soundButton = findViewById(R.id.soundButton);
            exerciseNameText = findViewById(R.id.exerciseNameText);
            repsCount = findViewById(R.id.repsCount);
            doneButton = findViewById(R.id.doneButton);
            previousButton = findViewById(R.id.previousButton);
            skipButton = findViewById(R.id.skipButton);
            userId = getIntent().getIntExtra("user_id", -1);
            // Set default text
            exerciseNameText.setText("KNEE PUSH-UPS");
            repsCount.setText("x14");

            // Set button actions
             // Go back
            videoButton.setOnClickListener(v -> playVideo());
            soundButton.setOnClickListener(v -> toggleSound());
            doneButton.setOnClickListener(v -> completeExercise());
            previousButton.setOnClickListener(v -> goToPrevious());
            skipButton.setOnClickListener(v -> skipExercise());
            ImageView exerciseImage = findViewById(R.id.exerciseImage);
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.ic_bbackground)  // Replace with your GIF resource ID
                    .into(exerciseImage);
        }

        private void playVideo() {
            // Logic to play the exercise video
        }

        private void toggleSound() {
            // Logic to toggle sound on/off
        }

        private void completeExercise() {
            // Logic to mark the exercise as complete
            Intent intent = new Intent(this, Gym.class);
            intent.putExtra("user_id",userId);
            startActivity(intent);
        }

        private void goToPrevious() {
            // Logic to go to the previous exercise
        }

        private void skipExercise() {
            // Logic to skip the current exercise
            Intent intent = new Intent(this, Gym.class);
            intent.putExtra("user_id",userId);
            startActivity(intent);
        }
    }
