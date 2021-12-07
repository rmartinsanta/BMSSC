package solver;

import model.Instance;
import model.Solution;
import solver.create.Constructor;
import solver.improve.Improver;

import java.util.Arrays;
import java.util.List;

public class GenericAlgorythmMulti extends GenericAlgorithm{

    List<Improver> improverList;
    /**
     * Create a new GenericAlgorythmMulti, @see algorithm
     *
     * @param i           Tries, the algorythm will be executed i times, returns the best.
     * @param constructor
     * @param improver
     */
    public GenericAlgorythmMulti(int i, Constructor constructor, Improver... improver) {
        super(i, constructor, null);
        this.improverList = Arrays.asList(improver);
    }

    /**
     * Executes the algorythm for the given instance
     * @param ins Instance the algorithm will process
     * @return Best solution found
     */
    //@Override
    public Solution algorithm2(Instance ins) {
        Solution s = construct(ins);
        for (int i = 1; i < executions; i++) {
            Solution temp = construct(ins);
            improverList.forEach(x -> x.improve(temp, 300));
            if (temp.getOptimalValue() < s.getOptimalValue())
                s = temp;
        }
        return s;
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
        Solution improveMe = s;
        improverList.forEach(x -> x.improve(improveMe, 300));
        return improveMe;
    }

    @Override
    public String toString() {
        return "GenericAlgorythmMulti{" +
                "executions=" + executions +
                ", constructor=" + constructor +
                ", improvers=" + improverList +
                '}';
    }

}
