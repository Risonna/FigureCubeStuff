package com.risonna.stuff.figurecubestuff.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Calculation implements Serializable {
    private String figureType;
    private TypeOfMetal material;
    private double height;
    private double base;
    private double deformedHeight;
    private double deformedBase;
    private LocalDateTime calculationTime;

    // Constructor
    public Calculation(String figureType, TypeOfMetal material, double height, double base, double deformedHeight, double deformedBase) {
        this.figureType = figureType;
        this.material = material;
        this.height = height;
        this.base = base;
        this.deformedHeight = deformedHeight;
        this.deformedBase = deformedBase;
        this.calculationTime = LocalDateTime.now();
    }

    // Getters
    public String getFigureType() { return figureType; }
    public TypeOfMetal getMaterial() { return material; }
    public double getHeight() { return height; }
    public double getBase() { return base; }
    public double getDeformedHeight() { return deformedHeight; }
    public double getDeformedBase() { return deformedBase; }
    public LocalDateTime getCalculationTime() { return calculationTime; }
}