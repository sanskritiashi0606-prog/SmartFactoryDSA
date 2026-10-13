package smartfactory.m4_flow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Max-Flow Min-Cut Theorem Application for Industrial Bottleneck Identification.
 * Domain Use Case: Identifies critical pipeline constraints and throughput bottlenecks
 * whose capacity exhaustion limits the entire factory's production volume.
 */
public class MaxFlowMinCut {

    public static class BottleneckLink {
        public final int fromNode;
        public final int toNode;
        public final String fromLabel;
        public final String toLabel;
        public final int saturatedCapacity;

        public BottleneckLink(int fromNode, int toNode, String fromLabel, String toLabel, int saturatedCapacity) {
            this.fromNode = fromNode;
            this.toNode = toNode;
            this.fromLabel = fromLabel;
            this.toLabel = toLabel;
            this.saturatedCapacity = saturatedCapacity;
        }

        @Override
        public String toString() {
            return String.format("[%s -> %s (Cap=%d)]", fromLabel, toLabel, saturatedCapacity);
        }
    }

    public static class MinCutResult {
        public final int maxThroughput;
        public final List<Integer> sourceSet;
        public final List<Integer> sinkSet;
        public final List<BottleneckLink> criticalBottlenecks;

        public MinCutResult(int maxThroughput, List<Integer> sourceSet, List<Integer> sinkSet, List<BottleneckLink> criticalBottlenecks) {
            this.maxThroughput = maxThroughput;
            this.sourceSet = sourceSet;
            this.sinkSet = sinkSet;
            this.criticalBottlenecks = criticalBottlenecks;
        }
    }

    /**
     * Identifies the minimum-cut bottleneck links across the factory flow network.
     *
     * Time Complexity: O(V * E^2) to compute max-flow followed by O(V + E) BFS traversal.
     * Space Complexity: O(V^2) for the residual capacity graph.
     *
     * @param capacity Directed capacity matrix between factory stations
     * @param source Raw supply station index
     * @param sink Finished dispatch station index
     * @param stationNames Human-readable labels for each station
     * @return MinCutResult identifying total throughput and critical saturated bottleneck connections
     */
    public static MinCutResult findBottlenecks(int[][] capacity, int source, int sink, String[] stationNames) {
        int n = capacity.length;
        EdmondsKarp.FlowResult flowResult = EdmondsKarp.computeMaxFlow(capacity, source, sink);
        int[][] residual = flowResult.residualGraph;

        // BFS to find all stations reachable from source in residual graph
        boolean[] reachable = new boolean[n];
        Queue<Integer> queue = new ArrayDeque<>();
        reachable[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < n; v++) {
                if (!reachable[v] && residual[u][v] > 0) {
                    reachable[v] = true;
                    queue.add(v);
                }
            }
        }

        List<Integer> sourcePartition = new ArrayList<>();
        List<Integer> sinkPartition = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (reachable[i]) {
                sourcePartition.add(i);
            } else {
                sinkPartition.add(i);
            }
        }

        // Find edges (u, v) where u is reachable and v is not, with original capacity > 0
        List<BottleneckLink> bottlenecks = new ArrayList<>();
        for (int u : sourcePartition) {
            for (int v : sinkPartition) {
                if (capacity[u][v] > 0) {
                    String uLabel = (stationNames != null && u < stationNames.length) ? stationNames[u] : "Node" + u;
                    String vLabel = (stationNames != null && v < stationNames.length) ? stationNames[v] : "Node" + v;
                    bottlenecks.add(new BottleneckLink(u, v, uLabel, vLabel, capacity[u][v]));
                }
            }
        }

        return new MinCutResult(flowResult.maxFlow, sourcePartition, sinkPartition, bottlenecks);
    }
}
