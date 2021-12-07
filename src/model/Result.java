package model;

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

    private String algorithmName;


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

    public Result setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
        return this;
    }

    public long getExecTime() {
        return execTime;
    }


    public String getAlgorithmName() {
        return this.algorithmName;
    }


    public String toString() {
        return "Instance Name: " + this.instanceName
                /*+ "\nAlgorithm Used: " + this.algorithmName*/
                /*+ (this.s.getObvbeforeimprv() != -1 ? ("\nOB Value Bef. Imprv.: " + Double.toString(this.s.getObvbeforeimprv()).replace('.', ',')) : "")*/
                + "\nCalculated Value: " + Double.toString(this.s.getOptimalValue()).replace('.', ',')
                + "\nExecution Time (ms): " + Double.toString(this.execTime / (double) 1000000).replace('.', ',')
                + "\n---------------------------------------------------------";
    }
}
