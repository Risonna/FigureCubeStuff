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
        this.deformOsnov = Math.round(this.origOsnov - this.origOsnov / (double)this.material.getFactor());
    }

    @Override
    protected void drawFigure(GraphicsContext gc, double centerX, double centerY,
                              double height, double base, double scale, boolean isDeformed) {
        double scaledHeight = height * scale;
        double scaledBase = base * scale;

        // Base ellipse
        gc.strokeOval(centerX - scaledBase/2, centerY + scaledHeight,
                scaledBase, scaledBase/4);

        // Lines to apex
        gc.strokeLine(centerX, centerY,
                centerX - scaledBase/2, centerY + scaledHeight + scaledBase/8);
        gc.strokeLine(centerX, centerY,
                centerX + scaledBase/2, centerY + scaledHeight + scaledBase/8);
    }
}