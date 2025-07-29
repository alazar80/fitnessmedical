package com.example.sql;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Notepad extends AppCompatActivity {
    private static final int REQUEST_OPEN = 1;
    private static final int REQUEST_SAVE = 2;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notepad);

        editText = findViewById(R.id.editText);
        ((Button)findViewById(R.id.btnOpen))
                .setOnClickListener(v -> openFile());
        ((Button)findViewById(R.id.btnSave))
                .setOnClickListener(v -> saveFile());
        ((Button)findViewById(R.id.btnClear))
                .setOnClickListener(v -> editText.setText(""));
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, REQUEST_OPEN);
    }

    private void saveFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "file.txt");
        startActivityForResult(intent, REQUEST_SAVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        Uri uri = data.getData();

        try {
            if (requestCode == REQUEST_OPEN) {
                // read
                try (InputStream in = getContentResolver().openInputStream(uri);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                    editText.setText(sb.toString());
                }

            } else if (requestCode == REQUEST_SAVE) {
                // write
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    out.write(editText.getText().toString().getBytes());
                }
                Toast.makeText(this,
                        "Saved to " + uri.getPath(),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this,
                    "File error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
