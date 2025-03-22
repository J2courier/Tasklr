package tasklr.main.ui.panels.quizPanel;

import javax.swing.*;
import tasklr.utilities.*;
import tasklr.authentication.UserSession;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
    private static CardLayout cardLayout;
    private static JPanel quizViewPanel;
    private static JPanel mainPanel;
    private static UIRefreshManager refreshManager;
    
    // Remove the old scheduler variables as we'll use UIRefreshManager instead
    // private static ScheduledExecutorService scheduler;
    // private static ScheduledFuture<?> refreshTask;

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
        // Use full size for the main panel
        mainPanel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), null);
        
        // Create and add list container
        JPanel listContainer = createListContainer();
        mainPanel.add(listContainer, BorderLayout.WEST);
        
        // Create quiz view panel with CardLayout
        cardLayout = new CardLayout();
        quizViewPanel = new JPanel(cardLayout);
        quizViewPanel.setBackground(BACKGROUND_COLOR);
        
        // Add empty state panel
        JPanel emptyStatePanel = createEmptyStatePanel();
        quizViewPanel.add(emptyStatePanel, "EMPTY_STATE");
        
        mainPanel.add(quizViewPanel, BorderLayout.CENTER);
        
        // Start the auto-refresh mechanism
        startAutoRefresh();
        
        // Add component listener to handle cleanup
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                stopAutoRefresh();
            }
            
            @Override
            public void componentShown(ComponentEvent e) {
                startAutoRefresh();
            }
        });
        
        // Show empty state initially
        cardLayout.show(quizViewPanel, "EMPTY_STATE");
        
        return mainPanel;
    }

    private static JPanel createEmptyStatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        JLabel messageLabel = new JLabel("Select a flashcard set to start a quiz");
        messageLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        messageLabel.setForeground(TEXT_COLOR);
        
        panel.add(messageLabel);
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

        // Configure quiz container
        quizContainer = createPanel.panel(LIST_CONTAINER_COLOR, null, null);
        quizContainer.setLayout(new BoxLayout(quizContainer, BoxLayout.Y_AXIS));
        quizContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create wrapper panel for proper scrolling
        JPanel wrapperPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), null);
        wrapperPanel.add(quizContainer, BorderLayout.NORTH);

        // Configure scroll pane
        scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(LIST_CONTAINER_COLOR);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Initial refresh
        refreshQuizContainer();
        
        return mainPanel;
    }

    private static void startAutoRefresh() {
        refreshManager = UIRefreshManager.getInstance();
        refreshManager.startRefresh(UIRefreshManager.QUIZ_CONTAINER, () -> {
            try {
                refreshQuizContainer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void stopAutoRefresh() {
        if (refreshManager != null) {
            refreshManager.stopRefresh(UIRefreshManager.QUIZ_CONTAINER);
        }
    }

    private static void showCenteredOptionPane(Component parentComponent, String message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static synchronized void refreshQuizContainer() {
        if (quizContainer == null || !mainPanel.isShowing()) return;
        
        SwingUtilities.invokeLater(() -> {
            quizContainer.removeAll();
            
            try (Connection conn = DatabaseManager.getConnection()) {
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
                        
                        // Update UI
                        quizContainer.revalidate();
                        quizContainer.repaint();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.error("Failed to refresh quiz container: " + e.getMessage());
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
        JPanel textPanel = createPanel.panel(null, new GridLayout(2, 1, 0, 5), null);
        
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
            mainPanel,
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
        List<FlashCard> flashcards = fetchFlashcardsForSet(setId);
        
        if (flashcards.isEmpty()) {
            Toast.error("No flashcards found in this set!");
            return;
        }

        JPanel quizPanel = createIdentificationQuizPanel(flashcards, subject, setId);
        quizViewPanel.add(quizPanel, "QUIZ_" + setId);
        cardLayout.show(quizViewPanel, "QUIZ_" + setId);
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
        System.out.println("[Quizzer Panel] Fetching flashcards for set " + setId + " at: ");
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
            System.out.println("[Quizzer Panel] Fetched " + flashcards.size() + " flashcards");
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error fetching flashcards: " + ex.getMessage());
        }
        
        return flashcards;
    }

    private static JPanel createIdentificationQuizPanel(List<FlashCard> flashcards, String subject, int setId) {
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
            List<String> userAnswers = new ArrayList<>();
            for (JTextField field : answerFields) {
                userAnswers.add(field.getText());
            }
            showQuizResults(score.get(), flashcards.size(), mainPanel, 
                flashcards, subject, "Identification", setId, userAnswers);
        });

        // Close button
        JButton closeButton = new JButton("Close Quiz");
        closeButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            // Show empty state and remove the quiz panel
            cardLayout.show(quizViewPanel, "EMPTY_STATE");
            quizViewPanel.remove(mainPanel);
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

    private static void showQuizResults(int score, int total, JPanel quizPanel, 
            List<FlashCard> flashcards, String subject, String quizType, int setId, 
            List<String> userAnswers) {
        // Store the quiz attempt
        storeQuizAttempt(setId, score, total, quizType);
        
        // Update statistics
        updateQuizStatistics(setId, score);
        
        // Remove the current quiz panel
        quizViewPanel.remove(quizPanel);
        
        // Show the overview
        showQuizOverview(flashcards, subject, score, total, quizType, setId, userAnswers);
    }

    private static void storeQuizAttempt(int setId, int score, int totalQuestions, String quizType) {
        try {
            String query = "INSERT INTO quiz_attempts (user_id, set_id, score, total_questions, quiz_type, completion_date) " +
                          "VALUES (?, ?, ?, ?, ?, NOW())";
            
            DatabaseManager.executeUpdate(
                query,
                UserSession.getUserId(),
                setId,
                score,
                totalQuestions,
                quizType
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error storing quiz attempt: " + ex.getMessage());
        }
    }

    private static void updateQuizStatistics(int setId, int newScore) {
        try {
            // First, check if statistics exist for this user-set combination
            String checkQuery = "SELECT * FROM quiz_statistics WHERE user_id = ? AND set_id = ?";
            ResultSet rs = DatabaseManager.executeQuery(checkQuery, UserSession.getUserId(), setId);
            
            if (rs.next()) {
                // Update existing statistics
                int currentAttempts = rs.getInt("total_attempts");
                double currentAverage = rs.getDouble("average_score");
                int highestScore = rs.getInt("highest_score");
                
                // Calculate new average
                double newAverage = ((currentAverage * currentAttempts) + newScore) / (currentAttempts + 1);
                
                // Update statistics
                String updateQuery = "UPDATE quiz_statistics SET " +
                                   "total_attempts = total_attempts + 1, " +
                                   "highest_score = GREATEST(highest_score, ?), " +
                                   "average_score = ?, " +
                                   "last_attempt_date = NOW() " +
                                   "WHERE user_id = ? AND set_id = ?";
                
                DatabaseManager.executeUpdate(
                    updateQuery,
                    newScore,
                    newAverage,
                    UserSession.getUserId(),
                    setId
                );
            } else {
                // Insert new statistics record
                String insertQuery = "INSERT INTO quiz_statistics " +
                                   "(user_id, set_id, total_attempts, highest_score, average_score, last_attempt_date) " +
                                   "VALUES (?, ?, 1, ?, ?, NOW())";
                
                DatabaseManager.executeUpdate(
                    insertQuery,
                    UserSession.getUserId(),
                    setId,
                    newScore,
                    newScore
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error updating quiz statistics: " + ex.getMessage());
        }
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

        JPanel quizPanel = createMultipleChoiceQuizPanel(flashcards, subject, setId);
        quizViewPanel.add(quizPanel, "QUIZ_" + setId);
        cardLayout.show(quizViewPanel, "QUIZ_" + setId);
    }

    private static JPanel createMultipleChoiceQuizPanel(List<FlashCard> flashcards, String subject, int setId) {
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
            List<String> userAnswers = new ArrayList<>();
            for (ButtonGroup group : answerGroups) {
                String selectedAnswer = "";
                for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                    AbstractButton button = buttons.nextElement();
                    if (button.isSelected()) {
                        selectedAnswer = button.getText();
                        break;
                    }
                }
                userAnswers.add(selectedAnswer);
            }
            showQuizResults(score.get(), flashcards.size(), mainPanel, 
                flashcards, subject, "Multiple Choice", setId, userAnswers);
        });

        // Close button
        JButton closeButton = new JButton("Close Quiz");
        closeButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            // Show empty state and remove the quiz panel
            cardLayout.show(quizViewPanel, "EMPTY_STATE");
            quizViewPanel.remove(mainPanel);
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

    // Add a helper method to handle quiz closure
    private static void closeQuiz(JPanel quizPanel) {
        cardLayout.show(quizViewPanel, "EMPTY_STATE");
        quizViewPanel.remove(quizPanel);
        quizViewPanel.revalidate();
        quizViewPanel.repaint();
    }

    private static void showQuizOverview(List<FlashCard> flashcards, String subject, int score, 
            int total, String quizType, int setId, List<String> userAnswers) {  // Add userAnswers parameter
        JPanel overviewPanel = new JPanel(new BorderLayout());
        overviewPanel.setBackground(BACKGROUND_COLOR);
        overviewPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Subject Label (Left)
        JLabel subjectLabel = new JLabel(subject + " - Quiz Overview");
        subjectLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));

        // Score Label (Right)
        double percentage = (score * 100.0) / total;
        JLabel scoreLabel = new JLabel(String.format("%d/%d (%.1f%%)", score, total, percentage));
        scoreLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));

        headerPanel.add(subjectLabel, BorderLayout.WEST);
        headerPanel.add(scoreLabel, BorderLayout.EAST);

        // Content Panel with ScrollPane
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Add items for each flashcard
        for (int i = 0; i < flashcards.size(); i++) {
            FlashCard card = flashcards.get(i);
            boolean isCorrect = userAnswers.get(i).trim().toLowerCase()
                .equals(card.term.trim().toLowerCase());
            
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            // Question number and definition
            JLabel definitionLabel = new JLabel((i + 1) + ". " + card.definition);
            definitionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
            // Set red color for question number if answer was wrong
            if (!isCorrect) {
                String numberPart = (i + 1) + ". ";
                String definitionPart = card.definition;
                definitionLabel.setText("<html><font color='#FF0000'>" + numberPart + "</font>" + definitionPart + "</html>");
                definitionLabel.putClientProperty("html.disable", Boolean.FALSE);
            }

            // User's answer
            JLabel userAnswerLabel = new JLabel("Your answer: " + userAnswers.get(i));
            userAnswerLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
            if (!isCorrect) {
                userAnswerLabel.setForeground(new Color(0xFF0000));
            }

            // Correct answer
            JLabel answerLabel = new JLabel("Correct answer: " + card.term);
            answerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
            answerLabel.setForeground(new Color(0x275CE2));

            itemPanel.add(definitionLabel);
            itemPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            itemPanel.add(userAnswerLabel);
            itemPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            itemPanel.add(answerLabel);
            
            // Add spacing between items
            contentPanel.add(itemPanel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // If this is the last item, add the button panel
            if (i == flashcards.size() - 1) {
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.setBackground(BACKGROUND_COLOR);
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

                // Retake button
                JButton retakeButton = new JButton("Retake Quiz");
                retakeButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
                retakeButton.setBackground(PRIMARY_BUTTON_COLOR);
                retakeButton.setForeground(Color.WHITE);
                retakeButton.setFocusPainted(false);
                retakeButton.addActionListener(e -> {
                    closeQuiz(overviewPanel);
                    if ("Identification".equals(quizType)) {
                        startIdentificationQuiz(setId, subject);
                    } else {
                        startMultipleChoiceQuiz(setId, subject);
                    }
                });

                // Close button
                JButton closeButton = new JButton("Close");
                closeButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
                closeButton.setBackground(Color.RED);
                closeButton.setForeground(Color.WHITE);
                closeButton.setFocusPainted(false);
                closeButton.addActionListener(e -> closeQuiz(overviewPanel));

                buttonPanel.add(retakeButton);
                buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
                buttonPanel.add(closeButton);
                
                contentPanel.add(buttonPanel);
            }
        }

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_COLOR);

        // Add components to main panel
        overviewPanel.add(headerPanel, BorderLayout.NORTH);
        overviewPanel.add(scrollPane, BorderLayout.CENTER);

        // Add to quiz view panel and show
        quizViewPanel.add(overviewPanel, "OVERVIEW");
        cardLayout.show(quizViewPanel, "OVERVIEW");
    }

    // Add cleanup method to be called when the panel is being disposed
    public static void cleanup() {
        stopAutoRefresh();
    }
}
        
