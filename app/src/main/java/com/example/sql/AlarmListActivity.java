package com.example.sql;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AlarmListActivity extends AppCompatActivity implements AlarmRecyclerAdapter.OnAlarmActionListener {

    private RecyclerView recyclerView;
    private ArrayList<AlarmData> alarmList = new ArrayList<>();
    private AlarmRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        recyclerView = findViewById(R.id.alarm_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAlarms();

        adapter = new AlarmRecyclerAdapter(alarmList, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder t) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                AlarmData alarm = alarmList.get(position);
                deleteAlarm(alarm);
                adapter.notifyItemRemoved(position);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void loadAlarms() {
        SharedPreferences prefs = getSharedPreferences("alarm_prefs", MODE_PRIVATE);
        String json = prefs.getString("alarms", "[]");
        Type type = new TypeToken<ArrayList<AlarmData>>() {}.getType();
        alarmList = new Gson().fromJson(json, type);
        if (alarmList == null) alarmList = new ArrayList<>();
    }

    private void saveAlarms() {
        SharedPreferences prefs = getSharedPreferences("alarm_prefs", MODE_PRIVATE);
        String json = new Gson().toJson(alarmList);
        prefs.edit().putString("alarms", json).apply();
    }

    public void deleteAlarm(AlarmData alarm) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, alarm.id, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        alarmList.remove(alarm);
        saveAlarms();
        adapter.notifyDataSetChanged();

        ToastUtil.show(this, "Alarm deleted", 1/3);
    }

    public void editAlarm(AlarmData alarm) {
        Intent intent = new Intent(this, Alarm.class);
        intent.putExtra("edit_alarm_id", alarm.id);
        intent.putExtra("edit_hour", alarm.hour);
        intent.putExtra("edit_minute", alarm.minute);
        intent.putExtra("edit_sound", alarm.soundSelect);
        startActivity(intent);
    }

    @Override
    public void onDelete(AlarmData alarm) {
        deleteAlarm(alarm);
    }

    @Override
    public void onEdit(AlarmData alarm) {
        editAlarm(alarm);
    }
}
