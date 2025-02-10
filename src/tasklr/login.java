package tasklr;

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

public class login extends JFrame {
    private JTextField createUsernameField;
    private JTextField createPasswordField;
    private JPasswordField confirmPasswordField;

    public login() {
        setTitle("Login and Sign up");
        setSize(1200, 920);
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(1800, 1000));
        getContentPane().setBackground(new Color(0x1C2128));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        ImageIcon appIcon = new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png");
        setIconImage(appIcon.getImage());

        JPanel signupPanel = new JPanel(new GridBagLayout());
        Border signupBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        signupPanel.setBorder(signupBorder);
        signupPanel.setPreferredSize(new Dimension(700, 700));
        signupPanel.setBackground(new Color(0x292E34));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel createUsernameLabel = new JLabel("Create Username");
        createUsernameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        signupPanel.add(createUsernameLabel, gbc);

        createUsernameField = new JTextField(50);
        createUsernameField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 1;
        signupPanel.add(createUsernameField, gbc);

        JLabel createPasswordLabel = new JLabel("Create Password");
        createPasswordLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        signupPanel.add(createPasswordLabel, gbc);

        createPasswordField = new JTextField(50);
        createPasswordField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 3;
        signupPanel.add(createPasswordField, gbc);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setForeground(Color.WHITE);
        gbc.gridy = 4;
        signupPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(50);
        confirmPasswordField.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 5;
        signupPanel.add(confirmPasswordField, gbc);

        JButton signupButton = new JButton("Sign Up");
        signupButton.setFocusable(false);
        signupButton.setPreferredSize(new Dimension(0, 40));
        signupButton.setBackground(new Color(0x2E5AEA));    
        gbc.gridy = 6;
        signupPanel.add(signupButton, gbc);

        JLabel loginLabel = new JLabel("Already have an account?");
        loginLabel.setForeground(Color.WHITE);
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        signupPanel.add(loginLabel, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setFocusable(false);
        loginButton.setPreferredSize(new Dimension(0, 40));
        loginButton.setBackground(new Color(0xFFFFFF));
        gbc.gridx = 1;
        signupPanel.add(loginButton, gbc);

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = createUsernameField.getText();
                String password = createPasswordField.getText();
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (password.equals(confirmPassword)) {
                    String hashedPassword = hashPassword(password);
                    if (insertUser(username, hashedPassword)) {
                        createUsernameField.setText("");
                        createPasswordField.setText("");
                        confirmPasswordField.setText("");

                        usernameDisplay user = new usernameDisplay();
                        user.DisplayUsername(username);
                        
                        new Tasklr().setVisible(true);
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(login.this, "Passwords do not match!");
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new signup().setVisible(true);
                dispose();
            }
        });

        GridBagConstraints frameGbc = new GridBagConstraints();
        frameGbc.gridx = 0;
        frameGbc.gridy = 0;
        frameGbc.anchor = GridBagConstraints.CENTER;
        add(signupPanel, frameGbc);
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

    private boolean insertUser(String username, String hashedPassword) {
        String url = "jdbc:mysql://localhost:3306/user_accounts";
        String user = "JFCompany";
        String pass = "";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String query = "INSERT INTO fields (userName, userPassword) VALUES (?, ?)";
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true);
        });
    }
}
