package com.github.frankfarrell.kds4j;

import java.util.function.Function;

/**
 * Created by frankfarrell on 22/02/2018.
 *
 */
public class KineticElement<E> {


    final E element;
    final Function<Double, Double> function;

    /*
    Where function is f(time) -> priority
    Eg, pass in time and get back a priority
     */
    public KineticElement(E element, Function<Double, Double> function) {
        this.element = element;
        this.function = function;
    }
}
