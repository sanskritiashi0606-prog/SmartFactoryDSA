package smartfactory.m2_suffix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compacted Suffix Tree Implementation.
 * Domain Use Case: Performs fast substring queries, multi-pattern occurrence counting,
 * and deepest repeated diagnostics analysis on factory operating documents.
 */
public class SuffixTree {

    public static class Node {
        final Map<Character, Edge> edges = new HashMap<>();
        int leafCount = 0; // Number of leaf descendants
    }

    public static class Edge {
        final int start;
        int end; // Inclusive
        final Node target;

        public Edge(int start, int end, Node target) {
            this.start = start;
            this.end = end;
            this.target = target;
        }

        public int length() {
            return end - start + 1;
        }
    }

    private final String text;
    private final Node root;

    /**
     * Constructs a Suffix Tree for factory text or maintenance narratives.
     *
     * Time Complexity: O(N^2) for straightforward insertion or O(N) with generalized Ukkonen.
     *                  Here implemented as robust compact trie insertion in O(N^2) time.
     * Space Complexity: O(N) nodes and edges.
     *
     * @param text Maintenance report or standard operating procedure manual
     */
    public SuffixTree(String text) {
        this.text = text;
        this.root = new Node();
        buildTree();
        computeLeafCounts(root);
    }

    private void buildTree() {
        int n = text.length();
        for (int i = 0; i < n; i++) {
            insertSuffix(i);
        }
    }

    private void insertSuffix(int suffixStart) {
        Node current = root;
        int i = suffixStart;
        int n = text.length();

        while (i < n) {
            char ch = text.charAt(i);
            Edge edge = current.edges.get(ch);

            if (edge == null) {
                // Create a new leaf edge
                Node leaf = new Node();
                current.edges.put(ch, new Edge(i, n - 1, leaf));
                return;
            }

            // Traverse existing edge
            int edgeLen = edge.length();
            int matchLen = 0;
            while (matchLen < edgeLen && i + matchLen < n &&
                    text.charAt(edge.start + matchLen) == text.charAt(i + matchLen)) {
                matchLen++;
            }

            if (matchLen == edgeLen) {
                // Consumed entire edge, descend to target node
                i += edgeLen;
                current = edge.target;
            } else {
                // Split edge
                Node splitNode = new Node();
                Edge nextEdge = new Edge(edge.start + matchLen, edge.end, edge.target);
                splitNode.edges.put(text.charAt(edge.start + matchLen), nextEdge);

                edge.end = edge.start + matchLen - 1;
                splitNode.edges.put(text.charAt(i + matchLen), new Edge(i + matchLen, n - 1, new Node()));

                // Link old edge to split node
                Edge newSplit = new Edge(edge.start, edge.end, splitNode);
                current.edges.put(ch, newSplit);
                return;
            }
        }
    }

    private int computeLeafCounts(Node node) {
        if (node.edges.isEmpty()) {
            node.leafCount = 1;
            return 1;
        }
        int count = 0;
        for (Edge edge : node.edges.values()) {
            count += computeLeafCounts(edge.target);
        }
        node.leafCount = count;
        return count;
    }

    /**
     * Checks if a target substring exists in the indexed factory report.
     *
     * Time Complexity: O(M) where M is query length.
     * Space Complexity: O(1) auxiliary space.
     *
     * @param query Target query substring
     * @return true if query exists in the document, false otherwise
     */
    public boolean containsSubstring(String query) {
        return countOccurrences(query) > 0;
    }

    /**
     * Counts the exact occurrences of a query substring in the indexed report.
     *
     * Time Complexity: O(M) where M is query length.
     * Space Complexity: O(1) auxiliary space.
     *
     * @param query Search keyword or part specification
     * @return Number of times query appears
     */
    public int countOccurrences(String query) {
        if (query == null || query.isEmpty()) return 0;
        Node current = root;
        int i = 0;
        int m = query.length();

        while (i < m) {
            char ch = query.charAt(i);
            Edge edge = current.edges.get(ch);
            if (edge == null) return 0;

            int edgeLen = edge.length();
            int cmpLen = Math.min(edgeLen, m - i);
            for (int k = 0; k < cmpLen; k++) {
                if (text.charAt(edge.start + k) != query.charAt(i + k)) {
                    return 0;
                }
            }
            i += cmpLen;
            current = edge.target;
        }

        return current.leafCount;
    }
}
