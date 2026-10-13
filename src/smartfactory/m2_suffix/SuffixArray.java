package smartfactory.m2_suffix;

import java.util.Arrays;

/**
 * Suffix Array Data Structure for Industrial Document Indexing.
 * Domain Use Case: Indexes factory operating manuals, machine blueprints, and
 * historical maintenance archives to perform logarithmic time pattern queries.
 */
public class SuffixArray {

    private final String document;
    private final int n;
    private final int[] sa;

    /**
     * Constructs a Suffix Array for the target factory document using prefix doubling.
     *
     * Time Complexity: O(N * (log N)^2) where N is document length.
     * Space Complexity: O(N) to store ranks and suffix array.
     *
     * @param document Factory technical document or historical maintenance log
     */
    public SuffixArray(String document) {
        this.document = document;
        this.n = document.length();
        this.sa = buildSuffixArray(document);
    }

    private static class SuffixRank implements Comparable<SuffixRank> {
        int index;
        int rank1;
        int rank2;

        @Override
        public int compareTo(SuffixRank o) {
            if (this.rank1 != o.rank1) {
                return Integer.compare(this.rank1, o.rank1);
            }
            return Integer.compare(this.rank2, o.rank2);
        }
    }

    private int[] buildSuffixArray(String text) {
        if (n == 0) return new int[0];
        SuffixRank[] suffixes = new SuffixRank[n];

        for (int i = 0; i < n; i++) {
            suffixes[i] = new SuffixRank();
            suffixes[i].index = i;
            suffixes[i].rank1 = text.charAt(i);
            suffixes[i].rank2 = (i + 1 < n) ? text.charAt(i + 1) : -1;
        }

        Arrays.sort(suffixes);
        int[] ind = new int[n];

        for (int k = 4; k < 2 * n; k *= 2) {
            int rank = 0;
            int prevRank1 = suffixes[0].rank1;
            suffixes[0].rank1 = rank;
            ind[suffixes[0].index] = 0;

            for (int i = 1; i < n; i++) {
                if (suffixes[i].rank1 == prevRank1 && suffixes[i].rank2 == suffixes[i - 1].rank2) {
                    prevRank1 = suffixes[i].rank1;
                    suffixes[i].rank1 = rank;
                } else {
                    prevRank1 = suffixes[i].rank1;
                    suffixes[i].rank1 = ++rank;
                }
                ind[suffixes[i].index] = i;
            }

            for (int i = 0; i < n; i++) {
                int nextIndex = suffixes[i].index + k / 2;
                suffixes[i].rank2 = (nextIndex < n) ? suffixes[ind[nextIndex]].rank1 : -1;
            }

            Arrays.sort(suffixes);
        }

        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = suffixes[i].index;
        }
        return result;
    }

    /**
     * Performs binary search on the suffix array to check if a pattern exists in the document.
     *
     * Time Complexity: O(M * log N) where M = pattern length, N = document length.
     * Space Complexity: O(1) auxiliary space.
     *
     * @param pattern Substring to find (e.g. part code or diagnostic phrase)
     * @return true if pattern appears in document, false otherwise
     */
    public boolean contains(String pattern) {
        if (pattern == null || pattern.isEmpty() || n == 0) return false;
        int m = pattern.length();
        int left = 0, right = n - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int suffixStart = sa[mid];
            int cmp = comparePatternToSuffix(pattern, suffixStart, m);

            if (cmp == 0) {
                return true;
            } else if (cmp < 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return false;
    }

    /**
     * Counts how many times a given keyword appears across the indexed document.
     *
     * Time Complexity: O(M * log N)
     * Space Complexity: O(1)
     *
     * @param pattern Query string
     * @return Frequency count of the pattern in the document
     */
    public int countOccurrences(String pattern) {
        if (pattern == null || pattern.isEmpty() || n == 0) return 0;
        int m = pattern.length();

        // Find lower bound
        int left = 0, right = n - 1;
        int first = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = comparePatternToSuffix(pattern, sa[mid], m);
            if (cmp <= 0) {
                if (cmp == 0) first = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        if (first == -1) return 0;

        // Find upper bound
        left = 0;
        right = n - 1;
        int last = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = comparePatternToSuffix(pattern, sa[mid], m);
            if (cmp >= 0) {
                if (cmp == 0) last = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return (last - first + 1);
    }

    private int comparePatternToSuffix(String pattern, int suffixStart, int m) {
        int len = Math.min(m, n - suffixStart);
        for (int i = 0; i < len; i++) {
            char pChar = pattern.charAt(i);
            char sChar = document.charAt(suffixStart + i);
            if (pChar != sChar) {
                return Character.compare(pChar, sChar);
            }
        }
        if (m <= n - suffixStart) return 0;
        return 1;
    }

    public int[] getSuffixArray() {
        return sa.clone();
    }

    public String getDocument() {
        return document;
    }
}
