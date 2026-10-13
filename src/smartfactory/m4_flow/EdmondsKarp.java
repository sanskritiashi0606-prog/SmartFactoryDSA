package smartfactory.m4_flow;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * Edmonds-Karp Network Flow Algorithm (BFS-based Shortest Augmenting Path).
 * Domain Use Case: Determines maximum material throughput through capacity-constrained
 * inter-station conveyor links in guaranteed polynomial time.
 */
public class EdmondsKarp {

    public static class FlowResult {
        public final int maxFlow;
        public final int[][] residualGraph;

        public FlowResult(int maxFlow, int[][] residualGraph) {
            this.maxFlow = maxFlow;
            this.residualGraph = residualGraph;
        }
    }

    /**
     * Computes the maximum material throughput from factory source to sink using BFS.
     *
     * Time Complexity: O(V * E^2) where V is station nodes and E is pipeline links.
     * Space Complexity: O(V^2) for the residual graph.
     *
     * @param capacity Directed capacity matrix between factory stations
     * @param source Raw intake node index
     * @param sink Packaging dispatch node index
     * @return FlowResult containing total throughput and residual graph
     */
    public static FlowResult computeMaxFlow(int[][] capacity, int source, int sink) {
        int n = capacity.length;
        int[][] residual = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(capacity[i], 0, residual[i], 0, n);
        }

        int maxFlow = 0;
        int[] parent = new int[n];

        while (bfs(residual, source, sink, parent)) {
            // Find minimum residual capacity along augmenting path
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residual[u][v]);
            }

            // Update residual capacities along path
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }

        return new FlowResult(maxFlow, residual);
    }

    private static boolean bfs(int[][] residual, int source, int sink, int[] parent) {
        Arrays.fill(parent, -1);
        parent[source] = source;
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (u == sink) {
                return true;
            }

            for (int v = 0; v < residual.length; v++) {
                if (parent[v] == -1 && residual[u][v] > 0) {
                    parent[v] = u;
                    queue.add(v);
                }
            }
        }

        return false;
    }
}
