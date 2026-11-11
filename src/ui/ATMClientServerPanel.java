package ui;

import banking.BankingService;
import communication.TransactionMessage;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Interactive Live ATM Terminal & Multi-Threaded TCP Banking Server Demonstration Panel.
 * Features auto-disabling amount for balance inquiry and clean, uncomplicated banking receipts.
 */
public class ATMClientServerPanel extends JPanel {

    private final BankingService bankingService;
    private JComboBox<String> cmbClientNode;
    private JComboBox<String> cmbAccount;
    private JComboBox<TransactionMessage.Type> cmbType;
    private JTextField txtAmount;
    private JTextArea txtReceipt;
    private JLabel lblServerState;
    private JButton btnToggleServer;

    public ATMClientServerPanel(BankingService bankingService) {
        this.bankingService = bankingService;

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        // --- Top Server Daemon Controller Bar ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        topBar.setBackground(UITheme.PANEL_BG);
        topBar.setBorder(BorderFactory.createLineBorder(UITheme.PANEL_BORDER, 1));

        JLabel lblServerTitle = new JLabel("Central Banking Server Daemon (TCP Port 9090):");
        lblServerTitle.setFont(UITheme.FONT_BOLD);
        lblServerTitle.setForeground(UITheme.TEXT_PRIMARY);
        topBar.add(lblServerTitle);

        lblServerState = new JLabel("RUNNING");
        lblServerState.setFont(UITheme.FONT_BOLD);
        lblServerState.setForeground(UITheme.SUCCESS_GREEN);
        topBar.add(lblServerState);

        btnToggleServer = UITheme.createPrimaryButton("Restart Server");
        btnToggleServer.addActionListener(e -> toggleServer());
        topBar.add(btnToggleServer);

        add(topBar, BorderLayout.NORTH);

        // --- Center Split ---
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 15, 0));
        centerGrid.setBackground(UITheme.BG_DARK);

        // Left Panel: ATM Terminal Client Interface
        JPanel atmCard = UITheme.createCardPanel("ATM CLIENT TRANSACTION DISPATCHER");
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblClient = new JLabel("Select Client Terminal:");
        lblClient.setForeground(UITheme.TEXT_PRIMARY);
        form.add(lblClient, gbc);

        gbc.gridx = 1;
        cmbClientNode = new JComboBox<>(new String[]{"ATM-01 (Retail Branch A)", "ATM-02 (Drive-Through)", "CUST-01 (Customer Desk)"});
        form.add(cmbClientNode, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblAcc = new JLabel("Target Bank Account:");
        lblAcc.setForeground(UITheme.TEXT_PRIMARY);
        form.add(lblAcc, gbc);

        gbc.gridx = 1;
        cmbAccount = new JComboBox<>(new String[]{
                "ACC-100234 (Alice Sharma - Savings)",
                "ACC-500891 (Rajesh Patel - Current)",
                "ACC-300452 (Priya Nair - Savings)",
                "ACC-700912 (Vikram Singh - Savings)"
        });
        form.add(cmbAccount, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblT = new JLabel("Transaction Type:");
        lblT.setForeground(UITheme.TEXT_PRIMARY);
        form.add(lblT, gbc);

        gbc.gridx = 1;
        cmbType = new JComboBox<>(TransactionMessage.Type.values());
        cmbType.setSelectedItem(TransactionMessage.Type.WITHDRAW);
        cmbType.addActionListener(e -> updateAmountFieldState());
        form.add(cmbType, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblAmt = new JLabel("Transaction Amount (₹):");
        lblAmt.setForeground(UITheme.TEXT_PRIMARY);
        form.add(lblAmt, gbc);

        gbc.gridx = 1;
        txtAmount = UITheme.createTextField(10);
        txtAmount.setText("2000.00");
        form.add(txtAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton btnSubmit = UITheme.createPrimaryButton("Transmit Encrypted Transaction over TCP");
        btnSubmit.addActionListener(e -> executeTransaction());
        form.add(btnSubmit, gbc);

        atmCard.add(form, BorderLayout.NORTH);

        JTextArea infoText = UITheme.createConsoleArea(10, 30);
        infoText.setText("""
            === REAL-WORLD WIRESHARK READY TCP FLOW ===
            
            1. Client opens java.net.Socket to 127.0.0.1:9090.
            2. TCP 3-Way Handshake generates actual SYN -> SYN-ACK -> ACK packets.
            3. Length-Prefixed framing encapsulates the AES-256-GCM ciphertext.
            4. ServerSocket accepts connection in a worker thread.
            5. Decrypted transaction validates against SQLite ACID database.
            6. Encrypted response is returned over the active TCP stream.
            """);
        atmCard.add(new JScrollPane(infoText), BorderLayout.CENTER);

        centerGrid.add(atmCard);

        // Right Panel: Clean, Uncomplicated Transaction Receipt & Status
        JPanel receiptCard = UITheme.createCardPanel("TRANSACTION RECEIPT & STATUS");
        txtReceipt = UITheme.createConsoleArea(18, 30);
        txtReceipt.setFont(new Font("Consolas", Font.BOLD, 13));
        txtReceipt.setText("\n  Select transaction type and click 'Transmit Encrypted Transaction'\n  to view customer receipt here.");
        receiptCard.add(new JScrollPane(txtReceipt), BorderLayout.CENTER);

        centerGrid.add(receiptCard);
        add(centerGrid, BorderLayout.CENTER);

        updateServerLabel();
        updateAmountFieldState();
    }

    private void updateAmountFieldState() {
        TransactionMessage.Type selected = (TransactionMessage.Type) cmbType.getSelectedItem();
        if (selected == TransactionMessage.Type.BALANCE_INQUIRY) {
            txtAmount.setText("0.00");
            txtAmount.setEnabled(false);
            txtAmount.setEditable(false);
            txtAmount.setBackground(new Color(25, 33, 50));
        } else {
            if ("0.00".equals(txtAmount.getText().trim()) || txtAmount.getText().trim().isEmpty()) {
                txtAmount.setText("2000.00");
            }
            txtAmount.setEnabled(true);
            txtAmount.setEditable(true);
            txtAmount.setBackground(UITheme.INPUT_BG);
        }
    }

    private void toggleServer() {
        try {
            if (bankingService.getBankingServer().isRunning()) {
                bankingService.stopServer();
            } else {
                bankingService.startServer();
            }
            updateServerLabel();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Server toggle failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateServerLabel() {
        boolean running = bankingService.getBankingServer().isRunning();
        lblServerState.setText(running ? "RUNNING (PORT 9090)" : "STOPPED");
        lblServerState.setForeground(running ? UITheme.SUCCESS_GREEN : UITheme.DANGER_RED);
        btnToggleServer.setText(running ? "Stop Server" : "Start Server");
    }

    private void executeTransaction() {
        try {
            String clientSel = (String) cmbClientNode.getSelectedItem();
            String clientId = clientSel.split(" ")[0];

            String accSel = (String) cmbAccount.getSelectedItem();
            String accountNumber = accSel.split(" ")[0];

            TransactionMessage.Type type = (TransactionMessage.Type) cmbType.getSelectedItem();
            double amount = 0.0;

            if (type != TransactionMessage.Type.BALANCE_INQUIRY) {
                try {
                    amount = Double.parseDouble(txtAmount.getText().trim());
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(this, "Transaction amount must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid numeric transaction amount.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            BankingService.EndToEndResult result = bankingService.executeAtmTransaction(clientId, accountNumber, type, amount);

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            StringBuilder sb = new StringBuilder();
            sb.append("============================================================\n");
            sb.append("                  BANK TRANSACTION RECEIPT                  \n");
            sb.append("============================================================\n\n");

            if (result.success) {
                sb.append("  TRANSACTION STATUS : [ SUCCESS / APPROVED ]\n");
            } else {
                sb.append("  TRANSACTION STATUS : [ FAILED / DECLINED ]\n");
            }

            sb.append(String.format("  TRANSACTION TYPE   : %s\n", type.name()));
            sb.append(String.format("  ACCOUNT TARGET     : %s\n", accSel));
            sb.append(String.format("  CLIENT TERMINAL    : %s\n", clientSel));
            sb.append("------------------------------------------------------------\n");

            if (type == TransactionMessage.Type.DEPOSIT) {
                sb.append(String.format("  AMOUNT DEPOSITED   : ₹%,.2f\n", amount));
                if (result.response != null) {
                    sb.append(String.format("  CURRENT BALANCE    : ₹%,.2f\n", result.response.getRemainingBalance()));
                }
            } else if (type == TransactionMessage.Type.WITHDRAW) {
                sb.append(String.format("  AMOUNT WITHDRAWN   : ₹%,.2f\n", amount));
                if (result.response != null) {
                    sb.append(String.format("  CURRENT BALANCE    : ₹%,.2f\n", result.response.getRemainingBalance()));
                }
            } else {
                // Balance Inquiry
                if (result.response != null) {
                    sb.append(String.format("  CURRENT BALANCE    : ₹%,.2f\n", result.response.getRemainingBalance()));
                }
            }

            sb.append("------------------------------------------------------------\n");
            if (result.response != null) {
                sb.append(String.format("  SERVER RESPONSE    : %s\n", result.response.getMessage()));
                sb.append(String.format("  RESPONSE CODE      : %s\n", result.response.getResponseCode()));
                sb.append(String.format("  TRANSACTION REF ID : %s\n", result.response.getRequestId()));
            } else {
                sb.append(String.format("  ERROR DETAILS      : %s\n", result.statusMessage));
            }

            sb.append(String.format("  SECURITY PROTOCOL  : AES-256-GCM Encrypted (Port 9090)\n"));
            sb.append(String.format("  TIMESTAMP          : %s\n", timeStamp));
            sb.append("============================================================\n");

            txtReceipt.setText(sb.toString());

        } catch (Exception ex) {
            txtReceipt.setText("\nTRANSACTION FAILURE: " + ex.getMessage() + "\n");
        }
    }
}
