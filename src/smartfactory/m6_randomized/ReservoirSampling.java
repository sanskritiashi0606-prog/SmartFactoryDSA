package smartfactory.m6_randomized;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Reservoir Sampling Algorithm (Algorithm R).
 * Domain Use Case: Retains an exact, unbiased uniform random sample of K telemetry points
 * from continuous, high-frequency IoT sensor streams of infinite or unknown total duration.
 */
public class ReservoirSampling {

    /**
     * Stateful streaming sampler that consumes telemetry records on-the-fly.
     *
     * @param <T> Sensor telemetry or incident data type
     */
    public static class StreamReservoir<T> {
        private final int k;
        private final List<T> reservoir;
        private int totalCount;
        private final Random random;

        public StreamReservoir(int sampleSize) {
            this(sampleSize, new Random(2026));
        }

        public StreamReservoir(int sampleSize, Random random) {
            this.k = sampleSize;
            this.reservoir = new ArrayList<>(sampleSize);
            this.totalCount = 0;
            this.random = random;
        }

        /**
         * Ingests the next telemetry reading from the machine stream.
         *
         * Time Complexity: O(1) per arriving sensor item.
         * Space Complexity: O(K) where K is the target sample size.
         *
         * @param item Next streaming sensor observation
         */
        public void accept(T item) {
            totalCount++;
            if (reservoir.size() < k) {
                reservoir.add(item);
            } else {
                int replaceIdx = random.nextInt(totalCount);
                if (replaceIdx < k) {
                    reservoir.set(replaceIdx, item);
                }
            }
        }

        public List<T> getSample() {
            return new ArrayList<>(reservoir);
        }

        public int getTotalItemsObserved() {
            return totalCount;
        }

        public int getSampleSize() {
            return reservoir.size();
        }
    }

    /**
     * Batch reservoir sampling for pre-collected continuous streams.
     *
     * Time Complexity: O(N) where N is total items in the stream.
     * Space Complexity: O(K) where K is sample size.
     *
     * @param stream List of sensor observations
     * @param k Target sample size
     * @param <T> Data type
     * @return Uniform random subset of size min(K, stream.size())
     */
    public static <T> List<T> sample(List<T> stream, int k) {
        if (stream == null || k <= 0) return new ArrayList<>();
        StreamReservoir<T> sampler = new StreamReservoir<>(k);
        for (T item : stream) {
            sampler.accept(item);
        }
        return sampler.getSample();
    }
}
