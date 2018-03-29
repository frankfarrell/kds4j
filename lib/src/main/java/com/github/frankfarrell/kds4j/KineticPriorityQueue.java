package com.github.frankfarrell.kds4j;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.*;
import org.apache.commons.math3.exception.NoBracketingException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    /*
    These two ArrayLists store elements and the relevant certificatesPriorityQueue. Eg, a cert with itself and i+1
     */
    private final ArrayList<QueueElement<E>> elements;
    private final ArrayList<Certificate> elementCertificates;

    /*
    Prioirity queue which stores certificatesPriorityQueue where priority is time of expriry
     */
    private final PriorityQueue<Certificate> certificatesPriorityQueue;
    private Double time;
    private final BracketingNthOrderBrentSolver solver;

    //TODO Make a single constructor
    public KineticPriorityQueue() {
        this.time = 0.0;
        this.solver = getDefaultSolver();

        this.elements = new ArrayList<>();

        this.elementCertificates = new ArrayList<>();
        this.certificatesPriorityQueue = new PriorityQueue<>(getCertificateComparator());

    }

    public KineticPriorityQueue(final Double startTime) {
        this.time = startTime;
        this.solver = getDefaultSolver();

        this.elements = new ArrayList<>();
        this.elementCertificates = new ArrayList<>();

        this.certificatesPriorityQueue = new PriorityQueue<>(getCertificateComparator());
    }


    public KineticPriorityQueue(final Double startTime,
                                final Collection<QueueElement<E>> elements) {
        this.time = startTime;
        this.solver = getDefaultSolver();

        this.elements = getTotalOrdering(elements, startTime);
        this.elementCertificates = getElementCertificates(this.elements);

        this.certificatesPriorityQueue = new PriorityQueue<>(getCertificateComparator());
        this.certificatesPriorityQueue.addAll(this.elementCertificates);

    }

    public KineticPriorityQueue(final Double startTime,
                                final Collection<QueueElement<E>> elements,
                                final BracketingNthOrderBrentSolver solver) {
        this.time = startTime;
        this.solver = solver;

        this.elements = getTotalOrdering(elements, startTime);
        this.elementCertificates = getElementCertificates(this.elements);

        this.certificatesPriorityQueue = new PriorityQueue<>(getCertificateComparator());
        this.certificatesPriorityQueue.addAll(this.elementCertificates);
    }



    public KineticPriorityQueue(final Double startTime,
                                 final BracketingNthOrderBrentSolver solver) {
        this.elements = new ArrayList<>();
        this.elementCertificates = new ArrayList<>();
        this.certificatesPriorityQueue = new PriorityQueue<>(getCertificateComparator());
        this.time = startTime;
        this.solver = solver;

    }

    protected ArrayList<QueueElement<E>> getTotalOrdering(final Collection<QueueElement<E>> elements, final Double time) {
        return elements.stream().sorted(Comparator.comparing(x -> x.function.apply(time))).collect(Collectors.toCollection(ArrayList::new));
    }

    private static BracketingNthOrderBrentSolver getDefaultSolver(){
        return new BracketingNthOrderBrentSolver(DEFAULT_RELATIVE_ACCURACY, DEFAULT_ABSOLUTE_ACCURACY, DEFAULT_MAXIMAL_ORDER);
    }

    private Comparator<Certificate> getCertificateComparator() {
        return (left, right) -> {
            if (left.expiryTime.isPresent() && right.expiryTime.isPresent()) {
                return left.expiryTime.get().compareTo(right.expiryTime.get());
            } else if (left.expiryTime.isPresent()) {
                return 1;
            } else if (right.expiryTime.isPresent()) {
                return -1;
            } else {
                return 0;
            }
        };
    }

    /*
    Returns a ArrayList of certificatesPriorityQueue with the same ordering as list passed.
    It does not give the last element a certificate
     */
    private ArrayList<Certificate> getElementCertificates(ArrayList<QueueElement<E>> elements) {
        return IntStream.range(0, elements.size() -1)
                .mapToObj(i -> {
                    final QueueElement<E> left = elements.get(i);
                    final QueueElement<E> right = elements.get(i +1);
                    return calculateIntersection(left.function, right.function)
                            .map(value ->  new Certificate(left.element, right.element, value))
                            .orElse(new Certificate(left.element, right.element));
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /*
    Does this when ever it hits an expiring certificate
     */
    private boolean reCalculatePriorities() {

        final List<Certificate> invalidatedCertificates = new ArrayList<>();
        while(this.certificatesPriorityQueue.size() > 0 &&
                this.certificatesPriorityQueue.peek().expiryTime.isPresent() &&
                this.certificatesPriorityQueue.peek().expiryTime.get() < this.time){
            invalidatedCertificates.add(this.certificatesPriorityQueue.poll());
        }

        if(invalidatedCertificates.size() > 0) {

            /*
            For each cert
            1) Get the relevant elements and certs from the 2 ArrayLists
            2) Do swapping as necessary and create certs
            3) Will need to create a new cert for i-1 too, since i always has a cert in relation to i+1
             */

            for(int i=0; i<invalidatedCertificates.size() && i+1 < elements.size(); i++){

                final QueueElement<E> newLeft = elements.get(i+1);
                final QueueElement<E> newRight = elements.get(i);
                final Certificate newCertificate =
                        calculateIntersection(newLeft.function, newRight.function)
                                .map(value ->  new Certificate(newLeft.element, newRight.element, value))
                                .orElse(new Certificate(newLeft.element, newRight.element));

                //The ordering of the certs should match the ordering of the elements?
                Collections.swap(elements, i, i+1);
                certificatesPriorityQueue.add(newCertificate);
            }
            return true;
        }
        else{
            return false;
            //Priority ordering hasn't changed
        }
        /*
        First we need to reorder at least the elements that are in the certificatesPriorityQueue,
        (and any elements that had certicates with those elements?)
        Compare all these elements to each other and create certificatesPriorityQueue
        Then do some sort of binary search on other elements and slot in new certificatesPriorityQueue.

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
            /*
            Check certificatesPriorityQueue
            If any change recalulate priorities and return true
            If not return false
             */
            return reCalculatePriorities();
        }
    }

    @Override
    public Iterator<QueueElement<E>> iterator() {

        //This is tricky
        //Does this call poll or peek?
        return null;
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean offer(final QueueElement<E> element) {
        /*
        Do binary search on ArrayList to determine where to insert it.
        We then create a new cert for i-1 -> i and i -> i+1
        We remove the cert between i-1 -> i+1 from ArrayList and also from priority queue
        We insert this new cert into ArrayList and priority queue
         */

        /*
        If we have a match its the index
        If not its -(insertion point) -1
        So if i >= 0 element was definitely found
         */
        final Integer binarySearchResult = Collections.binarySearch(elements,
                element,
                (x, y) -> {
                    Double xValue = x.function.apply(this.time);
                    Double yValue = y.function.apply(this.time);
                    if (xValue >yValue){
                        return -1;
                    }
                    else if (yValue > xValue){
                        return 1;
                    }
                    else return 0;
                });
        final Integer indexProper = binarySearchResult>=0? binarySearchResult: (binarySearchResult+1)*-1;

        elements.add(indexProper, element);

        //If its the highest priority element, it is not the right (lower) element in any certficate
        if(indexProper > 0){
            final QueueElement<E> higher = elements.get(indexProper -1);
            final Certificate higherCertificate = calculateIntersection(higher.function, element.function)
                    .map(value ->  new Certificate(higher.element, element.element, value))
                    .orElse(new Certificate(higher.element, element.element));

            certificatesPriorityQueue.add(higherCertificate);
            if(elementCertificates.size() < indexProper){
                elementCertificates.add(higherCertificate);
            }
            else{
                elementCertificates.set(indexProper -1, higherCertificate);
            }
        }

        //Element certificates is always of size one less than elements
        if(elements.size()-1 > indexProper){
            final QueueElement<E> lower = elements.get(indexProper  + 1);
            final Certificate lowerCertificate = calculateIntersection(element.function, lower.function)
                    .map(value ->  new Certificate(element.element, lower.element, value))
                    .orElse(new Certificate(element.element, lower.element));
            certificatesPriorityQueue.add(lowerCertificate);
            elementCertificates.add(indexProper, lowerCertificate);
        }

        return true;
    }

    @Override
    public QueueElement<E> poll() {

        //Returns and deletes the first element in the ArrayList.
        //also deletes from certs ArrayList
        final QueueElement<E> firstElement = elements.remove(0);
        if(elementCertificates.size() > 0){
            final Certificate firstCertificate = elementCertificates.remove(0);
            elementCertificates.remove(firstCertificate);
        }

        return firstElement;
    }

    @Override
    public QueueElement<E> peek() {
        //Returns first element if it exists from ArrayList without deletion
        return elements.get(0);
    }

    /*
    Left has higher priority than right until expiryTime
     */
    private class Certificate {
        final E left;//has priority
        final E right;
        final Optional<Double> expiryTime;

        private Certificate(final E left,
                            final E right,
                            final Double expiryTime) {
            this.left = left;
            this.right = right;
            this.expiryTime = Optional.of(expiryTime);
        }

        private Certificate(final E left,
                            final E right) {
            this.left = left;
            this.right = right;
            this.expiryTime = Optional.empty();
        }
    }

    private class QueueElemenetCertificate{
        final QueueElement<E> queueElement;
        final Certificate certificate;

        private QueueElemenetCertificate(QueueElement<E> queueElement, Certificate certificate) {
            this.queueElement = queueElement;
            this.certificate = certificate;
        }


    }

    protected Optional<Double> calculateIntersection(final Function<Double, Double> f,
                                                     final Function<Double, Double> g){
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
