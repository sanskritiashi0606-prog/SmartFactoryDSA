package smartfactory.m1_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) String Searching Algorithm.
 * Domain Use Case: Rapidly scans unstructured machine fault logs and telemetry
 * text to detect occurrences of specific Machine IDs and alert codes without backtracking.
 */
public class KMP {

    /**
     * Computes the Longest Proper Prefix that is also a Suffix (LPS) array for the given pattern.
     *
     * Time Complexity: O(M) where M is the length of the pattern.
     * Space Complexity: O(M) to store the LPS table.
     *
     * @param pattern The pattern (e.g., machine identifier or fault tag)
     * @return Array containing LPS values for each prefix of the pattern
     */
    public static int[] computeLPS(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return new int[0];
        }
        int m = pattern.length();
        int[] lps = new int[m];
        int length = 0; // length of previous longest prefix suffix
        int i = 1;
        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1]; // Fallback to previous longest prefix
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Searches for occurrences of a Machine ID or target keyword inside a fault log description.
     *
     * Time Complexity: O(N + M) where N is the text length and M is the pattern length.
     * Space Complexity: O(M) to store the LPS preprocessing table.
     *
     * @param faultDescription Unstructured technician report or telemetry log text
     * @param machineId Machine ID or diagnostic code to search for (e.g. "MCH-103")
     * @return List of 0-based start indices where the machine ID appears in the log
     */
    public static List<Integer> searchMachineId(String faultDescription, String machineId) {
        List<Integer> occurrences = new ArrayList<>();
        if (faultDescription == null || machineId == null || machineId.isEmpty() || faultDescription.length() < machineId.length()) {
            return occurrences;
        }

        int[] lps = computeLPS(machineId);
        int n = faultDescription.length();
        int m = machineId.length();

        int i = 0; // index for faultDescription (text)
        int j = 0; // index for machineId (pattern)

        while (i < n) {
            if (faultDescription.charAt(i) == machineId.charAt(j)) {
                i++;
                j++;
            }

            if (j == m) {
                occurrences.add(i - j); // Match found at index (i - j)
                j = lps[j - 1];
            } else if (i < n && faultDescription.charAt(i) != machineId.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return occurrences;
    }

    /**
     * Checks if a fault description references a specific machine identifier.
     *
     * Time Complexity: O(N + M) worst-case.
     * Space Complexity: O(M) auxiliary space.
     *
     * @param faultDescription Unstructured log content
     * @param machineId Machine identifier
     * @return true if machineId is found, false otherwise
     */
    public static boolean containsMachineId(String faultDescription, String machineId) {
        return !searchMachineId(faultDescription, machineId).isEmpty();
    }
}
