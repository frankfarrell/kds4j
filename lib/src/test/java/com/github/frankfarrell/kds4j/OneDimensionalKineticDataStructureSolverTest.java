package com.github.frankfarrell.kds4j;

import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.assertj.core.data.Percentage;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by frankfarrell on 30/03/2018.
 */
public class OneDimensionalKineticDataStructureSolverTest {

    @Test
    public void itCorrectlyEvaluatesIntersectionForLinearFunctions() {
        OneDimensionalKineticDataStructureSolver solverUnderTest = new OneDimensionalKineticDataStructureSolver();

        Optional<Double> value = solverUnderTest.calculateIntersection(x -> 8 - x, x -> x / 2 + 5, 0.0);
        assertThat(value.get()).isCloseTo(2.0, Percentage.withPercentage(0.001));

        Optional<Double> valueWithoutSolution = solverUnderTest.calculateIntersection(x -> 8 - x, x -> x / 2 + 5, 100.0);
        assertThat(valueWithoutSolution).isEmpty();
    }

    @Test
    public void itCorrectlyEvaluatesIntersectionForLinearAndQuadraticFunctions() {
        OneDimensionalKineticDataStructureSolver solverUnderTest = new OneDimensionalKineticDataStructureSolver();

        Optional<Double> value = solverUnderTest.calculateIntersection(x -> 8 - x, x -> (x * x) / 2 - 4 * x, 0.0);
        assertThat(value.get()).isCloseTo(8.0, Percentage.withPercentage(0.001));

        Optional<Double> valueWithoutSolution = solverUnderTest.calculateIntersection(x -> 8 - x, x -> (x * x) / 2 - 4 * x, 100.0);
        assertThat(valueWithoutSolution).isEmpty();
    }

    @Test
    public void itCorrectlyEvaluatesIntersectionForLinearAndGeometricsFunctions() {
        OneDimensionalKineticDataStructureSolver solverUnderTest = new OneDimensionalKineticDataStructureSolver();

        Optional<Double> value = solverUnderTest.calculateIntersection(x -> x, x -> (x + 3) * Math.cos(5-x), 1.0);
        assertThat(value.get()).isCloseTo(4.04, Percentage.withPercentage(0.05));

        Optional<Double> secondValue = solverUnderTest.calculateIntersection(x -> x, x -> (x + 3) * Math.cos(5-x), 5.0);
        assertThat(secondValue.get()).isCloseTo(5.849, Percentage.withPercentage(0.05));

    }

    @Test
    public void itCorrectlyReturnsEmptyForFunctionsThatDoNotIntersect() {
        OneDimensionalKineticDataStructureSolver solverUnderTest = new OneDimensionalKineticDataStructureSolver();

        Optional<Double> value = solverUnderTest.calculateIntersection(x -> x, x -> (x + 3), 1.0 );
        assertThat(value).isEmpty();

        Optional<Double> secondValue = solverUnderTest.calculateIntersection(x -> x, x -> (x + 3), 5.0);
        assertThat(secondValue).isEmpty();

    }

    @Test
    public void itCorrectlyCalculatesIntersectionForCubicFunctions() {
        OneDimensionalKineticDataStructureSolver solverUnderTest = new OneDimensionalKineticDataStructureSolver();

        Function<Double, Double> cubicFunctionA = x -> 3 + Math.sin(x) + x + (3*Math.pow(x,2))- Math.pow(x, 3);
        Function<Double, Double> cubicFunctionB = x -> 1 + Math.pow(x, 3) + (2*Math.pow(x,2)) - (8*x) +1;

        Optional<Double> value = solverUnderTest.calculateIntersection(cubicFunctionA, cubicFunctionB, -11.0);
        assertThat(value.get()).isCloseTo(-1.883,Percentage.withPercentage(0.05));

        Optional<Double> secondValue = solverUnderTest.calculateIntersection(cubicFunctionA, cubicFunctionB, -1.0);
        assertThat(secondValue.get()).isCloseTo(-0.1013,Percentage.withPercentage(0.05));

        Optional<Double> thirdValue = solverUnderTest.calculateIntersection(cubicFunctionA, cubicFunctionB, 1.0);
        assertThat(thirdValue.get()).isCloseTo(2.462,Percentage.withPercentage(0.05));

        Optional<Double> emptyValue = solverUnderTest.calculateIntersection(cubicFunctionA, cubicFunctionB, 5.0);
        assertThat(emptyValue).isEmpty();

    }

    @Test
    public void itCorrectlyReturnsEmptyForFunctionsToMeetButDoNotIntersect() {
        OneDimensionalKineticDataStructureSolver solverUnderTest = new OneDimensionalKineticDataStructureSolver();

        //They meet at 2.5 but do not intersect
        Function<Double, Double> quadraticFunctionA = x -> Math.pow(x,2) -5*x +2;
        Function<Double, Double> quadraticFunctionB = x -> -10.5 - Math.pow(x, 2) +5*x;

        Optional<Double> value = solverUnderTest.calculateIntersection(quadraticFunctionA, quadraticFunctionB, 1.0);
        assertThat(value).isEmpty();
    }

}
