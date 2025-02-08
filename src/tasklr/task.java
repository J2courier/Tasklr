package tasklr;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

public class task {

    public static JPanel createTaskPanel() {
        JPanel panel = createPanel.panel(new Color(0xFFFFFF), new GridBagLayout(), new Dimension(100, 100));
        JPanel taskContainer = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(300, 0));
        Border border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        taskContainer.setBorder(border);
        taskContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel inputContainer = createPanel.panel(new Color(0x292E34), new GridBagLayout(), new Dimension(500, 0));
        
        panel.add(inputContainer);
        panel.add(taskContainer);

        JLabel taskLabel = new JLabel("Task");
        taskLabel.setFont(new Font("Arial", Font.BOLD, 20));
        taskLabel.setVerticalAlignment(SwingConstants.CENTER);
        taskLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel taskContainerHeadLabel = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(95, 50));
        taskContainerHeadLabel.add(taskLabel, BorderLayout.CENTER);
        taskContainer.add(taskContainerHeadLabel, BorderLayout.NORTH);

        JPanel taskListContainer = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(95, 0));
        taskContainer.add(taskListContainer, BorderLayout.SOUTH);

        JTextField titleField = new JTextField(50);
        inputContainer.add(titleField);
        return panel; 
    }
        
    
}
