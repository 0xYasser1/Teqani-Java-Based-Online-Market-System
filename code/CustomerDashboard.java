import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CustomerDashboard extends JFrame implements ActionListener {

    private static final Color BG_COLOR  = new Color(0x6C110F);
    private static final Color BUTTON_BG = new Color(0x79413F);
    private static final Color FG_WHITE  = Color.WHITE;

    private int customerId;
    private Map<Product, Integer> cart;

    private RoundedButton browseButton;
    private RoundedButton cartButton;
    private RoundedButton checkoutButton;
    private RoundedButton historyButton;
    private RoundedButton profileButton;
    private RoundedButton reviewButton;
    private RoundedButton logoutButton;

    // Constructor with new cart
    public CustomerDashboard(int customerId) {
        this(customerId, new HashMap<>());
    }

    // Constructor with existing cart
    public CustomerDashboard(int customerId, Map<Product, Integer> cart) {
        this.customerId = customerId;
        this.cart = cart;

        setTitle("Customer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 550);
        setLocationRelativeTo(null);

        JPanel main = new JPanel();
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        main.add(createLabel("تقني", 30, Font.BOLD));
        main.add(Box.createRigidArea(new Dimension(0, 15)));
        main.add(createLabel("Customer Dashboard", 24, Font.BOLD));
        main.add(Box.createRigidArea(new Dimension(0, 10)));
        main.add(createLabel("Welcome to our store!", 16, Font.PLAIN));
        main.add(Box.createRigidArea(new Dimension(0, 30)));

        browseButton   = new RoundedButton("Browse our product", BUTTON_BG);
        cartButton     = new RoundedButton("your cart", BUTTON_BG);
        checkoutButton = new RoundedButton("checkout", BUTTON_BG);
        historyButton  = new RoundedButton("Order History", BUTTON_BG);
        profileButton  = new RoundedButton("profile Management", BUTTON_BG);
        reviewButton   = new RoundedButton("Review", BUTTON_BG);
        logoutButton   = new RoundedButton("Log out", BUTTON_BG);

        String[] commands = {"BROWSE", "CART", "CHECKOUT", "HISTORY", "PROFILE", "REVIEW", "LOGOUT"};
        RoundedButton[] buttons = {browseButton, cartButton, checkoutButton, historyButton, profileButton, reviewButton, logoutButton};

        for (int i = 0; i < buttons.length; i++) {
            RoundedButton btn = buttons[i];
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(300, 40));
            btn.setActionCommand(commands[i]);
            btn.addActionListener(this);
            main.add(btn);
            main.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        add(main);
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
            case "BROWSE":
                new BrowseProducts(customerId, cart).setVisible(true);
                this.dispose();
                break;
            case "CART":
                new ShoppingCart(customerId, cart).setVisible(true);
                this.dispose();
                break;
            case "CHECKOUT":
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to checkout?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Checkout checkout = new Checkout(customerId, cart);
                    if (checkout.placeOrder()) {
                        this.dispose();
                        new OrderHistory(customerId, cart).setVisible(true);
                    }
                }
                break;
            case "HISTORY":
                new OrderHistory(customerId, cart).setVisible(true);
                this.dispose();
                break;
            case "PROFILE":
                new CustomerProfileManagement(customerId).setVisible(true);
                this.dispose();
                break;
            case "REVIEW":
                new ReviewForm(customerId).setVisible(true);
                this.dispose();
                break;

            case "LOGOUT":
                dispose();
                new LoginPage().setVisible(true);
                break;
        }
    }

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
