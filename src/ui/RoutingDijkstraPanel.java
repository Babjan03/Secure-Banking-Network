package ui;

import network.NetworkLink;
import network.NetworkTopology;
import routing.Dijkstra;
import routing.RouteResult;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactive Dijkstra's Shortest Path Routing Demonstration Panel.
 */
public class RoutingDijkstraPanel extends JPanel {

    private final NetworkTopology topology;
    private JComboBox<String> cmbSource;
    private JComboBox<String> cmbDestination;
    private JTextArea txtRouteOutput;
    private JComboBox<String> cmbLinkFail;
    private JLabel lblStatusBadge;

    public RoutingDijkstraPanel(NetworkTopology topology) {
        this.topology = topology;
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        JPanel split = new JPanel(new GridLayout(1, 2, 15, 0));
        split.setBackground(UITheme.BG_DARK);

        // --- Left Panel: Path Configuration & Execution ---
        JPanel leftCard = UITheme.createCardPanel("DIJKSTRA SHORTEST PATH CALCULATION");
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        List<String> nodeIds = new ArrayList<>(topology.getNodes().keySet());

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblSrc = new JLabel("Source Node:");
        lblSrc.setForeground(UITheme.TEXT_PRIMARY);
        form.add(lblSrc, gbc);

        gbc.gridx = 1;
        cmbSource = new JComboBox<>(nodeIds.toArray(new String[0]));
        cmbSource.setSelectedItem("ATM-01");
        form.add(cmbSource, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblDst = new JLabel("Destination Node:");
        lblDst.setForeground(UITheme.TEXT_PRIMARY);
        form.add(lblDst, gbc);

        gbc.gridx = 1;
        cmbDestination = new JComboBox<>(nodeIds.toArray(new String[0]));
        cmbDestination.setSelectedItem("SRV-HQ");
        form.add(cmbDestination, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton btnCalculate = UITheme.createPrimaryButton("Run Dijkstra Shortest Path Algorithm");
        btnCalculate.addActionListener(e -> calculatePath());
        form.add(btnCalculate, gbc);

        // Simulation Link Failure Section
        gbc.gridy = 3;
        JLabel lblSim = new JLabel("--- DYNAMIC FAILOVER SIMULATION ---");
        lblSim.setFont(UITheme.FONT_BOLD);
        lblSim.setForeground(UITheme.ACCENT_CYAN);
        form.add(lblSim, gbc);

        gbc.gridy = 4;
        List<String> linkNames = new ArrayList<>();
        for (NetworkLink l : topology.getLinks().values()) {
            linkNames.add(l.getId() + " (" + l.getSourceNodeId() + " <-> " + l.getTargetNodeId() + ")");
        }
        cmbLinkFail = new JComboBox<>(linkNames.toArray(new String[0]));
        cmbLinkFail.setSelectedItem("L_RTR2_HQ (RTR-BR2 <-> RTR-HQ)");
        form.add(cmbLinkFail, gbc);

        gbc.gridy = 5;
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnBar.setBackground(UITheme.PANEL_BG);

        JButton btnFail = UITheme.createSecondaryButton("Sever Link (Simulate Cut)");
        btnFail.addActionListener(e -> setLinkState(false));
        btnBar.add(btnFail);

        JButton btnRestore = UITheme.createSecondaryButton("Restore Link");
        btnRestore.addActionListener(e -> setLinkState(true));
        btnBar.add(btnRestore);

        form.add(btnBar, gbc);

        leftCard.add(form, BorderLayout.NORTH);

        lblStatusBadge = new JLabel("Status: Ready to compute shortest path.");
        lblStatusBadge.setFont(UITheme.FONT_BOLD);
        lblStatusBadge.setForeground(UITheme.TEXT_SECONDARY);
        leftCard.add(lblStatusBadge, BorderLayout.SOUTH);

        split.add(leftCard);

        // --- Right Panel: Computed Path Results & Trace ---
        JPanel rightCard = UITheme.createCardPanel("ROUTING PATH TRACE & LINK METRICS");
        txtRouteOutput = UITheme.createConsoleArea(18, 30);
        rightCard.add(new JScrollPane(txtRouteOutput), BorderLayout.CENTER);

        split.add(rightCard);
        add(split, BorderLayout.CENTER);

        calculatePath();
    }

    private void setLinkState(boolean active) {
        String selected = (String) cmbLinkFail.getSelectedItem();
        if (selected != null) {
            String linkId = selected.split(" ")[0];
            NetworkLink link = topology.getLinks().get(linkId);
            if (link != null) {
                link.setActive(active);
                lblStatusBadge.setText("Link " + linkId + " is now " + (active ? "ACTIVE (RESTORED)" : "DOWN (CUT)"));
                lblStatusBadge.setForeground(active ? UITheme.SUCCESS_GREEN : UITheme.DANGER_RED);
                calculatePath();
            }
        }
    }

    private void calculatePath() {
        String src = (String) cmbSource.getSelectedItem();
        String dst = (String) cmbDestination.getSelectedItem();
        if (src == null || dst == null) return;

        RouteResult res = Dijkstra.findShortestPath(topology, src, dst);

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append(String.format(" DIJKSTRA LINK STATE SHORTEST PATH REPORT\n"));
        sb.append(String.format(" From: %-15s To: %-15s\n", src, dst));
        sb.append("============================================================\n\n");

        if (!res.isReachable()) {
            sb.append(" [!] ROUTING FAILURE: Destination is UNREACHABLE.\n");
            sb.append("     No path exists across active links in the topology.\n");
            txtRouteOutput.setText(sb.toString());
            return;
        }

        sb.append(String.format(" TOTAL ROUTE METRIC COST : %d\n", res.getTotalCost()));
        sb.append(String.format(" NUMBER OF HOPS          : %d\n\n", res.getHopCount()));
        sb.append(" SEQUENCE OF FORWARDING HOPS:\n");
        sb.append(" ------------------------------------------------------------\n");

        List<String> path = res.getPathNodes();
        int cumulativeCost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String u = path.get(i);
            String v = path.get(i + 1);
            NetworkLink link = topology.getLinkBetween(u, v);
            int cost = link != null ? link.getCost() : 0;
            cumulativeCost += cost;

            sb.append(String.format("  Hop %d: %-10s ---> %-10s [Link Cost: %-2d | Cumul: %-2d | BW: %d Mbps | Latency: %d ms]\n",
                    i + 1, u, v, cost, cumulativeCost,
                    link != null ? link.getBandwidthMbps() : 0,
                    link != null ? link.getLatencyMs() : 0));
        }

        sb.append(" ------------------------------------------------------------\n");
        sb.append("\n VISUAL PATH FLOW:\n ");
        sb.append(res.getFormattedPath());
        sb.append("\n\n [ACADEMIC NOTE]\n");
        sb.append(" Dijkstra is an O((V + E) log V) Link-State algorithm requiring\n");
        sb.append(" full global network topology knowledge (e.g. OSPF / IS-IS).\n");

        txtRouteOutput.setText(sb.toString());
    }
}
