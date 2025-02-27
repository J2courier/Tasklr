package tasklr.ExpensePanel;

import javax.swing.BorderFactory;
import javax.swing.*;
import javax.swing.border.Border;
import tasklr.createPanel;
import tasklr.TaskPanel.InputPanel;
import tasklr.TaskPanel.TaskListPanel;  
import java.awt.*;
import tasklr.main.overveiw.totalexpense; // Import totalexpense

public class expense {
    public static JPanel createExpensePanel() {
        // Main panel of task page
        JPanel panel = createPanel.panel(null, new BorderLayout(), new Dimension(100, 100));
        Border panelBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x6D6D6D));
        panel.setBorder(panelBorder);
        
        // Center panel
        JPanel CenterContainer = createPanel.panel(new Color(0xf1f3f6), new BorderLayout(), new Dimension(0, 0));
        panel.add(CenterContainer, BorderLayout.CENTER);

        // Create instances of expenseListPanel, expenseInputPanel, and totalexpense
        expenseListPanel expenseListPanel = new expenseListPanel(); 
        totalexpense totalExpensePanel = new totalexpense(); // Create totalexpense instance
        expenseInputPanel inputPanel = new expenseInputPanel(expenseListPanel, totalExpensePanel); // Pass reference to totalexpense
        expenseListPanel.setInputPanel(inputPanel.getInputPanel()); // Set inputPanel reference

        CenterContainer.add(expenseListPanel.getTaskPanel(), BorderLayout.WEST);
        CenterContainer.add(inputPanel.getInputPanel(), BorderLayout.CENTER);

        return panel; 
    }
}
