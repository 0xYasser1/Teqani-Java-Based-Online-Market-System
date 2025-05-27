import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class ReviewForm extends JFrame implements ActionListener {

    private static final Color BG_COLOR   = new Color(0x6C110F);
    private static final Color FIELD_BG   = new Color(0x79413F);
    private static final Color BUTTON_BG  = new Color(0x171D1B);
    private static final Color FG_WHITE   = Color.WHITE;

    private int customerId;
    private JComboBox<String> productDropdown;
    private JTextField ratingField;
    private JTextArea reviewArea;
    private RoundedButton submitButton;
    private RoundedButton backButton;
    private Map<String, Integer> productMap = new HashMap<>();

    public ReviewForm(int customerId) {
        this.customerId = customerId;

        setTitle("Submit Review");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 550);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG_COLOR);
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(main);

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(createLabel("Submit Review", 24, Font.BOLD));
        header.add(Box.createRigidArea(new Dimension(0, 20)));
        main.add(header, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        productDropdown = new JComboBox<>();
        styleComboBox(productDropdown);
        loadProducts();

        ratingField = createTextField();
        reviewArea = createTextArea();

        formPanel.add(createFieldGroup("Select Product", productDropdown));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFieldGroup("Rating (1–5)", ratingField));
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createFieldGroup("Review Content", reviewArea));
        main.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);

        submitButton = new RoundedButton("Submit Review", BUTTON_BG);
        submitButton.addActionListener(this);

        backButton = new RoundedButton("Back", BUTTON_BG);
        backButton.addActionListener(e -> {
            new CustomerDashboard(customerId).setVisible(true);
            dispose();
        });

        btnPanel.add(submitButton);
        btnPanel.add(backButton);
        main.add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        
        try (Connection conn = DriverManager.getConnection(
                "protocol:subProtcol:URI",
                "Username", "Password")) {

            String sql = "SELECT productID, Name FROM products";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("productID");
                String name = rs.getString("Name");
                productMap.put(name, id);
                productDropdown.addItem(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load products.");
        }
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField(25);
        tf.setMaximumSize(new Dimension(300, 30));
        tf.setFont(new Font("Arial", Font.PLAIN, 14));
        tf.setBackground(FIELD_BG);
        tf.setForeground(FG_WHITE);
        tf.setCaretColor(FG_WHITE);
        tf.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return tf;
    }

    private JTextArea createTextArea() {
        JTextArea ta = new JTextArea(4, 25);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setFont(new Font("Arial", Font.PLAIN, 14));
        ta.setBackground(FIELD_BG);
        ta.setForeground(FG_WHITE);
        ta.setCaretColor(FG_WHITE);
        ta.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return ta;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setMaximumSize(new Dimension(300, 30));
        comboBox.setBackground(FIELD_BG);
        comboBox.setForeground(FG_WHITE);
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setForeground(FG_WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.add(label);
        group.add(Box.createRigidArea(new Dimension(0, 5)));
        group.add(field);
        return group;
    }

    private JLabel createLabel(String text, int size, int style) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", style, size));
        label.setForeground(FG_WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String productName = (String) productDropdown.getSelectedItem();
            int productId = productMap.getOrDefault(productName, -1);
            int rating = Integer.parseInt(ratingField.getText().trim());
            String reviewText = reviewArea.getText().trim();

            if (productId == -1) {
                JOptionPane.showMessageDialog(this, "Invalid product selected.");
                return;
            }

            if (rating < 1 || rating > 5) {
                JOptionPane.showMessageDialog(this, "Rating must be between 1 and 5.");
                return;
            }

            Connection conn = DriverManager.getConnection(
                    "protocol:subProtcol:URI",
                "Username", "Password"
            );

            String sql = "INSERT INTO reviews (CustomerID, ProductID, Rating, ReviewContent, ReviewDate) " +
                    "VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            stmt.setInt(2, productId);
            stmt.setInt(3, rating);
            stmt.setString(4, reviewText);
            stmt.executeUpdate();

            conn.close();
            JOptionPane.showMessageDialog(this, "Review submitted successfully!");
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid rating.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    static class RoundedButton extends JButton {
        private final Color bg;

        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.bg = bgColor;
            setForeground(FG_WHITE);
            setFont(new Font("Arial", Font.PLAIN, 14));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
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
