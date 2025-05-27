import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class register extends JFrame implements ActionListener {
    // Colors
    private static final Color BG_COLOR   = new Color(108, 17, 15);
    private static final Color FIELD_BG   = new Color(121, 65, 63);
    private static final Color BUTTON_BG  = new Color(114, 40, 38);
    private static final Color BUTTON_BL  = new Color(41, 41, 41);
    private static final Color FG_WHITE   = Color.WHITE;

    private RoundedTextField fullNameField;
    private RoundedTextField emailField;
    private RoundedTextField phoneField;
    private RoundedPasswordField passwordField;
    private RoundedPasswordField confirmField;

    public register() {
        setTitle("Create New Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 650);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(createLabel("تقني", 30, Font.BOLD));
        header.add(Box.createRigidArea(new Dimension(0, 10)));
        header.add(createLabel("Create New Account", 22, Font.BOLD));
        header.add(Box.createRigidArea(new Dimension(0, 20)));
        main.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(createFieldGroup("Full Name:", fullNameField = new RoundedTextField(30)));
        form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(createFieldGroup("Email:", emailField = new RoundedTextField(30)));
        form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(createFieldGroup("Phone Number:", phoneField = new RoundedTextField(30)));
        form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(createFieldGroup("PASSWORD:", passwordField = new RoundedPasswordField(30)));
        form.add(Box.createRigidArea(new Dimension(0, 15)));
        form.add(createFieldGroup("Confirm PASSWORD:", confirmField = new RoundedPasswordField(30)));
        form.add(Box.createRigidArea(new Dimension(0, 25)));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);

        RoundedButton registerBtn = new RoundedButton("Register", BUTTON_BL);
        registerBtn.setActionCommand("REGISTER");
        registerBtn.addActionListener(this);

        RoundedButton backBtn = new RoundedButton("Back", BUTTON_BL);
        backBtn.setActionCommand("BACK");
        backBtn.addActionListener(this);

        btnPanel.add(registerBtn);
        btnPanel.add(backBtn);
        form.add(btnPanel);

        // Center form
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(form);
        main.add(center, BorderLayout.CENTER);

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
            case "REGISTER":
                String fullName = fullNameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmField.getPassword());

                if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!email.contains("@") || !email.contains(".")) {
                    JOptionPane.showMessageDialog(this, "Invalid email address.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Insert into DB
                try (Connection conn = DriverManager.getConnection(
                        "protocol:subProtcol:URI",
                "Username", "Password")) {
                    String sql = "INSERT INTO customers (Username, Email, Phone, Address, PasswordHash) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(sql);

                    ps.setString(1, fullName);
                    ps.setString(2, email);
                    ps.setString(3, phone);
                    ps.setString(4, ""); // Optional: Add address field if needed
                    ps.setString(5, AuthService.md5(password)); // Ensure AuthService has md5() method

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Registration successful!");
                        new LoginPage().setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "BACK":
                new LoginPage().setVisible(true);
                dispose();
                break;
        }
    }

    // Rounded text field with white font
    static class RoundedTextField extends JTextField {
        private static final Color BG = FIELD_BG;
        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
            setForeground(Color.WHITE); // ✅ white font
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

    // Rounded password field with white font
    static class RoundedPasswordField extends JPasswordField {
        private static final Color BG = FIELD_BG;
        public RoundedPasswordField(int columns) {
            super(columns);
            setOpaque(false);
            setForeground(Color.WHITE); // ✅ white font
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
        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.bg = bgColor;
            setForeground(FG_WHITE);
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
