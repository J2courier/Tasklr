package tasklr.main.ui.panels.Settings;

import javax.swing.*;
import java.awt.*;
import tasklr.utilities.createPanel;

public class SettingsPanel {
    private static final Color BACKGROUND_COLOR = new Color(0xf1f3f6);
    
    public static JPanel createSettingsPanel() {
        JPanel settingsPanel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(100, 100));
        
        // Header
        JPanel headerPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Main content
        JPanel contentPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create settings options
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(Color.WHITE);
        
        // Add settings options
        addSettingOption(optionsPanel, "Account Settings", "Manage your account information");
        addSettingOption(optionsPanel, "Appearance", "Customize the application theme");
        addSettingOption(optionsPanel, "Notifications", "Configure notification preferences");
        addSettingOption(optionsPanel, "Privacy", "Manage privacy settings");
        
        JScrollPane scrollPane = new JScrollPane(optionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add panels to main settings panel
        settingsPanel.add(headerPanel, BorderLayout.NORTH);
        settingsPanel.add(contentPanel, BorderLayout.CENTER);
        
        return settingsPanel;
    }
    
    private static void addSettingOption(JPanel container, String title, String description) {
        JPanel optionPanel = new JPanel(new BorderLayout());
        optionPanel.setBackground(Color.WHITE);
        optionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE5E7EB)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        descLabel.setForeground(new Color(0x6B7280));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);
        
        optionPanel.add(textPanel, BorderLayout.CENTER);
        optionPanel.add(new JLabel("→"), BorderLayout.EAST);
        
        // Add hover effect
        optionPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                optionPanel.setBackground(new Color(0xF3F4F6));
                textPanel.setBackground(new Color(0xF3F4F6));
                optionPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                optionPanel.setBackground(Color.WHITE);
                textPanel.setBackground(Color.WHITE);
                optionPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        container.add(optionPanel);
        container.add(Box.createRigidArea(new Dimension(0, 1)));
    }
}
