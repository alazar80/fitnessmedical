package com.example.sql;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class second  extends BaseActivity {
    private static final String TAG = "second";
    private DrawerLayout drawerLayout;
    private int userId;
    private Handler adHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);


        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            ToastUtil.show(this, "Invalid User ID", 1/3);
            finish();
            return;
        }

        setupToolbarAndDrawer();
        setupBottomNavigation();
        setupMainButtons();
        loadUserHeaderData(userId);
        if (userId != -1) {
//            initializeAdCheck(userId); // 👈 this activates popup check
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        View dietMedicalBtn = findViewById(R.id.dietmedical);

// Track both API results
        final boolean[] hasDoctor = {false};
        final boolean[] hasMedicalDetail = {false};
        final boolean[] doctorLoaded = {false};
        final boolean[] medicalLoaded = {false};

        Runnable updateButton = () -> {
            Log.d("BUTTON_DEBUG", "hasDoctor=" + hasDoctor[0] + ", hasMedicalDetail=" + hasMedicalDetail[0]);
            if (doctorLoaded[0] && medicalLoaded[0]) {
                if (hasDoctor[0] && !hasMedicalDetail[0]) {
                    dietMedicalBtn.setVisibility(View.VISIBLE);
                    dietMedicalBtn.setOnClickListener(v -> startActivityWithUser(DietMedicalActivity.class));
                } else {
                    dietMedicalBtn.setVisibility(View.GONE);
                }
            }
        };





// Doctor check
        String doctorUrl = ApiConfig.HAS_DOCTOR_ID;
        Map<String, String> doctorParams = new HashMap<>();
        doctorParams.put("user_id", String.valueOf(userId));

        JsonObjectRequest doctorRequest = new JsonObjectRequest(Request.Method.POST, doctorUrl, new JSONObject(doctorParams),
                response -> {
                    hasDoctor[0] = response.optBoolean("hasDoctor", false);
                    doctorLoaded[0] = true;
                    updateButton.run();
                },
                error -> {
                    doctorLoaded[0] = true; // treat as no doctor
                    updateButton.run();
                }
        );
        queue.add(doctorRequest);

// Medical detail check
        String medicalDetailUrl = ApiConfig.HAS_MEDICAL_DETAIL;
        Map<String, String> medicalParams = new HashMap<>();
        medicalParams.put("user_id", String.valueOf(userId));

        JsonObjectRequest medicalRequest = new JsonObjectRequest(Request.Method.POST, medicalDetailUrl, new JSONObject(medicalParams),
                response -> {
                    hasMedicalDetail[0] = response.optBoolean("hasMedicalDetail", false);
                    medicalLoaded[0] = true;
                    updateButton.run();
                },
                error -> {
                    medicalLoaded[0] = true; // treat as no detail
                    updateButton.run();
                }
        );
        queue.add(medicalRequest);


    }

    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            boolean handled = handleMenuAction(item.getItemId());
            drawerLayout.closeDrawers();
            return handled;
        });
    }



    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem prog = bottomNavigationView.getMenu().findItem(R.id.nav_progress);
        prog.setIcon(R.drawable.progress);        // your user‐specific drawable

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            }  else if (id == R.id.nav_settings) {
                startActivityWithUser(SettingsActivity.class);
                return true;
            }
            else if (id == R.id.nav_shop) {
                startActivityWithUser(Shop.class);
                return true;
            }
            else if (id == R.id.nav_progress) {
                startActivityWithUser(ProgressChartActivity.class);
                return true;
            }else {
                Log.w(TAG, "Unhandled bottom nav item: " + id);
                return false;
            }
        });
    }

    private void setupMainButtons() {
        findViewById(R.id.fitnessfirst).setOnClickListener(v -> startActivityWithUser(choose.class));
        findViewById(R.id.recommendedfirst).setOnClickListener(v -> startActivityWithUser(DietDay.class));
        findViewById(R.id.medicationalarmfirst).setOnClickListener(v -> startActivityWithUser(medicationalarmactivity.class));
        findViewById(R.id.doctorcontactfirst).setOnClickListener(v -> startActivityWithUser(choosetrainerforsms.class));
        findViewById(R.id.choosetrainer).setOnClickListener(v -> startActivityWithUser(choosetrainer.class));
        findViewById(R.id.detectfirst).setOnClickListener(v -> startActivityWithUser(detect.class));
    }

    private void startActivityWithUser(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    private boolean handleMenuAction(int id) {
        if (id == R.id.nav_my_profile) {
            startActivityWithUser(UserProfileActivity.class);
            return true;
        } else if (id == R.id.nav_other_apps) {
            launchUrl("https://play.google.com/store?hl=en");
            return true;
        } else if (id == R.id.nav_feedback) {
            startActivityWithUser(Enterfeedback.class);
            return true;
        } else if (id == R.id.nav_share) {
            shareAppApk();
            return true;
        } else if (id == R.id.nav_rate) {
            launchUrl("https://play.google.com/store?hl=en");
            return true;
        } else {
            Log.w(TAG, "Unhandled menu item: " + id);
            return false;
        }
    }

    private void launchUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(Intent.createChooser(intent, "Open with"));
    }

    private void loadUserHeaderData(int userId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ApiConfig.FETCH_PROFILE_URL;

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(userId));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                response -> {
                    String name = response.optString("name", "User");
                    String email = response.optString("email", "user@email.com");
                    String base64Image = response.optString("profile_image", "");
                    updateDrawerHeader(name, email, base64Image);
                },
                error -> Log.e(TAG, "Error loading user profile", error));

        queue.add(request);
    }

    private void updateDrawerHeader(String name, String email, String base64Image) {
        NavigationView navView = findViewById(R.id.navigation_view);
        View headerView = navView.getHeaderView(0);

        TextView nameView = headerView.findViewById(R.id.nav_header_title);
        TextView emailView = headerView.findViewById(R.id.nav_header_email);
        ImageView imageView = headerView.findViewById(R.id.nav_header_imageView);

        nameView.setText(name);
        emailView.setText(email);

        if (!base64Image.isEmpty()) {
            try {
                byte[] decoded = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.ic_default_profile);
            }
        } else {
            imageView.setImageResource(R.drawable.ic_default_profile);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return handleMenuAction(item.getItemId()) || super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        SettingsActivity.LANGUAGE_CHANGED = false; // Reset flag
    }



    private void shareAppApk() {
        try {
            String appPath = getPackageManager()
                    .getApplicationInfo(getPackageName(), 0).sourceDir;

            Uri apkUri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    new java.io.File(appPath)
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/vnd.android.package-archive");
            intent.putExtra(Intent.EXTRA_STREAM, apkUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Share App via"));
        } catch (Exception e) {
            ToastUtil.show(this, "Unable to share app", 1/3);
            Log.e(TAG, "Error sharing app", e);
        }
    }


}
