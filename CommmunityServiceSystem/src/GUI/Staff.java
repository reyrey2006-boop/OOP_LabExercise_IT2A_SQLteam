package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Staff extends JPanel {

    private static final Color BG_COLOR = new Color(220, 235, 250);
    private static final Color BUTTON_COLOR = new Color(0, 180, 200);
    private static final String WELCOME_VIEW = "Welcome";
    private static final String STAFF_LOGIN_VIEW = "StaffLogin";

    public Staff(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(15, 20, 0, 0));

        JLabel staffLabel = new JLabel("FOR STAFF");
        staffLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        staffLabel.setForeground(new Color(50, 70, 90));
        headerPanel.add(staffLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(110, 50, 50, 50));

        // Logo (FIXED IMAGE LOADING)
        JLabel logoLabel = new JLabel();

        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/CSSLogo.png"));
            logoLabel.setIcon(logoIcon);
        } catch (Exception e) {
            logoLabel.setText("Logo Missing (CSSLogo.png)");
            System.err.println("ERROR: Cannot load /images/CSSLogo.png");
        }

        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(20));

        // Title
        JLabel titleLabel = new JLabel("COMMUNITY SERVICE SYSTEM");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 70, 90));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);

        // Tagline
        JLabel taglineLabel = new JLabel("\"Serving Together for a Better Tomorrow\"");
        taglineLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        taglineLabel.setForeground(new Color(100, 120, 140));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(taglineLabel);
        centerPanel.add(Box.createVerticalStrut(50));

        // Buttons Container
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false);
        buttonContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login Button
        JButton loginButton = new RoundButton("Log in", BUTTON_COLOR);
        loginButton.setPreferredSize(new Dimension(170, 45));
        loginButton.setMaximumSize(new Dimension(170, 45));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> cardLayout.show(cardPanel, STAFF_LOGIN_VIEW));

        buttonContainer.add(loginButton);
        buttonContainer.add(Box.createVerticalStrut(15));

        // Back Button
        JButton backButton = new RoundButton("Back", BUTTON_COLOR);
        backButton.setPreferredSize(new Dimension(170, 45));
        backButton.setMaximumSize(new Dimension(170, 45));
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, WELCOME_VIEW));

        buttonContainer.add(backButton);

        centerPanel.add(buttonContainer);
        add(centerPanel, BorderLayout.CENTER);

        add(Box.createVerticalGlue(), BorderLayout.SOUTH);
    }
}
