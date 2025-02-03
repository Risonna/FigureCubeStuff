package com.risonna.stuff.figurecubestuff.model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Triangle extends AbstractFigure {
    public Triangle() {
        super();
    }

    public Triangle(double h, double osn, TypeOfMetal m) {
        super(h, osn, m);
    }

    @Override
    public void calc() {
        this.deformHeight = Math.round(this.origHeight - this.origHeight / (double)this.material.getFactor());
        this.deformOsnov = Math.round(this.origOsnov - this.origOsnov / (double)this.material.getFactor());
    }

    @Override
    public void draw(GraphicsContext gc, double scale) {
        // Clear the canvas
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setLineWidth(2);

        // Canvas dimensions
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        // Scaled dimensions
        int height = (int) (origHeight * scale);
        int osnov = (int) (origOsnov * scale);
        int defHeight = (int) (deformHeight * scale);
        int defOsnov = (int) (deformOsnov * scale);

        // Calculate start positions
        int deformXStart = (int) (canvasWidth / 2 - defOsnov / 2);
        int yStart = (int) (canvasHeight / 2 - height / 2);
        int xStart = (int) (canvasWidth / 2 - osnov / 2);

        // Original prism points
        Point2D C = new Point2D(xStart, yStart + height / 4);
        Point2D A = new Point2D(C.getX() + osnov / 2, yStart);
        Point2D B = new Point2D(A.getX() + osnov / 2, A.getY() + height / 4);
        Point2D D = new Point2D(C.getX() + osnov, C.getY());

        Point2D C2 = new Point2D(C.getX(), C.getY() + height);
        Point2D B2 = new Point2D(B.getX(), B.getY() + height);
        Point2D D2 = new Point2D(D.getX(), D.getY() + height);

        // Draw the original prism
        gc.setStroke(Color.BLACK);
        drawPrism(gc, A, B, C, D, C2, B2, D2);

        // Deformed prism points
        Point2D C_d = new Point2D(deformXStart, C2.getY() - defHeight);
        Point2D A_d = new Point2D(C_d.getX() + defOsnov / 2, C_d.getY() - height / 4);
        Point2D B_d = new Point2D(A_d.getX() + defOsnov / 2, A_d.getY() + height / 4);
        Point2D D_d = new Point2D(C_d.getX() + defOsnov, C_d.getY());

        Point2D C2_d = new Point2D(C_d.getX(), C2.getY());
        Point2D B2_d = new Point2D(B_d.getX(), C2.getY());
        Point2D D2_d = new Point2D(D_d.getX(), C2.getY());

        // Draw the deformed prism
        gc.setStroke(Color.BROWN);
        drawPrism(gc, A_d, B_d, C_d, D_d, C2_d, B2_d, D2_d);

        // Recursive scaling if the shape is too small or out of bounds
        if ((height < 110 && osnov < 120) || (osnov < 110 && height < 120)) {
            draw(gc, scale + 0.1);
        } else {
            // Check if points are out of bounds
            Point2D[] allPoints = { C, A, B, D, C2, B2, D2, C_d, A_d, B_d, D_d, C2_d, B2_d, D2_d };
            for (Point2D point : allPoints) {
                if (point.getX() <= 0 || point.getY() <= 0 || point.getX() >= canvasWidth || point.getY() >= canvasHeight) {
                    draw(gc, scale - 0.001);
                    break;
                }
            }
        }
    }

    // Helper method to draw a prism
    private void drawPrism(GraphicsContext gc, Point2D A, Point2D B, Point2D C, Point2D D, Point2D C2, Point2D B2, Point2D D2) {
        // Draw the front face
        gc.strokeLine(A.getX(), A.getY(), B.getX(), B.getY());
        gc.strokeLine(B.getX(), B.getY(), D.getX(), D.getY());
        gc.strokeLine(D.getX(), D.getY(), C.getX(), C.getY());
        gc.strokeLine(C.getX(), C.getY(), A.getX(), A.getY());

        // Connect the front and back faces
        gc.strokeLine(C.getX(), C.getY(), C2.getX(), C2.getY());
        gc.strokeLine(B.getX(), B.getY(), B2.getX(), B2.getY());
        gc.strokeLine(D.getX(), D.getY(), D2.getX(), D2.getY());

        // Draw the back face
        gc.strokeLine(C2.getX(), C2.getY(), B2.getX(), B2.getY());
        gc.strokeLine(B2.getX(), B2.getY(), D2.getX(), D2.getY());
        gc.strokeLine(D2.getX(), D2.getY(), C2.getX(), C2.getY());
    }
}