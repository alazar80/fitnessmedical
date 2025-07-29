package com.example.sql;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> implements Filterable {

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
        void onEditClick(Doctor doctor);
        void onDeleteClick(Doctor doctor);
    }

    private final List<Doctor> doctorList;
    private List<Doctor> fullList; // for filtering

    private final OnDoctorClickListener listener;
    private final int layoutId;

    // Pass layoutId in the constructor!
    public DoctorAdapter(List<Doctor> doctorList, int layoutId, OnDoctorClickListener listener) {
        this.doctorList = doctorList;
        this.fullList = new ArrayList<>(doctorList); // clone original
        this.layoutId = layoutId;
        this.listener = listener;
    }


    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.name.setText(doctor.getName());
        holder.phone.setText(doctor.getPhoneNumber());

        // Click for item
        holder.itemView.setOnClickListener(v -> listener.onDoctorClick(doctor));

        // If edit/delete buttons exist, set them up
        if (holder.editButton != null) {
            holder.editButton.setOnClickListener(v -> listener.onEditClick(doctor));
        }
        if (holder.deleteButton != null) {
            holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(doctor));
        }
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone;
        Button editButton, deleteButton;

        DoctorViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.doctorName);
            phone = itemView.findViewById(R.id.doctorPhone);
            // Will be null if the layout does not have these buttons
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
    public void updateData(List<Doctor> newData) {
        doctorList.clear();
        doctorList.addAll(newData);
        fullList.clear();
        fullList.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Doctor> filtered = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(fullList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Doctor doc : fullList) {
                        if (doc.getName().toLowerCase().contains(filterPattern) ||
                                doc.getPhoneNumber().toLowerCase().contains(filterPattern)) {
                            filtered.add(doc);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                doctorList.clear();
                doctorList.addAll((List<Doctor>) results.values);
                notifyDataSetChanged();
            }
        };
    }

}
