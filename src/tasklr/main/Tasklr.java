package tasklr.main;

//utilties
import javax.swing.*;
import java.awt.*;
//file pathing:
import tasklr.createPanel;
import tasklr.expense;
import tasklr.expenselist;
import tasklr.overview;
import tasklr.task;
import tasklr.tasklist;
import tasklr.login.login;

public class Tasklr extends JFrame {
    
    public Tasklr() {
        setTitle("Tasklr");
        setSize(1200, 920);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(1800, 1000));
        getContentPane().setBackground(new Color(0x1C2128));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        ImageIcon appIcon = new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png");
        setIconImage(appIcon.getImage());
        
        JPanel body = createPanel.panel(new Color(0x1C2128), new CardLayout(), new Dimension(0, 0));
        JPanel navbar = createPanel.panel(new Color(0x292E34), new FlowLayout(FlowLayout.CENTER, 10, 40), new Dimension(100, 0));
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
        body.add(homePanel(), "homePanel");
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
        
    public static JPanel homePanel() {
        JPanel homePanel = createPanel.panel(new Color(0x1C2128), new GridBagLayout(), new Dimension(100, 100));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10); // Add margin

        gbc.gridx = 0;
        gbc.gridy = 0;       
        homePanel.add(tasklist.createTaskList(), gbc);//just call the sub class tasklist and access the method
        gbc.gridx = 1;
        homePanel.add(expenselist.createExpenseList(), gbc);//just call the sub class tasklist and access the method
        gbc.gridx = 2;
        homePanel.add(overview.createOverview(), gbc);//just call the sub class tasklist and access the method
        
        return homePanel;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true);
        });
    }
}
