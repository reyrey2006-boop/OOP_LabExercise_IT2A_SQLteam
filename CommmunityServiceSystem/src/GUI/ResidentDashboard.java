package GUI;

import components.RoundButton;
import utils.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ResidentDashboard extends JPanel {

    private static final String RESIDENT_DASHBOARD_VIEW = "ResidentDashboard";
    private static final String RESIDENT_LOGIN_VIEW = "ResidentLogin";
    private static final String RESIDENT_ANNOUNCEMENT_VIEW = "ResidentAnnouncement";
    private static final String RESIDENT_NOTIFICATION_VIEW = "ResidentNotification";
    private static final String ADD_REQUESTS_VIEW = "AddRequests";
    private static final String RESIDENT_PROFILE_VIEW = "ResidentProfile";

    private static final Color MAIN_BG_LIGHT_BLUE = new Color(225, 240, 255);
    private static final Color MENU_PANEL_BG = Color.WHITE;
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEAL_BUTTON = new Color(0, 191, 191);
    private static final Color DARK_GRAY_TEXT = new Color(40, 40, 40);

    private static final Color POPULATION_COUNT_COLOR = new Color(0, 150, 0);
    private static final Color PROJECTS_COUNT_COLOR = new Color(50, 150, 200);
    private static final Color SERVICES_COUNT_COLOR = new Color(200, 0, 0);

    private static final Color OUTER_BORDER_COLOR = new Color(180, 220, 240);

    private int totalPopulation = 0;
    private int activeProjects = 0;
    private int servicesCompleted = 0;

    // AUTO-REFRESH TARGET LABELS
    private JLabel populationLabel;
    private JLabel projectsLabel;
    private JLabel servicesLabel;

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

        // AUTO REFRESH (NO RESTART REQUIRED)
        new Timer(2000, e -> {
            if (!isShowing()) return;
            fetchDashboardData();
            updateDashboardUI();
        }).start();

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

    // ================= DB LOGIC (Unchanged) =================
    private void fetchDashboardData() {
    try (Connection conn = DBConnection.getConnection()) {
        // This query gets the exact total count of all registered residents
        try (Statement s = conn.createStatement();
             ResultSet r = s.executeQuery("SELECT COUNT(*) FROM residents")) {
            if (r.next()) {
                totalPopulation = r.getInt(1); 
            }
        }
        
        // Active Projects count
        try (Statement s = conn.createStatement();
             ResultSet r = s.executeQuery("SELECT COUNT(*) FROM projects WHERE status='Active'")) {
            if (r.next()) activeProjects = r.getInt(1);
        }
        
        // Services Completed count
        try (Statement s = conn.createStatement();
             ResultSet r = s.executeQuery("SELECT COUNT(*) FROM resident_requests WHERE update_status='Done'")) {
            if (r.next()) servicesCompleted = r.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private void updateDashboardUI() {
        populationLabel.setText(String.valueOf(totalPopulation));
        projectsLabel.setText(String.valueOf(activeProjects));
        servicesLabel.setText(String.valueOf(servicesCompleted));
        repaint();
    }

    // ================= HEADER (Updated with Logout Validation) =================
    private JPanel createHeaderPanel(JFrame parentFrame) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(800, 60));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel welcome = new JLabel("WELCOME, " + residentName.toUpperCase() + "!");
        welcome.setFont(new Font("STXinwei", Font.BOLD, 26));
        welcome.setForeground(DARK_GRAY_TEXT);
        panel.add(welcome, BorderLayout.WEST);

        RoundButton logout = new RoundButton("LOG OUT", SERVICES_COUNT_COLOR);
        logout.setForeground(Color.WHITE);
        logout.setPreferredSize(new Dimension(120, 40));
        
        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                parentFrame, 
                    "Are you sure you want to log out?", 
                    "Confirm Logout", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                for (Component comp : cardPanel.getComponents()) {
                    if (comp instanceof ResidentLogin) {
                        ((ResidentLogin) comp).clearFields();
                        break;
                    }
                }
                cardLayout.show(cardPanel, RESIDENT_LOGIN_VIEW);
            }
        });

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        wrap.setOpaque(false);
        wrap.add(logout);
        panel.add(wrap, BorderLayout.EAST);

        return panel;
    }

    // ================= MENU (Layout Aligned with AddRequests) =================
    private JPanel createMenuPanel() {
        ResidentDashboard.RoundedPanel panel = new ResidentDashboard.RoundedPanel(new GridBagLayout());
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
        gbc.gridy = 2; panel.add(createNavLink("Req Progress", false), gbc);
        gbc.gridy = 3; panel.add(createNavLink("Announcement", false), gbc); 
        gbc.gridy = 4; panel.add(createNavLink("Add Requests", false), gbc);
        gbc.gridy = 5; panel.add(createNavLink("Profile", false), gbc);

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private RoundButton createNavLink(String text, boolean isSelected) {
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
            switch (text) {
                case "Dashboard" -> cardLayout.show(cardPanel, RESIDENT_DASHBOARD_VIEW);
                case "Announcement" -> cardLayout.show(cardPanel, RESIDENT_ANNOUNCEMENT_VIEW);
                case "Req Progress" -> cardLayout.show(cardPanel, RESIDENT_NOTIFICATION_VIEW);
                case "Add Requests" -> cardLayout.show(cardPanel, ADD_REQUESTS_VIEW);
                case "Profile" -> cardLayout.show(cardPanel, RESIDENT_PROFILE_VIEW);
            }
        });
        return button;
    }

    // ================= CENTER CONTENT =================
    private JPanel createCenterAndCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setOpaque(false);

        panel.add(createLogoMottoCard());
        panel.add(createPopulationCard());
        panel.add(createProjectsCard());
        panel.add(createServicesCard());

        return panel;
    }

    private JPanel createPopulationCard() {
        populationLabel = createStatLabel(totalPopulation, POPULATION_COUNT_COLOR);
        return createStatCard(populationLabel, "Total Population");
    }

    private JPanel createProjectsCard() {
        projectsLabel = createStatLabel(activeProjects, PROJECTS_COUNT_COLOR);
        return createStatCard(projectsLabel, "Active Community Projects");
    }

    private JPanel createServicesCard() {
        servicesLabel = createStatLabel(servicesCompleted, SERVICES_COUNT_COLOR);
        return createStatCard(servicesLabel, "Services Completed");
    }

    private JLabel createStatLabel(int value, Color color) {
        JLabel l = new JLabel(String.valueOf(value), SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 60));
        l.setForeground(color);
        return l;
    }

    private JPanel createStatCard(JLabel count, String text) {
        ResidentDashboard.RoundedPanel p = new ResidentDashboard.RoundedPanel(new GridBagLayout());
        p.setBackground(CARD_BG);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.weighty = 0.8;
        p.add(count, g);

        JLabel d = new JLabel(text, SwingConstants.CENTER);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        g.gridy = 1; g.weighty = 0.2;
        p.add(d, g);

        return p;
    }

    private JPanel createLogoMottoCard() {
        ResidentDashboard.RoundedPanel p = new ResidentDashboard.RoundedPanel(new GridBagLayout());
        p.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();

        // Logo Image
        JLabel logoImage = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/CSSLogo.png"));
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            logoImage.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            System.err.println("Dashboard Logo missing: " + e.getMessage());
        }
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        p.add(logoImage, gbc);

        // Logo Text
        JLabel l = new JLabel("COMMUNITY SERVICE SYSTEM", SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        p.add(l, gbc);

        return p;
    }

    // ================= ROUNDED PANEL =================
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
}