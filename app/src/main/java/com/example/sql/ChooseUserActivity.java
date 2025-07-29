package com.example.sql;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ChooseUserActivity extends AppCompatActivity {

    private ListView userListView;
    private ArrayList<String> userEmails = new ArrayList<>();
    private ArrayList<Integer> userIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);
        ThemeUtil.applyBackground(this, R.id.mainLayout);

        userListView = findViewById(R.id.userListView);
//        dbHelper = new DatabaseHelper(this);
        //loadUsers();
        userListView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            int selectedUserId = userIds.get(position);
            Intent intent = new Intent(ChooseUserActivity.this, DoctorAssignExerciseActivity.class);
            intent.putExtra("userId", selectedUserId);
            startActivity(intent);
        });
    }
//    private void loadUsers() {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT id, email FROM Users", null);
//        if (cursor.moveToFirst()) {
//            do {
//                userIds.add(cursor.getInt(0));
//                userEmails.add(cursor.getString(1));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userEmails);
//        userListView.setAdapter(adapter);
//    }
}
