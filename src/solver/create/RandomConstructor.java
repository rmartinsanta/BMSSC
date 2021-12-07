package solver.create;

import model.Instance;
import model.Solution;
import other.RandomManager;

import java.util.ArrayList;
import java.util.Random;

public class RandomConstructor implements Constructor {

    @Override
    public Solution construct(Instance ins) {

        Random r = RandomManager.getRandom();
        Solution solution = new Solution(ins);

        ArrayList<Counter> counters = new ArrayList<>(ins.k);
        for (int i = 0; i < ins.k; i++) {
            counters.add(new Counter(i, ins.getClusterSize(i)));
        }

        for (int i = 0; i < ins.n; i++) {
            // nextInt [0, k) --> [1, k]
            int randomNumber = r.nextInt(counters.size());
            Counter c = counters.get(randomNumber);
            solution.assign(i, c.n);

            if (--c.counter == 0)
                counters.remove(randomNumber);
        }

        return solution.recalculateAllObjValue();
    }

    private class Counter {

        /**
         * Cluster id
         */
        public int n;
        //public AtomicInteger counter;

        /**
         * Number of points until cluster is full
         */
        public int counter;

        public Counter(int n, int p) {
            this.n = n;
            this.counter = p;
            //this.counter = new AtomicInteger(p);
        }
    }
}
