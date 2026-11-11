package ui;

import banking.BankingService;
import communication.TransactionMessage;

import javax.swing.*;
import java.awt.*;

/**
 * Educational Wireshark Packet Inspection Guide & Live Capture Generation Panel.
 */
public class WiresharkGuidePanel extends JPanel {

    private final BankingService bankingService;
    private JLabel lblCaptureStatus;

    public WiresharkGuidePanel(BankingService bankingService) {
        this.bankingService = bankingService;

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        // --- Top Action Bar ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        topBar.setBackground(UITheme.PANEL_BG);
        topBar.setBorder(BorderFactory.createLineBorder(UITheme.PANEL_BORDER, 1));

        JButton btnGenerateTraffic = UITheme.createPrimaryButton("Generate Live TCP Traffic for Wireshark Capture");
        btnGenerateTraffic.addActionListener(e -> generateTrafficStream());
        topBar.add(btnGenerateTraffic);

        lblCaptureStatus = new JLabel("Wireshark Filter Ready: tcp.port == 9090");
        lblCaptureStatus.setFont(UITheme.FONT_BOLD);
        lblCaptureStatus.setForeground(UITheme.ACCENT_CYAN);
        topBar.add(lblCaptureStatus);

        add(topBar, BorderLayout.NORTH);

        // --- Center Instruction & Analysis Card ---
        JPanel guideCard = UITheme.createCardPanel("REAL-WORLD WIRESHARK PACKET CAPTURE & ANALYSIS GUIDE");
        JTextArea txtGuide = UITheme.createConsoleArea(20, 30);
        txtGuide.setText("""
            ========================================================================================
                      PRACTICAL WIRESHARK PACKET INSPECTION GUIDE FOR FACULTY VIVA
            ========================================================================================
            
            STEP 1: SELECT NETWORK INTERFACE
              - Launch Wireshark on your computer.
              - In the interface list, double-click:
                * On Windows: "Adapter for loopback traffic capture" (or "Npcap Loopback Adapter").
                * On Linux/macOS: "Loopback: lo" (or "lo0").
            
            STEP 2: APPLY DISPLAY FILTER
              - In the Wireshark filter bar at the top, type the following filter and press Enter:
                tcp.port == 9090
              - Wireshark will now filter and display ONLY the authorized TCP banking packets.
            
            STEP 3: GENERATE GENUINE TCP BANKING TRAFFIC
              - Click the button above: [Generate Live TCP Traffic for Wireshark Capture]
                OR navigate to the "ATM Client & Server" tab and submit a withdrawal.
              - Switch immediately to Wireshark to observe the captured packets!
            
            STEP 4: IDENTIFY TCP THREE-WAY HANDSHAKE (CONNECTION ESTABLISHMENT)
              - Packet 1 [SYN]     : Client -> Server | Flag: [SYN], Seq: 0, Win: 65535
              - Packet 2 [SYN, ACK]: Server -> Client | Flags: [SYN, ACK], Seq: 0, Ack: 1
              - Packet 3 [ACK]     : Client -> Server | Flag: [ACK], Seq: 1, Ack: 1
              - This proves genuine Layer 4 Transport Layer connection negotiation.
            
            STEP 5: EXAMINE APPLICATION DATA FRAMING & PAYLOAD ENCRYPTION
              - Look for the [PSH, ACK] packet with length ~150-250 bytes.
              - Right-click this packet -> Select "Follow" -> "TCP Stream".
              - Notice that the payload starts with:
                "ENC:<Base64 Nonce>:<Base64 Ciphertext+Tag>"
              - VERIFICATION OF CONFIDENTIALITY:
                No account numbers (e.g. ACC-100234), customer names, or withdrawal amounts
                can be read in plaintext! Eavesdroppers only observe high-entropy ciphertext.
            
            STEP 6: TCP FOUR-WAY HANDSHAKE (CONNECTION TEARDOWN)
              - Observe FIN, ACK -> ACK -> FIN, ACK -> ACK packets gracefully closing the socket.
            ========================================================================================
            """);
        guideCard.add(new JScrollPane(txtGuide), BorderLayout.CENTER);
        add(guideCard, BorderLayout.CENTER);
    }

    private void generateTrafficStream() {
        new Thread(() -> {
            try {
                lblCaptureStatus.setText("Transmitting live TCP packet stream to 127.0.0.1:9090...");
                lblCaptureStatus.setForeground(UITheme.WARNING_AMBER);

                for (int i = 0; i < 3; i++) {
                    bankingService.executeAtmTransaction("ATM-01", "ACC-100234", TransactionMessage.Type.BALANCE_INQUIRY, 0.0);
                    Thread.sleep(500);
                }

                lblCaptureStatus.setText("Traffic generated successfully! Check Wireshark window.");
                lblCaptureStatus.setForeground(UITheme.SUCCESS_GREEN);
                JOptionPane.showMessageDialog(this,
                        "Successfully sent 3 genuine encrypted TCP transactions over port 9090!\nOpen Wireshark and apply filter: tcp.port == 9090",
                        "Traffic Stream Generated", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                lblCaptureStatus.setText("Error: " + ex.getMessage());
                lblCaptureStatus.setForeground(UITheme.DANGER_RED);
            }
        }).start();
    }
}
