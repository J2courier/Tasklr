package tasklr;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class overview {

    public static JPanel createOverview(String username) {            
        //parent container of the overview
        JPanel panel = createPanel.panel(new Color(0x292E34), new GridBagLayout(), new Dimension(400, 0));
        Border panelBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0x6D6D6D));
        panel.setBorder(panelBorder);
        //task components display

        JPanel profile = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(400, 100));
        Border border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        JLabel userLabel = new JLabel("Welcome, " + username + "!");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profile.add(userLabel, BorderLayout.CENTER);
        profile.setBorder(border);

        //task label and Panel
        JPanel task_overview_lbl = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(100, 70));
        
        JLabel taskLabel = new JLabel("Task Overview");
        taskLabel.setForeground(Color.WHITE);
        taskLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        task_overview_lbl.add(taskLabel, BorderLayout.CENTER);
        Border taskov_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        task_overview_lbl.setBorder(taskov_border);
        
        //ongoing panel
        JPanel ongoing_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 200));
        Border ongoing_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        ongoing_panel.setBorder(ongoing_border);

        //done panel
        JPanel done_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 200));
        Border done_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        done_panel.setBorder(done_border);

        //total task panel
        JPanel totalTask_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 200));
        Border totalTask_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        totalTask_panel.setBorder(totalTask_border);

        //expense label and Panel
        JPanel expense_overview_lbl = createPanel.panel(new Color(0x292E34), new BorderLayout(), new Dimension(100, 70));
        JLabel expenseLabel = new JLabel("Expense Overview");
        expenseLabel.setForeground(Color.WHITE);
        expenseLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        expense_overview_lbl.add(expenseLabel, BorderLayout.CENTER);
        Border expenseov_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        expense_overview_lbl.setBorder(expenseov_border);
        
        //paid panel
        JPanel paid_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 200));
        Border paid_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        paid_panel.setBorder(paid_border);

        //unpaid panel
        JPanel unpaid_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 200));
        Border unpaid_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        unpaid_panel.setBorder(unpaid_border);

        //total expense panel
        JPanel totalExpense_panel = createPanel.panel(new Color(0x292E34), null, new Dimension(100, 200));
        Border totalExpense_border = BorderFactory.createLineBorder(new Color(0x6D6D6D), 1);
        totalExpense_panel.setBorder(totalExpense_border);

        JPanel spacer = createPanel.panel(null, null, new Dimension(100, 370));

        addComponent(panel, profile, 0, 0, 3, 1, new Insets(0, 5,10, 5));
        addComponent(panel, task_overview_lbl, 0, 2, 3, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, ongoing_panel, 0, 4, 1, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, done_panel, 1, 4, 1, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, totalTask_panel, 2, 4, 1, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, expense_overview_lbl, 0, 6, 3, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, paid_panel, 0, 7, 1, 1, new Insets(5, 5, 10, 5));   
        addComponent(panel, unpaid_panel, 1, 7,1, 1, new Insets(5, 5, 10, 5));
        addComponent(panel, totalExpense_panel, 2, 7, 1, 1, new Insets(5, 5, 10, 5));
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
