package com.risonna.stuff.figurecubestuff.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Parallelepiped extends AbstractFigure {
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
    public void draw(GraphicsContext gc, double scale) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        // Calculate scaled dimensions
        double scaledHeight = origHeight * scale;
        double scaledBase = origOsnov * scale;
        double scaledDeformHeight = deformHeight * scale;
        double scaledDeformBase = deformOsnov * scale;
        double depth = scaledBase * 0.5; // depth for 3D effect

        // Starting Y position - same for both figures
        double yStart = canvasHeight / 3;

        // Original parallelepiped - left side
        double xOriginal = canvasWidth / 3;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // Draw original parallelepiped
        // Front face
        gc.strokeRect(xOriginal - scaledBase/2, yStart, scaledBase, scaledHeight);

        // Top face
        double[] topX = {
                xOriginal - scaledBase/2,
                xOriginal + scaledBase/2,
                xOriginal + scaledBase/2 + depth,
                xOriginal - scaledBase/2 + depth
        };
        double[] topY = {
                yStart,
                yStart,
                yStart - depth/2,
                yStart - depth/2
        };
        gc.strokePolygon(topX, topY, 4);

        // Right face
        double[] rightX = {
                xOriginal + scaledBase/2,
                xOriginal + scaledBase/2 + depth,
                xOriginal + scaledBase/2 + depth,
                xOriginal + scaledBase/2
        };
        double[] rightY = {
                yStart,
                yStart - depth/2,
                yStart + scaledHeight - depth/2,
                yStart + scaledHeight
        };
        gc.strokePolygon(rightX, rightY, 4);

        // Deformed parallelepiped - right side
        double xDeformed = 2 * canvasWidth / 3;
        gc.setStroke(Color.BROWN);

        // Draw deformed parallelepiped
        // Front face
        gc.strokeRect(xDeformed - scaledDeformBase/2, yStart, scaledDeformBase, scaledDeformHeight);

        // Top face
        double[] defTopX = {
                xDeformed - scaledDeformBase/2,
                xDeformed + scaledDeformBase/2,
                xDeformed + scaledDeformBase/2 + depth,
                xDeformed - scaledDeformBase/2 + depth
        };
        double[] defTopY = {
                yStart,
                yStart,
                yStart - depth/2,
                yStart - depth/2
        };
        gc.strokePolygon(defTopX, defTopY, 4);

        // Right face
        double[] defRightX = {
                xDeformed + scaledDeformBase/2,
                xDeformed + scaledDeformBase/2 + depth,
                xDeformed + scaledDeformBase/2 + depth,
                xDeformed + scaledDeformBase/2
        };
        double[] defRightY = {
                yStart,
                yStart - depth/2,
                yStart + scaledDeformHeight - depth/2,
                yStart + scaledDeformHeight
        };
        gc.strokePolygon(defRightX, defRightY, 4);
    }
}