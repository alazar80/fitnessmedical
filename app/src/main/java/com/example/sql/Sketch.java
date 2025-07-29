package com.example.sql;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

public class Sketch extends AppCompatActivity {
    private DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);
        drawingView = findViewById(R.id.drawing_view);

        // Color buttons
        findViewById(R.id.btn_black).setOnClickListener(v ->
            drawingView.setColor(Color.BLACK));
        findViewById(R.id.btn_red).setOnClickListener(v ->
            drawingView.setColor(Color.RED));
        findViewById(R.id.btn_green).setOnClickListener(v ->
            drawingView.setColor(Color.GREEN));
        findViewById(R.id.btn_blue).setOnClickListener(v ->
            drawingView.setColor(Color.BLUE));

        // Stroke width
        SeekBar sb = findViewById(R.id.seekbar_stroke);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float w = Math.max(1f, progress);
                drawingView.setStrokeWidth(w);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Undo / Clear
        ((Button)findViewById(R.id.btn_undo)).setOnClickListener(v ->
            drawingView.undo());
        ((Button)findViewById(R.id.btn_clear)).setOnClickListener(v ->
            drawingView.clear());
    }
}
