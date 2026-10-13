package smartfactory.m1_strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rabin-Karp Rolling Hash String Matching Algorithm.
 * Domain Use Case: Rapidly scans warehouse manifests and batch catalogs to locate
 * specific production and inventory part codes (e.g., "AERO-TURBINE", "ROBOT-ARM").
 */
public class RabinKarp {

    private static final long MODULUS = 1_000_000_007L;
    private static final long RADIX = 256L;

    /**
     * Searches for a product or inventory code in a serialized production manifest.
     *
     * Time Complexity:
     *   - Average Case: O(N + M) where N = manifest length, M = code length.
     *   - Worst Case: O(N * M) in the rare event of frequent hash collisions.
     * Space Complexity: O(1) auxiliary space (excluding result container).
     *
     * @param manifest Production order catalog or warehouse inventory manifest
     * @param targetCode Product code or inventory SKU to locate
     * @return List of 0-based indices where the targetCode begins in the manifest
     */
    public static List<Integer> searchInventoryCode(String manifest, String targetCode) {
        List<Integer> occurrences = new ArrayList<>();
        if (manifest == null || targetCode == null || targetCode.isEmpty() || manifest.length() < targetCode.length()) {
            return occurrences;
        }

        int n = manifest.length();
        int m = targetCode.length();

        // Precompute RADIX^(m-1) % MODULUS
        long highRadix = 1;
        for (int i = 0; i < m - 1; i++) {
            highRadix = (highRadix * RADIX) % MODULUS;
        }

        long patternHash = 0;
        long windowHash = 0;

        // Compute initial hashes for pattern and first window
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * RADIX + manifest.charAt(i)) % MODULUS; // Wait, pattern hash from targetCode!
        }
        // Let's fix targetCode hash vs manifest hash:
        patternHash = 0;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * RADIX + targetCode.charAt(i)) % MODULUS;
            windowHash = (windowHash * RADIX + manifest.charAt(i)) % MODULUS;
        }

        for (int i = 0; i <= n - m; i++) {
            if (patternHash == windowHash) {
                // Verify against hash collisions
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (manifest.charAt(i + j) != targetCode.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    occurrences.add(i);
                }
            }

            // Roll hash to the next character
            if (i < n - m) {
                windowHash = (windowHash - manifest.charAt(i) * highRadix) % MODULUS;
                if (windowHash < 0) {
                    windowHash += MODULUS;
                }
                windowHash = (windowHash * RADIX + manifest.charAt(i + m)) % MODULUS;
            }
        }

        return occurrences;
    }

    /**
     * Searches for multiple fixed-length inventory codes in parallel using rolling hash set lookup.
     *
     * Time Complexity: O(N + K * M) where K is the number of target codes.
     * Space Complexity: O(K) to store code hash lookups.
     *
     * @param manifest Production manifest
     * @param codes List of inventory codes sharing the same character length
     * @return Map linking each matched code to its occurrence positions
     */
    public static Map<String, List<Integer>> searchMultipleFixedLengthCodes(String manifest, List<String> codes) {
        Map<String, List<Integer>> results = new HashMap<>();
        if (manifest == null || codes == null || codes.isEmpty()) {
            return results;
        }

        int m = codes.get(0).length();
        Map<Long, List<String>> hashToCodes = new HashMap<>();

        for (String code : codes) {
            results.put(code, new ArrayList<>());
            if (code.length() != m) {
                continue; // Only uniform length for standard single-window rolling
            }
            long h = 0;
            for (int i = 0; i < m; i++) {
                h = (h * RADIX + code.charAt(i)) % MODULUS;
            }
            hashToCodes.computeIfAbsent(h, k -> new ArrayList<>()).add(code);
        }

        int n = manifest.length();
        if (n < m) {
            return results;
        }

        long highRadix = 1;
        for (int i = 0; i < m - 1; i++) {
            highRadix = (highRadix * RADIX) % MODULUS;
        }

        long windowHash = 0;
        for (int i = 0; i < m; i++) {
            windowHash = (windowHash * RADIX + manifest.charAt(i)) % MODULUS;
        }

        for (int i = 0; i <= n - m; i++) {
            if (hashToCodes.containsKey(windowHash)) {
                String sub = manifest.substring(i, i + m);
                for (String candidate : hashToCodes.get(windowHash)) {
                    if (candidate.equals(sub)) {
                        results.get(candidate).add(i);
                    }
                }
            }

            if (i < n - m) {
                windowHash = (windowHash - manifest.charAt(i) * highRadix) % MODULUS;
                if (windowHash < 0) {
                    windowHash += MODULUS;
                }
                windowHash = (windowHash * RADIX + manifest.charAt(i + m)) % MODULUS;
            }
        }

        return results;
    }
}
