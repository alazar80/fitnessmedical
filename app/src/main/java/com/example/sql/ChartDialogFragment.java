package com.example.sql;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChartDialogFragment extends DialogFragment {
    private static final String ARG_USER_ID = "user_id";
    private int userId;
    private RequestQueue queue;

    public static ChartDialogFragment newInstance(int userId) {
        ChartDialogFragment f = new ChartDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        userId = getArguments().getInt(ARG_USER_ID);
        queue  = Volley.newRequestQueue(requireContext());

        AlertDialog.Builder b = new AlertDialog.Builder(requireContext());
        View v = requireActivity().getLayoutInflater()
                .inflate(R.layout.dialog_progress_charts, null);
        BarChart bar = v.findViewById(R.id.dialogBarChart);
        PieChart pie = v.findViewById(R.id.dialogPieChart);

        loadWeightHistory(bar);
        loadExerciseDist(pie);

        b.setView(v)
                .setTitle("Quick Progress")
                .setPositiveButton("OK", null);

        return b.create();
    }

    private void loadWeightHistory(BarChart chart) {
        String url = ApiConfig.BASE_URL + "get_weight_chart.php?user_id=" + userId;
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<BarEntry> entries = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject o = response.optJSONObject(i);
                        entries.add(new BarEntry(i, (float)o.optDouble("weight")));
                    }
                    BarDataSet ds = new BarDataSet(entries, "Weight");
                    chart.setData(new BarData(ds));
                    chart.getDescription().setEnabled(false);
                    chart.setFitBars(true);
                    chart.invalidate();
                },
                error -> {/* handle error */}
        );
        queue.add(req);
    }

    private void loadExerciseDist(PieChart chart) {
        String url = ApiConfig.BASE_URL + "get_exercise_dist.php?user_id=" + userId;
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<PieEntry> entries = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject o = response.optJSONObject(i);
                        entries.add(new PieEntry(o.optInt("calories"), o.optString("type")));
                    }
                    PieDataSet ds = new PieDataSet(entries, "Exercises");
                    chart.setData(new PieData(ds));
                    chart.getDescription().setEnabled(false);
                    chart.invalidate();
                },
                error -> {/* handle error */}
        );
        queue.add(req);
    }
}

