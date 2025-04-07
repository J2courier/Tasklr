package tasklr.main.ui.panels.Settings;

import javax.swing.*;
import java.awt.*;
import tasklr.utilities.createPanel;

public class AppearanceView {
    private static final Color BACKGROUND_COLOR = new Color(0xf1f3f6);
    
    public static JPanel createPanel() {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(100, 100));
        
        // Header
        JPanel headerPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Back button
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.addActionListener(e -> SettingsPanel.showMainView());
        
        JLabel titleLabel = new JLabel("Appearance");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content
        JPanel contentPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create appearance settings sections
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 15, 0);
        
        // Theme selection
        settingsPanel.add(createThemeSection(), gbc);
        
        // Color scheme
        settingsPanel.add(createColorSchemeSection(), gbc);
        
        // Font settings
        settingsPanel.add(createFontSection(), gbc);
        
        contentPanel.add(settingsPanel, BorderLayout.NORTH);
        
        // Add panels to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static JPanel createThemeSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Theme");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));  // Changed from 16
        
        JPanel themeOptions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        themeOptions.setBackground(Color.WHITE);
        
        ButtonGroup themeGroup = new ButtonGroup();
        JRadioButton lightTheme = new JRadioButton("Light");
        JRadioButton darkTheme = new JRadioButton("Dark");
        JRadioButton systemTheme = new JRadioButton("System Default");
        
        themeGroup.add(lightTheme);
        themeGroup.add(darkTheme);
        themeGroup.add(systemTheme);
        
        lightTheme.setSelected(true);
        
        themeOptions.add(lightTheme);
        themeOptions.add(darkTheme);
        themeOptions.add(systemTheme);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(themeOptions, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static JPanel createColorSchemeSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Color Scheme");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));  // Changed from 16
        
        JComboBox<String> colorSchemes = new JComboBox<>(new String[]{
            "Default Blue",
            "Forest Green",
            "Royal Purple",
            "Sunset Orange"
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(colorSchemes, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static JPanel createFontSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Font Settings");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));  // Changed from 16
        
        JPanel fontControls = new JPanel(new GridLayout(2, 2, 10, 10));
        fontControls.setBackground(Color.WHITE);
        
        fontControls.add(new JLabel("Font Family:"));
        fontControls.add(new JComboBox<>(new String[]{
            "Segoe UI Variable",
            "Arial",
            "Helvetica",
            "Times New Roman"
        }));
        
        fontControls.add(new JLabel("Font Size:"));
        fontControls.add(new JComboBox<>(new String[]{
            "Small",
            "Medium",
            "Large"
        }));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(fontControls, BorderLayout.CENTER);
        
        return panel;
    }
}
