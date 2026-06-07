package com.example.deteksidaun;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class OverlayView extends View {

    private List<DetectionResult> results = new ArrayList<>();
    private Paint boxPaint, textPaint;
    private int previewWidth, previewHeight;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void initPaints() {
        boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(5f);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40f);
        textPaint.setFakeBoldText(true);
        textPaint.setShadowLayer(5f, 0, 0, Color.BLACK);
    }

    public void setResults(List<DetectionResult> results, int previewWidth, int previewHeight) {
        this.results = results;
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
        postInvalidate(); // Minta view untuk digambar ulang
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (results != null) {
            for (DetectionResult result : results) {
                RectF box = result.boundingBox;
                String label = String.format("%s (%.2f)", result.label, result.score);

                // Draw bounding box
                canvas.drawRect(box, boxPaint);

                // Draw text background
                float textWidth = textPaint.measureText(label);
                canvas.drawRect(box.left, box.top - textPaint.getTextSize() - 10,
                        box.left + textWidth + 20, box.top, boxPaint); // Adjust background rect

                // Draw text
                canvas.drawText(label, box.left + 10, box.top - 10, textPaint);
            }
        }
    }

    public static class DetectionResult {
        RectF boundingBox;
        String label;
        float score;

        public DetectionResult(RectF boundingBox, String label, float score) {
            this.boundingBox = boundingBox;
            this.label = label;
            this.score = score;
        }
    }
}