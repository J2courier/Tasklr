package tasklr.authentication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.Box;
import java.awt.Font;
import tasklr.utilities.DatabaseManager;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginPanel {
    private static final int FIELD_WIDTH = 500;
    private static final int FIELD_HEIGHT = 40;
    private static final int BUTTON_HEIGHT = 40;
    private static final Color BUTTON_BASE_COLOR = new Color(0x3B82F6);    // New base color
    private static final Color BUTTON_HOVER_COLOR = new Color(0x60A5FA);   // Hover color
    private static final Color BUTTON_PRESSED_COLOR = new Color(0x2563EB); // Pressed color
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final String LOGO_PATH = "C://Users//ADMIN//Desktop//Tasklr//resource//icons//logo1.png";
    private static final Dimension PANEL_SIZE = new Dimension(700, 700);
    private static final Color LINK_COLOR = new Color(0x275CE2); // Add this constant for consistency
    
    private final JPanel loginPanel;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JCheckBox showPasswordCheckBox; // Add this field
    private final JLabel forgotPasswordLabel; // Add this field
    
    public LoginPanel() {
        loginPanel = createMainPanel();
        usernameField = createTextField();
        passwordField = createPasswordField();
        loginButton = createButton("Login");
        showPasswordCheckBox = createShowPasswordCheckBox();
        forgotPasswordLabel = createForgotPasswordLabel(); // Initialize the label
        
        setupLayout();
        setupKeyListeners();
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(PANEL_SIZE);
        panel.setBackground(null);
        return panel;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(50);
        field.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(50);
        field.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        return field;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(TEXT_COLOR);
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(0, BUTTON_HEIGHT));
        button.setBackground(BUTTON_BASE_COLOR);
        
        // Add hover and pressed effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BASE_COLOR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(BUTTON_PRESSED_COLOR);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.contains(e.getPoint())) {
                    button.setBackground(BUTTON_HOVER_COLOR);
                } else {
                    button.setBackground(BUTTON_BASE_COLOR);
                }
            }
        });

        // Remove button borders and focus painting
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        return button;
    }

    private void setupLayout() {
        GridBagConstraints gbc = createGridBagConstraints();

        // Add logo
        addLogo(gbc);

        // Add username components
        addUsernameComponents(gbc);

        // Add password components
        addPasswordComponents(gbc);

        // Add buttons
        addButtons(gbc);
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addLogo(GridBagConstraints gbc) {
        ImageIcon logo = new ImageIcon(LOGO_PATH);
        JLabel logoLabel = new JLabel(logo);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 40, 10); // Add padding around the logo
        loginPanel.add(logoLabel, gbc);
        gbc.insets = new Insets(5, 10, 10, 10); // Reset insets for other components
    }

    private void addUsernameComponents(GridBagConstraints gbc) {
        JLabel usernameLabel = createLabel("Username");
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        loginPanel.add(usernameLabel, gbc);

        gbc.gridy = 2;
        loginPanel.add(usernameField, gbc);
    }

    private void addPasswordComponents(GridBagConstraints gbc) {
        JLabel passwordLabel = createLabel("Password");
        gbc.gridy = 3;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridy = 4;
        loginPanel.add(passwordField, gbc);

        // Create a panel for checkbox and forgot password
        JPanel passwordOptionsPanel = new JPanel(new GridBagLayout());
        passwordOptionsPanel.setBackground(null);
        
        GridBagConstraints optionsGbc = new GridBagConstraints();
        optionsGbc.gridx = 0;
        optionsGbc.gridy = 0;
        optionsGbc.anchor = GridBagConstraints.WEST;
        optionsGbc.weightx = 1.0; // Give weight to create space between components
        passwordOptionsPanel.add(showPasswordCheckBox, optionsGbc);
        
        optionsGbc.gridx = 1;
        optionsGbc.anchor = GridBagConstraints.EAST;
        optionsGbc.weightx = 0.0; // Reset weight for the forgot password label
        passwordOptionsPanel.add(forgotPasswordLabel, optionsGbc);

        // Add the options panel
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(passwordOptionsPanel, gbc);
        
        // Reset constraints for subsequent components
        gbc.anchor = GridBagConstraints.CENTER;
    }

    private void addButtons(GridBagConstraints gbc) {
        gbc.gridy = 6; // Increment gridy by 1 for login button
        loginPanel.add(loginButton, gbc);

        // Add signup label
        JLabel signupLabel = createSignupLabel();
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE; // Don't stretch the label
        gbc.anchor = GridBagConstraints.CENTER; // Center the label
        loginPanel.add(signupLabel, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JLabel createSignupLabel() {
        JLabel signupLabel = new JLabel("New user sign up here!");
        signupLabel.setForeground(new Color(0x275CE2)); // Blue color
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        signupLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Window window = SwingUtilities.getWindowAncestor(loginPanel);
                if (window != null) {
                    window.dispose();
                    new Signup().setVisible(true);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                signupLabel.setText("<html><u>New user sign up here!</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                signupLabel.setText("New user sign up here!");
            }
        });
        
        return signupLabel;
    }

    private JLabel createForgotPasswordLabel() {
        JLabel label = new JLabel("Forgot password?");
        label.setForeground(LINK_COLOR);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setText("<html><u>Forgot password?</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                label.setText("Forgot password?");
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                showForgotPasswordDialog();
            }
        });
        
        return label;
    }

    private void showForgotPasswordDialog() {
        // Create the main panel with GridBagLayout for better control
        JPanel recoveryPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Title and instructions
        JLabel titleLabel = new JLabel("Account Recovery");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        recoveryPanel.add(titleLabel, gbc);
        
        JLabel instructionsLabel = new JLabel("<html>Please provide your account's backup information to verify your identity.</html>");
        recoveryPanel.add(instructionsLabel, gbc);
        
        // Add some space
        recoveryPanel.add(Box.createVerticalStrut(10), gbc);
        
        // Username field
        recoveryPanel.add(new JLabel("Username:"), gbc);
        JTextField usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(300, 40));
        recoveryPanel.add(usernameField, gbc);
        
        // Email address field
        recoveryPanel.add(new JLabel("Email Address:"), gbc);
        JTextField addressField = new JTextField(20);
        addressField.setPreferredSize(new Dimension(300, 40));
        recoveryPanel.add(addressField, gbc);
        
        // Contact number field
        recoveryPanel.add(new JLabel("Contact Number:"), gbc);
        JTextField contactField = new JTextField(20);
        contactField.setPreferredSize(new Dimension(300, 40));
        recoveryPanel.add(contactField, gbc);
        
        // Birthday field with JDateChooser
        recoveryPanel.add(new JLabel("Birthday:"), gbc);
        JDateChooser birthdayChooser = new JDateChooser();
        birthdayChooser.setPreferredSize(new Dimension(300, 40));
        birthdayChooser.setDateFormatString("yyyy-MM-dd");
        recoveryPanel.add(birthdayChooser, gbc);
        
        // Show the dialog
        int result = JOptionPane.showConfirmDialog(
            loginPanel,
            recoveryPanel,
            "Account Recovery",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        // Process the result
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String address = addressField.getText().trim();
            String contact = contactField.getText().trim();
            
            // Get formatted date from JDateChooser
            Date birthdayDate = birthdayChooser.getDate();
            
            // Validate input
            if (username.isEmpty() || address.isEmpty() || contact.isEmpty() || birthdayDate == null) {
                JOptionPane.showMessageDialog(
                    loginPanel,
                    "All fields are required",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Format the date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String birthday = sdf.format(birthdayDate);
            
            // Verify account information
            try {
                // First check if user exists
                String userQuery = "SELECT id FROM users WHERE username = ?";
                ResultSet userRs = DatabaseManager.executeQuery(userQuery, username);
                
                if (!userRs.next()) {
                    JOptionPane.showMessageDialog(
                        loginPanel,
                        "No account found with this username",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                
                int userId = userRs.getInt("id");
                
                // Check if backup info matches
                String backupQuery = "SELECT * FROM user_backup_info WHERE user_id = ? AND address = ? AND contact_number = ? AND birthday = ?";
                ResultSet backupRs = DatabaseManager.executeQuery(backupQuery, userId, address, contact, birthday);
                
                if (!backupRs.next()) {
                    JOptionPane.showMessageDialog(
                        loginPanel,
                        "The information provided does not match our records",
                        "Verification Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                
                // If verification successful, show password reset dialog
                showPasswordResetDialog(userId);
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    loginPanel,
                    "Error during account verification: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showPasswordResetDialog(int userId) {
        boolean passwordResetCompleted = false;
        
        while (!passwordResetCompleted) {
            // Create password reset panel
            JPanel resetPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            
            resetPanel.add(new JLabel("Create a new password:"), gbc);
            
            // New password field
            resetPanel.add(new JLabel("New Password:"), gbc);
            JPasswordField newPasswordField = new JPasswordField(20);
            newPasswordField.setPreferredSize(new Dimension(300, 30));
            resetPanel.add(newPasswordField, gbc);
            
            // Confirm password field
            resetPanel.add(new JLabel("Confirm Password:"), gbc);
            JPasswordField confirmPasswordField = new JPasswordField(20);
            confirmPasswordField.setPreferredSize(new Dimension(300, 30));
            resetPanel.add(confirmPasswordField, gbc);
            
            // Show password checkbox
            JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
            showPasswordCheckBox.addActionListener(e -> {
                if (showPasswordCheckBox.isSelected()) {
                    newPasswordField.setEchoChar((char) 0);
                    confirmPasswordField.setEchoChar((char) 0);
                } else {
                    newPasswordField.setEchoChar('•');
                    confirmPasswordField.setEchoChar('•');
                }
            });
            resetPanel.add(showPasswordCheckBox, gbc);
            
            // Show the dialog
            int result = JOptionPane.showConfirmDialog(
                loginPanel,
                resetPanel,
                "Reset Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                String newPassword = new String(newPasswordField.getPassword()).trim();
                String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
                
                // Validate passwords
                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        loginPanel,
                        "Password fields cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    continue;
                }
                
                if (newPassword.length() < 8) {
                    JOptionPane.showMessageDialog(
                        loginPanel,
                        "Password must be at least 8 characters long",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    continue;
                }
                
                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(
                        loginPanel,
                        "Passwords do not match",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    continue;
                }
                
                // Update password in database
                try {
                    String hashedPassword = hashPassword(newPassword);
                    String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
                    DatabaseManager.executeUpdate(updateQuery, hashedPassword, userId);
                    
                    JOptionPane.showMessageDialog(
                        loginPanel,
                        "Password has been reset successfully!\nYou can now log in with your new password.",
                        "Password Reset Complete",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    
                    passwordResetCompleted = true;
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        loginPanel,
                        "Error updating password: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                // User cancelled
                passwordResetCompleted = true;
            }
        }
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
            e.printStackTrace();
            return null;
        }
    }

    private void setupKeyListeners() {
        KeyAdapter enterKeyListener = createEnterKeyListener();
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
    }

    private KeyAdapter createEnterKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        };
    }

    // Public methods - these remain unchanged to maintain compatibility
    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    // Add this method to create the checkbox
    private JCheckBox createShowPasswordCheckBox() {
        JCheckBox checkBox = new JCheckBox("Show Password");
        checkBox.setFocusable(false);
        checkBox.setBackground(null);
        checkBox.setForeground(Color.BLACK);
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()) {
                passwordField.setEchoChar((char) 0); // Show password
            } else {
                passwordField.setEchoChar('•'); // Hide password
            }
        });
        return checkBox;
    }

    public boolean validateFields() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (username.isEmpty() && password.isEmpty()) {
            JOptionPane.showMessageDialog(
                loginPanel,
                "Please fill in both username and password fields!",
                "Empty Fields",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(
                loginPanel,
                "Please enter your username!",
                "Username Required",
                JOptionPane.WARNING_MESSAGE
            );
            usernameField.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(
                loginPanel,
                "Please enter your password!",
                "Password Required",
                JOptionPane.WARNING_MESSAGE
            );
            passwordField.requestFocus();
            return false;
        }
        
        return true;
    }
}
