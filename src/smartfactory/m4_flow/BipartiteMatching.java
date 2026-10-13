package smartfactory.m4_flow;

import java.util.*;

/**
 * Maximum Bipartite Matching for Industrial Workforce Scheduling.
 * Domain Use Case: Optimally assigns certified maintenance technicians and specialized
 * operators to eligible machines based on skill qualifications and safety clearance.
 */
public class BipartiteMatching {

    public static class AssignmentResult {
        public final int maxMatches;
        public final Map<String, String> workerToMachineMap;

        public AssignmentResult(int maxMatches, Map<String, String> workerToMachineMap) {
            this.maxMatches = maxMatches;
            this.workerToMachineMap = workerToMachineMap;
        }
    }

    /**
     * Finds the maximum number of worker-to-machine assignments such that each worker
     * is assigned to at most one certified machine and each machine is operated by one worker.
     *
     * Time Complexity: O(W * M) where W is number of workers and M is number of machines.
     * Space Complexity: O(W + M) auxiliary space for matching state and visited markers.
     *
     * @param eligibilityMap Map where key is Worker ID and value is List of Machine IDs they are qualified to run
     * @return AssignmentResult containing maximum matching count and worker-machine mappings
     */
    public static AssignmentResult matchWorkersToMachines(Map<String, List<String>> eligibilityMap) {
        if (eligibilityMap == null || eligibilityMap.isEmpty()) {
            return new AssignmentResult(0, new HashMap<>());
        }

        List<String> workers = new ArrayList<>(eligibilityMap.keySet());
        Set<String> machineSet = new LinkedHashSet<>();
        for (List<String> mList : eligibilityMap.values()) {
            if (mList != null) {
                machineSet.addAll(mList);
            }
        }
        List<String> machines = new ArrayList<>(machineSet);

        int numWorkers = workers.size();
        int numMachines = machines.size();

        // Adjacency matrix for indexed bipartite graph
        boolean[][] adj = new boolean[numWorkers][numMachines];
        for (int i = 0; i < numWorkers; i++) {
            List<String> allowed = eligibilityMap.get(workers.get(i));
            if (allowed != null) {
                for (String mId : allowed) {
                    int mIdx = machines.indexOf(mId);
                    if (mIdx != -1) {
                        adj[i][mIdx] = true;
                    }
                }
            }
        }

        // machineMatch[v] stores worker index matched to machine v (-1 if unassigned)
        int[] machineMatch = new int[numMachines];
        Arrays.fill(machineMatch, -1);

        int matchCount = 0;
        for (int w = 0; w < numWorkers; w++) {
            boolean[] visited = new boolean[numMachines];
            if (findAugmentingPath(w, adj, visited, machineMatch)) {
                matchCount++;
            }
        }

        // Build worker -> machine mapping
        Map<String, String> assignment = new LinkedHashMap<>();
        for (int m = 0; m < numMachines; m++) {
            if (machineMatch[m] != -1) {
                assignment.put(workers.get(machineMatch[m]), machines.get(m));
            }
        }

        return new AssignmentResult(matchCount, assignment);
    }

    private static boolean findAugmentingPath(int u, boolean[][] adj, boolean[] visited, int[] machineMatch) {
        int numMachines = adj[0].length;
        for (int v = 0; v < numMachines; v++) {
            if (adj[u][v] && !visited[v]) {
                visited[v] = true;

                // If machine v is free or previously assigned worker can find an alternate machine
                if (machineMatch[v] < 0 || findAugmentingPath(machineMatch[v], adj, visited, machineMatch)) {
                    machineMatch[v] = u;
                    return true;
                }
            }
        }
        return false;
    }
}
