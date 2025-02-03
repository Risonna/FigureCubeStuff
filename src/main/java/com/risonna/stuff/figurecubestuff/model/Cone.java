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
    public void draw(GraphicsContext gc, double scale) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        // Calculate scaled dimensions
        double scaledBase = origOsnov * scale; // Base of the triangle (horizontal)
        double scaledHeight = origHeight * scale; // Height of the triangle (vertical)
        double scaledDeformBase = deformOsnov * scale; // Deformed base
        double scaledDeformHeight = deformHeight * scale; // Deformed height
        double depth = scaledHeight * 0.3; // Depth for 3D effect

        // Center positions
        double xStart = canvasWidth / 3;
        double yCenter = canvasHeight / 2;

        // ---- Original Prism ----
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // Coordinates for triangular faces (rotated around Z-axis)
        double[] frontX = {
                xStart,                             // Top-left point
                xStart,                             // Bottom-left point
                xStart + scaledBase                 // Bottom-right point
        };
        double[] frontY = {
                yCenter - scaledHeight / 2,         // Top-left point
                yCenter + scaledHeight / 2,         // Bottom-left point
                yCenter                             // Bottom-right point
        };
        gc.strokePolygon(frontX, frontY, 3);

        // Back triangular face (shifted along Z-axis as depth)
        double[] backX = {
                xStart + depth,                     // Top-left point
                xStart + depth,                     // Bottom-left point
                xStart + scaledBase + depth         // Bottom-right point
        };
        double[] backY = {
                yCenter - scaledHeight / 2,         // Top-left point
                yCenter + scaledHeight / 2,         // Bottom-left point
                yCenter                             // Bottom-right point
        };
        gc.strokePolygon(backX, backY, 3);

        // Connect corresponding vertices to form the rectangular faces
        gc.strokeLine(frontX[0], frontY[0], backX[0], backY[0]); // Top-left
        gc.strokeLine(frontX[1], frontY[1], backX[1], backY[1]); // Bottom-left
        gc.strokeLine(frontX[2], frontY[2], backX[2], backY[2]); // Bottom-right

        // ---- Deformed Prism ----
        double xDeformed = 2 * canvasWidth / 3;

        gc.setStroke(Color.BROWN);

        // Coordinates for deformed triangular faces
        double[] defFrontX = {
                xDeformed,                          // Top-left point
                xDeformed,                          // Bottom-left point
                xDeformed + scaledDeformBase        // Bottom-right point
        };
        double[] defFrontY = {
                yCenter - scaledDeformHeight / 2,   // Top-left point
                yCenter + scaledDeformHeight / 2,   // Bottom-left point
                yCenter                             // Bottom-right point
        };
        gc.strokePolygon(defFrontX, defFrontY, 3);

        // Back triangular face (shifted along Z-axis as depth)
        double[] defBackX = {
                xDeformed + depth,                  // Top-left point
                xDeformed + depth,                  // Bottom-left point
                xDeformed + scaledDeformBase + depth // Bottom-right point
        };
        double[] defBackY = {
                yCenter - scaledDeformHeight / 2,   // Top-left point
                yCenter + scaledDeformHeight / 2,   // Bottom-left point
                yCenter                             // Bottom-right point
        };
        gc.strokePolygon(defBackX, defBackY, 3);

        // Connect corresponding vertices to form the rectangular faces
        gc.strokeLine(defFrontX[0], defFrontY[0], defBackX[0], defBackY[0]); // Top-left
        gc.strokeLine(defFrontX[1], defFrontY[1], defBackX[1], defBackY[1]); // Bottom-left
        gc.strokeLine(defFrontX[2], defFrontY[2], defBackX[2], defBackY[2]); // Bottom-right
    }
}