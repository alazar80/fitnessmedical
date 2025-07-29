package com.example.sql;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminAddMealFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 100;

    private ImageView mealImageView;
    private Button uploadImageButton, addMealButton;
    private EditText editTitle, editCalories, editDescription;
    private Spinner mealCategorySpinner, goalTypeSpinner;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_add_meal, container, false);

        initViews(view);
        setupSpinners();
        setListeners();

        return view;
    }

    private void initViews(View view) {
        mealImageView = view.findViewById(R.id.MealImageView);
        uploadImageButton = view.findViewById(R.id.uploadImageButton);
        addMealButton = view.findViewById(R.id.addMealButton);
        editTitle = view.findViewById(R.id.editMealTitle);
        editCalories = view.findViewById(R.id.editCalories);
        editDescription = view.findViewById(R.id.editDescription);
        goalTypeSpinner = view.findViewById(R.id.spinner_goal);
        mealCategorySpinner = view.findViewById(R.id.viewTypeSpinner);
    }

    private void setupSpinners() {
        setSpinner(mealCategorySpinner, R.array.meal_types);
        setSpinner(goalTypeSpinner, R.array.goal_types);
    }

    private void setSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setListeners() {
        uploadImageButton.setOnClickListener(v -> openImagePicker());

        addMealButton.setOnClickListener(v -> {
            if (validateInputs()) {
                try {
                    uploadMeal();
                } catch (IOException e) {
                    ToastUtil.show(getContext(), "Image error: " + e.getMessage(), 1/3);
                }
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            mealImageView.setImageURI(imageUri);
        }
    }

    private boolean validateInputs() {
        if (imageUri == null) {
            ToastUtil.show(getContext(), "Please select an image.", 1/3);
            return false;
        }

        if (editTitle.getText().toString().trim().isEmpty()
                || editCalories.getText().toString().trim().isEmpty()
                || editDescription.getText().toString().trim().isEmpty()) {
            ToastUtil.show(getContext(), "Please fill all text fields.", 1/3);
            return false;
        }

        return true;
    }

    private void uploadMeal() throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

        String title = editTitle.getText().toString().trim();
        String calories = editCalories.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String goal = goalTypeSpinner.getSelectedItem().toString().trim();
        String category = mealCategorySpinner.getSelectedItem().toString().trim();

        int userId = 1; // TODO: Get from shared preferences or login session

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.ADD_MEAL_URL,
                response -> {
                    Log.d("MealUpload", "Response: " + response);
                    if (response.contains("\"success\":true")) {
                        ToastUtil.show(getContext(), "Meal added successfully.", 1/3);
                    } else {
                        ToastUtil.show(getContext(), "Meal failed to save!", 1/3);
                    }
                },
                error -> {
                    Log.e("MealUpload", "Error: " + error.toString());
                    ToastUtil.show(getContext(), "Upload failed: " + error.getMessage(), 1/3);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("title", title);
                params.put("calories", calories);
                params.put("description", description);
                params.put("fitnessGoal", goal);
                params.put("category", category);
                params.put("image", encodedImage);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
