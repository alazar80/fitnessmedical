package com.example.sql;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlarmRecyclerAdapter extends RecyclerView.Adapter<AlarmRecyclerAdapter.AlarmViewHolder> {

    private final ArrayList<AlarmData> alarmList;
    private final OnAlarmActionListener actionListener;

    public interface OnAlarmActionListener {
        void onDelete(AlarmData alarm);
        void onEdit(AlarmData alarm);
    }

    public AlarmRecyclerAdapter(ArrayList<AlarmData> alarmList, OnAlarmActionListener actionListener) {
        this.alarmList = alarmList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmData alarm = alarmList.get(position);
        holder.timeText.setText(String.format("%02d:%02d", alarm.hour, alarm.minute));

        holder.editBtn.setOnClickListener(v -> actionListener.onEdit(alarm));
        holder.deleteBtn.setOnClickListener(v -> actionListener.onDelete(alarm));
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public AlarmData getAlarmAt(int position) {
        return alarmList.get(position);
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        Button editBtn, deleteBtn;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.alarm_time_text);
            editBtn = itemView.findViewById(R.id.btn_edit);
            deleteBtn = itemView.findViewById(R.id.btn_delete);
        }
    }
}
