package tasklr.main.ui.panels.quizPanel;


import javax.swing.*;
import javax.swing.border.Border;
import tasklr.utilities.*;
import tasklr.authentication.UserSession;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

public class FlashcardPanel {
    private static JPanel quizContainer;
    private static JScrollPane scrollPane;
    private static CardLayout cardLayout;
    private static JPanel mainCardPanel;
    // private static JPanel inputPanel;
    private static final Color TEXT_COLOR = new Color(0x242424);
    private static final Color BACKGROUND_COLOR = new Color(0xFFFFFF);
    // private static final Color TEXTFIELD_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_CONTAINER_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_ITEM_COLOR = new Color(0xFBFBFC);
    private static final Color LIST_ITEM_HOVER_BG = new Color(0xE8EAED);
    private static final Color LIST_ITEM_HOVER_BORDER = new Color(0x0082FC);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(0x275CE2);
    private static final Color PRIMARY_BUTTON_HOVER = new Color(0x3B6FF0);
    private static final Color PRIMARY_BUTTON_TEXT = Color.WHITE;
    private static int currentSetId = -1; // Track current set ID
    
    // Temporary storage for terms before set creation
    private static List<Map<String, String>> temporaryTerms = new ArrayList<>();
    
    public static JPanel createFlashcardPanel() {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(100, 100));
        Border panelBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        panel.setBorder(panelBorder);

        JPanel contentPanel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), null);
        
        mainCardPanel = new JPanel();
        cardLayout = new CardLayout();
        mainCardPanel.setLayout(cardLayout);
        
        // Create three panels: set creation, terms input, and flashcard mode
        JPanel setCreationPanel = createSetCreationPanel();
        JPanel termsInputPanel = createTermsInputPanel();
        JPanel flashcardModePanel = createFlashcardModePanel(-1); // Pass -1 for initial state
        
        mainCardPanel.add(setCreationPanel, "setCreation");
        mainCardPanel.add(termsInputPanel, "termsInput");
        mainCardPanel.add(flashcardModePanel, "flashcardMode");
        
        JPanel listContainer = createListContainer();
        
        contentPanel.add(mainCardPanel, BorderLayout.CENTER);
        contentPanel.add(listContainer, BorderLayout.WEST);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private static JPanel createSetCreationPanel() {
        JPanel panel = createPanel.panel(Color.WHITE, new GridBagLayout(), new Dimension(0, 0));
        
        JLabel titleLabel = new JLabel("CREATE FLASHCARD SET");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0x1d1d1d));
        
        JLabel subjectLabel = new JLabel("Subject Title");
        subjectLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JTextField subjectField = new JTextField(20);
        subjectField.setPreferredSize(new Dimension(700, 40));
        
        JLabel descriptionLabel = new JLabel("Description (Optional)");
        descriptionLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(700, 60));
        
        JButton createSetBtn = createButton.button("Create Set", null, Color.WHITE, null, false);
        createSetBtn.setBackground(new Color(0x0065D9));
        createSetBtn.setPreferredSize(new Dimension(100, 40));
        createSetBtn.addActionListener(e -> {
            String subject = subjectField.getText().trim();
            String description = descriptionArea.getText().trim();
            
            if (subject.isEmpty()) {
                Toast.error("Please enter a subject title!");
                return;
            }           
            if (createNewSet(subject, description)) {
                subjectField.setText("");
                descriptionArea.setText("");
                cardLayout.show(mainCardPanel, "termsInput");
                refreshListContainer(); // Refresh to show new set
            }
        });

        JPanel spacer = createPanel.panel(BACKGROUND_COLOR, null, new Dimension(0, 200));

        // Add components using ComponentUtil
        ComponentUtil.addComponent(panel, titleLabel, 0, 0, 2, 1, new Insets(10, 10, 20, 10), 0);
        ComponentUtil.addComponent(panel, subjectLabel, 0, 1, 1, 1, new Insets(5, 10, 5, 5), 0);
        ComponentUtil.addComponent(panel, subjectField, 0, 2, 2, 1, new Insets(0, 10, 20, 5), 0);
        ComponentUtil.addComponent(panel, descriptionLabel, 0, 3, 1, 1, new Insets(5, 10, 5, 5), 0);
        ComponentUtil.addComponent(panel, descriptionScroll, 0, 4, 2, 1, new Insets(0, 10, 20, 5), 0);
        ComponentUtil.addComponent(panel, createSetBtn, 0, 5, 2, 1, new Insets(0, 10, 10, 5), 0);
        ComponentUtil.addComponent(panel, spacer, 0, 6, 2, 1, new Insets(0, 10, 10, 5), 0);
        
        return panel;
    }

    private static JPanel createTermsInputPanel() {
        JPanel panel = createPanel.panel(Color.WHITE, new GridBagLayout(), new Dimension(0, 0));
        
        // Create header panel
        JPanel headerPanel = createPanel.panel(Color.WHITE, new BorderLayout(), new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("ADD TERMS TO SET");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        // Create Save button in header
        JButton saveBtn = createButton.button("Save", null, Color.WHITE, null, false);
        saveBtn.setBackground(new Color(0x0065D9));
        saveBtn.setPreferredSize(new Dimension(100, 40));
        
        // Add components to header
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(saveBtn, BorderLayout.EAST);
        
        JLabel termLabel = new JLabel("Term");
        termLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JTextField termField = new JTextField(20);
        termField.setPreferredSize(new Dimension(700, 40));
        
        JLabel definitionLabel = new JLabel("Definition");
        definitionLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        JTextArea definitionArea = new JTextArea();
        definitionArea.setLineWrap(true);
        definitionArea.setWrapStyleWord(true);
        JScrollPane definitionScroll = new JScrollPane(definitionArea);
        definitionScroll.setPreferredSize(new Dimension(700, 150));
        
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.RIGHT), new Dimension(700, 50));
        
        JButton addTermBtn = createButton.button("Add Term", null, Color.WHITE, null, false);
        addTermBtn.setBackground(new Color(0x275CE2));
        addTermBtn.setPreferredSize(new Dimension(120, 40));
        
        addTermBtn.addActionListener(e -> {
            String term = termField.getText().trim();
            String definition = definitionArea.getText().trim();
            
            if (term.isEmpty() || definition.isEmpty()) {
                Toast.error("Please enter both term and definition!");
                return;
            }
            
            if (addTermToSet(term, definition)) {
                termField.setText("");
                definitionArea.setText("");
                Toast.success("Term added successfully!");
            }
        });
        
        // Maintain the same functionality as the original "Done" button
        saveBtn.addActionListener(e -> {
            cardLayout.show(mainCardPanel, "setCreation");
            currentSetId = -1; // Reset current set
        });
        
        buttonPanel.add(addTermBtn);
        
        // Add components using ComponentUtil
        ComponentUtil.addComponent(panel, headerPanel, 0, 0, 2, 1, new Insets(0, 0, 20, 0), 0);
        ComponentUtil.addComponent(panel, termLabel, 0, 1, 1, 1, new Insets(5, 10, 5, 5), 0);
        ComponentUtil.addComponent(panel, termField, 0, 2, 2, 1, new Insets(0, 10, 20, 5), 0);
        ComponentUtil.addComponent(panel, definitionLabel, 0, 3, 1, 1, new Insets(5, 10, 5, 5), 0);
        ComponentUtil.addComponent(panel, definitionScroll, 0, 4, 2, 1, new Insets(0, 10, 20, 5), 0);
        ComponentUtil.addComponent(panel, buttonPanel, 0, 5, 2, 1, new Insets(0, 10, 10, 5), 0);
        
        return panel;
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
        JPanel mainPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), new Dimension(600, 0));
        
        quizContainer = createPanel.panel(LIST_CONTAINER_COLOR, null, null);
        quizContainer.setLayout(new BoxLayout(quizContainer, BoxLayout.Y_AXIS));
        quizContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        refreshListContainer();
        
        JScrollPane scrollPane = new JScrollPane(quizContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        // Make vertical scrollbar always visible
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        // Keep horizontal scrollbar as never visible
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private static void refreshListContainer() {
        if (quizContainer == null) return;
        quizContainer.removeAll();
        
        try {
            String query = "SELECT set_id, subject, description FROM flashcard_sets WHERE user_id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, UserSession.getUserId());
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    int setId = rs.getInt("set_id");
                    String subject = rs.getString("subject");
                    String description = rs.getString("description");
                    
                    JPanel setPanel = createSetItemPanel(setId, subject, description);
                    quizContainer.add(setPanel);
                    quizContainer.add(Box.createVerticalStrut(10));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error loading sets: " + ex.getMessage());
        }
        
        quizContainer.revalidate();
        quizContainer.repaint();
    }

    private static JPanel createSetItemPanel(int setId, String subject, String description) {
        JPanel panel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), new Dimension(0, 80));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JPanel contentPanel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JPanel textPanel = createPanel.panel(LIST_ITEM_COLOR, new GridLayout(2, 1, 2, 2), null);
        
        JLabel subjectLabel = new JLabel(subject);
        subjectLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        subjectLabel.setForeground(TEXT_COLOR);
        
        JLabel descLabel = new JLabel(description != null ? description : "No description");
        descLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        descLabel.setForeground(new Color(0x666666));
        
        textPanel.add(subjectLabel);
        textPanel.add(descLabel);

        // Button panel for more button
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.RIGHT, 5, 0), null);
        
        // More button with icon
        JButton moreBtn = new JButton();
        try {
            ImageIcon moreIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\moreIconBlack.png");
            Image scaledImage = moreIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            moreBtn.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Failed to load more icon: " + e.getMessage());
        }
        moreBtn.setBorderPainted(false);
        moreBtn.setContentAreaFilled(false);
        moreBtn.setFocusPainted(false);
        moreBtn.setPreferredSize(new Dimension(40, 40));

        // Create popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        
        // Edit menu item with icon
        JMenuItem editItem = new JMenuItem();
        try {
            ImageIcon editIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\editIcon.png");
            Image scaledEditImage = editIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            editItem.setIcon(new ImageIcon(scaledEditImage));
        } catch (Exception e) {
            System.err.println("Failed to load edit icon: " + e.getMessage());
        }
        editItem.setText("Edit");
        
        // Delete menu item with icon
        JMenuItem deleteItem = new JMenuItem();
        try {
            ImageIcon deleteIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\deleteIcon.png");
            Image scaledDeleteImage = deleteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            deleteItem.setIcon(new ImageIcon(scaledDeleteImage));
        } catch (Exception e) {
            System.err.println("Failed to load delete icon: " + e.getMessage());
        }
        deleteItem.setText("Delete");

        // Add items to popup menu
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        // Add action listeners
        moreBtn.addActionListener(e -> {
            popupMenu.show(moreBtn, 0, moreBtn.getHeight());
        });

        editItem.addActionListener(e -> {
            String newTitle = JOptionPane.showInputDialog(
                SwingUtilities.getWindowAncestor(moreBtn),
                "Edit set:",
                subject
            );
            if (newTitle != null && !newTitle.trim().isEmpty()) {
                try {
                    String query = "UPDATE flashcard_sets SET subject = ? WHERE subject = ? AND user_id = ?";
                    DatabaseManager.executeUpdate(query, 
                        newTitle.trim(), 
                        subject, 
                        UserSession.getUserId()
                    );
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(moreBtn),
                        "Set updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    refreshListContainer();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(moreBtn),
                        "Error updating set: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        deleteItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(moreBtn),
                "Are you sure you want to delete this set?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM flashcard_sets WHERE subject = ? AND user_id = ?";
                    DatabaseManager.executeUpdate(query, 
                        subject, 
                        UserSession.getUserId()
                    );
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(moreBtn),
                        "Set deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    refreshListContainer();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(moreBtn),
                        "Error deleting set: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        buttonPanel.add(moreBtn);
        
        contentPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        new HoverPanelEffect(panel, LIST_ITEM_COLOR, LIST_ITEM_HOVER_BG);
        
        // Add click listener
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showFlashcardMode(setId);
            }
        });
        
        return panel;
    }

    private static void showFlashcardMode(int setId) {
        // Update flashcard mode panel with the selected set's cards
        JPanel flashcardModePanel = createFlashcardModePanel(setId);
        mainCardPanel.add(flashcardModePanel, "flashcardMode");
        cardLayout.show(mainCardPanel, "flashcardMode");
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
   
    private static JPanel createFlashcardModePanel(int setId) {
        JPanel mainContainer = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = createPanel.panel(null, new BorderLayout(), null);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Left side panel for buttons
        JPanel leftButtonsPanel = createPanel.panel(null, new FlowLayout(FlowLayout.LEFT, 10, 0), null);
        
        // Back button
        JButton backButton = createButton.button("Back to Sets", null, Color.WHITE, null, false);
        backButton.setBackground(new Color(0x0065D9));
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> cardLayout.show(mainCardPanel, "setCreation"));
        
        // Add More Terms button
        JButton addMoreTermsBtn = createButton.button("Add More Terms", null, Color.WHITE, null, false);
        addMoreTermsBtn.setBackground(new Color(0x275CE2));
        addMoreTermsBtn.setPreferredSize(new Dimension(120, 40));
        addMoreTermsBtn.addActionListener(e -> {
            currentSetId = setId; // Set the current set ID
            cardLayout.show(mainCardPanel, "termsInput");
        });
        
        leftButtonsPanel.add(backButton);
        leftButtonsPanel.add(addMoreTermsBtn);
        
        // Title
        JLabel titleLabel = new JLabel("Study Mode", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        
        headerPanel.add(leftButtonsPanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Cards container
        JPanel cardsContainer = createPanel.panel(Color.WHITE, null, null);
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        
        // Fetch and display flashcards for this set
        try {
            String query = "SELECT term, definition FROM flashcards WHERE set_id = ?";
            ResultSet rs = DatabaseManager.executeQuery(query, setId);
            
            boolean hasCards = false;
            while (rs.next()) {
                hasCards = true;
                String term = rs.getString("term");
                String definition = rs.getString("definition");
                
                JPanel flashcard = createFlashcardItem(term, definition);
                cardsContainer.add(flashcard);
                cardsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
            if (!hasCards) {
                JLabel noCardsLabel = new JLabel("No flashcards in this set yet!", SwingConstants.CENTER);
                noCardsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
                noCardsLabel.setForeground(new Color(0x707070));
                noCardsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                cardsContainer.add(noCardsLabel);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error loading flashcards: " + ex.getMessage());
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
            mainCardPanel.add(createFlashcardModePanel(currentSetId), "flashcardMode"); // Pass currentSetId
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
            Toast.success("Flashcard updated successfully!");
            refreshQuizContainer();
            refreshFlashcardMode();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error updating flashcard: " + ex.getMessage());
        }
    }

    private static void deleteFlashcard(String term) {
        try {
            DatabaseManager.executeUpdate(
                "DELETE FROM quizzes WHERE term = ? AND user_id = ?",
                term, UserSession.getUserId()
            );
            Toast.success("Flashcard deleted successfully!");
            refreshQuizContainer();
            refreshFlashcardMode();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error deleting flashcard: " + ex.getMessage());
        }
    }

    private static void showCenteredOptionPane(Component parentComponent, String message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static void addToTemporaryList(String subject, String term, String definition) {
        Map<String, String> termMap = new HashMap<>();
        termMap.put("term", term);
        termMap.put("definition", definition);
        temporaryTerms.add(termMap);
    }
    
    private static void clearTemporaryList() {
        temporaryTerms.clear();
    }
    
    private static boolean createNewSet(String subject, String description) {
        try {
            // First create the set
            String setQuery = "INSERT INTO flashcard_sets (user_id, subject, description) VALUES (?, ?, ?)";
            int setId;
            
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(setQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, UserSession.getUserId());
                stmt.setString(2, subject);
                stmt.setString(3, description);
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        setId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to get generated set ID");
                    }
                }
            }
            
            // Then add all terms from temporary storage
            String cardQuery = "INSERT INTO flashcards (set_id, term, definition) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(cardQuery)) {
                for (Map<String, String> term : temporaryTerms) {
                    stmt.setInt(1, setId);
                    stmt.setString(2, term.get("term"));
                    stmt.setString(3, term.get("definition"));
                    stmt.executeUpdate();
                }
            }
            
            // Clear temporary storage after successful creation
            temporaryTerms.clear();
            currentSetId = setId;  // Set the current set ID
            
            Toast.success("Flashcard set created successfully!");
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error creating flashcard set: " + ex.getMessage());
            return false;
        }
    }

    private static boolean addTermToSet(String term, String definition) {
        if (currentSetId == -1) {
            // If no set is selected, add to temporary storage
            Map<String, String> termMap = new HashMap<>();
            termMap.put("term", term);
            termMap.put("definition", definition);
            temporaryTerms.add(termMap);
            return true;
        } else {
            // If a set is selected, add directly to database
            try {
                String query = "INSERT INTO flashcards (set_id, term, definition) VALUES (?, ?, ?)";
                DatabaseManager.executeUpdate(query, currentSetId, term, definition);
                return true;
            } catch (SQLException ex) {
                ex.printStackTrace();
                Toast.error("Error adding term: " + ex.getMessage());
                return false;
            }
        }
    }
}
