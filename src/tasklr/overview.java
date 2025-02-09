package tasklr;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.*;
public class overview {

    public static JPanel createOverview() {            
        //parent container of the overview
        JPanel panel = createPanel.panel(new Color(0x292E34), new GridBagLayout(), new Dimension(400, 0));

        //task components display
        JPanel header_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(400, 70));
        Border border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        header_panel.setBorder(border);

        //task label and Panel
        JPanel task_overview_lbl = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(100, 70));
        JLabel taskLabel = new JLabel("Task Overview");
        task_overview_lbl.add(taskLabel, BorderLayout.CENTER);
        Border taskov_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        task_overview_lbl.setBorder(taskov_border);
        
        //ongoing panel
        JPanel ongoing_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 100));
        Border ongoing_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        ongoing_panel.setBorder(ongoing_border);

        //done panel
        JPanel done_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 100));
        Border done_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        done_panel.setBorder(done_border);

        //total task panel
        JPanel totalTask_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 100));
        Border totalTask_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        totalTask_panel.setBorder(totalTask_border);

        //expense label and Panel
        JPanel expense_overview_lbl = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(100, 70));
        JLabel expenseLabel = new JLabel("Task Overview");
        expense_overview_lbl.add(expenseLabel, BorderLayout.CENTER);
        Border expenseov_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        expense_overview_lbl.setBorder(expenseov_border);
        
        //paid panel
        JPanel paid_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 100));
        Border paid_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        paid_panel.setBorder(paid_border);

        //unpaid panel
        JPanel unpaid_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 100));
        Border unpaid_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        unpaid_panel.setBorder(unpaid_border);

        //total expense panel
        JPanel totalExpense_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 100));
        Border totalExpense_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        totalExpense_panel.setBorder(totalExpense_border);

        addComponent(panel, header_panel, 0, 0, 3, 1);
        // addGap(header_panel, 0, 1, 3, 10);

        addComponent(panel, task_overview_lbl, 0, 2, 3, 1);
        // addGap(task_overview_lbl, 0, 3, 3, 5);

        addComponent(panel, ongoing_panel, 0, 4, 1, 1);
        // addGap(ongoing_panel, 0, 3, 3, 5);

        addComponent(panel, done_panel, 1, 4, 1, 1);
        // addGap(done_panel, 0, 5, 3, 10);

        addComponent(panel, totalTask_panel, 2, 4, 1, 1);
        // addGap(totalTask_panel, 0, 3, 3, 5);

        addComponent(panel, expense_overview_lbl, 0, 6, 3, 1);
        // addGap(expense_overview_lbl, 0, 3, 3, 5);

        addComponent(panel, paid_panel, 0, 7, 2, 1);
        // addGap(paid_panel, 0, 3, 3, 5);

        addComponent(panel, unpaid_panel, 1, 7,1, 1);
        // addGap(unpaid_panel, 0, 3, 3, 5);

        addComponent(panel, totalExpense_panel, 2, 7, 1, 1);
        // addGap(totalExpense_panel, 0, 3, 3, 5);

        return panel;
    }

    private static void addComponent(JPanel panel, JComponent comp, int x, int y, int width, int height) {
        GridBagConstraints gbc = new GridBagConstraints(); // New instance
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        panel.add(comp, gbc);
    }

    // === Utility Method for Adding Gaps ===
    private static void addGap(JPanel panel, int x, int y, int width, int height) {
        GridBagConstraints gbc = new GridBagConstraints(); // New instance
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = 1;
        panel.add(Box.createVerticalStrut(height), gbc);
    }

}


