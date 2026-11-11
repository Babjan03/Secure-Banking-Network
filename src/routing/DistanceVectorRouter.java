package routing;

import network.NetworkTopology;

import java.util.*;

/**
 * Simulates an autonomous router running the Bellman-Ford Distance Vector algorithm.
 */
public class DistanceVectorRouter {

    private final String routerId;
    private final RoutingTable routingTable;
    private final Map<String, Integer> neighborLinkCosts; // Direct active link costs to neighbors

    public DistanceVectorRouter(String routerId) {
        this.routerId = routerId;
        this.routingTable = new RoutingTable(routerId);
        this.neighborLinkCosts = new HashMap<>();
    }

    public String getRouterId() {
        return routerId;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    /**
     * Initializes the routing table with directly connected neighbors from topology.
     */
    public void initializeFromTopology(NetworkTopology topology, List<String> allNodeIds) {
        neighborLinkCosts.clear();
        Map<String, Integer> activeNeighbors = topology.getActiveNeighborsWithCosts(routerId);
        neighborLinkCosts.putAll(activeNeighbors);

        for (String targetId : allNodeIds) {
            if (targetId.equals(routerId)) {
                routingTable.updateEntry(targetId, 0, targetId);
            } else if (neighborLinkCosts.containsKey(targetId)) {
                routingTable.updateEntry(targetId, neighborLinkCosts.get(targetId), targetId);
            } else {
                routingTable.updateEntry(targetId, RoutingTableEntry.INFINITY, null);
            }
        }
    }

    /**
     * Applies the Bellman-Ford equation using a received distance vector from a neighbor:
     * D_x(y) = min_v { c(x,v) + D_v(y) }
     *
     * @return true if the local routing table was updated (modified), false otherwise.
     */
    public boolean processNeighborVector(String neighborId, Map<String, Integer> neighborVector) {
        if (!neighborLinkCosts.containsKey(neighborId)) return false;

        int costToNeighbor = neighborLinkCosts.get(neighborId);
        boolean changed = false;

        for (Map.Entry<String, Integer> entry : neighborVector.entrySet()) {
            String dest = entry.getKey();
            int neighborDistToDest = entry.getValue();

            int candidateCost;
            if (neighborDistToDest >= RoutingTableEntry.INFINITY) {
                candidateCost = RoutingTableEntry.INFINITY;
            } else {
                candidateCost = costToNeighbor + neighborDistToDest;
            }

            RoutingTableEntry currentEntry = routingTable.getEntry(dest);
            if (currentEntry == null) {
                routingTable.updateEntry(dest, candidateCost, neighborId);
                changed = true;
            } else {
                // If neighbor offers a strictly shorter path, or this neighbor is our current next hop and its cost changed
                boolean isCurrentNextHop = neighborId.equals(currentEntry.getNextHop());
                if (candidateCost < currentEntry.getCost() || (isCurrentNextHop && candidateCost != currentEntry.getCost())) {
                    if (candidateCost < RoutingTableEntry.INFINITY || isCurrentNextHop) {
                        currentEntry.setCost(candidateCost);
                        currentEntry.setNextHop(candidateCost >= RoutingTableEntry.INFINITY ? null : neighborId);
                        changed = true;
                    }
                }
            }
        }

        return changed;
    }
}
