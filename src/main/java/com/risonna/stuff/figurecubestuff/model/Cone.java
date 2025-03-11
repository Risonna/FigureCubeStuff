package com.risonna.stuff.figurecubestuff.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class Cone extends AbstractFigure {
    public Cone() {
        super();
    }

    public Cone(double h, double osn, TypeOfMetal m) {
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

        // Add semi-transparent fill if this is the deformed figure
        if (isDeformed) {
            gc.setGlobalAlpha(0.4);
            gc.setFill(Color.RED);
        }

        // Base ellipse
        gc.strokeOval(centerX - scaledBase/2, centerY + scaledHeight/2,
                scaledBase, scaledBase/4);
        if (isDeformed) {
            gc.fillOval(centerX - scaledBase/2, centerY + scaledHeight/2,
                    scaledBase, scaledBase/4);
        }

        // Lines to apex
        gc.strokeLine(centerX, centerY - scaledHeight/2,
                centerX - scaledBase/2, centerY + scaledHeight/2 + scaledBase/8);
        gc.strokeLine(centerX, centerY - scaledHeight/2,
                centerX + scaledBase/2, centerY + scaledHeight/2 + scaledBase/8);

        // Fill the cone shape if deformed
        if (isDeformed) {
            double[] xPoints = {centerX, centerX - scaledBase/2, centerX + scaledBase/2};
            double[] yPoints = {centerY - scaledHeight/2,
                    centerY + scaledHeight/2 + scaledBase/8,
                    centerY + scaledHeight/2 + scaledBase/8};
            gc.fillPolygon(xPoints, yPoints, 3);
        }

        // Reset transparency
        if (isDeformed) {
            gc.setGlobalAlpha(1.0);
        }
    }
}