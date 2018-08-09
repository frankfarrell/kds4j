package com.github.frankfarrell.kds4j;

public class BoundingBox {


    public final Double top;
    public final Double bottom;
    public final Double left;
    public final Double right;

    public BoundingBox(final Double top,
                       final Double bottom,
                       final Double left,
                       final Double right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }
}
