package com.example.sql;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import java.util.concurrent.TimeUnit;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.Rotation;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.components.XAxis;

import org.json.JSONArray;
import org.json.JSONObject;



import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nl.dionsegijn.konfetti.xml.KonfettiView;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.models.Size;

import android.os.Build;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.model.Circle;
// Utility for Arrays.asList(...)
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

// (And your vibration imports, if not already)
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.content.Context;




public class ProgressChartActivity extends AppCompatActivity {
    private LineChart percentageChart;
    private BarChart exercisesChart, timeChart;
    private int userId;
    public PieChart progressPieChart;
    private PieChart sleepPieChart;
    private KonfettiView konfettiView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        konfettiView = findViewById(R.id.konfettiView);

        // ② check flag
        boolean popFired = getIntent().getBooleanExtra("showPartyPopper", false);
        if (popFired) {
            // confetti
            // This is the new, correct code using the Builder pattern


            Party party = new Party(
                    /* angle */           0,
                    /* spread */          360,
                    /* speed */           0f,
                    /* maxSpeed */        30f,
                    /* damping */         0.9f,
                    /* size */            Arrays.asList(new Size(12, 5f, 0f)),
                    /* colors */          Arrays.asList(0xfffce18a, 0xfffdff6a, 0xffffb48f, 0xffff7eb3),
                    /* shapes */          Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE),
                    /* timeToLive */      2000L,
                    /* fadeOutEnabled */  true,
                    /* position */        new Position.Relative(0.5, 1.0),
                    /* delay */           0,
                    /* rotation */      Rotation.Companion.enabled(),            // ← REQUIRED
                    /* emitter */         new Emitter(100, TimeUnit.MILLISECONDS).max(100)
            );
            konfettiView.start(party);





            // vibration
            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vib != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vib.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vib.vibrate(200);
                }
            }
        }

        userId = getIntent().getIntExtra("user_id", -1);
        percentageChart = findViewById(R.id.percentageChart);
        exercisesChart = findViewById(R.id.exercisesChart);
        timeChart = findViewById(R.id.timeChart);
        progressPieChart = findViewById(R.id.progressPieChart);
        sleepPieChart = findViewById(R.id.sleepPieChart);
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {   onBackPressed();});
        loadSleepData(); // <- add this


        loadChartData();
    }

    private void loadChartData() {
        String url = ApiConfig.GET_WORKOUT_HISTORY + "?user_id=" + userId;

        StringRequest request = new StringRequest(
                url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);

                        ArrayList<Entry> percentageEntries = new ArrayList<>();
                        ArrayList<BarEntry> exerciseEntries = new ArrayList<>();
                        ArrayList<BarEntry> timeEntries = new ArrayList<>();
                        ArrayList<String> dates = new ArrayList<>();

                        int lastPercent = 0;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject session = array.getJSONObject(i);
                            int sessionNum = i + 1;

                            int percentage = session.getInt("percentage");
                            int exercises = session.getInt("exercises_done");
                            int timeSec = session.getInt("total_time_sec");
                            String date = session.getString("date");

                            percentageEntries.add(new Entry(sessionNum, percentage));
                            exerciseEntries.add(new BarEntry(sessionNum, exercises));
                            timeEntries.add(new BarEntry(sessionNum, timeSec));
                            dates.add(date);

                            lastPercent = percentage;
                        }

// Pie Chart: % Done vs Left
                        ArrayList<PieEntry> pieEntries = new ArrayList<>();
                        pieEntries.add(new PieEntry(lastPercent, "Completed"));
                        pieEntries.add(new PieEntry(100 - lastPercent, "Remaining"));

                        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Workout Completion");
                        progressPieChart.setData(new PieData(pieDataSet));
                        progressPieChart.invalidate();

// X-axis date labels for all charts
                        setDateLabels(percentageChart, dates);
                        setDateLabels(exercisesChart, dates);
                        setDateLabels(timeChart, dates);

// Set datasets
                        percentageChart.setData(new LineData(new LineDataSet(percentageEntries, "Progress %")));
                        exercisesChart.setData(new BarData(new BarDataSet(exerciseEntries, "Exercises")));
                        timeChart.setData(new BarData(new BarDataSet(timeEntries, "Time (Sec)")));

// Refresh
                        percentageChart.invalidate();
                        exercisesChart.invalidate();
                        timeChart.invalidate();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(this).add(request);
    }
//    private void setDateLabels(BarLineChartBase<?> chart, List<String> labels) {
//        XAxis xAxis = chart.getXAxis();
//        xAxis.setGranularity(1f);
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                int index = (int) value - 1;
//                if (index >= 0 && index < labels.size()) {
//                    return labels.get(index).split(" ")[0]; // just the date part
//                }
//                return "";c
//            }
//        });
//    }
    private void setDateLabels(BarLineChartBase<?> chart, List<String> labels) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value - 1;
                if (index >= 0 && index < labels.size()) {
                    String rawDate = labels.get(index);
                    try {
                        // Input: "2025-06-17 18:19:49"
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        Date date = inputFormat.parse(rawDate);

                        // Output: "Jun 17"
                        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd", Locale.US);
                        return outputFormat.format(date);
                    } catch (Exception e) {
                        return "";
                    }
                }
                return "";
            }
        });
    }

    private void loadSleepData() {
        String url = ApiConfig.BASE_URL+"get_sleep_summary.php?user_id=" + userId;

        StringRequest request = new StringRequest(
                url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        float percent = (float) obj.getDouble("percentage");
                        float avg = (float) obj.getDouble("average");

                        ArrayList<PieEntry> sleepEntries = new ArrayList<>();
                        sleepEntries.add(new PieEntry(percent, "Avg Sleep"));
                        sleepEntries.add(new PieEntry(100 - percent, "Sleep Deficit"));

                        PieDataSet dataSet = new PieDataSet(sleepEntries, "Sleep Progress");
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        PieData pieData = new PieData(dataSet);

                        sleepPieChart.setData(pieData);
                        sleepPieChart.setCenterText(avg + " hrs");
                        sleepPieChart.setUsePercentValues(true);
                        sleepPieChart.invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(this).add(request);
    }

}
