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
    protected void drawFigure(GraphicsContext gc, double centerX, double centerY,
                              double height, double base, double scale, boolean isDeformed) {
        double scaledHeight = height * scale;
        double scaledBase = base * scale;

        // Calculate points
        Point2D C = new Point2D(centerX - scaledBase/2, centerY + scaledHeight/4);
        Point2D A = new Point2D(C.getX() + scaledBase/2, centerY);
        Point2D B = new Point2D(A.getX() + scaledBase/2, A.getY() + scaledHeight/4);
        Point2D D = new Point2D(C.getX() + scaledBase, C.getY());

        Point2D C2 = new Point2D(C.getX(), C.getY() + scaledHeight);
        Point2D B2 = new Point2D(B.getX(), B.getY() + scaledHeight);
        Point2D D2 = new Point2D(D.getX(), D.getY() + scaledHeight);

        drawPrism(gc, A, B, C, D, C2, B2, D2);

        // Add auto-scaling if needed
        checkAndAdjustScale(gc, scale, new Point2D[]{A, B, C, D, C2, B2, D2}, scaledHeight, scaledBase);
    }

    private void drawPrism(GraphicsContext gc, Point2D A, Point2D B, Point2D C,
                           Point2D D, Point2D C2, Point2D B2, Point2D D2) {
        // Front face
        gc.strokeLine(A.getX(), A.getY(), B.getX(), B.getY());
        gc.strokeLine(B.getX(), B.getY(), D.getX(), D.getY());
        gc.strokeLine(D.getX(), D.getY(), C.getX(), C.getY());
        gc.strokeLine(C.getX(), C.getY(), A.getX(), A.getY());

        // Connect faces
        gc.strokeLine(C.getX(), C.getY(), C2.getX(), C2.getY());
        gc.strokeLine(B.getX(), B.getY(), B2.getX(), B2.getY());
        gc.strokeLine(D.getX(), D.getY(), D2.getX(), D2.getY());

        // Back face
        gc.strokeLine(C2.getX(), C2.getY(), B2.getX(), B2.getY());
        gc.strokeLine(B2.getX(), B2.getY(), D2.getX(), D2.getY());
        gc.strokeLine(D2.getX(), D2.getY(), C2.getX(), C2.getY());
    }

    private void checkAndAdjustScale(GraphicsContext gc, double scale, Point2D[] points,
                                     double height, double base) {
        // Add bounds to prevent infinite recursion
        if (scale <= 0.1 || scale >= 10) {
            return;
        }

        if ((height < 110 && base < 120) || (base < 110 && height < 120)) {
            double newScale = Math.min(scale + 0.1, 10);
            if (newScale != scale) {
                draw(gc, newScale);
            }
        } else {
            // Check bounds
            boolean needsAdjustment = false;
            for (Point2D point : points) {
                if (point.getX() <= 0 || point.getY() <= 0 ||
                        point.getX() >= gc.getCanvas().getWidth() ||
                        point.getY() >= gc.getCanvas().getHeight()) {
                    needsAdjustment = true;
                    break;
                }
            }
            if (needsAdjustment) {
                double newScale = Math.max(scale - 0.001, 0.1);
                if (newScale != scale) {
                    draw(gc, newScale);
                }
            }
        }
    }
}