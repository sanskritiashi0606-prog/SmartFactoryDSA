package smartfactory.m3_dp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Bitmask Dynamic Programming for Machine Combinations & Sequencing.
 * Domain Use Case: Determines the optimal retooling/changeover sequence across
 * specialized factory machines to minimize total downtime and setup penalties.
 */
public class BitmaskDP {

    public static class SequenceResult {
        public final int minCost;
        public final List<Integer> sequenceOrder;

        public SequenceResult(int minCost, List<Integer> sequenceOrder) {
            this.minCost = minCost;
            this.sequenceOrder = sequenceOrder;
        }
    }

    /**
     * Finds the optimal machine transition sequence that visits each machine exactly once,
     * minimizing the total changeover/re-tooling penalty (TSP Tour via Bitmask DP).
     *
     * Time Complexity: O(N^2 * 2^N) where N is the number of machines.
     * Space Complexity: O(N * 2^N) for the bitmask state memoization table.
     *
     * @param changeoverMatrix NxN matrix where cost[i][j] is the re-tooling penalty from machine i to j
     * @return SequenceResult with minimal downtime cost and optimal sequence
     */
    public static SequenceResult findOptimalMachineSequence(int[][] changeoverMatrix) {
        int n = changeoverMatrix.length;
        if (n == 0) return new SequenceResult(0, new ArrayList<>());
        if (n == 1) return new SequenceResult(0, List.of(0));

        int allMask = (1 << n);
        int[][] dp = new int[allMask][n];
        int[][] parent = new int[allMask][n];

        for (int[] row : dp) {
            Arrays.fill(row, 1_000_000_000);
        }

        // Base case: starting at machine 0
        dp[1][0] = 0;

        for (int mask = 1; mask < allMask; mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0 || dp[mask][u] >= 1_000_000_000) {
                    continue;
                }

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0) { // If machine v has not been visited
                        int nextMask = mask | (1 << v);
                        int newCost = dp[mask][u] + changeoverMatrix[u][v];
                        if (newCost < dp[nextMask][v]) {
                            dp[nextMask][v] = newCost;
                            parent[nextMask][v] = u;
                        }
                    }
                }
            }
        }

        // Find the best ending machine to close the tour back to 0
        int fullMask = allMask - 1;
        int minTotalCost = Integer.MAX_VALUE;
        int lastMachine = -1;

        for (int u = 1; u < n; u++) {
            int tourCost = dp[fullMask][u] + changeoverMatrix[u][0];
            if (tourCost < minTotalCost) {
                minTotalCost = tourCost;
                lastMachine = u;
            }
        }

        // Reconstruct path
        List<Integer> order = new ArrayList<>();
        int currMask = fullMask;
        int currNode = lastMachine;

        while (currNode != 0) {
            order.add(0, currNode);
            int prevNode = parent[currMask][currNode];
            currMask ^= (1 << currNode);
            currNode = prevNode;
        }
        order.add(0, 0); // Include start machine
        order.add(0);    // Return to start machine to complete cycle

        return new SequenceResult(minTotalCost, order);
    }

    /**
     * Solves the Optimal Machine Assignment problem (e.g. assigning N tasks to N machines)
     * where each machine can perform a task with a specific efficiency score.
     *
     * Time Complexity: O(N * 2^N).
     * Space Complexity: O(2^N).
     *
     * @param efficiencyMatrix N x N matrix of efficiency scores
     * @return Maximum total achievable production efficiency
     */
    public static int maxEfficiencyAssignment(int[][] efficiencyMatrix) {
        int n = efficiencyMatrix.length;
        int numStates = 1 << n;
        int[] dp = new int[numStates];
        Arrays.fill(dp, -1);
        dp[0] = 0;

        for (int mask = 0; mask < numStates; mask++) {
            if (dp[mask] == -1) continue;
            // Number of set bits represents the task index currently being assigned
            int task = Integer.bitCount(mask);
            if (task >= n) continue;

            for (int machine = 0; machine < n; machine++) {
                if ((mask & (1 << machine)) == 0) {
                    int nextMask = mask | (1 << machine);
                    dp[nextMask] = Math.max(dp[nextMask], dp[mask] + efficiencyMatrix[task][machine]);
                }
            }
        }

        return dp[numStates - 1];
    }
}
