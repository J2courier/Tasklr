package tasklr.main.ui.panels.quizPanel;

import javax.swing.*;
import tasklr.utilities.HoverPanelEffect;
import tasklr.utilities.ComponentUtil;
import tasklr.utilities.createButton;
import tasklr.utilities.createPanel;
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
    private static final Color TEXTFIELD_COLOR = new Color(0xFFFFFF);
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
        JPanel mainPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), new Dimension(600, 0));
        mainPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, LIST_ITEM_HOVER_BORDER));

        JPanel titlePanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), null);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Available Flashcards");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        quizContainer = createPanel.panel(BACKGROUND_COLOR, null, null);
        quizContainer.setLayout(new BoxLayout(quizContainer, BoxLayout.Y_AXIS));
        quizContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(quizContainer);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        
        // Improve scroll speed - multiply by panel height plus spacing
        scrollPane.getVerticalScrollBar().setUnitIncrement((80 + 5) * 3);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Initial load of flashcards
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
            String query = "SELECT term, definition FROM quizzes WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, UserSession.getUserId());
                try (ResultSet rs = stmt.executeQuery()) {
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
                        JLabel noItemsLabel = new JLabel("No flashcards available for quiz!");
                        noItemsLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
                        noItemsLabel.setForeground(TEXT_COLOR);
                        noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        quizContainer.add(noItemsLabel);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showCenteredOptionPane(null, "Error fetching flashcards: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Ensure UI updates happen on EDT
        SwingUtilities.invokeLater(() -> {
            quizContainer.revalidate();
            quizContainer.repaint();
            
            if (scrollPane != null) {
                scrollPane.getViewport().revalidate();
                scrollPane.getViewport().repaint();
            }
        });
    }

    private static JPanel createQuizItemPanel(String term, String definition) {
        JPanel panel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), new Dimension(0, 80));
        panel.setBorder(BorderFactory.createLineBorder(LIST_ITEM_HOVER_BORDER, 1));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JPanel contentPanel = createPanel.panel(null, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        contentPanel.setOpaque(false);
        
        JPanel textPanel = createPanel.panel(null, new GridLayout(2, 1, 0, 2), null);
        textPanel.setOpaque(false);
        
        JLabel termLabel = new JLabel(term);
        termLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        termLabel.setForeground(TEXT_COLOR);
        
        String shortDefinition = definition.length() > 50 ? definition.substring(0, 47) + "..." : definition;
        JLabel definitionLabel = new JLabel(shortDefinition);
        definitionLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        definitionLabel.setForeground(TEXT_COLOR);
        
        textPanel.add(termLabel);
        textPanel.add(definitionLabel);
        
        contentPanel.add(textPanel, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);

        // Add hover effect
        new HoverPanelEffect(panel, LIST_ITEM_COLOR, LIST_ITEM_HOVER_BG);
        
        return panel;
    }
}
        
