package com.risonna.stuff.figurecubestuff.model;

import javafx.scene.canvas.GraphicsContext;

public abstract class AbstractFigure {
    protected TypeOfMetal material;
    protected double origHeight, origOsnov, deformHeight, deformOsnov;

    public AbstractFigure() {
        origHeight = 1;
        origOsnov = 1;
        deformHeight = 1;
        deformOsnov = 1;
    }

    public AbstractFigure(double h, double osn, TypeOfMetal m) {
        origHeight = h;
        origOsnov = osn;
        deformHeight = h;
        deformOsnov = osn;
        material = m;
    }

    public abstract void calc();
    public abstract void draw(GraphicsContext gc, double scale);

    public double getDeformHeight() {
        return deformHeight;
    }

    public double getDeformOsnov() {
        return deformOsnov;
    }
}