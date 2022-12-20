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
    private int repetition;


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
        return "%s,%s,%s,%s,%s".formatted(getInstanceName(), getAlgorithmName(), this.repetition, this.s.getOptimalValue(), this.execTime / (double) 1_000_000_000);
    }

    public void setRepetitions(int repetition) {
        this.repetition = repetition;
    }

    public int getRepetition(){
        return this.repetition;
    }
}
