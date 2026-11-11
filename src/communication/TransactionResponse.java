package communication;

/**
 * Standard structured response sent from Banking Server back to ATM Client.
 */
public class TransactionResponse {

    private final String requestId;
    private final boolean success;
    private final String responseCode;
    private final String message;
    private final double remainingBalance;
    private final long processedTimestamp;

    public TransactionResponse(String requestId, boolean success, String responseCode, String message, double remainingBalance) {
        this.requestId = requestId;
        this.success = success;
        this.responseCode = responseCode;
        this.message = message;
        this.remainingBalance = remainingBalance;
        this.processedTimestamp = System.currentTimeMillis();
    }

    public String getRequestId() {
        return requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getMessage() {
        return message;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public long getProcessedTimestamp() {
        return processedTimestamp;
    }

    public String serialize() {
        return String.format("%s|%b|%s|%s|%.2f|%d",
                requestId, success, responseCode, message.replace("|", "/"), remainingBalance, processedTimestamp);
    }

    public static TransactionResponse deserialize(String data) {
        String[] parts = data.split("\\|");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid response format: " + data);
        }
        return new TransactionResponse(
                parts[0],
                Boolean.parseBoolean(parts[1]),
                parts[2],
                parts[3],
                Double.parseDouble(parts[4])
        );
    }

    @Override
    public String toString() {
        return String.format("TxResp[%s] Status: %s (%s) - %s | Balance: ₹%.2f",
                requestId, success ? "SUCCESS" : "FAILED", responseCode, message, remainingBalance);
    }
}
