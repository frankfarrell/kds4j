package com.github.frankfarrell.kds4j;

import org.apache.commons.math3.analysis.solvers.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by frankfarrell on 22/02/2018.
 */
public class KineticPriorityQueue<E> extends AbstractQueue<KineticElement<E>> implements KineticDataStructure {

    private final KineticSortedList<E> backingKineticSortedList;

    //TODO Make a single constructor
    public KineticPriorityQueue() {
        this.backingKineticSortedList = new KineticSortedList<>();
    }

    public KineticPriorityQueue(final Double startTime) {
        this.backingKineticSortedList = new KineticSortedList<>(startTime);
    }


    public KineticPriorityQueue(final Double startTime,
                                final Collection<KineticElement<E>> elements) {
        this.backingKineticSortedList = new KineticSortedList<>(startTime, elements);
    }

    public KineticPriorityQueue(final Double startTime,
                                final Collection<KineticElement<E>> elements,
                                final BracketingNthOrderBrentSolver solver) {
        this.backingKineticSortedList = new KineticSortedList<>(startTime, elements, solver);
    }



    public KineticPriorityQueue(final Double startTime,
                                 final BracketingNthOrderBrentSolver solver) {

        this.backingKineticSortedList = new KineticSortedList<>(startTime, solver);
    }

    //Advances the system to time
    //Returns boolean indicating whether any priorities have changed
    public Boolean advance(final Double t) {
        return this.backingKineticSortedList.advance(t);
    }

    @Deprecated
    @Override
    public Iterator<KineticElement<E>> iterator() {
        throw new NotImplementedException();
    }

    @Override
    public int size() {
        return this.backingKineticSortedList.size();
    }

    @Override
    public boolean offer(final KineticElement<E> element) {
        return this.backingKineticSortedList.add(element);
    }

    @Override
    public KineticElement<E> poll() {
        return this.backingKineticSortedList.remove(0);
    }

    @Override
    public KineticElement<E> peek() {
        //Returns first element if it exists from ArrayList without deletion
        return this.backingKineticSortedList.get(0);
    }
}
