package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Modern Dark Navy / Blue UI Theme for the Banking Network Dashboard.
 */
public class UITheme {

    // Color Palette
    public static final Color BG_DARK = new Color(15, 23, 42);          // Deep Slate #0F172A
    public static final Color PANEL_BG = new Color(30, 41, 59);         // Card Background #1E293B
    public static final Color PANEL_BORDER = new Color(51, 65, 85);     // Card Border #334155
    public static final Color ACCENT_CYAN = new Color(14, 165, 233);    // Vibrant Sky Blue #0EA5E9
    public static final Color ACCENT_BLUE = new Color(2, 132, 199);     // Banking Blue #0284C7
    public static final Color SUCCESS_GREEN = new Color(16, 185, 129);  // Emerald #10B981
    public static final Color DANGER_RED = new Color(239, 68, 68);      // Crimson #EF4444
    public static final Color WARNING_AMBER = new Color(245, 158, 11);  // Amber #F59E0B
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252);  // Near White #F8FAFC
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);// Slate Gray #94A3B8
    public static final Color INPUT_BG = new Color(15, 23, 42);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);
    public static final Font FONT_METRIC = new Font("Segoe UI", Font.BOLD, 24);

    public static JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PANEL_BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        if (title != null && !title.isEmpty()) {
            JLabel lbl = new JLabel(title);
            lbl.setFont(FONT_HEADER);
            lbl.setForeground(ACCENT_CYAN);
            panel.add(lbl, BorderLayout.NORTH);
        }
        return panel;
    }

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(ACCENT_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_CYAN, 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(PANEL_BORDER);
        btn.setForeground(TEXT_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PANEL_BORDER, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setBackground(INPUT_BG);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(PANEL_BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return tf;
    }

    public static JTextArea createConsoleArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(FONT_MONO);
        ta.setBackground(new Color(11, 15, 25));
        ta.setForeground(new Color(56, 189, 248)); // Sky 400
        ta.setCaretColor(TEXT_PRIMARY);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(8, 8, 8, 8));
        return ta;
    }

    public static void styleTable(JTable table) {
        table.setBackground(PANEL_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setGridColor(PANEL_BORDER);
        table.setShowGrid(true);
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_DARK);
        header.setForeground(ACCENT_CYAN);
        header.setFont(FONT_BOLD);
        header.setBorder(new LineBorder(PANEL_BORDER, 1));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, v, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? PANEL_BG : new Color(38, 52, 74));
                }
                return c;
            }
        });
    }
}
