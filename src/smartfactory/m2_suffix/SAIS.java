package smartfactory.m2_suffix;

import java.util.Arrays;

/**
 * SA-IS (Suffix Array by Induced Sorting) Algorithm.
 * Domain Use Case: Constructs exact Suffix Arrays for very large manufacturing reports,
 * vibration logs, and factory telemetry records in guaranteed O(N) linear time.
 */
public class SAIS {

    private static final boolean S_TYPE = true;
    private static final boolean L_TYPE = false;

    /**
     * Constructs the Suffix Array for a factory text document using SA-IS.
     *
     * Time Complexity: O(N) strictly linear where N is text length.
     * Space Complexity: O(N) strictly linear auxiliary space.
     *
     * @param text Raw maintenance logs or sensor telemetry
     * @return Suffix Array containing 0-based indices of all suffixes in lexicographic order
     */
    public static int[] buildSuffixArray(String text) {
        if (text == null || text.isEmpty()) {
            return new int[0];
        }

        // Convert string to integer array terminated with a unique smallest sentinel (0)
        int n = text.length();
        int[] s = new int[n + 1];
        int maxChar = 0;
        for (int i = 0; i < n; i++) {
            s[i] = text.charAt(i) + 1; // Shift characters up so 0 is reserved as sentinel
            if (s[i] > maxChar) {
                maxChar = s[i];
            }
        }
        s[n] = 0; // Sentinel representing end of string

        int[] saWithSentinel = sais(s, maxChar + 1);

        // Strip the sentinel suffix at sa[0]
        int[] sa = new int[n];
        System.arraycopy(saWithSentinel, 1, sa, 0, n);
        return sa;
    }

    /**
     * Core recursive SA-IS routine.
     *
     * Time Complexity: O(N)
     * Space Complexity: O(N)
     */
    private static int[] sais(int[] s, int alphabetSize) {
        int n = s.length;
        if (n == 1) {
            return new int[]{0};
        }
        if (n == 2) {
            if (s[0] < s[1]) {
                return new int[]{0, 1};
            } else {
                return new int[]{1, 0};
            }
        }

        // 1. Classify suffix types (S-type vs L-type)
        boolean[] type = new boolean[n];
        type[n - 1] = S_TYPE;
        for (int i = n - 2; i >= 0; i--) {
            if (s[i] < s[i + 1]) {
                type[i] = S_TYPE;
            } else if (s[i] > s[i + 1]) {
                type[i] = L_TYPE;
            } else {
                type[i] = type[i + 1];
            }
        }

        // 2. Identify LMS (Left-Most S-type) characters
        int[] bucketSizes = getBucketSizes(s, alphabetSize);
        int[] sa = new int[n];
        Arrays.fill(sa, -1);

        // Bucket endpoints
        int[] bucketEnds = getBucketEnds(bucketSizes);
        for (int i = 1; i < n; i++) {
            if (isLMS(type, i)) {
                sa[--bucketEnds[s[i]]] = i;
            }
        }

        // Induced sort L and S types
        induceSort(s, sa, type, alphabetSize, bucketSizes);

        // 3. Compact LMS suffixes and assign names to LMS substrings
        int[] lmsMap = new int[n];
        Arrays.fill(lmsMap, -1);
        int lmsCount = 0;
        for (int i = 1; i < n; i++) {
            if (isLMS(type, i)) {
                lmsMap[i] = lmsCount++;
            }
        }

        int[] lmsSubstrings = new int[lmsCount];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            if (lmsMap[sa[i]] != -1) {
                lmsSubstrings[idx++] = sa[i];
            }
        }

        int[] s1 = new int[lmsCount];
        int name = 0;
        s1[lmsMap[lmsSubstrings[0]]] = name;

        for (int i = 1; i < lmsCount; i++) {
            int prev = lmsSubstrings[i - 1];
            int curr = lmsSubstrings[i];
            if (!areLMSEqual(s, type, prev, curr)) {
                name++;
            }
            s1[lmsMap[curr]] = name;
        }

        // 4. Recursion or direct derivation
        int[] sa1;
        if (name + 1 < lmsCount) {
            sa1 = sais(s1, name + 1);
        } else {
            sa1 = new int[lmsCount];
            for (int i = 0; i < lmsCount; i++) {
                sa1[s1[i]] = i;
            }
        }

        // 5. Induced sort from sorted LMS suffixes
        int[] lmsPositions = new int[lmsCount];
        int posIdx = 0;
        for (int i = 1; i < n; i++) {
            if (isLMS(type, i)) {
                lmsPositions[posIdx++] = i;
            }
        }

        Arrays.fill(sa, -1);
        bucketEnds = getBucketEnds(bucketSizes);
        for (int i = lmsCount - 1; i >= 0; i--) {
            int j = lmsPositions[sa1[i]];
            sa[--bucketEnds[s[j]]] = j;
        }

        induceSort(s, sa, type, alphabetSize, bucketSizes);
        return sa;
    }

    private static boolean isLMS(boolean[] type, int i) {
        return i > 0 && type[i] == S_TYPE && type[i - 1] == L_TYPE;
    }

    private static boolean areLMSEqual(int[] s, boolean[] type, int i, int j) {
        while (true) {
            boolean iLms = isLMS(type, i);
            boolean jLms = isLMS(type, j);
            if (i != s.length - 1 && j != s.length - 1 && iLms && jLms && i != j && s[i] == s[j]) {
                return true;
            }
            if (s[i] != s[j] || type[i] != type[j]) {
                return false;
            }
            if (i == s.length - 1 || j == s.length - 1) {
                return i == j;
            }
            i++;
            j++;
        }
    }

    private static void induceSort(int[] s, int[] sa, boolean[] type, int alphabetSize, int[] bucketSizes) {
        int n = s.length;
        int[] bucketHeads = getBucketHeads(bucketSizes);
        for (int i = 0; i < n; i++) {
            int j = sa[i] - 1;
            if (j >= 0 && type[j] == L_TYPE) {
                sa[bucketHeads[s[j]]++] = j;
            }
        }

        int[] bucketEnds = getBucketEnds(bucketSizes);
        for (int i = n - 1; i >= 0; i--) {
            int j = sa[i] - 1;
            if (j >= 0 && type[j] == S_TYPE) {
                sa[--bucketEnds[s[j]]] = j;
            }
        }
    }

    private static int[] getBucketSizes(int[] s, int alphabetSize) {
        int[] sizes = new int[alphabetSize];
        for (int ch : s) {
            sizes[ch]++;
        }
        return sizes;
    }

    private static int[] getBucketHeads(int[] bucketSizes) {
        int[] heads = new int[bucketSizes.length];
        int sum = 0;
        for (int i = 0; i < bucketSizes.length; i++) {
            heads[i] = sum;
            sum += bucketSizes[i];
        }
        return heads;
    }

    private static int[] getBucketEnds(int[] bucketSizes) {
        int[] ends = new int[bucketSizes.length];
        int sum = 0;
        for (int i = 0; i < bucketSizes.length; i++) {
            sum += bucketSizes[i];
            ends[i] = sum;
        }
        return ends;
    }
}
