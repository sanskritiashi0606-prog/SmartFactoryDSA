package smartfactory;

import smartfactory.m1_strings.AhoCorasick;
import smartfactory.m1_strings.KMP;
import smartfactory.m1_strings.RabinKarp;
import smartfactory.m1_strings.ZFunction;
import smartfactory.m2_suffix.LCPKasai;
import smartfactory.m2_suffix.SAIS;
import smartfactory.m2_suffix.SuffixArray;
import smartfactory.m2_suffix.SuffixTree;
import smartfactory.m3_dp.BitmaskDP;
import smartfactory.m3_dp.EditDistance;
import smartfactory.m3_dp.MatrixChain;
import smartfactory.m3_dp.SubsetDP;
import smartfactory.m4_flow.BipartiteMatching;
import smartfactory.m4_flow.Dinic;
import smartfactory.m4_flow.EdmondsKarp;
import smartfactory.m4_flow.FordFulkerson;
import smartfactory.m4_flow.MaxFlowMinCut;
import smartfactory.m5_np.Reductions;
import smartfactory.m5_np.SATSolver;
import smartfactory.m5_np.VertexCoverApprox;
import smartfactory.m6_randomized.*;
import smartfactory.model.Machine;
import smartfactory.model.MaintenanceReport;
import smartfactory.model.ProductionOrder;

import java.util.*;

/**
 * SmartFactory – Industrial Production Analytics Platform
 * ========================================================
 * Entry point demonstrating DSA implementations across manufacturing workflows.
 *
 * Modules Overview:
 *  - Module 1: String Algorithms (KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick)
 *  - Module 2: Suffix Structures (Suffix Array, SA-IS, LCP Kasai, Suffix Tree)
 *  - Module 3: Advanced DP (Edit Distance, Matrix Chain, Bitmask DP, Subset DP)
 *  - Module 4: Network Flow (Ford-Fulkerson, Edmonds-Karp, Dinic, Bipartite Matching, Min-Cut)
 *  - Module 5: NP-Completeness & Approximation (SAT Solver, Reductions, Vertex Cover Approx)
 *  - Module 6: Randomized & Parallel (Randomized QuickSort, Miller-Rabin, Reservoir Sampling,
 *                                    Blelloch Scan, Parallel Reduce, Brent's Theorem)
 */
public class Main {

    public static void main(String[] args) {
        printBanner();

        // Initialize Mock Industrial Dataset
        List<Machine> machines = createMockMachines();
        List<ProductionOrder> orders = createMockOrders();
        List<MaintenanceReport> reports = createMockReports();

        System.out.println("Initialized SmartFactory Industrial Platform with:");
        System.out.printf("  - %d Machines%n", machines.size());
        System.out.printf("  - %d Production Orders%n", orders.size());
        System.out.printf("  - %d Maintenance Reports%n%n", reports.size());

        // Execute all modules
        runModule1Strings(reports);
        runModule2Suffix(reports);
        runModule3DP(machines, orders);
        runModule4Flow(machines);
        runModule5NP(machines);
        runModule6RandomizedParallel(orders);

        System.out.println("================================================================================");
        System.out.println(" SmartFactory Platform Execution Completed Successfully. ");
        System.out.println("================================================================================");
    }

    private static void printBanner() {
        System.out.println("================================================================================");
        System.out.println("        SMARTFACTORY: INDUSTRIAL PRODUCTION ANALYTICS PLATFORM                  ");
        System.out.println("        Core Data Structures & Advanced Algorithms Benchmark Suite              ");
        System.out.println("================================================================================");
    }

    public static List<Machine> createMockMachines() {
        List<Machine> list = new ArrayList<>();
        list.add(new Machine("MCH-101", "High-Precision CNC Lathe", "CNC", 120, "OPERATIONAL", 0.08));
        list.add(new Machine("MCH-102", "Robotic Welding Cell Alpha", "WELDING", 85, "OPERATIONAL", 0.22));
        list.add(new Machine("MCH-103", "Heavy Hydraulic Stamping Press", "STAMPING", 200, "MAINTENANCE_REQUIRED", 0.74));
        list.add(new Machine("MCH-104", "Automated Surface Painting Unit", "PAINTING", 150, "OPERATIONAL", 0.15));
        list.add(new Machine("MCH-105", "Vision Quality Inspection Gantry", "INSPECTION", 300, "OPERATIONAL", 0.05));
        list.add(new Machine("MCH-106", "Thermal Heat Treatment Oven", "THERMAL", 90, "IDLE", 0.31));
        return list;
    }

    public static List<ProductionOrder> createMockOrders() {
        List<ProductionOrder> list = new ArrayList<>();
        long now = System.currentTimeMillis();
        list.add(new ProductionOrder("ORD-901", "AERO-TURBINE-BLADE-01", 450, 9, now + 86400000L, "CNC", 45000.0));
        list.add(new ProductionOrder("ORD-902", "AUTO-CHASSIS-BRACKET-X", 1200, 5, now + 172800000L, "STAMPING", 18500.0));
        list.add(new ProductionOrder("ORD-903", "ROBOT-ARM-JOINT-COUPLER", 300, 8, now + 43200000L, "WELDING", 32000.0));
        list.add(new ProductionOrder("ORD-904", "PANEL-HOUSING-CORROSION", 600, 3, now + 259200000L, "PAINTING", 9200.0));
        list.add(new ProductionOrder("ORD-905", "BEARING-FLANGE-ASSEMBLY", 850, 7, now + 129600000L, "CNC", 27400.0));
        list.add(new ProductionOrder("ORD-906", "OPTICAL-SENSOR-CASING-Z", 1500, 10, now + 21600000L, "INSPECTION", 58000.0));
        return list;
    }

    public static List<MaintenanceReport> createMockReports() {
        List<MaintenanceReport> list = new ArrayList<>();
        list.add(new MaintenanceReport("RPT-001", "MCH-103", "TECH-SARAH", "2026-10-01 08:30", "HIGH",
                "Severe hydraulic fluid pressure loss in main chamber MCH-103. Hydraulic valve cavitation observed during duty cycle.",
                "Replaced hydraulic seals and refilled pressure reservoir."));
        list.add(new MaintenanceReport("RPT-002", "MCH-102", "TECH-ALEX", "2026-10-03 14:15", "CRITICAL",
                "Robotic welding arc instability at nozzle head. Overheating detected in electrode servo assembly MCH-102.",
                "Calibrated nozzle impedance and tested cooling circuit."));
        list.add(new MaintenanceReport("RPT-003", "MCH-101", "TECH-SARAH", "2026-10-05 11:00", "LOW",
                "Minor vibration detected on spindle spindle spindle axis during high speed rotation of MCH-101.",
                "Spindle bearing lubricated and dynamically balanced."));
        list.add(new MaintenanceReport("RPT-004", "MCH-104", "TECH-DAVE", "2026-10-07 16:45", "MEDIUM",
                "Paint atomizer nozzle clogged with dried solvent residue. Flow rate reduced by 35 percent on MCH-104.",
                "Flushed line with solvent flush cycle 4B."));
        return list;
    }

    private static void runModule1Strings(List<MaintenanceReport> reports) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" [MODULE 1: String Algorithms (CO1)]");
        System.out.println("--------------------------------------------------------------------------------");

        // 1. KMP Demo
        System.out.println("[1.1 KMP Algorithm] Machine Identifier Search in Fault Logs:");
        String targetMachineId = "MCH-103";
        for (MaintenanceReport r : reports) {
            List<Integer> occurrences = KMP.searchMachineId(r.getFaultDescription(), targetMachineId);
            if (!occurrences.isEmpty()) {
                System.out.printf("  -> Report %s references %s at index positions: %s%n",
                        r.getReportId(), targetMachineId, occurrences);
            }
        }

        // 2. Z-Function Demo
        System.out.println("\n[1.2 Z-Algorithm] Repetitive Pattern & Sensor Periodicity Analysis:");
        String sensorTrace = "VIB_CYCLE_120HZ_VIB_CYCLE_120HZ_VIB_CYCLE_120HZ_";
        int period = ZFunction.findSmallestPeriod(sensorTrace);
        System.out.printf("  -> Telemetry stream: \"%s\"%n", sensorTrace);
        System.out.printf("  -> Minimal repeating period detected by Z-Algorithm: %d characters (\"%s\")%n",
                period, sensorTrace.substring(0, period));

        MaintenanceReport r3 = reports.get(2);
        List<Integer> spindleMatches = ZFunction.searchPattern(r3.getFaultDescription(), "spindle");
        System.out.printf("  -> Found repeated anomaly token 'spindle' in %s at indices: %s%n",
                r3.getReportId(), spindleMatches);

        // 3. Rabin-Karp Demo
        System.out.println("\n[1.3 Rabin-Karp] Rolling-Hash Production Code Manifest Search:");
        String manifest = "BATCH#8841-AERO-TURBINE-BLADE-01-LOT#992-ROBOT-ARM-JOINT-COUPLER-LOT#993";
        String searchCode = "ROBOT-ARM";
        List<Integer> rkMatches = RabinKarp.searchInventoryCode(manifest, searchCode);
        System.out.printf("  -> Manifest snippet: \"%s\"%n", manifest);
        System.out.printf("  -> Located part code '%s' at index: %s%n", searchCode, rkMatches);

        // 4. Aho-Corasick Demo
        System.out.println("\n[1.4 Aho-Corasick] Simultaneous Multi-Keyword Fault Scanning:");
        List<String> faultKeywords = Arrays.asList("hydraulic", "pressure loss", "cavitation", "overheating", "nozzle clogged", "vibration");
        AhoCorasick ac = new AhoCorasick(faultKeywords);
        System.out.printf("  -> Initialized Automaton with %d industrial fault keywords: %s%n",
                faultKeywords.size(), faultKeywords);

        for (MaintenanceReport report : reports) {
            List<AhoCorasick.MatchResult> hits = ac.scanReport(report.getFaultDescription());
            System.out.printf("  -> Report %s (%s, %s): %d hits -> %s%n",
                    report.getReportId(), report.getMachineId(), report.getSeverity(), hits.size(), hits);
        }
        System.out.println();
    }

    private static void runModule2Suffix(List<MaintenanceReport> reports) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" [MODULE 2: Suffix Structures (CO2)]");
        System.out.println("--------------------------------------------------------------------------------");

        StringBuilder corpusBuilder = new StringBuilder();
        for (MaintenanceReport r : reports) {
            corpusBuilder.append(r.getFaultDescription()).append(" # ");
        }
        String maintenanceCorpus = corpusBuilder.toString();

        // 2.1 Suffix Array
        System.out.println("[2.1 Suffix Array] Indexing Technical Maintenance Documentation:");
        SuffixArray sa = new SuffixArray(maintenanceCorpus);
        System.out.printf("  -> Indexed corpus size: %d characters%n", maintenanceCorpus.length());
        System.out.printf("  -> Pattern query 'cavitation' exists? %b (count = %d)%n",
                sa.contains("cavitation"), sa.countOccurrences("cavitation"));
        System.out.printf("  -> Pattern query 'MCH-' exists? %b (count = %d)%n",
                sa.contains("MCH-"), sa.countOccurrences("MCH-"));

        // 2.2 SA-IS
        System.out.println("\n[2.2 SA-IS Algorithm] O(N) Induced Sorting Suffix Array Construction:");
        int[] saisArray = SAIS.buildSuffixArray(maintenanceCorpus);
        System.out.printf("  -> SA-IS constructed suffix array of length %d in linear O(N) time%n", saisArray.length);
        System.out.printf("  -> First 5 lexicographical suffix positions: [%d, %d, %d, %d, %d]%n",
                saisArray[0], saisArray[1], saisArray[2], saisArray[3], saisArray[4]);

        // 2.3 LCP Kasai
        System.out.println("\n[2.3 Kasai's LCP Algorithm] Recurring Incident Phrase Extraction:");
        int[] lcp = LCPKasai.computeLCP(maintenanceCorpus, saisArray);
        String longestRepeatedPhrase = LCPKasai.findLongestRepeatedFaultPhrase(maintenanceCorpus, saisArray, lcp);
        System.out.printf("  -> Computed LCP array in O(N) time.%n");
        System.out.printf("  -> Longest repeated diagnostic substring: \"%s\" (Length: %d)%n",
                longestRepeatedPhrase.trim(), longestRepeatedPhrase.trim().length());
        List<String> repeatedPhrases = LCPKasai.findRepeatedPhrasesAboveThreshold(maintenanceCorpus, saisArray, lcp, 7);
        System.out.printf("  -> Sample repeated phrases >= 7 chars: %s%n", repeatedPhrases.subList(0, Math.min(5, repeatedPhrases.size())));

        // 2.4 Suffix Tree
        System.out.println("\n[2.4 Suffix Tree] Substring Report & Frequency Queries:");
        SuffixTree tree = new SuffixTree(maintenanceCorpus);
        String[] testQueries = {"spindle", "hydraulic", "pressure", "nozzle", "unrelatedKeyword"};
        for (String q : testQueries) {
            System.out.printf("  -> SuffixTree Query '%s' -> Exists: %b, Occurrences: %d%n",
                    q, tree.containsSubstring(q), tree.countOccurrences(q));
        }
        System.out.println();
    }

    private static void runModule3DP(List<Machine> machines, List<ProductionOrder> orders) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" [MODULE 3: Advanced Dynamic Programming (CO3)]");
        System.out.println("--------------------------------------------------------------------------------");

        // 3.1 Edit Distance Demo
        System.out.println("[3.1 Edit Distance] Typo & Serial Number Error Correction:");
        String enteredTypo = "MHC-101"; // Transposition typo for MCH-101
        String canonicalId = "MCH-101";
        int levDist = EditDistance.levenshteinDistance(enteredTypo, canonicalId);
        int damerauDist = EditDistance.damerauLevenshteinDistance(enteredTypo, canonicalId);
        int weightedDist = EditDistance.weightedEditDistance(enteredTypo, canonicalId, 2, 2, 3);

        System.out.printf("  -> Comparing entered '%s' vs canonical '%s':%n", enteredTypo, canonicalId);
        System.out.printf("      - Standard Levenshtein: %d%n", levDist);
        System.out.printf("      - Damerau-Levenshtein (transposition recognized): %d%n", damerauDist);
        System.out.printf("      - Weighted Edit Distance (Ins=2, Del=2, Sub=3): %d%n", weightedDist);

        List<String> registeredIds = new ArrayList<>();
        for (Machine m : machines) registeredIds.add(m.getId());
        String autoCorrected = EditDistance.findClosestMachineId("MCH-103X", registeredIds);
        System.out.printf("  -> Auto-corrected miskeyed log 'MCH-103X' -> Closest Registered Machine: '%s'%n", autoCorrected);

        // 3.2 Matrix Chain Multiplication Demo
        System.out.println("\n[3.2 Matrix Chain DP] Chained Manufacturing Kinematics Optimization:");
        int[] dims = {10, 100, 5, 50, 20};
        String[] opLabels = {"CAD_Mesh", "Preform", "Toolpath", "Kinematics"};
        MatrixChain.OptimalChainResult chainResult = MatrixChain.computeOptimalOrder(dims, opLabels);
        System.out.printf("  -> Matrix dimension sequence: [10x100, 100x5, 5x50, 50x20]%n");
        System.out.printf("  -> Minimum scalar operations required: %d%n", chainResult.minOperations);
        System.out.printf("  -> Optimal execution order: %s%n", chainResult.parenthesizedExpression);

        // 3.3 Bitmask DP Demo
        System.out.println("\n[3.3 Bitmask DP] Machine Retooling / Changeover Downtime Minimization:");
        int[][] changeoverCost = {
                {0, 15, 25, 20},
                {15, 0, 35, 10},
                {25, 35, 0, 30},
                {20, 10, 30, 0}
        };
        BitmaskDP.SequenceResult seqResult = BitmaskDP.findOptimalMachineSequence(changeoverCost);
        System.out.printf("  -> Evaluated all 4! setup routes across 4 specialized production stations.%n");
        System.out.printf("  -> Minimal changeover downtime: %d minutes%n", seqResult.minCost);
        System.out.printf("  -> Optimal sequential setup order: %s%n", seqResult.sequenceOrder);

        int[][] workerTaskEfficiency = {
                {90, 75, 80, 85},
                {70, 95, 85, 80},
                {85, 80, 90, 75},
                {80, 85, 75, 95}
        };
        int maxEff = BitmaskDP.maxEfficiencyAssignment(workerTaskEfficiency);
        System.out.printf("  -> Max combined task-to-machine efficiency assignment: %d points%n", maxEff);

        // 3.4 Subset DP Demo
        System.out.println("\n[3.4 Subset DP] Workload Balancing & Resource-Constrained Knapsack:");
        int[] machineHourlyLoads = {40, 20, 30, 10, 60};
        boolean canBalance = SubsetDP.canPartitionWorkloadEqually(machineHourlyLoads);
        System.out.printf("  -> Line workload array %s: Can divide exactly 50/50 across dual lines? %b%n",
                Arrays.toString(machineHourlyLoads), canBalance);

        int[] orderYields = {450, 1200, 300, 600, 850};
        int[] machineHours = {4, 8, 2, 5, 6};
        int maxAvailableHours = 15;
        SubsetDP.KnapsackResult knapResult = SubsetDP.optimizeResourceAllocation(orderYields, machineHours, maxAvailableHours);
        System.out.printf("  -> 0/1 Knapsack yield under %d machine hours: Max Profit = $%d (Selected Job Indices: %s)%n",
                maxAvailableHours, knapResult.maxYield, knapResult.selectedItemIndices);

        System.out.println();
    }

    private static void runModule4Flow(List<Machine> machines) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" [MODULE 4: Network Flow (CO4)]");
        System.out.println("--------------------------------------------------------------------------------");

        String[] stationNames = {"Raw_Intake", "Extrusion", "CNC_Lathe", "Heat_Treat", "Inspection", "Packaging"};
        int n = 6;
        int source = 0;
        int sink = 5;

        int[][] capacityMatrix = new int[n][n];
        capacityMatrix[0][1] = 160;
        capacityMatrix[0][2] = 130;
        capacityMatrix[1][2] = 100;
        capacityMatrix[1][3] = 120;
        capacityMatrix[2][1] = 40;
        capacityMatrix[2][4] = 140;
        capacityMatrix[3][2] = 90;
        capacityMatrix[3][5] = 200;
        capacityMatrix[4][3] = 70;
        capacityMatrix[4][5] = 40;

        // 4.1 Ford-Fulkerson
        System.out.println("[4.1 Ford-Fulkerson Algorithm] Raw Material Flow Routing (DFS Augmenting):");
        int ffMaxFlow = FordFulkerson.computeMaxFlow(capacityMatrix, source, sink);
        System.out.printf("  -> Total throughput computed by Ford-Fulkerson: %d units/hr%n", ffMaxFlow);

        // 4.2 Edmonds-Karp
        System.out.println("\n[4.2 Edmonds-Karp Algorithm] Polynomial-Time Material Flow (BFS Shortest Path):");
        EdmondsKarp.FlowResult ekResult = EdmondsKarp.computeMaxFlow(capacityMatrix, source, sink);
        System.out.printf("  -> Total throughput verified by Edmonds-Karp: %d units/hr%n", ekResult.maxFlow);

        // 4.3 Dinic's Algorithm
        System.out.println("\n[4.3 Dinic's Algorithm] High-Throughput Network Optimization (Level Graph & Blocking Flow):");
        Dinic dinic = new Dinic(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (capacityMatrix[i][j] > 0) {
                    dinic.addEdge(i, j, capacityMatrix[i][j]);
                }
            }
        }
        int dinicMaxFlow = dinic.computeMaxFlow(source, sink);
        System.out.printf("  -> Total throughput verified by Dinic's Algorithm: %d units/hr%n", dinicMaxFlow);

        // 4.4 Bipartite Matching
        System.out.println("\n[4.4 Bipartite Matching] Certified Technician-to-Machine Staffing:");
        Map<String, List<String>> technicianQualifications = new LinkedHashMap<>();
        technicianQualifications.put("TECH-SARAH", Arrays.asList("MCH-101", "MCH-105"));
        technicianQualifications.put("TECH-ALEX", Arrays.asList("MCH-101", "MCH-102"));
        technicianQualifications.put("TECH-DAVE", Arrays.asList("MCH-103", "MCH-104"));
        technicianQualifications.put("TECH-EMMA", Arrays.asList("MCH-105", "MCH-106"));

        BipartiteMatching.AssignmentResult matchResult = BipartiteMatching.matchWorkersToMachines(technicianQualifications);
        System.out.printf("  -> Total technicians successfully scheduled: %d / %d%n",
                matchResult.maxMatches, technicianQualifications.size());
        for (Map.Entry<String, String> entry : matchResult.workerToMachineMap.entrySet()) {
            System.out.printf("      - %s assigned to certified station %s%n", entry.getKey(), entry.getValue());
        }

        // 4.5 Max-Flow Min-Cut Bottlenecks
        System.out.println("\n[4.5 Max-Flow Min-Cut Theorem] Factory Pipeline Bottleneck Identification:");
        MaxFlowMinCut.MinCutResult minCutResult = MaxFlowMinCut.findBottlenecks(capacityMatrix, source, sink, stationNames);
        System.out.printf("  -> Max Flow = Min Cut Capacity: %d units/hr%n", minCutResult.maxThroughput);
        System.out.printf("  -> Source Component S: %s%n", minCutResult.sourceSet);
        System.out.printf("  -> Sink Component T: %s%n", minCutResult.sinkSet);
        System.out.printf("  -> Saturated Bottleneck Links restricting factory output: %s%n", minCutResult.criticalBottlenecks);

        System.out.println();
    }

    private static void runModule5NP(List<Machine> machines) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" [MODULE 5: NP-Completeness and Approximation (CO5)]");
        System.out.println("--------------------------------------------------------------------------------");

        // 5.1 DPLL SAT Solver Demo
        System.out.println("[5.1 DPLL SAT Solver] Industrial Machine Interlock & Scheduling Logic:");
        // Safety interlock variables:
        // 1: MCH-101 (CNC) Active, 2: MCH-103 (Hydraulic) Active, 3: Coolant System Active, 4: Exhaust Fan Active
        // Constraints:
        // C1: (1 or 2)              -> At least one production line must run
        // C2: (-1 or 3)             -> If CNC runs, Coolant must be Active
        // C3: (-2 or 3)             -> If Hydraulic runs, Coolant must be Active
        // C4: (-3 or 4)             -> If Coolant is Active, Exhaust Fan must be Active
        // C5: (-1 or -2)            -> CNC and Hydraulic cannot run simultaneously on shared power bus
        List<List<Integer>> safetyClauses = new ArrayList<>();
        safetyClauses.add(Arrays.asList(1, 2));
        safetyClauses.add(Arrays.asList(-1, 3));
        safetyClauses.add(Arrays.asList(-2, 3));
        safetyClauses.add(Arrays.asList(-3, 4));
        safetyClauses.add(Arrays.asList(-1, -2));

        SATSolver.SATResult satResult = SATSolver.solve(4, safetyClauses);
        System.out.printf("  -> Evaluating 5 industrial safety interlock clauses across 4 variables...%n");
        System.out.printf("  -> Solver Result: %s%n", satResult);
        if (satResult.isSatisfiable) {
            System.out.printf("      - MCH-101 Active: %b%n", satResult.model.get(1));
            System.out.printf("      - MCH-103 Active: %b%n", satResult.model.get(2));
            System.out.printf("      - Coolant Pump Active: %b%n", satResult.model.get(3));
            System.out.printf("      - Exhaust Fan Active: %b%n", satResult.model.get(4));
        }

        // 5.2 Reductions Demo: 3-SAT -> CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER
        System.out.println("\n[5.2 Karp Reductions] 3-SAT -> CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER:");
        List<List<Integer>> threeSatFormula = new ArrayList<>();
        threeSatFormula.add(Arrays.asList(1, 2, -3));
        threeSatFormula.add(Arrays.asList(-1, 3, 4));
        threeSatFormula.add(Arrays.asList(2, -4, 1));

        Reductions.ReductionResult cliqueRed = Reductions.reduce3SatToClique(threeSatFormula);
        System.out.printf("  -> Step A: %s%n", cliqueRed.description);

        Reductions.ReductionResult indSetRed = Reductions.reduceCliqueToIndependentSet(cliqueRed.graph, cliqueRed.targetSize);
        System.out.printf("  -> Step B: %s%n", indSetRed.description);

        Reductions.ReductionResult vcRed = Reductions.reduceIndependentSetToVertexCover(indSetRed.graph, indSetRed.targetSize);
        System.out.printf("  -> Step C: %s%n", vcRed.description);

        // 5.3 Vertex Cover 2-Approximation Demo
        System.out.println("\n[5.3 Vertex Cover Approximation] Machine Hazard Conflict Monitoring:");
        // Conflict edges between machines where interference monitors are needed:
        // [0: CNC, 1: WELDING, 2: STAMPING, 3: PAINTING, 4: INSPECTION, 5: THERMAL]
        List<int[]> hazardConflicts = Arrays.asList(
                new int[]{0, 1}, new int[]{0, 2}, new int[]{1, 2},
                new int[]{2, 3}, new int[]{3, 4}, new int[]{4, 5},
                new int[]{1, 5}
        );

        VertexCoverApprox.CoverResult approxResult = VertexCoverApprox.compute2Approximation(6, hazardConflicts);
        VertexCoverApprox.CoverResult exactResult = VertexCoverApprox.computeExactMinimumCover(6, hazardConflicts);

        System.out.printf("  -> Conflict graph: 6 Machine stations with %d hazard interference edges.%n", hazardConflicts.size());
        System.out.printf("  -> 2-Approximation Cover: %s (Selected: %s)%n", approxResult.coverSize, approxResult.coveredMachineIndices);
        System.out.printf("  -> Exact Minimum Cover:   %s (Selected: %s)%n", exactResult.coverSize, exactResult.coveredMachineIndices);
        double empiricalRatio = (double) approxResult.coverSize / exactResult.coverSize;
        System.out.printf("  -> Empirical Approximation Ratio: %.2f (guaranteed <= 2.0x)%n", empiricalRatio);

        System.out.println();
    }

    private static void runModule6RandomizedParallel(List<ProductionOrder> orders) {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" [MODULE 6: Randomized and Parallel Algorithms (CO6)]");
        System.out.println("--------------------------------------------------------------------------------");

        // 6.1 Randomized QuickSort Demo
        System.out.println("[6.1 Randomized QuickSort] Ranking Production Orders by Priority:");
        List<ProductionOrder> sortableOrders = new ArrayList<>(orders);
        System.out.println("  -> Pre-sort Order Sequence:");
        for (ProductionOrder po : sortableOrders) {
            System.out.printf("      * %s | Priority: %d | Cost: $%.2f%n", po.getOrderId(), po.getPriority(), po.getEstimatedCost());
        }

        // Sort descending by priority, breaking ties by ascending cost
        RandomizedQuickSort.sort(sortableOrders, (a, b) -> {
            if (b.getPriority() != a.getPriority()) {
                return Integer.compare(b.getPriority(), a.getPriority());
            }
            return Double.compare(a.getEstimatedCost(), b.getEstimatedCost());
        });

        System.out.println("  -> Ranked Sequence (Randomized QuickSort O(N log N) expected):");
        for (ProductionOrder po : sortableOrders) {
            System.out.printf("      * %s | Priority: %d | Cost: $%.2f%n", po.getOrderId(), po.getPriority(), po.getEstimatedCost());
        }

        // 6.2 Miller-Rabin Primality Test Demo
        System.out.println("\n[6.2 Miller-Rabin] IIoT Cryptographic Key & Parameter Verification:");
        long[] candidatePrimes = {1_000_000_007L, 2_147_483_647L, 1_000_000_009L, 1_000_000_015L};
        for (long cand : candidatePrimes) {
            boolean isPrime = MillerRabin.isProbablePrime(cand, 20);
            System.out.printf("  -> Integer %,d is prime? %b (Error Bound <= 4^-20)%n", cand, isPrime);
        }

        // 6.3 Reservoir Sampling Demo
        System.out.println("\n[6.3 Reservoir Sampling] Uniform Telemetry Extraction from Machine Stream:");
        List<Double> continuousSensorFeed = new ArrayList<>();
        Random rand = new Random(999);
        for (int i = 0; i < 1000; i++) {
            continuousSensorFeed.add(45.0 + rand.nextDouble() * 50.0); // Temperature in Celsius
        }
        int sampleSize = 5;
        List<Double> representativeSample = ReservoirSampling.sample(continuousSensorFeed, sampleSize);
        System.out.printf("  -> Streamed %,d sensor telemetry readings into Reservoir (k=%d).%n", continuousSensorFeed.size(), sampleSize);
        System.out.printf("  -> Uniform Random Sample Retained: %s%n", representativeSample);

        // 6.4 Blelloch Parallel Prefix Sum (Scan) Demo
        System.out.println("\n[6.4 Blelloch Scan] Parallel Prefix Sum for Cumulative Output Statistics:");
        int[] hourlyUnitsPerCell = {12, 25, 8, 41, 19, 33, 14, 28}; // 8 manufacturing workcells
        BlellochScan.ScanResult scanResult = BlellochScan.executeScan(hourlyUnitsPerCell);
        System.out.printf("  -> Input Workcell Units:    %s%n", Arrays.toString(hourlyUnitsPerCell));
        System.out.printf("  -> Exclusive Prefix Sum:    %s%n", Arrays.toString(scanResult.exclusivePrefixSum));
        System.out.printf("  -> Inclusive Prefix Sum:    %s%n", Arrays.toString(scanResult.inclusivePrefixSum));
        System.out.printf("  -> Total Units Produced:    %d%n", scanResult.totalSum);
        System.out.printf("  -> Algorithmic Work (T1):   %d operations | Parallel Span (T_inf): %d steps%n",
                scanResult.work, scanResult.span);

        // 6.5 Parallel Tree Reduction Demo
        System.out.println("\n[6.5 Parallel Reduce] Distributed Telemetry Power Consumption Aggregation:");
        long[] powerReadingsWatts = {450, 1200, 890, 320, 2400, 1650, 980, 510};
        int serverCores = 4;
        ParallelReduce.ReductionResult sumResult = ParallelReduce.reduce(powerReadingsWatts, ParallelReduce.Operator.SUM, serverCores);
        ParallelReduce.ReductionResult peakResult = ParallelReduce.reduce(powerReadingsWatts, ParallelReduce.Operator.MAX, serverCores);
        System.out.printf("  -> Total Factory Power Draw:  %,d Watts (Work=%d, Span=%d, Speedup=%.2fx on %d cores)%n",
                sumResult.aggregatedValue, sumResult.work, sumResult.span, sumResult.speedupWithPProcessors, serverCores);
        System.out.printf("  -> Peak Workstation Spike:    %,d Watts%n", peakResult.aggregatedValue);

        // 6.6 Brent's Theorem Evaluation Demo
        System.out.println("\n[6.6 Brent's Theorem] Theoretical Speedup & Hardware Sizing Evaluation:");
        long bigDataWork = 100_000L; // 100k operations
        long pipelineSpan = 25L;     // critical path span 25 steps
        int[] coreConfigurations = {1, 2, 4, 8, 16, 32, 64};
        Map<Integer, BrentsTheorem.EfficiencyProfile> report = BrentsTheorem.generateScalabilityReport(bigDataWork, pipelineSpan, coreConfigurations);
        int halfPowerProcessors = BrentsTheorem.computeHalfPowerProcessorCount(bigDataWork, pipelineSpan);

        System.out.printf("  -> Workload Parameters: Work W = %,d ops | Span S = %d steps%n", bigDataWork, pipelineSpan);
        System.out.printf("  -> Half-Power Processor Allocation P_1/2: %d cores%n", halfPowerProcessors);
        System.out.println("  -> Core Scaling Profile:");
        for (BrentsTheorem.EfficiencyProfile prof : report.values()) {
            System.out.printf("      * %s%n", prof);
        }

        System.out.println();
    }
}
