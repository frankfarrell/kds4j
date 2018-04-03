package com.github.frankfarrell.kds4j;

import java.util.Optional;

/**
 * A certificate for use in Kinetic Data Structures
 *
 * The certificate is valid until expiryTime, which is an Optional Double.
 * If expiryTime is not present, this means the certficate is valid indefinitely.
 * What that means depends on how the certificate was created.
 *  1) If it was determined through analysis that left is always greater than right, then it is valid forever
 *  2) If it was determined computationally, then it is valid until whatever upper bound was used in the computation.
 *
 * Left has higher priority than Right until expiryTime
 *
 * @author frankfarrell
 * @since 0.0.1
 */
public class Certificate<E> {

    /**
     * Left element in certificate, has priority
     */
    public final E left;

    /**
     * Right element in certificate, does not have priority
     */
    public final E right;

    /**
     * When certficate expires. If not present, certificate is valid indefinitely
     */
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
