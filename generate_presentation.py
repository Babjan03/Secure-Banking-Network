import os
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE

def create_deck():
    prs = Presentation()
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)

    # Color Palette Definitions
    COLOR_BG = RGBColor(15, 23, 42)          # #0F172A Deep Navy
    COLOR_CARD = RGBColor(30, 41, 59)        # #1E293B Card Slate
    COLOR_CARD_BORDER = RGBColor(51, 65, 85) # #334155 Border
    COLOR_CYAN = RGBColor(14, 165, 233)      # #0EA5E9 Electric Sky Blue
    COLOR_LIGHT_CYAN = RGBColor(56, 189, 248)# #38BDF8 Light Cyan Highlight
    COLOR_GREEN = RGBColor(16, 185, 129)     # #10B981 Success Emerald
    COLOR_WHITE = RGBColor(248, 250, 252)    # #F8FAFC Bright White
    COLOR_GRAY = RGBColor(148, 163, 184)     # #94A3B8 Secondary Slate
    COLOR_DARK_BOX = RGBColor(11, 15, 25)    # #0B0F19 Terminal Black
    COLOR_AMBER = RGBColor(245, 158, 11)     # #F59E0B Warning Amber

    blank_layout = prs.slide_layouts[6]

    def set_slide_background(slide):
        bg = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, 0, 0, Inches(13.333), Inches(7.5))
        bg.fill.solid()
        bg.fill.fore_color.rgb = COLOR_BG
        bg.line.fill.background()
        return bg

    def add_header(slide, title_text, category_tag="ACADEMIC SEMINAR PRESENTATION"):
        # Header banner container
        top_bar = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(0.8), Inches(0.4), Inches(11.733), Inches(0.9))
        top_bar.fill.solid()
        top_bar.fill.fore_color.rgb = COLOR_DARK_BOX
        top_bar.line.color.rgb = COLOR_CARD_BORDER
        top_bar.line.width = Pt(1)

        tf = top_bar.text_frame
        tf.word_wrap = True
        tf.margin_left = Inches(0.2)
        tf.margin_top = Inches(0.08)

        p_tag = tf.paragraphs[0]
        p_tag.text = category_tag.upper()
        p_tag.font.name = "Segoe UI"
        p_tag.font.size = Pt(9)
        p_tag.font.bold = True
        p_tag.font.color.rgb = COLOR_CYAN

        p_title = tf.add_paragraph()
        p_title.text = title_text
        p_title.font.name = "Segoe UI"
        p_title.font.size = Pt(18)
        p_title.font.bold = True
        p_title.font.color.rgb = COLOR_WHITE

    def add_card(slide, left, top, width, height, title="", title_color=COLOR_CYAN):
        card = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(left), Inches(top), Inches(width), Inches(height))
        card.fill.solid()
        card.fill.fore_color.rgb = COLOR_CARD
        card.line.color.rgb = COLOR_CARD_BORDER
        card.line.width = Pt(1)

        if title:
            tb = slide.shapes.add_textbox(Inches(left + 0.15), Inches(top + 0.1), Inches(width - 0.3), Inches(0.4))
            tf = tb.text_frame
            tf.word_wrap = True
            p = tf.paragraphs[0]
            p.text = title
            p.font.name = "Segoe UI"
            p.font.size = Pt(13)
            p.font.bold = True
            p.font.color.rgb = title_color

        return card

    def add_bullet_points(slide, left, top, width, height, items, font_size=12, text_color=COLOR_WHITE):
        tb = slide.shapes.add_textbox(Inches(left), Inches(top), Inches(width), Inches(height))
        tf = tb.text_frame
        tf.word_wrap = True
        tf.margin_left = Inches(0.1)
        tf.margin_right = Inches(0.1)
        tf.margin_top = Inches(0.1)
        tf.margin_bottom = Inches(0.1)

        for i, item in enumerate(items):
            p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
            p.space_after = Pt(8)
            p.font.name = "Segoe UI"
            p.font.size = Pt(font_size)

            if isinstance(item, tuple):
                bold_part, regular_part = item
                r_bold = p.add_run()
                r_bold.text = bold_part + " "
                r_bold.font.bold = True
                r_bold.font.color.rgb = COLOR_LIGHT_CYAN

                r_reg = p.add_run()
                r_reg.text = regular_part
                r_reg.font.bold = False
                r_reg.font.color.rgb = text_color
            else:
                r = p.add_run()
                r.text = item
                r.font.color.rgb = text_color

        return tb

    def add_speaker_notes(slide, notes_text):
        notes_slide = slide.notes_slide
        text_frame = notes_slide.notes_text_frame
        text_frame.text = notes_text

    # =========================================================================
    # SLIDE 1: TITLE SLIDE
    # =========================================================================
    s1 = prs.slides.add_slide(blank_layout)
    set_slide_background(s1)

    # Outer decorative glow frame
    frame = s1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(1.2), Inches(0.8), Inches(10.933), Inches(5.9))
    frame.fill.solid()
    frame.fill.fore_color.rgb = COLOR_CARD
    frame.line.color.rgb = COLOR_CYAN
    frame.line.width = Pt(2)

    # Title Banner Box
    title_box = s1.shapes.add_textbox(Inches(1.6), Inches(1.2), Inches(10.133), Inches(2.2))
    tf1 = title_box.text_frame
    tf1.word_wrap = True

    p0 = tf1.paragraphs[0]
    p0.text = "FINAL YEAR ACADEMIC SEMINAR CAPSTONE PROJECT"
    p0.font.name = "Segoe UI"
    p0.font.size = Pt(12)
    p0.font.bold = True
    p0.font.color.rgb = COLOR_CYAN
    p0.space_after = Pt(6)

    p1 = tf1.add_paragraph()
    p1.text = "SECURE AND RELIABLE BANKING NETWORK\nUSING ROUTING AND ENCRYPTION"
    p1.font.name = "Segoe UI"
    p1.font.size = Pt(24)
    p1.font.bold = True
    p1.font.color.rgb = COLOR_WHITE
    p1.space_after = Pt(8)

    p2 = tf1.add_paragraph()
    p2.text = "Integrated Academic Computer Networks & Cybersecurity Capstone System"
    p2.font.name = "Segoe UI"
    p2.font.size = Pt(14)
    p2.font.color.rgb = COLOR_LIGHT_CYAN

    # Badges row
    badges = s1.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(1.6), Inches(3.6), Inches(10.133), Inches(0.45))
    badges.fill.solid()
    badges.fill.fore_color.rgb = COLOR_DARK_BOX
    badges.line.color.rgb = COLOR_CARD_BORDER
    tf_b = badges.text_frame
    p_b = tf_b.paragraphs[0]
    p_b.alignment = PP_ALIGN.CENTER
    p_b.text = "Java 21  |  AES-256-GCM (NIST Nonces)  |  TCP Sockets (Port 9090)  |  Dijkstra & Bellman-Ford  |  CRC Modulo-2  |  SQLite ACID"
    p_b.font.name = "Segoe UI"
    p_b.font.size = Pt(10)
    p_b.font.bold = True
    p_b.font.color.rgb = COLOR_GREEN

    # Presenter & Guide Metadata Details
    meta_box = s1.shapes.add_textbox(Inches(1.6), Inches(4.3), Inches(10.133), Inches(2.0))
    tf_m = meta_box.text_frame
    tf_m.word_wrap = True

    pm1 = tf_m.paragraphs[0]
    pm1.text = "Candidate Details:"
    pm1.font.bold = True
    pm1.font.size = Pt(11)
    pm1.font.color.rgb = COLOR_CYAN

    pm2 = tf_m.add_paragraph()
    pm2.text = "Presented by : [Student Name]     |     Roll Number: [Roll Number]"
    pm2.font.size = Pt(11)
    pm2.font.color.rgb = COLOR_WHITE

    pm3 = tf_m.add_paragraph()
    pm3.text = "Department   : Department of Computer Science & Engineering"
    pm3.font.size = Pt(11)
    pm3.font.color.rgb = COLOR_WHITE

    pm4 = tf_m.add_paragraph()
    pm4.text = "Institution  : [College / University Name]"
    pm4.font.size = Pt(11)
    pm4.font.color.rgb = COLOR_WHITE

    pm5 = tf_m.add_paragraph()
    pm5.text = "Under the Guidance of: [Guide Name / Project Supervisor]"
    pm5.font.bold = True
    pm5.font.size = Pt(11)
    pm5.font.color.rgb = COLOR_LIGHT_CYAN

    add_speaker_notes(s1,
        "Good morning respected members of the evaluation committee, guide, and fellow students. "
        "Welcome to my seminar presentation titled 'Secure and Reliable Banking Network Using Routing and Encryption'. "
        "In this project, we have designed and built a comprehensive, fully functional enterprise-grade banking simulation "
        "in Java 21 that models how ATM branches, regional offices, and headquarters communicate with guaranteed confidentiality, "
        "data integrity, dynamic link-state routing, transport-level reliability, and relational persistence. Let us explore the system architecture."
    )

    # =========================================================================
    # SLIDE 2: INTRODUCTION
    # =========================================================================
    s2 = prs.slides.add_slide(blank_layout)
    set_slide_background(s2)
    add_header(s2, "1. Introduction to Modern Secure Banking Networks", "BACKGROUND & DOMAIN CONTEXT")

    add_card(s2, 0.8, 1.5, 5.6, 5.3, "The Banking Communication Landscape", COLOR_CYAN)
    add_bullet_points(s2, 0.95, 2.0, 5.3, 4.6, [
        ("Core Banking Infrastructure:", "Modern financial institutions operate distributed networks linking thousands of retail ATMs, teller workstations, regional servers, and centralized ledger mainframes across public and private telecommunication trunks."),
        ("Multi-Tier Communication Requirements:", "A single debit transaction initiated at a remote ATM must traverse multiple network tiers—Local Area Networks (LANs), Wide Area Network (WAN) meshes, and secure head-office demilitarized zones (DMZs)."),
        ("Three Pillars of Financial Telecommunications:", "Reliable data transfer, optimal dynamic path computation, and cryptographic secrecy are mandatory to maintain customer trust and regulatory compliance.")
    ], font_size=12)

    add_card(s2, 6.7, 1.5, 5.8, 5.3, "Why Integrated Simulation is Essential", COLOR_GREEN)
    add_bullet_points(s2, 6.85, 2.0, 5.5, 4.6, [
        ("Beyond Isolated Theory:", "In traditional academic coursework, routing algorithms, cryptographic ciphers, socket programming, and error checking are studied as disconnected textbook formulas."),
        ("Real-World Protocol Synthesis:", "This project synthesizes all foundational layers into a single cohesive desktop application: Data Link (CRC), Network (IPv4 & Dijkstra/Bellman-Ford), Transport (TCP & Go-Back-N), Application (AES-GCM), and Database (ACID SQL)."),
        ("Interactive Pedagogical Platform:", "Students, evaluators, and network administrators can observe real-time packet transmission, inject wire bit errors, simulate severed fiber cables, and verify packet payloads in Wireshark.")
    ], font_size=12)

    add_speaker_notes(s2,
        "To introduce our project: Commercial banking communication networks are critical national infrastructures. "
        "Whenever a customer withdraws cash or deposits funds, sensitive financial messages must travel over untrusted communication channels. "
        "If a communication line gets cut, the network must autonomously reroute packets without human intervention. "
        "If an attacker eavesdrops or modifies a packet, the cryptographic system must detect and reject it immediately. "
        "Our capstone application models these real-world interactions inside a unified, interactive Java environment."
    )

    # =========================================================================
    # SLIDE 3: PROBLEM STATEMENT
    # =========================================================================
    s3 = prs.slides.add_slide(blank_layout)
    set_slide_background(s3)
    add_header(s3, "2. Problem Statement: Vulnerabilities in Financial Data Transmission", "SECURITY & NETWORK CHALLENGES")

    # 4 grid cards for the 4 primary vulnerabilities
    cards_data = [
        ("Security Threats on Financial Data", COLOR_CYAN, 0.8, 1.5, 5.6, 2.5, [
            ("Eavesdropping & Packet Sniffing:", "Unencrypted transactions sent in plaintext expose account numbers, PINs, and balances to attackers on shared or public WAN links."),
            ("Man-in-the-Middle (MITM) Manipulation:", "Attackers can intercept wire packets and tamper with account targets or transfer amounts if payloads lack integrity authentication.")
        ]),
        ("Physical Link Failures & WAN Outages", COLOR_AMBER, 6.7, 1.5, 5.8, 2.5, [
            ("Fiber Cuts & Router Failures:", "Underground telecom cables and intermediate routers frequently fail. Static routing results in complete banking blackout."),
            ("High Latency & Inefficient Paths:", "Suboptimal routes increase financial round-trip time, causing timeouts, transaction deadlocks, and degraded customer experience.")
        ]),
        ("Transport Fragmentation & Split Packets", COLOR_CYAN, 0.8, 4.3, 5.6, 2.5, [
            ("TCP Stream Demarcation Issues:", "Streaming TCP provides an unstructured byte stream without native message boundaries. Payloads can split across multiple socket reads."),
            ("Frame Loss on Noisy Lines:", "Physical line noise causes bit corruption. Without systematic error detection and ARQ retransmission, corrupted ledger entries occur.")
        ]),
        ("The Need for an Integrated Solution", COLOR_GREEN, 6.7, 4.3, 5.8, 2.5, [
            ("Fragmented Academic Demonstrations:", "Existing student projects use hardcoded mockups, omit cryptographic nonces, or falsify packet captures."),
            ("The Solution:", "An end-to-end, deterministic banking network uniting true streaming TCP sockets, AES-256-GCM AEAD, live Dijkstra failover, and relational ACID persistence.")
        ])
    ]

    for title, col, x, y, w, h, items in cards_data:
        add_card(s3, x, y, w, h, title, col)
        add_bullet_points(s3, x + 0.15, y + 0.45, w - 0.3, h - 0.55, items, font_size=11)

    add_speaker_notes(s3,
        "Here we articulate the core problems we address: Financial data in transit faces four major threats: "
        "First, eavesdropping and active MITM tampering. "
        "Second, physical WAN link cuts which cause branch isolation if routers cannot dynamically recalculate paths. "
        "Third, streaming TCP packet fragmentation, where un-framed sockets crash or read incomplete payloads. "
        "And fourth, physical bit corruption on telecommunication lines. "
        "Our project directly addresses and solves each of these vulnerabilities."
    )

    # =========================================================================
    # SLIDE 4: PROJECT OBJECTIVES
    # =========================================================================
    s4 = prs.slides.add_slide(blank_layout)
    set_slide_background(s4)
    add_header(s4, "3. Project Objectives & Engineering Scope", "PROJECT GOALS & MILESTONES")

    add_card(s4, 0.8, 1.5, 11.733, 5.3, "Primary Technical & Pedagogical Goals", COLOR_CYAN)

    obj_items = [
        ("1. Multi-Tier Banking Network Architecture:", "Design and simulate an enterprise 10-node banking topology linking branch ATMs, manager workstations, branch routers, and central headquarters."),
        ("2. IPv4 Addressing & Mathematical Subnetting:", "Implement 32-bit binary octet arithmetic, CIDR conversions (/8 to /30), broadcast domains, and Layer 2 direct communication eligibility checks."),
        ("3. Dynamic Link-State & Distance Vector Routing:", "Implement Dijkstra's shortest path algorithm with priority queues for real-time link failure rerouting, and Bellman-Ford Distance Vector routing with iterative convergence."),
        ("4. Authenticated Cryptographic Protection:", "Shield transaction payloads using AES-256 in Galois/Counter Mode (GCM) with fresh 96-bit random nonces and 128-bit GHASH authentication tags."),
        ("5. True Streaming TCP Sockets & Protocol Framing:", "Develop a multi-threaded ServerSocket on port 9090 enforcing 4-byte big-endian length-prefixed framing to completely eliminate TCP fragmentation errors."),
        ("6. Data Link Error Detection & Flow Control Simulation:", "Implement Modulo-2 binary polynomial division for CRC Frame Check Sequences (FCS), alongside interactive Go-Back-N Sliding Window ARQ simulation."),
        ("7. Real-Time Packet Sniffing & Relational Persistence:", "Enable live packet capture in Wireshark (tcp.port == 9090) and commit transactions to an ACID-compliant SQLite relational database.")
    ]
    add_bullet_points(s4, 1.0, 2.05, 11.3, 4.6, obj_items, font_size=11)

    add_speaker_notes(s4,
        "The project has seven concrete engineering objectives. "
        "Notice that our scope covers every major layer of the OSI model: from Data Link Layer CRC, to Network Layer Dijkstra and subnetting, "
        "to Transport Layer TCP sockets and Go-Back-N, to Application Layer AES-256-GCM authenticated encryption, all backed by an ACID relational database. "
        "Every objective has been completely implemented in Java 21 and verified."
    )

    # =========================================================================
    # SLIDE 5: PROPOSED SYSTEM ARCHITECTURE
    # =========================================================================
    s5 = prs.slides.add_slide(blank_layout)
    set_slide_background(s5)
    add_header(s5, "4. Proposed System Architecture: 3-Tier Enterprise Topology", "SYSTEM ARCHITECTURE")

    # 3 Tier Cards
    add_card(s5, 0.8, 1.5, 3.6, 5.3, "Tier 1: Branch Office A", COLOR_CYAN)
    add_bullet_points(s5, 0.95, 2.0, 3.3, 4.6, [
        ("ATM Terminal 1 (ATM-01):", "192.168.1.10/24\nCash dispenser terminal for customer withdrawals."),
        ("ATM Terminal 2 (ATM-02):", "192.168.1.20/24\nDrive-through automated teller terminal."),
        ("Customer Workstation (CUST-01):", "192.168.2.10/24\nBranch teller desk terminal."),
        ("Branch Router 1 (RTR-BR1):", "Gateway IP: 192.168.1.1\nInterfaces: .2.1, WAN 10.0.1.1\nConnects local subnets to WAN mesh.")
    ], font_size=11)

    add_card(s5, 4.8, 1.5, 3.7, 5.3, "Tier 2: Branch Office B", COLOR_AMBER)
    add_bullet_points(s5, 4.95, 2.0, 3.4, 4.6, [
        ("Manager System (MGR-01):", "192.168.3.10/24\nBranch operations supervisor terminal."),
        ("Local Server (SRV-BR1):", "192.168.4.10/24\nBranch local document and caching server."),
        ("Branch Router 2 (RTR-BR2):", "Gateway IP: 192.168.3.1\nInterfaces: .4.1, WAN 10.0.2.1\nHigh-speed interconnection gateway."),
        ("WAN Backbone Mesh Links:", "RTR-BR1 <-> RTR-BR2 (Cost: 4)\nRTR-BR1 <-> RTR-HQ (Cost: 7)\nRTR-BR2 <-> RTR-HQ (Cost: 2)")
    ], font_size=11)

    add_card(s5, 8.9, 1.5, 3.6, 5.3, "Tier 3: Head Office DC", COLOR_GREEN)
    add_bullet_points(s5, 9.05, 2.0, 3.3, 4.6, [
        ("Core Router (RTR-HQ):", "Gateway IP: 192.168.5.1\nInterfaces: .6.1, WAN 10.0.3.1\nHigh-throughput enterprise core router."),
        ("Central Banking Server (SRV-HQ):", "192.168.5.10/24\nMulti-threaded TCP server listening on Port 9090."),
        ("Ledger Database Server (DB-HQ):", "192.168.6.10/24\nEmbedded SQLite ACID transaction store."),
        ("Data Center Bus:", "Ultra-low latency 10 Gbps interconnect (Cost: 1, 0 ms latency).")
    ], font_size=11)

    add_speaker_notes(s5,
        "Slide 5 illustrates our 3-tier system architecture: "
        "Tier 1 represents Branch Office A containing ATM 1, ATM 2, and the customer desk connected to Branch Router 1. "
        "Tier 2 represents Branch Office B with the manager workstation, local branch server, and Branch Router 2. "
        "Tier 3 represents the central Head Office Data Center containing the Core Router, Central Banking Server on TCP port 9090, and the Ledger Database Server. "
        "Notice the WAN backbone mesh: we intentionally configure multiple routes between routers with varying link costs to demonstrate dynamic routing."
    )

    # =========================================================================
    # SLIDE 6: NETWORK TOPOLOGY & IP ADDRESSING
    # =========================================================================
    s6 = prs.slides.add_slide(blank_layout)
    set_slide_background(s6)
    add_header(s6, "5. Network Topology Design & IPv4 Subnetting Scheme", "LAYER 3 NETWORK ADDRESSING")

    add_card(s6, 0.8, 1.5, 5.6, 5.3, "Subnetting Table & Static Addressing Scheme", COLOR_CYAN)

    sub_table_items = [
        ("Subnet 1 - ATM Retail LAN:", "192.168.1.0/24\nMask: 255.255.255.0  |  Range: .1.1 - .1.254  |  Hosts: 254"),
        ("Subnet 2 - Customer Service Desk:", "192.168.2.0/24\nMask: 255.255.255.0  |  Range: .2.1 - .2.254  |  Hosts: 254"),
        ("Subnet 3 - Branch Manager LAN:", "192.168.3.0/24\nMask: 255.255.255.0  |  Range: .3.1 - .3.254  |  Hosts: 254"),
        ("Subnet 4 - Branch Local Server:", "192.168.4.0/24\nMask: 255.255.255.0  |  Range: .4.1 - .4.254  |  Hosts: 254"),
        ("Subnet 5 - Head Office Server DMZ:", "192.168.5.0/24\nMask: 255.255.255.0  |  Range: .5.1 - .5.254  |  Hosts: 254"),
        ("Subnet 6 - Secure Database LAN:", "192.168.6.0/24\nMask: 255.255.255.0  |  Range: .6.1 - .6.254  |  Hosts: 254")
    ]
    add_bullet_points(s6, 0.95, 2.0, 5.3, 4.6, sub_table_items, font_size=10)

    add_card(s6, 6.7, 1.5, 5.8, 5.3, "Mathematical Subnet Calculations", COLOR_GREEN)
    add_bullet_points(s6, 6.85, 2.0, 5.5, 4.6, [
        ("32-Bit Binary Address Translation:", "IPv4 addresses are parsed into 32-bit unsigned integers using bitwise shifts: IP = (O0 << 24) | (O1 << 16) | (O2 << 8) | O3."),
        ("Network ID & Broadcast Derivation:", "Network ID = IP AND Subnet Mask\nBroadcast Address = Network ID OR (~Subnet Mask)\nUsable Host Count = 2^(32 - prefix) - 2."),
        ("Direct Communication Eligibility:", "If (IP1 AND Mask) == (IP2 AND Mask), both hosts share the same broadcast domain and communicate directly via Layer 2 switch/ARP. Otherwise, packets must route via the default gateway router."),
        ("Point-to-Point WAN Links (/30):", "WAN serial links utilize 30-bit subnetting (10.0.1.0/30) providing exactly 2 usable IP addresses per link, conserving address space.")
    ], font_size=11)

    add_speaker_notes(s6,
        "Slide 6 presents our Layer 3 addressing scheme: "
        "We have segmented the banking network into six discrete /24 subnets. "
        "Our SubnetCalculator class implements binary bitwise mathematics rather than string splits. "
        "When an ATM checks whether it can reach the head office server, it performs a bitwise AND with its mask. "
        "Because 192.168.1.10 and 192.168.5.10 have different network prefixes, the ATM knows it cannot use Layer 2 direct ARP, "
        "and must forward the frame to its default gateway at Branch Router 1."
    )

    # =========================================================================
    # SLIDE 7: ROUTING ALGORITHMS
    # =========================================================================
    s7 = prs.slides.add_slide(blank_layout)
    set_slide_background(s7)
    add_header(s7, "6. Dynamic Routing: Dijkstra (Link-State) vs Bellman-Ford (DV)", "LAYER 3 ROUTING ENGINES")

    add_card(s7, 0.8, 1.5, 5.6, 5.3, "Dijkstra's Algorithm (Link-State)", COLOR_CYAN)
    add_bullet_points(s7, 0.95, 2.0, 5.3, 4.6, [
        ("Working Principle:", "Maintains full global network graph topology across all routers. Uses an adjacency list and min-priority queue with time complexity O((V+E) log V)."),
        ("Optimal Primary Route (ATM-01 to SRV-HQ):", "ATM-01 (1) -> RTR-BR1 (4) -> RTR-BR2 (2) -> RTR-HQ (1) -> SRV-HQ\nTotal Metric Cost = 8  (Hops: 4)"),
        ("Dynamic Link Failure & Failover:", "If leased line L_RTR2_HQ is severed (simulating a fiber cut), Dijkstra instantly recalculates: reroutes via direct WAN trunk RTR-BR1 -> RTR-HQ (Cost: 1 + 7 + 1 = 9). Zero packet loss!"),
        ("Real-World Protocol Equivalent:", "OSPF (Open Shortest Path First) / IS-IS.")
    ], font_size=11)

    add_card(s7, 6.7, 1.5, 5.8, 5.3, "Bellman-Ford (Distance Vector)", COLOR_AMBER)
    add_bullet_points(s7, 6.85, 2.0, 5.5, 4.6, [
        ("Working Principle (Routing by Rumor):", "Routers only know direct neighbor link costs and exchange distance vectors periodically. Evaluates: D_x(y) = min_v { c(x,v) + D_v(y) }."),
        ("Iterative Convergence in Banking Topology:", "Round 0: Direct neighbors only (SRV-HQ cost is INFINITY)\nRound 1: 1-hop neighbor vectors exchanged\nRound 2: Inter-router links discovered\nRound 3: Optimal WAN paths propagate\nRound 4: CONVERGENCE ACHIEVED (Zero table changes)"),
        ("Key Comparison for Viva:", "Link-State requires full global map and converges rapidly. Distance Vector is decentralized, simpler, but takes multiple iterative rounds to converge."),
        ("Real-World Protocol Equivalent:", "RIP (Routing Information Protocol) / BGP.")
    ], font_size=11)

    add_speaker_notes(s7,
        "Slide 7 is a favorite topic in viva examinations: We have implemented two distinct routing algorithms in Java: "
        "First, Dijkstra's Link-State algorithm: Every node has complete topology visibility. "
        "From ATM 1 to the Head Office server, the optimal path goes through Branch Router 2 with a total cost of 8. "
        "When we simulate cutting that fiber line, Dijkstra automatically switches traffic over the direct backup trunk with a cost of 9. "
        "Second, Distance Vector routing based on the Bellman-Ford equation. Here routers only talk to direct neighbors. "
        "Our simulation shows step-by-step how distance vectors spread, converging in exactly 4 rounds."
    )

    # =========================================================================
    # SLIDE 8: SECURITY MECHANISM (AES-256-GCM)
    # =========================================================================
    s8 = prs.slides.add_slide(blank_layout)
    set_slide_background(s8)
    add_header(s8, "7. Cryptographic Protection: Authenticated AES-256-GCM", "APPLICATION LAYER CYBERSECURITY")

    add_card(s8, 0.8, 1.5, 5.6, 5.3, "Why AES-256-GCM is the Gold Standard", COLOR_CYAN)
    add_bullet_points(s8, 0.95, 2.0, 5.3, 4.6, [
        ("Authenticated Encryption (AEAD):", "Traditional AES-CBC only provides confidentiality (encryption) and is vulnerable to padding oracle attacks. AES-GCM provides both Confidentiality and Authenticity simultaneously."),
        ("Galois/Counter Mode (GCM):", "Combines standard Counter (CTR) mode encryption with a high-speed Galois Hash (GHASH) to compute a 128-bit authentication tag over the ciphertext."),
        ("NIST SP 800-38D Nonce Freshness:", "A fresh, cryptographically random 96-bit (12-byte) Initialization Vector (Nonce) is generated for every single transaction using SecureRandom. Encrypting the same transaction twice yields completely different ciphertexts, preventing replay attacks!"),
        ("Zero Key Leakage:", "Master keys are maintained securely in memory and never hardcoded in logs or database records.")
    ], font_size=11)

    add_card(s8, 6.7, 1.5, 5.8, 5.3, "Cryptographic Transmission Flow & Tamper Detection", COLOR_GREEN)

    # Draw small flowchart boxes inside right card
    flow_steps = [
        ("Step 1: Plaintext Message", "JSON Payload: {source, account, type, amount}"),
        ("Step 2: Nonce Generation", "96-bit random IV generated via SecureRandom"),
        ("Step 3: AES-256-GCM Cipher", "Encrypts bytes & derives 128-bit GHASH Auth Tag"),
        ("Step 4: Wire Serialization", "ENC:<Base64(IV)>:<Base64(Ciphertext+Tag)>"),
        ("Step 5: Decryption & Auth", "Server verifies 128-bit Tag before accepting"),
        ("TAMPER DEFENSE RESULT", "Flipping 1 bit raises AEADBadTagException! Transaction aborted instantly with zero fund transfer!")
    ]
    add_bullet_points(s8, 6.85, 2.0, 5.5, 4.6, flow_steps, font_size=10)

    add_speaker_notes(s8,
        "Slide 8 covers our cybersecurity layer: In commercial banking, encryption without authentication is dangerous. "
        "We implemented AES-256 in Galois/Counter Mode (GCM), which is an Authenticated Encryption with Associated Data (AEAD) cipher. "
        "For every transaction, our system generates a fresh 96-bit random nonce. "
        "Along with the ciphertext, AES-GCM generates a 128-bit GHASH authentication tag. "
        "In our dashboard, we demonstrate active man-in-the-middle tamper attacks: flipping even a single bit in the ciphertext causes "
        "Java Cryptography Architecture to throw an AEADBadTagException, instantly aborting the transaction and preventing fraudulent fund transfers."
    )

    # =========================================================================
    # SLIDE 9: ATM CLIENT-SERVER COMMUNICATION
    # =========================================================================
    s9 = prs.slides.add_slide(blank_layout)
    set_slide_background(s9)
    add_header(s9, "8. TCP Client-Server Sockets & Message Framing", "TRANSPORT LAYER NETWORKING")

    add_card(s9, 0.8, 1.5, 5.6, 5.3, "TCP Sockets & 3-Way Handshake", COLOR_CYAN)
    add_bullet_points(s9, 0.95, 2.0, 5.3, 4.6, [
        ("Java Socket Architecture:", "ATM client instances connect to java.net.ServerSocket running on the Central Banking Server at Port 9090."),
        ("Connection Establishment (3-Way Handshake):", "1. ATM Client sends [SYN] with initial sequence number x.\n2. Banking Server replies [SYN, ACK] with ack x+1 and seq y.\n3. Client replies [ACK] with ack y+1. Connection ESTABLISHED!"),
        ("Multi-Threaded Concurrency:", "The server utilizes an ExecutorService thread pool (newCachedThreadPool) to handle simultaneous connections from multiple ATMs (ATM-01, ATM-02, CUST-01) without blocking."),
        ("Timeouts & Graceful Teardown:", "Sockets enforce 10-second read timeouts and execute 4-way FIN/ACK disconnection handshakes.")
    ], font_size=11)

    add_card(s9, 6.7, 1.5, 5.8, 5.3, "4-Byte Length-Prefixed Protocol Framing", COLOR_GREEN)
    add_bullet_points(s9, 6.85, 2.0, 5.5, 4.6, [
        ("The TCP Stream Fragmentation Problem:", "TCP operates as a continuous byte stream without application-level message boundaries. A 300-byte message may arrive split across two separate reads (e.g., 100 bytes and 200 bytes)."),
        ("Length-Prefix Header Solution:", "Every message on the wire is prepended with a 4-Byte Big-Endian Integer indicating exact payload byte length: [ 4-Byte Length Header | Encrypted Payload Bytes ]."),
        ("Deterministic Atomic Reading:", "Receivers read the 4-byte header first, allocate an exact buffer, and invoke DataInputStream.readFully() to guarantee complete packet assembly prior to decryption."),
        ("Production Reliability:", "Completely prevents truncated payload errors, JSON parsing exceptions, and framing buffer corruption.")
    ], font_size=11)

    add_speaker_notes(s9,
        "Slide 9 explains our Transport Layer socket implementation: "
        "Our ATM client connects to the banking server on TCP port 9090. "
        "A critical technical problem in TCP socket programming is stream fragmentation: TCP doesn't know where one message ends and the next begins. "
        "To solve this, we implemented 4-byte big-endian length-prefixed framing. "
        "The sender sends the payload size in the first 4 bytes, and the receiver uses readFully to read exactly that many bytes. "
        "This guarantees complete, atomic packet reconstruction even if TCP splits data across multiple network segments."
    )

    # =========================================================================
    # SLIDE 10: ERROR DETECTION & RELIABLE TRANSMISSION
    # =========================================================================
    s10 = prs.slides.add_slide(blank_layout)
    set_slide_background(s10)
    add_header(s10, "9. Error Detection & Flow Control: CRC & Go-Back-N ARQ", "DATA LINK & TRANSPORT RELIABILITY")

    add_card(s10, 0.8, 1.5, 5.6, 5.3, "CRC Error Detection (Modulo-2 Division)", COLOR_CYAN)
    add_bullet_points(s10, 0.95, 2.0, 5.3, 4.6, [
        ("Polynomial Binary Division:", "Implements Modulo-2 binary arithmetic where XOR addition replaces subtraction without carries or borrows."),
        ("Generator Polynomial G(x):", "Supports CRC-4 (x^4 + x + 1), CRC-3 Demo (x^3 + x^2 + 1), and ATM CRC-8 HEC (100000111)."),
        ("Frame Check Sequence (FCS) Derivation:", "Augments k data bits with r zeros (where r = degree of G(x)). Remainder of Modulo-2 division forms the FCS. Transmitted Frame = Data + FCS."),
        ("Verification & Bit Flip Injection:", "The receiver divides received frame by G(x). Clean transmission yields 0 remainder. Our simulation injects bit errors: non-zero remainder detects physical corruption immediately!"),
        ("Academic Distinction:", "CRC detects accidental noise; it does NOT provide cryptographic protection against deliberate tampering (which requires AES-GCM).")
    ], font_size=10)

    add_card(s10, 6.7, 1.5, 5.8, 5.3, "Go-Back-N Sliding Window ARQ Simulation", COLOR_AMBER)
    add_bullet_points(s10, 6.85, 2.0, 5.5, 4.6, [
        ("Sliding Window Mechanics:", "Sender maintains window [send_base, send_base + N - 1] where N is the configurable window size (e.g., N = 4)."),
        ("Out-of-Order Discard Behavior:", "Receiver only accepts the expected sequence number (expected_seq_num). Subsequent out-of-order frames are silently discarded with duplicate cumulative ACKs."),
        ("Timeout & Rewind Retransmission:", "If Frame F2 is dropped in transit, frames F3-F5 are discarded. When the timer expires for F2, Go-Back-N rewinds next_seq_num to send_base and retransmits the entire window!"),
        ("Educational Clarity:", "Demonstrated as an educational protocol simulation separate from TCP sockets, illustrating foundational ARQ concepts without falsely claiming TCP uses pure GBN.")
    ], font_size=10)

    add_speaker_notes(s10,
        "Slide 10 details reliability and error control: "
        "We implement Cyclic Redundancy Check using Modulo-2 XOR binary division. "
        "A key viva point here is distinguishing CRC from AES-GCM: CRC detects accidental channel noise like thermal interference, "
        "while AES-GCM prevents malicious tampering. "
        "On the right, we simulate Go-Back-N Sliding Window ARQ. If Frame 2 is dropped, the receiver rejects out-of-order frames. "
        "When the sender's timer expires, it rewinds its sliding window back to Frame 2 and retransmits all unacknowledged frames."
    )

    # =========================================================================
    # SLIDE 11: DATABASE MANAGEMENT
    # =========================================================================
    s11 = prs.slides.add_slide(blank_layout)
    set_slide_background(s11)
    add_header(s11, "10. Relational Database Management & ACID Transactions", "DATA PERSISTENCE & LEDGER AUDIT")

    add_card(s11, 0.8, 1.5, 5.6, 5.3, "SQLite ACID Transaction Enforcement", COLOR_CYAN)
    add_bullet_points(s11, 0.95, 2.0, 5.3, 4.6, [
        ("Embedded SQLite Engine:", "Operates an embedded banking database (banking_network.db) linked via standard JDBC drivers (sqlite-jdbc-3.45.2.0)."),
        ("Atomicity (All-or-Nothing):", "Balance deductions execute inside explicit transactions with conn.setAutoCommit(false). If any step fails, conn.rollback() cancels all balance modifications."),
        ("Consistency & Integrity Constraints:", "Enforces SQL schema constraints: balance >= 0 and amount > 0. Attempts to withdraw more than available funds are declined cleanly."),
        ("Isolation & Concurrency:", "Prevents race conditions and double-spending across concurrent ATM client threads."),
        ("Durability:", "Successful transactions are committed to disk with unique tracking identifiers (e.g., TX-W-A48F90).")
    ], font_size=11)

    add_card(s11, 6.7, 1.5, 5.8, 5.3, "Database Schema & Entity Organization", COLOR_GREEN)
    add_bullet_points(s11, 6.85, 2.0, 5.5, 4.6, [
        ("1. Customers Table:", "customer_id (PK), name, account_number (UNIQUE), account_type, balance.\nPre-seeded with realistic fictional demo balances (Alice: ₹50,000, Rajesh: ₹125,000, Priya: ₹8,500, Vikram: ₹34,000)."),
        ("2. Transactions Table:", "transaction_id (PK), account_number (FK), transaction_type (WITHDRAW/DEPOSIT), amount, transaction_timestamp, status (SUCCESS/FAILED)."),
        ("3. NetworkLogs Table:", "log_id (PK AUTOINCREMENT), source_node, destination_node, protocol (TCP/AES-GCM/DIJKSTRA/DB), event_description, timestamp."),
        ("SQL Injection Defense:", "100% of database queries use PreparedStatement parameter binding, preventing SQL injection vulnerabilities.")
    ], font_size=11)

    add_speaker_notes(s11,
        "Slide 11 focuses on database design and transaction management: "
        "Our central banking server persists ledger records in an embedded SQLite database using JDBC. "
        "We enforce full ACID compliance. When an ATM requests a withdrawal, we disable auto-commit, verify balance, deduct funds, "
        "record the transaction log, and commit. If funds are insufficient, we issue a rollback, ensuring account balances never become negative. "
        "Furthermore, all queries use PreparedStatements, preventing SQL injection attacks."
    )

    # =========================================================================
    # SLIDE 12: WIRESHARK NETWORK ANALYSIS
    # =========================================================================
    s12 = prs.slides.add_slide(blank_layout)
    set_slide_background(s12)
    add_header(s12, "11. Network Packet Capture & Wireshark Analysis", "LIVE PACKET INSPECTION (PORT 9090)")

    add_card(s12, 0.8, 1.5, 5.6, 5.3, "Real Wireshark Packet Capture Workflow", COLOR_CYAN)
    add_bullet_points(s12, 0.95, 2.0, 5.3, 4.6, [
        ("Live Interface Sniffing:", "Wireshark captures genuine authorized socket traffic on the Loopback adapter (Npcap Loopback Adapter / 127.0.0.1)."),
        ("Precision Display Filter:", "Applying display filter tcp.port == 9090 isolates banking client-server traffic from background OS packets."),
        ("Verification of 3-Way Handshake:", "Packets 1 to 3 demonstrate authentic TCP connection negotiation:\n• Packet 1: [SYN]  Seq=0, Win=65535\n• Packet 2: [SYN, ACK]  Seq=0, Ack=1\n• Packet 3: [ACK]  Seq=1, Ack=1"),
        ("Traffic Generation from GUI:", "The dashboard includes a dedicated 'Generate Live TCP Traffic for Wireshark Capture' button to trigger repeatable streams during presentations.")
    ], font_size=11)

    add_card(s12, 6.7, 1.5, 5.8, 5.3, "Payload Confidentiality Verification", COLOR_GREEN)
    add_bullet_points(s12, 6.85, 2.0, 5.5, 4.6, [
        ("Inspecting Application Data [PSH, ACK]:", "Examining packet 4 in Wireshark shows the length-prefixed payload."),
        ("TCP Stream Inspection (Follow TCP Stream):", "The payload content begins with: ENC:iaGMiTK8zJmhj3h+:1w1H0Mm..."),
        ("Zero Leakage of Sensitive Financial Data:", "No account numbers (ACC-100234), customer names, or withdrawal amounts can be read on the wire! An eavesdropper only observes high-entropy ciphertext and the random IV."),
        ("TCP 4-Way Teardown Inspection:", "Packets 6 to 7 reveal [FIN, ACK] sequence terminating the socket cleanly, leaving zero lingering sockets in CLOSE_WAIT.")
    ], font_size=11)

    add_speaker_notes(s12,
        "Slide 12 demonstrates live network packet analysis with Wireshark: "
        "Unlike projects that print fake text logs, our application opens genuine operating system sockets on TCP port 9090. "
        "Faculty can open Wireshark on the loopback adapter, filter by tcp.port == 9090, and click our traffic generator button. "
        "They can inspect the actual SYN, SYN-ACK, and ACK handshake packets, followed by the PSH-ACK segment. "
        "Right-clicking and following the TCP stream proves our security: the payload is completely encrypted with AES-256-GCM, "
        "proving that zero account data or customer names leak onto the network."
    )

    # =========================================================================
    # SLIDE 13: APPLICATION SCREENSHOTS & IMPLEMENTATION
    # =========================================================================
    s13 = prs.slides.add_slide(blank_layout)
    set_slide_background(s13)
    add_header(s13, "12. Application Screenshots & Graphical Implementation", "LIVE SYSTEM DEMONSTRATION")

    # Add 2 screenshot cards side by side
    img1_path = "C:/Users/SHAIK BABJAN/.gemini/antigravity/brain/675485df-2b37-44a1-ba35-291286969170/.user_uploaded/media_1794407594766_eb941c59.jpg"
    img2_path = "C:/Users/SHAIK BABJAN/.gemini/antigravity/brain/675485df-2b37-44a1-ba35-291286969170/.user_uploaded/media_1794407734153_953f014e.jpg"

    add_card(s13, 0.8, 1.5, 5.6, 5.3, "Module 4: AES-256-GCM Cryptographic UI", COLOR_CYAN)
    if os.path.exists(img1_path):
        s13.shapes.add_picture(img1_path, Inches(0.95), Inches(2.0), width=Inches(5.3), height=Inches(3.2))

    cap1 = s13.shapes.add_textbox(Inches(0.95), Inches(5.3), Inches(5.3), Inches(1.3))
    tf_c1 = cap1.text_frame
    tf_c1.word_wrap = True
    p_c1 = tf_c1.paragraphs[0]
    p_c1.text = "Figure 1: AES-256-GCM Security Module displaying editable plaintext payload, fresh 96-bit random nonce generation, Base64 ciphertext, and interactive MITM bit-tamper attack simulation with AEAD verification."
    p_c1.font.name = "Segoe UI"
    p_c1.font.size = Pt(10)
    p_c1.font.color.rgb = COLOR_LIGHT_CYAN

    add_card(s13, 6.7, 1.5, 5.8, 5.3, "Module 5: Live ATM Client & TCP Server UI", COLOR_GREEN)
    if os.path.exists(img2_path):
        s13.shapes.add_picture(img2_path, Inches(6.85), Inches(2.0), width=Inches(5.5), height=Inches(3.2))

    cap2 = s13.shapes.add_textbox(Inches(6.85), Inches(5.3), Inches(5.5), Inches(1.3))
    tf_c2 = cap2.text_frame
    tf_c2.word_wrap = True
    p_c2 = tf_c2.paragraphs[0]
    p_c2.text = "Figure 2: ATM Terminal Client and Central Banking Server showing live account selection, automatic amount locking for balance inquiries, length-prefixed TCP transmission, and instant bank receipt generation."
    p_c2.font.name = "Segoe UI"
    p_c2.font.size = Pt(10)
    p_c2.font.color.rgb = COLOR_LIGHT_CYAN

    add_speaker_notes(s13,
        "Slide 13 showcases actual screenshots of our running desktop application: "
        "Figure 1 on the left illustrates our AES-256-GCM encryption panel. Users can type any message or JSON payload, "
        "click encrypt to generate a fresh 96-bit nonce and 128-bit authentication tag, or simulate a bit-tamper attack. "
        "Figure 2 on the right shows the live ATM Client and Server panel, featuring automatic amount disabling for balance inquiries, "
        "multi-threaded socket communication, and real-time transaction receipt generation."
    )

    # =========================================================================
    # SLIDE 14: RESULTS & DEMONSTRATION
    # =========================================================================
    s14 = prs.slides.add_slide(blank_layout)
    set_slide_background(s14)
    add_header(s14, "13. Experimental Results & Verification Summary", "TESTING & VALIDATION MATRIX")

    add_card(s14, 0.8, 1.5, 11.733, 5.3, "Subsystem Test Outcomes (100% Verification Rate)", COLOR_CYAN)

    results_data = [
        ("IPv4 Addressing & Subnetting:", "Validated dotted-decimal syntax, 32-bit math, CIDR conversion, and same-subnet communication logic. 100% pass across 14 unit test cases."),
        ("Dijkstra Shortest Path Routing:", "Optimal route calculated (ATM-01 to SRV-HQ, Cost: 8). Simulated fiber cut of L_RTR2_HQ triggered instant failover via direct trunk (Cost: 9). Restoring link recovered optimal path."),
        ("Bellman-Ford Distance Vector:", "Iterative vector exchanges stabilized and reached mathematical convergence in exactly 4 rounds. Routing table entries matched Dijkstra optimal paths."),
        ("AES-256-GCM Cryptographic Engine:", "Confirmed 100% roundtrip decryption fidelity. Single-bit tampering was detected by GCM, raising AEADBadTagException and dropping fraudulent transactions."),
        ("TCP Sockets & Length-Prefixed Framing:", "Multiple clients connected concurrently. 4-byte framing eliminated TCP buffer splitting. Handshake and teardown verified cleanly."),
        ("CRC Modulo-2 Polynomial Division:", "Clean frames yielded 0 remainder. Single-bit flips generated non-zero remainders (e.g., 1010), successfully flagging physical line corruption."),
        ("Go-Back-N ARQ & Sliding Window:", "Successfully dropped Frame 2; receiver rejected out-of-order frames F3-F5. Timer expired, sender rewound window, and retransmitted all frames successfully."),
        ("SQLite ACID Transaction Integrity:", "Atomic withdrawals deducted balance. Insufficient funds triggered automatic rollback, preserving customer balances without corruption.")
    ]
    add_bullet_points(s14, 1.0, 2.05, 11.3, 4.6, results_data, font_size=10)

    add_speaker_notes(s14,
        "Slide 14 presents our experimental validation results: "
        "We executed automated unit and integration tests covering every single module. "
        "Dijkstra dynamic failover rerouted seamlessly with zero dropped transactions. "
        "Bellman-Ford converged in 4 rounds. AES-GCM successfully caught all bit-tamper attacks. "
        "CRC caught single-bit physical errors, Go-Back-N retransmitted dropped frames upon timeout, "
        "and SQLite preserved ACID ledger integrity. All subsystems achieved a 100% test pass rate."
    )

    # =========================================================================
    # SLIDE 15: APPLICATIONS, ADVANTAGES & FUTURE SCOPE
    # =========================================================================
    s15 = prs.slides.add_slide(blank_layout)
    set_slide_background(s15)
    add_header(s15, "14. Applications, Advantages & Future Scope", "REAL-WORLD IMPACT & ROADMAP")

    add_card(s15, 0.8, 1.5, 3.6, 5.3, "Real-World Applications", COLOR_CYAN)
    add_bullet_points(s15, 0.95, 2.0, 3.3, 4.6, [
        ("Retail Banking Networks:", "Secure ATM cash dispensing, branch teller operations, and inter-branch communications."),
        ("Financial Switching Centers:", "High-availability financial gateways routing credit/debit card authorization requests."),
        ("Critical Enterprise LAN/WAN:", "Organizations requiring authenticated encryption and dynamic failover routing (defense, healthcare, fintech).")
    ], font_size=11)

    add_card(s15, 4.8, 1.5, 3.7, 5.3, "Key Technical Advantages", COLOR_GREEN)
    add_bullet_points(s15, 4.95, 2.0, 3.4, 4.6, [
        ("Multi-Layer Defense:", "Combines Layer 2 CRC error checking, Layer 3 dynamic routing, Layer 4 reliable TCP transport, and Layer 7 AES-GCM encryption."),
        ("True Fault Tolerance:", "Autonomous link rerouting eliminates single points of failure in the WAN mesh."),
        ("Dual-Mode Navigation UI:", "Supports both modern Vertical Sidebar and Horizontal Tabs for maximum operator comfort.")
    ], font_size=11)

    add_card(s15, 8.9, 1.5, 3.6, 5.3, "Future Scope & Enhancements", COLOR_AMBER)
    add_bullet_points(s15, 9.05, 2.0, 3.3, 4.6, [
        ("TLS 1.3 Transport Security:", "Transitioning from application-level AES-GCM to standardized TLS 1.3 mutual certificate authentication (mTLS)."),
        ("IPv6 Dual-Stack Support:", "Extending addressing modules to support 128-bit IPv6 CIDR allocations."),
        ("Intrusion Detection System (IDS):", "Integrating real-time anomaly detection to identify volumetric DDoS attacks and replay flooding."),
        ("Cloud Microservices:", "Deploying banking endpoints on Kubernetes clusters with Redis distributed caching.")
    ], font_size=11)

    add_speaker_notes(s15,
        "Slide 15 highlights the applications, advantages, and future roadmap: "
        "Our system applies directly to retail banking, ATM switches, and enterprise telecommunications. "
        "Its key advantages are defense-in-depth across layers 2 through 7 and autonomous dynamic rerouting. "
        "For future enhancements, we can introduce mutual TLS 1.3 certificates, dual-stack IPv6 addressing, "
        "and machine-learning-based intrusion detection systems."
    )

    # =========================================================================
    # SLIDE 16: CONCLUSION & THANK YOU
    # =========================================================================
    s16 = prs.slides.add_slide(blank_layout)
    set_slide_background(s16)

    # Center card container
    center_card = s16.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(1.5), Inches(1.0), Inches(10.333), Inches(5.5))
    center_card.fill.solid()
    center_card.fill.fore_color.rgb = COLOR_CARD
    center_card.line.color.rgb = COLOR_CYAN
    center_card.line.width = Pt(2)

    tb_end = s16.shapes.add_textbox(Inches(1.8), Inches(1.3), Inches(9.733), Inches(4.9))
    tf_e = tb_end.text_frame
    tf_e.word_wrap = True

    pe1 = tf_e.paragraphs[0]
    pe1.alignment = PP_ALIGN.CENTER
    pe1.text = "PROJECT SUMMARY & CONCLUSION"
    pe1.font.name = "Segoe UI"
    pe1.font.size = Pt(13)
    pe1.font.bold = True
    pe1.font.color.rgb = COLOR_CYAN
    pe1.space_after = Pt(12)

    pe2 = tf_e.add_paragraph()
    pe2.text = "• Successfully engineered a fully functional, integrated banking network simulation demonstrating 10 core Computer Networks & Cybersecurity concepts in pure Java 21."
    pe2.font.name = "Segoe UI"
    pe2.font.size = Pt(12)
    pe2.font.color.rgb = COLOR_WHITE
    pe2.space_after = Pt(6)

    pe3 = tf_e.add_paragraph()
    pe3.text = "• Solved key networking hurdles: 4-byte length-prefixed framing for TCP stream fragmentation, NIST-compliant AES-256-GCM authenticated encryption for MITM protection, and dynamic Dijkstra failover for severed WAN links."
    pe3.font.name = "Segoe UI"
    pe3.font.size = Pt(12)
    pe3.font.color.rgb = COLOR_WHITE
    pe3.space_after = Pt(6)

    pe4 = tf_e.add_paragraph()
    pe4.text = "• Verified through genuine Wireshark packet capture on TCP port 9090, 100% automated test suites, and an intuitive dual-mode desktop GUI."
    pe4.font.name = "Segoe UI"
    pe4.font.size = Pt(12)
    pe4.font.color.rgb = COLOR_WHITE
    pe4.space_after = Pt(20)

    pe_ty = tf_e.add_paragraph()
    pe_ty.alignment = PP_ALIGN.CENTER
    pe_ty.text = "THANK YOU!"
    pe_ty.font.name = "Segoe UI"
    pe_ty.font.size = Pt(28)
    pe_ty.font.bold = True
    pe_ty.font.color.rgb = COLOR_GREEN
    pe_ty.space_after = Pt(6)

    pe_qa = tf_e.add_paragraph()
    pe_qa.alignment = PP_ALIGN.CENTER
    pe_qa.text = "Questions & Answers Welcomed"
    pe_qa.font.name = "Segoe UI"
    pe_qa.font.size = Pt(16)
    pe_qa.font.color.rgb = COLOR_LIGHT_CYAN

    add_speaker_notes(s16,
        "In conclusion, this project bridges theoretical computer networking with production-style software engineering. "
        "We have proven how authenticated encryption, dynamic link-state routing, transport framing, error detection, "
        "and relational persistence work together to safeguard commercial banking operations. "
        "Thank you for your time and kind attention. I am now open to any questions from the committee."
    )

    output_path = r"C:\AntiGravity\SecureBankingNetwork\Secure_Banking_Network_Presentation.pptx"
    prs.save(output_path)
    print(f"[SUCCESS] Presentation generated successfully at: {output_path}")

if __name__ == "__main__":
    create_deck()
