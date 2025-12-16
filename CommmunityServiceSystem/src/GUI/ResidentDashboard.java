package GUI;

import components.RoundButton;
import utils.DBConnection; // Import the DBConnection class
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ResidentDashboard extends JPanel {

    private static final String RESIDENT_LOGIN_VIEW = "ResidentLogin";
    private static final String RESIDENT_ANNOUNCEMNENT_VIEW = "ResidentAnnouncement";
    private static final String RESIDENT_NOTIFICATION_VIEW = "ResidentNotification";
    private static final String ADD_REQUESTS_VIEW = "AddRequests";
    private static final String RESIDENT_PROFILE_VIEW = "ResidentProfile";
    
    private static final Color MAIN_BG_LIGHT_BLUE = new Color(225, 240, 255);
    private static final Color MENU_PANEL_BG = Color.WHITE;
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEAL_BUTTON = new Color(0, 191, 191);
    private static final Color DARK_GRAY_TEXT = new Color(40, 40, 40);

    // Stats Card Colors
    private static final Color POPULATION_COUNT_COLOR = new Color(0, 150, 0);       // Green
    private static final Color PROJECTS_COUNT_COLOR = new Color(50, 150, 200);     // Blue
    private static final Color SERVICES_COUNT_COLOR = new Color(200, 0, 0);        // Red
    
    // Border color around the entire dashboard
    private static final Color OUTER_BORDER_COLOR = new Color(180, 220, 240);

    // Data (fetched from DB)
    private int totalPopulation = 0; 
    private int activeProjects = 0; 
    private int servicesCompleted = 0; 
    
    private String residentName; 

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JFrame parentFrame;

    public ResidentDashboard(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel, String residentName) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.residentName = residentName;
        this.parentFrame = parentFrame;
        
        fetchDashboardData(); 

        setLayout(new BorderLayout());
        setBackground(MAIN_BG_LIGHT_BLUE);
        setBorder(new LineBorder(OUTER_BORDER_COLOR, 3)); 

        add(createHeaderPanel(parentFrame), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); 

        contentPanel.add(createMenuPanel(), BorderLayout.WEST);
        contentPanel.add(createCenterAndCardsPanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void fetchDashboardData() {
        try (Connection conn = DBConnection.getConnection()) {

            // Total Residents
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM residents")) {
                if (rs.next()) {
                    totalPopulation = rs.getInt("total");
                }
            }

            // Active Projects
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total_active FROM projects WHERE status = 'Active'")) {
                if (rs.next()) {
                    activeProjects = rs.getInt("total_active");
                }
            }

            // Services Completed
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS completed FROM resident_requests WHERE status = 'Completed'")) {
                if (rs.next()) {
                    servicesCompleted = rs.getInt("completed");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame,
                    "Failed to fetch data from database.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);

            totalPopulation = 0;
            activeProjects = 0;
            servicesCompleted = 0;
        }
    }

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
            
            // Shadow
            g2d.setColor(new Color(0, 0, 0, 10));
            g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, cornerRadius, cornerRadius);

            // Background
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

        JLabel welcomeLabel = new JLabel("WELCOME, " + residentName.toUpperCase() + "!");
        welcomeLabel.setFont(new Font("STXinwei", Font.BOLD, 26)); 
        welcomeLabel.setForeground(DARK_GRAY_TEXT); 
        panel.add(welcomeLabel, BorderLayout.WEST);

        RoundButton logoutButton = new RoundButton("LOG OUT", SERVICES_COUNT_COLOR);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setPreferredSize(new Dimension(120, 40));

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION && cardLayout != null) {
                cardLayout.show(cardPanel, RESIDENT_LOGIN_VIEW);
            }
        });

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(logoutButton);
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
        gbc.gridy = 1; panel.add(createNavLink("Dashboard", true), gbc);
        gbc.gridy = 2; panel.add(createNavLink("Notification", false), gbc);
        gbc.gridy = 3; panel.add(createNavLink("Announcement", false), gbc);
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
                cardLayout.show(cardPanel, RESIDENT_ANNOUNCEMNENT_VIEW);
            } else if ("Notification".equals(text)) {
                cardLayout.show(cardPanel, RESIDENT_NOTIFICATION_VIEW);
            } else if ("Add Requests".equals(text)) {
                cardLayout.show(cardPanel, ADD_REQUESTS_VIEW);
            } else if ("Profile".equals(text)) {
                cardLayout.show(cardPanel, RESIDENT_PROFILE_VIEW);
            } else {
                System.out.println("Navigating Resident to: " + text);
            }
        });
        return button;
    }

    private JPanel createCenterAndCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setOpaque(false);

        panel.add(createLogoMottoCard());
        panel.add(createStatsCard(String.valueOf(totalPopulation), "Total Population", POPULATION_COUNT_COLOR));
        panel.add(createStatsCard(String.valueOf(activeProjects), "Active Community Projects", PROJECTS_COUNT_COLOR));
        panel.add(createStatsCard(String.valueOf(servicesCompleted), "Services Completed", SERVICES_COUNT_COLOR));

        return panel;
    }

    private JPanel createLogoMottoCard() {
        RoundedPanel panel = new RoundedPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 5.0;

        JLabel logoLabel = new JLabel();
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/CSSLogo.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            logoLabel.setText("Logo Missing (CSSLogo.png)");
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 0;
        gbc.insets = new Insets(20, 10, 5, 10);
        gbc.weighty = 0.7;
        panel.add(logoLabel, gbc);

        JLabel systemName = new JLabel("COMMUNITY SERVICE SYSTEM");
        systemName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        systemName.setForeground(DARK_GRAY_TEXT);
        systemName.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weighty = 0.1;
        panel.add(systemName, gbc);

        JLabel motto = new JLabel("“Serving Together for a Better Tomorrow”");
        motto.setFont(new Font("Times New Roman", Font.ITALIC, 14));
        motto.setForeground(new Color(100, 100, 100));
        motto.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 10, 20, 10);
        gbc.weighty = 0.2;
        panel.add(motto, gbc);

        return panel;
    }

    private JPanel createStatsCard(String count, String label, Color countColor) {
        RoundedPanel panel = new RoundedPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 60)); 
        countLabel.setForeground(countColor);
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 0;
        gbc.weighty = 0.8;
        gbc.insets = new Insets(10, 10, 0, 10);
        panel.add(countLabel, gbc);

        JLabel descriptorLabel = new JLabel(label);
        descriptorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        descriptorLabel.setForeground(DARK_GRAY_TEXT);
        descriptorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridy = 1;
        gbc.weighty = 0.2;
        gbc.insets = new Insets(0, 10, 20, 10);
        panel.add(descriptorLabel, gbc);

        return panel;
    }
}
