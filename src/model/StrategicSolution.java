package model;

import java.util.Arrays;

public class StrategicSolution extends SolutionWithCache{

    private static final float MARGIN = .25f;
    private final int[] relaxedClusterSize;

    private boolean cacheIsValid = false;

    public StrategicSolution(Instance ins) {
        this(ins, MARGIN);
    }

    public StrategicSolution(Instance ins, float margin){
        super(ins);
        relaxedClusterSize = new int[ins.k];
        for (int i = 0; i < ins.k; i++) {
            relaxedClusterSize[i] = Math.round(ins.getClusterSize(i)*(1+margin));
        }
    }

    public int getRelaxedClusterSize(int i){
        assert i >= 0 && i < relaxedClusterSize.length: "i not in range ->" + i;
        return relaxedClusterSize[i];
    }

    public int[] getRelaxedClusterSizes(){
        return Arrays.copyOf(relaxedClusterSize, relaxedClusterSize.length);
    }

    public double[] calculateReassignCost(int a, int k){

        assert getRelaxedClusterSize(k) > solution[k].size(): "Trying to exceed maximum size";
        if(!cacheIsValid){
            generateCache();
            cacheIsValid = true;
        }

        return new double[]{removeCost[a], assignCost[a][k]};
    }

    public void reassign(int a, int k, double[] adjustment){
        assert !getCluster(k).contains(a): "K already contains A";
        assert adjustment != null && adjustment.length == 2: "Invalid adjustment";

        int kOfa = getClusterOf(a);
        getCluster(kOfa).forEach(n -> removeCost[n] += ins.getDistanceBetween(n,a));
        getCluster(k).forEach(n -> removeCost[n] -= ins.getDistanceBetween(n,a));

        for (int i = 0; i < ins.n; i++) {
            if(getClusterOf(i) != kOfa)
                this.assignCost[i][kOfa] -= ins.getDistanceBetween(i,a);
            if(getClusterOf(i) != k)
                this.assignCost[i][k] += ins.getDistanceBetween(i,a);
        }

        this.solution[kOfa].remove(a);
        this.solution[k].add(a);
        this.setOfPoint[a] = k;

        this.localObjValue[kOfa] += adjustment[0];
        this.localObjValue[k] += adjustment[1];

        updateCacheFor(a, kOfa, k);

        assert areAssignCostsValid() && areRemoveCostsValid(): "Cache is not consistent";
    }

    public boolean validClusterSizes(){
        for (int i = 0; i < ins.k; i++)
            if(this.getCluster(i).size() > ins.getClusterSize(i))
                return false;
        return true;
    }

}
