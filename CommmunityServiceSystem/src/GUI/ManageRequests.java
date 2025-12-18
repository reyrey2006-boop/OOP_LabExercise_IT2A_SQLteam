package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.sql.*;
import utils.DBConnection;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ManageRequests extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JFrame parentFrame;

    // Data for the currently viewed request
    private int requestId;
    private String requestCategory;
    private String residentFullName;
    private String residentEmail; // Added variable for email
    private String residentUnit;
    private String requestDescription;
    private String dateSubmitted;

    // UI Components
    private JLabel requestIDLabel;
    private JLabel categoryLabel;
    private JLabel residentNameLabel;
    private JLabel residentEmailLabel; // Added UI component for email
    private JLabel unitLabel;
    private JTextArea descriptionArea;
    
    private RoundButton inProcessButton;
    private RoundButton doneButton;

    // AUTO REFRESH TIMER
    private Timer refreshTimer;

    private static final String STAFF_ANNOUNCEMENT_VIEW = "StaffAnnouncement";
    private static final String STAFF_DASHBOARD_VIEW = "StaffDashboard";
    private static final String RESIDENTS_LISTS_VIEW = "ResidentsLists";
    private static final String REQUESTS_VIEW = "Requests";

    private static final Color DARK_TEXT = new Color(0, 0, 0);
    private static final Color TEAL_ACCENT = new Color(0, 191, 191);
    private static final Color BG_COLOR = new Color(225, 240, 255);
    private static final Color WHITE_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(50, 70, 90);

    private static final Color BLUE_IN_PROCESS = new Color(0, 150, 200);
    private static final Color GREEN_DONE = new Color(40, 167, 69);

    public ManageRequests(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
        this.parentFrame = parentFrame;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        contentPanel.add(createNavigationPanel(), BorderLayout.WEST);
        contentPanel.add(createRequestDetailsPanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Updated initial state to include email
        setRequestData(0, "N/A", "N/A", "N/A", "N/A",
                "Select an Approved request to manage.", null);

        startAutoRefresh();
    }

    /* ================= AUTO REFRESH ================= */

    private void startAutoRefresh() {
        refreshTimer = new Timer(5000, e -> {
            if (!isShowing() || requestId == 0) return;

            String latestStatus = fetchUpdateStatusFromDB(requestId);
            applyButtonState(latestStatus);
        });
        refreshTimer.start();
    }

    // Updated setRequestData to handle the email parameter
    public void setRequestData(int id, String category, String fullName, String email, String unit, String description, String currentUpdateStatus) {
        this.requestId = id;
        this.requestCategory = category;
        this.residentFullName = fullName;
        this.residentEmail = email; // Store email
        this.residentUnit = unit;
        this.requestDescription = description;

        if (id > 0) {
            requestIDLabel.setText(String.valueOf(id));
            categoryLabel.setText(category);
            residentNameLabel.setText(fullName);
            residentEmailLabel.setText(email); // Display the auto-filled email
            unitLabel.setText(unit);
            descriptionArea.setText(description);
        } else {
            requestIDLabel.setText("N/A");
            categoryLabel.setText("N/A");
            residentNameLabel.setText("N/A");
            residentEmailLabel.setText("N/A");
            unitLabel.setText("N/A");
            descriptionArea.setText(description);
        }

        if (inProcessButton != null && doneButton != null) {
            applyButtonState(currentUpdateStatus);
        }
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        headerPanel.setPreferredSize(new Dimension(800, 70));

        JLabel welcomeLabel = new JLabel("MANAGE REQUEST");
        welcomeLabel.setFont(new Font("STXinwei", Font.BOLD, 30));
        welcomeLabel.setForeground(PRIMARY_COLOR);
        welcomeLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        logoutWrapper.setBackground(Color.WHITE);
        headerPanel.add(logoutWrapper, BorderLayout.EAST);
        return headerPanel;
    }
    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridBagLayout());
        navPanel.setBackground(Color.WHITE);
        navPanel.setPreferredSize(new Dimension(200, 500));
        navPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);

        JLabel navTitle = new JLabel("MENU");
        navTitle.setFont(new Font("Leelawadee", Font.BOLD, 30));
        navTitle.setForeground(DARK_TEXT);
        navTitle.setHorizontalAlignment(SwingConstants.CENTER);
        navTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        gbc.gridy = 0;
        navPanel.add(navTitle, gbc);

        gbc.gridy = 1; navPanel.add(createStaffNavLink("Dashboard", false), gbc);
        gbc.gridy = 2; navPanel.add(createStaffNavLink("Announcement", false), gbc);
        gbc.gridy = 3; navPanel.add(createStaffNavLink("Residents", false), gbc);
        gbc.gridy = 4; navPanel.add(createStaffNavLink("Requests", true), gbc); 
        
        JButton backButton = new RoundButton("Back to Requests", PRIMARY_COLOR); 
        backButton.setFont(new Font("Leelawadee", Font.BOLD, 16));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(150, 54));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, REQUESTS_VIEW)); 
        
        gbc.gridy = 5; gbc.insets = new Insets(40, 0, 15, 0); 
        navPanel.add(backButton, gbc);
        gbc.insets = new Insets(0, 0, 15, 0);
        
        gbc.gridy = 6; gbc.weighty = 1.0; navPanel.add(Box.createVerticalGlue(), gbc);
        
        return navPanel;
    }

    private JPanel createRequestDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(WHITE_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Increased GridLayout rows to 5 to accommodate the Email label
        JPanel detailsGrid = new JPanel(new GridLayout(5, 2, 10, 10));
        detailsGrid.setBackground(WHITE_BG);

        Font labelFont = new Font("Leelawadee", Font.BOLD, 15);
        Font valueFont = new Font("Leelawadee", Font.PLAIN, 15);

        detailsGrid.add(createLabel("Request ID:", labelFont, PRIMARY_COLOR));
        requestIDLabel = createLabel("N/A", valueFont, DARK_TEXT);
        detailsGrid.add(requestIDLabel);

        detailsGrid.add(createLabel("Category:", labelFont, PRIMARY_COLOR));
        categoryLabel = createLabel("N/A", valueFont, DARK_TEXT);
        detailsGrid.add(categoryLabel);

        detailsGrid.add(createLabel("Resident Name:", labelFont, PRIMARY_COLOR));
        residentNameLabel = createLabel("N/A", valueFont, DARK_TEXT);
        detailsGrid.add(residentNameLabel);

        // ADDED EMAIL LABEL AND VALUE
        detailsGrid.add(createLabel("Resident Email:", labelFont, PRIMARY_COLOR));
        residentEmailLabel = createLabel("N/A", valueFont, DARK_TEXT);
        detailsGrid.add(residentEmailLabel);

        detailsGrid.add(createLabel("Location of the Incident:", labelFont, PRIMARY_COLOR));
        unitLabel = createLabel("N/A", valueFont, DARK_TEXT);
        detailsGrid.add(unitLabel);

        panel.add(detailsGrid, BorderLayout.NORTH);

        descriptionArea = new JTextArea(10, 1);
        descriptionArea.setFont(valueFont);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Request Description", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            labelFont, 
            PRIMARY_COLOR
        ));

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        actionPanel.setBackground(WHITE_BG);
        
        inProcessButton = new RoundButton("In Process", BLUE_IN_PROCESS);
        inProcessButton.setFont(new Font("Leelawadee", Font.BOLD, 18));
        inProcessButton.setForeground(Color.WHITE);
        inProcessButton.setPreferredSize(new Dimension(200, 50));
        inProcessButton.addActionListener(e -> processRequestUpdate("In Process"));
        
        doneButton = new RoundButton("Done", GREEN_DONE);
        doneButton.setFont(new Font("Leelawadee", Font.BOLD, 18));
        doneButton.setForeground(Color.WHITE);
        doneButton.setPreferredSize(new Dimension(200, 50));
        doneButton.addActionListener(e -> processRequestUpdate("Done"));

        actionPanel.add(inProcessButton);
        actionPanel.add(doneButton);
        
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private void processRequestUpdate(String newUpdateStatus) {
        if (this.requestId == 0) {
            JOptionPane.showMessageDialog(this, "Please select a valid request to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to set Request ID " + this.requestId + " update status to '" + newUpdateStatus + "'?", 
            "Confirm Action", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (updateRequestUpdateStatus(newUpdateStatus)) {
                JOptionPane.showMessageDialog(this, 
                    "Request ID " + this.requestId + " successfully set to \"" + newUpdateStatus + "\".", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear the view and return to table after update
                setRequestData(0, "N/A", "N/A", "N/A", "N/A", "Select an Approved request to manage.", null);
                cardLayout.show(cardPanel, REQUESTS_VIEW); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update request status in the database.", "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean updateRequestUpdateStatus(String status) {
        String sql;
        if (status.equals("Done")) {
            sql = "UPDATE resident_requests SET update_status = ?, date_processed = NOW() WHERE request_id = ?";
        } else if (status.equals("In Process")) {
            sql = "UPDATE resident_requests SET update_status = ? WHERE request_id = ?";
        } else {
            return false; 
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating request update_status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private RoundButton createStaffNavLink(String text, boolean isActive) {
        RoundButton button = new RoundButton(text, TEAL_ACCENT);
        button.setFont(new Font("Leelawadee", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(150, 54));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            String viewName = "";
            switch (text) {
                case "Dashboard": viewName = STAFF_DASHBOARD_VIEW; break;
                case "Announcement": viewName = STAFF_ANNOUNCEMENT_VIEW; break;
                case "Residents": viewName = RESIDENTS_LISTS_VIEW; break; 
                case "Requests": viewName = REQUESTS_VIEW; break;
            }
            if (!viewName.isEmpty() && !(viewName.equals(REQUESTS_VIEW) && isActive)) {
                 this.cardLayout.show(this.cardPanel, viewName);
            }
        });

        if (isActive) {
            button.setBackground(TEAL_ACCENT);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(BG_COLOR);
            button.setForeground(PRIMARY_COLOR);
        }

        return button;
    }

    private void applyButtonState(String latestStatus) {
        if (latestStatus == null || latestStatus.equals("Approved")) {
            inProcessButton.setEnabled(true);
            doneButton.setEnabled(false);
        } else if (latestStatus.equals("In Process")) {
            inProcessButton.setEnabled(false);
            doneButton.setEnabled(true);
        } else if (latestStatus.equals("Done")) {
            inProcessButton.setEnabled(false);
            doneButton.setEnabled(false);
        }
    }

    private String fetchUpdateStatusFromDB(int requestId) {
        String status = null;
        String sql = "SELECT update_status FROM resident_requests WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    status = rs.getString("update_status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }
}