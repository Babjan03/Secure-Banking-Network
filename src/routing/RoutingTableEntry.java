package routing;

/**
 * An individual entry in a router's Distance Vector routing table.
 */
public class RoutingTableEntry {

    public static final int INFINITY = 9999;

    private final String destination;
    private int cost;
    private String nextHop;

    public RoutingTableEntry(String destination, int cost, String nextHop) {
        this.destination = destination;
        this.cost = cost;
        this.nextHop = nextHop;
    }

    public String getDestination() {
        return destination;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-6s | %-10s",
                destination,
                cost >= INFINITY ? "INF" : String.valueOf(cost),
                nextHop != null ? nextHop : "DIRECT");
    }
}
