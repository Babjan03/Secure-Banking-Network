package ui;

import error.CRC;

import javax.swing.*;
import java.awt.*;

/**
 * Interactive Cyclic Redundancy Check (CRC) Polynomial Binary Division & Error Detection UI.
 */
public class CRCPanel extends JPanel {

    private JTextField txtBinaryData;
    private JComboBox<String> cmbPolyPreset;
    private JTextField txtCustomPoly;
    private JTextArea txtDivisionTrace;
    private JLabel lblFcsResult;
    private CRC.CRCResult lastCrcResult;
    private JSpinner spErrorBit;

    public CRCPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        JPanel split = new JPanel(new GridLayout(1, 2, 15, 0));
        split.setBackground(UITheme.BG_DARK);

        // --- Left Panel: Input & Computation ---
        JPanel leftCard = UITheme.createCardPanel("CRC FRAME GENERATION & FCS CALCULATION");
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblData = new JLabel("Binary Data Bits (Dividend):");
        lblData.setForeground(UITheme.TEXT_PRIMARY);
        form.add(lblData, gbc);

        gbc.gridx = 1;
        txtBinaryData = UITheme.createTextField(14);
        txtBinaryData.setText("1101011011");
        form.add(txtBinaryData, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPoly = new JLabel("Generator Polynomial (Divisor):");
        lblPoly.setForeground(UITheme.TEXT_PRIMARY);
        form.add(lblPoly, gbc);

        gbc.gridx = 1;
        cmbPolyPreset = new JComboBox<>(new String[]{
                "10011 (CRC-4: x^4 + x + 1)",
                "1101 (CRC-3 Demo: x^3 + x^2 + 1)",
                "100000111 (ATM CRC-8 HEC)",
                "Custom Generator"
        });
        cmbPolyPreset.addActionListener(e -> updatePolyField());
        form.add(cmbPolyPreset, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblCust = new JLabel("Active Polynomial Bits:");
        lblCust.setForeground(UITheme.TEXT_PRIMARY);
        form.add(lblCust, gbc);

        gbc.gridx = 1;
        txtCustomPoly = UITheme.createTextField(14);
        txtCustomPoly.setText("10011");
        form.add(txtCustomPoly, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton btnCalculate = UITheme.createPrimaryButton("Perform Modulo-2 Division (Generate CRC)");
        btnCalculate.addActionListener(e -> computeCRC());
        form.add(btnCalculate, gbc);

        leftCard.add(form, BorderLayout.NORTH);

        JPanel fcsBox = new JPanel(new BorderLayout(5, 5));
        fcsBox.setBackground(UITheme.PANEL_BG);
        lblFcsResult = new JLabel("FCS: Click calculate to generate Frame Check Sequence.");
        lblFcsResult.setFont(UITheme.FONT_BOLD);
        lblFcsResult.setForeground(UITheme.ACCENT_CYAN);
        fcsBox.add(lblFcsResult, BorderLayout.NORTH);

        JTextArea explainArea = UITheme.createConsoleArea(10, 30);
        explainArea.setText("""
            === CRC MATHEMATICAL CONCEPTS ===
            
            1. Polynomial Representation:
               Bit string 10011 represents x^4 + x + 1.
            
            2. Appending Zeros:
               Degree r = poly length - 1.
               We append r zeros to data before division.
            
            3. Modulo-2 Division:
               Uses XOR addition (without carry).
               Remainder is the r-bit Frame Check Sequence (FCS).
            
            4. Transmitted Frame = Original Data + Remainder FCS.
            
            [CRITICAL DISTINCTION]
            CRC detects accidental corruption (thermal noise, bit errors).
            It does NOT protect against deliberate malicious tampering!
            (AES-GCM AEAD is required for cryptographic integrity).
            """);
        fcsBox.add(new JScrollPane(explainArea), BorderLayout.CENTER);
        leftCard.add(fcsBox, BorderLayout.CENTER);

        split.add(leftCard);

        // --- Right Panel: Verification & Error Injection ---
        JPanel rightCard = UITheme.createCardPanel("CRC VERIFICATION & BIT CORRUPTION SIMULATOR");

        JPanel verifyBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        verifyBar.setBackground(UITheme.PANEL_BG);

        JButton btnVerifyClean = UITheme.createPrimaryButton("Verify Clean Frame");
        btnVerifyClean.addActionListener(e -> verifyClean());
        verifyBar.add(btnVerifyClean);

        verifyBar.add(new JLabel("Flip Bit Index:"));
        spErrorBit = new JSpinner(new SpinnerNumberModel(4, 0, 32, 1));
        verifyBar.add(spErrorBit);

        JButton btnInjectError = UITheme.createSecondaryButton("Inject Bit Error & Verify");
        btnInjectError.addActionListener(e -> injectAndVerify());
        verifyBar.add(btnInjectError);

        rightCard.add(verifyBar, BorderLayout.NORTH);

        txtDivisionTrace = UITheme.createConsoleArea(18, 30);
        rightCard.add(new JScrollPane(txtDivisionTrace), BorderLayout.CENTER);

        split.add(rightCard);
        add(split, BorderLayout.CENTER);

        computeCRC();
    }

    private void updatePolyField() {
        int idx = cmbPolyPreset.getSelectedIndex();
        if (idx == 0) txtCustomPoly.setText("10011");
        else if (idx == 1) txtCustomPoly.setText("1101");
        else if (idx == 2) txtCustomPoly.setText("100000111");
    }

    private void computeCRC() {
        try {
            String data = txtBinaryData.getText().trim();
            String poly = txtCustomPoly.getText().trim();

            lastCrcResult = CRC.computeCRC(data, poly);

            lblFcsResult.setText(String.format("CRC Remainder: %s | Transmitted Frame: %s",
                    lastCrcResult.remainder, lastCrcResult.transmittedFrame));

            StringBuilder sb = new StringBuilder();
            sb.append("=== CRC MODULO-2 DIVISION EXECUTION TRACE ===\n");
            sb.append("Original Data Bits     : ").append(lastCrcResult.originalData).append("\n");
            sb.append("Generator Polynomial   : ").append(lastCrcResult.generatorPolynomial).append("\n");
            sb.append("Augmented Dividend     : ").append(lastCrcResult.augmentedData).append("\n");
            sb.append("FCS / Remainder Bits   : ").append(lastCrcResult.remainder).append("\n");
            sb.append("Full Transmitted Frame : ").append(lastCrcResult.transmittedFrame).append("\n");
            sb.append("------------------------------------------------------------\n");
            sb.append("Step-by-step XOR Modulo-2 shifts:\n");

            for (CRC.DivisionStep step : lastCrcResult.divisionSteps) {
                sb.append(String.format("  Step %2d | Operation: %-20s | Remainder: %s\n",
                        step.stepIndex, step.operation, step.currentRemainder));
            }

            txtDivisionTrace.setText(sb.toString());
        } catch (Exception ex) {
            txtDivisionTrace.setText("CRC ERROR: " + ex.getMessage());
        }
    }

    private void verifyClean() {
        if (lastCrcResult == null) return;
        CRC.VerificationResult res = CRC.verifyFrame(lastCrcResult.transmittedFrame, lastCrcResult.generatorPolynomial);

        StringBuilder sb = new StringBuilder();
        sb.append("=== RECEIVER CRC VERIFICATION (CLEAN TRANSMISSION) ===\n\n");
        sb.append("Received Frame     : ").append(res.receivedFrame).append("\n");
        sb.append("Generator Poly     : ").append(lastCrcResult.generatorPolynomial).append("\n");
        sb.append("Computed Remainder : ").append(res.remainder).append("\n\n");
        sb.append("VERIFICATION RESULT:\n").append(res.explanation).append("\n\n");
        sb.append("All syndrome bits are zero. Frame accepted by Layer 2 Data Link controller!");

        txtDivisionTrace.setText(sb.toString());
    }

    private void injectAndVerify() {
        if (lastCrcResult == null) return;
        try {
            int bitIdx = (Integer) spErrorBit.getValue();
            if (bitIdx >= lastCrcResult.transmittedFrame.length()) {
                bitIdx = lastCrcResult.transmittedFrame.length() - 1;
            }

            String corrupted = CRC.injectBitError(lastCrcResult.transmittedFrame, bitIdx);
            CRC.VerificationResult res = CRC.verifyFrame(corrupted, lastCrcResult.generatorPolynomial);

            StringBuilder sb = new StringBuilder();
            sb.append("=== RECEIVER CRC VERIFICATION (CORRUPTED TRANSMISSION) ===\n\n");
            sb.append("Original Frame     : ").append(lastCrcResult.transmittedFrame).append("\n");
            sb.append("Injected Error At  : Bit Index ").append(bitIdx).append("\n");
            sb.append("Corrupted Frame    : ").append(corrupted).append("\n");
            sb.append("Computed Remainder : ").append(res.remainder).append(" (Non-Zero!)\n\n");
            sb.append("VERIFICATION RESULT:\n").append(res.explanation).append("\n\n");
            sb.append("Action: The Data Link Layer detects corruption and silently discards the frame,\n");
            sb.append("forcing Layer 4 ARQ (Go-Back-N / TCP) to retransmit!");

            txtDivisionTrace.setText(sb.toString());
        } catch (Exception ex) {
            txtDivisionTrace.setText("ERROR: " + ex.getMessage());
        }
    }
}
