package solver;

import model.Solution;
import solver.improve.StrategicOscillation;

public class GraspOscillationFirstLSAlgorithm2 extends GraspFirstLsAlgorithm{

    private int itCount;

    public GraspOscillationFirstLSAlgorithm2(int i, double randomness, int maxSeconds, int itCount) {
        super(i, randomness, maxSeconds);
        this.itCount = itCount;
    }

    @Override
    Solution improve(Solution s) {
        for (int i = 0; i < itCount; i++) {
            s = new StrategicOscillation().improve(super.improve(s), maxSeconds);
        }
        return super.improve(s);
    }

    @Override
    public String toString() {
        return "GraspOscillationFirstLSAlgorithm2{" +
                "itCount=" + itCount +
                '}';
    }
}
