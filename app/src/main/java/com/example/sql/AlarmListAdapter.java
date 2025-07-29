package com.example.sql;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class AlarmListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<AlarmData> alarmList;

    public AlarmListAdapter(Context context, ArrayList<AlarmData> alarmList) {
        this.context = context;
        this.alarmList = alarmList;
    }

    @Override
    public int getCount() {
        return alarmList.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return alarmList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        AlarmData alarm = alarmList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
            holder = new ViewHolder();
            holder.alarmTimeText = convertView.findViewById(R.id.alarm_time_text);
            holder.editBtn = convertView.findViewById(R.id.btn_edit);
            holder.deleteBtn = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.alarmTimeText.setText(String.format("%02d:%02d", alarm.hour, alarm.minute));

        holder.deleteBtn.setOnClickListener(v -> {
            if (context instanceof AlarmListActivity) {
                ((AlarmListActivity) context).deleteAlarm(alarm);
            }
        });

        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, Alarm.class);
            intent.putExtra("edit_alarm_id", alarm.id);
            intent.putExtra("edit_hour", alarm.hour);
            intent.putExtra("edit_minute", alarm.minute);
            intent.putExtra("edit_sound", alarm.soundSelect);
            context.startActivity(intent);
        });

        return convertView;
    }

    static class ViewHolder {
        TextView alarmTimeText;
        Button editBtn, deleteBtn;
    }
}
