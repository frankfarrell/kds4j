package com.github.frankfarrell.kds4j;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by frankfarrell on 03/04/2018.
 */
public class KineticSortedListTest {

    @Test
    public void itHasTheCorrectOrderingAsTimeAdvances() {

        KineticSortedList<String> listUnderTest = new KineticSortedList<String>(0.0);

        /*
        Priority will be the follow
        time:
        0  -> A, B, C
        2  -> B, A, C
        8  -> B, C, A
        10 -> C, B, A
         */

        listUnderTest.add(new KineticElement<>("A", x -> 8 - x));
        listUnderTest.add(new KineticElement<>("B", x -> x / 2 + 5));
        listUnderTest.add(new KineticElement<>("C", x -> (x * x) / 2 - 4 * x));

        assertThat(listUnderTest.get(0).element).isEqualTo("A");
        assertThat(listUnderTest.get(1).element).isEqualTo("B");
        assertThat(listUnderTest.get(2).element).isEqualTo("C");

        assertThat(listUnderTest.advance(3.0)).isTrue();
        assertThat(listUnderTest.get(0).element).isEqualTo("B");
        assertThat(listUnderTest.get(1).element).isEqualTo("A");
        assertThat(listUnderTest.get(2).element).isEqualTo("C");

        //Order does not change from 3->4
        assertThat(listUnderTest.advance(4.0)).isFalse();

        assertThat(listUnderTest.advance(9.0)).isTrue();
        assertThat(listUnderTest.get(0).element).isEqualTo("B");
        assertThat(listUnderTest.get(1).element).isEqualTo("C");
        assertThat(listUnderTest.get(2).element).isEqualTo("A");

        assertThat(listUnderTest.advance(11.0)).isTrue();
        assertThat(listUnderTest.get(0).element).isEqualTo("C");
        assertThat(listUnderTest.get(1).element).isEqualTo("B");
        assertThat(listUnderTest.get(2).element).isEqualTo("A");
    }

    @Test
    public void itHasTheCorrectOrderingWhenItemAreRemove() {

        KineticSortedList<String> listUnderTest = new KineticSortedList<String>(0.0);

        /*
        Priority will be the follow
        time:
        0  -> A, B, C
        2  -> B, A, C
        8  -> B, C, A
        10 -> C, B, A
         */

        KineticElement<String> first = new KineticElement<>("A", x -> 8 - x);
        KineticElement<String> second = new KineticElement<>("B", x -> x / 2 + 5);
        KineticElement<String> third = new KineticElement<>("C", x -> (x * x) / 2 - 4 * x);

        listUnderTest.add(new KineticElement<>("A", x -> 8 - x));
        listUnderTest.add(new KineticElement<>("B", x -> x / 2 + 5));
        listUnderTest.add(new KineticElement<>("C", x -> (x * x) / 2 - 4 * x));

        assertThat(listUnderTest.advance(3.0)).isTrue();
        assertThat(listUnderTest.remove(1));

        assertThat(listUnderTest.get(0).element).isEqualTo("B");
        assertThat(listUnderTest.get(1).element).isEqualTo("C");

        assertThat(listUnderTest.advance(11.0)).isTrue();
        assertThat(listUnderTest.get(0).element).isEqualTo("C");
        assertThat(listUnderTest.get(1).element).isEqualTo("B");
    }
}
