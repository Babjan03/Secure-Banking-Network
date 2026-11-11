package database;

import banking.Customer;
import banking.TransactionRecord;
import logging.NetworkLogEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object executing ACID transactional updates, queries,
 * and audit logging on the SQLite database.
 */
public class BankingRepository {

    public static class TransactionResult {
        public final boolean success;
        public final String responseCode;
        public final String message;
        public final double newBalance;
        public final String transactionId;

        public TransactionResult(boolean success, String responseCode, String message, double newBalance, String transactionId) {
            this.success = success;
            this.responseCode = responseCode;
            this.message = message;
            this.newBalance = newBalance;
            this.transactionId = transactionId;
        }
    }

    private final DatabaseConnection dbConn;

    public BankingRepository(DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    /**
     * Retrieves account balance for a given account number.
     */
    public Double getBalance(String accountNumber) throws SQLException {
        String sql = "SELECT balance FROM Customers WHERE account_number = ?";
        try (Connection conn = dbConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        }
        return null;
    }

    /**
     * Executes an atomic withdrawal inside an explicit database transaction.
     */
    public TransactionResult processWithdrawal(String accountNumber, double amount, String sourceNode) {
        if (amount <= 0) {
            return new TransactionResult(false, "ERR_INVALID_AMOUNT", "Withdrawal amount must be strictly positive", 0, null);
        }

        String txId = "TX-W-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        long now = System.currentTimeMillis();

        try (Connection conn = dbConn.getConnection()) {
            conn.setAutoCommit(false); // Begin ACID Transaction

            try {
                // 1. Fetch current balance
                String selectSql = "SELECT balance FROM Customers WHERE account_number = ?";
                double currentBalance;
                try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                    pstmt.setString(1, accountNumber);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            recordFailedTx(conn, txId, accountNumber, "WITHDRAW", amount, "ACCOUNT_NOT_FOUND");
                            return new TransactionResult(false, "ERR_NO_ACCOUNT", "Account does not exist: " + accountNumber, 0, txId);
                        }
                        currentBalance = rs.getDouble("balance");
                    }
                }

                // 2. Validate sufficient funds
                if (currentBalance < amount) {
                    recordFailedTx(conn, txId, accountNumber, "WITHDRAW", amount, "INSUFFICIENT_FUNDS");
                    conn.commit();
                    return new TransactionResult(false, "ERR_INSUFFICIENT_FUNDS",
                            String.format("Insufficient funds! Available: ₹%.2f, Requested: ₹%.2f", currentBalance, amount),
                            currentBalance, txId);
                }

                // 3. Deduct balance
                double newBalance = currentBalance - amount;
                String updateSql = "UPDATE Customers SET balance = ? WHERE account_number = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setDouble(1, newBalance);
                    pstmt.setString(2, accountNumber);
                    pstmt.executeUpdate();
                }

                // 4. Record transaction ledger entry
                String insertTxSql = """
                    INSERT INTO Transactions (transaction_id, account_number, transaction_type, amount, transaction_timestamp, status)
                    VALUES (?, ?, 'WITHDRAW', ?, ?, 'SUCCESS');
                """;
                try (PreparedStatement pstmt = conn.prepareStatement(insertTxSql)) {
                    pstmt.setString(1, txId);
                    pstmt.setString(2, accountNumber);
                    pstmt.setDouble(3, amount);
                    pstmt.setLong(4, now);
                    pstmt.executeUpdate();
                }

                conn.commit(); // Commit Transaction

                // Log network audit
                logNetworkEvent(sourceNode, "DB-HQ", "DATABASE",
                        String.format("Withdrawal of ₹%.2f approved for %s. New Balance: ₹%.2f", amount, accountNumber, newBalance));

                return new TransactionResult(true, "OK_200", "Withdrawal successful", newBalance, txId);

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return new TransactionResult(false, "ERR_DB_EXCEPTION", "Database error: " + e.getMessage(), 0, txId);
        }
    }

    /**
     * Executes an atomic deposit inside an explicit database transaction.
     */
    public TransactionResult processDeposit(String accountNumber, double amount, String sourceNode) {
        if (amount <= 0) {
            return new TransactionResult(false, "ERR_INVALID_AMOUNT", "Deposit amount must be positive", 0, null);
        }

        String txId = "TX-D-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        long now = System.currentTimeMillis();

        try (Connection conn = dbConn.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Fetch current balance
                String selectSql = "SELECT balance FROM Customers WHERE account_number = ?";
                double currentBalance;
                try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                    pstmt.setString(1, accountNumber);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return new TransactionResult(false, "ERR_NO_ACCOUNT", "Account does not exist: " + accountNumber, 0, txId);
                        }
                        currentBalance = rs.getDouble("balance");
                    }
                }

                double newBalance = currentBalance + amount;
                String updateSql = "UPDATE Customers SET balance = ? WHERE account_number = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setDouble(1, newBalance);
                    pstmt.setString(2, accountNumber);
                    pstmt.executeUpdate();
                }

                String insertTxSql = """
                    INSERT INTO Transactions (transaction_id, account_number, transaction_type, amount, transaction_timestamp, status)
                    VALUES (?, ?, 'DEPOSIT', ?, ?, 'SUCCESS');
                """;
                try (PreparedStatement pstmt = conn.prepareStatement(insertTxSql)) {
                    pstmt.setString(1, txId);
                    pstmt.setString(2, accountNumber);
                    pstmt.setDouble(3, amount);
                    pstmt.setLong(4, now);
                    pstmt.executeUpdate();
                }

                conn.commit();

                logNetworkEvent(sourceNode, "DB-HQ", "DATABASE",
                        String.format("Deposit of ₹%.2f credited to %s. New Balance: ₹%.2f", amount, accountNumber, newBalance));

                return new TransactionResult(true, "OK_200", "Deposit successful", newBalance, txId);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return new TransactionResult(false, "ERR_DB_EXCEPTION", "Database error: " + e.getMessage(), 0, txId);
        }
    }

    private void recordFailedTx(Connection conn, String txId, String acc, String type, double amount, String reason) {
        String sql = """
            INSERT INTO Transactions (transaction_id, account_number, transaction_type, amount, transaction_timestamp, status)
            VALUES (?, ?, ?, ?, ?, ?);
        """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, txId);
            pstmt.setString(2, acc);
            pstmt.setString(3, type);
            pstmt.setDouble(4, amount);
            pstmt.setLong(5, System.currentTimeMillis());
            pstmt.setString(6, "FAILED_" + reason);
            pstmt.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customers ORDER BY name ASC";
        try (Connection conn = dbConn.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Customer(
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getString("account_number"),
                        rs.getString("account_type"),
                        rs.getDouble("balance")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<TransactionRecord> getRecentTransactions(int limit) {
        List<TransactionRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM Transactions ORDER BY transaction_timestamp DESC LIMIT " + limit;
        try (Connection conn = dbConn.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new TransactionRecord(
                        rs.getString("transaction_id"),
                        rs.getString("account_number"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getLong("transaction_timestamp"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void logNetworkEvent(String src, String dst, String proto, String desc) {
        String sql = """
            INSERT INTO NetworkLogs (source_node, destination_node, protocol, event_description, timestamp)
            VALUES (?, ?, ?, ?, ?);
        """;
        try (Connection conn = dbConn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, src);
            pstmt.setString(2, dst);
            pstmt.setString(3, proto);
            pstmt.setString(4, desc);
            pstmt.setLong(5, System.currentTimeMillis());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<NetworkLogEntry> getRecentLogs(int limit) {
        List<NetworkLogEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM NetworkLogs ORDER BY log_id DESC LIMIT " + limit;
        try (Connection conn = dbConn.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new NetworkLogEntry(
                        rs.getLong("log_id"),
                        rs.getString("source_node"),
                        rs.getString("destination_node"),
                        rs.getString("protocol"),
                        rs.getString("event_description"),
                        rs.getLong("timestamp")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
