package com.example.sql;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class DietMedicalActivity extends AppCompatActivity {
    RadioGroup dietRadioGroup;
    EditText etAllergies, etConditions, etMedication;
    Button btnSubmit;
    ImageView backButton;
int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_medical);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        dietRadioGroup = findViewById(R.id.diet_radio_group);
        etAllergies = findViewById(R.id.et_allergies);
        etConditions = findViewById(R.id.et_conditions);
        etMedication = findViewById(R.id.et_medication);
        btnSubmit = findViewById(R.id.btn_submit);
        backButton = findViewById(R.id.backButton);
        userId = getIntent().getIntExtra("user_id", -1);
        backButton.setOnClickListener(v -> {   onBackPressed();});
        btnSubmit.setOnClickListener(v -> {
            String dietPref = ((RadioButton) findViewById(dietRadioGroup.getCheckedRadioButtonId())).getText().toString();
            String allergies = etAllergies.getText().toString().trim();
            String conditions = etConditions.getText().toString().trim();
            String medication = etMedication.getText().toString().trim();

            sendToServer(dietPref, allergies, conditions, medication);

            onBackPressed();
        });
    }

    private void sendToServer(String diet, String allergies, String conditions, String medication) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiConfig.BASE_URL+"save_diet_medical.php",
                response -> ToastUtil.show(this, "Saved successfully!", 1/3),
                error -> ToastUtil.show(this, "Error: " + error.getMessage(), 1/3)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("diet_preference", diet);
                params.put("allergies", allergies);
                params.put("medical_conditions", conditions);
                params.put("medication", medication);
                params.put("user_id", String.valueOf(userId)); // Replace with actual user ID from session
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}

