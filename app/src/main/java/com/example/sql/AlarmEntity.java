package com.example.sql;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarms")
public class AlarmEntity {
    @PrimaryKey
        public int id;
        public int hour;
        public int minute;
        public int soundSelect;
        public long timeInMillis;

        // NEW FIELDS
        public String medicationName;
        public String dosage;

}
