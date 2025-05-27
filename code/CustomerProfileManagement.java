import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CustomerProfileManagement extends JFrame implements ActionListener {
    // Colors matching design
    private static final Color BG_COLOR   = new Color(108, 17, 15);
    private static final Color FIELD_BG   = new Color(121, 65, 63);
    private static final Color BUTTON_BG  = new Color(114, 40, 38);
    private static final Color BUTTON_BL  = new Color(41, 41, 41);

    private static final Color FG_WHITE   = Color.WHITE;
    private int customerId;
    // Form fields
    private RoundedTextField emailField;
    private RoundedTextField phoneField;
    private RoundedPasswordField passwordField;
    private RoundedTextField addressField;

    public CustomerProfileManagement(int customerId) {

        setTitle("Customer Profile Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 650);
        setLocationRelativeTo(null);
        this.customerId = customerId;
        // Main panel with BorderLayout
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(createLabel("تقني", 30, Font.BOLD));
        header.add(Box.createRigidArea(new Dimension(0, 10)));
        header.add(createLabel("Customer Profile", 22, Font.BOLD));
        header.add(createLabel("Management", 22, Font.BOLD));
        header.add(Box.createRigidArea(new Dimension(0, 30)));
        main.add(header, BorderLayout.NORTH);

        // Form panel with GridBagLayout for two columns
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Changed to HORIZONTAL
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0; // Allow columns to expand


        // Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        emailField = new RoundedTextField(25);  // Standard size
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));  // Adjusted font size
        emailField.setForeground(FG_WHITE);  // Set text color to white
        form.add(createFieldGroup("Email", emailField), gbc);

        // Phone
        gbc.gridx = 1;
        gbc.gridy=0;

        phoneField = new RoundedTextField(25);  // Standard size
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));  // Adjusted font size
        phoneField.setForeground(FG_WHITE);  // Set text color to white
        form.add(createFieldGroup("Phone", phoneField), gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        passwordField = new RoundedPasswordField(25);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));  // Adjusted font size
        passwordField.setForeground(FG_WHITE);
        form.add(createFieldGroup("PASSWORD", passwordField), gbc);

        // Address
        gbc.gridx = 1;
        addressField = new RoundedTextField(25);
        addressField.setFont(new Font("Arial", Font.PLAIN, 14));
        addressField.setForeground(FG_WHITE);
        form.add(createFieldGroup("Edit address",addressField ), gbc);

        main.add(form, BorderLayout.CENTER);

        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        btnPanel.setOpaque(false);
        RoundedButton backBtn = new RoundedButton("Back", BUTTON_BL);
        backBtn.setActionCommand("BACK"); backBtn.addActionListener(this);
        RoundedButton updateBtn = new RoundedButton("Update", BUTTON_BL);
        updateBtn.setActionCommand("UPDATE"); updateBtn.addActionListener(this);
        RoundedButton deleteBtn = new RoundedButton("Delete account", BUTTON_BL);

        deleteBtn.setActionCommand("DELETE"); deleteBtn.addActionListener(this);
        btnPanel.add(backBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        main.add(btnPanel, BorderLayout.SOUTH);

        add(main);
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setForeground(FG_WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));

        field.setPreferredSize(new Dimension(300, 30));
        field.setMaximumSize(new Dimension(300, 30));
        field.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        group.add(label);
        group.add(Box.createRigidArea(new Dimension(1, 5)));
        group.add(field);
        return group;
    }

    private JLabel createLabel(String text, int size, int style) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", style, size));
        lbl.setForeground(FG_WHITE);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "BACK":
                CustomerDashboard customerDashboard =new CustomerDashboard(customerId);
                customerDashboard.setVisible(true);
                this.dispose();
                break;
            case "UPDATE":
                updateProfile();
                break;
            case "DELETE":
                int choice = JOptionPane.showConfirmDialog(this, "Delete account?", "Confirm deletion", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    deleteAccount();
                }
                break;
        }
    }
    private void updateProfile() {
        String emailInput = emailField.getText().trim();
        String phoneInput = phoneField.getText().trim();
        String addressInput = addressField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String passwordInput = new String(passwordChars);

        if (emailInput.isEmpty() || phoneInput.isEmpty() || addressInput.isEmpty() || passwordInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "protocol:subProtcol:URI",
                "Username", "Password"))
        {
            String sql = "UPDATE customers SET Email = ?, Phone = ?, Address = ?, PasswordHash = ? WHERE CustomerID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, emailInput);
            ps.setString(2, phoneInput);
            ps.setString(3, addressInput);
            ps.setString(4, AuthService.md5(passwordInput));
            ps.setInt(5, customerId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                new CustomerDashboard(1).setVisible(true);
                this.dispose(); // Close after update
            } else {
                JOptionPane.showMessageDialog(this, "Profile update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAccount() {
        try (Connection conn = DriverManager.getConnection(
                "protocol:subProtcol:URI",
                "Username", "Password"))
        {
            String sql = "DELETE FROM customers WHERE CustomerID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, customerId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Account deleted successfully!");
                dispose(); // Close window
                // You can return to login page if you want
            } else {
                JOptionPane.showMessageDialog(this, "Account deletion failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Rounded text field
    static class RoundedTextField extends JTextField {
        private static final Color BG = FIELD_BG;
        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // Rounded password field
    static class RoundedPasswordField extends JPasswordField {
        private static final Color BG = FIELD_BG;
        public RoundedPasswordField(int columns) {
            super(columns);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // Rounded button
    static class RoundedButton extends JButton {
        private Color bg;
        private static final Color FG = FG_WHITE;
        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.bg = bgColor;
            setForeground(FG);
            setFont(new Font("Arial", Font.PLAIN, 14));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

}
