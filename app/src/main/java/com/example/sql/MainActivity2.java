package com.example.sql;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    private List<Exercise> exerciseList;
    private int currentWorkoutIndex = 0;
    private int userId;
    private int lastProgressSent = 0;

    // Keep track of how many exercises have been completed in this session
    private int exercisesCompletedCount = 0;
    private static final int THRESHOLD_TO_LEVEL_UP = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applySavedLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ThemeUtil.applyBackground(this, R.id.mainLayout);

        // ① Read user_id from the Intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId < 0) {
            ToastUtil.show(this, "No user ID", 1/3);
            finish();
            return;
        }

        // ② Read exerciseList from the Intent
        exerciseList = getIntent().getParcelableArrayListExtra("exerciseList");
        if (exerciseList == null || exerciseList.isEmpty()) {
            ToastUtil.show(this, "No workout data received", 1/3);
            finish();
            return;
        }

        loadNextWorkout();
    }

    /**
     * Called by WorkoutFragment when one exercise finishes.
     * 1) Increments the local completed‐count.
     * 2) Computes overallPercent = (exercisesCompletedCount / totalExercises) * 100.
     * 3) Sends overallPercent to the server to update users.progress.
     * 4) If count reaches 30, bumps experience level and resets the counter.
     * 5) Then attempts to load the next exercise (or finishes if none left).
     */
//    public void onExerciseCompleted() {
//        exercisesCompletedCount++;
//
//        // ── (A) Determine if we’ve hit one of the 4 “milestones” ────────────────────
////        Integer milestonePercent = null;
////        switch (exercisesCompletedCount) {
////            case 7:
////                milestonePercent = 25;
////                break;
////            case 14:
////                milestonePercent = 50;
////                break;
////            case 21:
////                milestonePercent = 75;
////                break;
////            case 30:
////                milestonePercent = 100;
////                break;
////        }
//
//        // ── (B) If this count matches 7, 14, 21, or 30, send that exact percent to the server ─────────
//        int totalExercises = exerciseList.size();
//        int overallPercent = (int) ((exercisesCompletedCount / (float) totalExercises) * 100);
//
//        saveUserProgressToServer(userId, overallPercent);
//
//
//        // ── (C) If we’ve now reached (or exceeded) the 30-exercise threshold, bump level once ─────────
//        if (exercisesCompletedCount >= THRESHOLD_TO_LEVEL_UP) {
//            bumpExperienceLevelOnServer(userId);
//            exercisesCompletedCount = 0; // reset so the next 30 start counting anew
//        }
//
//        // ── (D) Finally, move on to the next exercise (or finish) ───────────────────────────────
//        loadNextWorkout();
//    }

    public void onExerciseCompleted() {
        exercisesCompletedCount++;

        int totalExercises = exerciseList.size();
        int overallPercent = (int) ((exercisesCompletedCount / (float) totalExercises) * 100);
        int progressDelta = overallPercent - lastProgressSent; // send only the new percent

        if (progressDelta > 0) {
            saveUserWorkoutSessionToServer(userId, exercisesCompletedCount, exercisesCompletedCount * 30, exerciseList.size());

            lastProgressSent = overallPercent;
        }

        if (exercisesCompletedCount >= THRESHOLD_TO_LEVEL_UP) {
            bumpExperienceLevelOnServer(userId);
            exercisesCompletedCount = 0;
            lastProgressSent = 0; // reset both on level up!
        }

        loadNextWorkout();
    }
    /**
     * Loads the next exercise fragment. If we've exhausted the list, go to DoneActivity.
     */
    public void loadNextWorkout() {
        if (currentWorkoutIndex >= exerciseList.size()) {
            int totalExercises = exerciseList.size();
            int overallPercent = (int) ((exercisesCompletedCount / (float) totalExercises) * 100);
            int progressDelta = overallPercent - lastProgressSent;
            if (progressDelta > 0) {
                saveUserWorkoutSessionToServer(userId, exercisesCompletedCount, exercisesCompletedCount * 30, exerciseList.size());
                lastProgressSent = overallPercent;
            }
// after you POST the new set to the server...
            new ChartDialogFragment()
                    .newInstance(userId)
                    .show(getSupportFragmentManager(), "progressDialog");

            Intent intent = new Intent(MainActivity2.this, ProgressChartActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("showPartyPopper", true);    // ← add this
            startActivity(intent);
            finish();
            return;
        }


        // 1) Grab the next exercise object
        Exercise exercise = exerciseList.get(currentWorkoutIndex);
        int exerciseId   = exercise.getId();
        String gifUrl    = ApiConfig.GET_EXERCISE_ICON_ID
                + exercise.getIconId() + ".gif";
        String ttsText   = exercise.getDescription();
        int workoutTime  = parseDurationToSeconds(exercise.getDuration());

        // 2) Create a fragment instance including userId and exerciseId
        WorkoutFragment fragment = WorkoutFragment.newInstance(
                userId,
                exerciseId,
                gifUrl,
                ttsText,
                workoutTime
        );

        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        // 3) Advance the index so next time we load the next one
        currentWorkoutIndex++;
    }


    private int parseDurationToSeconds(String duration) {
        try {
            return Integer.parseInt(duration.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 30; // fallback if parsing fails
        }
    }

    /**
     * (Unchanged) Mark one exercise as “done” on the server (i.e. write progress=100 into exercises table).
     * Called from WorkoutFragment when an exercise’s timers finish.
     */

    /**
     * NEW: Save the overall percent (0–100) into users.progress.
     * Called each time onExerciseCompleted() runs.
     */
    private void saveUserWorkoutSessionToServer(int userId, int exercisesDone, int totalTimeSec, int sessionSize) {
        String url = ApiConfig.UPDATE_USER_WORKOUT; // e.g. update_user_workout.php

        StringRequest req = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d("Workout", "Saved: " + response);
                },
                error -> ToastUtil.show(this, "Workout save failed", 1/3)
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("exercises_done", String.valueOf(exercisesDone));
                params.put("total_time_sec", String.valueOf(totalTimeSec));

                params.put("session_size", String.valueOf(exerciseList.size()));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }


//    public void trackProgress(String userId, int completedExercises) {
//        StringRequest request = new StringRequest(Request.Method.POST, "https://yourserver.com/update_user_progress.php",
//                response -> Log.d("Progress", "Update success"),
//                error -> Log.e("Progress", "Update failed", error)
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", userId);
//                params.put("completed_exercises", String.valueOf(completedExercises));
//                return params;
//            }
//        };
//        Volley.newRequestQueue(this).add(request);
//    }


    /**
     * NEW: Bump the user's experience level on the server once they've completed 30 exercises.
     */
    private void bumpExperienceLevelOnServer(int userId) {
        String url = ApiConfig.UPDATE_EXPERIENCE_LEVEL;
        // e.g. "https://your.api.com/update_experience_level.php"
        StringRequest req = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    // Optionally parse {"status":"ok"}… but in any case, do nothing further
                },
                error -> ToastUtil.show(this, "Failed to update experience level", 1/3)
        ) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }
}
