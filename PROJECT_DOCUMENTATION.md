# SECURE AND RELIABLE BANKING NETWORK USING ROUTING AND ENCRYPTION
## Academic Project Report & Comprehensive Technical Specification

---

## 1. TITLE PAGE

- **Project Title**: Secure and Reliable Banking Network Using Routing and Encryption
- **Domain**: Computer Networks, Cryptography & Network Security, Distributed Systems
- **Target Degree**: Bachelor of Technology (B.Tech) / Bachelor of Engineering (B.E.) in Computer Science & Engineering / Information Technology
- **Implementation Language**: Java (JDK 17 / 21 / 26)
- **Primary Frameworks**: Java Swing, Java Socket API, Java Cryptography Architecture (JCA), SQLite JDBC
- **Analysis Tools**: Wireshark Packet Analyzer

---

## 2. ABSTRACT

In modern financial ecosystems, commercial banking transactions demand absolute confidentiality, end-to-end data integrity, resilient packet routing, and deterministic fault tolerance across wide area networks (WANs). 

This project, titled **"Secure and Reliable Banking Network Using Routing and Encryption"**, develops an integrated, multi-tiered, professional Java application simulating an enterprise banking communication infrastructure. It interconnects retail automated teller machines (ATMs), branch customer service computers, branch manager terminals, branch routers, central head office routers, application servers, and core database ledger servers.

The application systematically implements and demonstrates ten foundational Computer Networks and Cybersecurity principles:
1. **IPv4 Addressing & Binary Octet Calculations**
2. **Subnetting, CIDR Conversion & Broadcast Domain Verification**
3. **Dijkstra's Shortest Path Routing (Link-State Protocol)**
4. **Distance Vector Routing with Bellman-Ford Convergence**
5. **Authenticated Symmetric Encryption (AES-256-GCM with Fresh Nonces)**
6. **Streaming Multi-Threaded TCP Client-Server Sockets with Length-Prefixed Framing**
7. **Cyclic Redundancy Check (CRC Modulo-2 Polynomial Binary Division)**
8. **Sliding Window Flow Control & Go-Back-N ARQ Protocol Simulation**
9. **Live Wireshark Packet Sniffing & Three-Way Handshake Inspection**
10. **ACID Transactional Ledger Persistence with SQLite**

The system provides a high-performance, dark-themed Java Swing desktop dashboard uniting all ten modules into an educational, interactive control center suitable for final-year engineering evaluations, laboratory demonstrations, and technical viva presentations.

---

## 3. INTRODUCTION

Commercial banking operations rely on distributed telecommunication networks where financial messages travel across untrusted public and private telecommunication trunks. When an individual initiates a cash withdrawal at a remote ATM, the withdrawal request must traverse multiple networking layers:
- At the **Data Link Layer (Layer 2)**, frames must be verified for accidental physical corruption using polynomial division (CRC).
- At the **Network Layer (Layer 3)**, packets must be addressed using standardized IPv4 subnets and routed dynamically across autonomous routers via Link-State (Dijkstra) or Distance Vector (Bellman-Ford) routing protocols to bypass severed fiber trunks.
- At the **Transport Layer (Layer 4)**, reliable byte streams must be established using TCP three-way handshakes (`SYN`, `SYN-ACK`, `ACK`), while framing mechanisms prevent segment fragmentation.
- At the **Application Layer (Layer 7)**, financial payloads must be shielded against eavesdropping and active man-in-the-middle (MITM) tampering via authenticated symmetric ciphers (AES-GCM).

This project bridges theoretical computer networking textbooks with production software engineering by building an integrated simulation in pure Java.

---

## 4. PROBLEM STATEMENT

Conventional academic demonstrations of Computer Networks often suffer from significant deficiencies:
1. **Disconnected Code Snippets**: Routing algorithms (Dijkstra/Bellman-Ford) are taught as isolated array manipulations without being integrated into real socket communications.
2. **Insecure Transport Demonstrations**: Socket programming exercises typically transmit plaintext strings (`Socket.write("withdraw 2000")`), misleading students on real-world banking security requirements.
3. **TCP Framing Neglect**: Standard student projects assume TCP operates as a message-based protocol, ignoring streaming fragmentation and buffer splitting.
4. **Superficial Error Detection**: CRC is presented merely as a formula on paper rather than demonstrating why CRC cannot defend against malicious cryptographic tampering.

This project resolves these pedagogical gaps by delivering a unified, fully working enterprise system where every single layer interacts realistically.

---

## 5. PROJECT OBJECTIVES

1. Design and construct a 10-node banking topology linking branch offices, routers, and central headquarters.
2. Implement 32-bit binary IPv4 validation, CIDR mask conversions, and same-subnet communication verification.
3. Implement Dijkstra's shortest path algorithm using adjacency lists and priority queues with live link failover rerouting.
4. Implement Distance Vector routing using the Bellman-Ford equation with iterative vector exchange and convergence tracking.
5. Secure all financial transactions using 256-bit AES in Galois/Counter Mode (GCM) with 96-bit unique nonces.
6. Build a multi-threaded TCP client-server subsystem enforcing 4-byte Big-Endian Length-Prefixed message framing.
7. Implement Modulo-2 binary polynomial CRC division to illustrate accidental bit corruption detection.
8. Simulate Sliding Window flow control and Go-Back-N ARQ with frame loss, duplicate ACKs, and timer timeouts.
9. Provide an integrated guide for packet capture analysis in Wireshark (`tcp.port == 9090`).
10. Store fictional customer balances and transaction history in an embedded ACID SQLite database with rollback capabilities.
11. Package all modules into an intuitive, polished Java Swing desktop dashboard.

---

## 6. EXISTING SYSTEM VS. PROPOSED SYSTEM

| Parameter | Existing Academic Projects | Proposed Banking Network System |
| :--- | :--- | :--- |
| **Architecture** | Scattered standalone scripts | Unified multi-tiered enterprise architecture |
| **Routing** | Hardcoded path strings | True Dijkstra (PriorityQueue) & Bellman-Ford |
| **Failover** | Static; crashes on link failure | Automatic dynamic path recalculation |
| **Cryptography** | Plaintext or vulnerable Caesar/DES | NIST-grade AES-256-GCM with fresh nonces |
| **Transport** | Unframed raw sockets | Length-prefixed framed TCP sockets |
| **Database** | Volatile in-memory maps | ACID relational SQLite with rollback safety |
| **Error Control** | Theoretical formulas | Modulo-2 division + GBN Sliding Window |
| **Packet Inspection** | Fabricated text logs | Genuine TCP packets inspectable in Wireshark |
| **User Interface** | Primitive CLI console | Polished Dark-Navy Java Swing dashboard |

---

## 7. SYSTEM REQUIREMENTS

### Hardware Requirements
- **Processor**: Intel Core i3 / AMD Ryzen 3 or higher (Dual-Core 2.0 GHz minimum)
- **RAM**: 4 GB minimum (8 GB recommended)
- **Storage**: 200 MB available disk space
- **Display**: Minimum screen resolution 1280 x 800 pixels

### Software Requirements
- **Operating System**: Windows 10/11, macOS 11+, or Linux (Ubuntu 20.04+)
- **Runtime Environment**: Java Development Kit (JDK 17, JDK 21, or JDK 26)
- **Database Engine**: SQLite 3 (embedded via JDBC)
- **Packet Analyzer**: Wireshark 4.0+ (optional for live packet inspection)
- **IDE**: IntelliJ IDEA, Eclipse, NetBeans, or VS Code

---

## 8. SYSTEM ARCHITECTURE

```mermaid
flowchart TD
    subgraph Branch_Office_A ["Branch Office A (Subnet 192.168.1.0/24 & .2.0/24)"]
        ATM1["ATM Terminal 1\n192.168.1.10"]
        ATM2["ATM Terminal 2\n192.168.1.20"]
        CUST["Customer Service\n192.168.2.10"]
        RTR1["Branch Router 1\n(RTR-BR1)\n192.168.1.1 / 10.0.1.1"]
        ATM1 -->|Cost: 1| RTR1
        ATM2 -->|Cost: 1| RTR1
        CUST -->|Cost: 1| RTR1
    end

    subgraph Branch_Office_B ["Branch Office B (Subnet 192.168.3.0/24 & .4.0/24)"]
        MGR["Manager System\n192.168.3.10"]
        SRV_BR["Branch Server\n192.168.4.10"]
        RTR2["Branch Router 2\n(RTR-BR2)\n192.168.3.1 / 10.0.2.1"]
        MGR -->|Cost: 1| RTR2
        SRV_BR -->|Cost: 1| RTR2
    end

    subgraph WAN_Backbone ["WAN Inter-Router Mesh"]
        RTR1 <-->|WAN Link Cost: 4| RTR2
        RTR1 <-->|Direct WAN Trunk Cost: 7| RTR_HQ["Head Office Core Router\n(RTR-HQ)\n192.168.5.1 / 10.0.3.1"]
        RTR2 <-->|Leased Fiber Line Cost: 2| RTR_HQ
    end

    subgraph Head_Office_DC ["Head Office Data Center (Subnet 192.168.5.0/24 & .6.0/24)"]
        SRV_HQ["Central Banking Server\n(SRV-HQ)\n192.168.5.10\n[TCP Port 9090]"]
        DB_HQ[("Ledger Database Server\n(DB-HQ)\n192.168.6.10\n[SQLite ACID]")]
        RTR_HQ -->|Cost: 1| SRV_HQ
        RTR_HQ -->|Cost: 1| DB_HQ
        SRV_HQ <-->|Internal Bus Cost: 1| DB_HQ
    end
```

---

## 9. DETAILED MODULE SPECIFICATIONS

### Module 1: IPv4 Addressing and Subnetting
- **Bitwise Logic**: IPv4 addresses are converted into 32-bit unsigned integers:
  $$\text{IP Value} = \sum_{i=0}^{3} \text{Octet}_i \times 2^{8 \times (3-i)}$$
- **Subnet Calculation**:
  $$\text{Subnet Mask} = \frac{2^{32} - 1}{2^{32 - \text{prefix}}} \times 2^{32 - \text{prefix}}$$
  $$\text{Network ID} = \text{IP} \text{ AND } \text{Mask}$$
  $$\text{Broadcast Address} = \text{Network ID} \text{ OR } (\text{NOT } \text{Mask})$$
  $$\text{Usable Hosts} = 2^{32 - \text{prefix}} - 2$$
- **Membership Test**: Two hosts belong to the same broadcast domain if and only if $(\text{IP}_1 \text{ AND } \text{Mask}) == (\text{IP}_2 \text{ AND } \text{Mask})$.

### Module 2: Dijkstra's Shortest Path Routing (Link-State)
- **Algorithm**: Link-State routing maintaining an adjacency list and min-priority queue:
  $$D(v) = \min \{ D(v), D(u) + c(u, v) \}$$
- **Optimal Routing Path**:
  $$\text{ATM-01} \xrightarrow{\text{cost 1}} \text{RTR-BR1} \xrightarrow{\text{cost 4}} \text{RTR-BR2} \xrightarrow{\text{cost 2}} \text{RTR-HQ} \xrightarrow{\text{cost 1}} \text{SRV-HQ} \quad (\text{Total Cost} = 8)$$
- **Dynamic Failover**: If link `L_RTR2_HQ` fails, Dijkstra instantly reroutes traffic via direct trunk `RTR-BR1 -> RTR-HQ` (Total Cost = 9).

### Module 3: Distance Vector Routing (Bellman-Ford)
- **Distributed Equation**:
  $$D_x(y) = \min_v \{ c(x, v) + D_v(y) \}$$
- Each router periodically exchanges its distance vector with direct neighbors. In the simulated topology, routing tables converge within **4 iterative rounds**.

### Module 4: AES-256-GCM Cryptographic Security
- **Algorithm**: AES in Galois/Counter Mode (`AES/GCM/NoPadding`) through SunJCE.
- **Nonce Freshness**: A cryptographically secure 96-bit (12-byte) initialization vector is generated for each transaction using `SecureRandom`.
- **Integrity**: Authenticated encryption generates a 128-bit GHASH authentication tag over the ciphertext. Altering 1 bit triggers `javax.crypto.AEADBadTagException`.

### Module 5: TCP Streaming Client-Server Communication
- **Socket APIs**: `java.net.ServerSocket` and `java.net.Socket`.
- **Length-Prefixed Framing**: Streaming TCP byte streams lack record demarcations. The protocol prepends a 4-byte Big-Endian length header:
  $$[\, \text{4-Byte Length Header} \,|\, \text{UTF-8 Encrypted Payload} \,]$$
  Receivers call `DataInputStream.readFully()` to assemble complete messages atomically.

### Module 6: Cyclic Redundancy Check (CRC Modulo-2)
- Implements Modulo-2 polynomial division where XOR replaces arithmetic subtraction without borrows.
- Augments $k$-bit data with $r$ zeros, divides by generator polynomial $G(x)$, and appends the $r$-bit remainder (Frame Check Sequence / FCS).
- Re-dividing the received frame by $G(x)$ produces zero remainder for clean frames, and non-zero remainder upon bit corruption.

### Module 7: Sliding Window & Go-Back-N ARQ Protocol
- Configurable window size $N$ with sequence numbers $F_0, F_1, F_2, \dots$
- Sender maintains window $[ \text{send\_base}, \text{send\_base} + N - 1 ]$.
- Receiver discards out-of-order frames and emits cumulative acknowledgements.
- On timeout, the sender rewinds `next_seq_num` to `send_base` and retransmits the entire window.

### Module 8: Wireshark Packet Inspection
- Instructs students to select the Loopback adapter and filter on `tcp.port == 9090`.
- Allows faculty to inspect actual TCP handshakes, sequence numbers, window sizing, and verify that payloads are completely encrypted.

### Module 9: SQLite ACID Database Design
- Tables: `Customers`, `Transactions`, and `NetworkLogs`.
- Atomic balance deductions execute inside explicit transactions (`conn.setAutoCommit(false)`), executing `commit()` on success and `rollback()` on insufficient funds.

---

## 10. END-TO-END TRANSACTION SEQUENCE DIAGRAM

```mermaid
sequenceDiagram
    autonumber
    actor Customer as Bank Customer
    participant ATM as ATM Client (ATM-01)
    participant Subnet as Subnet Module
    participant Router as Routing Engine (Dijkstra)
    participant Crypto as Security Engine (AES-GCM)
    participant Server as Banking Server (SRV-HQ:9090)
    participant DB as SQLite Ledger (DB-HQ)

    Customer->>ATM: Insert Card & Request Withdrawal (₹2,000)
    ATM->>Subnet: Verify Destination IP Subnet
    Subnet-->>ATM: Destination in External Subnet (192.168.5.0/24)
    ATM->>Router: Compute Shortest Path to SRV-HQ
    Router-->>ATM: Path: ATM-01 -> RTR-BR1 -> RTR-BR2 -> RTR-HQ -> SRV-HQ
    ATM->>Crypto: Encrypt Payload with AES-256-GCM + 96-bit Nonce
    Crypto-->>ATM: Return Encrypted Packet (IV + Ciphertext + Tag)
    ATM->>Server: Transmit Length-Prefixed TCP Segment (Port 9090)
    Server->>Crypto: Decrypt Packet & Verify 128-bit Auth Tag
    Crypto-->>Server: Authentication Successful (Plaintext Payload)
    Server->>DB: Begin ACID Transaction (SELECT Balance & UPDATE)
    alt Balance >= ₹2,000
        DB-->>Server: Balance Deducted (New Balance: ₹48,000)
        Server->>Crypto: Encrypt Transaction Approval Response
        Crypto-->>Server: Encrypted Response Packet
        Server->>ATM: Transmit TCP Framed Response
        ATM->>Customer: Dispense ₹2,000 Cash & Print Receipt
    else Balance < ₹2,000
        DB-->>Server: Insufficient Funds (Rollback Transaction)
        Server->>ATM: Transmit Encrypted Rejection Response
        ATM->>Customer: Display "Transaction Declined: Insufficient Funds"
    end
```

---

## 11. DATABASE ENTITY-RELATIONSHIP (ER) DIAGRAM

```mermaid
erDiagram
    CUSTOMERS ||--o{ TRANSACTIONS : "initiates"
    CUSTOMERS {
        string customer_id PK
        string name
        string account_number UK
        string account_type
        double balance
    }
    TRANSACTIONS {
        string transaction_id PK
        string account_number FK
        string transaction_type
        double amount
        long transaction_timestamp
        string status
    }
    NETWORK_LOGS {
        long log_id PK
        string source_node
        string destination_node
        string protocol
        string event_description
        long timestamp
    }
```

---

## 12. COMPREHENSIVE TEST RESULTS MATRIX

| Module | Test Case ID | Test Description | Input Data | Expected Output | Actual Output | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **IP Addressing** | TC-IP-01 | Dotted Decimal Parsing | `192.168.1.10` | 32-bit Integer & Binary Conversion | Exact 32-bit match | **PASS** |
| **IP Addressing** | TC-IP-02 | Format Syntax Validation | `256.1.1.1` | Reject as illegal IPv4 | Rejected | **PASS** |
| **Subnetting** | TC-SUB-01 | /24 Subnet Calculation | `192.168.1.10/24` | Net: `.1.0`, Bcast: `.1.255`, Hosts: 254 | Matches specification | **PASS** |
| **Subnetting** | TC-SUB-02 | Same Subnet Verification | `192.168.1.10` & `192.168.1.20` | Report SAME subnet | True (Same Subnet) | **PASS** |
| **Dijkstra** | TC-DIJK-01 | Optimal Path ATM to Server | `ATM-01` to `SRV-HQ` | Path via RTR-BR2, Total Cost = 8 | Cost 8 via RTR-BR2 | **PASS** |
| **Dijkstra** | TC-DIJK-02 | Dynamic Link Failover | Sever link `L_RTR2_HQ` | Reroute via RTR-HQ trunk, Cost = 9 | Cost 9 failover route | **PASS** |
| **Distance Vector**| TC-DV-01 | Bellman-Ford Convergence | 10-node mesh | Converges with 0 table changes | Converged in 4 rounds | **PASS** |
| **Cryptography** | TC-SEC-01 | AES-256-GCM Round-trip | JSON banking payload | Decrypted plaintext equals original | Exact match | **PASS** |
| **Cryptography** | TC-SEC-02 | MITM Ciphertext Tamper | Mutate 1 bit in ciphertext | Throw `AEADBadTagException` | Caught & Transaction Rejected | **PASS** |
| **TCP Sockets** | TC-TCP-01 | Length-Prefixed Streaming | Withdrawal over port 9090 | Complete packet received & processed | Approved, Balance ₹48,000 | **PASS** |
| **CRC** | TC-CRC-01 | Clean Frame Verification | Data: `1101011011`, Poly: `10011` | Remainder is 0; verification PASS | Remainder 0 (PASS) | **PASS** |
| **CRC** | TC-CRC-02 | Bit Error Detection | Mutate Bit 4 in frame | Non-zero remainder; verification FAIL | Remainder `1010` (Detected) | **PASS** |
| **Go-Back-N** | TC-GBN-01 | Frame Loss & Rewind | Drop Frame F2, Window N=4 | Discard F3-F5; timeout and retransmit | All 8 frames ACKed | **PASS** |
| **Database** | TC-DB-01 | Atomic Withdrawal | Withdraw ₹2,000 from Alice | Deduct balance to ₹48,000 | New Balance ₹48,000 | **PASS** |
| **Database** | TC-DB-02 | Insufficient Funds Rollback | Withdraw ₹100,000 from Alice | Reject transaction; balance unchanged | Rejected, Balance ₹48,000 | **PASS** |

---

## 13. SCREENSHOTS & DEMONSTRATION GUIDANCE

When demonstrating the project to your faculty panel, follow this sequential walkthrough:

1. **Dashboard Overview (Tab 1)**:
   - Highlight the 4 real-time metric cards: Total Network Devices (10), Active Routers (3/3), TCP Server (Online on 9090), and Ledger Transactions.
   - Show the live transaction ledger table dynamically populating.
2. **Network Topology Canvas (Tab 2)**:
   - Point out the color-coded nodes: Cyan ATMs, Amber Routers, Emerald Servers, and Purple Database.
   - Show the glowing green line tracing the active shortest path.
   - Click **"Toggle Link UP/DOWN"** on `L_RTR2_HQ` to show the line turn dashed red and immediately observe the shortest path reroute!
3. **IP & Subnetting Calculator (Tab 3)**:
   - Enter `192.168.1.10`, select `/24`, and click calculate. Point out the 32-bit binary representation and usable host count ($254$).
   - Test two devices in the same subnet vs different subnets.
4. **Dijkstra Routing (Tab 4)**:
   - Select Source `ATM-01` and Destination `SRV-HQ`.
   - Explain the hop-by-hop link costs and cumulative path cost ($8$).
5. **Distance Vector (Tab 5)**:
   - Click **"Reset"** and then **"Step Next Round"**.
   - Show the faculty how each iteration updates the routing table until Round 4 displays `[CONVERGED SUCCESSFULLY]`.
6. **AES-256-GCM Encryption (Tab 6)**:
   - Show the plaintext JSON transaction.
   - Click **"Encrypt"** and explain the NIST 96-bit random nonce.
   - Click **"Simulate MITM Bit-Flip Tamper Attack"** and show the glowing red `AEADBadTagException` banner!
7. **ATM Client & TCP Server (Tab 7)**:
   - Select `ATM-01`, Account `ACC-100234` (Alice Sharma), Amount `₹2000.00`, and click **"Transmit Encrypted Transaction over TCP"**.
   - Show the console trace displaying the 9-step end-to-end execution.
8. **CRC Error Detection (Tab 8)**:
   - Show the step-by-step Modulo-2 XOR division trace.
   - Click **"Inject Bit Error & Verify"** to demonstrate physical layer error detection.
9. **Sliding Window / Go-Back-N (Tab 9)**:
   - Set window size to 4, designate loss on Frame F2, and click **"Transmit Allowed Window"**.
   - Point out that F0 and F1 are green (ACKed), F2 is red (Lost), and F3 is amber (Discarded out of order).
   - Click **"Simulate ACK Timeout"** to show GBN rewinding and successfully retransmitting.
10. **Wireshark Guide (Tab 11)**:
    - Open Wireshark, apply filter `tcp.port == 9090`, and click **"Generate Live TCP Traffic for Wireshark Capture"**.
    - Show the faculty the real `SYN`, `SYN-ACK`, `ACK` packets and encrypted payload stream.

---

## 14. 25 ESSENTIAL VIVA QUESTIONS & ANSWERS

**Q1: What is the primary difference between Link-State and Distance Vector routing?**
> *Answer*: Link-State routing (e.g., Dijkstra/OSPF) requires every router to possess the complete, identical topology map of the network and independently computes the shortest path tree. Distance Vector routing (e.g., Bellman-Ford/RIP) relies on "routing by rumor," where routers only know direct neighbor link costs and iteratively exchange distance tables.

**Q2: Why did you choose AES-256-GCM instead of AES-CBC?**
> *Answer*: AES-CBC only provides confidentiality (encryption) and is vulnerable to padding oracle attacks. AES-GCM provides Authenticated Encryption with Associated Data (AEAD), combining counter mode encryption with a 128-bit GHASH authentication tag. It simultaneously guarantees confidentiality, integrity, and authenticity.

**Q3: What happens if an attacker modifies 1 bit of an AES-GCM ciphertext in transit?**
> *Answer*: During decryption, the receiver computes the GHASH authentication tag over the ciphertext and compares it with the embedded tag. Modifying even a single bit causes a tag mismatch, causing Java to throw `javax.crypto.AEADBadTagException` and immediately abort the transaction.

**Q4: Why must a fresh 96-bit Nonce/IV be generated for every transaction in AES-GCM?**
> *Answer*: Reusing the same IV with the same key in GCM mode destroys the cryptographic security of the cipher, allowing an attacker to recover the authentication key and forge valid messages.

**Q5: What is TCP packet fragmentation, and how does your project handle it?**
> *Answer*: TCP is a byte-stream protocol, not a message-oriented protocol. A 200-byte message might arrive in two separate 100-byte reads. We implemented Length-Prefixed Framing (`MessageProtocol.java`), where every packet is preceded by a 4-byte big-endian integer specifying payload size. The receiver reads this header and calls `readFully()` to wait for the complete message.

**Q6: What is the formula for calculating total usable hosts in a /24 subnet?**
> *Answer*: Total hosts $= 2^{(32 - 24)} - 2 = 2^8 - 2 = 256 - 2 = 254$. Two addresses are reserved: the Network Address (all host bits 0) and the Broadcast Address (all host bits 1).

**Q7: Can CRC detect malicious modifications by an attacker?**
> *Answer*: No. CRC is a linear error-detecting code designed solely for accidental bit flips caused by thermal noise or attenuation. An attacker who modifies the data can easily calculate and substitute a matching CRC remainder. Cryptographic protection requires cryptographic MACs or AEAD tags.

**Q8: Why does the Go-Back-N receiver discard out-of-order frames instead of buffering them?**
> *Answer*: Go-Back-N is designed for simple receivers with minimal buffer memory. It only accepts frames matching `expected_seq_num`. If a frame is lost, all subsequent frames are rejected with duplicate ACKs, forcing the sender's sliding window to retransmit all unacknowledged frames upon timeout. (Protocols that buffer out-of-order frames use Selective Repeat ARQ).

**Q9: What happens during a TCP three-way handshake?**
> *Answer*:
> 1. Client sends `SYN` (Synchronize) with an initial sequence number $x$.
> 2. Server replies with `SYN-ACK` with its own sequence number $y$ and acknowledgement $x+1$.
> 3. Client replies with `ACK` acknowledging sequence $y+1$.

**Q10: What is Bellman-Ford's equation?**
> *Answer*: $D_x(y) = \min_v \{ c(x, v) + D_v(y) \}$, where router $x$ minimizes its total estimated cost to destination $y$ across all immediate neighbors $v$, adding the direct link cost $c(x,v)$ to neighbor $v$'s cost.

*(Questions 11 to 25 covering SQLite ACID properties, MAC addressing, Wireshark display filters, CIDR aggregation, and thread pooling are detailed in the full report).*

---

## 15. PRESENTATION SLIDES OUTLINE (15 SLIDES)

- **Slide 1: Title & Author Details** (Project Title, Student Name, Roll Number, Department, Guide Name).
- **Slide 2: Problem Statement & Motivation** (Need for secure, reliable, fault-tolerant banking networks).
- **Slide 3: Project Objectives** (10 integrated networking and cryptographic goals).
- **Slide 4: Overall Banking Network Topology** (10 nodes, 3 tiers: Branch, WAN Mesh, Head Office DC).
- **Slide 5: Module 1 - IP Addressing & Subnetting** (32-bit binary math, CIDR conversion, subnet boundaries).
- **Slide 6: Module 2 - Dijkstra's Shortest Path Algorithm** (Adjacency list, priority queue, dynamic failover).
- **Slide 7: Module 3 - Distance Vector Routing** (Bellman-Ford equation, iterative vector exchange, convergence).
- **Slide 8: Module 4 - Cryptographic Security** (AES-256-GCM, 96-bit nonces, AEAD authentication tag defense).
- **Slide 9: Module 5 - TCP Socket Communication** (Multi-threading, 4-byte length-prefixed framing).
- **Slide 10: Module 6 - CRC Modulo-2 Error Detection** (Polynomial division, FCS remainder, bit flip detection).
- **Slide 11: Module 7 - Go-Back-N Sliding Window ARQ** (Window size N, out-of-order discard, timeout rewind).
- **Slide 12: Module 8 - Wireshark Packet Inspection** (Capturing TCP port 9090, 3-way handshake, encrypted payload).
- **Slide 13: Module 9 - Database Design & ACID Ledger** (SQLite transactional updates, rollback safety).
- **Slide 14: Experimental Test Results** (100% test pass rate across all 15 test cases).
- **Slide 15: Conclusion & Future Scope** (Summary, academic contributions, IPv6 / TLS 1.3 roadmap).

---

## 16. CONCLUSION & REFERENCES

This project successfully demonstrates a complete, production-structured academic banking network application uniting the full stack of Computer Networks and Cryptography principles. Every module is executable, thoroughly tested, and designed for rigorous academic evaluation.

### References
1. Kurose, J. F., & Ross, K. W. *Computer Networking: A Top-Down Approach* (8th ed.). Pearson.
2. Tanenbaum, A. S., & Wetherall, D. J. *Computer Networks* (5th ed.). Prentice Hall.
3. Stallings, W. *Cryptography and Network Security: Principles and Practice* (8th ed.). Pearson.
4. Dworkin, M. *NIST Special Publication 800-38D: Recommendation for Block Cipher Modes of Operation: Galois/Counter Mode (GCM)*. National Institute of Standards and Technology.
5. Postel, J. *Transmission Control Protocol (RFC 793)*. Internet Engineering Task Force (IETF).
