package com.github.frankfarrell.kds4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by frankfarrell on 22/02/2018.
 */
public class KineticPriorityQueue<E> extends AbstractQueue<QueueElement<E>> {

    private final LinkedList<QueueElement<E>> elements;
    private final PriorityQueue<Certificate> certificates;
    private final AtomicLong time;

    public KineticPriorityQueue() {
        this.elements = new LinkedList<>();
        this.certificates = new PriorityQueue<>(Comparator.comparing(x -> x.expiryTime));
        this.time = new AtomicLong(0L);
    }

    public KineticPriorityQueue(final Long startTime) {
        this.elements = new LinkedList<>();
        this.certificates = new PriorityQueue<>(Comparator.comparing(x -> x.expiryTime));
        this.time = new AtomicLong(startTime);
    }


    public KineticPriorityQueue(final Long startTime,
                                final Collection<QueueElement<E>> elements) {
        this.elements = new LinkedList<>(elements);
        this.certificates = new PriorityQueue<>();
        this.time = new AtomicLong(startTime);
        calculatePriorities();
    }

    /*
    Does this when ever it hits an expiring certificate
     */
    private void calculatePriorities() {

        final List<Certificate> invalidatedCertificates = new ArrayList<>();
        while(this.certificates.peek().expiryTime < this.time.get()){
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
    public Boolean advance(final Long t){
        this.time.set(t);


        calculatePriorities();
        /*
        Check certificates
        If any change recalulate priorities and return true
        If not return false
         */
        return true;
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
    public boolean offer(QueueElement<E> eQueueElement) {
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
        final Long expiryTime;

        private Certificate(E left, E right, Long expiryTime) {
            this.left = left;
            this.right = right;
            this.expiryTime = expiryTime;
        }
    }
}
