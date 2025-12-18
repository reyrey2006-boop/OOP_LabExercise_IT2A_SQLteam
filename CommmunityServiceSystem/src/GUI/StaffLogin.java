package GUI;

import components.RoundButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class StaffLogin extends JPanel {

    private static final String STAFF_VIEW = "Staff";
    private static final String STAFF_DASHBOARD_VIEW = "StaffDashboard";
    
    private static final String VALID_EMAIL = "communityService@gmail.com";
    private static final String VALID_PASSWORD = "communitypass123";
    
    // MODIFIED: Made fields instance variables for external access
    private JTextField emailField;
    private JPasswordField passwordField;

    public StaffLogin(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        

        JPanel leftPanel = new JPanel();
            leftPanel.setBackground(new Color(204, 227, 246));
            leftPanel.setPreferredSize(new Dimension(469, 0));
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setBorder(new EmptyBorder(80, 20, 80, 22));

            
        JLabel logo = new JLabel();
            try {
               ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/CSSLogo.png"));
               Image scaled = originalIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
               logo.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
               logo.setText("Logo Missing");
               logo.setHorizontalAlignment(SwingConstants.CENTER);
               logo.setPreferredSize(new Dimension(250, 250));
               System.err.println("Error loading CSSLogo.png from /images/: " + e.getMessage());
            }
               logo.setAlignmentX(Component.CENTER_ALIGNMENT);


        // SYSTEM NAME
        JLabel systemName = new JLabel("COMMUNITY SERVICE SYSTEM");
            systemName.setFont(new Font("STXinwei", Font.BOLD, 20));
            systemName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // SLOGAN
        JLabel slogan = new JLabel("\"Serving Together for a Better Tomorrow\"");
            slogan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(logo);
        leftPanel.add(Box.createVerticalStrut(10)); // Adjusted vertical strut for better spacing
        leftPanel.add(systemName);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(slogan);
        leftPanel.add(Box.createVerticalGlue());

        // --- RIGHT PANEL (White Background) ---
        JPanel rightPanel = new JPanel();
            rightPanel.setBackground(Color.WHITE);
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setBorder(new EmptyBorder(80, 100, 80, 100));

        // TITLE
        JLabel title = new JLabel("STAFF LOG IN");
            title.setFont(new Font("Segoe UI", Font.BOLD, 28));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            title.setBorder(new EmptyBorder(0, 0, 40, 0));
            rightPanel.add(title);

        // EMAIL PANEL
        JPanel emailPanel = new JPanel();
            emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
            emailPanel.setBackground(Color.WHITE);
            emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("Default Email");
            emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // MODIFIED: Initialize instance variable
        emailField = new JTextField();
            emailField.setMaximumSize(new Dimension(317, 44));
            emailField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

            emailPanel.add(emailLabel);
            emailPanel.add(Box.createVerticalStrut(5));
            emailPanel.add(emailField);
            emailPanel.add(Box.createVerticalStrut(30));
            rightPanel.add(emailPanel);

        JPanel passPanel = new JPanel();
            passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
            passPanel.setBackground(Color.WHITE);
            passPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passLabel = new JLabel("Default Password");
            passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // MODIFIED: Initialize instance variable
        passwordField = new JPasswordField();
            passwordField.setMaximumSize(new Dimension(317, 44));
            passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
            passwordField.setEchoChar('•'); // Ensure default is masked

        // Show Password Checkbox
        JCheckBox showPass = new JCheckBox("Show Password");
            showPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            showPass.setBackground(Color.WHITE);
            showPass.setAlignmentX(Component.LEFT_ALIGNMENT);
            showPass.setBorder(new EmptyBorder(5, 0, 0, 0));

            showPass.addActionListener(e ->
                passwordField.setEchoChar(showPass.isSelected() ? (char) 0 : '•')
        );

        passPanel.add(passLabel);
        passPanel.add(Box.createVerticalStrut(5));
        passPanel.add(passwordField);
        passPanel.add(showPass);
        passPanel.add(Box.createVerticalStrut(40));
        rightPanel.add(passPanel);
        rightPanel.add(Box.createVerticalStrut(25));


        // BUTTON PANEL
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0));
            btnPanel.setBackground(Color.WHITE);
            btnPanel.setMaximumSize(new Dimension(350, 45));

        JButton cancelBtn = new RoundButton("Cancel", new Color(135, 180, 230));
            cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setFocusPainted(false);
            cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            cancelBtn.addActionListener(e -> {
                emailField.setText("");
                passwordField.setText("");

                cardLayout.show(cardPanel, STAFF_VIEW);
            });


        JButton loginBtn = new RoundButton("Login", new Color(0, 180, 170));
            loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            loginBtn.setForeground(Color.WHITE);
            loginBtn.setFocusPainted(false);
            loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(passwordField.getPassword());
            
            // Check against built-in credentials
            if (email.equals(VALID_EMAIL) && pass.equals(VALID_PASSWORD)) {
                // Success
                JOptionPane.showMessageDialog(parentFrame,
                    "Staff Login Successful!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                cardLayout.show(cardPanel, STAFF_DASHBOARD_VIEW); 
            } else {
                // Failure
                JOptionPane.showMessageDialog(parentFrame,
                    "Invalid email or password. Please try again.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });


        btnPanel.add(cancelBtn);
        btnPanel.add(loginBtn);

        rightPanel.add(btnPanel);

        // ADD PANELS TO MAIN PANEL
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
    
    // ADDED: Public method to clear fields from external caller
    public void clearFields() {
        if (emailField != null) {
            emailField.setText("");
        }
        if (passwordField != null) {
            passwordField.setText("");
        }
    }
}