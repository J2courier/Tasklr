package tasklr.main.ui.panels.overveiw;
import javax.swing.*;
import javax.swing.border.Border;

import tasklr.utilities.createPanel;

import java.awt.*;

public class TaskOverview {
    public static JPanel createTaskOverviewHeader(){
        JPanel panel = createPanel.panel(null, new BorderLayout(), new Dimension(100, 70));
        Border taskov_border = BorderFactory.createLineBorder(new Color(0xB9B9B9), 1);
        JLabel taskLabel = new JLabel("TASK OVERVIEW");
        

        taskLabel.setForeground(Color.BLACK);
        taskLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        taskLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        panel.add(taskLabel, BorderLayout.CENTER);
        
        panel.setBorder(taskov_border);
        return panel;
    }
}
