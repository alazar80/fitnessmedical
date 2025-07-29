package com.example.sql;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import java.util.HashMap;
import java.util.Map;
// At the top
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    EditText editDateTime, editReason;
    Button btnBook;
    String userId;
    String bookUrl = ApiConfig.BASE_URL + "book_appointment.php"; // New combined PHP script

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        editDateTime = findViewById(R.id.editDateTime);
        editReason = findViewById(R.id.editReason);
        btnBook = findViewById(R.id.btnBook);
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {   onBackPressed();});
        userId = String.valueOf(getIntent().getIntExtra("user_id", -1));
        editDateTime.setOnClickListener(v -> showDateTimePicker());

        btnBook.setOnClickListener(view -> {

            String dateTime = editDateTime.getText().toString().trim();
            String reason = editReason.getText().toString().trim();

            // Call the single PHP script that handles both fetching doctor ID and booking the appointment
            bookAppointment(userId, dateTime, reason);
            onBackPressed();
        });
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                String formattedDateTime = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d:00",
                        year, month + 1, dayOfMonth, hourOfDay, minute);
                editDateTime.setText(formattedDateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void bookAppointment(String userId, String dateTime, String reason) {
        // Send the user ID, appointment date, and reason to the single PHP endpoint
        StringRequest request = new StringRequest(Request.Method.POST, bookUrl,
                response -> {
                    try {
                        // Handle the response (success or error)
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("error")) {
                            ToastUtil.show(this, jsonResponse.getString("error"), Toast.LENGTH_SHORT);
                        } else if (jsonResponse.has("success")) {
                            // Successfully booked the appointment
                            ToastUtil.show(this, "Appointment booked successfully", Toast.LENGTH_SHORT);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.show(this, "Error processing the response", Toast.LENGTH_SHORT);
                    }
                },
                error -> ToastUtil.show(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("appointment_date", dateTime);
                params.put("reason", reason);
                return params;
            }
        };

        // Send the request using Volley
        Volley.newRequestQueue(this).add(request);
    }
}
