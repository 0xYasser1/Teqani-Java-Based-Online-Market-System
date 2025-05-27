import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;

public class user {
    private int id;
    private String email;
    private String role; // "Admin" or "Customer"
    private String Username;
    private LocalDateTime createdAt;

    private AuthService auth = new AuthService();

    public user() {
        // default constructor
    }

   
    public boolean login(String emailInput, String passwordInput) {
        AuthService.LoginResult result = auth.authenticate(emailInput, passwordInput);
        if (result.success) {
            this.id = result.id;
            this.email = emailInput;
            this.role = result.role;
            this.Username = result.username;
            this.createdAt = LocalDateTime.now(); // Optional for session timestamp
            return true;
        }
        return false;
    }

    public void logout() {
        this.id = 0;
        this.email = null;
        this.role = null;
        this.Username = null;
        this.createdAt = null;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return Username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

class AuthService {
    // Update these constants for your environment
    private static final String JDBC_URL      = "protocol:subProtcol:URI";
    private static final String JDBC_USER     = "Username";
    private static final String JDBC_PASSWORD = "Password";

   
    public LoginResult authenticate(String email, String password) {
        // Try Admins table first
        String adminSql = "SELECT AdminID, PasswordHash FROM admins WHERE Email = ?";
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(adminSql)
        ) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("PasswordHash");
                    int adminId = rs.getInt("AdminID");
                    String candidateHash = md5(password);
                    if (storedHash.equalsIgnoreCase(candidateHash)) {
                        return new LoginResult(true, adminId, "Admin", null);
                    } else {
                        return new LoginResult(false, -1, null, null);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Try Customers table if not found in Admins
        String customerSql = "SELECT CustomerID, Username, PasswordHash FROM customers WHERE Email = ?";
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                PreparedStatement ps = conn.prepareStatement(customerSql)
        ) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("PasswordHash");
                    int customerId = rs.getInt("CustomerID");
                    String username = rs.getString("Username");
                    String candidateHash = md5(password);
                    if (storedHash.equalsIgnoreCase(candidateHash)) {
                        return new LoginResult(true, customerId, "Customer", username);
                    } else {
                        return new LoginResult(false, -1, null, null);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Not found anywhere
        return new LoginResult(false, -1, null, null);
    }

    /**
     * Utility: MD5 hash of a UTF-8 string, returned as hex.
     */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    /**
     * Inner class to hold authentication results.
     */
    public static class LoginResult {
        public final boolean success;
        public final int id;
        public final String role;
        public final String username;

        public LoginResult(boolean success, int id, String role, String username) {
            this.success = success;
            this.id = id;
            this.role = role;
            this.username = username;
        }
    }
}
