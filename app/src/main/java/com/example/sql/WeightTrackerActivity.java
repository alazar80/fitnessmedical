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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WeightTrackerActivity extends AppCompatActivity {



        private EditText weightInputEditText;
        private Button addWeightButton;
        private ListView weightListView;

        private ArrayList<String> weightEntries;
        private ArrayAdapter<String> adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_weight_tracker);
            ThemeUtil.applyBackground(this, R.id.mainLayout);
            ThemeUtil.applyThemeFromPrefs(this);
            weightInputEditText = findViewById(R.id.weightInputEditText);
            addWeightButton = findViewById(R.id.addWeightButton);
            weightListView = findViewById(R.id.weightListView);

            weightEntries = new ArrayList<>();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, weightEntries);
            weightListView.setAdapter(adapter);
            ImageView backButton=findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> {   onBackPressed();});
            addWeightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addWeightEntry();
                }
            });
        }

        private void addWeightEntry() {
            String weightStr = weightInputEditText.getText().toString().trim();
            if (weightStr.isEmpty()) {
                ToastUtil.show(this, "Please enter a valid weight.", 1/3);
                return;
            }

            double weight = Double.parseDouble(weightStr);
            if (weight <= 0) {
                ToastUtil.show(this, "Weight must be greater than 0.", 1/3);
                return;
            }

            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String entry = String.format("Date: %s, Weight: %.1f", currentDate, weight);
            weightEntries.add(entry);

            adapter.notifyDataSetChanged();
            weightInputEditText.setText("");
            ToastUtil.show(this, "Weight entry added!", 1/3);
        }
    }
