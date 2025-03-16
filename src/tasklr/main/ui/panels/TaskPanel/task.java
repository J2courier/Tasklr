package tasklr.main.ui.panels.TaskPanel;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.Border;

import tasklr.authentication.UserSession;
import tasklr.utilities.ComponentUtil;
import tasklr.utilities.HoverPanelEffect;
import tasklr.utilities.createButton;
import tasklr.utilities.createPanel;
import tasklr.utilities.HoverButtonEffect;

import java.awt.*;
import java.util.List;
import java.sql.*;

public class task {
    // Color constants
    private static final Color TEXT_COLOR = new Color(0x242424);
    private static final Color BACKGROUND_COLOR = new Color(0xF1F3F6);
    private static final Color TEXTFIELD_COLOR = new Color(0xFBFBFC);
    private static final Color LIST_CONTAINER_COLOR = new Color(0xF1F3F6);
    private static final Color LIST_ITEM_COLOR = new Color(0xFBFBFC);
    private static final Color LIST_ITEM_HOVER_BG = new Color(0xE8EAED);
    private static final Color LIST_ITEM_HOVER_BORDER = new Color(0x0082FC);
    private static final Color PRIMARY_BUTTON_COLOR = new Color(0x275CE2);
    private static final Color PRIMARY_BUTTON_HOVER = new Color(0x3B6FF0);
    private static final Color PRIMARY_BUTTON_TEXT = Color.WHITE;

    private static final String url = "jdbc:mysql://localhost:3306/tasklrdb";
    private static final String dbUser = "JFCompany";
    private static final String dbPass = "";
    private static DefaultListModel<String> taskListModel;
    private static JList<String> taskList;
    private static JPanel taskContainer;
    private static JScrollPane scrollPane;

    public static JPanel createTaskPanel(String username) {
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), new Dimension(100, 100));
        Border panelBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        panel.setBorder(panelBorder);
    
        JPanel inputPanel = createInputPanel(username);
        JPanel listContainer = createListContainer();
        panel.add(inputPanel, BorderLayout.CENTER); 
        panel.add(listContainer, BorderLayout.WEST); 

        return panel;
    }
    

    private static JPanel createInputPanel(String username) {
        JPanel inputPanel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), new Dimension(0, 0));
        
        // Username label showing logged-in user
        JLabel usernameLabel = new JLabel("Hello " + username + "!");
        usernameLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 80));
        usernameLabel.setForeground(TEXT_COLOR);
        
        // Paragraph area for task description
        JLabel paragraph = new JLabel("Every task you add is a step closer to achieving your goals.");
        paragraph.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        paragraph.setForeground(TEXT_COLOR);
        
        JLabel AddTaskLbl = new JLabel("Add Task");
        AddTaskLbl.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        AddTaskLbl.setForeground(TEXT_COLOR);

        JTextField titleField = new JTextField(20);
        titleField.setPreferredSize(new Dimension(700, 40));
        titleField.setBackground(TEXTFIELD_COLOR);
        titleField.setForeground(TEXT_COLOR);

        JButton addTaskBtn = createButton.button("Add Task", PRIMARY_BUTTON_COLOR, PRIMARY_BUTTON_TEXT, null, false);
        addTaskBtn.setPreferredSize(new Dimension(70, 40));
        addTaskBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addTaskBtn.setBackground(PRIMARY_BUTTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addTaskBtn.setBackground(PRIMARY_BUTTON_COLOR);
            }
        });

        JPanel AddTaskComponent = createPanel.panel(TEXTFIELD_COLOR, new BorderLayout(), new Dimension(700, 40));
        AddTaskComponent.add(titleField, BorderLayout.CENTER);
        

        JLabel setDueLbl = new JLabel("Set Due Date");
        setDueLbl.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        setDueLbl.setForeground(TEXT_COLOR);
        
        // Date components
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(640, 40));
        dateChooser.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        dateChooser.setBackground(TEXTFIELD_COLOR);

        JPanel AddDueComponent = createPanel.panel(TEXTFIELD_COLOR, new BorderLayout(), new Dimension(700, 40));
        AddDueComponent.add(dateChooser, BorderLayout.CENTER);
        AddDueComponent.add(addTaskBtn, BorderLayout.EAST);

        // Add action listeners for the buttons
        addTaskBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            java.util.Date dueDate = dateChooser.getDate();
            
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(addTaskBtn), // parent component
                    "Please enter a task title!",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            if (insertTask(title, dueDate)) {
                titleField.setText("");
                dateChooser.setDate(null);
                refreshTaskContainer();
            }
        });

        JPanel spacer = createPanel.panel(BACKGROUND_COLOR, null, new Dimension(0, 300));
        
        // Add components using ComponentUtil
        ComponentUtil.addComponent(inputPanel, usernameLabel, 0, 0, 2, 1, new Insets(10, 10, 10, 5), 0);
        ComponentUtil.addComponent(inputPanel, paragraph, 0, 1, 2, 1, new Insets(10, 10, 20, 10), 0);
        ComponentUtil.addComponent(inputPanel, AddTaskComponent, 0, 2, 2, 1, new Insets(20, 10, 10, 5), 0);
        ComponentUtil.addComponent(inputPanel, AddDueComponent, 0, 3, 1, 1, new Insets(5, 10, 10, 5), 0);
        ComponentUtil.addComponent(inputPanel, spacer, 0, 4, 2, 1, new Insets(10, 10, 10, 10), 0);
        
        return inputPanel;
    }

    private static boolean insertTask(String title, java.util.Date dueDate) {
        String url = "jdbc:mysql://localhost:3306/tasklrdb";
        String dbUser = "JFCompany";
        String dbPass = "";
        
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String query = "INSERT INTO tasks (user_id, title, due_date, status) VALUES (?, ?, ?, 'pending')";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, UserSession.getUserId());
                stmt.setString(2, title);
                stmt.setTimestamp(3, dueDate != null ? new Timestamp(dueDate.getTime()) : null);
                
                int result = stmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Task added successfully!");
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding task: " + ex.getMessage());
        }
        return false;
    }

    private static void refreshTaskContainer() {
        if (taskContainer == null) return;
        
        // Clear existing tasks
        taskContainer.removeAll();
        
        // Fetch and add tasks
        List<String[]> tasks = TaskFetcher.getUserTasks();
        for (String[] task : tasks) {
            JPanel taskPanel = createTaskItemPanel(task[0]);
            taskContainer.add(taskPanel);
            taskContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Ensure the UI updates
        taskContainer.revalidate();
        taskContainer.repaint();
        
        if (scrollPane != null) {
            scrollPane.revalidate();
            scrollPane.repaint();
        }
    }

    private static JPanel createListContainer() {
        JPanel mainPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), new Dimension(400, 0));
        Border border = BorderFactory.createMatteBorder(1, 0, 0, 1, LIST_ITEM_HOVER_BORDER);
        mainPanel.setBorder(border);

        taskContainer = createPanel.panel(LIST_CONTAINER_COLOR, null, null);
        taskContainer.setLayout(new BoxLayout(taskContainer, BoxLayout.Y_AXIS));
        taskContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        refreshTaskContainer();

        scrollPane = new JScrollPane(taskContainer);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Improve scroll speed - multiply by panel height (50) plus spacing (5)
        scrollPane.getVerticalScrollBar().setUnitIncrement((50 + 5) * 3);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private static JPanel createTaskItemPanel(String title) {
        // Main panel with fixed height and flexible width
        JPanel panel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), new Dimension(0, 50));
        
        // Inner panel for consistent padding and content positioning
        JPanel contentPanel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Task title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Button panel for edit and delete
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.RIGHT, 5, 0), null);
        
        // Edit button
        JButton editBtn = createButton.button("Edit", new Color(0xE9E9E9), new Color(0x242424), null, false);
        editBtn.setPreferredSize(new Dimension(70, 40));
        new HoverButtonEffect(editBtn, 
            new Color(0xE9E9E9), // default background
            new Color(0xBFBFBF), // hover background
            new Color(0x242424), // default text
            Color.WHITE         // hover text
        );

        // Delete button
        JButton deleteBtn = createButton.button("Delete", new Color(0xFB2C36), Color.WHITE, null, false);
        deleteBtn.setPreferredSize(new Dimension(70, 40));
        new HoverButtonEffect(deleteBtn, 
            new Color(0xFB2C36),  // default background
            new Color(0xFF6467),  // hover background
            Color.WHITE,          // default text
            Color.WHITE          // hover text
        );

        // Keep existing action listeners
        editBtn.addActionListener(e -> {
            String newTitle = JOptionPane.showInputDialog(
                SwingUtilities.getWindowAncestor(editBtn), // parent component
                "Edit task:",
                title
            );
            if (newTitle != null && !newTitle.trim().isEmpty()) {
                try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
                    String query = "UPDATE tasks SET title = ? WHERE title = ? AND user_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, newTitle.trim());
                        stmt.setString(2, title);
                        stmt.setInt(3, UserSession.getUserId());
                        
                        int result = stmt.executeUpdate();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(
                                SwingUtilities.getWindowAncestor(editBtn), // parent component
                                "Task updated successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            refreshTaskContainer();
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(editBtn), // parent component
                        "Error updating task: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(deleteBtn), // parent component
                "Are you sure you want to delete this task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
                    String query = "DELETE FROM tasks WHERE title = ? AND user_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setString(1, title);
                        stmt.setInt(2, UserSession.getUserId());
                        
                        int result = stmt.executeUpdate();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(
                                SwingUtilities.getWindowAncestor(deleteBtn), // parent component
                                "Task deleted successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            refreshTaskContainer();
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(deleteBtn), // parent component
                        "Error deleting task: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        // Add buttons to button panel
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        // Add components to content panel
        contentPanel.add(titleLabel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createLineBorder(LIST_ITEM_HOVER_BORDER, 1));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Hover effect configuration
        new HoverPanelEffect(panel, LIST_ITEM_COLOR, LIST_ITEM_HOVER_BG);

        return panel;
    }
}
