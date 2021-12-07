package solver.improve;

import model.Solution;

import static other.Debug.debug;

public interface Improver {
    /**
     * Improves a model.Solution
     * Iterates until we run out of time, or we cannot improve the current solution any further
     * @param s model.Solution to improve
     * @return Improved s
     */
    default Solution improve(Solution s, int maxSeconds) {
        s.saveOBValue();
        assert debug("### USING TIMER: MAX TIME " + maxSeconds + "s ### ");
        long stopTime = System.nanoTime() + maxSeconds*1000000000L;
        int rounds = 0;
        while (System.nanoTime() < stopTime && iteration(s)){
            rounds++;
        }
        assert debug("Delta Time to Scheduled Stop (ms): " + (System.nanoTime()-stopTime)/1000000d);
        assert debug("Improvement rounds: "+rounds);
        return s;
    }

    /**
     * Tries to improve the recieved solution
     * @param s Solution to improve
     * @return True if the solution has been improved, false otherwise
     */
    boolean iteration(Solution s);



}
