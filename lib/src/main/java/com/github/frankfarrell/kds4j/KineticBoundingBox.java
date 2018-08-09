package com.github.frankfarrell.kds4j;

/**
 * A naive implementation of a Kinetic Bounding Box
 *
 * Maintains two KineticSortedLists to keep track of top, bottom, left and right.
 * The bounding box at any time is determined by the head and tails of these queues.
 */
public class KineticBoundingBox<E> implements KineticDataStructure{

    private Double time;

    private final KineticSortedList<E> xQueue;
    private final KineticSortedList<E> yQueue;

    public KineticBoundingBox() {
        this.xQueue = new KineticSortedList<>();
        this.yQueue = new KineticSortedList<>();
    }

    @Override
    public Boolean advance(Double t) {
        if (t < time) {
            throw new RuntimeException("Cannot reverse time");
        } else if (t.equals(time)) {
            return false;
        } else {
            this.time = t;
            return this.xQueue.advance(t) && this.yQueue.advance(t);
        }
    }

    public boolean add(final TwoDimensionalKineticElement<E> element) {
        return this.xQueue.add(new OneDimensionalKineticElement<>(element.element, element.xFunction)) && this.yQueue.add(new OneDimensionalKineticElement<>(element.element, element.yFunction));
    }

    public BoundingBox getBoundingBox(){
        final Double top = yQueue.get(0).function.apply(time);
        final Double bottom = yQueue.get(yQueue.size() -1 ).function.apply(time);
        final Double left = xQueue.get(0).function.apply(time);
        final Double right = xQueue.get(xQueue.size() -1 ).function.apply(time);
        return new BoundingBox(top, bottom, left, right);
    }

}
