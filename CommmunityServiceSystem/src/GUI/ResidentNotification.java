package GUI;

import components.RoundButton;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.LineBorder;
import utils.DBConnection;

public class ResidentNotification extends JPanel {

    // --- Navigation Views ---
    private static final String RESIDENT_DASHBOARD_VIEW = "ResidentDashboard"; 
    private static final String RESIDENT_ANNOUNCEMENT_VIEW = "ResidentAnnouncement"; 
    private static final String RESIDENT_NOTIFICATION_VIEW = "ResidentNotification"; 
    private static final String ADD_REQUESTS_VIEW = "AddRequests";
    private static final String RESIDENT_PROFILE_VIEW = "ResidentProfile";

    // --- Professional Color Palette (Synced with ResidentAnnouncement for Menu) ---
    private static final Color BG_SOFT_BLUE = new Color(225, 240, 255);
    private static final Color MENU_PANEL_BG = Color.WHITE;
    private static final Color CARD_WHITE = Color.WHITE;
    private static final Color DARK_GRAY_TEXT = new Color(40, 40, 40);
    private static final Color TEXT_MUTED = new Color(120, 120, 120);
    private static final Color TEAL_BUTTON = new Color(0, 191, 191); // Match Announcement

    // --- Components & State ---
    private JPanel notificationsListPanel;
    private String residentEmail;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Timer refreshTimer;

    private static class NotificationData {
        String title, dateTime, message, status, workStatus;

        NotificationData(String title, String dateTime, String message, String status, String workStatus) {
            this.title = title;
            this.dateTime = dateTime;
            this.message = message;
            this.status = status;
            this.workStatus = workStatus;
        }
    }

    public ResidentNotification(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel, String residentEmail) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.residentEmail = residentEmail;

        setLayout(new BorderLayout());
        setBackground(BG_SOFT_BLUE);

        // Header Section
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main Content Area
        JPanel mainContentWrapper = new JPanel(new BorderLayout(15, 15));
        mainContentWrapper.setOpaque(false);
        mainContentWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainContentWrapper.add(createMenuPanel(), BorderLayout.WEST);
        mainContentWrapper.add(createNotificationContentPanel(), BorderLayout.CENTER);

        add(mainContentWrapper, BorderLayout.CENTER);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                startAutoRefresh();
                refreshNotifications();
            }
            @Override
            public void componentHidden(ComponentEvent e) {
                if (refreshTimer != null) refreshTimer.stop();
            }
        });
    }

    private void startAutoRefresh() {
        if (refreshTimer == null) {
            refreshTimer = new Timer(5000, e -> refreshNotifications());
        }
        if (!refreshTimer.isRunning()) refreshTimer.start();
    }

    private void refreshNotifications() {
        SwingWorker<List<NotificationData>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<NotificationData> doInBackground() {
                return fetchNotifications();
            }

            @Override
            protected void done() {
                try {
                    List<NotificationData> notifications = get();
                    notificationsListPanel.removeAll();
                    if (notifications.isEmpty()) {
                        showEmptyState();
                    } else {
                        for (NotificationData data : notifications) {
                            notificationsListPanel.add(createNotificationCard(data));
                        }
                    }
                    notificationsListPanel.revalidate();
                    notificationsListPanel.repaint();
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }

    private List<NotificationData> fetchNotifications() {
        List<NotificationData> notifications = new ArrayList<>();
        String sql = "SELECT category, status, update_status, date_submitted FROM resident_requests " +
                     "WHERE email_address = ? ORDER BY date_submitted DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.residentEmail); 
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new NotificationData(
                        "Request Update: " + rs.getString("category"),
                        rs.getString("date_submitted") != null ? rs.getString("date_submitted") : "Just now",
                        "Update for your request.",
                        rs.getString("status"), rs.getString("update_status")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return notifications;
    }

    private void showEmptyState() {
        JLabel emptyMsg = new JLabel("No new updates yet.");
        emptyMsg.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        emptyMsg.setForeground(TEXT_MUTED);
        emptyMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        notificationsListPanel.add(Box.createVerticalGlue());
        notificationsListPanel.add(emptyMsg);
        notificationsListPanel.add(Box.createVerticalGlue());
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 25, 10, 25));
        JLabel title = new JLabel("Request Progress");
        title.setFont(new Font("STXinwei", Font.BOLD, 28));
        title.setForeground(DARK_GRAY_TEXT);
        panel.add(title);
        return panel;
    }

    // --- MENU PANEL MATCHED TO RESIDENT ANNOUNCEMENT ---
    private JPanel createMenuPanel() {
        RoundedPanel panel = new RoundedPanel(new GridBagLayout());
        panel.setBackground(MENU_PANEL_BG);
        panel.setPreferredSize(new Dimension(200, 500));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel menuTitle = new JLabel("MENU");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        menuTitle.setForeground(DARK_GRAY_TEXT);
        menuTitle.setHorizontalAlignment(SwingConstants.CENTER);
        menuTitle.setBorder(new EmptyBorder(15, 10, 15, 0));

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 15, 10, 15);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(menuTitle, gbc);

        gbc.insets = new Insets(5, 10, 5, 10);
        
        gbc.gridy = 1; panel.add(createNavLink("Dashboard", false), gbc);
        gbc.gridy = 2; panel.add(createNavLink("Req Progress", true), gbc);
        gbc.gridy = 3; panel.add(createNavLink("Announcement", false), gbc); 
        gbc.gridy = 4; panel.add(createNavLink("Add Requests", false), gbc);
        gbc.gridy = 5; panel.add(createNavLink("Profile", false), gbc);

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    // --- NAV LINK MATCHED TO RESIDENT ANNOUNCEMENT ---
    private RoundButton createNavLink(String text, boolean isSelected) {
        // Sync unselected color to TEAL_BUTTON (0, 191, 191) from Announcement
        Color defaultColor = isSelected ? new Color(0, 150, 150) : TEAL_BUTTON; 
        RoundButton button = new RoundButton(text, defaultColor);
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        Dimension size = new Dimension(200, 45);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            String view = switch (text) {
                case "Dashboard" -> RESIDENT_DASHBOARD_VIEW;
                case "Announcement" -> RESIDENT_ANNOUNCEMENT_VIEW;
                case "Req Progress" -> RESIDENT_NOTIFICATION_VIEW;
                case "Add Requests" -> ADD_REQUESTS_VIEW;
                case "Profile" -> RESIDENT_PROFILE_VIEW;
                default -> RESIDENT_NOTIFICATION_VIEW;
            };
            cardLayout.show(cardPanel, view);
        });
        return button;
    }

    private JPanel createNotificationContentPanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        notificationsListPanel = new JPanel();
        notificationsListPanel.setLayout(new BoxLayout(notificationsListPanel, BoxLayout.Y_AXIS));
        notificationsListPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(notificationsListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BG_SOFT_BLUE);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
    }

    private JPanel createNotificationCard(NotificationData data) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(15, 10));
        card.setBackground(CARD_WHITE);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel title = new JLabel(data.title);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(DARK_GRAY_TEXT);
        JLabel date = new JLabel(data.dateTime);
        date.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        date.setForeground(TEXT_MUTED);
        topRow.add(title, BorderLayout.WEST);
        topRow.add(date, BorderLayout.EAST);

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        badgePanel.setOpaque(false);
        badgePanel.add(createStatusBadge("ADMIN: " + data.status, getStatusColor(data.status)));
        badgePanel.add(createStatusBadge("WORK: " + data.workStatus, getStatusColor(data.workStatus)));

        card.add(topRow, BorderLayout.NORTH);
        card.add(badgePanel, BorderLayout.CENTER);

        JPanel marginWrapper = new JPanel(new BorderLayout());
        marginWrapper.setOpaque(false);
        marginWrapper.setBorder(new EmptyBorder(0, 0, 12, 0));
        marginWrapper.add(card, BorderLayout.CENTER);
        return marginWrapper;
    }

    private JLabel createStatusBadge(String text, Color bg) {
        JLabel label = new JLabel("  " + text.toUpperCase() + "  ");
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setOpaque(true);
        label.setBackground(bg);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return label;
    }

    private Color getStatusColor(String status) {
        if (status == null) return TEXT_MUTED;
        return switch (status.toLowerCase()) {
            case "approved", "done" -> new Color(40, 167, 69);
            case "declined" -> new Color(220, 53, 69);
            case "pending", "in process" -> new Color(255, 193, 7);
            default -> new Color(0, 150, 150);
        };
    }

    // --- ROUNDED PANEL MATCHED TO RESIDENT ANNOUNCEMENT (No Border) ---
    private class RoundedPanel extends JPanel {
        public RoundedPanel(LayoutManager layout) { super(layout); setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            g2.dispose();
        }
    }
}