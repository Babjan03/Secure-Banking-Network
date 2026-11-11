package network;

import java.util.*;

/**
 * Maintains the graph representation of the banking network topology:
 * nodes, links, link costs, dynamic status toggling, and neighbor lookups.
 */
public class NetworkTopology {

    private final NetworkArchitecture architecture;
    private final Map<String, NetworkLink> links;
    private final Map<String, List<NetworkLink>> adjacencyMap;

    public NetworkTopology() {
        this.architecture = new NetworkArchitecture();
        this.links = new LinkedHashMap<>();
        this.adjacencyMap = new HashMap<>();
        buildDefaultTopology();
    }

    private void buildDefaultTopology() {
        // Local Access Links (Cost 1, 1000 Mbps, 1 ms)
        addLink("L_ATM1_RTR1", "ATM-01", "RTR-BR1", 1, 1000, 1);
        addLink("L_ATM2_RTR1", "ATM-02", "RTR-BR1", 1, 1000, 1);
        addLink("L_CUST_RTR1", "CUST-01", "RTR-BR1", 1, 1000, 1);
        addLink("L_MGR_RTR2",  "MGR-01",  "RTR-BR2", 1, 1000, 1);
        addLink("L_SRV1_RTR2", "SRV-BR1", "RTR-BR2", 1, 1000, 1);

        // Core WAN Backbone Links
        // Link 1: RTR-BR1 <---> RTR-BR2 (Branch Backbone: Cost 4, 100 Mbps, 10 ms)
        addLink("L_RTR1_RTR2", "RTR-BR1", "RTR-BR2", 4, 100, 10);

        // Link 2: RTR-BR1 <---> RTR-HQ (Direct WAN Trunk: Cost 7, 50 Mbps, 25 ms)
        addLink("L_RTR1_HQ",   "RTR-BR1", "RTR-HQ",  7, 50, 25);

        // Link 3: RTR-BR2 <---> RTR-HQ (High-Speed Fiber Leased Line: Cost 2, 200 Mbps, 5 ms)
        addLink("L_RTR2_HQ",   "RTR-BR2", "RTR-HQ",  2, 200, 5);

        // Head Office Data Center Local Links (Cost 1, 10000 Mbps, 0 ms)
        addLink("L_HQ_SRVHQ",  "RTR-HQ",  "SRV-HQ",  1, 10000, 0);
        addLink("L_HQ_DBHQ",   "RTR-HQ",  "DB-HQ",   1, 10000, 0);
        addLink("L_SRVHQ_DBHQ","SRV-HQ",  "DB-HQ",   1, 10000, 0);
    }

    public void addLink(String id, String nodeA, String nodeB, int cost, int bandwidthMbps, int latencyMs) {
        NetworkLink link = new NetworkLink(id, nodeA, nodeB, cost, bandwidthMbps, latencyMs);
        links.put(id, link);

        adjacencyMap.computeIfAbsent(nodeA, k -> new ArrayList<>()).add(link);
        adjacencyMap.computeIfAbsent(nodeB, k -> new ArrayList<>()).add(link);
    }

    public NetworkArchitecture getArchitecture() {
        return architecture;
    }

    public Map<String, NetworkNode> getNodes() {
        return architecture.getAllNodes();
    }

    public NetworkNode getNode(String id) {
        return architecture.getNode(id);
    }

    public Map<String, NetworkLink> getLinks() {
        return Collections.unmodifiableMap(links);
    }

    public List<NetworkLink> getLinksForNode(String nodeId) {
        return adjacencyMap.getOrDefault(nodeId, Collections.emptyList());
    }

    public NetworkLink getLinkBetween(String nodeA, String nodeB) {
        for (NetworkLink link : links.values()) {
            if (link.connects(nodeA, nodeB)) return link;
        }
        return null;
    }

    /**
     * Toggles a link's operational status (UP/DOWN) to simulate link failure or recovery.
     */
    public boolean setLinkStatus(String linkId, boolean active) {
        NetworkLink link = links.get(linkId);
        if (link != null) {
            link.setActive(active);
            return true;
        }
        return false;
    }

    /**
     * Returns all active neighbors of a given node and the link connecting to them.
     */
    public Map<String, Integer> getActiveNeighborsWithCosts(String nodeId) {
        Map<String, Integer> neighbors = new HashMap<>();
        NetworkNode src = getNode(nodeId);
        if (src == null || !src.isOnline()) return neighbors;

        for (NetworkLink link : getLinksForNode(nodeId)) {
            if (!link.isActive()) continue;
            String other = link.getOtherNode(nodeId);
            NetworkNode target = getNode(other);
            if (target != null && target.isOnline()) {
                neighbors.put(other, link.getCost());
            }
        }
        return neighbors;
    }
}
