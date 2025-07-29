package com.example.sql;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class BodyFatPercentageCalculatorAcitivity extends AppCompatActivity {



        private EditText waistEditText, neckEditText, heightEditText;
        private Spinner genderSpinner;
        private Button calculateButton;
        private TextView resultTextView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_body_fat_percentage_calculator_acitivity);
            ThemeUtil.applyBackground(this, R.id.mainLayout);
            ThemeUtil.applyThemeFromPrefs(this);

            waistEditText = findViewById(R.id.waistEditText);
            neckEditText = findViewById(R.id.neckEditText);
            heightEditText = findViewById(R.id.heightEditText);
            genderSpinner = findViewById(R.id.genderSpinner);
            calculateButton = findViewById(R.id.calculateButton);
            resultTextView = findViewById(R.id.resultTextView);

            // Set up gender spinner
            ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                    R.array.gender_options, android.R.layout.simple_spinner_item);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genderSpinner.setAdapter(genderAdapter);

            calculateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calculateBodyFat();
                }
            });
        }

        private void calculateBodyFat() {
            String waistStr = waistEditText.getText().toString();
            String neckStr = neckEditText.getText().toString();
            String heightStr = heightEditText.getText().toString();

            if (waistStr.isEmpty() || neckStr.isEmpty() || heightStr.isEmpty()) {
                ToastUtil.show(this, "Please fill in all fields.", 1/3);
                return;
            }

            double waist = Double.parseDouble(waistStr);
            double neck = Double.parseDouble(neckStr);
            double height = Double.parseDouble(heightStr);
            String gender = genderSpinner.getSelectedItem().toString();

            if (waist <= 0 || neck <= 0 || height <= 0) {
                ToastUtil.show(this, "Enter valid positive values.", 1/3);
                return;
            }

            double bodyFatPercentage;
            if (gender.equals("Male")) {
                // Navy formula for males
                bodyFatPercentage = 86.01 * Math.log10(waist - neck) - 70.041 * Math.log10(height) + 36.76;
            } else {
                // Navy formula for females
                bodyFatPercentage = 163.205 * Math.log10(waist + neck) - 97.684 * Math.log10(height) - 78.387;
            }

            resultTextView.setText(String.format("Your Body Fat Percentage: %.2f%%", bodyFatPercentage));
        }
    }
