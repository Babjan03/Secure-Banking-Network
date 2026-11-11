package routing;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the output of a shortest path routing calculation.
 */
public class RouteResult {

    private final String source;
    private final String destination;
    private final int totalCost;
    private final List<String> pathNodes;
    private final boolean reachable;
    private final String algorithm;

    public RouteResult(String source, String destination, int totalCost, List<String> pathNodes, boolean reachable, String algorithm) {
        this.source = source;
        this.destination = destination;
        this.totalCost = totalCost;
        this.pathNodes = pathNodes != null ? pathNodes : Collections.emptyList();
        this.reachable = reachable;
        this.algorithm = algorithm;
    }

    public static RouteResult unreachable(String source, String destination, String algorithm) {
        return new RouteResult(source, destination, Integer.MAX_VALUE, Collections.emptyList(), false, algorithm);
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public List<String> getPathNodes() {
        return Collections.unmodifiableList(pathNodes);
    }

    public boolean isReachable() {
        return reachable;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public int getHopCount() {
        return pathNodes.size() > 1 ? pathNodes.size() - 1 : 0;
    }

    public String getFormattedPath() {
        if (!reachable || pathNodes.isEmpty()) {
            return "NO ROUTE FOUND (Destination Unreachable)";
        }
        return String.join("  --->  ", pathNodes);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s to %s | Cost: %s | Hops: %d | Path: %s",
                algorithm, source, destination,
                reachable ? String.valueOf(totalCost) : "INFINITY",
                getHopCount(), getFormattedPath());
    }
}
