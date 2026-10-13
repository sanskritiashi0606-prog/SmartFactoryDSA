package smartfactory.m1_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Z-Algorithm (Z-Function) Implementation.
 * Domain Use Case: Detects repetitive vibration signatures, cyclic sensor readings,
 * and recurring phrase anomalies within SmartFactory maintenance logs.
 */
public class ZFunction {

    /**
     * Computes the Z-array for a string of factory log or sensor telemetry.
     * z[i] represents the length of the longest substring starting from index i
     * that matches the prefix of the string.
     *
     * Time Complexity: O(N) where N is the length of the input string.
     * Space Complexity: O(N) to store the Z-array.
     *
     * @param text Maintenance log string or sensor stream
     * @return int array where entry i contains Z-box length starting at i
     */
    public static int[] computeZ(String text) {
        if (text == null || text.isEmpty()) {
            return new int[0];
        }
        int n = text.length();
        int[] z = new int[n];
        int l = 0;
        int r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < n && text.charAt(z[i]) == text.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }

    /**
     * Searches for exact occurrences of an anomaly pattern in a maintenance report
     * by constructing the combined string: pattern + "$" + reportText.
     *
     * Time Complexity: O(N + M) where N = reportText.length(), M = pattern.length().
     * Space Complexity: O(N + M) for combined string and Z-array.
     *
     * @param reportText Unstructured technician report text
     * @param anomalyPattern Target pattern to detect (e.g. "cavitation", "pressure loss")
     * @return List of 0-based start positions in reportText where pattern begins
     */
    public static List<Integer> searchPattern(String reportText, String anomalyPattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (reportText == null || anomalyPattern == null || anomalyPattern.isEmpty() || reportText.length() < anomalyPattern.length()) {
            return occurrences;
        }

        String combined = anomalyPattern + "$" + reportText;
        int[] z = computeZ(combined);
        int m = anomalyPattern.length();

        for (int i = m + 1; i < combined.length(); i++) {
            if (z[i] == m) {
                occurrences.add(i - (m + 1));
            }
        }
        return occurrences;
    }

    /**
     * Determines the shortest repeating period of an automated sensor stream trace.
     * If the sensor reading is "VIB-VIB-VIB-", returns the repeating period length.
     *
     * Time Complexity: O(N) where N is the length of the telemetry trace.
     * Space Complexity: O(N) to compute the Z-array.
     *
     * @param sensorTrace Cyclic sensor string
     * @return The minimal period length T such that the string is composed of repeating chunks,
     *         or N if no periodic repetition exists.
     */
    public static int findSmallestPeriod(String sensorTrace) {
        if (sensorTrace == null || sensorTrace.isEmpty()) {
            return 0;
        }
        int n = sensorTrace.length();
        int[] z = computeZ(sensorTrace);

        for (int p = 1; p <= n; p++) {
            if (n % p == 0) {
                if (p == n || z[p] == n - p) {
                    return p;
                }
            }
        }
        return n;
    }

    /**
     * Identifies the longest repeated prefix in maintenance reports.
     *
     * Time Complexity: O(N)
     * Space Complexity: O(N)
     *
     * @param reportText Diagnostic text
     * @return Substring corresponding to the longest prefix repeated elsewhere in the text
     */
    public static String findLongestRepeatedPrefix(String reportText) {
        if (reportText == null || reportText.isEmpty()) {
            return "";
        }
        int[] z = computeZ(reportText);
        int maxLen = 0;
        for (int len : z) {
            if (len > maxLen) {
                maxLen = len;
            }
        }
        return reportText.substring(0, maxLen);
    }
}
