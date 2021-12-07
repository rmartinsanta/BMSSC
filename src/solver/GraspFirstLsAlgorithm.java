package solver;

import model.Solution;
import solver.improve.FirstImpLS;

public class GraspFirstLsAlgorithm extends GRASPAlgorithm {

    int maxSeconds;

    public GraspFirstLsAlgorithm(int i, double randomness, int maxSeconds) {
        super(i, randomness);
        this.maxSeconds = maxSeconds;
    }

    public GraspFirstLsAlgorithm(int i, double randomness, int maxSeconds, float margin) {
        super(i, randomness, margin);
        this.maxSeconds = maxSeconds;
    }

    @Override
    Solution improve(Solution s) {
        return new FirstImpLS().improve(s, maxSeconds);
    }

    @Override
    public String toString() {
        return "GraspFirstLsAlgorithm{" +
                "maxSeconds=" + maxSeconds +
                '}';
    }
}
