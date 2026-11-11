package ui;

import security.DecryptionService;
import security.EncryptedPacket;
import security.EncryptionService;

import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;

/**
 * Interactive AES-256-GCM Cryptographic Security, Nonce Generation,
 * Decryption, and MITM Bit-Tamper Attack Demonstration Panel.
 * Supports arbitrary user-editable inputs for encryption and decryption.
 */
public class SecurityPanel extends JPanel {

    private final EncryptionService encryptionService;
    private final DecryptionService decryptionService;
    private final SecretKey masterKey;

    private JTextArea txtPlaintext;
    private JTextArea txtEncryptedWire;
    private JTextArea txtDecryptedOutput;
    private JLabel lblStatus;
    private EncryptedPacket lastEncryptedPacket;

    public SecurityPanel(SecretKey masterKey) {
        this.encryptionService = new EncryptionService();
        this.decryptionService = new DecryptionService();
        this.masterKey = masterKey;

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        JPanel split = new JPanel(new GridLayout(1, 2, 15, 0));
        split.setBackground(UITheme.BG_DARK);

        // --- Left Panel: Encryption Input & Ciphertext Generation ---
        JPanel leftCard = UITheme.createCardPanel("1. AUTHENTICATED AES-256-GCM ENCRYPTION");

        JPanel plainBox = new JPanel(new BorderLayout(5, 5));
        plainBox.setBackground(UITheme.PANEL_BG);
        JLabel lblP = new JLabel("Plaintext Input (Editable - Type Any Message, Numbers, or JSON):");
        lblP.setFont(UITheme.FONT_BOLD);
        lblP.setForeground(UITheme.ACCENT_CYAN);
        plainBox.add(lblP, BorderLayout.NORTH);

        txtPlaintext = UITheme.createConsoleArea(7, 30);
        txtPlaintext.setEditable(true);
        txtPlaintext.setBackground(new Color(20, 29, 47));
        txtPlaintext.setForeground(Color.WHITE);
        txtPlaintext.setCaretColor(Color.CYAN);
        txtPlaintext.setFont(new Font("Consolas", Font.PLAIN, 13));
        loadDefaultJson();
        plainBox.add(new JScrollPane(txtPlaintext), BorderLayout.CENTER);

        // Toolbar below Plaintext box
        JPanel btnBar1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        btnBar1.setBackground(UITheme.PANEL_BG);

        JButton btnEncrypt = UITheme.createPrimaryButton("Encrypt Input (Generate 96-bit Nonce)");
        btnEncrypt.addActionListener(e -> encryptAction());
        btnBar1.add(btnEncrypt);

        JButton btnSampleJson = UITheme.createSecondaryButton("Load Sample JSON");
        btnSampleJson.addActionListener(e -> loadDefaultJson());
        btnBar1.add(btnSampleJson);

        JButton btnClear = UITheme.createSecondaryButton("Clear Text");
        btnClear.addActionListener(e -> {
            txtPlaintext.setText("");
            txtPlaintext.requestFocus();
        });
        btnBar1.add(btnClear);

        plainBox.add(btnBar1, BorderLayout.SOUTH);
        leftCard.add(plainBox, BorderLayout.NORTH);

        JPanel wireBox = new JPanel(new BorderLayout(5, 5));
        wireBox.setBackground(UITheme.PANEL_BG);
        JLabel lblW = new JLabel("Encrypted Wire Packet (IV + Ciphertext + 128-bit Auth Tag):");
        lblW.setForeground(UITheme.TEXT_PRIMARY);
        wireBox.add(lblW, BorderLayout.NORTH);

        txtEncryptedWire = UITheme.createConsoleArea(9, 30);
        txtEncryptedWire.setEditable(true); // Allow user to inspect or edit/paste wire packets
        txtEncryptedWire.setBackground(new Color(15, 23, 42));
        txtEncryptedWire.setCaretColor(Color.CYAN);
        wireBox.add(new JScrollPane(txtEncryptedWire), BorderLayout.CENTER);
        leftCard.add(wireBox, BorderLayout.CENTER);

        split.add(leftCard);

        // --- Right Panel: Decryption & Tamper Attack Simulator ---
        JPanel rightCard = UITheme.createCardPanel("2. DECRYPTION & MITM TAMPER ATTACK SIMULATION");

        JPanel attackBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        attackBar.setBackground(UITheme.PANEL_BG);

        JButton btnDecrypt = UITheme.createPrimaryButton("Decrypt Current Packet");
        btnDecrypt.addActionListener(e -> decryptAction(false));
        attackBar.add(btnDecrypt);

        JButton btnTamper = UITheme.createSecondaryButton("Simulate MITM Bit-Flip Tamper Attack");
        btnTamper.addActionListener(e -> decryptAction(true));
        attackBar.add(btnTamper);

        JButton btnWrongKey = UITheme.createSecondaryButton("Test Unauthorized Key");
        btnWrongKey.addActionListener(e -> testWrongKey());
        attackBar.add(btnWrongKey);

        rightCard.add(attackBar, BorderLayout.NORTH);

        JPanel decBox = new JPanel(new BorderLayout(5, 5));
        decBox.setBackground(UITheme.PANEL_BG);
        JLabel lblD = new JLabel("Decrypted Output / Integrity Verification Result:");
        lblD.setForeground(UITheme.TEXT_PRIMARY);
        decBox.add(lblD, BorderLayout.NORTH);

        txtDecryptedOutput = UITheme.createConsoleArea(12, 30);
        decBox.add(new JScrollPane(txtDecryptedOutput), BorderLayout.CENTER);

        lblStatus = new JLabel("Status: Ready. Type any text and click Encrypt.");
        lblStatus.setFont(UITheme.FONT_BOLD);
        lblStatus.setForeground(UITheme.TEXT_SECONDARY);
        decBox.add(lblStatus, BorderLayout.SOUTH);

        rightCard.add(decBox, BorderLayout.CENTER);

        split.add(rightCard);
        add(split, BorderLayout.CENTER);

        encryptAction(); // Initial run
    }

    private void loadDefaultJson() {
        txtPlaintext.setText("{\n  \"source\": \"ATM-01\",\n  \"accountNumber\": \"ACC-100234\",\n  \"transactionType\": \"WITHDRAW\",\n  \"amount\": 2000.00,\n  \"currency\": \"INR\",\n  \"timestamp\": " + System.currentTimeMillis() + "\n}");
    }

    private void encryptAction() {
        try {
            String plain = txtPlaintext.getText();
            if (plain == null || plain.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter some text in the input box to encrypt.", "Empty Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            lastEncryptedPacket = encryptionService.encrypt(plain, masterKey);

            StringBuilder sb = new StringBuilder();
            sb.append("=== AES-256-GCM ENCRYPTED CONTAINER ===\n");
            sb.append(String.format("Algorithm / Mode   : AES / Galois Counter Mode (GCM)\n"));
            sb.append(String.format("Cipher Key Size    : 256 bits (32 bytes)\n"));
            sb.append(String.format("GCM Nonce / IV     : %s  (96-bit fresh random nonce)\n", lastEncryptedPacket.getIvBase64()));
            sb.append(String.format("Ciphertext + Tag   : %s\n", lastEncryptedPacket.getCiphertextBase64()));
            sb.append(String.format("Authentication Tag : 128-bit GHASH Galois MAC\n"));
            sb.append("------------------------------------------------------------\n");
            sb.append("WIRE TRANSMISSION FORMAT:\n");
            sb.append(lastEncryptedPacket.toWireString()).append("\n");

            txtEncryptedWire.setText(sb.toString());
            lblStatus.setText("Payload successfully encrypted with fresh 96-bit nonce.");
            lblStatus.setForeground(UITheme.SUCCESS_GREEN);
        } catch (Exception ex) {
            txtEncryptedWire.setText("ENCRYPTION ERROR: " + ex.getMessage());
        }
    }

    private void decryptAction(boolean simulateTamper) {
        EncryptedPacket packetToDecrypt = null;

        // Extract packet: check if user edited/pasted an ENC:... line in txtEncryptedWire
        String wireText = txtEncryptedWire.getText();
        if (wireText != null) {
            for (String line : wireText.split("\\r?\\n")) {
                if (line.trim().startsWith("ENC:")) {
                    try {
                        packetToDecrypt = EncryptedPacket.fromWireString(line.trim());
                        break;
                    } catch (Exception ignored) {}
                }
            }
        }

        if (packetToDecrypt == null) {
            packetToDecrypt = lastEncryptedPacket;
        }

        if (packetToDecrypt == null) {
            JOptionPane.showMessageDialog(this, "No encrypted data found to decrypt. Please encrypt a message first.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            if (simulateTamper) {
                packetToDecrypt = packetToDecrypt.createTamperedCopy();
                txtDecryptedOutput.setText("Attacker intercepted wire packet and flipped bit 0 of ciphertext!\nAttempting decryption at Banking Server...\n\n");
            }

            String recovered = decryptionService.decrypt(packetToDecrypt, masterKey);

            txtDecryptedOutput.setText("=== DECRYPTION & AUTHENTICATION SUCCESSFUL ===\n\n" +
                    "Galois/Counter Mode (GCM) verified the 128-bit authentication tag.\n" +
                    "No in-transit tampering or eavesdropping modification detected!\n\n" +
                    "RECOVERED ORIGINAL PLAINTEXT:\n" + recovered);
            lblStatus.setText("Decryption PASSED: Tag authenticated.");
            lblStatus.setForeground(UITheme.SUCCESS_GREEN);

        } catch (AEADBadTagException ex) {
            txtDecryptedOutput.setText("""
                ============================================================
                 [CRITICAL SECURITY ALERT] AUTHENTICATION TAG MISMATCH!
                ============================================================
                Exception Raised : javax.crypto.AEADBadTagException: Tag mismatch!
                
                Cryptographic Root Cause:
                In-transit data was modified or corrupted by an attacker or network noise.
                Because AES-GCM calculates a 128-bit GHASH authentication tag over both
                the plaintext and the IV, modifying even a single bit in the ciphertext
                mathematically breaks tag verification.
                
                Security Action:
                The Banking Server immediately dropped the packet and rejected the transaction!
                Zero fraudulent funds were transferred.
                """);
            lblStatus.setText("ATTACK DETECTED: Tampered ciphertext rejected by AEAD!");
            lblStatus.setForeground(UITheme.DANGER_RED);
        } catch (Exception ex) {
            txtDecryptedOutput.setText("DECRYPTION ERROR: " + ex.getMessage());
        }
    }

    private void testWrongKey() {
        if (lastEncryptedPacket == null) return;
        try {
            SecretKey wrongKey = EncryptionService.generateKey();
            decryptionService.decrypt(lastEncryptedPacket, wrongKey);
            txtDecryptedOutput.setText("Unintended: Wrong key somehow decrypted.");
        } catch (Exception ex) {
            txtDecryptedOutput.setText("""
                ============================================================
                 [UNAUTHORIZED KEY REJECTED]
                ============================================================
                An unauthorized device attempted to decrypt the banking message
                using an invalid secret key.
                
                Cipher Exception: """ + ex.getClass().getSimpleName() + "\n" +
                "The banking communication channel is completely opaque to unauthorized nodes.\n");
            lblStatus.setText("Wrong key rejected by cipher.");
            lblStatus.setForeground(UITheme.WARNING_AMBER);
        }
    }
}
