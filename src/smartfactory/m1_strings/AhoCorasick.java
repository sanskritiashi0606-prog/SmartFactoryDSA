package smartfactory.m1_strings;

import java.util.*;

/**
 * Aho-Corasick Multi-Pattern String Matching Automaton.
 * Domain Use Case: Detects multiple critical machine-fault keywords simultaneously
 * across large streams of industrial incident reports in a single linear pass.
 */
public class AhoCorasick {

    /**
     * Internal Trie node representation augmented with failure and dictionary links.
     */
    private static class Node {
        final Map<Character, Node> children = new HashMap<>();
        Node fail = null;
        final List<String> matchedKeywords = new ArrayList<>();
    }

    /**
     * Encapsulates a pattern match result within a report text.
     */
    public static class MatchResult {
        private final String keyword;
        private final int startIndex;
        private final int endIndex;

        public MatchResult(String keyword, int startIndex, int endIndex) {
            this.keyword = keyword;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getKeyword() {
            return keyword;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        @Override
        public String toString() {
            return String.format("[%s @ index %d..%d]", keyword, startIndex, endIndex);
        }
    }

    private final Node root;
    private final List<String> keywords;

    /**
     * Constructs the Aho-Corasick Trie and builds failure links across all fault keywords.
     *
     * Time Complexity: O(Sum of |Pattern_i| * Alphabet) to construct trie and BFS failure transitions.
     * Space Complexity: O(Sum of |Pattern_i| * Alphabet) for the state transition graph.
     *
     * @param faultKeywords List of critical fault terminology (e.g. "overheating", "cavitation", "vibration")
     */
    public AhoCorasick(List<String> faultKeywords) {
        this.root = new Node();
        this.keywords = new ArrayList<>(faultKeywords);
        buildTrie();
        buildFailureTransitions();
    }

    private void buildTrie() {
        for (String word : keywords) {
            if (word == null || word.isEmpty()) continue;
            String lowerWord = word.toLowerCase();
            Node current = root;
            for (char ch : lowerWord.toCharArray()) {
                current = current.children.computeIfAbsent(ch, c -> new Node());
            }
            current.matchedKeywords.add(lowerWord);
        }
    }

    private void buildFailureTransitions() {
        Queue<Node> queue = new ArrayDeque<>();

        // Level 1 children fail to root
        for (Node child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }

        // BFS traversal for subsequent levels
        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();

                Node fallback = current.fail;
                while (fallback != null && !fallback.children.containsKey(ch)) {
                    fallback = fallback.fail;
                }

                if (fallback == null) {
                    child.fail = root;
                } else {
                    child.fail = fallback.children.get(ch);
                }

                // Inherit matched keywords from failure path
                if (child.fail != null) {
                    child.matchedKeywords.addAll(child.fail.matchedKeywords);
                }

                queue.add(child);
            }
        }
    }

    /**
     * Scans a maintenance report for all configured machine-fault keywords simultaneously.
     * Case-insensitive matching is performed to handle varied technician logging styles.
     *
     * Time Complexity: O(N + Z) where N is report text length and Z is total occurrences found.
     * Space Complexity: O(Z) to store the match findings.
     *
     * @param reportContent Unstructured text of the maintenance report
     * @return List of MatchResult objects indicating detected keywords and their positions
     */
    public List<MatchResult> scanReport(String reportContent) {
        List<MatchResult> results = new ArrayList<>();
        if (reportContent == null || reportContent.isEmpty()) {
            return results;
        }

        Node current = root;
        String lowerContent = reportContent.toLowerCase();

        for (int i = 0; i < lowerContent.length(); i++) {
            char ch = lowerContent.charAt(i);

            // Follow failure transitions if character does not match
            while (current != null && !current.children.containsKey(ch)) {
                current = current.fail;
            }

            if (current == null) {
                current = root;
                continue;
            }

            current = current.children.get(ch);
            for (String matched : current.matchedKeywords) {
                int start = i - matched.length() + 1;
                // Preserve original substring casing in the hit or provide match
                results.add(new MatchResult(matched, start, i));
            }
        }

        return results;
    }
}
