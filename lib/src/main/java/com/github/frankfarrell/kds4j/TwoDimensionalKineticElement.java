package com.github.frankfarrell.kds4j;

import java.util.function.Function;

public class TwoDimensionalKineticElement<E>  {


    /**
     * The element to store in the data structure.
     *
     * This must implement equals and hashcode correctly
     */
    public final E element;

    /**
     * Function f(t) = (x, y) over time
     *
     * Function must be continuous to evaluate. If not, behaviour is ill-defined.
     *
     * @see <a href="http://commons.apache.org/proper/commons-math/userguide/analysis.html">Apache commons math root finding contraints</a>
     */
    public final Function<Double, Double> xFunction;

    public final Function<Double, Double> yFunction;

    public TwoDimensionalKineticElement(final E element,
                                        final Function<Double, Double> xFunction,
                                        final Function<Double, Double> yFunction) {
        this.element = element;
        this.xFunction = xFunction;
        this.yFunction = yFunction;
    }
}
