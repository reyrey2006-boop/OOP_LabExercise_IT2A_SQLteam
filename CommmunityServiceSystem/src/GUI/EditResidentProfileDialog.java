package GUI;

import components.RoundButton;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import utils.DBConnection;

public class EditResidentProfileDialog extends JDialog {

    // --- Colors ---
    private static final Color MAIN_BG_LIGHT_BLUE = new Color(240, 248, 255);
    private static final Color TEAL_BUTTON = new Color(0, 191, 191);   
    private static final Color DARK_GRAY_TEXT = new Color(40, 40, 40);
    private static final Color CARD_BG = Color.WHITE;

    // --- Form Fields ---
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField contactField;
    private JTextField addressField;
    private JTextField birthdayField;
    private JComboBox<String> sexComboBox;

    private String originalEmailIdentifier;

    public EditResidentProfileDialog(JFrame parentFrame, String residentIdentifier) {
        super(parentFrame, "Edit Resident Profile", true);

        this.originalEmailIdentifier = residentIdentifier;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(550, 650);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());
        setBackground(MAIN_BG_LIGHT_BLUE);

        JScrollPane scrollPane = new JScrollPane(createFormPanel());
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        loadExistingData();
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setBackground(CARD_BG);
        formWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        int y = 0;

        JLabel title = new JLabel("RESIDENT INFORMATION");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEAL_BUTTON.darker());
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.gridwidth = 2;
        formWrapper.add(title, gbc);

        JSeparator separator = new JSeparator();
        gbc.gridy = y++;
        gbc.insets = new Insets(10, 0, 20, 0);
        formWrapper.add(separator, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 1;

        firstNameField = new JTextField(20);
        y = addFormField(formWrapper, "First Name:", firstNameField, y);

        lastNameField = new JTextField(20);
        y = addFormField(formWrapper, "Last Name:", lastNameField, y);

        emailField = new JTextField(20);
        y = addFormField(formWrapper, "Email Address:", emailField, y);

        contactField = new JTextField(20);
        y = addFormField(formWrapper, "Contact Number:", contactField, y);

        birthdayField = new JTextField(20);
        y = addFormField(formWrapper, "Date of Birth (YYYY-MM-DD):", birthdayField, y);

        sexComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other", "N/A"});
        y = addFormField(formWrapper, "Sex:", sexComboBox, y);

        addressField = new JTextField(20);
        y = addFormField(formWrapper, "Home Address:", addressField, y, 2);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weighty = 1.0;
        gbc.gridwidth = 2;
        formWrapper.add(Box.createVerticalGlue(), gbc);

        return formWrapper;
    }

    private int addFormField(JPanel panel, String labelText, JComponent field, int y) {
        return addFormField(panel, labelText, field, y, 2);
    }

    private int addFormField(JPanel panel, String labelText, JComponent field, int y, int span) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(DARK_GRAY_TEXT);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = span - 1;
        panel.add(field, gbc);

        return y + 1;
    }

    private void loadExistingData() {
        String sql = "SELECT firstname, lastname, birthday, sex, contact, address, email FROM residents WHERE email=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, originalEmailIdentifier);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    firstNameField.setText(rs.getString("firstname"));
                    lastNameField.setText(rs.getString("lastname"));
                    birthdayField.setText(rs.getString("birthday"));
                    sexComboBox.setSelectedItem(rs.getString("sex"));
                    contactField.setText(rs.getString("contact"));
                    addressField.setText(rs.getString("address"));
                    emailField.setText(rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading resident data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(MAIN_BG_LIGHT_BLUE);
        panel.setBorder(new EmptyBorder(0, 15, 15, 15));

        RoundButton save = new RoundButton("Save Changes", TEAL_BUTTON.darker());
        save.setPreferredSize(new Dimension(150, 40));
        save.setCursor(new Cursor(Cursor.HAND_CURSOR));
        save.setForeground(Color.WHITE);
        
        save.addActionListener(e -> saveChanges());
        

        RoundButton cancel = new RoundButton("Cancel", Color.LIGHT_GRAY);
        cancel.setPreferredSize(new Dimension(100, 40));
        cancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancel.addActionListener(e -> dispose());

        panel.add(cancel);
        panel.add(save);

        return panel;
    }

    private void saveChanges() {
        String sql = "UPDATE residents SET firstname=?, lastname=?, birthday=?, sex=?, contact=?, address=?, email=? WHERE email=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, firstNameField.getText().trim());
            ps.setString(2, lastNameField.getText().trim());
            ps.setString(3, birthdayField.getText().trim());
            ps.setString(4, sexComboBox.getSelectedItem().toString());
            ps.setString(5, contactField.getText().trim());
            ps.setString(6, addressField.getText().trim());
            ps.setString(7, emailField.getText().trim());
            ps.setString(8, originalEmailIdentifier);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Profile updated successfully.");
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
