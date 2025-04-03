package tasklr.main.ui.panels.Settings;

import javax.swing.*;
import java.awt.*;
import tasklr.utilities.createPanel;

public class ManageEntriesView {
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
        
        JLabel titleLabel = new JLabel("Manage Entries");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content
        JPanel contentPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create entries management sections
        JPanel sectionsPanel = new JPanel();
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBackground(Color.WHITE);
        
        // Tasks section
        sectionsPanel.add(createSection("Tasks", "Manage your tasks and categories"));
        sectionsPanel.add(Box.createVerticalStrut(15));
        
        // Flashcard Sets section
        sectionsPanel.add(createSection("Flashcard Sets", "Manage your flashcard sets"));
        sectionsPanel.add(Box.createVerticalStrut(15));
        
        // Flashcards section
        sectionsPanel.add(createSection("Flashcards", "Manage individual flashcards"));
        
        JScrollPane scrollPane = new JScrollPane(sectionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add panels to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static JPanel createSection(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Header with title and manage button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JButton manageButton = new JButton("Manage");
        manageButton.setBackground(new Color(0x0065D9));
        manageButton.setForeground(Color.WHITE);
        manageButton.setFocusPainted(false);
        manageButton.addActionListener(e -> handleManage(title));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(manageButton, BorderLayout.EAST);
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.GRAY);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(descLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static void handleManage(String section) {
        // TODO: Implement management functionality for each section
        JOptionPane.showMessageDialog(
            null,
            section + " management functionality coming soon!",
            "Not Implemented",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
