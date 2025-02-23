package tasklr.TaskPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import tasklr.createPanel;
import java.awt.*;

public class TaskListPanel {
    private JPanel taskContainerScrollPanel;
    private JPanel taskPanel;

    public TaskListPanel() {
        // Task list container
        taskPanel = createPanel.panel(new Color(0x0A4A7E), new BorderLayout(), new Dimension(500, 1125));
        Border taskContainerBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0x6D6D6D));
        taskPanel.setBorder(taskContainerBorder);

        // Header
        JPanel taskContainerHeader = createPanel.panel(new Color(0x3066EF), new BorderLayout(), new Dimension(0, 70));

        JLabel taskContainerLabel = new JLabel("ADDED TASKS");
        taskContainerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        taskContainerLabel.setForeground(Color.WHITE);
        taskContainerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        taskContainerHeader.add(taskContainerLabel, BorderLayout.CENTER);

        // Scrollable Task Panel
        taskContainerScrollPanel = new JPanel();
        taskContainerScrollPanel.setLayout(new BoxLayout(taskContainerScrollPanel, BoxLayout.Y_AXIS));
        taskContainerScrollPanel.setBackground(new Color(0xE0E3E2));

        JScrollPane scrollPane = new JScrollPane(taskContainerScrollPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        Border scrollPaneBorder = BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x6D6D6D));
        scrollPane.setBorder(scrollPaneBorder);

        taskPanel.add(taskContainerHeader, BorderLayout.NORTH);
        taskPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel getTaskPanel() {
        return taskPanel;
    }

    public void addTask(String title, String desc, String category) {
        // Create a panel for the task
        JPanel taskPanel = new JPanel();
        taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
        taskPanel.setBackground(new Color(0xE0E3E2)); // Match background color
    
        // Title label
        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>");
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(10, 20, 10, 10));
    
        // Description label
        JLabel descLabel = new JLabel("<html>" + desc + "</html>");
        descLabel.setForeground(Color.BLACK);
    
        // Add components to the task panel
        taskPanel.add(titleLabel);
        taskPanel.add(Box.createRigidArea(new Dimension(0, 5))); 
        // taskPanel.add(descLabel);
    
        // Add the task panel to the scrollable container
        taskContainerScrollPanel.add(taskPanel);
        taskContainerScrollPanel.add(Box.createRigidArea(new Dimension(0, 5))); 
    
        // Revalidate and repaint
        taskContainerScrollPanel.revalidate();
        taskContainerScrollPanel.repaint();
    }
}
