package com.example.sql;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class ZenQuotesActivity extends AppCompatActivity {
    private TextView tvQuote, tvAuthor;
    private Button btnRefresh;
    private RequestQueue queue;
    private static final String URL = "https://zenquotes.io/api/today";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zen_quotes);

        tvQuote  = findViewById(R.id.tvQuote);
        tvAuthor = findViewById(R.id.tvAuthor);
        btnRefresh = findViewById(R.id.btnRefresh);

        queue = Volley.newRequestQueue(this);

        fetchQuote();

        btnRefresh.setOnClickListener(v -> fetchQuote());
    }

    private void fetchQuote() {
        tvQuote.setText("Loading…");
        tvAuthor.setText("");

        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET,
            URL,
            null,
            response -> {
                try {
                    // API returns an array with one object
                    JSONObject obj = response.getJSONObject(0);
                    String q = obj.getString("q");
                    String a = obj.getString("a");
                    tvQuote.setText("\"" + q + "\"");
                    tvAuthor.setText("- " + a);
                } catch (Exception e) {
                    tvQuote.setText("Parsing error");
                    tvAuthor.setText("");
                }
            },
            error -> {
                tvQuote.setText("Request failed");
                tvAuthor.setText("");
            }
        );

        // Limit to 1 request at a time
        request.setTag("ZEN_QUOTES");
        queue.cancelAll("ZEN_QUOTES");
        queue.add(request);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // cancel pending requests to avoid leaks
        if (queue != null) queue.cancelAll("ZEN_QUOTES");
    }
}
