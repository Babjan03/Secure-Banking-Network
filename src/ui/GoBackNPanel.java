package ui;

import reliability.Frame;
import reliability.GoBackNSimulation;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Interactive Go-Back-N (GBN) Sliding Window ARQ Simulation Panel.
 */
public class GoBackNPanel extends JPanel {

    private GoBackNSimulation simulation;
    private JSpinner spWindowSize;
    private JComboBox<String> cmbDropFrame;
    private JTextArea txtEventLog;
    private FrameVisualizerPanel visualizer;
    private JLabel lblWindowBounds;

    public GoBackNPanel() {
        this.simulation = new GoBackNSimulation(4, 10);

        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        // --- Top Control Bar ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topBar.setBackground(UITheme.PANEL_BG);
        topBar.setBorder(BorderFactory.createLineBorder(UITheme.PANEL_BORDER, 1));

        topBar.add(new JLabel("Window Size (N):"));
        spWindowSize = new JSpinner(new SpinnerNumberModel(4, 1, 8, 1));
        spWindowSize.addChangeListener(e -> {
            simulation.setWindowSize((Integer) spWindowSize.getValue());
            updateUIState();
        });
        topBar.add(spWindowSize);

        topBar.add(new JLabel("Inject Frame Loss:"));
        cmbDropFrame = new JComboBox<>(new String[]{"None", "Frame F2", "Frame F3", "Frame F4"});
        topBar.add(cmbDropFrame);

        JButton btnTransmit = UITheme.createPrimaryButton("Transmit Allowed Window");
        btnTransmit.addActionListener(e -> transmitStep());
        topBar.add(btnTransmit);

        JButton btnTimeout = UITheme.createSecondaryButton("Simulate ACK Timeout (Rewind)");
        btnTimeout.addActionListener(e -> timeoutStep());
        topBar.add(btnTimeout);

        JButton btnRunAll = UITheme.createSecondaryButton("Run All to Completion");
        btnRunAll.addActionListener(e -> runAll());
        topBar.add(btnRunAll);

        JButton btnReset = UITheme.createSecondaryButton("Reset");
        btnReset.addActionListener(e -> resetSim());
        topBar.add(btnReset);

        add(topBar, BorderLayout.NORTH);

        // --- Center Split ---
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(UITheme.BG_DARK);

        // Visualizer Box
        JPanel visCard = UITheme.createCardPanel("SENDER & RECEIVER SLIDING WINDOW BUFFER STATE");
        visualizer = new FrameVisualizerPanel();
        visualizer.setPreferredSize(new Dimension(850, 140));
        visCard.add(visualizer, BorderLayout.CENTER);

        lblWindowBounds = new JLabel("Sender Window: [0 .. 3] | Receiver Expected: Frame 0");
        lblWindowBounds.setFont(UITheme.FONT_BOLD);
        lblWindowBounds.setForeground(UITheme.ACCENT_CYAN);
        visCard.add(lblWindowBounds, BorderLayout.SOUTH);

        centerPanel.add(visCard, BorderLayout.NORTH);

        // Bottom Event Log
        JPanel logCard = UITheme.createCardPanel("GO-BACK-N PROTOCOL TIMELINE & ARQ ACTIONS");
        txtEventLog = UITheme.createConsoleArea(14, 30);
        logCard.add(new JScrollPane(txtEventLog), BorderLayout.CENTER);

        centerPanel.add(logCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        updateUIState();
    }

    private void transmitStep() {
        String dropSel = (String) cmbDropFrame.getSelectedItem();
        if (dropSel != null && dropSel.startsWith("Frame F")) {
            int dropSeq = Integer.parseInt(dropSel.replace("Frame F", ""));
            simulation.designateLoss(dropSeq);
            cmbDropFrame.setSelectedIndex(0); // Consume selection
        }

        simulation.transmitWindow();
        updateUIState();
    }

    private void timeoutStep() {
        simulation.triggerTimeout();
        updateUIState();
    }

    private void runAll() {
        String dropSel = (String) cmbDropFrame.getSelectedItem();
        if (dropSel != null && dropSel.startsWith("Frame F")) {
            int dropSeq = Integer.parseInt(dropSel.replace("Frame F", ""));
            simulation.designateLoss(dropSeq);
            cmbDropFrame.setSelectedIndex(0);
        }
        simulation.runToCompletion(15);
        updateUIState();
    }

    private void resetSim() {
        int win = (Integer) spWindowSize.getValue();
        simulation = new GoBackNSimulation(win, 10);
        updateUIState();
    }

    private void updateUIState() {
        int base = simulation.getSendBase();
        int win = simulation.getWindowSize();
        int expected = simulation.getExpectedSeqNum();
        int maxSeq = base + win - 1;

        lblWindowBounds.setText(String.format("Sender Window: [F%d .. F%d] (Base: %d) | Receiver Expected: F%d | Status: %s",
                base, maxSeq, base, expected, simulation.isComplete() ? "ALL FRAMES DELIVERED!" : "TRANSMITTING"));
        lblWindowBounds.setForeground(simulation.isComplete() ? UITheme.SUCCESS_GREEN : UITheme.ACCENT_CYAN);

        StringBuilder sb = new StringBuilder();
        for (GoBackNSimulation.SimulationEvent ev : simulation.getEventLog()) {
            sb.append(ev.toString()).append("\n");
        }
        txtEventLog.setText(sb.toString());
        txtEventLog.setCaretPosition(txtEventLog.getDocument().getLength());

        visualizer.repaint();
    }

    private class FrameVisualizerPanel extends JPanel {
        public FrameVisualizerPanel() {
            setBackground(new Color(13, 19, 33));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            List<Frame> frames = simulation.getFrames();
            int startX = 30;
            int startY = 35;
            int boxWidth = 65;
            int boxHeight = 50;
            int gap = 12;

            int sendBase = simulation.getSendBase();
            int winSize = simulation.getWindowSize();

            for (int i = 0; i < frames.size(); i++) {
                Frame f = frames.get(i);
                int x = startX + i * (boxWidth + gap);

                Color fillColor;
                switch (f.getStatus()) {
                    case ACKNOWLEDGED -> fillColor = UITheme.SUCCESS_GREEN;
                    case IN_TRANSIT -> fillColor = UITheme.ACCENT_CYAN;
                    case LOST -> fillColor = UITheme.DANGER_RED;
                    case DISCARDED -> fillColor = UITheme.WARNING_AMBER;
                    case RECEIVED -> fillColor = new Color(59, 130, 246);
                    default -> fillColor = new Color(51, 65, 85); // PENDING
                }

                // Draw Frame Box
                g2.setColor(fillColor);
                g2.fillRoundRect(x, startY, boxWidth, boxHeight, 8, 8);
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(x, startY, boxWidth, boxHeight, 8, 8);

                // Frame Sequence Label
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(Color.WHITE);
                g2.drawString("F" + f.getSequenceNumber(), x + 22, startY + 22);

                // Status Label
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                String st = f.getStatus().name();
                if (st.length() > 6) st = st.substring(0, 6);
                g2.drawString(st, x + 8, startY + 40);

                // Draw Sliding Window Bracket over active window
                if (i >= sendBase && i < sendBase + winSize) {
                    g2.setColor(new Color(56, 189, 248));
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRect(x - 2, startY - 8, boxWidth + 4, boxHeight + 16);
                }
            }

            // Legend
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(UITheme.TEXT_SECONDARY);
            g2.drawString("Legend: [Green = ACKed]  [Cyan = In-Transit]  [Red = Lost/Dropped]  [Amber = Discarded Out-of-Order]  [Blue Box = Current Sender Window]", 30, 120);

            g2.dispose();
        }
    }
}
