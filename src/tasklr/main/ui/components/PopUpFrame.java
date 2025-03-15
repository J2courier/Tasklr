package tasklr.main.ui.components;

import javax.swing.*;
import java.awt.*;

public class PopUpFrame extends JFrame {
    private static final int DEFAULT_WIDTH = 900;
    private static final int DEFAULT_HEIGHT = 1000;
    private static final Color BACKGROUND_COLOR = new Color(0xf1f3f6);
    
    public PopUpFrame(String title) {
        setupFrame(title);
    }
    
    private void setupFrame(String title) {
        setTitle(title);
        setSize(700, 920);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        getContentPane().setBackground(BACKGROUND_COLOR);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public static JPanel createPopUpComponent(String labelValue) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(100, 70));
        
        JLabel label = new JLabel(labelValue, SwingConstants.CENTER);
        label.setFont(BaseComponent.COUNTER_FONT);
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }
}
