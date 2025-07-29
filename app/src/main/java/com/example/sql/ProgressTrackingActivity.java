package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressTrackingActivity extends AppCompatActivity {

    private EditText weightInput, heightInput;
    private TextView bmiResult;
    private Button calculateBMIButton, calculateBodyFatButton, openWeightTrackerButton, viewProgressChartButton;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        //ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_tracking);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);


        // Initialize UI components

        calculateBodyFatButton = findViewById(R.id.calculateBodyFatButton);
        openWeightTrackerButton = findViewById(R.id.openWeightTrackerButton);
        viewProgressChartButton = findViewById(R.id.viewProgressChartButton);

        // Set listeners
        calculateBMIButton.setOnClickListener(v -> calculateBMI());
        calculateBodyFatButton.setOnClickListener(v -> BodyFatPercentageCalculatorAcitivity());
        openWeightTrackerButton.setOnClickListener(v -> WeightTrackerActivity());
        viewProgressChartButton.setOnClickListener(v -> ProgressVisualizationActivity());
    }

    private void calculateBMI() {
        try {
            double weight = Double.parseDouble(weightInput.getText().toString());
            double height = Double.parseDouble(heightInput.getText().toString());

            // BMI Formula
            double bmi = weight / (height * height);

            // Categorize BMI
            String category;
            if (bmi < 18.5) {
                category = "Underweight";
            } else if (bmi < 24.9) {
                category = "Normal weight";
            } else if (bmi < 29.9) {
                category = "Overweight";
            } else {
                category = "Obese";
            }

            // Display Result
            bmiResult.setText(String.format("BMI: %.2f\nCategory: %s", bmi, category));
        } catch (Exception e) {
            ToastUtil.show(this, "Please enter valid weight and height.", 1/3);
        }
    }

    private void WeightTrackerActivity() {
        intent = new Intent(ProgressTrackingActivity.this, WeightTrackerActivity.class);
        startActivity(intent);
    }

    private void ProgressVisualizationActivity() {
        intent = new Intent(ProgressTrackingActivity.this, ProgressTrackingActivity.class);
        startActivity(intent);
    }
    private void BodyFatPercentageCalculatorAcitivity(){
        intent = new Intent(ProgressTrackingActivity.this, BodyFatPercentageCalculatorAcitivity.class);
        startActivity(intent);
    }
}

