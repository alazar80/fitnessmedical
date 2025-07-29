package com.example.sql;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OtherFeaturesActivity extends AppCompatActivity {

    private EditText waterIntakeInput, goalInput;
    private TextView waterStatus, goalStatus;
    private Button logWaterButton,  setGoalButton;
    Intent intent;
    private double dailyWaterTarget = 3000; // in milliliters
    private double waterConsumed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_features);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);

        // Initialize UI components
        waterIntakeInput = findViewById(R.id.waterIntakeInput);
        waterStatus = findViewById(R.id.waterStatus);
        logWaterButton = findViewById(R.id.logWaterButton);



        // Set listeners
        logWaterButton.setOnClickListener(v -> logWaterIntake());

        setGoalButton.setOnClickListener(v -> setGoal());

    }

    private void logWaterIntake() {
        try {
            double water = Double.parseDouble(waterIntakeInput.getText().toString());
            waterConsumed += water;

            // Update water status
            if (waterConsumed >= dailyWaterTarget) {
                waterStatus.setText(String.format("Congratulations! You've met your daily water target of %.0f ml!", dailyWaterTarget));
            } else {
                waterStatus.setText(String.format("Water Consumed: %.0f ml\nRemaining: %.0f ml",
                        waterConsumed, dailyWaterTarget - waterConsumed));
            }
        } catch (Exception e) {
            ToastUtil.show(this, "Please enter a valid water intake amount.", 1/3);
        }
    }

    private void setGoal() {
        String goal = goalInput.getText().toString().trim();
        if (!goal.isEmpty()) {
            goalStatus.setText(String.format("Your Goal: %s", goal));
            ToastUtil.show(this, "Goal Set Successfully!", 1/3);
        } else {
            ToastUtil.show(this, "Please enter a fitness goal.", 1/3);
        }
    }



}
