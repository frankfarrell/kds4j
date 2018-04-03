package com.github.frankfarrell.kds4j;

import java.util.AbstractList;

/**
 * Created by ffarrell on 30/03/2018.
 */
public class KineticSortedList<E> extends AbstractList<KineticElement<E>> implements KineticDataStructure{



    @Override
    public KineticElement<E> get(final int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean add(final KineticElement<E> kineticElement){
        return true;
    }

    @Override
    public Boolean advance(Double t) {
        return null;
    }
}
