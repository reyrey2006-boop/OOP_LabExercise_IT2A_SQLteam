package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class WelcomeFrame extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);

    private static final String WELCOME_VIEW = "Welcome";
    private static final String RESIDENT_VIEW = "Resident";
    private static final String RESIDENT_LOGIN_VIEW = "ResidentLogin";
    private static final String STAFF_VIEW = "Staff";
    private static final String STAFF_LOGIN_VIEW = "StaffLogin";
    private static final String STAFF_DASHBOARD_VIEW = "StaffDashboard";
    private static final String STAFF_ANNOUNCEMENT_VIEW = "StaffAnnouncement";
    private static final String RESIDENTS_LISTS_VIEW = "ResidentsLists"; 
    private static final String REQUESTS_VIEW = "Requests";
    
    private static final String RESIDENT_DASHBOARD_VIEW = "ResidentDashboard";
    private static final String RESIDENT_ANNOUNCEMENT_VIEW = "ResidentAnnouncement";
    private static final String RESIDENT_NOTIFICATION_VIEW = "ResidentNotification";
    private static final String ADD_REQUESTS_VIEW = "AddRequests";
    private static final String RESIDENT_PROFILE_VIEW = "ResidentProfile";

    // --- New Constants for Request Sub-Views (Must match names used in Requests.java) ---
    private static final String PENDING_REQUESTS_VIEW = "PendingRequests";
    private static final String APPROVED_REQUESTS_VIEW = "ApprovedRequests";
    private static final String DECLINED_REQUESTS_VIEW = "DeclinedRequests";
    private static final String MANAGE_REQUESTS_VIEW = "ManageRequests";
    // -----------------------------------------------------------------------------------


    public WelcomeFrame() {
        setTitle("Community Service System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Load window icon correctly
        try {
            ImageIcon logo = new ImageIcon(getClass().getResource("/images/CSSLogo.png"));
            this.setIconImage(logo.getImage());
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load /images/CSSLogo.png for app icon");
        }

        // --- Add pages ---
        
        // General Views
        cardPanel.add(getWelcomePanel(), WELCOME_VIEW);
        cardPanel.add(new Resident(this, cardLayout, cardPanel), RESIDENT_VIEW);
        cardPanel.add(new ResidentLogin(this, cardLayout, cardPanel), RESIDENT_LOGIN_VIEW);
        cardPanel.add(new Staff(this, cardLayout, cardPanel), STAFF_VIEW);
        cardPanel.add(new StaffLogin(this, cardLayout, cardPanel), STAFF_LOGIN_VIEW);
        
        // Resident Main Views (Passing a hardcoded name for demonstration)
        String dummyResidentName = " ";
        
        // Initializing and adding Resident Dashboard
        cardPanel.add(new ResidentDashboard(this, cardLayout, cardPanel, dummyResidentName), RESIDENT_DASHBOARD_VIEW);
        cardPanel.add(new ResidentAnnouncement(this, cardLayout, cardPanel, dummyResidentName), RESIDENT_ANNOUNCEMENT_VIEW);
        cardPanel.add(new ResidentNotification(this, cardLayout, cardPanel, dummyResidentName), RESIDENT_NOTIFICATION_VIEW);
        cardPanel.add(new AddRequests(this, cardLayout, cardPanel, dummyResidentName), ADD_REQUESTS_VIEW);
        cardPanel.add(new ResidentProfile(this, cardLayout, cardPanel, dummyResidentName), RESIDENT_PROFILE_VIEW);
        
        
        // Staff Main Navigation Views
        cardPanel.add(new StaffDashboard(this, cardLayout, cardPanel), STAFF_DASHBOARD_VIEW);
        cardPanel.add(new StaffAnnouncement(this, cardLayout, cardPanel), STAFF_ANNOUNCEMENT_VIEW);
        cardPanel.add(new ResidentLists(this, cardLayout, cardPanel), RESIDENTS_LISTS_VIEW);
        cardPanel.add(new Requests(this, cardLayout, cardPanel), REQUESTS_VIEW);
        
        // --- Staff Requests Sub-Views (New additions) ---
        cardPanel.add(new PendingRequests(this, cardLayout, cardPanel), PENDING_REQUESTS_VIEW);
        cardPanel.add(new ApprovedRequests(this, cardLayout, cardPanel), APPROVED_REQUESTS_VIEW);
        cardPanel.add(new DeclinedRequests(this, cardLayout, cardPanel), DECLINED_REQUESTS_VIEW);
        cardPanel.add(new ManageRequests(this, cardLayout, cardPanel), MANAGE_REQUESTS_VIEW);

        add(cardPanel);
    }

    private JPanel getWelcomePanel() {
        // ... (rest of the Welcome panel logic remains the same)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(220, 235, 250));

        // Top spacer
        JPanel topSpacer = new JPanel();
        topSpacer.setOpaque(false);
        topSpacer.setPreferredSize(new Dimension(0, 50));
        mainPanel.add(topSpacer, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(100, 0, 50, 0));

        // Load logo image correctly using NetBeans resource system
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/CSSLogo.png"));
            logoLabel.setIcon(logoIcon);
        } catch (Exception e) {
            logoLabel.setText("Logo Missing (CSSLogo.png)");
            System.err.println("ERROR: Cannot load /images/CSSLogo.png");
        }

        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(20));

        // Title
        JLabel titleLabel = new JLabel("COMMUNITY SERVICE SYSTEM");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 70, 90));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(5));

        // Tagline
        JLabel taglineLabel = new JLabel("\"Serving Together for a Better Tomorrow\"");
        taglineLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        taglineLabel.setForeground(new Color(100, 120, 140));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(taglineLabel);
        centerPanel.add(Box.createVerticalStrut(40));

        // Welcome Text
        JLabel welcomeLabel = new JLabel("WELCOME");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        welcomeLabel.setForeground(new Color(50, 70, 90));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createVerticalStrut(25));

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);

        Color buttonColor = new Color(0, 180, 200);

        // Staff Button
        JButton staffButton = new RoundButton("Staff", buttonColor);
        staffButton.setPreferredSize(new Dimension(150, 50));
        staffButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        staffButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        staffButton.addActionListener(e -> cardLayout.show(cardPanel, STAFF_VIEW));
        buttonPanel.add(staffButton);

        // Resident Button
        JButton residentButton = new RoundButton("Resident", buttonColor);
        residentButton.setPreferredSize(new Dimension(150, 50));
        residentButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        residentButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        residentButton.addActionListener(e -> cardLayout.show(cardPanel, RESIDENT_VIEW));
        buttonPanel.add(residentButton);

        centerPanel.add(buttonPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        return mainPanel;
    }
}