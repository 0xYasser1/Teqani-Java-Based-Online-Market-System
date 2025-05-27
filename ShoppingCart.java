import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class ShoppingCart extends JFrame {

    private static final Color BG_COLOR = new Color(0x6C110F);
    private static final Color FG_WHITE = Color.WHITE;
    private static final Color ITEM_BG = new Color(0x79413F);

    private Map<Product, Integer> cart;
    private int customerId;

    private JPanel itemsPanel;
    private JLabel totalLabel;

    public ShoppingCart(int customerId, Map<Product, Integer> cart) {
        this.customerId = customerId;
        this.cart = cart;

        setTitle("Shopping Cart");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Your Cart");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(FG_WHITE);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(header, BorderLayout.NORTH);

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(BG_COLOR);

        totalLabel = new JLabel();
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(FG_WHITE);

        loadCartItems();

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        updateTotal();
        bottomPanel.add(totalLabel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton checkoutButton = new JButton("Checkout");
        JButton backButton = new JButton("Back");
        styleBottomButton(checkoutButton);
        styleBottomButton(backButton);

        // ✅ Updated Checkout logic to use the Checkout class
        checkoutButton.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty.");
                return;
            }

            Checkout checkout = new Checkout(customerId, cart);
            if (checkout.placeOrder()) {
                dispose();
                new OrderHistory(customerId, cart).setVisible(true);
            }
        });

        backButton.addActionListener(e -> {
            new CustomerDashboard(customerId, cart).setVisible(true);
            dispose();
        });

        buttonPanel.add(checkoutButton);
        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void loadCartItems() {
        itemsPanel.removeAll();

        if (cart.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your cart is empty.");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            emptyLabel.setForeground(FG_WHITE);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            itemsPanel.add(emptyLabel);
        } else {
            for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();

                JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                productPanel.setBackground(ITEM_BG);
                productPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                JLabel nameLabel = new JLabel(product.getName() + " - " + product.getPrice() + " SAR");
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                nameLabel.setForeground(FG_WHITE);

                JButton addBtn = new JButton("+");
                JButton removeBtn = new JButton("-");
                JLabel qtyLabel = new JLabel(String.valueOf(quantity));
                qtyLabel.setFont(new Font("Arial", Font.BOLD, 16));
                qtyLabel.setForeground(FG_WHITE);

                styleQtyButton(addBtn);
                styleQtyButton(removeBtn);

                addBtn.addActionListener(e -> {
                    cart.put(product, quantity + 1);
                    loadCartItems();
                });

                removeBtn.addActionListener(e -> {
                    if (quantity > 1) {
                        cart.put(product, quantity - 1);
                    } else {
                        cart.remove(product);
                    }
                    loadCartItems();
                });

                productPanel.add(nameLabel);
                productPanel.add(Box.createHorizontalStrut(20));
                productPanel.add(removeBtn);
                productPanel.add(qtyLabel);
                productPanel.add(addBtn);

                itemsPanel.add(productPanel);
                itemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        updateTotal();
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private void updateTotal() {
        totalLabel.setText("Total: " + calculateTotal() + " SAR");
    }

    private float calculateTotal() {
        float total = 0;
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    private void styleQtyButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(BG_COLOR);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(45, 30));
        btn.setFocusPainted(false);
    }

    private void styleBottomButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(BG_COLOR);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 40));
    }
}
