package tasklr.TaskPanel;

import javax.swing.*;
import javax.swing.border.Border;

import tasklr.createPanel;

import java.awt.*;
public class TaskListPanel {
    
    public static JPanel createDisplayPanel() {
        //two panels inside the Center Panel
        //task list container
        JPanel taskContainer = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(500, 1100));
        Border taskContainerBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        taskContainer.setBorder(taskContainerBorder);

       //head and body component of the tasklist container panel
        JPanel taskContainerHeader = createPanel.panel(new Color(0x292E34), new BorderLayout(),new Dimension(0, 70));
        Border taskContainerHeaderBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        taskContainerHeader.setBorder(taskContainerHeaderBorder);

        //label of the tasklist header
        JLabel taskContainerLabel = new JLabel("ON-GOING LIST");
        taskContainerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        taskContainerLabel.setForeground(Color.WHITE);
        taskContainerLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        taskContainerHeader.add(taskContainerLabel, BorderLayout.CENTER);

        //SrollPanel
        JPanel taskContainerScrollPanel = new JPanel();
        taskContainerScrollPanel.setLayout(new BoxLayout(taskContainerScrollPanel, BoxLayout.Y_AXIS));
        taskContainerScrollPanel.setBackground(new Color(0x292E34));

        //JScrollPane
        JScrollPane scrollPane = new JScrollPane(taskContainerScrollPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        taskContainer.add(taskContainerHeader, BorderLayout.NORTH);        
        taskContainer.add(scrollPane, BorderLayout.CENTER);
        return taskContainer;
        
    }

}
