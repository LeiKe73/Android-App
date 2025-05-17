package com.example.testcamerax;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.mlkit.vision.text.Text;

public class TextGraphic extends GraphicOverlay.Graphic {
    private final Text.Element element;
    private final Paint rectPaint;
    private final Paint textPaint;

    public TextGraphic(GraphicOverlay overlay, Text.Element element) {
        this.element = element;

        rectPaint = new Paint();
        rectPaint.setColor(Color.RED);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(4.0f);

        textPaint = new Paint();
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(40.0f);
    }

    @Override
    public void draw(Canvas canvas) {
        if (element == null || element.getBoundingBox() == null) return;

        RectF rect = new RectF(element.getBoundingBox());
        canvas.drawRect(rect, rectPaint);
        canvas.drawText(element.getText(), rect.left, rect.bottom, textPaint);
    }
}
