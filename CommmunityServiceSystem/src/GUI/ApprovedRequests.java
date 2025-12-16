package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.sql.*;
import utils.DBConnection;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ApprovedRequests extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JFrame parentFrame;
    private JTable approvedRequestsTable;
    private DefaultTableModel tableModel;
    
    // Reference to ManageRequests panel instance to pass data
    private ManageRequests manageRequestsPanel; // Initialized to null

    private static final String STAFF_LOGIN_VIEW = "StaffLogin";
    private static final String STAFF_ANNOUNCEMENT_VIEW = "StaffAnnouncement";
    private static final String STAFF_DASHBOARD_VIEW = "StaffDashboard";
    private static final String RESIDENTS_LISTS_VIEW = "ResidentsLists";
    private static final String REQUESTS_VIEW = "Requests"; // Main Requests View
    private static final String MANAGE_REQUESTS_VIEW = "ManageRequests"; 

    private static final Color DARK_TEXT = new Color(0, 0, 0);
    private static final Color TEAL_ACCENT = new Color(0, 191, 191);
    private static final Color BG_COLOR = new Color(225, 240, 255);
    private static final Color WHITE_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(50, 70, 90);
    private static final Color ACCENT_COLOR = new Color(0, 150, 200);

    // Data structure to hold request information
    private static class RequestData {
        int requestId;
        String fullName;
        String category;
        String unit; // Will hold 'Location' from DB
        String description; 
        String updateStatus; // NEW: Field for the update_status column
        String dateSubmitted;
        String dateProcessed;
        
        public RequestData(int requestId, String fullName, String category, String unit, String description, String updateStatus, String dateSubmitted, String dateProcessed) {
            this.requestId = requestId;
            this.fullName = fullName;
            this.category = category;
            this.unit = unit;
            this.description = description;
            // Display "Approved" if update_status is NULL, otherwise display the actual status
            this.updateStatus = updateStatus != null ? updateStatus : "Approved"; 
            this.dateSubmitted = formatDate(dateSubmitted);
            this.dateProcessed = formatDate(dateProcessed);
        }

        private static String formatDate(String dateStr) {
            if (dateStr == null) return "N/A";
            try {
                // Assuming dateStr is a DATETIME string
                LocalDateTime dateTime = LocalDateTime.parse(dateStr.replace(' ', 'T'));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                return dateTime.format(formatter);
            } catch (Exception e) {
                return dateStr;
            }
        }
    }

    public ApprovedRequests(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
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
        contentPanel.add(createApprovedRequestsTablePanel(), BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        loadApprovedRequests();
    }
    
    // --- UI Components ---
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        headerPanel.setPreferredSize(new Dimension(800, 70));

        JLabel welcomeLabel = new JLabel("APPROVED REQUESTS");
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

    private JPanel createApprovedRequestsTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE_BG);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // MODIFIED: Added 'Update' column
        String[] columnNames = {"ID", "Complainant Name", "Category", "Date Submitted", "Update"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Makes table read-only
            }
        };

        approvedRequestsTable = new JTable(tableModel);
        styleTable(approvedRequestsTable);

        // Adjust column widths if needed
        approvedRequestsTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(approvedRequestsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        RoundButton manageButton = new RoundButton("Manage Request", ACCENT_COLOR);
        manageButton.setFont(new Font("Leelawadee", Font.BOLD, 16));
        manageButton.setForeground(Color.WHITE);
        manageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        manageButton.setPreferredSize(new Dimension(250, 50));
        
        // --- Action Listener to move request to ManageRequests ---
        manageButton.addActionListener(e -> {
            int selectedRow = approvedRequestsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a request from the table to manage.", 
                    "No Request Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Get the Request ID from the selected row (column 0)
            int requestId = (int) tableModel.getValueAt(selectedRow, 0);
            
            // Fetch the full request details including description and unit (Location)
            RequestData details = fetchRequestDetailsById(requestId);
            
            // Ensure the panel is found right before use
            ManageRequests targetPanel = findManageRequestsPanel(); 
            
            if (details != null && targetPanel != null) {
                // MODIFIED: Passed the new updateStatus parameter
                targetPanel.setRequestData(
                    details.requestId, 
                    details.category, 
                    details.fullName, 
                    details.unit, // This contains the Location value
                    details.description,
                    details.updateStatus // Pass the update status for button control
                );
                
                // Switch view
                cardLayout.show(cardPanel, MANAGE_REQUESTS_VIEW);
            } else {
                JOptionPane.showMessageDialog(this, "Could not load full request details or the ManageRequests panel is missing.", "Initialization Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        buttonPanel.setBackground(WHITE_BG);
        buttonPanel.add(manageButton);
        
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        return tablePanel;
    }
    
    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(new Font("Leelawadee", Font.PLAIN, 11));
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setBackground(TEAL_ACCENT);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Leelawadee", Font.BOLD, 12));
    }

    /**
     * Attempts to find the ManageRequests panel instance in the cardPanel if it hasn't been found yet.
     */
    private ManageRequests findManageRequestsPanel() {
        if (this.manageRequestsPanel == null) {
            for (Component comp : cardPanel.getComponents()) {
                if (comp instanceof ManageRequests) {
                    this.manageRequestsPanel = (ManageRequests) comp;
                    break;
                }
            }
        }
        return this.manageRequestsPanel;
    }

    // --- Data Logic ---
    private List<RequestData> fetchApprovedRequestsFromDB() {
        List<RequestData> requests = new ArrayList<>();
        // MODIFIED: Select the new column update_status. Only show requests where status IS 'Approved'.
        String sql = "SELECT request_id, full_name, category, update_status, date_submitted, date_processed FROM resident_requests WHERE status = 'Approved' ORDER BY date_submitted DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(new RequestData(
                    rs.getInt("request_id"),
                    rs.getString("full_name"),
                    rs.getString("category"),
                    "", // unit placeholder
                    "", // description placeholder
                    rs.getString("update_status"), // Pass the new update_status
                    rs.getString("date_submitted"),
                    rs.getString("date_processed")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading Approved Requests from DB: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return requests;
    }
    
    /**
     * Fetches all necessary details for a request by its ID, including the current update_status.
     */
    private RequestData fetchRequestDetailsById(int requestId) {
        // MODIFIED: Select the new column update_status. Only fetches Approved requests.
        String sql = "SELECT request_id, full_name, category, Location, description, update_status, date_submitted, date_processed FROM resident_requests WHERE request_id = ? AND status = 'Approved'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, requestId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new RequestData(
                        rs.getInt("request_id"),
                        rs.getString("full_name"),
                        rs.getString("category"),
                        rs.getString("Location"), 
                        rs.getString("description"),
                        rs.getString("update_status"), // Pass the new update_status
                        rs.getString("date_submitted"),
                        rs.getString("date_processed")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching request details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }


    public void loadApprovedRequests() {
        List<RequestData> approvedRequests = fetchApprovedRequestsFromDB();
        
        tableModel.setRowCount(0); // Clear existing data

        if (approvedRequests.isEmpty()) {
            // No need for a message, just an empty table
        } else {
            for (RequestData request : approvedRequests) {
                // MODIFIED: Display the updateStatus in the table (column 4)
                tableModel.addRow(new Object[]{
                    request.requestId,
                    request.fullName,
                    request.category,
                    request.dateSubmitted,
                    request.updateStatus 
                });
            }
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