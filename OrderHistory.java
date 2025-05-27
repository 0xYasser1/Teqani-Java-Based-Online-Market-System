import java.awt.*;
import java.sql.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class OrderHistory extends JFrame {

    private static final Color BG_COLOR = new Color(0x6C110F);
    private static final Color FG_WHITE = Color.WHITE;
    private static final Color ITEM_BG = new Color(0x79413F);

    private int customerId;
    private Map<Product, Integer> cart;

    public OrderHistory(int customerId, Map<Product, Integer> cart) {
        this.customerId = customerId;
        this.cart = cart;

        setTitle("Order History");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Your Order History");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(FG_WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        try {
            Connection conn = DriverManager.getConnection(
                    "protocol:subProtcol:URI",
                "Username", "Password" // your DB password
            );

            String orderSQL = "SELECT OrderID, OrderDate, TotalAmount FROM Orders WHERE CustomerID = ?";
            PreparedStatement orderStmt = conn.prepareStatement(orderSQL);
            orderStmt.setInt(1, customerId);
            ResultSet orderRs = orderStmt.executeQuery();

            boolean hasOrders = false;

            while (orderRs.next()) {
                hasOrders = true;
                int orderId = orderRs.getInt("OrderID");
                Timestamp orderDate = orderRs.getTimestamp("OrderDate");
                float total = orderRs.getFloat("TotalAmount");

                JPanel orderPanel = new JPanel();
                orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
                orderPanel.setBackground(ITEM_BG);
                orderPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                orderPanel.add(createLabel("Order ID: " + orderId));
                orderPanel.add(createLabel("Date: " + orderDate.toString()));
                orderPanel.add(createLabel("Total: " + total + " SAR"));
                orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));

                // Get products from OrderDetail
                String detailSQL = "SELECT od.ProductID, od.Quantity, od.Subtotal, p.Name " +
                        "FROM orderdetails od JOIN products p ON od.ProductID = p.productID " +
                        "WHERE od.OrderID = ?";

                PreparedStatement detailStmt = conn.prepareStatement(detailSQL);
                detailStmt.setInt(1, orderId);
                ResultSet detailRs = detailStmt.executeQuery();

                while (detailRs.next()) {
                    String productName = detailRs.getString("Name");
                    int quantity = detailRs.getInt("Quantity");
                    float subtotal = detailRs.getFloat("Subtotal");

                    orderPanel.add(createLabel(" - " + productName + " x" + quantity + " = " + subtotal + " SAR"));
                }

                mainPanel.add(orderPanel);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            if (!hasOrders) {
                JLabel noOrders = new JLabel("You have no previous orders.");
                noOrders.setFont(new Font("Arial", Font.PLAIN, 18));
                noOrders.setForeground(FG_WHITE);
                noOrders.setAlignmentX(Component.CENTER_ALIGNMENT);
                mainPanel.add(noOrders);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load order history.");
        }

        // Back button to return to dashboard
        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(BG_COLOR);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setPreferredSize(new Dimension(100, 40));
        backButton.addActionListener(e -> {
            new CustomerDashboard(customerId, cart).setVisible(true);
            dispose();
        });

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(backButton);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 16));
        lbl.setForeground(FG_WHITE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}
