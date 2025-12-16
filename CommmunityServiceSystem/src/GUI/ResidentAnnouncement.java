package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import utils.DBConnection; // Assumed database connection utility

public class ResidentAnnouncement extends JPanel {
    
    // Constants copied from ResidentDashboard/ResidentAnnouncement context
    private static final String RESIDENT_DASHBOARD_VIEW = "ResidentDashboard"; 
    private static final String RESIDENT_ANNOUNCEMENT_VIEW = "ResidentAnnouncement";
    private static final String RESIDENT_NOTIFICATION_VIEW = "ResidentNotification";
    private static final String ADD_REQUESTS_VIEW = "AddRequests";
    private static final String RESIDENT_PROFILE_VIEW = "ResidentProfile";
    
    // --- Colors ---
    private static final Color MAIN_BG_LIGHT_BLUE = new Color(225, 240, 255);
    private static final Color MENU_PANEL_BG = Color.WHITE;
    private static final Color CARD_BG = Color.WHITE; 
    
    // Aligned TEAL color names with ResidentDashboard
    private static final Color TEAL_BUTTON = new Color(0, 191, 191);   
    private static final Color DARK_GRAY_TEXT = new Color(40, 40, 40);
    private static final Color SERVICES_COUNT_COLOR = new Color(200, 0, 0); 
    
    // The subtle light blue border around each announcement card
    private static final Color ANNOUNCEMENT_CARD_BORDER_COLOR = new Color(210, 230, 250);

    // Added outer border color from ResidentDashboard
    private static final Color OUTER_BORDER_COLOR = new Color(180, 220, 240); 

    // --- Data Model for Announcements (Fetched from DB) ---
    private static class AnnouncementData {
        String author;
        String dateTime; // Combination of date and time from DB
        String statement;

        public AnnouncementData(String author, String dateTime, String statement) {
            this.author = author;
            this.dateTime = dateTime;
            this.statement = statement;
        }
    }
    
    // --- Instance Variables ---
    private String residentName;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // --- Constructor ---
    public ResidentAnnouncement(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel, String residentName) {
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
        mainContentWrapper.add(createAnnouncementContentPanel(), BorderLayout.CENTER);

        add(mainContentWrapper, BorderLayout.CENTER);
    }

    // --- Database Fetch Method ---
    private List<AnnouncementData> fetchAnnouncementsFromDB() {
        List<AnnouncementData> announcements = new ArrayList<>();
        String sql = "SELECT statement, date, time FROM announcements ORDER BY date DESC, time DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String statement = rs.getString("statement");
                String date = rs.getString("date");
                String time = rs.getString("time");

                // Author is always "Admin"
                String author = "Admin";
                
                // Combine date and time for the header label
                String dateTime = date + " | " + time;

                announcements.add(new AnnouncementData(author, dateTime, statement));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading announcements: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return announcements;
    }

    // --- Custom Rounded Panel ---
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

            g2d.setColor(new Color(0, 0, 0, 10));
            g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, cornerRadius, cornerRadius);

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

    private JPanel createAnnouncementContentPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JLabel titleLabel = new JLabel("ANNOUNCEMENT");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 26));
        titleLabel.setForeground(DARK_GRAY_TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 5, 10, 0));
        wrapper.add(titleLabel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        List<AnnouncementData> announcements = fetchAnnouncementsFromDB();

        if (announcements.isEmpty()) {
             JLabel noAnnouncements = new JLabel("No announcements available at this time.");
             noAnnouncements.setFont(new Font("Segoe UI", Font.PLAIN, 16));
             noAnnouncements.setForeground(DARK_GRAY_TEXT);
             listPanel.add(noAnnouncements);
             listPanel.add(Box.createVerticalGlue());
        } else {
            for (int i = 0; i < announcements.size(); i++) {
                listPanel.add(createAnnouncementCard(announcements.get(i)));
                if (i < announcements.size() - 1) {
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createAnnouncementCard(AnnouncementData data) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ANNOUNCEMENT_CARD_BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Author label set to Admin
        JLabel authorLabel = new JLabel(data.author);
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        authorLabel.setForeground(DARK_GRAY_TEXT);
        headerPanel.add(authorLabel, BorderLayout.WEST);

        JLabel timeLabel = new JLabel(data.dateTime);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(DARK_GRAY_TEXT);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        card.add(headerPanel, BorderLayout.NORTH);

        JLabel statementLabel = new JLabel(data.statement);
        statementLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statementLabel.setForeground(DARK_GRAY_TEXT);
        statementLabel.setBorder(new EmptyBorder(5, 0, 10, 0));
        card.add(statementLabel, BorderLayout.CENTER);

        return card;
    }

    private void styleInteractionButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(TEAL_BUTTON);
        button.setBackground(CARD_BG);
        button.setBorder(new EmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
    }
}
