package smartfactory.m6_randomized;

import java.util.Arrays;

/**
 * Blelloch Parallel Prefix Sum (Exclusive Scan) Algorithm.
 * Domain Use Case: Computes running cumulative production statistics and timeline offsets
 * across parallel manufacturing cells with work-efficient O(N) operations and O(log N) parallel depth.
 */
public class BlellochScan {

    public static class ScanResult {
        public final int[] exclusivePrefixSum;
        public final int[] inclusivePrefixSum;
        public final int totalSum;
        public final int work;  // Total operations
        public final int span;  // Critical path depth

        public ScanResult(int[] exclusivePrefixSum, int[] inclusivePrefixSum, int totalSum, int work, int span) {
            this.exclusivePrefixSum = exclusivePrefixSum;
            this.inclusivePrefixSum = inclusivePrefixSum;
            this.totalSum = totalSum;
            this.work = work;
            this.span = span;
        }
    }

    /**
     * Executes the work-efficient Blelloch Scan simulating parallel Up-Sweep and Down-Sweep phases.
     *
     * Complexity Analysis:
     *   - Work (T_1): O(N) additions strictly linear work.
     *   - Span (T_infinity): O(log N) parallel step depth.
     *   - Space: O(N) padded to nearest power of 2.
     *
     * @param productionUnits Hourly output units array from parallel factory lines
     * @return ScanResult with exclusive and inclusive cumulative totals, work, and span metrics
     */
    public static ScanResult executeScan(int[] productionUnits) {
        if (productionUnits == null || productionUnits.length == 0) {
            return new ScanResult(new int[0], new int[0], 0, 0, 0);
        }

        int origLen = productionUnits.length;
        int n = 1;
        while (n < origLen) {
            n <<= 1; // Pad to next power of 2
        }

        int[] tree = new int[n];
        System.arraycopy(productionUnits, 0, tree, 0, origLen);

        int workOps = 0;
        int depthSteps = 0;

        // --- Phase 1: Up-Sweep (Parallel Reduction Tree) ---
        for (int stride = 1; stride < n; stride *= 2) {
            depthSteps++;
            for (int i = 2 * stride - 1; i < n; i += 2 * stride) {
                tree[i] += tree[i - stride];
                workOps++;
            }
        }

        int totalSum = tree[n - 1];

        // --- Phase 2: Down-Sweep (Prefix Distribution) ---
        tree[n - 1] = 0; // Set root of the tree to zero
        for (int stride = n / 2; stride >= 1; stride /= 2) {
            depthSteps++;
            for (int i = 2 * stride - 1; i < n; i += 2 * stride) {
                int leftChildVal = tree[i - stride];
                tree[i - stride] = tree[i];
                tree[i] += leftChildVal;
                workOps++;
            }
        }

        // Extract exclusive and inclusive prefix sums for original input size
        int[] exclusive = new int[origLen];
        int[] inclusive = new int[origLen];
        System.arraycopy(tree, 0, exclusive, 0, origLen);

        for (int i = 0; i < origLen; i++) {
            inclusive[i] = exclusive[i] + productionUnits[i];
        }

        return new ScanResult(exclusive, inclusive, totalSum, workOps, depthSteps);
    }
}
