package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Automatically initializes SQLite database schema and seeds initial fictional accounts.
 */
public class DatabaseInitializer {

    private final DatabaseConnection dbConn;

    public DatabaseInitializer(DatabaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    public void initializeDatabase() throws SQLException {
        try (Connection conn = dbConn.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Customers Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Customers (
                    customer_id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    account_number TEXT UNIQUE NOT NULL,
                    account_type TEXT NOT NULL,
                    balance REAL NOT NULL CHECK(balance >= 0)
                );
            """);

            // 2. Transactions Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Transactions (
                    transaction_id TEXT PRIMARY KEY,
                    account_number TEXT NOT NULL,
                    transaction_type TEXT NOT NULL,
                    amount REAL NOT NULL CHECK(amount > 0),
                    transaction_timestamp INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    FOREIGN KEY (account_number) REFERENCES Customers(account_number)
                );
            """);

            // 3. NetworkLogs Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS NetworkLogs (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    source_node TEXT NOT NULL,
                    destination_node TEXT NOT NULL,
                    protocol TEXT NOT NULL,
                    event_description TEXT NOT NULL,
                    timestamp INTEGER NOT NULL
                );
            """);

            seedInitialData(conn);
        }
    }

    private void seedInitialData(Connection conn) throws SQLException {
        // Check if customers already exist
        try (Statement checkStmt = conn.createStatement();
             var rs = checkStmt.executeQuery("SELECT COUNT(*) FROM Customers")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Already seeded
            }
        }

        String insertCustomerSql = """
            INSERT INTO Customers (customer_id, name, account_number, account_type, balance)
            VALUES (?, ?, ?, ?, ?);
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(insertCustomerSql)) {
            // Customer 1: Alice Sharma
            pstmt.setString(1, "CUST-001");
            pstmt.setString(2, "Alice Sharma");
            pstmt.setString(3, "ACC-100234");
            pstmt.setString(4, "SAVINGS");
            pstmt.setDouble(5, 50000.00);
            pstmt.executeUpdate();

            // Customer 2: Rajesh Patel
            pstmt.setString(1, "CUST-002");
            pstmt.setString(2, "Rajesh Patel");
            pstmt.setString(3, "ACC-500891");
            pstmt.setString(4, "CURRENT");
            pstmt.setDouble(5, 125000.00);
            pstmt.executeUpdate();

            // Customer 3: Priya Nair
            pstmt.setString(1, "CUST-003");
            pstmt.setString(2, "Priya Nair");
            pstmt.setString(3, "ACC-300452");
            pstmt.setString(4, "SAVINGS");
            pstmt.setDouble(5, 8500.00);
            pstmt.executeUpdate();

            // Customer 4: Vikram Singh
            pstmt.setString(1, "CUST-004");
            pstmt.setString(2, "Vikram Singh");
            pstmt.setString(3, "ACC-700912");
            pstmt.setString(4, "SAVINGS");
            pstmt.setDouble(5, 34000.00);
            pstmt.executeUpdate();
        }
    }
}
