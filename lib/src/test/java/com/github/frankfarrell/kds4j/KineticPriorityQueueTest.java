package com.github.frankfarrell.kds4j;

import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.assertj.core.data.Percentage;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by frankfarrell on 13/03/2018.
 *
 * Nice site for visualising these functions: https://www.desmos.com/calculator
 *
 * https://stackoverflow.com/questions/27836303/unable-to-solve-univariate-function-using-apache-commons-math-library
 *
 * https://en.wikipedia.org/wiki/Brent%27s_method
 */
public class KineticPriorityQueueTest {

    @Test
    public void itHasTheCorrectHeadElementAsTimeAdvances() {
        KineticPriorityQueue<String> queueUnderTest = new KineticPriorityQueue<String>(0.0);

        /*
        Priority will be the follow
        time:
        0  -> A, B, C
        2  -> B, A, C
        8  -> B, C, A
        10 -> C, B, A
         */

        queueUnderTest.add(new QueueElement<>("A", x -> 8 - x));
        queueUnderTest.add(new QueueElement<>("B", x -> x / 2 + 5));
        queueUnderTest.add(new QueueElement<>("C", x -> (x * x) / 2 - 4 * x));

        assertThat(queueUnderTest.peek().element).isEqualTo("A");

        assertThat(queueUnderTest.advance(3.0)).isTrue();
        assertThat(queueUnderTest.peek().element).isEqualTo("B");

        //Order does not change from 3->4
        assertThat(queueUnderTest.advance(4.0)).isFalse();

        assertThat(queueUnderTest.advance(9.0)).isTrue();
        assertThat(queueUnderTest.peek().element).isEqualTo("B");

        assertThat(queueUnderTest.advance(11.0)).isTrue();
        assertThat(queueUnderTest.poll().element).isEqualTo("C");
        assertThat(queueUnderTest.poll().element).isEqualTo("B");
        assertThat(queueUnderTest.poll().element).isEqualTo("A");
    }

    @Test
    public void itHasTheCorrectOrderingAtTheInitialTime() {

        /*
        Priority will be the follow
        time:
        0  -> A, B, C
        2  -> B, A, C
        8  -> B, C, A
        10 -> C, B, A
         */
        QueueElement<String> first = new QueueElement<>("A", x -> 8 - x);
        QueueElement<String> second = new QueueElement<>("B", x -> x / 2 + 5);
        QueueElement<String> third = new QueueElement<>("C", x -> (x * x) / 2 - 4 * x);

        KineticPriorityQueue<String> queueUnderTestAtZero = new KineticPriorityQueue<String>(0.0);

        queueUnderTestAtZero.add(first);
        queueUnderTestAtZero.add(second);
        queueUnderTestAtZero.add(third);

        assertThat(queueUnderTestAtZero.poll().element).isEqualTo("A");
        assertThat(queueUnderTestAtZero.poll().element).isEqualTo("B");
        assertThat(queueUnderTestAtZero.poll().element).isEqualTo("C");

        KineticPriorityQueue<String> queueUnderTestAt3 = new KineticPriorityQueue<String>(3.0);

        queueUnderTestAt3.add(first);
        queueUnderTestAt3.add(second);
        queueUnderTestAt3.add(third);

        assertThat(queueUnderTestAt3.poll().element).isEqualTo("B");
        assertThat(queueUnderTestAt3.poll().element).isEqualTo("A");
        assertThat(queueUnderTestAt3.poll().element).isEqualTo("C");

        KineticPriorityQueue<String> queueUnderTestAt9 = new KineticPriorityQueue<String>(9.0);

        queueUnderTestAt9.add(first);
        queueUnderTestAt9.add(second);
        queueUnderTestAt9.add(third);

        assertThat(queueUnderTestAt9.poll().element).isEqualTo("B");
        assertThat(queueUnderTestAt9.poll().element).isEqualTo("C");
        assertThat(queueUnderTestAt9.poll().element).isEqualTo("A");

        KineticPriorityQueue<String> queueUnderTestAt10 = new KineticPriorityQueue<String>(10.0);

        queueUnderTestAt10.add(first);
        queueUnderTestAt10.add(second);
        queueUnderTestAt10.add(third);

        assertThat(queueUnderTestAt10.poll().element).isEqualTo("C");
        assertThat(queueUnderTestAt10.poll().element).isEqualTo("B");
        assertThat(queueUnderTestAt10.poll().element).isEqualTo("A");

    }

}