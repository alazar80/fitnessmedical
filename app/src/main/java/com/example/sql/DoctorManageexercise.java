package com.example.sql;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
// ✅ RIGHT
import androidx.appcompat.widget.SearchView;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorManageexercise extends AppCompatActivity {

    private RecyclerView recyclerViewExercises;
    private ExerciseAdapter adapter;
    private List<Exercise> exerciseList;
    private int doctorId;
    private TextView outputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_manage_exercise);
        ThemeUtil.applyBackground(this, R.id.mainLayout);
        ThemeUtil.applyThemeFromPrefs(this);

        doctorId = getIntent().getIntExtra("doctor_id", -1);
        ToastUtil.show(this, String.valueOf(doctorId), 1/3);
        exerciseList = new ArrayList<>();
        recyclerViewExercises = findViewById(R.id.recyclerViewAssigned);
//        outputView = findViewById(R.id.output);
        adapter = new ExerciseAdapter(
                exerciseList,
                true,  // editable mode
                new ExerciseAdapter.OnExerciseActionListener() {
                    @Override
                    public void onEdit(Exercise exercise) {
                        showEditExerciseDialog(
                                DoctorManageexercise.this,  // context
                                exercise,
                                false,  // isAdmin = false (for doctor)
                                () -> adapter.notifyDataSetChanged()  // refresh after editing
                        );
                    }

                    @Override
                    public void onDelete(Exercise exercise) {
                        deleteExercise(String.valueOf(exercise.getId()));
                    }

                },
                doctorId
        );
        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExercises.setAdapter(adapter);
        setupButtons();
        fetchExercises();
        ImageView backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->onBackPressed());
        SearchView searchView = findViewById(R.id.searchViewExercises);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


    }

    private void setupButtons() {
        Button btnAssignExercises = findViewById(R.id.btnAssignExercises);
        Button btnAddExercises = findViewById(R.id.btnManageExercises);

        btnAssignExercises.setOnClickListener(view -> {
            Intent intent = new Intent(this, DoctorAssignExerciseActivity.class);
            intent.putExtra("doctor_id", doctorId);
            startActivity(intent);
        });

        btnAddExercises.setOnClickListener(view -> {
            Intent intent = new Intent(this, DoctorAddExerciseActivity.class);
            intent.putExtra("doctor_id", doctorId);
            startActivity(intent);
        });
    }


    private void fetchExercises() {
        String url = ApiConfig.GET_EXERCISE_BY_DOCTOR_FOR_HOME + "?doctor_id=" + doctorId;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> handleFetchResponse(response),
                error -> ToastUtil.show(this, "Failed to load exercises", 1/3));
        Volley.newRequestQueue(this).add(request);
    }


    private void handleFetchResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray exercises = jsonObject.getJSONArray("exercises");

            List<Exercise> parsedList = new ArrayList<>();
            for (int i = 0; i < exercises.length(); i++) {
                JSONObject obj = exercises.getJSONObject(i);
                Exercise exercise = new Exercise(
                        obj.getString("name"),
                        obj.getString("duration"),
                        obj.getString("type"),
                        obj.getString("description"),
                        obj.getString("fitnessGoal"),
                        obj.getString("experienceLevel"),
                        obj.getString("icon_id")
                );
                exercise.setId(obj.getInt("id"));
                parsedList.add(exercise);
            }

            adapter.updateData(parsedList);  // ✅ full sync

        } catch (Exception e) {
            ToastUtil.show(this, "Error parsing data", 1/3);
            Log.e("AdminManageExercise", "Parsing error", e);
        }
    }


    private void updateExercise(String url, String id,
                                String name, String desc, String duration,
                                String type, String fitnessGoal,
                                String experienceLevel, String iconId,
                                Runnable onSuccess) {
        StringRequest req = new StringRequest(
                Request.Method.POST, url,
                resp -> {
                    // optionally parse {"success":true,...}
                    onSuccess.run();
                    ToastUtil.show(this, "Exercise updated", 1/3);
                },
                err -> ToastUtil.show(this, "Update failed", 1/3)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("id",               id);
                p.put("name",             name);
                p.put("description",      desc);
                p.put("duration",         duration);
                p.put("type",             type);
                p.put("fitnessGoal",      fitnessGoal);
                p.put("experienceLevel",  experienceLevel);
                p.put("icon_id",          iconId);
                return p;
            }
        };
        Volley.newRequestQueue(this).add(req);
    }
    private void filterExercises(String query) {
        List<Exercise> filteredList = new ArrayList<>();
        for (Exercise ex : exerciseList) {
            if (ex.getName().toLowerCase().contains(query) ||
                    ex.getType().toLowerCase().contains(query) ||
                    ex.getDescription().toLowerCase().contains(query)) {
                filteredList.add(ex);
            }
        }
        adapter.updateData(filteredList);  // See next step
    }

    private void deleteExercise(String id) {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.URL_DELETE_EXERCISE,
                response -> {
                    ToastUtil.show(this, "Exercise deleted", 1/3);
                    fetchExercises();
                },
                error -> ToastUtil.show(this, "Failed to delete", 1/3)) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
    private void showEditExerciseDialog(Context context,
                                        Exercise exercise,
                                        boolean isAdmin,
                                        Runnable onEdited) {
        // Inflate your dialog view
        View editView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_edit_exercise, null);

        EditText nameInput  = editView.findViewById(R.id.editExerciseName);
        EditText descInput  = editView.findViewById(R.id.editExerciseDescription);
        EditText durationInput = editView.findViewById(R.id.editExerciseDuration);
        Spinner typeSpinner = editView.findViewById(R.id.editExerciseType);
        Spinner goalSpinner = editView.findViewById(R.id.editExerciseGoal);
        Spinner expSpinner  = editView.findViewById(R.id.editExerciseExperience);
        EditText iconIdInput  = editView.findViewById(R.id.editExerciseIconId);

        // Pre-fill with current values from the exercise object
        nameInput.setText(exercise.getName());
        descInput.setText(exercise.getDescription());
        durationInput.setText(exercise.getDuration());
        typeSpinner.setSelection(((ArrayAdapter<String>)typeSpinner.getAdapter())
                .getPosition(exercise.getType()));
        goalSpinner.setSelection(((ArrayAdapter<String>)goalSpinner.getAdapter())
                .getPosition(exercise.getFitnessGoal()));
        expSpinner.setSelection(((ArrayAdapter<String>)expSpinner.getAdapter())
                .getPosition(exercise.getExperienceLevel()));
        iconIdInput.setText(exercise.getIconId());

        // Show dialog
        new AlertDialog.Builder(context)
                .setTitle("Edit Exercise")
                .setView(editView)
                .setPositiveButton("Save", (d,w) -> {
                    // Collect the updated information
                    String newName  = nameInput.getText().toString().trim();
                    String newDesc  = descInput.getText().toString().trim();
                    String newDur   = durationInput.getText().toString().trim();
                    String newType  = typeSpinner.getSelectedItem().toString();
                    String newGoal  = goalSpinner.getSelectedItem().toString();
                    String newExp   = expSpinner.getSelectedItem().toString();
                    String newIcon  = iconIdInput.getText().toString().trim();
                    String exId     = String.valueOf(exercise.getId());

                    // Choose the correct URL based on isAdmin
                    String url = isAdmin
                            ? ApiConfig.ADMIN_UPDATE_EXERCISE_URL
                            : ApiConfig.DOCTOR_UPDATE_EXERCISE_URL;

                    // Call helper to send the data to the server
                    updateExercise(url, exId,
                            newName, newDesc, newDur,
                            newType, newGoal, newExp, newIcon,
                            () -> {
                                // Update local object & refresh the UI
                                exercise.setName(newName);
                                exercise.setDescription(newDesc);
                                exercise.setDuration(newDur);
                                exercise.setType(newType);
                                exercise.setFitnessGoal(newGoal);
                                exercise.setExperienceLevel(newExp);
                                exercise.setIconId(newIcon);
                                onEdited.run();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}
