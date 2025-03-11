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

        // Keep the original positioning logic
        double yOffset = scaledHeight/4;
        double topY = centerY - scaledHeight/2;

        // Calculate points for the front face (trapezoid)
        Point2D C = new Point2D(centerX - scaledBase/2, topY + yOffset);
        Point2D A = new Point2D(C.getX() + scaledBase/2, topY);
        Point2D B = new Point2D(A.getX() + scaledBase/2, A.getY() + yOffset);
        Point2D D = new Point2D(C.getX() + scaledBase, C.getY());

        // Calculate points for the back face
        Point2D C2 = new Point2D(C.getX(), C.getY() + scaledHeight);
        Point2D B2 = new Point2D(B.getX(), B.getY() + scaledHeight);
        Point2D D2 = new Point2D(D.getX(), D.getY() + scaledHeight);
        Point2D A2 = new Point2D(A.getX(), A.getY() + scaledHeight);

        // Draw the complete prism with all visible edges
        drawPrism(gc, A, B, C, D, A2, B2, C2, D2, isDeformed);

        // Auto-scaling check
        checkAndAdjustScale(gc, scale, new Point2D[]{A, B, C, D, A2, B2, C2, D2}, scaledHeight, scaledBase);

        // Reset transparency
        if (isDeformed) {
            gc.setGlobalAlpha(1.0);
        }
    }

    private void drawPrism(GraphicsContext gc,
                           Point2D A, Point2D B, Point2D C, Point2D D,
                           Point2D A2, Point2D B2, Point2D C2, Point2D D2,
                           boolean isDeformed) {
        // Front face (trapezoid)
        gc.strokeLine(A.getX(), A.getY(), B.getX(), B.getY());
        gc.strokeLine(B.getX(), B.getY(), D.getX(), D.getY());
        gc.strokeLine(D.getX(), D.getY(), C.getX(), C.getY());
        gc.strokeLine(C.getX(), C.getY(), A.getX(), A.getY());

        // Connect front face to back face
        gc.strokeLine(A.getX(), A.getY(), A2.getX(), A2.getY());
        gc.strokeLine(C.getX(), C.getY(), C2.getX(), C2.getY());
        gc.strokeLine(B.getX(), B.getY(), B2.getX(), B2.getY());
        gc.strokeLine(D.getX(), D.getY(), D2.getX(), D2.getY());

        // Back face (trapezoid)
        gc.strokeLine(A2.getX(), A2.getY(), B2.getX(), B2.getY());
        gc.strokeLine(B2.getX(), B2.getY(), D2.getX(), D2.getY());
        gc.strokeLine(D2.getX(), D2.getY(), C2.getX(), C2.getY());
        gc.strokeLine(C2.getX(), C2.getY(), A2.getX(), A2.getY());

        // Fill faces if this is the deformed figure
        if (isDeformed) {
            // Front face
            double[] frontX = {A.getX(), B.getX(), D.getX(), C.getX()};
            double[] frontY = {A.getY(), B.getY(), D.getY(), C.getY()};
            gc.fillPolygon(frontX, frontY, 4);

            // Top face
            double[] topX = {A.getX(), B.getX(), B2.getX(), A2.getX()};
            double[] topY = {A.getY(), B.getY(), B2.getY(), A2.getY()};
            gc.fillPolygon(topX, topY, 4);

            // Bottom face
            double[] bottomX = {C.getX(), D.getX(), D2.getX(), C2.getX()};
            double[] bottomY = {C.getY(), D.getY(), D2.getY(), C2.getY()};
            gc.fillPolygon(bottomX, bottomY, 4);

            // Left side
            double[] leftX = {A.getX(), C.getX(), C2.getX(), A2.getX()};
            double[] leftY = {A.getY(), C.getY(), C2.getY(), A2.getY()};
            gc.fillPolygon(leftX, leftY, 4);

            // Right side
            double[] rightX = {B.getX(), D.getX(), D2.getX(), B2.getX()};
            double[] rightY = {B.getY(), D.getY(), D2.getY(), B2.getY()};
            gc.fillPolygon(rightX, rightY, 4);

            // Back face
            double[] backX = {A2.getX(), B2.getX(), D2.getX(), C2.getX()};
            double[] backY = {A2.getY(), B2.getY(), D2.getY(), C2.getY()};
            gc.fillPolygon(backX, backY, 4);
        }
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