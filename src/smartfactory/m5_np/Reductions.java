package smartfactory.m5_np;

import java.util.*;

/**
 * Computational Complexity Reductions Suite.
 * Demonstrates polynomial-time Karp reductions linking classic NP-Complete problems:
 *   3-SAT -> CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER
 *
 * Domain Use Case: Proves hardness of production optimization decisions and transforms
 * factory constraint satisfiability problems into graph-theoretic monitoring structures.
 */
public class Reductions {

    /**
     * Graph representation using an adjacency matrix.
     */
    public static class Graph {
        public final int numVertices;
        public final boolean[][] adj;
        public final List<String> vertexLabels;

        public Graph(int numVertices) {
            this.numVertices = numVertices;
            this.adj = new boolean[numVertices][numVertices];
            this.vertexLabels = new ArrayList<>(numVertices);
            for (int i = 0; i < numVertices; i++) {
                vertexLabels.add("V" + i);
            }
        }

        public void addEdge(int u, int v) {
            if (u != v) {
                adj[u][v] = true;
                adj[v][u] = true;
            }
        }

        public int countEdges() {
            int count = 0;
            for (int i = 0; i < numVertices; i++) {
                for (int j = i + 1; j < numVertices; j++) {
                    if (adj[i][j]) count++;
                }
            }
            return count;
        }
    }

    public static class ReductionResult {
        public final Graph graph;
        public final int targetSize;
        public final String description;

        public ReductionResult(Graph graph, int targetSize, String description) {
            this.graph = graph;
            this.targetSize = targetSize;
            this.description = description;
        }
    }

    /**
     * Reduces a 3-SAT formula with k clauses to a CLIQUE problem instance.
     * Construct a graph with 3k vertices (one for each literal in each clause).
     * Connect vertex u to vertex v iff they belong to different clauses and their
     * literals are not negations of each other.
     *
     * Time Complexity: O(K^2) strictly polynomial where K is number of clauses.
     * Space Complexity: O(K^2) for the constructed graph adjacency matrix.
     *
     * @param threeSatClauses List of clauses where each clause has at most 3 literals
     * @return ReductionResult containing graph and target clique size k
     */
    public static ReductionResult reduce3SatToClique(List<List<Integer>> threeSatClauses) {
        int k = threeSatClauses.size();
        int totalVertices = 0;
        for (List<Integer> c : threeSatClauses) {
            totalVertices += c.size();
        }

        Graph g = new Graph(totalVertices);
        List<int[]> literalInfo = new ArrayList<>(); // [clauseIndex, literalValue]

        int vIdx = 0;
        for (int c = 0; c < k; c++) {
            List<Integer> clause = threeSatClauses.get(c);
            for (int lit : clause) {
                literalInfo.add(new int[]{c, lit});
                g.vertexLabels.set(vIdx, String.format("C%d:Lit(%s%d)", c + 1, lit > 0 ? "+" : "-", Math.abs(lit)));
                vIdx++;
            }
        }

        // Add edges between consistent literals in different clauses
        for (int i = 0; i < totalVertices; i++) {
            int clauseI = literalInfo.get(i)[0];
            int litI = literalInfo.get(i)[1];

            for (int j = i + 1; j < totalVertices; j++) {
                int clauseJ = literalInfo.get(j)[0];
                int litJ = literalInfo.get(j)[1];

                if (clauseI != clauseJ && litI != -litJ) {
                    g.addEdge(i, j);
                }
            }
        }

        return new ReductionResult(g, k,
                String.format("3-SAT -> CLIQUE: Built %d vertices and %d edges. 3-SAT is satisfiable iff Graph has Clique of size %d.",
                        totalVertices, g.countEdges(), k));
    }

    /**
     * Reduces a CLIQUE instance (Graph G, size k) to an INDEPENDENT-SET instance.
     * The complement graph G_comp has an independent set of size k iff G has a clique of size k.
     *
     * Time Complexity: O(V^2) polynomial time.
     * Space Complexity: O(V^2).
     *
     * @param original Graph G
     * @param k Target clique size
     * @return ReductionResult containing the complement graph and target independent set size k
     */
    public static ReductionResult reduceCliqueToIndependentSet(Graph original, int k) {
        int n = original.numVertices;
        Graph complement = new Graph(n);
        for (int i = 0; i < n; i++) {
            complement.vertexLabels.set(i, original.vertexLabels.get(i));
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (!original.adj[i][j]) {
                    complement.addEdge(i, j);
                }
            }
        }

        return new ReductionResult(complement, k,
                String.format("CLIQUE -> INDEPENDENT-SET: Constructed complement graph (%d vertices, %d edges). G has Clique(k=%d) iff G_comp has IndependentSet(k=%d).",
                        n, complement.countEdges(), k, k));
    }

    /**
     * Reduces an INDEPENDENT-SET instance (Graph G, size k) to a VERTEX-COVER instance.
     * S is an independent set of size k in G iff V \ S is a vertex cover of size |V| - k.
     *
     * Time Complexity: O(V) polynomial time.
     * Space Complexity: O(1) auxiliary space (operates on same graph topology).
     *
     * @param g Graph G
     * @param k Target independent set size
     * @return ReductionResult specifying target vertex cover size |V| - k
     */
    public static ReductionResult reduceIndependentSetToVertexCover(Graph g, int k) {
        int n = g.numVertices;
        int targetCoverSize = n - k;

        return new ReductionResult(g, targetCoverSize,
                String.format("INDEPENDENT-SET -> VERTEX-COVER: On same graph of %d vertices, IndependentSet of size %d exists iff VertexCover of size %d (|V| - k) exists.",
                        n, k, targetCoverSize));
    }
}
