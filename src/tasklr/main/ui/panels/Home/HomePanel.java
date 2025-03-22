package tasklr.main.ui.panels.Home;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import tasklr.utilities.createPanel;
import tasklr.main.ui.components.TaskCounterPanel;
import tasklr.main.ui.panels.TaskPanel.TaskFetcher;
import tasklr.main.ui.panels.overveiw.overview;
import tasklr.utilities.DatabaseManager;
import tasklr.utilities.RefreshUI;
import tasklr.authentication.UserSession;
import tasklr.utilities.createButton;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import tasklr.utilities.UIRefreshManager;

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
    private static final Color COMPLETED_COLOR = new Color(0x7CCE00);
    private static JPanel tasksContainer;
    private static JPanel mainPanel;
    private static ScheduledExecutorService scheduler;
    private static JPanel tasksWrapper;
    private static JScrollPane taskListContainer;
    private static final int REFRESH_INTERVAL = 2000; // Changed from 5000 to 2000 milliseconds (2 seconds)
    private static UIRefreshManager refreshManager;

    private static TaskCounterPanel pendingTasksPanel;
    private static TaskCounterPanel completedTasksPanel;
    private static TaskCounterPanel totalTasksPanel;
    
    // Add new panel variables for flashcard statistics
    private static TaskCounterPanel totalFlashcardSetsPanel;
    private static TaskCounterPanel pendingQuizProgressPanel;
    private static TaskCounterPanel completedQuizProgressPanel;

    public static JPanel createOverview(String username) {            
        // Initialize the refresh manager
        refreshManager = UIRefreshManager.getInstance();
        
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
        mainPanel.add(TaskContainer(), gbc);

        // Start the refresh mechanisms
        initializeRefresh();

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
        

    public static JPanel TaskContainer() {
        JPanel TaskContainer = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(500, 0));

        // Create header panel
        JPanel headerPanel = createPanel.panel(CARD_COLOR, new BorderLayout(), new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel headerLabel = new JLabel("Your Tasks");
        headerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 18));
        headerLabel.setForeground(TEXT_DARK);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Create wrapper panel for tasks with BoxLayout
        tasksWrapper = createPanel.panel(BACKGROUND_COLOR, null, null);
        tasksWrapper.setLayout(new BoxLayout(tasksWrapper, BoxLayout.Y_AXIS));
        tasksWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Initial load of tasks
        refreshTasksList();

        // Create scroll pane for tasks
        taskListContainer = new JScrollPane(tasksWrapper);
        taskListContainer.setBorder(null);
        taskListContainer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        taskListContainer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        taskListContainer.getVerticalScrollBar().setUnitIncrement(16);
        taskListContainer.getViewport().setBackground(BACKGROUND_COLOR);
        taskListContainer.setPreferredSize(new Dimension(0, 400));

        // Start the auto-refresh scheduler
        startAutoRefresh();

        JPanel scrollWrapper = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), null);
        scrollWrapper.add(taskListContainer, BorderLayout.CENTER);

        // Add header and scroll wrapper to main container
        TaskContainer.add(headerPanel, BorderLayout.NORTH);
        TaskContainer.add(scrollWrapper, BorderLayout.CENTER);
        
        return TaskContainer;
    }

    private static void startAutoRefresh() {
        // Stop existing scheduler if running
        stopAutoRefresh();
        
        // Create new scheduler
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        // Schedule periodic refresh
        scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                refreshTasksList();
                // showToaster("Tasks Refreshed Successfully");
            });
        }, REFRESH_INTERVAL, REFRESH_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private static void stopAutoRefresh() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                scheduler.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void refreshTasksList() {
        System.out.println("[Home Panel] Refreshing tasks list at: ");
        if (tasksWrapper == null) return;
        
        tasksWrapper.removeAll();
        
        try {
            String query = "SELECT title, status, due_date FROM tasks WHERE user_id = ? ORDER BY due_date ASC";
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
                // Add "No tasks" message
                JLabel noTasksLabel = new JLabel("No tasks found");
                noTasksLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noTasksLabel.setForeground(TEXT_DARK);
                tasksWrapper.add(noTasksLabel);
            }

            tasksWrapper.revalidate();
            tasksWrapper.repaint();
            
            if (taskListContainer != null) {
                taskListContainer.revalidate();
                taskListContainer.repaint();
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
            int confirm = JOptionPane.showConfirmDialog(
                parent,
                "Are you sure you want to delete this task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM tasks WHERE title = ? AND user_id = ?";
                    DatabaseManager.executeUpdate(query, title, UserSession.getUserId());
                    Window popup = SwingUtilities.getWindowAncestor(popupPanel);
                    popup.dispose();
                    refreshTaskContainer();
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
        
        // Center the popup relative to the parent component
        Point location = parent.getLocationOnScreen();
        dialog.setLocation(
            location.x + (parent.getWidth() - dialog.getWidth()) / 2,
            location.y + (parent.getHeight() - dialog.getHeight()) / 2
        );
        
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

    // private static void showToaster(String message) {
    //     JPanel toaster = new JPanel();
    //     toaster.setBackground(new Color(0x059669));
    //     toaster.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
    //     JLabel label = new JLabel(message);
    //     label.setForeground(Color.WHITE);
    //     label.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
    //     toaster.add(label);
        
    //     JDialog dialog = new JDialog();
    //     dialog.setUndecorated(true);
    //     dialog.setContentPane(toaster);
    //     dialog.pack();
        
    //     // Position the toaster at the bottom right
    //     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //     dialog.setLocation(
    //         screenSize.width - dialog.getWidth() - 20,
    //         screenSize.height - dialog.getHeight() - 40
    //     );
        
    //     dialog.setVisible(true);
        
    //     // Hide the toaster after 2 seconds
    //     Timer timer = new Timer(2000, e -> {
    //         dialog.dispose();
    //     });
    //     timer.setRepeats(false);
    //     timer.start();
    // }
}
