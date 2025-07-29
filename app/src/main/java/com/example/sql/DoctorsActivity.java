package com.example.sql;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DoctorsActivity  extends BaseActivity {

    private static final String TAG = "DoctorDashboardActivity";
    private Button btnManageExercises,btnManageMeals;
    int doctorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctors2);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
//        bottomNav.getMenu().findItem(R.id.nav_progress).setVisible(false);

        doctorId = getIntent().getIntExtra("doctor_id", -1);


                initViews();
                setListeners();
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// Add hamburger toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

// Load name, email, and photo
        loadDoctorHeader(doctorId);
        navigationView.setNavigationItemSelectedListener(item -> {
            TextView progress =findViewById(R.id.nav_progress);
            int id = item.getItemId();

            if (id == R.id.nav_my_profile) {
                Intent intent = new Intent(this, doctorprofile.class);
                intent.putExtra("doctor_id", doctorId);
                startActivity(intent);
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(this, DoctorSettingsActivity.class);
                intent.putExtra("doctor_id", doctorId);
                startActivity(intent);
            }
            else if (id == R.id.nav_logout) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            else if (id == R.id.nav_progress) {

                startActivity(new Intent(this, DoctorNotificationsActivity.class));
                return true;
            }            else {
                Log.w("Drawer", "Unhandled item: " + id);
                return false;
            }

            drawerLayout.closeDrawers(); // Close after selection
            return true;
        });


    }

            private void initViews() {


                BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                btnManageExercises = findViewById(R.id.btnManageExercises);
                btnManageMeals = findViewById(R.id.btnManageMeals);

//                findViewById(R.id.backButton).setOnClickListener(v -> {
//                    startActivity(new Intent(this, MainActivity.class));
//                    finish();
//                });
                MenuItem prog = bottomNavigationView.getMenu().findItem(R.id.nav_progress);
                prog.setIcon(R.drawable.ic_notification);      // your doctor‐specific drawable
                prog.setTitle("Notification"); // your doctor‐specific string
                // BottomNavigationView item selection listener
                bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                    Log.d("BottomNav", "Selected Item ID: " + item.getItemId());

                    if (item.getItemId() == R.id.nav_home) {
                        Log.d("BottomNav", "Home Selected");
                        ToastUtil.show(this, "Doctor ID: " + doctorId, 1/3);
                        Intent secintent = new Intent(this, DoctorsActivity.class);
                        secintent.putExtra("doctor_id", doctorId);
                        startActivity(secintent);
                        return true;
                    }
                    else if (item.getItemId() == R.id.nav_shop) {
                        Intent profileIntent = new Intent(this, Shop.class);
                        profileIntent.putExtra("doctor_id", doctorId);
                        startActivity(profileIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;
                    }
                    else if (item.getItemId() == R.id.nav_progress) {
                        Intent profileIntent = new Intent(this, DoctorNotificationsActivity.class);
                        profileIntent.putExtra("doctor_id", doctorId);
                        startActivity(profileIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;
                    }
                   else if (item.getItemId() == R.id.nav_settings) {
                        Log.d("BottomNav", "Settings Selected");
                        Intent settingIntent = new Intent(this, DoctorSettingsActivity.class);
                        settingIntent.putExtra("doctor_id", doctorId);
                        startActivity(settingIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;
                    } else {
                        return false;
                    }
                });
            }
    private void startActivityWithDoctor(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("doctor_id", doctorId); // ✅ doctor-specific ID
        startActivity(intent);
    }

    private void showToast(String message) {
        ToastUtil.show(this, message, 1/3);
    }

    private void launchUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(Intent.createChooser(intent, "Open with"));
    }

    private boolean handleMenuAction(int id) {
        if (id == R.id.nav_my_profile) {
            startActivityWithDoctor(doctorprofile.class);  // Change to your actual doctor profile activity
            return true;

        } else if (id == R.id.nav_other_apps || id == R.id.nav_rate) {
            launchUrl("https://play.google.com/store?hl=en");
            return true;

        } else if (id == R.id.nav_feedback) {
            startActivityWithDoctor(Enterfeedback.class);
            return true;

        } else if (id == R.id.nav_share) {
            showToast("Share Selected");
            return true;
        }
        else {
            Log.w(TAG, "Unhandled menu item: " + id);
            return false;
        }
    }

            private void setListeners() {
                btnManageExercises.setOnClickListener(v ->  navigateToManageExercises());
                btnManageMeals.setOnClickListener(v ->  navigateToManageMeals());            }
//            private void deleteMeal() {
//                String mealIdStr = editMealId.getText().toString().trim();
//                if (TextUtils.isEmpty(mealIdStr)) {
//                    showToast("Please enter Meal ID");
//                    return;
//                }
//                int mealId = Integer.parseInt(mealIdStr);
//                SQLiteDatabase db = dbHelper.getWritableDatabase();
//                int rows = db.delete(DatabaseHelper.TABLE_MEALS, DatabaseHelper.COLUMN_MEAL_ID + "=?", new String[]{String.valueOf(mealId)});
//                showToast(rows > 0 ? "Meal deleted" : "Meal not found");
//                db.close();
//            }
//
//            private void deleteExercise() {
//                String exerciseIdStr = editExerciseId.getText().toString().trim();
//                if (TextUtils.isEmpty(exerciseIdStr)) {
//                    showToast("Please enter Exercise ID");
//                    return;
//                }
//
//                int exerciseId = Integer.parseInt(exerciseIdStr);
//                SQLiteDatabase db = dbHelper.getWritableDatabase();
//                int rows = db.delete(DatabaseHelper.TABLE_EXERCISES, DatabaseHelper.COLUMN_EXERCISE_ID + "=?", new String[]{String.valueOf(exerciseId)});
//                showToast(rows > 0 ? "Exercise deleted" : "Exercise not found");
//                db.close();
//            }
//
//
//            private void showToast(String msg) {
//                ToastUtil.show(this, msg, 1/3);
//            }
//
//            private void showExercises() {
//                SQLiteDatabase db = dbHelper.getReadableDatabase();
//                Cursor cursor = db.query(DatabaseHelper.TABLE_EXERCISES,
//                        new String[]{DatabaseHelper.COLUMN_EXERCISE_NAME, DatabaseHelper.COLUMN_EXERCISE_TYPE, DatabaseHelper.COLUMN_EXERCISE_FITNESS_GOAL},
//                        null, null, null, null, null);
//
//                StringBuilder builder = new StringBuilder();
//                if (cursor.moveToFirst()) {
//                    do {
//                        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXERCISE_NAME));
//                        String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXERCISE_TYPE));
//                        String goal = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXERCISE_FITNESS_GOAL));
//                        builder.append("Exercise: ").append(name)
//                                .append(", Type: ").append(type)
//                                .append(", Goal: ").append(goal)
//                                .append("\n");
//                    } while (cursor.moveToNext());
//                } else {
//                    builder.append("No exercises found.");
//                }
//
//                cursor.close();
//                db.close();
//                showToast(builder.toString());
//            }
//
//            private void showMeals() {
//                SQLiteDatabase db = dbHelper.getReadableDatabase();
//                Cursor cursor = db.query(DatabaseHelper.TABLE_MEALS,
//                        new String[]{DatabaseHelper.COLUMN_MEAL_TITLE, DatabaseHelper.COLUMN_MEAL_CATEGORY, DatabaseHelper.COLUMN_MEAL_FITNESS_GOAL},
//                        null, null, null, null, null);
//
//                StringBuilder builder = new StringBuilder();
//                if (cursor.moveToFirst()) {
//                    do {
//                        String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEAL_TITLE));
//                        String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEAL_CATEGORY));
//                        String goal = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEAL_FITNESS_GOAL));
//                        builder.append("Meal: ").append(title)
//                                .append(", Category: ").append(category)
//                                .append(", Goal: ").append(goal)
//                                .append("\n");
//                    } while (cursor.moveToNext());
//                } else {
//                    builder.append("No meals found.");
//                }
//
//                cursor.close();
//                db.close();
//                showToast(builder.toString());
//            }
private void loadDoctorHeader(int doctorId) {
    String url = ApiConfig.DOCTOR_PROFILE_URL;

    Map<String, String> params = new HashMap<>();
    params.put("doctor_id", String.valueOf(doctorId));

    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
            response -> {
                try {
                    JSONObject data = response.getJSONObject("data");  // ✅ fix here

                    String name = data.getString("first_name") + " " + data.getString("last_name");
                    String email = data.getString("email");
                    String base64Image = data.optString("profile_image", "");

                    View header = ((NavigationView) findViewById(R.id.navigation_view)).getHeaderView(0);
                    ((TextView) header.findViewById(R.id.nav_header_title)).setText(name);
                    ((TextView) header.findViewById(R.id.nav_header_email)).setText(email);

                    ImageView imageView = header.findViewById(R.id.nav_header_imageView);
                    if (!base64Image.isEmpty()) {
                        byte[] decoded = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageResource(R.drawable.ic_default_profile);
                    }
                } catch (Exception e) {
                    Log.e("Drawer", "Parse error", e);
                }
            },
            error -> Log.e("Drawer", "Failed to load doctor header", error)
    );

    Volley.newRequestQueue(this).add(request);
}




    private void navigateToManageMeals() {
        Intent intent = new Intent(this, DoctorManagemeal.class);
        intent.putExtra("doctor_id", doctorId);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        finish();
    }
    private void navigateToManageExercises() {
        Intent intent = new Intent(this, DoctorManageexercise.class);
        intent.putExtra("doctor_id", doctorId);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        finish();
    }
//    @Override
//    public void onBackPressed() {
//            Intent intent = new Intent(this, LoginActivity.class);
//            intent.putExtra("doctor_id", doctorId);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//            super.onBackPressed();
//        }
    }


