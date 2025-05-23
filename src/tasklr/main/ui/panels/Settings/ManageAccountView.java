package tasklr.main.ui.panels.Settings;

import javax.swing.*;
import java.awt.*;
import tasklr.utilities.HoverButtonEffect;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import tasklr.utilities.DatabaseManager;
import tasklr.utilities.createPanel;
import tasklr.authentication.UserSession;
import tasklr.main.ui.panels.Home.HomePanel;
import tasklr.authentication.*;

public class ManageAccountView {
    private static final Color BACKGROUND_COLOR = new Color(0xf1f3f6);
    
    public static JPanel createPanel() {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(100, 100));
        
        // Header
        JPanel headerPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        headerPanel.setPreferredSize(new Dimension(600, 80)); // Set header height
        
        // Back button
        JButton backButton = new JButton();
        try {
            ImageIcon backIcon = new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/BackArrow.png");
            Image scaledImage = backIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            backButton.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            backButton.setText("←"); // Fallback to text arrow if image fails to load
            System.err.println("Failed to load back arrow icon: " + e.getMessage());
        }
        backButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.addActionListener(e -> SettingsPanel.showMainView());

        // Add hover effect
        new HoverButtonEffect(backButton, 
            Color.WHITE,           // default background
            new Color(0xF5F5F5),   // hover background
            Color.BLACK,           // default text
            Color.BLACK            // hover text
        );
        
        JLabel titleLabel = new JLabel("Manage Account");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content
        JPanel contentPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create main form panel with GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        
        // Add Username section
        formPanel.add(createFormSection("Username", "Change your username"), gbc);
        
        // Add space
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 15, 0);
        formPanel.add(Box.createVerticalStrut(1), gbc);
        
        // Add Password section
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(createFormSection("Password", "Change your password"), gbc);
        
        // Add space
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 15, 0);
        formPanel.add(Box.createVerticalStrut(1), gbc);
        
        // Add Backup section
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(createFormSection("Backup Password", "Configure backup password"), gbc);
        
        // Add space
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 15, 0);
        formPanel.add(Box.createVerticalStrut(1), gbc);
        
        // Add Delete section
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        
        // Create Delete Account section
        JPanel deleteSection = new JPanel();
        deleteSection.setLayout(new BoxLayout(deleteSection, BoxLayout.Y_AXIS));
        deleteSection.setBackground(Color.WHITE);
        deleteSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xFFCDD2), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Delete Account Label
        JLabel deleteLabel = new JLabel("Delete Account");
        deleteLabel.setForeground(new Color(0xD32F2F));
        deleteLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        deleteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Warning Label
        JLabel warningLabel = new JLabel("This action can't be undone.");
        warningLabel.setForeground(Color.GRAY);
        warningLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        warningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Delete Button
        JButton deleteButton = new JButton("Delete Account");
        deleteButton.setPreferredSize(new Dimension(0, 35));
        deleteButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        deleteButton.setBackground(new Color(0xD32F2F));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        deleteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteButton.addActionListener(e -> handleDeleteAccount());

        // Add components to delete section
        deleteSection.add(deleteLabel);
        deleteSection.add(Box.createVerticalStrut(8));
        deleteSection.add(warningLabel);
        deleteSection.add(Box.createVerticalStrut(15));
        deleteSection.add(deleteButton);
        
        formPanel.add(deleteSection, gbc);
        
        // Add spacer panel after delete section
        gbc.gridy++;
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(0, 200));
        spacerPanel.setBackground(Color.WHITE);
        formPanel.add(spacerPanel, gbc);
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Color.WHITE);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add panels to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static void handleBackupSetup() {
        JTextField addressField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField birthdayField = new JTextField();

        JPanel backupPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        backupPanel.add(new JLabel("Please answer the following questions for account backup:"), gbc);
        backupPanel.add(new JLabel("Add your email address."), gbc);
        backupPanel.add(addressField, gbc);
        backupPanel.add(new JLabel("Add your contact number."), gbc);
        backupPanel.add(contactField, gbc);
        backupPanel.add(new JLabel("Add your birthday. (YYYY-MM-DD)"), gbc);
        backupPanel.add(birthdayField, gbc);

        int result = JOptionPane.showConfirmDialog(
            null,
            backupPanel,
            "Account Backup Setup",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String address = addressField.getText().trim();
            String contact = contactField.getText().trim();
            String birthday = birthdayField.getText().trim();

            // Validate inputs
            if (address.isEmpty() || contact.isEmpty() || birthday.isEmpty()) {
                JOptionPane.showMessageDialog(
                    null,
                    "All fields must be filled out",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try {
                // Check if backup info already exists
                String checkQuery = "SELECT id FROM user_backup_info WHERE user_id = ?";
                ResultSet rs = DatabaseManager.executeQuery(checkQuery, UserSession.getUserId());
                
                String query;
                if (rs.next()) {
                    // Update existing backup info
                    query = "UPDATE user_backup_info SET address = ?, contact_number = ?, birthday = ? WHERE user_id = ?";
                } else {
                    // Insert new backup info
                    query = "INSERT INTO user_backup_info (user_id, address, contact_number, birthday) VALUES (?, ?, ?, ?)";
                }

                DatabaseManager.executeUpdate(
                    query,
                    UserSession.getUserId(),
                    address,
                    contact,
                    birthday
                );

                JOptionPane.showMessageDialog(
                    null,
                    "Backup information saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "Error saving backup information: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private static JPanel createFormSection(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        // Add a compound border: Line border + Empty border for padding
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1), // Light gray border
            BorderFactory.createEmptyBorder(25, 25, 25, 25) // Increased padding
        ));
        panel.setPreferredSize(new Dimension(600, 150)); // Increased height from 80 to 100
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        // Create button with consistent size
        JButton actionButton = new JButton();
        actionButton.setBackground(new Color(0x0065D9));
        actionButton.setForeground(Color.WHITE);
        actionButton.setFocusPainted(false);
        actionButton.setBorderPainted(false);
        actionButton.setPreferredSize(new Dimension(80, 35));
        actionButton.setText(title.equals("Backup Password") ? "Backup" : "Edit");
        
        // Add hover effect
        actionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                actionButton.setBackground(new Color(0x0052AE)); // Darker blue on hover
                actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                actionButton.setBackground(new Color(0x0065D9)); // Return to original color
                actionButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        // Add specific action listeners based on section
        if (title.equals("Username")) {
            actionButton.addActionListener(e -> handleUsernameEdit());
        } else if (title.equals("Password")) {
            actionButton.addActionListener(e -> handlePasswordEdit());
        } else if (title.equals("Backup Password")) {
            actionButton.addActionListener(e -> handleBackupSetup());
        }
        
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0)); // Add horizontal gap between title and button
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(actionButton, BorderLayout.EAST);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.GRAY);
        descLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        descLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0)); // Increased top padding for description
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(descLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static void handleDeleteAccount() {
        // Create verification panel
        JPanel verificationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        verificationPanel.add(new JLabel("Please verify your identity to delete your account:"), gbc);
        verificationPanel.add(new JLabel("Username:"), gbc);
        verificationPanel.add(usernameField, gbc);
        verificationPanel.add(new JLabel("Password:"), gbc);
        verificationPanel.add(passwordField, gbc);
        verificationPanel.add(new JLabel("This action cannot be undone!"), gbc);

        // Show verification dialog
        int verificationResult = JOptionPane.showConfirmDialog(
            null,
            verificationPanel,
            "Verify Identity",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (verificationResult != JOptionPane.OK_OPTION) {
            return;
        }

        String enteredUsername = usernameField.getText().trim();
        String enteredPassword = new String(passwordField.getPassword()).trim();

        // Verify credentials
        try {
            String hashedPassword = hashPassword(enteredPassword);
            String verifyQuery = "SELECT id FROM users WHERE username = ? AND password = ? AND id = ?";
            ResultSet rs = DatabaseManager.executeQuery(
                verifyQuery, 
                enteredUsername, 
                hashedPassword, 
                UserSession.getUserId()
            );

            if (!rs.next()) {
                JOptionPane.showMessageDialog(
                    null,
                    "Invalid credentials. Please try again.",
                    "Verification Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Final confirmation
            int confirmResult = JOptionPane.showConfirmDialog(
                null,
                "Are you absolutely sure you want to delete your account?\n" +
                "This will permanently delete all your data including:\n" +
                "- Tasks\n" +
                "- Flashcards\n" +
                "- Account settings\n\n" +
                "This action CANNOT be undone!",
                "Final Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirmResult == JOptionPane.YES_OPTION) {
                try {
                    Connection conn = DatabaseManager.getConnection();
                    conn.setAutoCommit(false);

                    try {
                        // Delete user's data in correct order (respecting foreign keys)
                        DatabaseManager.executeUpdate(
                            "DELETE FROM flashcards WHERE set_id IN " +
                            "(SELECT set_id FROM flashcard_sets WHERE user_id = ?)",
                            UserSession.getUserId()
                        );

                        DatabaseManager.executeUpdate(
                            "DELETE FROM flashcard_sets WHERE user_id = ?",
                            UserSession.getUserId()
                        );

                        DatabaseManager.executeUpdate(
                            "DELETE FROM tasks WHERE user_id = ?",
                            UserSession.getUserId()
                        );

                        DatabaseManager.executeUpdate(
                            "DELETE FROM user_backup_info WHERE user_id = ?",
                            UserSession.getUserId()
                        );

                        DatabaseManager.executeUpdate(
                            "DELETE FROM sessions WHERE user_id = ?",
                            UserSession.getUserId()
                        );

                        DatabaseManager.executeUpdate(
                            "DELETE FROM users WHERE id = ?",
                            UserSession.getUserId()
                        );

                        conn.commit();

                        // Clear session and exit to login screen
                        UserSession.clearSession();
                        JOptionPane.showMessageDialog(
                            null,
                            "Your account has been successfully deleted.",
                            "Account Deleted",
                            JOptionPane.INFORMATION_MESSAGE
                        );

                        // Find and dispose the main application frame
                        for (Window window : Window.getWindows()) {
                            if (window instanceof JFrame && window.isVisible() && window instanceof tasklr.main.ui.frames.Tasklr) {
                                window.dispose();
                                break;
                            }
                        }
                        
                        // Show login screen
                        new Login().setVisible(true);

                    } catch (SQLException ex) {
                        conn.rollback();
                        throw ex;
                    } finally {
                        conn.setAutoCommit(true);
                        conn.close();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        null,
                        "Error deleting account: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Error verifying credentials: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static void handleUsernameEdit() {
        try {
            // Get current username
            String currentUsername = UserSession.getUsername();
            
            // Create input dialog
            JTextField usernameField = new JTextField(currentUsername);
            JPanel inputPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            inputPanel.add(new JLabel("New Username:"));
            inputPanel.add(usernameField);

            int result = JOptionPane.showConfirmDialog(
                null,
                inputPanel,
                "Change Username",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String newUsername = usernameField.getText().trim();

                // Validate new username
                if (newUsername.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Username cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                if (newUsername.equals(currentUsername)) {
                    JOptionPane.showMessageDialog(
                        null,
                        "New username is same as current username",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                // Check if username already exists
                String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ? AND id != ?";
                ResultSet rs = DatabaseManager.executeQuery(checkQuery, newUsername, UserSession.getUserId());
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Username already exists",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // Update username in database
                String updateQuery = "UPDATE users SET username = ? WHERE id = ?";
                DatabaseManager.executeUpdate(updateQuery, newUsername, UserSession.getUserId());

                // Update session username
                UserSession.setUsername(newUsername);

                // Refresh HomePanel
                SwingUtilities.invokeLater(() -> {
                    HomePanel.updateWelcomeMessage();
                });

                JOptionPane.showMessageDialog(
                    null,
                    "Username updated successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Error updating username: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static void handlePasswordEdit() {
        try {
            // First, verify user identity
            JTextField usernameField = new JTextField();
            JPasswordField currentPasswordField = new JPasswordField();
            
            JPanel verificationPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            
            verificationPanel.add(new JLabel("Please verify your identity:"), gbc);
            verificationPanel.add(new JLabel("Username:"), gbc);
            verificationPanel.add(usernameField, gbc);
            verificationPanel.add(new JLabel("Current Password:"), gbc);
            verificationPanel.add(currentPasswordField, gbc);

            int verificationResult = JOptionPane.showConfirmDialog(
                null,
                verificationPanel,
                "Verify Identity",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (verificationResult != JOptionPane.OK_OPTION) {
                return;
            }

            String enteredUsername = usernameField.getText().trim();
            String enteredPassword = new String(currentPasswordField.getPassword()).trim();

            // Verify credentials
            if (!verifyCredentials(enteredUsername, enteredPassword)) {
                JOptionPane.showMessageDialog(
                    null,
                    "Invalid credentials. Please try again.",
                    "Verification Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // If verified, show password change dialog
            boolean passwordChangeCompleted = false;
            
            while (!passwordChangeCompleted) {
                JPasswordField newPasswordField = new JPasswordField();
                JPasswordField confirmPasswordField = new JPasswordField();
                JCheckBox showPasswordCheckBox = new JCheckBox("Show Passwords");

                // Create password change panel
                JPanel changePanel = new JPanel(new GridBagLayout());
                gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.gridwidth = GridBagConstraints.REMAINDER;

                changePanel.add(new JLabel("Enter New Password:"), gbc);
                changePanel.add(newPasswordField, gbc);
                changePanel.add(new JLabel("Confirm New Password:"), gbc);
                changePanel.add(confirmPasswordField, gbc);
                changePanel.add(showPasswordCheckBox, gbc);

                // Add show/hide password functionality
                showPasswordCheckBox.addActionListener(e -> {
                    char echoChar = showPasswordCheckBox.isSelected() ? (char)0 : '•';
                    newPasswordField.setEchoChar(echoChar);
                    confirmPasswordField.setEchoChar(echoChar);
                });

                int changeResult = JOptionPane.showConfirmDialog(
                    null,
                    changePanel,
                    "Change Password",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
                );

                if (changeResult == JOptionPane.OK_OPTION) {
                    String newPassword = new String(newPasswordField.getPassword()).trim();
                    String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

                    // Validate new password
                    if (validateNewPassword(newPassword, confirmPassword)) {
                        // Hash and update the new password
                        String hashedNewPassword = hashPassword(newPassword);
                        String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
                        DatabaseManager.executeUpdate(updateQuery, hashedNewPassword, UserSession.getUserId());

                        JOptionPane.showMessageDialog(
                            null,
                            "Password updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        passwordChangeCompleted = true;
                    }
                    // If validation fails, the loop continues and shows the dialog again
                } else {
                    // User clicked Cancel
                    passwordChangeCompleted = true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Error updating password: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static boolean validateNewPassword(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "New password cannot be empty",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(
                null,
                "Passwords do not match",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        // Add password strength requirements
        if (newPassword.length() < 8) {
            JOptionPane.showMessageDialog(
                null,
                "Password must be at least 8 characters long",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        // Check for at least one number
        if (!newPassword.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(
                null,
                "Password must contain at least one number",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        return true;
    }

    private static String hashPassword(String password) {
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

    private static boolean verifyCredentials(String username, String password) {
        try {
            String hashedPassword = hashPassword(password);
            String query = "SELECT id FROM users WHERE username = ? AND password = ?";
            
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() && rs.getInt("id") == UserSession.getUserId();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Error verifying credentials: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }
}
