package ui;

import banking.BankingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Enterprise Desktop Banking Network Dashboard Application.
 * Supports dual navigation modes: Modern Vertical Sidebar & Horizontal Top Tabs
 * for user comfort and versatile presentation.
 */
public class Dashboard extends JFrame {

    private final BankingService bankingService;

    // View panels
    private OverviewPanel overviewPanel;
    private TopologyPanel topologyPanel;
    private SubnetPanel subnetPanel;
    private RoutingDijkstraPanel dijkstraPanel;
    private RoutingDVPanel dvPanel;
    private SecurityPanel securityPanel;
    private ATMClientServerPanel atmServerPanel;
    private CRCPanel crcPanel;
    private GoBackNPanel gbnPanel;
    private DatabasePanel databasePanel;
    private WiresharkGuidePanel wiresharkPanel;
    private LogsPanel logsPanel;
    private AboutPanel aboutPanel;

    private static final String[] TAB_NAMES = {
            "Dashboard Overview",
            "Network Topology",
            "IP & Subnetting",
            "Dijkstra Routing",
            "Distance Vector",
            "AES-256-GCM Encryption",
            "ATM Client & Server",
            "CRC Error Detection",
            "Sliding Window / GBN",
            "Database & Accounts",
            "Wireshark Guide",
            "Audit Logs",
            "About Project"
    };

    private static final String[] TAB_ICONS = {
            "📊", "🌐", "🔢", "🛤️", "🔄", "🔐", "🏧", "🔍", "🪟", "🗄️", "🦈", "📜", "ℹ️"
    };

    // Dual Navigation State
    private boolean isVerticalNav = true; // Default to sleek Vertical Sidebar
    private int currentActiveIndex = 0;

    // UI Containers
    private JPanel mainContainer;
    private JPanel verticalSidebarPanel;
    private JPanel horizontalNavPanel;
    private JPanel contentCardsPanel;
    private CardLayout cardLayout;
    private JButton btnToggleNavMode;
    private final List<JButton> sidebarButtons = new ArrayList<>();
    private final List<JButton> horizontalButtons = new ArrayList<>();

    public Dashboard() {
        this(new BankingService());
    }

    public Dashboard(BankingService bankingService) {
        this.bankingService = bankingService;

        setTitle("Secure and Reliable Banking Network Using Routing and Encryption - Enterprise Architecture");
        setSize(1240, 800);
        setMinimumSize(new Dimension(1080, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initPanels();
        initUI();
    }

    private void initPanels() {
        overviewPanel = new OverviewPanel(bankingService);
        topologyPanel = new TopologyPanel(bankingService.getTopology());
        subnetPanel = new SubnetPanel();
        dijkstraPanel = new RoutingDijkstraPanel(bankingService.getTopology());
        dvPanel = new RoutingDVPanel(bankingService.getDvSimulation());
        securityPanel = new SecurityPanel(bankingService.getMasterKey());
        atmServerPanel = new ATMClientServerPanel(bankingService);
        crcPanel = new CRCPanel();
        gbnPanel = new GoBackNPanel();
        databasePanel = new DatabasePanel(bankingService);
        wiresharkPanel = new WiresharkGuidePanel(bankingService);
        logsPanel = new LogsPanel(bankingService);
        aboutPanel = new AboutPanel();
    }

    private void initUI() {
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        // 1. Top Header Banner
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // 2. Central Workspace with CardLayout
        mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UITheme.BG_DARK);

        cardLayout = new CardLayout();
        contentCardsPanel = new JPanel(cardLayout);
        contentCardsPanel.setBackground(UITheme.BG_DARK);

        // Add 13 subsystems to CardLayout
        contentCardsPanel.add(overviewPanel, "TAB_0");
        contentCardsPanel.add(topologyPanel, "TAB_1");
        contentCardsPanel.add(subnetPanel, "TAB_2");
        contentCardsPanel.add(dijkstraPanel, "TAB_3");
        contentCardsPanel.add(dvPanel, "TAB_4");
        contentCardsPanel.add(securityPanel, "TAB_5");
        contentCardsPanel.add(atmServerPanel, "TAB_6");
        contentCardsPanel.add(crcPanel, "TAB_7");
        contentCardsPanel.add(gbnPanel, "TAB_8");
        contentCardsPanel.add(databasePanel, "TAB_9");
        contentCardsPanel.add(wiresharkPanel, "TAB_10");
        contentCardsPanel.add(logsPanel, "TAB_11");
        contentCardsPanel.add(aboutPanel, "TAB_12");

        // 3. Construct Both Navigation Component Bars
        verticalSidebarPanel = createVerticalSidebar();
        horizontalNavPanel = createHorizontalNavBar();

        // Assemble into main container based on initial mode
        updateNavigationLayout();

        mainContainer.add(contentCardsPanel, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);

        // Automatically start banking server daemon on port 9090
        try {
            bankingService.startServer();
        } catch (Exception e) {
            System.err.println("Could not auto-start server on startup: " + e.getMessage());
        }

        // Clean shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(bankingService::stopServer));
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(11, 15, 25));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.PANEL_BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        // Title and Subtitle
        JLabel titleLabel = new JLabel("SECURE AND RELIABLE BANKING NETWORK USING ROUTING AND ENCRYPTION");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subLabel = new JLabel("Integrated Academic Computer Networks & Cybersecurity Capstone System");
        subLabel.setFont(UITheme.FONT_BODY);
        subLabel.setForeground(UITheme.ACCENT_CYAN);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(subLabel);
        headerPanel.add(titleBox, BorderLayout.WEST);

        // Right Action Bar with Layout Toggle Button
        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        rightBar.setOpaque(false);

        JLabel tag = new JLabel("[AES-256-GCM | TCP 9090 | SQLITE ACID]");
        tag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tag.setForeground(UITheme.TEXT_SECONDARY);
        rightBar.add(tag);

        btnToggleNavMode = UITheme.createPrimaryButton("⇋ Switch to Horizontal Tabs");
        btnToggleNavMode.setToolTipText("Toggle between Vertical Sidebar Navigation and Horizontal Top Navigation");
        btnToggleNavMode.addActionListener(e -> toggleNavigationOrientation());
        rightBar.add(btnToggleNavMode);

        headerPanel.add(rightBar, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createVerticalSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(15, 23, 42));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.PANEL_BORDER),
                BorderFactory.createEmptyBorder(10, 8, 10, 8)
        ));
        sidebar.setPreferredSize(new Dimension(245, 0));

        sidebarButtons.clear();

        // Categorized Sidebar Navigation Items
        addSidebarCategoryHeader(sidebar, "EXECUTIVE DASHBOARD");
        addSidebarButton(sidebar, 0); // Overview

        addSidebarCategoryHeader(sidebar, "NETWORK & TOPOLOGY");
        addSidebarButton(sidebar, 1); // Topology
        addSidebarButton(sidebar, 2); // IP & Subnetting

        addSidebarCategoryHeader(sidebar, "ROUTING ENGINES");
        addSidebarButton(sidebar, 3); // Dijkstra
        addSidebarButton(sidebar, 4); // Distance Vector

        addSidebarCategoryHeader(sidebar, "SECURITY & TRANSPORT");
        addSidebarButton(sidebar, 5); // AES-GCM
        addSidebarButton(sidebar, 6); // ATM Client & Server

        addSidebarCategoryHeader(sidebar, "ERROR & RELIABILITY");
        addSidebarButton(sidebar, 7); // CRC
        addSidebarButton(sidebar, 8); // Sliding Window / GBN

        addSidebarCategoryHeader(sidebar, "DATA & ANALYSIS");
        addSidebarButton(sidebar, 9);  // Database
        addSidebarButton(sidebar, 10); // Wireshark Guide
        addSidebarButton(sidebar, 11); // Audit Logs
        addSidebarButton(sidebar, 12); // About Project

        sidebar.add(Box.createVerticalGlue()); // Push items up

        // Wrap in a scroll pane to guarantee comfort on smaller displays
        JScrollPane scrollPane = new JScrollPane(sidebar);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(250, 0));

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UITheme.BG_DARK);
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private void addSidebarCategoryHeader(JPanel panel, String title) {
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(100, 116, 139)); // Slate 500
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 4, 4, 4));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
    }

    private void addSidebarButton(JPanel panel, int index) {
        JButton btn = new JButton(TAB_ICONS[index] + "  " + TAB_NAMES[index]);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(230, 34));
        btn.setPreferredSize(new Dimension(230, 34));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> selectTab(index));

        sidebarButtons.add(btn);
        panel.add(btn);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
    }

    private JPanel createHorizontalNavBar() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 6));
        navPanel.setBackground(new Color(11, 15, 25));
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.PANEL_BORDER));

        horizontalButtons.clear();
        for (int i = 0; i < TAB_NAMES.length; i++) {
            final int idx = i;
            JButton btn = new JButton(TAB_ICONS[i] + " " + TAB_NAMES[i]);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> selectTab(idx));

            horizontalButtons.add(btn);
            navPanel.add(btn);
        }

        // Horizontal scroll pane for smaller laptop screens
        JScrollPane scrollPane = new JScrollPane(navPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(24);
        scrollPane.setPreferredSize(new Dimension(0, 48));

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(11, 15, 25));
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    public void selectTab(int index) {
        if (index < 0 || index >= TAB_NAMES.length) return;
        this.currentActiveIndex = index;

        // Switch visible card
        cardLayout.show(contentCardsPanel, "TAB_" + index);

        // Update styling of both Sidebar and Horizontal button sets
        updateButtonStyles();

        // Trigger dynamic data refresh if moving to Overview, Database, or Audit Logs
        if (index == 0) overviewPanel.refreshData();
        else if (index == 9) databasePanel.refreshTables();
        else if (index == 11) logsPanel.refreshLogs();
    }

    private void updateButtonStyles() {
        // Style Vertical Sidebar Buttons
        for (int i = 0; i < sidebarButtons.size(); i++) {
            JButton btn = sidebarButtons.get(i);
            boolean active = (i == currentActiveIndex);
            if (active) {
                btn.setBackground(UITheme.ACCENT_BLUE);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.ACCENT_CYAN),
                        new EmptyBorder(6, 12, 6, 8)
                ));
            } else {
                btn.setBackground(new Color(15, 23, 42));
                btn.setForeground(UITheme.TEXT_SECONDARY);
                btn.setBorder(new EmptyBorder(6, 16, 6, 8));
            }
        }

        // Style Horizontal Top Buttons
        for (int i = 0; i < horizontalButtons.size(); i++) {
            JButton btn = horizontalButtons.get(i);
            boolean active = (i == currentActiveIndex);
            if (active) {
                btn.setBackground(UITheme.ACCENT_BLUE);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 3, 0, UITheme.ACCENT_CYAN),
                        new EmptyBorder(6, 10, 6, 10)
                ));
            } else {
                btn.setBackground(new Color(30, 41, 59));
                btn.setForeground(UITheme.TEXT_SECONDARY);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.PANEL_BORDER, 1),
                        new EmptyBorder(6, 10, 6, 10)
                ));
            }
        }
    }

    private void toggleNavigationOrientation() {
        isVerticalNav = !isVerticalNav;
        updateNavigationLayout();
    }

    private void updateNavigationLayout() {
        mainContainer.remove(verticalSidebarPanel);
        mainContainer.remove(horizontalNavPanel);

        if (isVerticalNav) {
            btnToggleNavMode.setText("⇋ Switch to Horizontal Tabs");
            mainContainer.add(verticalSidebarPanel, BorderLayout.WEST);
        } else {
            btnToggleNavMode.setText("⇋ Switch to Vertical Sidebar");
            mainContainer.add(horizontalNavPanel, BorderLayout.NORTH);
        }

        selectTab(currentActiveIndex);
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
        });
    }
}
