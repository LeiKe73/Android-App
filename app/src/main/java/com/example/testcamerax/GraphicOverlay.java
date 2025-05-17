package com.example.testcamerax;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GraphicOverlay extends View {
    private final List<Graphic> graphics = new ArrayList<>();

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void clear() {
        graphics.clear();
        postInvalidate();
    }

    public void add(Graphic graphic) {
        graphics.add(graphic);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Graphic graphic : graphics) {
            graphic.draw(canvas);
        }
    }

    public static abstract class Graphic {
        public abstract void draw(Canvas canvas);
    }
}
