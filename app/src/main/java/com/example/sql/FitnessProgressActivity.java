//package com.example.sql;
//
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.Volley;
//
//import com.github.edubarr.heatmapcalendar.HeatMapCalendarView;
//import com.github.edubarr.heatmapcalendar.DayModel;
//import com.github.mikephil.charting.charts.*;
//import com.github.mikephil.charting.data.*;
//import com.lzy.widget.CircleProgress;
//
//import org.json.JSONObject;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//public class FitnessProgressActivity extends AppCompatActivity {
//    private HeatMapCalendarView heatmap;
//    private LineChart weightChart, caloriesChart;
//    private BarChart barChart;
//    private PieChart pieChart;
//    private RadarChart radarChart;
//    private RequestQueue queue;
//    private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_fitness_progress);
//
//        // 1) bind views
//        heatmap        = findViewById(R.id.heatmapCalendar);
//        weightChart    = findViewById(R.id.weightLineChart);
//        caloriesChart  = findViewById(R.id.caloriesLineChart);
//        barChart       = findViewById(R.id.barChart);
//        pieChart       = findViewById(R.id.pieChart);
//        radarChart     = findViewById(R.id.radarChart);
//
//        // 2) init Volley queue
//        queue = Volley.newRequestQueue(this);
//
//        // 3) load all six
//        loadWorkouts();
//        loadWeights();
//        loadCaloriesAndBar();
//        loadExerciseDist();
//        loadMuscleCoverage();
//    }
//
//    private void loadWorkouts() {
//        String url = ApiConfig.GET_WORKOUT_SESSIONS;
//        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
//                response -> {
//                    List<DayModel> days = new ArrayList<>();
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject o = response.optJSONObject(i);
//                        try {
//                            Date d = fmt.parse(o.getString("date"));
//                            int intensity = o.getInt("intensity");
//                            days.add(new DayModel(d, intensity));
//                        } catch (Exception ignored) {}
//                    }
//                    heatmap.setValues(days);
//                },
//                error -> { /* log or toast error */ }
//        );
//        queue.add(req);
//    }
//
//    private void loadWeights() {
//        String url = ApiConfig.GET_WEIGHT_RECORDS;
//        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
//                response -> {
//                    List<Entry> entries = new ArrayList<>();
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject o = response.optJSONObject(i);
//                        float w = (float)o.optDouble("weight");
//                        entries.add(new Entry(i, w));
//                    }
//                    weightChart.setData(new LineData(new LineDataSet(entries, "Weight")));
//                    weightChart.invalidate();
//                },
//                error -> {}
//        );
//        queue.add(req);
//    }
//
//    private void loadCaloriesAndBar() {
//        String url = ApiConfig.GET_CALORIE_RECORDS;
//        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
//                response -> {
//                    List<Entry>    lineEnt = new ArrayList<>();
//                    List<BarEntry> barEnt  = new ArrayList<>();
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject o = response.optJSONObject(i);
//                        int cal = o.optInt("calories");
//                        lineEnt.add(new Entry(i, cal));
//                        barEnt .add(new BarEntry(i, cal));
//                    }
//                    caloriesChart.setData(new LineData(new LineDataSet(lineEnt, "Calories")));
//                    barChart       .setData(new BarData(new BarDataSet(barEnt,  "Calories")));
//                    caloriesChart.invalidate();
//                    barChart.invalidate();
//                },
//                error -> {}
//        );
//        queue.add(req);
//    }
//
//    private void loadExerciseDist() {
//        String url = ApiConfig.GET_EXERCISE_DISTRIBUTION;
//        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
//                response -> {
//                    List<PieEntry> pieEnt = new ArrayList<>();
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject o = response.optJSONObject(i);
//                        pieEnt.add(new PieEntry(o.optInt("calories"), o.optString("type")));
//                    }
//                    pieChart.setData(new PieData(new PieDataSet(pieEnt, "Exercise Types")));
//                    pieChart.invalidate();
//                },
//                error -> {}
//        );
//        queue.add(req);
//    }
//
//    private void loadMuscleCoverage() {
//        String url = ApiConfig.GET_MUSCLE_COVERAGE;
//        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
//                response -> {
//                    List<RadarEntry> radEnt = new ArrayList<>();
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject o = response.optJSONObject(i);
//                        radEnt.add(new RadarEntry(o.optInt("score")));
//                    }
//                    radarChart.setData(new RadarData(new RadarDataSet(radEnt, "Coverage")));
//                    radarChart.invalidate();
//                },
//                error -> {}
//        );
//        queue.add(req);
//    }
//}
