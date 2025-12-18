package GUI;

import components.RoundButton;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import utils.DBConnection;
import java.security.MessageDigest;
// Added imports for the listener
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ResidentLogin extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private static final String RESIDENT_VIEW = "Resident";
    private static final String RESIDENT_DASHBOARD_VIEW = "ResidentDashboard"; 
    private static final String RESIDENT_PROFILE_VIEW = "ResidentProfile"; 

    private JTextField emailField;
    private JPasswordField passwordField;

    public ResidentLogin(JFrame parentFrame, CardLayout cardLayout, JPanel cardPanel) {
        this.cardLayout = cardLayout;
        this.cardPanel = cardPanel;
        
        setLayout(new BorderLayout());

        // --- AUTO REFRESH LOGIC ---
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                clearFields();
            }
        });

        // --- LEFT PANEL (LAYOUT UNCHANGED) ---
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
        }
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel systemName = new JLabel("COMMUNITY SERVICE SYSTEM");
        systemName.setFont(new Font("STXinwei", Font.BOLD, 20));
        systemName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel slogan = new JLabel("\"Serving Together for a Better Tomorrow\"");
        slogan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(logo);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(systemName);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(slogan);
        leftPanel.add(Box.createVerticalGlue());

        // --- RIGHT PANEL (LAYOUT UNCHANGED) ---
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(80, 100, 80, 100));

        JLabel title = new JLabel("LOG IN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 40, 0));
        rightPanel.add(title);

        // EMAIL PANEL
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBackground(Color.WHITE);
        emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(317, 44));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailPanel.add(emailLabel);
        emailPanel.add(Box.createVerticalStrut(5));
        emailPanel.add(emailField);
        emailPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(emailPanel);

        // PASSWORD PANEL
        JPanel passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        passPanel.setBackground(Color.WHITE);
        passPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(317, 44));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setEchoChar('•');

        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPass.setBackground(Color.WHITE);
        showPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPass.setBorder(new EmptyBorder(5, 0, 0, 0));
        showPass.addActionListener(e -> passwordField.setEchoChar(showPass.isSelected() ? (char)0 : '•'));

        passPanel.add(passLabel);
        passPanel.add(Box.createVerticalStrut(5));
        passPanel.add(passwordField);
        passPanel.add(showPass);
        passPanel.add(Box.createVerticalStrut(40));
        rightPanel.add(passPanel);

        // REGISTER LABEL PANEL
        JPanel regPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        regPanel.setBackground(Color.WHITE);
        regPanel.setMaximumSize(new Dimension(350, 30));

        JLabel noAcc = new JLabel("Don't have an account?");
        noAcc.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel register = new JLabel("Register");
        register.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        register.setForeground(new Color(0, 90, 200));
        register.setCursor(new Cursor(Cursor.HAND_CURSOR));
        register.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearFields(); 
                parentFrame.setVisible(false);
                new RegisterFrame(parentFrame).setVisible(true); 
            }
        });

        regPanel.add(noAcc);
        regPanel.add(register);
        rightPanel.add(regPanel);
        rightPanel.add(Box.createVerticalStrut(25));

        // BUTTON PANEL
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setMaximumSize(new Dimension(350, 45));

        JButton cancelBtn = new RoundButton("Cancel", new Color(135, 180, 230));
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> {
            clearFields();
            cardLayout.show(cardPanel, RESIDENT_VIEW);
        });

        JButton loginBtn = new RoundButton("Login", new Color(0, 180, 170));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- UPDATED LOGIN LOGIC ---
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if(email.isEmpty() || pass.isEmpty()){
                JOptionPane.showMessageDialog(parentFrame, "Please enter email and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try(Connection conn = DBConnection.getConnection()){
                String sql = "SELECT password, firstname, lastname, status FROM residents WHERE email = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();

                if(rs.next()){
                    if(rs.getString("status").equals("Blocked")){
                        JOptionPane.showMessageDialog(parentFrame, "Your account has been BLOCKED.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String storedHash = rs.getString("password");
                    if(storedHash.equals(hashPassword(pass))){
                        String firstname = rs.getString("firstname");
                        String lastname = rs.getString("lastname");
                        String fullName = firstname + " " + lastname;
                        
                        JOptionPane.showMessageDialog(parentFrame, "Welcome, " + firstname + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);

                        // Refresh views to ensure user session data is passed correctly
                        for (Component comp : cardPanel.getComponents()) {
                            if (comp.getName() != null && (
                                comp.getName().equals(RESIDENT_DASHBOARD_VIEW) || 
                                comp.getName().equals(RESIDENT_PROFILE_VIEW) ||
                                comp.getName().equals("AddRequests") ||
                                comp.getName().equals("ResidentNotification")
                            )) {
                                cardPanel.remove(comp);
                            }
                        }
                        
                        // Re-initialize panels with the logged-in user's details
                        ResidentDashboard newDashboard = new ResidentDashboard(parentFrame, cardLayout, cardPanel, firstname);
                        newDashboard.setName(RESIDENT_DASHBOARD_VIEW); 
                        cardPanel.add(newDashboard, RESIDENT_DASHBOARD_VIEW);

                        ResidentProfile newProfile = new ResidentProfile(parentFrame, cardLayout, cardPanel, email);
                        newProfile.setName(RESIDENT_PROFILE_VIEW); 
                        cardPanel.add(newProfile, RESIDENT_PROFILE_VIEW);

                        // Pass fullName and email to AddRequests for automatic storage
                        AddRequests newAddReq = new AddRequests(parentFrame, cardLayout, cardPanel, fullName, email);
                        newAddReq.setName("AddRequests");
                        cardPanel.add(newAddReq, "AddRequests");

                        // Pass email to ResidentNotification to filter status updates
                        ResidentNotification newNotif = new ResidentNotification(parentFrame, cardLayout, cardPanel, email);
                        newNotif.setName("ResidentNotification");
                        cardPanel.add(newNotif, "ResidentNotification");

                        cardLayout.show(cardPanel, RESIDENT_DASHBOARD_VIEW);

                    } else {
                        JOptionPane.showMessageDialog(parentFrame, "Incorrect password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Invalid Credentials!", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(loginBtn);
        rightPanel.add(btnPanel);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    public void clearFields() {
        if (emailField != null) emailField.setText("");
        if (passwordField != null) passwordField.setText("");
    }

    private String hashPassword(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(byte b: hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch(Exception e){
            e.printStackTrace();
            return password;
        }
    }
}