package banking;

/**
 * Historical transaction ledger record.
 */
public class TransactionRecord {

    private final String transactionId;
    private final String accountNumber;
    private final String transactionType;
    private final double amount;
    private final long timestamp;
    private final String status; // "SUCCESS", "FAILED_INSUFFICIENT_FUNDS", "FAILED_INVALID_AMOUNT"

    public TransactionRecord(String transactionId, String accountNumber, String transactionType, double amount, long timestamp, String status) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("TxRecord[%s] Acc: %s | %s ₹%.2f | Status: %s",
                transactionId, accountNumber, transactionType, amount, status);
    }
}
