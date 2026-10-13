package smartfactory.m5_np;

import java.util.*;

/**
 * Davis-Putnam-Logemann-Loveland (DPLL) SAT / 3-SAT Solver.
 * Domain Use Case: Evaluates complex industrial safety interlocks, mutual exclusion
 * constraints, and production scheduling rules expressed as Boolean logic formulas.
 */
public class SATSolver {

    public static class SATResult {
        public final boolean isSatisfiable;
        public final Map<Integer, Boolean> model; // Variable ID -> true/false

        public SATResult(boolean isSatisfiable, Map<Integer, Boolean> model) {
            this.isSatisfiable = isSatisfiable;
            this.model = model;
        }

        @Override
        public String toString() {
            if (!isSatisfiable) return "UNSATISFIABLE (Production constraints cannot be simultaneously satisfied)";
            return "SATISFIABLE with Model: " + model;
        }
    }

    /**
     * Solves a Conjunctive Normal Form (CNF) production constraint formula using DPLL.
     * Each clause is a list of non-zero integers: positive integer k for variable x_k,
     * negative integer -k for negation NOT(x_k).
     *
     * Time Complexity: O(2^V * C) worst case where V is number of variables and C is clauses.
     * Space Complexity: O(V + C) recursion stack and assignment state.
     *
     * @param numVariables Total number of Boolean variables (e.g. machine operational states)
     * @param clauses List of disjunctive clauses (OR expressions) in the CNF formula
     * @return SATResult indicating satisfiability and a valid variable assignment if SAT
     */
    public static SATResult solve(int numVariables, List<List<Integer>> clauses) {
        Map<Integer, Boolean> assignment = new HashMap<>();
        List<List<Integer>> currentClauses = deepCopy(clauses);

        boolean sat = dpll(currentClauses, assignment, numVariables);
        if (sat) {
            // Fill unassigned variables with default true
            for (int v = 1; v <= numVariables; v++) {
                assignment.putIfAbsent(v, true);
            }
            return new SATResult(true, assignment);
        } else {
            return new SATResult(false, Collections.emptyMap());
        }
    }

    private static boolean dpll(List<List<Integer>> clauses, Map<Integer, Boolean> assignment, int numVars) {
        // 1. Unit Propagation
        boolean changed = true;
        while (changed) {
            changed = false;
            Integer unitLiteral = null;
            for (List<Integer> clause : clauses) {
                if (clause.isEmpty()) {
                    return false; // Empty clause encountered -> contradiction
                }
                if (clause.size() == 1) {
                    unitLiteral = clause.get(0);
                    break;
                }
            }

            if (unitLiteral != null) {
                int var = Math.abs(unitLiteral);
                boolean val = unitLiteral > 0;
                assignment.put(var, val);
                clauses = simplify(clauses, unitLiteral);
                changed = true;
            }
        }

        // Check if all clauses are satisfied
        if (clauses.isEmpty()) {
            return true;
        }

        // Check if any clause is empty
        for (List<Integer> c : clauses) {
            if (c.isEmpty()) return false;
        }

        // 2. Pure Literal Elimination
        Set<Integer> literals = new HashSet<>();
        for (List<Integer> c : clauses) {
            literals.addAll(c);
        }
        for (int lit : literals) {
            if (!literals.contains(-lit)) {
                int var = Math.abs(lit);
                boolean val = lit > 0;
                assignment.put(var, val);
                clauses = simplify(clauses, lit);
            }
        }

        if (clauses.isEmpty()) {
            return true;
        }

        // 3. Choose branching variable
        int branchVar = Math.abs(clauses.get(0).get(0));

        // Branch 1: Try branchVar = true (literal +branchVar)
        Map<Integer, Boolean> branch1Assignment = new HashMap<>(assignment);
        branch1Assignment.put(branchVar, true);
        List<List<Integer>> branch1Clauses = simplify(clauses, branchVar);
        if (dpll(branch1Clauses, branch1Assignment, numVars)) {
            assignment.putAll(branch1Assignment);
            return true;
        }

        // Branch 2: Try branchVar = false (literal -branchVar)
        Map<Integer, Boolean> branch2Assignment = new HashMap<>(assignment);
        branch2Assignment.put(branchVar, false);
        List<List<Integer>> branch2Clauses = simplify(clauses, -branchVar);
        if (dpll(branch2Clauses, branch2Assignment, numVars)) {
            assignment.putAll(branch2Assignment);
            return true;
        }

        return false;
    }

    private static List<List<Integer>> simplify(List<List<Integer>> clauses, int assignedLiteral) {
        List<List<Integer>> simplified = new ArrayList<>();
        int negatedLiteral = -assignedLiteral;

        for (List<Integer> clause : clauses) {
            if (clause.contains(assignedLiteral)) {
                // Clause is satisfied, drop it
                continue;
            }
            if (clause.contains(negatedLiteral)) {
                // Remove the false literal from the clause
                List<Integer> newClause = new ArrayList<>(clause);
                newClause.remove(Integer.valueOf(negatedLiteral));
                simplified.add(newClause);
            } else {
                simplified.add(new ArrayList<>(clause));
            }
        }
        return simplified;
    }

    private static List<List<Integer>> deepCopy(List<List<Integer>> list) {
        List<List<Integer>> copy = new ArrayList<>(list.size());
        for (List<Integer> inner : list) {
            copy.add(new ArrayList<>(inner));
        }
        return copy;
    }
}
