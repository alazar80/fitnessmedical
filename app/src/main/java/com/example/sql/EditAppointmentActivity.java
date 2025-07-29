package com.example.sql;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class EditAppointmentActivity extends AppCompatActivity {

    EditText editAppointmentId, editNewDate, editNewReason;
    Button btnUpdate;
    String url = ApiConfig.BASE_URL+"update_appointment.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointment);

        editAppointmentId = findViewById(R.id.editAppointmentId);
        editNewDate = findViewById(R.id.editNewDate);
        editNewReason = findViewById(R.id.editNewReason);
        btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(view -> {
            String id = editAppointmentId.getText().toString().trim();
            String newDate = editNewDate.getText().toString().trim();
            String reason = editNewReason.getText().toString().trim();

            if (id.isEmpty() || newDate.isEmpty() || reason.isEmpty()) {
                ToastUtil.show(this, "Fill all fields", 1/3);
                return;
            }

            updateAppointment(id, newDate, reason);
        });
    }

    private void updateAppointment(String id, String newDate, String reason) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> ToastUtil.show(this, response, 1/3),
                error -> ToastUtil.show(this, "Error: " + error.getMessage(), 1/3)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("appointment_id", id);
                map.put("appointment_date", newDate);
                map.put("reason", reason);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
