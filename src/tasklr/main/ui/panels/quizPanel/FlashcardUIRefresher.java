package tasklr.main.ui.panels.quizPanel;

import tasklr.authentication.UserSession;
import tasklr.utilities.DatabaseManager;
import tasklr.utilities.Toast;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlashcardUIRefresher {
    private final JPanel quizContainer;
    private final JPanel mainCardPanel;
    private final CardLayout cardLayout;
    private final JScrollPane scrollPane;

    public FlashcardUIRefresher(JPanel quizContainer, JPanel mainCardPanel, 
                               CardLayout cardLayout, JScrollPane scrollPane) {
        this.quizContainer = quizContainer;
        this.mainCardPanel = mainCardPanel;
        this.cardLayout = cardLayout;
        this.scrollPane = scrollPane;
    }

    public void refreshListContainer() {
        if (quizContainer == null) return;
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

    public void refreshFlashcardMode(int setId) {
        if (mainCardPanel == null) return;

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

    private JPanel createFlashcardModePanel(int setId) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        try {
            String query = "SELECT term, definition FROM flashcards WHERE set_id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, setId);
                ResultSet rs = stmt.executeQuery();

                JPanel cardsContainer = new JPanel();
                cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
                
                boolean hasCards = false;
                while (rs.next()) {
                    hasCards = true;
                    String term = rs.getString("term");
                    String definition = rs.getString("definition");
                    
                    JPanel cardPanel = createFlashcardPanel(term, definition);
                    cardsContainer.add(cardPanel);
                    cardsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
                }

                if (!hasCards) {
                    addNoCardsLabel(cardsContainer);
                }

                JScrollPane scrollPane = new JScrollPane(cardsContainer);
                scrollPane.setBorder(null);
                scrollPane.getVerticalScrollBar().setUnitIncrement(16);
                panel.add(scrollPane, BorderLayout.CENTER);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error loading flashcards: " + ex.getMessage());
        }

        return panel;
    }

    public void refreshUIComponents() {
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

    private void addNoCardsLabel(JPanel container) {
        JLabel noCardsLabel = new JLabel("No flashcards in this set yet!", SwingConstants.CENTER);
        noCardsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        noCardsLabel.setForeground(new Color(0x707070));
        noCardsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(noCardsLabel);
    }

    // These methods should be implemented according to your UI design
    private JPanel createSetItemPanel(int setId, String subject, String description) {
        // Implement according to your UI design
        return new JPanel(); // Placeholder
    }

    private JPanel createFlashcardPanel(String term, String definition) {
        // Implement according to your UI design
        return new JPanel(); // Placeholder
    }
}