package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Academic Project Presentation, Objectives, and Viva Guide Panel.
 */
public class AboutPanel extends JPanel {

    public AboutPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        JPanel card = UITheme.createCardPanel("ACADEMIC PROJECT OVERVIEW & VIVA QUICK REFERENCE");
        JTextArea txtAbout = UITheme.createConsoleArea(20, 30);
        txtAbout.setText("""
            ========================================================================================
             PROJECT: SECURE AND RELIABLE BANKING NETWORK USING ROUTING AND ENCRYPTION
            ========================================================================================
            
            1. PROJECT ABSTRACT & PURPOSE:
               This project provides an integrated, fully functional, multi-tiered banking network
               system designed to demonstrate fundamental Computer Networks and Cybersecurity principles
               in an enterprise context. It models an end-to-end banking topology where ATM clients,
               branch workstations, branch routers, head office core routers, and database servers
               communicate securely and reliably.
            
            2. CORE COMPUTER NETWORKS CONCEPTS DEMONSTRATED:
               [x] IPv4 Addressing, Octet Math, and RFC 1918 Private Ranges.
               [x] Subnetting, CIDR Conversion, Subnet Masks, Network/Broadcast IDs, Usable Hosts.
               [x] Dijkstra's Shortest Path Algorithm (Link-State Routing with Priority Queue).
               [x] Distance Vector Routing (Bellman-Ford Equation, Iterative Convergence).
               [x] Cryptographic Authenticated Encryption (AES-256-GCM, Nonces, AEAD Tag Verification).
               [x] True Streaming TCP Sockets (ServerSocket, Socket, 4-Byte Length-Prefixed Framing).
               [x] Cyclic Redundancy Check (CRC Modulo-2 Binary Division, FCS generation, Bit Error Detection).
               [x] Reliable Data Transfer: Sliding Window & Go-Back-N ARQ Protocol Simulation.
               [x] Real-world Wire packet analysis via Wireshark on TCP port 9090.
               [x] ACID Relational Ledger Database (SQLite, PreparedStatements, Explicit Rollback/Commit).
            
            3. KEY VIVA QUESTIONS & ANSWERS:
               Q1: Why does this project use AES-GCM rather than AES-CBC?
                   A: AES-CBC only provides confidentiality and is vulnerable to padding oracle attacks.
                      AES-GCM provides Authenticated Encryption with Associated Data (AEAD), guaranteeing
                      both confidentiality and integrity via a 128-bit GHASH authentication tag.
            
               Q2: How does the application solve TCP packet fragmentation and concatenation?
                   A: Streaming TCP is a byte stream without message boundaries. The application implements
                      4-Byte Big-Endian Length-Prefixed Framing (MessageProtocol.java), ensuring receivers
                      read the exact length before deserializing.
            
               Q3: What is the mathematical difference between Dijkstra and Bellman-Ford?
                   A: Dijkstra requires complete global network topology knowledge and runs in O((V+E)log V).
                      Bellman-Ford iteratively solves D_x(y) = min_v { c(x,v) + D_v(y) } by exchanging vectors
                      strictly with immediate neighbors, converging in O(V * E).
            
               Q4: Does CRC detect malicious tampering?
                   A: No. CRC is designed purely for accidental physical bit corruption (thermal noise).
                      An attacker can alter plaintext and recalculate matching CRC remainder.
                      Cryptographic AEAD tags (AES-GCM) are required to detect intentional tampering.
            
            4. SYSTEM ARCHITECTURE & ENVIRONMENT:
               - Language       : Java (JDK 17 / 21 / 26 compatible)
               - GUI Framework  : Java Swing (Modern Enterprise Dark Navy Theme)
               - Transport      : java.net.ServerSocket & java.net.Socket
               - Database       : SQLite (via JDBC driver sqlite-jdbc-3.45.2.0.jar)
               - Cryptography   : Java Cryptography Architecture (SunJCE AES-256-GCM)
            ========================================================================================
            """);
        card.add(new JScrollPane(txtAbout), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);
    }
}
