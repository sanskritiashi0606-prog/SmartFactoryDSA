package smartfactory.m5_np;

import java.util.*;

/**
 * 2-Approximation Algorithm for Minimum Vertex Cover.
 * Domain Use Case: Identifies a minimal subset of industrial machines to equip with
 * safety interlocks/monitoring gantries such that all mutual interference and hazard
 * conflict edges between adjacent machine stations are fully covered.
 */
public class VertexCoverApprox {

    public static class CoverResult {
        public final Set<Integer> coveredMachineIndices;
        public final int coverSize;
        public final double approximationRatioBound;

        public CoverResult(Set<Integer> coveredMachineIndices, int coverSize, double approximationRatioBound) {
            this.coveredMachineIndices = coveredMachineIndices;
            this.coverSize = coverSize;
            this.approximationRatioBound = approximationRatioBound;
        }

        @Override
        public String toString() {
            return String.format("VertexCover[Size=%d, Machines=%s, ApproxFactor<=%.1f]",
                    coverSize, coveredMachineIndices, approximationRatioBound);
        }
    }

    /**
     * Computes a 2-approximation of the Minimum Vertex Cover using the maximal matching heuristic.
     * Continuously picks an uncovered edge (u, v), adds both endpoints to the cover,
     * and deletes all edges incident to u or v.
     *
     * Time Complexity: O(V + E) linear in graph size.
     * Space Complexity: O(V) to store the cover set and visited node markers.
     *
     * @param numMachines Total number of machine stations
     * @param conflictEdges List of pairs [u, v] representing mutual interference/hazard conflicts
     * @return CoverResult with chosen machine set and 2.0 approximation bound guarantee
     */
    public static CoverResult compute2Approximation(int numMachines, List<int[]> conflictEdges) {
        Set<Integer> cover = new LinkedHashSet<>();
        boolean[] inCover = new boolean[numMachines];

        for (int[] edge : conflictEdges) {
            int u = edge[0];
            int v = edge[1];

            // If neither endpoint is currently in the cover, add both
            if (!inCover[u] && !inCover[v]) {
                inCover[u] = true;
                inCover[v] = true;
                cover.add(u);
                cover.add(v);
            }
        }

        return new CoverResult(cover, cover.size(), 2.0);
    }

    /**
     * Computes the EXACT optimal Minimum Vertex Cover via exhaustive search (for small N <= 20)
     * to empirically verify the approximation ratio achieved in practice.
     *
     * Time Complexity: O(2^V * (V + E)).
     * Space Complexity: O(V).
     *
     * @param numMachines Number of machines (N <= 20)
     * @param conflictEdges Conflict edges
     * @return CoverResult containing exact minimal vertex cover
     */
    public static CoverResult computeExactMinimumCover(int numMachines, List<int[]> conflictEdges) {
        int bestSize = numMachines;
        Set<Integer> bestCover = new LinkedHashSet<>();

        int totalSubsets = 1 << numMachines;
        for (int mask = 0; mask < totalSubsets; mask++) {
            int size = Integer.bitCount(mask);
            if (size >= bestSize) continue;

            boolean coversAll = true;
            for (int[] edge : conflictEdges) {
                int u = edge[0];
                int v = edge[1];
                if ((mask & (1 << u)) == 0 && (mask & (1 << v)) == 0) {
                    coversAll = false;
                    break;
                }
            }

            if (coversAll) {
                bestSize = size;
                bestCover.clear();
                for (int i = 0; i < numMachines; i++) {
                    if ((mask & (1 << i)) != 0) {
                        bestCover.add(i);
                    }
                }
            }
        }

        return new CoverResult(bestCover, bestSize, 1.0);
    }
}
