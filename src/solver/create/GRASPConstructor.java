package solver.create;

import model.Instance;
import model.Solution;
import model.StrategicSolution;
import org.jetbrains.annotations.NotNull;
import other.DoubleComparator;
import other.RandomManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GRASPConstructor implements Constructor {

    private double a;

    private float margin;

    /**
     * GRASP Constructor
     * @param alpha Randomness, adjusts the candidate list size.
     *                   Takes values between [0,1] being 1 --> totally random, 0 --> full greedy.
     *                   Special Values:
     *                   MIN_VALUE (Generate a random value each construction, and keep it until the solution is built)
     *                   MAX_VALUE (Generate a random value each time we read it, this way it will take different values in the same construction)
     */
    public GRASPConstructor(double alpha){
        assert alpha == Double.MIN_VALUE || alpha == Double.MAX_VALUE || (alpha >= 0d && alpha <= 1d);
        a = (alpha == Double.MIN_VALUE)? RandomManager.getRandom().nextDouble() : alpha;
    }

    public GRASPConstructor(double alpha, float margin){
        this(alpha);
        this.margin = margin;
    }

    @Override
    public Solution construct(Instance i) {

        Solution sol = new StrategicSolution(i, margin);
        Random r = RandomManager.getRandom();

        assert i.k > 1 && i.n > 1;

        // Assign a random point to the first cluster
        int firstPoint = r.nextInt(sol.ins.n);
        sol.assign(firstPoint, 0, 0);

        // Assign a point to each remaining cluster
        for (int j = 1; j < sol.ins.k; j++) {

            double globalMax = 0;
            int point = -1;

            for (int n = 0; n < sol.ins.n; n++) {

                if (sol.isAssigned(n))
                    continue;

                double localMin = Double.MAX_VALUE;
                for (int l = 0; l < j; l++) {
                    double distanceToPoint = sol.ins.getDistanceBetween(sol.getCluster(l).iterator().next(), n);
                    if (distanceToPoint < localMin)
                        localMin = distanceToPoint;
                }


                if (localMin > globalMax) {
                    globalMax = localMin;
                    point = n;
                }
            }

            assert point != -1;
            sol.assign(point, j, 0);
        }


        List<PointDistance> pd = generateCandidateList(sol);

        while (!pd.isEmpty()) {

            double min = pd.get(0).cost;
            double max = pd.get(pd.size() - 1).cost;
            double umbral = min + (a == Double.MAX_VALUE? RandomManager.getRandom().nextDouble() : a ) * (max - min);

            int indexTope;
            for (indexTope = 0; indexTope < pd.size(); indexTope++) {

                if (pd.get(indexTope).cost > umbral)
                    break;
            }
            PointDistance chosen = pd.remove(r.nextInt(indexTope));
            sol.assign(chosen.point, chosen.cluster, chosen.cost);

            pd = updateCandidateList(sol, pd, chosen.cluster);

        }

        assert DoubleComparator.equals(sol.getOptimalValue(), sol.recalculateAllObjValue().getOptimalValue()) : "No me cuadran las cuentas :S";
        return sol;
    }


    private List<PointDistance> generateCandidateList(Solution sol) {

        List<PointDistance> candidateList = new ArrayList<>(sol.ins.n);

        for (int i = 0; i < sol.ins.n; i++) {

            if (sol.isAssigned(i))
                continue;

            PointDistance temp = new PointDistance(i, new double[sol.ins.k]);

            for (int k = 0; k < sol.ins.k; k++) {

                assert !sol.isFullCluster(k);

                temp.othercosts[k] = sol.getAssignCost(i, k);

            }

            candidateList.add(temp.refresh());

        }

        Collections.sort(candidateList);
        return candidateList;
    }


    @Override
    public String toString() {
        return "GRASPConstructor{" +
                "a=" + a +
                ", margin=" + margin +
                '}';
    }

    private List<PointDistance> updateCandidateList(Solution sol, List<PointDistance> oldList, int cluster) {

        boolean isFull = sol.isFullCluster(cluster);
        for (int i = 0; i < oldList.size(); i++) {

            PointDistance item = oldList.get(i);

            // Update the cost of the updated cluster, refresh mininum cost and best cluster
            item.othercosts[cluster] = isFull ? Double.MAX_VALUE : sol.getAssignCost(item.point, cluster);
            if (cluster == item.cluster)
                item.refresh();
        }

        // Some points may have updated their best cluster, resort.
        Collections.sort(oldList);
        return oldList;


    }


    private class PointDistance implements Comparable<PointDistance> {

        int point;
        double cost;
        int cluster = -1;

        double[] othercosts;

        PointDistance(int point, double[] othercosts) {
            this.point = point;
            this.othercosts = othercosts;
        }

        @Override
        public int compareTo(@NotNull PointDistance pointDistance) {

            return Double.compare(cost, pointDistance.cost);
        }

        PointDistance refresh() {
            cost = Double.MAX_VALUE;
            for (int i = 0; i < othercosts.length; i++) {
                if (othercosts[i] < cost) {
                    cost = othercosts[i];
                    cluster = i;
                }
            }
            assert cost < Double.MAX_VALUE;
            return this;
        }


    }


}
