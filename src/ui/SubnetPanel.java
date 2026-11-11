package ui;

import network.IPAddress;
import network.SubnetCalculator;

import javax.swing.*;
import java.awt.*;

/**
 * Interactive IP Addressing, CIDR, and Subnetting Calculator UI.
 */
public class SubnetPanel extends JPanel {

    private JTextField txtIp;
    private JComboBox<Integer> cmbPrefix;
    private JTextArea txtResults;
    private JTextField txtIp1;
    private JTextField txtIp2;
    private JComboBox<Integer> cmbSubnetCheckPrefix;
    private JLabel lblDirectCommResult;

    public SubnetPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
    }

    private void initUI() {
        JPanel grid = new JPanel(new GridLayout(1, 2, 15, 0));
        grid.setBackground(UITheme.BG_DARK);

        // --- Left Panel: Subnet Calculator ---
        JPanel calcCard = UITheme.createCardPanel("IPV4 SUBNET CALCULATOR");
        JPanel inputForm = new JPanel(new GridBagLayout());
        inputForm.setBackground(UITheme.PANEL_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblIp = new JLabel("IPv4 Address:");
        lblIp.setForeground(UITheme.TEXT_PRIMARY);
        inputForm.add(lblIp, gbc);

        gbc.gridx = 1;
        txtIp = UITheme.createTextField(14);
        txtIp.setText("192.168.1.10");
        inputForm.add(txtIp, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPrefix = new JLabel("CIDR Prefix (Mask):");
        lblPrefix.setForeground(UITheme.TEXT_PRIMARY);
        inputForm.add(lblPrefix, gbc);

        gbc.gridx = 1;
        Integer[] prefixes = new Integer[24];
        for (int i = 0; i < 24; i++) prefixes[i] = i + 8; // /8 to /31
        cmbPrefix = new JComboBox<>(prefixes);
        cmbPrefix.setSelectedItem(24);
        inputForm.add(cmbPrefix, gbc);

        // Preset Buttons
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel presetBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        presetBar.setBackground(UITheme.PANEL_BG);

        JButton btnAtmPreset = UITheme.createSecondaryButton("ATM Subnet (/24)");
        btnAtmPreset.addActionListener(e -> { txtIp.setText("192.168.1.10"); cmbPrefix.setSelectedItem(24); calculate(); });
        presetBar.add(btnAtmPreset);

        JButton btnWanPreset = UITheme.createSecondaryButton("WAN Link (/30)");
        btnWanPreset.addActionListener(e -> { txtIp.setText("10.0.1.1"); cmbPrefix.setSelectedItem(30); calculate(); });
        presetBar.add(btnWanPreset);

        inputForm.add(presetBar, gbc);

        gbc.gridy = 3;
        JButton btnCalc = UITheme.createPrimaryButton("Calculate Subnet Details");
        btnCalc.addActionListener(e -> calculate());
        inputForm.add(btnCalc, gbc);

        calcCard.add(inputForm, BorderLayout.NORTH);

        txtResults = UITheme.createConsoleArea(14, 30);
        calcCard.add(new JScrollPane(txtResults), BorderLayout.CENTER);

        grid.add(calcCard);

        // --- Right Panel: Subnet Membership & Layer 2 Communication Test ---
        JPanel commCard = UITheme.createCardPanel("DEVICE COMMUNICATION ELIGIBILITY TEST");
        JPanel commForm = new JPanel(new GridBagLayout());
        commForm.setBackground(UITheme.PANEL_BG);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(6, 6, 6, 6);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        gbc2.gridx = 0; gbc2.gridy = 0;
        JLabel lblDev1 = new JLabel("Device 1 IP:");
        lblDev1.setForeground(UITheme.TEXT_PRIMARY);
        commForm.add(lblDev1, gbc2);

        gbc2.gridx = 1;
        txtIp1 = UITheme.createTextField(14);
        txtIp1.setText("192.168.1.10");
        commForm.add(txtIp1, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1;
        JLabel lblDev2 = new JLabel("Device 2 IP:");
        lblDev2.setForeground(UITheme.TEXT_PRIMARY);
        commForm.add(lblDev2, gbc2);

        gbc2.gridx = 1;
        txtIp2 = UITheme.createTextField(14);
        txtIp2.setText("192.168.1.20");
        commForm.add(txtIp2, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 2;
        JLabel lblP = new JLabel("Subnet Prefix:");
        lblP.setForeground(UITheme.TEXT_PRIMARY);
        commForm.add(lblP, gbc2);

        gbc2.gridx = 1;
        cmbSubnetCheckPrefix = new JComboBox<>(prefixes);
        cmbSubnetCheckPrefix.setSelectedItem(24);
        commForm.add(cmbSubnetCheckPrefix, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 3; gbc2.gridwidth = 2;
        JButton btnCheckComm = UITheme.createPrimaryButton("Verify Subnet Membership");
        btnCheckComm.addActionListener(e -> checkCommunication());
        commForm.add(btnCheckComm, gbc2);

        gbc2.gridy = 4;
        lblDirectCommResult = new JLabel("Enter two IP addresses and verify membership.");
        lblDirectCommResult.setFont(UITheme.FONT_BOLD);
        lblDirectCommResult.setForeground(UITheme.ACCENT_CYAN);
        commForm.add(lblDirectCommResult, gbc2);

        commCard.add(commForm, BorderLayout.NORTH);

        JTextArea commExplain = UITheme.createConsoleArea(10, 30);
        commExplain.setText("""
            === HOW SAME-SUBNET VERIFICATION WORKS ===
            
            1. Binary Bitwise Masking:
               Network ID 1 = IP 1 AND Subnet Mask
               Network ID 2 = IP 2 AND Subnet Mask
            
            2. Decision Logic:
               - If Network ID 1 == Network ID 2:
                 Devices share the same broadcast domain.
                 Packets travel directly via Layer 2 ARP / Switch without a router!
            
               - If Network ID 1 != Network ID 2:
                 Devices are in different subnets.
                 Packets MUST be dispatched to Default Gateway (Router) for Layer 3 inter-VLAN routing.
            """);
        commCard.add(new JScrollPane(commExplain), BorderLayout.CENTER);

        grid.add(commCard);
        add(grid, BorderLayout.CENTER);

        calculate();
    }

    private void calculate() {
        try {
            String ip = txtIp.getText().trim();
            int prefix = (Integer) cmbPrefix.getSelectedItem();
            SubnetCalculator.SubnetDetails d = SubnetCalculator.calculate(ip, prefix);

            StringBuilder sb = new StringBuilder();
            sb.append("============================================================\n");
            sb.append(String.format(" SUBNET CALCULATION REPORT: %s/%d\n", ip, prefix));
            sb.append("============================================================\n");
            sb.append(String.format("  IP Address (Dec)   : %s\n", d.ipAddress));
            sb.append(String.format("  IP Address (Bin)   : %s\n", d.ipAddress.toBinaryString()));
            sb.append(String.format("  Address Class      : %s\n", d.ipAddress.getAddressClass()));
            sb.append(String.format("  RFC 1918 Private?  : %b\n", d.ipAddress.isPrivateAddress()));
            sb.append(String.format("  Subnet Mask        : %s\n", d.subnetMask));
            sb.append(String.format("  Subnet Mask (Bin)  : %s\n", d.subnetMask.toBinaryString()));
            sb.append(String.format("  Wildcard Mask      : %s\n", d.wildcardMask));
            sb.append("------------------------------------------------------------\n");
            sb.append(String.format("  Network ID Address : %s\n", d.networkAddress));
            sb.append(String.format("  Broadcast Address  : %s\n", d.broadcastAddress));
            sb.append(String.format("  First Usable Host  : %s\n", d.firstUsableIp));
            sb.append(String.format("  Last Usable Host   : %s\n", d.lastUsableIp));
            sb.append(String.format("  Total Usable Hosts : %,d  (2^(%d) - 2)\n", d.usableHosts, 32 - prefix));
            sb.append("============================================================\n");

            txtResults.setText(sb.toString());
        } catch (Exception ex) {
            txtResults.setText("ERROR: " + ex.getMessage());
        }
    }

    private void checkCommunication() {
        try {
            String ip1Str = txtIp1.getText().trim();
            String ip2Str = txtIp2.getText().trim();
            int prefix = (Integer) cmbSubnetCheckPrefix.getSelectedItem();

            IPAddress ip1 = new IPAddress(ip1Str);
            IPAddress ip2 = new IPAddress(ip2Str);

            boolean same = SubnetCalculator.isSameSubnet(ip1, ip2, prefix);
            if (same) {
                lblDirectCommResult.setText("ELIGIBLE: Devices are in the SAME subnet. Direct L2 communication!");
                lblDirectCommResult.setForeground(UITheme.SUCCESS_GREEN);
            } else {
                lblDirectCommResult.setText("ROUTING REQUIRED: Devices in DIFFERENT subnets. Default Gateway needed.");
                lblDirectCommResult.setForeground(UITheme.WARNING_AMBER);
            }
        } catch (Exception ex) {
            lblDirectCommResult.setText("ERROR: " + ex.getMessage());
            lblDirectCommResult.setForeground(UITheme.DANGER_RED);
        }
    }
}
