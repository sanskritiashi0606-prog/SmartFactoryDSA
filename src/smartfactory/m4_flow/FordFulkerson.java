package smartfactory.m4_flow;

import java.util.Arrays;

/**
 * Ford-Fulkerson Network Flow Algorithm (DFS-based Augmenting Paths).
 * Domain Use Case: Models continuous raw-material pipelines routing through
 * sequential factory processing stages to determine total achievable flow.
 */
public class FordFulkerson {

    /**
     * Computes maximum material flow from raw material intake (source)
     * to packaging dispatch (sink) using Depth-First Search for augmenting paths.
     *
     * Time Complexity: O(E * MaxFlow) where E is the number of conveyor/pipe connections.
     * Space Complexity: O(V^2) for the residual capacity adjacency matrix.
     *
     * @param capacity Directed pipeline capacity matrix between factory nodes
     * @param source Node index of raw material intake
     * @param sink Node index of finished goods dispatch
     * @return Maximum units of material routed per hour
     */
    public static int computeMaxFlow(int[][] capacity, int source, int sink) {
        int n = capacity.length;
        int[][] residual = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(capacity[i], 0, residual[i], 0, n);
        }

        int maxFlow = 0;
        boolean[] visited = new boolean[n];

        while (true) {
            Arrays.fill(visited, false);
            int bottleneck = dfs(residual, source, sink, Integer.MAX_VALUE, visited);
            if (bottleneck == 0) {
                break;
            }
            maxFlow += bottleneck;
        }

        return maxFlow;
    }

    private static int dfs(int[][] residual, int u, int sink, int flow, boolean[] visited) {
        if (u == sink) {
            return flow;
        }
        visited[u] = true;

        for (int v = 0; v < residual.length; v++) {
            if (!visited[v] && residual[u][v] > 0) {
                int bottleneck = dfs(residual, v, sink, Math.min(flow, residual[u][v]), visited);
                if (bottleneck > 0) {
                    residual[u][v] -= bottleneck;
                    residual[v][u] += bottleneck;
                    return bottleneck;
                }
            }
        }
        return 0;
    }
}
