package tasklr.main.overveiw;

import javax.swing.*;
import javax.swing.border.Border;

import tasklr.createPanel;

import java.awt.*;


public class overview {

    public static JPanel createOverview(String username) {            
        //parent container of the overview
        JPanel panel = createPanel.panel(null, new GridBagLayout(), new Dimension(400, 0));
        // Border panelBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        // panel.setBorder(panelBorder);
        //task components display

        JPanel profile = createPanel.panel(new Color(0x0A4A7E), new BorderLayout(), new Dimension(400, 100));
        Border border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        JLabel userLabel = new JLabel("Welcome, " + username + "!");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profile.add(userLabel, BorderLayout.CENTER);
        profile.setBorder(border);

        //task label and Panel
        JPanel task_overview_lbl = createPanel.panel(new Color(0x0A4A7E), new BorderLayout(), new Dimension(100, 70));
        
        JLabel taskLabel = new JLabel("Task Overview");
        taskLabel.setForeground(Color.WHITE);
        taskLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        task_overview_lbl.add(taskLabel, BorderLayout.CENTER);
        Border taskov_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        task_overview_lbl.setBorder(taskov_border);
        
   


        //expense label and Panel
        JPanel expense_overview_lbl = createPanel.panel(new Color(0x0A4A7E), new BorderLayout(), new Dimension(100, 70));
        JLabel expenseLabel = new JLabel("Expense Overview");
        expenseLabel.setForeground(Color.WHITE);
        expenseLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        expense_overview_lbl.add(expenseLabel, BorderLayout.CENTER);
        Border expenseov_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        expense_overview_lbl.setBorder(expenseov_border);
        
        
 
        JPanel spacer = createPanel.panel(null, null, new Dimension(100, 370));

        addComponent(panel, profile, 0, 0, 3, 1, new Insets(0, 5,10, 5));
        addComponent(panel, task_overview_lbl, 0, 2, 3, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, ongoing.createOngoingPanel(), 0, 4, 1, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, done.createDonePanel(), 1, 4, 1, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, totaltask.createTotalTaskPanel(), 2, 4, 1, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, expense_overview_lbl, 0, 6, 3, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, paid.createPaidPanel(), 0, 7, 1, 1, new Insets(5, 5, 10, 5));   
        addComponent(panel, unpaid.createUnpaidPanel(), 1, 7,1, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, totalexpense.createTotalExpensePanel(), 2, 7, 1, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, spacer, 0, 8, 3, 1, new Insets(5, 5, 5, 5));
    
        return panel;
    }

    private static void addComponent(JPanel panel, JComponent comp, int x, int y, int width, int height, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.insets = insets; // Use custom insets
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        panel.add(comp, gbc);
    }

}
