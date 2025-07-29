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

public class AdminAddExerciseFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 101;

    private ImageView exerciseIconView;
    private Button uploadIconButton, addExerciseButton;
    private EditText editName, editDescription, editDuration;
    private Spinner experienceSpinner, goalSpinner, typeSpinner;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_add_exercise, container, false);

        // 🟢 GET doctorId from arguments
        Bundle args = getArguments();
        if (args != null) {
            int doctorId = args.getInt("doctor_id", -1);
            ToastUtil.show(requireContext(), "Doctor ID: " + doctorId, 1/3);
        } else {
            ToastUtil.show(requireContext(), "No Doctor ID received", 1/3);
        }

        initViews(view);
        setupSpinners();
        setListeners();

        return view;
    }


    private void initViews(View view) {
        exerciseIconView = view.findViewById(R.id.ExerciseImageView);
        uploadIconButton = view.findViewById(R.id.uploadImageButton);
        addExerciseButton = view.findViewById(R.id.addExerciseButton);
        editName = view.findViewById(R.id.editExerciseName);
        editDescription = view.findViewById(R.id.editDescription);
        editDuration = view.findViewById(R.id.editDuration);
        experienceSpinner = view.findViewById(R.id.spinner_experience);
        goalSpinner = view.findViewById(R.id.spinner_goal);
        typeSpinner = view.findViewById(R.id.viewTypeSpinner);
    }

    private void setupSpinners() {
        setSpinner(experienceSpinner, R.array.experience_levels);
        setSpinner(goalSpinner, R.array.goal_types);
        setSpinner(typeSpinner, R.array.exercise_types);
    }

    private void setSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setListeners() {
        uploadIconButton.setOnClickListener(v -> openImagePicker());
        addExerciseButton.setOnClickListener(v -> {
            if (validateInputs()) {
                try {
                    uploadExercise();
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
            exerciseIconView.setImageURI(imageUri);
        }
    }

    private boolean validateInputs() {
        return imageUri != null
                && !editName.getText().toString().trim().isEmpty()
                && !editDescription.getText().toString().trim().isEmpty()
                && !editDuration.getText().toString().trim().isEmpty();
    }

    private void uploadExercise() throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String duration = editDuration.getText().toString().trim();
        String level = experienceSpinner.getSelectedItem().toString().trim();
        String goal = goalSpinner.getSelectedItem().toString().trim();
        String type = typeSpinner.getSelectedItem().toString().trim();

        int userId = 1; // TODO: Replace with actual user ID

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.ADD_EXERCISE_URL,
                response -> {
                    Log.d("ExerciseUpload", "Response: " + response);
                    ToastUtil.show(getContext(), "Exercise added.", 1/3);
                },
                error -> {
                    Log.e("ExerciseUpload", "Error: " + error.toString());
                    ToastUtil.show(getContext(), "Failed: " + error.getMessage(), 1/3);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("name", name);
                params.put("description", description);
                params.put("duration", duration);
                params.put("experienceLevel", level);
                params.put("fitnessGoal", goal);
                params.put("type", type);
                params.put("icon", encodedImage);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
