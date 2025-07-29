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

public class WorkoutPlanGeneratorActivity extends AppCompatActivity {


        private Spinner goalSpinner, experienceSpinner;
        private EditText daysEditText;
        private Button generatePlanButton;
        private TextView workoutPlanTextView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_workout_plan_generator);
            ThemeUtil.applyBackground(this, R.id.mainLayout);

            goalSpinner = findViewById(R.id.goalSpinner);
            experienceSpinner = findViewById(R.id.experienceSpinner);
            daysEditText = findViewById(R.id.daysEditText);
            generatePlanButton = findViewById(R.id.generatePlanButton);
            workoutPlanTextView = findViewById(R.id.workoutPlanTextView);

            setupSpinners();
            generatePlanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    generateWorkoutPlan();
                }
            });
        }

        private void setupSpinners() {
            ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                    R.array.fitness_goals, android.R.layout.simple_spinner_item);
            goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            goalSpinner.setAdapter(goalAdapter);

            ArrayAdapter<CharSequence> experienceAdapter = ArrayAdapter.createFromResource(this,
                    R.array.experience_levels, android.R.layout.simple_spinner_item);
            experienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            experienceSpinner.setAdapter(experienceAdapter);
        }

        private void generateWorkoutPlan() {
            String goal = goalSpinner.getSelectedItem().toString();
            String experience = experienceSpinner.getSelectedItem().toString();
            String daysStr = daysEditText.getText().toString().trim();

            if (daysStr.isEmpty()) {
                ToastUtil.show(this, "Please enter the number of days you can workout.", 1/3);
                return;
            }

            int days = Integer.parseInt(daysStr);
            if (days <= 0 || days > 7) {
                ToastUtil.show(this, "Please enter a valid number of days (1-7).", 1/3);
                return;
            }

            String workoutPlan = generatePlan(goal, experience, days);
            workoutPlanTextView.setText(workoutPlan);
        }

        private String generatePlan(String goal, String experience, int days) {
            StringBuilder plan = new StringBuilder();

            plan.append(String.format("Goal: %s\nExperience: %s\nWorkout Days: %d\n\n", goal, experience, days));

            for (int i = 1; i <= days; i++) {
                plan.append(String.format("Day %d: ", i));
                if (goal.equals("Weight Loss")) {
                    plan.append("30 minutes cardio + 15 minutes strength training.\n");
                } else if (goal.equals("Muscle Gain")) {
                    plan.append("45 minutes strength training (targeting a muscle group).\n");
                } else if (goal.equals("Endurance Improvement")) {
                    plan.append("40 minutes endurance training (e.g., running, cycling).\n");
                } else {
                    plan.append("General fitness exercises (e.g., yoga, light cardio).\n");
                }
            }

            return plan.toString();
        }
    }
