package solver;

import model.Instance;
import model.Result;
import model.Solution;

public interface Algorithm {

    /**
     * Wrapper for algorithm, measures the execution time (in nanoseconds).
     * @param i Instance the algorithm will process
     * @return Result of the execution, contains the solution and the execution time
     * @see #algorithm
     */
    default Result execute(Instance i) {
        long startTime = System.nanoTime();
        Solution s = algorithm(i);
        long ellapsedTime = System.nanoTime() - startTime;

        return new Result(s, ellapsedTime);
    }

    /**
     * Algorithm implementation
     *
     * @param ins Instance the algorithm will process
     * @return Proposed solution
     */
    Solution algorithm(Instance ins);
}
