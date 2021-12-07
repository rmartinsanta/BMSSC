package solver;

import model.Instance;
import model.Solution;
import solver.create.RandomConstructor;

public class TotallyRandomAlgorithm implements Algorithm {

    int executions;

    public TotallyRandomAlgorithm() {
        this(1);
    }

    public TotallyRandomAlgorithm(int i) {
        executions = i;
    }

    @Override
    public Solution algorithm(Instance ins) {

        Solution s = new RandomConstructor().construct(ins);
        for (int i = 1; i < executions; i++) {
            Solution temp = new RandomConstructor().construct(ins);
            if (temp.getOptimalValue() < s.getOptimalValue())
                s = temp;
        }

        return s;
    }

    @Override
    public String toString() {
        return "TotallyRandomAlgorithm{" +
                "executions=" + executions +
                '}';
    }
}
