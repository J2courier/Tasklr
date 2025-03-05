package tasklr.main.TaskPanel;


import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.Border;

import tasklr.authentication.UserSession;
import tasklr.utilities.ComponentUtil;
import tasklr.utilities.HoverPanelEffect;
import tasklr.utilities.createButton;
import tasklr.utilities.createPanel;

import java.awt.*;
import java.util.List;
import java.sql.*;

public class task {
    private static DefaultListModel<String> taskListModel;
    private static JList<String> taskList;

    public static JPanel createTaskPanel(String username) {
        JPanel panel = createPanel.panel(new Color(0xf1f3f6), new BorderLayout(), new Dimension(100, 100));
        Border panelBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        panel.setBorder(panelBorder);
    
        JPanel inputPanel = createInputPanel(username);
        JPanel listContainer = createListContainer();
        panel.add(inputPanel, BorderLayout.CENTER); 
        panel.add(listContainer, BorderLayout.WEST); 

        return panel;
    }
    

    private static JPanel createInputPanel(String username) {
        JPanel inputPanel = createPanel.panel(null, new GridBagLayout(), new Dimension(0, 0));
        
        // Username label showing logged-in user
        JLabel usernameLabel = new JLabel("Hello, " + username + "!");
        usernameLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 80));
        
        // Paragraph area for task description
        JLabel paragraph = new JLabel("Every task you add is a step closer to achieving your goals.");
        paragraph.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        paragraph.setForeground(new Color(0x707070));
        
        JLabel AddTaskLbl = new JLabel("Add Task");
        AddTaskLbl.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));

        JTextField titleField = new JTextField(20);
        titleField.setPreferredSize(new Dimension(700, 40));

        JButton addTaskBtn = createButton.button("Add Task", null, Color.WHITE, null, false);
        addTaskBtn.setBackground(new Color(0x0065D9));
        addTaskBtn.setPreferredSize(new Dimension(70, 40));

        JPanel AddTaskComponent = createPanel.panel(new Color(0xD9D9D9), new BorderLayout(), new Dimension(700, 40));
        AddTaskComponent.add(titleField, BorderLayout.CENTER);
        

        JLabel setDueLbl = new JLabel("Set Due Date");
        setDueLbl.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        // Date components
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(640, 40));
        dateChooser.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

 
        JPanel AddDueComponent = createPanel.panel(new Color(0xD9D9D9), new BorderLayout(), new Dimension(700, 40));
        AddDueComponent.add(dateChooser, BorderLayout.CENTER);
        AddDueComponent.add(addTaskBtn, BorderLayout.EAST);

        // Add action listeners for the buttons
        addTaskBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            java.util.Date dueDate = dateChooser.getDate();
            
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a task title!");
                return;
            }
            
            if (insertTask(title, dueDate)) {
                titleField.setText("");
                dateChooser.setDate(null);
                refreshTaskList();
            }
        });

        JPanel spacer = createPanel.panel(null, null, new Dimension(0, 300));
        
        // Add components using ComponentUtil
        ComponentUtil.addComponent(inputPanel, usernameLabel, 0, 0, 2, 1, new Insets(10, 20, 10, 5), 0);
        ComponentUtil.addComponent(inputPanel, paragraph, 0, 1, 2, 1, new Insets(10, 40, 20, 10), 0);
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

    private static void refreshTaskList() {
        // Get the parent container (mainPanel)
        Container parent = taskList.getParent();
        while (!(parent instanceof JScrollPane)) {
            parent = parent.getParent();
            if (parent == null) return;
        }
        JScrollPane scrollPane = (JScrollPane) parent;
        
        // Create new task container
        JPanel taskContainer = createPanel.panel(null, null, null);
        taskContainer.setLayout(new BoxLayout(taskContainer, BoxLayout.Y_AXIS));
        taskContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add tasks
        List<String[]> tasks = TaskFetcher.getUserTasks();
        for (String[] task : tasks) {
            JPanel taskPanel = createTaskItemPanel(task[0]);
            taskContainer.add(taskPanel);
            taskContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Update the scroll pane with new content
        scrollPane.setViewportView(taskContainer);
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    private static JPanel createListContainer() {
        JPanel mainPanel = createPanel.panel(null, new BorderLayout(), new Dimension(400, 0));
        Border border = BorderFactory.createMatteBorder(1, 0, 0, 1, new Color(0x749AAD));
        mainPanel.setBorder(border);

        // Create a panel that will contain all task panels
        JPanel taskContainer = createPanel.panel(null, null, null);
        taskContainer.setLayout(new BoxLayout(taskContainer, BoxLayout.Y_AXIS));
        
        // Add some padding to the container
        taskContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Fetch and add tasks
        List<String[]> tasks = TaskFetcher.getUserTasks();
        for (String[] task : tasks) {
            JPanel taskPanel = createTaskItemPanel(task[0]);
            taskContainer.add(taskPanel);
            // Add some vertical spacing between panels
            taskContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Wrap the container in a scroll pane
        JScrollPane scrollPane = new JScrollPane(taskContainer);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private static JPanel createTaskItemPanel(String title) {
        // Create main panel without border initially
        JPanel panel = createPanel.panel(new Color(0xE0E3E2), new BorderLayout(), new Dimension(0, 50));
        
        // Create an inner panel to hold the content with consistent padding
        JPanel contentPanel = createPanel.panel(null, new BorderLayout(), null);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Variable", Font.PLAIN, 14));
        contentPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Add content panel to main panel
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Set the line border separately
        panel.setBorder(BorderFactory.createLineBorder(new Color(0x749AAD), 1));
        
        // Make the panel maintain its height but stretch horizontally
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Add hover effect
        Color defaultColor = new Color(0xE0E3E2);
        Color hoverColor = new Color(0xCACFCE); 
        new HoverPanelEffect(panel, defaultColor, hoverColor);

        return panel;
    }
}
