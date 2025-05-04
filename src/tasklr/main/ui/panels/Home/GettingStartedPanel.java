package tasklr.main.ui.panels.Home;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.concurrent.atomic.AtomicBoolean;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;

import tasklr.utilities.*;
import tasklr.authentication.UserSession;
import tasklr.main.ui.frames.Tasklr;

public class GettingStartedPanel extends JDialog {
    private static final Color BACKGROUND_COLOR = new Color(0xf1f3f6);
    private static final Color PRIMARY_COLOR = new Color(0x275CE2);
    private static final Color BUTTON_COLOR = new Color(0x0065D9);
    private static final Color TEXT_COLOR = new Color(0x1D1D1D);
    private static final Color BORDER_COLOR = new Color(0xE0E0E0);
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private final Frame owner;
    private final String username;
    
    public GettingStartedPanel(Frame owner, String username) {
        super(owner, "Welcome to Duetz", true);
        this.owner = owner;
        this.username = username;
        initializePanel();
    }
    
    private void initializePanel() {
        setSize(800, 600);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        
        // Add views
        mainPanel.add(createGreetingsView(), "greetings");
        mainPanel.add(createSetupView(), "setup");
        
        add(mainPanel);
        
        // Show first view
        cardLayout.show(mainPanel, "greetings");
    }
    
    private JPanel createGreetingsView() {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to Duetz!");
        welcomeLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 32));
        welcomeLabel.setForeground(PRIMARY_COLOR);
        gbc.gridy = 0;
        panel.add(welcomeLabel, gbc);
        
        // Username greeting
        JLabel usernameLabel = new JLabel("Hello, " + username + "!");
        usernameLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        gbc.gridy = 1;
        panel.add(usernameLabel, gbc);
        
        // Description
        JLabel descLabel = new JLabel("<html><div style='text-align: center; width: 400px;'>" +
            "We're excited to help you organize your tasks and boost your productivity. " +
            "Let's take a moment to set up your account for the best experience.</div></html>");
        descLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 20, 30, 20);
        panel.add(descLabel, gbc);
        
        // Next button
        JButton nextButton = new JButton("Let's Get Started");
        nextButton.setBackground(BUTTON_COLOR);
        nextButton.setForeground(Color.WHITE);
        nextButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(e -> cardLayout.show(mainPanel, "setup"));
        
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(nextButton, gbc);
        
        return panel;
    }
    
    private JPanel createSetupView() {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), null);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 20, 0, 20);
        
        // Title
        JLabel titleLabel = new JLabel("Account Security Setup");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);
        
        // Space 5
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel.add(Box.createVerticalStrut(5), gbc);
        
        // Explanation
        JLabel explanationLabel = new JLabel("<html><div style='width: 400px;'>" +
            "To ensure the security of your account and enable account recovery if needed, " +
            "please provide the following information. This information will be used to verify " +
            "your identity if you ever need to recover your account access.</div></html>");
        explanationLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 0, 20);
        panel.add(explanationLabel, gbc);
        
        // Space 5
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel.add(Box.createVerticalStrut(10), gbc);
        
        // Input fields
        JTextField addressField = new JTextField();
        addressField.setPreferredSize(new Dimension(400, 30));
        addressField.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));

        JTextField contactField = new JTextField();
        contactField.setPreferredSize(new Dimension(400, 30));
        contactField.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));

        JDateChooser birthdayChooser = new JDateChooser();
        birthdayChooser.setPreferredSize(new Dimension(400, 30));
        birthdayChooser.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        birthdayChooser.setDateFormatString("yyyy-MM-dd");
        
        // Address
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 20, 0, 20);
        panel.add(addressLabel, gbc);
        
        gbc.gridy = 5;
        panel.add(addressField, gbc);
        
        // Space 5
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel.add(Box.createVerticalStrut(10), gbc);
        
        // Contact
        JLabel contactLabel = new JLabel("Contact Number:");
        contactLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 20, 0, 20);
        panel.add(contactLabel, gbc);
        
        gbc.gridy = 8;
        panel.add(contactField, gbc);
        
        // Space 5
        gbc.gridy = 9;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel.add(Box.createVerticalStrut(10), gbc);
        
        // Birthday
        JLabel birthdayLabel = new JLabel("Birthday:");
        birthdayLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 20, 0, 20);
        panel.add(birthdayLabel, gbc);
        
        gbc.gridy = 11;
        panel.add(birthdayChooser, gbc);
        
        // Space 5
        gbc.gridy = 12;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel.add(Box.createVerticalStrut(55), gbc);
        
        // Finish button
        JButton finishButton = new JButton("Complete Setup");
        finishButton.setBackground(BUTTON_COLOR);
        finishButton.setForeground(Color.WHITE);
        finishButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        finishButton.setFocusPainted(false);
        finishButton.setFocusable(false);

        finishButton.addActionListener(e -> {
            if (validateAndSaveBackupInfo(addressField.getText().trim(), contactField.getText().trim(), birthdayChooser)) {
                dispose();
            }
        });

        gbc.gridy = 13;
        gbc.insets = new Insets(0, 20, 0, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(finishButton, gbc);

        // Space after finish button
        gbc.gridy = 14;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel.add(Box.createVerticalStrut(10), gbc);
        
        return panel;
    }
    
    private boolean validateAndSaveBackupInfo(String address, String contact, JDateChooser birthdayChooser) {
        // Validate inputs
        if (address.isEmpty() || contact.isEmpty() || birthdayChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                "All fields must be filled out",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Format the date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String birthday = sdf.format(birthdayChooser.getDate());
        
        // Save to database
        try {
            String query = "INSERT INTO user_backup_info (user_id, address, contact_number, birthday) VALUES (?, ?, ?, ?)";
            DatabaseManager.executeUpdate(query, UserSession.getUserId(), address, contact, birthday);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error saving backup information: " + ex.getMessage());
            return false;
        }
    }
    
    public static void showIfFirstTime(Frame owner, String username) {
        try {
            String checkQuery = "SELECT id FROM user_backup_info WHERE user_id = ?";
            ResultSet rs = DatabaseManager.executeQuery(checkQuery, UserSession.getUserId());
            if (!rs.next()) {
                SwingUtilities.invokeLater(() -> {
                    GettingStartedPanel panel = new GettingStartedPanel(owner, username);
                    panel.setVisible(true);
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error checking backup information: " + ex.getMessage());
        }
    }
}
