package solver.create;

import model.Instance;
import model.Solution;
import model.SolutionWithCache;
import other.DoubleComparator;
import other.RandomManager;

import java.util.Random;

public class GreedyConstructor implements Constructor {

    @Override
    public Solution construct(Instance ins) {
        Solution sol = new SolutionWithCache(ins);
        Random r = RandomManager.getRandom();

        // Assign first points "randomly"
        for (int i = 0; i < sol.ins.k; i++) {
            //sol.assign(i, i);
            int point = r.nextInt(sol.ins.n);
            while (sol.isAssigned(point))
                point = r.nextInt(sol.ins.n);
            sol.assign(point, i);
        }

        for (int i = 0; i < sol.ins.n; i++) {
            if (sol.isAssigned(i))
                continue;
            greedyAsign(i, sol);
        }

        assert DoubleComparator.equals(sol.getOptimalValue(), sol.recalculateAllObjValue().getOptimalValue()) : "No me cuadran las cuentas :S";
        return sol;
    }

    /**
     * Assigns a point to the cluster which will increase the less the obj value.
     *
     * @param a Point to assign
     */
    private void greedyAsign(int a, Solution sol) {

        double cost = Double.MAX_VALUE;
        int cluster = -1;

        for (int i = 0; i < sol.ins.k; i++) {
            if (sol.isFullCluster(i))
                continue;

            double temp = sol.getAssignCost(a, i);
            if (temp < cost) {
                cost = temp;
                cluster = i;
            }
        }

        if (cluster == -1)
            throw new IllegalStateException("Aqui ha pasao algo raro xikiyo, no deberia sobrar ningun punto?");

        sol.assign(a, cluster, cost);
    }
}
