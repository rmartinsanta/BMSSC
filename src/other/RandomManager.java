package other;

import java.util.Random;

public class RandomManager {

    private static final long SEED = 423152352;
    private static final Random r = new Random(SEED);

    /**
     * Get a Random instance, used for reproducibility
     * @return A Random instance
     */
    public static Random getRandom() {
        return r;
    }

}
