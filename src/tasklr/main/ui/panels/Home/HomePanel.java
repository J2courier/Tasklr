package tasklr.main.ui.panels.Home;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import tasklr.main.ui.components.TaskCounterPanel;
import tasklr.main.ui.panels.TaskPanel.TaskFetcher;
import tasklr.utilities.*;
import tasklr.authentication.UserSession;
import java.sql.*;
import java.util.concurrent.ScheduledExecutorService;

public class HomePanel {
    private static final Color PRIMARY_COLOR = new Color(0x275CE2);    // Primary blue
    private static final Color SECONDARY_COLOR = new Color(0xE0E3E2);  // Light gray
    private static final Color BACKGROUND_COLOR = Color.WHITE; // Light background
    private static final Color CARD_COLOR = new Color(0xFFFFFF);       // White
    private static final Color TEXT_DARK = new Color(0x1D1D1D);        // Dark text
    private static final Color BORDER_COLOR = new Color(0xE0E0E0);     // Border gray
    private static final Color CLOSE_COLOR = new Color(0x404040);      // White background
    private static final Color TASK_DONE_COLOR = new Color(0x34D399);  // Green for completed tasks
    private static final Color TASK_PENDING_COLOR = new Color(0xF87171); // Red for pending tasks
    private static final Color DROP_COLOR = new Color(0xFB2C36);
    private static final Color COMPLETED_COLOR = new Color(0x59F070);
    private static JPanel tasksContainer;
    private static JPanel tasksWrapper;
    private static JScrollPane taskListContainer;
    private static final int REFRESH_INTERVAL = 5000; // Changed from 5000 to 2000 milliseconds (2 seconds)
    private static UIRefreshManager refreshManager;

    private static TaskCounterPanel pendingTasksPanel;
    private static TaskCounterPanel completedTasksPanel;
    private static TaskCounterPanel totalTasksPanel;
    
    // Add new panel variables for flashcard statistics
    private static TaskCounterPanel totalFlashcardSetsPanel;
    private static TaskCounterPanel totalQuizTakenPanel;
    private static TaskCounterPanel totalQuizRetakedPanel;

    public static JPanel createOverview(String username) {            
        JPanel mainPanel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), new Dimension(400, 0));

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

        // Task Container Section
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        mainPanel.add(createTaskContainer(), gbc);

        // Initial fetch and display of tasks
        refreshTasksList();

        return mainPanel;
    }

    private static void initializeRefresh() {
        // Start task list refresh
        refreshManager.startRefresh(UIRefreshManager.TASK_LIST, () -> {
            refreshTasksList();
        }, REFRESH_INTERVAL);

        // Start task counters refresh
        refreshManager.startRefresh(UIRefreshManager.TASK_COUNTER, () -> {
            refreshTaskCounters();
        }, REFRESH_INTERVAL);
    }

    // Add cleanup method
    public static void cleanup() {
        if (refreshManager != null) {
            refreshManager.stopRefresh(UIRefreshManager.TASK_LIST);
            refreshManager.stopRefresh(UIRefreshManager.TASK_COUNTER);
        }
    }

    // Add new method for flashcard statistics section
    private static JPanel createFlashcardStatisticsSection() {
        JPanel statsPanel = createPanel.panel(null, new GridLayout(1, 3, 15, 0), null);
        statsPanel.setOpaque(false);

        totalFlashcardSetsPanel = new TaskCounterPanel(0, "Total Flashcard Sets");
        totalQuizTakenPanel = new TaskCounterPanel(0, "Total Quiz Taken");
        totalQuizRetakedPanel = new TaskCounterPanel(0, "Total Quiz Retaked");

        // Style and add counter panels
        for (TaskCounterPanel panel : new TaskCounterPanel[]{
            totalFlashcardSetsPanel, 
            totalQuizTakenPanel, 
            totalQuizRetakedPanel
        }) {
            JPanel card = panel.createPanel();
            styleCard(card);
            statsPanel.add(card);
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
        

    private static JPanel createTaskContainer() {
        JPanel taskContainer = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(500, 0));
        
        // Create header panel with "Remaining Task" label and refresh button
        JPanel headerPanel = createPanel.panel(CARD_COLOR, new BorderLayout(), new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Left side - Title
        JLabel remainingTaskLabel = new JLabel("Remaining Tasks");
        remainingTaskLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        remainingTaskLabel.setForeground(TEXT_DARK);
        
        // Right side - Refresh button
        JButton refreshButton = new JButton();
        refreshButton.setPreferredSize(new Dimension(100, 40));
        refreshButton.setText("⟳"); 
        refreshButton.setBorderPainted(false);
        refreshButton.setContentAreaFilled(true); // Changed to true to show background color
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setToolTipText("Refresh tasks");
        refreshButton.setBackground(COMPLETED_COLOR);
        refreshButton.setForeground(Color.WHITE);
        
        refreshButton.addActionListener(e -> {
            refreshButton.setEnabled(false);
            refreshTasksList();
            refreshTaskCounters();
            // Re-enable the button after a short delay
            Timer timer = new Timer(1000, event -> refreshButton.setEnabled(true));
            timer.setRepeats(false);
            timer.start();
        });

        // Create a panel for the right side
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // rightPanel.setPreferredSize(new Dimension(100, 40));
        rightPanel.setOpaque(false);
        rightPanel.add(refreshButton);

        headerPanel.add(rightPanel, BorderLayout.WEST);
        // headerPanel.add(, BorderLayout.EAST);

        // Tasks wrapper panel
        tasksWrapper = new JPanel();
        tasksWrapper.setLayout(new BoxLayout(tasksWrapper, BoxLayout.Y_AXIS));
        tasksWrapper.setBackground(BACKGROUND_COLOR);
        tasksWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        taskListContainer = new JScrollPane(tasksWrapper);
        taskListContainer.setBorder(null);
        taskListContainer.setBackground(BACKGROUND_COLOR);
        
        // Enhance scrolling behavior
        taskListContainer.getVerticalScrollBar().setUnitIncrement(32);
        taskListContainer.getVerticalScrollBar().setBlockIncrement(128);
        taskListContainer.getVerticalScrollBar().putClientProperty("JScrollBar.smoothScrolling", true);
        
        // Optional: Add mouse wheel listener for even smoother scrolling
        tasksWrapper.addMouseWheelListener(e -> {
            JScrollBar scrollBar = taskListContainer.getVerticalScrollBar();
            int direction = e.getWheelRotation();
            int increment = scrollBar.getUnitIncrement() * 3; // Multiply by 3 for faster scrolling
            int newValue = scrollBar.getValue() + (direction * increment);
            scrollBar.setValue(newValue);
        });

        // Add components to main container
        taskContainer.add(headerPanel, BorderLayout.NORTH);
        taskContainer.add(taskListContainer, BorderLayout.CENTER);
        
        return taskContainer;
    }

    public static void refreshTasksList() {
        if (tasksWrapper == null) return;
        
        tasksWrapper.removeAll();
        
        try {
            // Modified query to order by status (pending first) and then due_date
            String query = "SELECT title, status, due_date FROM tasks " +
                          "WHERE user_id = ? " +
                          "ORDER BY CASE " +
                              "WHEN status = 'pending' THEN 0 " +
                              "WHEN status = 'completed' THEN 1 " +
                              "ELSE 2 END, " +
                          "due_date ASC";
                          
            ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId());

            boolean hasItems = false;
            while (rs.next()) {
                hasItems = true;
                String title = rs.getString("title");
                String status = rs.getString("status");
                java.sql.Date dueDate = rs.getDate("due_date");

                JPanel taskItemPanel = createTaskItemPanel(title, status, dueDate);
                tasksWrapper.add(taskItemPanel);
                tasksWrapper.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            if (!hasItems) {
                JLabel noTasksLabel = new JLabel("No tasks found");
                noTasksLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noTasksLabel.setForeground(TEXT_DARK);
                tasksWrapper.add(noTasksLabel);
            }

            tasksWrapper.revalidate();
            tasksWrapper.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error fetching tasks: " + ex.getMessage());
        }
    }

    private static JPanel createTaskItemPanel(String title, String status, java.sql.Date dueDate) {
        JPanel taskPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);
        taskPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        taskPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE5E7EB), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Content panel (left side)
        JPanel contentPanel = createPanel.panel(Color.WHITE, new BorderLayout(), null);

        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 24));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Add due date if exists
        JLabel dateLabel;
        if (dueDate != null) {
            dateLabel = new JLabel("Due: " + dueDate.toString());
        } else {
            dateLabel = new JLabel("Due: None");
        }
        dateLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(0x6D6D6D));
        contentPanel.add(dateLabel, BorderLayout.SOUTH);

        // Status label (right side)
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        statusLabel.setForeground(status.equals("completed") ? TASK_DONE_COLOR : TASK_PENDING_COLOR);
        
        JPanel rightPanel = createPanel.panel(Color.WHITE, new FlowLayout(FlowLayout.RIGHT), null);
        rightPanel.add(statusLabel);

        taskPanel.add(contentPanel, BorderLayout.CENTER);
        taskPanel.add(rightPanel, BorderLayout.EAST);

        // Add hover effect
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
        // Create custom popup panel
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

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        popupPanel.add(titleLabel, gbc);

        // Due Date
        JLabel dateLabel = new JLabel("Due: " + (dueDate != null ? dueDate.toString() : "None"));
        dateLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(0x6D6D6D));
        popupPanel.add(dateLabel, gbc);

        // Status
        JLabel statusLabel = new JLabel("Status: " + status);
        statusLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        statusLabel.setForeground(status.equals("completed") ? TASK_DONE_COLOR : TASK_PENDING_COLOR);
        popupPanel.add(statusLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        // Drop button
        JButton dropBtn = createButton.button("Drop Task", DROP_COLOR, Color.WHITE, null, false);
        dropBtn.setPreferredSize(new Dimension(100, 35));
        dropBtn.addActionListener(e -> {
            // Create a JOptionPane and center it
            JOptionPane optionPane = new JOptionPane(
                "Are you sure you want to delete this task?",
                JOptionPane.WARNING_MESSAGE,
                JOptionPane.YES_NO_OPTION
            );
            
            JDialog dialog = optionPane.createDialog(parent, "Confirm Delete");
            dialog.setLocationRelativeTo(null); // Center the dialog
            dialog.setVisible(true);
            
            // Get the user's selection
            Object selectedValue = optionPane.getValue();
            if (selectedValue != null && selectedValue.equals(JOptionPane.YES_OPTION)) {
                try {
                    String query = "DELETE FROM tasks WHERE title = ? AND user_id = ?";
                    DatabaseManager.executeUpdate(query, title, UserSession.getUserId());
                    Window popup = SwingUtilities.getWindowAncestor(popupPanel);
                    popup.dispose();
                    refreshTaskContainer();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    // Create centered error dialog
                    JOptionPane errorPane = new JOptionPane(
                        "Error deleting task: " + ex.getMessage(),
                        JOptionPane.ERROR_MESSAGE
                    );
                    JDialog errorDialog = errorPane.createDialog(parent, "Error");
                    errorDialog.setLocationRelativeTo(null);
                    errorDialog.setVisible(true);
                }
            }
        });

        // Mark as done button
        JButton doneBtn = createButton.button("Mark as done", COMPLETED_COLOR, Color.WHITE, null, false);
        doneBtn.setPreferredSize(new Dimension(100, 35));
        doneBtn.setEnabled(!status.equals("COMPLETED"));
        doneBtn.addActionListener(e -> {
            try {
                String query = "UPDATE tasks SET status = 'COMPLETED' WHERE title = ? AND user_id = ?";
                DatabaseManager.executeUpdate(query, title, UserSession.getUserId());
                Window popup = SwingUtilities.getWindowAncestor(popupPanel);
                popup.dispose();
                // No need to call refreshTaskContainer() as the auto-refresh will handle it
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

        // Add close button
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

        // Show popup
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setContentPane(popupPanel);
        dialog.pack();
        
        // Center the dialog on the screen
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


}
