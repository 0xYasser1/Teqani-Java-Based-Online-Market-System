import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class ProductManager extends JFrame {

    private JTextField idField, productIdDisplayField, nameField, priceField, stockField, categoryField;
    private JButton findButton, addButton, updateButton, deleteButton, clearButton, backButton;
    private JComboBox<String> searchTypeBox;
    private Connection connection;
    private user loggedInUser;

    public ProductManager(user user) {
        super("Admin - Product Manager");
        this.loggedInUser = user;

        try {
            connection = DriverManager.getConnection("protocol:subProtcol:URI","Username", "Password");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database.");
            System.exit(1);
        }

        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(102, 0, 0));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel title = new JLabel("Product Manager");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Search Type
        searchTypeBox = new JComboBox<>(new String[]{"Product ID", "Name"});
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

        // Input Fields
        addLabelAndField("Search Value:", idField = new JTextField(20), 2, gbc);
        addLabelAndField("Product ID:", productIdDisplayField = new JTextField(20), 3, gbc);
        productIdDisplayField.setEditable(false);
        productIdDisplayField.setBackground(new Color(80, 80, 80));

        addLabelAndField("Product Name:", nameField = new JTextField(20), 4, gbc);
        addLabelAndField("Price:", priceField = new JTextField(20), 5, gbc);
        addLabelAndField("Stock:", stockField = new JTextField(20), 6, gbc);
        addLabelAndField("Category ID:", categoryField = new JTextField(20), 7, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(102, 0, 0));

        findButton = new JButton("Find");
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        backButton = new JButton("Back");

        styleButton(findButton);
        styleButton(addButton);
        styleButton(updateButton);
        styleButton(deleteButton);
        styleButton(clearButton);
        styleButton(backButton);

        buttonPanel.add(findButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Event Handlers
        findButton.addActionListener(e -> findProduct());
        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearFields());
        backButton.addActionListener(e -> {
            new AdminDashboard(loggedInUser).setVisible(true);
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
        button.setBackground(new Color(41, 41, 41));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    private void findProduct() {
        String value = idField.getText().trim();
        String searchBy = (String) searchTypeBox.getSelectedItem();

        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a search value.");
            return;
        }

        try {
            String sql = searchBy.equals("Product ID")
                    ? "SELECT * FROM products WHERE ProductID = ?"
                    : "SELECT * FROM products WHERE Name = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);

            if (searchBy.equals("Product ID")) {
                stmt.setInt(1, Integer.parseInt(value));
            } else {
                stmt.setString(1, value);
            }

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                productIdDisplayField.setText(rs.getString("ProductID")); // show actual ID here
                nameField.setText(rs.getString("Name"));
                priceField.setText(rs.getString("Price"));
                stockField.setText(rs.getString("Stock"));
                categoryField.setText(rs.getString("CategoryID"));
            } else {
                JOptionPane.showMessageDialog(this, "Product not found.");
            }

            rs.close();
            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error finding product.");
        }
    }

    private void addProduct() {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO products (ProductID, Name, Price, Stock, CategoryID) VALUES (?, ?, ?, ?, ?)"
            );
            stmt.setInt(1, Integer.parseInt(idField.getText().trim()));
            stmt.setString(2, nameField.getText().trim());
            stmt.setDouble(3, Double.parseDouble(priceField.getText().trim()));
            stmt.setInt(4, Integer.parseInt(stockField.getText().trim()));
            stmt.setInt(5, Integer.parseInt(categoryField.getText().trim()));

            int inserted = stmt.executeUpdate();
            if (inserted > 0) {
                JOptionPane.showMessageDialog(this, "Product added successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Add failed.");
            }
            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding product.");
        }
    }

    private void updateProduct() {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE products SET Name=?, Price=?, Stock=?, CategoryID=? WHERE ProductID=?"
            );
            stmt.setString(1, nameField.getText().trim());
            stmt.setDouble(2, Double.parseDouble(priceField.getText().trim()));
            stmt.setInt(3, Integer.parseInt(stockField.getText().trim()));
            stmt.setInt(4, Integer.parseInt(categoryField.getText().trim()));
            stmt.setInt(5, Integer.parseInt(productIdDisplayField.getText().trim())); // use displayed ID

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Product updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }

            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating product.");
        }
    }

    private void deleteProduct() {
        try {
            int productId = Integer.parseInt(productIdDisplayField.getText().trim());
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM products WHERE ProductID = ?");
            stmt.setInt(1, productId);

            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                JOptionPane.showMessageDialog(this, "Product deleted.");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }

            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting product.");
        }
    }

    private void clearFields() {
        idField.setText("");
        productIdDisplayField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        categoryField.setText("");
    }
}
