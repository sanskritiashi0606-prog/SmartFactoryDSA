package smartfactory.m4_flow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * Dinic's Algorithm for High-Throughput Network Flow.
 * Domain Use Case: Scales to large-scale multi-tier factory supply and logistics networks,
 * using level graphs and blocking flows to maximize computational performance.
 */
public class Dinic {

    public static class Edge {
        public final int to;
        public final int rev;
        public int cap;
        public int flow;

        public Edge(int to, int rev, int cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
            this.flow = 0;
        }

        public int remainingCapacity() {
            return cap - flow;
        }
    }

    private final int n;
    private final List<List<Edge>> graph;
    private final int[] level;
    private final int[] ptr;

    /**
     * Initializes a Dinic network for N factory stations.
     *
     * Time Complexity: O(V)
     * Space Complexity: O(V + E)
     *
     * @param n Number of factory stations/nodes
     */
    public Dinic(int n) {
        this.n = n;
        this.graph = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        this.level = new int[n];
        this.ptr = new int[n];
    }

    /**
     * Adds a directed conveyor or piping connection with given hourly capacity.
     *
     * @param from Origin station
     * @param to Destination station
     * @param capacity Maximum hourly transfer volume
     */
    public void addEdge(int from, int to, int capacity) {
        Edge forward = new Edge(to, graph.get(to).size(), capacity);
        Edge backward = new Edge(from, graph.get(from).size(), 0);
        graph.get(from).add(forward);
        graph.get(to).add(backward);
    }

    /**
     * Computes the maximum flow from source to sink using Level Graph BFS and Blocking Flow DFS.
     *
     * Time Complexity: O(V^2 * E) general capacity networks, O(E * sqrt(V)) for unit networks.
     * Space Complexity: O(V + E) auxiliary space.
     *
     * @param source Raw supply intake
     * @param sink Finished dispatch terminal
     * @return Maximum total flow capacity
     */
    public int computeMaxFlow(int source, int sink) {
        int maxFlow = 0;

        while (bfs(source, sink)) {
            Arrays.fill(ptr, 0);
            while (true) {
                int pushed = dfs(source, sink, Integer.MAX_VALUE);
                if (pushed == 0) break;
                maxFlow += pushed;
            }
        }

        return maxFlow;
    }

    private boolean bfs(int source, int sink) {
        Arrays.fill(level, -1);
        level[source] = 0;
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (Edge edge : graph.get(u)) {
                if (edge.remainingCapacity() > 0 && level[edge.to] == -1) {
                    level[edge.to] = level[u] + 1;
                    queue.add(edge.to);
                }
            }
        }
        return level[sink] != -1;
    }

    private int dfs(int u, int sink, int pushed) {
        if (pushed == 0 || u == sink) return pushed;

        for (int cid = ptr[u]; cid < graph.get(u).size(); cid++) {
            ptr[u] = cid;
            Edge edge = graph.get(u).get(cid);
            int trg = edge.to;

            if (level[u] + 1 != level[trg] || edge.remainingCapacity() == 0) {
                continue;
            }

            int tr = dfs(trg, sink, Math.min(pushed, edge.remainingCapacity()));
            if (tr == 0) continue;

            edge.flow += tr;
            graph.get(trg).get(edge.rev).flow -= tr;
            return tr;
        }

        return 0;
    }

    public List<List<Edge>> getGraph() {
        return graph;
    }
}
