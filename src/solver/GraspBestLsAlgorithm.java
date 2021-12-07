package solver;

import model.Solution;
import solver.improve.BestImpLS;

public class GraspBestLsAlgorithm extends GRASPAlgorithm {

    private int maxSeconds;

    public GraspBestLsAlgorithm(int i, double randomness, int maxSeconds) {
        super(i, randomness);
        this.maxSeconds = maxSeconds;
    }

    @Override
    Solution improve(Solution s) {
        return new BestImpLS().improve(s, maxSeconds);
    }

    @Override
    public String toString() {
        return "GraspBestLsAlgorithm{" +
                "maxSeconds=" + maxSeconds +
                '}';
    }
}
