package tasklr.main.ui.panels.quizPanel;

import javax.swing.*;
import javax.swing.border.Border;
import tasklr.utilities.*;
import tasklr.authentication.UserSession;
import java.awt.*;
import java.sql.*;

public class FlashcardPanel {
    private static JPanel quizContainer;
    private static JScrollPane scrollPane;
    private static CardLayout cardLayout;
    private static JPanel mainCardPanel;
    private static JPanel inputPanel;
    private static final Color TEXT_COLOR = new Color(0x242424);
    private static final Color BACKGROUND_COLOR = new Color(0xFFFFFF);
    private static final Color TEXTFIELD_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_CONTAINER_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_ITEM_COLOR = new Color(0xFBFBFC);
    private static final Color LIST_ITEM_HOVER_BG = new Color(0xE8EAED);
    private static final Color LIST_ITEM_HOVER_BORDER = new Color(0x0082FC);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(0x275CE2);
    private static final Color PRIMARY_BUTTON_HOVER = new Color(0x3B6FF0);
    private static final Color PRIMARY_BUTTON_TEXT = Color.WHITE;
    
    public static JPanel createFlashcardPanel() {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(100, 100));
        Border panelBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        panel.setBorder(panelBorder);

        // Create content panel to hold input and list
        JPanel contentPanel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), null);
        
        // Create card layout panel
        mainCardPanel = new JPanel();
        cardLayout = new CardLayout();
        mainCardPanel.setLayout(cardLayout);
        
        // Create input panel and flashcard mode panel
        inputPanel = createFlashcardInputPanel();
        JPanel flashcardModePanel = createFlashcardModePanel();
        
        // Add both panels to card layout
        mainCardPanel.add(inputPanel, "input");
        mainCardPanel.add(flashcardModePanel, "flashcardMode");
        
        JPanel listContainer = createListContainer();
        
        contentPanel.add(mainCardPanel, BorderLayout.CENTER);
        contentPanel.add(listContainer, BorderLayout.WEST);

        // Add both panels to main panel
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel createFlashcardInputPanel() {
        JPanel inputPanel = createPanel.panel(Color.WHITE, new GridBagLayout(), new Dimension(0, 0));
        
        // Remove the username greeting and directly start with the paragraph
        JLabel paragraph = new JLabel("CREATE FLASHCARDS");
        paragraph.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        paragraph.setForeground(new Color(0x1d1d1d));
        
        // Term field
        JLabel termLabel = new JLabel("Term");
        termLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JTextField termField = new JTextField(20);
        termField.setPreferredSize(new Dimension(700, 40));
        
        JPanel termComponent = createPanel.panel(new Color(0xD9D9D9), new BorderLayout(), new Dimension(700, 40));
        termComponent.add(termField, BorderLayout.CENTER);
        
        // Definition textarea
        JLabel definitionLabel = new JLabel("Definition");
        definitionLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JTextArea definitionArea = new JTextArea();
        definitionArea.setLineWrap(true);
        definitionArea.setWrapStyleWord(true);
        JScrollPane definitionScroll = new JScrollPane(definitionArea);
        definitionScroll.setPreferredSize(new Dimension(700, 150));
        
        JButton addFlashcardBtn = createButton.button("Add Flashcard", null, Color.WHITE, null, false);
        addFlashcardBtn.setBackground(new Color(0x0065D9));
        addFlashcardBtn.setPreferredSize(new Dimension(120, 40));
        
        // Add button panel
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.RIGHT), new Dimension(700, 50));
        buttonPanel.add(addFlashcardBtn);

        // Add action listener
        addFlashcardBtn.addActionListener(e -> {
            String term = termField.getText().trim();
            String definition = definitionArea.getText().trim();
            
            if (term.isEmpty() || definition.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both term and definition!");
                return;
            }
            
            if (insertQuiz(term, definition)) {
                termField.setText("");
                definitionArea.setText("");
                refreshQuizContainer();
            }
        });

        // Add components using ComponentUtil
        ComponentUtil.addComponent(inputPanel, paragraph, 0, 0, 2, 1, new Insets(10, 10, 20, 10), 0);
        ComponentUtil.addComponent(inputPanel, termLabel, 0, 1, 1, 1, new Insets(5, 10, 5, 5), 0);
        ComponentUtil.addComponent(inputPanel, termComponent, 0, 2, 2, 1, new Insets(0, 10, 20, 5), 0);
        ComponentUtil.addComponent(inputPanel, definitionLabel, 0, 3, 1, 1, new Insets(5, 10, 5, 5), 0);
        ComponentUtil.addComponent(inputPanel, definitionScroll, 0, 4, 2, 1, new Insets(0, 10, 10, 5), 0);
        ComponentUtil.addComponent(inputPanel, buttonPanel, 0, 5, 2, 1, new Insets(0, 10, 10, 5), 0);
        return inputPanel;
    }

    private static boolean insertQuiz(String term, String definition) {
        try {
            DatabaseManager.executeUpdate(
                "INSERT INTO quizzes (user_id, term, definition) VALUES (?, ?, ?)",
                UserSession.getUserId(), term, definition
            );
            showCenteredOptionPane(null, "Quiz added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshQuizContainer();
            refreshFlashcardMode();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            showCenteredOptionPane(null, "Error adding quiz: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static void refreshQuizContainer() {
        if (quizContainer == null) return;
        quizContainer.removeAll();
        
        try {
            ResultSet rs = DatabaseManager.executeQuery(
                "SELECT term, definition FROM quizzes WHERE user_id = ?",
                UserSession.getUserId()
            );
            
            boolean hasItems = false;
            while (rs.next()) {
                hasItems = true;
                String term = rs.getString("term");
                String definition = rs.getString("definition");
                
                JPanel quizPanel = createQuizItemPanel(term, definition);
                quizContainer.add(quizPanel);
                quizContainer.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            
            if (!hasItems) {
                JLabel noItemsLabel = new JLabel("No flashcards yet. Create one!");
                noItemsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
                noItemsLabel.setForeground(new Color(0x707070));
                noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                quizContainer.add(noItemsLabel);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching flashcards: " + ex.getMessage());
        }

        quizContainer.revalidate();
        quizContainer.repaint();
        
        if (scrollPane != null) {
            scrollPane.getViewport().revalidate();
            scrollPane.getViewport().repaint();
        }
    }

    public static JPanel createListContainer() {
        // Main panel with fixed width - increased from 400 to 600
        JPanel mainPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), new Dimension(600, 0));

        // Configure quiz container with BoxLayout (Y_AXIS)
        quizContainer = createPanel.panel(LIST_CONTAINER_COLOR, null, null);
        quizContainer.setLayout(new BoxLayout(quizContainer, BoxLayout.Y_AXIS));
        quizContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), null);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("My Flashcards");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        
        JButton viewButton = createButton.button("View All", PRIMARY_BUTTON_COLOR, PRIMARY_BUTTON_TEXT, null, false);
        viewButton.setPreferredSize(new Dimension(80, 35));
        new HoverButtonEffect(viewButton, PRIMARY_BUTTON_COLOR, PRIMARY_BUTTON_HOVER, PRIMARY_BUTTON_TEXT, PRIMARY_BUTTON_TEXT);
        viewButton.addActionListener(e -> cardLayout.show(mainCardPanel, "flashcardMode"));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(viewButton, BorderLayout.EAST);

        // Add initial flashcards
        refreshQuizContainer();

        // Create a wrapper panel to properly contain the quiz container
        JPanel wrapperPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), null);
        wrapperPanel.add(quizContainer, BorderLayout.NORTH);
        
        // Add filler panel to push content to top
        JPanel fillerPanel = createPanel.panel(LIST_CONTAINER_COLOR, null, null);
        wrapperPanel.add(fillerPanel, BorderLayout.CENTER);

        // Configure ScrollPane
        scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LIST_CONTAINER_COLOR);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private static JPanel createQuizItemPanel(String term, String definition) {
        // Main panel with fixed height and flexible width
        JPanel panel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), new Dimension(0, 60));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // This ensures proper width scaling
        
        // Inner panel for consistent padding
        JPanel contentPanel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Text panel for term and definition
        JPanel textPanel = createPanel.panel(LIST_ITEM_COLOR, new GridLayout(2, 1, 2, 2), null);
        
        JLabel termLabel = new JLabel(term);
        termLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        termLabel.setForeground(TEXT_COLOR);
        
        JLabel definitionLabel = new JLabel(definition);
        definitionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        definitionLabel.setForeground(new Color(0x666666));
        
        textPanel.add(termLabel);
        textPanel.add(definitionLabel);
        
        // Button panel
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.RIGHT, 5, 0), null);
        
        // Edit button
        JButton editBtn = createButton.button("Edit", new Color(0xE9E9E9), new Color(0x242424), null, false);
        editBtn.setPreferredSize(new Dimension(70, 40));
        new HoverButtonEffect(editBtn, 
            new Color(0xE9E9E9), // default background
            new Color(0xBFBFBF), // hover background
            new Color(0x242424), // default text
            Color.WHITE         // hover text
        );

        // Delete button
        JButton deleteBtn = createButton.button("Delete", new Color(0xFB2C36), Color.WHITE, null, false);
        deleteBtn.setPreferredSize(new Dimension(70, 40));
        new HoverButtonEffect(deleteBtn, 
            new Color(0xFB2C36),  // default background
            new Color(0xFF6467),  // hover background
            Color.WHITE,          // default text
            Color.WHITE          // hover text
        );

        editBtn.addActionListener(e -> {
            JTextField termField = new JTextField(term);
            JTextField definitionField = new JTextField(definition);
            
            JPanel editPanel = new JPanel(new GridLayout(4, 1));  // Changed from 'panel' to 'editPanel'
            editPanel.add(new JLabel("Edit term:"));
            editPanel.add(termField);
            editPanel.add(new JLabel("Edit definition:"));
            editPanel.add(definitionField);
            
            JOptionPane pane = new JOptionPane(editPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = pane.createDialog(null, "Edit Flashcard");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            
            Object result = pane.getValue();
            if (result != null && (Integer) result == JOptionPane.OK_OPTION) {
                String newTerm = termField.getText().trim();
                String newDefinition = definitionField.getText().trim();
                
                if (!newTerm.isEmpty() && !newDefinition.isEmpty()) {
                    updateFlashcard(term, newTerm, newDefinition);
                }
            }
        });
        
        deleteBtn.addActionListener(e -> {
            JOptionPane pane = new JOptionPane(
                "Are you sure you want to delete this flashcard?",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION
            );
            JDialog dialog = pane.createDialog(null, "Confirm Delete");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            
            Object result = pane.getValue();
            if (result != null && (Integer) result == JOptionPane.YES_OPTION) {
                deleteFlashcard(term);
            }
        });

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        contentPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Add hover effect
        new HoverPanelEffect(panel, LIST_ITEM_COLOR, LIST_ITEM_HOVER_BG);

        return panel;
    }
   
    private static JPanel createFlashcardModePanel() {
        JPanel mainContainer = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = createPanel.panel(null, new BorderLayout(), null);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Back button
        JButton backButton = createButton.button("Back to Input", null, Color.WHITE, null, false);
        backButton.setBackground(new Color(0x0065D9));
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> cardLayout.show(mainCardPanel, "input"));
        
        // Title
        JLabel titleLabel = new JLabel("Study Mode", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Cards container
        JPanel cardsContainer = createPanel.panel(Color.WHITE, null, null);
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        
        // Fetch and display flashcards
        try {
            ResultSet rs = DatabaseManager.executeQuery(
                "SELECT term, definition FROM quizzes WHERE user_id = ? ORDER BY id DESC",
                UserSession.getUserId()
            );
            
            boolean hasCards = false;
            while (rs.next()) {
                hasCards = true;
                String term = rs.getString("term");
                String definition = rs.getString("definition");
                
                // Create flashcard panel
                JPanel flashcard = createFlashcardItem(term, definition);
                cardsContainer.add(flashcard);
                cardsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
            if (!hasCards) {
                JLabel noCardsLabel = new JLabel("No flashcards available. Create some first!", SwingConstants.CENTER);
                noCardsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
                noCardsLabel.setForeground(new Color(0x707070));
                noCardsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                cardsContainer.add(noCardsLabel);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading flashcards: " + ex.getMessage());
        }
        
        // Scroll pane for cards
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add components to main container
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        
        return mainContainer;
    }

    // Helper method to create individual flashcard items
    private static JPanel createFlashcardItem(String term, String definition) {
        JPanel panel = createPanel.panel(new Color(0xF5F5F5), new BorderLayout(), null);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Term panel
        JPanel termPanel = createPanel.panel(null, new BorderLayout(), null);
        JLabel termLabel = new JLabel(term);
        termLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        termPanel.add(termLabel);
        
        // Definition panel
        JTextArea definitionArea = new JTextArea(definition);
        definitionArea.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        definitionArea.setLineWrap(true);
        definitionArea.setWrapStyleWord(true);
        definitionArea.setEditable(false);
        definitionArea.setBackground(panel.getBackground());
        
        JScrollPane definitionScroll = new JScrollPane(definitionArea);
        definitionScroll.setBorder(null);
        definitionScroll.setBackground(panel.getBackground());
        
        // Add components
        panel.add(termPanel, BorderLayout.NORTH);
        panel.add(definitionScroll, BorderLayout.CENTER);
        
        // Set preferred size
        panel.setPreferredSize(new Dimension(600, 150));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        return panel;
    }

    // Add this method to refresh the view mode
    public static void refreshFlashcardMode() {
        if (mainCardPanel != null) {
            mainCardPanel.remove(mainCardPanel.getComponent(1)); // Remove old flashcard mode panel
            mainCardPanel.add(createFlashcardModePanel(), "flashcardMode"); // Add new one
            mainCardPanel.revalidate();
            mainCardPanel.repaint();
        }
    }

    private static void updateFlashcard(String oldTerm, String newTerm, String newDefinition) {
        try {
            DatabaseManager.executeUpdate(
                "UPDATE quizzes SET term = ?, definition = ? WHERE term = ? AND user_id = ?",
                newTerm, newDefinition, oldTerm, UserSession.getUserId()
            );
            showCenteredOptionPane(null, "Flashcard updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshQuizContainer();
            refreshFlashcardMode();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showCenteredOptionPane(null, "Error updating flashcard: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void deleteFlashcard(String term) {
        try {
            DatabaseManager.executeUpdate(
                "DELETE FROM quizzes WHERE term = ? AND user_id = ?",
                term, UserSession.getUserId()
            );
            showCenteredOptionPane(null, "Flashcard deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshQuizContainer();
            refreshFlashcardMode();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showCenteredOptionPane(null, "Error deleting flashcard: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showCenteredOptionPane(Component parentComponent, String message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
