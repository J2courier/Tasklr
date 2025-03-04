package tasklr.authentication;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import tasklr.main.Tasklr;

import tasklr.authentication.UserSession;

public class login extends JFrame {
    private JPanel centerContainer; 

    public login() {
        centerContainer = new JPanel(new BorderLayout()); 
        centerContainer.setBackground(new Color(0xf1f3f6)); 
        pack();
        setTitle("Login");
        setSize(700, 920);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 1000));
        getContentPane().setBackground(new Color(0xf1f3f6));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        ImageIcon appIcon = new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png");
        setIconImage(appIcon.getImage());

        LoginPanel loginPanel = new LoginPanel(); 

        // Adding action listener to login button
        loginPanel.addLoginListener(e -> {
            String username = loginPanel.getUsername();
            String password = loginPanel.getPassword();
            String hashedPassword = hashPassword(password);

            int userId = validateUser(username, hashedPassword);
            if (userId != -1) { 
                String sessionToken = generateSessionToken();
                UserSession.createSession(userId, username, sessionToken);
                new Tasklr(username).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        });

        loginPanel.addSignupListener(e -> {
            new signup().setVisible(true);
            dispose();
        });

        GridBagConstraints frameGbc = new GridBagConstraints();
        frameGbc.gridx = 0;
        frameGbc.gridy = 0;
        frameGbc.anchor = GridBagConstraints.CENTER;
        add(loginPanel.getLoginPanel(), frameGbc);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Updated to return userId instead of boolean
    private int validateUser(String username, String hashedPassword) {
        String url = "jdbc:mysql://localhost:3306/tasklrdb";
        String user = "JFCompany";
        String pass = "";
    
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String query = "SELECT id, username FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("id"); // Get the user ID
                        return userId; 
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error validating user: " + ex.getMessage());
        }
        return -1; // Return -1 if user is not valid
    }
    

    private String generateSessionToken() {
        return java.util.UUID.randomUUID().toString(); 
    }
}