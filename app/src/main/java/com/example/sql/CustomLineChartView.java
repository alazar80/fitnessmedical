package com.example.sql;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class CustomLineChartView extends View {

    private List<Float> dataPoints; // Data to plot
    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;

    public CustomLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize Paint for lines
        linePaint = new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);

        // Initialize Paint for points
        pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStyle(Paint.Style.FILL);

        // Initialize Paint for text
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30f);
    }

    public void setData(List<Float> data) {
        this.dataPoints = data;
        invalidate(); // Redraw the view with new data
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dataPoints == null || dataPoints.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float padding = 50;

        // Calculate spacing
        float maxDataPoint = 100; // Assume maximum weight is 100kg
        float xStep = (width - 2 * padding) / (dataPoints.size() - 1);
        float yStep = (height - 2 * padding) / maxDataPoint;

        // Draw axes
        canvas.drawLine(padding, height - padding, width - padding, height - padding, textPaint); // X-axis
        canvas.drawLine(padding, padding, padding, height - padding, textPaint); // Y-axis

        // Draw data
        for (int i = 0; i < dataPoints.size() - 1; i++) {
            float x1 = padding + i * xStep;
            float y1 = height - padding - (dataPoints.get(i) * yStep);
            float x2 = padding + (i + 1) * xStep;
            float y2 = height - padding - (dataPoints.get(i + 1) * yStep);

            // Draw line between points
            canvas.drawLine(x1, y1, x2, y2, linePaint);

            // Draw points
            canvas.drawCircle(x1, y1, 10, pointPaint);
        }

        // Draw the last point
        float lastX = padding + (dataPoints.size() - 1) * xStep;
        float lastY = height - padding - (dataPoints.get(dataPoints.size() - 1) * yStep);
        canvas.drawCircle(lastX, lastY, 10, pointPaint);
    }
}

