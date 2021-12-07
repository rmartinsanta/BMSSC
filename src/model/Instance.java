package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Instance {

    /**
     * Number of points
     */
    public final int n;

    /**
     * Number of dimensions
     */
    public final int d;

    /**
     * Number of clusters
     */
    public final int k;

    /**
     * Filename
     */
    String name;

    /**
     * Points matrix, each row represents a point, each column a dimension
     */
    private double[][] points;

    /**
     * Symmetric matrix, stores the cost between any two points
     */
    private double[][] distances;

    /**
     * Minimum points per cluster (minPointsPerCluster = n / k)
     */
    public final int minPointsPerCluster;

    /**
     * Assigned Size for each cluster
     */
    private int[] clusterSizes;

    /**
     * Creates an instance given the instance details
     *
     * @param ss Array of strings that contains the filename, the number of points,
     *           the dimension, and the number of clusters we are splitting the points between (in that order)
     */
    public Instance(String[] ss) {
        this.name = ss[0];
        this.n = Integer.parseInt(ss[1]);
        this.d = Integer.parseInt(ss[2]);
        this.k = Integer.parseInt(ss[3]);

        this.minPointsPerCluster = n / k;
        this.clusterSizes = new int[k];
        for (int i = 0; i < clusterSizes.length; i++) {
            clusterSizes[i] = minPointsPerCluster;
        }
        for (int i = 0; i < n % k; i++) {
            clusterSizes[i]++;
        }

        loadData(Paths.get(this.name));

        calculateDistances();

        assert validate();
    }

    private void loadData(Path p) {
        try (Stream<String> linesStream = Files.lines(p)) {

            String[] lines = linesStream.filter(line -> !line.isEmpty()).toArray(String[]::new);

            if (lines.length <= 0 || lines[0].length() <= 0)
                throw new IllegalArgumentException("Invalid points file");

            points = new double[lines.length][lines[0].split(",").length];

//            Map<Integer, Integer> stats = new HashMap<>();

            for (int i = 0; i < points.length; i++) {
                String[] temp = lines[i].split(",");
//                stats.putIfAbsent(temp.length, 0);
//                stats.put(temp.length, stats.get(temp.length) + 1);
//                if(temp.length != d){
//                    System.out.println(stats);
//                    throw new IllegalArgumentException(String.format("Error processing file, line %s has %s dimensions, declared dimensions %s", i+1, temp.length, d));
//                }
                for (int j = 0; j < temp.length; j++) {
                    points[i][j] = Double.parseDouble(temp[j]);
                }
            }
//            System.out.println(stats);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validate() {
        if (points.length != n)
            throw new RuntimeException("Declared number of points does not correspond with the datafile: " + this.name);

        for (double[] f : points) {
            if (f.length != d)
                throw new RuntimeException(String.format("Point length %s: %s does not match declared dimension %d, instance %s", f.length, Arrays.toString(f), this.d, this.name));
        }

        return true;
    }

    private void calculateDistances() {

        assert (this.distances == null) : "First call to calculate cost";

        this.distances = new double[this.n][this.n];
        for (int i = 0; i < this.n - 1; i++) {
            for (int j = i + 1; j < this.n; j++) {
                double distance = distanceBetween(this.points[i], this.points[j]);
                this.distances[i][j] = distance;
                this.distances[j][i] = distance;
            }
        }
    }

    public double distanceBetween(double[] a, double[] b) {

        assert (a != null && b != null && a.length == b.length) : "Points are null, or have different number of dimensions";

        double distance = 0;
        for (int i = 0; i < a.length; i++) {
            double t = (a[i] - b[i]);
            distance += t * t;
        }
        return distance;
    }

    public double[] getPoint(int a) {
        return this.points[a];
    }

    public int getClusterSize(int i) {
        return clusterSizes[i];
    }

    public int[] getClusterSizes() {
        return Arrays.copyOf(clusterSizes, clusterSizes.length);
    }

    public double getDistanceBetween(int a, int b) {
        return this.distances[a][b];
    }

    public String getName() {
        return name;
    }
}
