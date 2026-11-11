package main;

import ui.Dashboard;

import javax.swing.*;

/**
 * Main application launcher for the Secure and Reliable Banking Network project.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println(" SECURE AND RELIABLE BANKING NETWORK USING ROUTING AND ENCRYPTION       ");
        System.out.println(" Senior Architect & Networking Capstone Implementation                  ");
        System.out.println("========================================================================");
        System.out.println("Initializing Banking Subsystems, Cryptography Engine, and TCP Server...");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}

            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
            System.out.println("Dashboard GUI launched successfully. Listening on TCP port 9090.");
        });
    }
}
