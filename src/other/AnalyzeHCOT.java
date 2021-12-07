package other;

import model.Instance;
import model.StrategicSolution;
import solver.improve.StrategicOscillation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.*;

public class AnalyzeHCOT {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("indexFile required");
            System.exit(-1);
        }
        System.out.println("Results, can be copy pasted in Calc, or imported as CSV in Excel: ");
        System.out.println("Instance Name,Declared Score,Calculated Score, Boulding Index, Calculated Score Fixed, Boulding Index Fixed");
        Main.loadIndex(args[0], AnalyzeHCOT::analyzeResult);
    }

    private static void analyzeResult(String line) {
        String[] parts = line.split(",");
        var instance = new Instance(parts);
        printHCOTScore(parts[0] + ".hcot", instance);
    }

    private static List<Set<Integer>> getOutputPointsSayCluster(Scanner sc) {
        ArrayList<Set<Integer>> sets = new ArrayList<>();
        int currentPoint = 0;
        while(sc.hasNextLine()){
            sc.nextLine();
            int[] parts = Arrays.stream(sc.nextLine().split("\\s+")).filter(not(String::isBlank)).mapToInt(Integer::parseInt).toArray();
            for(int cluster: parts){
                while (sets.size() < cluster) {
                    sets.add(new HashSet<>());
                }
                sets.get(cluster - 1).add(currentPoint++);
            }
        }

        return sets;
    }

    private static void printHCOTScore(String filename, Instance instance) {
        try (Scanner sc = new Scanner(new FileInputStream(filename))){
            String line = sc.nextLine();
            if(line.contains("FAIL")){
                System.out.format("%s,%s,%s,%s,%s,%s\n", filename, "*", "*", "*","*", "*");
            } else {
                double expectedScore = Double.parseDouble(line);
                List<Set<Integer>> _sets = getOutputPointsSayCluster(sc);
                if (_sets.size() != instance.k) {
                    throw new RuntimeException(String.format("Failed cluster validation, expexted %s, got %s", instance.k, _sets.size()));
                }
                int realPoints = 0;
                for (Set<Integer> set : _sets) {
                    realPoints += set.size();
                }
                if (realPoints != instance.n) {
                    throw new RuntimeException(String.format("Failed size validation, expexted %s, got %s", instance.n, realPoints));
                }

                double davies = daviesBuildinIndex(instance, _sets);
                double oldMetric = oldMetric(instance, _sets);

                var solution = buildFromImportedData(instance, _sets);

                _sets = solution.getSolutionAsList();
                double davies2 = daviesBuildinIndex(instance, _sets);
                double oldMetric2 = oldMetric(instance, _sets);

                System.out.format("%s,%s,%s,%s,%s,%s\n", filename, expectedScore, oldMetric, davies, oldMetric2, davies2);
            }
        } catch (Exception e) {
            System.out.println("Failed analyzing file: "+filename);
            throw new RuntimeException(e);
        }
    }

    private static StrategicSolution buildFromImportedData(Instance instance, List<Set<Integer>> sets) {
        var solution = new StrategicSolution(instance);
        for (int i = 0; i < sets.size(); i++) {
            for (int id : sets.get(i)) {
                solution.assign(id, i, solution.getAssignCost(id, i));
            }
        }
        StrategicOscillation strategicOscillation = new StrategicOscillation();
        strategicOscillation.fixClusters(solution);
        return solution;
    }


    private static double daviesBuildinIndex(Instance instance, List<Set<Integer>> clusters) {
        double[] avgDistToCentroid = new double[clusters.size()];
        double[][] centroids = new double[clusters.size()][];
        double total = 0;
        for (int i = 0; i < avgDistToCentroid.length; i++) {
            avgDistToCentroid[i] = averageDistanceToCentroid(instance, clusters.get(i));
            centroids[i] = centroid(instance, clusters.get(i));
        }

        for (int i = 0; i < clusters.size(); i++) {
            double max = Integer.MIN_VALUE;
            for (int j = 0; j < clusters.size(); j++) {
                if (i == j) continue;
                double value = (avgDistToCentroid[i] + avgDistToCentroid[j]) / instance.distanceBetween(centroids[i], centroids[j]);
                max = Math.max(max, value);
            }
            if (max == Integer.MIN_VALUE){
                throw new IllegalStateException();
            }
            total += max;
        }

        return total / clusters.size();
    }

    private static double oldMetric(Instance instance, List<Set<Integer>> solution) {
        double[] costePorCluster = new double[solution.size()];

        Integer[] type = new Integer[0];
        for (int i = 0; i < solution.size(); i++) {
            Set<Integer> integers = solution.get(i);
            Integer[] pointsInSet = integers.toArray(type);
            for (int j = 0; j < pointsInSet.length - 1; j++) {
                for (int k = j + 1; k < pointsInSet.length; k++) {
                    costePorCluster[i] += instance.getDistanceBetween(pointsInSet[j], pointsInSet[k]);
                }
            }
        }
        double total = 0;
        for (int i = 0; i < costePorCluster.length; i++) {
            total += costePorCluster[i] / solution.get(i).size();
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
