package com.example.sql;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseActivity extends AppCompatActivity {
    protected int userId;
    private Handler adHandler = new Handler();
    private Runnable adRunnable;

    protected void initializeAdCheck(int uid) {
        this.userId = uid;
        checkDoctorAndShowAd(userId);
    }

    private void checkDoctorAndShowAd(int userId) {
        String url = ApiConfig.GET_USER_DOCTOR+"?user_id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.isNull("doctor_id")) {
                        showPopupAdRepeatedly(); // Show ad loop
                    }
                },
                error -> ToastUtil.show(this, "Ad check failed", 1/3)
        );
        queue.add(request);
    }

    private void showPopupAdRepeatedly() {
        adRunnable = new Runnable() {
            @Override
            public void run() {
                showPopupAd();
                adHandler.postDelayed(this, 5 * 60 * 1000); // every 5 minutes
            }
        };
        adHandler.post(adRunnable);
    }

    private void showPopupAd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Get Personal Help!");
        builder.setMessage("You are not connected with a doctor. Connect now for better health plans.");

        builder.setPositiveButton("Find Doctor", (dialog, which) -> {
            Intent intent = new Intent(this, choosetrainer.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        builder.setNegativeButton("Later", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adHandler != null && adRunnable != null) {
            adHandler.removeCallbacks(adRunnable); // stop on destroy
        }
    }
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        float fontScale = prefs.getFloat("fontScale", 1.0f);  // default = medium
        Configuration config = new Configuration(res.getConfiguration());
        if (config.fontScale != fontScale) {
            config.fontScale = fontScale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return res;
    }

}
