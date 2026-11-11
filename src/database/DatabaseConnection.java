package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages JDBC connections to the embedded SQLite banking database.
 */
public class DatabaseConnection {

    private static final String DEFAULT_DB_URL = "jdbc:sqlite:banking_network.db";
    private final String dbUrl;

    public DatabaseConnection() {
        this(DEFAULT_DB_URL);
    }

    public DatabaseConnection(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found in classpath", e);
        }
        return DriverManager.getConnection(dbUrl);
    }

    public String getDbUrl() {
        return dbUrl;
    }
}
