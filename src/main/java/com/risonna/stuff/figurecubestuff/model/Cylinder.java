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
    public void draw(GraphicsContext gc, double scale) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        // Calculate scaled dimensions
        double scaledHeight = origHeight * scale;
        double scaledBase = origOsnov * scale;
        double scaledDeformHeight = deformHeight * scale;
        double scaledDeformBase = deformOsnov * scale;

        // Starting Y position - same for both figures
        double yStart = canvasHeight / 4; // Moved higher up on canvas

        // Original cylinder - left side
        double xOriginal = canvasWidth / 3;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // Draw original cylinder
        // Top ellipse
        gc.strokeOval(xOriginal - scaledBase/2, yStart, scaledBase, scaledBase/4);

        // Vertical lines - now starting from middle of ovals
        gc.strokeLine(xOriginal - scaledBase/2, yStart + scaledBase/8,
                xOriginal - scaledBase/2, yStart + scaledHeight + scaledBase/8);
        gc.strokeLine(xOriginal + scaledBase/2, yStart + scaledBase/8,
                xOriginal + scaledBase/2, yStart + scaledHeight + scaledBase/8);

        // Bottom ellipse
        gc.strokeOval(xOriginal - scaledBase/2, yStart + scaledHeight, scaledBase, scaledBase/4);

        // Deformed cylinder - right side
        double xDeformed = 2 * canvasWidth / 3;
        gc.setStroke(Color.BROWN);

        // Draw deformed cylinder
        // Top ellipse
        gc.strokeOval(xDeformed - scaledDeformBase/2, yStart, scaledDeformBase, scaledDeformBase/4);

        // Vertical lines - starting from middle of ovals
        gc.strokeLine(xDeformed - scaledDeformBase/2, yStart + scaledDeformBase/8,
                xDeformed - scaledDeformBase/2, yStart + scaledDeformHeight + scaledDeformBase/8);
        gc.strokeLine(xDeformed + scaledDeformBase/2, yStart + scaledDeformBase/8,
                xDeformed + scaledDeformBase/2, yStart + scaledDeformHeight + scaledDeformBase/8);

        // Bottom ellipse
        gc.strokeOval(xDeformed - scaledDeformBase/2, yStart + scaledDeformHeight,
                scaledDeformBase, scaledDeformBase/4);
    }
}