package tasklr.TaskPanel;


import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.Border;

import tasklr.ComponentUtil;
import tasklr.createButton;
import tasklr.createPanel;
import tasklr.authentication.UserSession;

import java.awt.*;

public class task {

    public static JPanel createTaskPanel(String username) {
        JPanel panel = createPanel.panel(null, new BorderLayout(), new Dimension(100, 100));
        Border panelBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        panel.setBorder(panelBorder);
    
        JPanel inputPanel = createInputPanel(username);
        panel.add(inputPanel, BorderLayout.CENTER); // Add input panel to task panel
    
        return panel;
    }
    

    private static JPanel createInputPanel(String username) {
        JPanel inputPanel = createPanel.panel(null, new GridBagLayout(), new Dimension(0, 0));
        
        // Username label showing logged-in user
        JLabel usernameLabel = new JLabel("HELLO " + username + "!");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 40));
        
        // Paragraph area for task description
        JLabel paragraph = new JLabel("Every task you add is a step closer to achieving your goals. By organizing your priorities, you’re taking control of your day and setting yourself up for success. Go ahead—take that step!");
        
        // Title field
        JTextField titleField = new JTextField(20);
        titleField.setPreferredSize(new Dimension(100, 60));
        
        // Add Task button
        JButton addTaskBtn = new JButton("Add Task");
        addTaskBtn.setBackground(new Color(0x0A4A7E));
        addTaskBtn.setForeground(Color.WHITE);
        addTaskBtn.setPreferredSize(new Dimension(100, 60));

        JLabel setDueLbl = new JLabel("Set Due Date");
        setDueLbl.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Date components
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(100, 60));

        JButton addDueDateBtn = new JButton("Add Due Date");
        addDueDateBtn.setBackground(new Color(0x0A4A7E));
        addDueDateBtn.setForeground(Color.WHITE);
        addDueDateBtn.setPreferredSize(new Dimension(100, 60));

        // Add components using ComponentUtil
        // Row 0: Username (spans 2 columns)
        ComponentUtil.addComponent(inputPanel, usernameLabel, 0, 0, 2, 1, new Insets(10, 10, 10, 10), 0.0);
        ComponentUtil.addComponent(inputPanel, paragraph, 0, 1, 2, 1, new Insets(10, 10, 10, 10), 0.0);
        ComponentUtil.addComponent(inputPanel, titleField, 0, 2, 1, 1, new Insets(10, 10, 10, 5), 0.0);
        ComponentUtil.addComponent(inputPanel, addTaskBtn, 1, 2, 1, 1, new Insets(10, 5, 10, 10), 0.0);
        ComponentUtil.addComponent(inputPanel, setDueLbl, 0, 3, 1, 1, new Insets(10, 10, 10, 5), 0.0);
        ComponentUtil.addComponent(inputPanel, dateChooser, 0, 4, 2, 1, new Insets(10, 10, 10, 10), 0.0);
        ComponentUtil.addComponent(inputPanel, addDueDateBtn, 0, 5, 2, 1, new Insets(10, 10, 10, 10), 0.0);
        return inputPanel;
    }
}