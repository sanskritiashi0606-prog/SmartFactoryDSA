package smartfactory.m3_dp;

import java.util.List;

/**
 * Edit Distance Dynamic Programming Suite.
 * Domain Use Case: Corrects miskeyed machine serial numbers, sensor tags, and
 * operator inputs in maintenance and production logs.
 */
public class EditDistance {

    /**
     * Computes standard Levenshtein Distance (Insert, Delete, Substitute each cost 1).
     *
     * Time Complexity: O(N * M) where N = s1.length(), M = s2.length().
     * Space Complexity: O(N * M) for the dynamic programming table.
     *
     * @param s1 Source string (e.g. entered machine serial)
     * @param s2 Target string (e.g. cataloged machine serial)
     * @return Minimum number of single-character operations required
     */
    public static int levenshteinDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;
        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int insert = dp[i][j - 1] + 1;
                    int delete = dp[i - 1][j] + 1;
                    int substitute = dp[i - 1][j - 1] + 1;
                    dp[i][j] = Math.min(insert, Math.min(delete, substitute));
                }
            }
        }
        return dp[n][m];
    }

    /**
     * Computes Damerau-Levenshtein Distance supporting adjacent character transpositions.
     * Useful for technician keyboard slips such as typing "MHC-101" instead of "MCH-101".
     *
     * Time Complexity: O(N * M).
     * Space Complexity: O(N * M).
     *
     * @param s1 Source serial or code
     * @param s2 Target canonical code
     * @return Distance accounting for insertions, deletions, substitutions, and transpositions
     */
    public static int damerauLevenshteinDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;
        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(dp[i - 1][j] + 1,                     // deletion
                           Math.min(dp[i][j - 1] + 1,                     // insertion
                                    dp[i - 1][j - 1] + cost));           // substitution

                // Transposition check
                if (i > 1 && j > 1 &&
                        s1.charAt(i - 1) == s2.charAt(j - 2) &&
                        s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }
        return dp[n][m];
    }

    /**
     * Computes Weighted Edit Distance with configurable penalties for insertion,
     * deletion, and substitution operations.
     *
     * Time Complexity: O(N * M).
     * Space Complexity: O(N * M).
     *
     * @param s1 Source string
     * @param s2 Target string
     * @param insertCost Penalty for character insertion
     * @param deleteCost Penalty for character deletion
     * @param substituteCost Penalty for character substitution
     * @return Total weighted edit penalty
     */
    public static int weightedEditDistance(String s1, String s2, int insertCost, int deleteCost, int substituteCost) {
        if (s1 == null || s2 == null) return 0;
        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i * deleteCost;
        for (int j = 0; j <= m; j++) dp[0][j] = j * insertCost;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int ins = dp[i][j - 1] + insertCost;
                    int del = dp[i - 1][j] + deleteCost;
                    int sub = dp[i - 1][j - 1] + substituteCost;
                    dp[i][j] = Math.min(ins, Math.min(del, sub));
                }
            }
        }
        return dp[n][m];
    }

    /**
     * Corrects a miskeyed machine identifier by selecting the closest matching valid ID.
     *
     * Time Complexity: O(K * N * M) where K is the number of valid machines.
     * Space Complexity: O(N * M).
     *
     * @param typoId The entered identifier containing potential typos
     * @param validMachineIds List of registered machine IDs
     * @return The most likely canonical machine ID
     */
    public static String findClosestMachineId(String typoId, List<String> validMachineIds) {
        if (validMachineIds == null || validMachineIds.isEmpty()) return typoId;
        String bestMatch = validMachineIds.get(0);
        int minDistance = Integer.MAX_VALUE;

        for (String validId : validMachineIds) {
            int dist = damerauLevenshteinDistance(typoId, validId);
            if (dist < minDistance) {
                minDistance = dist;
                bestMatch = validId;
            }
        }
        return bestMatch;
    }
}
