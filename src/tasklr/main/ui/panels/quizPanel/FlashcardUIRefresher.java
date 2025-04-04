package tasklr.main.ui.panels.quizPanel;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import tasklr.utilities.DatabaseManager;
import tasklr.utilities.Toast;
import tasklr.authentication.UserSession;

public class FlashcardUIRefresher {
    private static FlashcardUIRefresher instance;
    private JPanel quizContainer;
    private JPanel mainCardPanel;
    private CardLayout cardLayout;
    private JScrollPane scrollPane;

    private FlashcardUIRefresher(JPanel quizContainer, JPanel mainCardPanel, 
                                CardLayout cardLayout, JScrollPane scrollPane) {
        this.quizContainer = quizContainer;
        this.mainCardPanel = mainCardPanel;
        this.cardLayout = cardLayout;
        this.scrollPane = scrollPane;
    }

    public static void initialize(JPanel quizContainer, JPanel mainCardPanel, 
                                CardLayout cardLayout, JScrollPane scrollPane) {
        instance = new FlashcardUIRefresher(quizContainer, mainCardPanel, cardLayout, scrollPane);
    }

    private static FlashcardUIRefresher getInstance() {
        if (instance == null) {
            // Instead of throwing an exception, create a dummy instance that does nothing
            return new FlashcardUIRefresher(null, null, null, null);
        }
        return instance;
    }

    public static void refreshListContainer() {
        getInstance().refreshListContainerImpl();
    }

    public static void refreshFlashcardMode(int setId) {
        getInstance().refreshFlashcardModeImpl(setId);
    }

    private void refreshListContainerImpl() {
        if (quizContainer == null) return; // Silently return if not initialized
        quizContainer.removeAll();
        
        try {
            String query = "SELECT set_id, subject, description FROM flashcard_sets WHERE user_id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, UserSession.getUserId());
                ResultSet rs = stmt.executeQuery();
                
                boolean hasItems = false;
                while (rs.next()) {
                    hasItems = true;
                    int setId = rs.getInt("set_id");
                    String subject = rs.getString("subject");
                    String description = rs.getString("description");
                    
                    JPanel setPanel = createSetItemPanel(setId, subject, description);
                    quizContainer.add(setPanel);
                    quizContainer.add(Box.createVerticalStrut(10));
                }

                if (!hasItems) {
                    addNoSetsLabel();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error loading sets: " + ex.getMessage());
        }
        
        refreshUIComponents();
    }

    private void refreshFlashcardModeImpl(int setId) {
        if (mainCardPanel == null) return; // Silently return if not initialized

        // Remove existing flashcard mode panel if it exists
        Component[] components = mainCardPanel.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals("flashcardMode")) {
                mainCardPanel.remove(comp);
                break;
            }
        }

        // Create and add new flashcard mode panel
        JPanel flashcardModePanel = createFlashcardModePanel(setId);
        flashcardModePanel.setName("flashcardMode");
        mainCardPanel.add(flashcardModePanel, "flashcardMode");
        
        refreshUIComponents();
    }

    private void refreshUIComponents() {
        if (quizContainer != null) {
            quizContainer.revalidate();
            quizContainer.repaint();
        }
        
        if (scrollPane != null) {
            scrollPane.getViewport().revalidate();
            scrollPane.getViewport().repaint();
        }
        
        if (mainCardPanel != null) {
            mainCardPanel.revalidate();
            mainCardPanel.repaint();
        }
    }

    private void addNoSetsLabel() {
        JLabel noSetsLabel = new JLabel("No flashcard sets yet. Create one!", SwingConstants.CENTER);
        noSetsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        noSetsLabel.setForeground(new Color(0x707070));
        noSetsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quizContainer.add(noSetsLabel);
    }

    private JPanel createSetItemPanel(int setId, String subject, String description) {
        // Implement according to your UI design
        JPanel panel = new JPanel(new BorderLayout());
        // Add your panel implementation here
        return panel;
    }

    private JPanel createFlashcardModePanel(int setId) {
        // Implement according to your UI design
        JPanel panel = new JPanel(new BorderLayout());
        // Add your panel implementation here
        return panel;
    }
}

