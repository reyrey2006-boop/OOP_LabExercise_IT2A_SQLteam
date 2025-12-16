package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.sql.*;
import java.awt.event.*;
import utils.DBConnection;

public class ResidentLists extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private static final String STAFF_LOGIN_VIEW = "StaffLogin";
    private static final String STAFF_ANNOUNCEMENT_VIEW = "StaffAnnouncement";
    private static final String STAFF_DASHBOARD_VIEW = "StaffDashboard";
    private static final String RESIDENTS_LISTS_VIEW = "ResidentsLists";
    private static final String REQUESTS_VIEW = "Requests";

    private static final Color DARK_TEXT = new Color(0, 0, 0);
    private static final Color TEAL_ACCENT = new Color(0, 191, 191);
    private static final Color BG_COLOR = new Color(225, 240, 255);
    private static final Color WHITE_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(50, 70, 90);
    private static final Color ACCENT_COLOR = new Color(0, 150, 200);
    private static final Color BLOCKED_RED = Color.RED;

    private JTable residentsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public ResidentLists(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHITE_BG);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        headerPanel.setPreferredSize(new Dimension(800, 70));

        JLabel titleLabel = new JLabel("LIST OF RESIDENTS");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 30));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        logoutWrapper.setBackground(WHITE_BG);
        headerPanel.add(logoutWrapper, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Area ---
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // --- Navigation Panel ---
        JPanel navPanel = new JPanel(new GridBagLayout());
        navPanel.setBackground(WHITE_BG);
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
        gbc.gridy = 3; navPanel.add(createStaffNavLink("Residents", true), gbc); // ACTIVE
        gbc.gridy = 4; navPanel.add(createStaffNavLink("Requests", false), gbc);

        // Removed the "Back" button and its related layout constraints (gridy 5 and 6)

        // Vertical glue to push all buttons to the top
        gbc.gridy = 5; gbc.weighty = 1.0; gbc.insets = new Insets(0, 0, 0, 0); navPanel.add(Box.createVerticalGlue(), gbc);
        
        contentPanel.add(navPanel, BorderLayout.WEST);

        // --- Main Data Panel ---
        JPanel mainDataPanel = new JPanel(new BorderLayout(10, 10));
        mainDataPanel.setOpaque(false);

        // --- Search Bar ---
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0,0,10,0));
        searchField = new JTextField();
        searchField.setFont(new Font("Leelawadee", Font.PLAIN, 16));
        JButton searchBtn = new RoundButton("Search", TEAL_ACCENT);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Leelawadee", Font.BOLD, 16));
        searchBtn.setPreferredSize(new Dimension(120, 40));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        mainDataPanel.add(searchPanel, BorderLayout.NORTH);

        // --- Residents Table ---
        String[] columns = {"ID", "First Name", "Last Name", "Birthday", "Sex", "Contact", "Address", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        residentsTable = new JTable(tableModel);
        residentsTable.setFillsViewportHeight(true);
        residentsTable.setRowHeight(30);
        residentsTable.setFont(new Font("Leelawadee", Font.PLAIN, 14));
        residentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // important for horizontal scroll

        // Renderer to color blocked accounts red
        residentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) table.getModel().getValueAt(row, 8);
                if("Blocked".equalsIgnoreCase(status)){
                    c.setForeground(BLOCKED_RED);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(residentsTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(150, 200, 255), 2));
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        mainDataPanel.add(tableScrollPane, BorderLayout.CENTER);

        // --- Bottom Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        buttonPanel.setOpaque(false);

        RoundButton blockButton = new RoundButton("Block", TEAL_ACCENT);
        blockButton.setFont(new Font("Leelawadee", Font.BOLD, 18));
        blockButton.setForeground(Color.WHITE);
        blockButton.setPreferredSize(new Dimension(150, 50));

        RoundButton unblockButton = new RoundButton("Unblock", TEAL_ACCENT);
        unblockButton.setFont(new Font("Leelawadee", Font.BOLD, 18));
        unblockButton.setForeground(Color.WHITE);
        unblockButton.setPreferredSize(new Dimension(150, 50));

        buttonPanel.add(blockButton);
        buttonPanel.add(unblockButton);
        mainDataPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.add(mainDataPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // --- Load Data ---
        loadResidentsData();
        autoResizeTableColumns(residentsTable);

        // --- Search Action ---
        ActionListener searchAction = e -> {
            String term = searchField.getText().trim();
            if(term.isEmpty()){
                loadResidentsData();
            } else {
                searchResidents(term);
            }
            autoResizeTableColumns(residentsTable);
        };
        searchBtn.addActionListener(searchAction);
        searchField.addActionListener(searchAction);

        // --- Block Action ---
        blockButton.addActionListener(e -> blockOrUnblockResident("Blocked"));
        unblockButton.addActionListener(e -> blockOrUnblockResident("Active"));
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
                case "Requests":
                    // Dynamic loading logic for Requests
                    boolean existsRequests = false;
                    for (Component c : cardPanel.getComponents()) {
                        if (c instanceof Requests) {
                            existsRequests = true;
                            break;
                        }
                    }
                    if (!existsRequests) {
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                        Requests requestsPanel = new Requests(parentFrame, this.cardLayout, this.cardPanel);
                        cardPanel.add(requestsPanel, REQUESTS_VIEW);
                    }
                    viewName = REQUESTS_VIEW;
                    break;
            }
            if (!viewName.isEmpty()) this.cardLayout.show(this.cardPanel, viewName);
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

    private void loadResidentsData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT id, firstname, lastname, birthday, sex, contact, address, email, status FROM residents")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getDate("birthday"),
                        rs.getString("sex"),
                        rs.getString("contact"),
                        rs.getString("address"),
                        rs.getString("email"),
                        rs.getString("status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load residents.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchResidents(String term) {
        tableModel.setRowCount(0);
        String query = "SELECT id, firstname, lastname, birthday, sex, contact, address, email, status FROM residents " +
                "WHERE firstname LIKE ? OR lastname LIKE ? OR email LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            String likeTerm = "%" + term + "%";
            ps.setString(1, likeTerm);
            ps.setString(2, likeTerm);
            ps.setString(3, likeTerm);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("firstname"),
                            rs.getString("lastname"),
                            rs.getDate("birthday"),
                            rs.getString("sex"),
                            rs.getString("contact"),
                            rs.getString("address"),
                            rs.getString("email"),
                            rs.getString("status")
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to search residents.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateResidentStatus(int id, String status){
        String query = "UPDATE residents SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Failed to update status.\n"+e.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void blockOrUnblockResident(String targetStatus){
        int selectedRow = residentsTable.getSelectedRow();
        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Select a resident.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 8);

        if(currentStatus.equalsIgnoreCase(targetStatus)){
            JOptionPane.showMessageDialog(this, "Resident is already " + targetStatus + ".", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                targetStatus.equals("Blocked") ? "Block this resident?" : "Unblock this resident?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            updateResidentStatus(id, targetStatus);
            loadResidentsData();
            autoResizeTableColumns(residentsTable);
        }
    }

    private void autoResizeTableColumns(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 10, width);
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
}