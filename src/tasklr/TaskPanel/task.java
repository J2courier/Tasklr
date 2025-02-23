package tasklr.TaskPanel;

import javax.swing.*;
import javax.swing.border.Border;
import tasklr.createPanel;
import java.awt.*;

public class task {
    public static JPanel createTaskPanel() {
        // Main panel of task page
        JPanel panel = createPanel.panel(null, new BorderLayout(), new Dimension(100, 100));
        Border panelBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        panel.setBorder(panelBorder);
        
        // Center panel
        JPanel CenterContainer = createPanel.panel(new Color(0xE0E3E2), new GridBagLayout(), new Dimension(0, 0));
        panel.add(CenterContainer, BorderLayout.CENTER);

        // Create instances of TaskListPanel and InputPanel
        TaskListPanel taskListPanel = new TaskListPanel(); 
        InputPanel inputPanel = new InputPanel(taskListPanel); // Pass reference

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // No extra space allocation
        gbc.anchor = GridBagConstraints.WEST; // Align in flex-start    
        gbc.fill = GridBagConstraints.NONE;
        // gbc.insets = new Insets(5, 5, 5, 0); // Margin around component
        CenterContainer.add(taskListPanel.getTaskPanel(), gbc); // Call instance method

        gbc.gridx = 1;
        gbc.weightx = 1; // Allows inputContainer to take available space
        gbc.fill = GridBagConstraints.NONE;
        CenterContainer.add(inputPanel.getInputPanel(), gbc); // Call instance method

        return panel; 
    }
}
