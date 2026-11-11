package logging;

/**
 * Audit log entry capturing network routing, transmission, and security events.
 */
public class NetworkLogEntry {

    private final long logId;
    private final String sourceNode;
    private final String destinationNode;
    private final String protocol; // "TCP", "AES-GCM", "DIJKSTRA", "GBN", "CRC", "DATABASE"
    private final String eventDescription;
    private final long timestamp;

    public NetworkLogEntry(long logId, String sourceNode, String destinationNode, String protocol, String eventDescription, long timestamp) {
        this.logId = logId;
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
        this.protocol = protocol;
        this.eventDescription = eventDescription;
        this.timestamp = timestamp;
    }

    public long getLogId() {
        return logId;
    }

    public String getSourceNode() {
        return sourceNode;
    }

    public String getDestinationNode() {
        return destinationNode;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s (%s): %s",
                new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date(timestamp)),
                sourceNode, destinationNode, protocol, eventDescription);
    }
}
