package com.risonna.stuff.figurecubestuff.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class AbstractFigure {
    protected TypeOfMetal material;
    protected double origHeight, origOsnov, deformHeight, deformOsnov;
    protected static final Color ORIGINAL_COLOR = Color.BLUE; // Changed from BLACK
    protected static final Color DEFORMED_COLOR = Color.RED;  // Changed from BROWN
    protected static final double LINE_WIDTH = 2.5; // Slightly thicker lines

    public AbstractFigure() {
        origHeight = 1;
        origOsnov = 1;
        deformHeight = 1;
        deformOsnov = 1;
    }

    public AbstractFigure(double h, double osn, TypeOfMetal m) {
        origHeight = h;
        origOsnov = osn;
        deformHeight = h;
        deformOsnov = osn;
        material = m;
    }

    public abstract void calc();

    // Replace the draw method in AbstractFigure.java
    public void draw(GraphicsContext gc, double scale) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setLineWidth(LINE_WIDTH);

        // Calculate center of canvas
        double centerX = gc.getCanvas().getWidth() / 2;
        double centerY = gc.getCanvas().getHeight() / 2;

        // Draw original figure
        gc.setStroke(ORIGINAL_COLOR);
        drawFigure(gc, centerX, centerY, origHeight, origOsnov, scale, false);

        // Draw deformed figure inside the original figure
        // The bottom of the deformed figure is aligned with the bottom of the original
        gc.setStroke(DEFORMED_COLOR);

        // For the deformed figure, we place it at the same X position but adjust Y position
        // to align bottoms while keeping the deformed figure's center at the appropriate height
        double deformedCenterY = centerY + (origHeight - deformHeight) * scale / 2;
        drawFigure(gc, centerX, deformedCenterY, deformHeight, deformOsnov, scale, true);
    }

    protected abstract void drawFigure(GraphicsContext gc, double centerX, double centerY,
                                       double height, double base, double scale, boolean isDeformed);

    public double getDeformHeight() {
        return deformHeight;
    }

    public double getDeformOsnov() {
        return deformOsnov;
    }
}