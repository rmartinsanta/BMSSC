package other;

import model.Instance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Analyze {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("indexFile required");
            System.exit(-1);
        }
        System.out.println("Results, can be copy pasted in Calc, or imported as CSV in Excel: ");
        System.out.println("Instance Name,MyScore Old,My Score New Metric, Mathlab Old Score, Mathlab new Metri" +
                "c");
        Main.loadIndex(args[0], Analyze::analyzeResult);

    }

    private static void analyzeResult(String line) {
        String[] parts = line.split(",");
        var instance = new Instance(parts);
        var myOutput = getOutputClusterperLine(parts[0] + ".out");
        var mathlabOutput = getOutputPointsSayCluster(parts[0] + ".matlab");

        var myScore1 = oldMetric(instance, myOutput);
        var myScore2 = daviesBuildinIndex(instance, myOutput);

        var mathLab1 = oldMetric(instance, mathlabOutput);
        var mathLab2 = daviesBuildinIndex(instance, mathlabOutput);
        System.out.format("%s,%s,%s,%s,%s\n", parts[0], myScore1, myScore2, mathLab1, mathLab2);
    }

    private static Set<Integer>[] getOutputClusterperLine(String line) {
        ArrayList<Set<Integer>> sets = new ArrayList<>();
        try {
            Files.lines(Path.of(line)).forEach(l -> {
                sets.add(Arrays.stream(l.split(" ")).map(Integer::parseInt).collect(Collectors.toSet()));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //noinspection unchecked
        return sets.toArray(new Set[0]);
    }

    private static Set<Integer>[] getOutputPointsSayCluster(String line) {
        ArrayList<Set<Integer>> sets = new ArrayList<>();
        try (Scanner sc = new Scanner(new FileInputStream(new File(line)))) {
            int currentPoint = 0;
            while (sc.hasNextDouble()) {
                int cluster = (int) Math.round(sc.nextDouble());
                while (sets.size() < cluster) {
                    sets.add(new HashSet<>());
                }
                sets.get(cluster - 1).add(currentPoint++);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //noinspection unchecked
        return sets.toArray(new Set[0]);
    }


    private static double daviesBuildinIndex(Instance instance, Set<Integer>[] clusters) {
        double[] avgDistToCentroid = new double[clusters.length];
        double[][] centroids = new double[clusters.length][];
        double total = 0;
        for (int i = 0; i < avgDistToCentroid.length; i++) {
            avgDistToCentroid[i] = averageDistanceToCentroid(instance, clusters[i]);
            centroids[i] = centroid(instance, clusters[i]);
        }

        for (int i = 0; i < clusters.length; i++) {
            double max = Integer.MIN_VALUE;
            for (int j = 0; j < clusters.length; j++) {
                if (i == j) continue;
                double value = (avgDistToCentroid[i] + avgDistToCentroid[j]) / instance.distanceBetween(centroids[i], centroids[j]);
                max = Math.max(max, value);
            }
            if (max == Integer.MIN_VALUE) throw new IllegalStateException();
            total += max;
        }

        return total / clusters.length;
    }

    private static double oldMetric(Instance instance, Set<Integer>[] solution) {
        double[] costePorCluster = new double[solution.length];

        Integer[] type = new Integer[0];
        for (int i = 0; i < solution.length; i++) {
            Set<Integer> integers = solution[i];
            Integer[] pointsInSet = integers.toArray(type);
            for (int j = 0; j < pointsInSet.length - 1; j++) {
                for (int k = j + 1; k < pointsInSet.length; k++) {
                    costePorCluster[i] += instance.getDistanceBetween(pointsInSet[j], pointsInSet[k]);
                }
            }
        }
        double total = 0;
        for (int i = 0; i < costePorCluster.length; i++) {
            total += costePorCluster[i] / solution[i].size();
        }
        return total;
    }

    private static double[] centroid(Instance instance, Set<Integer> pointsIds) {
        double[] totals = null;
        for (var p : pointsIds) {
            double[] point = instance.getPoint(p);
            if (totals == null) totals = new double[point.length];
            for (int i = 0; i < point.length; i++) {
                totals[i] += point[i];
            }
        }

        int totalPoints = pointsIds.size();
        for (int i = 0; i < totals.length; i++) {
            totals[i] /= totalPoints;
        }
        return totals;
    }

    private static double averageDistanceToCentroid(Instance instance, Set<Integer> pointsIds) {
        var c = centroid(instance, pointsIds);
        double total = 0;
        for (var pId : pointsIds) {
            total += instance.distanceBetween(instance.getPoint(pId), c);
        }
        return total;
    }
}
