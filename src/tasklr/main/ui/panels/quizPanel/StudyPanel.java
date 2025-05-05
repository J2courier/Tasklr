package tasklr.main.ui.panels.quizPanel;

import javax.swing.*;
import javax.swing.border.Border;

import tasklr.utilities.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.Box;
import tasklr.authentication.UserSession;
import tasklr.utilities.DatabaseManager;
import tasklr.utilities.TextPrompt;
import tasklr.utilities.Toast;

public class StudyPanel {
    private static JPanel cardPanel;
    private static CardLayout cardLayout;
    private static final Color PRIMARY_COLOR = new Color(0x275CE2);    // Add this constant for header color
    private static final int HEADER_HEIGHT = 70;                       // Add this constant for header height

    public static JPanel createStudyPanel() {
        JPanel mainPanel = createPanel.panel(new Color(0xFFFFFF), new BorderLayout(), new Dimension(100, 100));
        Border mainBorder = BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D)),
        BorderFactory.createEmptyBorder(20, 20, 0, 20)  // Add 20px padding on all sides
    );
        mainPanel.setBorder(mainBorder);

        // Create navigation panel
        JPanel navPanel = createNavPanel();
        mainPanel.add(navPanel, BorderLayout.NORTH);

        // Create card panel
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Add panels to card layout
        cardPanel.add(FlashcardPanel.createFlashcardPanel(), "flashcard");
        cardPanel.add(QuizzerPanel.createQuizzerPanel(), "quizzer");

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private static JPanel createNavPanel() {
        JPanel navPanel = createPanel.panel(PRIMARY_COLOR, new BorderLayout(), new Dimension(0, HEADER_HEIGHT));
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Left side - Navigation buttons
        JPanel navButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        navButtonsPanel.setOpaque(false);

        // Create navigation buttons with initial states
        JButton flashcardBtn = createButton.button("FLASHCARDS", null, PRIMARY_COLOR, null, false);
        flashcardBtn.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        flashcardBtn.setPreferredSize(new Dimension(180, 40));
        flashcardBtn.setBorderPainted(false);
        flashcardBtn.setFocusPainted(false);
        
        JButton quizzerBtn = createButton.button("QUIZZER", null, PRIMARY_COLOR, null, false);
        quizzerBtn.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        quizzerBtn.setPreferredSize(new Dimension(180, 40));
        quizzerBtn.setBorderPainted(false);
        quizzerBtn.setFocusPainted(false);

        flashcardBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "flashcard");
        });

        quizzerBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "quizzer");
            // Fetch and load flashcard sets when Quizzer tab is clicked
            QuizzerPanel.refreshQuizContainer();
        });

        // Set initial active state (Flashcard active by default)
        flashcardBtn.setBackground(PRIMARY_COLOR);
        flashcardBtn.setForeground(Color.WHITE);
        quizzerBtn.setBackground(PRIMARY_COLOR);
        quizzerBtn.setForeground(Color.WHITE);

        navButtonsPanel.add(flashcardBtn);
        navButtonsPanel.add(quizzerBtn);
        navPanel.add(navButtonsPanel, BorderLayout.WEST);
        
        // Right side - Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        searchPanel.setOpaque(false);
        
        // Create search field with placeholder
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xDDDDDD)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Add placeholder text
        TextPrompt placeholder = new TextPrompt("Search flashcard sets...", searchField);
        placeholder.changeAlpha(0.7f);
        placeholder.changeStyle(Font.ITALIC);
        
        // Create search button
        JButton searchButton = createButton.button("Search", null, Color.WHITE, null, false);
        searchButton.setBackground(new Color(0x1E40AF));
        searchButton.setPreferredSize(new Dimension(80, 35));
        
        // Create close button (initially hidden)
        JButton closeButton = createButton.button("×", null, new Color(0xDC3545), null, false);
        closeButton.setPreferredSize(new Dimension(35, 35));
        closeButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        closeButton.setFocusPainted(false);
        closeButton.setVisible(false); // Initially hidden
        
        // Add search functionality
        ActionListener searchAction = e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (!searchTerm.isEmpty()) {
                closeButton.setVisible(true); // Show close button when search is active
                searchFlashcardSets(searchTerm);
            }
        };
        
        searchButton.addActionListener(searchAction);
        
        // Add close button functionality
        closeButton.addActionListener(e -> {
            searchField.setText(""); // Clear search field
            closeButton.setVisible(false); // Hide close button
            searchFlashcardSets(""); // Show all sets
        });
        
        // Add enter key listener to search field
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String searchTerm = searchField.getText().trim().toLowerCase();
                    if (!searchTerm.isEmpty()) {
                        closeButton.setVisible(true); // Show close button when search is active
                    }
                    searchFlashcardSets(searchTerm);
                }
            }
        });
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(closeButton);
        
        navPanel.add(searchPanel, BorderLayout.EAST);

        return navPanel;
    }

    // Update the static methods to handle button states instead of navLabel
    public static void showFlashcardCreation() {
        if (cardPanel != null && cardLayout != null) {
            cardLayout.show(cardPanel, "flashcard");
        }
    }

    public static void showQuizzer() {
        if (cardPanel != null && cardLayout != null) {
            cardLayout.show(cardPanel, "quizzer");
            // Refresh the quiz container when switching to Quizzer
            QuizzerPanel.refreshQuizContainer();
        }
    }

    // Add this method to handle searching flashcard sets
    private static void searchFlashcardSets(String searchTerm) {
        // Get the appropriate container based on which panel is active
        JPanel activeContainer = null;
        
        if (cardPanel.getComponent(0).isVisible()) {
            // Flashcard panel is active
            activeContainer = FlashcardPanel.getQuizContainer();
        } else if (cardPanel.getComponent(1).isVisible()) {
            // Quizzer panel is active
            activeContainer = QuizzerPanel.getQuizContainer();
        }
        
        if (activeContainer == null) return;
        
        // Clear the container
        activeContainer.removeAll();
        
        try {
            String query;
            ResultSet rs;
            
            if (searchTerm.isEmpty()) {
                // If search term is empty, show all sets
                query = "SELECT set_id, subject, description FROM flashcard_sets WHERE user_id = ? ORDER BY subject ASC";
                rs = DatabaseManager.executeQuery(query, UserSession.getUserId());
            } else {
                // If search term is not empty, filter sets
                query = "SELECT set_id, subject, description FROM flashcard_sets WHERE user_id = ? AND " +
                       "(subject LIKE ? OR description LIKE ?) ORDER BY subject ASC";
                rs = DatabaseManager.executeQuery(
                    query, 
                    UserSession.getUserId(),
                    "%" + searchTerm + "%",
                    "%" + searchTerm + "%"
                );
            }
            
            boolean hasItems = false;
            
            while (rs.next()) {
                hasItems = true;
                int setId = rs.getInt("set_id");
                String subject = rs.getString("subject");
                String description = rs.getString("description");
                
                // Create and add set panel based on which view is active
                if (cardPanel.getComponent(0).isVisible()) {
                    // Flashcard panel is active
                    JPanel setPanel = FlashcardPanel.createSetItemPanel(setId, subject, description);
                    activeContainer.add(setPanel);
                    
                    // Use consistent spacing of 5 pixels between items
                    activeContainer.add(Box.createVerticalStrut(5));
                } else {
                    // Quizzer panel is active
                    JPanel setPanel = QuizzerPanel.createQuizSetItemPanel(setId, subject, description);
                    activeContainer.add(setPanel);
                    
                    // Use consistent spacing of 5 pixels between items
                    activeContainer.add(Box.createVerticalStrut(5));
                }
            }
            
            // Add "No sets found" message if there are no matching sets
            if (!hasItems) {
                JPanel centeringPanel = new JPanel(new GridBagLayout());
                centeringPanel.setBackground(Color.WHITE);
                
                JLabel noSetsLabel = new JLabel(searchTerm.isEmpty() ? 
                                             "No flashcard sets yet" : 
                                             "No flashcard sets matching '" + searchTerm + "'");
                noSetsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                noSetsLabel.setForeground(new Color(0x707070));
                
                centeringPanel.add(noSetsLabel);
                activeContainer.add(centeringPanel);
            }
            
            // Refresh the container
            activeContainer.revalidate();
            activeContainer.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error searching flashcard sets: " + ex.getMessage());
        }
    }

    // Add this method to create set item panels
    private static JPanel createSetItemPanel(int setId, String subject, String description) {
        // Main panel with fixed height and full width
        JPanel panel = createPanel.panel(new Color(0xFFFFFF), new BorderLayout(), null);
        panel.setPreferredSize(new Dimension(550, 100));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)),
            BorderFactory.createEmptyBorder(20, 0, 20, 10)
        ));

        // Content panel
        JPanel contentPanel = createPanel.panel(new Color(0xFFFFFF), new BorderLayout(10, 0), null);

        // Text Panel (Left side)
        JPanel textPanel = createPanel.panel(null, new BorderLayout(), null);
        
        // Subject label
        JLabel subjectLabel = new JLabel(subject);
        subjectLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        subjectLabel.setForeground(new Color(0x333333));
        
        // Description label
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(0x707070));
        
        // Add labels to text panel
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        labelsPanel.setOpaque(false);
        labelsPanel.add(subjectLabel);
        labelsPanel.add(Box.createVerticalStrut(5));
        labelsPanel.add(descriptionLabel);
        
        textPanel.add(labelsPanel, BorderLayout.CENTER);
        
        // Button Panel (Right side)
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.RIGHT), null);
        
        // View button
        JButton viewBtn = createButton.button("View", null, Color.WHITE, null, false);
        viewBtn.setBackground(new Color(0x275CE2));
        viewBtn.setPreferredSize(new Dimension(80, 35));
        viewBtn.addActionListener(e -> {
            if (cardPanel.getComponent(0).isVisible()) {
                // If in Flashcard panel, show flashcard mode
                FlashcardPanel.showFlashcardMode(setId);
            } else {
                // If in Quizzer panel, show quiz options
                QuizzerPanel.showQuizOptions(setId, subject);
            }
        });
        
        buttonPanel.add(viewBtn);
        
        contentPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Add hover effect
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(0xF5F5F5));
                contentPanel.setBackground(new Color(0xF5F5F5));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(0xFFFFFF));
                contentPanel.setBackground(new Color(0xFFFFFF));
            }
        });
        
        return panel;
    }
}
