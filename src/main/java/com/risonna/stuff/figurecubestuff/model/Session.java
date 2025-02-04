package com.risonna.stuff.figurecubestuff.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Session implements Serializable {
    private List<Calculation> calculations = new ArrayList<>();

    // Add a new calculation to the session
    public void addCalculation(String figureType, TypeOfMetal material, double height, double base, double deformedHeight, double deformedBase) {
        calculations.add(new Calculation(figureType, material, height, base, deformedHeight, deformedBase));
    }

    // Get all calculations
    public List<Calculation> getCalculations() {
        return calculations;
    }
}