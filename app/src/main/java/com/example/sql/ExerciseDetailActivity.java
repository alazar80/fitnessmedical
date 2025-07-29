package com.example.sql;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ExerciseDetailActivity extends BottomSheetDialogFragment {

    private String title;
    private String description;
    private String gifUrl;

    // Create a new instance with arguments
    public static ExerciseDetailActivity newInstance(String title, String description, String gifUrl) {
        ExerciseDetailActivity fragment = new ExerciseDetailActivity();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);
        args.putString("gifUrl", gifUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_exercise_detail, container, false);

        // Get views
        TextView titleText = view.findViewById(R.id.titleText);
        TextView descriptionText = view.findViewById(R.id.exerciseDescription);
        ImageView exerciseImage = view.findViewById(R.id.animationImage);

        // Retrieve arguments
        if (getArguments() != null) {
            title = getArguments().getString("title");
            description = getArguments().getString("description");
            gifUrl = getArguments().getString("gifUrl");
        }

        // Set values
        titleText.setText(title);
        descriptionText.setText(description);

        // Load image
        Glide.with(this).asGif().load(gifUrl).into(exerciseImage);

        return view;
    }
}
