package com.example.sql;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// HistoryActivity.java
public class HistoryActivity extends AppCompatActivity {
    RecyclerView rv;
    List<ImageItem> list = new ArrayList<>();

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_history);
        rv = findViewById(R.id.historyRecycler);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.setAdapter(new HistoryAdapter(list, this));
        new FetchTask().execute();
    }

    class FetchTask extends AsyncTask<Void, Void, List<ImageItem>> {
        @Override
        protected List<ImageItem> doInBackground(Void... voids) {
            try {
                URL u = new URL(
                        "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=8&mkt=en-US"
                );
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(c.getInputStream())
                );
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject root = new JSONObject(sb.toString());
                JSONArray imgs = root.getJSONArray("images");
                List<ImageItem> out = new ArrayList<>();

                for (int i = 0; i < imgs.length(); i++) {
                    JSONObject o = imgs.getJSONObject(i);
                    String path = o.getString("url");
                    String dd   = o.getString("startdate");   // e.g. "20250717"
                    String date = dd.substring(4,6) + "/" + dd.substring(6);
                    out.add(new ImageItem("https://www.bing.com" + path, date));
                }
                return out;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ImageItem> data) {
            if (data != null) {
                list.clear();
                list.addAll(data);
                rv.getAdapter().notifyDataSetChanged();
            }
        }
    }

}
