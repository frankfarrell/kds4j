package com.github.frankfarrell.kds4j;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.AllowedSolution;
import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver;
import org.apache.commons.math3.exception.NoBracketingException;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by frankfarrell on 30/03/2018.
 */
public class OneDimensionalKineticDataStructureSolver {

    //Make all these configurable if the client has some particular use case.
    //Tweaking these can make the data structure more efficient in certain circumstances
    private static final Integer MAX_SOLVER_BRACKETING_ITERATIONS = 20;
    public static final double DEFAULT_RELATIVE_ACCURACY = 1.0e-12;
    public static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0e-8;
    public static final int DEFAULT_MAXIMAL_ORDER = 5;
    //If the upper and lower brackets have the same sign, we add exponents of the this value to the upper until they aren't, for SOLVER_BRACKETING_ITERATIONS
    public static final int BRACKETING_EXPONANT_BASE = 2;
    public static final int SOLVER_MAX_EVAL = 1000;

    private final BracketingNthOrderBrentSolver solver;

    public OneDimensionalKineticDataStructureSolver() {
        this.solver = getDefaultSolver();
    }

    public OneDimensionalKineticDataStructureSolver(final BracketingNthOrderBrentSolver solver) {
        this.solver = solver;
    }

    private static BracketingNthOrderBrentSolver getDefaultSolver(){
        return new BracketingNthOrderBrentSolver(DEFAULT_RELATIVE_ACCURACY, DEFAULT_ABSOLUTE_ACCURACY, DEFAULT_MAXIMAL_ORDER);
    }

    public Optional<Double> calculateIntersection(final Function<Double, Double> f,
                                                  final Function<Double, Double> g,
                                                  final Double time){
        return calculateIntersectionInner(f,g,time, time+1, 0);
    }

    public Optional<Double> calculateIntersectionInner(final Function<Double, Double> f,
                                                       final Function<Double, Double> g,
                                                       final Double time,
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
            return calculateIntersectionInner(f,g, time, time + Math.pow(BRACKETING_EXPONANT_BASE, iteration), iteration+1);
        }
        catch (Exception e){
            return Optional.empty();
        }
    }
}
