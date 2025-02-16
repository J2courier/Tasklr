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
        
        //center panel
        JPanel CenterContainer = createPanel.panel(new Color(0x1C2128), new GridBagLayout(), new Dimension(0, 0));
        panel.add(CenterContainer, BorderLayout.CENTER);

        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // No extra space allocation
        gbc.anchor = GridBagConstraints.WEST;//to be align in flex-start    
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 15, 5, 0); // Margin around component
        CenterContainer.add(TaskListPanel.createDisplayPanel(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1; // Allows inputContainer to take available space
        gbc.fill = GridBagConstraints.NONE;
        CenterContainer.add(InputPanel.createInputPanel(), gbc);
        return panel; 
    }
        
    
}
