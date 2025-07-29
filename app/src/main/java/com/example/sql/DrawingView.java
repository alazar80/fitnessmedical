package com.example.sql;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {
    private static class Stroke {
        Path path;
        Paint paint;
        Stroke(Path p, Paint pt) { path = p; paint = pt; }
    }

    private final List<Stroke> strokes = new ArrayList<>();
    private Path currentPath;
    private Paint currentPaint;

    public DrawingView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        initPaint();
    }

    private void initPaint() {
        currentPaint = new Paint();
        currentPaint.setColor(Color.BLACK);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(4f);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPath = new Path();
        strokes.add(new Stroke(currentPath, new Paint(currentPaint)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Stroke s : strokes) {
            canvas.drawPath(s.path, s.paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float x = ev.getX(), y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                currentPath.moveTo(x, y);
                strokes.add(new Stroke(currentPath, new Paint(currentPaint)));
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                // nothing extra
                invalidate();
                return true;
        }
        return super.onTouchEvent(ev);
    }

    // Public APIs to control paint & canvas
    public void setColor(int color) {
        currentPaint.setColor(color);
    }

    public void setStrokeWidth(float w) {
        currentPaint.setStrokeWidth(w);
    }

    public void undo() {
        if (!strokes.isEmpty()) {
            strokes.remove(strokes.size() - 1);
            invalidate();
        }
    }

    public void clear() {
        strokes.clear();
        initPaint();
        invalidate();
    }
}
