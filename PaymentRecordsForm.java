import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PaymentRecordsForm extends JFrame {
    // Updated color scheme to match your app design
    private static final Color BG_COLOR     = new Color(108, 17, 15);   // deep red background
    private static final Color CARD_COLOR   = new Color(41, 41, 41);    // panel/card color
    private static final Color BUTTON_COLOR = new Color(41, 41, 41);    // button color
    private static final Color TEXT_COLOR   = Color.WHITE;
    private user loggedInUser;
    public PaymentRecordsForm(user user) {
        this.loggedInUser = user;
        setTitle("Payment Records");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Title
        JLabel title = new JLabel("💳 Payment Records", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Grid for payment cards
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        gridPanel.setBackground(BG_COLOR);
        gridPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("protocol:subProtcol:URI","Username", "Password");

            String sql = "SELECT * FROM payments ORDER BY PaymentDate DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("PaymentID");
                int orderId = rs.getInt("OrderID");
                String method = rs.getString("PaymentMethod");
                double amount = rs.getDouble("Amount");
                Timestamp date = rs.getTimestamp("PaymentDate");

                JPanel card = createPaymentCard(id, orderId, method, amount, date);
                gridPanel.add(card);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading payments.");
        }

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Back button
        JButton back = new JButton("← Back");
        back.setFont(new Font("SansSerif", Font.BOLD, 16));
        back.setBackground(BUTTON_COLOR);
        back.setForeground(TEXT_COLOR);
        back.setFocusPainted(false);
        back.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> {
            new AdminDashboard(loggedInUser).setVisible(true);
            this.dispose();
        }
        );

        JPanel footer = new JPanel();
        footer.setBackground(BG_COLOR);
        footer.add(back);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createPaymentCard(int id, int orderId, String method, double amount, Timestamp date) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BG_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel header = new JLabel("Payment #" + id);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setForeground(TEXT_COLOR);
        card.add(header);

        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(createLabel("Order ID: " + orderId));
        card.add(createLabel("Method: " + method));
        card.add(createLabel("Amount: " + amount + " SAR"));
        card.add(createLabel("Date: " + date.toString()));

        return card;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl.setForeground(TEXT_COLOR);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

}
