package solver;

import model.Instance;
import model.Solution;
import solver.create.Constructor;
import solver.improve.Improver;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TimedAlgorythm implements Algorithm {

    private List<Improver> improvers;
    private Constructor constructor;
    private long nanos;

    @SafeVarargs
    public TimedAlgorythm(long time, TimeUnit unit, Supplier<Constructor> constructorSupplier, Supplier<Improver>... improvers) {
        this.constructor = constructorSupplier.get();
        this.improvers = Arrays.stream(improvers).map(Supplier::get).collect(Collectors.toList());
        this.nanos = unit.toNanos(time);
    }

    @Override
    public Solution algorithm(Instance ins) {
        long start = System.nanoTime();
        int iter = 0;

        Solution global = null;
        double globalValue = Double.MAX_VALUE;

        System.out.println();
        System.out.print("Iteración: ");
        while (System.nanoTime() - start < this.nanos){
            iter++;
            System.out.print(iter + "...");
            Solution s = null;
            double bestValue = Double.MAX_VALUE;
            for (int i = 0; i < 10; i++) {
                Solution temp = this.constructor.construct(ins);
                if (temp.getOptimalValue() < bestValue){
                    s = temp;
                    bestValue = s.getOptimalValue();
                }
            }
            assert s != null: "Construction failed";
            System.out.println();
            Solution compilerIsABitRetarded = s;
            this.improvers.forEach(x -> x.improve(compilerIsABitRetarded, 300));

            if(s.getOptimalValue() < globalValue){
                System.out.println("Better solution found!!");
                global = s;
                globalValue = global.getOptimalValue();
            }
        }

        assert global != null: "Wooops";
        return global;
    }
}
