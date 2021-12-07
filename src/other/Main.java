package other;

import model.Instance;
import model.Result;
import solver.Algorithm;
import solver.GenericAlgorithmMulti;
import solver.create.GRASPConstructor;
import solver.improve.FirstImpLS;
import solver.improve.StrategicOscillation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Main {

//    static Algorithm[] exp1 = {
//            new ReallyGreedyAlgorithm(1),
//            new ReallyGreedyAlgorithm(100),
//            new GRASPAlgorithm(100, 0.25),
//            new GRASPAlgorithm(100, 0.5),
//            new GRASPAlgorithm(100, 0.75),
//            new GRASPAlgorithm(100, Double.MIN_VALUE),
//            new GRASPAlgorithm(100, Double.MAX_VALUE)
//    };
//
//    static Algorithm[] exp2 = {
//            new GenericAlgorithm(100, new GRASPConstructor(0.25), new BestImpLS()),
//            new GenericAlgorithm(100, new GRASPConstructor(0.5), new BestImpLS()),
//            new GenericAlgorithm(100, new GRASPConstructor(0.75), new BestImpLS()),
//            new GenericAlgorithm(100, new GRASPConstructor(Double.MAX_VALUE), new BestImpLS()),
//            new GenericAlgorithm(100, new GRASPConstructor(Double.MIN_VALUE), new BestImpLS()),
//            new GenericAlgorithm(100, new GRASPConstructor(0.25), new FirstImpLS()),
//            new GenericAlgorithm(100, new GRASPConstructor(0.5), new FirstImpLS()),
//            new GenericAlgorithm(100, new GRASPConstructor(0.75), new FirstImpLS()),
//            new GenericAlgorithm(100, new GRASPConstructor(Double.MAX_VALUE), new FirstImpLS()),
//            new GenericAlgorithm(100, new GRASPConstructor(Double.MIN_VALUE), new FirstImpLS()),
//            new GenericAlgorithm(100, new RandomConstructor(), new BestImpLS()),
//            new GenericAlgorithm(100, new RandomConstructor(), new FirstImpLS()),
//            new GenericAlgorithm(100, new GreedyConstructor(), new BestImpLS()),
//            new GenericAlgorithm(100, new GreedyConstructor(), new FirstImpLS()),
//    };
//
//    static Algorithm[] exp3 = {
//            new GraspOscillationFirstLSAlgorithm(100, RANDOMNESS, 60, 0.1f),
//            new GraspOscillationFirstLSAlgorithm(100, RANDOMNESS, 60, 0.25f),
//            new GraspOscillationFirstLSAlgorithm(100, RANDOMNESS, 60, 0.5f),
//            new GraspOscillationFirstLSAlgorithm(100, RANDOMNESS, 60, 0.75f),
//    };
//
//    static Algorithm[] exp4 = {
//            new GenericAlgorithmMulti(1000, new GRASPConstructor(RANDOMNESS, 2f), new FirstImpLS(), new StrategicOscillation(), new FirstImpLS(), new StrategicOscillation(), new FirstImpLS(), new StrategicOscillation(), new FirstImpLS(), new StrategicOscillation(), new FirstImpLS(), new StrategicOscillation(), new FirstImpLS(), new StrategicOscillation(), new FirstImpLS(), new StrategicOscillation(), new FirstImpLS())
//    };
//
//    static Algorithm[] exp5 = {
//            //new TimedAlgorithm(10, TimeUnit.SECONDS, () -> new GRASPConstructor(RANDOMNESS, 1f), FirstImpLS::new, StrategicOscillation::new, FirstImpLS::new)
//            new GenericAlgorithmMulti(100, new GRASPConstructor(RANDOMNESS, 0.75f), new FirstImpLS(), new StrategicOscillation(), new FirstImpLS())
//    };

    // sensitivity analysis experiment
    static Algorithm[] exp6 = {
            new GenericAlgorithmMulti(100, new GRASPConstructor(0.75, 0.75f), new FirstImpLS(), new StrategicOscillation(), new FirstImpLS())
    };

    static List<Result> results = new ArrayList<>();

    public static void main(String[] s) throws IOException {
        if(s.length != 1){
            System.out.println("Usage: java -jar file.jar path/to/indexfile");
            System.exit(-1);
        }
        System.out.println("Solver started, please wait for results...");
        loadIndex(s[0], Main::executeAlgorithmsForInstance);
        printTable(results);
        //exportToDisk(results);
    }

    public static void loadIndex(String indexPath, Consumer<String> method) throws IOException {
        Files.lines(Paths.get(indexPath))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .forEach(method);
    }

    private static void exportToDisk(List<Result> results) {
        for(Result r: results){
            String output = r.getInstanceName().split("/")[1] + ".out";
            File f = new File(output);
            var data = r.s.getSolution();
            StringBuilder sb = new StringBuilder();
            try (var writer = new FileWriter(f)) {
                for(var set: data){
                    for(var point: set){
                        sb.append(point).append(' ');
                    }
                    sb.append('\n');
                }
                writer.write(sb.toString());
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    private static void executeAlgorithmsForInstance(String line) {
        Instance ins = new Instance(line.split(","));

        for (Algorithm algorithm : exp6) {
                Result r = algorithm.execute(ins).setInstanceName(ins.getName()).setAlgorithmName(algorithm.getClass().toString());
                results.add(r);
                System.out.println(r);
        }
    }

    private static void executeForInstanceSensitivity(String line) {
        Instance ins = new Instance(line.split(","));
        for (Algorithm algorithm : exp6) {
            for (int i = 0; i < 30; i++) {
                Result r = algorithm.execute(ins).setInstanceName(ins.getName() + i).setAlgorithmName(algorithm.getClass().toString());
                results.add(r);
                System.out.println(r);
            }
        }
    }

    static void printTable(List<Result> results) {
        System.out.println("------- FORMATED DATA FOR COPY PASTE IN CALC --------");
        Arrays.stream(exp6).map(alg -> "\t\t" + alg.toString()).forEach(System.out::print);
        System.out.println();
        results.stream()
                .collect(Collectors.groupingBy(Result::getInstanceName)).entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(e -> {
                    System.out.print(e.getKey());
                    e.getValue().forEach(v -> System.out.print(("\t" + Double.toString(v.s.getOptimalValue()) + "\t" + v.getExecTime() / 1000000 / 1000d).replace('.', ',')));
                    System.out.println();
                });
    }

    public static void regenFiles() {
        ConversionUtil.convertFile("r/originales/Glass", "r/Glass", ConversionUtil.GLASS_CONVERTER);
        ConversionUtil.convertFile("r/originales/Ionosphere", "r/Ionosphere", ConversionUtil.IONOSPHERE_CONVERTER);
        ConversionUtil.convertFile("r/originales/Libra", "r/Libra", ConversionUtil.LIBRA_CONVERTER);
        ConversionUtil.convertFile("r/originales/UserKnowledge", "r/UserKnowledge", ConversionUtil.USER_KNOWLEDGE_CONVERTER);
        ConversionUtil.convertFile("r/originales/BreastCancer", "r/BreastCancer", ConversionUtil.BREAST_CONVERTER);
        ConversionUtil.convertFile("r/originales/SyntheticControl", "r/SyntheticControl", ConversionUtil.SYNTHETIC_CONTROL_CONVERTER);
        ConversionUtil.convertFile("r/originales/Vehicle", "r/Vehicle", ConversionUtil.VEHICLE_CONVERTER);
        ConversionUtil.convertFile("r/originales/MultipleFeatures", "r/MultipleFeatures", ConversionUtil.MULTIPLE_FEATURES_CONVERTER);
        ConversionUtil.convertFile("r/originales/ImageSegmentation", "r/ImageSegmentation", ConversionUtil.IMAGE_SEGMENTATION_CONVERTER);
    }

    public static void convertNewFiles() {
        ConversionUtil.convertFile("n/originales/PhonesAcc.csv", "n/PhonesAcc", ConversionUtil.ACC_GYRO_CONVERTER);
        ConversionUtil.convertFile("n/originales/PhonesGyro.csv", "n/PhonesGyro", ConversionUtil.ACC_GYRO_CONVERTER);
        ConversionUtil.convertFile("n/originales/WatchAcc.csv", "n/WatchAcc", ConversionUtil.ACC_GYRO_CONVERTER);
        ConversionUtil.convertFile("n/originales/WatchGyro.csv", "n/WatchGyro", ConversionUtil.ACC_GYRO_CONVERTER);
        ConversionUtil.convertFile("n/originales/InternetAds.txt", "n/InternetAds", ConversionUtil.INTERNET_ADS_CONVERTER);
    }

    public static void reduceNewFiles(int pointsLimit) {
        ConversionUtil.convertFile("n/WatchGyro", "n/WatchGyro.reduced", ConversionUtil.ACC_GYRO_REDUCER, pointsLimit);
        ConversionUtil.convertFile("n/WatchAcc", "n/WatchAcc.reduced", ConversionUtil.ACC_GYRO_REDUCER, pointsLimit);
        ConversionUtil.convertFile("n/PhonesAcc", "n/PhonesAcc.reduced", ConversionUtil.ACC_GYRO_REDUCER, pointsLimit);
        ConversionUtil.convertFile("n/PhonesGyro", "n/PhonesGyro.reduced", ConversionUtil.ACC_GYRO_REDUCER, pointsLimit);
    }
}
