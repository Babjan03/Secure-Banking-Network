# Secure and Reliable Banking Network Using Routing and Encryption

An Enterprise-grade Academic Computer Networks & Cybersecurity Capstone Application developed in Java.

---

## 🌟 Executive Summary
This project provides a fully integrated, multi-tiered banking network simulation demonstrating how a financial transaction originates at a retail ATM, resolves its network subnet and routing path, is secured using authenticated symmetric cryptography (AES-256-GCM), traverses streaming TCP sockets using custom length-prefixed framing, validates against an ACID relational database, and is verified at the link/transport layers using CRC and Go-Back-N ARQ.

---

## 📋 Key Modules Implemented

1. **IPv4 Addressing & Subnetting**: Bitwise 32-bit math, CIDR prefix conversions (`/8` to `/31`), Network ID, Broadcast ID, Usable Host range, and Subnet Membership check.
2. **Dijkstra Shortest Path Routing**: Link-state graph routing with priority queue, hop-by-hop link costs, and real-time link failure failover rerouting.
3. **Distance Vector Routing (Bellman-Ford)**: Distributed table updates ($D_x(y) = \min_v \{ c(x,v) + D_v(y) \}$), iterative vector exchange, and convergence tracking.
4. **AES-256-GCM Cryptographic Security**: Authenticated Encryption with Associated Data (AEAD), NIST-compliant 96-bit random nonce generation per transaction, and tamper defense (`AEADBadTagException`).
5. **Real TCP Client-Server Sockets**: Java `ServerSocket` and `Socket` communicating on port 9090 with 4-byte Big-Endian Length-Prefixed framing to prevent TCP segmentation fragmentation.
6. **Cyclic Redundancy Check (CRC)**: Modulo-2 binary polynomial division, Frame Check Sequence (FCS) remainder generation, and bit corruption detection.
7. **Sliding Window & Go-Back-N ARQ**: Configurable window size $N$, out-of-order frame discard, cumulative acknowledgements, and window-wide timer rewind retransmission.
8. **SQLite ACID Database**: Embedded transactional ledger with explicit `commit()` and `rollback()` handling withdrawals, deposits, and audit logging.
9. **Wireshark Packet Analysis Guide**: Dedicated workflow for capturing authorized TCP packets (`tcp.port == 9090`), inspecting 3-way handshake (`SYN`, `SYN-ACK`, `ACK`), and verifying payload confidentiality.
10. **Modern Desktop Dashboard**: Dark Navy enterprise Java Swing dashboard featuring 13 interactive tabs and real-time visualization canvases.

---

## 🚀 Quick Start Guide

### Prerequisites
- **Operating System**: Windows 10/11, macOS, or Linux
- **Java Development Kit**: JDK 17, JDK 21, or newer (Tested on JDK 26)

### One-Click Execution (Windows)
Double-click `run.bat` in this folder, or execute via terminal:

```cmd
cd c:\AntiGravity\SecureBankingNetwork
run.bat
```

### Manual Compilation & Execution
```cmd
cd c:\AntiGravity\SecureBankingNetwork

REM 1. Compile all Java modules
javac -encoding UTF-8 -cp "lib/*" -d bin src\network\*.java src\routing\*.java src\security\*.java src\communication\*.java src\error\*.java src\reliability\*.java src\banking\*.java src\database\*.java src\logging\*.java src\ui\*.java src\main\*.java

REM 2. Run Automated Integration Test Suite
java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" main.MasterIntegrationTest

REM 3. Launch Graphical Dashboard
java --enable-native-access=ALL-UNNAMED -cp "bin;lib/*" main.Main
```

---

## 🏗️ Project Architecture Layout
```
SecureBankingNetwork/
├── bin/                          # Compiled bytecode .class files
├── lib/                          # Bundled SQLite JDBC & SLF4J libraries
│   ├── sqlite-jdbc-3.45.2.0.jar
│   ├── slf4j-api-2.0.12.jar
│   └── slf4j-simple-2.0.12.jar
├── src/
│   ├── main/
│   │   ├── Main.java             # Desktop Application Entry Point
│   │   └── MasterIntegrationTest.java # 100% Automated Test Suite
│   ├── network/                  # IPv4, Subnetting, Nodes, Topology Graph
│   ├── routing/                  # Dijkstra & Distance Vector Algorithms
│   ├── security/                 # AES-256-GCM AEAD Cryptography Engine
│   ├── communication/            # Multi-Threaded TCP Server & ATM Client
│   ├── error/                    # Modulo-2 CRC Polynomial Binary Division
│   ├── reliability/              # Sliding Window & Go-Back-N ARQ Simulation
│   ├── banking/                  # Customer, Transaction Domain Models & Facade
│   ├── database/                 # SQLite JDBC Repository & Schema Initializer
│   ├── logging/                  # Network Audit Logger
│   └── ui/                       # Modern Dark Navy Java Swing Dashboard
├── compile.bat                   # Compilation script
├── run.bat                       # Launch script
├── PROJECT_DOCUMENTATION.md      # Comprehensive 28-Section Academic Report
└── README.md                     # Quick overview and guide
```

---

## 🎓 Academic Viva Reference
- **Why AES-GCM over AES-CBC?** AES-GCM guarantees both **confidentiality** and **authenticity** (AEAD) with an authentication tag, preventing active bit-flipping attacks.
- **Why Length-Prefixed Framing over TCP?** TCP is an unstructured stream of bytes without message boundaries. The 4-byte header guarantees atomic receipt of full frames.
- **Can CRC protect against hackers?** No. CRC only detects accidental physical noise. Malicious tampering requires cryptographic MAC/AEAD tags.
