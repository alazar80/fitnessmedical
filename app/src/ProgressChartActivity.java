package com.example.sql;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProgressChartActivity extends AppCompatActivity {

    private TextInputEditText heightEditText,
            weightEditText,
            waistEditText,
            neckEditText,
            hipEditText;
    private MaterialButton executeAllButton;
    private TextView resultTextView;
    private BarChart barChart;

    private int userId;
    private float storedHeight, storedWeight,
            storedWaist, storedNeck, storedHip;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);

        // — Get userId
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId < 0) {
            ToastUtil.show(this, "User ID missing!", 1/3);
            finish();
            return;
        }

        // — Bind views (IDs must match your XML)
        heightEditText   = findViewById(R.id.heightEditText);
        weightEditText   = findViewById(R.id.weightEditText);
        waistEditText    = findViewById(R.id.waistEditText);
        neckEditText     = findViewById(R.id.neckEditText);
        hipEditText      = findViewById(R.id.hipEditText);
        resultTextView   = findViewById(R.id.resultTextView);
        barChart         = findViewById(R.id.barChart);
        executeAllButton = findViewById(R.id.executeAllButton);

        fetchUserInfo();        // prefill fields & storedXxx
        loadWeightChartData();  // draw initial chart

        // — Single “Execute All” listener
        executeAllButton.setOnClickListener(v -> {
            calculateBodyFat();
            loadWeightChartData();
        });
    }

    // —— fetch user’s saved metrics, disable waist/neck if already set
    private void fetchUserInfo() {
        String url = ApiConfig.BASE_URL + "get_user.php?user_id=" + userId;
        Volley.newRequestQueue(this).add(new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject o = new JSONObject(response);
                        storedWeight = (float)o.getDouble("weight");
                        storedHeight = (float)o.getDouble("height");
                        gender       = o.getString("gender");

                        weightEditText.setText(String.valueOf(storedWeight));
                        heightEditText.setText(String.valueOf(storedHeight));

                        if (o.has("waist_cm")) {
                            storedWaist = (float)o.getDouble("waist_cm");
                            waistEditText.setText(String.valueOf(storedWaist));
                            waistEditText.setEnabled(false);
                        }
                        if (o.has("neck_cm")) {
                            storedNeck = (float)o.getDouble("neck_cm");
                            neckEditText.setText(String.valueOf(storedNeck));
                            neckEditText.setEnabled(false);
                        }
                        if (o.has("hip_cm")) {
                            storedHip = (float)o.getDouble("hip_cm");
                            hipEditText.setText(String.valueOf(storedHip));
                        }
                    } catch (JSONException e) {
                        ToastUtil.show(this, "User data parse error", 1/3);
                    }
                },
                error -> {
                    Log.e("API_ERROR", "fetchUserInfo failed", error);
                    ToastUtil.show(this, "Failed to fetch user info", 1/3);
                }
        ));
    }

    // —— calculate, display & POST back
    private void calculateBodyFat() {
        try {
            // read or fallback to stored
            float height = parseOr(storedHeight,  heightEditText);
            float weight = parseOr(storedWeight,  weightEditText);
            float waist  = parseOr(storedWaist,   waistEditText);
            float neck   = parseOr(storedNeck,    neckEditText);
            float hip    = parseOr(storedHip,     hipEditText);

            // update stored values for next time
            storedHeight = height;
            storedWeight = weight;
            storedWaist  = waist;
            storedNeck   = neck;
            storedHip    = hip;

            // formula
            double bf;
            if ("male".equalsIgnoreCase(gender)) {
                bf = 495 / (1.0324 - 0.19077*Math.log10(waist - neck)
                        + 0.15456*Math.log10(height))
                        - 450;
            } else {
                bf = 495 / (1.29579 - 0.35004*Math.log10(waist + hip - neck)
                        + 0.22100*Math.log10(height))
                        - 450;
            }

            String pct = String.format(Locale.US, "%.2f", bf);
            resultTextView.setText("Your Body Fat %: " + pct);

            // POST update all metrics
            updateUserMetrics(userId, weight, waist, neck, hip, bf);

        } catch (NumberFormatException ex) {
            ToastUtil.show(this, "Please enter valid numbers", 1/3);
        }
    }

    // helper to parse, or return default
    private float parseOr(float def, TextInputEditText et) {
        String s = et.getText().toString().trim();
        return s.isEmpty() ? def : Float.parseFloat(s);
    }

    // —— send updated metrics to server
    private void updateUserMetrics(int uid, double w, double waist,
                                   double neck, double hip, double bf) {
        String url = ApiConfig.BASE_URL + "update_user_metrics.php";
        RequestQueue q = Volley.newRequestQueue(this);
        q.add(new StringRequest(Request.Method.POST, url,
                resp -> ToastUtil.show(this, "User data updated.", 1/3),
                err  -> ToastUtil.show(this, "Error updating data.", 1/3)
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("user_id", String.valueOf(uid));
                p.put("weight",  String.valueOf(w));
                p.put("waist_cm",String.valueOf(waist));
                p.put("neck_cm", String.valueOf(neck));
                p.put("hip_cm",  String.valueOf(hip));
                p.put("body_fat_percentage", String.valueOf(bf));
                return p;
            }
        });
    }

    // —— reload chart
    private void loadWeightChartData() {
        String url = ApiConfig.BASE_URL + "get_weight_chart.php?user_id=" + userId;
        Volley.newRequestQueue(this).add(new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        ArrayList<BarEntry> entries = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            entries.add(new BarEntry(i, (float)o.getDouble("weight")));
                        }
                        BarDataSet ds = new BarDataSet(entries, "Weight Over Time");
                        BarData bd = new BarData(ds);
                        bd.setBarWidth(0.9f);

                        barChart.setData(bd);
                        barChart.getDescription().setEnabled(false);
                        barChart.setFitBars(true);
                        barChart.invalidate();
                    } catch (JSONException e) {
                        ToastUtil.show(this, "Chart parse error", 1/3);
                    }
                },
                error -> ToastUtil.show(this, "Error loading chart", 1/3)
        ));
    }
}
