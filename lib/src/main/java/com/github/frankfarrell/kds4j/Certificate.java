package com.github.frankfarrell.kds4j;

import java.util.Optional;

/**
 * Created by frankfarrell on 03/04/2018.
 *
 * Left has higher priority than right until expiryTime
 */
public class Certificate<E> {
    public final E left;//has priority
    public final E right;
    public final Optional<Double> expiryTime;

    public Certificate(final E left,
                        final E right,
                        final Double expiryTime) {
        this.left = left;
        this.right = right;
        this.expiryTime = Optional.of(expiryTime);
    }

    public Certificate(final E left,
                        final E right) {
        this.left = left;
        this.right = right;
        this.expiryTime = Optional.empty();
    }
}
