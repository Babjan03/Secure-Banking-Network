package network;

public class IPAddressingTest {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("   PHASE 2 TEST: IP ADDRESSING & BANKING NETWORK ARCHITECTURE   ");
        System.out.println("================================================================");

        int passed = 0;
        int total = 0;

        // Test 1: Valid IP Validation
        String[] validIps = {"192.168.1.1", "10.0.0.1", "172.16.254.1", "127.0.0.1", "8.8.8.8"};
        for (String ip : validIps) {
            total++;
            boolean valid = IPAddress.isValid(ip);
            System.out.printf("[TEST %d] Validating valid IP: %-15s -> %s\n", total, ip, valid ? "PASS" : "FAIL");
            if (valid) passed++;
        }

        // Test 2: Invalid IP Validation
        String[] invalidIps = {"256.1.1.1", "192.168.1", "192.168.1.1.1", "192.168.01.1", "-1.0.0.1", "abc.def.ghi.jkl"};
        for (String ip : invalidIps) {
            total++;
            boolean valid = IPAddress.isValid(ip);
            System.out.printf("[TEST %d] Rejecting invalid IP: %-15s -> %s\n", total, ip, !valid ? "PASS" : "FAIL");
            if (!valid) passed++;
        }

        // Test 3: Binary Conversion & Classification
        total++;
        IPAddress testIp = new IPAddress("192.168.1.10");
        String expectedBinary = "11000000.10101000.00000001.00001010";
        boolean binMatch = testIp.toBinaryString().equals(expectedBinary);
        System.out.printf("[TEST %d] Binary conversion of %s: %s (Expected: %s) -> %s\n",
                total, testIp, testIp.toBinaryString(), expectedBinary, binMatch ? "PASS" : "FAIL");
        if (binMatch) passed++;

        total++;
        boolean isPrivate = testIp.isPrivateAddress();
        System.out.printf("[TEST %d] Private Address check for %s: %b -> %s\n", total, testIp, isPrivate, isPrivate ? "PASS" : "FAIL");
        if (isPrivate) passed++;

        // Test 4: Architecture Initialization of 10 Banking Nodes
        total++;
        NetworkArchitecture arch = new NetworkArchitecture();
        int nodeCount = arch.getAllNodes().size();
        System.out.printf("[TEST %d] Banking Network Nodes loaded: %d / 10 -> %s\n", total, nodeCount, nodeCount == 10 ? "PASS" : "FAIL");
        if (nodeCount == 10) passed++;

        System.out.println("\n--- Configured Banking Infrastructure Nodes ---");
        for (NetworkNode node : arch.getAllNodes().values()) {
            System.out.printf("  %-8s | %-35s | IP: %-15s | Type: %-12s | GW: %s\n",
                    node.getId(),
                    node.getName(),
                    node.getPrimaryIp(),
                    node.getType(),
                    node.getDefaultGateway() != null ? node.getDefaultGateway().toString() : "Direct/Routing");
        }

        System.out.println("================================================================");
        System.out.printf("TEST RESULTS: %d / %d Tests Passed.\n", passed, total);
        System.out.println("================================================================");

        if (passed != total) {
            System.exit(1);
        }
    }
}
