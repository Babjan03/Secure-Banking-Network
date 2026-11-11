package ui;

import banking.BankingService;
import banking.Customer;
import banking.TransactionRecord;
import database.BankingRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Interactive SQLite Database Ledger, Accounts, and Balance Management UI.
 */
public class DatabasePanel extends JPanel {

    private final BankingService bankingService;
    private DefaultTableModel custModel;
    private DefaultTableModel txModel;
    private JComboBox<String> cmbAccSelect;
    private JTextField txtAmt;
    private JComboBox<String> cmbOp;

    public DatabasePanel(BankingService bankingService) {
        this.bankingService = bankingService;

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
        refreshTables();
    }

    private void initUI() {
        // --- Top Quick Action Form ---
        JPanel topCard = UITheme.createCardPanel("ACID TRANSACTION LEDGER DIRECT OPERATION");
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        form.setBackground(UITheme.PANEL_BG);

        form.add(new JLabel("Target Account:"));
        cmbAccSelect = new JComboBox<>(new String[]{
                "ACC-100234 (Alice Sharma)",
                "ACC-500891 (Rajesh Patel)",
                "ACC-300452 (Priya Nair)",
                "ACC-700912 (Vikram Singh)"
        });
        form.add(cmbAccSelect);

        form.add(new JLabel("Operation:"));
        cmbOp = new JComboBox<>(new String[]{"WITHDRAW", "DEPOSIT"});
        form.add(cmbOp);

        form.add(new JLabel("Amount (₹):"));
        txtAmt = UITheme.createTextField(8);
        txtAmt.setText("1000.00");
        form.add(txtAmt);

        JButton btnExec = UITheme.createPrimaryButton("Commit Transaction");
        btnExec.addActionListener(e -> executeDirectTx());
        form.add(btnExec);

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh Tables");
        btnRefresh.addActionListener(e -> refreshTables());
        form.add(btnRefresh);

        topCard.add(form, BorderLayout.CENTER);
        add(topCard, BorderLayout.NORTH);

        // --- Center Split: Customers Table & Transactions Table ---
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 15, 0));
        centerGrid.setBackground(UITheme.BG_DARK);

        // Left: Customers Table
        JPanel custCard = UITheme.createCardPanel("CUSTOMER BANKING ACCOUNTS (SQLITE)");
        String[] custCols = {"Cust ID", "Customer Name", "Account Number", "Type", "Current Balance"};
        custModel = new DefaultTableModel(custCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable custTable = new JTable(custModel);
        UITheme.styleTable(custTable);
        custCard.add(new JScrollPane(custTable), BorderLayout.CENTER);
        centerGrid.add(custCard);

        // Right: Transactions Table
        JPanel txCard = UITheme.createCardPanel("TRANSACTION AUDIT LEDGER (ACID COMMITTED)");
        String[] txCols = {"Tx ID", "Account Number", "Type", "Amount", "Status"};
        txModel = new DefaultTableModel(txCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable txTable = new JTable(txModel);
        UITheme.styleTable(txTable);
        txCard.add(new JScrollPane(txTable), BorderLayout.CENTER);
        centerGrid.add(txCard);

        add(centerGrid, BorderLayout.CENTER);
    }

    private void executeDirectTx() {
        try {
            String accSel = (String) cmbAccSelect.getSelectedItem();
            String acc = accSel.split(" ")[0];
            String op = (String) cmbOp.getSelectedItem();
            double amt = Double.parseDouble(txtAmt.getText().trim());

            BankingRepository.TransactionResult res;
            if ("WITHDRAW".equals(op)) {
                res = bankingService.getRepository().processWithdrawal(acc, amt, "DIRECT_UI");
            } else {
                res = bankingService.getRepository().processDeposit(acc, amt, "DIRECT_UI");
            }

            if (res.success) {
                JOptionPane.showMessageDialog(this,
                        String.format("Transaction Successful!\nNew Balance: ₹%.2f\nTx ID: %s", res.newBalance, res.transactionId),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        String.format("Transaction Failed!\nReason: %s\nCode: %s", res.message, res.responseCode),
                        "Transaction Declined", JOptionPane.WARNING_MESSAGE);
            }

            refreshTables();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTables() {
        custModel.setRowCount(0);
        List<Customer> customers = bankingService.getRepository().getAllCustomers();
        for (Customer c : customers) {
            custModel.addRow(new Object[]{
                    c.getCustomerId(),
                    c.getName(),
                    c.getAccountNumber(),
                    c.getAccountType(),
                    String.format("₹%,.2f", c.getBalance())
            });
        }

        txModel.setRowCount(0);
        List<TransactionRecord> txs = bankingService.getRepository().getRecentTransactions(30);
        for (TransactionRecord tx : txs) {
            txModel.addRow(new Object[]{
                    tx.getTransactionId(),
                    tx.getAccountNumber(),
                    tx.getTransactionType(),
                    String.format("₹%,.2f", tx.getAmount()),
                    tx.getStatus()
            });
        }
    }
}
