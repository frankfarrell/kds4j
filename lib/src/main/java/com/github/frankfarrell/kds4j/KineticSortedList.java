package com.github.frankfarrell.kds4j;

import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of a kinetic sorted list.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Kinetic_sorted_list">Wikipedia entry</a>
 *
 * @author frankfarrell
 * @since 0.0.1
 */
public class KineticSortedList<E> extends AbstractList<OneDimensionalKineticElement<E>> implements KineticDataStructure {

    @Override
    public OneDimensionalKineticElement<E> get(final int index) {
        return this.elements.get(index);
    }

    /*
    These two ArrayLists store elements and the relevant certificatesPriorityQueue. Eg, a cert with itself and i+1
     */
    private final ArrayList<OneDimensionalKineticElement<E>> elements;
    private final ArrayList<Certificate<E>> elementCertificates;

    /*
    Prioirity queue which stores certificatesPriorityQueue where priority is time of expriry
     */
    private final PriorityQueue<Certificate<E>> certificatesPriorityQueue;
    private Double time;

    private final OneDimensionalKDSSolver solver;

    //TODO Make a single constructor
    public KineticSortedList() {
        this.time = 0.0;

        this.solver = new OneDimensionalKDSSolver();

        this.elements = new ArrayList<>();

        this.elementCertificates = new ArrayList<>();
        this.certificatesPriorityQueue = new PriorityQueue<>(getCertificateComparator());

    }

    public KineticSortedList(final Double startTime) {
        this.time = startTime;

        this.solver = new OneDimensionalKDSSolver();

        this.elements = new ArrayList<>();
        this.elementCertificates = new ArrayList<>();

        this.certificatesPriorityQueue = new PriorityQueue<>(getCertificateComparator());
    }


    public KineticSortedList(final Double startTime,
                                final Collection<OneDimensionalKineticElement<E>> elements) {
        this.time = startTime;

        this.solver = new OneDimensionalKDSSolver();

        this.elements = getTotalOrdering(elements, startTime);
        this.elementCertificates = getElementCertificates(this.elements);

        this.certificatesPriorityQueue = new PriorityQueue<>(getCertificateComparator());
        this.certificatesPriorityQueue.addAll(this.elementCertificates);

    }

    public KineticSortedList(final Double startTime,
                                final Collection<OneDimensionalKineticElement<E>> elements,
                                final BracketingNthOrderBrentSolver solver) {
        this.time = startTime;
        this.solver = new OneDimensionalKDSSolver(solver);

        this.elements = getTotalOrdering(elements, startTime);
        this.elementCertificates = getElementCertificates(this.elements);

        this.certificatesPriorityQueue = new PriorityQueue<>(getCertificateComparator());
        this.certificatesPriorityQueue.addAll(this.elementCertificates);
    }



    public KineticSortedList(final Double startTime,
                                final BracketingNthOrderBrentSolver solver) {

        this.time = startTime;

        this.solver = new OneDimensionalKDSSolver(solver);

        this.elements = new ArrayList<>();
        this.elementCertificates = new ArrayList<>();
        this.certificatesPriorityQueue = new PriorityQueue<>(getCertificateComparator());
    }

    public Boolean advance(final Double t) {

        if (t < time) {
            throw new RuntimeException("Cannot reverse time");
        } else if (t.equals(time)) {
            return false;
        } else {
            this.time = t;

            return reCalculatePriorities();
        }
    }

    /**
     * @return An iterator on unmodifiable list snapshot of the ordered elements
     */
    @Override
    public Iterator<OneDimensionalKineticElement<E>> iterator() {
        return Collections.unmodifiableList(elements).iterator();
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean add(final OneDimensionalKineticElement<E> element) {
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
            final OneDimensionalKineticElement<E> higher = elements.get(indexProper -1);
            final Certificate<E> higherCertificate = solver.calculateIntersection(higher.function, element.function, this.time)
                    .map(value ->  new Certificate<E>(higher.element, element.element, value))
                    .orElse(new Certificate<E>(higher.element, element.element));

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
            final OneDimensionalKineticElement<E> lower = elements.get(indexProper  + 1);
            final Certificate<E> lowerCertificate = solver.calculateIntersection(element.function, lower.function, this.time)
                    .map(value ->  new Certificate<E>(element.element, lower.element, value))
                    .orElse(new Certificate<E>(element.element, lower.element));
            certificatesPriorityQueue.add(lowerCertificate);
            elementCertificates.add(indexProper, lowerCertificate);
        }

        return true;
    }

    @Override
    public OneDimensionalKineticElement<E> remove(final int index) {

        //Returns and deletes the first element in the ArrayList.
        //also deletes from certs ArrayList
        final OneDimensionalKineticElement<E> element = elements.remove(index);
        if(elementCertificates.size() > 0){
            final Certificate<E> cert = elementCertificates.remove(index);
            elementCertificates.remove(cert);
            certificatesPriorityQueue.remove(cert);
        }

        if(index>0){
            //we need to create a new cert for i-1 pointing to index

            final OneDimensionalKineticElement<E> left = elements.get(index -1);
            final OneDimensionalKineticElement<E> right = elements.get(index);
            final Certificate<E> newCertificate =
                    solver.calculateIntersection(left.function, right.function, this.time)
                            .map(value ->  new Certificate<E>(left.element, right.element, value))
                            .orElse(new Certificate<E>(left.element, right.element));
            final Certificate<E> redundantCertificate = elementCertificates.set(index - 1, newCertificate);

            certificatesPriorityQueue.remove(redundantCertificate);
            certificatesPriorityQueue.add(newCertificate);
        }

        return element;
    }

    protected ArrayList<OneDimensionalKineticElement<E>> getTotalOrdering(final Collection<OneDimensionalKineticElement<E>> elements, final Double time) {
        return elements.stream().sorted((x, y) -> {
            final Double xValue = x.function.apply(time);
            final Double yValue = y.function.apply(time);
            if (xValue >yValue){
                return -1;
            }
            else if (yValue > xValue){
                return 1;
            }
            else return 0;
        }).collect(Collectors.toCollection(ArrayList::new));
    }



    private Comparator<Certificate<E>> getCertificateComparator() {
        return (left, right) -> {
            if (left.expiryTime.isPresent() && right.expiryTime.isPresent()) {
                return left.expiryTime.get().compareTo(right.expiryTime.get());
            } else if (left.expiryTime.isPresent()) {
                return -1;
            } else if (right.expiryTime.isPresent()) {
                return 1;
            } else {
                return 0;
            }
        };
    }

    /*
    Returns a ArrayList of certificatesPriorityQueue with the same ordering as list passed.
    It does not give the last element a certificate
     */
    private ArrayList<Certificate<E>> getElementCertificates(ArrayList<OneDimensionalKineticElement<E>> elements) {
        return IntStream.range(0, elements.size() -1)
                .mapToObj(i -> {
                    final OneDimensionalKineticElement<E> left = elements.get(i);
                    final OneDimensionalKineticElement<E> right = elements.get(i +1);
                    return solver.calculateIntersection(left.function, right.function, this.time)
                            .map(value ->  new Certificate<E>(left.element, right.element, value))
                            .orElse(new Certificate<E>(left.element, right.element));
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /*
    Does this when ever it hits an expiring certificate
     */
    private boolean reCalculatePriorities() {

        final List<Certificate> invalidatedCertificates = new ArrayList<>();
        //noinspection ConstantConditions
        while(this.certificatesPriorityQueue.size() > 0 &&
                Objects.requireNonNull(this.certificatesPriorityQueue.peek()).expiryTime.isPresent() &&
                Objects.requireNonNull(this.certificatesPriorityQueue.peek()).expiryTime.get() < this.time){
            invalidatedCertificates.add(this.certificatesPriorityQueue.poll());
        }

        if(invalidatedCertificates.size() > 0) {

            final SortedSet<Integer> elementIndices =
                    invalidatedCertificates.stream()
                            .map(elementCertificates::indexOf)
                            //Filter out certficates of non-contiguous elements that have been removed already from the elementsCertificates list
                            .filter(index -> index >=0)
                            .collect(Collectors.toCollection(TreeSet::new));

            final SortedSet<Integer> elementIndicesPlusOneTreeSet = new TreeSet<>();
            for(Integer index : elementIndices){
                if(index > 0) {
                    elementIndicesPlusOneTreeSet.add(index -1);
                }
                elementIndicesPlusOneTreeSet.add(index);
                elementIndicesPlusOneTreeSet.add(index +1);
            }

            final List<Integer> elementIndicesPlusOne = new ArrayList<>(elementIndicesPlusOneTreeSet);

            final List<OneDimensionalKineticElement<E>> invalidatedElements = elementIndicesPlusOne.stream().map(elements::get).collect(Collectors.toList());

            final List<OneDimensionalKineticElement<E>> sortedElements = getTotalOrdering(invalidatedElements, this.time);

            //Zip indices and the sorted elements into the elements and certificates lists
            final List<Certificate<E>> newCertificates =
                    IntStream.range(0, sortedElements.size())
                            //This is a way to reverse the order of the intstream
                            .boxed()
                            .collect(Collector.of(
                                    ArrayDeque<Integer>::new,
                                    ArrayDeque::addFirst,
                                    (ArrayDeque<Integer> d1, ArrayDeque<Integer> d2) -> { d2.addAll(d1); return d2; }))
                            .stream()
                            .map(i -> {
                                //Im not sure how this could happen though its the last element, it wont have a certificate
                                if(elementIndicesPlusOne.get(i) > elements.size() -1){
                                    elements.add(sortedElements.get(i));
                                    return Optional.<Certificate<E>>empty();
                                }
                                else{

                                    final OneDimensionalKineticElement<E> left = sortedElements.get(i);
                                    final Integer indexOfLeft = elementIndicesPlusOne.get(i);
                                    final OneDimensionalKineticElement<E> potentialRight = elements.set(indexOfLeft, left);

                                    //Its the last element now, it has no certificate
                                    if (indexOfLeft +1 >= elements.size()) {
                                        return Optional.<Certificate<E>>empty();
                                    }

                                    final Certificate<E> newCertificate;
                                    //Its the last of the sorted elements no need to swap
                                    if(i.equals(sortedElements.size()-1)){
                                        final OneDimensionalKineticElement<E> right = elements.get(indexOfLeft +1);
                                        newCertificate =
                                                solver.calculateIntersection(left.function, right.function, this.time)
                                                        .map(value ->  new Certificate<E>(left.element, right.element, value))
                                                        .orElse(new Certificate<E>(left.element, right.element));
                                    }
                                    else{
                                        final OneDimensionalKineticElement<E> actualRight;

                                        //If there was no change we do not need to reorder
                                        if(!potentialRight.equals(left)){
                                            elements.set(indexOfLeft +1 , potentialRight);
                                            actualRight= potentialRight;
                                        }
                                        else {
                                            actualRight = elements.get(indexOfLeft +1);
                                        }

                                        newCertificate =
                                                solver.calculateIntersection(left.function, actualRight.function, this.time)
                                                        .map(value ->  new Certificate<E>(left.element, actualRight.element, value))
                                                        .orElse(new Certificate<E>(left.element, actualRight.element));
                                    }

                                    if(elementCertificates.size() < elementIndicesPlusOne.get(i)){
                                        elementCertificates.add(newCertificate);
                                    }
                                    else{
                                        elementCertificates.set(elementIndicesPlusOne.get(i), newCertificate);
                                    }

                                    return Optional.of(newCertificate);
                                }
                            })
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList());

            certificatesPriorityQueue.addAll(newCertificates);
            return true;
        }
        else{
            return false;
            //Priority ordering hasn't changed
        }
    }

}
