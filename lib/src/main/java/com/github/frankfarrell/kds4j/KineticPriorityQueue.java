package com.github.frankfarrell.kds4j;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.*;
import org.apache.commons.math3.exception.NoBracketingException;

import java.util.*;
import java.util.function.Function;

/**
 * Created by frankfarrell on 22/02/2018.
 */
public class KineticPriorityQueue<E> extends AbstractQueue<QueueElement<E>> {

    //Make all these configurable if the client has some particular use case.
    //Tweaking these can make the data structure more efficient in certain circumstances
    private static final Integer MAX_SOLVER_BRACKETING_ITERATIONS = 20;
    public static final double DEFAULT_RELATIVE_ACCURACY = 1.0e-12;
    public static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0e-8;
    public static final int DEFAULT_MAXIMAL_ORDER = 5;
    //If the upper and lower brackets have the same sign, we add exponents of the this value to the upper until they aren't, for SOLVER_BRACKETING_ITERATIONS
    public static final int BRACKETING_EXPONANT_BASE = 2;
    public static final int SOLVER_MAX_EVAL = 1000;
    private final LinkedList<QueueElement<E>> elements;
    private final PriorityQueue<Certificate> certificates;
    private Double time;
    private final BracketingNthOrderBrentSolver solver;

    public KineticPriorityQueue() {
        this.elements = new LinkedList<>();
        this.certificates = new PriorityQueue<>(Comparator.comparing(x -> x.expiryTime));
        this.time = 0.0;
        this.solver = getDefaultSolver();
    }

    public KineticPriorityQueue(final Double startTime) {
        this.elements = new LinkedList<>();
        this.certificates = new PriorityQueue<>(Comparator.comparing(x -> x.expiryTime));
        this.time = startTime;
        this.solver = getDefaultSolver();
    }


    public KineticPriorityQueue(final Double startTime,
                                final Collection<QueueElement<E>> elements) {
        this.elements = new LinkedList<>(elements);
        this.certificates = new PriorityQueue<>(Comparator.comparing(x -> x.expiryTime));
        this.time = startTime;
        this.solver = getDefaultSolver();
        calculatePriorities();
    }

    public KineticPriorityQueue(final Double startTime,
                                final Collection<QueueElement<E>> elements,
                                final BracketingNthOrderBrentSolver solver) {
        this.elements = new LinkedList<>(elements);
        this.certificates = new PriorityQueue<>(Comparator.comparing(x -> x.expiryTime));
        this.time = startTime;
        this.solver = solver;
        calculatePriorities();
    }

    private static BracketingNthOrderBrentSolver getDefaultSolver(){
        return new BracketingNthOrderBrentSolver(DEFAULT_RELATIVE_ACCURACY, DEFAULT_ABSOLUTE_ACCURACY, DEFAULT_MAXIMAL_ORDER);
    }

    public KineticPriorityQueue(final Double startTime,
                                final BracketingNthOrderBrentSolver solver) {
        this.elements = new LinkedList<>();
        this.certificates = new PriorityQueue<>(Comparator.comparing(x -> x.expiryTime));
        this.time = startTime;
        this.solver = solver;
        calculatePriorities();
    }


    /*
    Does this when ever it hits an expiring certificate
     */
    private void calculatePriorities() {

        final List<Certificate> invalidatedCertificates = new ArrayList<>();
        while(this.certificates.size() > 0 && this.certificates.peek().expiryTime < this.time){
            invalidatedCertificates.add(this.certificates.poll());
        }

        if(invalidatedCertificates.size() > 0) {

        }
        else{
            //Priority ordering hasn't changed
        }
        /*
        First we need to reorder at least the elements that are in the certificates,
        (and any elements that had certicates with those elements?)
        Compare all these elements to each other and create certificates
        Then do some sort of binary search on other elements and slot in new certificates.

         */
    }

    //Advances the system to time
    //Returns boolean indicating whether any priorities have changed
    public Boolean advance(final Double t) {

        if (t < time) {
            throw new RuntimeException("Cannot reverse time");
        } else if (t.equals(time)) {
            return false;
        } else {
            this.time = t;

            calculatePriorities();
            /*
            Check certificates
            If any change recalulate priorities and return true
            If not return false
             */
            return true;
        }
    }

    @Override
    public Iterator<QueueElement<E>> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean offer(final QueueElement<E> eQueueElement) {
        return false;
    }

    @Override
    public QueueElement<E> poll() {
        return null;
    }

    @Override
    public QueueElement<E> peek() {
        return null;
    }

    /*
    Left has higher priority than right until expiryTime
     */
    private class Certificate {
        final E left;
        final E right;
        final Double expiryTime;

        private Certificate(final E left,
                            final E right,
                            final Double expiryTime) {
            this.left = left;
            this.right = right;
            this.expiryTime = expiryTime;
        }
    }

    protected Optional<Double> calculateIntersection(final Function<Double, Double> f,
                                                     Function<Double, Double> g){
        return calculateIntersectionInner(f,g,time+1, 0);
    }

    protected Optional<Double> calculateIntersectionInner(final Function<Double, Double> f,
                                                          final Function<Double, Double> g,
                                                          final Double upperBound,
                                                          final Integer iteration){
        if(iteration >= MAX_SOLVER_BRACKETING_ITERATIONS){
            return Optional.empty();
        }

        final UnivariateFunction h = x -> f.apply(x) - g.apply(x);

        try{
            return Optional.of(solver.solve(SOLVER_MAX_EVAL, h, time, upperBound, AllowedSolution.LEFT_SIDE));
        }
        catch (NoBracketingException ex){
            return calculateIntersectionInner(f,g,time + Math.pow(BRACKETING_EXPONANT_BASE, iteration), iteration+1);
        }
        catch (Exception e){
            return Optional.empty();
        }
    }
}
