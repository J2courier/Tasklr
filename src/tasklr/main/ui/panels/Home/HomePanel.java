package tasklr.main.ui.panels.Home;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import tasklr.utilities.createPanel;
import tasklr.main.ui.panels.TaskPanel.TaskFetcher;
import tasklr.main.ui.panels.overveiw.overview;
import tasklr.utilities.DatabaseManager;
import tasklr.authentication.UserSession;
import tasklr.utilities.createButton;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.Timer;

public class HomePanel {
    // Color constants matching project theme
    private static final Color PRIMARY_COLOR = new Color(0x275CE2);    // Primary blue
    private static final Color BACKGROUND_COLOR = Color.WHITE;         // White background
    private static final Color TASK_DONE_COLOR = new Color(0x34D399);  // Green for completed tasks
    private static final Color TASK_PENDING_COLOR = new Color(0xF87171); // Red for pending tasks
    private static JPanel tasksContainer;
    private static JPanel mainPanel;
    private static ScheduledExecutorService scheduler;
    private static JPanel tasksWrapper;
    private static JScrollPane taskListContainer;
    private static final int REFRESH_INTERVAL = 5000; // 5 seconds

    public static JPanel createHomePanel(String username) {
        // Create main panel with GridBagLayout
        JPanel HomePanel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), null);
        
        // Add 20px margin around the entire panel
        HomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create GridBagConstraints for layout control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Components take up entire row
        gbc.fill = GridBagConstraints.BOTH; // Components fill their display area
        gbc.insets = new Insets(0, 0, 0, 0); // Default spacing
        
        // Add header panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.0; // Don't expand vertically
        gbc.weightx = 1.0; // Expand horizontally
        JPanel headerPanel = HeaderPanel(username);
        HomePanel.add(headerPanel, gbc);
        
        // Add body panel
        gbc.gridy = 1;
        gbc.weighty = 1.0; // Expand to fill remaining vertical space
        gbc.insets = new Insets(20, 0, 0, 0); // Add top spacing between header and body
        JPanel bodyPanel = BodyPanel();
        HomePanel.add(bodyPanel, gbc);
        
        return HomePanel;
    }

    public static JPanel HeaderPanel(String username){
        // Use GridBagLayout for better control over component positioning
        JPanel HeaderPanel = createPanel.panel(PRIMARY_COLOR, new GridBagLayout(), new Dimension(100, 100));
        
        // Create a panel for the greeting label
        JPanel greetingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        greetingPanel.setOpaque(false);
        
        JLabel Greetings = new JLabel("WELCOME, " + username.toUpperCase());
        Greetings.setForeground(Color.WHITE);
        Greetings.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        // Add left margin to greeting
        Greetings.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        greetingPanel.add(Greetings);
        
        // Create a panel for the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton SwitchOverview = createButton.button("Overview", PRIMARY_COLOR, BACKGROUND_COLOR, null, false);
        SwitchOverview.setPreferredSize(new Dimension(100, 40));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); // Right margin
        
        SwitchOverview.addActionListener(e -> {
            Container topLevelContainer = SwingUtilities.getWindowAncestor(HeaderPanel);
            if (topLevelContainer instanceof JFrame) {
                JFrame frame = (JFrame) topLevelContainer;
                JPanel body = (JPanel) frame.getContentPane().getComponent(0);
                CardLayout cardLayout = (CardLayout) body.getLayout();
                
                try {
                    body.remove(body.getComponent(body.getComponentCount() - 1));
                } catch (ArrayIndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }
                
                JPanel overviewPanel = overview.createOverview(username);
                body.add(overviewPanel, "overview");
                cardLayout.show(body, "overview");
                overview.refreshTaskCounters();
                
                body.revalidate();
                body.repaint();
            }
        });
        
        buttonPanel.add(SwitchOverview);
        
        // Setup GridBagConstraints for positioning
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Position greeting panel to the left
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        HeaderPanel.add(greetingPanel, gbc);
        
        // Position button panel to the right
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        HeaderPanel.add(buttonPanel, gbc);
        
        // Add overall padding to the header panel
        HeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        return HeaderPanel;
    }

    public static JPanel BodyPanel() {
        JPanel BodyPanel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), null);
        BodyPanel.add(TaskContainer(), BorderLayout.CENTER);
        return BodyPanel;
    }

    public static JPanel TaskContainer() {
        JPanel TaskContainer = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(500, 0));

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
                JLabel noTasksLabel = new JLabel("No tasks available");
                noTasksLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
                noTasksLabel.setForeground(new Color(0x6D6D6D));
                noTasksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                tasksWrapper.add(noTasksLabel);
            }

            tasksWrapper.revalidate();
            tasksWrapper.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching tasks: " + ex.getMessage());
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
        JButton dropBtn = createButton.button("Drop Task", new Color(0xDC2626), Color.WHITE, null, false);
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
        JButton doneBtn = createButton.button("Mark as done", new Color(0x059669), Color.WHITE, null, false);
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

        buttonPanel.add(dropBtn);
        buttonPanel.add(doneBtn);
        
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
            
            JPanel homePanel = createHomePanel(UserSession.getUsername());
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
