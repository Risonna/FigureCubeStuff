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

        // Add a semi-transparent fill if this is the deformed figure
        if (isDeformed) {
            gc.setGlobalAlpha(0.4); // Semi-transparent
            gc.setFill(Color.RED);
        }

        // Top ellipse
        gc.strokeOval(centerX - scaledBase/2, centerY - scaledHeight/2, scaledBase, scaledBase/4);
        if (isDeformed) gc.fillOval(centerX - scaledBase/2, centerY - scaledHeight/2, scaledBase, scaledBase/4);

        // Vertical lines
        gc.strokeLine(centerX - scaledBase/2, centerY - scaledHeight/2 + scaledBase/8,
                centerX - scaledBase/2, centerY + scaledHeight/2 + scaledBase/8);
        gc.strokeLine(centerX + scaledBase/2, centerY - scaledHeight/2 + scaledBase/8,
                centerX + scaledBase/2, centerY + scaledHeight/2 + scaledBase/8);

        // Bottom ellipse
        gc.strokeOval(centerX - scaledBase/2, centerY + scaledHeight/2,
                scaledBase, scaledBase/4);
        if (isDeformed) gc.fillOval(centerX - scaledBase/2, centerY + scaledHeight/2, scaledBase, scaledBase/4);

        // Reset transparency
        if (isDeformed) {
            gc.setGlobalAlpha(1.0);
        }
    }
}