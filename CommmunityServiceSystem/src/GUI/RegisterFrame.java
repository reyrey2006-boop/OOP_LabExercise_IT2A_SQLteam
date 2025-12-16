package GUI;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import utils.DBConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterFrame extends JFrame {

    private JFrame parentFrame;

    public RegisterFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;

        setTitle("Community Service System");
        setSize(550, 766);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Program icon - FIX APPLIED HERE: Changed "/image/CSSLogo.png" to "/images/CSSLogo.png"
        ImageIcon logo = new ImageIcon(getClass().getResource("/images/CSSLogo.png"));
        this.setIconImage(logo.getImage());

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(230, 240, 255));

        GridBagConstraints gbcTitle = new GridBagConstraints();
        gbcTitle.gridx = 0;
        gbcTitle.gridy = 0;
        gbcTitle.gridwidth = 2;
        gbcTitle.fill = GridBagConstraints.HORIZONTAL;
        gbcTitle.insets = new Insets(10, 0, 30, 0);

        JLabel title = new JLabel("Create Your Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        mainPanel.add(title, gbcTitle);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        java.util.function.Consumer<JComponent> setFieldSize = field ->
                field.setPreferredSize(new Dimension(220, 28));

        // First Name
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        JTextField firstname = new JTextField(); setFieldSize.accept(firstname);
        mainPanel.add(firstname, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        JTextField lastname = new JTextField(); setFieldSize.accept(lastname);
        mainPanel.add(lastname, gbc);

        // Birthday
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Birthday:"), gbc);
        gbc.gridx = 1;
        JPanel birthdayPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        JComboBox<String> month = new JComboBox<>(new String[]{
                "Month","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"});
        JComboBox<String> day = new JComboBox<>();
        day.addItem("Day"); for(int i=1;i<=31;i++) day.addItem(String.valueOf(i));
        JComboBox<String> year = new JComboBox<>(); year.addItem("Year"); for(int i=2025;i>=1900;i--) year.addItem(String.valueOf(i));
        birthdayPanel.add(month); birthdayPanel.add(day); birthdayPanel.add(year);
        mainPanel.add(birthdayPanel, gbc);

        // Sex
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Sex:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> sexCombo = new JComboBox<>(new String[]{"Male","Female"});
        setFieldSize.accept(sexCombo);
        mainPanel.add(sexCombo, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Contact Number:"), gbc);
        gbc.gridx = 1;
        JTextField contact = new JTextField(); setFieldSize.accept(contact);
        setDigitLimit(contact,11);
        mainPanel.add(contact, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Full Address:"), gbc);
        gbc.gridx = 1;
        JTextField address = new JTextField(); setFieldSize.accept(address);
        mainPanel.add(address, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Email Address:"), gbc);
        gbc.gridx = 1;
        JTextField email = new JTextField(); setFieldSize.accept(email);
        mainPanel.add(email, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Create Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField password = new JPasswordField(); setFieldSize.accept(password); password.setEchoChar('\u2022');
        JButton togglePass = new JButton("Show"); togglePass.setPreferredSize(new Dimension(70,28));
        JPanel passPanel = new JPanel(new BorderLayout()); passPanel.add(password, BorderLayout.CENTER); passPanel.add(togglePass, BorderLayout.EAST);
        mainPanel.add(passPanel, gbc);
        togglePass.addActionListener(e -> {
            if(password.getEchoChar()=='\u2022'){ password.setEchoChar((char)0); togglePass.setText("Hide"); }
            else { password.setEchoChar('\u2022'); togglePass.setText("Show"); }
        });

        // Confirm password
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPassword = new JPasswordField(); setFieldSize.accept(confirmPassword); confirmPassword.setEchoChar('\u2022');
        JButton toggleConfirm = new JButton("Show"); toggleConfirm.setPreferredSize(new Dimension(70,28));
        JPanel confirmPanel = new JPanel(new BorderLayout()); confirmPanel.add(confirmPassword, BorderLayout.CENTER); confirmPanel.add(toggleConfirm, BorderLayout.EAST);
        mainPanel.add(confirmPanel, gbc);
        toggleConfirm.addActionListener(e -> {
            if(confirmPassword.getEchoChar()=='\u2022'){ confirmPassword.setEchoChar((char)0); toggleConfirm.setText("Hide"); }
            else{ confirmPassword.setEchoChar('\u2022'); toggleConfirm.setText("Show"); }
        });

        // Buttons
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth=2; gbc.anchor=GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new GridLayout(1,2,50,0));
        JButton cancelBtn = new JButton("Cancel"); cancelBtn.setBackground(new Color(180,50,50)); cancelBtn.setForeground(Color.WHITE); cancelBtn.setFont(new Font("Arial",Font.BOLD,16));
        cancelBtn.addActionListener(e -> { dispose(); parentFrame.setVisible(true); });
        JButton registerBtn = new JButton("Register"); registerBtn.setBackground(new Color(0,150,200)); registerBtn.setForeground(Color.WHITE); registerBtn.setFont(new Font("Arial",Font.BOLD,16));
        btnPanel.add(cancelBtn); btnPanel.add(registerBtn); mainPanel.add(btnPanel, gbc);

        // Register button logic (save to DB)
        registerBtn.addActionListener(e -> {
            String first = firstname.getText().trim();
            String last = lastname.getText().trim();
            String add = address.getText().trim();
            String emailText = email.getText().trim();
            String contactText = contact.getText().trim();
            String pass = new String(password.getPassword());
            String confirmPass = new String(confirmPassword.getPassword());

            // Empty fields validation
            if(first.isEmpty() || last.isEmpty() || add.isEmpty() || emailText.isEmpty() || contactText.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()
                || month.getSelectedIndex()==0 || day.getSelectedIndex()==0 || year.getSelectedIndex()==0) {
                JOptionPane.showMessageDialog(this,"Please fill out all fields.","Incomplete Form",JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Email validation
            if(!isValidEmail(emailText)){
                JOptionPane.showMessageDialog(this,"Please enter a valid email.","Invalid Email",JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Contact validation
            if(contactText.length()!=11){
                JOptionPane.showMessageDialog(this,"Contact number must be 11 digits.","Invalid Contact",JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Password match
            if(!pass.equals(confirmPass)){
                JOptionPane.showMessageDialog(this,"Passwords do not match.","Password Error",JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Birthday as YYYY-MM-DD
            int monthIndex = month.getSelectedIndex(); // Jan=1
            String birth = year.getSelectedItem()+"-"+monthIndex+"-"+day.getSelectedItem();

            // Hash password
            String hashedPass = hashPassword(pass);

            // Save to DB
            try(Connection conn = DBConnection.getConnection()){
                String sql = "INSERT INTO residents (firstname, lastname, birthday, sex, contact, address, email, password) VALUES (?,?,?,?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, first);
                ps.setString(2, last);
                ps.setDate(3, java.sql.Date.valueOf(birth));
                ps.setString(4, sexCombo.getSelectedItem().toString());
                ps.setString(5, contactText);
                ps.setString(6, add);
                ps.setString(7, emailText);
                ps.setString(8, hashedPass);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,"Registration Successful!");
                dispose(); parentFrame.setVisible(true);

            } catch(SQLException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,"Error saving data: "+ex.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
            }

        });

        add(mainPanel);
    }

    // Limit digits
    private void setDigitLimit(JTextField textField, int maxLength){
        AbstractDocument doc = (AbstractDocument) textField.getDocument();
        doc.setDocumentFilter(new DocumentFilter(){
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException{
                if(string==null) return;
                String newString = string.replaceAll("[^0-9]", "");
                if(fb.getDocument().getLength()+newString.length()<=maxLength)
                    super.insertString(fb, offset, newString, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException{
                if(text==null) return;
                String newText = text.replaceAll("[^0-9]", "");
                if(fb.getDocument().getLength()-length+newText.length()<=maxLength)
                    super.replace(fb, offset, length, newText, attrs);
            }
        });
    }

    private boolean isValidEmail(String email){
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(emailRegex,email);
    }

    // Hash password using SHA-256
    private String hashPassword(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(byte b: hash) sb.append(String.format("%02x", b));
            return sb.toString();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return password; // fallback
        }
    }
}