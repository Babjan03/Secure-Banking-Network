package reliability;

import java.util.*;

/**
 * Interactive Go-Back-N (GBN) Sliding Window Automatic Repeat reQuest (ARQ) Simulation.
 * Implements sliding window protocol with sequence numbers, frame drop simulation,
 * out-of-order rejection, cumulative acknowledgements, and window-wide retransmissions upon timeout.
 */
public class GoBackNSimulation {

    public static class SimulationEvent {
        public final int eventId;
        public final String type; // "TRANSMIT", "LOSS", "RECEIVE", "DISCARD", "ACK", "TIMEOUT", "RETRANSMIT"
        public final String description;
        public final int sendBase;
        public final int nextSeqNum;
        public final int expectedSeqNum;

        public SimulationEvent(int eventId, String type, String description, int sendBase, int nextSeqNum, int expectedSeqNum) {
            this.eventId = eventId;
            this.type = type;
            this.description = description;
            this.sendBase = sendBase;
            this.nextSeqNum = nextSeqNum;
            this.expectedSeqNum = expectedSeqNum;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s | Window: [%d..%d) | Receiver Expected: %d",
                    type, description, sendBase, nextSeqNum, expectedSeqNum);
        }
    }

    private int windowSize;
    private int totalFrames;
    private int sendBase;
    private int nextSeqNum;
    private int expectedSeqNum;
    private final List<Frame> frames;
    private final Set<Integer> lostFrameSequences; // Frame numbers designated to be dropped
    private final List<SimulationEvent> eventLog;
    private int eventCounter;

    public GoBackNSimulation(int windowSize, int totalFrames) {
        this.windowSize = Math.max(1, windowSize);
        this.totalFrames = Math.max(1, totalFrames);
        this.frames = new ArrayList<>();
        this.lostFrameSequences = new HashSet<>();
        this.eventLog = new ArrayList<>();
        reset();
    }

    public void reset() {
        this.sendBase = 0;
        this.nextSeqNum = 0;
        this.expectedSeqNum = 0;
        this.eventCounter = 0;
        this.frames.clear();
        this.lostFrameSequences.clear();
        this.eventLog.clear();

        for (int i = 0; i < totalFrames; i++) {
            frames.add(new Frame(i, "TX_PKT_" + i));
        }

        log("INIT", "Simulation initialized. Window Size N = " + windowSize + ", Total Frames = " + totalFrames);
    }

    public void designateLoss(int seqNum) {
        lostFrameSequences.add(seqNum);
        log("CONFIG", "Configured intentional packet loss for Frame " + seqNum);
    }

    public void clearLossDesignations() {
        lostFrameSequences.clear();
    }

    /**
     * Attempts to transmit frames allowed within the sender's current window [sendBase, sendBase + windowSize).
     */
    public boolean transmitWindow() {
        boolean sentAny = false;
        while (nextSeqNum < sendBase + windowSize && nextSeqNum < totalFrames) {
            Frame frame = frames.get(nextSeqNum);
            frame.incrementAttempts();

            if (lostFrameSequences.contains(frame.getSequenceNumber())) {
                // Simulate frame drop
                frame.setStatus(Frame.Status.LOST);
                lostFrameSequences.remove(frame.getSequenceNumber()); // Consume drop
                log("LOSS", "Frame F" + frame.getSequenceNumber() + " dropped in transit (Network Congestion / Noise)!");
            } else {
                frame.setStatus(Frame.Status.IN_TRANSIT);
                log("TRANSMIT", "Sender dispatched Frame F" + frame.getSequenceNumber() + " (Attempt #" + frame.getTransmissionAttempts() + ")");
                processReceiver(frame);
            }

            nextSeqNum++;
            sentAny = true;
        }
        return sentAny;
    }

    private void processReceiver(Frame frame) {
        int seq = frame.getSequenceNumber();
        if (seq == expectedSeqNum) {
            frame.setStatus(Frame.Status.RECEIVED);
            log("RECEIVE", "Receiver accepted in-order Frame F" + seq + ". Generating cumulative ACK" + seq);
            expectedSeqNum++;

            // Process cumulative ACK back to sender
            processAck(seq);
        } else {
            // Out-of-order frame in Go-Back-N must be discarded!
            frame.setStatus(Frame.Status.DISCARDED);
            int lastAck = expectedSeqNum - 1;
            log("DISCARD", "Receiver rejected out-of-order Frame F" + seq + " (Expected F" + expectedSeqNum + "). Duplicate ACK" + lastAck + " resent.");
        }
    }

    private void processAck(int ackNum) {
        // In GBN, an ACK for seqNum cumulatively acknowledges all frames up to and including ackNum
        for (int i = sendBase; i <= ackNum; i++) {
            if (i < frames.size()) {
                frames.get(i).setStatus(Frame.Status.ACKNOWLEDGED);
            }
        }
        int oldBase = sendBase;
        sendBase = ackNum + 1;
        log("ACK", "Sender received ACK" + ackNum + ". Window slides from [" + oldBase + "] to [" + sendBase + "]");
    }

    /**
     * Simulates timer expiration for the oldest unacknowledged frame (sendBase).
     * Triggers retransmission of all frames currently in window starting from sendBase!
     */
    public void triggerTimeout() {
        if (sendBase >= totalFrames || sendBase == nextSeqNum) {
            log("INFO", "No unacknowledged frames in window to timeout.");
            return;
        }

        log("TIMEOUT", "Timer expired for Frame F" + sendBase + "! Initiating Go-Back-N retransmission of window.");
        // GBN rewind: reset nextSeqNum back to sendBase
        nextSeqNum = sendBase;
        transmitWindow();
    }

    /**
     * Executes the simulation until all frames are successfully acknowledged.
     */
    public void runToCompletion(int maxRounds) {
        int rounds = 0;
        while (sendBase < totalFrames && rounds < maxRounds) {
            rounds++;
            boolean sent = transmitWindow();
            if (!sent || sendBase < nextSeqNum) {
                // If frames were transmitted but sendBase has not advanced to nextSeqNum, we simulate timeout
                if (sendBase < nextSeqNum && frames.get(sendBase).getStatus() != Frame.Status.ACKNOWLEDGED) {
                    triggerTimeout();
                }
            }
        }
    }

    private void log(String type, String desc) {
        eventCounter++;
        eventLog.add(new SimulationEvent(eventCounter, type, desc, sendBase, nextSeqNum, expectedSeqNum));
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = Math.max(1, windowSize);
    }

    public int getSendBase() {
        return sendBase;
    }

    public int getNextSeqNum() {
        return nextSeqNum;
    }

    public int getExpectedSeqNum() {
        return expectedSeqNum;
    }

    public List<Frame> getFrames() {
        return Collections.unmodifiableList(frames);
    }

    public List<SimulationEvent> getEventLog() {
        return Collections.unmodifiableList(eventLog);
    }

    public boolean isComplete() {
        return sendBase >= totalFrames;
    }
}
