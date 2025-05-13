package tasklr.main.ui.panels.Home;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.toedter.calendar.JDateChooser;
import tasklr.main.ui.components.TaskCounterPanel;
import java.util.Date;
import java.text.SimpleDateFormat;
import tasklr.main.ui.panels.TaskPanel.TaskFetcher;
import tasklr.utilities.*;
import tasklr.authentication.UserSession;
import java.sql.*;
import tasklr.main.ui.panels.quizPanel.QuizzerPanel;

public class HomePanel {
    private static JPanel tasksContainer;
    private static JPanel tasksWrapper;
    private static JPanel recentTasksWrapper;
    private static final int CONTAINER_WIDTH = 400;
    private static final int REFRESH_INTERVAL = 5000;
    private static JLabel welcomeLabel;
    private static JScrollPane taskListContainer;
    private static JScrollPane recentTaskListContainer;
    private static final Color PRIMARY_COLOR = new Color(0x275CE2);
    private static final Color SECONDARY_COLOR = new Color(0xE0E3E2);
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color CARD_COLOR = new Color(0xFFFFFF);
    private static final Color TEXT_DARK = new Color(0x1D1D1D);
    private static final Color BORDER_COLOR = new Color(0xE0E0E0);
    private static final Color CLOSE_COLOR = new Color(0x404040);
    private static final Color TASK_DONE_COLOR = new Color(0x34D399);
    private static final Color TASK_PENDING_COLOR = new Color(0xFF0000);
    private static final Color DROP_COLOR = new Color(0xFB2C36);
    private static final Color COMPLETED_COLOR = new Color(0x17BD0F);
    private static final int WELCOME_HEADER_HEIGHT = 100;
    private static TaskCounterPanel pendingTasksPanel;
    private static TaskCounterPanel completedTasksPanel;
    private static TaskCounterPanel totalTasksPanel;
    private static TaskCounterPanel totalFlashcardSetsPanel;
    private static TaskCounterPanel totalQuizTakenPanel;
    private static TaskCounterPanel totalQuizRetakedPanel;
    private static UIRefreshManager refreshManager;
    
    // Add these static variables to track session goals
    private static int dailyGoalTarget = 2; // Start with 2 quizzes
    private static int dailyGoalCompleted = 0;
    private static JLabel dailyProgressPercentLabel;
    private static JProgressBar dailyProgressBar;
    private static JLabel dailyProgressStatusLabel;
    
    public static JPanel createOverview(String username) {            
        JPanel mainPanel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), new Dimension(400, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = new Insets(10, 20, 10, 20);

        gbc.gridy = 0;
        mainPanel.add(createHeaderSection(username), gbc);

        gbc.gridy = 1;
        mainPanel.add(createStatisticsSection(), gbc);

        gbc.gridy = 2;
        mainPanel.add(createFlashcardStatisticsSection(), gbc);

        QuizzerPanel.initializeHomePanel();

        JPanel taskContainersPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        taskContainersPanel.setBackground(BACKGROUND_COLOR);

        taskContainersPanel.add(createTaskContainer());
        taskContainersPanel.add(createProgressContainer()); // Changed from createRecentTaskContainer()

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        mainPanel.add(taskContainersPanel, gbc);

        refreshTasksList();
        refreshTaskProgress();

        return mainPanel;
    }

    private static void initializeRefresh() {
        refreshManager.startRefresh(UIRefreshManager.TASK_LIST, () -> {
            refreshTasksList();
        }, REFRESH_INTERVAL);

        refreshManager.startRefresh(UIRefreshManager.TASK_COUNTER, () -> {
            refreshTaskCounters();
        }, REFRESH_INTERVAL);
        
        refreshManager.startRefresh(UIRefreshManager.QUIZ_STATS, () -> {
            refreshQuizProgress();
        }, REFRESH_INTERVAL);
    }

    public static void cleanup() {
        if (refreshManager != null) {
            refreshManager.stopRefresh(UIRefreshManager.TASK_LIST);
            refreshManager.stopRefresh(UIRefreshManager.TASK_COUNTER);
            refreshManager.stopRefresh(UIRefreshManager.QUIZ_STATS);
        }
    }

    private static JPanel createFlashcardStatisticsSection() {
        JPanel statsPanel = createPanel.panel(null, new GridLayout(1, 3, 15, 0), null);
        statsPanel.setOpaque(false);

        totalFlashcardSetsPanel = new TaskCounterPanel(0, "Total Flashcard Sets");
        totalQuizTakenPanel = new TaskCounterPanel(0, "Total Quiz Taken");
        totalQuizRetakedPanel = new TaskCounterPanel(0, "Total Quiz Retaked");

        for (TaskCounterPanel panel : new TaskCounterPanel[]{
            totalFlashcardSetsPanel, 
            totalQuizTakenPanel, 
            totalQuizRetakedPanel
        }) {
            JPanel card = panel.createPanel();
            styleCard(card);
            statsPanel.add(card);
        }

        try {
            String countQuery = """
                SELECT 
                    COUNT(*) as total_taken,
                    SUM(CASE 
                        WHEN EXISTS (
                            SELECT 1 FROM quiz_attempts qa2 
                            WHERE qa2.user_id = qa1.user_id 
                            AND qa2.set_id = qa1.set_id 
                            AND qa2.completion_date < qa1.completion_date
                        ) THEN 1 
                        ELSE 0 
                    END) as total_retaken,
                    (SELECT COUNT(*) FROM flashcard_sets WHERE user_id = ?) as total_sets
                FROM quiz_attempts qa1 
                WHERE user_id = ?
            """;
            
            ResultSet rs = DatabaseManager.executeQuery(countQuery, UserSession.getUserId(), UserSession.getUserId());
            if (rs.next()) {
                totalQuizTakenPanel.updateCount(rs.getInt("total_taken"));
                totalQuizRetakedPanel.updateCount(rs.getInt("total_retaken"));
                totalFlashcardSetsPanel.updateCount(rs.getInt("total_sets"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error fetching quiz statistics: " + ex.getMessage());
        }

        return statsPanel;
    }

    public static void refreshTaskCounters() {
        System.out.println("[Home Panel] Refreshing task counters at: ");
        if (pendingTasksPanel != null && completedTasksPanel != null && totalTasksPanel != null) {
            try {
                Map<String, Integer> taskCounts = new TaskFetcher().getTaskCounts();
                if (taskCounts != null) {
                    totalTasksPanel.updateCount(taskCounts.getOrDefault("total", 0));
                    pendingTasksPanel.updateCount(taskCounts.getOrDefault("pending", 0));
                    completedTasksPanel.updateCount(taskCounts.getOrDefault("completed", 0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static JPanel createHeaderSection(String username) {
        JPanel headerPanel = createPanel.panel(PRIMARY_COLOR, new BorderLayout(20, 0), 
            new Dimension(0, WELCOME_HEADER_HEIGHT));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setOpaque(false);

        welcomePanel.add(Box.createVerticalGlue());

        JLabel dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        dateLabel.setForeground(new Color(0xFFFFFF, true));
        dateLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomePanel.add(dateLabel);

        welcomePanel.add(Box.createVerticalStrut(5));

        welcomeLabel = new JLabel("WELCOME, " + username);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomePanel.add(welcomeLabel);

        welcomePanel.add(Box.createVerticalGlue());

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        summaryPanel.setOpaque(false);
        
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

        for (TaskCounterPanel panel : new TaskCounterPanel[]{pendingTasksPanel, completedTasksPanel, totalTasksPanel}) {
            JPanel card = panel.createPanel();
            styleCard(card);
            statsPanel.add(card);
        }

        return statsPanel;
    }

    private static void styleCard(JPanel card) {
        card.setPreferredSize(new Dimension(0, 150));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 20, 5, 20)
        ));
        card.setBackground(CARD_COLOR);
    }
        

    private static JPanel createTaskContainer() {
        JPanel taskContainer = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(CONTAINER_WIDTH, 0));
        taskContainer.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JPanel headerPanel = createPanel.panel(CARD_COLOR, new BorderLayout(), new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JLabel remainingTaskLabel = new JLabel("Remaining Tasks");
        remainingTaskLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        remainingTaskLabel.setForeground(TEXT_DARK);
        
        JButton refreshButton = new JButton();
        refreshButton.setPreferredSize(new Dimension(100, 40));
        refreshButton.setText("⟳"); 
        refreshButton.setBorderPainted(false);
        refreshButton.setContentAreaFilled(true);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setToolTipText("Refresh tasks");
        refreshButton.setBackground(COMPLETED_COLOR);
        refreshButton.setForeground(Color.WHITE);
        
        refreshButton.addActionListener(e -> {
            refreshButton.setEnabled(false);
            
            // Use a more direct approach to update progress bars
            try {
                // Update task progress
                Map<String, Integer> taskCounts = new TaskFetcher().getTaskCounts();
                if (taskCounts != null) {
                    int total = taskCounts.getOrDefault("total", 0);
                    int completed = taskCounts.getOrDefault("completed", 0);
                    int pending = taskCounts.getOrDefault("pending", 0);
                    int percentage = (total > 0) ? (completed * 100) / total : 0;
                    
                    // Force update all progress panels
                    forceUpdateAllProgressPanels(percentage, total, completed, pending);
                }
                
                // Update quiz progress
                String query = """
                    SELECT 
                        (SELECT COUNT(*) FROM flashcard_sets WHERE user_id = ?) AS total_sets,
                        (SELECT COUNT(DISTINCT set_id) FROM quiz_attempts WHERE user_id = ?) AS sets_quizzed
                """;
                
                ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId(), UserSession.getUserId());
                if (rs.next()) {
                    int totalSets = rs.getInt("total_sets");
                    int setsQuizzed = rs.getInt("sets_quizzed");
                    int percentage = (totalSets > 0) ? (setsQuizzed * 100) / totalSets : 0;
                    
                    // Force update quiz progress panel
                    forceUpdateQuizProgressPanel(percentage, totalSets, setsQuizzed);
                }
                
                // Also refresh task list and counters
                refreshTasksList();
                refreshTaskCounters();
                
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.error("Error updating progress: " + ex.getMessage());
            }
            
            // Re-enable the button after a delay
            Timer timer = new Timer(1000, event -> refreshButton.setEnabled(true));
            timer.setRepeats(false);
            timer.start();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(refreshButton);

        headerPanel.add(remainingTaskLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        tasksWrapper = new JPanel();
        tasksWrapper.setLayout(new BoxLayout(tasksWrapper, BoxLayout.Y_AXIS));
        tasksWrapper.setBackground(BACKGROUND_COLOR);
        tasksWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        taskListContainer = new JScrollPane(tasksWrapper);
        taskListContainer.setBorder(null);
        taskListContainer.setBackground(BACKGROUND_COLOR);
        
        taskListContainer.getVerticalScrollBar().setUnitIncrement(32);
        taskListContainer.getVerticalScrollBar().setBlockIncrement(128);
        taskListContainer.getVerticalScrollBar().putClientProperty("JScrollBar.smoothScrolling", true);
        
        tasksWrapper.addMouseWheelListener(e -> {
            JScrollBar scrollBar = taskListContainer.getVerticalScrollBar();
            int direction = e.getWheelRotation();
            int increment = scrollBar.getUnitIncrement() * 3;
            int newValue = scrollBar.getValue() + (direction * increment);
            scrollBar.setValue(newValue);
        });

        taskContainer.add(headerPanel, BorderLayout.NORTH);
        taskContainer.add(taskListContainer, BorderLayout.CENTER);
        
        return taskContainer;
    }

    public static void refreshTasksList() {
        if (tasksWrapper == null) return;
        
        tasksWrapper.removeAll();
        
        try {
            String query = "SELECT title, status, due_date FROM tasks " +
                          "WHERE user_id = ? AND status = 'pending' " +
                          "ORDER BY due_date ASC";
                          
            ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId());

            boolean hasItems = false;
            while (rs.next()) {
                hasItems = true;
                String title = rs.getString("title");
                String status = rs.getString("status");
                java.sql.Date dueDate = rs.getDate("due_date");

                JPanel taskItemPanel = createTaskItemPanel(title, status, dueDate);
                tasksWrapper.add(taskItemPanel);
                tasksWrapper.add(Box.createRigidArea(new Dimension(0, 5))); // Reduced from 10 to 5

            }

            if (!hasItems) {
                JPanel centeringPanel = new JPanel(new GridBagLayout());
                centeringPanel.setBackground(BACKGROUND_COLOR);
                
                JLabel noTasksLabel = new JLabel("No tasks found");
                noTasksLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noTasksLabel.setForeground(TEXT_DARK);
                noTasksLabel.setHorizontalAlignment(SwingConstants.CENTER);
                
                centeringPanel.add(noTasksLabel);
                
                tasksWrapper.add(centeringPanel);
                
                tasksWrapper.add(Box.createVerticalGlue());
            }

            tasksWrapper.revalidate();
            tasksWrapper.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error fetching tasks: " + ex.getMessage());
        }
    }

    private static JPanel createTaskItemPanel(String title, String status, java.sql.Date dueDate) {
        JPanel taskPanel = createPanel.panel(Color.WHITE, new BorderLayout(2, 0), null);
        taskPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        taskPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),         // Add 1px border
            BorderFactory.createEmptyBorder(10, 25, 10, 25)         // Keep existing padding
        ));

        JPanel contentPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 20));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel dateLabel;
        if (dueDate != null) {
            dateLabel = new JLabel("Due: " + dueDate.toString());
            dateLabel.setForeground(new Color(0xFF0000)); // Red for tasks with due date
        } else {
            dateLabel = new JLabel("Due: None");
            dateLabel.setForeground(new Color(0x17BD0F)); // Green for tasks without due date
        }
        dateLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        contentPanel.add(dateLabel, BorderLayout.SOUTH);

        JLabel statusLabel = new JLabel(status.toUpperCase());
        statusLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16 ));
        statusLabel.setForeground(status.equals("completed") ? TASK_DONE_COLOR : TASK_PENDING_COLOR);
        
        JPanel rightPanel = createPanel.panel(Color.WHITE, new FlowLayout(FlowLayout.RIGHT), null);
        rightPanel.add(statusLabel);

        taskPanel.add(contentPanel, BorderLayout.CENTER);
        taskPanel.add(rightPanel, BorderLayout.EAST);

        taskPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                taskPanel.setBackground(new Color(0xF9FAFB));
                contentPanel.setBackground(new Color(0xF9FAFB));
                rightPanel.setBackground(new Color(0xF9FAFB));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                taskPanel.setBackground(Color.WHITE);
                contentPanel.setBackground(Color.WHITE);
                rightPanel.setBackground(Color.WHITE);
            }

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showTaskDetailsPopup(title, status, dueDate, evt.getComponent());
            }
        });

        return taskPanel;
    }

    private static void showTaskDetailsPopup(String title, String status, java.sql.Date dueDate, Component parent) {
        JPanel popupPanel = new JPanel(new GridBagLayout());
        popupPanel.setBackground(Color.WHITE);
        popupPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE5E7EB), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        popupPanel.add(titleLabel, gbc);

        JLabel dateLabel = new JLabel("Due: " + (dueDate != null ? dueDate.toString() : "None"));
        dateLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(0x6D6D6D));
        popupPanel.add(dateLabel, gbc);

        JLabel statusLabel = new JLabel("Status: " + status);
        statusLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        statusLabel.setForeground(status.equals("completed") ? TASK_DONE_COLOR : TASK_PENDING_COLOR);
        popupPanel.add(statusLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton dropBtn = createButton.button("Drop Task", DROP_COLOR, Color.WHITE, null, false);
        dropBtn.setPreferredSize(new Dimension(100, 35));
        dropBtn.addActionListener(e -> {
            JOptionPane optionPane = new JOptionPane(
                "Are you sure you want to delete this task?",
                JOptionPane.WARNING_MESSAGE,
                JOptionPane.YES_NO_OPTION
            );
            
            JDialog dialog = optionPane.createDialog(parent, "Confirm Delete");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            
            Object selectedValue = optionPane.getValue();
            if (selectedValue != null && selectedValue.equals(JOptionPane.YES_OPTION)) {
                try {
                    String query = "DELETE FROM tasks WHERE title = ? AND user_id = ?";
                    DatabaseManager.executeUpdate(query, title, UserSession.getUserId());
                    
                    // Close the popup
                    Window popup = SwingUtilities.getWindowAncestor(popupPanel);
                    popup.dispose();
                    
                    // Update task progress immediately
                    Map<String, Integer> taskCounts = new TaskFetcher().getTaskCounts();
                    if (taskCounts != null) {
                        int total = taskCounts.getOrDefault("total", 0);
                        int completed = taskCounts.getOrDefault("completed", 0);
                        int pending = taskCounts.getOrDefault("pending", 0);
                        int percentage = (total > 0) ? (completed * 100) / total : 0;
                        
                        // Force update all progress panels
                        forceUpdateAllProgressPanels(percentage, total, completed, pending);
                    }
                    
                    // Also refresh task list and counters
                    refreshTasksList();
                    refreshTaskCounters();
                    
                    // Also refresh the task list container in task.java
                    tasklr.main.ui.panels.TaskPanel.task.refreshTaskContainer();
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        parent,
                        "Error deleting task: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        JButton doneBtn = createButton.button("Mark as done", COMPLETED_COLOR, Color.WHITE, null, false);
        doneBtn.setPreferredSize(new Dimension(100, 35));
        doneBtn.setEnabled(!status.equals("COMPLETED"));
        doneBtn.addActionListener(e -> {
            try {
                String query = "UPDATE tasks SET status = 'COMPLETED' WHERE title = ? AND user_id = ?";
                DatabaseManager.executeUpdate(query, title, UserSession.getUserId());
                
                // Close the popup
                Window popup = SwingUtilities.getWindowAncestor(popupPanel);
                popup.dispose();
                
                // Update task progress immediately
                Map<String, Integer> taskCounts = new TaskFetcher().getTaskCounts();
                if (taskCounts != null) {
                    int total = taskCounts.getOrDefault("total", 0);
                    int completed = taskCounts.getOrDefault("completed", 0);
                    int pending = taskCounts.getOrDefault("pending", 0);
                    int percentage = (total > 0) ? (completed * 100) / total : 0;
                    
                    // Force update all progress panels
                    forceUpdateAllProgressPanels(percentage, total, completed, pending);
                }
                
                // Also refresh task list and counters
                refreshTasksList();
                refreshTaskCounters();
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    parent,
                    "Error updating task status: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JButton closeBtn = createButton.button("Close", CLOSE_COLOR, Color.WHITE, null, false);
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.addActionListener(e -> {
            Window popup = SwingUtilities.getWindowAncestor(popupPanel);
            popup.dispose();
        });

        buttonPanel.add(dropBtn);
        buttonPanel.add(doneBtn);
        buttonPanel.add(closeBtn);
        
        gbc.insets = new Insets(15, 5, 5, 5);
        popupPanel.add(buttonPanel, gbc);

        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setContentPane(popupPanel);
        dialog.pack();
        
        dialog.setLocationRelativeTo(null);
        
        dialog.setVisible(true);
    }

    private static void refreshTaskContainer() {
        Container topLevelContainer = SwingUtilities.getWindowAncestor(tasksContainer);
        if (topLevelContainer instanceof JFrame) {
            JFrame frame = (JFrame) topLevelContainer;
            JPanel body = (JPanel) frame.getContentPane().getComponent(0);
            CardLayout cardLayout = (CardLayout) body.getLayout();
            
            try {
                body.remove(body.getComponent(body.getComponentCount() - 1));
            } catch (ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
            
            JPanel homePanel = createOverview(UserSession.getUsername());
            body.add(homePanel, "homePanel");
            cardLayout.show(body, "homePanel");
            
            body.revalidate();
            body.repaint();
        }
    }

    public static TaskCounterPanel getTotalQuizTakenPanel() {
        return totalQuizTakenPanel;
    }

    public static TaskCounterPanel getTotalQuizRetakedPanel() {
        return totalQuizRetakedPanel;
    }

    private static JPanel createProgressContainer() {
        JPanel progressContainer = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(CONTAINER_WIDTH, 0));
        progressContainer.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        JPanel headerPanel = createPanel.panel(CARD_COLOR, new BorderLayout(), new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JLabel progressLabel = new JLabel("Task Progress");
        progressLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        progressLabel.setForeground(TEXT_DARK);
        headerPanel.add(progressLabel, BorderLayout.WEST);

        // Main content panel with fixed layout
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add overall progress components
        JPanel overallProgressPanel = createTaskProgressPanel("Overall Progress");
        contentPanel.add(overallProgressPanel);
        
        // Add spacing between panels
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Add daily progress components
        JPanel dailyProgressPanel = createDailyTaskProgressPanel();
        contentPanel.add(dailyProgressPanel);
        
        // Add spacing between panels
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Add quiz progress components
        JPanel quizProgressPanel = createQuizProgressPanel();
        contentPanel.add(quizProgressPanel);
        
        // Add to main container
        progressContainer.add(headerPanel, BorderLayout.NORTH);
        progressContainer.add(contentPanel, BorderLayout.CENTER);
        
        return progressContainer;
    }

    private static JPanel createTaskProgressPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Make panel have fixed height
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        panel.setPreferredSize(new Dimension(CONTAINER_WIDTH - 30, 160));
        
        // Section title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress percentage
        JLabel percentLabel = new JLabel("0%");
        percentLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        percentLabel.setForeground(PRIMARY_COLOR);
        percentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(CONTAINER_WIDTH - 80, 20));
        progressBar.setMaximumSize(new Dimension(CONTAINER_WIDTH - 80, 20));
        progressBar.setForeground(COMPLETED_COLOR);
        progressBar.setBackground(new Color(0xE0E0E0));
        
        // Status label
        JLabel statusLabel = new JLabel("No tasks to complete");
        statusLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        statusLabel.setForeground(TEXT_DARK);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components with spacing
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(percentLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(progressBar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(statusLabel);
        
        // Update progress initially
        updateTaskProgress(percentLabel, progressBar, statusLabel);
        
        return panel;
    }

    private static JPanel createDailyTaskProgressPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Make panel have fixed height
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        panel.setPreferredSize(new Dimension(CONTAINER_WIDTH - 30, 160));
        
        // Section title
        JLabel titleLabel = new JLabel("Daily Progress");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress percentage
        dailyProgressPercentLabel = new JLabel("0%");
        dailyProgressPercentLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        dailyProgressPercentLabel.setForeground(PRIMARY_COLOR);
        dailyProgressPercentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress bar
        dailyProgressBar = new JProgressBar(0, 100);
        dailyProgressBar.setValue(0);
        dailyProgressBar.setStringPainted(false);
        dailyProgressBar.setPreferredSize(new Dimension(CONTAINER_WIDTH - 80, 20));
        dailyProgressBar.setMaximumSize(new Dimension(CONTAINER_WIDTH - 80, 20));
        dailyProgressBar.setForeground(COMPLETED_COLOR);
        dailyProgressBar.setBackground(new Color(0xE0E0E0));
        
        // Status label
        dailyProgressStatusLabel = new JLabel("Take " + dailyGoalTarget + " quizzes today");
        dailyProgressStatusLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        dailyProgressStatusLabel.setForeground(TEXT_DARK);
        dailyProgressStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components with spacing
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(dailyProgressPercentLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(dailyProgressBar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(dailyProgressStatusLabel);
        
        // Update daily progress initially
        updateDailyProgress();
        
        return panel;
    }

    private static JPanel createQuizProgressPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Make panel have fixed height
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        panel.setPreferredSize(new Dimension(CONTAINER_WIDTH - 30, 160));
        
        // Section title
        JLabel titleLabel = new JLabel("Quiz Progress");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress percentage
        JLabel percentLabel = new JLabel("0%");
        percentLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        percentLabel.setForeground(PRIMARY_COLOR);
        percentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(CONTAINER_WIDTH - 80, 20));
        progressBar.setMaximumSize(new Dimension(CONTAINER_WIDTH - 80, 20));
        progressBar.setForeground(COMPLETED_COLOR);
        progressBar.setBackground(new Color(0xE0E0E0));
        
        // Status label
        JLabel statusLabel = new JLabel("No quizzes taken yet");
        statusLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        statusLabel.setForeground(TEXT_DARK);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components with spacing
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(percentLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(progressBar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(statusLabel);
        
        // Update quiz progress initially
        updateQuizProgress(percentLabel, progressBar, statusLabel);
        
        return panel;
    }

    public static void updateTaskProgress(JLabel percentLabel, JProgressBar progressBar, JLabel statusLabel) {
        try {
            Map<String, Integer> taskCounts = new TaskFetcher().getTaskCounts();
            
            if (taskCounts != null) {
                int total = taskCounts.getOrDefault("total", 0);
                int completed = taskCounts.getOrDefault("completed", 0);
                int pending = taskCounts.getOrDefault("pending", 0);
                
                // Calculate percentage
                int percentage = (total > 0) ? (completed * 100) / total : 0;
                
                // Update UI components
                percentLabel.setText(percentage + "%");
                progressBar.setValue(percentage);
                
                // Update status message
                if (total == 0) {
                    statusLabel.setText("No tasks to complete");
                } else if (pending == 0) {
                    statusLabel.setText("All tasks completed!");
                } else {
                    statusLabel.setText(pending + " task" + (pending > 1 ? "s" : "") + " remaining");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.error("Error updating progress: " + e.getMessage());
        }
    }

    private static void updateQuizProgress(JLabel percentLabel, JProgressBar progressBar, JLabel statusLabel) {
        try {
            // Query to get total flashcard sets and total quizzes taken
            String query = """
                SELECT 
                    (SELECT COUNT(*) FROM flashcard_sets WHERE user_id = ?) AS total_sets,
                    (SELECT COUNT(DISTINCT set_id) FROM quiz_attempts WHERE user_id = ?) AS sets_quizzed
            """;
            
            ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId(), UserSession.getUserId());
            
            if (rs.next()) {
                int totalSets = rs.getInt("total_sets");
                int setsQuizzed = rs.getInt("sets_quizzed");
                
                // Calculate percentage
                int percentage = (totalSets > 0) ? (setsQuizzed * 100) / totalSets : 0;
                
                // Update UI components
                percentLabel.setText(percentage + "%");
                progressBar.setValue(percentage);
                
                // Update status message
                if (totalSets == 0) {
                    statusLabel.setText("No flashcard sets created");
                } else if (setsQuizzed == 0) {
                    statusLabel.setText("No quizzes taken yet");
                } else if (setsQuizzed == totalSets) {
                    statusLabel.setText("All flashcard sets quizzed!");
                } else {
                    int remaining = totalSets - setsQuizzed;
                    statusLabel.setText(remaining + " set" + (remaining > 1 ? "s" : "") + " remaining");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.error("Error updating quiz progress: " + e.getMessage());
        }
    }

    public static void updateWelcomeMessage() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("WELCOME, " + UserSession.getUsername());
        }
    }

    public static void refreshTaskProgress() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Get task counts
                Map<String, Integer> taskCounts = new TaskFetcher().getTaskCounts();
                
                if (taskCounts != null) {
                    int total = taskCounts.getOrDefault("total", 0);
                    int completed = taskCounts.getOrDefault("completed", 0);
                    int pending = taskCounts.getOrDefault("pending", 0);
                    
                    // Calculate percentage
                    int percentage = (total > 0) ? (completed * 100) / total : 0;
                    
                    System.out.println("Refreshing task progress: " + percentage + "% (" + pending + " tasks remaining)");
                    
                    // Find and update the overall progress panel
                    findAndUpdateTaskProgressPanel(percentage, total, completed, pending);
                    
                    // Also update all progress panels using the updateProgressPanel method
                    Container container = SwingUtilities.getAncestorOfClass(JPanel.class, taskListContainer);
                    if (container != null) {
                        updateAllProgressPanels(container);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.error("Error refreshing task progress: " + e.getMessage());
            }
        });
    }

    private static void findAndUpdateTaskProgressPanel(int percentage, int total, int completed, int pending) {
        // Start from the main panel and search for the overall progress panel
        Container container = SwingUtilities.getAncestorOfClass(JPanel.class, taskListContainer);
        if (container == null) return;
        
        // Search through all components recursively
        findAndUpdateTaskProgressPanelRecursive(container, percentage, total, completed, pending);
    }

    private static boolean findAndUpdateTaskProgressPanelRecursive(Container container, int percentage, int total, int completed, int pending) {
        Component[] components = container.getComponents();
        
        for (Component comp : components) {
            // Check if this is the overall progress panel by looking for the "Overall Progress" label
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                boolean foundOverallLabel = false;
                JLabel percentLabel = null;
                JProgressBar progressBar = null;
                JLabel statusLabel = null;
                
                // Check all components in this panel
                for (Component c : panel.getComponents()) {
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        if (label.getText().equals("Overall Progress")) {
                            foundOverallLabel = true;
                        } else if (label.getText().endsWith("%")) {
                            percentLabel = label;
                        } else if (!label.getText().equals("Overall Progress") && 
                                  !label.getText().endsWith("%")) {
                            statusLabel = label;
                        }
                    } else if (c instanceof JProgressBar) {
                        progressBar = (JProgressBar) c;
                    }
                }
                
                // If we found the overall progress panel, update it
                if (foundOverallLabel && percentLabel != null && progressBar != null && statusLabel != null) {
                    // Update UI components
                    percentLabel.setText(percentage + "%");
                    progressBar.setValue(percentage);
                    
                    // Update status message
                    if (total == 0) {
                        statusLabel.setText("No tasks to complete");
                    } else if (pending == 0) {
                        statusLabel.setText("All tasks completed!");
                    } else {
                        statusLabel.setText(pending + " task" + (pending > 1 ? "s" : "") + " remaining");
                    }
                    
                    // Revalidate and repaint the panel
                    panel.revalidate();
                    panel.repaint();
                    return true;
                }
                
                // If this panel has sub-containers, search them too
                if (findAndUpdateTaskProgressPanelRecursive(panel, percentage, total, completed, pending)) {
                    return true;
                }
            } else if (comp instanceof Container) {
                // Search other types of containers
                if (findAndUpdateTaskProgressPanelRecursive((Container) comp, percentage, total, completed, pending)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public static void refreshQuizProgress() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Direct approach to update quiz progress
                // Query to get total flashcard sets and total quizzes taken
                String query = """
                    SELECT 
                        (SELECT COUNT(*) FROM flashcard_sets WHERE user_id = ?) AS total_sets,
                        (SELECT COUNT(DISTINCT set_id) FROM quiz_attempts WHERE user_id = ?) AS sets_quizzed
                """;
                
                ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId(), UserSession.getUserId());
                
                if (rs.next()) {
                    int totalSets = rs.getInt("total_sets");
                    int setsQuizzed = rs.getInt("sets_quizzed");
                    
                    // Calculate percentage
                    int percentage = (totalSets > 0) ? (setsQuizzed * 100) / totalSets : 0;
                    
                    // Now find and update the quiz progress panel
                    findAndUpdateQuizProgressPanel(percentage, totalSets, setsQuizzed);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.error("Error refreshing quiz progress: " + e.getMessage());
            }
        });
    }

    private static void findAndUpdateQuizProgressPanel(int percentage, int totalSets, int setsQuizzed) {
        // Start from the main panel and search for the quiz progress panel
        Container container = SwingUtilities.getAncestorOfClass(JPanel.class, taskListContainer);
        if (container == null) return;
        
        // Search through all components recursively
        findAndUpdateQuizProgressPanelRecursive(container, percentage, totalSets, setsQuizzed);
    }

    private static boolean findAndUpdateQuizProgressPanelRecursive(Container container, int percentage, int totalSets, int setsQuizzed) {
        Component[] components = container.getComponents();
        
        for (Component comp : components) {
            // Check if this is the quiz progress panel by looking for the "Quiz Progress" label
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                boolean foundQuizLabel = false;
                JLabel percentLabel = null;
                JProgressBar progressBar = null;
                JLabel statusLabel = null;
                
                // Check all components in this panel
                for (Component c : panel.getComponents()) {
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        if (label.getText().equals("Quiz Progress")) {
                            foundQuizLabel = true;
                        } else if (label.getText().endsWith("%")) {
                            percentLabel = label;
                        } else if (!label.getText().equals("Quiz Progress") && 
                                  !label.getText().endsWith("%")) {
                            statusLabel = label;
                        }
                    } else if (c instanceof JProgressBar) {
                        progressBar = (JProgressBar) c;
                    }
                }
                
                // If we found the quiz progress panel, update it
                if (foundQuizLabel && percentLabel != null && progressBar != null && statusLabel != null) {
                    // Update UI components
                    percentLabel.setText(percentage + "%");
                    progressBar.setValue(percentage);
                    
                    // Update status message
                    if (totalSets == 0) {
                        statusLabel.setText("No flashcard sets created");
                    } else if (setsQuizzed == 0) {
                        statusLabel.setText("No quizzes taken yet");
                    } else if (setsQuizzed == totalSets) {
                        statusLabel.setText("All flashcard sets quizzed!");
                    } else {
                        int remaining = totalSets - setsQuizzed;
                        statusLabel.setText(remaining + " set" + (remaining > 1 ? "s" : "") + " not yet quizzed");
                    }
                    
                    // Revalidate and repaint the panel
                    panel.revalidate();
                    panel.repaint();
                    return true;
                }
                
                // If this panel has sub-containers, search them too
                if (findAndUpdateQuizProgressPanelRecursive(panel, percentage, totalSets, setsQuizzed)) {
                    return true;
                }
            } else if (comp instanceof Container) {
                // Search other types of containers
                if (findAndUpdateQuizProgressPanelRecursive((Container) comp, percentage, totalSets, setsQuizzed)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private static void updateProgressPanel(JPanel panel) {
        JLabel titleLabel = null;
        JLabel percentLabel = null;
        JProgressBar progressBar = null;
        JLabel statusLabel = null;
        
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel)c;
                String text = label.getText();
                
                if (text.equals("Overall Progress") || text.equals("Daily Progress") || text.equals("Quiz Progress")) {
                    titleLabel = label;
                } else if (text.endsWith("%")) {
                    percentLabel = label;
                } else if (!text.equals("Overall Progress") && !text.equals("Daily Progress") && !text.equals("Quiz Progress")) {
                    statusLabel = label;
                }
            } else if (c instanceof JProgressBar) {
                progressBar = (JProgressBar)c;
            }
        }
        
        if (titleLabel != null && percentLabel != null && progressBar != null && statusLabel != null) {
            if (titleLabel.getText().equals("Overall Progress")) {
                try {
                    Map<String, Integer> taskCounts = new TaskFetcher().getTaskCounts();
                    
                    if (taskCounts != null) {
                        int total = taskCounts.getOrDefault("total", 0);
                        int completed = taskCounts.getOrDefault("completed", 0);
                        int pending = taskCounts.getOrDefault("pending", 0);
                        
                        // Calculate percentage
                        int percentage = (total > 0) ? (completed * 100) / total : 0;
                        
                        // Update UI components
                        percentLabel.setText(percentage + "%");
                        progressBar.setValue(percentage);
                        
                        // Update status message
                        if (total == 0) {
                            statusLabel.setText("No tasks to complete");
                        } else if (pending == 0) {
                            statusLabel.setText("All tasks completed!");
                        } else {
                            statusLabel.setText(pending + " task" + (pending > 1 ? "s" : "") + " remaining");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (titleLabel.getText().equals("Daily Progress")) {
                // For now, we'll leave this at 0% since daily tasks aren't implemented
                percentLabel.setText("0%");
                progressBar.setValue(0);
                statusLabel.setText("Daily tasks coming soon");
            } else if (titleLabel.getText().equals("Quiz Progress")) {
                try {
                    // Query to get total flashcard sets and total quizzes taken
                    String query = """
                        SELECT 
                            (SELECT COUNT(*) FROM flashcard_sets WHERE user_id = ?) AS total_sets,
                            (SELECT COUNT(DISTINCT set_id) FROM quiz_attempts WHERE user_id = ?) AS sets_quizzed
                    """;
                    
                    ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId(), UserSession.getUserId());
                    
                    if (rs.next()) {
                        int totalSets = rs.getInt("total_sets");
                        int setsQuizzed = rs.getInt("sets_quizzed");
                        
                        // Calculate percentage
                        int percentage = (totalSets > 0) ? (setsQuizzed * 100) / totalSets : 0;
                        
                        // Update UI components
                        percentLabel.setText(percentage + "%");
                        progressBar.setValue(percentage);
                        
                        // Update status message
                        if (totalSets == 0) {
                            statusLabel.setText("No flashcard sets created");
                        } else if (setsQuizzed == 0) {
                            statusLabel.setText("No quizzes taken yet");
                        } else if (setsQuizzed == totalSets) {
                            statusLabel.setText("All flashcard sets quizzed!");
                        } else {
                            int remaining = totalSets - setsQuizzed;
                            statusLabel.setText(remaining + " set" + (remaining > 1 ? "s" : "") + " not yet quizzed");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            // Revalidate and repaint the panel
            panel.revalidate();
            panel.repaint();
        }
    }

    private static void updateAllProgressPanels(Container container) {
        Component[] components = container.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                
                // Check if this panel has a BoxLayout and white background (likely a progress panel)
                if (panel.getLayout() instanceof BoxLayout && 
                    panel.getBackground().equals(Color.WHITE)) {
                    
                    updateProgressPanel(panel);
                }
                
                // Recursively check child containers
                if (comp instanceof Container) {
                    updateAllProgressPanels((Container) comp);
                }
            } else if (comp instanceof Container) {
                updateAllProgressPanels((Container) comp);
            }
        }
    }

    public static void forceUpdateAllProgressPanels(int taskPercentage, int total, int completed, int pending) {
        // Get the root container
        Container root = SwingUtilities.getWindowAncestor(taskListContainer);
        if (root == null) return;
        
        // Update all panels recursively
        updateAllPanelsRecursive(root, taskPercentage, total, completed, pending);
    }

    private static void updateAllPanelsRecursive(Container container, int taskPercentage, int total, int completed, int pending) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                
                // Check for progress panels
                boolean isProgressPanel = false;
                String panelType = "";
                
                for (Component c : panel.getComponents()) {
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        String text = label.getText();
                        if (text != null && (text.equals("Overall Progress") || text.equals("Quiz Progress") || text.equals("Daily Progress"))) {
                            isProgressPanel = true;
                            panelType = text;
                            break;
                        }
                    }
                }
                
                if (isProgressPanel) {
                    // Found a progress panel, update it
                    JLabel percentLabel = null;
                    JProgressBar progressBar = null;
                    JLabel statusLabel = null;
                    
                    for (Component c : panel.getComponents()) {
                        if (c instanceof JLabel) {
                            JLabel label = (JLabel) c;
                            String text = label.getText();
                            if (text != null && text.endsWith("%")) {
                                percentLabel = label;
                            } else if (text != null && !text.equals("Overall Progress") && 
                                      !text.equals("Quiz Progress") && !text.equals("Daily Progress")) {
                                statusLabel = label;
                            }
                        } else if (c instanceof JProgressBar) {
                            progressBar = (JProgressBar) c;
                        }
                    }
                    
                    if (percentLabel != null && progressBar != null && statusLabel != null) {
                        if (panelType.equals("Overall Progress")) {
                            // Update task progress
                            percentLabel.setText(taskPercentage + "%");
                            progressBar.setValue(taskPercentage);
                            
                            if (total == 0) {
                                statusLabel.setText("No tasks to complete");
                            } else if (pending == 0) {
                                statusLabel.setText("All tasks completed!");
                            } else {
                                statusLabel.setText(pending + " task" + (pending > 1 ? "s" : "") + " remaining");
                            }
                            
                            System.out.println("Updated Overall Progress panel: " + taskPercentage + "%");
                        }
                    }
                    
                    // Force immediate repaint
                    panel.revalidate();
                    panel.repaint();
                }
            }
            
            // Recursively check child containers
            if (comp instanceof Container) {
                updateAllPanelsRecursive((Container) comp, taskPercentage, total, completed, pending);
            }
        }
    }

    public static void forceUpdateQuizProgressPanel(int quizPercentage, int totalSets, int setsQuizzed) {
        // Get the root container
        Container root = SwingUtilities.getWindowAncestor(taskListContainer);
        if (root == null) return;
        
        // Update all panels recursively
        updateQuizPanelRecursive(root, quizPercentage, totalSets, setsQuizzed);
    }

    private static void updateQuizPanelRecursive(Container container, int quizPercentage, int totalSets, int setsQuizzed) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                
                // Check for quiz progress panel
                boolean isQuizPanel = false;
                
                for (Component c : panel.getComponents()) {
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        String text = label.getText();
                        if (text != null && text.equals("Quiz Progress")) {
                            isQuizPanel = true;
                            break;
                        }
                    }
                }
                
                if (isQuizPanel) {
                    // Found a quiz progress panel, update it
                    JLabel percentLabel = null;
                    JProgressBar progressBar = null;
                    JLabel statusLabel = null;
                    
                    for (Component c : panel.getComponents()) {
                        if (c instanceof JLabel) {
                            JLabel label = (JLabel) c;
                            String text = label.getText();
                            if (text != null && text.endsWith("%")) {
                                percentLabel = label;
                            } else if (text != null && !text.equals("Quiz Progress")) {
                                statusLabel = label;
                            }
                        } else if (c instanceof JProgressBar) {
                            progressBar = (JProgressBar) c;
                        }
                    }
                    
                    if (percentLabel != null && progressBar != null && statusLabel != null) {
                        // Update quiz progress
                        percentLabel.setText(quizPercentage + "%");
                        progressBar.setValue(quizPercentage);
                        
                        if (totalSets == 0) {
                            statusLabel.setText("No flashcard sets created");
                        } else if (setsQuizzed == 0) {
                            statusLabel.setText("No quizzes taken yet");
                        } else if (setsQuizzed == totalSets) {
                            statusLabel.setText("All flashcard sets quizzed!");
                        } else {
                            int remaining = totalSets - setsQuizzed;
                            statusLabel.setText(remaining + " set" + (remaining > 1 ? "s" : "") + " not yet quizzed");
                        }
                        
                        System.out.println("Updated Quiz Progress panel: " + quizPercentage + "%");
                    }
                    
                    // Force immediate repaint
                    panel.revalidate();
                    panel.repaint();
                }
            }
            
            // Recursively check child containers
            if (comp instanceof Container) {
                updateQuizPanelRecursive((Container) comp, quizPercentage, totalSets, setsQuizzed);
            }
        }
    }

    private static void updateDailyProgress() {
        if (dailyProgressPercentLabel == null || dailyProgressBar == null || dailyProgressStatusLabel == null) {
            return;
        }
        
        // Calculate percentage
        int percentage = (dailyGoalTarget > 0) ? (dailyGoalCompleted * 100) / dailyGoalTarget : 0;
        
        // Update UI components
        dailyProgressPercentLabel.setText(percentage + "%");
        dailyProgressBar.setValue(percentage);
        
        // Update status message
        if (dailyGoalCompleted >= dailyGoalTarget) {
            dailyProgressStatusLabel.setText("Daily goal completed!");
        } else {
            int remaining = dailyGoalTarget - dailyGoalCompleted;
            dailyProgressStatusLabel.setText("Take " + remaining + " more quiz" + (remaining > 1 ? "es" : ""));
        }
    }

    // Method to be called when a quiz is completed
    public static void incrementDailyProgress() {
        dailyGoalCompleted++;
        updateDailyProgress();
        
        // Check if goal is completed
        if (dailyGoalCompleted >= dailyGoalTarget) {
            showDailyGoalCompletedDialog();
        }
    }

    private static void showDailyGoalCompletedDialog() {
        SwingUtilities.invokeLater(() -> {
            JPanel panel = new JPanel(new BorderLayout(0, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel congratsLabel = new JLabel("Congratulations!");
            congratsLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 20));
            congratsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel messageLabel = new JLabel("You've completed your daily goal of " + dailyGoalTarget + " quizzes!");
            messageLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JButton okButton = new JButton("OK");
            okButton.setBackground(COMPLETED_COLOR);
            okButton.setForeground(Color.WHITE);
            okButton.setFocusPainted(false);
            
            panel.add(congratsLabel, BorderLayout.NORTH);
            panel.add(messageLabel, BorderLayout.CENTER);
            panel.add(okButton, BorderLayout.SOUTH);
            
            JDialog dialog = new JDialog();
            dialog.setTitle("Daily Goal Completed");
            dialog.setModal(true);
            dialog.setContentPane(panel);
            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(null);
            
            okButton.addActionListener(e -> {
                dialog.dispose();
                
                // Increment the goal for next time (up to the total number of flashcard sets)
                try {
                    String countQuery = "SELECT COUNT(*) as total FROM flashcard_sets WHERE user_id = ?";
                    ResultSet rs = DatabaseManager.executeQuery(countQuery, UserSession.getUserId());
                    if (rs.next()) {
                        int totalSets = rs.getInt("total");
                        if (dailyGoalTarget < totalSets) {
                            dailyGoalTarget++;
                            dailyGoalCompleted = 0;
                            updateDailyProgress();
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Toast.error("Error counting flashcard sets: " + ex.getMessage());
                }
            });
            
            dialog.setVisible(true);
        });
    }

    // Reset daily progress (call this when application starts)
    public static void resetDailyProgress() {
        dailyGoalCompleted = 0;
        updateDailyProgress();
    }
}
