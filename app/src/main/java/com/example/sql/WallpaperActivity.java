package com.example.sql;

import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.content.ContentValues;
import android.provider.MediaStore;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import android.os.Environment;
import android.content.ContentValues;
import android.provider.MediaStore;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import com.example.sql.BingApi;
import com.example.sql.BingResponse;
import com.example.sql.BingImage;

public class WallpaperActivity extends AppCompatActivity {

        ImageView imageView;
        TextView titleText, detailText;
        Button saveBtn, homeBtn, lockBtn, bothBtn,prewallBtn;
        Bitmap bitmap;
        String fullImageUrl;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_wallpaper);
            imageView = findViewById(R.id.imageView);
            titleText = findViewById(R.id.titleText);
            detailText = findViewById(R.id.detailText);
            saveBtn = findViewById(R.id.saveBtn);
            homeBtn = findViewById(R.id.homeBtn);
            lockBtn = findViewById(R.id.lockBtn);
            bothBtn = findViewById(R.id.bothBtn);
            prewallBtn = findViewById(R.id.prewallBtn);
            // 1️⃣ Check for a history-selected URL
            String histUrl = getIntent().getStringExtra("image_url");
            if (histUrl != null) {
                fullImageUrl = histUrl;
                titleText.setText("From History");
                detailText.setText("");

                Glide.with(this)
                        .asBitmap()
                        .load(fullImageUrl)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                bitmap = resource;
                                imageView.setImageBitmap(bitmap);
                            }
                        });

            } else {
                // 2️⃣ No history extra → do your Retrofit fetch as before
                loadTodayWallpaper();
            }

            imageView = findViewById(R.id.imageView);
            titleText = findViewById(R.id.titleText);
            detailText = findViewById(R.id.detailText);
            saveBtn = findViewById(R.id.saveBtn);
            homeBtn = findViewById(R.id.homeBtn);
            lockBtn = findViewById(R.id.lockBtn);
            bothBtn = findViewById(R.id.bothBtn);
            prewallBtn = findViewById(R.id.prewallBtn);

//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl("https://www.bing.com/")
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            BingApi api = retrofit.create(BingApi.class);
//            api.getImage().enqueue(new Callback<BingResponse>() {
//                @Override
//                public void onResponse(Call<BingResponse> call, Response<BingResponse> response) {
//                    if (response.isSuccessful()) {
//                        BingImage image = response.body().images.get(0);
//                        fullImageUrl = "https://www.bing.com" + image.url;
//
//                        titleText.setText(image.title != null ? image.title : "Bing Wallpaper");
//                        detailText.setText(image.copyright);
//
//                        Glide.with(WallpaperActivity.this)
//                                .asBitmap()
//                                .load(fullImageUrl)
//                                .into(new SimpleTarget<Bitmap>() {
//                                    @Override
//                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                                        bitmap = resource;
//                                        imageView.setImageBitmap(bitmap);
//                                    }
//                                });
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<BingResponse> call, Throwable t) {
//                    Toast.makeText(WallpaperActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
//                }
//            });

            saveBtn.setOnClickListener(v -> saveImage());
            homeBtn.setOnClickListener(v -> setWallpaper(WallpaperManager.FLAG_SYSTEM));
            lockBtn.setOnClickListener(v -> setWallpaper(WallpaperManager.FLAG_LOCK));
            bothBtn.setOnClickListener(v -> setBothWallpaper());

            prewallBtn.setOnClickListener(v ->   startActivity(new Intent(this, HistoryActivity.class)));
        }

    void setWallpaper(int flag) {
        try {
            WallpaperManager wm = WallpaperManager.getInstance(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // API 24+: you can choose home vs lock screen
                wm.setBitmap(bitmap, null, true, flag);
            } else {
                if (flag == WallpaperManager.FLAG_SYSTEM) {
                    // older devices can only set the home screen
                    wm.setBitmap(bitmap);
                } else {
                    Toast.makeText(this,
                            "Lock-screen wallpaper requires Android N or higher",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(this, "Wallpaper Set!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting wallpaper", Toast.LENGTH_SHORT).show();
        }
    }


    void setBothWallpaper() {
        try {
            WallpaperManager wm = WallpaperManager.getInstance(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // API 24+: set home AND lock in one call
                wm.setBitmap(bitmap, null, true,
                        WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);
            } else {
                // older devices: only home screen is supported
                wm.setBitmap(bitmap);
                Toast.makeText(this,
                        "Home screen set; lock-screen requires Android N or higher",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Both Wallpapers Set!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting wallpapers", Toast.LENGTH_SHORT).show();
        }
    }


        void saveImage() {
            String fileName = "bing_" + System.currentTimeMillis() + ".jpg";
            OutputStream fos;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    fos = getContentResolver().openOutputStream(uri);
                } else {
                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "BingWallpapers");
                    if (!dir.exists()) dir.mkdirs();
                    File file = new File(dir, fileName);
                    fos = new FileOutputStream(file);
                }

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    // extract your Retrofit code into its own method
    private void loadTodayWallpaper() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.bing.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BingApi api = retrofit.create(BingApi.class);
        api.getImage().enqueue(new Callback<BingResponse>() {
            @Override
            public void onResponse(Call<BingResponse> call, Response<BingResponse> response) {
                if (response.isSuccessful()) {
                    BingImage image = response.body().images.get(0);
                    fullImageUrl = "https://www.bing.com" + image.url;
                    titleText.setText(image.title != null ? image.title : "Bing Wallpaper");
                    detailText.setText(image.copyright);

                    Glide.with(WallpaperActivity.this)
                            .asBitmap()
                            .load(fullImageUrl)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    bitmap = resource;
                                    imageView.setImageBitmap(bitmap);
                                }
                            });
                }
            }
            @Override public void onFailure(Call<BingResponse> call, Throwable t) {
                Toast.makeText(WallpaperActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }
    }