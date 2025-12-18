package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.sql.*;
import utils.DBConnection;

public class AddRequests extends JPanel {
    // Constants for CardLayout views
    private static final String RESIDENT_DASHBOARD_VIEW = "ResidentDashboard"; 
    private static final String RESIDENT_ANNOUNCEMENT_VIEW = "ResidentAnnouncement"; 
    private static final String RESIDENT_NOTIFICATION_VIEW = "ResidentNotification";
    private static final String ADD_REQUESTS_VIEW = "AddRequests";
    private static final String RESIDENT_PROFILE_VIEW = "ResidentProfile";

    // --- Colors (Synced with ResidentDashboard) ---
    private static final Color MAIN_BG_LIGHT_BLUE = new Color(225, 240, 255);
    private static final Color MENU_PANEL_BG = Color.WHITE;
    private static final Color CARD_BG = Color.WHITE; 
    private static final Color TEAL_BUTTON = new Color(0, 191, 191);
    private static final Color DARK_GRAY_TEXT = new Color(40, 40, 40);
    private static final Color LIGHT_BLUE_TEXT = new Color(100, 100, 100);
    private static final Color OUTER_BORDER_COLOR = new Color(180, 220, 240); 

    private String residentName;
    private String residentEmail;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    private JTextField fullNameField;
    private JTextField contactNumberField;
    private JTextField homeAddressField;
    private JCheckBox garbageSanitationBox, roadStreetBox, safetyHazardBox, environmentalIssueBox;
    private JTextField locationOfIncidentField;
    private JTextArea descriptionOfComplaintArea;
    private RoundButton submitButton;

    public AddRequests(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel, String residentName, String residentEmail) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.residentName = residentName;
        this.residentEmail = residentEmail;

        setLayout(new BorderLayout());
        setBackground(MAIN_BG_LIGHT_BLUE);
        setBorder(new LineBorder(OUTER_BORDER_COLOR, 3));

        // Layout construction
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        contentPanel.add(createMenuPanel(), BorderLayout.WEST);
        contentPanel.add(createAddRequestsContentPanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createAddRequestsContentPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(15, 15));
        wrapper.setOpaque(false);

        JLabel titleLabel = new JLabel("SUBMIT A SERVICE REQUEST");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(DARK_GRAY_TEXT);
        wrapper.add(titleLabel, BorderLayout.NORTH);

        // Improved Form Container
        RoundedPanel formPanel = new RoundedPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 18, 8);

        // --- Section 1: Resident Info ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel section1 = new JLabel("CONTACT INFORMATION");
        section1.setFont(new Font("Segoe UI", Font.BOLD, 15));
        section1.setForeground(new Color(0, 150, 150));
        formPanel.add(section1, gbc);

        fullNameField = createStyledTextField();
        contactNumberField = createStyledTextField();
        homeAddressField = createStyledTextField();

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.weightx = 0.5;
        formPanel.add(createFieldGroup("Full Name", fullNameField), gbc);
        gbc.gridx = 1;
        formPanel.add(createFieldGroup("Contact Number", contactNumberField), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(createFieldGroup("Home Address", homeAddressField), gbc);

        // --- Section 2: Complaint Details ---
        gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 18, 8); // Extra top padding for section
        JLabel section2 = new JLabel("COMPLAINT DETAILS");
        section2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        section2.setForeground(new Color(0, 150, 150));
        formPanel.add(section2, gbc);

        gbc.insets = new Insets(8, 8, 18, 8);
        gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(createComplaintTypePanel(), gbc);
        
        gbc.gridx = 1;
        locationOfIncidentField = createStyledTextField();
        formPanel.add(createFieldGroup("Location of Incident", locationOfIncidentField), gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        descriptionOfComplaintArea = new JTextArea(6, 20);
        descriptionOfComplaintArea.setLineWrap(true);
        descriptionOfComplaintArea.setWrapStyleWord(true);
        descriptionOfComplaintArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionOfComplaintArea.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        
        JScrollPane descScroll = new JScrollPane(descriptionOfComplaintArea);
        descScroll.setBorder(null);
        formPanel.add(createFieldGroup("Description of Complaint", descScroll), gbc);

        // ScrollPane for the form
        JScrollPane mainScroll = new JScrollPane(formPanel);
        mainScroll.setBorder(null);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        wrapper.add(mainScroll, BorderLayout.CENTER);

        // Submit Button
        submitButton = new RoundButton("SUBMIT REQUEST", TEAL_BUTTON);
        submitButton.setForeground(Color.WHITE);
        submitButton.setPreferredSize(new Dimension(180, 45));
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.addActionListener(e -> handleSubmitRequest());
        
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrapper.setOpaque(false);
        btnWrapper.add(submitButton);
        wrapper.add(btnWrapper, BorderLayout.SOUTH);

        return wrapper;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(0, 35));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    private JPanel createFieldGroup(String labelText, Component field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(DARK_GRAY_TEXT);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPanel createComplaintTypePanel() {
        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        p.setOpaque(false);
        garbageSanitationBox = new JCheckBox("Garbage");
        roadStreetBox = new JCheckBox("Roads");
        safetyHazardBox = new JCheckBox("Safety");
        environmentalIssueBox = new JCheckBox("Environment");
        
        ButtonGroup group = new ButtonGroup();
        JCheckBox[] boxes = {garbageSanitationBox, roadStreetBox, safetyHazardBox, environmentalIssueBox};
        for(JCheckBox b : boxes) {
            b.setOpaque(false);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            group.add(b);
            p.add(b);
        }
        return createFieldGroup("Category", p);
    }

    // ================= SIDEBAR MENU (Matched with ResidentDashboard) =================
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
        gbc.gridy = 2; panel.add(createNavLink("Req Progress", false), gbc);
        gbc.gridy = 3; panel.add(createNavLink("Announcement", false), gbc); 
        gbc.gridy = 4; panel.add(createNavLink("Add Requests", true), gbc);
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

    private JPanel createHeaderPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(800, 60));
        p.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel welcome = new JLabel("WELCOME, " + residentName.toUpperCase() + "!");
        welcome.setFont(new Font("STXinwei", Font.BOLD, 26));
        welcome.setForeground(DARK_GRAY_TEXT);
        p.add(welcome, BorderLayout.WEST);

        return p;
    }

    private void handleSubmitRequest() {
        String category = garbageSanitationBox.isSelected() ? "Garbage" : 
                         roadStreetBox.isSelected() ? "Roads" :
                         safetyHazardBox.isSelected() ? "Safety" :
                         environmentalIssueBox.isSelected() ? "Environment" : "";

        if (fullNameField.getText().isEmpty() || category.isEmpty() || locationOfIncidentField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }

        boolean success = insertRequestIntoDB(
            residentName,
            fullNameField.getText().trim(),
            contactNumberField.getText().trim(),
            residentEmail,
            homeAddressField.getText().trim(),
            locationOfIncidentField.getText().trim(),
            category,
            descriptionOfComplaintArea.getText().trim()
        );

        if (success) {
            JOptionPane.showMessageDialog(this, "Request Submitted Successfully!");
            clearFields();
            cardLayout.show(cardPanel, RESIDENT_DASHBOARD_VIEW);
        }
    }

    private boolean insertRequestIntoDB(String resName, String full, String contact, String email, String addr, String loc, String cat, String desc) {
        String sql = "INSERT INTO resident_requests (resident_name, full_name, contact_number, email_address, home_address, location, category, description, status, update_status, date_submitted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pending', 'Waiting', NOW())";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resName);
            pstmt.setString(2, full);
            pstmt.setString(3, contact);
            pstmt.setString(4, email);
            pstmt.setString(5, addr);
            pstmt.setString(6, loc);
            pstmt.setString(7, cat);
            pstmt.setString(8, desc);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void clearFields() {
        fullNameField.setText("");
        contactNumberField.setText("");
        homeAddressField.setText("");
        locationOfIncidentField.setText("");
        descriptionOfComplaintArea.setText("");
        garbageSanitationBox.setSelected(false);
        roadStreetBox.setSelected(false);
        safetyHazardBox.setSelected(false);
        environmentalIssueBox.setSelected(false);
    }

    // ================= ROUNDED PANEL CLASS =================
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