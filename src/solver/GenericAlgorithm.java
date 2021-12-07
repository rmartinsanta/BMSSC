package solver;

import model.Instance;
import model.Solution;
import solver.create.Constructor;
import solver.improve.Improver;

public class GenericAlgorithm implements Algorithm{

    int executions;

    Constructor constructor;

    Improver improver;


    /**
     * Create a new GRASPAlgorithm, @see algorithm
     * @param i Tries, the algorythm will be executed i times, returns the best.
     */
    public GenericAlgorithm(int i, Constructor constructor, Improver improver) {
        this.executions = i;
        this.improver = improver;
        this.constructor = constructor;
    }

    /**
     * Executes the algorythm for the given instance
     * @param ins Instance the algorithm will process
     * @return Best solution found
     */
    @Override
    public Solution algorithm(Instance ins) {
        Solution s = construct(ins);
        for (int i = 1; i < executions; i++) {
            Solution temp = construct(ins);
            if (temp.getOptimalValue() < s.getOptimalValue())
                s = temp;
        }
        return improve(s);
    }

    Solution construct(Instance ins) {
        return this.constructor.construct(ins);
    }

    Solution improve(Solution s) {
        return improver.improve(s, 300);
    }

    @Override
    public String toString() {
        return "GenericAlgorithm{" +
                "executions=" + executions +
                ", constructor=" + constructor +
                ", improver=" + improver +
                '}';
    }
}
