package tasklr.TaskPanel;

import javax.swing.*;
import javax.swing.border.Border;

import tasklr.createPanel;

import java.awt.*;

public class task {
//this class is for task option
    public static JPanel createTaskPanel() {
        //main panel of task page
        JPanel panel = createPanel.panel(new Color(0xFFFFFF), new BorderLayout(), new Dimension(100, 100));
        Border panelBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        panel.setBorder(panelBorder);

        //north and center container inside the parent panel
        //north panel
        JPanel LabelContainer = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(0, 100));
        Border LabelContainerBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        LabelContainer.setBorder(LabelContainerBorder);
        JLabel TaskLabel = new JLabel("TASK");
        TaskLabel.setForeground(Color.WHITE);
        TaskLabel.setFont(new Font("Arial", Font.BOLD, 30));
        TaskLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        LabelContainer.add(TaskLabel, BorderLayout.CENTER);

        //center panel
        JPanel CenterContainer = createPanel.panel(new Color(0x1C2128), new GridBagLayout(), new Dimension(0, 0));
        // Border CenterContainerBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        // CenterContainer.setBorder(CenterContainerBorder);

        //adding the north and center panel
        panel.add(LabelContainer, BorderLayout.NORTH);
        panel.add(CenterContainer, BorderLayout.CENTER);
        
        //two panels inside the Center Panel
        //task list container
        JPanel taskContainer = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(500, 1010));
        Border taskContainerBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        taskContainer.setBorder(taskContainerBorder);

        //input field container
        JPanel inputContainer = createPanel.panel(new Color(0x292E34), new GridBagLayout(), new Dimension(700, 1010));
        Border inputContainerBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        inputContainer.setBorder(inputContainerBorder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // No extra space allocation
        gbc.anchor = GridBagConstraints.WEST;//to be align in flex-start    
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 15, 5, 0); // Margin around component
        CenterContainer.add(taskContainer, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1; // Allows inputContainer to take available space
        gbc.fill = GridBagConstraints.NONE;
        CenterContainer.add(inputContainer, gbc);
        return panel; 
    }
        
    
}
