package tasklr.main.ui.panels.Settings;

import javax.swing.*;
import java.awt.*;
import tasklr.utilities.createPanel;
import tasklr.authentication.Login;
import tasklr.authentication.UserSession;

public class SettingsPanel {
    private static final Color BACKGROUND_COLOR = new Color(0xf1f3f6);
    private static final Color OPTION_HOVER_COLOR = new Color(0xE8F0FE);
    private static final Color BORDER_COLOR = new Color(0xE0E0E0);
    private static final Font TITLE_FONT = new Font("Segoe UI Variable", Font.BOLD, 16);
    private static final Font DESCRIPTION_FONT = new Font("Segoe UI Variable", Font.PLAIN, 14);
    private static CardLayout cardLayout;
    private static JPanel cardPanel;
    private static final String MAIN_VIEW = "main";
    private static final String ACCOUNT_VIEW = "account";
    private static final String ENTRIES_VIEW = "entries";
    private static final String APPEARANCE_VIEW = "appearance";
    
    public static JPanel createSettingsPanel() {
        JPanel settingsPanel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(100, 100));
        // Create card panel
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        
        // Add all views to the card panel
        cardPanel.add(createMainView(), MAIN_VIEW);
        cardPanel.add(ManageAccountView.createPanel(), ACCOUNT_VIEW);
        cardPanel.add(ManageEntriesView.createPanel(), ENTRIES_VIEW);
        cardPanel.add(AppearanceView.createPanel(), APPEARANCE_VIEW);
        
        settingsPanel.add(cardPanel, BorderLayout.CENTER);
        
        return settingsPanel;
    }
    
    private static JPanel createOptionPanel(String title, String description, java.awt.event.ActionListener action) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Content Panel (for title and description)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(DESCRIPTION_FONT);
        descLabel.setForeground(Color.GRAY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(descLabel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Add hover effect
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(OPTION_HOVER_COLOR);
                contentPanel.setBackground(OPTION_HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(Color.WHITE);
                contentPanel.setBackground(Color.WHITE);
            }
            
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                action.actionPerformed(new java.awt.event.ActionEvent(panel, 0, "clicked"));
            }
        });
        
        // Make the panel take full width
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        
        return panel;
    }
    
    private static void showManageAccountDialog() {
        showAccountView();
    }
    
    private static void showManageEntriesDialog() {
        showEntriesView();
    }
    
    private static void showAppearanceDialog() {
        showAppearanceView();
    }
    
    private static void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Clear the user session
                UserSession.clearSession();
                
                // Find the current window using the cardPanel
                Window window = SwingUtilities.getWindowAncestor(cardPanel);
                if (window instanceof JFrame) {
                    window.dispose();
                    // Show login screen
                    new Login().setVisible(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "Error during logout: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void showMainView() {
        cardLayout.show(cardPanel, MAIN_VIEW);
    }

    public static void showAccountView() {
        cardLayout.show(cardPanel, ACCOUNT_VIEW);
    }

    public static void showEntriesView() {
        cardLayout.show(cardPanel, ENTRIES_VIEW);
    }

    public static void showAppearanceView() {
        cardLayout.show(cardPanel, APPEARANCE_VIEW);
    }

    private static JPanel createMainView() {
        JPanel mainView = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        
        // Header
        JPanel headerPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Options Panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(Color.WHITE);
        
        // Add options
        optionsPanel.add(createOptionPanel(
            "Manage Account",
            "Change account settings, password, and security options",
            e -> showManageAccountDialog()
        ));
        
        optionsPanel.add(createOptionPanel(
            "Manage Entries",
            "Manage your tasks, flashcards, and other content",
            e -> showManageEntriesDialog()
        ));
        
        optionsPanel.add(createOptionPanel(
            "Appearance",
            "Customize the look and feel of the application",
            e -> showAppearanceDialog()
        ));
        
        optionsPanel.add(createOptionPanel(
            "Logout",
            "Sign out of your account",
            e -> handleLogout()
        ));
        
        // Wrap options in a scroll pane
        JScrollPane scrollPane = new JScrollPane(optionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add components to main view
        mainView.add(headerPanel, BorderLayout.NORTH);
        mainView.add(scrollPane, BorderLayout.CENTER);
        
        return mainView;
    }
}
