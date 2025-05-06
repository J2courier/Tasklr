package tasklr.main.ui.frames;

//utilties
import javax.swing.*;
import java.awt.*;
import tasklr.authentication.Login;
import tasklr.utilities.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import tasklr.main.ui.panels.Home.GettingStartedPanel;
import tasklr.main.ui.panels.TaskPanel.TaskFetcher;
import tasklr.main.ui.panels.TaskPanel.task;
import tasklr.main.ui.panels.quizPanel.StudyPanel;
import tasklr.main.ui.panels.Home.HomePanel;
import tasklr.main.ui.panels.Settings.SettingsPanel;


public class Tasklr extends JFrame {
    private String username;
    private static final Color NAV_BACKGROUND_COLOR = new Color(0x275CE2);
    private JPanel body;
    private CardLayout cardLayout;

    public Tasklr(String username) {
        pack();
        this.username = username;             
        setTitle("Duets");
        setSize(1200, 1170);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(1800, 1170));
        getContentPane().setBackground(new Color(0xFFFFFF));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        ImageIcon originalIcon = new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/AppLogo.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        ImageIcon appIcon = new ImageIcon(scaledImage);
        setIconImage(appIcon.getImage());
        
        // Initialize components
        initializeComponents();
        
        // Add window listener to show getting started panel after frame is visible
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    GettingStartedPanel.showIfFirstTime(Tasklr.this, username);
                });
            }
        });
    }

    private void initializeComponents() {
        // Initialize body panel with CardLayout
        body = createPanel.panel(new Color(0xFFFFFF), new CardLayout(), new Dimension(0, 0));
        cardLayout = (CardLayout) body.getLayout();
        
        // Create navbar
        JPanel navbar = createPanel.panel(NAV_BACKGROUND_COLOR, new FlowLayout(FlowLayout.CENTER, 10, 20), new Dimension(70, 0));
        navbar.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0));
        
        add(body, BorderLayout.CENTER);
        add(navbar, BorderLayout.WEST);

        // Create navigation icons
        ImageIcon homeBtn = new ImageIcon(new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/Home.png")
            .getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        ImageIcon taskBtn = new ImageIcon(new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/Task.png")
            .getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        ImageIcon quizBtn = new ImageIcon(new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/FlashcardIcon.png")
            .getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        ImageIcon settingsBtn = new ImageIcon(new ImageIcon("C:/Users/ADMIN/Desktop/Tasklr/resource/icons/settings.png")
            .getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

        JLabel homeBtnIcon = new JLabel(homeBtn);
        JLabel taskBtnIcon = new JLabel(taskBtn);
        JLabel quizBtnIcon = new JLabel(quizBtn);
        JLabel settingsBtnIcon = new JLabel(settingsBtn);
        
        // Add tooltips to navigation buttons
        homeBtnIcon.setToolTipText("Home Dashboard");
        taskBtnIcon.setToolTipText("Manage Tasks");
        quizBtnIcon.setToolTipText("Flashcards & Quizzes");
        settingsBtnIcon.setToolTipText("Settings");
        
        // Add icons to navbar
        navbar.add(homeBtnIcon);
        navbar.add(taskBtnIcon);
        navbar.add(quizBtnIcon);
        navbar.add(settingsBtnIcon);
  
        // Add panels to body with specific constraints
        body.add(homePanel(username), "homePanel");
        body.add(task.createTaskPanel(username), "taskPanel");
        body.add(StudyPanel.createStudyPanel(), "quizPanel");
        body.add(SettingsPanel.createSettingsPanel(), "settingsPanel");  // Add settings panel
        
        // Show initial panel
        cardLayout.show(body, "homePanel");
        
        // Add mouse listeners with cursor changes
        homeBtnIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(body, "homePanel");
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        taskBtnIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(body, "taskPanel");
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        quizBtnIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(body, "quizPanel");
                StudyPanel.showFlashcardCreation(); // Ensure the flashcard view is shown by default
                revalidate();
                repaint();
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        // Add mouse listener for settings button
        settingsBtnIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(body, "settingsPanel");
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
        homePanel.add(HomePanel.createOverview(username), gbc);//just call the sub class tasklist and access the method
        
        return homePanel;
    }

    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // new Tasklr("admin").setVisible(true);
            new Login().setVisible(true);
            TaskFetcher tf = new TaskFetcher();
            tf.getUserTasks();
        });
    }
}
