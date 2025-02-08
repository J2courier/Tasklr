package tasklr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.border.Border;

public class userLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public userLogin() {
        setTitle("Login");
        setSize(1200, 920);
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(1800, 1000));
        getContentPane().setBackground(new Color(0x1C2128));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        ImageIcon appIcon = new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png");
        setIconImage(appIcon.getImage());

        JPanel loginPanel = new JPanel(new GridBagLayout());
        Border loginBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        loginPanel.setBorder(loginBorder);
        loginPanel.setPreferredSize(new Dimension(700, 700));
        loginPanel.setBackground(new Color(0x292E34));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(50);
        usernameField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 1;
        loginPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(50);
        passwordField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 3;
        loginPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setFocusable(false);
        loginButton.setPreferredSize(new Dimension(0, 40));
        loginButton.setBackground(new Color(0x2E5AEA));    
        gbc.gridy = 4;
        loginPanel.add(loginButton, gbc);

        JButton signupButton = new JButton("Sign Up");
        signupButton.setFocusable(false);
        signupButton.setPreferredSize(new Dimension(0, 40));
        signupButton.setBackground(new Color(0x2E5AEA));    
        gbc.gridy = 5;
        loginPanel.add(signupButton, gbc);

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new login().setVisible(true);
                dispose();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String hashedPassword = hashPassword(password);

                if (validateUser(username, hashedPassword)) {
                    new Tasklr().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(userLogin.this, "Invalid username or password!");
                }
            }
        });

        GridBagConstraints frameGbc = new GridBagConstraints();
        frameGbc.gridx = 0;
        frameGbc.gridy = 0;
        frameGbc.anchor = GridBagConstraints.CENTER;
        add(loginPanel, frameGbc);
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

    private boolean validateUser(String username, String hashedPassword) {
        String url = "jdbc:mysql://localhost:3306/user_accounts";
        String user = "JFCompany";
        String pass = "";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String query = "SELECT * FROM fields WHERE userName = ? AND userPassword = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error validating user: " + ex.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new userLogin().setVisible(true);
        });
    }
}
