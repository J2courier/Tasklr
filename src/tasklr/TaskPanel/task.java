package tasklr.TaskPanel;


import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.Border;
import tasklr.ComponentUtil;
import tasklr.createPanel;


import java.awt.*;

public class task {

    public static JPanel createTaskPanel(String username) {
        JPanel panel = createPanel.panel(new Color(0xf1f3f6), new BorderLayout(), new Dimension(100, 100));
        Border panelBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        panel.setBorder(panelBorder);
    
        JPanel inputPanel = createInputPanel(username);
        JPanel listContainer = createListContainer();
        panel.add(inputPanel, BorderLayout.CENTER); 
        panel.add(listContainer, BorderLayout.WEST); 

        return panel;
    }
    

    private static JPanel createInputPanel(String username) {
        JPanel inputPanel = createPanel.panel(null, new GridBagLayout(), new Dimension(0, 0));
        
        // Username label showing logged-in user
        JLabel usernameLabel = new JLabel("Hello, " + username + "!");
        usernameLabel.setFont(new Font("Segoe UI Variable", Font.BOLD, 60));
        
        // Paragraph area for task description
        JLabel paragraph = new JLabel("Every task you add is a step closer to achieving your goals.");
        paragraph.setFont(new Font("Segoe UI Variable", Font.PLAIN, 16));
        // Title field
        JTextField titleField = new JTextField(20);
        titleField.setPreferredSize(new Dimension(700, 40));
        
        JLabel AddTaskLbl = new JLabel("Add Task");
        AddTaskLbl.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));

        // Add Task button
        JButton addTaskBtn = new JButton("Add Task");
        addTaskBtn.setBackground(new Color(0x0A4A7E));
        addTaskBtn.setForeground(Color.WHITE);
        addTaskBtn.setPreferredSize(new Dimension(20, 40));

        JLabel setDueLbl = new JLabel("Set Due Date");
        setDueLbl.setFont(new Font("Segoe UI Variable", Font.BOLD, 16));
        
        // Date components
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(100, 40));

        JButton addDueDateBtn = new JButton("Add Due Date");
        addDueDateBtn.setBackground(new Color(0x0A4A7E));
        addDueDateBtn.setForeground(Color.WHITE);
        addDueDateBtn.setPreferredSize(new Dimension(20,40));

        JPanel spacer = createPanel.panel(null, null, new Dimension(0, 300));
        // Add components using ComponentUtil
        // Row 0: Username (spans 2 columns)
        ComponentUtil.addComponent(inputPanel, usernameLabel, 0, 0, 2, 1, new Insets(10, 40, 10, 10), 0);
        ComponentUtil.addComponent(inputPanel, paragraph, 0, 1, 2, 1, new Insets(10, 40, 20, 10), 0);
        ComponentUtil.addComponent(inputPanel, titleField, 0, 2, 1, 1, new Insets(20, 10, 20, 5), 0);
        ComponentUtil.addComponent(inputPanel, addTaskBtn, 1, 2, 1, 1, new Insets(20, 5, 20, 10), 0);
        ComponentUtil.addComponent(inputPanel, dateChooser, 0, 3, 1, 1, new Insets(5, 10, 10, 5), 0);
        ComponentUtil.addComponent(inputPanel, addDueDateBtn, 1, 3, 1, 1, new Insets(5, 5, 10, 10), 0);
        ComponentUtil.addComponent(inputPanel, spacer, 0, 4, 2, 1, new Insets(10, 10, 10, 10), 0);
        return inputPanel;
    }

    private static JPanel createListContainer() {
        JPanel panel = createPanel.panel(null, new BorderLayout(), new Dimension(400, 0));
        Border border = BorderFactory.createMatteBorder(1, 0, 0, 1, new Color(0x749AAD));
        panel.setBorder(border);
        return panel;
    }
}