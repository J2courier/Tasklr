    package tasklr.main;

//utilties
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
//file pathing:
import tasklr.createPanel;
import tasklr.ExpensePanel.expense;
import tasklr.TaskPanel.task;
import tasklr.login.login;
import tasklr.main.expenselist.expenselist;
import tasklr.main.overveiw.overview;
import tasklr.main.tasklist.tasklist;

public class Tasklr extends JFrame {
    private String username;

    public Tasklr(String username) {
        this.username = username;
        setTitle("Tasklr");
        setSize(1200, 1170);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(1800, 1170));
        getContentPane().setBackground(new Color(0xE0E3E2));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        ImageIcon appIcon = new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png");
        setIconImage(appIcon.getImage());
        
        JPanel body = createPanel.panel(new Color(0xE0E3E2), new CardLayout(), new Dimension(0, 0));
        JPanel navbar = createPanel.panel(new Color(0x3066EF), new FlowLayout(FlowLayout.CENTER, 10, 40), new Dimension(100, 0));
        // Border border = BorderFactory.createLineBorder(new Color(0xFFFFFF), 1);
        // navbar.setBorder(border);
        add(body, BorderLayout.CENTER);
        add(navbar, BorderLayout.WEST);
        

        ImageIcon homeBtn = new ImageIcon(new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/home.png").getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
        ImageIcon taskBtn = new ImageIcon(new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/task.png").getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
        ImageIcon expenseBtn = new ImageIcon(new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/expense.png").getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
        
        JLabel homeBtnIcon = new JLabel(homeBtn);
        JLabel taskBtnIcon = new JLabel(taskBtn);
        JLabel expenseBtnIcon = new JLabel(expenseBtn);
        navbar.add(homeBtnIcon);
        navbar.add(taskBtnIcon);
        navbar.add(expenseBtnIcon);    
        body.add(homePanel(username), "homePanel");
        body.add(task.createTaskPanel(), "taskPanel");
        body.add(expense.createExpensePanel(), "expensePanel");
        
        CardLayout cardLayout = (CardLayout) body.getLayout();
        
        homeBtnIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(body, "homePanel");
            }
        });
        
        taskBtnIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(body, "taskPanel");
            }
        });
        
        expenseBtnIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(body, "expensePanel");
            }
        });
    }
        
    public static JPanel homePanel(String username) {
        JPanel homePanel = createPanel.panel(new Color(0xE0E3E2), new GridBagLayout(), new Dimension(100, 100));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1.0;
        // gbc.insets = new Insets(10, 5, 10, 5); // Add margin
        gbc.gridx = 0;
        gbc.gridy = 0;       
        homePanel.add(overview.createOverview(username), gbc);//just call the sub class tasklist and access the method
        
        return homePanel;
    }

    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Tasklr("admin").setVisible(true);
            // new login().setVisible(true);
        });
    }
}
