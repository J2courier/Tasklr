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
import tasklr.main.ui.panels.quizPanel.QuizzerPanel;

public class HomePanel {
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
    private static JPanel tasksContainer;
    private static JPanel tasksWrapper;
    private static JScrollPane taskListContainer;
    private static final int REFRESH_INTERVAL = 5000;
    private static UIRefreshManager refreshManager;
    private static JPanel recentTasksWrapper;
    private static JScrollPane recentTaskListContainer;
    private static final int CONTAINER_WIDTH = 400;

    private static TaskCounterPanel pendingTasksPanel;
    private static TaskCounterPanel completedTasksPanel;
    private static TaskCounterPanel totalTasksPanel;
    
    private static TaskCounterPanel totalFlashcardSetsPanel;
    private static TaskCounterPanel totalQuizTakenPanel;
    private static TaskCounterPanel totalQuizRetakedPanel;

    private static JLabel welcomeLabel;

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
        taskContainersPanel.add(createRecentTaskContainer());

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        mainPanel.add(taskContainersPanel, gbc);

        refreshTasksList();
        refreshRecentTasksList();

        return mainPanel;
    }

    private static void initializeRefresh() {
        refreshManager.startRefresh(UIRefreshManager.TASK_LIST, () -> {
            refreshTasksList();
        }, REFRESH_INTERVAL);

        refreshManager.startRefresh(UIRefreshManager.TASK_COUNTER, () -> {
            refreshTaskCounters();
        }, REFRESH_INTERVAL);
    }

    public static void cleanup() {
        if (refreshManager != null) {
            refreshManager.stopRefresh(UIRefreshManager.TASK_LIST);
            refreshManager.stopRefresh(UIRefreshManager.TASK_COUNTER);
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

        try {
            ImageIcon notifIcon = new ImageIcon("C://Users//ADMIN//Desktop//Tasklr//resource//icons//NotificationIcon.png");
            Image scaledImage = notifIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            JLabel notificationLabel = new JLabel(new ImageIcon(scaledImage));
            notificationLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        remainingTaskLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 20));
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
            refreshTasksList();
            refreshTaskCounters();
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
                tasksWrapper.add(Box.createRigidArea(new Dimension(0, 10)));

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
            
            refreshRecentTasksList();
            
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
        titleLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 24));
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
            dialog.setLocationRelativeTo(null); // Center the dialog
            dialog.setVisible(true);
            
            Object selectedValue = optionPane.getValue();
            if (selectedValue != null && selectedValue.equals(JOptionPane.YES_OPTION)) {
                try {
                    String query = "DELETE FROM tasks WHERE title = ? AND user_id = ?";
                    DatabaseManager.executeUpdate(query, title, UserSession.getUserId());
                    Window popup = SwingUtilities.getWindowAncestor(popupPanel);
                    popup.dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
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

        JButton doneBtn = createButton.button("Mark as done", COMPLETED_COLOR, Color.WHITE, null, false);
        doneBtn.setPreferredSize(new Dimension(100, 35));
        doneBtn.setEnabled(!status.equals("COMPLETED"));
        doneBtn.addActionListener(e -> {
            try {
                String query = "UPDATE tasks SET status = 'COMPLETED' WHERE title = ? AND user_id = ?";
                DatabaseManager.executeUpdate(query, title, UserSession.getUserId());
                Window popup = SwingUtilities.getWindowAncestor(popupPanel);
                popup.dispose();
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

    private static JPanel createRecentTaskContainer() {
        JPanel recentTaskContainer = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(CONTAINER_WIDTH, 0));
        recentTaskContainer.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        JPanel headerPanel = createPanel.panel(CARD_COLOR, new BorderLayout(), new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JLabel completedTaskLabel = new JLabel("Completed Tasks");
        completedTaskLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 20)); // Modified font size
        completedTaskLabel.setForeground(TEXT_DARK);
        headerPanel.add(completedTaskLabel, BorderLayout.WEST);

        recentTasksWrapper = new JPanel();
        recentTasksWrapper.setLayout(new BoxLayout(recentTasksWrapper, BoxLayout.Y_AXIS));
        recentTasksWrapper.setBackground(BACKGROUND_COLOR);
        recentTasksWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        recentTaskListContainer = new JScrollPane(recentTasksWrapper);
        recentTaskListContainer.setBorder(null);
        recentTaskListContainer.setBackground(BACKGROUND_COLOR);
        recentTaskListContainer.getVerticalScrollBar().setUnitIncrement(16);
        
        recentTaskListContainer.setPreferredSize(new Dimension(CONTAINER_WIDTH, 300)); // Fixed height of 300px
        
        recentTasksWrapper.setMinimumSize(new Dimension(CONTAINER_WIDTH - 20, 300));
        
        recentTaskListContainer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        recentTaskListContainer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        refreshRecentTasksList();

        recentTaskContainer.add(headerPanel, BorderLayout.NORTH);
        recentTaskContainer.add(recentTaskListContainer, BorderLayout.CENTER);
        
        return recentTaskContainer;
    }

    public static void refreshRecentTasksList() {
        if (recentTasksWrapper == null) return;
        
        recentTasksWrapper.removeAll();
        
        try {
            String query = "SELECT title FROM tasks " + 
                          "WHERE user_id = ? AND status = 'completed' ";
                          
            ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId());

            boolean hasItems = false;
            while (rs.next()) {
                hasItems = true;
                String title = rs.getString("title");

                JPanel taskItemPanel = new JPanel(new BorderLayout(2, 0));
                taskItemPanel.setBackground(CARD_COLOR);
                taskItemPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));  // Only padding, no line border

                taskItemPanel.setPreferredSize(new Dimension(CONTAINER_WIDTH - 40, 80));
                taskItemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                taskItemPanel.setMinimumSize(new Dimension(CONTAINER_WIDTH - 40, 80));

                JLabel titleLabel = new JLabel(title);
                titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                titleLabel.setForeground(TEXT_DARK);
                taskItemPanel.add(titleLabel, BorderLayout.CENTER);

                new HoverPanelEffect(taskItemPanel, 
                    null,                    // default background
                    new Color(0xF5F5F5)           // hover background
                );

                JPanel wrapperPanel = new JPanel();
                wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
                wrapperPanel.setBackground(BACKGROUND_COLOR);
                wrapperPanel.add(taskItemPanel);
                
                recentTasksWrapper.add(wrapperPanel);
                recentTasksWrapper.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing between items
            }

            if (!hasItems) {
                JLabel noTasksLabel = new JLabel("No completed tasks");
                noTasksLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                noTasksLabel.setForeground(TEXT_DARK);
                noTasksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                recentTasksWrapper.add(noTasksLabel);
            }

            recentTasksWrapper.revalidate();
            recentTasksWrapper.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error fetching completed tasks: " + ex.getMessage());
        }
    }

    public static void updateWelcomeMessage() {
        if (welcomeLabel != null) {
            welcomeLabel.setText("WELCOME, " + UserSession.getUsername());
        }
    }

}
