package com.github.frankfarrell.kds4j;

import java.util.function.Function;

/**
 * General element for one dimensional kinetic data structures.
 *
 * @author frankfarrell
 */
public class OneDimensionalKineticElement<E> {


    /**
     * The element to store in the data structure.
     *
     * This must implement equals and hashcode correctly
     */
    public final E element;

    /**
     * Function f(t) = y over time, which determines its priority y.
     *
     * Function must be continuous to evaluate. If not, behaviour is ill-defined.
     *
     * @see <a href="http://commons.apache.org/proper/commons-math/userguide/analysis.html">Apache commons math root finding contraints</a>
     */
    public final Function<Double, Double> function;

    /*
    Where function is f(time) -> priority
    Eg, pass in time and get back a priority
     */
    public OneDimensionalKineticElement(E element, Function<Double, Double> function) {
        this.element = element;
        this.function = function;
    }
}
