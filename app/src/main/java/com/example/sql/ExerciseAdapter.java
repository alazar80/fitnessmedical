package com.example.sql;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> implements Filterable {


    public interface OnExerciseActionListener {
        void onEdit(Exercise exercise);
        void onDelete(Exercise exercise);
    }

    private List<Exercise> exerciseList;
    private boolean isEditable;
    private OnExerciseActionListener listener;
    private int currentUserId;
    private List<Exercise> fullList;


    // ✅ Constructor for editable mode (DoctorManageExercise)
    public ExerciseAdapter(List<Exercise> exerciseList, boolean isEditable, OnExerciseActionListener listener,int userId) {
        this.exerciseList = exerciseList;
        this.fullList = new ArrayList<>(exerciseList);
        this.isEditable = isEditable;
        this.listener = listener;
        this.currentUserId = userId;
    }


    // ✅ Constructor for non-editable mode (e.g., EnduranceImprovementExercise)
    public ExerciseAdapter(List<Exercise> exerciseList, boolean isEditable,  int userId ) {
        this(exerciseList, isEditable, null,userId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                isEditable ? R.layout.item_exercises_edit_delete : R.layout.item_exercises,
                parent, false
        );
        return new ViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Exercise exercise = exerciseList.get(position);
//        holder.exerciseName.setText(exercise.getName());
//        holder.exerciseDuration.setText(exercise.getDuration());
//        holder.exerciseIcon.setImageResource(R.drawable.notif);
//        // Image (handles both item_meal:id/mealImage and edit layout:id/mealIcon)
////        Glide.with(context)
////                .load(ApiConfig.BASE_URL + "/fitness/" + exercise.getIconId() + ".gif")
////                .placeholder(R.drawable.ic_plate_fork)
////                .into(holder.exerciseIcon);
//        if (isEditable) {
//            holder.btnEdit.setVisibility(View.VISIBLE);
//            holder.btnDelete.setVisibility(View.VISIBLE);
//
//            holder.btnEdit.setOnClickListener(v -> {
//                if (listener != null) listener.onEdit(exercise);
//            });
//
//            holder.btnDelete.setOnClickListener(v -> {
//                if (listener != null) listener.onDelete(exercise);
//            });
//        } else {
//            if (holder.btnEdit != null) holder.btnEdit.setVisibility(View.GONE);
//            if (holder.btnDelete != null) holder.btnDelete.setVisibility(View.GONE);
//        }
//    }

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Exercise exercise = exerciseList.get(position);
    holder.exerciseName.setText(exercise.getName());
    holder.exerciseDuration.setText(exercise.getDuration());
    Glide.with(holder.exerciseIcon.getContext())
            .load(ApiConfig.BASE_URL + "/fitness/" + exercise.getIconId() + ".gif")
            .placeholder(R.drawable.notif)      // shown while loading
            .error(R.drawable.notif)            // fallback if load fails
            .into(holder.exerciseIcon);

    holder.itemView.setOnClickListener(v -> {
        // Build a BottomSheetDialogFragment and pass in title/description/gifUrl
        Bundle args = new Bundle();
        args.putString("title", exercise.getName());
        args.putString("description", exercise.getDescription());
        // Assuming your Detail fragment is expecting a full URL to the GIF:
        String gifUrl = ApiConfig.BASE_URL + "/fitness/" + exercise.getIconId() + ".gif";
        args.putString("gifUrl", gifUrl);

        ExerciseDetailActivity detailSheet = new ExerciseDetailActivity();
        detailSheet.setArguments(args);

        // Show it via the support FragmentManager.
        // Make sure the context is an AppCompatActivity (or FragmentActivity).
        Context ctx = holder.itemView.getContext();
        if (ctx instanceof AppCompatActivity) {
            detailSheet.show(
                    ((AppCompatActivity) ctx).getSupportFragmentManager(),
                    "exercise_detail"
            );
        }
    });
    // 1) FETCH SAVED PROGRESS from server
    // (POST "user_id" + "exercise_id" → your get_progress.php → returns {"progress": int})
    String fetchUrl = ApiConfig.GET_PROGRESS;
    StringRequest fetchRequest = new StringRequest(
            Request.Method.POST,
            fetchUrl,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        // Parse JSON: e.g. {"progress":75}
                        JSONObject obj = new JSONObject(response);
                        int savedProgress = obj.optInt("progress", 0);
                        // Update UI on main thread:

                    } catch (Exception e) {
                        // If parse fails, default to 0%

                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // On error, set progress to 0%
                }
            }
    ) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            // You need to pass the current userId here—either store it in the adapter or
            // retrieve it from a shared place. Suppose you added `private int currentUserId;`
            // to your adapter and passed it in from the Activity’s constructor.
            params.put("user_id", String.valueOf(currentUserId));
            params.put("exercise_id", String.valueOf(exercise.getId()));
            return params;
        }
    };
    Volley.newRequestQueue(holder.itemView.getContext()).add(fetchRequest);

    // 2) SET UP “Done” SWITCH listener
    // When toggled, immediately update UI and then POST new progress to save_progress.php
//    holder.completeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//        int newProgress = isChecked ? 100 : 0;
//        // Update UI immediately:
//        holder.exerciseProgressBar.setProgress(newProgress);
//        holder.progressText.setText(newProgress + "%");
//
//        // POST new progress:
//        String saveUrl = ApiConfig.SAVE_PROGRESS;
//        StringRequest saveRequest = new StringRequest(
//                Request.Method.POST,
//                saveUrl,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // (Optionally) check server’s response for success
//                        // e.g., { "status": "ok" }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // If saving fails, revert switch and progress bar
//                        holder.completeSwitch.setChecked(!isChecked);
//                        holder.exerciseProgressBar.setProgress(isChecked ? 0 : 100);
//                        holder.progressText.setText((isChecked ? 0 : 100) + "%");
//                        ToastUtil.show(
//                                holder.itemView.getContext(),
//                                "Failed to save progress",
//                                Toast.LENGTH_SHORT
//                        ).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", String.valueOf(currentUserId));
//                params.put("exercise_id", String.valueOf(exercise.getId()));
//                params.put("progress", String.valueOf(newProgress));
//                return params;
//            }
//        };
//        Volley.newRequestQueue(holder.itemView.getContext()).add(saveRequest);
//    });

    // 3) Handle Edit/Delete buttons if in “editable” mode


    if (isEditable) {
        holder.btnEdit.setVisibility(View.VISIBLE);
        holder.btnDelete.setVisibility(View.VISIBLE);
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(exercise);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(exercise);
        });
    } else {
        if (holder.btnEdit != null) holder.btnEdit.setVisibility(View.GONE);
        if (holder.btnDelete != null) holder.btnDelete.setVisibility(View.GONE);
    }
}


    @Override
    public int getItemCount() {
        return exerciseList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName, exerciseDuration;
        ImageView exerciseIcon, exerciseArrow;
        TextView progressText;
        SwitchCompat completeSwitch;
        Button btnEdit, btnDelete; // only used in editable mode

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseDuration = itemView.findViewById(R.id.exerciseDuration);
            exerciseIcon = itemView.findViewById(R.id.exerciseIcon);
            exerciseArrow = itemView.findViewById(R.id.exerciseArrow);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView exerciseName, exerciseDuration;
//        ImageView exerciseIcon, exerciseArrow;
//        Button btnEdit, btnDelete;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            exerciseName = itemView.findViewById(R.id.exerciseName);
//            exerciseDuration = itemView.findViewById(R.id.exerciseDuration);
//            exerciseIcon = itemView.findViewById(R.id.exerciseIcon);
//            exerciseArrow = itemView.findViewById(R.id.exerciseArrow);
//            btnEdit = itemView.findViewById(R.id.btnEdit);
//            btnDelete = itemView.findViewById(R.id.btnDelete);
//        }
//
//    }
public void updateData(List<Exercise> newData) {
    this.exerciseList.clear();
    this.exerciseList.addAll(newData);

    this.fullList = new ArrayList<>(newData); // ensures clean reference
    notifyDataSetChanged();
}


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Exercise> filtered = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(fullList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Exercise ex : fullList) {
                        if (ex.getName().toLowerCase().contains(filterPattern) ||
                                ex.getType().toLowerCase().contains(filterPattern) ||
                                ex.getDescription().toLowerCase().contains(filterPattern)) {
                            filtered.add(ex);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                exerciseList.clear();
                exerciseList.addAll((List) results.values);
                notifyDataSetChanged();

            }
        };
    }

}
