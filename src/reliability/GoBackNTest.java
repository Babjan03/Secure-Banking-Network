package reliability;

public class GoBackNTest {
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("   PHASE 9 TEST: SLIDING WINDOW & GO-BACK-N ARQ SIMULATION      ");
        System.out.println("================================================================");

        int passed = 0;
        int total = 0;

        int windowSize = 4;
        int totalFrames = 8;
        GoBackNSimulation sim = new GoBackNSimulation(windowSize, totalFrames);

        // Configure Frame 2 to be intentionally dropped in transit
        sim.designateLoss(2);

        System.out.println("Initial State: Window Size = " + windowSize + ", Frames = " + totalFrames);
        System.out.println("Injecting intentional packet loss on Frame F2...");

        // Step 1: Initial window transmission
        sim.transmitWindow();

        // Verify that F0 and F1 are acknowledged, F2 is LOST, F3 is DISCARDED
        total++;
        boolean f0Ack = sim.getFrames().get(0).getStatus() == Frame.Status.ACKNOWLEDGED;
        boolean f1Ack = sim.getFrames().get(1).getStatus() == Frame.Status.ACKNOWLEDGED;
        boolean f2Lost = sim.getFrames().get(2).getStatus() == Frame.Status.LOST;
        boolean f3Discarded = sim.getFrames().get(3).getStatus() == Frame.Status.DISCARDED;
        System.out.printf("[TEST 1] Initial window transmission (F0: %s, F1: %s, F2: %s, F3: %s)\n",
                sim.getFrames().get(0).getStatus(), sim.getFrames().get(1).getStatus(),
                sim.getFrames().get(2).getStatus(), sim.getFrames().get(3).getStatus());
        boolean t1Ok = f0Ack && f1Ack && f2Lost && f3Discarded;
        System.out.printf("         Result: %s\n", t1Ok ? "PASS" : "FAIL");
        if (t1Ok) passed++;

        // Step 2: Trigger Timeout for lost frame F2
        total++;
        System.out.println("\nSimulating ACK Timeout for Frame F2...");
        sim.triggerTimeout();

        // After timeout retransmission, F2 and F3 should now be received
        boolean f2Retransmitted = sim.getFrames().get(2).getStatus() == Frame.Status.ACKNOWLEDGED;
        boolean f3Retransmitted = sim.getFrames().get(3).getStatus() == Frame.Status.ACKNOWLEDGED;
        System.out.printf("[TEST 2] Retransmission after Timeout (F2: %s, F3: %s)\n",
                sim.getFrames().get(2).getStatus(), sim.getFrames().get(3).getStatus());
        boolean t2Ok = f2Retransmitted && f3Retransmitted;
        System.out.printf("         Result: %s\n", t2Ok ? "PASS" : "FAIL");
        if (t2Ok) passed++;

        // Step 3: Run to completion
        total++;
        sim.runToCompletion(10);
        boolean allComplete = sim.isComplete();
        System.out.printf("[TEST 3] Simulation run to completion: All %d frames ACKed -> %s\n",
                totalFrames, allComplete ? "PASS" : "FAIL");
        if (allComplete) passed++;

        System.out.println("\n--- Event Execution Log Summary ---");
        for (GoBackNSimulation.SimulationEvent ev : sim.getEventLog()) {
            System.out.println("  " + ev);
        }

        System.out.println("================================================================");
        System.out.printf("PHASE 9 RESULTS: %d / %d Tests Passed.\n", passed, total);
        System.out.println("================================================================");

        if (passed != total) System.exit(1);
    }
}
