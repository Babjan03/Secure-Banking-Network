package routing;

import network.NetworkTopology;

import java.util.*;

/**
 * Implementation of Dijkstra's Shortest Path Algorithm (Link State routing)
 * using an adjacency list and min-priority queue.
 */
public class Dijkstra {

    private static class NodeEntry implements Comparable<NodeEntry> {
        final String nodeId;
        final int distance;

        NodeEntry(String nodeId, int distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeEntry o) {
            return Integer.compare(this.distance, o.distance);
        }
    }

    /**
     * Computes the shortest path from source to destination across active topology links.
     */
    public static RouteResult findShortestPath(NetworkTopology topology, String sourceId, String destId) {
        if (topology == null || sourceId == null || destId == null) {
            return RouteResult.unreachable(sourceId, destId, "Dijkstra");
        }

        if (sourceId.equals(destId)) {
            return new RouteResult(sourceId, destId, 0, List.of(sourceId), true, "Dijkstra");
        }

        if (topology.getNode(sourceId) == null || topology.getNode(destId) == null) {
            return RouteResult.unreachable(sourceId, destId, "Dijkstra");
        }

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<NodeEntry> pq = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();

        // Initialize distances
        for (String nodeId : topology.getNodes().keySet()) {
            dist.put(nodeId, Integer.MAX_VALUE);
        }
        dist.put(sourceId, 0);
        pq.offer(new NodeEntry(sourceId, 0));

        while (!pq.isEmpty()) {
            NodeEntry current = pq.poll();
            String u = current.nodeId;

            if (visited.contains(u)) continue;
            visited.add(u);

            if (u.equals(destId)) break;

            Map<String, Integer> neighbors = topology.getActiveNeighborsWithCosts(u);
            for (Map.Entry<String, Integer> edge : neighbors.entrySet()) {
                String v = edge.getKey();
                int weight = edge.getValue();

                if (visited.contains(v)) continue;

                int alt = dist.get(u) + weight;
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    pq.offer(new NodeEntry(v, alt));
                }
            }
        }

        int finalCost = dist.getOrDefault(destId, Integer.MAX_VALUE);
        if (finalCost == Integer.MAX_VALUE || !visited.contains(destId)) {
            return RouteResult.unreachable(sourceId, destId, "Dijkstra");
        }

        // Reconstruct path
        LinkedList<String> path = new LinkedList<>();
        String curr = destId;
        while (curr != null) {
            path.addFirst(curr);
            curr = prev.get(curr);
        }

        return new RouteResult(sourceId, destId, finalCost, path, true, "Dijkstra");
    }
}
