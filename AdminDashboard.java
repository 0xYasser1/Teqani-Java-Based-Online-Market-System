import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AdminDashboard extends JFrame implements ActionListener {
    private user loggedInUser;

    public AdminDashboard(user user) {
        this.loggedInUser = user;

        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(108, 17, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(createLabel("تقني", 30, Font.BOLD));
        header.add(Box.createRigidArea(new Dimension(0, 15)));
        header.add(createLabel("ADMIN DASHBOARD", 20, Font.BOLD));
        header.add(Box.createRigidArea(new Dimension(0, 10)));
        header.add(createLabel("Welcome, Admin ", 16, Font.PLAIN));
        mainPanel.add(header, BorderLayout.NORTH);

        // (buttonPanel and the rest remain the same)


        // Buttons panel (centered)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        String[] btnTexts = {
                "Product Management",
                "Customer Management",
                "Order Management",
                "Payment Management",
                "Profile Management",
                "Log out"
        };
        Dimension btnSize = new Dimension(300, 40); // fixed size matching design
        for (String text : btnTexts) {
            RoundedButton btn = new RoundedButton(text);
            btn.setPreferredSize(btnSize);
            btn.setMaximumSize(btnSize);
            btn.setMinimumSize(btnSize);
            btn.setActionCommand(text);
            btn.addActionListener(this);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(btn);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        // Wrap buttonPanel so it's vertically centered
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(buttonPanel); // GridBag centers by default

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        add(mainPanel);
    }

    /** Utility to create a centered, white JLabel with given size/style **/
    private JLabel createLabel(String text, int fontSize, int style) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", style, fontSize));
        lbl.setForeground(Color.WHITE);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }



    // Handle all button actions
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Product Management":    onProductManagement();    break;
            case "Customer Management":   onCustomerManagement();   break;
            case "Order Management":      onOrderManagement();      break;
            case "Payment Management":    onPaymentManagement();    break;
            case "Profile Management":    onProfileManagement();    break;
            case "Log out":               onLogout();               break;
        }
    }

    // Stub methods
    private void onProductManagement()  {
        ProductManager productManager =new ProductManager(loggedInUser);
        productManager.setVisible(true);
        this.dispose();
    }
    private void onCustomerManagement() {
        CustomerManager customerManager = new CustomerManager(loggedInUser);
        customerManager.setVisible(true);
        this.dispose();
    }
    private void onOrderManagement()    {
        OrderManagement orderManagement = new OrderManagement(loggedInUser);
        orderManagement.setVisible(true);
        this.dispose();
    }
    private void onPaymentManagement()  {
      PaymentRecordsForm paymentRecordsForm = new PaymentRecordsForm(loggedInUser);
      paymentRecordsForm.setVisible(true);
      this.dispose();
    }
    private void onProfileManagement()  {
        AdminProfileManagement profilePage = new AdminProfileManagement(loggedInUser);
        profilePage.setVisible(true);
        this.dispose();
    }
    private void onLogout() {
        if (JOptionPane.showConfirmDialog(this, "Log out?", "Confirm", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            dispose();
            new LoginPage().setVisible(true);
        }
    }

    // Rounded button class as before
    static class RoundedButton extends JButton {
        private static final Color BG = new Color(121, 65, 63);
        private static final Color FG = Color.WHITE;

        public RoundedButton(String text) {
            super(text);
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
            g2.setColor(BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
