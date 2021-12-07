package model;

import java.util.Arrays;

/**
 * The result of the execution of an algorithm
 */
public class Result {

    public final Solution s;

    /**
     * In nano seconds
     */
    private final long execTime;

    private String instanceName;

    private String algorythmName;


    public Result(Solution s, long executionTime) {
        this.s = s;
        this.execTime = executionTime;
    }

    public Result setInstanceName(String s) {
        this.instanceName = s;
        return this;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public Result setAlgorythmName(String algorythmName) {
        this.algorythmName = algorythmName;
        return this;
    }

    public long getExecTime() {
        return execTime;
    }


    public String getAlgorythmName() {
        return this.algorythmName;
    }

    //    public String toString(){
//        return "Instance Name: " + this.instanceName
//                + "\nAlgorythm Used: " + this.algorythmName
//                + "\nCalculated Value: "+ this.s.getOptimalValue()
//                + "\nExecution Time (ms): " + (this.execTime / (double) 1000000)
//                + "\n---------------------------------------------------------";
//    }
    public String toString() {
        return "Instance Name: " + this.instanceName
                + "\nAlgorythm Used: " + this.algorythmName
                + (this.s.getObvbeforeimprv() != -1 ? ("\nOB Value Bef. Imprv.: " + Double.toString(this.s.getObvbeforeimprv()).replace('.', ',')) : "")
                + "\nCalculated Value: " + Double.toString(this.s.getOptimalValue()).replace('.', ',')
                + "\nExecution Time (ms): " + Double.toString(this.execTime / (double) 1000000).replace('.', ',')
                + "\n---------------------------------------------------------";
    }
}
