
package tasklr.main.ui.panels.TaskPanel;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.Border;

import org.w3c.dom.events.MouseEvent;

import tasklr.utilities.Toast;
import tasklr.authentication.UserSession;
import tasklr.utilities.ComponentUtil;
import tasklr.utilities.HoverPanelEffect;
import tasklr.utilities.createButton;
import tasklr.utilities.createPanel;
import tasklr.utilities.HoverButtonEffect;
import tasklr.utilities.DatabaseManager;


import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.sql.*;

public class task {
    // Color constants
    private static final Color TEXT_COLOR = new Color(0x242424);
    private static final Color BACKGROUND_COLOR = new Color(0xFFFFFF);
    private static final Color TEXTFIELD_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_CONTAINER_COLOR = new Color(0xFFFFFF);
    private static final Color LIST_ITEM_COLOR = new Color(0xFFFFFF);
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
        // Create main panel with GridBagLayout
        JPanel panel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), new Dimension(100, 100));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D)),
            BorderFactory.createEmptyBorder(20, 20, 0, 20)  // Add 20px margin
        ));

        // Create GridBagConstraints for layout control
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Components take up entire row
        gbc.fill = GridBagConstraints.BOTH; // Components fill their display area
        gbc.weightx = 1.0; // Expand horizontally
        gbc.insets = new Insets(0, 0, 0, 0); // Default spacing

        // Create and add header panel
        JPanel headerPanel = createPanel.panel(PRIMARY_COLOR, new BorderLayout(), new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        JLabel headerLabel = new JLabel("TASK LIST");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Add header panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.0; // Don't expand vertically
        panel.add(headerPanel, gbc);

        // Create content panel to hold input and list
        JPanel contentPanel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), null);
        GridBagConstraints contentGbc = new GridBagConstraints();
        contentGbc.fill = GridBagConstraints.BOTH;
        contentGbc.insets = new Insets(0, 0, 0, 0); // Add top spacing

        // Add list container (left side)
        contentGbc.gridx = 0;
        contentGbc.gridy = 0;
        contentGbc.weightx = 0.6; // 60% of horizontal space
        contentGbc.weighty = 1.0;
        contentGbc.insets = new Insets(0, 0, 0, 0); // Add right margin
        JPanel listContainer = createListContainer();
        // Set preferred size for list container
        listContainer.setPreferredSize(new Dimension(300, 0));
        contentPanel.add(listContainer, contentGbc);

        // Add input panel (right side)
        contentGbc.gridx = 1;
        contentGbc.weightx = 0.4; // 40% of horizontal space
        contentGbc.insets = new Insets(0, 0, 0, 0); // Reset insets
        JPanel inputPanel = createInputPanel(username);
        // Set minimum size for input panel to prevent collapse
        inputPanel.setMinimumSize(new Dimension(400, 0));
        contentPanel.add(inputPanel, contentGbc);

        // Add content panel to main panel
        gbc.gridy = 1;
        gbc.weighty = 1.0; 
        gbc.insets = new Insets(1, 0, 0, 0); 
        panel.add(contentPanel, gbc);

        return panel;
    }
    

    private static JPanel createInputPanel(String username) {
        JPanel inputPanel = createPanel.panel(BACKGROUND_COLOR, new GridBagLayout(), new Dimension(700, 0));
        
        JLabel AddTaskLbl = new JLabel("Add Task");
        AddTaskLbl.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));  // Changed from 16
        AddTaskLbl.setForeground(TEXT_COLOR);

        // Title Label
        JLabel titleLabel = new JLabel("TITLE");
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));  // Changed from 14
        titleLabel.setForeground(TEXT_COLOR);

        JTextField titleField = new JTextField(20);
        titleField.setPreferredSize(new Dimension(700, 40));
        titleField.setBackground(TEXTFIELD_COLOR);
        titleField.setForeground(TEXT_COLOR);
        titleField.setFont(new Font("Segoe UI Variable", Font.PLAIN, 20));  // Added font size

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
        // dateChooser.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
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
                
                // Refresh all necessary panels
                refreshTaskContainer(); // Original refresh
                tasklr.main.ui.panels.Home.HomePanel.refreshTasksList(); // Home panel refresh
                
                // Show success message
                Toast.success("Task added successfully!");
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
                return result > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error adding task: " + ex.getMessage());
            return false;
        }
    }

    public static void refreshTaskContainer() {
        if (taskContainer == null) return;
        
        // Clear existing tasks
        taskContainer.removeAll();
        
        try {
            String query = "SELECT title, due_date FROM tasks WHERE user_id = ? ORDER BY due_date ASC";
            ResultSet rs = DatabaseManager.executeQuery(query, UserSession.getUserId());
            
            while (rs.next()) {
                String title = rs.getString("title");
                java.sql.Date dueDate = rs.getDate("due_date");
                
                JPanel taskPanel = createTaskItemPanel(title, dueDate);
                taskContainer.add(taskPanel);
                taskContainer.add(Box.createRigidArea(new Dimension(0, 5)));
            }

            taskContainer.revalidate();
            taskContainer.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error fetching tasks: " + ex.getMessage());
        }
    }

    private static JPanel createListContainer() {
        // Main panel with fixed width
        JPanel mainPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), new Dimension(300, 0));
    
        
        // Configure task container with BoxLayout (Y_AXIS)
        taskContainer = createPanel.panel(LIST_CONTAINER_COLOR, null, null);
        taskContainer.setLayout(new BoxLayout(taskContainer, BoxLayout.Y_AXIS));
        taskContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 20, 5));

        // Add initial tasks
        refreshTaskContainer();

        // Create a wrapper panel with proper background
        JPanel wrapperPanel = createPanel.panel(LIST_CONTAINER_COLOR, new BorderLayout(), null);
        wrapperPanel.add(taskContainer, BorderLayout.NORTH);

        // Create scroll pane with consistent styling
        scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(LIST_CONTAINER_COLOR);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }
    
    

    private static JPanel createTaskItemPanel(String title, java.sql.Date dueDate) {
        // Main panel with fixed height and flexible width
        JPanel panel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), new Dimension(0, 100)); // Changed height to 100
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Changed height to match preferredSize
        
        // Inner panel for consistent padding and content positioning
        JPanel contentPanel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 00, 10)); // Adjusted padding
        
        // Text panel using BorderLayout for north-south positioning
        JPanel textPanel = createPanel.panel(null, new BorderLayout(), null);
       
        
        // Task title label at the top (NORTH)
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Due date label at the bottom (SOUTH)
        String dueDateText = dueDate != null ? String.format("Due: %tF", dueDate) : "No due date";
        JLabel dueDateLabel = new JLabel(dueDateText);
        dueDateLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 12));
        dueDateLabel.setForeground(new Color(0x666666));
        
        // Add labels to text panel
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(dueDateLabel, BorderLayout.SOUTH);
        
        // Button panel for more button
        JPanel buttonPanel = createPanel.panel(null, new FlowLayout(FlowLayout.RIGHT, 5, 0), null);
        
        // More button with popup menu
        JButton moreBtn = new JButton();
        try {
            ImageIcon moreIcon = new ImageIcon("C:\\Users\\ADMIN\\Desktop\\Tasklr\\resource\\icons\\moreIconBlack.png");
            Image scaledImage = moreIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            moreBtn.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            moreBtn.setText("•••");
        }
        moreBtn.setBorderPainted(false);
        moreBtn.setContentAreaFilled(false);
        moreBtn.setFocusPainted(false);
        moreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        moreBtn.setPreferredSize(new Dimension(30, 30));

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
            // Create edit panel with title field and date chooser
            JPanel editPanel = new JPanel(new GridLayout(4, 1, 5, 5));
            
            JTextField titleField = new JTextField(title);
            JDateChooser dateChooser = new JDateChooser();
            if (dueDate != null) {
                dateChooser.setDate(dueDate);
            }
            
            editPanel.add(new JLabel("Title:"));
            editPanel.add(titleField);
            editPanel.add(new JLabel("Due Date:"));
            editPanel.add(dateChooser);

            int result = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(moreBtn),
                editPanel,
                "Edit Task",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String newTitle = titleField.getText().trim();
                java.util.Date newDueDate = dateChooser.getDate();
                
                if (newTitle.isEmpty()) {
                    Toast.error("Title cannot be empty!");
                    return;
                }

                try {
                    String query = "UPDATE tasks SET title = ?, due_date = ? WHERE title = ? AND user_id = ?";
                    DatabaseManager.executeUpdate(query, newTitle, newDueDate != null ? new java.sql.Timestamp(newDueDate.getTime()) : null,
                        title,
                        UserSession.getUserId()
                    );
                    Toast.success("Task updated successfully!");
                    refreshTaskContainer();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Toast.error("Error updating task: " + ex.getMessage());
                }
            }
        });

        deleteItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(moreBtn),
                "Are you sure you want to delete this task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM tasks WHERE title = ? AND user_id = ?";
                    DatabaseManager.executeUpdate(query, 
                        title,
                        UserSession.getUserId()
                    );
                    Toast.success("Task deleted successfully!");
                    refreshTaskContainer();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Toast.error("Error deleting task: " + ex.getMessage());
                }
            }
        });

        buttonPanel.add(moreBtn);
        
        contentPanel.add(textPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Add hover effect
        new HoverPanelEffect(panel, LIST_ITEM_COLOR, LIST_ITEM_HOVER_BG);
        
        panel.addMouseListener(new MouseAdapter() {
           //add another view panel to add additional information in the task 
           //so that when it clicked it will give the user another option what to do in the task
           //give a add button to add a descrpition on task that they want to do 
           //the description that will be written in task will be in JCheckBox
           //first resize list container, set the value of list visible to true
           //then add a condition if list visible true set list container preferred size into 0 else list container preferred size 600
           //then create a panel that will contain the JTextField the input will to added into checkbox
           //add a task_desc table in the database to be the container of the data being input in the JTextField
        });

        return panel;
    }
}
