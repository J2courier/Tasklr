
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
import tasklr.utilities.*;


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

        // Create and add header panel with FlowLayout
        JPanel headerPanel = createPanel.panel(PRIMARY_COLOR, new FlowLayout(FlowLayout.LEFT, 20, 15), new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Create a panel for the header label
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        labelPanel.setPreferredSize(new Dimension(200, 40));

        JLabel headerLabel = new JLabel("TASK LIST");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
        labelPanel.add(headerLabel);

        // Create a panel for the search components
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(350, 40));

        // Create search field
        JTextField searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
        searchField.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));

        // Create search button
        JButton searchButton = createButton.button("Search", null, PRIMARY_COLOR, null, false);
        searchButton.setBackground(Color.WHITE);
        searchButton.setForeground(PRIMARY_COLOR);
        searchButton.setPreferredSize(new Dimension(80, 30));
        searchButton.setFocusPainted(false);

        // Create close button (initially hidden)
        JButton closeButton = createButton.button("×", null, new Color(0xDC3545), null, false);
        closeButton.setPreferredSize(new Dimension(30, 30));
        closeButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        closeButton.setFocusPainted(false);
        closeButton.setVisible(false); // Initially hidden

        // Add search functionality
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim().toLowerCase();
            if (!searchTerm.isEmpty()) {
                closeButton.setVisible(true); // Show close button when search is active
            }
            searchTasks(searchTerm);
        });

        // Add close button functionality
        closeButton.addActionListener(e -> {
            searchField.setText(""); // Clear search field
            closeButton.setVisible(false); // Hide close button
            searchTasks(""); // Show all tasks
        });

        // Add enter key listener to search field
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String searchTerm = searchField.getText().trim().toLowerCase();
                    if (!searchTerm.isEmpty()) {
                        closeButton.setVisible(true); // Show close button when search is active
                    }
                    searchTasks(searchTerm);
                }
            }
        });

        // Add components to search panel
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(closeButton);

        // Add panels to header panel
        headerPanel.add(labelPanel);
        headerPanel.add(searchPanel);

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
            
            boolean hasItems = false;
            while (rs.next()) {
                hasItems = true;
                String title = rs.getString("title");
                java.sql.Date dueDate = rs.getDate("due_date");
                
                JPanel taskPanel = createTaskItemPanel(title, dueDate);
                taskContainer.add(taskPanel);
                taskContainer.add(Box.createRigidArea(new Dimension(0, 5)));
            }

            // Add "No tasks yet" message if there are no tasks
            if (!hasItems) {
                // Create a panel that takes up the full height of the container
                JPanel centeringPanel = new JPanel(new GridBagLayout());
                centeringPanel.setBackground(LIST_CONTAINER_COLOR);
                centeringPanel.setPreferredSize(new Dimension(taskContainer.getWidth(), 200));
                
                JLabel noTasksLabel = new JLabel("No tasks yet");
                noTasksLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                noTasksLabel.setForeground(TEXT_COLOR);
                noTasksLabel.setHorizontalAlignment(SwingConstants.CENTER);
                
                // Add the label to the centering panel which will center it
                centeringPanel.add(noTasksLabel);
                
                // Add the centering panel to fill the entire container
                taskContainer.add(centeringPanel);
                
                // Make the centering panel expand to fill available space
                taskContainer.add(Box.createVerticalGlue());
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
        taskContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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
    
    private static void searchTasks(String searchTerm) {
        if (taskContainer == null) {
            return;
        }
        taskContainer.removeAll();
        
        try {
            String query;
            ResultSet rs;
            
            if (searchTerm.isEmpty()) {
                // If search term is empty, show all tasks
                query = "SELECT id, title, due_date, status, description FROM tasks WHERE user_id = ? ORDER BY due_date ASC";
                rs = DatabaseManager.executeQuery(query, UserSession.getUserId());
            } else {
                // If search term is not empty, filter tasks
                query = "SELECT id, title, due_date, status, description FROM tasks WHERE user_id = ? AND title LIKE ? ORDER BY due_date ASC";
                rs = DatabaseManager.executeQuery(query, 
                    UserSession.getUserId(),
                    "%" + searchTerm + "%"
                );
            }
            
            boolean hasItems = false;
            int resultCount = 0;
            while (rs.next()) {
                hasItems = true;
                resultCount++;
                String title = rs.getString("title");
                java.sql.Date dueDate = rs.getDate("due_date");
                
                JPanel taskPanel = createTaskItemPanel(title, dueDate);
                taskContainer.add(taskPanel);
                taskContainer.add(Box.createRigidArea(new Dimension(0, 5)));
            }

            // Add "No tasks found" message if there are no matching tasks
            if (!hasItems) {
                JPanel centeringPanel = new JPanel(new GridBagLayout());
                centeringPanel.setBackground(LIST_CONTAINER_COLOR);
                centeringPanel.setPreferredSize(new Dimension(taskContainer.getWidth(), 200));
                
                JLabel noTasksLabel = new JLabel(searchTerm.isEmpty() ? 
                                               "No tasks yet" : 
                                               "No tasks matching '" + searchTerm + "'");
                noTasksLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                noTasksLabel.setForeground(TEXT_COLOR);
                noTasksLabel.setHorizontalAlignment(SwingConstants.CENTER);
                
                centeringPanel.add(noTasksLabel);
                taskContainer.add(centeringPanel);
                taskContainer.add(Box.createVerticalGlue());
            }

            taskContainer.revalidate();
            taskContainer.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            Toast.error("Error searching tasks: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.error("Error during search: " + ex.getMessage());
        }
    }
    
    private static JPanel createTaskItemPanel(String title, java.sql.Date dueDate) {
        // Main panel with fixed height and flexible width
        JPanel panel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), new Dimension(0, 100)); // Changed height to 100
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Changed height to match preferredSize
        
        // Inner panel for consistent padding and content positioning
        JPanel contentPanel = createPanel.panel(LIST_ITEM_COLOR, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Adjusted padding
        
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
            @Override
            public void mouseClicked(MouseEvent e) {
                // Create main description panel with BorderLayout
                JPanel descriptionPanel = new JPanel(new BorderLayout());
                descriptionPanel.setBackground(Color.WHITE);
                descriptionPanel.setPreferredSize(new Dimension(600, 500));

                // Header Panel (NORTH)
                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setBackground(Color.WHITE);
                headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
                
                // Store the existing description in a final reference
                final String[] existingDescriptionRef = {""};
                
                try {
                    String query = "SELECT description FROM tasks WHERE title = ? AND user_id = ?";
                    ResultSet rs = DatabaseManager.executeQuery(query, title, UserSession.getUserId());
                    if (rs.next()) {
                        existingDescriptionRef[0] = rs.getString("description");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Toast.error("Error fetching description: " + ex.getMessage());
                }

                JLabel titleLabel = new JLabel(existingDescriptionRef[0] == null || existingDescriptionRef[0].isEmpty() ? 
                                             "Add Description" : "Edit Description");
                titleLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 24));
                headerPanel.add(titleLabel, BorderLayout.WEST);
                
                descriptionPanel.add(headerPanel, BorderLayout.NORTH);

                // Description Area (CENTER)
                JPanel centerPanel = new JPanel(new BorderLayout());
                centerPanel.setBackground(Color.WHITE);
                centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                
                JTextArea descriptionArea = new JTextArea();
                descriptionArea.setLineWrap(true);
                descriptionArea.setWrapStyleWord(true);
                descriptionArea.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
                
                // Set existing description if any
                if (existingDescriptionRef[0] != null && !existingDescriptionRef[0].isEmpty()) {
                    descriptionArea.setText(existingDescriptionRef[0]);
                }
                
                JScrollPane scrollPane = new JScrollPane(descriptionArea);
                scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
                
                centerPanel.add(scrollPane, BorderLayout.CENTER);
                descriptionPanel.add(centerPanel, BorderLayout.CENTER);

                // Button Panel (SOUTH)
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                buttonPanel.setBackground(Color.WHITE);
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

                // Close button
                JButton closeButton = new JButton("Close");
                closeButton.setPreferredSize(new Dimension(150, 40));
                closeButton.setBackground(new Color(0xFF3B30));
                closeButton.setForeground(Color.WHITE);
                closeButton.setFocusPainted(false);
                closeButton.setBorderPainted(false);
                closeButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));

                // Save button (renamed from Add Description)
                JButton saveButton = new JButton(existingDescriptionRef[0] == null || existingDescriptionRef[0].isEmpty() ? 
                                               "Add Description" : "Save Changes");
                saveButton.setPreferredSize(new Dimension(150, 40));
                saveButton.setBackground(new Color(0x34C759));
                saveButton.setForeground(Color.WHITE);
                saveButton.setFocusPainted(false);
                saveButton.setBorderPainted(false);
                saveButton.setFont(new Font("Segoe UI Variable", Font.BOLD, 14));

                JDialog dialog = new JDialog();
                // Button actions
                closeButton.addActionListener(event -> {
                    String currentText = descriptionArea.getText().trim();
                    if (!currentText.equals(existingDescriptionRef[0] != null ? existingDescriptionRef[0].trim() : "")) {
                        int result = JOptionPane.showConfirmDialog(
                            dialog,
                            "Are you sure you want to close? Any unsaved changes will be lost.",
                            "Confirm Close",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                        );
                        
                        if (result != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    dialog.dispose();
                });

                saveButton.addActionListener(evt -> {
                    String description = descriptionArea.getText().trim();
                    
                    if (description.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            dialog,
                            "Please enter a description.",
                            "Empty Description",
                            JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    try {
                        String query = "UPDATE tasks SET description = ? WHERE title = ? AND user_id = ?";
                        DatabaseManager.executeUpdate(query, 
                            description,
                            title,
                            UserSession.getUserId()
                        );
                        
                        Toast.success(existingDescriptionRef[0] == null || existingDescriptionRef[0].isEmpty() ? 
                                    "Description added successfully!" : "Description updated successfully!");
                        dialog.dispose();
                        
                        // Refresh the task container to show updated data
                        refreshTaskContainer();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        Toast.error("Error saving description: " + ex.getMessage());
                    }
                });

                buttonPanel.add(closeButton);
                buttonPanel.add(saveButton);
                descriptionPanel.add(buttonPanel, BorderLayout.SOUTH);

                // Create and setup dialog
      
                dialog.setUndecorated(true);
                dialog.setContentPane(descriptionPanel);

                // Add a border to the main panel
                descriptionPanel.setBorder(BorderFactory.createLineBorder(new Color(0x000000), 1));

                // Final dialog setup
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });

        return panel;
    }
}
