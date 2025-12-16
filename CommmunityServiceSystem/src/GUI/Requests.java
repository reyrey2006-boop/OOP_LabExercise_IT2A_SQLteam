package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import java.awt.event.*;
import utils.DBConnection;

public class Requests extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private static final String STAFF_LOGIN_VIEW = "StaffLogin";
    private static final String STAFF_ANNOUNCEMENT_VIEW = "StaffAnnouncement";
    private static final String STAFF_DASHBOARD_VIEW = "StaffDashboard";
    private static final String RESIDENTS_LISTS_VIEW = "ResidentsLists";
    private static final String REQUESTS_VIEW = "Requests";

    private static final String PENDING_REQUESTS_VIEW = "PendingRequests";
    private static final String APPROVED_REQUESTS_VIEW = "ApprovedRequests";
    private static final String DECLINED_REQUESTS_VIEW = "DeclinedRequests";

    private static final Color DARK_TEXT = new Color(0, 0, 0);
    private static final Color TEAL_ACCENT = new Color(0, 191, 191);
    private static final Color BG_COLOR = new Color(225, 240, 255);
    private static final Color PRIMARY_COLOR = new Color(50, 70, 90);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);

    private int pendingRequests = 0;
    private int approvedRequests = 0;
    private int declinedRequests = 0;

    public Requests(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        fetchRequestCountsFromDB();

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // ================= HEADER =================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        headerPanel.setPreferredSize(new Dimension(800, 70));

        JLabel titleLabel = new JLabel("REQUEST LIST");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 30));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // ================= CONTENT =================
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // ================= SIDEBAR =================
        JPanel navPanel = new JPanel(new GridBagLayout());
        navPanel.setBackground(Color.WHITE);
        navPanel.setPreferredSize(new Dimension(200, 500));
        navPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);

        JLabel navTitle = new JLabel("MENU");
        navTitle.setFont(new Font("Leelawadee", Font.BOLD, 30));
        navTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        navPanel.add(navTitle, gbc);

        gbc.gridy = 1; navPanel.add(createStaffNavLink("Dashboard", false), gbc);
        gbc.gridy = 2; navPanel.add(createStaffNavLink("Announcement", false), gbc);
        gbc.gridy = 3; navPanel.add(createStaffNavLink("Residents", false), gbc);
        gbc.gridy = 4; navPanel.add(createStaffNavLink("Requests", true), gbc);

        gbc.gridy = 5;
        gbc.weighty = 1;
        navPanel.add(Box.createVerticalGlue(), gbc);

        contentPanel.add(navPanel, BorderLayout.WEST);

        // ================= DASHBOARD =================
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setOpaque(false);

        JPanel topMetricPanel = new JPanel(new GridLayout(1, 1));
        topMetricPanel.setOpaque(false);
        topMetricPanel.setPreferredSize(new Dimension(0, 180));
        topMetricPanel.add(createMetricCard(
                "Pending Request",
                pendingRequests,
                new Color(40, 167, 69)
        ));

        JPanel bottomMetricPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomMetricPanel.setOpaque(false);
        bottomMetricPanel.add(createMetricCard(
                "Approved Requests",
                approvedRequests,
                ACCENT_COLOR
        ));
        bottomMetricPanel.add(createMetricCard(
                "Declined Requests",
                declinedRequests,
                new Color(220, 53, 69)
        ));

        JPanel combinedMetrics = new JPanel();
        combinedMetrics.setLayout(new BoxLayout(combinedMetrics, BoxLayout.Y_AXIS));
        combinedMetrics.setOpaque(false);
        combinedMetrics.add(topMetricPanel);
        combinedMetrics.add(Box.createVerticalStrut(30));
        combinedMetrics.add(bottomMetricPanel);

        dashboardPanel.add(combinedMetrics, BorderLayout.CENTER);
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    // ================= DATABASE =================
    private int getCountForStatus(Connection conn, String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM resident_requests WHERE status = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private void fetchRequestCountsFromDB() {
        try (Connection conn = DBConnection.getConnection()) {
            pendingRequests = getCountForStatus(conn, "Pending");
            approvedRequests = getCountForStatus(conn, "Approved");
            declinedRequests = getCountForStatus(conn, "Declined");
        } catch (SQLException e) {
            pendingRequests = approvedRequests = declinedRequests = 0;
        }
    }

    // ================= ROUNDED CARD =================
    private JPanel createMetricCard(String title, int count, Color color) {

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 25, 25);

                // card
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12, 25, 25);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(30, 20, 30, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel countLabel = new JLabel(String.valueOf(count), SwingConstants.CENTER);
        countLabel.setFont(new Font("Leelawadee", Font.BOLD, 52));
        countLabel.setForeground(color);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Leelawadee", Font.PLAIN, 22));
        titleLabel.setForeground(PRIMARY_COLOR);

        card.add(countLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                switch (title) {
                    case "Pending Request": cardLayout.show(cardPanel, PENDING_REQUESTS_VIEW); break;
                    case "Approved Requests": cardLayout.show(cardPanel, APPROVED_REQUESTS_VIEW); break;
                    case "Declined Requests": cardLayout.show(cardPanel, DECLINED_REQUESTS_VIEW); break;
                }
            }

            public void mouseEntered(MouseEvent e) {
                card.setBorder(new EmptyBorder(25, 15, 35, 25));
                card.repaint();
            }

            public void mouseExited(MouseEvent e) {
                card.setBorder(new EmptyBorder(30, 20, 30, 20));
                card.repaint();
            }
        });

        return card;
    }

    // ================= NAV BUTTON =================
    private RoundButton createStaffNavLink(String text, boolean isActive) {
        RoundButton button = new RoundButton(text, TEAL_ACCENT);
        button.setFont(new Font("Leelawadee", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(150, 54));

        if (isActive) {
            button.setBackground(TEAL_ACCENT);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(BG_COLOR);
            button.setForeground(PRIMARY_COLOR);
        }

        button.addActionListener(e -> {
            switch (text) {
                case "Dashboard": cardLayout.show(cardPanel, STAFF_DASHBOARD_VIEW); break;
                case "Announcement": cardLayout.show(cardPanel, STAFF_ANNOUNCEMENT_VIEW); break;
                case "Residents": cardLayout.show(cardPanel, RESIDENTS_LISTS_VIEW); break;
                case "Requests": cardLayout.show(cardPanel, REQUESTS_VIEW); break;
            }
        });

        return button;
    }
}
