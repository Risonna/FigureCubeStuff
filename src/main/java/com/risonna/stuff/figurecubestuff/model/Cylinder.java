package com.risonna.stuff.figurecubestuff.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cylinder extends AbstractFigure {
    public Cylinder() {
        super();
    }

    public Cylinder(double h, double osn, TypeOfMetal m) {
        super(h, osn, m);
    }

    @Override
    public void calc() {
        this.deformHeight = Math.round(this.origHeight - this.origHeight / (double)this.material.getFactor());
        this.deformOsnov = Math.round(2 * Math.sqrt(this.origHeight * Math.pow((this.origOsnov * 0.5), 2) / this.deformHeight));
    }

    @Override
    protected void drawFigure(GraphicsContext gc, double centerX, double centerY,
                              double height, double base, double scale, boolean isDeformed) {
        double scaledHeight = height * scale;
        double scaledBase = base * scale;

        // Top ellipse
        gc.strokeOval(centerX - scaledBase/2, centerY, scaledBase, scaledBase/4);

        // Vertical lines
        gc.strokeLine(centerX - scaledBase/2, centerY + scaledBase/8,
                centerX - scaledBase/2, centerY + scaledHeight + scaledBase/8);
        gc.strokeLine(centerX + scaledBase/2, centerY + scaledBase/8,
                centerX + scaledBase/2, centerY + scaledHeight + scaledBase/8);

        // Bottom ellipse
        gc.strokeOval(centerX - scaledBase/2, centerY + scaledHeight,
                scaledBase, scaledBase/4);
    }
}