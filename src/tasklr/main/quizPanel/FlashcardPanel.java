package tasklr.main.quizPanel;

import javax.swing.*;
import javax.swing.border.Border;
import tasklr.authentication.UserSession;
import tasklr.utilities.ComponentUtil;
import tasklr.utilities.createButton;
import tasklr.utilities.createPanel;
import java.awt.*;
import java.sql.*;

public class FlashcardPanel {
    private static final String url = "jdbc:mysql://localhost:3306/tasklrdb";
    private static final String dbUser = "JFCompany";
    private static final String dbPass = "";
    private static JPanel quizContainer;
    private static JScrollPane scrollPane;
    private static CardLayout cardLayout;
    private static JPanel mainCardPanel;
    private static JPanel inputPanel;
    
    public static JPanel createFlashcardPanel(String username) {
        JPanel panel = createPanel.panel(null, new BorderLayout(), new Dimension(100, 100));
        
        // Create card layout panel
        mainCardPanel = new JPanel();
        cardLayout = new CardLayout();
        mainCardPanel.setLayout(cardLayout);
        
        // Create input panel and store it in field
        inputPanel = createFlashcardInputPanel(username);
        JPanel flashcardModePanel = createFlashcardModePanel();
        
        // Add both panels to card layout
        mainCardPanel.add(inputPanel, "input");
        mainCardPanel.add(flashcardModePanel, "flashcardMode");
        
        JPanel listContainer = createListContainer();

        panel.add(mainCardPanel, BorderLayout.CENTER);
        panel.add(listContainer, BorderLayout.WEST);
        return panel;
    }

    private static JPanel createFlashcardInputPanel(String username) {
        JPanel inputPanel = createPanel.panel(null, new GridBagLayout(), new Dimension(0, 0));
        
        // Welcome message
        JLabel usernameLabel = new JLabel("Hello " + username + "!");
        usernameLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 80));
        
        JLabel paragraph = new JLabel("Create flashcards to help you study and learn.");
        paragraph.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        paragraph.setForeground(new Color(0x707070));
        
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
        ComponentUtil.addComponent(inputPanel, usernameLabel, 0, 0, 2, 1, new Insets(10, 10, 10, 5), 0);
        ComponentUtil.addComponent(inputPanel, paragraph, 0, 1, 2, 1, new Insets(10, 10, 20, 10), 0);
        ComponentUtil.addComponent(inputPanel, termLabel, 0, 2, 1, 1, new Insets(5, 10, 5, 5), 0);
        ComponentUtil.addComponent(inputPanel, termComponent, 0, 3, 2, 1, new Insets(0, 10, 20, 5), 0);
        ComponentUtil.addComponent(inputPanel, definitionLabel, 0, 4, 1, 1, new Insets(5, 10, 5, 5), 0);
        ComponentUtil.addComponent(inputPanel, definitionScroll, 0, 5, 2, 1, new Insets(0, 10, 10, 5), 0);
        ComponentUtil.addComponent(inputPanel, buttonPanel, 0, 6, 2, 1, new Insets(0, 10, 10, 5), 0);
        return inputPanel;
    }

    private static boolean insertQuiz(String term, String definition) {
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String query = "INSERT INTO quizzes (user_id, term, definition) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, UserSession.getUserId());
                stmt.setString(2, term);
                stmt.setString(3, definition);
                
                int result = stmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Quiz added successfully!");
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding quiz: " + ex.getMessage());
        }
        return false;
    }

    private static void refreshQuizContainer() {
        if (quizContainer == null) return;
        quizContainer.removeAll();
        
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String query = "SELECT term, definition FROM quizzes WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, UserSession.getUserId());
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean hasItems = false;
                    
                    while (rs.next()) {
                        hasItems = true;
                        String term = rs.getString("term");
                        String definition = rs.getString("definition");
                        
                        // Create and add the quiz panel
                        JPanel quizPanel = createQuizItemPanel(term, definition);
                        quizContainer.add(quizPanel);
                        quizContainer.add(Box.createRigidArea(new Dimension(0, 5)));
                    }
                    
                    // Show message if no items found
                    if (!hasItems) {
                        JLabel noItemsLabel = new JLabel("No flashcards yet. Create one!");
                        noItemsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
                        noItemsLabel.setForeground(new Color(0x707070));
                        noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        quizContainer.add(noItemsLabel);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching flashcards: " + ex.getMessage());
        }

        // Ensure proper repainting
        quizContainer.revalidate();
        quizContainer.repaint();
        
        if (scrollPane != null) {
            scrollPane.getViewport().revalidate();
            scrollPane.getViewport().repaint();
        }
    }

    public static JPanel createListContainer() {
        // Create main panel with fixed width
        JPanel mainPanel = createPanel.panel(null, new BorderLayout(), new Dimension(400, 0));
        mainPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, new Color(0x749AAD)));

        // Create title panel with FlowLayout to accommodate both title and button
        JPanel titlePanel = createPanel.panel(null, new BorderLayout(), null);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create a container for the title and view button
        JPanel headerContainer = createPanel.panel(null, new BorderLayout(), null);
        
        // Add title label
        JLabel titleLabel = new JLabel("My Flashcards");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        headerContainer.add(titleLabel, BorderLayout.WEST);
        
        // Create and style view button
        JButton viewButton = createButton.button("View All", null, Color.WHITE, null, false);
        viewButton.setBackground(new Color(0x0065D9));
        viewButton.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        viewButton.setPreferredSize(new Dimension(80, 30));
        
        // Add action listener to view button
        viewButton.addActionListener(e -> cardLayout.show(mainCardPanel, "flashcardMode"));
        
        // Add button to header container
        headerContainer.add(viewButton, BorderLayout.EAST);
        
        // Add header container to title panel
        titlePanel.add(headerContainer, BorderLayout.CENTER);

        // Initialize quiz container
        quizContainer = createPanel.panel(Color.WHITE, null, null);
        quizContainer.setLayout(new BoxLayout(quizContainer, BoxLayout.Y_AXIS));
        quizContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create scroll pane
        scrollPane = new JScrollPane(quizContainer);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Add components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        refreshQuizContainer();

        return mainPanel;
    }

    private static JPanel createQuizItemPanel(String term, String definition) {
        // Create main panel with increased height
        JPanel panel = createPanel.panel(new Color(0xE0E3E2), new BorderLayout(), new Dimension(0, 80));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0x749AAD), 1));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Create content panel
        JPanel contentPanel = createPanel.panel(null, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        contentPanel.setOpaque(false);
        
        // Create text panel to hold both term and definition
        JPanel textPanel = createPanel.panel(null, new GridLayout(2, 1, 0, 2), null);
        textPanel.setOpaque(false);
        
        // Create term label
        JLabel termLabel = new JLabel(term);
        termLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        
        // Create definition label (shortened if too long)
        String shortDefinition = definition.length() > 50 ? definition.substring(0, 47) + "..." : definition;
        JLabel definitionLabel = new JLabel(shortDefinition);
        definitionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        definitionLabel.setForeground(new Color(0x666666));
        
        // Add labels to text panel
        textPanel.add(termLabel);
        textPanel.add(definitionLabel);
        
        // Create buttons panel
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.RIGHT, 5, 0), null);
        buttonPanel.setOpaque(false);
        
        // Create edit and delete buttons
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        editBtn.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        deleteBtn.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        
        // Add buttons to button panel
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        // Add action listeners (keeping your existing ones)
        editBtn.addActionListener(e -> {
            String newTerm = JOptionPane.showInputDialog(panel, "Edit term:", term);
            String newDefinition = JOptionPane.showInputDialog(panel, "Edit definition:", definition);
            
            if (newTerm != null && newDefinition != null && 
                !newTerm.trim().isEmpty() && !newDefinition.trim().isEmpty()) {
                updateFlashcard(term, newTerm.trim(), newDefinition.trim());
            }
        });
        
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                panel,
                "Are you sure you want to delete this flashcard?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                deleteFlashcard(term);
            }
        });
        
        // Add mouse listener for flashcard mode
        contentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(mainCardPanel, "flashcardMode");
            }
        });
        
        contentPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contentPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
   
    private static JPanel createFlashcardModePanel() {
        // Main container that will be scrollable
        JPanel mainContainer = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create back button panel (this will stay fixed at top)
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.LEFT), null);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton backButton = createButton.button("Back to Input", null, Color.WHITE, null, false);
        backButton.setBackground(new Color(0x0065D9));
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> cardLayout.show(mainCardPanel, "input"));
        buttonPanel.add(backButton);
        
        // Content panel that will hold title and cards
        JPanel contentPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Create title
        JLabel titleLabel = new JLabel("Study Mode", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        buttonPanel.add(titleLabel);
        // Create container for flashcards with BoxLayout
        JPanel cardsContainer = createPanel.panel(Color.WHITE, null, null);
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        
        // Load flashcards
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String query = "SELECT term, definition FROM quizzes WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, UserSession.getUserId());
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    JPanel cardPanel = createFlashcardModeItem(rs.getString("term"), rs.getString("definition"));
                    cardsContainer.add(cardPanel);
                    // Add spacing between cards
                    cardsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Add cards container to content panel
        contentPanel.add(cardsContainer, BorderLayout.CENTER);
        
        // Add content panel to main container
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        // Create the main scroll pane that will contain everything except the back button
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Final panel to hold both fixed button panel and scrollable content
        JPanel finalPanel = createPanel.panel(null, new BorderLayout(), new Dimension(100, 100));
        finalPanel.add(buttonPanel, BorderLayout.NORTH); // Fixed at top
        finalPanel.add(scrollPane, BorderLayout.CENTER); // Scrollable content
        
        return finalPanel;
    }
    
    private static JPanel createFlashcardModeItem(String term, String definition) {

        JPanel panel = createPanel.panel(null, new BorderLayout(), new Dimension(0, 150));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); 
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x749AAD), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Add hover effect
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(0xF5F5F5));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(0xFFFFFF));
            }
        });
        
        // Term panel
        JPanel termPanel = createPanel.panel(null, new FlowLayout(FlowLayout.LEFT), null);
        termPanel.setOpaque(false);
        
        JLabel termLabel = new JLabel(term);
        termLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        termPanel.add(termLabel);
    
        JTextArea definitionArea = new JTextArea(definition);
        definitionArea.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        definitionArea.setLineWrap(true);
        definitionArea.setWrapStyleWord(true);
        definitionArea.setEditable(false);
        definitionArea.setBackground(panel.getBackground());
        
        JScrollPane definitionScroll = new JScrollPane(definitionArea);
        definitionScroll.setBorder(null);
        definitionScroll.setBackground(panel.getBackground());
    
        panel.add(termPanel, BorderLayout.NORTH);
        panel.add(definitionScroll, BorderLayout.CENTER);
        return panel;
    }

    private static void updateFlashcard(String oldTerm, String newTerm, String newDefinition) {
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String query = "UPDATE quizzes SET term = ?, definition = ? WHERE term = ? AND user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, newTerm);
                stmt.setString(2, newDefinition);
                stmt.setString(3, oldTerm);
                stmt.setInt(4, UserSession.getUserId());
                
                int result = stmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Flashcard updated successfully!");
                    refreshQuizContainer();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating flashcard: " + ex.getMessage());
        }
    }

    private static void deleteFlashcard(String term) {
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String query = "DELETE FROM quizzes WHERE term = ? AND user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, term);
                stmt.setInt(2, UserSession.getUserId());
                
                int result = stmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Flashcard deleted successfully!");
                    refreshQuizContainer();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting flashcard: " + ex.getMessage());
        }
    }
}
