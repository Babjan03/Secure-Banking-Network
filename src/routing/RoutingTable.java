package routing;

import java.util.*;

/**
 * Encapsulates the routing table of a single network node.
 */
public class RoutingTable {

    private final String ownerNodeId;
    private final Map<String, RoutingTableEntry> entries;

    public RoutingTable(String ownerNodeId) {
        this.ownerNodeId = ownerNodeId;
        this.entries = new TreeMap<>();
    }

    public String getOwnerNodeId() {
        return ownerNodeId;
    }

    public void updateEntry(String dest, int cost, String nextHop) {
        entries.put(dest, new RoutingTableEntry(dest, cost, nextHop));
    }

    public RoutingTableEntry getEntry(String dest) {
        return entries.get(dest);
    }

    public Map<String, RoutingTableEntry> getEntries() {
        return Collections.unmodifiableMap(entries);
    }

    public Map<String, Integer> getDistanceVector() {
        Map<String, Integer> vector = new HashMap<>();
        for (Map.Entry<String, RoutingTableEntry> e : entries.entrySet()) {
            vector.put(e.getKey(), e.getValue().getCost());
        }
        return vector;
    }

    public String formatTable() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== ROUTING TABLE FOR [%s] ===\n", ownerNodeId));
        sb.append("Destination | Cost   | Next Hop  \n");
        sb.append("------------+--------+-----------\n");
        for (RoutingTableEntry entry : entries.values()) {
            sb.append(entry.toString()).append("\n");
        }
        return sb.toString();
    }
}
