import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AdminProfileManagement extends JFrame implements ActionListener{

    // Colors
    private static final Color BG_COLOR   = new Color(0x6C110F);
    private static final Color FIELD_BG   = new Color(0x79413F);
    private static final Color BUTTON_BG  = new Color(0x171D1B);
    private static final Color BUTTON_BL  = new Color(41, 41, 41);
    private user loggedInUser;

    private static final Color FG_WHITE   = Color.WHITE;

    // UI Components
    private PlaceholderTextField emailField;
    private RoundedPasswordField passwordField;

    public AdminProfileManagement(user user) {
        this.loggedInUser = user;

        setTitle("Admin Profile Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Main panel
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(createLabel("تقني", 30, Font.BOLD));
        header.add(Box.createRigidArea(new Dimension(0, 10)));
        header.add(createLabel("Admin Profile Management", 22, Font.BOLD));
        header.add(Box.createRigidArea(new Dimension(0, 30)));
        main.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Email field
        emailField = new PlaceholderTextField(25, "******@example.com");
        form.add(createFieldGroup("Email", emailField));
        form.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password field
        passwordField = new RoundedPasswordField(25);
        form.add(createFieldGroup("PASSWORD", passwordField));

        main.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setOpaque(false);
        RoundedButton loginBtn = new RoundedButton("Back", BUTTON_BL);
        loginBtn.setActionCommand("BACK");
        loginBtn.addActionListener(this);
        RoundedButton registerBtn = new RoundedButton("Update", BUTTON_BL);
        registerBtn.setActionCommand("UPDATE");
        registerBtn.addActionListener(this);
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        main.add(btnPanel, BorderLayout.SOUTH);

        add(main);
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setForeground(FG_WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));

        field.setMaximumSize(new Dimension(300, 30));

        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        group.add(label);
        group.add(Box.createRigidArea(new Dimension(0, 5)));
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
                AdminDashboard adminDashboard = new AdminDashboard(loggedInUser);
                adminDashboard.setVisible(true);
                this.dispose();
                break;
            case "UPDATE":
                updateProfile();
                break;
        }
    }
    private void updateProfile() {
        String emailInput = emailField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String passwordInput = new String(passwordChars);

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!emailInput.contains("@") || !emailInput.contains(".")) {
            JOptionPane.showMessageDialog(this, "Invalid email address.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "protocol:subProtcol:URI",
                "Username", "Password"))
        {
            // Update statement
            String sql = "UPDATE admins SET Email = ?, PasswordHash = ? WHERE AdminID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, emailInput);
            ps.setString(2, AuthService.md5(passwordInput));
            ps.setInt(3, loggedInUser.getId());
            // AdminID = 1 (assuming you are updating the main admin)

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                dispose(); // Close after update
                new AdminDashboard(loggedInUser).setVisible(true); // Return to dashboard (optional)
            } else {
                JOptionPane.showMessageDialog(this, "Profile update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Placeholder text field
    static class PlaceholderTextField extends JTextField {
        private final String placeholder;
        private static final Color BG = FIELD_BG;

        public PlaceholderTextField(int columns, String placeholder) {
            super(columns);
            this.placeholder = placeholder;
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
            if (getText().isEmpty()) {
                g2.setColor(Color.LIGHT_GRAY);
                Insets ins = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int x = ins.left;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(placeholder, x, y);
            }
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
        private final Color bg;
        private static final Color FG = FG_WHITE;

        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.bg = bgColor;
            setForeground(FG);
            setFont(new Font("Arial", Font.PLAIN, 14));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g2);
            g2.dispose();
        }
    }



}

