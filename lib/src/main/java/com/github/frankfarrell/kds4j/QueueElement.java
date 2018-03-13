package com.github.frankfarrell.kds4j;

import java.util.function.Function;

/**
 * Created by frankfarrell on 22/02/2018.
 *
 */
public class QueueElement<E> {


    final E element;
    final Function<Long, Long> function;

    /*
    Where function is f(time) -> priority
    Eg, pass in time and get back a priority
     */
    public QueueElement(E element, Function<Long, Long> function) {
        this.element = element;
        this.function = function;
    }
}
