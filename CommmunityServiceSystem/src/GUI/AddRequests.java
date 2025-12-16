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

public class AddRequests extends JPanel {
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

    // --- Instance Variables ---
    private String residentName;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // --- Form Components (Adapted to match functionality while adhering to image) ---
    private JTextField fullNameField;
    private JTextField contactNumberField;
    private JTextField emailAddressField;
    private JTextField homeAddressField;
    private JCheckBox garbageSanitationBox;
    private JCheckBox roadStreetBox;
    private JCheckBox safetyHazardBox;
    private JCheckBox environmentalIssueBox;
    private JTextField locationOfIncidentField;
    private JTextArea descriptionOfComplaintArea;
    
    // Original fields that must be preserved for compatibility with handleSubmitRequest()
    // NOTE: These are initialized below to point to the new fields for compatibility
    private JComboBox<String> categoryComboBox; 
    private JTextArea requestDescriptionArea; 
    private JTextField titleField; 
    private RoundButton submitButton;
    private RoundButton cancelButton;
    
    // --- Custom Rounded Panel (for Menu and Form) ---
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
    
    public AddRequests(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel, String residentName) {
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

        // Right announcement content area (Now incorporates scrolling and fixed submit button)
        mainContentWrapper.add(createAddRequestsContentPanel(), BorderLayout.CENTER);

        add(mainContentWrapper, BorderLayout.CENTER);
        
        // Initialize original fields to map to new fields for handleSubmitRequest compatibility
        // NOTE: These rely on locationOfIncidentField and descriptionOfComplaintArea being initialized in createAddRequestsContentPanel()
        titleField = locationOfIncidentField;
        requestDescriptionArea = descriptionOfComplaintArea;
        categoryComboBox = new JComboBox<>(new String[]{"Dummy Category"}); // Dummy initialization
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
        AddRequests.RoundedPanel panel = new AddRequests.RoundedPanel(new GridBagLayout());
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
        gbc.gridy = 3; panel.add(createNavLink("Announcement", false), gbc); 
        // Note: Set 'Add Requests' to selected = true based on the screenshot, which shows it being the active panel
        gbc.gridy = 4; panel.add(createNavLink("Add Requests", true), gbc);
        gbc.gridy = 5; panel.add(createNavLink("Profile", false), gbc);

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
                cardLayout.show(cardPanel, "ResidentDashboard"); 
            } else if ("Announcement".equals(text)) {
                cardLayout.show(cardPanel, "ResidentAnnouncement");
            } else if ("Notification".equals(text)) {
                cardLayout.show(cardPanel, RESIDENT_NOTIFICATION_VIEW);
            } else if ("Add Requests".equals(text)) {
                cardLayout.show(cardPanel, ADD_REQUESTS_VIEW);
            } else if ("Profile".equals(text)) {
                cardLayout.show(cardPanel, "ResidentProfile");
            // Assuming a Profile view exists
            } else {
                System.out.println("Navigating Resident to: " + text + " (Link not fully implemented)");
            }
        });
        return button;
    }
    
    // Helper method to create labeled text fields as seen in the screenshot
    private JPanel createTextFieldPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(LIGHT_BLUE_TEXT);
        
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Removed fixed preferred size to allow scaling, but keep a minimum width
        textField.setPreferredSize(new Dimension(250, 25)); 
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 180, 180))); // Underline effect
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }
    
    // Helper method for the checkbox group
    private JPanel createComplaintTypePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        garbageSanitationBox = new JCheckBox("Garbage/Sanitation Issue");
        roadStreetBox = new JCheckBox("Road/Street Problem");
        safetyHazardBox = new JCheckBox("Safety Hazard");
        environmentalIssueBox = new JCheckBox("Environmental Issue");
        
        // Set fonts for checkboxes
        Font cbFont = new Font("Segoe UI", Font.PLAIN, 14);
        garbageSanitationBox.setFont(cbFont);
        roadStreetBox.setFont(cbFont);
        safetyHazardBox.setFont(cbFont);
        environmentalIssueBox.setFont(cbFont);
        
        // Remove background for transparency
        garbageSanitationBox.setOpaque(false);
        roadStreetBox.setOpaque(false);
        safetyHazardBox.setOpaque(false);
        environmentalIssueBox.setOpaque(false);
        
        // Group the checkboxes for radio-button-like behavior (select only one)
        ButtonGroup group = new ButtonGroup();
        group.add(garbageSanitationBox);
        group.add(roadStreetBox);
        group.add(safetyHazardBox);
        group.add(environmentalIssueBox);
        
        // Add checkboxes to the panel with spacing
        panel.add(garbageSanitationBox);
        panel.add(Box.createVerticalStrut(5));
        panel.add(roadStreetBox);
        panel.add(Box.createVerticalStrut(5));
        panel.add(safetyHazardBox);
        panel.add(Box.createVerticalStrut(5));
        panel.add(environmentalIssueBox);
        
        return panel;
    }

    private JPanel createAddRequestsContentPanel() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Title: RESIDENT'S REQUESTS (kept at the top, outside the scrollable area)
        JLabel titleLabel = new JLabel("RESIDENT'S REQUESTS");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 26));
        titleLabel.setForeground(DARK_GRAY_TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        wrapperPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Rounded Form Panel (the white card - this is the element that should scroll)
        RoundedPanel formPanel = new RoundedPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 15, 20); // Top, Left, Bottom, Right padding

        // --- 1. Resident Information Section ---
        JLabel residentInfoTitle = new JLabel("Resident Information");
        residentInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        formPanel.add(residentInfoTitle, gbc);

        // Field initializations for the screenshot
        fullNameField = new JTextField();
        contactNumberField = new JTextField();
        emailAddressField = new JTextField();
        homeAddressField = new JTextField();

        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        gbc.insets = new Insets(5, 0, 5, 0); 
        
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(createTextFieldPanel("Full name", fullNameField), gbc);
        gbc.gridy = 2; formPanel.add(createTextFieldPanel("Contact Number", contactNumberField), gbc);
        gbc.gridy = 3; formPanel.add(createTextFieldPanel("Email Address (Optional)", emailAddressField), gbc);
        gbc.gridy = 4; formPanel.add(createTextFieldPanel("Home Address", homeAddressField), gbc); 

        // --- 2. Complaint Details / Description of Complaint ---
        // This panel is the container for the Complaint Type and Location fields
        JPanel complaintTypeAndLocationPanel = new JPanel(new BorderLayout(0, 15));
        complaintTypeAndLocationPanel.setOpaque(false);
        
        // A. Complaint Type (Left Column Top)
        JPanel complaintTypePanel = new JPanel(new BorderLayout(0, 10));
        complaintTypePanel.setOpaque(false);
        
        JLabel complaintTypeLabel = new JLabel("Type of Complaint (select one):");
        complaintTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        complaintTypePanel.add(complaintTypeLabel, BorderLayout.NORTH);
        complaintTypePanel.add(createComplaintTypePanel(), BorderLayout.CENTER);
        
        complaintTypeAndLocationPanel.add(complaintTypePanel, BorderLayout.NORTH);
        
        // B. Location of the Incident (Left Column Bottom)
        JPanel locationPanel = new JPanel(new BorderLayout(0, 5));
        locationPanel.setOpaque(false);
        
        JLabel locationLabel = new JLabel("Location of the Incident");
        locationLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Title size
        locationLabel.setBorder(new EmptyBorder(20, 0, 5, 0)); // Separator
        
        locationOfIncidentField = new JTextField();
        locationOfIncidentField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        locationOfIncidentField.setPreferredSize(new Dimension(300, 30));
        locationOfIncidentField.setBorder(new LineBorder(new Color(180, 180, 180), 1));
        
        locationPanel.add(locationLabel, BorderLayout.NORTH);
        locationPanel.add(locationOfIncidentField, BorderLayout.CENTER);
        
        complaintTypeAndLocationPanel.add(locationPanel, BorderLayout.CENTER);


        // C. Description of the Complaint (Right Column)
        JPanel descriptionPanel = new JPanel(new BorderLayout(0, 5));
        descriptionPanel.setOpaque(false);
        
        JLabel descriptionLabel = new JLabel("Description of the Complaint");
        descriptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel descriptionSubLabel = new JLabel("Provide a clear and complete explanation of what happened:");
        descriptionSubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionSubLabel.setForeground(LIGHT_BLUE_TEXT);
        
        descriptionOfComplaintArea = new JTextArea("Type here...");
        descriptionOfComplaintArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descriptionOfComplaintArea.setLineWrap(true);
        descriptionOfComplaintArea.setWrapStyleWord(true);
        // Use a mouse listener to clear "Type here..." on focus
        descriptionOfComplaintArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (descriptionOfComplaintArea.getText().equals("Type here...")) {
                    descriptionOfComplaintArea.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (descriptionOfComplaintArea.getText().isEmpty()) {
                    descriptionOfComplaintArea.setText("Type here...");
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(descriptionOfComplaintArea);
        // Removed fixed width dimension to let the layout manager calculate the width
        scrollPane.setPreferredSize(new Dimension(350, 250)); 
        scrollPane.setBorder(new LineBorder(new Color(180, 180, 180), 1));

        JPanel descriptionHeaderPanel = new JPanel(new BorderLayout());
        descriptionHeaderPanel.setOpaque(false);
        descriptionHeaderPanel.add(descriptionLabel, BorderLayout.NORTH);
        descriptionHeaderPanel.add(descriptionSubLabel, BorderLayout.CENTER);

        descriptionPanel.add(descriptionHeaderPanel, BorderLayout.NORTH);
        descriptionPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Layout for Complaint/Description section (2 columns) ---
        gbc.gridy = 5; gbc.gridwidth = 2; gbc.weighty = 0; gbc.insets = new Insets(20, 0, 5, 0);
        JLabel spacerTitle = new JLabel("Complaint Details");
        spacerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.add(spacerTitle, gbc);
        
        JPanel complaintDetailsRow = new JPanel(new GridBagLayout());
        complaintDetailsRow.setOpaque(false);
        
        GridBagConstraints gbcRow = new GridBagConstraints();
        gbcRow.anchor = GridBagConstraints.NORTHWEST;
        gbcRow.fill = GridBagConstraints.BOTH;
        gbcRow.insets = new Insets(10, 0, 10, 20);
        
        // Left column (Complaint Type + Location)
        gbcRow.gridx = 0; gbcRow.weightx = 0.5; complaintDetailsRow.add(complaintTypeAndLocationPanel, gbcRow);
        
        // Right column (Description)
        gbcRow.gridx = 1; gbcRow.weightx = 0.5; gbcRow.insets = new Insets(10, 0, 10, 0); complaintDetailsRow.add(descriptionPanel, gbcRow);
        
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(complaintDetailsRow, gbc);

        // --- Submit Button Panel (Moved to BorderLayout.SOUTH of the wrapper for fixed position) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0)); // Add some padding above the button
        
        // Only one Submit button is in the screenshot
        submitButton = new RoundButton("Submit", new Color(40, 167, 167)); // Matching the screenshot's teal button
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setPreferredSize(new Dimension(100, 35));
        submitButton.addActionListener(e -> handleSubmitRequest());
        buttonPanel.add(submitButton);

        
        // --- Assemble Final Scrollable Panel ---
        
        // Wrap the formPanel (which contains all content) inside a JScrollPane
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        
        // *** CRITICAL CHANGE: Removed HORIZONTAL_SCROLLBAR_AS_NEEDED ***
        formScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
        formScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove default scroll pane border
        
        // Add the scrollable form to the center of the wrapper
        wrapperPanel.add(formScrollPane, BorderLayout.CENTER);
        
        // Add the fixed Submit button panel to the bottom of the wrapper
        wrapperPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return wrapperPanel;
    }

    private void handleSubmitRequest() {
        // --- Collect Resident Information ---
        String fullName = fullNameField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String emailAddress = emailAddressField.getText().trim();
        String homeAddress = homeAddressField.getText().trim();

        // --- Collect Complaint Details ---
        String location = locationOfIncidentField.getText().trim(); // Mapped to original titleField (now Location)
        String description = descriptionOfComplaintArea.getText().trim(); // Mapped to original requestDescriptionArea
        
        // Determine category from checkboxes
        String category = "Other"; // Default if none selected
        
        // Check if any checkbox is selected
        boolean isCategorySelected = false;
        if (garbageSanitationBox != null && garbageSanitationBox.isSelected()) {
            category = "Garbage/Sanitation Issue";
            isCategorySelected = true;
        } else if (roadStreetBox != null && roadStreetBox.isSelected()) {
            category = "Road/Street Problem";
            isCategorySelected = true;
        } else if (safetyHazardBox != null && safetyHazardBox.isSelected()) {
            category = "Safety Hazard";
            isCategorySelected = true;
        } else if (environmentalIssueBox != null && environmentalIssueBox.isSelected()) {
            category = "Environmental Issue";
            isCategorySelected = true;
        }

        // --- Validation ---
        // Basic required fields validation (Full Name, Contact, Home Address, Location, Description, Category)
        if (fullName.isEmpty() || contactNumber.isEmpty() || homeAddress.isEmpty() || location.isEmpty() || description.isEmpty() || description.equals("Type here...") || !isCategorySelected) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields (Full name, Contact Number, Home Address, Location, Description, and select a Complaint Type).",
                "Submission Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call the updated insertion method, passing ALL collected values
        boolean success = insertRequestIntoDB(
            residentName, // Stored resident identifier (used for logging/tracking)
            fullName,
            contactNumber,
            emailAddress,
            homeAddress,
            location,
            category,
            description,
            "Pending"
        );

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Your request has been submitted successfully.\nCategory: " + category + "\nLocation: " + location,
                "Submission Complete", JOptionPane.INFORMATION_MESSAGE);
            clearFormAndNavigateToDashboard();
        } else {
             JOptionPane.showMessageDialog(this,
                "Failed to submit request to the database.",
                "Submission Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // *** CORRECTED METHOD SIGNATURE AND IMPLEMENTATION (Matches the required SQL logic) ***
    private boolean insertRequestIntoDB(String residentIdentifier, String fullName, String contactNumber, 
                                        String emailAddress, String homeAddress, String location,
                                        String category, String description, String status) {
        
        // This SQL statement now assumes the database schema has been updated to include 'resident_name'
        String sql = "INSERT INTO resident_requests ("
                   + "resident_name, full_name, contact_number, email_address, home_address, "
                   + "location, category, description, status, date_submitted"
                   + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Binding the parameters to the PreparedStatement
            int i = 1;
            pstmt.setString(i++, residentIdentifier); // 1. resident_name (The key fix)
            pstmt.setString(i++, fullName);           // 2. full_name
            pstmt.setString(i++, contactNumber);      // 3. contact_number
            pstmt.setString(i++, emailAddress);       // 4. email_address
            pstmt.setString(i++, homeAddress);        // 5. home_address
            pstmt.setString(i++, location);           // 6. location
            pstmt.setString(i++, category);           // 7. category
            pstmt.setString(i++, description);        // 8. description
            pstmt.setString(i++, status);             // 9. status

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Database Insertion Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void clearFormAndNavigateToDashboard() {
        // Clear all fields in the new UI
        if (fullNameField != null) fullNameField.setText("");
        if (contactNumberField != null) contactNumberField.setText("");
        if (emailAddressField != null) emailAddressField.setText("");
        if (homeAddressField != null) homeAddressField.setText("");
        if (locationOfIncidentField != null) locationOfIncidentField.setText("");
        if (descriptionOfComplaintArea != null) descriptionOfComplaintArea.setText("Type here...");
        
        // Clear checkboxes
        if (garbageSanitationBox != null) garbageSanitationBox.setSelected(false);
        if (roadStreetBox != null) roadStreetBox.setSelected(false);
        if (safetyHazardBox != null) safetyHazardBox.setSelected(false);
        if (environmentalIssueBox != null) environmentalIssueBox.setSelected(false);
        
        // Navigate
        cardLayout.show(cardPanel, RESIDENT_DASHBOARD_VIEW);
    }
}