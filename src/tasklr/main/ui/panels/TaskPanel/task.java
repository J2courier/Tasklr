
package tasklr.main.ui.panels.TaskPanel;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.Border;
import tasklr.utilities.Toast;
import tasklr.authentication.UserSession;
import tasklr.utilities.ComponentUtil;
import tasklr.utilities.HoverPanelEffect;
import tasklr.utilities.createButton;
import tasklr.utilities.createPanel;
import tasklr.utilities.HoverButtonEffect;
import tasklr.utilities.DatabaseManager;

import java.awt.*;
import java.util.List;
import java.sql.*;

public class task {
    // Color constants
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
    private static final Color PRIMARY_COLOR = new Color(0x275CE2);    // Primary blue
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

        // Create header panel
        JPanel headerPanel = createPanel.panel(PRIMARY_COLOR, new BorderLayout(), new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        JLabel headerLabel = new JLabel("TASK LIST");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Create content panel to hold input and list
        JPanel contentPanel = createPanel.panel(BACKGROUND_COLOR, new BorderLayout(), null);
        JPanel inputPanel = createInputPanel(username);
        JPanel listContainer = createListContainer();
        contentPanel.add(inputPanel, BorderLayout.CENTER); 
        contentPanel.add(listContainer, BorderLayout.WEST); 

        // Add both panels to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }
    

    private static JPanel createInputPanel(String username) {
        JPanel inputPanel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), new Dimension(0, 0));
        
        JLabel AddTaskLbl = new JLabel("Add Task");
        AddTaskLbl.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        AddTaskLbl.setForeground(TEXT_COLOR);

        // Title Label
        JLabel titleLabel = new JLabel("TITLE");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);

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

        // Date Label
        JLabel dateLabel = new JLabel("MM/DD/YY");
        dateLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));
        dateLabel.setForeground(TEXT_COLOR);
        
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
        ComponentUtil.addComponent(inputPanel, titleLabel, 0, 1, 2, 1, new Insets(20, 10, 5, 5), 0);
        ComponentUtil.addComponent(inputPanel, AddTaskComponent, 0, 2, 2, 1, new Insets(0, 10, 10, 5), 0);
        ComponentUtil.addComponent(inputPanel, dateLabel, 0, 3, 2, 1, new Insets(10, 10, 5, 5), 0);
        ComponentUtil.addComponent(inputPanel, AddDueComponent, 0, 4, 1, 1, new Insets(0, 10, 10, 5), 0);
        ComponentUtil.addComponent(inputPanel, spacer, 0, 5, 2, 1, new Insets(10, 10, 10, 10), 0);
        
        return inputPanel;
    }

    private static boolean insertTask(String title, java.util.Date dueDate) {
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String query = "INSERT INTO tasks (user_id, title, due_date, status) VALUES (?, ?, ?, 'pending')";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, UserSession.getUserId());
                stmt.setString(2, title);
                stmt.setTimestamp(3, dueDate != null ? new Timestamp(dueDate.getTime()) : null);
                
                int result = stmt.executeUpdate();
                if (result > 0) {
                    Toast.success("Task added successfully!");
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error adding task: " + ex.getMessage());
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
        // Main panel with fixed width - increased width
        JPanel mainPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), new Dimension(600, 0));
        // Border border = BorderFactory.createMatteBorder(1, 0, 0, 1, LIST_ITEM_HOVER_BORDER);
        // mainPanel.setBorder(border);

        // Configure task container with BoxLayout (Y_AXIS)
        taskContainer = createPanel.panel(LIST_CONTAINER_COLOR, null, null);
        taskContainer.setLayout(new BoxLayout(taskContainer, BoxLayout.Y_AXIS));
        taskContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add initial tasks
        refreshTaskContainer();

        // Create a wrapper panel to properly contain the task container
        JPanel wrapperPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), null);
        wrapperPanel.add(taskContainer, BorderLayout.NORTH);
        
        // Add filler panel to push content to top and allow proper scrolling
        JPanel fillerPanel = createPanel.panel(LIST_CONTAINER_COLOR, null, null);
        wrapperPanel.add(fillerPanel, BorderLayout.CENTER);

        // Configure ScrollPane with the wrapper panel
        scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // Changed to ALWAYS
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        
        // Remove border from scroll pane
        
        // Set viewport background to match container
        scrollPane.getViewport().setBackground(LIST_CONTAINER_COLOR);

        // Add ScrollPane to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }
    
    

    private static JPanel createTaskItemPanel(String title) {
        // Main panel with fixed height and flexible width
        JPanel panel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), new Dimension(0, 60));
        
        // Inner panel for consistent padding and content positioning
        JPanel contentPanel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Task title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Button panel for more button
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.RIGHT, 5, 0), null);
        
        // More button with icon
        JButton moreBtn = new JButton();
        try {
            ImageIcon moreIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\moreIconBlack.png");
            Image scaledImage = moreIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            moreBtn.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Failed to load more icon: " + e.getMessage());
        }
        moreBtn.setBorderPainted(false);
        moreBtn.setContentAreaFilled(false);
        moreBtn.setFocusPainted(false);
        moreBtn.setPreferredSize(new Dimension(40, 40));

        // Create popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        
        // Edit menu item with icon
        JMenuItem editItem = new JMenuItem();
        try {
            ImageIcon editIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\editIcon.png");
            Image scaledEditImage = editIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            editItem.setIcon(new ImageIcon(scaledEditImage));
        } catch (Exception e) {
            System.err.println("Failed to load edit icon: " + e.getMessage());
        }
        editItem.setText("Edit");
        
        // Delete menu item with icon
        JMenuItem deleteItem = new JMenuItem();
        try {
            ImageIcon deleteIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\deleteIcon.png");
            Image scaledDeleteImage = deleteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            deleteItem.setIcon(new ImageIcon(scaledDeleteImage));
        } catch (Exception e) {
            System.err.println("Failed to load delete icon: " + e.getMessage());
        }
        deleteItem.setText("Delete");

        // Add items to popup menu
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        // Add action listeners
        moreBtn.addActionListener(e -> {
            popupMenu.show(moreBtn, 0, moreBtn.getHeight());
        });

        editItem.addActionListener(e -> {
            String newTitle = JOptionPane.showInputDialog(
                SwingUtilities.getWindowAncestor(moreBtn),
                "Edit task:",
                title
            );
            if (newTitle != null && !newTitle.trim().isEmpty()) {
                try {
                    String query = "UPDATE tasks SET title = ? WHERE title = ? AND user_id = ?";
                    DatabaseManager.executeUpdate(query, 
                        newTitle.trim(), 
                        title, 
                        UserSession.getUserId()
                    );
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(moreBtn),
                        "Task updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    refreshTaskContainer();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(moreBtn),
                        "Error updating task: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        deleteItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(moreBtn),
                "Are you sure you want to delete this task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM tasks WHERE title = ? AND user_id = ?";
                    DatabaseManager.executeUpdate(query, 
                        title, 
                        UserSession.getUserId()
                    );
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(moreBtn),
                        "Task deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    refreshTaskContainer();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(moreBtn),
                        "Error deleting task: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        buttonPanel.add(moreBtn);
        
        contentPanel.add(titleLabel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        // panel.setBorder(BorderFactory.createLineBorder(LIST_ITEM_HOVER_BORDER, 1));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Hover effect configuration
        new HoverPanelEffect(panel, LIST_ITEM_COLOR, LIST_ITEM_HOVER_BG);

        return panel;
    }
}
