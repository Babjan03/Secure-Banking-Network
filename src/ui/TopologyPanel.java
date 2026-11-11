package ui;

import network.NetworkLink;
import network.NetworkNode;
import network.NetworkTopology;
import network.NodeType;
import routing.Dijkstra;
import routing.RouteResult;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

/**
 * Interactive visual canvas rendering the banking network topology:
 * nodes, routers, links, link costs, link failure toggles, and dynamic shortest path highlighting.
 */
public class TopologyPanel extends JPanel {

    private final NetworkTopology topology;
    private final Map<String, Point> nodePositions;
    private List<String> highlightedPath;
    private JComboBox<String> linkSelector;
    private JButton btnToggleLink;
    private JLabel lblRouteInfo;

    public TopologyPanel(NetworkTopology topology) {
        this.topology = topology;
        this.nodePositions = new HashMap<>();
        this.highlightedPath = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initNodePositions();
        initControls();
    }

    private void initNodePositions() {
        // Layout 10 nodes across canvas coordinates (relative to 900x520 default canvas)
        nodePositions.put("ATM-01",   new Point(100, 90));
        nodePositions.put("ATM-02",   new Point(100, 210));
        nodePositions.put("CUST-01",  new Point(100, 330));
        nodePositions.put("RTR-BR1",  new Point(280, 210));

        nodePositions.put("MGR-01",   new Point(420, 80));
        nodePositions.put("SRV-BR1",  new Point(420, 380));
        nodePositions.put("RTR-BR2",  new Point(490, 210));

        nodePositions.put("RTR-HQ",   new Point(700, 210));
        nodePositions.put("SRV-HQ",   new Point(850, 120));
        nodePositions.put("DB-HQ",    new Point(850, 300));
    }

    private void initControls() {
        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        controlBar.setBackground(UITheme.PANEL_BG);
        controlBar.setBorder(BorderFactory.createLineBorder(UITheme.PANEL_BORDER, 1));

        JLabel lblLink = new JLabel("Select Link to Toggle (Simulate Cut):");
        lblLink.setFont(UITheme.FONT_BOLD);
        lblLink.setForeground(UITheme.TEXT_PRIMARY);
        controlBar.add(lblLink);

        linkSelector = new JComboBox<>();
        linkSelector.setFont(UITheme.FONT_BODY);
        for (NetworkLink link : topology.getLinks().values()) {
            linkSelector.addItem(link.getId() + " (" + link.getSourceNodeId() + " <-> " + link.getTargetNodeId() + ")");
        }
        controlBar.add(linkSelector);

        btnToggleLink = UITheme.createPrimaryButton("Toggle Link UP/DOWN");
        btnToggleLink.addActionListener(e -> toggleSelectedLink());
        controlBar.add(btnToggleLink);

        JButton btnTestRoute = UITheme.createSecondaryButton("Trace Route: ATM-01 -> SRV-HQ");
        btnTestRoute.addActionListener(e -> traceAtmRoute());
        controlBar.add(btnTestRoute);

        JButton btnResetLinks = UITheme.createSecondaryButton("Restore All Links");
        btnResetLinks.addActionListener(e -> restoreAllLinks());
        controlBar.add(btnResetLinks);

        lblRouteInfo = new JLabel("Route: Default Shortest Path active");
        lblRouteInfo.setFont(UITheme.FONT_BOLD);
        lblRouteInfo.setForeground(UITheme.ACCENT_CYAN);
        controlBar.add(lblRouteInfo);

        add(controlBar, BorderLayout.NORTH);

        // Center visual canvas
        CanvasPanel canvas = new CanvasPanel();
        add(canvas, BorderLayout.CENTER);

        traceAtmRoute(); // Initial highlight
    }

    private void toggleSelectedLink() {
        int idx = linkSelector.getSelectedIndex();
        if (idx >= 0) {
            String selected = (String) linkSelector.getSelectedItem();
            String linkId = selected.split(" ")[0];
            NetworkLink link = topology.getLinks().get(linkId);
            if (link != null) {
                link.setActive(!link.isActive());
                traceAtmRoute();
                repaint();
            }
        }
    }

    private void restoreAllLinks() {
        for (NetworkLink link : topology.getLinks().values()) {
            link.setActive(true);
        }
        traceAtmRoute();
        repaint();
    }

    public void setHighlightedPath(List<String> path) {
        this.highlightedPath = path != null ? new ArrayList<>(path) : new ArrayList<>();
        repaint();
    }

    private void traceAtmRoute() {
        RouteResult res = Dijkstra.findShortestPath(topology, "ATM-01", "SRV-HQ");
        if (res.isReachable()) {
            setHighlightedPath(res.getPathNodes());
            lblRouteInfo.setText(String.format("Active Route: %s (Cost: %d)", res.getFormattedPath(), res.getTotalCost()));
            lblRouteInfo.setForeground(UITheme.SUCCESS_GREEN);
        } else {
            setHighlightedPath(Collections.emptyList());
            lblRouteInfo.setText("Active Route: UNREACHABLE (All paths severed!)");
            lblRouteInfo.setForeground(UITheme.DANGER_RED);
        }
    }

    private class CanvasPanel extends JPanel {
        public CanvasPanel() {
            setBackground(new Color(13, 19, 33));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Draw Links
            for (NetworkLink link : topology.getLinks().values()) {
                Point p1 = nodePositions.get(link.getSourceNodeId());
                Point p2 = nodePositions.get(link.getTargetNodeId());
                if (p1 == null || p2 == null) continue;

                boolean inHighlightedPath = isLinkInPath(link.getSourceNodeId(), link.getTargetNodeId());

                if (!link.isActive()) {
                    // Inactive/Cut link: Dashed Red
                    g2.setColor(UITheme.DANGER_RED);
                    float[] dash = {6.0f, 6.0f};
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                } else if (inHighlightedPath) {
                    // Active Shortest Path: Glowing Green
                    g2.setColor(UITheme.SUCCESS_GREEN);
                    g2.setStroke(new BasicStroke(4.0f));
                } else {
                    // Regular Active Link: Muted Slate
                    g2.setColor(new Color(71, 85, 105));
                    g2.setStroke(new BasicStroke(2.0f));
                }

                g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));

                // Draw Cost Badge at midpoint
                int midX = (p1.x + p2.x) / 2;
                int midY = (p1.y + p2.y) / 2;
                String costText = "Cost: " + link.getCost() + (link.isActive() ? "" : " [CUT]");
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(new Color(15, 23, 42));
                g2.fillRect(midX - 22, midY - 9, link.isActive() ? 44 : 58, 16);
                g2.setColor(link.isActive() ? UITheme.ACCENT_CYAN : UITheme.DANGER_RED);
                g2.drawRect(midX - 22, midY - 9, link.isActive() ? 44 : 58, 16);
                g2.drawString(costText, midX - 20, midY + 3);
            }

            // 2. Draw Nodes
            for (NetworkNode node : topology.getNodes().values()) {
                Point p = nodePositions.get(node.getId());
                if (p == null) continue;

                int radius = node.getType() == NodeType.ROUTER ? 24 : 20;
                Color nodeColor;
                if (node.getType() == NodeType.ROUTER) {
                    nodeColor = UITheme.WARNING_AMBER;
                } else if (node.getType() == NodeType.ATM) {
                    nodeColor = UITheme.ACCENT_CYAN;
                } else if (node.getType() == NodeType.DATABASE) {
                    nodeColor = new Color(168, 85, 247); // Purple
                } else if (node.getType() == NodeType.SERVER) {
                    nodeColor = UITheme.SUCCESS_GREEN;
                } else {
                    nodeColor = new Color(59, 130, 246);
                }

                boolean inPath = highlightedPath.contains(node.getId());

                // Outer halo if in path
                if (inPath) {
                    g2.setColor(new Color(16, 185, 129, 90));
                    g2.fill(new Ellipse2D.Double(p.x - radius - 6, p.y - radius - 6, (radius + 6) * 2, (radius + 6) * 2));
                }

                // Inner circle
                g2.setColor(nodeColor);
                g2.fill(new Ellipse2D.Double(p.x - radius, p.y - radius, radius * 2, radius * 2));
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.0f));
                g2.draw(new Ellipse2D.Double(p.x - radius, p.y - radius, radius * 2, radius * 2));

                // Node Labels
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int labelWidth = fm.stringWidth(node.getId());
                g2.drawString(node.getId(), p.x - (labelWidth / 2), p.y + radius + 15);

                // Subtitle / IP
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(UITheme.TEXT_SECONDARY);
                String ipStr = node.getPrimaryIp().toString();
                int ipWidth = g2.getFontMetrics().stringWidth(ipStr);
                g2.drawString(ipStr, p.x - (ipWidth / 2), p.y + radius + 28);
            }

            g2.dispose();
        }

        private boolean isLinkInPath(String a, String b) {
            if (highlightedPath.size() < 2) return false;
            for (int i = 0; i < highlightedPath.size() - 1; i++) {
                String u = highlightedPath.get(i);
                String v = highlightedPath.get(i + 1);
                if ((u.equals(a) && v.equals(b)) || (u.equals(b) && v.equals(a))) {
                    return true;
                }
            }
            return false;
        }
    }
}
