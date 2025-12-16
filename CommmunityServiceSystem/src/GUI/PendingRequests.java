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

public class PendingRequests extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JFrame parentFrame; // Added parentFrame instance variable

    private static final String STAFF_LOGIN_VIEW = "StaffLogin";
    private static final String STAFF_ANNOUNCEMENT_VIEW = "StaffAnnouncement";
    private static final String STAFF_DASHBOARD_VIEW = "StaffDashboard";
    private static final String RESIDENTS_LISTS_VIEW = "ResidentsLists"; 
    private static final String REQUESTS_VIEW = "Requests"; // Main Requests View

    // --- NEW CONSTANTS FOR NAVIGATION ---
    private static final String APPROVED_REQUESTS_VIEW = "ApprovedRequests";
    private static final String DECLINED_REQUESTS_VIEW = "DeclinedRequests";
    // ------------------------------------
    
    private static final Color DARK_TEXT = new Color(0, 0, 0);
    private static final Color TEAL_ACCENT = new Color(0, 191, 191);
    private static final Color BG_COLOR = new Color(225, 240, 255);
    private static final Color WHITE_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(50, 70, 90);
    private static final Color RED_DECLINE = new Color(200, 50, 50); 
    private static final Color GREEN_APPROVE = new Color(40, 167, 69); 

    // --- Components for dynamic display ---
    private JPanel requestsListPanel; 
    private JLabel detailsDateLabel;  
    private JLabel detailsNameLabel;
    private JLabel detailsAddressLabel;
    private JLabel detailsContactLabel;
    private JLabel detailsEmailLabel;
    private JLabel detailsCategoryLabel;
    private JLabel detailsLocationLabel;
    private JTextArea detailsStatementTextArea;
    private JButton approveButton;
    private JButton declineButton;
    private ResidentRequestData selectedRequest = null;

    // --- Data Class to hold resident request data ---
    private static class ResidentRequestData {
        int requestId;
        String fullName;
        String contactNumber;
        String emailAddress;
        String homeAddress;
        String location; 
        String category;
        String description;
        String dateSubmitted; 

        public ResidentRequestData(int requestId, String fullName, String contactNumber, String emailAddress, String homeAddress, String location, String category, String description, String dateSubmitted) {
            this.requestId = requestId;
            this.fullName = fullName;
            this.contactNumber = contactNumber;
            this.emailAddress = emailAddress;
            this.homeAddress = homeAddress;
            this.location = location;
            this.category = category;
            this.description = description;
            this.dateSubmitted = dateSubmitted;
        }
        
        public String getFormattedDate() {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateSubmitted.replace(' ', 'T'));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
                return dateTime.format(formatter);
            } catch (Exception e) {
                return dateSubmitted;
            }
        }
    }


    public PendingRequests(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
        this.parentFrame = parentFrame;
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Header Panel ---
        JPanel headerPanel = createHeaderPanel(); 
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Area ---
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // --- Navigation Panel (Sidebar) ---
        JPanel navPanel = createNavigationPanel(); 
        contentPanel.add(navPanel, BorderLayout.WEST);
        
        // --- Request View Split (Center) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.25); 
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        // Left: Requests List Panel
        requestsListPanel = new JPanel();
        requestsListPanel.setLayout(new BoxLayout(requestsListPanel, BoxLayout.Y_AXIS));
        requestsListPanel.setBackground(BG_COLOR); 
        requestsListPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane listScrollPane = new JScrollPane(requestsListPanel);
        listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        listScrollPane.setBorder(BorderFactory.createEmptyBorder());
        listScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Right: Details Panel
        JPanel detailsViewPanel = createDetailsView();

        splitPane.setLeftComponent(listScrollPane);
        splitPane.setRightComponent(detailsViewPanel);

        contentPanel.add(splitPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
        
        // Load data immediately
        loadPendingRequests();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        headerPanel.setPreferredSize(new Dimension(800, 70));

        JLabel welcomeLabel = new JLabel("PENDING REQUESTS");
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
        gbc.gridy = 4; navPanel.add(createStaffNavLink("Requests", true), gbc); // ACTIVE
        
        JButton backButton = new RoundButton("Back", PRIMARY_COLOR); 
        backButton.setFont(new Font("Leelawadee", Font.BOLD, 18));
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
    
    private List<ResidentRequestData> fetchPendingRequestsFromDB() {
        List<ResidentRequestData> requests = new ArrayList<>();
        String sql = "SELECT request_id, full_name, contact_number, email_address, home_address, location, category, description, date_submitted FROM resident_requests WHERE status = 'Pending' ORDER BY date_submitted DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("request_id");
                String fullName = rs.getString("full_name");
                String contactNumber = rs.getString("contact_number");
                String emailAddress = rs.getString("email_address");
                String homeAddress = rs.getString("home_address");
                String location = rs.getString("location");
                String category = rs.getString("category");
                String description = rs.getString("description");
                String dateSubmitted = rs.getString("date_submitted"); 

                requests.add(new ResidentRequestData(id, fullName, contactNumber, emailAddress, homeAddress, location, category, description, dateSubmitted));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading Pending Requests from DB: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return requests;
    }

    private void loadPendingRequests() {
        requestsListPanel.removeAll();
        List<ResidentRequestData> pendingRequests = fetchPendingRequestsFromDB();

        if (pendingRequests.isEmpty()) {
            JLabel noRequests = new JLabel("No Pending Requests.");
            noRequests.setFont(new Font("Leelawadee", Font.ITALIC, 16));
            noRequests.setBorder(new EmptyBorder(20, 10, 0, 10));
            requestsListPanel.add(noRequests);
            displayRequestDetails(null); 
        } else {
            if (selectedRequest == null || pendingRequests.stream().noneMatch(r -> r.requestId == selectedRequest.requestId)) {
                 displayRequestDetails(pendingRequests.get(0));
            }
            
            for (ResidentRequestData request : pendingRequests) {
                requestsListPanel.add(createRequestSummaryPanel(request));
                requestsListPanel.add(Box.createVerticalStrut(5)); 
            }
        }
        
        requestsListPanel.revalidate();
        requestsListPanel.repaint();
    }
    
    private JPanel createRequestSummaryPanel(ResidentRequestData request) {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BorderLayout(5, 5));
        summaryPanel.setBackground(WHITE_BG); 
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        summaryPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel categoryLabel = new JLabel(request.category);
        categoryLabel.setFont(new Font("Leelawadee", Font.BOLD, 16));
        categoryLabel.setForeground(TEAL_ACCENT);
        summaryPanel.add(categoryLabel, BorderLayout.NORTH);
        
        JLabel descLabel = new JLabel(request.fullName);
        descLabel.setFont(new Font("Leelawadee", Font.PLAIN, 14));
        descLabel.setForeground(DARK_TEXT);
        summaryPanel.add(descLabel, BorderLayout.CENTER);
        
        JLabel dateLabel = new JLabel(request.getFormattedDate());
        dateLabel.setFont(new Font("Leelawadee", Font.ITALIC, 12));
        dateLabel.setForeground(new Color(150, 150, 150));
        summaryPanel.add(dateLabel, BorderLayout.SOUTH);

        summaryPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                displayRequestDetails(request);
            }
        });

        return summaryPanel;
    }
    
    private JPanel createDetailsView() {
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(WHITE_BG);

        JPanel requestDetailsContent = new JPanel();
        requestDetailsContent.setLayout(new BoxLayout(requestDetailsContent, BoxLayout.Y_AXIS));
        requestDetailsContent.setOpaque(true);
        requestDetailsContent.setBackground(WHITE_BG);
        requestDetailsContent.setBorder(new EmptyBorder(30, 30, 10, 30)); 

        Font labelFont = new Font("Leelawadee", Font.BOLD, 16);
        Font valueFont = new Font("Leelawadee", Font.PLAIN, 16);

        JPanel dateWrapper = new JPanel(new BorderLayout());
        dateWrapper.setOpaque(false);
        dateWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsDateLabel = new JLabel("Date: (Select a request)"); 
        detailsDateLabel.setFont(new Font("Leelawadee", Font.ITALIC, 14));
        detailsDateLabel.setForeground(new Color(100, 100, 100));
        detailsDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        dateWrapper.add(detailsDateLabel, BorderLayout.EAST);
        requestDetailsContent.add(dateWrapper);
        requestDetailsContent.add(Box.createVerticalStrut(15)); 
        
        JPanel residentInfoPanel = createTitledPanel("Resident Information");
        residentInfoPanel.setLayout(new GridBagLayout());
        residentInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        detailsNameLabel = new JLabel("N/A");
        detailsAddressLabel = new JLabel("N/A");
        detailsContactLabel = new JLabel("N/A");
        detailsEmailLabel = new JLabel("N/A");
        
        addDetailRow(residentInfoPanel, gbc, "Name of Complainant:", detailsNameLabel, labelFont, valueFont, 0);
        addDetailRow(residentInfoPanel, gbc, "Address:", detailsAddressLabel, labelFont, valueFont, 1);
        addDetailRow(residentInfoPanel, gbc, "Contact Number:", detailsContactLabel, labelFont, valueFont, 2);
        addDetailRow(residentInfoPanel, gbc, "Email Address:", detailsEmailLabel, labelFont, valueFont, 3);
        
        requestDetailsContent.add(residentInfoPanel);
        requestDetailsContent.add(Box.createVerticalStrut(20));
        
        JPanel incidentDetailsPanel = createTitledPanel("Complaint/Incident Details");
        incidentDetailsPanel.setLayout(new GridBagLayout());
        incidentDetailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        detailsCategoryLabel = new JLabel("N/A");
        detailsLocationLabel = new JLabel("N/A");

        addDetailRow(incidentDetailsPanel, gbc2, "Type of complaint:", detailsCategoryLabel, labelFont, valueFont, 0);
        addDetailRow(incidentDetailsPanel, gbc2, "Location of the incident:", detailsLocationLabel, labelFont, valueFont, 1);
        
        requestDetailsContent.add(incidentDetailsPanel);
        requestDetailsContent.add(Box.createVerticalStrut(30)); 

        JLabel statementTitle = new JLabel("Statement / Description of Complaint");
        statementTitle.setFont(new Font("Leelawadee", Font.BOLD, 20)); 
        statementTitle.setForeground(PRIMARY_COLOR);
        statementTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        requestDetailsContent.add(statementTitle);
        requestDetailsContent.add(Box.createVerticalStrut(10));

        detailsStatementTextArea = new JTextArea("Select a request from the list to view its details."); 
        detailsStatementTextArea.setLineWrap(true);
        detailsStatementTextArea.setWrapStyleWord(true);
        detailsStatementTextArea.setEditable(false);
        detailsStatementTextArea.setFont(valueFont);
        detailsStatementTextArea.setBackground(BG_COLOR); 
        
        JScrollPane scrollPaneForStatement = new JScrollPane(detailsStatementTextArea);
        scrollPaneForStatement.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10) 
        )); 
        scrollPaneForStatement.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPaneForStatement.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250)); 
        scrollPaneForStatement.setPreferredSize(new Dimension(600, 250));

        requestDetailsContent.add(scrollPaneForStatement);
        requestDetailsContent.add(Box.createVerticalGlue()); 

        JScrollPane mainScrollPane = new JScrollPane(requestDetailsContent);
        mainScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16); 

        detailsPanel.add(mainScrollPane, BorderLayout.CENTER);

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        actionButtonPanel.setOpaque(true);
        actionButtonPanel.setBackground(WHITE_BG);
        actionButtonPanel.setBorder(new EmptyBorder(15, 30, 30, 30)); 

        declineButton = new RoundButton("Decline", RED_DECLINE);
        declineButton.setPreferredSize(new Dimension(150, 50));
        declineButton.setFont(new Font("Leelawadee", Font.BOLD, 20));
        declineButton.setForeground(Color.WHITE);
        declineButton.setEnabled(false); 
        declineButton.addActionListener(e -> updateRequestStatus("Declined"));

        approveButton = new RoundButton("Approve", GREEN_APPROVE);
        approveButton.setPreferredSize(new Dimension(150, 50));
        approveButton.setFont(new Font("Leelawadee", Font.BOLD, 20));
        approveButton.setForeground(Color.WHITE);
        approveButton.setEnabled(false); 
        approveButton.addActionListener(e -> updateRequestStatus("Approved"));

        actionButtonPanel.add(declineButton);
        actionButtonPanel.add(approveButton);
        
        detailsPanel.add(actionButtonPanel, BorderLayout.SOUTH);
        
        return detailsPanel;
    }
    
    private JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 0, 5, 0), 
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)), 
                title, 
                TitledBorder.LEFT, 
                TitledBorder.TOP, 
                new Font("Leelawadee", Font.BOLD, 18), 
                TEAL_ACCENT
            )
        ));
        return panel;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String title, JLabel valueLabel, Font titleFont, Font valueFont, int row) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(PRIMARY_COLOR);

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0; 
        gbc.insets = new Insets(5, 5, 5, 10);
        gbc.fill = GridBagConstraints.NONE;
        panel.add(titleLabel, gbc);

        valueLabel.setFont(valueFont);
        valueLabel.setForeground(DARK_TEXT);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 5);
        panel.add(valueLabel, gbc);
    }

    private void displayRequestDetails(ResidentRequestData request) {
        this.selectedRequest = request;
        boolean isSelected = request != null;
        
        if (isSelected) {
            detailsNameLabel.setText(request.fullName);
            detailsAddressLabel.setText(request.homeAddress);
            detailsContactLabel.setText(request.contactNumber);
            detailsEmailLabel.setText(request.emailAddress);
            detailsCategoryLabel.setText(request.category);
            detailsLocationLabel.setText(request.location);
            detailsDateLabel.setText("Date Submitted: " + request.getFormattedDate());
            detailsStatementTextArea.setText(request.description);
            detailsStatementTextArea.setBackground(WHITE_BG);
        } else {
            detailsNameLabel.setText("N/A");
            detailsAddressLabel.setText("N/A");
            detailsContactLabel.setText("N/A");
            detailsEmailLabel.setText("N/A");
            detailsCategoryLabel.setText("N/A");
            detailsLocationLabel.setText("N/A");
            detailsDateLabel.setText("Date: (Select a request)");
            detailsStatementTextArea.setText("Select a request from the list to view its details.");
            detailsStatementTextArea.setBackground(BG_COLOR); 
        }
        
        detailsStatementTextArea.setCaretPosition(0); 
        
        approveButton.setEnabled(isSelected);
        declineButton.setEnabled(isSelected);
    }
    
    // --- MODIFIED METHOD FOR NAVIGATION ---
    private void updateRequestStatus(String newStatus) {
        if (selectedRequest == null) return;
        
        String sql = "UPDATE resident_requests SET status = ?, date_processed = NOW() WHERE request_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, selectedRequest.requestId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(parentFrame, "Request ID " + selectedRequest.requestId + " has been " + newStatus + ".", "Request Handled", JOptionPane.INFORMATION_MESSAGE);
                
                // Navigate based on the new status
                if (newStatus.equals("Approved")) {
                    cardLayout.show(cardPanel, APPROVED_REQUESTS_VIEW);
                } else if (newStatus.equals("Declined")) {
                    cardLayout.show(cardPanel, DECLINED_REQUESTS_VIEW);
                } else {
                    cardLayout.show(cardPanel, REQUESTS_VIEW);
                }
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Failed to update request status.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
}