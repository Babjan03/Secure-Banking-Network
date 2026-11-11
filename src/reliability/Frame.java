package reliability;

/**
 * Represents a data frame or acknowledgement in the Go-Back-N ARQ protocol simulation.
 */
public class Frame {

    public enum Status {
        PENDING,
        IN_TRANSIT,
        RECEIVED,
        DISCARDED,
        LOST,
        ACKNOWLEDGED
    }

    private final int sequenceNumber;
    private final String payload;
    private Status status;
    private int transmissionAttempts;

    public Frame(int sequenceNumber, String payload) {
        this.sequenceNumber = sequenceNumber;
        this.payload = payload;
        this.status = Status.PENDING;
        this.transmissionAttempts = 0;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getPayload() {
        return payload;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getTransmissionAttempts() {
        return transmissionAttempts;
    }

    public void incrementAttempts() {
        this.transmissionAttempts++;
    }

    @Override
    public String toString() {
        return String.format("Frame[Seq: %d, Status: %s, Attempts: %d, Payload: '%s']",
                sequenceNumber, status, transmissionAttempts, payload);
    }
}
