package network;

import java.util.Objects;

/**
 * Represents a physical or logical link connecting two network devices
 * with distance/cost metric, bandwidth, and operational status.
 */
public class NetworkLink {

    private final String id;
    private final String sourceNodeId;
    private final String targetNodeId;
    private int cost;
    private final int bandwidthMbps;
    private final int latencyMs;
    private boolean active;

    public NetworkLink(String id, String sourceNodeId, String targetNodeId, int cost, int bandwidthMbps, int latencyMs) {
        this.id = id;
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.cost = cost;
        this.bandwidthMbps = bandwidthMbps;
        this.latencyMs = latencyMs;
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public String getSourceNodeId() {
        return sourceNodeId;
    }

    public String getTargetNodeId() {
        return targetNodeId;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getBandwidthMbps() {
        return bandwidthMbps;
    }

    public int getLatencyMs() {
        return latencyMs;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean connects(String nodeA, String nodeB) {
        return (sourceNodeId.equals(nodeA) && targetNodeId.equals(nodeB)) ||
               (sourceNodeId.equals(nodeB) && targetNodeId.equals(nodeA));
    }

    public String getOtherNode(String nodeId) {
        if (sourceNodeId.equals(nodeId)) return targetNodeId;
        if (targetNodeId.equals(nodeId)) return sourceNodeId;
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s <---> %s (Cost: %d, %d Mbps, %d ms) [%s]",
                sourceNodeId, targetNodeId, cost, bandwidthMbps, latencyMs, active ? "UP" : "DOWN");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkLink that = (NetworkLink) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
