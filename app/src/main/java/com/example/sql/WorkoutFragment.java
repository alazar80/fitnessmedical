package com.example.sql;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Locale;

public class WorkoutFragment extends Fragment {
    private ImageView gifImageView;
    private TextView timerTextView;
    private Button nextWorkoutButton;
    private Button skipButton;
    private TextToSpeech textToSpeech;
    private CountDownTimer countDownTimer;

    private static final String KEY_USER_ID     = "user_id";
    private static final String KEY_EXERCISE_ID = "exercise_id";

    private int userId;
    private int exerciseId;
    private String ttsText;
    private int workoutTime;
    private String gifUrl;

    // ─── Factory / arguments ───────────────────────────────────────────────────────
    public static WorkoutFragment newInstance(int userId,
                                              int exerciseId,
                                              String gifUrl,
                                              String ttsText,
                                              int workoutTime) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_USER_ID, userId);
        args.putInt(KEY_EXERCISE_ID, exerciseId);
        args.putString("gifUrl", gifUrl);
        args.putString("ttsText", ttsText);
        args.putInt("workoutTime", workoutTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve data passed to the fragment via arguments
        if (getArguments() != null) {
            userId     = getArguments().getInt(KEY_USER_ID);
            exerciseId = getArguments().getInt(KEY_EXERCISE_ID);
            gifUrl     = getArguments().getString("gifUrl");
            ttsText    = getArguments().getString("ttsText");
            workoutTime= getArguments().getInt("workoutTime", 30);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        gifImageView       = view.findViewById(R.id.gifImageView);
        timerTextView      = view.findViewById(R.id.timerTextView);
        nextWorkoutButton  = view.findViewById(R.id.nextWorkoutButton);
        skipButton         = view.findViewById(R.id.skipButton);

        // Hide “Next” until timers finish
        nextWorkoutButton.setVisibility(View.GONE);

        loadGifFromUrl();
        speakText(ttsText);
        startWorkoutTimer(workoutTime);

        // When “Next” is tapped (after rest), go to the next exercise
        nextWorkoutButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity2) {
                ((MainActivity2) getActivity()).loadNextWorkout();
            }
        });

        // If user explicitly skips, cancel timers and load the next
        skipButton.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            if (getActivity() instanceof MainActivity2) {
                ((MainActivity2) getActivity()).loadNextWorkout();
            }
        });

        return view;
    }

    private void loadGifFromUrl() {
        if (gifUrl != null && !gifUrl.isEmpty()) {
            Glide.with(this)
                    .asGif()
                    .load(gifUrl)
                    .placeholder(R.drawable.ic_default_profile)
                    .error(R.drawable.ic_default_profile)
                    .into(gifImageView);
        } else {
            Log.e("WorkoutFragment", "GIF URL is missing");
        }
    }

    private void speakText(String text) {
        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    private void startWorkoutTimer(int seconds) {
        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time Left: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                // When workout timer ends, switch to a 10s rest
                timerTextView.setText("Rest Time! 10s");
                startRestTimer();
            }
        }.start();
    }

    private void startRestTimer() {
        new CountDownTimer(10 * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Rest: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                // ① Mark this exercise as “done” on server (100% progress)
                if (getActivity() instanceof MainActivity2) {
                    ((MainActivity2) getActivity()).onExerciseCompleted();
                }

                // ③ Now show the “Next” button so user can proceed
                nextWorkoutButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}
