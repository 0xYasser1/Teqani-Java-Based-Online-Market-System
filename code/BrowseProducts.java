import java.awt.*;
import java.sql.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class BrowseProducts extends JFrame {

    private static final Color BG_COLOR  = new Color(0x6C110F);
    private static final Color BUTTON_BG = new Color(0x79413F);
    private static final Color FG_WHITE  = Color.WHITE;

    private Map<Product, Integer> cart;
    private int customerId;

    public BrowseProducts(int customerId, Map<Product, Integer> cart) {
        this.cart = cart;
        this.customerId = customerId;

        setTitle("Browse Products");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(createCenteredLabel("تقني", 30, Font.BOLD));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(createCenteredLabel("Browse Our Products", 24, Font.BOLD));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "protocol:subProtcol:URI",
                "Username", "Password" // your password
            );

            String sql = "SELECT * FROM products";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int productID = rs.getInt("productID");
                String name = rs.getString("Name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("Stock");
                Product product = new Product(productID, name, (float) price, stock);

                JPanel productPanel = new JPanel(new BorderLayout());
                productPanel.setBackground(BUTTON_BG);
                productPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                JPanel textPanel = new JPanel();
                textPanel.setOpaque(false);
                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.add(createLeftLabel("Product: " + name, 18, Font.BOLD));
                textPanel.add(createLeftLabel("Price: " + price + " SAR", 16, Font.PLAIN));
                textPanel.add(createLeftLabel("Stock: " + stock + " units", 14, Font.PLAIN));
                textPanel.add(createLeftLabel("ID: " + productID, 14, Font.PLAIN));

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setOpaque(false);
                JButton addButton = new JButton("+ Add to Cart");
                styleButton(addButton);
                addButton.addActionListener(e -> addToCart(product));
                buttonPanel.add(addButton);

                productPanel.add(textPanel, BorderLayout.WEST);
                productPanel.add(buttonPanel, BorderLayout.EAST);
                mainPanel.add(productPanel);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products from database.");
        }

        JButton viewCartButton = new JButton("View Cart");
        styleViewCartButton(viewCartButton);
        viewCartButton.addActionListener(e -> {
            new ShoppingCart(customerId, cart).setVisible(true);
            this.dispose();
        });
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(viewCartButton);


        JButton backButton = new JButton("Back");
        styleBackButton(backButton);
        backButton.addActionListener(e -> {
            new CustomerDashboard(customerId, cart).setVisible(true);
            this.dispose();
        });
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(backButton);

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

    private JLabel createLeftLabel(String text, int size, int style) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", style, size));
        lbl.setForeground(FG_WHITE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(BG_COLOR);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(150, 45));
    }

    private void styleBackButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(BG_COLOR);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(150, 45));
    }

    private void styleViewCartButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(BG_COLOR);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(150, 45));
    }

    private void addToCart(Product product) {
        cart.put(product, cart.getOrDefault(product, 0) + 1);
        JOptionPane.showMessageDialog(this, "Added " + product.getName() + " to cart! Quantity: " + cart.get(product));
    }
}
