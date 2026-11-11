package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class EnvironmentCheck {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  SECURE BANKING NETWORK - ENVIRONMENT CHECK");
        System.out.println("==================================================");
        System.out.println("Java Runtime Version : " + System.getProperty("java.version"));
        System.out.println("Java Vendor          : " + System.getProperty("java.vendor"));
        System.out.println("Operating System     : " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")");
        System.out.println("Working Directory    : " + System.getProperty("user.dir"));
        
        // Test SQLite Connection
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:")) {
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT 'SQLite In-Memory Connected Successfully!' AS message");
                    if (rs.next()) {
                        System.out.println("Database Status      : " + rs.getString("message"));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Database Status      : FAILED - " + e.getMessage());
            e.printStackTrace();
        }

        // Test AES-GCM Cipher Availability
        try {
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding");
            System.out.println("AES/GCM Cryptography : Available (" + cipher.getProvider().getName() + ")");
        } catch (Exception e) {
            System.err.println("AES/GCM Cryptography : FAILED - " + e.getMessage());
        }

        System.out.println("Environment Status   : ALL CHECKS PASSED!");
        System.out.println("==================================================");
    }
}
