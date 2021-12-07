package solver.improve;

import model.Solution;
import model.StrategicSolution;

import java.util.Arrays;

import static other.Debug.debug;

public class StrategicOscillation implements Improver {

    @Override
    public boolean iteration(Solution s) {
        assert s instanceof StrategicSolution: "Not my cup of tea";

        StrategicSolution ss = (StrategicSolution) s;

        assert debug("Cluster sizes before: " + Arrays.toString(ss.ins.getClusterSizes()));
        assert debug("Relaxed sizes before: " + Arrays.toString(ss.getRelaxedClusterSizes()));

        int c = 0;
        while(tryReassign(ss)){
            c++;
        }

        assert debug("Cluster sizes after: " );
        for (int i = 0; i < s.ins.k; i++) {
            assert debug(s.getCluster(i).size() + " ");
        }

        fixClusters(ss);
        return false;
    }

    public void fixClusters(StrategicSolution s) {
        for (int i = 0; i < s.ins.k; i++) {
            while(s.getCluster(i).size() > s.ins.getClusterSize(i)){
                movePointAnywhere(i, s);

                for (int k = 0; k < s.ins.k; k++) {
                    assert debug(s.getCluster(k).size() + " ");
                }
            }
        }

        assert s.validClusterSizes(): "Failed Check: Cluster Size <= Assigned Size";
    }

    public void movePointAnywhere(int kSource, StrategicSolution s) {
        int point = -1, to = -1;
        double bestValue = Double.MAX_VALUE;
        double[] adjustment = null; // Pensaba que el compilador era mas inteligente

        for (int i : s.getCluster(kSource)){
            for (int j = 0; j < s.ins.k; j++) {
                if(s.getClusterOf(i) == j || s.getCluster(j).size() >= s.ins.getClusterSize(j))
                    continue;
                double[] temp1 = s.calculateReassignCost(i,j); assert temp1.length == 2;
                double temp2 = temp1[0] + temp1[1];
                if(temp2 < bestValue) {
                    bestValue = temp2;
                    adjustment = temp1;
                    point = i;
                    to = j;
                }
            }
        }
        assert point != -1 && to != -1;
        s.reassign(point, to, adjustment);
    }

    private boolean tryReassign(StrategicSolution s){
        int point = -1, to = -1;
        double bestValue = 0;
        double[] adjustment = null; // Pensaba que el compilador era mas inteligente
        boolean foundAssignemnt = false;
        for (int i = 0; i < s.ins.n; i++) {
            for (int j = 0; j < s.ins.k; j++) {
                if(s.getClusterOf(i) == j || s.getCluster(j).size() >= s.getRelaxedClusterSize(j))
                    continue;
                double[] temp1 = s.calculateReassignCost(i,j); assert temp1.length == 2;
                double temp2 = temp1[0] + temp1[1];
                if(temp2 < bestValue){
                    foundAssignemnt = true;
                    bestValue = temp2;
                    adjustment = temp1;
                    point = i;
                    to = j;
                }
            }
        }
        if(foundAssignemnt)
            s.reassign(point, to, adjustment);
        return foundAssignemnt;
    }

}
