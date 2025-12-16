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

    private int pendingApprovals = 0;
    private int servicesCompleted = 0;

    public StaffDashboard(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
        this.parentFrame = parentFrame;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

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
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(130, 42));

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                parentFrame,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
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

        // ================= NAV =================
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
        gbc.gridy = 0;
        navPanel.add(navTitle, gbc);

        gbc.gridy = 1; navPanel.add(createStaffNavLink("Dashboard", true), gbc);
        gbc.gridy = 2; navPanel.add(createStaffNavLink("Announcement", false), gbc);
        gbc.gridy = 3; navPanel.add(createStaffNavLink("Residents", false), gbc);
        gbc.gridy = 4; navPanel.add(createStaffNavLink("Requests", false), gbc);
        gbc.gridy = 5; gbc.weighty = 1.0; navPanel.add(Box.createVerticalGlue(), gbc);

        contentPanel.add(navPanel, BorderLayout.WEST);

        // ================= DATABASE =================
        int totalResidents = 0;
        int activeProjects = 0;

        try (Connection conn = DBConnection.getConnection()) {

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM residents")) {
                if (rs.next()) totalResidents = rs.getInt(1);
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) FROM projects WHERE status='Active'")) {
                if (rs.next()) activeProjects = rs.getInt(1);
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) FROM resident_requests WHERE status='Pending'")) {
                if (rs.next()) pendingApprovals = rs.getInt(1);
            }

            // MODIFIED: Services Completed must be based on update_status = 'Done' for consistency
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) FROM resident_requests WHERE update_status='Done'")) { // <-- CORRECTED QUERY HERE
                if (rs.next()) servicesCompleted = rs.getInt(1);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                parentFrame,
                "Database error:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        // ================= DASHBOARD CARDS =================
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        dashboardPanel.setOpaque(false);

        dashboardPanel.add(createMetricCard("Total Registered Residents", totalResidents, new Color(40, 167, 69)));
        dashboardPanel.add(createMetricCard("Active Community Projects", activeProjects, ACCENT_COLOR));
        dashboardPanel.add(createMetricCard("Pending Service Approvals", pendingApprovals, new Color(255, 193, 7)));
        dashboardPanel.add(createMetricCard("Services Completed", servicesCompleted, new Color(184, 1, 1)));

        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    // ================= METRIC CARD =================
    private JPanel createMetricCard(String title, int count, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel countLabel = new JLabel(String.valueOf(count), SwingConstants.CENTER);
        countLabel.setFont(new Font("Leelawadee", Font.BOLD, 53));
        countLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Leelawadee", Font.PLAIN, 23));
        titleLabel.setForeground(PRIMARY_COLOR);

        card.add(countLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    // ================= NAV BUTTON =================
    private RoundButton createStaffNavLink(String text, boolean isActive) {
        RoundButton button = new RoundButton(text, TEAL_ACCENT);
        button.setFont(new Font("Leelawadee", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(150, 54));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        button.addActionListener(e -> {
            String view = switch (text) {
                case "Dashboard" -> STAFF_DASHBOARD_VIEW;
                case "Announcement" -> STAFF_ANNOUNCEMENT_VIEW;
                case "Residents" -> RESIDENTS_LISTS_VIEW;
                case "Requests" -> REQUESTS_VIEW;
                default -> "";
            };
            if (!view.isEmpty()) cardLayout.show(cardPanel, view);
        });

        button.setBackground(isActive ? TEAL_ACCENT : BG_COLOR);
        button.setForeground(isActive ? Color.WHITE : PRIMARY_COLOR);

        return button;
    }
}