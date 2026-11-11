package ui;

import routing.DistanceVectorRouter;
import routing.DistanceVectorSimulation;
import routing.RoutingTableEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Interactive Distance Vector (Bellman-Ford) Simulation and Routing Table Visualization Panel.
 */
public class RoutingDVPanel extends JPanel {

    private final DistanceVectorSimulation simulation;
    private JLabel lblRound;
    private JLabel lblConvergenceStatus;
    private JComboBox<String> cmbRouterSelect;
    private DefaultTableModel tableModel;
    private JTextArea txtComparison;

    public RoutingDVPanel(DistanceVectorSimulation simulation) {
        this.simulation = simulation;
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        // --- Top Control Bar ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        topBar.setBackground(UITheme.PANEL_BG);
        topBar.setBorder(BorderFactory.createLineBorder(UITheme.PANEL_BORDER, 1));

        JButton btnStep = UITheme.createPrimaryButton("Step Next Round (Exchange Vectors)");
        btnStep.addActionListener(e -> stepRound());
        topBar.add(btnStep);

        JButton btnRunAll = UITheme.createSecondaryButton("Run to Full Convergence");
        btnRunAll.addActionListener(e -> runAll());
        topBar.add(btnRunAll);

        JButton btnReset = UITheme.createSecondaryButton("Reset Simulation");
        btnReset.addActionListener(e -> resetSim());
        topBar.add(btnReset);

        lblRound = new JLabel("Current Iteration: Round 0");
        lblRound.setFont(UITheme.FONT_BOLD);
        lblRound.setForeground(UITheme.ACCENT_CYAN);
        topBar.add(lblRound);

        lblConvergenceStatus = new JLabel("[NOT CONVERGED]");
        lblConvergenceStatus.setFont(UITheme.FONT_BOLD);
        lblConvergenceStatus.setForeground(UITheme.WARNING_AMBER);
        topBar.add(lblConvergenceStatus);

        add(topBar, BorderLayout.NORTH);

        // --- Center Split ---
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 15, 0));
        centerGrid.setBackground(UITheme.BG_DARK);

        // Left Panel: Selected Router Routing Table
        JPanel tableCard = UITheme.createCardPanel("ROUTER DISTANCE VECTOR TABLE");
        JPanel selectorBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        selectorBar.setBackground(UITheme.PANEL_BG);
        selectorBar.add(new JLabel("Inspect Router:"));
        cmbRouterSelect = new JComboBox<>(new String[]{"RTR-BR1", "RTR-BR2", "RTR-HQ", "ATM-01", "SRV-HQ", "DB-HQ"});
        cmbRouterSelect.addActionListener(e -> updateTableDisplay());
        selectorBar.add(cmbRouterSelect);
        tableCard.add(selectorBar, BorderLayout.NORTH);

        String[] cols = {"Destination Node", "Metric Cost", "Next Hop Router"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        centerGrid.add(tableCard);

        // Right Panel: Academic Comparison (Link State vs Distance Vector)
        JPanel compCard = UITheme.createCardPanel("LINK STATE VS DISTANCE VECTOR (ACADEMIC VIVA)");
        txtComparison = UITheme.createConsoleArea(18, 30);
        txtComparison.setText("""
            ============================================================
             LINK STATE (DIJKSTRA) vs DISTANCE VECTOR (BELLMAN-FORD)
            ============================================================
            
            1. TOPOLOGY KNOWLEDGE:
               - Link State: Every node possesses the complete, identical
                 global network graph (Link-State Advertisements / LSAs).
               - Distance Vector: Nodes only know direct neighbor link costs
                 and share distance estimates ("Routing by rumor").
            
            2. BELLMAN-FORD MATHEMATICAL UPDATE:
               - D_x(y) = min_v { c(x,v) + D_v(y) }
               - Distance from router x to destination y is minimized over
                 all active neighbors v, adding cost c(x,v) to v's distance.
            
            3. CONVERGENCE SPEED & OVERHEAD:
               - Link State: Fast convergence, high CPU/memory overhead.
               - Distance Vector: Slower iterative convergence, susceptible
                 to count-to-infinity / routing loops without split-horizon.
            
            4. REAL-WORLD BANKING WAN PROTOCOLS:
               - Link State : OSPF (Interior Gateway Protocol in Banks)
               - Distance Vector: RIP / BGP (Path-Vector on the Internet)
            """);
        compCard.add(new JScrollPane(txtComparison), BorderLayout.CENTER);

        centerGrid.add(compCard);
        add(centerGrid, BorderLayout.CENTER);

        updateTableDisplay();
    }

    private void stepRound() {
        boolean conv = simulation.step();
        updateLabels(conv);
        updateTableDisplay();
    }

    private void runAll() {
        int rounds = simulation.runToConvergence(15);
        updateLabels(simulation.isConverged());
        updateTableDisplay();
    }

    private void resetSim() {
        simulation.reset();
        updateLabels(false);
        updateTableDisplay();
    }

    private void updateLabels(boolean conv) {
        lblRound.setText("Current Iteration: Round " + simulation.getCurrentIteration());
        if (conv) {
            lblConvergenceStatus.setText("[CONVERGED SUCCESSFULLY]");
            lblConvergenceStatus.setForeground(UITheme.SUCCESS_GREEN);
        } else {
            lblConvergenceStatus.setText("[ROUTING INFORMATION PROPAGATING]");
            lblConvergenceStatus.setForeground(UITheme.WARNING_AMBER);
        }
    }

    private void updateTableDisplay() {
        String selectedRouter = (String) cmbRouterSelect.getSelectedItem();
        if (selectedRouter == null) return;

        DistanceVectorRouter router = simulation.getRouter(selectedRouter);
        tableModel.setRowCount(0);

        if (router != null) {
            for (Map.Entry<String, RoutingTableEntry> entry : router.getRoutingTable().getEntries().entrySet()) {
                RoutingTableEntry e = entry.getValue();
                tableModel.addRow(new Object[]{
                        e.getDestination(),
                        e.getCost() >= RoutingTableEntry.INFINITY ? "INFINITY" : e.getCost(),
                        e.getNextHop() != null ? e.getNextHop() : "DIRECT"
                });
            }
        }
    }
}
