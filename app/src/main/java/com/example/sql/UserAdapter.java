package com.example.sql;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class UserAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_edit_delete_user, parent, false);
        }

        // 1) grab your data + views
        User   user           = users.get(position);
        TextView usernameTextView   = convertView.findViewById(R.id.usernameTextView);
        TextView emailTextView      = convertView.findViewById(R.id.emailTextView);
        TextView phoneTextView      = convertView.findViewById(R.id.phoneTextView);
        Button   editButton   = convertView.findViewById(R.id.editButton);
        Button   deleteButton = convertView.findViewById(R.id.deleteButton);

        // 2) bind data
        usernameTextView.setText(user.getUsername());
        emailTextView   .setText(user.getEmail());
        phoneTextView   .setText(user.getPhone());

        // 3) DELETE with confirmation dialog
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete user?")
                    .setMessage("Really delete “" + user.getUsername() + "”?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // call your Activity’s deleteUser(...)
                        if (context instanceof Manageuser) {
                            ((Manageuser)context)
                                    .deleteUser(String.valueOf(user.getId()));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return convertView;
    }



    public void updateList(ArrayList<User> newList) {
        this.users = newList;
        notifyDataSetChanged();
    }

}
