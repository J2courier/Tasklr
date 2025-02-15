package tasklr.ExpensePanel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import tasklr.createPanel;

import java.awt.*;
public class expense {
    public static JPanel createExpensePanel(){
        JPanel panel = createPanel.panel(new Color(0x1C2128), new GridBagLayout(), new Dimension(100, 100));
        Border panelBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        panel.setBorder(panelBorder);
        JPanel expenseContainer = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(300, 1));
        Border border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        expenseContainer.setBorder(border);
        expenseContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel inputContainer = createPanel.panel(new Color(0x292E34), new GridBagLayout(), new Dimension(500, 0));
        
        panel.add(inputContainer);
        panel.add(expenseContainer);

        JLabel taskLabel = new JLabel("Task");
        taskLabel.setFont(new Font("Arial", Font.BOLD, 20));
        taskLabel.setVerticalAlignment(SwingConstants.CENTER);
        taskLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel taskContainerHeadLabel = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(95, 50));
        taskContainerHeadLabel.add(taskLabel, BorderLayout.CENTER);
        expenseContainer.add(taskContainerHeadLabel, BorderLayout.NORTH);

        JPanel taskListContainer = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(95, 0));
        expenseContainer.add(taskListContainer, BorderLayout.SOUTH);

        JTextField titleField = new JTextField(50);
        inputContainer.add(titleField);
        return panel; 
    }
}
