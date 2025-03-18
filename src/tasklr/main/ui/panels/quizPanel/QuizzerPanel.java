package tasklr.main.ui.panels.quizPanel;

import javax.swing.*;
import tasklr.utilities.*;
import tasklr.authentication.UserSession;
import java.awt.*;
import java.sql.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Enumeration;

public class QuizzerPanel {
    private static final String url = "jdbc:mysql://localhost:3306/tasklrdb";
    private static final String dbUser = "JFCompany";
    private static final String dbPass = "";
    private static JPanel quizContainer;
    private static JScrollPane scrollPane;
    private static ScheduledExecutorService scheduler;
    private static ScheduledFuture<?> refreshTask;
    private static final Color TEXT_COLOR = new Color(0x242424);
    private static final Color BACKGROUND_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_CONTAINER_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_ITEM_COLOR = new Color(0xFBFBFC);
    private static final Color LIST_ITEM_HOVER_BG = new Color(0xE8EAED);
    private static final Color LIST_ITEM_HOVER_BORDER = new Color(0x0082FC);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(0x275CE2);
    private static final Color PRIMARY_BUTTON_HOVER = new Color(0x3B6FF0);
    private static final Color PRIMARY_BUTTON_TEXT = Color.WHITE;
    
    public static JPanel createQuizzerPanel() {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(100, 100));
        
        // Create and add list container
        JPanel listContainer = createListContainer();
        panel.add(listContainer, BorderLayout.WEST);
        
        // Start the auto-refresh mechanism
        startAutoRefresh();
        
        return panel;
    }

    private static JPanel createListContainer() {
        // Main container with fixed width
        JPanel mainPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), new Dimension(600, 0));
        mainPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, LIST_ITEM_HOVER_BORDER));

        // Title Panel
        JPanel titlePanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), null);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Available Flashcard Sets");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Container for quiz sets
        JPanel cardsContainer = createPanel.panel(BACKGROUND_COLOR, null, null);
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Scroll pane configuration
        scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        // Set the quiz container reference for future updates
        quizContainer = cardsContainer;

        // Add components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Initial load of flashcard sets
        refreshQuizContainer();

        return mainPanel;
    }

    private static void startAutoRefresh() {
        // Create a single-thread scheduler
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true); // Make it a daemon thread so it doesn't prevent JVM shutdown
            return t;
        });

        // Schedule the refresh task to run every 2 seconds
        refreshTask = scheduler.scheduleAtFixedRate(() -> {
            // Ensure UI updates happen on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    refreshQuizContainer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }, 0, 2, TimeUnit.SECONDS);
    }

    private static void showCenteredOptionPane(Component parentComponent, String message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static synchronized void refreshQuizContainer() {
        if (quizContainer == null) return;
        
        quizContainer.removeAll();
        
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String query = "SELECT set_id, subject, description FROM flashcard_sets WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, UserSession.getUserId());
                try (ResultSet rs = stmt.executeQuery()) {
                    boolean hasItems = false;
                    
                    while (rs.next()) {
                        hasItems = true;
                        int setId = rs.getInt("set_id");
                        String subject = rs.getString("subject");
                        String description = rs.getString("description");
                        
                        JPanel setPanel = createQuizSetItemPanel(setId, subject, description);
                        quizContainer.add(setPanel);
                        quizContainer.add(Box.createRigidArea(new Dimension(0, 5)));
                    }
                    
                    if (!hasItems) {
                        JLabel noItemsLabel = new JLabel("No flashcard sets available for quiz!");
                        noItemsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
                        noItemsLabel.setForeground(TEXT_COLOR);
                        noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        quizContainer.add(noItemsLabel);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showCenteredOptionPane(null, "Error fetching flashcard sets: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        SwingUtilities.invokeLater(() -> {
            quizContainer.revalidate();
            quizContainer.repaint();
            
            if (scrollPane != null) {
                scrollPane.getViewport().revalidate();
                scrollPane.getViewport().repaint();
            }
        });
    }

    private static JPanel createQuizSetItemPanel(int setId, String subject, String description) {
        // Main panel with fixed height and full width
        JPanel panel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), null);
        panel.setPreferredSize(new Dimension(550, 80));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Content panel
        JPanel contentPanel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(10, 0), null);

        // Text Panel (Left side)
        JPanel textPanel = createPanel.panel(LIST_ITEM_COLOR, new GridLayout(2, 1, 0, 5), null);
        
        JLabel subjectLabel = new JLabel(subject);
        subjectLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        subjectLabel.setForeground(TEXT_COLOR);
        
        String shortDescription = description != null && description.length() > 50 
            ? description.substring(0, 47) + "..." 
            : (description != null ? description : "No description");
        JLabel descriptionLabel = new JLabel(shortDescription);
        descriptionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        descriptionLabel.setForeground(TEXT_COLOR);
        
        textPanel.add(subjectLabel);
        textPanel.add(descriptionLabel);

        // Button Panel (Right side)
        JButton startQuizBtn = createButton.button("Start Quiz", null, Color.WHITE, null, false);
        startQuizBtn.setBackground(new Color(0x275CE2));
        startQuizBtn.setPreferredSize(new Dimension(100, 30));
        startQuizBtn.addActionListener(e -> showQuizTypeDialog(setId, subject));

        contentPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.add(startQuizBtn, BorderLayout.EAST);
        panel.add(contentPanel, BorderLayout.CENTER);

        // Add hover effect
        new HoverPanelEffect(panel, LIST_ITEM_COLOR, LIST_ITEM_HOVER_BG);
        
        return panel;
    }

    private static void showQuizTypeDialog(int setId, String subject) {
        String[] options = {"Identification", "Multiple Choice"};
        JPanel dialogPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Select Quiz Type for: " + subject);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        
        JComboBox<String> quizTypeCombo = new JComboBox<>(options);
        quizTypeCombo.setPreferredSize(new Dimension(200, 30));
        
        dialogPanel.add(titleLabel);
        dialogPanel.add(new JLabel("Quiz Type:"));
        dialogPanel.add(quizTypeCombo);

        int result = JOptionPane.showConfirmDialog(
            null,
            dialogPanel,
            "Select Quiz Type",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedType = (String) quizTypeCombo.getSelectedItem();
            if ("Identification".equals(selectedType)) {
                startIdentificationQuiz(setId, subject);
            } else {
                startMultipleChoiceQuiz(setId, subject);
            }
        }
    }

    private static void startIdentificationQuiz(int setId, String subject) {
        // Fetch all flashcards for this set
        List<FlashCard> flashcards = fetchFlashcardsForSet(setId);
        
        if (flashcards.isEmpty()) {
            Toast.error("No flashcards found in this set!");
            return;
        }

        // Create quiz panel
        JPanel quizPanel = createIdentificationQuizPanel(flashcards, subject);
        
        // Show quiz in a new dialog
        JDialog quizDialog = new JDialog();
        quizDialog.setTitle("Identification Quiz - " + subject);
        quizDialog.setModal(true);
        quizDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        quizDialog.setSize(800, 600);
        quizDialog.setLocationRelativeTo(null);
        quizDialog.add(quizPanel);
        quizDialog.setVisible(true);
    }

    private static class FlashCard {
        String term;
        String definition;
        
        FlashCard(String term, String definition) {
            this.term = term;
            this.definition = definition;
        }
    }

    private static List<FlashCard> fetchFlashcardsForSet(int setId) {
        List<FlashCard> flashcards = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String query = "SELECT term, definition FROM flashcards WHERE set_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, setId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        flashcards.add(new FlashCard(
                            rs.getString("term"),
                            rs.getString("definition")
                        ));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error fetching flashcards: " + ex.getMessage());
        }
        
        return flashcards;
    }

    private static JPanel createIdentificationQuizPanel(List<FlashCard> flashcards, String subject) {
        // Randomize flashcards
        Collections.shuffle(flashcards);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel(subject + " - Identification Quiz");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Questions container
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setBackground(BACKGROUND_COLOR);

        // Create scrollable container
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Score tracking
        AtomicInteger score = new AtomicInteger(0);
        List<JTextField> answerFields = new ArrayList<>();
        List<FlashCard> questionOrder = new ArrayList<>(flashcards);

        // Create question panels
        for (int i = 0; i < flashcards.size(); i++) {
            FlashCard card = flashcards.get(i);
            
            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
            questionPanel.setBackground(BACKGROUND_COLOR);
            questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
            
            JLabel questionLabel = new JLabel((i + 1) + ". " + card.definition);
            questionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
            questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JTextField answerField = new JTextField();
            answerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            answerField.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
            answerFields.add(answerField);
            
            questionPanel.add(questionLabel);
            questionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            questionPanel.add(answerField);
            
            questionsPanel.add(questionPanel);
        }

        // Submit button
        JButton submitButton = new JButton("Submit Quiz");
        submitButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        submitButton.setBackground(PRIMARY_BUTTON_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> {
            // Check answers and calculate score
            for (int i = 0; i < answerFields.size(); i++) {
                String userAnswer = answerFields.get(i).getText().trim().toLowerCase();
                String correctAnswer = questionOrder.get(i).term.toLowerCase();
                
                if (userAnswer.equals(correctAnswer)) {
                    score.incrementAndGet();
                }
            }
            
            // Show results
            showQuizResults(score.get(), flashcards.size(), (JDialog) SwingUtilities.getWindowAncestor(mainPanel));
        });

        // Close button
        JButton closeButton = new JButton("Close Quiz");
        closeButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(mainPanel);
            if (window instanceof JDialog) {
                ((JDialog) window).dispose();
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(submitButton);
        buttonPanel.add(closeButton);

        // Add all components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private static void showQuizResults(int score, int total, JDialog quizDialog) {
        double percentage = (score * 100.0) / total;
        String message = String.format(
            "Quiz Complete!\n\nScore: %d/%d (%.1f%%)",
            score, total, percentage
        );
        
        JOptionPane.showMessageDialog(
            quizDialog,
            message,
            "Quiz Results",
            JOptionPane.INFORMATION_MESSAGE
        );
        
        quizDialog.dispose();
    }

    private static void startMultipleChoiceQuiz(int setId, String subject) {
        List<FlashCard> flashcards = fetchFlashcardsForSet(setId);
        
        if (flashcards.isEmpty()) {
            Toast.error("No flashcards found in this set!");
            return;
        }

        if (flashcards.size() < 2) {
            Toast.error("Multiple choice quiz requires at least 2 flashcards!");
            return;
        }

        JPanel quizPanel = createMultipleChoiceQuizPanel(flashcards, subject);
        
        JDialog quizDialog = new JDialog();
        quizDialog.setTitle("Multiple Choice Quiz - " + subject);
        quizDialog.setModal(true);
        quizDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        quizDialog.setSize(800, 600);
        quizDialog.setLocationRelativeTo(null);
        quizDialog.add(quizPanel);
        quizDialog.setVisible(true);
    }

    private static JPanel createMultipleChoiceQuizPanel(List<FlashCard> flashcards, String subject) {
        // Randomize flashcards
        Collections.shuffle(flashcards);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel(subject + " - Multiple Choice Quiz");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Questions container
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Score tracking
        AtomicInteger score = new AtomicInteger(0);
        List<ButtonGroup> answerGroups = new ArrayList<>();
        List<FlashCard> questionOrder = new ArrayList<>(flashcards);

        // Create question panels
        for (int i = 0; i < flashcards.size(); i++) {
            FlashCard currentCard = flashcards.get(i);
            
            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
            questionPanel.setBackground(BACKGROUND_COLOR);
            questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
            
            // Question label
            JLabel questionLabel = new JLabel((i + 1) + ". " + currentCard.definition);
            questionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
            questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Create choices
            ButtonGroup choiceGroup = new ButtonGroup();
            JPanel choicesPanel = new JPanel();
            choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
            choicesPanel.setBackground(BACKGROUND_COLOR);
            choicesPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            
            // Generate choices
            List<String> choices = generateChoices(flashcards, currentCard, 
                Math.min(4, flashcards.size())); // Maximum 4 choices
            
            for (String choice : choices) {
                JRadioButton radioBtn = new JRadioButton(choice);
                radioBtn.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
                radioBtn.setBackground(BACKGROUND_COLOR);
                choiceGroup.add(radioBtn);
                
                // Create a panel for each choice for better alignment
                JPanel choiceItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                choiceItemPanel.setBackground(BACKGROUND_COLOR);
                choiceItemPanel.add(radioBtn);
                choicesPanel.add(choiceItemPanel);
            }
            
            answerGroups.add(choiceGroup);
            
            questionPanel.add(questionLabel);
            questionPanel.add(choicesPanel);
            questionsPanel.add(questionPanel);
        }

        // Submit button
        JButton submitButton = new JButton("Submit Quiz");
        submitButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        submitButton.setBackground(PRIMARY_BUTTON_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> {
            // Check answers
            for (int i = 0; i < answerGroups.size(); i++) {
                ButtonGroup group = answerGroups.get(i);
                String correctAnswer = questionOrder.get(i).term;
                
                for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                    AbstractButton button = buttons.nextElement();
                    if (button.isSelected() && button.getText().equals(correctAnswer)) {
                        score.incrementAndGet();
                        break;
                    }
                }
            }
            
            // Show results
            showQuizResults(score.get(), flashcards.size(), 
                (JDialog) SwingUtilities.getWindowAncestor(mainPanel));
        });

        // Close button
        JButton closeButton = new JButton("Close Quiz");
        closeButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(mainPanel);
            if (window instanceof JDialog) {
                ((JDialog) window).dispose();
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(submitButton);
        buttonPanel.add(closeButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private static List<String> generateChoices(List<FlashCard> allCards, FlashCard correctCard, int numChoices) {
        List<String> choices = new ArrayList<>();
        choices.add(correctCard.term); // Add correct answer
        
        // Create a list of other cards to choose from
        List<FlashCard> otherCards = new ArrayList<>(allCards);
        otherCards.remove(correctCard);
        Collections.shuffle(otherCards);
        
        // Add random incorrect choices
        for (int i = 0; i < numChoices - 1 && i < otherCards.size(); i++) {
            choices.add(otherCards.get(i).term);
        }
        
        // Shuffle the choices
        Collections.shuffle(choices);
        return choices;
    }
}
        
