package model;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Solution {

    static final int NOT_ASSIGNED = -1;

    public final Instance ins;

    /**
     * Array of Sets, each set contains the points assigned to that set
     */
    final Set<Integer>[] solution;

    /**
     * Value of the objective function for each cluster
     */
    double[] localObjValue;

    /**
     * Stores which point is assigned to which cluster
     */
    final int[] setOfPoint;

    /**
     * I need a reference to E type in order to convert a Set<E> to E[].
     */
    static Integer[] type = new Integer[0];

    private double obvbeforeimprv = -1;

    @SuppressWarnings("unchecked")
    public Solution(Instance ins) {

        this.ins = ins;

        this.solution = (Set<Integer>[]) Array.newInstance(Set.class, ins.k);
        for (int j = 0; j < ins.k; j++) {
            this.solution[j] = new HashSet<>(ins.getClusterSize(j));
        }

        this.setOfPoint = new int[ins.n];
        Arrays.fill(setOfPoint, NOT_ASSIGNED);

        this.localObjValue = new double[ins.k];
    }

    public Set<Integer>[] getSolution() {
        return solution;
    }

    public List<Set<Integer>> getSolutionAsList() {
        return Arrays.stream(solution).collect(Collectors.toList());
    }

    public Set<Integer> getCluster(int i) {
        assert i < this.solution.length;
        return this.solution[i];
    }

    /**
     * Checks if a cluster is full
     *
     * @param i cluster to check
     * @return true if the current cluster size is equals to its assigned size, false otherwise
     */
    public boolean isFullCluster(int i) {
        assert i < this.solution.length;

        return this.solution[i].size() == this.ins.getClusterSize(i);
    }


    /**
     * Get the current optimal value
     *
     * @return the current optimal value
     */
    public double getOptimalValue() {
        assert localObjValue.length == solution.length;

        double temp = 0;
        for (int i = 0; i < localObjValue.length; i++) {
            temp += localObjValue[i] / solution[i].size();
        }
        return temp;
    }

    /**
     * Get the set a point is inside of
     *
     * @param a Point to get which cluster is assigned to
     * @return the cluster ID, or -1 if the point is not assigned yet
     */
    public int getClusterOf(int a) {
        return this.setOfPoint[a];
    }

    /**
     * Recalculates all the local obj values, useful for testing.
     */
    public Solution recalculateAllObjValue() {
        for (int i = 0; i < solution.length; i++) {

            this.localObjValue[i] = 0;
            Integer[] pointsInSet = solution[i].toArray(type);

            for (int j = 0; j < pointsInSet.length - 1; j++) {
                for (int k = j + 1; k < pointsInSet.length; k++) {
                    this.localObjValue[i] += ins.getDistanceBetween(pointsInSet[j], pointsInSet[k]);
                }
            }
        }
        return this;
    }

    /**
     * Calculate how much the optimal value would increase or decrease if we assign point a to cluster k
     *
     * @param a Point to assign
     * @param k Cluster where the point would be assigned to
     */
    public double getAssignCost(int a, int k) {
        assert !solution[k].contains(a);
        return _getAssignCost(a, k);
    }

    double _getAssignCost(int a, int k) {
        if(getClusterOf(a)==k)
            return 0;
        return this.solution[k].stream().mapToDouble(i -> ins.getDistanceBetween(a, i)).sum();
    }

    /**
     * Calculate change to the optimal value if we remove point A from its cluster
     *
     * @param a Point to assign
     */
    public double calculateRemoveCost(int a) {
        assert isAssigned(a);

        return _calculateRemoveCost(a, getClusterOf(a));
    }

    /**
     * Calculate how much the optimal value would increase or decrease if we assign point a to cluster k
     *
     * @param a Point to assign
     * @param k Cluster where the point would be assigned to
     */
    double _calculateRemoveCost(int a, int k) {
        assert solution[k].contains(a);

        return -this.solution[k].stream().mapToDouble(i -> ins.getDistanceBetween(a, i)).sum();
    }

    /**
     * Assigns a point to a cluster. Calculates adjustment to the optimal value automatically..
     *
     * @param a Point to assign
     * @param k Cluster where the point is going to be put on
     */
    public void assign(int a, int k) {
        this.assign(a, k, getAssignCost(a, k));
    }

    /**
     * Assigns a point to a cluster. Trusts user input to adjusts optimal value.
     *
     * @param a    Point to assign
     * @param k    Cluster where the point is going to be put on
     * @param cost Cost increase/decrease, I trust you...
     */
    public void assign(int a, int k, double cost) {
        assert ins.getClusterSize(k) > this.solution[k].size() : "Cluster is full, cannot assign point";
        assert !solution[k].contains(a) : "Point already in cluster";

        this.localObjValue[k] += cost;
        this.solution[k].add(a);
        this.setOfPoint[a] = k;
    }

    /**
     * Checks if a point is asigned to a cluster
     *
     * @param a Point to check
     * @return true if assigned to a cluster, false otherwise
     */
    public boolean isAssigned(int a) {
        return this.setOfPoint[a] != NOT_ASSIGNED;
    }

    /**
     * Returns the cost of swapping a with b.
     *
     * @param a
     * @param b
     * @return An array with the change to A and B cluster.
     */
    public double[] calculateSwapCost(int a, int b) {
        int clusterA = this.setOfPoint[a];
        int clusterB = this.setOfPoint[b];

        double removeA = _calculateRemoveCost(a, clusterA);
        double removeB = _calculateRemoveCost(b, clusterB);

        // _getAssignCost does not take into account that when we assign A, B is still part of the cluster.
        // Because the localObjValue is calculated as the distance between all the points, we can just subtract it from the total
        double assignA = _getAssignCost(a, clusterB) - this.ins.getDistanceBetween(a, b);
        double assignB = _getAssignCost(b, clusterA) - this.ins.getDistanceBetween(a, b);

        // Change to a cluster when swapping: removedPoint + assignedPoint
        return new double[]{removeA + assignB, removeB + assignA};
    }

    /**
     * Swaps two points, recalculates the current optimal value.
     *
     * @param a
     * @param b
     */
    public void swap(int a, int b, double[] costAdjustment) {
        assert a >= 0 && b >= 0 : "Invalid point ID";
        assert isAssigned(a) && isAssigned(b) : "Not assigned";
        assert getClusterOf(a) != getClusterOf(b) : "Both points are in the same cluster";

        _swap(a, b, costAdjustment);

    }

    void _swap(int a, int b, double[] costAdjustment) {
        int ka = setOfPoint[a];
        int kb = setOfPoint[b];

        this.solution[ka].remove(a);
        this.solution[ka].add(b);
        this.solution[kb].remove(b);
        this.solution[kb].add(a);

        this.setOfPoint[b] = ka;
        this.setOfPoint[a] = kb;

        this.localObjValue[ka] += costAdjustment[0];
        this.localObjValue[kb] += costAdjustment[1];
    }

    public double getObvbeforeimprv() {
        return obvbeforeimprv;
    }

    public void saveOBValue() {
        this.obvbeforeimprv = this.getOptimalValue();
    }

}
