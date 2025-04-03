package tasklr.main.ui.panels.Settings;

import javax.swing.*;
import java.awt.*;
import tasklr.utilities.*;
import tasklr.authentication.UserSession;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import tasklr.main.ui.panels.Home.HomePanel;

public class ManageAccountView {
    private static final Color BACKGROUND_COLOR = new Color(0xf1f3f6);
    
    public static JPanel createPanel() {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(100, 100));
        
        // Header
        JPanel headerPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        headerPanel.setPreferredSize(new Dimension(600, 80)); // Set header height
        
        // Back button
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.addActionListener(e -> SettingsPanel.showMainView());
        
        JLabel titleLabel = new JLabel("Manage Account");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content
        JPanel contentPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setPreferredSize(new Dimension(600, 600)); // Set content panel size
        
        // Create a wrapper panel for the form
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(Color.WHITE);
        formWrapper.setPreferredSize(new Dimension(600, 500)); // Set form wrapper size
        
        // Add your account management components here
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(600, 450)); // Set form panel size
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0);
        
        // Add sections
        formPanel.add(createFormSection("Username", "Change your username"), gbc);
        formPanel.add(createFormSection("Password", "Change your password"), gbc);
        formPanel.add(createFormSection("Backup Password", "Configure backup password"), gbc);
        
        // Delete Account section
        JPanel deleteSection = new JPanel(new BorderLayout());
        deleteSection.setBackground(Color.WHITE);
        deleteSection.setPreferredSize(new Dimension(600, 100)); // Set delete section size
        deleteSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xFFCDD2), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel deleteLabel = new JLabel("Delete Account");
        deleteLabel.setForeground(new Color(0xD32F2F));
        deleteLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JButton deleteButton = new JButton("Delete Account");
        deleteButton.setBackground(new Color(0xD32F2F));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> handleDeleteAccount());
        
        deleteSection.add(deleteLabel, BorderLayout.NORTH);
        deleteSection.add(new JLabel("This action cannot be undone."), BorderLayout.CENTER);
        deleteSection.add(deleteButton, BorderLayout.SOUTH);
        
        gbc.insets = new Insets(30, 0, 0, 0);
        formPanel.add(deleteSection, gbc);
        
        formWrapper.add(formPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(formWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Color.WHITE);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static JPanel createFormSection(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        panel.setPreferredSize(new Dimension(600, 90));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JButton editButton = new JButton("Edit");
        editButton.setBackground(new Color(0x0065D9));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        
        // Add specific action listener for username editing
        if (title.equals("Username")) {
            editButton.addActionListener(e -> handleUsernameEdit());
        }
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(editButton, BorderLayout.EAST);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.GRAY);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(descLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static void handleDeleteAccount() {
        int result = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want to delete your account? This action cannot be undone.",
            "Confirm Account Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                null,
                "Account deletion functionality coming soon!",
                "Not Implemented",
                JOptionPane.INFORMATION_MESSAGE
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
}
