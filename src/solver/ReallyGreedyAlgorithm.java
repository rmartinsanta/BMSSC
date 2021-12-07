package solver;

import model.Instance;
import model.Solution;
import solver.create.GreedyConstructor;

public class ReallyGreedyAlgorithm implements Algorithm {

    int executions;

    public ReallyGreedyAlgorithm() {
        this(1);
    }

    public ReallyGreedyAlgorithm(int i) {
        this.executions = i;
    }

    @Override
    public Solution algorithm(Instance ins) {

        Solution s = new GreedyConstructor().construct(ins);
        for (int i = 1; i < executions; i++) {
            Solution temp = new GreedyConstructor().construct(ins);
            if (temp.getOptimalValue() < s.getOptimalValue())
                s = temp;
        }
        return s;
    }

    @Override
    public String toString() {
        return "ReallyGreedyAlgorithm{" +
                "executions=" + executions +
                '}';
    }
}
