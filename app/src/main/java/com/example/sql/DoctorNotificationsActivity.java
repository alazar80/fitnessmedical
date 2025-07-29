package com.example.sql;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import java.util.HashMap;
public class DoctorNotificationsActivity extends AppCompatActivity {

    ListView listNotifications;
    ArrayList<String> notificationsList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    int doctorId ; // Replace with actual doctor ID (could be from login/session)
    String url = ApiConfig.BASE_URL+"get_doctor_notifications.php";
    ArrayList<String> notificationMessages = new ArrayList<>();
    ArrayList<Integer> notificationIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_notifications);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);

        listNotifications = findViewById(R.id.listNotifications);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationsList);
        listNotifications.setAdapter(adapter);
        listNotifications.setOnItemLongClickListener((adapterView, view, position, id) -> {
            int notificationId = notificationIds.get(position);
            deleteNotification(notificationId, position);
            return true;
        });
        doctorId= getIntent().getIntExtra("doctor_id", -1);
        fetchNotifications();
    }

    private void fetchNotifications() {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        notificationMessages.clear();
                        notificationIds.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String msg = obj.getString("message") + "\n" + obj.getString("created_at");
                            notificationMessages.add(msg);
                            notificationIds.add(obj.getInt("id"));
                        }
                        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationMessages);
                        listNotifications.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        ToastUtil.show(this, "Parse error",1/3);
                    }
                },
                error -> ToastUtil.show(this, "Error: " + error.getMessage(), 1/3)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("doctor_id", String.valueOf(doctorId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
    private void deleteNotification(int notificationId, int position) {
        String deleteUrl = ApiConfig.BASE_URL+"delete_notification.php";

        StringRequest request = new StringRequest(Request.Method.POST, deleteUrl,
                response -> {
                    ToastUtil.show(this, response, 1/3);
                    notificationMessages.remove(position);
                    notificationIds.remove(position);
                    adapter.notifyDataSetChanged();
                },
                error -> ToastUtil.show(this, "Delete error: " + error.getMessage(), 1/3)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("notification_id", String.valueOf(notificationId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}
