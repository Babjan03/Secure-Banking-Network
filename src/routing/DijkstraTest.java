package routing;

import network.NetworkTopology;

public class DijkstraTest {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("   PHASE 4 TEST: DIJKSTRA'S SHORTEST PATH ALGORITHM             ");
        System.out.println("================================================================");

        NetworkTopology topology = new NetworkTopology();
        int passed = 0;
        int total = 0;

        // Test 1: Optimal Path ATM-01 -> SRV-HQ
        total++;
        RouteResult route1 = Dijkstra.findShortestPath(topology, "ATM-01", "SRV-HQ");
        System.out.println("[TEST 1] ATM-01 to SRV-HQ Path: " + route1.getFormattedPath());
        System.out.println("         Total Route Cost     : " + route1.getTotalCost() + " (Expected: 8)");
        boolean t1Ok = route1.isReachable() && route1.getTotalCost() == 8;
        System.out.printf("         Result               : %s\n", t1Ok ? "PASS" : "FAIL");
        if (t1Ok) passed++;

        // Test 2: Dynamic Link Failure Simulation
        // Fail the primary leased line between RTR-BR2 and RTR-HQ
        total++;
        System.out.println("\n[SIMULATION] Deactivating Link L_RTR2_HQ (Simulating WAN fiber cut)...");
        topology.setLinkStatus("L_RTR2_HQ", false);

        RouteResult routeFailover = Dijkstra.findShortestPath(topology, "ATM-01", "SRV-HQ");
        System.out.println("[TEST 2] Rerouted Path        : " + routeFailover.getFormattedPath());
        System.out.println("         New Failover Cost    : " + routeFailover.getTotalCost() + " (Expected: 9)");
        boolean t2Ok = routeFailover.isReachable() && routeFailover.getTotalCost() == 9
                && routeFailover.getPathNodes().contains("RTR-HQ");
        System.out.printf("         Result               : %s\n", t2Ok ? "PASS" : "FAIL");
        if (t2Ok) passed++;

        // Test 3: Link Recovery
        total++;
        System.out.println("\n[SIMULATION] Restoring Link L_RTR2_HQ...");
        topology.setLinkStatus("L_RTR2_HQ", true);
        RouteResult routeRestored = Dijkstra.findShortestPath(topology, "ATM-01", "SRV-HQ");
        boolean t3Ok = routeRestored.getTotalCost() == 8;
        System.out.println("[TEST 3] Restored Path Cost   : " + routeRestored.getTotalCost() + " (Expected: 8)");
        System.out.printf("         Result               : %s\n", t3Ok ? "PASS" : "FAIL");
        if (t3Ok) passed++;

        // Test 4: Unreachable Destination Test
        total++;
        System.out.println("\n[SIMULATION] Isolating DB-HQ by disabling all incoming links...");
        topology.setLinkStatus("L_HQ_DBHQ", false);
        topology.setLinkStatus("L_SRVHQ_DBHQ", false);

        RouteResult routeIsolated = Dijkstra.findShortestPath(topology, "ATM-01", "DB-HQ");
        System.out.println("[TEST 4] Isolated Route Check : " + routeIsolated.getFormattedPath());
        boolean t4Ok = !routeIsolated.isReachable();
        System.out.printf("         Result               : %s\n", t4Ok ? "PASS" : "FAIL");
        if (t4Ok) passed++;

        // Restore links for future use
        topology.setLinkStatus("L_HQ_DBHQ", true);
        topology.setLinkStatus("L_SRVHQ_DBHQ", true);

        System.out.println("================================================================");
        System.out.printf("PHASE 4 RESULTS: %d / %d Tests Passed.\n", passed, total);
        System.out.println("================================================================");

        if (passed != total) System.exit(1);
    }
}
