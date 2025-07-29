package com.example.sql;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.sql.databinding.ActivityDoctorAddMealBinding;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DoctorAddMealActivity extends AppCompatActivity {
    private ActivityDoctorAddMealBinding binding;
    private Uri imageUri;
int doctorId;
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    binding.MealImageView.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorAddMealBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        doctorId = getIntent().getIntExtra("doctor_id", -1);
        binding.uploadMealImageButton.setOnClickListener(v -> {
            Intent pick = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );
            pickImageLauncher.launch(pick);
        });
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
        binding.addMealButton.setOnClickListener(v -> submitMeal());
    }

    private void submitMeal() {
        String title = binding.editMealTitle.getText().toString().trim();
        String calories = binding.editCalories.getText().toString().trim();
        String desc = binding.editMealDescription.getText().toString().trim();
        if (title.isEmpty() || calories.isEmpty() || desc.isEmpty()) {
            ToastUtil.show(this, "All fields are required", 1/3);
            return;
        }
        if (imageUri == null) {
            ToastUtil.show(this, "Please select an image", 1/3);
            return;
        }

        Map<String,String> params = new HashMap<>();
        params.put("title", title);
        params.put("calories", calories);
        params.put("description", desc);
        params.put("category", binding.spinnerCategory.getSelectedItem().toString());
        params.put("fitnessGoal", binding.spinnerFitnessGoal.getSelectedItem().toString());
        params.put("mealtype", binding.spinnerMealType.getSelectedItem().toString());
        params.put("doctor_id", String.valueOf(getIntent().getIntExtra("doctor_id", -1)));
//System.currentTimeMillis() +
        byte[] imageBytes = uriToBytes(imageUri);
        final Map<String, VolleyMultipartRequest.DataPart> byteData = new HashMap<>();
        String filename = "ic_" + title + ".jpg";
        byteData.put("image", new VolleyMultipartRequest.DataPart(
                filename, imageBytes, "image/jpeg"
        ));

        VolleyMultipartRequest vmr = new VolleyMultipartRequest(
                Request.Method.POST,
                ApiConfig.DOCTOR_ADD_MEAL,
                response -> {
                    ToastUtil.show(this, "Meal added", 1/3);
                    finish();
                },
                error -> ToastUtil.show(this, "Error: " + error.getMessage(), 1/3)
        );
        vmr.setParams(params);
        vmr.setByteData(byteData);
        Volley.newRequestQueue(this).add(vmr);
    }

    private byte[] uriToBytes(Uri uri) {
        try (
                InputStream is = getContentResolver().openInputStream(uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) > 0) baos.write(buf, 0, n);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
