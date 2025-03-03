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
        JPanel CenterContainer = createPanel.panel(new Color(0xf1f3f6), new BorderLayout(), new Dimension(0, 0));
        panel.add(CenterContainer, BorderLayout.CENTER);

        // Create instances of TaskListPanel, InputPanel, and totaltask
        TaskListPanel taskListPanel = new TaskListPanel(CenterContainer); // Pass CenterContainer parameter
       
        InputPanel inputPanel = new InputPanel(taskListPanel); // Pass reference to totaltask
        SelectedPanel selectedPanel = new SelectedPanel();
        // selectedPanel.createEditPanel().setVisible(false);
        taskListPanel.setInputPanel(inputPanel.getInputPanel()); // Set inputPanel reference

        CenterContainer.add(taskListPanel.getTaskPanel(), BorderLayout.WEST);
        CenterContainer.add(selectedPanel.createEditPanel(), BorderLayout.CENTER);
        CenterContainer.add(inputPanel.getInputPanel(), BorderLayout.CENTER);

        return panel; 
    }
}