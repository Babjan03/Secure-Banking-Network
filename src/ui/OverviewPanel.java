package ui;

import banking.BankingService;
import banking.Customer;
import banking.TransactionRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Executive Overview Dashboard displaying system health, key metrics,
 * active routing summary, and real-time transaction ledger.
 */
public class OverviewPanel extends JPanel {

    private final BankingService bankingService;
    private JLabel lblTotalNodes;
    private JLabel lblActiveRouters;
    private JLabel lblServerStatus;
    private JLabel lblTxCount;
    private DefaultTableModel txTableModel;

    public OverviewPanel(BankingService bankingService) {
        this.bankingService = bankingService;
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
        refreshData();
    }

    private void initUI() {
        // 1. Top Metrics Bar
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        metricsPanel.setBackground(UITheme.BG_DARK);

        lblTotalNodes = new JLabel("10", SwingConstants.CENTER);
        lblActiveRouters = new JLabel("3 / 3", SwingConstants.CENTER);
        lblServerStatus = new JLabel("ONLINE (9090)", SwingConstants.CENTER);
        lblTxCount = new JLabel("0", SwingConstants.CENTER);

        metricsPanel.add(createMetricCard("TOTAL NETWORK DEVICES", lblTotalNodes, UITheme.ACCENT_CYAN));
        metricsPanel.add(createMetricCard("ACTIVE ROUTERS", lblActiveRouters, UITheme.SUCCESS_GREEN));
        metricsPanel.add(createMetricCard("TCP BANKING SERVER", lblServerStatus, UITheme.WARNING_AMBER));
        metricsPanel.add(createMetricCard("LEDGER TRANSACTIONS", lblTxCount, UITheme.ACCENT_BLUE));

        add(metricsPanel, BorderLayout.NORTH);

        // 2. Center Content Split
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBackground(UITheme.BG_DARK);

        // Left Card: Architecture Summary
        JPanel archCard = UITheme.createCardPanel("BANKING NETWORK INFRASTRUCTURE");
        JTextArea archText = UITheme.createConsoleArea(14, 30);
        archText.setText("""
            === SIMULATED MULTI-TIER BANKING NETWORK ===
            
            [TIER 1: RETAIL BRANCH CHANNELS]
              - ATM-01 : Retail Branch A Cash Dispenser (192.168.1.10/24)
              - ATM-02 : Drive-Through Terminal (192.168.1.20/24)
              - CUST-01: Customer Service Workstation (192.168.2.10/24)
              - RTR-BR1: Branch Gateway Router (192.168.1.1, 10.0.1.1)
            
            [TIER 2: BRANCH OPERATIONS & LOCAL APP]
              - MGR-01 : Branch Manager System (192.168.3.10/24)
              - SRV-BR1: Local Application Server (192.168.4.10/24)
              - RTR-BR2: Branch Operations Router (192.168.3.1, 10.0.2.1)
            
            [TIER 3: HEAD OFFICE DATA CENTER & LEDGER]
              - RTR-HQ : Core Enterprise Router (192.168.5.1, 10.0.3.1)
              - SRV-HQ : Central Banking Server (192.168.5.10/24)
              - DB-HQ  : ACID Ledger Database Server (192.168.6.10/24)
            
            Security: 256-Bit AES Galois/Counter Mode (GCM)
            Transport: Java Streaming Sockets with Length-Prefixed Framing
            Routing : Dynamic Dijkstra Shortest Path + Bellman-Ford DV
            """);
        archCard.add(new JScrollPane(archText), BorderLayout.CENTER);
        centerPanel.add(archCard);

        // Right Card: Recent Transactions Table
        JPanel txCard = UITheme.createCardPanel("RECENT FINANCIAL TRANSACTIONS");
        String[] cols = {"Tx ID", "Account", "Type", "Amount", "Status"};
        txTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable txTable = new JTable(txTableModel);
        UITheme.styleTable(txTable);
        txCard.add(new JScrollPane(txTable), BorderLayout.CENTER);

        JButton btnRefresh = UITheme.createSecondaryButton("Refresh Overview Data");
        btnRefresh.addActionListener(e -> refreshData());
        txCard.add(btnRefresh, BorderLayout.SOUTH);

        centerPanel.add(txCard);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(UITheme.PANEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PANEL_BORDER, 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(UITheme.TEXT_SECONDARY);

        valueLabel.setFont(UITheme.FONT_METRIC);
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        int totalNodes = bankingService.getTopology().getNodes().size();
        lblTotalNodes.setText(String.valueOf(totalNodes));

        lblServerStatus.setText(bankingService.getBankingServer().isRunning() ? "ONLINE (9090)" : "STOPPED");
        lblServerStatus.setForeground(bankingService.getBankingServer().isRunning() ? UITheme.SUCCESS_GREEN : UITheme.DANGER_RED);

        List<TransactionRecord> recent = bankingService.getRepository().getRecentTransactions(20);
        lblTxCount.setText(String.valueOf(recent.size()));

        txTableModel.setRowCount(0);
        for (TransactionRecord tx : recent) {
            txTableModel.addRow(new Object[]{
                    tx.getTransactionId(),
                    tx.getAccountNumber(),
                    tx.getTransactionType(),
                    String.format("₹%.2f", tx.getAmount()),
                    tx.getStatus()
            });
        }
    }
}
