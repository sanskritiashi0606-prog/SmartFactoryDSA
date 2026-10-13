package smartfactory.m6_randomized;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Brent's Theorem (Brent's Scheduling Lemma) Framework.
 * Domain Use Case: Evaluates theoretical processing speedup, core efficiency,
 * and hardware sizing bounds for parallel factory data ingestion pipelines.
 *
 * Lemma: For a parallel computation with Work W and Span S running on P processors,
 *        Execution Time T_P <= (W - S) / P + S
 */
public class BrentsTheorem {

    public static class EfficiencyProfile {
        public final int processors;
        public final double theoreticalTime;
        public final double speedup;
        public final double efficiencyPercentage;

        public EfficiencyProfile(int processors, double theoreticalTime, double speedup, double efficiencyPercentage) {
            this.processors = processors;
            this.theoreticalTime = theoreticalTime;
            this.speedup = speedup;
            this.efficiencyPercentage = efficiencyPercentage;
        }

        @Override
        public String toString() {
            return String.format("Cores=%d | Time=%.2f units | Speedup=%.2fx | Efficiency=%.1f%%",
                    processors, theoreticalTime, speedup, efficiencyPercentage);
        }
    }

    /**
     * Evaluates parallel performance for a specific worker core allocation P.
     *
     * Time Complexity: O(1).
     * Space Complexity: O(1).
     *
     * @param work Total sequential operations (W = T_1)
     * @param span Critical path length / tree depth (S = T_infinity)
     * @param processors Number of available parallel processing cores (P)
     * @return EfficiencyProfile with execution time, speedup, and utilization percentage
     */
    public static EfficiencyProfile evaluate(long work, long span, int processors) {
        if (processors <= 0) processors = 1;
        double tP = ((double) (work - span)) / processors + span;
        double speedup = (double) work / tP;
        double efficiency = (speedup / processors) * 100.0;

        return new EfficiencyProfile(processors, tP, speedup, efficiency);
    }

    /**
     * Calculates the half-power processor count (P_1/2), representing the number
     * of processors required to achieve 50% of the theoretical maximum speedup (W / S).
     *
     * @param work Total work
     * @param span Critical path span
     * @return Minimum processors required for half-power efficiency
     */
    public static int computeHalfPowerProcessorCount(long work, long span) {
        if (span <= 0) return 1;
        return (int) Math.max(1, (work - span) / span);
    }

    /**
     * Generates a scalability profile across varying server core counts.
     *
     * @param work Total work operations
     * @param span Critical path span
     * @param processorCounts Array of core allocations to benchmark (e.g. {1, 2, 4, 8, 16, 32})
     * @return Map linking core count to its EfficiencyProfile
     */
    public static Map<Integer, EfficiencyProfile> generateScalabilityReport(long work, long span, int[] processorCounts) {
        Map<Integer, EfficiencyProfile> report = new LinkedHashMap<>();
        for (int p : processorCounts) {
            report.put(p, evaluate(work, span, p));
        }
        return report;
    }
}
