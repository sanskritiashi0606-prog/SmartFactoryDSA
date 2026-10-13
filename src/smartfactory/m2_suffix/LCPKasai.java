package smartfactory.m2_suffix;

import java.util.ArrayList;
import java.util.List;

/**
 * Kasai's Algorithm for Longest Common Prefix (LCP) Array Construction.
 * Domain Use Case: Detects repeated failure modes, duplicated technician notes,
 * and recurring maintenance patterns across historical factory records in O(N) time.
 */
public class LCPKasai {

    /**
     * Computes the LCP array in linear time using Kasai's algorithm.
     * lcp[i] stores the length of the longest common prefix between
     * suffix sa[i] and suffix sa[i - 1].
     *
     * Time Complexity: O(N) strictly linear where N is text length.
     * Space Complexity: O(N) to store the inverted rank table and LCP array.
     *
     * @param text Maintenance log corpus
     * @param sa Suffix Array corresponding to the text
     * @return LCP array of length N (with lcp[0] = 0)
     */
    public static int[] computeLCP(String text, int[] sa) {
        if (text == null || sa == null || text.length() != sa.length) {
            return new int[0];
        }
        int n = text.length();
        int[] lcp = new int[n];
        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int h = 0; // Length of current common prefix
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int prevSuffixIndex = sa[rank[i] - 1];
                while (i + h < n && prevSuffixIndex + h < n && text.charAt(i + h) == text.charAt(prevSuffixIndex + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--;
                }
            } else {
                lcp[0] = 0;
            }
        }
        return lcp;
    }

    /**
     * Identifies the longest repeated fault description or phrase in the corpus.
     *
     * Time Complexity: O(N) to scan LCP array.
     * Space Complexity: O(1) auxiliary space.
     *
     * @param text Maintenance log corpus
     * @param sa Precomputed Suffix Array
     * @param lcp Precomputed LCP Array
     * @return The longest repeated diagnostic substring
     */
    public static String findLongestRepeatedFaultPhrase(String text, int[] sa, int[] lcp) {
        if (text == null || sa == null || lcp == null || lcp.length == 0) {
            return "";
        }
        int maxLcp = 0;
        int maxIdx = 0;

        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] > maxLcp) {
                maxLcp = lcp[i];
                maxIdx = sa[i];
            }
        }

        if (maxLcp == 0) return "";
        return text.substring(maxIdx, maxIdx + maxLcp);
    }

    /**
     * Extracts all recurring incident description phrases exceeding a given character threshold.
     *
     * Time Complexity: O(N)
     * Space Complexity: O(K) where K is number of qualifying repeated phrases.
     *
     * @param text Maintenance text
     * @param sa Suffix array
     * @param lcp LCP array
     * @param minLength Minimum phrase length to report
     * @return List of recurring diagnostic phrases
     */
    public static List<String> findRepeatedPhrasesAboveThreshold(String text, int[] sa, int[] lcp, int minLength) {
        List<String> phrases = new ArrayList<>();
        if (text == null || lcp == null) return phrases;

        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] >= minLength) {
                String candidate = text.substring(sa[i], sa[i] + lcp[i]);
                if (!phrases.contains(candidate)) {
                    phrases.add(candidate);
                }
            }
        }
        return phrases;
    }
}
