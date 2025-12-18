package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import utils.DBConnection;

public class ResidentAnnouncement extends JPanel {

    private static final String RESIDENT_DASHBOARD_VIEW = "ResidentDashboard";
    private static final String RESIDENT_NOTIFICATION_VIEW = "ResidentNotification";
    private static final String ADD_REQUESTS_VIEW = "AddRequests";
    private static final String RESIDENT_PROFILE_VIEW = "ResidentProfile";
    private static final String RESIDENT_ANNOUNCEMENT_VIEW = "ResidentAnnouncement";

    private static final Color MAIN_BG_LIGHT_BLUE = new Color(225, 240, 255);
    private static final Color MENU_PANEL_BG = Color.WHITE;
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEAL_BUTTON = new Color(0, 191, 191);
    private static final Color DARK_GRAY_TEXT = new Color(40, 40, 40);
    private static final Color ANNOUNCEMENT_CARD_BORDER_COLOR = new Color(210, 230, 250);
    private static final Color OUTER_BORDER_COLOR = new Color(180, 220, 240);

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // 🔹 AUTO-REFRESH TARGET PANEL
    private JPanel announcementListPanel;

    // ================= DATA MODEL =================
    private static class AnnouncementData {
        String author;
        String dateTime;
        String statement;

        AnnouncementData(String author, String dateTime, String statement) {
            this.author = author;
            this.dateTime = dateTime;
            this.statement = statement;
        }
    }

    // ================= CONSTRUCTOR =================
    public ResidentAnnouncement(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel, String residentName) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;

        setLayout(new BorderLayout());
        setBackground(MAIN_BG_LIGHT_BLUE);
        setBorder(new LineBorder(OUTER_BORDER_COLOR, 3));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel mainWrapper = new JPanel(new BorderLayout(15, 15));
        mainWrapper.setOpaque(false);
        mainWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainWrapper.add(createMenuPanel(), BorderLayout.WEST);
        mainWrapper.add(createAnnouncementContentPanel(), BorderLayout.CENTER);

        add(mainWrapper, BorderLayout.CENTER);

        // ✅ AUTO REFRESH — NO RESTART REQUIRED
        new Timer(2000, e -> {
            if (isShowing()) {
                refreshAnnouncements();
            }
        }).start();
    }

    // ================= DATABASE =================
    private List<AnnouncementData> fetchAnnouncementsFromDB() {
        List<AnnouncementData> list = new ArrayList<>();
        String sql = "SELECT statement, date, time FROM announcements ORDER BY date DESC, time DESC";

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {

            while (r.next()) {
                list.add(new AnnouncementData(
                        "Community Service Office",
                        r.getString("date") + " | " + r.getString("time"),
                        r.getString("statement")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================= AUTO REFRESH METHOD =================
    private void refreshAnnouncements() {
        announcementListPanel.removeAll();

        List<AnnouncementData> announcements = fetchAnnouncementsFromDB();

        if (announcements.isEmpty()) {
            JLabel empty = new JLabel("No announcements available at this time.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            empty.setForeground(DARK_GRAY_TEXT);
            announcementListPanel.add(empty);
        } else {
            for (int i = 0; i < announcements.size(); i++) {
                announcementListPanel.add(createAnnouncementCard(announcements.get(i)));
                if (i < announcements.size() - 1) {
                    announcementListPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        announcementListPanel.revalidate();
        announcementListPanel.repaint();
    }

    // ================= HEADER =================
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(800, 60));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        return panel;
    }

    // ================= MENU (Aligned with AddRequests) =================
    private JPanel createMenuPanel() {
        ResidentAnnouncement.RoundedPanel panel = new ResidentAnnouncement.RoundedPanel(new GridBagLayout());
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
        menuTitle.setBorder(new javax.swing.border.EmptyBorder(15, 10, 15, 0));

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 15, 10, 15);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(menuTitle, gbc);

        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Navigation Links
        gbc.gridy = 1; panel.add(nav("Dashboard", false), gbc);
        gbc.gridy = 2; panel.add(nav("Req Progress", false), gbc);
        gbc.gridy = 3; panel.add(nav("Announcement", true), gbc); 
        gbc.gridy = 4; panel.add(nav("Add Requests", false), gbc);
        gbc.gridy = 5; panel.add(nav("Profile", false), gbc);

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private RoundButton nav(String text, boolean isSelected) {
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

    // ================= CONTENT =================
    private JPanel createAnnouncementContentPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JLabel title = new JLabel("ANNOUNCEMENT");
        title.setFont(new Font("STXinwei", Font.BOLD, 26));
        title.setBorder(new EmptyBorder(0, 5, 10, 0));
        wrapper.add(title, BorderLayout.NORTH);

        announcementListPanel = new JPanel();
        announcementListPanel.setLayout(new BoxLayout(announcementListPanel, BoxLayout.Y_AXIS));
        announcementListPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(announcementListPanel);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        wrapper.add(scroll, BorderLayout.CENTER);

        refreshAnnouncements(); // Initial load
        return wrapper;
    }

    private JPanel createAnnouncementCard(AnnouncementData d) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ANNOUNCEMENT_CARD_BORDER_COLOR),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel author = new JLabel(d.author);
        author.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel time = new JLabel(d.dateTime);
        time.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        header.add(author, BorderLayout.WEST);
        header.add(time, BorderLayout.EAST);

        JTextArea text = new JTextArea(d.statement);
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.setEditable(false);
        text.setOpaque(false);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        card.add(header, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);

        return card;
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