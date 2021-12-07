package solver;

import model.Instance;
import model.Solution;
import solver.create.GRASPConstructor;

public class GRASPAlgorithm implements Algorithm {

    private int executions;

    private double randomness;

    protected float margin;

    /**
     * Create a new GRASPAlgorithm, @see algorithm
     * @param i Tries, the algorythm will be executed i times, returns the best.
     * @param randomness Randomness, adjusts the candidate list size.
     *                   Takes values between [0,1] being 1 --> totally random, 0 --> full greedy.
     *                   Special Values:
     *                   MIN_VALUE (Generate a random value each construction, and keep it until the solution is built)
     *                   MAX_VALUE (Generate a random value each time we read it, this way it will take different values in the same construction.
     */
    public GRASPAlgorithm(int i, double randomness) {
        this.executions = i;
        this.randomness = randomness;
    }

    GRASPAlgorithm(int i, double randomness, float margin) {
        this.executions = i;
        this.randomness = randomness;
        this.margin = margin;
    }

    /**
     * Executes the algorythm for the given instance
     * @param ins Instance the algorithm will process
     * @return Best solution found
     */
    @Override
    public Solution algorithm(Instance ins) {
        return improve(construct(ins));
    }

    Solution construct(Instance ins) {
        Solution s = new GRASPConstructor(randomness, margin).construct(ins);
        for (int i = 1; i < executions; i++) {
            Solution temp = new GRASPConstructor(randomness, margin).construct(ins);
            if (temp.getOptimalValue() < s.getOptimalValue())
                s = temp;
        }
        return s;
    }

    Solution improve(Solution s) {
        return s;  //NOP
    }

    @Override
    public String toString() {
        return "GRASPAlgorithm{executions=" + executions +", randomness=" + randomness +'}';
    }
}
