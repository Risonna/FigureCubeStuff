package com.risonna.stuff.figurecubestuff.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Parallelepiped extends AbstractFigure {
    private static final double DEPTH_RATIO = 0.5;

    public Parallelepiped() {
        super();
    }

    public Parallelepiped(double h, double osn, TypeOfMetal m) {
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
        double depth = scaledBase * DEPTH_RATIO;

        // Front face
        gc.strokeRect(centerX - scaledBase/2, centerY, scaledBase, scaledHeight);

        // Top face
        double[] topX = {
                centerX - scaledBase/2,
                centerX + scaledBase/2,
                centerX + scaledBase/2 + depth,
                centerX - scaledBase/2 + depth
        };
        double[] topY = {
                centerY,
                centerY,
                centerY - depth/2,
                centerY - depth/2
        };
        gc.strokePolygon(topX, topY, 4);

        // Right face
        double[] rightX = {
                centerX + scaledBase/2,
                centerX + scaledBase/2 + depth,
                centerX + scaledBase/2 + depth,
                centerX + scaledBase/2
        };
        double[] rightY = {
                centerY,
                centerY - depth/2,
                centerY + scaledHeight - depth/2,
                centerY + scaledHeight
        };
        gc.strokePolygon(rightX, rightY, 4);
    }
}