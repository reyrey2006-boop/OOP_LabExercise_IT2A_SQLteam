package GUI;

import components.RoundButton;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.LineBorder;
import utils.DBConnection; // Assumed utility for DB connection

public class ResidentNotification extends JPanel {
    
    // Constants for CardLayout views
    private static final String RESIDENT_DASHBOARD_VIEW = "ResidentDashboard"; 
    private static final String RESIDENT_ANNOUNCEMENT_VIEW = "ResidentAnnouncement"; 
    private static final String RESIDENT_NOTIFICATION_VIEW = "ResidentNotification"; 
    private static final String ADD_REQUESTS_VIEW = "AddRequests";
    private static final String RESIDENT_PROFILE_VIEW = "ResidentProfile";
    
    // --- Colors ---
    private static final Color MAIN_BG_LIGHT_BLUE = new Color(240, 248, 255);
    private static final Color MENU_PANEL_BG = Color.WHITE;
    private static final Color CARD_BG = Color.WHITE; 
    
    // Aligned TEAL color names with ResidentDashboard
    private static final Color TEAL_BUTTON = new Color(0, 191, 191);   
    private static final Color DARK_GRAY_TEXT = new Color(40, 40, 40);
    private static final Color LIGHT_BLUE_TEXT = new Color(100, 100, 100);

    // The subtle light blue border/background from the image
    private static final Color NOTIFICATION_CARD_BG_COLOR = new Color(240, 248, 255); 

    // Added outer border color from ResidentDashboard
    private static final Color OUTER_BORDER_COLOR = new Color(180, 220, 240); 

    // --- Data Model for Notifications (Simulating Request Status Updates) ---
    private static class NotificationData {
        String title;
        String dateTime; 
        String message; 

        public NotificationData(String title, String dateTime, String message) {
            this.title = title;
            this.dateTime = dateTime;
            this.message = message;
        }
    }
    
    // --- Instance Variables ---
    private String residentName;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // --- Constructor ---
    public ResidentNotification(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel, String residentName) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.residentName = residentName;

        setLayout(new BorderLayout());
        setBackground(MAIN_BG_LIGHT_BLUE);
        
        // Outer border to match ResidentDashboard
        setBorder(new LineBorder(OUTER_BORDER_COLOR, 3));

        // Header panel to match ResidentDashboard
        add(createHeaderPanel(parentFrame), BorderLayout.NORTH);

        // Main content area layout
        JPanel mainContentWrapper = new JPanel(new BorderLayout(15, 15));
        mainContentWrapper.setOpaque(false);
        mainContentWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Left menu panel
        mainContentWrapper.add(createMenuPanel(), BorderLayout.WEST);

        // Right announcement content area
        mainContentWrapper.add(createNotificationContentPanel(), BorderLayout.CENTER);

        add(mainContentWrapper, BorderLayout.CENTER);
    }


    // --- Database/Data Fetch Method (Simulated for Demo) ---
    private List<NotificationData> fetchNotifications() {
        List<NotificationData> notifications = new ArrayList<>();
        String date = "December 4, 2025";
        String time = "12:09 PM";
        String dateTime = date + " " + time; 

        notifications.add(new NotificationData(
            "Community Service Updates",
            dateTime,
            "Your request is approved/declined"
        ));
        notifications.add(new NotificationData(
            "Community Service Updates",
            dateTime,
            "Your request is in process"
        ));
        notifications.add(new NotificationData(
            "Community Service Updates",
            dateTime,
            "Your request is in successfully DONE! Thank for helping our community :)"
        ));
        
        return notifications;
    }

    // --- Custom Rounded Panel (for Menu) ---
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 25;

        public RoundedPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2d.dispose();
        }
    }

    private JPanel createHeaderPanel(JFrame parentFrame) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(800, 60));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel welcomeLabel = new JLabel(" ");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26)); 
        welcomeLabel.setForeground(DARK_GRAY_TEXT); 
        panel.add(welcomeLabel, BorderLayout.WEST);

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setOpaque(false);
        panel.add(buttonWrapper, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMenuPanel() {
        ResidentNotification.RoundedPanel panel = new ResidentNotification.RoundedPanel(new GridBagLayout());
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
        
        // Navigation Links
        gbc.gridy = 1; panel.add(createNavLink("Dashboard", false), gbc);
        gbc.gridy = 2; panel.add(createNavLink("Notification", false), gbc);
        gbc.gridy = 3; panel.add(createNavLink("Announcement", true), gbc); 
        gbc.gridy = 4; panel.add(createNavLink("Add Requests", false), gbc);
        gbc.gridy = 5; panel.add(createNavLink("Profile", false), gbc);

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private RoundButton createNavLink(String text, boolean isSelected) {
        Color defaultColor = isSelected ? TEAL_BUTTON : new Color(0, 200, 200); 
        RoundButton button = new RoundButton(text, defaultColor);
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        Dimension size = new Dimension(200, 45);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            if ("Dashboard".equals(text)) {
                cardLayout.show(cardPanel, "ResidentDashboard"); 
            } else if ("Announcement".equals(text)) {
                cardLayout.show(cardPanel, "ResidentAnnouncement");
            } else if ("Notification".equals(text)) {
                cardLayout.show(cardPanel, RESIDENT_NOTIFICATION_VIEW);
            } else if ("Add Requests".equals(text)) {
                cardLayout.show(cardPanel, ADD_REQUESTS_VIEW);
            } else if ("Profile".equals(text)) {
                cardLayout.show(cardPanel, "ResidentProfile");
            } else {
                System.out.println("Navigating Resident to: " + text + " (Link not fully implemented)");
            }
        });
        return button;
    }

    private JPanel createNotificationContentPanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("NOTIFICATION");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 26));
        titleLabel.setForeground(DARK_GRAY_TEXT);
        titlePanel.add(titleLabel);
        wrapperPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel notificationsListPanel = new JPanel();
        notificationsListPanel.setLayout(new BoxLayout(notificationsListPanel, BoxLayout.Y_AXIS));
        notificationsListPanel.setOpaque(false); 

        for (NotificationData data : fetchNotifications()) {
            notificationsListPanel.add(createNotificationCard(data));
        }

        JScrollPane scrollPane = new JScrollPane(notificationsListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(MAIN_BG_LIGHT_BLUE);

        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
    }

    private JPanel createNotificationCard(NotificationData data) {

        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(CARD_BG); 
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); 
        card.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(data.title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(DARK_GRAY_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel dateTimeLabel = new JLabel(data.dateTime);
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateTimeLabel.setForeground(LIGHT_BLUE_TEXT);
        headerPanel.add(dateTimeLabel, BorderLayout.EAST);

        card.add(headerPanel, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel(data.message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setForeground(DARK_GRAY_TEXT);
        messageLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        card.add(messageLabel, BorderLayout.CENTER);
        
        JPanel backgroundWrapper = new JPanel(new BorderLayout());
        backgroundWrapper.setBackground(NOTIFICATION_CARD_BG_COLOR);
        backgroundWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        backgroundWrapper.add(card, BorderLayout.CENTER);
        
        JPanel finalWrapper = new JPanel(new BorderLayout());
        finalWrapper.setOpaque(false);
        finalWrapper.setBorder(new EmptyBorder(0, 0, 15, 0));
        finalWrapper.add(backgroundWrapper, BorderLayout.CENTER);

        return finalWrapper;
        
    }
}
