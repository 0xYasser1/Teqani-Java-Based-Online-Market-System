import java.awt.*;
import java.sql.*;
import java.util.Map;
import javax.swing.*;

public class Checkout {

    private final int customerId;
    private final Map<Product, Integer> cart;

    public Checkout(int customerId, Map<Product, Integer> cart) {
        this.customerId = customerId;
        this.cart = cart;
    }

    public boolean placeOrder() {
        float total = calculateTotal();

        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Your cart is empty.");
            return false;
        }

        String selectedMethod = showPaymentMethodDialog();
        if (selectedMethod == null) {
            JOptionPane.showMessageDialog(null, "Checkout canceled or no payment method selected.");
            return false;
        }

        try {
            Connection conn = DriverManager.getConnection(
                    "protocol:subProtcol:URI",
                "Username", "Password" // your actual DB password
            );
            conn.setAutoCommit(true);

            // Insert into Orders
            String orderSQL = "INSERT INTO Orders (CustomerID, OrderDate, TotalAmount) VALUES (?, NOW(), ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSQL, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, customerId);
            orderStmt.setFloat(2, total);
            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            int orderId = -1;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            if (orderId == -1) throw new SQLException("Failed to retrieve OrderID.");

            // Insert order details
            String detailSQL = "INSERT INTO orderdetails (OrderID, ProductID, Quantity, Subtotal) VALUES (?, ?, ?, ?)";
            PreparedStatement detailStmt = conn.prepareStatement(detailSQL);
            for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                float subtotal = (float) product.getPrice() * quantity;

                detailStmt.setInt(1, orderId);
                detailStmt.setInt(2, product.getProductId());
                detailStmt.setInt(3, quantity);
                detailStmt.setFloat(4, subtotal);
                detailStmt.addBatch();
            }
            detailStmt.executeBatch();

            // Insert payment
            String paySQL = "INSERT INTO payments (OrderID, PaymentMethod, Amount, PaymentDate) VALUES (?, ?, ?, NOW())";
            PreparedStatement payStmt = conn.prepareStatement(paySQL);
            payStmt.setInt(1, orderId);
            payStmt.setString(2, selectedMethod);
            payStmt.setFloat(3, total);
            payStmt.executeUpdate();

            conn.close();

            JOptionPane.showMessageDialog(null,
                    "Order placed successfully!\nPayment Method: " + selectedMethod + "\nTotal: " + total + " SAR");
            cart.clear();
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error during checkout: " + ex.getMessage());
            return false;
        }
    }

    private float calculateTotal() {
        float total = 0;
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            total += (float) entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    // Show custom payment selection dialog
    private String showPaymentMethodDialog() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel("Select your payment method:");
        String[] methods = {"PayPal", "Cash", "Bank Transfer"};
        JComboBox<String> comboBox = new JComboBox<>(methods);
        comboBox.setSelectedIndex(-1);
        comboBox.setPreferredSize(new Dimension(200, 25));

        panel.add(label, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Payment Method",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION && comboBox.getSelectedItem() != null) {
            return comboBox.getSelectedItem().toString();
        }
        return null;
    }
}
