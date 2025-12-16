package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Resident extends JPanel {

    private static final Color BG_COLOR = new Color(220, 235, 250); 
    private static final Color BUTTON_COLOR = new Color(0, 180, 200); 
    private static final String WELCOME_VIEW = "Welcome";
    private static final String RESIDENT_LOGIN_VIEW = "ResidentLogin";

    public Resident(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(50, 0, 50, 0));

        // --- Logo ---
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = null;
        try {
            logoIcon = new ImageIcon(getClass().getResource("/images/CSSLogo.png"));
        } catch (Exception e) {
            System.err.println("Error loading CSSLogo.png: " + e.getMessage());
        }
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
            logoLabel.setPreferredSize(new Dimension(logoIcon.getIconWidth(), logoIcon.getIconHeight()));
        } else {
            logoLabel.setText("Logo Missing");
            logoLabel.setPreferredSize(new Dimension(200, 180));
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(20));

        // --- Title & Tagline ---
        JLabel titleLabel = new JLabel("COMMUNITY SERVICE SYSTEM");
        titleLabel.setFont(new Font("STXinwei", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 70, 90));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(5));

        JLabel taglineLabel = new JLabel("\"Serving Together for a Better Tomorrow\"");
        taglineLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        taglineLabel.setForeground(new Color(100, 120, 140));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(taglineLabel);
        centerPanel.add(Box.createVerticalStrut(40));

        // --- Buttons ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        // Register button
        JButton registerButton = new RoundButton("Register", BUTTON_COLOR);
        registerButton.setPreferredSize(new Dimension(200, 39));
        registerButton.setMaximumSize(new Dimension(200, 50));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 19));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> {
            try {
                parentFrame.setVisible(false);
                RegisterFrame registerFrame = new RegisterFrame(parentFrame);
                registerFrame.setLocationRelativeTo(null);
                registerFrame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, 
                    "Error opening registration window:\n" + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createVerticalStrut(20));

        // Login button
        JButton loginButton = new RoundButton("Log in", BUTTON_COLOR);
        loginButton.setPreferredSize(new Dimension(200, 39));
        loginButton.setMaximumSize(new Dimension(200, 50));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 19));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> cardLayout.show(cardPanel, RESIDENT_LOGIN_VIEW));
        buttonPanel.add(loginButton);

        centerPanel.add(buttonPanel);

        // Back button
        JButton backButton = new RoundButton("Back", BUTTON_COLOR);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 17));
        backButton.setPreferredSize(new Dimension(160, 39));
        backButton.setMaximumSize(new Dimension(160, 45));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> cardLayout.show(cardPanel, WELCOME_VIEW));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(backButton);

        add(centerPanel, BorderLayout.CENTER);
    }
}
