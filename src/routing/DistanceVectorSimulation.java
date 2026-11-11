package routing;

import network.NetworkTopology;

import java.util.*;

/**
 * Orchestrates synchronous or step-by-step Distance Vector iterations across the topology,
 * monitoring Bellman-Ford convergence and capturing step-by-step logs for UI visualization.
 */
public class DistanceVectorSimulation {

    public static class IterationSnapshot {
        public final int iteration;
        public final Map<String, Map<String, Integer>> routerVectors;
        public final boolean converged;

        public IterationSnapshot(int iteration, Map<String, Map<String, Integer>> routerVectors, boolean converged) {
            this.iteration = iteration;
            this.routerVectors = routerVectors;
            this.converged = converged;
        }
    }

    private final NetworkTopology topology;
    private final Map<String, DistanceVectorRouter> routers;
    private final List<IterationSnapshot> history;
    private int currentIteration;
    private boolean converged;

    public DistanceVectorSimulation(NetworkTopology topology) {
        this.topology = topology;
        this.routers = new LinkedHashMap<>();
        this.history = new ArrayList<>();
        this.currentIteration = 0;
        this.converged = false;
        reset();
    }

    public void reset() {
        routers.clear();
        history.clear();
        currentIteration = 0;
        converged = false;

        List<String> allNodeIds = new ArrayList<>(topology.getNodes().keySet());

        // Focus DV simulation on the 3 core banking routers: RTR-BR1, RTR-BR2, RTR-HQ
        for (String nodeId : allNodeIds) {
            DistanceVectorRouter rtr = new DistanceVectorRouter(nodeId);
            rtr.initializeFromTopology(topology, allNodeIds);
            routers.put(nodeId, rtr);
        }

        recordSnapshot(false);
    }

    /**
     * Executes one synchronous iteration: every router exchanges distance vectors with its active neighbors.
     * @return true if convergence was reached during this step.
     */
    public boolean step() {
        if (converged) return true;

        currentIteration++;
        boolean anyChanged = false;

        // Snapshot current vectors to simulate simultaneous transmission
        Map<String, Map<String, Integer>> currentVectors = new HashMap<>();
        for (Map.Entry<String, DistanceVectorRouter> entry : routers.entrySet()) {
            currentVectors.put(entry.getKey(), entry.getValue().getRoutingTable().getDistanceVector());
        }

        // Each router receives vectors from all its active neighbors
        for (Map.Entry<String, DistanceVectorRouter> entry : routers.entrySet()) {
            String nodeId = entry.getKey();
            DistanceVectorRouter router = entry.getValue();

            Map<String, Integer> neighbors = topology.getActiveNeighborsWithCosts(nodeId);
            for (String neighborId : neighbors.keySet()) {
                Map<String, Integer> neighborVec = currentVectors.get(neighborId);
                if (neighborVec != null) {
                    boolean changed = router.processNeighborVector(neighborId, neighborVec);
                    if (changed) {
                        anyChanged = true;
                    }
                }
            }
        }

        if (!anyChanged) {
            this.converged = true;
        }

        recordSnapshot(this.converged);
        return this.converged;
    }

    /**
     * Runs full simulation until convergence or max iterations (to guard against count-to-infinity).
     */
    public int runToConvergence(int maxIterations) {
        while (!converged && currentIteration < maxIterations) {
            step();
        }
        return currentIteration;
    }

    private void recordSnapshot(boolean isConverged) {
        Map<String, Map<String, Integer>> snapshot = new LinkedHashMap<>();
        for (Map.Entry<String, DistanceVectorRouter> e : routers.entrySet()) {
            snapshot.put(e.getKey(), new HashMap<>(e.getValue().getRoutingTable().getDistanceVector()));
        }
        history.add(new IterationSnapshot(currentIteration, snapshot, isConverged));
    }

    public Map<String, DistanceVectorRouter> getRouters() {
        return Collections.unmodifiableMap(routers);
    }

    public DistanceVectorRouter getRouter(String id) {
        return routers.get(id);
    }

    public List<IterationSnapshot> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public boolean isConverged() {
        return converged;
    }

    public int getCurrentIteration() {
        return currentIteration;
    }
}
