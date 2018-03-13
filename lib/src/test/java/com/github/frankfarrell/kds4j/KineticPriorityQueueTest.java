package com.github.frankfarrell.kds4j;

import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.assertj.core.data.Percentage;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by frankfarrell on 13/03/2018.
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
        assertThat(queueUnderTest.peek().element).isEqualTo("C");
    }

    @Test
    public void itCorrectlyEvaluatesIntersectionForLinearFunctions() {
        KineticPriorityQueue<String> queueUnderTest = new KineticPriorityQueue<>(0.0);

        BrentSolver brentSolver = new BrentSolver();

        Optional<Double> value = queueUnderTest.calculateIntersection(brentSolver, x -> 8 - x, x -> x / 2 + 5);
        assertThat(value.get()).isCloseTo(2.0, Percentage.withPercentage(0.001));

        queueUnderTest.advance(100.0);
        Optional<Double> valueWithoutSolution = queueUnderTest.calculateIntersection(brentSolver, x -> 8 - x, x -> x / 2 + 5);
        assertThat(valueWithoutSolution).isEmpty();
    }

    @Test
    public void itCorrectlyEvaluatesIntersectionForLinearAndQuadraticFunctions() {
        KineticPriorityQueue<String> queueUnderTest = new KineticPriorityQueue<>(0.0);

        BrentSolver brentSolver = new BrentSolver();

        Optional<Double> value = queueUnderTest.calculateIntersection(brentSolver, x -> 8 - x, x -> (x * x) / 2 - 4 * x);
        assertThat(value.get()).isCloseTo(8.0, Percentage.withPercentage(0.001));

        queueUnderTest.advance(100.0);
        Optional<Double> valueWithoutSolution = queueUnderTest.calculateIntersection(brentSolver, x -> 8 - x, x -> (x * x) / 2 - 4 * x);
        assertThat(valueWithoutSolution).isEmpty();
    }
}