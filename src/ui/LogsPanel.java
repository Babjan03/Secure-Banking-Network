package ui;

import banking.BankingService;
import logging.NetworkLogEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Audit and Network Event Log Panel.
 */
public class LogsPanel extends JPanel {

    private final BankingService bankingService;
    private DefaultTableModel logModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public LogsPanel(BankingService bankingService) {
        this.bankingService = bankingService;

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
        refreshLogs();
    }

    private void initUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topBar.setBackground(UITheme.PANEL_BG);
        topBar.setBorder(BorderFactory.createLineBorder(UITheme.PANEL_BORDER, 1));

        JButton btnRefresh = UITheme.createPrimaryButton("Refresh Audit Logs");
        btnRefresh.addActionListener(e -> refreshLogs());
        topBar.add(btnRefresh);

        JButton btnClearFilter = UITheme.createSecondaryButton("Show All (Limit 100)");
        btnClearFilter.addActionListener(e -> refreshLogs());
        topBar.add(btnClearFilter);

        add(topBar, BorderLayout.NORTH);

        JPanel card = UITheme.createCardPanel("REAL-TIME NETWORK EVENT & AUDIT TRAIL");
        String[] cols = {"Log ID", "Timestamp", "Source Node", "Destination Node", "Protocol", "Event Description"};
        logModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(logModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(5).setPreferredWidth(450);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    public void refreshLogs() {
        logModel.setRowCount(0);
        List<NetworkLogEntry> logs = bankingService.getRepository().getRecentLogs(100);
        for (NetworkLogEntry l : logs) {
            logModel.addRow(new Object[]{
                    l.getLogId(),
                    dateFormat.format(new Date(l.getTimestamp())),
                    l.getSourceNode(),
                    l.getDestinationNode(),
                    l.getProtocol(),
                    l.getEventDescription()
            });
        }
    }
}
