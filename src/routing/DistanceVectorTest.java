package routing;

import network.NetworkTopology;

public class DistanceVectorTest {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("   PHASE 5 TEST: DISTANCE VECTOR ROUTING (BELLMAN-FORD)         ");
        System.out.println("================================================================");

        NetworkTopology topology = new NetworkTopology();
        DistanceVectorSimulation sim = new DistanceVectorSimulation(topology);

        int passed = 0;
        int total = 0;

        // Test 1: Initial state before any vector exchange
        total++;
        DistanceVectorRouter rtr1 = sim.getRouter("RTR-BR1");
        int initialCostToHq = rtr1.getRoutingTable().getEntry("SRV-HQ").getCost();
        System.out.println("[TEST 1] Initial RTR-BR1 cost to SRV-HQ: " +
                (initialCostToHq >= RoutingTableEntry.INFINITY ? "INFINITY" : initialCostToHq) + " (Expected: INFINITY)");
        boolean t1Ok = initialCostToHq >= RoutingTableEntry.INFINITY;
        System.out.printf("         Result                        : %s\n", t1Ok ? "PASS" : "FAIL");
        if (t1Ok) passed++;

        // Test 2: Run step-by-step iterations and observe updates
        total++;
        int round = 0;
        while (!sim.isConverged() && round < 10) {
            round++;
            boolean conv = sim.step();
            System.out.printf("   [Round %d] Simulation step completed. Converged: %b\n", round, conv);
        }
        boolean t2Ok = sim.isConverged() && round <= 5;
        System.out.printf("[TEST 2] Convergence reached in %d rounds -> %s\n", round, t2Ok ? "PASS" : "FAIL");
        if (t2Ok) passed++;

        // Test 3: Converged Route Verification
        total++;
        int finalCostRtr1ToSrvHq = rtr1.getRoutingTable().getEntry("SRV-HQ").getCost();
        String nextHop = rtr1.getRoutingTable().getEntry("SRV-HQ").getNextHop();
        // Path from RTR-BR1 to SRV-HQ: RTR-BR1 -> RTR-BR2 (4) -> RTR-HQ (2) -> SRV-HQ (1) = Total 7 from RTR-BR1
        // (Whereas direct WAN to RTR-HQ is 7 + 1 = 8)
        System.out.printf("[TEST 3] Converged cost from RTR-BR1 to SRV-HQ: %d, NextHop: %s (Expected: 7 via RTR-BR2)\n",
                finalCostRtr1ToSrvHq, nextHop);
        boolean t3Ok = finalCostRtr1ToSrvHq == 7 && "RTR-BR2".equals(nextHop);
        System.out.printf("         Result: %s\n", t3Ok ? "PASS" : "FAIL");
        if (t3Ok) passed++;

        // Display sample routing table
        System.out.println("\n" + rtr1.getRoutingTable().formatTable());

        System.out.println("================================================================");
        System.out.printf("PHASE 5 RESULTS: %d / %d Tests Passed.\n", passed, total);
        System.out.println("================================================================");

        if (passed != total) System.exit(1);
    }
}
