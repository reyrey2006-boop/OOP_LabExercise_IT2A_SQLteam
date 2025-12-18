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

public class PendingRequests extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JFrame parentFrame;
    private JTable pendingRequestsTable;
    private DefaultTableModel tableModel;

    private static final String STAFF_LOGIN_VIEW = "StaffLogin";
    private static final String STAFF_ANNOUNCEMENT_VIEW = "StaffAnnouncement";
    private static final String STAFF_DASHBOARD_VIEW = "StaffDashboard";
    private static final String RESIDENTS_LISTS_VIEW = "ResidentsLists";
    private static final String REQUESTS_VIEW = "Requests"; // Main Requests View

    private static final Color DARK_TEXT = new Color(0, 0, 0);
    private static final Color TEAL_ACCENT = new Color(0, 191, 191);
    private static final Color BG_COLOR = new Color(225, 240, 255);
    private static final Color WHITE_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(50, 70, 90);

    // --- Data Structure ---
    private static class RequestData {
        int requestId;
        String fullName;
        String category;
        String dateSubmitted;

        public RequestData(int requestId, String fullName, String category, String dateSubmitted) {
            this.requestId = requestId;
            this.fullName = fullName;
            this.category = category;
            this.dateSubmitted = formatDate(dateSubmitted);
        }

        private static String formatDate(String dateStr) {
            if (dateStr == null) return "N/A";
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateStr.replace(' ', 'T'));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                return dateTime.format(formatter);
            } catch (Exception e) {
                return dateStr;
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

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        contentPanel.add(createNavigationPanel(), BorderLayout.WEST);
        contentPanel.add(createPendingRequestsTablePanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Auto-refresh every 5 seconds
        Timer timer = new Timer(5000, e -> loadPendingRequests());
        timer.start();

        loadPendingRequests();
    }

    // --- UI Components ---
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        headerPanel.setPreferredSize(new Dimension(800, 70));

        JLabel titleLabel = new JLabel("PENDING REQUESTS");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 30));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

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

        gbc.gridy = 5;
        gbc.insets = new Insets(40, 0, 15, 0);
        navPanel.add(backButton, gbc);
        gbc.insets = new Insets(0, 0, 15, 0);

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        navPanel.add(Box.createVerticalGlue(), gbc);

        return navPanel;
    }

    private JPanel createPendingRequestsTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE_BG);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columnNames = {"ID", "Complainant Name", "Category", "Date Submitted"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pendingRequestsTable = new JTable(tableModel);
        styleTable(pendingRequestsTable);

        JScrollPane scrollPane = new JScrollPane(pendingRequestsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // --- APPROVE / DECLINE BUTTONS PANEL ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBackground(WHITE_BG);

        RoundButton approveBtn = new RoundButton("Approve", new Color(40, 167, 69));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setFont(new Font("Leelawadee", Font.BOLD, 16));
        approveBtn.setPreferredSize(new Dimension(150, 50));
        approveBtn.addActionListener(e -> updateSelectedRequestStatus("Approved"));

        RoundButton declineBtn = new RoundButton("Decline", new Color(220, 53, 69));
        declineBtn.setForeground(Color.WHITE);
        declineBtn.setFont(new Font("Leelawadee", Font.BOLD, 16));
        declineBtn.setPreferredSize(new Dimension(150, 50));
        declineBtn.addActionListener(e -> updateSelectedRequestStatus("Declined"));

        buttonsPanel.add(approveBtn);
        buttonsPanel.add(declineBtn);

        tablePanel.add(buttonsPanel, BorderLayout.SOUTH);

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

    // --- Data Logic ---
    private List<RequestData> fetchPendingRequestsFromDB() {
        List<RequestData> requests = new ArrayList<>();
        String sql = "SELECT request_id, full_name, category, date_submitted FROM resident_requests WHERE status = 'Pending' ORDER BY date_submitted DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(new RequestData(
                        rs.getInt("request_id"),
                        rs.getString("full_name"),
                        rs.getString("category"),
                        rs.getString("date_submitted")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading Pending Requests: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return requests;
    }

    public void loadPendingRequests() {
        List<RequestData> pendingRequests = fetchPendingRequestsFromDB();

        tableModel.setRowCount(0); // Clear existing data

        for (RequestData request : pendingRequests) {
            tableModel.addRow(new Object[]{
                    request.requestId,
                    request.fullName,
                    request.category,
                    request.dateSubmitted
            });
        }
    }

    private void updateSelectedRequestStatus(String status) {
        int selectedRow = pendingRequestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to set Request ID " + requestId + " to '" + status + "'?", 
                "Confirm Action", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "UPDATE resident_requests SET status = ? WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Request updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPendingRequests();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update request.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
