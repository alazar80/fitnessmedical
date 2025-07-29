package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class choose extends AppCompatActivity {

    Button btnHomeWorkout, btnGymWorkout, btnMacroNutrient, btnFoodDatabase, btnWaterIntake, btnSleepTracker,btnProgressVisualization,generateWorkoutPlanButton,trackSleepButton;
public int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);

        userId = getIntent().getIntExtra("user_id", -1);


        // Initialize buttons
        ImageView backButton=findViewById(R.id.backButton);
        btnHomeWorkout = findViewById(R.id.btn_home_workout);
        btnGymWorkout = findViewById(R.id.btn_gym_workout);
        btnMacroNutrient = findViewById(R.id.btn_macro_nutrient);
        btnFoodDatabase = findViewById(R.id.btn_food_database);
        btnWaterIntake = findViewById(R.id.btn_water_intake);
        btnSleepTracker = findViewById(R.id.btn_sleep_tracker);

        generateWorkoutPlanButton = findViewById(R.id.bookAppointmentButton);

        backButton.setOnClickListener(v ->onBackPressed());


        // Set button listeners
        btnHomeWorkout.setOnClickListener(v -> {
            // Add functionality for Home Workout here
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        });

        btnGymWorkout.setOnClickListener(v -> {
            // Add functionality for Gym Workout here
            Intent intent = new Intent(this, Gym.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        });

        btnMacroNutrient.setOnClickListener(v -> {
            // Navigate to Calorie Tracker

            Intent intent = new Intent(this, MacronutrientCalculatorActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_top_right, R.anim.slide_out_bottom);
        });

        btnFoodDatabase.setOnClickListener(v -> {
            // Navigate to Workout Exercise

            Intent intent = new Intent(this, FoodDatabaseActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        });

        btnWaterIntake.setOnClickListener(v -> {
            // Navigate to Progress Tracking

            Intent intent = new Intent(this, WeightTrackerActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        btnSleepTracker.setOnClickListener(v -> {
            // Navigate to Other Features

            Intent intent = new Intent(this, SleepTrackerAcivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
        });

//        btnProgressVisualization.setOnClickListener(v -> {
//            // Navigate to Other Features
//
//            Intent intent = new Intent(this, ProgressTrackingActivity.class);
//            intent.putExtra("user_id", userId);
//            startActivity(intent);
//            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
//        });
//        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        generateWorkoutPlanButton.setOnClickListener(v -> {
            // Navigate to Progress Tracking

            Intent intent = new Intent(this, BookAppointmentActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

    }


}

