package other;

public class DoubleComparator {

    private static final double EPSILON = 0.00001;

    /**
     * Test two doubles for equality, uses default error margin
     * @param d1 first double to test
     * @param d2 second double to test
     * @return True, if the difference between them is less than 0.001%, false otherwise
     */
    public static boolean equals(double d1, double d2) {
        return equals(d1, d2, EPSILON);
    }

    /**
     * Test two doubles for equality
     * @param d1 first double to test
     * @param d2 second double to test
     * @param eps Error margin
     * @return True, if the difference between them is less than the error margin, false otherwise
     */
    public static boolean equals(double d1, double d2, double eps) {
        return Math.abs(d1 - d2) - eps < 0;
    }

}
