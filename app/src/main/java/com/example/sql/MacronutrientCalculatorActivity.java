package com.example.sql;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MacronutrientCalculatorActivity extends AppCompatActivity {

    private EditText calorieGoalEditText;
    private Spinner dietaryPreferenceSpinner;
    private Button calculateButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macronutrient_calculator);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        calorieGoalEditText = findViewById(R.id.calorieGoalEditText);
        dietaryPreferenceSpinner = findViewById(R.id.dietaryPreferenceSpinner);
        calculateButton = findViewById(R.id.calculateButton);
        resultTextView = findViewById(R.id.resultTextView);
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {   onBackPressed();});
        // Set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dietary_preferences, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dietaryPreferenceSpinner.setAdapter(adapter);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateMacros();
            }
        });
    }

    private void calculateMacros() {
        String calorieGoalStr = calorieGoalEditText.getText().toString();
        if (calorieGoalStr.isEmpty()) {
            ToastUtil.show(this, "Please enter your calorie goal.", 1/3);
            return;
        }

        int calorieGoal = Integer.parseInt(calorieGoalStr);
        String dietaryPreference = dietaryPreferenceSpinner.getSelectedItem().toString();

        double proteinRatio, carbRatio, fatRatio;

        // Set ratios based on dietary preference
        switch (dietaryPreference) {
            case "Keto":
                proteinRatio = 0.25;
                carbRatio = 0.05;
                fatRatio = 0.70;
                break;
            case "Low-Carb":
                proteinRatio = 0.40;
                carbRatio = 0.20;
                fatRatio = 0.40;
                break;
            case "High-Protein":
                proteinRatio = 0.50;
                carbRatio = 0.30;
                fatRatio = 0.20;
                break;
            default:
                proteinRatio = 0.30;
                carbRatio = 0.50;
                fatRatio = 0.20;
                break;
        }

        // Calculate macros
        double proteinGrams = (calorieGoal * proteinRatio) / 4; // 1g protein = 4kcal
        double carbGrams = (calorieGoal * carbRatio) / 4;       // 1g carb = 4kcal
        double fatGrams = (calorieGoal * fatRatio) / 9;         // 1g fat = 9kcal

        String result = String.format("Protein: %.1f g\nCarbohydrates: %.1f g\nFats: %.1f g",
                proteinGrams, carbGrams, fatGrams);

        resultTextView.setText(result);
    }
}
