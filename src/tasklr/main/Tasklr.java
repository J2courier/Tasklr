package tasklr.main;

//utilties
import javax.swing.*;
import java.awt.*;

import tasklr.main.quizPanel.StudyPanel;
import tasklr.main.TaskPanel.TaskFetcher;
import tasklr.main.TaskPanel.task;
import tasklr.main.overveiw.overview;
import tasklr.utilities.createPanel;
import tasklr.authentication.login;

public class Tasklr extends JFrame {
    private String username;
    private JPanel centerContainer; 

    public Tasklr(String username) {
        pack();
        this.username = username;             
        setTitle("Tasklr");
        setSize(1200, 1170);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(1800, 1170));
        getContentPane().setBackground(new Color(0xFFFFFF));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        ImageIcon appIcon = new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png");
        setIconImage(appIcon.getImage());
        
        JPanel body = createPanel.panel(new Color(0xFFFFFF), new CardLayout(), new Dimension(0, 0));
        JPanel navbar = createPanel.panel(new Color(0x191919), new FlowLayout(FlowLayout.CENTER, 10, 40), new Dimension(70, 0));
        // Border border = BorderFactory.createLineBorder(new Color(0xFFFFFF), 1);
        // navbar.setBorder(border);
        add(body, BorderLayout.CENTER);
        add(navbar, BorderLayout.WEST);
        

        ImageIcon homeBtn = new ImageIcon(new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/HomeLogo.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        ImageIcon taskBtn = new ImageIcon(new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/TaskLogo.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        ImageIcon quizBtn = new ImageIcon(new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AddQuizWhite.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        JLabel homeBtnIcon = new JLabel(homeBtn);
        JLabel taskBtnIcon = new JLabel(taskBtn);
        JLabel quizBtnIcon = new JLabel(quizBtn);
        
        navbar.add(homeBtnIcon);
        navbar.add(taskBtnIcon);
        navbar.add(quizBtnIcon);
  
        body.add(homePanel(username), "homePanel");
        body.add(task.createTaskPanel(username), "taskPanel");
        body.add(StudyPanel.createStudyPanel(username), "quizPanel");
        
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
        
        quizBtnIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(body, "quizPanel");
            }
        });
    
    }
        
    public static JPanel homePanel(String username) {
        JPanel homePanel = createPanel.panel(new Color(0xf1f3f6), new GridBagLayout(), new Dimension(100, 100));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1.0;
        
        gbc.gridx = 0;
        gbc.gridy = 0;       
        homePanel.add(overview.createOverview(username), gbc);//just call the sub class tasklist and access the method
        
        return homePanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // new Tasklr("admin").setVisible(true);
            new login().setVisible(true);
            TaskFetcher tf = new TaskFetcher();
            tf.getUserTasks();;
        });
    }
}
