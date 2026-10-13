package smartfactory.m3_dp;

/**
 * Matrix Chain Multiplication Dynamic Programming Optimization.
 * Domain Use Case: Minimizes scalar arithmetic operations when executing chained
 * geometric coordinate transforms, robot kinematics frames, and multi-stage
 * production sensor data pipelines.
 */
public class MatrixChain {

    public static class OptimalChainResult {
        public final long minOperations;
        public final int[][] splitTable;
        public final String parenthesizedExpression;

        public OptimalChainResult(long minOperations, int[][] splitTable, String parenthesizedExpression) {
            this.minOperations = minOperations;
            this.splitTable = splitTable;
            this.parenthesizedExpression = parenthesizedExpression;
        }
    }

    /**
     * Determines the optimal parenthesization order for chained matrix multiplications.
     * Dimensions array p has length (n + 1), where matrix i has dimensions p[i-1] x p[i].
     *
     * Time Complexity: O(N^3) where N is the number of matrices in the sequence.
     * Space Complexity: O(N^2) to maintain DP table and split decisions.
     *
     * @param p Matrix dimension sequence (e.g. {10, 30, 5, 60} for matrices 10x30, 30x5, 5x60)
     * @param matrixLabels Names of the production operations or matrices
     * @return OptimalChainResult containing minimum scalar operations and optimal parenthesization
     */
    public static OptimalChainResult computeOptimalOrder(int[] p, String[] matrixLabels) {
        if (p == null || p.length < 2) {
            return new OptimalChainResult(0, new int[0][0], "");
        }
        int n = p.length - 1; // Number of matrices
        long[][] m = new long[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        // Chains of length 1 require 0 multiplications
        for (int i = 1; i <= n; i++) {
            m[i][i] = 0;
        }

        // l is the chain length
        for (int l = 2; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                m[i][j] = Long.MAX_VALUE;

                for (int k = i; k <= j - 1; k++) {
                    long q = m[i][k] + m[k + 1][j] + (long) p[i - 1] * p[k] * p[j];
                    if (q < m[i][j]) {
                        m[i][j] = q;
                        s[i][j] = k;
                    }
                }
            }
        }

        String expression = buildParenthesization(s, 1, n, matrixLabels);
        return new OptimalChainResult(m[1][n], s, expression);
    }

    private static String buildParenthesization(int[][] s, int i, int j, String[] labels) {
        if (i == j) {
            if (labels != null && i - 1 < labels.length) {
                return labels[i - 1];
            }
            return "Op" + i;
        }
        return "(" + buildParenthesization(s, i, s[i][j], labels) + " x " +
                buildParenthesization(s, s[i][j] + 1, j, labels) + ")";
    }
}
