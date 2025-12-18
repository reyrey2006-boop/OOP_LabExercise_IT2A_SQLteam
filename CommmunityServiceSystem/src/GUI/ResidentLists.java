package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.sql.*;
import java.awt.event.*;
import utils.DBConnection;

public class ResidentLists extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private static final String STAFF_LOGIN_VIEW = "StaffLogin";
    private static final String STAFF_ANNOUNCEMENT_VIEW = "StaffAnnouncement";
    private static final String STAFF_DASHBOARD_VIEW = "StaffDashboard";
    private static final String RESIDENTS_LISTS_VIEW = "ResidentsLists";
    private static final String REQUESTS_VIEW = "Requests";

    private static final Color DARK_TEXT = new Color(0, 0, 0);
    private static final Color TEAL_ACCENT = new Color(0, 191, 191);
    private static final Color BG_COLOR = new Color(225, 240, 255);
    private static final Color WHITE_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(50, 70, 90);
    private static final Color BLOCKED_RED = Color.RED;

    private JTable residentsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public ResidentLists(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ================= HEADER =================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE_BG);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        headerPanel.setPreferredSize(new Dimension(800, 70));

        JLabel titleLabel = new JLabel("LIST OF RESIDENTS");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 30));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // ================= CONTENT =================
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // ================= NAV (Aligned with StaffAnnouncement) =================
        JPanel navPanel = new JPanel(new GridBagLayout());
        navPanel.setBackground(WHITE_BG);
        navPanel.setPreferredSize(new Dimension(200, 500));
        navPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);

        JLabel navTitle = new JLabel("MENU");
        navTitle.setFont(new Font("Leelawadee", Font.BOLD, 30));
        navTitle.setForeground(DARK_TEXT);
        navTitle.setHorizontalAlignment(SwingConstants.CENTER);
        navTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        gbc.gridy = 0;
        navPanel.add(navTitle, gbc);

        gbc.gridy = 1; navPanel.add(createStaffNavLink("Dashboard", false), gbc);
        gbc.gridy = 2; navPanel.add(createStaffNavLink("Announcement", false), gbc);
        gbc.gridy = 3; navPanel.add(createStaffNavLink("Residents", true), gbc);
        gbc.gridy = 4; navPanel.add(createStaffNavLink("Requests", false), gbc);

        gbc.gridy = 5;
        gbc.weighty = 1.0;
        navPanel.add(Box.createVerticalGlue(), gbc);

        contentPanel.add(navPanel, BorderLayout.WEST);

        // ================= MAIN DATA =================
        JPanel mainDataPanel = new JPanel(new BorderLayout(10, 10));
        mainDataPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setOpaque(false);
        searchField = new JTextField();
        searchField.setFont(new Font("Leelawadee", Font.PLAIN, 16));

        JButton searchBtn = new RoundButton("Search", TEAL_ACCENT);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Leelawadee", Font.BOLD, 16));
        searchBtn.setPreferredSize(new Dimension(120, 40));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        mainDataPanel.add(searchPanel, BorderLayout.NORTH);

        // ================= TABLE =================
        String[] columns = {"ID", "First Name", "Last Name", "Birthday", "Sex", "Contact", "Address", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        residentsTable = new JTable(tableModel);
        residentsTable.setRowHeight(30);
        residentsTable.setFont(new Font("Leelawadee", Font.PLAIN, 14));
        residentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        residentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                String status = (String) t.getModel().getValueAt(r, 8);
                comp.setForeground("Blocked".equalsIgnoreCase(status) ? BLOCKED_RED : Color.BLACK);
                return comp;
            }
        });

        JScrollPane tableScroll = new JScrollPane(residentsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(150, 200, 255), 2));
        mainDataPanel.add(tableScroll, BorderLayout.CENTER);

        // ================= BUTTONS =================
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        btnPanel.setOpaque(false);

        RoundButton blockBtn = new RoundButton("Block", TEAL_ACCENT);
        RoundButton unblockBtn = new RoundButton("Unblock", TEAL_ACCENT);

        blockBtn.setForeground(Color.WHITE);
        unblockBtn.setForeground(Color.WHITE);

        blockBtn.setPreferredSize(new Dimension(150, 50));
        unblockBtn.setPreferredSize(new Dimension(150, 50));

        btnPanel.add(blockBtn);
        btnPanel.add(unblockBtn);

        mainDataPanel.add(btnPanel, BorderLayout.SOUTH);

        contentPanel.add(mainDataPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // ================= ACTIONS =================
        loadResidentsData();
        autoResizeTableColumns(residentsTable);
        startAutoRefresh();

        ActionListener searchAction = e -> {
            String term = searchField.getText().trim();
            if (term.isEmpty()) loadResidentsData();
            else searchResidents(term);
            autoResizeTableColumns(residentsTable);
        };

        searchBtn.addActionListener(searchAction);
        searchField.addActionListener(searchAction);

        blockBtn.addActionListener(e -> blockOrUnblock("Blocked"));
        unblockBtn.addActionListener(e -> blockOrUnblock("Active"));
    }

    // ================= AUTO REFRESH =================
    private void startAutoRefresh() {
        new Timer(5000, e -> {
            if (!isShowing()) return;
            String term = searchField.getText().trim();
            if (term.isEmpty()) loadResidentsData();
            else searchResidents(term);
            autoResizeTableColumns(residentsTable);
        }).start();
    }

    // ================= DATA =================
    private void loadResidentsData() {
        tableModel.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT * FROM residents")) {

            while (r.next()) {
                tableModel.addRow(new Object[]{
                        r.getInt("id"),
                        r.getString("firstname"),
                        r.getString("lastname"),
                        r.getDate("birthday"),
                        r.getString("sex"),
                        r.getString("contact"),
                        r.getString("address"),
                        r.getString("email"),
                        r.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchResidents(String term) {
        tableModel.setRowCount(0);
        String q = "SELECT * FROM residents WHERE firstname LIKE ? OR lastname LIKE ? OR email LIKE ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(q)) {

            String like = "%" + term + "%";
            p.setString(1, like);
            p.setString(2, like);
            p.setString(3, like);

            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    tableModel.addRow(new Object[]{
                            r.getInt("id"),
                            r.getString("firstname"),
                            r.getString("lastname"),
                            r.getDate("birthday"),
                            r.getString("sex"),
                            r.getString("contact"),
                            r.getString("address"),
                            r.getString("email"),
                            r.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void blockOrUnblock(String status) {
        int row = residentsTable.getSelectedRow();
        
        // 1. Validation: Check if a row is selected
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a resident from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = (String) tableModel.getValueAt(row, 8);
        String firstName = (String) tableModel.getValueAt(row, 1);
        String lastName = (String) tableModel.getValueAt(row, 2);

        // 2. Validation: Check if the user is already in that state
        if (currentStatus.equalsIgnoreCase(status)) {
            String message = status.equalsIgnoreCase("Blocked") ? "This resident is already blocked." : "This resident is already active.";
            JOptionPane.showMessageDialog(this, message, "Invalid Action", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 3. Validation: Confirmation dialog
        String action = status.equalsIgnoreCase("Blocked") ? "block" : "unblock";
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to " + action + " " + firstName + " " + lastName + "?", 
            "Confirm Action", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) tableModel.getValueAt(row, 0);
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement("UPDATE residents SET status=? WHERE id=?")) {
            p.setString(1, status);
            p.setInt(2, id);
            p.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Resident successfully " + (status.equalsIgnoreCase("Blocked") ? "blocked." : "unblocked."));
            loadResidentsData();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= NAV BUTTON (Aligned styling with StaffAnnouncement) =================
    private RoundButton createStaffNavLink(String text, boolean active) {
        RoundButton btn = new RoundButton(text, TEAL_ACCENT);
        btn.setFont(new Font("Leelawadee", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setPreferredSize(new Dimension(150, 54));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (active) {
            btn.setBackground(TEAL_ACCENT);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(BG_COLOR);
            btn.setForeground(PRIMARY_COLOR);
        }

        btn.addActionListener(e -> {
            String viewName = switch (text) {
                case "Dashboard" -> STAFF_DASHBOARD_VIEW;
                case "Announcement" -> STAFF_ANNOUNCEMENT_VIEW;
                case "Residents" -> RESIDENTS_LISTS_VIEW;
                case "Requests" -> REQUESTS_VIEW;
                default -> RESIDENTS_LISTS_VIEW;
            };
            cardLayout.show(cardPanel, viewName);
        });

        return btn;
    }

    private void autoResizeTableColumns(JTable table) {
        TableColumnModel model = table.getColumnModel();
        for (int c = 0; c < table.getColumnCount(); c++) {
            int w = 60;
            for (int r = 0; r < table.getRowCount(); r++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(r, c), r, c);
                w = Math.max(w, comp.getPreferredSize().width + 10);
            }
            model.getColumn(c).setPreferredWidth(w);
        }
    }
}