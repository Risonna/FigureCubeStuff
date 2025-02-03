package com.risonna.stuff.figurecubestuff.model;

public enum TypeOfMetal {
    Cu(2), Al(3), Fe(4), Ni(5);

    private final int factor;

    TypeOfMetal(int factor) {
        this.factor = factor;
    }

    public int getFactor() {
        return factor;
    }
}