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
import com.example.sql.databinding.ActivityDoctorAddExerciseBinding;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DoctorAddExerciseActivity extends AppCompatActivity {
    private ActivityDoctorAddExerciseBinding binding;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null) {
                            imageUri = result.getData().getData();
                            binding.ExerciseImageView.setImageURI(imageUri);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorAddExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);
        binding.uploadImageButton.setOnClickListener(v -> {
            Intent pick = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );
            pickImageLauncher.launch(pick);
        });
       ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
        binding.addExerciseButton.setOnClickListener(v -> submitExercise());
    }

    private void submitExercise() {
        String name            = binding.editExerciseName.getText().toString().trim();
        String duration        = binding.editDuration.getText().toString().trim();
        String description     = binding.editDescription.getText().toString().trim();
        String fitnessGoal     = binding.goalSpinner.getSelectedItem().toString();
        String experienceLevel = binding.experienceSpinner.getSelectedItem().toString();
        String type            = binding.viewTypeSpinner.getSelectedItem().toString();
        String location = binding.locationSpinner.getSelectedItem().toString();

        int doctorId           = getIntent().getIntExtra("doctor_id", -1);

        if (name.isEmpty() || duration.isEmpty() || description.isEmpty()) {
            ToastUtil.show(this,
                    "Name, duration & description are required",
                    1/3
            );
            return;
        }
        if (imageUri == null) {
            ToastUtil.show(this,
                    "Please select an image",
                    1/3
            );
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("name",            name);
        params.put("duration",        duration);
        params.put("description",     description);
        params.put("fitnessGoal",     fitnessGoal);
        params.put("experienceLevel", experienceLevel);
        params.put("type",            type);
        params.put("location", location);

        params.put("doctor_id",       String.valueOf(doctorId));

        byte[] imageBytes = uriToBytes(imageUri);
        if (imageBytes.length == 0) {
            ToastUtil.show(this,
                    "Failed to read image",
                    1/3
            );
            return;
        }
//System.currentTimeMillis() +
        Map<String, VolleyMultipartRequest.DataPart> byteData = new HashMap<>();
        String filename = "ic_"+name +".gif";
        byteData.put("image", new VolleyMultipartRequest.DataPart(
                filename, imageBytes, "image/jpeg"
        ));

        VolleyMultipartRequest vmr = new VolleyMultipartRequest(
                Request.Method.POST,
                ApiConfig.DOCTOR_ADD_EXERCISE_URL,
                response -> {
                    ToastUtil.show(this,
                            "Exercise added",
                            1/3
                    );
                    finish();
                },
                error -> {
                    if (error.networkResponse != null
                            && error.networkResponse.data != null) {
                        String body = new String(error.networkResponse.data);
                        ToastUtil.show(this,
                                "Server error "
                                        + error.networkResponse.statusCode
                                        + ": " + body,
                                1/3
                        );
                    } else {
                        ToastUtil.show(this,
                                "Network error: " + error.toString(),
                                1/3
                        );
                    }
                }
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
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) > 0) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
