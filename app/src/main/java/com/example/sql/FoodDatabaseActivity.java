package com.example.sql;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class FoodDatabaseActivity extends AppCompatActivity {



        private EditText foodSearchEditText;
        private Button searchButton;
        private TextView searchResultsTextView ;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_food_database);
            ThemeUtil.applyBackground(this, R.id.mainLayout);
            ThemeUtil.applyThemeFromPrefs(this);

            foodSearchEditText = findViewById(R.id.foodSearchEditText);
            searchButton = findViewById(R.id.searchButton);
            searchResultsTextView = findViewById(R.id.searchResultsTextView);
            ImageView backButton=findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> {   onBackPressed();});

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchFoodDatabase();
                }
            });

//            scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startBarcodeScanner();
//                }
//            });
        }

//        private void searchFoodDatabase() {
//            String query = foodSearchEditText.getText().toString().trim();
//            if (query.isEmpty()) {
//                ToastUtil.show(this, "Please enter a food name to search.", 1/3);
//                return;
//            }
//
//            // Placeholder for API integration
//            String mockResult = "Mock result for: " + query + "\nCalories: 200kcal, Protein: 10g, Fat: 5g, Carbs: 30g";
//            searchResultsTextView.setText(mockResult);
//        }
private void searchFoodDatabase() {
    String query = foodSearchEditText.getText().toString().trim();
    if (query.isEmpty()) {
        ToastUtil.show(this, "Please enter a food name to search.", 1/3);
        return;
    }

    new Thread(() -> {
        try {
            String urlString = ApiConfig.BASE_URL+"search_meals_for_food_database.php?query=" + URLEncoder.encode(query, "UTF-8");
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            JSONArray meals = new JSONArray(responseBuilder.toString());
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < meals.length(); i++) {
                JSONObject meal = meals.getJSONObject(i);
                result.append("Title: ").append(meal.getString("title")).append("\n");
                result.append("Calories: ").append(meal.getInt("calories")).append(" kcal\n");
                result.append("Description: ").append(meal.getString("description")).append("\n\n");
            }

            runOnUiThread(() -> searchResultsTextView.setText(result.toString()));

        } catch (Exception e) {
            runOnUiThread(() -> ToastUtil.show(FoodDatabaseActivity.this, "Error: " + e.getMessage(), 1/3));
        }
    }).start();
}

//        private void startBarcodeScanner() {
//            IntentIntegrator integrator = new IntentIntegrator(this);
//            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//            integrator.setPrompt("Scan a barcode to fetch food details.");
//            integrator.setCameraId(0); // Use a specific camera of the device
//            integrator.setBeepEnabled(true);
//            integrator.setBarcodeImageEnabled(true);
//            integrator.initiateScan();
//        }

//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//            if (result != null) {
//                if (result.getContents() != null) {
//                    // Placeholder for barcode API integration
//                    String mockBarcodeResult = "Mock result for barcode: " + result.getContents() +
//                            "\nCalories: 150kcal, Protein: 8g, Fat: 3g, Carbs: 20g";
//                    barcodeResultTextView.setText(mockBarcodeResult);
//                } else {
//                    ToastUtil.show(this, "No barcode detected.", 1/3);
//                }
//            } else {
//                super.onActivityResult(requestCode, resultCode, data);
//            }
//        }





//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            if (result.getContents() != null) {
//                searchMealByBarcode(result.getContents());
//            } else {
//                ToastUtil.show(this, "No barcode detected.", 1/3);
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//    private void searchMealByBarcode(String barcodeValue) {
//        new Thread(() -> {
//            try {
//                String urlStr = ApiConfig.BASE_URL + "search_meals_for_food_database.php?query=" + URLEncoder.encode(barcodeValue, "UTF-8");
//                URL url = new URL(urlStr);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("GET");
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                StringBuilder responseBuilder = new StringBuilder();
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    responseBuilder.append(line);
//                }
//                reader.close();
//
//                JSONArray meals = new JSONArray(responseBuilder.toString());
//                StringBuilder result = new StringBuilder();
//
//                for (int i = 0; i < meals.length(); i++) {
//                    JSONObject meal = meals.getJSONObject(i);
//                    result.append("Title: ").append(meal.getString("title")).append("\n");
//                    result.append("Calories: ").append(meal.getInt("calories")).append(" kcal\n");
//                    result.append("Description: ").append(meal.getString("description")).append("\n\n");
//                }
//
//                runOnUiThread(() -> barcodeResultTextView.setText(result.toString()));
//
//            } catch (Exception e) {
//                runOnUiThread(() -> ToastUtil.show(FoodDatabaseActivity.this, "Error: " + e.getMessage(), 1/3));
//            }
//        }).start();
//    }

}
