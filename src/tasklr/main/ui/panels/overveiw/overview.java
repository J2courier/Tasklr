package tasklr.main.ui.panels.overveiw;

import javax.swing.*;
import java.awt.*;
import tasklr.main.ui.components.ComponentFactory;
import tasklr.main.ui.components.TaskCounterPanel;
import tasklr.utilities.RefreshUI;
import tasklr.utilities.createPanel;
import tasklr.main.ui.panels.TaskPanel.TaskFetcher;
import tasklr.main.ui.panels.quizPanel.StudyPanel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class overview {
    // Color constants
    private static final Color PRIMARY_COLOR = new Color(0x275CE2);    // Primary blue
    private static final Color SECONDARY_COLOR = new Color(0xE0E3E2);  // Light gray
    private static final Color BACKGROUND_COLOR = Color.WHITE; // Light background
    private static final Color CARD_COLOR = new Color(0xFFFFFF);       // White
    private static final Color TEXT_DARK = new Color(0x1D1D1D);        // Dark text
    private static final Color BORDER_COLOR = new Color(0xE0E0E0);     // Border gray

    private static TaskCounterPanel pendingTasksPanel;
    private static TaskCounterPanel completedTasksPanel;
    private static TaskCounterPanel totalTasksPanel;
    
    // Add new panel variables for flashcard statistics
    private static TaskCounterPanel totalFlashcardSetsPanel;
    private static TaskCounterPanel pendingQuizProgressPanel;
    private static TaskCounterPanel completedQuizProgressPanel;

    public static JPanel createOverview(String username) {            
        JPanel mainPanel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), new Dimension(400, 0));

        JPanel spacer = createPanel.panel(BACKGROUND_COLOR, null, new Dimension(0, 300));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = new Insets(20, 20, 20, 20);

        // Header Section
        gbc.gridy = 0;
        mainPanel.add(createHeaderSection(username), gbc);

        // Task Statistics Section
        gbc.gridy = 1;
        mainPanel.add(createStatisticsSection(), gbc);

        // Flashcard Statistics Section
        gbc.gridy = 2;
        mainPanel.add(createFlashcardStatisticsSection(), gbc);

        // Recent Quiz Progress Section
        gbc.gridy = 3;
        mainPanel.add(createRecentQuizProgressSection(), gbc);

        gbc.gridy = 4;
        gbc.weighty = 1.0;
        mainPanel.add(spacer, gbc);

        return mainPanel;
    }

    // Add new method for flashcard statistics section
    private static JPanel createFlashcardStatisticsSection() {
        JPanel statsPanel = createPanel.panel(null, new GridLayout(1, 3, 15, 0), null);
        statsPanel.setOpaque(false);

        totalFlashcardSetsPanel = new TaskCounterPanel(0, "Total Sets");
        pendingQuizProgressPanel = new TaskCounterPanel(0, "Pending Quiz");
        completedQuizProgressPanel = new TaskCounterPanel(0, "Completed Quiz");

        // Style and add counter panels
        for (TaskCounterPanel panel : new TaskCounterPanel[]{
            totalFlashcardSetsPanel, 
            pendingQuizProgressPanel, 
            completedQuizProgressPanel
        }) {
            JPanel card = panel.createPanel();
            styleCard(card);
            statsPanel.add(card);
        }

        return statsPanel;
    }

    public static void refreshTaskCounters() {
        if (pendingTasksPanel != null && completedTasksPanel != null && 
            totalTasksPanel != null && totalFlashcardSetsPanel != null && 
            pendingQuizProgressPanel != null && completedQuizProgressPanel != null) {
            
            RefreshUI refreshUI = new RefreshUI(
                totalTasksPanel, 
                pendingTasksPanel, 
                completedTasksPanel
            );
            refreshUI.execute();
        }
    }

    private static JPanel createHeaderSection(String username) {
        JPanel headerPanel = createPanel.panel(PRIMARY_COLOR, new BorderLayout(20, 0), null);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Left side - Welcome message
        JPanel welcomePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        welcomePanel.setOpaque(false);

        JLabel dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        dateLabel.setForeground(new Color(0xFFFFFF, true));
        dateLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));

        JLabel welcomeLabel = new JLabel("WELCOME, " + username);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 28));

        welcomePanel.add(dateLabel);
        welcomePanel.add(welcomeLabel);

        // Right side - Quick summary with notification icon
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);

        // Add notification icon
        try {
            ImageIcon notifIcon = new ImageIcon("C://Users//ADMIN//Desktop//Tasklr//resource//icons//NotificationIcon.png");
            Image scaledImage = notifIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            JLabel notificationLabel = new JLabel(new ImageIcon(scaledImage));
            notificationLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            notificationLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    notificationLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            });
            summaryPanel.add(notificationLabel);
        } catch (Exception e) {
            System.err.println("Failed to load notification icon: " + e.getMessage());
        }

        headerPanel.add(welcomePanel, BorderLayout.WEST);
        headerPanel.add(summaryPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private static JPanel createStatisticsSection() {
        JPanel statsPanel = createPanel.panel(null, new GridLayout(1, 3, 15, 0), null);
        statsPanel.setOpaque(false);

        pendingTasksPanel = new TaskCounterPanel(0, "Pending Tasks");
        completedTasksPanel = new TaskCounterPanel(0, "Completed");
        totalTasksPanel = new TaskCounterPanel(0, "Total Tasks");

        // Style and add counter panels
        for (TaskCounterPanel panel : new TaskCounterPanel[]{pendingTasksPanel, completedTasksPanel, totalTasksPanel}) {
            JPanel card = panel.createPanel();
            styleCard(card);
            statsPanel.add(card);
        }

        // Start the refresh worker
        RefreshUI refreshUI = new RefreshUI(totalTasksPanel, pendingTasksPanel, completedTasksPanel);
        refreshUI.execute();
        return statsPanel;
    }

    private static JPanel createRecentQuizProgressSection() {
        JPanel recentQuizPanel = createPanel.panel(CARD_COLOR, new BorderLayout(), null);
        styleCard(recentQuizPanel);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Recent Quiz Progress");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);

        JButton viewAllButton = new JButton("View All");
        viewAllButton.setForeground(PRIMARY_COLOR);
        viewAllButton.setBorderPainted(false);
        viewAllButton.setContentAreaFilled(false);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(viewAllButton, BorderLayout.EAST);

        // Quiz progress list
        JPanel quizProgressListPanel = new JPanel();
        quizProgressListPanel.setLayout(new BoxLayout(quizProgressListPanel, BoxLayout.Y_AXIS));
        quizProgressListPanel.setOpaque(false);
        // Quiz progress items will be added here later

        recentQuizPanel.add(headerPanel, BorderLayout.NORTH);
        recentQuizPanel.add(quizProgressListPanel, BorderLayout.CENTER);

        return recentQuizPanel;
    }

    

    private static void styleCard(JPanel panel) {
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setBackground(CARD_COLOR);
    }
}
