package other;

import model.Instance;
import model.Result;
import solver.Algorithm;
import solver.GenericAlgorithmMulti;
import solver.create.GRASPConstructor;
import solver.improve.FirstImpLS;
import solver.improve.StrategicOscillation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class Main {

    // sensitivity analysis experiment
    static Algorithm[] exp6 = {
            new GenericAlgorithmMulti(100, new GRASPConstructor(0.75, 0.75f), new FirstImpLS(), new StrategicOscillation(), new FirstImpLS())
    };

    public static void main(String[] s) throws IOException {
        if (s.length != 1) {
            System.out.println("Usage: java -jar file.jar path/to/indexfile");
            System.exit(-1);
        }
        System.out.println("Solver started, please wait for results...");
        loadIndex(s[0], Main::executeAlgorithmsForInstance);
    }

    public static void loadIndex(String indexPath, Consumer<String> method) throws IOException {
        Files.lines(Paths.get(indexPath))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .filter(line -> !line.startsWith("#"))
                .forEach(method);
    }

    private static void executeAlgorithmsForInstance(String line) {
        int repetitions = 30;
        Instance ins = new Instance(line.split(","));

        for (int i = 0; i < repetitions; i++) {
            for (Algorithm algorithm : exp6) {
                Result r = algorithm.execute(ins).setInstanceName(ins.getName()).setAlgorithmName(algorithm.getClass().toString());
                r.setRepetitions(i);
                System.out.println(r);
            }
        }
    }
}
