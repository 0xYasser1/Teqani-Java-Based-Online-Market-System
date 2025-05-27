import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class OrderManagement extends JFrame {
    private user loggedInUser;

    private static final Color BG_COLOR   = new Color(0x6C110F);
    private static final Color CARD_BG    = new Color(0x79413F);
    private static final Color BUTTON_BG  = new Color(114, 40, 38);
    private static final Color FG_WHITE   = Color.WHITE;

    public OrderManagement(user user) {
        this.loggedInUser = user;
        setTitle("Order History - Admin View");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Header
        mainPanel.add(createCenteredLabel("تقني", 28, Font.BOLD));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(createCenteredLabel("All Customer Orders", 22, Font.BOLD));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Fetch order data
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("protocol:subProtcol:URI","Username", "Password");

            String sql = "SELECT * FROM orders ORDER BY OrderDate DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("OrderID");
                int customerId = rs.getInt("CustomerID");
                Timestamp orderDate = rs.getTimestamp("OrderDate");
                double totalAmount = rs.getDouble("TotalAmount");

                JPanel orderPanel = new JPanel(new GridLayout(0, 1));
                orderPanel.setBackground(CARD_BG);
                orderPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                orderPanel.add(createLabel("Order ID: " + orderId, 16));
                orderPanel.add(createLabel("Customer ID: " + customerId, 16));
                orderPanel.add(createLabel("Order Date: " + orderDate.toString(), 16));
                orderPanel.add(createLabel("Total Amount: " + totalAmount + " SAR", 16));

                mainPanel.add(orderPanel);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading order data.");
        }

        // Back button
        JButton backButton = new JButton("Back");
        styleButton(backButton);
        backButton.addActionListener(e -> {
            new AdminDashboard(loggedInUser).setVisible(true);
            this.dispose();
        });

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(backButton);

        // Scrollable view
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);
    }

    private JLabel createCenteredLabel(String text, int size, int style) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", style, size));
        lbl.setForeground(FG_WHITE);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JLabel createLabel(String text, int size) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, size));
        lbl.setForeground(FG_WHITE);
        return lbl;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(BUTTON_BG);
        btn.setForeground(FG_WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }


}
