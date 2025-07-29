package com.example.sql;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class SleepTrackerAcivity extends AppCompatActivity {

    private EditText sleepHoursEditText;
    private Button addSleepDataButton;
    private TextView averageSleepTextView, lastNightSleepTextView;

    public int userId; // Replace with actual user ID
   // Replace with your local IP (e.g., 192.168.x.x)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker_acivity);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        sleepHoursEditText = findViewById(R.id.sleepHoursEditText);
        addSleepDataButton = findViewById(R.id.addSleepDataButton);
        averageSleepTextView = findViewById(R.id.averageSleepTextView);
        lastNightSleepTextView = findViewById(R.id.lastNightSleepTextView);
        userId = getIntent().getIntExtra("user_id", -1);
        addSleepDataButton.setOnClickListener(v -> addSleepData());
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {   onBackPressed();});
        updateStatistics(); // Load stats initially
    }

    private void addSleepData() {
        String sleepHoursStr = sleepHoursEditText.getText().toString().trim();
        if (sleepHoursStr.isEmpty()) {
            Toast.makeText(this, "Enter sleep hours", Toast.LENGTH_SHORT).show();
            return;
        }

        double sleepHours = Double.parseDouble(sleepHoursStr);
        if (sleepHours <= 0 || sleepHours > 24) {
            Toast.makeText(this, "Sleep must be 1-24 hours", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiConfig.BASE_URL + "save_sleep.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Sleep data saved", Toast.LENGTH_SHORT).show();
                    sleepHoursEditText.setText("");
                    updateStatistics();
                },
                error -> Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("hours_slept", sleepHoursStr);
                return params;
            }
        };

        queue.add(request);
    }

    private void updateStatistics() {
        String url = ApiConfig.BASE_URL + "get_sleep.php?user_id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        if (array.length() == 0) {
                            averageSleepTextView.setText("Average Sleep: - hours");
                            lastNightSleepTextView.setText("Last Night's Sleep: - hours");
                            return;
                        }

                        double total = 0;
                        for (int i = 0; i < array.length(); i++) {
                            total += array.getDouble(i);
                        }

                        double avg = total / array.length();
                        double last = array.getDouble(array.length() - 1);

                        averageSleepTextView.setText(String.format("Average Sleep: %.1f hours", avg));
                        lastNightSleepTextView.setText(String.format("Last Night's Sleep: %.1f hours", last));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error loading stats", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}
