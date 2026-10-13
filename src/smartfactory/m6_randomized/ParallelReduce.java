package smartfactory.m6_randomized;

/**
 * Parallel Tree Reduction Simulation for Large-Scale Factory Aggregation.
 * Domain Use Case: Aggregates real-time factory sensor data (e.g. total energy consumption,
 * maximum temperature peak, aggregate defect counts) across distributed edge nodes.
 */
public class ParallelReduce {

    public enum Operator {
        SUM, MAX, MIN
    }

    public static class ReductionResult {
        public final long aggregatedValue;
        public final int work;       // Total scalar operations executed
        public final int span;       // Parallel step depth (critical path length)
        public final double speedupWithPProcessors;

        public ReductionResult(long aggregatedValue, int work, int span, double speedupWithPProcessors) {
            this.aggregatedValue = aggregatedValue;
            this.work = work;
            this.span = span;
            this.speedupWithPProcessors = speedupWithPProcessors;
        }

        @Override
        public String toString() {
            return String.format("ReductionResult[Value=%d, Work(T1)=%d, Span(T_inf)=%d, Speedup(P)=%.2fx]",
                    aggregatedValue, work, span, speedupWithPProcessors);
        }
    }

    /**
     * Simulates parallel tree reduction over a stream of industrial telemetry readings.
     *
     * Complexity Analysis:
     *   - Work (T_1): O(N) operations.
     *   - Span (T_infinity): O(log N) tree depth.
     *   - Space: O(N) auxiliary array for tree reduction.
     *
     * @param sensorData Telemetry observations (e.g. instantaneous power draws in Watts)
     * @param op Reduction operation (SUM, MAX, MIN)
     * @param numProcessors Simulated parallel worker cores (P)
     * @return ReductionResult containing aggregate value and parallel execution metrics
     */
    public static ReductionResult reduce(long[] sensorData, Operator op, int numProcessors) {
        if (sensorData == null || sensorData.length == 0) {
            return new ReductionResult(0, 0, 0, 1.0);
        }
        int n = sensorData.length;
        if (n == 1) {
            return new ReductionResult(sensorData[0], 0, 0, 1.0);
        }

        long[] current = sensorData.clone();
        int active = n;
        int workOps = 0;
        int spanDepth = 0;

        while (active > 1) {
            spanDepth++;
            int nextSize = (active + 1) / 2;
            for (int i = 0; i < active / 2; i++) {
                int left = 2 * i;
                int right = 2 * i + 1;
                current[i] = applyOp(current[left], current[right], op);
                workOps++;
            }
            if (active % 2 != 0) {
                current[nextSize - 1] = current[active - 1]; // Carry over odd element
            }
            active = nextSize;
        }

        // Brent's Theorem estimate for simulated runtime: T_p <= (Work - Span) / P + Span
        double timeWithP = ((double) (workOps - spanDepth)) / Math.max(1, numProcessors) + spanDepth;
        double speedup = (double) workOps / Math.max(1.0, timeWithP);

        return new ReductionResult(current[0], workOps, spanDepth, speedup);
    }

    private static long applyOp(long a, long b, Operator op) {
        return switch (op) {
            case SUM -> a + b;
            case MAX -> Math.max(a, b);
            case MIN -> Math.min(a, b);
        };
    }
}
