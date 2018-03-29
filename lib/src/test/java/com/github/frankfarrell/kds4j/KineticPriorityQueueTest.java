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
        assertThat(queueUnderTest.peek().element).isEqualTo("C");
    }

    @Test
    public void itHasTheCorrectOrderingAtTheInitialTime() {
        KineticPriorityQueue<String> queueUnderTest = new KineticPriorityQueue<String>(9.0);

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

        assertThat(queueUnderTest.poll().element).isEqualTo("B");
        assertThat(queueUnderTest.poll().element).isEqualTo("C");
        assertThat(queueUnderTest.poll().element).isEqualTo("A");

    }
    @Test
    public void itCorrectlyEvaluatesIntersectionForLinearFunctions() {
        KineticPriorityQueue<String> queueUnderTest = new KineticPriorityQueue<>(0.0);

        BrentSolver brentSolver = new BrentSolver();

        Optional<Double> value = queueUnderTest.calculateIntersection(x -> 8 - x, x -> x / 2 + 5);
        assertThat(value.get()).isCloseTo(2.0, Percentage.withPercentage(0.001));

        queueUnderTest.advance(100.0);
        Optional<Double> valueWithoutSolution = queueUnderTest.calculateIntersection(x -> 8 - x, x -> x / 2 + 5);
        assertThat(valueWithoutSolution).isEmpty();
    }

    @Test
    public void itCorrectlyEvaluatesIntersectionForLinearAndQuadraticFunctions() {
        KineticPriorityQueue<String> queueUnderTest = new KineticPriorityQueue<>(0.0);

        BrentSolver brentSolver = new BrentSolver();

        Optional<Double> value = queueUnderTest.calculateIntersection(x -> 8 - x, x -> (x * x) / 2 - 4 * x);
        assertThat(value.get()).isCloseTo(8.0, Percentage.withPercentage(0.001));

        queueUnderTest.advance(100.0);
        Optional<Double> valueWithoutSolution = queueUnderTest.calculateIntersection(x -> 8 - x, x -> (x * x) / 2 - 4 * x);
        assertThat(valueWithoutSolution).isEmpty();
    }

    @Test
    public void itCorrectlyEvaluatesIntersectionForLinearAndGeometricsFunctions() {
        KineticPriorityQueue<String> queueUnderTest = new KineticPriorityQueue<>(1.0);

        Optional<Double> value = queueUnderTest.calculateIntersection(x -> x, x -> (x + 3) * Math.cos(5-x));
        assertThat(value.get()).isCloseTo(4.04, Percentage.withPercentage(0.05));

        queueUnderTest.advance(5.0);
        Optional<Double> secondValue = queueUnderTest.calculateIntersection(x -> x, x -> (x + 3) * Math.cos(5-x));
        assertThat(secondValue.get()).isCloseTo(5.849, Percentage.withPercentage(0.05));

    }

    @Test
    public void itCorrectlyReturnsEmptyForFunctionsThatDoNotIntersect() {
        KineticPriorityQueue<String> queueUnderTest = new KineticPriorityQueue<>(1.0);

        Optional<Double> value = queueUnderTest.calculateIntersection(x -> x, x -> (x + 3) );
        assertThat(value).isEmpty();

        queueUnderTest.advance(5.0);
        Optional<Double> secondValue = queueUnderTest.calculateIntersection(x -> x, x -> (x + 3));
        assertThat(secondValue).isEmpty();

    }

    @Test
    public void itCorrectlyCalculatesIntersectionForCubicFunctions() {
        KineticPriorityQueue<String> queueUnderTest = new KineticPriorityQueue<>(-11.0);

        Function<Double, Double> cubicFunctionA = x -> 3 + Math.sin(x) + x + (3*Math.pow(x,2))- Math.pow(x, 3);
        Function<Double, Double> cubicFunctionB = x -> 1 + Math.pow(x, 3) + (2*Math.pow(x,2)) - (8*x) +1;

        Optional<Double> value = queueUnderTest.calculateIntersection(cubicFunctionA, cubicFunctionB);
        assertThat(value.get()).isCloseTo(-1.883,Percentage.withPercentage(0.05));

        queueUnderTest.advance(-1.0);
        Optional<Double> secondValue = queueUnderTest.calculateIntersection(cubicFunctionA, cubicFunctionB);
        assertThat(secondValue.get()).isCloseTo(-0.1013,Percentage.withPercentage(0.05));


        queueUnderTest.advance(1.0);
        Optional<Double> thirdValue = queueUnderTest.calculateIntersection(cubicFunctionA, cubicFunctionB);
        assertThat(thirdValue.get()).isCloseTo(2.462,Percentage.withPercentage(0.05));

        queueUnderTest.advance(5.0);
        Optional<Double> emptyValue = queueUnderTest.calculateIntersection(cubicFunctionA, cubicFunctionB);
        assertThat(emptyValue).isEmpty();

    }

    @Test
    public void itCorrectlyReturnsEmptyForFunctionsToMeetButDoNotIntersect() {
        KineticPriorityQueue<String> queueUnderTest = new KineticPriorityQueue<>(1.0);

        //They meet at 2.5 but do not intersect
        Function<Double, Double> quadraticFunctionA = x -> Math.pow(x,2) -5*x +2;
        Function<Double, Double> quadraticFunctionB = x -> -10.5 - Math.pow(x, 2) +5*x;

        Optional<Double> value = queueUnderTest.calculateIntersection(quadraticFunctionA, quadraticFunctionB);
        assertThat(value).isEmpty();
    }
}