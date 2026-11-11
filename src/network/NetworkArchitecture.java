package network;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines the standard topology and static IP configuration for the 10 banking network nodes
 * as specified in the banking network system architecture.
 */
public class NetworkArchitecture {

    // Subnet Definitions
    public static final String SUBNET_ATM = "192.168.1.0/24";
    public static final String SUBNET_CUSTOMER_SERVICE = "192.168.2.0/24";
    public static final String SUBNET_MANAGER = "192.168.3.0/24";
    public static final String SUBNET_BRANCH_SERVER = "192.168.4.0/24";
    public static final String SUBNET_HEAD_OFFICE = "192.168.5.0/24";
    public static final String SUBNET_DATABASE = "192.168.6.0/24";

    private final Map<String, NetworkNode> nodes;

    public NetworkArchitecture() {
        this.nodes = new LinkedHashMap<>();
        initializeDefaultNodes();
    }

    private void initializeDefaultNodes() {
        // 1. ATM 1
        NetworkNode atm1 = new NetworkNode(
            "ATM-01",
            "ATM Terminal 1 (Retail Branch A)",
            NodeType.ATM,
            new IPAddress("192.168.1.10"),
            SUBNET_ATM,
            new IPAddress("192.168.1.1"),
            "02:00:00:00:01:10"
        );
        nodes.put(atm1.getId(), atm1);

        // 2. ATM 2
        NetworkNode atm2 = new NetworkNode(
            "ATM-02",
            "ATM Terminal 2 (Drive-Through Branch A)",
            NodeType.ATM,
            new IPAddress("192.168.1.20"),
            SUBNET_ATM,
            new IPAddress("192.168.1.1"),
            "02:00:00:00:01:20"
        );
        nodes.put(atm2.getId(), atm2);

        // 3. Customer Service System
        NetworkNode custService = new NetworkNode(
            "CUST-01",
            "Customer Service Desk Workstation",
            NodeType.WORKSTATION,
            new IPAddress("192.168.2.10"),
            SUBNET_CUSTOMER_SERVICE,
            new IPAddress("192.168.2.1"),
            "02:00:00:00:02:10"
        );
        nodes.put(custService.getId(), custService);

        // 4. Manager System
        NetworkNode managerSys = new NetworkNode(
            "MGR-01",
            "Branch Operations Manager Terminal",
            NodeType.WORKSTATION,
            new IPAddress("192.168.3.10"),
            SUBNET_MANAGER,
            new IPAddress("192.168.3.1"),
            "02:00:00:00:03:10"
        );
        nodes.put(managerSys.getId(), managerSys);

        // 5. Branch Router 1 (Connects ATM Subnet, Cust Service Subnet & Backbone)
        NetworkNode router1 = new NetworkNode(
            "RTR-BR1",
            "Branch Router 1 (Gateway Subnets 1 & 2)",
            NodeType.ROUTER,
            new IPAddress("192.168.1.1"),
            SUBNET_ATM,
            null, // Default gateway is other routers via dynamic routing
            "02:00:00:00:01:01"
        );
        router1.addInterface(new IPAddress("192.168.2.1")); // Interface to Customer Service Subnet
        router1.addInterface(new IPAddress("10.0.1.1"));    // WAN Backbone link interface
        nodes.put(router1.getId(), router1);

        // 6. Branch Router 2 (Connects Manager Subnet, Branch Server Subnet & Backbone)
        NetworkNode router2 = new NetworkNode(
            "RTR-BR2",
            "Branch Router 2 (Gateway Subnets 3 & 4)",
            NodeType.ROUTER,
            new IPAddress("192.168.3.1"),
            SUBNET_MANAGER,
            null,
            "02:00:00:00:03:01"
        );
        router2.addInterface(new IPAddress("192.168.4.1")); // Interface to Branch Server Subnet
        router2.addInterface(new IPAddress("10.0.2.1"));    // WAN Backbone link interface
        nodes.put(router2.getId(), router2);

        // 7. Head Office Router (Connects HO Network, DB Network & Backbone)
        NetworkNode routerHQ = new NetworkNode(
            "RTR-HQ",
            "Head Office Core Router (Gateway Subnets 5 & 6)",
            NodeType.ROUTER,
            new IPAddress("192.168.5.1"),
            SUBNET_HEAD_OFFICE,
            null,
            "02:00:00:00:05:01"
        );
        routerHQ.addInterface(new IPAddress("192.168.6.1")); // Interface to Secure Database Subnet
        routerHQ.addInterface(new IPAddress("10.0.3.1"));    // WAN Backbone link interface
        nodes.put(routerHQ.getId(), routerHQ);

        // 8. Branch Server
        NetworkNode branchServer = new NetworkNode(
            "SRV-BR1",
            "Branch Local Application Server",
            NodeType.SERVER,
            new IPAddress("192.168.4.10"),
            SUBNET_BRANCH_SERVER,
            new IPAddress("192.168.4.1"),
            "02:00:00:00:04:10"
        );
        nodes.put(branchServer.getId(), branchServer);

        // 9. Head Office Server
        NetworkNode hqServer = new NetworkNode(
            "SRV-HQ",
            "Head Office Central Banking Server",
            NodeType.SERVER,
            new IPAddress("192.168.5.10"),
            SUBNET_HEAD_OFFICE,
            new IPAddress("192.168.5.1"),
            "02:00:00:00:05:10"
        );
        nodes.put(hqServer.getId(), hqServer);

        // 10. Database Server
        NetworkNode dbServer = new NetworkNode(
            "DB-HQ",
            "Central Ledger & Transaction Database Server",
            NodeType.DATABASE,
            new IPAddress("192.168.6.10"),
            SUBNET_DATABASE,
            new IPAddress("192.168.6.1"),
            "02:00:00:00:06:10"
        );
        nodes.put(dbServer.getId(), dbServer);
    }

    public Map<String, NetworkNode> getAllNodes() {
        return Collections.unmodifiableMap(nodes);
    }

    public NetworkNode getNode(String id) {
        return nodes.get(id);
    }

    public NetworkNode findNodeByIp(IPAddress ip) {
        for (NetworkNode node : nodes.values()) {
            if (node.hasIp(ip)) return node;
        }
        return null;
    }
}
