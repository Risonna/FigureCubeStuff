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
        this.deformOsnov = Math.round(2 * Math.sqrt(this.origHeight * Math.pow((this.origOsnov * 0.5), 2) / this.deformHeight));
    }

    @Override
    protected void drawFigure(GraphicsContext gc, double centerX, double centerY,
                              double height, double base, double scale, boolean isDeformed) {
        double scaledHeight = height * scale;
        double scaledBase = base * scale;
        double depth = scaledBase * DEPTH_RATIO;

        // Add semi-transparent fill if this is the deformed figure
        if (isDeformed) {
            gc.setGlobalAlpha(0.4);
            gc.setFill(Color.RED);
        }

        // Adjust vertical position to center
        double topY = centerY - scaledHeight/2;
        double bottomY = centerY + scaledHeight/2;

        // Front face
        gc.strokeRect(centerX - scaledBase/2, topY, scaledBase, scaledHeight);
        if (isDeformed) {
            gc.fillRect(centerX - scaledBase/2, topY, scaledBase, scaledHeight);
        }

        // Top face
        double[] topX = {
                centerX - scaledBase/2,
                centerX + scaledBase/2,
                centerX + scaledBase/2 + depth,
                centerX - scaledBase/2 + depth
        };
        double[] topYA = {
                topY,
                topY,
                topY - depth/2,
                topY - depth/2
        };
        gc.strokePolygon(topX, topYA, 4);
        if (isDeformed) {
            gc.fillPolygon(topX, topYA, 4);
        }

        // Right face
        double[] rightX = {
                centerX + scaledBase/2,
                centerX + scaledBase/2 + depth,
                centerX + scaledBase/2 + depth,
                centerX + scaledBase/2
        };
        double[] rightY = {
                topY,
                topY - depth/2,
                bottomY - depth/2,
                bottomY
        };
        gc.strokePolygon(rightX, rightY, 4);
        if (isDeformed) {
            gc.fillPolygon(rightX, rightY, 4);
        }

        // Reset transparency
        if (isDeformed) {
            gc.setGlobalAlpha(1.0);
        }
    }
}