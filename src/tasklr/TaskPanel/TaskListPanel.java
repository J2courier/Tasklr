package tasklr.TaskPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import tasklr.createButton;
import tasklr.createPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import tasklr.setProgress;

public class TaskListPanel {
    private JPanel taskContainerScrollPanel;
    private JPanel taskPanel;
    private JPanel inputPanel; // Reference to the input panel
    private SelectedPanel selectedPanel; // Reference to SelectedPanel
    private JPanel centerContainer; // Reference to CenterContainer

    public TaskListPanel(JPanel centerContainer) { // Add centerContainer parameter
        this.centerContainer = centerContainer; // Initialize centerContainer

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
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scroll

        taskPanel.add(taskContainerHeader, BorderLayout.NORTH);
        taskPanel.add(scrollPane, BorderLayout.CENTER);

        // Initialize SelectedPanel
        selectedPanel = new SelectedPanel();
    }

    public JPanel getTaskPanel() {
        return taskPanel;
    }

    public void setInputPanel(JPanel inputPanel) {
        this.inputPanel = inputPanel;
    }

    public void addTask(String title, String desc, String category) {
        // Create a panel for the task
        JPanel taskPanel = new JPanel();
        taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
        taskPanel.setBackground(new Color(0xf1f3f6)); // Match background color
    
        // Title label
        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>");
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(10, 20, 10, 10));
        
        // Add mouse listener to title label
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel editPanel = selectedPanel.createEditPanel();
                editPanel.setVisible(true);
                // Add the edit panel to the center container
                centerContainer.add(editPanel, BorderLayout.CENTER);
                centerContainer.revalidate();
                centerContainer.repaint();
            }
        });
    
        // Description label
        JLabel descLabel = new JLabel("<html>" + desc + "</html>");
        descLabel.setForeground(Color.BLACK);
    
        // Add components to the task panel
        taskPanel.add(titleLabel);
        taskPanel.add(Box.createRigidArea(new Dimension(0, 5))); 
    
        // Add the task panel to the scrollable container
        taskContainerScrollPanel.add(taskPanel);
        taskContainerScrollPanel.add(Box.createRigidArea(new Dimension(0, 5))); 
    
        // Revalidate and repaint
        taskContainerScrollPanel.revalidate();
        taskContainerScrollPanel.repaint();
    }
}

