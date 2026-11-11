package communication;

import java.util.UUID;

/**
 * Structured banking message exchanged between ATM client and central banking server.
 */
public class TransactionMessage {

    public enum Type {
        WITHDRAW,
        DEPOSIT,
        BALANCE_INQUIRY
    }

    private final String requestId;
    private final String sourceNodeId;
    private final String accountNumber;
    private final Type type;
    private final double amount;
    private final long timestamp;

    public TransactionMessage(String sourceNodeId, String accountNumber, Type type, double amount) {
        this(UUID.randomUUID().toString().substring(0, 8), sourceNodeId, accountNumber, type, amount, System.currentTimeMillis());
    }

    public TransactionMessage(String requestId, String sourceNodeId, String accountNumber, Type type, double amount, long timestamp) {
        this.requestId = requestId;
        this.sourceNodeId = sourceNodeId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getSourceNodeId() {
        return sourceNodeId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Serializes to a standard delimited string format:
     * "REQ_ID|SOURCE|ACCOUNT|TYPE|AMOUNT|TIMESTAMP"
     */
    public String serialize() {
        return String.format("%s|%s|%s|%s|%.2f|%d",
                requestId, sourceNodeId, accountNumber, type.name(), amount, timestamp);
    }

    public static TransactionMessage deserialize(String data) {
        String[] parts = data.split("\\|");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid transaction message format: " + data);
        }
        return new TransactionMessage(
                parts[0],
                parts[1],
                parts[2],
                Type.valueOf(parts[3]),
                Double.parseDouble(parts[4]),
                Long.parseLong(parts[5])
        );
    }

    @Override
    public String toString() {
        return String.format("TxReq[%s] %s on %s: ₹%.2f (From: %s)",
                requestId, type, accountNumber, amount, sourceNodeId);
    }
}
