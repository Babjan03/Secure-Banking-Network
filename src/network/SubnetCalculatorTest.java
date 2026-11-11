package network;

public class SubnetCalculatorTest {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("   PHASE 3 TEST: SUBNETTING & TOPOLOGY GRAPH                   ");
        System.out.println("================================================================");

        int passed = 0;
        int total = 0;

        // Test 1: Subnet calculation for ATM Subnet 192.168.1.10/24
        total++;
        SubnetCalculator.SubnetDetails atmSub = SubnetCalculator.calculateCIDR("192.168.1.10/24");
        boolean netMatch = atmSub.networkAddress.toString().equals("192.168.1.0");
        boolean bcastMatch = atmSub.broadcastAddress.toString().equals("192.168.1.255");
        boolean hostsMatch = atmSub.usableHosts == 254;
        System.out.printf("[TEST %d] ATM Subnet Calculation: Net=%s, Bcast=%s, UsableHosts=%d -> %s\n",
                total, atmSub.networkAddress, atmSub.broadcastAddress, atmSub.usableHosts,
                (netMatch && bcastMatch && hostsMatch) ? "PASS" : "FAIL");
        if (netMatch && bcastMatch && hostsMatch) passed++;

        // Test 2: Subnet calculation for Point-to-Point WAN link /30
        total++;
        SubnetCalculator.SubnetDetails wanSub = SubnetCalculator.calculateCIDR("10.0.1.1/30");
        boolean wanNet = wanSub.networkAddress.toString().equals("10.0.1.0");
        boolean wanBcast = wanSub.broadcastAddress.toString().equals("10.0.1.3");
        boolean wanHosts = wanSub.usableHosts == 2;
        System.out.printf("[TEST %d] WAN /30 Subnet: Net=%s, Bcast=%s, UsableHosts=%d -> %s\n",
                total, wanSub.networkAddress, wanSub.broadcastAddress, wanSub.usableHosts,
                (wanNet && wanBcast && wanHosts) ? "PASS" : "FAIL");
        if (wanNet && wanBcast && wanHosts) passed++;

        // Test 3: Same Subnet Membership Check
        total++;
        IPAddress ipAtm1 = new IPAddress("192.168.1.10");
        IPAddress ipAtm2 = new IPAddress("192.168.1.20");
        IPAddress ipCust = new IPAddress("192.168.2.10");
        boolean sameSubAtm = SubnetCalculator.isSameSubnet(ipAtm1, ipAtm2, 24);
        boolean diffSubCust = !SubnetCalculator.isSameSubnet(ipAtm1, ipCust, 24);
        System.out.printf("[TEST %d] Membership check: ATM1 & ATM2 same=%b, ATM1 & Cust diff=%b -> %s\n",
                total, sameSubAtm, diffSubCust, (sameSubAtm && diffSubCust) ? "PASS" : "FAIL");
        if (sameSubAtm && diffSubCust) passed++;

        // Test 4: Topology Graph Construction & Active Neighbors
        total++;
        NetworkTopology topology = new NetworkTopology();
        int totalLinks = topology.getLinks().size();
        System.out.printf("[TEST %d] Topology Graph links loaded: %d (Expected: 11) -> %s\n",
                total, totalLinks, totalLinks == 11 ? "PASS" : "FAIL");
        if (totalLinks == 11) passed++;

        // Test 5: Router 1 Neighbor Verification
        total++;
        var rtr1Neighbors = topology.getActiveNeighborsWithCosts("RTR-BR1");
        boolean rtr1HasAtm = rtr1Neighbors.containsKey("ATM-01");
        boolean rtr1HasRtr2 = rtr1Neighbors.containsKey("RTR-BR2");
        System.out.printf("[TEST %d] Router RTR-BR1 active neighbors: %s -> %s\n",
                total, rtr1Neighbors.keySet(), (rtr1HasAtm && rtr1HasRtr2) ? "PASS" : "FAIL");
        if (rtr1HasAtm && rtr1HasRtr2) passed++;

        System.out.println("================================================================");
        System.out.printf("PHASE 3 RESULTS: %d / %d Tests Passed.\n", passed, total);
        System.out.println("================================================================");

        if (passed != total) System.exit(1);
    }
}
