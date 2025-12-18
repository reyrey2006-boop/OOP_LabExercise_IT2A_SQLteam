package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import utils.DBConnection;

// REQUIRED imports
import GUI.ResidentLists;
import GUI.Requests;

public class StaffDashboard extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JFrame parentFrame;
    private StaffLogin staffLogin;

    private static final String STAFF_LOGIN_VIEW = "StaffLogin";
    private static final String STAFF_ANNOUNCEMENT_VIEW = "StaffAnnouncement";
    private static final String STAFF_DASHBOARD_VIEW = "StaffDashboard";
    private static final String RESIDENTS_LISTS_VIEW = "ResidentsLists";
    private static final String REQUESTS_VIEW = "Requests";

    private static final Color DARK_TEXT = new Color(0, 0, 0);
    private static final Color TEAL_ACCENT = new Color(0, 191, 191);
    private static final Color BG_COLOR = new Color(225, 240, 255);
    private static final Color PRIMARY_COLOR = new Color(50, 70, 90);
    private static final Color ACCENT_COLOR = new Color(0, 150, 200);

    // 🔹 LABEL REFERENCES (for auto refresh)
    private JLabel residentsLabel;
    private JLabel projectsLabel;
    private JLabel pendingLabel;
    private JLabel completedLabel;

    public StaffDashboard(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel, StaffLogin staffLogin) {
        this.parentFrame = parentFrame;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.staffLogin = staffLogin;

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ================= HEADER =================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        headerPanel.setPreferredSize(new Dimension(800, 70));

        JLabel welcomeLabel = new JLabel("WELCOME, STAFF COORDINATOR");
        welcomeLabel.setFont(new Font("STXinwei", Font.BOLD, 30));
        welcomeLabel.setForeground(PRIMARY_COLOR);
        welcomeLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new RoundButton("LOG OUT", new Color(200, 50, 50));
        logoutButton.setFont(new Font("Leelawadee", Font.PLAIN, 17));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setPreferredSize(new Dimension(130, 42));

        logoutButton.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(parentFrame,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                if (staffLogin != null) staffLogin.clearFields();
                cardLayout.show(cardPanel, STAFF_LOGIN_VIEW);
            }
        });

        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        logoutWrapper.setBackground(Color.WHITE);
        logoutWrapper.add(logoutButton);
        headerPanel.add(logoutWrapper, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ================= CONTENT =================
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // ================= NAV (Aligned with StaffAnnouncement) =================
        JPanel navPanel = new JPanel(new GridBagLayout());
        navPanel.setBackground(Color.WHITE);
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

        gbc.gridy = 1; navPanel.add(createStaffNavLink("Dashboard", true), gbc);
        gbc.gridy = 2; navPanel.add(createStaffNavLink("Announcement", false), gbc);
        gbc.gridy = 3; navPanel.add(createStaffNavLink("Residents", false), gbc);
        gbc.gridy = 4; navPanel.add(createStaffNavLink("Requests", false), gbc);
        
        // Added vertical glue to push buttons to the top, matching StaffAnnouncement layout
        gbc.gridy = 5; 
        gbc.weighty = 1.0; 
        navPanel.add(Box.createVerticalGlue(), gbc);

        contentPanel.add(navPanel, BorderLayout.WEST);

        // ================= DASHBOARD =================
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        dashboardPanel.setOpaque(false);

        residentsLabel = createMetricCard("Total Registered Residents", new Color(40, 167, 69), dashboardPanel);
        projectsLabel  = createMetricCard("Active Community Projects", ACCENT_COLOR, dashboardPanel);
        pendingLabel   = createMetricCard("Pending Service Approvals", new Color(255, 193, 7), dashboardPanel);
        completedLabel = createMetricCard("Services Completed", new Color(184, 1, 1), dashboardPanel);

        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // Initial load + auto refresh
        refreshDashboardData();
        startAutoRefresh();
    }

    // ================= AUTO REFRESH =================
    private void startAutoRefresh() {
        new Timer(2000, e -> refreshDashboardData()).start(); // every 5 seconds
    }

    private void refreshDashboardData() {
        try (Connection conn = DBConnection.getConnection()) {

            residentsLabel.setText(getCount(conn, "SELECT COUNT(*) FROM residents"));
            projectsLabel.setText(getCount(conn, "SELECT COUNT(*) FROM projects WHERE status='Active'"));
            pendingLabel.setText(getCount(conn, "SELECT COUNT(*) FROM resident_requests WHERE status='Pending'"));
            completedLabel.setText(getCount(conn, "SELECT COUNT(*) FROM resident_requests WHERE update_status='Done'"));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private String getCount(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? String.valueOf(rs.getInt(1)) : "0";
        }
    }

    // ================= METRIC CARD =================
    private JLabel createMetricCard(String title, Color color, JPanel parent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel countLabel = new JLabel("0", SwingConstants.CENTER);
        countLabel.setFont(new Font("Leelawadee", Font.BOLD, 53));
        countLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Leelawadee", Font.PLAIN, 23));

        card.add(countLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        parent.add(card);

        return countLabel;
    }

    // ================= NAV BUTTON (Aligned styling with StaffAnnouncement) =================
    private RoundButton createStaffNavLink(String text, boolean isActive) {
        RoundButton btn = new RoundButton(text, TEAL_ACCENT);
        btn.setFont(new Font("Leelawadee", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setPreferredSize(new Dimension(150, 54));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            switch (text) {
                case "Dashboard" -> cardLayout.show(cardPanel, STAFF_DASHBOARD_VIEW);
                case "Announcement" -> cardLayout.show(cardPanel, STAFF_ANNOUNCEMENT_VIEW);
                case "Residents" -> cardLayout.show(cardPanel, RESIDENTS_LISTS_VIEW);
                case "Requests" -> cardLayout.show(cardPanel, REQUESTS_VIEW);
            }
        });

        // Consistent active/inactive color logic
        if (isActive) {
            btn.setBackground(TEAL_ACCENT);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(BG_COLOR);
            btn.setForeground(PRIMARY_COLOR);
        }
        
        return btn;
    }
}