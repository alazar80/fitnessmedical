package com.example.sql;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sql.databinding.ActivitySaveContactsBinding;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SaveContactsActivity extends AppCompatActivity {
    private static final String OPEN_WEATHER_API_KEY = "38d1a52c59c92bd7f82a08720407e641";
    private static final String CURRENCY_API_KEY     = "73c4c90ecb987201155a4189";

    private ActivitySaveContactsBinding binding;
    private RequestQueue queue;
    private List<String> cityList = new ArrayList<>();
    private String contactsData;

    private ActivityResultLauncher<String> requestContactsLauncher;
    private ActivityResultLauncher<String> createDocLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySaveContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);
        initActivityResultLaunchers();
        loadCitiesFromCsv();
        setupListeners();
        initCurrencyConverter();
    }

    private void initActivityResultLaunchers() {
        requestContactsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) gatherContactsAndSave();
                    else showToast("Permission denied to read contacts");
                }
        );
        createDocLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("text/plain"),
                uri -> {
                    if (uri != null) writeContactsToUri(uri);
                    else showToast("Failed to create document");
                }
        );
    }

    private void setupListeners() {
        // Save Contacts
        binding.btnSaveContacts.setOnClickListener(v -> {
            boolean ok = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED;
            if (ok) gatherContactsAndSave();
            else requestContactsLauncher.launch(Manifest.permission.READ_CONTACTS);
        });

        // Fetch Weather
        binding.btnFetchWeather.setOnClickListener(v -> {
            String city = binding.autoCompleteCity.getText().toString().trim();
            if (TextUtils.isEmpty(city)) {
                showToast("Please select or type a city");
            } else {
                binding.tvSelectedCity.setText(city);
                fetchWeather(city);
            }
        });
    }

    // ────────────────────────────────────────────────
    // IMPROVED WEATHER FETCH
    // ────────────────────────────────────────────────

    private void fetchWeather(String city) {
        binding.tvWeather.setText("Loading…");
        try {
            String url = "https://api.openweathermap.org/data/2.5/weather"
                    + "?q="    + URLEncoder.encode(city, "UTF-8")
                    + "&units=metric"
                    + "&appid=" + OPEN_WEATHER_API_KEY;

            queue.add(new StringRequest(
                    Request.Method.GET, url,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONObject main = json.getJSONObject("main");
                            double temp    = main.getDouble("temp");
                            double feels   = main.optDouble("feels_like", temp);
                            double tmin    = main.optDouble("temp_min", temp);
                            double tmax    = main.optDouble("temp_max", temp);
                            int humidity   = main.optInt("humidity", 0);

                            JSONObject windObj = json.optJSONObject("wind");
                            double wind      = windObj != null
                                    ? windObj.optDouble("speed", 0)
                                    : 0;

                            JSONArray wArr   = json.optJSONArray("weather");
                            String descr     = "";
                            if (wArr != null && wArr.length() > 0) {
                                descr = wArr.getJSONObject(0).optString("description", "");
                            }

                            String result = String.format(Locale.getDefault(),
                                    "%s\n%s\nTemp: %.1f°C (feels like %.1f°C)\nMin/Max: %.1f°C/%.1f°C\nHumidity: %d%%\nWind: %.1f m/s",
                                    capitalize(city),
                                    capitalize(descr),
                                    temp, feels,
                                    tmin, tmax,
                                    humidity,
                                    wind
                            );
                            binding.tvWeather.setText(result);

                        } catch (Exception ex) {
                            binding.tvWeather.setText("Parse error");
                        }
                    },
                    error -> binding.tvWeather.setText("Request failed")
            ));
        } catch (Exception ex) {
            binding.tvWeather.setText("Invalid city name");
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }

    // ────────────────────────────────────────────────
    // REST OF YOUR ORIGINAL CODE (CONTACTS + CURRENCY + CSV)
    // ────────────────────────────────────────────────

    private void gatherContactsAndSave() {
        StringBuilder sb = new StringBuilder();
        try (Cursor c = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )) {
            if (c != null) {
                int idxName   = c.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int idxNumber = c.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.NUMBER);
                while (c.moveToNext()) {
                    sb.append(c.getString(idxName))
                            .append(": ")
                            .append(c.getString(idxNumber))
                            .append("\n");
                }
            }
        }
        contactsData = sb.toString();
        createDocLauncher.launch("contacts.txt");
    }

    private void writeContactsToUri(Uri uri) {
        try (OutputStream os = getContentResolver().openOutputStream(uri)) {
            os.write(contactsData.getBytes());
            showToast("Contacts saved to: " + uri.getLastPathSegment());
        } catch (Exception e) {
            showToast("Save failed: " + e.getMessage());
        }
    }

    private void loadCitiesFromCsv() {
        new Thread(() -> {
            try (InputStream is = getAssets().open("worldcities.csv");
                 BufferedReader br = new BufferedReader(
                         new InputStreamReader(is, "UTF-8"))
            ) {
                br.readLine(); // skip header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] cols = line.split(
                            ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"
                    );
                    cityList.add(cols[0].replaceAll("^\"|\"$", ""));
                }
                runOnUiThread(() -> {
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            cityList
                    );
                    binding.autoCompleteCity.setAdapter(cityAdapter);
                    binding.autoCompleteCity.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    binding.autoCompleteCity.setOnItemClickListener((p, v, pos, id) -> {
                        String sel = cityList.get(pos);
                        binding.tvSelectedCity.setText(sel);
                    });
                });
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> showToast("Failed loading cities"));
            }
        }).start();
    }

    private void initCurrencyConverter() {
        Set<Currency> currencySet = Currency.getAvailableCurrencies();
        List<String> list = new ArrayList<>();
        for (Currency c : currencySet) list.add(c.getCurrencyCode());
        Collections.sort(list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                list
        );
        binding.autoFrom.setAdapter(adapter);
        binding.autoTo .setAdapter(adapter);
        binding.autoFrom.setText("USD", false);
        binding.autoTo .setText("EUR", false);

        binding.btnConvertCurrency.setOnClickListener(v -> {
            String from = binding.autoFrom.getText().toString().trim();
            String to   = binding.autoTo.getText().toString().trim();
            String amt  = binding.etAmount.getText().toString().trim();
            if (from.isEmpty() || to.isEmpty() || amt.isEmpty()) {
                showToast("Fill amount and both currencies");
            } else {
                convertCurrency(from, to, amt);
            }
        });
    }

    private void convertCurrency(String from, String to, String amount) {
        try {
            String url = "https://v6.exchangerate-api.com/v6/"
                    + CURRENCY_API_KEY
                    + "/pair/"
                    + URLEncoder.encode(from,   "UTF-8")
                    + "/"
                    + URLEncoder.encode(to,     "UTF-8")
                    + "/"
                    + URLEncoder.encode(amount, "UTF-8");

            queue.add(new StringRequest(
                    Request.Method.GET, url,
                    resp -> {
                        try {
                            JSONObject obj = new JSONObject(resp);
                            if ("success".equals(obj.getString("result"))) {
                                double conv = obj.getDouble("conversion_result");
                                binding.tvConversionResult.setText(
                                        String.format("%s %s = %.2f %s",
                                                amount, from, conv, to)
                                );
                            } else {
                                binding.tvConversionResult.setText("Conversion error");
                            }
                        } catch (Exception ex) {
                            binding.tvConversionResult.setText("Parse error");
                        }
                    },
                    err -> binding.tvConversionResult.setText("Request failed")
            ));
        } catch (Exception ex) {
            binding.tvConversionResult.setText("Invalid input");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // no restart
    }
}
