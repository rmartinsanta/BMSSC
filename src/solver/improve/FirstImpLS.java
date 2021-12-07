package solver.improve;

import model.Solution;

public class FirstImpLS implements Improver {

    public boolean iteration(Solution s) {

        for (int i = 0; i < s.ins.n - 1; i++) {
            for (int j = i + 1; j < s.ins.n; j++) {
                if (s.getClusterOf(i) == s.getClusterOf(j))
                    continue;
                double[] cost = s.calculateSwapCost(i, j);
                if ((cost[0] + cost[1]) < 0) {
                    s.swap(i, j, cost);
                    return true;
                }
            }
        }
        return false;
    }
}
