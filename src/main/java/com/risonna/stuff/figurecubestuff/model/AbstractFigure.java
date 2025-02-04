package com.risonna.stuff.figurecubestuff.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class AbstractFigure {
    protected TypeOfMetal material;
    protected double origHeight, origOsnov, deformHeight, deformOsnov;
    protected static final Color ORIGINAL_COLOR = Color.BLACK;
    protected static final Color DEFORMED_COLOR = Color.BROWN;
    protected static final double LINE_WIDTH = 2;

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

    public void draw(GraphicsContext gc, double scale) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setLineWidth(LINE_WIDTH);

        // Draw original figure
        gc.setStroke(ORIGINAL_COLOR);
        drawFigure(gc, gc.getCanvas().getWidth() / 3, gc.getCanvas().getHeight() / 3,
                origHeight, origOsnov, scale, false);

        // Draw deformed figure
        gc.setStroke(DEFORMED_COLOR);
        drawFigure(gc, 2 * gc.getCanvas().getWidth() / 3, gc.getCanvas().getHeight() / 3,
                deformHeight, deformOsnov, scale, true);
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