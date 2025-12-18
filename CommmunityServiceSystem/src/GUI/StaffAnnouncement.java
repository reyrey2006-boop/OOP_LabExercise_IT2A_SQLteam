package GUI;

import components.RoundButton;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;

import utils.DBConnection; // Your DB connection utility

public class StaffAnnouncement extends JPanel {

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

    private JPanel announcementListPanel;

    public StaffAnnouncement(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        headerPanel.setPreferredSize(new Dimension(800, 70));

        JLabel titleLabel = new JLabel("ANNOUNCEMENT");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 30));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        logoutWrapper.setBackground(Color.WHITE);
        headerPanel.add(logoutWrapper, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content ---
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // --- Sidebar ---
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

        // Set 'Announcement' as active = true
        gbc.gridy = 1; navPanel.add(createNavButton("Dashboard", false), gbc);
        gbc.gridy = 2; navPanel.add(createNavButton("Announcement", true), gbc);
        gbc.gridy = 3; navPanel.add(createNavButton("Residents", false), gbc);
        gbc.gridy = 4; navPanel.add(createNavButton("Requests", false), gbc);
        gbc.gridy = 5; gbc.weighty = 1.0; navPanel.add(Box.createVerticalGlue(), gbc);

        contentPanel.add(navPanel, BorderLayout.WEST);

        // --- Announcement List ---
        announcementListPanel = new JPanel();
        announcementListPanel.setLayout(new BoxLayout(announcementListPanel, BoxLayout.Y_AXIS));
        announcementListPanel.setBackground(BG_COLOR);
        announcementListPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        JScrollPane scrollPane = new JScrollPane(announcementListPanel);
        scrollPane.setBorder(null);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // --- Post Button ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        bottomPanel.setBackground(BG_COLOR);

        JButton postBtn = new JButton("Post New Announcement");
        postBtn.setBackground(TEAL_ACCENT);
        postBtn.setForeground(Color.WHITE);
        postBtn.setFocusPainted(false);
        postBtn.setPreferredSize(new Dimension(200, 40));
        postBtn.addActionListener(e -> openPostAnnouncementFrame());

        bottomPanel.add(postBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load announcements from database
        loadAnnouncementsFromDB();

        if (parentFrame != null) parentFrame.setVisible(true);
    }

    private void loadAnnouncementsFromDB() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM announcements ORDER BY id DESC")) {
            while (rs.next()) {
                String statement = rs.getString("statement");
                String date = rs.getString("date");
                String time = rs.getString("time");
                announcementListPanel.add(createAnnouncementCard("Community Service Office", date, time, statement)); // Changed author to Admin
                announcementListPanel.add(Box.createVerticalStrut(10));
            }
            announcementListPanel.revalidate();
            announcementListPanel.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load announcements from database.\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPostAnnouncementFrame() {
        JDialog postFrame = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Announcement", true);
        postFrame.setSize(400, 300);
        postFrame.setLocationRelativeTo(this);
        postFrame.setLayout(new BorderLayout(10, 10));

        JTextArea announcementArea = new JTextArea();
        announcementArea.setLineWrap(true);
        announcementArea.setWrapStyleWord(true);
        announcementArea.setFont(new Font("Leelawadee", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(announcementArea);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton postBtn = new JButton("Post");
        postBtn.setBackground(TEAL_ACCENT);
        postBtn.setForeground(Color.WHITE);
        postBtn.setFocusPainted(false);
        postBtn.addActionListener(e -> {
            String content = announcementArea.getText().trim();
            if (!content.isEmpty()) {
                ZonedDateTime phTime = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
                String date = phTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String time = phTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                // Store in database
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pst = conn.prepareStatement(
                             "INSERT INTO announcements (statement, date, time) VALUES (?, ?, ?)")) {
                    pst.setString(1, content);
                    pst.setString(2, date);
                    pst.setString(3, time);
                    pst.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(postFrame,
                            "Failed to save announcement.\n" + ex.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                announcementListPanel.add(createAnnouncementCard("Admin", date, time, content)); // Author = Admin
                announcementListPanel.add(Box.createVerticalStrut(10));
                announcementListPanel.revalidate();
                announcementListPanel.repaint();
                postFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(postFrame, "Announcement cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(postBtn);

        postFrame.add(scrollPane, BorderLayout.CENTER);
        postFrame.add(bottomPanel, BorderLayout.SOUTH);
        postFrame.setVisible(true);
    }

    private JPanel createAnnouncementCard(String author, String date, String time, String statement) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(WHITE_BG);

        JLabel authorLabel = new JLabel(author);
        authorLabel.setFont(new Font("Leelawadee", Font.BOLD, 16));

        JLabel dateTimeLabel = new JLabel("   " + date + " | " + time); // Adjusted spacing before date
        dateTimeLabel.setFont(new Font("Leelawadee", Font.PLAIN, 14));
        dateTimeLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(WHITE_BG);
        titlePanel.add(authorLabel, BorderLayout.WEST);
        titlePanel.add(dateTimeLabel, BorderLayout.EAST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnPanel.setBackground(WHITE_BG);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(TEAL_ACCENT);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);

        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(TEAL_ACCENT);
        editBtn.setForeground(Color.WHITE);
        editBtn.setFocusPainted(false);

        // Delete
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this announcement?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pst = conn.prepareStatement("DELETE FROM announcements WHERE statement = ? AND time = ?")) {
                    pst.setString(1, statement);
                    pst.setString(2, time);
                    pst.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to delete announcement.\n" + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                announcementListPanel.remove(card);
                announcementListPanel.revalidate();
                announcementListPanel.repaint();
            }
        });

        // Edit
        editBtn.addActionListener(e -> {
            JTextArea editArea = new JTextArea(statement);
            editArea.setLineWrap(true);
            editArea.setWrapStyleWord(true);
            editArea.setFont(new Font("Leelawadee", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(editArea);
            scrollPane.setPreferredSize(new Dimension(350, 150));

            int result = JOptionPane.showConfirmDialog(this, scrollPane, "Edit Announcement", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String newText = editArea.getText().trim();
                if (!newText.isEmpty()) {
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement pst = conn.prepareStatement(
                                 "UPDATE announcements SET statement = ? WHERE statement = ? AND time = ?")) {
                        pst.setString(1, newText);
                        pst.setString(2, statement);
                        pst.setString(3, time);
                        pst.executeUpdate();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Failed to update announcement.\n" + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    for (Component c : card.getComponents()) {
                        if (c instanceof JTextArea) {
                            ((JTextArea) c).setText(newText);
                            break;
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Announcement cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnPanel.add(deleteBtn);
        btnPanel.add(editBtn);

        topRow.add(titlePanel, BorderLayout.WEST);
        topRow.add(btnPanel, BorderLayout.EAST);

        JTextArea statementArea = new JTextArea(statement);
        statementArea.setLineWrap(true);
        statementArea.setWrapStyleWord(true);
        statementArea.setEditable(false);
        statementArea.setBackground(WHITE_BG);
        statementArea.setBorder(new EmptyBorder(5, 0, 0, 0));

        card.add(topRow, BorderLayout.NORTH);
        card.add(statementArea, BorderLayout.CENTER);

        return card;
    }

    private RoundButton createNavButton(String text, boolean isActive) {
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
                case "Dashboard":
                    viewName = STAFF_DASHBOARD_VIEW;
                    break;
                case "Announcement":
                    viewName = STAFF_ANNOUNCEMENT_VIEW;
                    break;
                case "Residents":
                    boolean exists = false;
                    for (Component c : cardPanel.getComponents()) {
                        if (c instanceof ResidentLists) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                        ResidentLists residentsPanel = new ResidentLists(parentFrame, this.cardLayout, this.cardPanel);
                        cardPanel.add(residentsPanel, RESIDENTS_LISTS_VIEW);
                    }
                    viewName = RESIDENTS_LISTS_VIEW;
                    break;
                case "Requests":
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
}
