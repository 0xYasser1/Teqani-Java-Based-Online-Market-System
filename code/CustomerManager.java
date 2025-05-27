import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class CustomerManager extends JFrame {

    private JTextField idField, usernameField, emailField, phoneField, addressField;
    private JButton findButton, updateButton, clearButton, backButton;
    private JComboBox<String> searchTypeBox;
    private Connection connection;
    private user loggedInUser;
    public CustomerManager(user user) {

        super("Admin - Customer Manager");
        this.loggedInUser = user;
        // Connect to database
        try {
            connection = DriverManager.getConnection(
                    "protocol:subProtcol:URI",
                "Username", "Password"
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database.");
            System.exit(1);
        }

        // Frame setup
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(102, 0, 0));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel title = new JLabel("Customer Manager");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Search By
        searchTypeBox = new JComboBox<>(new String[]{"Customer ID", "Username"});
        searchTypeBox.setBackground(new Color(102, 51, 51));
        searchTypeBox.setForeground(Color.WHITE);
        searchTypeBox.setFocusable(false);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Search By:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(searchTypeBox, gbc);

        // Search Value field
        addLabelAndField("Search Value:", idField = new JTextField(20), 2, gbc);
        addLabelAndField("Username:", usernameField = new JTextField(20), 3, gbc);
        addLabelAndField("Email:", emailField = new JTextField(20), 4, gbc);
        addLabelAndField("Phone:", phoneField = new JTextField(20), 5, gbc);
        addLabelAndField("Address:", addressField = new JTextField(20), 6, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(102, 0, 0));

        findButton = new JButton("Find");
        updateButton = new JButton("Update");
        clearButton = new JButton("Clear");
        backButton = new JButton("Back");

        styleButton(findButton);
        styleButton(updateButton);
        styleButton(clearButton);
        styleButton(backButton);

        buttonPanel.add(findButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Event listeners
        findButton.addActionListener(e -> findCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        clearButton.addActionListener(e -> clearFields());
        backButton.addActionListener(e -> {
            new AdminDashboard(loggedInUser).setVisible(true); // Replace with your actual admin dashboard class
            dispose();
        });


    }

    private void addLabelAndField(String labelText, JTextField field, int row, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        field.setBackground(new Color(102, 51, 51));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setPreferredSize(new Dimension(300, 30));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(field, gbc);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(34, 34, 34));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    private void findCustomer() {
        String value = idField.getText().trim();
        String searchBy = (String) searchTypeBox.getSelectedItem();

        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a search value.");
            return;
        }

        try {
            String sql = searchBy.equals("Customer ID")
                    ? "SELECT * FROM customers WHERE CustomerID = ?"
                    : "SELECT * FROM customers WHERE Username = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);

            if (searchBy.equals("Customer ID")) {
                stmt.setInt(1, Integer.parseInt(value));
            } else {
                stmt.setString(1, value);
            }

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idField.setText(rs.getString("CustomerID"));
                usernameField.setText(rs.getString("Username"));
                emailField.setText(rs.getString("Email"));
                phoneField.setText(rs.getString("Phone"));
                addressField.setText(rs.getString("Address"));
            } else {
                JOptionPane.showMessageDialog(this, "Customer not found.");
            }

            rs.close();
            stmt.close();
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving customer.");
        }
    }

    private void updateCustomer() {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE customers SET Username=?, Email=?, Phone=?, Address=? WHERE CustomerID=?"
            );
            stmt.setString(1, usernameField.getText().trim());
            stmt.setString(2, emailField.getText().trim());
            stmt.setString(3, phoneField.getText().trim());
            stmt.setString(4, addressField.getText().trim());
            stmt.setInt(5, Integer.parseInt(idField.getText().trim()));

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }

            stmt.close();
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating customer.");
        }
    }

    private void clearFields() {
        idField.setText("");
        usernameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
    }


}
