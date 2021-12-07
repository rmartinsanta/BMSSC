package solver.create;

import model.Instance;
import model.Solution;

public interface Constructor {
    /**
     * CREATES a s for the given instance
     *
     * @param i Instance1
     * @return model.Solution
     */
    Solution construct(Instance i);
}
