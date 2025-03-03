package tasklr.TaskPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.util.List;  
import java.util.ArrayList; 
import tasklr.createButton;
import tasklr.createPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import tasklr.setProgress;
import tasklr.authentication.UserSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TaskListPanel {
    private JPanel taskContainerScrollPanel;
    private JPanel taskPanel;
    private JPanel inputPanel; 
    private SelectedPanel selectedPanel; 
    private JPanel centerContainer; 

    public TaskListPanel(JPanel centerContainer) {
        this.centerContainer = centerContainer; 
    
        // Task list container
        taskPanel = createPanel.panel(new Color(0x0A4A7E), new BorderLayout(), new Dimension(1000, 1125));
        Border taskContainerBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0x6D6D6D));
        taskPanel.setBorder(taskContainerBorder);
    
        // Header
        JPanel taskContainerHeader = createPanel.panel(new Color(0x191919), new BorderLayout(), new Dimension(0, 70));
    
        JLabel taskContainerLabel = new JLabel("ADDED TASKS");
        taskContainerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        taskContainerLabel.setForeground(new Color(0xe8eaed));
        taskContainerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        taskContainerHeader.add(taskContainerLabel, BorderLayout.CENTER);
    
        JButton addTaskBtn = createButton.button("ADD TASK", null, new Color(0xe8eaed), null, false);
        addTaskBtn.setPreferredSize(new Dimension(100, 0));
        addTaskBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inputPanel != null) {
                    inputPanel.setVisible(!inputPanel.isVisible());
                }
            }
        });
        taskContainerHeader.add(addTaskBtn, BorderLayout.EAST);
    
        // Scrollable Task Panel
        taskContainerScrollPanel = new JPanel();
        taskContainerScrollPanel.setLayout(new BoxLayout(taskContainerScrollPanel, BoxLayout.Y_AXIS));
        taskContainerScrollPanel.setBackground(new Color(0xf1f3f6));
    
        JScrollPane scrollPane = new JScrollPane(taskContainerScrollPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
        taskPanel.add(taskContainerHeader, BorderLayout.NORTH);
        taskPanel.add(scrollPane, BorderLayout.CENTER);
    
        // Initialize SelectedPanel
        selectedPanel = new SelectedPanel();
    
        // Now that everything is initialized, fetch tasks
        if (UserSession.getUserId() != -1) {
            fetchAndDisplayTasks(); // Now runs at the right time
        } else {
            System.out.println("User not logged in yet, skipping task fetch.");
        }
    }
    

    public JPanel getTaskPanel() {
        return taskPanel;
    }

    public void setInputPanel(JPanel inputPanel) {
        this.inputPanel = inputPanel;
    }

    public void addTask(String title, String status) {
        JPanel taskItemPanel = new JPanel();
        taskItemPanel.setLayout(new BoxLayout(taskItemPanel, BoxLayout.X_AXIS));
        taskItemPanel.setBackground(new Color(0xf1f3f6));
        taskItemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Control height while allowing width to expand
        
        JLabel titleLabel = new JLabel(title + " - " + status);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(10, 20, 10, 10));
        
        taskItemPanel.add(titleLabel);
        taskItemPanel.add(Box.createHorizontalGlue()); // This will push everything to the left
        taskContainerScrollPanel.add(taskItemPanel);
        taskContainerScrollPanel.add(Box.createVerticalStrut(5)); // Add spacing between tasks
        
        // Ensure JScrollPane updates properly
        taskContainerScrollPanel.revalidate();
        taskContainerScrollPanel.repaint();
    }

    public void fetchAndDisplayTasks() {
        System.out.println("🔍 Fetching tasks..."); // Debugging
    
        // Clear previous tasks to prevent duplicates
        taskContainerScrollPanel.removeAll();
    
        // Fetch tasks for the logged-in user
        List<String[]> tasks = TaskFetcher.getUserTasks();
    
        System.out.println("✅ Found " + tasks.size() + " tasks!"); // Debugging
    
        for (String[] task : tasks) {
            System.out.println("📌 Task: " + task[0] + " - " + task[1]); // Debugging
            addTask(task[0], task[1]); // task[0] = title, task[1] = status
        }
    
        // Ensure the UI updates properly
        taskContainerScrollPanel.revalidate();
        taskContainerScrollPanel.repaint();
    }
    
}

