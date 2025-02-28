package tasklr.authentication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.border.Border;

import tasklr.main.Tasklr;

public class signup extends JFrame {
    private JTextField createUsernameField;
    private JTextField createPasswordField;
    private JPasswordField confirmPasswordField;

    public signup() {
        pack();
        setTitle("Sign up"); 
        setSize(1200, 920); 
        setLayout(new GridBagLayout()); 
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 1000));
        getContentPane().setBackground(new Color(0xf1f3f6)); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Set application icon
        ImageIcon appIcon = new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png");
        setIconImage(appIcon.getImage());

        // Create main signup panel
        JPanel signupPanel = new JPanel(new GridBagLayout());

        signupPanel.setPreferredSize(new Dimension(700, 700)); // Set panel size
        signupPanel.setBackground(new Color(0xf1f3f6)); // Set panel background color
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        JLabel createUsernameLabel = new JLabel("Create Username");
        createUsernameLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        signupPanel.add(createUsernameLabel, gbc);

        createUsernameField = new JTextField(50);
        createUsernameField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 1;
        signupPanel.add(createUsernameField, gbc);

        // Password field
        JLabel createPasswordLabel = new JLabel("Create Password");
        createPasswordLabel.setForeground(Color.BLACK);
        gbc.gridy = 2;
        signupPanel.add(createPasswordLabel, gbc);

        createPasswordField = new JTextField(50);
        createPasswordField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 3;
        signupPanel.add(createPasswordField, gbc);

        // Confirm password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setForeground(Color.BLACK);
        gbc.gridy = 4;
        signupPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(50);
        confirmPasswordField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 5;
        signupPanel.add(confirmPasswordField, gbc);

        // Signup button
        JButton signupButton = new JButton("Sign Up");
        signupButton.setFocusable(false);
        signupButton.setForeground(Color.WHITE);
        signupButton.setPreferredSize(new Dimension(0, 40));
        signupButton.setBackground(new Color(0x2E5AEA));    
        gbc.gridy = 6;
        signupPanel.add(signupButton, gbc);

        // Login label and button
        JLabel loginLabel = new JLabel("Already have an account?");
        loginLabel.setForeground(Color.BLACK);
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        signupPanel.add(loginLabel, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setFocusable(false);
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(0, 40));
        loginButton.setBackground(new Color(0x2E5AEA));
        gbc.gridx = 1;
        signupPanel.add(loginButton, gbc);

        // Signup button action listener
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = createUsernameField.getText();
                String password = createPasswordField.getText();
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (password.equals(confirmPassword)) { // Check if passwords match
                    String hashedPassword = hashPassword(password); // Hash the password
                    if (insertUser(username, hashedPassword)) { // Insert user into database
                        createUsernameField.setText("");
                        createPasswordField.setText("");
                        confirmPasswordField.setText("");
                        new Tasklr(username).setVisible(true); // Open main Tasklr application
                        dispose(); // Close signup window
                    }
                } else {
                    JOptionPane.showMessageDialog(signup.this, "Passwords do not match!");
                }
            }
        });

        // Login button action listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new login().setVisible(true); // Open login window
                dispose(); // Close signup window
            }
        });

        // Add signup panel to frame
        GridBagConstraints frameGbc = new GridBagConstraints();
        frameGbc.gridx = 0;
        frameGbc.gridy = 0;
        frameGbc.anchor = GridBagConstraints.CENTER;
        add(signupPanel, frameGbc);
    }

    // Method to hash password using SHA-256
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

    // Method to insert user into database
    private boolean insertUser(String username, String hashedPassword) {
        String url = "jdbc:mysql://localhost:3306/tasklrdb";
        String user = "JFCompany";
        String pass = "";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String query = "INSERT INTO users (username, userpassword) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registered Successfully!");
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inserting user: " + ex.getMessage());
            return false;
        }
    }
}
