package solver.improve;

import model.Solution;

public class BestImpLS implements Improver {

    public boolean iteration(Solution s) {

        int a = 0, b = 0;
        double[] mincost = null; // Mandatory ¯\_(ツ)_/¯

        double costcost = 0;
        for (int i = 0; i < s.ins.n - 1; i++) {
            for (int j = i + 1; j < s.ins.n; j++) {
                if (s.getClusterOf(i) == s.getClusterOf(j))
                    continue;
                double[] cost = s.calculateSwapCost(i, j);
                // Get the one that will make the biggest change to the obj function
                if (cost[0] + cost[1] < costcost) {
                    a = i;
                    b = j;
                    mincost = cost;
                    costcost = cost[0] + cost[1];
                }
            }
        }

        if (mincost != null)
            s.swap(a, b, mincost);
        return mincost != null;
    }
}
