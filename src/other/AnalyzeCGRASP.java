package other;

import model.Instance;
import model.StrategicSolution;
import solver.improve.StrategicOscillation;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;
import static other.Debug.debug;

public class AnalyzeCGRASP {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("indexFile required");
            System.exit(-1);
        }
        System.out.println("Results, can be copy pasted in Calc, or imported as CSV in Excel: ");
        System.out.println("Instance Name,Declared Score,Calculated Score, Boulding Index, Calculated Score Fixed, Boulding Index Fixed");
        Main.loadIndex(args[0], AnalyzeCGRASP::analyzeResult);
    }

    private static void analyzeResult(String line) {
        String[] parts = line.split(",");
        var instance = new Instance(parts);
        printCGRASPScore(parts[0] + ".cgrasp", instance);
    }

    private static List<Set<Integer>> getOutputPointsSayCluster(Scanner sc, Instance ins) {
        ArrayList<Set<Integer>> sets = new ArrayList<>();
        for (int i = 0; i < ins.k; i++) {
            sets.add(new HashSet<>());
        }
        var doubles = getDoubles(sc);
        double[][] centroids = new double[ins.k][ins.d];
        for (int i = 0; i < doubles.size(); i++) {
            centroids[i / ins.d][i % ins.d] = doubles.get(i);
        }

        for (int i = 0; i < ins.n; i++) {
            double min = Double.MAX_VALUE;
            int k = -1;
            for (int j = 0; j < ins.k; j++) {
                double distance = ins.distanceBetween(centroids[j], ins.getPoint(i));
                if(distance < min){
                    min = distance;
                    k = j;
                }
            }
            assert k != -1;
            sets.get(k).add(i);
        }

        //noinspection unchecked
        return sets;
    }

    private static List<Double> getDoubles(Scanner sc){
        var list = new ArrayList<Double>();
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            if(line.isBlank() || line.contains("Column")) continue;
            for(String part: line.split("\\s+")){
                try {
                    double d = Double.parseDouble(part);
                    list.add(d);
                } catch(Exception e){
                    // Ignore parse errors
                }
            }
        }
        return list;
    }

    private static void printCGRASPScore(String filename, Instance instance) {
        try (Scanner sc = new Scanner(new FileInputStream(filename))){
            String token = sc.next();
            if(token.contains("FAIL")){
                System.out.format("%s,%s,%s,%s\n", filename, "*", "*", "*");
            } else {
                double expectedScore = -1;
                while(expectedScore == -1){
                    try {
                        expectedScore = Double.parseDouble(token);
                    } catch (Exception e){
                        token = sc.next();
                    }
                }
                var _sets = getOutputPointsSayCluster(sc, instance);
                if (_sets.size() != instance.k) {
                    throw new RuntimeException(String.format("Failed cluster validation, expected %s, got %s", instance.k, _sets.size()));
                }
                int realPoints = 0;
                for (Set<Integer> set : _sets) {
                    realPoints += set.size();
                }
                if (realPoints != instance.n) {
                    throw new RuntimeException(String.format("Failed size validation, expexted %s, got %s", instance.n, realPoints));
                }

                var notEmptySets = _sets.stream().filter(not(Set::isEmpty)).collect(Collectors.toList());

                double davies = daviesBuildinIndex(instance, notEmptySets);
                double oldMetric = oldMetric(instance, notEmptySets);

                var solution = buildFromImportedData(instance, _sets);
                _sets = solution.getSolutionAsList();
                double davies2 = daviesBuildinIndex(instance, _sets);
                double oldMetric2 = oldMetric(instance, _sets);
                System.out.format("%s,%s,%s,%s,%s,%s\n", filename, expectedScore, oldMetric, davies, oldMetric2, davies2);
            }
        } catch (Exception e) {
            System.out.println("Failed analyzing file: "+filename + ", error: "+ e + ", " + Arrays.toString(e.getStackTrace()));
            //throw new RuntimeException(e);
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
        double[] totals = new double[instance.d];
        for (var p : pointsIds) {
            double[] point = instance.getPoint(p);
            //if (totals == null) totals = new double[point.length];
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
