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
import tasklr.main.ui.components.TaskCounterPanel;
import tasklr.main.ui.panels.Home.HomePanel;
import java.util.Arrays;

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


    private static final Color TEXT_COLOR = new Color(0x242424);
    private static final Color BACKGROUND_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_CONTAINER_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_ITEM_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_ITEM_HOVER_BG = new Color(0xE8EAED);
    private static final Color LIST_ITEM_HOVER_BORDER = new Color(0x0082FC);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(0x275CE2);
    private static final Color PRIMARY_BUTTON_HOVER = new Color(0x3B6FF0);
    private static final Color PANEL_SHADOW_COLOR = new Color(0xE0E0E0);
    private static final Color QUESTION_PANEL_BG = new Color(0xF1F2F5);
    private static final Color HEADER_BG = new Color(0xF8F9FA);
    private static final Color ACCENT_COLOR = new Color(0x275CE2);
    private static final Color ANSWER_FIELD_BG = new Color(0xF5F7FA);
    private static final Color PRIMARY_BUTTON_TEXT = Color.WHITE;

    // Add references to HomePanel's counter panels
    private static TaskCounterPanel totalQuizTakenPanel;
    private static TaskCounterPanel totalQuizRetakedPanel;

    // Add setter methods to establish connection with HomePanel's counters
    public static void setCounterPanels(TaskCounterPanel quizTakenPanel, TaskCounterPanel quizRetakedPanel) {
        totalQuizTakenPanel = quizTakenPanel;
        totalQuizRetakedPanel = quizRetakedPanel;
    }

    // Add this method to initialize the connection
    public static void initializeHomePanel() {
        if (HomePanel.getTotalQuizTakenPanel() != null && HomePanel.getTotalQuizRetakedPanel() != null) {
            setCounterPanels(
                HomePanel.getTotalQuizTakenPanel(),
                HomePanel.getTotalQuizRetakedPanel()
            );
        }
    }

    private static JPanel listContainer;

    public static JPanel createQuizzerPanel() {
        // Use full size for the main panel
        mainPanel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), null);

        // Create and add list container - now taking full width
        JPanel listContainerPanel = createListContainer();
        mainPanel.add(listContainerPanel, BorderLayout.CENTER); // Changed from WEST to CENTER to take full space

        cardLayout = new CardLayout();
        quizViewPanel = new JPanel(cardLayout);
        quizViewPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel emptyStatePanel = createEmptyStatePanel();
        quizViewPanel.add(emptyStatePanel, "EMPTY_STATE");
        cardLayout.show(quizViewPanel, "EMPTY_STATE");

        return mainPanel;
    }

    private static JPanel createEmptyStatePanel() {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), null);
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)));

        
        // Create content panel for the message
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        JLabel messageLabel = new JLabel("Select a flashcard set to start a quiz");
        messageLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        messageLabel.setForeground(TEXT_COLOR);

        contentPanel.add(messageLabel);

        // Add components to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel createListContainer() {
        // Main container with no fixed width (will expand to fill available space)
        listContainer = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), null);
        
        // Configure quiz container
        quizContainer = createPanel.panel(LIST_CONTAINER_COLOR, null, null);
        quizContainer.setLayout(new BoxLayout(quizContainer, BoxLayout.Y_AXIS));
        quizContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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

        listContainer.add(scrollPane, BorderLayout.CENTER);
        return listContainer;
    }


    private static void showCenteredOptionPane(Component parentComponent, String message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        JDialog dialog = pane.createDialog(parentComponent, title);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static synchronized void refreshQuizContainer() {
        if (quizContainer == null || !mainPanel.isShowing()) return;

        quizContainer.removeAll();
        
        try {
            String query = "SELECT set_id, subject, description FROM flashcard_sets WHERE user_id = ? ORDER BY subject ASC";
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
                    
                    JPanel setPanel = createQuizSetItemPanel(setId, subject, description);
                    quizContainer.add(setPanel);
                    
                    // Use consistent spacing of 5 pixels
                    quizContainer.add(Box.createVerticalStrut(5));
                }
                
                // Add "No sets" message if needed
                if (!hasItems) {
                    // Add empty state message
                    JLabel noSetsLabel = new JLabel("No flashcard sets yet. Create one!", SwingConstants.CENTER);
                    noSetsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
                    noSetsLabel.setForeground(new Color(0x707070));
                    noSetsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    quizContainer.add(noSetsLabel);
                }
            }
            
            // Refresh UI components
            quizContainer.revalidate();
            quizContainer.repaint();
            
            if (scrollPane != null) {
                scrollPane.getViewport().revalidate();
                scrollPane.getViewport().repaint();
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error fetching flashcard sets: " + ex.getMessage());
        }
    }

    public static JPanel createQuizSetItemPanel(int setId, String subject, String description) {
        // Main panel with fixed height and full width
        JPanel panel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), null);
        panel.setPreferredSize(new Dimension(550, 80));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Content panel
        JPanel contentPanel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(10, 0), null);

        // Text Panel (Left side)
        JPanel textPanel = createPanel.panel(null, new GridLayout(2, 1, 0, 5), null);

        JLabel subjectLabel = new JLabel(subject);
        subjectLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
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
        // First, get the total number of flashcards
        int totalFlashcards = getTotalFlashcards(setId);
        if (totalFlashcards == 0) {
            Toast.error("No flashcards found in this set!");
            return;
        }

        // Main dialog panel with padding and fixed width
        JPanel dialogPanel = new JPanel(new BorderLayout(0, 10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        dialogPanel.setPreferredSize(new Dimension(350, 300)); // Fixed width for the dialog

        // Title panel at the top
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel titleLabel = new JLabel("Quiz Settings");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Content panel for all inputs
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Quiz type section
        JPanel quizTypeSection = new JPanel();
        quizTypeSection.setLayout(new BoxLayout(quizTypeSection, BoxLayout.Y_AXIS));
        quizTypeSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel typeLabel = new JLabel("Quiz Type:");
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));

        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        String[] options = {"Identification", "Multiple Choice"};
        JComboBox<String> quizTypeCombo = new JComboBox<>(options);
        quizTypeCombo.setPreferredSize(new Dimension(300, 30)); // Fixed width
        quizTypeCombo.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        comboPanel.add(quizTypeCombo);
        comboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        quizTypeSection.add(typeLabel);
        quizTypeSection.add(comboPanel);

        // Number of items section
        JPanel itemsSection = new JPanel();
        itemsSection.setLayout(new BoxLayout(itemsSection, BoxLayout.Y_AXIS));
        itemsSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemsSection.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel itemsLabel = new JLabel("Number of Items (max " + totalFlashcards + "):");
        itemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));

        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
            Math.min(10, totalFlashcards), // initial value
            1,                            // minimum value
            totalFlashcards,              // maximum value
            1                             // step
        );
        JSpinner itemsSpinner = new JSpinner(spinnerModel);
        itemsSpinner.setPreferredSize(new Dimension(300, 30)); // Fixed width
        itemsSpinner.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        spinnerPanel.add(itemsSpinner);
        spinnerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        itemsSection.add(itemsLabel);
        itemsSection.add(spinnerPanel);

        // Time duration section
        JPanel timeSection = new JPanel();
        timeSection.setLayout(new BoxLayout(timeSection, BoxLayout.Y_AXIS));
        timeSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeSection.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel timeLabel = new JLabel("Time Duration (minutes):");
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));

        JPanel timeComboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        Integer[] timeOptions = {5, 10, 15, 20, 25, 30, 40, 45, 50, 55, 60};
        JComboBox<Integer> timeCombo = new JComboBox<>(timeOptions);
        timeCombo.setSelectedItem(15); // Default to 15 minutes
        timeCombo.setPreferredSize(new Dimension(300, 30)); // Fixed width
        timeCombo.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        timeComboPanel.add(timeCombo);
        timeComboPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        timeSection.add(timeLabel);
        timeSection.add(timeComboPanel);

        // Add all sections to content panel
        contentPanel.add(quizTypeSection);
        contentPanel.add(itemsSection);
        contentPanel.add(timeSection);

        // Add all panels to dialog panel
        dialogPanel.add(titlePanel, BorderLayout.NORTH);
        dialogPanel.add(contentPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
            mainPanel,
            dialogPanel,
            "Start Quiz",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            // Remove list container and add quiz view panel
            mainPanel.removeAll();
            mainPanel.add(quizViewPanel, BorderLayout.CENTER);
            
            String selectedType = (String) quizTypeCombo.getSelectedItem();
            int numberOfItems = (Integer) itemsSpinner.getValue();
            int timeDuration = (Integer) timeCombo.getSelectedItem();

            if ("Identification".equals(selectedType)) {
                startIdentificationQuiz(setId, subject, numberOfItems, timeDuration);
            } else {
                startMultipleChoiceQuiz(setId, subject, numberOfItems, timeDuration);
            }
            
            // Make sure the main panel and all its parents are properly validated and repainted
            Container parent = mainPanel;
            while (parent != null) {
                parent.revalidate();
                parent.repaint();
                parent = parent.getParent();
            }
        }
    }

    private static int getTotalFlashcards(int setId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT COUNT(*) as total FROM flashcards WHERE set_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, setId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("total");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error counting flashcards: " + ex.getMessage());
        }
        return 0;
    }

    public static void startIdentificationQuiz(int setId, String subject, int numberOfItems, int timeDuration) {
        List<FlashCard> flashcards = fetchFlashcardsForSet(setId);

        if (flashcards.isEmpty()) {
            Toast.error("No flashcards found in this set!");
            return;
        }

        // Shuffle and limit to selected number of items
        Collections.shuffle(flashcards);
        flashcards = flashcards.subList(0, Math.min(numberOfItems, flashcards.size()));

        // Create the quiz panel
        JPanel quizPanel = createIdentificationQuizPanel(flashcards, subject, setId, timeDuration);
        
        // Hide navbars
        hideNavbars();
        
        // Add the quiz panel to the quiz view panel with a unique identifier
        String quizId = "QUIZ_" + setId + "_" + System.currentTimeMillis();
        quizViewPanel.add(quizPanel, quizId);
        
        // Show the quiz panel
        cardLayout.show(quizViewPanel, quizId);
        
        // Make sure the quiz view panel is visible
        quizViewPanel.setVisible(true);
        
        // Revalidate and repaint
        quizViewPanel.revalidate();
        quizViewPanel.repaint();
        mainPanel.revalidate();
        mainPanel.repaint();
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

    // Helper method to create consistent question panels for both quiz types
    // private static JPanel createStandardQuestionPanel(int questionNumber, String definition, int width) {
    //     JPanel questionPanel = new JPanel();
    //     questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
    //     questionPanel.setBackground(BACKGROUND_COLOR);
    //     questionPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    //     // Question container for proper alignment
    //     JPanel questionContainer = new JPanel(new BorderLayout(10, 0));
    //     questionContainer.setBackground(BACKGROUND_COLOR);
    //     questionContainer.setMaximumSize(new Dimension(width - 30, Integer.MAX_VALUE));

    //     // Question number
    //     JLabel numberLabel = new JLabel(questionNumber + ".");
    //     numberLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
    //     numberLabel.setVerticalAlignment(SwingConstants.TOP);

    //     // Definition with dynamic wrapping
    //     JTextArea definitionLabel = new JTextArea(definition);
    //     definitionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
    //     definitionLabel.setBackground(BACKGROUND_COLOR);
    //     definitionLabel.setEditable(false);
    //     definitionLabel.setWrapStyleWord(true);
    //     definitionLabel.setLineWrap(true);
    //     definitionLabel.setBorder(null);

    //     // Calculate preferred height based on text content
    //     FontMetrics fm = definitionLabel.getFontMetrics(definitionLabel.getFont());
    //     int textWidth = width - 80;
    //     int lineHeight = fm.getHeight();
    //     int textLength = fm.stringWidth(definition);
    //     int lines = (textLength / textWidth) + 1;
    //     int definitionHeight = Math.max(50, lines * lineHeight);

    //     definitionLabel.setPreferredSize(new Dimension(textWidth, definitionHeight));

    //     questionContainer.add(numberLabel, BorderLayout.WEST);
    //     questionContainer.add(definitionLabel, BorderLayout.CENTER);

    //     questionPanel.add(questionContainer);
    //     questionPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Increased spacing after question

    //     // Calculate total height based on content
    //     int contentHeight = definitionHeight + 30; // Basic height for question

    //     // Set panel sizes - remove fixed maximum height to allow content to expand
    //     questionPanel.setPreferredSize(new Dimension(width, contentHeight));
    //     // Remove setMaximumSize to allow panel to grow based on content

    //     return questionPanel;
    // }

    private static JPanel createIdentificationQuizPanel(List<FlashCard> flashcards, String subject, int setId, int timeDuration) {
        // Randomize flashcards
        Collections.shuffle(flashcards);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header with BorderLayout
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0)); // Added gap between components
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Left side: Title
        JLabel titleLabel = new JLabel(subject.toUpperCase() + " Identification Quiz");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Right side: Timer only (removed toggle button)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(Color.WHITE);

        // Add timer panel
        JPanel timerPanel = createTimerPanel(timeDuration, () -> {
            JOptionPane.showMessageDialog(
                mainPanel,
                "Time's up! Your quiz will be submitted automatically.",
                "Time's Up",
                JOptionPane.WARNING_MESSAGE
            );
        });

        controlPanel.add(timerPanel);
        headerPanel.add(controlPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

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

        // Create individual question panels
        for (int i = 0; i < flashcards.size(); i++) {
            FlashCard card = flashcards.get(i);

            // Create question panel with GridBagLayout
            JPanel questionPanel = new JPanel(new GridBagLayout());
            questionPanel.setBackground(QUESTION_PANEL_BG);
            questionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xAAAAAA), 1), // Outer border with color 0xAAAAAA
                BorderFactory.createEmptyBorder(15, 15, 15, 15)  // Inner padding
            ));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Question label (row 1)
            JTextArea questionLabel = new JTextArea((i + 1) + ". " + card.definition);
            questionLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18)); // Changed to BOLD
            questionLabel.setBackground(QUESTION_PANEL_BG);
            questionLabel.setEditable(false);
            questionLabel.setWrapStyleWord(true);
            questionLabel.setLineWrap(true);
            questionLabel.setBorder(null);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            questionPanel.add(questionLabel, gbc);

            // Answer field (row 2)
            JTextField answerField = new JTextField();
            answerField.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
            answerField.setBackground(QUESTION_PANEL_BG);
            // Create bottom-only border with padding
            answerField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x575757)), // Only bottom border
                BorderFactory.createEmptyBorder(5, 0, 5, 0)  // Top, left, bottom, right padding
            ));
            answerField.setPreferredSize(new Dimension(500, 35));
            answerFields.add(answerField);

            gbc.gridy = 1;
            gbc.insets = new Insets(10, 5, 5, 5); // Extra top padding for the answer field
            questionPanel.add(answerField, gbc);

            // Add question panel to questions container
            questionsPanel.add(questionPanel);
            questionsPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Increased spacing between questions
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        // Submit button
        JButton submitButton = new JButton("Submit Quiz");
        submitButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        submitButton.setBackground(PRIMARY_BUTTON_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> {
            // Validate all fields are filled
            boolean allFieldsFilled = true;
            List<Integer> emptyQuestions = new ArrayList<>();

            for (int i = 0; i < answerFields.size(); i++) {
                if (answerFields.get(i).getText().trim().isEmpty()) {
                    allFieldsFilled = false;
                    emptyQuestions.add(i + 1);
                }
            }

            if (!allFieldsFilled) {
                StringBuilder message = new StringBuilder("The following questions are unanswered:\n\n");
                for (int questionNum : emptyQuestions) {
                    message.append("Question ").append(questionNum).append("\n");
                }
                message.append("\nDo you want to submit the quiz anyway?");

                // Create custom JOptionPane
                JOptionPane optionPane = new JOptionPane(
                    message.toString(),
                    JOptionPane.PLAIN_MESSAGE,  // Remove warning icon
                    JOptionPane.YES_NO_OPTION
                );

                // Get the buttons from the option pane
                JDialog dialog = optionPane.createDialog(mainPanel, "Incomplete Quiz");
                
                // Find and modify the buttons
                for (Component comp : optionPane.getComponents()) {
                    if (comp instanceof JPanel) {
                        for (Component btn : ((JPanel) comp).getComponents()) {
                            if (btn instanceof JButton) {
                                JButton button = (JButton) btn;
                                button.setFocusable(false);
                                button.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
                            }
                        }
                    }
                }

                // Center the dialog relative to the main panel
                dialog.setLocationRelativeTo(mainPanel);
                dialog.setVisible(true);

                // Get the user's choice
                Object selectedValue = optionPane.getValue();
                
                // If user closes the dialog or clicks No, return
                if (selectedValue == null || 
                    (Integer) selectedValue != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Proceed with existing score calculation
            for (int i = 0; i < answerFields.size(); i++) {
                String userAnswer = answerFields.get(i).getText().trim().toLowerCase();
                String correctAnswer = questionOrder.get(i).term.toLowerCase();

                if (!userAnswer.isEmpty() && userAnswer.equals(correctAnswer)) {
                    score.incrementAndGet();
                }
            }

            // Collect user answers (including empty ones)
            List<String> userAnswers = new ArrayList<>();
            for (JTextField field : answerFields) {
                userAnswers.add(field.getText().trim());
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
            // Add confirmation dialog
            int result = JOptionPane.showConfirmDialog(
                mainPanel,
                "Are you sure you want to close this quiz? Your answers will not be saved.",
                "Confirm Close",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                closeQuiz(mainPanel);
                refreshQuizContainer(); // Add this line to refresh the container
            }
        });

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

        // Show the overview (navbars should remain hidden)
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

    public static void startMultipleChoiceQuiz(int setId, String subject, int numberOfItems, int timeDuration) {
        List<FlashCard> flashcards = fetchFlashcardsForSet(setId);

        if (flashcards.isEmpty()) {
            Toast.error("No flashcards found in this set!");
            return;
        }

        if (flashcards.size() < 2) {
            Toast.error("Multiple choice quiz requires at least 2 flashcards!");
            return;
        }

        // Shuffle and limit to selected number of items
        Collections.shuffle(flashcards);
        flashcards = flashcards.subList(0, Math.min(numberOfItems, flashcards.size()));

        // Create the quiz panel
        JPanel quizPanel = createMultipleChoiceQuizPanel(flashcards, subject, setId, timeDuration);
        
        // Hide navbars
        hideNavbars();
        
        // Add the quiz panel to the quiz view panel with a unique identifier
        String quizId = "QUIZ_" + setId + "_" + System.currentTimeMillis();
        quizViewPanel.add(quizPanel, quizId);
        
        // Show the quiz panel
        cardLayout.show(quizViewPanel, quizId);
        
        // Make sure the quiz view panel is visible
        quizViewPanel.setVisible(true);
        
        // Revalidate and repaint
        quizViewPanel.revalidate();
        quizViewPanel.repaint();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static JPanel createMultipleChoiceQuizPanel(List<FlashCard> flashcards, String subject, int setId, int timeDuration) {
        // Randomize flashcards
        Collections.shuffle(flashcards);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header with BorderLayout
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0)); // Added gap between components
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Left side: Title
        JLabel titleLabel = new JLabel(subject.toUpperCase() + " Multiple Choice Quiz");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Right side: Timer only (removed toggle button)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(Color.WHITE);

        // Add timer panel
        JPanel timerPanel = createTimerPanel(timeDuration, () -> {
            JOptionPane.showMessageDialog(
                mainPanel,
                "Time's up! Your quiz will be submitted automatically.",
                "Time's Up",
                JOptionPane.WARNING_MESSAGE
            );
        });

        controlPanel.add(timerPanel);
        headerPanel.add(controlPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Questions container with vertical BoxLayout
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setBackground(BACKGROUND_COLOR);

        // Scrollable container
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Score tracking
        AtomicInteger score = new AtomicInteger(0);
        List<ButtonGroup> answerGroups = new ArrayList<>();
        List<FlashCard> questionOrder = new ArrayList<>(flashcards);

        // Create individual question panels
        for (int i = 0; i < flashcards.size(); i++) {
            FlashCard currentCard = flashcards.get(i);

            // Create question panel with GridBagLayout
            JPanel questionPanel = new JPanel(new GridBagLayout());
            questionPanel.setBackground(BACKGROUND_COLOR);
            questionPanel.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xE0E0E0), 1), // Light border
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)  // Increased padding
                )
            );

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Question label (row 1, spans both columns)
            JTextArea questionLabel = new JTextArea((i + 1) + ". " + currentCard.definition);
            questionLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14)); // Changed to BOLD
            questionLabel.setBackground(BACKGROUND_COLOR);
            questionLabel.setEditable(false);
            questionLabel.setWrapStyleWord(true);
            questionLabel.setLineWrap(true);
            questionLabel.setBorder(null);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            questionPanel.add(questionLabel, gbc);

            // Generate choices
            List<String> choices = generateChoices(flashcards, currentCard, 4);
            ButtonGroup choiceGroup = new ButtonGroup();

            // Add radio buttons in 2x2 grid
            for (int j = 0; j < choices.size(); j++) {
                JRadioButton radioBtn = new JRadioButton(choices.get(j));
                radioBtn.setFont(new Font("Segoe UI Variable", Font.PLAIN, 18));
                radioBtn.setBackground(BACKGROUND_COLOR);
                choiceGroup.add(radioBtn);

                gbc.gridx = j % 2;
                gbc.gridy = (j / 2) + 1;
                gbc.gridwidth = 1;
                gbc.weightx = 0.5;
                questionPanel.add(radioBtn, gbc);
            }

            answerGroups.add(choiceGroup);

            // Add question panel to questions container
            questionsPanel.add(questionPanel);
            questionsPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Increased spacing between questions
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        // Submit button
        JButton submitButton = new JButton("Submit Quiz");
        submitButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        submitButton.setBackground(PRIMARY_BUTTON_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(e -> {
            // Validate all questions have been answered
            boolean allQuestionsAnswered = true;
            List<Integer> unansweredQuestions = new ArrayList<>();

            for (int i = 0; i < answerGroups.size(); i++) {
                ButtonGroup group = answerGroups.get(i);
                boolean answered = false;

                for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
                    if (buttons.nextElement().isSelected()) {
                        answered = true;
                        break;
                    }
                }

                if (!answered) {
                    allQuestionsAnswered = false;
                    unansweredQuestions.add(i + 1);
                }
            }

            if (!allQuestionsAnswered) {
                StringBuilder message = new StringBuilder("The following questions are unanswered:\n\n");
                for (int questionNum : unansweredQuestions) {
                    message.append("Question ").append(questionNum).append("\n");
                }
                message.append("\nDo you want to submit the quiz anyway?");

                // Create custom JOptionPane
                JOptionPane optionPane = new JOptionPane(
                    message.toString(),
                    JOptionPane.PLAIN_MESSAGE,  // Remove warning icon
                    JOptionPane.YES_NO_OPTION
                );

                // Get the buttons from the option pane
                JDialog dialog = optionPane.createDialog(mainPanel, "Incomplete Quiz");
                
                // Find and modify the buttons
                for (Component comp : optionPane.getComponents()) {
                    if (comp instanceof JPanel) {
                        for (Component btn : ((JPanel) comp).getComponents()) {
                            if (btn instanceof JButton) {
                                JButton button = (JButton) btn;
                                button.setFocusable(false);
                                button.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
                            }
                        }
                    }
                }

                // Center the dialog relative to the main panel
                dialog.setLocationRelativeTo(mainPanel);
                dialog.setVisible(true);

                // Get the user's choice
                Object selectedValue = optionPane.getValue();
                
                // If user closes the dialog or clicks No, return
                if (selectedValue == null || 
                    (Integer) selectedValue != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Proceed with score calculation
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

            // Collect user answers
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
            // Add confirmation dialog
            int result = JOptionPane.showConfirmDialog(
                mainPanel,
                "Are you sure you want to close this quiz? Your answers will not be saved.",
                "Confirm Close",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                closeQuiz(mainPanel);
                refreshQuizContainer(); // Add this line to refresh the container
            }
        });

        buttonPanel.add(submitButton);
        buttonPanel.add(closeButton);

        // Add all components to main panel
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
        // Show navbars
        showNavbars();
        
        // Remove quiz view panel
        mainPanel.removeAll();
        
        // Create and add list container
        JPanel listContainerPanel = createListContainer();
        mainPanel.add(listContainerPanel, BorderLayout.CENTER);
        
        // Remove the quiz panel from quizViewPanel
        quizViewPanel.remove(quizPanel);
        
        // Revalidate and repaint
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void showQuizOverview(List<FlashCard> flashcards, String subject, int score,
            int total, String quizType, int setId, List<String> userAnswers) {
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

        // Content Panel with GridBagLayout for better spacing
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left

        // Add items for each flashcard
        for (int i = 0; i < flashcards.size(); i++) {
            FlashCard card = flashcards.get(i);
            boolean isCorrect = userAnswers.get(i).trim().toLowerCase()
                .equals(card.term.trim().toLowerCase());

            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align panel to the left

            // Question number and definition
            JLabel definitionLabel = new JLabel("<html><body style='width: 100%'>" + (i + 1) + ". " + card.definition + "</body></html>");
            definitionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
            definitionLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align label to the left
            if (!isCorrect) {
                String numberPart = (i + 1) + ". ";
                String definitionPart = card.definition;
                definitionLabel.setText("<html><body style='width: 100%'><font color='#FF0000'>" + numberPart + "</font>" + definitionPart + "</body></html>");
            }

            // User's answer with icon
            JPanel userAnswerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            userAnswerPanel.setBackground(Color.WHITE);
            userAnswerPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align panel to the left

            // Add icon based on correctness
            ImageIcon icon = new ImageIcon(isCorrect ?
                "src/tasklr/resources/images/correct.png" :
                "src/tasklr/resources/images/wrong.png");
            Image scaledImage = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

            JLabel userAnswerLabel = new JLabel("Your answer: " + userAnswers.get(i));
            userAnswerLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
            if (!isCorrect) {
                userAnswerLabel.setForeground(new Color(0xFF0000));
            }

            userAnswerPanel.add(iconLabel);
            userAnswerPanel.add(userAnswerLabel);

            // Correct answer
            JLabel answerLabel = new JLabel("Correct answer: " + card.term);
            answerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
            answerLabel.setForeground(new Color(0x275CE2));
            answerLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align label to the left

            // Add components to item panel
            itemPanel.add(definitionLabel);
            itemPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            itemPanel.add(userAnswerPanel);
            itemPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            itemPanel.add(answerLabel);

            // Add item panel to content panel
            contentPanel.add(itemPanel, gbc);
        }

        // Add empty space at the bottom to push content up
        gbc.weighty = 1.0;
        contentPanel.add(Box.createVerticalGlue(), gbc);

        // Button Panel
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
            // Remove the overview panel
            quizViewPanel.remove(overviewPanel);
            
            // Update retake statistics
            if (totalQuizRetakedPanel != null) {
                try {
                    String countQuery = "SELECT COUNT(*) as total_retaken FROM quiz_attempts qa1 " +
                        "WHERE user_id = ? AND EXISTS (" +
                        "SELECT 1 FROM quiz_attempts qa2 " +
                        "WHERE qa2.user_id = qa1.user_id " +
                        "AND qa2.set_id = qa1.set_id " +
                        "AND qa2.completion_date < qa1.completion_date)";
                    
                    ResultSet rs = DatabaseManager.executeQuery(countQuery, UserSession.getUserId());
                    if (rs.next()) {
                        totalQuizRetakedPanel.updateCount(rs.getInt("total_retaken") + 1); // Add 1 for current retake
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            // Start the appropriate quiz type
            if ("Identification".equals(quizType)) {
                startIdentificationQuiz(setId, subject, total, 30); // Default 30 minutes for retake
            } else {
                startMultipleChoiceQuiz(setId, subject, total, 30); // Default 30 minutes for retake
            }
            
            // Update home statistics
            updateHomeStatistics();
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

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_COLOR);

        // Add components to main panel
        overviewPanel.add(headerPanel, BorderLayout.NORTH);
        overviewPanel.add(scrollPane, BorderLayout.CENTER);
        overviewPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add to quiz view panel and show
        quizViewPanel.add(overviewPanel, "OVERVIEW");
        cardLayout.show(quizViewPanel, "OVERVIEW");
    }


    private static void updateHomeStatistics() {
        if (totalQuizTakenPanel != null && totalQuizRetakedPanel != null) {
            try {
                String countQuery = """
                    SELECT COUNT(*) as total_taken,
                        SUM(CASE
                            WHEN EXISTS (
                                SELECT 1 FROM quiz_attempts qa2
                                WHERE qa2.user_id = qa1.user_id
                                AND qa2.set_id = qa1.set_id
                                AND qa2.completion_date < qa1.completion_date
                            ) THEN 1
                            ELSE 0
                        END) as total_retaken
                    FROM quiz_attempts qa1
                    WHERE user_id = ?
                """;

                ResultSet rs = DatabaseManager.executeQuery(countQuery, UserSession.getUserId());
                if (rs.next()) {
                    totalQuizTakenPanel.updateCount(rs.getInt("total_taken"));
                    totalQuizRetakedPanel.updateCount(rs.getInt("total_retaken"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                Toast.error("Error updating quiz statistics: " + ex.getMessage());
            }
        }
    }

    // Helper method to create timer panel
    private static JPanel createTimerPanel(int minutes, Runnable onTimeUp) {
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timerPanel.setBackground(Color.WHITE);
        
        JLabel timerLabel = new JLabel(String.format("%02d:00", minutes));
        timerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        timerLabel.setForeground(new Color(0x275CE2));
        
        Timer timer = new Timer(1000, e -> {
            String[] parts = timerLabel.getText().split(":");
            int mins = Integer.parseInt(parts[0]);
            int secs = Integer.parseInt(parts[1]);
            
            if (mins == 0 && secs == 0) {
                ((Timer)e.getSource()).stop();
                onTimeUp.run();
                return;
            }
            
            if (secs == 0) {
                mins--;
                secs = 59;
            } else {
                secs--;
            }
    
            if (mins == 0 && secs <= 59) {
                timerLabel.setForeground(Color.RED);
            }
            
            timerLabel.setText(String.format("%02d:%02d", mins, secs));
        });
        
        timer.start();
        timerPanel.add(new JLabel("Time Remaining: "));
        timerPanel.add(timerLabel);
        
        return timerPanel;
    }



    // Add this getter method to access the quiz container
    public static JPanel getQuizContainer() {
        return quizContainer;
    }

// Add this method if it doesn't exist
public static void showQuizOptions(int setId, String subject) {
    // Count the number of flashcards in the set
    int totalFlashcards = 0;
    try {
        String countQuery = "SELECT COUNT(*) FROM flashcards WHERE set_id = ?";
        ResultSet rs = DatabaseManager.executeQuery(countQuery, setId);
        if (rs.next()) {
            totalFlashcards = rs.getInt(1);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        Toast.error("Error counting flashcards: " + ex.getMessage());
        return;
    }
    
    if (totalFlashcards == 0) {
        Toast.error("This set has no flashcards. Add some flashcards first!");
        return;
    }
    
    // Create dialog components
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Quiz type selection
    JLabel typeLabel = new JLabel("Quiz Type:");
    String[] quizTypes = {"Multiple Choice", "Identification"};
    JComboBox<String> quizTypeCombo = new JComboBox<>(quizTypes);
    
    // Number of items selection
    JLabel itemsLabel = new JLabel("Number of Items:");
    SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
        Math.min(10, totalFlashcards), // initial value
        1,                            // minimum value
        totalFlashcards,              // maximum value
        1                            // step
    );
    JSpinner itemsSpinner = new JSpinner(spinnerModel);
    
    // Time duration selection
    JLabel timeLabel = new JLabel("Time Limit (minutes):");
    Integer[] timeOptions = {5, 10, 15, 20, 30, 45, 60};
    JComboBox<Integer> timeCombo = new JComboBox<>(timeOptions);
    timeCombo.setSelectedItem(15); // Default to 15 minutes
    
    // Add components to panel
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Start Quiz: " + subject), gbc);
    
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    panel.add(typeLabel, gbc);
    gbc.gridx = 1;
    panel.add(quizTypeCombo, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(itemsLabel, gbc);
    gbc.gridx = 1;
    panel.add(itemsSpinner, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(timeLabel, gbc);
    gbc.gridx = 1;
    panel.add(timeCombo, gbc);
    
    // Show dialog
    int result = JOptionPane.showConfirmDialog(
        null, panel, "Start Quiz", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
    );
    
    if (result == JOptionPane.OK_OPTION) {
        String selectedType = (String) quizTypeCombo.getSelectedItem();
        int numberOfItems = (Integer) itemsSpinner.getValue();
        int timeDuration = (Integer) timeCombo.getSelectedItem();

        if ("Identification".equals(selectedType)) {
            startIdentificationQuiz(setId, subject, numberOfItems, timeDuration);
        } else {
            startMultipleChoiceQuiz(setId, subject, numberOfItems, timeDuration);
        }
    }
  }

    // Add these methods to control navbar visibility
    private static void hideNavbars() {
        // Get the Tasklr frame
        Container topLevelContainer = SwingUtilities.getWindowAncestor(mainPanel);
        if (topLevelContainer instanceof JFrame) {
            JFrame frame = (JFrame) topLevelContainer;
            
            // Hide the main navbar (west component in Tasklr)
            if (frame.getContentPane().getLayout() instanceof BorderLayout) {
                Component navbar = ((BorderLayout)frame.getContentPane().getLayout()).getLayoutComponent(BorderLayout.WEST);
                if (navbar != null) {
                    navbar.setVisible(false);
                }
            }
            
            // Hide the StudyPanel navbar
            Component studyNavbar = findStudyPanelNavbar();
            if (studyNavbar != null) {
                studyNavbar.setVisible(false);
            }
            
            // Revalidate and repaint
            frame.revalidate();
            frame.repaint();
        }
    }

    private static void showNavbars() {
        // Get the Tasklr frame
        Container topLevelContainer = SwingUtilities.getWindowAncestor(mainPanel);
        if (topLevelContainer instanceof JFrame) {
            JFrame frame = (JFrame) topLevelContainer;
            
            // Show the main navbar
            if (frame.getContentPane().getLayout() instanceof BorderLayout) {
                Component navbar = ((BorderLayout)frame.getContentPane().getLayout()).getLayoutComponent(BorderLayout.WEST);
                if (navbar != null) {
                    navbar.setVisible(true);
                }
            }
            
            // Show the StudyPanel navbar
            Component studyNavbar = findStudyPanelNavbar();
            if (studyNavbar != null) {
                studyNavbar.setVisible(true);
            }
            
            // Revalidate and repaint
            frame.revalidate();
            frame.repaint();
        }
    }

    private static Component findStudyPanelNavbar() {
        // Find the StudyPanel's navbar by traversing the component hierarchy
        Container parent = mainPanel.getParent();
        while (parent != null) {
            if (parent instanceof JPanel) {
                // Look for the nav panel which is typically the first (NORTH) component in BorderLayout
                if (parent.getLayout() instanceof BorderLayout) {
                    Component navComponent = ((BorderLayout)parent.getLayout()).getLayoutComponent(BorderLayout.NORTH);
                    if (navComponent != null) {
                        return navComponent;
                    }
                }
            }
            parent = parent.getParent();
        }
        return null;
    }
}
