package smartfactory.m6_randomized;

import java.util.Random;

/**
 * Miller-Rabin Probabilistic Primality Testing Algorithm.
 * Domain Use Case: Generates and verifies cryptographic prime parameters for secure
 * edge-device handshakes, tamper-proof IIoT telemetry hashing, and digital signatures.
 */
public class MillerRabin {

    private static final Random RNG = new Random(101);

    /**
     * Determines whether a given 64-bit integer is prime with high probability.
     *
     * Time Complexity: O(K * log^3 N) where K is number of probabilistic rounds.
     * Space Complexity: O(1) auxiliary space.
     *
     * @param n Number to test for primality (n >= 2)
     * @param k Number of independent random witness rounds (e.g. k = 20 yields error <= 4^-20)
     * @return true if n is almost certainly prime, false if definitively composite
     */
    public static boolean isProbablePrime(long n, int k) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;

        // Write n - 1 as 2^s * d with d odd
        long d = n - 1;
        int s = 0;
        while (d % 2 == 0) {
            d /= 2;
            s++;
        }

        // Perform k independent Miller-Rabin witness trials
        for (int i = 0; i < k; i++) {
            long a = 2 + (long) (RNG.nextDouble() * (n - 4));
            if (!millerRabinPass(a, s, d, n)) {
                return false; // Definitively composite
            }
        }

        return true; // Probable prime
    }

    private static boolean millerRabinPass(long a, int s, long d, long n) {
        long x = modularPow(a, d, n);
        if (x == 1 || x == n - 1) {
            return true;
        }

        for (int r = 1; r < s; r++) {
            x = multiplyMod(x, x, n);
            if (x == n - 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Computes (base^exp) % mod avoiding overflow using modular multiplication.
     *
     * Time Complexity: O(log exp).
     * Space Complexity: O(1).
     */
    public static long modularPow(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = multiplyMod(result, base, mod);
            }
            base = multiplyMod(base, base, mod);
            exp >>= 1;
        }
        return result;
    }

    /**
     * Safe (a * b) % mod to prevent 64-bit primitive integer overflow.
     */
    private static long multiplyMod(long a, long b, long mod) {
        long res = 0;
        a %= mod;
        while (b > 0) {
            if ((b & 1) == 1) {
                res = (res + a) % mod;
            }
            a = (2 * a) % mod;
            b >>= 1;
        }
        return res;
    }
}
