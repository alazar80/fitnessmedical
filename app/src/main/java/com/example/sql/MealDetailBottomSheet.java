package com.example.sql;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MealDetailBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESC = "description";
    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_CALORIES = "calories";

    public static MealDetailBottomSheet newInstance(String title, String description, String imageUrl, int calories) {
        MealDetailBottomSheet sheet = new MealDetailBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, description);
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putInt(ARG_CALORIES, calories);
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_meal_detail, container, false);

        ImageView mealImage = view.findViewById(R.id.mealImage);
        TextView title = view.findViewById(R.id.titleText);
        TextView calories = view.findViewById(R.id.caloriesText);
        TextView description = view.findViewById(R.id.mealDescription);

        Bundle args = getArguments();
        if (args != null) {
            title.setText(args.getString(ARG_TITLE));
            calories.setText(args.getInt(ARG_CALORIES) + " kcal");
            description.setText(args.getString(ARG_DESC));

            Glide.with(requireContext())
                    .load(args.getString(ARG_IMAGE_URL))
                    .placeholder(R.drawable.ic_plate_fork)
                    .into(mealImage);
        }

        return view;
    }
}
