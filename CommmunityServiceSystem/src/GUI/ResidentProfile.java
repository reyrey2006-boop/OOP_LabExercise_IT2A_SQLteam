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

public class ResidentProfile extends JPanel{
    // Constants for CardLayout views
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
    private static final Color LIGHT_BLUE_TEXT = new Color(100, 100, 100);

    // The subtle light blue border/background
    private static final Color NOTIFICATION_CARD_BG_COLOR = new Color(240, 248, 255); 

    // Added outer border color from ResidentDashboard
    private static final Color OUTER_BORDER_COLOR = new Color(180, 220, 240); 
    
    // --- Instance Variables for ResidentProfile ---
    private String residentIdentifier; // RENAMED from residentName for clarity
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Dummy field initializations required by the constructor's initialization logic
    private JTextField locationOfIncidentField;
    private JTextArea descriptionOfComplaintArea;
    private JComboBox<String> categoryComboBox; 
    private JTextArea requestDescriptionArea; 
    private JTextField titleField; 

    // --- Profile Data Fields ---
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel contactLabel;
    private JLabel addressLabel;
    private JLabel birthdayLabel;
    private JLabel sexLabel;
    private JLabel statusLabel;
    
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
    
    public ResidentProfile(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel, String residentIdentifier) { // PARAMETER RENAMED
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.residentIdentifier = residentIdentifier; // INSTANCE VARIABLE UPDATED

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

        // Right announcement content area (Now incorporates scrolling and fixed submit button)
        mainContentWrapper.add(createResidentProfileContentPanel(), BorderLayout.CENTER);

        add(mainContentWrapper, BorderLayout.CENTER);
        
        // Initialize original fields to map to new fields for handleSubmitRequest compatibility (required to satisfy the original constructor code)
        locationOfIncidentField = new JTextField(); 
        descriptionOfComplaintArea = new JTextArea();
        titleField = locationOfIncidentField;
        requestDescriptionArea = descriptionOfComplaintArea;
        categoryComboBox = new JComboBox<>(new String[]{"Dummy Category"}); // Dummy initialization
        
        // Load the data immediately after initialization
        loadResidentData(residentIdentifier); // ARGUMENT UPDATED
    }
    
    private JPanel createHeaderPanel(JFrame parentFrame) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(800, 60));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Display the resident's name in the header
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
        ResidentProfile.RoundedPanel panel = new ResidentProfile.RoundedPanel(new GridBagLayout());
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
        gbc.gridy = 2; panel.add(createNavLink("Req Progress", false), gbc);
        gbc.gridy = 3; panel.add(createNavLink("Announcement", false), gbc); 
        gbc.gridy = 4; panel.add(createNavLink("Add Requests", false), gbc);
        // Set Profile to selected = true
        gbc.gridy = 5; panel.add(createNavLink("Profile", true), gbc);

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private RoundButton createNavLink(String text, boolean isSelected) {
        // Updated colors to match the screenshot buttons' appearance more closely (teal buttons)
        Color defaultColor = isSelected ? new Color(0, 150, 150) : new Color(0, 200, 200); 
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
                cardLayout.show(cardPanel, RESIDENT_DASHBOARD_VIEW); 
            } else if ("Announcement".equals(text)) {
                cardLayout.show(cardPanel, RESIDENT_ANNOUNCEMENT_VIEW);
            } else if ("Req Progress".equals(text)) {
                cardLayout.show(cardPanel, RESIDENT_NOTIFICATION_VIEW);
            } else if ("Add Requests".equals(text)) {
                cardLayout.show(cardPanel, ADD_REQUESTS_VIEW);
            } else if ("Profile".equals(text)) {
                cardLayout.show(cardPanel, RESIDENT_PROFILE_VIEW); 
            } else {
                System.out.println("Navigating Resident to: " + text + " (Link not fully implemented)");
            }
        });
        return button;
    }
    
    // Helper method to create a labeled display field
    private JPanel createDisplayPanel(String labelText, JLabel valueLabel, int gridX, int gridY, GridBagConstraints gbc, JPanel container) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(DARK_GRAY_TEXT);
        
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        valueLabel.setForeground(LIGHT_BLUE_TEXT.darker());
        valueLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 180, 180)));
        valueLabel.setPreferredSize(new Dimension(250, 30)); 
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        gbc.gridx = gridX; 
        gbc.gridy = gridY; 
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        container.add(panel, gbc);
        
        return panel;
    }


    private JPanel createResidentProfileContentPanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Title: RESIDENT PROFILE
        JLabel titleLabel = new JLabel("RESIDENT PROFILE");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 26));
        titleLabel.setForeground(DARK_GRAY_TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        wrapperPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Rounded Profile Card
        RoundedPanel profileCard = new RoundedPanel(new GridBagLayout());
        profileCard.setBackground(CARD_BG);
        profileCard.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 15, 0); 
        
        // --- 1. Header/Image Section ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        headerPanel.setOpaque(false);
        
        // Placeholder for Profile Picture
        JLabel imagePlaceholder = new JLabel("  ", SwingConstants.CENTER);
        imagePlaceholder.setPreferredSize(new Dimension(80, 80));
        imagePlaceholder.setBorder(BorderFactory.createLineBorder(LIGHT_BLUE_TEXT, 2));
        imagePlaceholder.setFont(new Font("Segoe UI", Font.BOLD, 10));
        headerPanel.add(imagePlaceholder);

        // Name and Status (will be updated by loadResidentData)
        JPanel nameStatusPanel = new JPanel(new GridLayout(2, 1));
        nameStatusPanel.setOpaque(false);
        
        nameLabel = new JLabel("Loading Full Name...");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(TEAL_BUTTON.darker());
        
        statusLabel = new JLabel("Status: Active");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        statusLabel.setForeground(Color.GREEN.darker());
        
        nameStatusPanel.add(nameLabel);
        nameStatusPanel.add(statusLabel);
        
        headerPanel.add(nameStatusPanel);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0; 
        profileCard.add(headerPanel, gbc);
        
        // Separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(OUTER_BORDER_COLOR);
        gbc.gridy = 1; gbc.insets = new Insets(15, 0, 15, 0); gbc.fill = GridBagConstraints.HORIZONTAL;
        profileCard.add(separator, gbc);
        
        // --- 2. Information Grid ---
        
        // Initialize the display labels
        emailLabel = new JLabel("N/A");
        contactLabel = new JLabel("N/A");
        addressLabel = new JLabel("N/A");
        birthdayLabel = new JLabel("N/A");
        sexLabel = new JLabel("N/A");

        // Use a new GBC for the grid layout
        GridBagConstraints infoGbc = new GridBagConstraints();
        infoGbc.anchor = GridBagConstraints.NORTHWEST;
        infoGbc.weighty = 0; // Don't stretch vertically

        // Row 1: Email and Contact
        createDisplayPanel("Email Address", emailLabel, 0, 2, infoGbc, profileCard);
        createDisplayPanel("Contact Number", contactLabel, 1, 2, infoGbc, profileCard);

        // Row 2: Birthday and Sex
        createDisplayPanel("Date of Birth", birthdayLabel, 0, 3, infoGbc, profileCard);
        createDisplayPanel("Sex", sexLabel, 1, 3, infoGbc, profileCard);
        
        // Row 3: Address (Spanning 2 columns)
        JPanel addressPanel = createDisplayPanel("Home Address", addressLabel, 0, 4, infoGbc, profileCard);
        // Manually adjust the address panel to span two columns
        infoGbc.gridwidth = 2;
        infoGbc.gridx = 0;
        infoGbc.gridy = 4;
        profileCard.add(addressPanel, infoGbc);

        // Add vertical glue to push content to the top
        gbc.gridy = 5; gbc.weighty = 1.0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.VERTICAL;
        profileCard.add(Box.createVerticalGlue(), gbc);

        // Wrap the card in a scroll pane to handle smaller screen sizes
        JScrollPane scrollPane = new JScrollPane(profileCard);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add Edit/Update button at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        RoundButton editButton = new RoundButton("Edit Profile", new Color(0, 191, 191));
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editButton.setPreferredSize(new Dimension(150, 35));
        editButton.addActionListener(e -> {
    // Create and show the new EditResidentProfileDialog
    EditResidentProfileDialog dialog = new EditResidentProfileDialog(
        (JFrame) SwingUtilities.getWindowAncestor(this), // Get the main JFrame
        residentIdentifier // Pass the current resident's email
    );
    dialog.setVisible(true);
    
    // After the dialog closes, reload the data to reflect any changes
    loadResidentData(residentIdentifier); 
});
        buttonPanel.add(editButton);
        
        wrapperPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return wrapperPanel;
    }

    /**
     * Fetches resident data from the database using the resident's identifier (which is expected to be the email).
     */
    private void loadResidentData(String residentIdentifier) {
        // SQL query correctly uses email for lookup
        String sql = "SELECT firstname, lastname, birthday, sex, contact, address, email, status FROM residents WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Use trim() to clean up the identifier from any leading/trailing spaces
            pstmt.setString(1, residentIdentifier.trim()); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("firstname");
                    String lastName = rs.getString("lastname");
                    String birthday = rs.getString("birthday");
                    String sex = rs.getString("sex");
                    String contact = rs.getString("contact");
                    String address = rs.getString("address");
                    String email = rs.getString("email");
                    String status = rs.getString("status");
                    
                    // Update UI Labels
                    nameLabel.setText(firstName + " " + lastName);
                    emailLabel.setText(email);
                    contactLabel.setText(contact);
                    addressLabel.setText(address);
                    
                    // Format birthday (Optional: to display in a nicer format)
                    try {
                        LocalDate date = LocalDate.parse(birthday);
                        birthdayLabel.setText(date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
                    } catch (Exception e) {
                        birthdayLabel.setText(birthday);
                    }
                    
                    sexLabel.setText(sex);
                    statusLabel.setText("Status: " + status);
                    
                    // Change status color
                    if ("Active".equalsIgnoreCase(status)) {
                        statusLabel.setForeground(Color.GREEN.darker());
                    } else {
                        statusLabel.setForeground(Color.RED.darker());
                    }
                    
                } else {
                    nameLabel.setText("Resident Not Found");
                    // Removed the error dialog since the login logic should ensure this data exists.
                    // If this still fails, it suggests a missing database entry, but the program can proceed with "Not Found" message.
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            nameLabel.setText("Database Error");
            JOptionPane.showMessageDialog(this, "An error occurred while fetching resident data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}