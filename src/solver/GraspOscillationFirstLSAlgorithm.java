package solver;

import model.Solution;
import solver.improve.StrategicOscillation;

public class GraspOscillationFirstLSAlgorithm extends GraspFirstLsAlgorithm{


    public GraspOscillationFirstLSAlgorithm(int i, double randomness, int maxSeconds, float margin) {
        super(i, randomness, maxSeconds, margin);
    }

    @Override
    Solution improve(Solution s) {
        return super.improve(new StrategicOscillation().improve(super.improve(s), maxSeconds));
    }

    @Override
    public String toString() {
        return "GraspOscillationFirstLSAlgorithm{" +
                "margin=" + margin +
                '}';
    }
}
