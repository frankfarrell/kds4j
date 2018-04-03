package com.github.frankfarrell.kds4j;

/**
 * General interface for all kinetic data structures.
 *
 * The only method currently is advance(Double) which advances time to the specified Double.
 *
 * @author frankfarrell
 */
public interface KineticDataStructure {

    /**
     * Advances the system to time
     * Returns boolean indicating whether any priorities have changed
     *
     * @param t Current time
     *
     * @return Boolean indicating if any elements were reordered
     */
    Boolean advance(final Double t);

}
