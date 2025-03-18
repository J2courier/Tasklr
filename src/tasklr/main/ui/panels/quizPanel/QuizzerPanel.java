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
            // TODO: Implement quiz type specific functionality
            if ("Identification".equals(selectedType)) {
                Toast.info("Starting Identification Quiz... (To be implemented)");
            } else {
                Toast.info("Starting Multiple Choice Quiz... (To be implemented)");
            }
        }
    }
}
        
