package model;

import other.DoubleComparator;

import static other.Debug.debug;

public class SolutionWithCache extends Solution {

    final double[][] assignCost;

    double[] removeCost = null;

    public SolutionWithCache(Instance ins) {
        super(ins);
        this.assignCost = new double[ins.n][ins.k];
    }

    /**
     * Calculate how much the optimal value would increase or decrease if we assign point a to cluster k, uses cache for lookup
     *
     * @param a Point to assign
     * @param k Cluster where the point would be assigned to
     */
    public double getAssignCost(int a, int k) {
        assert !solution[k].contains(a);

        return this.assignCost[a][k];
    }

    /**
     * Assigns a point to a cluster. Trusts user input to adjusts optimal value. Updates cache.
     *
     * @param a    Point to assign
     * @param k    Cluster where the point is going to be put on
     * @param cost Cost increase/decrease, I trust you...
     */
    public void assign(int a, int k, double cost) {

        assert ins.getClusterSize(k) > this.solution[k].size() : "Cluster is full, cannot assign point";

        this.localObjValue[k] += cost;
        this.solution[k].add(a);
        this.setOfPoint[a] = k;

        for (int i = 0; i < ins.n; i++) {
            this.assignCost[i][k] += this.ins.getDistanceBetween(a, i);
        }
    }

    @Override
    double _calculateRemoveCost(int a, int k) {
        return super._calculateRemoveCost(a, k);
    }

    @Override
    void _swap(int a, int b, double[] costAdjustment) {

        int ka = setOfPoint[a];
        int kb = setOfPoint[b];

        // removeCost[n] is negative; by removing a point from the cluster, the possible improvement from removing itself decreases
        getCluster(ka).forEach(n -> this.removeCost[n] += ins.getDistanceBetween(n, a) - ins.getDistanceBetween(n, b));
        getCluster(kb).forEach(n -> this.removeCost[n] += ins.getDistanceBetween(n, b) - ins.getDistanceBetween(n, a));

        for (int i = 0; i < ins.n; i++) {
            if(getClusterOf(i) != ka)
                this.assignCost[i][ka] += ins.getDistanceBetween(i,b) - ins.getDistanceBetween(i,a);
            if(getClusterOf(i) != kb)
                this.assignCost[i][kb] += ins.getDistanceBetween(i,a) - ins.getDistanceBetween(i,b);
        }

        this.solution[ka].remove(a);
        this.solution[ka].add(b);
        this.solution[kb].remove(b);
        this.solution[kb].add(a);

        this.setOfPoint[b] = ka;
        this.setOfPoint[a] = kb;

        this.localObjValue[ka] += costAdjustment[0];
        this.localObjValue[kb] += costAdjustment[1];

        updateCacheFor(a, ka, kb);
        updateCacheFor(b, kb, ka);

        assert areRemoveCostsValid();
        assert areAssignCostsValid(): a + " " + b;
    }

    @Override
    public double[] calculateSwapCost(int a, int b) {
        if(this.removeCost == null){
            generateCache();
        }

        int clusterA = this.getClusterOf(a);
        int clusterB = this.getClusterOf(b);

        // --> { deltaClusterA, deltaClusterB }
        return new double[]{
                this.assignCost[b][clusterA] - this.ins.getDistanceBetween(a, b) + this.removeCost[a],
                this.assignCost[a][clusterB] - this.ins.getDistanceBetween(a, b) + this.removeCost[b]
        };
    }

    void initAssignCosts() {
        for (int i = 0; i < ins.n; i++) {
            for (int j = 0; j < ins.k; j++) {
                this.assignCost[i][j] = super._getAssignCost(i,j);
            }
        }
    }

    void initRemoveCosts(){
        for (int i = 0; i < ins.n; i++)
                this.removeCost[i] = super.calculateRemoveCost(i);
    }

    boolean areRemoveCostsValid(){
        for (int i = 0; i < ins.n; i++)
            if(!DoubleComparator.equals(this.removeCost[i], super.calculateRemoveCost(i)))
                return false;
        return true;
    }

    boolean areAssignCostsValid(){
        for (int i = 0; i < ins.n; i++)
            for (int j = 0; j < ins.k; j++)
                if(!DoubleComparator.equals(this.assignCost[i][j], super._getAssignCost(i,j)))
                    return false;
        return true;
    }

    void updateCacheFor(int a, int kOrig, int kDest){
        this.assignCost[a][kOrig] = super.getAssignCost(a, kOrig);
        this.assignCost[a][kDest] = 0;
        this.removeCost[a] = super.calculateRemoveCost(a);
    }

    void generateCache(){
        this.removeCost = new double[ins.n];
        long start = System.nanoTime();
        initRemoveCosts(); assert areRemoveCostsValid();
        assert debug("Time to init Remove Costs (ms): "+(System.nanoTime()-start)/1000000);
        initAssignCosts(); assert areAssignCostsValid();
        assert debug("Time to init Assign Costs (ms): "+(System.nanoTime()-start)/1000000);
    }
}
